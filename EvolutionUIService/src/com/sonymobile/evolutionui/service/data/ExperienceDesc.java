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

import com.sonymobile.evolutionui.Domain;
import com.sonymobile.tools.xappdbg.properties.XAppDbgPropDescr;

public class ExperienceDesc {

    private String mId;
    private String mName;
    private int mXP;

    private int mCounter;
    private Domain mDomain;
    private EvUIModel mModel;

    public ExperienceDesc(EvUIModel model, XMLNode item, Domain domain) {
        mModel = model;
        mId = item.getAttribute("id");
        mName = item.getAttribute("name");
        mXP = Integer.parseInt(item.getAttribute("xp"));
        mDomain = domain;
    }

    @XAppDbgPropDescr("Internal id of the experience (user action). This one is used to reference it from achievements.")
    public String getId() {
        return mId;
    }

    @XAppDbgPropDescr("Human readable name of the experience (user action)")
    public String getName() {
        return mName;
    }

    @XAppDbgPropDescr("How much XP (experience points) the user gets every time when performing the action.")
    public int getXP() {
        return mXP;
    }

    /* package */ Domain getDomain() {
        return mDomain;
    }

    @Override
    public String toString() {
        return "ExperienceDesc [mId=" + mId + ", mName=" + mName + ", mXP=" + mXP + "]";
    }

    /* package */ int increment() {
        mCounter++;
        return mXP;
    }

    @XAppDbgPropDescr("How many times the action was performed by the user.")
    public int getCounter() {
        return mCounter;
    }

    @XAppDbgPropDescr("Reset the internal counter.")
    public void reset() {
        mCounter = 0;
    }

    /* package */ void save(XMLNode node) {
        XMLNode item = new XMLNode("experience", node);
        item.setAttribute("id", getId());
        item.setAttribute("count", Integer.toString(getCounter()));
    }

    /* package */ void load(XMLNode node) {
        XMLNode item = node.findNodeByAttr("id", getId());
        if (item != null) {
            mCounter = Integer.parseInt(item.getAttribute("count"));
        }
    }

    /**********************************************************************************************
     * For debugging and testing only
     **********************************************************************************************/

    @XAppDbgPropDescr("Simulate the experience, i.e. pretend the user performed the action.")
    public void simulate() {
        mModel.onNewExperience(mId);
    }


}
