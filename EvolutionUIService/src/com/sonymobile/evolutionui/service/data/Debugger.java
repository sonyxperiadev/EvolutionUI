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

import com.sonymobile.evolutionui.service.EvolutionUIService;
import com.sonymobile.tools.xappdbg.properties.XAppDbgPropDescr;
import com.sonymobile.tools.xappdbg.properties.XAppDbgTreeParser;

/**
 * This class is used only for debugging.
 * Using the XAppDbg tool, it exposes the internal state, so the developer can monitor it and
 * even change it run-time from a PC. (and by PC I mean any Windows, Linux and even Mac
 * computer ;-p)
 */
public class Debugger implements XAppDbgTreeParser {

    private static final int CHILD_FEATURES = 0;
    private static final int CHILD_EXPERIENCES = 1;
    private static final int CHILD_ACHIEVEMENTS = 2;
    private static final int CHILD_LEVELS = 3;
    private static final int CHILD_COUNT = 4;

    private EvolutionUIService mService;

    public Debugger(EvolutionUIService service) {
        mService = service;
    }

    public int getCurrentLevel() {
        return mService.getModel().getCurLevel();
    }

    public int getCurrentXP() {
        return mService.getModel().getCurXP();
    }

    @XAppDbgPropDescr("Changes the current profile level")
    public void setProfileLevel(int level) {
        EvUIModel model = mService.getModel();
        model.setProfile(level, model.isProfileDynamic());
    }

    @XAppDbgPropDescr("Changes the current profile type")
    public void setProfileDynamic(boolean dynamic) {
        EvUIModel model = mService.getModel();
        model.setProfile(model.getProfileLevel(), dynamic);
    }

    @XAppDbgPropDescr("Adds some experience points")
    public void addXP(int xp) {
        mService.getModel().addXP(xp);
    }

    @XAppDbgPropDescr("Reset Level and XP")
    public void resetLevelAndXP() {
        mService.getModel().resetStats();
    }

    @Override
    public Object getChild(Object parent, int idx) {
        EvUIModel model = mService.getModel();
        Vector<FeatureDesc> features = model.getFeatures();
        Vector<ExperienceDesc> experiences = model.getExperiences();
        Vector<AchievementDesc> achievements = model.getAchievements();
        Vector<LevelDesc> levels = model.getLevels();

        // Check root node
        if (parent == this) {
            switch (idx) {
                case CHILD_FEATURES: return features;
                case CHILD_EXPERIENCES: return experiences;
                case CHILD_ACHIEVEMENTS: return achievements;
                case CHILD_LEVELS: return levels;
            }
            return null;
        }

        // Achievements could have conditions
        if (parent instanceof AchievementDesc) {
            AchievementDesc achievement = (AchievementDesc)parent;
            Condition cond = achievement.getCondition();
            return cond;
        }

        // Handle condition
        if (parent instanceof Condition) {
            Condition cond = (Condition)parent;
            return cond.getChild(idx);
        }

        // Check profiles, achievements, etc
        if (parent == features) return features.get(idx);
        if (parent == experiences) return experiences.get(idx);
        if (parent == achievements) return achievements.get(idx);
        if (parent == levels) return levels.get(idx);

        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        EvUIModel model = mService.getModel();
        Vector<FeatureDesc> features = model.getFeatures();
        Vector<ExperienceDesc> experiences = model.getExperiences();
        Vector<AchievementDesc> achievements = model.getAchievements();
        Vector<LevelDesc> levels = model.getLevels();

        // Check root node
        if (parent == this) {
            return CHILD_COUNT;
        }

        // Check profiles, achievements, etc
        if (parent == features) return features.size();
        if (parent == experiences) return experiences.size();
        if (parent == achievements) return achievements.size();
        if (parent == levels) return levels.size();

        // Achievements could have conditions
        if (parent instanceof AchievementDesc) {
            AchievementDesc achievement = (AchievementDesc)parent;
            Condition cond = achievement.getCondition();
            return cond == null ? 0 : 1;
        }

        // Handle condition
        if (parent instanceof Condition) {
            Condition cond = (Condition)parent;
            return cond.getChildCount();
        }

        return 0;
    }

    @Override
    public String getText(Object node) {
        EvUIModel model = mService.getModel();
        Vector<FeatureDesc> features = model.getFeatures();
        Vector<ExperienceDesc> experiences = model.getExperiences();
        Vector<AchievementDesc> achievements = model.getAchievements();
        Vector<LevelDesc> levels = model.getLevels();

        // Check root node
        if (node == this) {
            return "Root - Debug commands";
        }

        // Check profiles, achievements, etc
        if (node == features) return "Features";
        if (node == experiences) return "Experiences";
        if (node == achievements) return "Achievements";
        if (node == levels) return "Levels";

        return node.toString();
    }

}

