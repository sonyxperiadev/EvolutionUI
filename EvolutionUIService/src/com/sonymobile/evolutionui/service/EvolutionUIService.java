/*
 * Copyright (C) 2014 Sony Mobile Communications AB
 *
 * This file is part of EvolutionUI.
 *
 * EvolutionUI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * EvolutionUI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EvolutionUI. If not, see <http://www.gnu.org/licenses/>.
 */
package com.sonymobile.evolutionui.service;

import com.sonymobile.evolutionui.Achievement;
import com.sonymobile.evolutionui.Coin;
import com.sonymobile.evolutionui.EvolutionUIIntents;
import com.sonymobile.evolutionui.Feature;
import com.sonymobile.evolutionui.IEvolutionUIService;
import com.sonymobile.evolutionui.IEvolutionUIServiceCB;
import com.sonymobile.evolutionui.Level;
import com.sonymobile.evolutionui.R;
import com.sonymobile.evolutionui.service.data.AchievementDesc;
import com.sonymobile.evolutionui.service.data.Debugger;
import com.sonymobile.evolutionui.service.data.ExperienceDesc;
import com.sonymobile.evolutionui.service.data.FeatureDesc;
import com.sonymobile.evolutionui.service.data.LevelDesc;
import com.sonymobile.evolutionui.service.data.EvUIModel;
import com.sonymobile.evolutionui.service.status.EvolutionUIStatusActivity;
import com.sonymobile.tools.xappdbg.XAppDbgServer;
import com.sonymobile.tools.xappdbg.properties.XAppDbgTreeModule;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Vector;

/**
 * This is the core service. It maintains the status of all the features, experiences,
 * achievements, etc.
 *
 * The service uses an ongoing notification both to show new events, and also to allow the user
 * to quickly check the current status.
 *
 * Each application needs to communicate with this service. The service will detect the feature
 * list from the application meta-datas.
 */
public class EvolutionUIService extends Service {

    private static final String TAG = "EvolutionUIService";

    /** ID of the ongoing notifcation */
    private static final int NOTIFICATION_ID = 1;
    /** The ongoing notificaiton. */
    private Notification mNotification;
    /** The notification callback */
    private PendingIntent mNotificationIntent;
    /** Quick ref to the notification reference */
    private NotificationManager mNotificationManager;

    /** The implementation of the service */
    private IBinder mService = new IEvolutionUIServiceImpl();
    /** The currently connected clients of the service */
    private Vector<IEvolutionUIServiceCB> mCallbacks = new Vector<IEvolutionUIServiceCB>();
    /** The current state of all the features/achievements/experiences/etc. */
    private EvUIModel mModel;

    /** For easier debugging */
    private XAppDbgServer mPDS;

    /** In order to execute stuff on the main thread */
    private Handler mHandler = new Handler();

