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

import com.sonymobile.evolutionui.Achievement;
import com.sonymobile.evolutionui.Coin;
import com.sonymobile.evolutionui.Domain;
import com.sonymobile.tools.xappdbg.properties.XAppDbgPropDescr;

import java.util.Vector;

public class AchievementDesc extends Achievement {

    private EvUIModel mModel;

    private Condition mCond;
    private Vector<FeatureDesc> mFeatures = new Vector<FeatureDesc>();
    private Vector<String> mCoins = new Vector<String>();

    public AchievementDesc(EvUIModel model, XMLNode item, Domain domain) {
        mModel = model;

        setId(item.getAttribute("id"));
        setName(item.getAttribute("name"));
        setDomain(domain);
        XMLNode child = item.findChild("condition");
        if (child != null) {
            mCond = new Condition(child);
        }
        child = item.findChild("description");
        if (child != null) {
            setDescription(child.getAttribute(XMLNode.TEXT_ATTR));
        }

        for (int i = 0; i < item.getChildCount(); i++) {
            child = item.getChild(i);
            if (child.getName().equals("enable")) {
                String featureId = child.getAttribute("feature");
                if (featureId != null) {
                    FeatureDesc feature = model.getFeature(featureId);
                    if (feature != null) {
                        mFeatures.add(feature);
                    }
                }
                String coinId = child.getAttribute("coin");
                if (coinId != null) {
                    mCoins.add(coinId);
                }
            }
        }

    }

    public EvUIModel getModel() {
        return mModel;
    }

    @Override
    public String toString() {
        return "AchievementDesc [mId=" + getId() + ", mName=" + getName() + "]";
    }

    @XAppDbgPropDescr("Assuming the condition is met, execute the achievement, i.e. enable features, give coins, etc.")
    public void execute() {
        for (FeatureDesc f : mFeatures) {
            f.enableByRule();
        }
        for (String coin : mCoins) {
            mModel.addCoin(new Coin(coin));
        }
    }

    /* package */ boolean dependsOn(ExperienceDesc exp) {
        if (mCond != null) {
            return mCond.dependsOn(exp);
        }
        return false;
    }

    /* package */ boolean check() {
        // If already achieved, don't do anything
        if (getState() != STATE_NONE) return false;

        // Check special case when condition is missing
        if (mCond == null) return false;

        // Evaluate condition
        boolean ret = mCond.eval();

        return ret;
    }

    /* package */ Condition getCondition() {
        return mCond;
    }

    /* package */ void lookup(EvUIModel model) {
        if (mCond != null) {
            mCond.lookup(model);
        }
    }

    @Override
    public void setState(int state) {
        super.setState(state);
    }

    @XAppDbgPropDescr("Reset the achievement state.")
    public void reset() {
        setState(STATE_NONE);
    }

    /* package */ void save(XMLNode node) {
        XMLNode item = new XMLNode("achievement", node);
        item.setAttribute("id", getId());
        item.setAttribute("state", Integer.toString(getState()));
    }

    /* package */ void load(XMLNode node) {
        XMLNode item = node.findNodeByAttr("id", getId());
        if (item != null) {
            setState(Integer.parseInt(item.getAttribute("state")));
        }
    }

    /**********************************************************************************************
     * For debugging and testing only
     **********************************************************************************************/

    @XAppDbgPropDescr("Fake/Simulate the achievement")
    public void obtainAchievement() {
        getModel().onNewAchievement(this);
    }

}
