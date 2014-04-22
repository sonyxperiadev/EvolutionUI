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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;

public class AdminActivity extends EvUICActivity implements OnClickListener {

    public static final String APP_PKG_PREFIX = "com.android.settings.";
    public static final String APP_PKG_NAME = APP_PKG_PREFIX + "ApplicationPkgName";

    private View mBtnReset;
    private View mBtnSettings;
    private View mBtnUnset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.admin);

        mBtnReset = findViewById(R.id.admin_reset);
        mBtnReset.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mBtnReset) {
            resetStats();
        }
    }

    private void resetStats() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to reset all achievements, levels, features and xp?")
        .setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                doResetStats();
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    protected void doResetStats() {
        try {
            IEvolutionUIService service = getService();
            if (service == null) return;
            service.resetStats();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        // finish() is not enough, because then we would show the status screen again
        // we need to go back home
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_HOME);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
