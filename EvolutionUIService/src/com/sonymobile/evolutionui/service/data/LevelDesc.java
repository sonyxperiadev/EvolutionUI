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
package com.sonymobile.evolutionui.service.data;

import com.sonymobile.evolutionui.Level;

import android.content.res.Resources;

public class LevelDesc extends Level {

    private EvUIModel mModel;

    public LevelDesc(EvUIModel model, XMLNode item) {
        mModel = model;

        setNumber(Integer.parseInt(item.getAttribute("num")));
        setTrigger(Integer.parseInt(item.getAttribute("trigger")));

        String icon = item.getAttribute("icon");
        if (icon != null) {
            Resources res = model.getService().getResources();
            int resId = res.getIdentifier(icon, "drawable", model.getService().getPackageName());
            setIconResId(resId);
        }
    }

    public EvUIModel getModel() {
        return mModel;
    }

    @Override
    public String toString() {
        return "LevelDesc [mNum=" + getNumber() + ", mTrigger=" + getTrigger() + "]";
    }

}
