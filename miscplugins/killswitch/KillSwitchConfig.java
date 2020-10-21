/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.miscplugins.killswitch;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("killswitch")
public interface KillSwitchConfig extends Config
{

	@ConfigItem(
			position = 0,
			keyName = "discordWebhook",
			name = "Send Discord webhook when triggered",
			description = "aa"
	)
	default String discordWebhook()
	{
		return "";
	}

	@ConfigItem(
		position = 0,
		keyName = "killSwitchActive",
		name = "Activate kill switch",
		description = "aa"
	)
	default boolean killSwitchActive()
	{
		return false;
	}


	@ConfigSection(
			name = "Timers",
			description = "Timers",
			position = 1,
			closedByDefault = false
	)
	String timers = "timers";


	@ConfigItem(
			keyName = "pauseTimers",
			name = "Pause timers when mouse is outside of screen",
			description = "aa",
			position = 0,
			section = timers
	)
	default boolean pauseOnExit()
	{
		return false;
	}

	@ConfigItem(
			keyName = "checkMaxRun",
			name = "Check Max Runtime",
			description = "aa",
			position = 1,
			section = timers
	)
	default boolean checkMaxRun()
	{
		return true;
	}


	@ConfigItem(
			keyName = "maxRuntime",
			name = "Max runtime (s)",
			description = "Max Runtime",
			position = 1,
			section = timers
	)
	default int checkMaxRuntime()
	{
		return 3600;
	}

	@ConfigItem(
		keyName = "checkAnimation",
		name = "Check animation",
		description = "aa",
		section = timers
	)
	default boolean checkAnimation()
	{
		return true;
	}

	@ConfigItem(
		keyName = "checkAnimationTime",
		name = "Check animation time (s)",
		description = "The notification delay after the player is idle",
		section = timers
	)
	default int checkAnimationTime()
	{
		return 5;
	}

	@ConfigItem(
		keyName = "checkExperience",
		name = "Check experience",
		description = "aa",
		section = timers
	)
	default boolean checkExperience()
	{
		return true;
	}

	@ConfigItem(
		keyName = "checkExperienceTime",
		name = "Check experience time (s)",
		description = "The notification delay after the player is idle",
			section = timers
	)
	default int checkExperienceTime()
	{
		return 5;
	}

	@ConfigItem(
		keyName = "checkMovement",
		name = "Check movement",
		description = "aa",
			section = timers
	)
	default boolean checkMovement()
	{
		return true;
	}

	@ConfigItem(
		keyName = "checkMovementTime",
		name = "Check movement time (s)",
		description = "The notification delay after the player is idle",
			section = timers
	)
	default int checkMovementTime()
	{
		return 5;
	}


	@ConfigItem(
		keyName = "checkInteraction",
		name = "Check interaction",
		description = "aa",
			section = timers
	)
	default boolean checkInteraction()
	{
		return true;
	}

	@ConfigItem(
		keyName = "checkInteractionTime",
		name = "Check interaction time (s)",
		description = "The notification delay after the player is idle",
			section = timers
	)
	default int checkInteractionTime()
	{
		return 5;
	}



	@ConfigSection(
			name = "Triggers",
			description = "Triggers",
			position = 1,
			closedByDefault = false
	)
	String words = "Triggers";


	@ConfigItem(
			keyName = "checkItemSlots",
			name = "Check item slots",
			description = "aa",
			section = words
	)
	default boolean checkItemSlots()
	{
		return true;
	}

	@ConfigItem(
			keyName = "checkEmptySlot",
			name = "Check empty slot",
			description = "aa",
			section = words
	)
	default int checkEmptySlot()
	{
		return -1;
	}


	@ConfigItem(
			keyName = "checkEmptySlot1",
			name = "Check empty slot1",
			description = "aa",
			section = words
	)
	default int checkEmptySlot1()
	{
		return -1;
	}


	@ConfigItem(
			keyName = "checkEmptySlot2",
			name = "Check empty slot2",
			description = "aa",
			section = words
	)
	default int checkEmptySlot2()
	{
		return -1;
	}

	@ConfigItem(
			keyName = "checkEmptyBank",
			name = "Check empty bank",
			description = "aa",
			section = words
	)
	default boolean checkEmptyBank()
	{
		return true;
	}

	@ConfigItem(
			keyName = "checkStationary",
			name = "Check stationary",
			description = "aa",
			section = words
	)
	default boolean checkStationary()
	{
		return false;
	}

	@ConfigItem(
		keyName = "filteredWords",
		name = "Filtered Words",
		description = "List of filtered words, separated by commas",
		section = words
	)
	default String filteredWords()
	{
		return "";
	}

	@ConfigItem(
		keyName = "filteredRegex",
		name = "Filtered Regex",
		description = "List of regular expressions to filter, one per line",
		section = words
	)
	default String filteredRegex()
	{
		return "";
	}


}
