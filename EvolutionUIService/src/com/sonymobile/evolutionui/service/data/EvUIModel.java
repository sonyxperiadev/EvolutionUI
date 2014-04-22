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
import com.sonymobile.evolutionui.EvolutionUIIntents;
import com.sonymobile.evolutionui.Feature;
import com.sonymobile.evolutionui.R;
import com.sonymobile.evolutionui.service.EvolutionUIService;
import com.sonymobile.evolutionui.service.Util;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

public class EvUIModel {

    private static final String TAG = "EvUIModel";

    private static final int FREE_FEATURE_UNLOCK_COUNT = 3;

    private EvolutionUIService mService;

    private Vector<FeatureDesc> mFeaturesArr = new Vector<FeatureDesc>();
    private HashMap<String, FeatureDesc> mFeatures = new HashMap<String, FeatureDesc>();

    private Vector<ExperienceDesc> mExperiencesArr = new Vector<ExperienceDesc>();
    private HashMap<String, ExperienceDesc> mExperiences = new HashMap<String, ExperienceDesc>();

    private Vector<AchievementDesc> mAchievementsArr = new Vector<AchievementDesc>();
    private HashMap<String, AchievementDesc> mAchievements = new HashMap<String, AchievementDesc>();

    private Vector<LevelDesc> mLevels = new Vector<LevelDesc>();

    // --- begin data to save --- //
    private int mProfileLevel = 1;
    private boolean mProfileDynamic = true;

    private int mCurLevel = 1;
    private int mCurXP = 0;

    private Vector<AchievementDesc> mPendingAchiements = new Vector<AchievementDesc>();
    private Vector<FeatureDesc> mPendingFeatures = new Vector<FeatureDesc>();
    private Vector<Coin> mCoins = new Vector<Coin>();

    private int mStatusScreenShown;
    // --- end data to save --- //

    private boolean mDataLoaded;

    public EvUIModel(EvolutionUIService service) {
        mService = service;
    }

    public void onCreate() {
        loadData(mService, R.xml.data);
    }

    public void finishLoad() {
        // After parsing the data, we still have a few tasks to do

        // Look up experiences (and maybe other stuff) in achievements/conditions
        for (AchievementDesc item : mAchievementsArr) {
            item.lookup(this);
        }

        // Load the configuration
        load();

        // Update the features based on the default profile
        updateProfile();

        // Finished loading data, from now on we can save data
        mDataLoaded = true;
    }

