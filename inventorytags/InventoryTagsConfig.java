/*
 * Copyright (c) 2018 kulers
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
package net.runelite.client.plugins.inventorytags;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("inventorytags")
public interface InventoryTagsConfig extends Config
{
	String GROUP = "inventorytags";

	@ConfigSection(
			name = "Solid Square",
			description = "Solid square modification",
			position = 0,
			closedByDefault = false
	)
	String solidSquare = "solidSquare";

	@ConfigItem(
			keyName = "solidSquareSize",
			name = "solidSquareSize",
			description = "solidSquareSize",
			section = solidSquare
	)
	default int solidSquareSize()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "fillSolidColor",
			name = "Color whole item",
			description = "Configures if fills whole item image with color, solidSquare must be 0",
			section = solidSquare
	)
	default boolean fillSolidColor()
	{
		return true;
	}

	@ConfigItem(
			keyName = "delay",
			name = "delay",
			description = "delay",
			section = solidSquare
	)
	default int delay()
	{
		return 3000;
	}

	@ConfigItem(
			keyName = "stacks",
			name = "Count item stacks",
			description = "Count item stacks",
			section = solidSquare
	)
	default boolean stacks()
	{
		return true;
	}


	@ConfigSection(
			name = "groupColor1",
			description = "group1Color",
			position = 0,
			closedByDefault = true
	)
	String group1Color = "group1Color";

	@ConfigItem(
			keyName = "groupColor1",
			name = "Group1 Color",
			description = "Color of the Tag",
			section = group1Color
	)
	default Color getGroup1Color()
	{
		return new Color(255, 0, 0);
	}

	@ConfigItem(
			keyName = "group1min",
			name = "Group1 min amount",
			description = "group1min",
			section = group1Color
	)
	default int getGroup1Min()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "group1max",
			name = "Group1 max amount",
			description = "group1max",
			section = group1Color
	)
	default int getGroup1Max()
	{
		return 28;
	}

	@ConfigSection(
			name = "groupColor2",
			description = "group2Color",
			position = 0,
			closedByDefault = true
	)
	String group2Color = "group2Color";

	@ConfigItem(
			keyName = "groupColor2",
			name = "Group2 Color",
			description = "Color of the Tag",
			section = group2Color
	)
	default Color getGroup2Color()
	{
		return new Color(255, 0, 0);
	}

	@ConfigItem(
			keyName = "group2min",
			name = "Group2 min amount",
			description = "group2min",
			section = group2Color
	)
	default int getGroup2Min()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "group2max",
			name = "Group2 max amount",
			description = "group2max",
			section = group2Color
	)
	default int getGroup2Max()
	{
		return 28;
	}









	@ConfigSection(
			name = "groupColor3",
			description = "group3Color",
			position = 0,
			closedByDefault = true
	)
	String group3Color = "group3Color";

	@ConfigItem(
			keyName = "groupColor3",
			name = "Group3 Color",
			description = "Color of the Tag",
			section = group3Color
	)
	default Color getGroup3Color()
	{
		return new Color(255, 0, 0);
	}

	@ConfigItem(
			keyName = "group3min",
			name = "Group3 min amount",
			description = "group3min",
			section = group3Color
	)
	default int getGroup3Min()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "group3max",
			name = "Group3 max amount",
			description = "group3max",
			section = group3Color
	)
	default int getGroup3Max()
	{
		return 28;
	}




	@ConfigSection(
			name = "groupColor4",
			description = "group4Color",
			position = 0,
			closedByDefault = true
	)
	String group4Color = "group4Color";

	@ConfigItem(
			keyName = "groupColor4",
			name = "Group4 Color",
			description = "Color of the Tag",
			section = group4Color
	)
	default Color getGroup4Color()
	{
		return new Color(255, 0, 0);
	}

	@ConfigItem(
			keyName = "group4min",
			name = "Group4 min amount",
			description = "group4min",
			section = group4Color
	)
	default int getGroup4Min()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "group4max",
			name = "Group4 max amount",
			description = "group4max",
			section = group4Color
	)
	default int getGroup4Max()
	{
		return 28;
	}




	@ConfigSection(
			name = "groupColor5",
			description = "group5Color",
			position = 0,
			closedByDefault = true
	)
	String group5Color = "group5Color";

	@ConfigItem(
			keyName = "groupColor5",
			name = "Group5 Color",
			description = "Color of the Tag",
			section = group5Color
	)
	default Color getGroup5Color()
	{
		return new Color(255, 0, 0);
	}

	@ConfigItem(
			keyName = "group5min",
			name = "Group5 min amount",
			description = "group5min",
			section = group5Color
	)
	default int getGroup5Min()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "group5max",
			name = "Group5 max amount",
			description = "group5max",
			section = group5Color
	)
	default int getGroup5Max()
	{
		return 28;
	}



	@ConfigSection(
			name = "groupColor6",
			description = "group6Color",
			position = 0,
			closedByDefault = true
	)
	String group6Color = "group6Color";

	@ConfigItem(
			keyName = "groupColor6",
			name = "Group6 Color",
			description = "Color of the Tag",
			section = group6Color
	)
	default Color getGroup6Color()
	{
		return new Color(255, 0, 0);
	}

	@ConfigItem(
			keyName = "group6min",
			name = "Group6 min amount",
			description = "group6min",
			section = group6Color
	)
	default int getGroup6Min()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "group6max",
			name = "Group6 max amount",
			description = "group6max",
			section = group6Color
	)
	default int getGroup6Max()
	{
		return 28;
	}
}