    @Override
    public IBinder onBind(Intent intent) {
        return mService;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Load the core data
        mModel = new EvUIModel(this);
        mModel.onCreate();

        // Create the notification
        mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mNotification = new Notification();
        sendStatusNotification();
        startForeground(NOTIFICATION_ID, mNotification);

        // Listen for broadcast intents
        IntentFilter filter = new IntentFilter();
        filter.addAction(EvolutionUIIntents.ACTION_EXPERIENCE);
        registerReceiver(mBroadcastReceiver, filter);

        // Collect achievements from 3rd parties
        PackageManager pm = getPackageManager();
        List<PackageInfo> list = pm.getInstalledPackages(PackageManager.GET_META_DATA);
        for (PackageInfo pi : list) {
            Bundle meta = pi.applicationInfo.metaData;
            if (meta != null && meta.containsKey("com.sonymobile.evolutionui")) {
                int resId = meta.getInt("com.sonymobile.evolutionui");
                try {
                    Context contextExtra = createPackageContext(pi.packageName, 0);
                    Log.i(TAG, "Found metadata in pkg " + pi.packageName + ": 0x" + Integer.toHexString(resId));
                    mModel.loadData(contextExtra, resId);
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        mModel.finishLoad();

        // Start the debug server
        startDebugServer();
    }

    @Override
    public void onDestroy() {
        stopDebugServer();

        mModel.onDestroy();
        mModel = null;

        unregisterReceiver(mBroadcastReceiver);

        cancelNotification();
        mNotificationManager = null;

        super.onDestroy();
    }

    public EvUIModel getModel() {
        return mModel;
    }

    private void sendStatusNotification() {
        sendNotification(false, "Click here to see your status", null);
    }

    private void sendNewAchievementNotificaton() {
        sendNotification(true, "New achievement!", "New achievement!");
    }

    private void sendNewFeatureNotificaton() {
        sendNotification(true, "New feature to activate!", "New feature to activate!");
    }

    private void sendNewLevelNotificaton() {
        sendNotification(true, "You leveled up!", "You leveled up!");
    }

    private void sendNotification(boolean b, String msgText, String ticker) {
        long now = System.currentTimeMillis();
        int resId = b ? R.drawable.notification_icon : R.drawable.notification_icon_inactive;
        mNotification.tickerText = ticker;
        mNotification.when = now;
        mNotification.icon = resId;
        int level = mModel.getCurLevel();
        mNotification.setLatestEventInfo(this, msgText, "Level: " + level, getNotificationIntent());
        mNotification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_FOREGROUND_SERVICE;
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    private PendingIntent getNotificationIntent() {
        if (mNotificationIntent == null) {
            Intent intent = new Intent(this, EvolutionUIStatusActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mNotificationIntent = PendingIntent.getActivity(this, 0, intent, 0);
        }
        return mNotificationIntent;
    }

    private void cancelNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    private void startDebugServer() {
        mPDS = new XAppDbgServer();
        mPDS.addModule(new XAppDbgTreeModule(mDebugger, mDebugger));
        mPDS.start();
    }

    private void stopDebugServer() {
        mPDS.stop();
    }

    public void onFeatureChanged(FeatureDesc feature) throws RemoteException {
        String name = feature.getName();
        int level = feature.isEnabled();
        // Update status, if the user needs to enable this
        if (level == Feature.STATE_ENABLED) {
            onNewFeature(feature);
        }
        // Notify the clients
        for (IEvolutionUIServiceCB cb : mCallbacks) {
            cb.onFeatureChanged(name, level);
        }
    }

    /**
     * Called by various other applications to notify us that the user
     * executed some action.
     * @param experience The experience/action id
     */
    protected void onNewExperience(String experience) {
        // Check that we are using a dynamic profile
        if (!mModel.isProfileDynamic()) {
            // Ignore experiences if this is not a dynamic profile
            return;
        }

        mModel.onNewExperience(experience);
    }

    /**
     * Called from the model to notify that the user got a new achievement
     * @param item The new achievement
     */
    public void onNewAchievement(AchievementDesc item) {
        // Send a notification to the user
        if (mModel.getPendingAchievementCount() == 0) {
            if (mNotificationManager != null) {
                sendNewAchievementNotificaton();
            }
        }

        // Add the achievement to the list (always insert as first, so it will be the first
        // one visible to the user)
        mModel.addNewPendingAchievement(item);
    }

    public void onNewLevel(final LevelDesc item) {
        // The user got a new free feature to select
        mModel.addCoin(new Coin(""));

        // Notify the user about it
        if (mNotificationManager != null) {
            sendNewLevelNotificaton();
        }

        // Show a Toast as well
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // Show a toast notification as well
                Toast toast = new Toast(EvolutionUIService.this);
                View view = LayoutInflater.from(EvolutionUIService.this).inflate(R.layout.toast_level, null);
                TextView tv = (TextView)view.findViewById(R.id.level_name);
                tv.setText("Level " + item.getNumber());
                toast.setView(view);
                toast.show();
            }
        });

    }

    /**
     * Called from the model to notify that the user can enable a new feature
     * @param feature
     */
    private void onNewFeature(final FeatureDesc item) {
        // Note: there is a special case: if a feature was enabled,
        // and the user changed profile to static then back to dynamic, then
        // this will be called again.
        // So we must check if this feature is already in the pending list
        // Add the achievement to the list (always insert as first, so it will be the first
        // one visible to the user)
        if (!mModel.addPendingFeature(item)) {
            // It was already added
            return;
        }

        // Send a notification to the user
        if (mModel.getPendingFeaturesCount() == 0) {
            if (mNotificationManager != null) {
                sendNewFeatureNotificaton();
            }
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // Show a toast notification as well
                Toast toast = new Toast(EvolutionUIService.this);
                View view = LayoutInflater.from(EvolutionUIService.this).inflate(R.layout.toast_feature, null);
                TextView tv = (TextView)view.findViewById(R.id.feature_name);
                tv.setText(item.getName());
                ImageView iv = (ImageView)view.findViewById(R.id.feature_icon);
                iv.setImageDrawable(Util.fetchIconFor(EvolutionUIService.this, item));
                toast.setView(view);
                toast.show();
            }
        });
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle extras = intent.getExtras();
            if (action.equals(EvolutionUIIntents.ACTION_EXPERIENCE)) {
                String experience = extras.getString(EvolutionUIIntents.EXTRA_EXPERIENCE_ID);
                EvolutionUIService.this.onNewExperience(experience);
            }

        }
    };

    class IEvolutionUIServiceImpl extends IEvolutionUIService.Stub {

        @Override
        public void registerCallback(final IEvolutionUIServiceCB cb) throws RemoteException {
            mCallbacks.add(cb);
            cb.asBinder().linkToDeath(new DeathRecipient() {
                @Override
                public void binderDied() {
                    mCallbacks.remove(cb);
                }
            }, 0);
        }

        @Override
        public void unregisterCallback(IEvolutionUIServiceCB cb) throws RemoteException {
            mCallbacks.remove(cb);
        }

        @Override
        public String getServicePackageName() throws RemoteException {
            return getPackageName();
        }

        @Override
        public int getFeatureState(String featureId) throws RemoteException {
            return mModel.getFeatureState(featureId);
        }

        @Override
        public Achievement[] getNewAchievements() throws RemoteException {
            return mModel.getPendingAchievements();
        }

        @Override
        public void setAchievementSeen(Achievement item) throws RemoteException {
            mModel.setAchievementSeen(item);
            checkResetNotification();
        }

        @Override
        public int getCurrentXP() throws RemoteException {
            return mModel.getCurXP();
        }

        @Override
        public Feature[] getNewFeatures() throws RemoteException {
            return mModel.getPendingFeatures();
        }

        @Override
        public void setFeatureSeen(Feature item) throws RemoteException {
            mModel.setFeatureSeen(item);
            checkResetNotification();
        }

        @Override
        public void setFeatureActivated(Feature item) throws RemoteException {
            mModel.setFeatureActivated(item);
            checkResetNotification();
        }

        @Override
        public Level getNextLevel() throws RemoteException {
            return mModel.getNextLevelDesc();
        }

        @Override
        public Level getCurrentLevel() throws RemoteException {
            return mModel.getCurLevelDesc();
        }

        @Override
        public void resetStats() throws RemoteException {
            mModel.resetStats();
            sendStatusNotification();
        }

        @Override
        public Feature[] whatCanBeBoughtWith(Coin item) throws RemoteException {
            return mModel.whatCanBeBoughtWith(item);
        }

        @Override
        public void buyFeature(Feature item, Coin coin) throws RemoteException {
            mModel.buyFeature(item, coin);
        }

        @Override
        public int isStatusScreenShown() throws RemoteException {
            return mModel.isStatusScreenShown();
        }

        @Override
        public void setStatusScreenShown() throws RemoteException {
            mModel.setStatusScreenShown();
        }

        @Override
        public int getExperienceCount(String id) throws RemoteException {
            ExperienceDesc exp = mModel.getExperience(id);
            return exp == null ? -1 : exp.getCounter();
        }

        @Override
        public int getProfileLevel() throws RemoteException {
            return mModel.getProfileLevel();
        }

        @Override
        public boolean isProfileDynamic() throws RemoteException {
            return mModel.isProfileDynamic();
        }

        @Override
        public void setProfile(int level, boolean dynamic) throws RemoteException {
            mModel.setProfile(level, dynamic);
        }

        @Override
        public Coin[] getCoins() throws RemoteException {
            return mModel.getCoins();
        }

        @Override
        public int getCoinCount() throws RemoteException {
            return mModel.getCoinCount();
        }

        @Override
        public void addExperience(String exp) throws RemoteException {
            onNewExperience(exp);
        }

    }

    protected Debugger mDebugger = new Debugger(this);

    public void checkResetNotification() {
        if (mModel.getPendingAchievementCount() > 0) {
            // There are still pending achievements
            return;
        }
        for (Feature f : mModel.getPendingFeatures()) {
            if (f.isEnabled() < Feature.STATE_SEEN) {
                // There are still unseen features
                return;
            }
        }
        if (mModel.getCoinCount() > 0) {
            // There are still features to unlock
            return;
        }
        // Clear the notification
        sendStatusNotification();
    }

}
