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
package com.sonymobile.evolutionui.service.status;

import com.sonymobile.evolutionui.Achievement;
import com.sonymobile.evolutionui.Coin;
import com.sonymobile.evolutionui.EvolutionUIIntents;
import com.sonymobile.evolutionui.Feature;
import com.sonymobile.evolutionui.IEvolutionUIService;
import com.sonymobile.evolutionui.Level;
import com.sonymobile.evolutionui.R;
import com.sonymobile.evolutionui.service.Util;
import com.sonymobile.evolutionui.util.EvUICActivity;
import com.sonymobile.evolutionui.util.EvUICListener;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EvolutionUIStatusActivity extends EvUICActivity implements EvUICListener {

    private Handler mHandler = new Handler();

    private View mFeaturesGroup;
    private Gallery mFeaturesList;
    private StatusAdapter mFeaturesAdapter;
    private View mAchievementGroup;
    private Gallery mAchievementList;
    private StatusAdapter mAchievementAdapter;

    private Feature mSelectedNewFeature;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_status);

        mFeaturesGroup = findViewById(R.id.new_features);
        mFeaturesList = (Gallery)findViewById(R.id.new_features_list);
        mFeaturesAdapter = new StatusAdapter(this);
        mFeaturesList.setAdapter(mFeaturesAdapter);
        mFeaturesList.setOnItemClickListener(mFeatureItemListener);

        mAchievementGroup = findViewById(R.id.new_achievements);
        mAchievementList = (Gallery)findViewById(R.id.new_achievements_list);
        mAchievementAdapter = new StatusAdapter(this);
        mAchievementList.setAdapter(mAchievementAdapter);
        mAchievementList.setOnItemClickListener(mAchievementItemListener);

        View view = findViewById(R.id.cur_level_and_xp);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showProfileSelectionActivity();
            }
        });

        view = findViewById(R.id.admin);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showAdminScreen();
            }
        });
    }

    protected void showAdminScreen() {
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }

    protected void showProfileSelectionActivity() {
        Intent intent = new Intent(this, SelectProfileActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter();
        filter.addAction(EvolutionUIIntents.ACTION_PROFILE_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(mBroadcastReceiver);

        super.onStop();
    }

    private void updateProfileStatus() {
        IEvolutionUIService service = getService();
        if (service == null) return;
        try {
            int profileLevel = service.getProfileLevel();
            boolean profileDynamic = service.isProfileDynamic();
            updateProfile(profileLevel, profileDynamic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void updateProfile(int profileLevel, boolean profileDynamic) {
        // TODO
//        ImageView iv = (ImageView)findViewById(R.id.profile_icon);
//        if (iv != null) {
//            iv.setImageResource(profileIconId);
//        }
//        TextView tv = (TextView)findViewById(R.id.profile_name);
//        if (tv != null) {
//            tv.setText("Profile: " + profileName);
//        }
    }

    private void updateCurrentLevelAndXP() {
//        if (mProfile.getType() != Profile.TYPE_DYNAMIC) {
            // Hide the current level and xp
//            View view = findViewById(R.id.cur_level_and_xp);
//            view.setVisibility(View.GONE);
//            // Show the message
//            view = findViewById(R.id.msg_static_profile);
//            view.setVisibility(View.VISIBLE);
//            return;
//        } else {
            // Show the current level and xp
//            View view = findViewById(R.id.cur_level_and_xp);
//            view.setVisibility(View.VISIBLE);
//            // Hide the message
//            view = findViewById(R.id.msg_static_profile);
//            view.setVisibility(View.GONE);
//        }

        IEvolutionUIService service = getService();
        if (service == null) return;
        try {
            // Update current level and xp
            Level levelDesc = service.getCurrentLevel();
            int level = levelDesc.getNumber();
            int xp = service.getCurrentXP();
            TextView tv;
            tv = (TextView)findViewById(R.id.cur_level);
            tv.setText("Current level: " + level);
            tv = (TextView)findViewById(R.id.cur_xp);
            tv.setText("Current XP: " + xp);
//            ImageView iv = (ImageView)findViewById(R.id.cur_level_icon);
//            iv.setImageResource(levelDesc.getIconResId());

//            // Check if there are new features to unlock
//            View view = findViewById(R.id.new_level);
//            int unlock = service.getFreeFeaturesCount();
//            if (unlock == 0) {
//                view.setVisibility(View.GONE);
//            } else {
//                view.setVisibility(View.VISIBLE);
//                tv = (TextView)findViewById(R.id.new_level_label);
//                tv.setText(String.format("You reached a new level! As a result, you can unlock %s new features", unlock));
//                view.setOnClickListener(mNewLevelListener);
//            }

            // Also let the service know that the status screen was shown
            service.setStatusScreenShown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateNewAchievements() {
//        if (mProfile.getType() != Profile.TYPE_DYNAMIC) {
//            // Hide new achievements info
//            mAchievementLabel.setVisibility(View.GONE);
//            mAchievementList.setVisibility(View.GONE);
//            return;
//        }

        IEvolutionUIService service = getService();
        if (service == null) return;
        try {
            Achievement items[] = service.getNewAchievements();
            if (items.length == 0) {
                // Hide new achievements info
                mAchievementGroup.setVisibility(View.GONE);
            } else {
                // Show info
                mAchievementGroup.setVisibility(View.VISIBLE);
                mAchievementAdapter.clear();
                // Populate data
                for (Achievement item : items) {
                    mAchievementAdapter.add(item);
                }
                mAchievementAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void showAchievement(final Achievement item, final View itemView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(item.getName());

        View view = LayoutInflater.from(this).inflate(R.layout.achievement_descr, null);
        TextView tv = (TextView)view.findViewById(R.id.ach_text);
        tv.setText(item.getDescription());
        ImageView iv = (ImageView)view.findViewById(R.id.ach_icon);
        iv.setImageDrawable(Util.fetchIconFor(this, item));
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Remove the achievement from the list
        if (mAchievementAdapter.remove(item)) {
            mAchievementGroup.setVisibility(View.GONE);
        }
        mAchievementAdapter.notifyDataSetChanged();

        // Also notify the service that the user has seen the achievement
        try {
            IEvolutionUIService service = getService();
            if (service == null) return;
            service.setAchievementSeen(item);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void updateNewFeatures() {
//        if (mProfile.getType() != Profile.TYPE_DYNAMIC) {
//            // Hide new achievements info
//            mFeaturesLabel.setVisibility(View.GONE);
//            mFeaturesList.setVisibility(View.GONE);
//            return;
//        }

        IEvolutionUIService service = getService();
        if (service == null) return;
        try {
            int total = 0;
            mFeaturesAdapter.clear();

            // Show features
            Feature items[] = service.getNewFeatures();
            if (items.length != 0) {
                total += items.length;
                // Populate data
                for (Feature item : items) {
                    mFeaturesAdapter.add(item);
                }
            }

            // Show coins
            Coin[] coins = service.getCoins();
            if (coins.length != 0) {
                total += coins.length;
                for (Coin coin : coins) {
                    mFeaturesAdapter.add(coin);
                }
            }

            // Calculate visibility
            if (total == 0) {
                // Hide new features info
                mFeaturesGroup.setVisibility(View.GONE);
            } else {
                mFeaturesGroup.setVisibility(View.VISIBLE);
                mFeaturesAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void showFeature(final Feature item, final View itemView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(this).inflate(R.layout.feature_descr, null);
        TextView tv = (TextView)view.findViewById(R.id.feature_descr);
        tv.setText(item.getDescription());
        tv = (TextView)view.findViewById(R.id.feature_name);
        tv.setText(item.getName());
        ImageView iv = (ImageView)view.findViewById(R.id.feature_icon);
        iv.setImageDrawable(Util.fetchIconFor(this, item));
        builder.setView(view);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                try {
                    IEvolutionUIService service = getService();
                    if (service == null) return;
                    service.setFeatureActivated(item);
                    // Also remove the list item
                    if (mFeaturesAdapter.remove(item)) {
                        mFeaturesGroup.setVisibility(View.GONE);
                    }
                    mFeaturesAdapter.notifyDataSetChanged();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Also notify the service that the user has seen the achievement
        try {
            IEvolutionUIService service = getService();
            if (service == null) return;
            service.setFeatureSeen(item);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected void showBuyNewFeature(final Coin coin) {
        final Feature[] items;
        String itemNames[];
        try {
            IEvolutionUIService service = getService();
            if (service == null) return;
            items = service.whatCanBeBoughtWith(coin);
            int cnt = items.length;
            if (cnt == 0) {
                return;
            }
            itemNames = new String[cnt];
            for (int i = 0; i < cnt; i++) {
                itemNames[i] = items[i].getName();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return;
        }

        mSelectedNewFeature = items[0];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select feature to buy:");

        final TextView descr = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.gap_size);
        lp.setMargins(margin, margin, margin, margin);
        descr.setLayoutParams(lp);
        descr.setText(mSelectedNewFeature.getDescription());

        builder.setSingleChoiceItems(itemNames, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Feature f = items[which];
                mSelectedNewFeature = f;
                descr.setText(f.getDescription());
            }
        });

        builder.setView(descr);

        builder.setPositiveButton("Activate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                try {
                    IEvolutionUIService service = getService();
                    if (service == null) return;
                    service.buyFeature(mSelectedNewFeature, coin);
                    mFeaturesAdapter.remove(coin);
                    mFeaturesAdapter.notifyDataSetChanged();
                    updateCurrentLevelAndXP();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onServiceConnected(IEvolutionUIService service) {
        updateProfileStatus();
        updateNewAchievements();
        updateNewFeatures();
        updateCurrentLevelAndXP();
    }

    @Override
    public void onServiceDisconnected() {
    }

    private OnItemClickListener mFeatureItemListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View v, int pos, long id) {
            Object item = v.getTag();
            if (item instanceof Feature) {
                showFeature((Feature)item, v);
            } else {
                showBuyNewFeature((Coin)item);
            }
        }
    };

    private OnItemClickListener mAchievementItemListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View v, int pos, long id) {
            showAchievement((Achievement)v.getTag(), v);
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            Bundle extras = intent.getExtras();
            if (action.equals(EvolutionUIIntents.ACTION_PROFILE_CHANGED)) {
                final int profileLevel = extras.getInt(EvolutionUIIntents.EXTRA_PROFILE_LEVEL);
                final boolean profileDynamic = extras.getBoolean(EvolutionUIIntents.EXTRA_PROFILE_DYNAMIC);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateProfile(profileLevel, profileDynamic);
                    }
                });
            }
        }
    };

}
