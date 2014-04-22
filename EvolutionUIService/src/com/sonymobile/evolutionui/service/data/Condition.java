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

import java.util.Vector;

public class Condition {

    public static final int TYPE_INVALID = 0;
    public static final int TYPE_AND = 1;
    public static final int TYPE_OR = 2;
    public static final int TYPE_NOT = 3;
    public static final int TYPE_IF_COUNT = 4;
    public static final int TYPE_COUNT = 5;

    public static final String TYPE_NAMES[] = {
        "invalid",
        "and",
        "or",
        "not",
        "ifcount",
    };

    private int mType = TYPE_INVALID;

    private Vector<Condition> mChildren = new Vector<Condition>();
    private String mExperienceId;
    private ExperienceDesc mExperience;
    private int mTrigger;

    public Condition(XMLNode child) {
        // condition == and (for convenience)
        String name = child.getName();
        if (name.equals("condition")) {
            name = "and";
        }

        // Find type
        for (int i = 0; i < TYPE_COUNT; i++) {
            if (name.equals(TYPE_NAMES[i])) {
                mType = i;
                break;
            }
        }

        // Fetch extra parameters
        switch (mType) {
            case TYPE_IF_COUNT:
                mExperienceId = child.getAttribute("experience");
                mTrigger = Integer.parseInt(child.getAttribute("count"));
                break;

            default:
                break;
        }

        // Fetch children
        for (int i = 0; i < child.getChildCount(); i++) {
            Condition c = new Condition(child.getChild(i));
            mChildren.add(c);
        }

    }

    public int getChildCount() {
        return mChildren.size();
    }

    public Condition getChild(int idx) {
        return mChildren.get(idx);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(TYPE_NAMES[mType]);
        switch (mType) {
            case TYPE_IF_COUNT:
                sb.append(" experience=");
                sb.append(mExperienceId);
                sb.append(" count=");
                sb.append(mTrigger);
                break;
        }
        return sb.toString();
    }

    public boolean dependsOn(ExperienceDesc exp) {
        if (mType == TYPE_IF_COUNT) {
            if (mExperience == exp) {
                return true;
            }
        }

        for (Condition child : mChildren) {
            if (child.dependsOn(exp)) {
                return true;
            }
        }

        return false;
    }

    public boolean eval() {
        switch (mType) {
            case TYPE_AND:
                for (Condition child : mChildren) {
                    if (!child.eval()) {
                        return false;
                    }
                }
                return true;
            case TYPE_OR:
                for (Condition child : mChildren) {
                    if (child.eval()) {
                        return true;
                    }
                }
                return mChildren.size() == 0;
            case TYPE_NOT:
                if (mChildren.size() != 1) {
                    return false;
                }
                return !mChildren.get(0).eval();
            case TYPE_IF_COUNT:
                return mExperience.getCounter() >= mTrigger;
            default:
                return false;
        }
    }

    public void lookup(EvUIModel model) {
        if (mType == TYPE_IF_COUNT) {
            mExperience = model.getExperience(mExperienceId);
        }
        for (Condition child : mChildren) {
            child.lookup(model);
        }
    }


}
