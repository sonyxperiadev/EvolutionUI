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
 * Represent information about a specific application package.
 * Domains are basically application packages: each application's items will be grouped into a
 * domain identified by the package name.
 * <p>
 * This class is used to retrieve/transfer detailed information about a given domain/package.
 * Currently only the version number is used, apart from the package name. The version number is
 * needed to detect updates to the application, which might contain updated rules.
 */
public class Domain implements Parcelable {

    public Domain() {
    }

    /**
     * Create a Domain information with the given package name and version number.
     * @param pkg the package name.
     * @param ver the version number.
     */
    public Domain(String pkg, int ver) {
        mName = pkg;
        mVersion = ver;
    }

    /**
     * Returns the package name.
     * @return the package name.
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns the version number of the package.
     * @return the version number of the package.
     */
    public int getVersion() {
        return mVersion;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Implementation details below
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private String mName;
    private int mVersion;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mName);
        out.writeInt(mVersion);
    }

    public static final Parcelable.Creator<Domain> CREATOR = new Parcelable.Creator<Domain>() {
        @Override
        public Domain createFromParcel(Parcel in) {
            return new Domain(in);
        }

        @Override
        public Domain[] newArray(int size) {
            return new Domain[size];
        }
    };

    private Domain(Parcel in) {
        mName = in.readString();
        mVersion = in.readInt();
    }

}
