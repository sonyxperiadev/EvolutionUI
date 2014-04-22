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
 * Common class for all the base concepts (experiences, features, etc).
 */
public abstract class BasicConcept {

    /**
     * Returns the unique ID of this item.
     * This value is used internally, and is not visible to the user.
     * Each ID must be unique inside the same domain (package).
     * @return the unique ID of this item.
     */
    public String getId() {
        return mId;
    }

    /**
     * Returns the package this item belongs to.
     * To avoid naming conflicts, each basic concept will be marked with the originating package.
     * @return the the package this item belongs to.
     */
    public Domain getDomain() {
        return mDomain;
    }

    /**
     * Returns the name of the item, localized.
     * This is the name visible to the user. For example the name of a feature can be "Send MMS".
     * The name of an achievement can be "Sent first SMS".
     * @return the name of the item, localized.
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns the longer explanation of the item.
     * For example if the name of a feature is "Send MMS", the description should contain a longer
     * explanation, for example "This feature allows the user to send Multimedia Messages as well".
     * @return the longer explanation of the item.
     */
    public String getDescription() {
        return mDescr;
    }

    /**
     * Returns the name of the drawable resources which contains the icon (image representation)
     * of this item. The resource name is usually derived from the ID of the item.
     * @return the name of the icon resource.
     */
    public abstract String getIconRes();


    /**
     * Returns the name of the fallback icon resource, in case the unique one is not found.
     * This is the name of the generic feature icon or achievement icon.
     * @return the name of the fallback icon resource.
     */
    public abstract String getDefaultIconRes();

    /**
     * Changes the ID of the item.
     * <p>
     * Note: these setter functions should probably be called by the service only, and not the
     * clients of the API.
     * @param id the new ID.
     * @see BasicConcept#getId()
     */
    protected void setId(String id) {
        mId = id;
    }

    /**
     * Changes the name of the item.
     * <p>
     * Note: these setter functions should probably be called by the service only, and not the
     * clients of the API.
     * @param name the new name of the item.
     * @see BasicConcept#getName()
     */
    protected void setName(String name) {
        mName = name;
    }

    /**
     * Changes the description of the item.
     * @param descr the new description of the item.
     * @see BasicConcept#getDescription()
     */
    protected void setDescription(String descr) {
        mDescr = descr;
    }

    /**
     * Changes the domain of the item.
     * <p>
     * Note: these setter functions should probably be called by the service only, and not the
     * clients of the API.
     * @param domain the new domain (package name) of the item.
     * @see BasicConcept#getDomain()
     */
    protected void setDomain(Domain domain) {
        mDomain = domain;
    }

    private String mId;
    private String mName;
    private String mDescr;
    private Domain mDomain;

}
