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

import com.sonymobile.evolutionui.IEvolutionUIServiceCB;
import com.sonymobile.evolutionui.Profile;
import com.sonymobile.evolutionui.Achievement;
import com.sonymobile.evolutionui.Feature;
import com.sonymobile.evolutionui.Level;
import com.sonymobile.evolutionui.Coin;

interface IEvolutionUIService {

  /* Register and unregister callbacks */

  void registerCallback(in IEvolutionUIServiceCB cb);

  void unregisterCallback(in IEvolutionUIServiceCB cb);

  /* Misc methods */

  String getServicePackageName();

  int isStatusScreenShown();

  void setStatusScreenShown();

  /* Level and XP */

  Level getCurrentLevel();

  Level getNextLevel();

  int getCurrentXP();

  void addExperience(String experienceId);

  /* Handle profiles */

  int getProfileLevel();

  boolean isProfileDynamic();

  void setProfile(in int profileLevel, in boolean profileDynamic);

  /* Handle features */

  int getFeatureState(in String featureId);

  Feature[] getNewFeatures();

  void setFeatureSeen(in Feature item);

  void setFeatureActivated(in Feature item);

  Feature[] whatCanBeBoughtWith(in Coin item);

  void buyFeature(in Feature item, in Coin coin);

  int getCoinCount();

  Coin[] getCoins();

  /* Handle achievements */

  Achievement[] getNewAchievements();

  void setAchievementSeen(in Achievement item);

  /* Handle achievements */

  int getExperienceCount(in String id);

  /* Admin methods */

  void resetStats();

}
