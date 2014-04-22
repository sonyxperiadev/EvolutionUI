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

import com.sonymobile.evolutionui.IEvolutionUIService;
import com.sonymobile.evolutionui.R;
import com.sonymobile.evolutionui.util.EvUICActivity;
import com.sonymobile.evolutionui.util.EvUICListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class SelectProfileActivity extends EvUICActivity implements OnClickListener, EvUICListener {

    private Handler mHandler = new Handler();
    private View mBtnCancel;
    private View mBtnOk;
    private View mBtnDynamicGrp;
    private ImageView mBtnDynamic;
    private int mLevel;
    private boolean mDynamic = true;
    private LevelSelector mLevelSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_select_profile);

        mBtnCancel = findViewById(R.id.cancel);
        mBtnCancel.setOnClickListener(this);

        mBtnOk = findViewById(R.id.ok);
        mBtnOk.setOnClickListener(this);

        mBtnDynamic = (ImageView) findViewById(R.id.btnDynamic);
        mBtnDynamicGrp = findViewById(R.id.btnDynamicGrp);
        mBtnDynamicGrp.setOnClickListener(this);

        mLevelSelector = (LevelSelector)findViewById(R.id.select_profile_level);
    }

    private void updateDynamicBtn() {
        mBtnDynamic.setImageResource(mDynamic ? R.drawable.chkbox_big_checked : R.drawable.chkbox_big_unchecked);
    }

    @Override
    public void onServiceConnected(IEvolutionUIService service) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                IEvolutionUIService service = getService();
                try {
                    mLevel = service.getProfileLevel();
                    mDynamic = service.isProfileDynamic();

                    updateDynamicBtn();
                    mLevelSelector.setLevel(mLevel);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onServiceDisconnected() {
    }

    @Override
    public void onClick(View view) {
        if (view == mBtnCancel) {
            finish();
            return;
        }

        if (view == mBtnOk) {
            try {
                IEvolutionUIService service = getService();
                if (service == null) return;
                mLevel = mLevelSelector.getLevel();
                service.setProfile(mLevel, mDynamic);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            finish();
            return;
        }

        if (view == mBtnDynamicGrp) {
            mDynamic = !mDynamic;
            updateDynamicBtn();
        }

    }

}
