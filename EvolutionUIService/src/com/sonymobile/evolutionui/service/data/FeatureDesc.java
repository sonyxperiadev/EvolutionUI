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
import com.sonymobile.evolutionui.Feature;
import com.sonymobile.tools.xappdbg.properties.XAppDbgPropDescr;

import android.os.RemoteException;

import java.util.Vector;

public class FeatureDesc extends Feature {

    private EvUIModel mModel;
    private Vector<String> mDepends = new Vector<String>();

    public FeatureDesc(EvUIModel model, XMLNode item, Domain domain) {
        mModel = model;

        setId(item.getAttribute("id"));
        setName(item.getAttribute("name"));
        setProfile(Integer.parseInt(item.getAttribute("profile")));
        setDescription(item.getAttribute(XMLNode.TEXT_ATTR));
        setDomain(domain);
        for (int i = 0; i < item.getChildCount(); i++) {
            XMLNode child = item.getChild(i);
            if ("depends".equals(child.getName())) {
                mDepends.add(child.getAttribute("on"));
            }
        }
    }

    @Override
    public void onStateChanged(int level) {
        try {
            mModel.getService().onFeatureChanged(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "FeatureDesc [mDescr=" + getDescription() + ", mId=" + getId() + ", mName=" + getName()
                + ", mProfile=" + getProfile() + "]";
    }

    @XAppDbgPropDescr("Reset the state of the feature (i.e. disabled)")
    public void reset() {
        setEnabledByUser(STATE_DISABLED);
    }

    /* package */ void save(XMLNode node) {
        XMLNode item = new XMLNode("feature", node);
        item.setAttribute("id", getId());
        item.setAttribute("state", Integer.toString(isEnabledByUser()));
    }

    /* package */ void load(XMLNode node) {
        XMLNode item = node.findNodeByAttr("id", getId());
        if (item != null) {
            setEnabledByUser(Integer.parseInt(item.getAttribute("state")));
        }
    }

    /* package */ Vector<String> getDependencies() {
        return mDepends;
    }

}
