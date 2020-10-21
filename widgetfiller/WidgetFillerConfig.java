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
package net.runelite.client.plugins.widgetfiller;

import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.*;

@ConfigGroup("widgetfiller")
public interface WidgetFillerConfig extends Config
{
	String GROUP = "inventorytags";

	@ConfigSection(
			name = "Preset widgets",
			description = "Preset widgets",
			position = 0
	)
	String presetWidgets = "presetWidgets";

	@ConfigSection(
			name = "Custom widgets",
			description = "Custom widgets",
			position = 1
	)
	String customWidgets = "customWidgets";

	@ConfigSection(
			name = "XP Drop widget",
			description = "XP Drop widget",
			position = 2
	)
	String xpDropWidget = "xpDropWidget";


	@ConfigItem(
			keyName = "groupColor0",
			name = "Custom widget Color",
			description = "Color of the Tag",
			section = customWidgets
	)
	default Color getGroup0Color()
	{
		return new Color(255, 0, 0);
	}

	@ConfigItem(
			keyName = "parent",
			name = "customParent",
			description = "parent",
			section = customWidgets
	)
	default int parent()
	{
		return 270;
	}

	@ConfigItem(
			keyName = "child",
			name = "customChild",
			description = "child",
			section = customWidgets
	)
	default int child()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "solidSquare",
		name = "solidSquare",
		description = "solidSquare"
	)
	default int solidSquare()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "checkVisible",
			name = "checkVisible",
			description = "checkVisible"
	)
	default boolean checkVisible()
	{
		return true;
	}

	@ConfigItem(
		keyName = "delay",
		name = "delay",
		description = "delay"
	)
	default int delay()
	{
		return 3000;
	}

	@ConfigItem(
			keyName = "widget1",
			name = "widget1",
			description = "widget1",
			section = presetWidgets
	)
	default UsefulWidgets widget1()
	{
		return UsefulWidgets.DIALOG_NPC_CONT;
	}

	@ConfigItem(
			keyName = "groupColor1",
			name = "Group 1 Color",
			description = "Color of the Tag",
			section = presetWidgets
	)
	default Color getGroup1Color()
	{
		return new Color(255, 0, 0);
	}


	@ConfigItem(
			keyName = "widget2",
			name = "widget2",
			description = "widget2",
			section = presetWidgets
	)
	default UsefulWidgets widget2()
	{
		return UsefulWidgets.DIALOG_OPTION;
	}

	@ConfigItem(
			keyName = "groupColor2",
			name = "Group 2 Color",
			description = "Color of the Tag",
			section = presetWidgets
	)
	default Color getGroup2Color()
	{
		return new Color(0, 255, 0);
	}


	@ConfigItem(
			keyName = "widget3",
			name = "widget3",
			description = "widget3",
			section = presetWidgets
	)
	default UsefulWidgets widget3()
	{
		return UsefulWidgets.BANK_CONTAINER;
	}

	@ConfigItem(
			keyName = "groupColor3",
			name = "Group 3 Color",
			description = "Color of the Tag",
			section = presetWidgets
	)
	default Color getGroup3Color()
	{
		return new Color(0, 0, 255);
	}


	@ConfigItem(
			keyName = "widget4",
			name = "widget4",
			description = "widget4",
			section = presetWidgets
	)
	default UsefulWidgets widget4()
	{
		return UsefulWidgets.LEVEL_UP;
	}

	@ConfigItem(
			keyName = "groupColor4",
			name = "Group 4 Color",
			description = "Color of the Tag",
			section = presetWidgets
	)
	default Color getGroup4Color()
	{
		return new Color(255, 0, 255);
	}

	@ConfigItem(
			keyName = "highlightChat",
			name = "highlightChat",
			description = "Show color next to chat with specific text. Format: (item), (item)"
	)
	default String getHighlightItems()
	{
		return "";
	}

	@ConfigItem(
			keyName = "highlightChatColor",
			name = "highlightChatColor",
			description = "Chat color"
	)
	default Color getHighlightChatColor()
	{
		return new Color(255, 0, 255);
	}

	@ConfigItem(
			keyName = "fillXpDrop",
			name = "fillXpDrop",
			description = "Requires the XP Drop Plugin to show skill icon",
			section = xpDropWidget
	)
	default boolean fillXpDrop()
	{
		return true;
	}

	@ConfigItem(
			keyName = "xpDropColor",
			name = "xpDropColor",
			description = "xpDropColor",
			section = xpDropWidget
	)
	default Color getXpDropColor()
	{
		return new Color(255, 0, 255);
	}
}
