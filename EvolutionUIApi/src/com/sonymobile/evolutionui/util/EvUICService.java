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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class EvUICService extends Service {

    private EvUICHelper mEvUIHelper = new EvUICHelper(this);

    @Override
    public void onCreate() {
        super.onCreate();
        mEvUIHelper.clear();
        mEvUIHelper.onStart();
    }

    @Override
    public void onDestroy() {
        mEvUIHelper.onStop();
        super.onDestroy();
    }

    protected void addController(Controller controller) {
        mEvUIHelper.addController(controller);
    }

    protected void addFeature(Feature feature) {
        mEvUIHelper.addFeature(feature);
    }

    public EvUIClient getClient() {
        return mEvUIHelper.getClient();
    }

    public IEvolutionUIService getService() {
        return mEvUIHelper.getService();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
