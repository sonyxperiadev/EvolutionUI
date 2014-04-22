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
 * A coin can be used to "buy" features.
 * Coins can be obtained when leveling up and/or with obtaining achievements.
 */
public class Coin extends BasicConcept implements Parcelable {

    public Coin() {
    }

    /**
     * Create a coin with the given ID.
     * @param id the coin's ID.
     */
    public Coin(String id) {
        setId(id);
    }

    @Override
    public String getIconRes() {
        return "coin_" + getId().replace('.', '_');
    }

    @Override
    public String getDefaultIconRes() {
        return "coin_unknown";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Implementation details below
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getId());
        out.writeString(getName());
        out.writeString(getDescription());
    }

    public static final Parcelable.Creator<Coin> CREATOR = new Parcelable.Creator<Coin>() {
        @Override
        public Coin createFromParcel(Parcel in) {
            return new Coin(in);
        }

        @Override
        public Coin[] newArray(int size) {
            return new Coin[size];
        }
    };

    private Coin(Parcel in) {
        setId(in.readString());
        setName(in.readString());
        setDescription(in.readString());
    }

}
