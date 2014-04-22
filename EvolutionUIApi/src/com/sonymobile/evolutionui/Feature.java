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

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * A Functionality represents a possible use case of the phone.
 * <p>
 * For example a functionality is to send an SMS.
 * Functionalities can be unlocked through Achievements or Levels.
 * <p>
 * Note the distinction between unlocking a feature and activating a feature:
 * <ul>
 * <li>A feature is unlocked (or enabled) by achievements. This means the user can activate it, if
 * he/she wants. But without activating it, nothing will change in the application.</li>
 * <li>By activating a feature, the application will change allowing access to the new
 * functionality.</li>
 * </ul>
 * <p>
 * Features can also be activated automatically by profiles. For example if the user does not
 * wish to start with the lowest set of features, he/she can switch to a medium profile, which
 * will activate a part of the features by default (for example the MMS functionality can be
 * automatically activated from start, but the delivery notification needs to be unlocked first
 * via an achievement).
 */
public class Feature extends BasicConcept implements Parcelable {

    /** The feature is not available */
    public static final int STATE_DISABLED = 0;
    /** The user could turn on the feature */
    public static final int STATE_ENABLED = 1;
    /** The user has seen the feature */
    public static final int STATE_SEEN = 2;
    /** The feature is available */
    public static final int STATE_ACTIVATED = 3;

    public Feature() {
    }

    public Feature(Context context, int resId) {
        setId(context.getString(resId));
    }

    public Feature(String id) {
        setId(id);
    }

    public int getProfile() {
        return mProfile;
    }

    protected void setProfile(int profile) {
        mProfile = profile;
    }

    public void setEnabledInProfile(boolean enabled, boolean dynamicEnabled) {
        mEnabledInProfile = enabled;
        mEnabledDynamic = dynamicEnabled;
        recalc();
    }

    public void setEnabledByUser(int enabled) {
        mEnabledByUser = enabled;
        recalc();
    }

    public void enableByRule() {
        if (mEnabledByUser == STATE_DISABLED) {
            mEnabledByUser = STATE_ENABLED;
            recalc();
        }
    }

    private void recalc() {
        int newState;
        if (mEnabledInProfile) {
            newState = STATE_ACTIVATED;
        } else if (mEnabledDynamic) {
            newState = mEnabledByUser;
        } else {
            newState = STATE_DISABLED;
        }

        if (newState != mEnabled) {
            mEnabled = newState;
            onStateChanged(newState);
        }
    }

    public void onStateChanged(int level) {
        // This line should be needed only on the client side
        mEnabled = level;
    }

    public boolean isEnabledInProfile() {
        return mEnabledInProfile;
    }

    public int isEnabledByUser() {
        return mEnabledByUser;
    }

    public int isEnabled() {
        return mEnabled;
    }

    public boolean isActivated() {
        return mEnabled == STATE_ACTIVATED;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Implementation details below
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int mProfile;
    private boolean mEnabledInProfile = false;
    private boolean mEnabledDynamic = false;
    private int mEnabledByUser = STATE_DISABLED;
    private int mEnabled = STATE_DISABLED;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getId());
        out.writeString(getName());
        out.writeString(getDescription());
        out.writeInt(mProfile);
        out.writeInt(mEnabledInProfile ? 1 : 0);
        out.writeInt(mEnabledDynamic ? 1 : 0);
        out.writeInt(mEnabledByUser);
        out.writeInt(mEnabled);
    }

    public static final Parcelable.Creator<Feature> CREATOR = new Parcelable.Creator<Feature>() {
        @Override
        public Feature createFromParcel(Parcel in) {
            return new Feature(in);
        }

        @Override
        public Feature[] newArray(int size) {
            return new Feature[size];
        }
    };

    private Feature(Parcel in) {
        setId(in.readString());
        setName(in.readString());
        setDescription(in.readString());
        mProfile = in.readInt();
        mEnabledInProfile = in.readInt() != 0;
        mEnabledDynamic = in.readInt() != 0;
        mEnabledByUser = in.readInt();
        mEnabled = in.readInt();
    }

    @Override
    public String getIconRes() {
        return "feat_" + getId().replace('.', '_');
    }

    @Override
    public String getDefaultIconRes() {
        return "feat_unknown";
    }

}
