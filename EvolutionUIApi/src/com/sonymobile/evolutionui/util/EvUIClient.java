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

import com.sonymobile.evolutionui.IEvolutionUIService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class EvUIClient implements ServiceConnection {

    private static final String TAG = "EvUIClient";

    private static final String SERVICE_PKG = "com.sonymobile.evolutionui";

    private static final String SERVICE_CLS = SERVICE_PKG + ".service.EvolutionUIService";

    /** The handle to the service */
    private IEvolutionUIService mService;

    /** Connection handle towards the service */
    private ServiceConnection mConnection;

    /** The current android context */
    private Context mContext;

    private ServiceConnection mCB;

    public EvUIClient(Context context, ServiceConnection cb) {
        mContext = context;
        mCB = cb;
    }

    public Context getContext() {
        return mContext;
    }

    public IEvolutionUIService getService() {
        return mService;
    }

    /**
     * Establish the connection with the service
     * @return true if the connection will be successful.
     */
    public boolean connect() {

        if (mConnection != null) {
            // Already connected
            return false;
        }

        Intent intent = new Intent();
        intent.setClassName(SERVICE_PKG, SERVICE_CLS);

        Log.v(TAG, "Starting service via intent: " + intent);
        mContext.startService(intent);

        Log.v(TAG, "Connecting to service via intent: " + intent);
        mConnection = this;
        boolean ret = getContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.v(TAG, "bindService: ret=" + ret);

        return ret;
    }

    /**
     * Disconnect from the service
     */
    public void disconnect() {
        if (mConnection == null) {
            // Already disconnected
            return;
        }

        Log.v(TAG, "Disconnecting from service");
        getContext().unbindService(mConnection);
        mConnection = null;
    }

    public void onServiceConnected(ComponentName cmp, IBinder binder) {
        mService = IEvolutionUIService.Stub.asInterface(binder);
        if (mCB != null) {
            mCB.onServiceConnected(cmp, binder);
        }
    }

    public void onServiceDisconnected(ComponentName cmp) {
        if (mCB != null) {
            mCB.onServiceDisconnected(cmp);
        }
    }

}
