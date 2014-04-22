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
package com.sonymobile.evolutionui;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A Level is a special Achievement which is obtained by getting a certain
 * amount of experience points.
 * <p>
 * Levels, unlike the rest of the basic concepts, are identified by numbers, and the level
 * identifiers must be sequential numbers.
 */
public class Level implements Parcelable {

    public Level() {
    }

    public int getNumber() {
        return mNum;
    }

    public int getTrigger() {
        return mTrigger;
    }

    public int getIconResId() {
        return mIconResId;
    }

    protected void setNumber(int value) {
        mNum = value;
    }

    protected void setTrigger(int value) {
        mTrigger = value;
    }

    protected void setIconResId(int resId) {
        mIconResId = resId;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Implementation details below
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int mNum;
    private int mTrigger;
    private int mIconResId;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mNum);
        out.writeInt(mIconResId);
    }

    public static final Parcelable.Creator<Level> CREATOR = new Parcelable.Creator<Level>() {
        public Level createFromParcel(Parcel in) {
            return new Level(in);
        }

        public Level[] newArray(int size) {
            return new Level[size];
        }
    };

    private Level(Parcel in) {
        mNum = in.readInt();
        mIconResId = in.readInt();
    }

}
