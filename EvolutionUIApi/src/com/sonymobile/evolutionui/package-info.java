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
/**
EvolutionUI is a small API which allows easy integration of Gamification in the whole Phone OS.
<p>
The core idea behind EvolutionUI is the introduction of achievements in order to unlock features.
This will allow less experienced users to start out with a much simpler user experience, much
easier to learn, but still allowing him/her to enable the rest of the features, step by step,
based on the phone usage.
<p>
The basic concepts are shown in the following diagram:
<p>
<img src="doc-files/BasicConcepts.png" />
<p>
Applications detect user actions and record them as "Experiences". For example an experience is
making a phone call, starting an application, sending an SMS, etc. Each experience will increment
an internal counter, so the service can track how many times the user has performed a certain
action, and also will give experience points to the user.
<p>
When the counters of certain experience reach a given threshold, an "Achievement" will be unlocked.
For example, one achievement could be triggered when the user has sent 10 SMSs.
When an achievement is accomplished, it may (or may not) trigger the unlocking of a "Feature".
A feature represents a piece of functionality in an application. Note that the achievement simply
unlocks the feature, the activation is up to the user. In other words, the achievement makes the
feature available to be enabled by the user, but it's still up to the user to decide if he/she is
interested in it, or not. This will allow the users to customize the phone to their needs.
<p>
Of course achievements can have more complicated conditions, they can depend on several
experiences as well as on other achievements.
<p>
Another way of unlocking new features is using the experience points. Every experience will give
the user a certain amount of experience points. When this reaches certain values, the user will
reach a new "Level" (in other words, the user will "level up"). When leveling up, the user will
obtain a "Coin", which can be used to unlock a feature.
<p>
Note that technically it's also possible to give coins with achievements, or to unlock specific
features when levelling up as well.
*/
package com.sonymobile.evolutionui;
