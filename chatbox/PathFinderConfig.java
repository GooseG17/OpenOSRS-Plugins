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
package net.runelite.client.plugins.chatbox;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("pathfinder")
public interface PathFinderConfig extends Config
{
	String UPDATE = "updatePath";


	@ConfigItem(
			position = 0,
			keyName = "daxKey"+UPDATE,
			name = "daxKey",
			description = "daxKey"
	)
	default String daxKey()
	{
		return "sub_DPjXXzL5DeSiPf";
	}

	@ConfigItem(
			position = 1,
			keyName = "daxSecret"+UPDATE,
			name = "daxSecret",
			description = "daxSecret"
	)
	default String daxSecret()
	{
		return "PUBLIC-KEY";
	}

	@ConfigItem(
			position = 2,
			keyName = "toEndColor",
			name = "To End Color",
			description = "To End Color"
	)
	default Color getToEndColor()
	{
		return new Color(255, 0, 0);
	}


	@ConfigItem(
			position = 3,
			keyName = "toStartColor",
			name = "To Start Color",
			description = "To Dest Color"
	)
	default Color getToStartColor()
	{
		return new Color(0, 0, 255);
	}


	@ConfigItem(
			position = 4,
			keyName = "startPosition"+UPDATE,
			name = "startPosition",
			description = "startPosition e.g. 1,1,1, empty for current player location"
	)
	default String startPosition()
	{
		return "";
	}


	@ConfigItem(
			position = 5,
			keyName = "endPosition"+UPDATE,
			name = "endPosition",
			description = "endPosition e.g. 1,1,1"
	)
	default String endPosition()
	{
		return "";
	}

	@ConfigItem(
			position = 6,
			keyName = "fixedPath"+UPDATE,
			name = "fixedPath",
			description = "Fixed path, overrides all other paths e.g. 1,1,1,2,2,1,3,3,1"
	)
	default String fixedPath()
	{
		return "";
	}

	@ConfigItem(
			position = 7,
			keyName = "refreshDelaySeconds",
			name = "refreshDelaySeconds",
			description = "How often to remake the path, set to 0 to not change after path is made"
	)
	default int refreshDelaySeconds()
	{
		return 0;
	}

	@ConfigItem(
			position = 8,
			keyName = "solidSquare",
			name = "solidSquare",
			description = "solidSquare"
	)
	default int solidSquare()
	{
		return 0;
	}

	@ConfigItem(
			position = 9,
			keyName = "distance",
			name = "distance",
			description = "distance"
	)
	default int distance()
	{
		return 20;
	}

	@ConfigItem(
			position = 10,
			keyName = "onlyMinimap",
			name = "onlyMinimap",
			description = "onlyMinimap"
	)
	default boolean onlyMinimap()
	{
		return true;
	}

}
