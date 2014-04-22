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

import com.sonymobile.evolutionui.BasicConcept;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class Util {

    public static final boolean STRICT_MODE = true;

    public static String checkStringRes(Context context, String s) {
        if (s.startsWith("@string/")) {
            Resources res = context.getResources();
            int id = res.getIdentifier(s.substring(8), "string", context.getPackageName());
            if (id != 0) {
                s = res.getString(id);
            } else {
                if (Util.STRICT_MODE) {
                    throw new RuntimeException("Could not find ID of resource: " + s);
                }
            }
        }
        return s;
    }

    public static Drawable fetchIconFor(Context context, BasicConcept item) {
        Resources defRes = context.getResources();
        Resources res = defRes;
        String defPkg = context.getPackageName();
        String pkg = defPkg;
        if (item.getDomain() != null) {
            pkg = item.getDomain().getName();
            if (!pkg.equals(defPkg)) {
                try {
                    Context tmp = context.createPackageContext(pkg, 0);
                    res = tmp.getResources();
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        int id = res.getIdentifier(item.getIconRes(), "drawable", pkg);
        if (id != 0) {
            return res.getDrawable(id);
        } else {
            id = defRes.getIdentifier(item.getDefaultIconRes(), "drawable", defPkg);
            if (id != 0) {
                return defRes.getDrawable(id);
            } else {
                if (STRICT_MODE) {
                    throw new RuntimeException("Could not find resource id (or default resource id) of resource: " + item.getIconRes());
                }
            }
        }
        return null;
    }

}
