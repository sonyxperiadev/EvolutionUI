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
package com.sonymobile.evolutionui.util;

import com.sonymobile.evolutionui.Feature;
import com.sonymobile.evolutionui.IEvolutionUIService;
import com.sonymobile.evolutionui.IEvolutionUIServiceCB;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.Vector;

public class EvUICHelper extends ContextWrapper implements ServiceConnection {

    private static final String TAG = "EvUICHelper";

    private EvUIClient mClient;

    private FeatureLibrary mFeatures;

    private Vector<Controller> mControllers = new Vector<Controller>();

    private ServiceCBImpl mServiceCB;

    private Handler mHandler = new Handler();

    private EvUICListener mListener;

    public EvUICHelper(Context base) {
        super(base);
        if (base instanceof EvUICListener) {
            mListener = (EvUICListener)base;
        }
    }

    public void clear() {
        mFeatures = null;
        mControllers.clear();
    }

    public void onStart() {
        // Start the controllers
        for (int i = 0; i < mControllers.size(); i++) {
            final Controller ctrl = mControllers.get(i);
            ctrl.onStart();
        }

        mClient = new EvUIClient(this, this);
        mClient.connect();
    }

    public void onStop() {
        mClient.disconnect();
        mClient = null;

        // Stop the controllers (in reverse order)
        for (int i = mControllers.size() - 1; i >= 0; i--) {
            final Controller ctrl = mControllers.get(i);
            ctrl.onStop();
        }
    }

    public void addController(Controller controller) {
        mControllers.add(controller);
    }

    public void addFeature(Feature feature) {
        if (mFeatures == null) {
            mFeatures = new FeatureLibrary();
        }
        mFeatures.addFeature(feature);
    }

    public EvUIClient getClient() {
        return mClient;
    }

    public IEvolutionUIService getService() {
        return mClient == null ? null : mClient.getService();
    }

    @Override
    public void onServiceConnected(ComponentName cmp, IBinder binder) {
        Log.v(TAG, "Connected to service");

        try {
            mServiceCB = new ServiceCBImpl();
            IEvolutionUIService service = getService();
            if (service != null) {
                service.registerCallback(mServiceCB);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Fetch the initial values from the service
        if (mFeatures != null) {
            mFeatures.readValues(mClient.getService());
        }

        if (mListener != null) {
            mListener.onServiceConnected(mClient.getService());
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName binder) {
        Log.v(TAG, "Disconnected from service");
        try {
            if (mListener != null) {
                mListener.onServiceDisconnected();
            }

            IEvolutionUIService service = getService();
            if (service != null) {
                service.unregisterCallback(mServiceCB);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mServiceCB = null;
    }

    class ServiceCBImpl extends IEvolutionUIServiceCB.Stub {
        @Override
        public void onFeatureChanged(String name, int level) throws RemoteException {
            EvUICHelper.this.onFeatureChanged(name, level);
        }
    }

    public void onFeatureChanged(final String name, final int level) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mFeatures != null) {
                    mFeatures.onFeatureChanged(name, level);
                }
            }
        });
    }

}
