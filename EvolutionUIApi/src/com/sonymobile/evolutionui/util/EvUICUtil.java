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

import com.sonymobile.evolutionui.EvolutionUIIntents;

import android.content.Context;
import android.content.Intent;

public class EvUICUtil {

    public static void sendExperience(Context context, int resId) {
        String exp = context.getString(resId);
        sendExperience(context, exp);
    }

    public static void sendExperience(Context context, String experience) {
        Intent intent = new Intent(EvolutionUIIntents.ACTION_EXPERIENCE);
        intent.putExtra(EvolutionUIIntents.EXTRA_EXPERIENCE_ID, experience);
        context.sendBroadcast(intent);
    }

}
