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

import android.os.RemoteException;

import java.util.Vector;

/**
 * This is a helper class which handles a collection of Features
 */
public class FeatureLibrary {

    private Vector<Feature> mItems = new Vector<Feature>();

    public FeatureLibrary() {
    }

    public void addFeature(Feature item) {
        mItems.add(item);
    }

    public void onFeatureChanged(String name, int level) {
        for (Feature f : mItems) {
            if (name.equals(f.getId())) {
                f.onStateChanged(level);
            }
        }
    }

    public void readValues(IEvolutionUIService service) {
        for (Feature f : mItems) {
            try {
                int level = service.getFeatureState(f.getId());
                f.onStateChanged(level);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
