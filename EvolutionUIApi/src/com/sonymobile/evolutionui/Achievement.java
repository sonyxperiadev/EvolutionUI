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
 * An Achievement is a meta-goal, an accomplishment, which usually results in unlocking
 * new functionalities.
 * <p>
 * Achievements have a trigger condition, which are usually connected to experiences.
 * For example an Achievement can be to make 10 phone calls. In this example making a phone
 * call is an experience.
 * <p>
 * Achievements are similar to trophies in the PlayStation Network.
 */
public class Achievement extends BasicConcept implements Parcelable {

    /**
     * The Achievement is not accomplished yet.
     */
    public static final int STATE_NONE = 0;
    /**
     * The Achievement is accomplished, but it's still in the "unread" state.
     * This means it will be highlighted in the status screen.
     */
    public static final int STATE_ACHIEVED = 1;
    /**
     * The Achievement is accomplished, and was also seen by the user.
     * This means it won't be shown any more in the status screen.
     */
    public static final int STATE_SEEN = 2;

    public Achievement() {
    }

    /**
     * Returns the current state of the achievement.
     * @return the current state of the achievement.
     */
    public int getState() {
        return mState;
    }

    /**
     * Changes the current state of the achievement.
     * @param state The new state of the achievement.
     */
    protected void setState(int state) {
        mState = state;
    }

    /* (non-Javadoc)
     * @see com.sonymobile.evolutionui.Item#getIconRes()
     */
    @Override
    public String getIconRes() {
        return "ach_" + getId().replace('.', '_');
    }

    /* (non-Javadoc)
     * @see com.sonymobile.evolutionui.Item#getDefaultIconRes()
     */
    @Override
    public String getDefaultIconRes() {
        return "ach_unknown";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Implementation details below
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int mState;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getId());
        out.writeString(getName());
        out.writeString(getDescription());
        out.writeInt(mState);
    }

    public static final Parcelable.Creator<Achievement> CREATOR = new Parcelable.Creator<Achievement>() {
        public Achievement createFromParcel(Parcel in) {
            return new Achievement(in);
        }

        public Achievement[] newArray(int size) {
            return new Achievement[size];
        }
    };

    private Achievement(Parcel in) {
        setId(in.readString());
        setName(in.readString());
        setDescription(in.readString());
        mState = in.readInt();
    }

}
