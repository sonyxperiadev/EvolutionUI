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

/**
 * This class contains the constants used in the Intents, when sending messages to the
 * EvolutionUI service.
 */
public class EvolutionUIIntents {

    private static final String PKG = "com.sonymobile.evolutionui";

    /**
     * This Intent action is used when the user changed the current profile.
     * Note that the {@link #EXTRA_PROFILE_DYNAMIC} and {@link #EXTRA_PROFILE_LEVEL} intent
     * parameters are mandatory.
     */
    public static final String ACTION_PROFILE_CHANGED = PKG + ".action.PROFILE_CHANGED";

    /**
     * Intent parameter which stores the ID of the selected profile.
     */
    public static final String EXTRA_PROFILE_LEVEL = PKG + ".extra.PROFILE_LEVEL";
    /**
     * Intent parameter which stores true, if the dynamic feature unlocking is enabled, and false
     * otherwise. In other words, if this value is set to false, the whole achievement system
     * will be disabled, and only the features automatically activated by the given profile will
     * be available.
     */
    public static final String EXTRA_PROFILE_DYNAMIC = PKG + ".extra.PROFILE_DYNAMIC";

    /**
     * This Intent action is used by applications to send experience notifications to the services.
     * This is an alternative to connecting to the service directly (this solution can be simpler
     * for some applications).
     * Note that the {@link #EXTRA_EXPERIENCE_ID} intent parameter is mandatory.
     */
    public static final String ACTION_EXPERIENCE = PKG + ".action.EXPERIENCE";

    /**
     * Intent parameter which stores the unique ID of the experience the user got (by performing
     * a certain action).
     */
    public static final String EXTRA_EXPERIENCE_ID = PKG + ".extra.EXPERIENCE_ID";

}