    public void loadData(Context context, int resId) {
        // First parse the data xml file
        Domain domain = new Domain(context.getPackageName(), 0);
        Resources res = context.getResources();
        XmlResourceParser xml = res.getXml(resId);
        try {
            XMLNode root = XMLNode.parse(context, xml);
            if (!root.getName().equals("data")) {
                throw new XmlPullParserException("The root node must be 'data'");
            }
            for (int i = 0; i < root.getChildCount(); i++) {
                XMLNode item = root.getChild(i);
                String type = item.getName();
                if (type == null) {
                    Log.e(TAG, "Text found outside of item in the data.xml file, ignoring it");
                } else if (type.equals("feature")) {
                    addFeature(new FeatureDesc(this, item, domain));
                } else if (type.equals("experience")) {
                    addExperience(new ExperienceDesc(this, item, domain));
                } else if (type.equals("achievement")) {
                    addAchievement(new AchievementDesc(this, item, domain));
                } else if (type.equals("level")) {
                    if (!context.getPackageName().equals("com.sonymobile.evolutionui")) {
                        throw new RuntimeException("Only the core package can define levels!");
                    }
                    addLevel(new LevelDesc(this, item));
                } else {
                    Log.e(TAG, "Unknown item in the data.xml file: " + type + " (ignoring it)");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateProfile() {
        // Now go through all features and set them based on the profile
        for (FeatureDesc f : mFeaturesArr) {
            boolean enabled = f.getProfile() <= mProfileLevel;
            boolean dynamicEnabled = mProfileDynamic;
            f.setEnabledInProfile(enabled, dynamicEnabled);
        }

        // Notify other apps that the profile has changed
        Intent intent = new Intent(EvolutionUIIntents.ACTION_PROFILE_CHANGED);
        intent.putExtra(EvolutionUIIntents.EXTRA_PROFILE_LEVEL, mProfileLevel);
        intent.putExtra(EvolutionUIIntents.EXTRA_PROFILE_DYNAMIC, mProfileDynamic);
        getService().sendBroadcast(intent);
    }

    public void setProfile(int level, boolean dynamic) {
        mProfileLevel = level;
        mProfileDynamic = dynamic;
        updateProfile();
    }

    public boolean isProfileDynamic() {
        return mProfileDynamic;
    }

    public int getProfileLevel() {
        return mProfileLevel;
    }

    private void addLevel(LevelDesc levelDesc) {
        int idx = levelDesc.getNumber();
        while (mLevels.size() <= idx) {
            mLevels.add(null);
        }
        mLevels.set(idx, levelDesc);
    }

    private void addAchievement(AchievementDesc achievementDesc) {
        mAchievements.put(achievementDesc.getId(), achievementDesc);
        mAchievementsArr.add(achievementDesc);
    }

    private void addExperience(ExperienceDesc experienceDesc) {
        mExperiences.put(experienceDesc.getId(), experienceDesc);
        mExperiencesArr.add(experienceDesc);
    }

    private void addFeature(FeatureDesc featureDesc) {
        mFeatures.put(featureDesc.getId(), featureDesc);
        mFeaturesArr.add(featureDesc);
    }

    public void onDestroy() {
    }

    public Vector<AchievementDesc> getAchievements() {
        return mAchievementsArr;
    }

    public Vector<FeatureDesc> getFeatures() {
        return mFeaturesArr;
    }

    public Vector<ExperienceDesc> getExperiences() {
        return mExperiencesArr;
    }

    public Vector<LevelDesc> getLevels() {
        return mLevels;
    }

    public EvolutionUIService getService() {
        return mService;
    }

    public int getFeatureState(String featureId) {
        FeatureDesc f = mFeatures.get(featureId);
        if (f != null) {
            return f.isEnabled();
        } else {
            return FeatureDesc.STATE_DISABLED;
        }
    }

    public void onNewExperience(String experience) {
        // Look up experience
        ExperienceDesc exp = mExperiences.get(experience);
        if (exp == null) {
            Log.e(TAG, "Experience '" + experience + "' not found! Ignoring it.");
            if (Util.STRICT_MODE) {
                throw new RuntimeException("Experience '" + experience + "' not found!");
            } else {
                return;
            }
        }

        // Increment the counter
        int newXP = exp.increment();

        // Increment the XP
        if (newXP > 0) {
            addXP(newXP);
        }

        // Check if we just achieved something
        // TODO: this can be optimized for bigger data sets which has tables, pre-search, etc
        for (AchievementDesc item : mAchievementsArr) {
            if (item.dependsOn(exp)) {
                if (item.check()) {
                    onNewAchievement(item);
                }
            }
        }

        // Save new stats
        save();
    }

    public void onNewAchievement(AchievementDesc item) {
        // Change achievement state
        item.setState(AchievementDesc.STATE_ACHIEVED);

        // Notify user
        mService.onNewAchievement(item);

        // Execute achievement
        item.execute();

    }

    public void addXP(int xp) {
        mCurXP += xp;

        while (true) {
            LevelDesc nextLevel = getNextLevelDesc();
            if (nextLevel == null) {
                // No more levels
                break;
            }

            if (mCurXP < nextLevel.getTrigger()) {
                // Not reached next level yet
                break;
            }

            mCurLevel++;
            onNewLevel(nextLevel);
        }

        // save new stat
        save();
    }

    private void onNewLevel(LevelDesc item) {
        // Notify user
        mService.onNewLevel(item);
    }

    public LevelDesc getNextLevelDesc() {
        if (mCurLevel + 1 >= mLevels.size()) {
            return null;
        }
        return mLevels.get(mCurLevel + 1);
    }

    public LevelDesc getCurLevelDesc() {
        if (mCurLevel >= mLevels.size()) {
            return null;
        }
        return mLevels.get(mCurLevel);
    }

    public ExperienceDesc getExperience(String id) {
        return mExperiences.get(id);
    }

    public FeatureDesc getFeature(String id) {
        return mFeatures.get(id);
    }

    public AchievementDesc getAchievement(String id) {
        return mAchievements.get(id);
    }

    public int getCurLevel() {
        return mCurLevel;
    }

    public int getCurXP() {
        return mCurXP;
    }

    public void resetStats() {
        mCurLevel = 1;
        mCurXP = 0;
        mProfileLevel = 1;
        mProfileDynamic = true;

        mPendingAchiements.clear();
        mPendingFeatures.clear();
        mCoins.clear();
        mStatusScreenShown = 0;

        for (ExperienceDesc item : mExperiencesArr) {
            item.reset();
        }
        for (AchievementDesc item : mAchievementsArr) {
            item.reset();
        }
        for (FeatureDesc item : mFeaturesArr) {
            item.reset();
        }
        // Save new stats
        save();
    }

    public void save() {
        // Cannot save until all data is loaded
        if (!mDataLoaded) return;

        // Collect information
        XMLNode config = new XMLNode("config", null);
        config.setAttribute("level", Integer.toString(mCurLevel));
        config.setAttribute("xp", Integer.toString(mCurXP));
        config.setAttribute("profileLevel", mProfileLevel);
        config.setAttribute("profileDynamic", mProfileDynamic);
        config.setAttribute("shown", Integer.toString(mStatusScreenShown));
        saveCoins(config);
        savePendingAchievements(config);
        savePendingFeatures(config);
        saveExperiences(config);
        saveAchievements(config);
        saveFeatures(config);

        // Save to disk
        File dir = getService().getDir("files", 0);
        File f = new File(dir, "config");
        config.writeDocumentTo(f);
    }

    public void load() {
        // Load from disk
        File dir = getService().getDir("files", 0);
        File f = new File(dir, "config");
        XMLNode config = XMLNode.readDocument(getService(), f);
        if (config == null) return;
        String s;

        // Restore information
        s = config.getAttribute("level");
        if (s != null) {
            mCurLevel = Integer.parseInt(s);
        }
        s = config.getAttribute("xp");
        if (s != null) {
            mCurXP = Integer.parseInt(s);
        }
        s = config.getAttribute("profileLevel");
        if (s != null) {
            mProfileLevel = Integer.parseInt(s);
        }
        s = config.getAttribute("profileDynamic");
        if (s != null) {
            mProfileDynamic = Boolean.parseBoolean(s);
        }
        s = config.getAttribute("shown");
        if (s != null) {
            mStatusScreenShown = Integer.parseInt(s);
        }
        loadCoins(config);
        loadPendingAchievements(config);
        loadPendingFeatures(config);
        loadExperiences(config);
        loadAchievements(config);
        loadFeatures(config);

    }

    private void saveCoins(XMLNode config) {
        XMLNode node = new XMLNode("coins", config);
        for (Coin item : mCoins) {
            XMLNode child = new XMLNode("item", node);
            child.setAttribute("id", item.getId());
        }
    }

    private void loadCoins(XMLNode config) {
        XMLNode node = config.findNodeByName(config, "coins");
        if (node != null) {
            mCoins.clear();
            for (int i = 0; i < node.getChildCount(); i++) {
                XMLNode child = node.getChild(i);
                String id = child.getAttribute("id");
                Coin item = new Coin(id);
                mCoins.add(item);
            }
        }
    }

    private void savePendingAchievements(XMLNode config) {
        XMLNode node = new XMLNode("pendingAchievements", config);
        for (AchievementDesc item : mPendingAchiements) {
            XMLNode child = new XMLNode("item", node);
            child.setAttribute("id", item.getId());
        }
    }

    private void loadPendingAchievements(XMLNode config) {
        XMLNode node = config.findNodeByName(config, "pendingAchievements");
        if (node != null) {
            mPendingAchiements.clear();
            for (int i = 0; i < node.getChildCount(); i++) {
                XMLNode child = node.getChild(i);
                String id = child.getAttribute("id");
                AchievementDesc item = mAchievements.get(id);
                mPendingAchiements.add(item);
            }
        }
    }

    private void savePendingFeatures(XMLNode config) {
        XMLNode node = new XMLNode("pendingFeatures", config);
        for (FeatureDesc item : mPendingFeatures) {
            XMLNode child = new XMLNode("item", node);
            child.setAttribute("id", item.getId());
        }
    }

    private void loadPendingFeatures(XMLNode config) {
        XMLNode node = config.findNodeByName(config, "pendingFeatures");
        if (node != null) {
            mPendingFeatures.clear();
            for (int i = 0; i < node.getChildCount(); i++) {
                XMLNode child = node.getChild(i);
                String id = child.getAttribute("id");
                FeatureDesc item = mFeatures.get(id);
                mPendingFeatures.add(item);
            }
        }
    }

    private void saveExperiences(XMLNode config) {
        XMLNode node = new XMLNode("experiences", config);
        for (ExperienceDesc item : mExperiencesArr) {
            item.save(node);
        }
    }

    private void loadExperiences(XMLNode config) {
        XMLNode node = config.findNodeByName(config, "experiences");
        if (node != null) {
            for (ExperienceDesc item : mExperiencesArr) {
                item.load(node);
            }
        }
    }

    private void saveAchievements(XMLNode config) {
        XMLNode node = new XMLNode("achievements", config);
        for (AchievementDesc item : mAchievementsArr) {
            item.save(node);
        }
    }

    private void loadAchievements(XMLNode config) {
        XMLNode node = config.findNodeByName(config, "achievements");
        if (node != null) {
            for (AchievementDesc item : mAchievementsArr) {
                item.load(node);
            }
        }
    }

    private void saveFeatures(XMLNode config) {
        XMLNode node = new XMLNode("features", config);
        for (FeatureDesc item : mFeaturesArr) {
            item.save(node);
        }
    }

    private void loadFeatures(XMLNode config) {
        XMLNode node = config.findNodeByName(config, "features");
        if (node != null) {
            for (FeatureDesc item : mFeaturesArr) {
                item.load(node);
            }
        }
    }

    public boolean addNewPendingAchievement(AchievementDesc item) {
        if (mPendingAchiements.contains(item)) {
            return false;
        }
        mPendingAchiements.add(0, item);
        return true;
    }

    public int getPendingAchievementCount() {
        return mPendingAchiements.size();
    }

    public int getPendingFeaturesCount() {
        return mPendingFeatures.size();
    }

    public boolean addPendingFeature(FeatureDesc item) {
        if (mPendingFeatures.contains(item)) {
            return false;
        }
        mPendingFeatures.add(0, item);
        return true;
    }

    public Achievement[] getPendingAchievements() {
        Achievement items[] = new Achievement[mPendingAchiements.size()];
        mPendingAchiements.toArray(items);
        return items;
    }

    public void setAchievementSeen(Achievement item) {
        AchievementDesc realItem = getAchievement(item.getId());
        realItem.setState(Achievement.STATE_SEEN);
        mPendingAchiements.remove(realItem);
        // Save new stats
        save();
    }

    public Feature[] getPendingFeatures() {
        Feature items[] = new Feature[mPendingFeatures.size()];
        mPendingFeatures.toArray(items);
        return items;
    }

    public void setFeatureSeen(Feature item) {
        FeatureDesc realItem = getFeature(item.getId());
        realItem.setEnabledByUser(Feature.STATE_SEEN);
        // Save new stats
        save();
    }

    public void setFeatureActivated(Feature item) {
        FeatureDesc realItem = getFeature(item.getId());
        realItem.setEnabledByUser(Feature.STATE_ACTIVATED);
        mPendingFeatures.remove(realItem);
        // Save new stats
        save();
    }

    public void buyFeature(Feature item, Coin coin) {
        setFeatureActivated(item);
        for (Coin tmp : mCoins) {
            if (tmp.getId().equals(coin.getId())) {
                mCoins.remove(tmp);
                break;
            }
        }
    }

    public int isStatusScreenShown() {
        return mStatusScreenShown;
    }

    public void setStatusScreenShown() {
        mStatusScreenShown++;
        // Save new stats
        save();
    }

    public void addCoin(Coin coin) {
        mCoins.add(coin);
    }

    public Coin[] getCoins() {
        Coin ret[] = new Coin[mCoins.size()];
        return mCoins.toArray(ret);
    }

    public int getCoinCount() {
        return mCoins.size();
    }

    public Feature[] whatCanBeBoughtWith(Coin item) {
        Vector<Feature> tmp = new Vector<Feature>();
        for (FeatureDesc f : mFeaturesArr) {
            if (match(f, item)) {
                if (canBeUnlocked(f)) {
                    tmp.add(f);
                    if (tmp.size() == FREE_FEATURE_UNLOCK_COUNT) {
                        break;
                    }
                }
            }
        }
        Feature ret[] = new Feature[tmp.size()];
        return tmp.toArray(ret);
    }

    private boolean canBeUnlocked(FeatureDesc f) {
        // Check if it's not enabled yet
        if (f.isEnabledByUser() != Feature.STATE_DISABLED) {
            return false;
        }

        // Check the dependencies
        for (String dep : f.getDependencies()) {
            FeatureDesc fDep = mFeatures.get(dep);
            if (fDep != null) {
                if (fDep.isEnabledByUser() != Feature.STATE_ACTIVATED && !fDep.isEnabledInProfile()) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean match(Feature f, Coin item) {
        return f.getId().startsWith(item.getId());
    }

}
