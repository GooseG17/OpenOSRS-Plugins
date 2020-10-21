/*
 * Copyright (c) 2017, Seth <Sethtroll3@gmail.com>
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
package net.runelite.client.plugins.minnowsoverlay;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("minnows")
public interface MinnowsConfig extends Config
{


	@ConfigSection(
			name = "Solid square",
			description = "The render style of NPC highlighting",
			position = 0
	)
	String solidSquare = "solidSquare";



	@ConfigItem(
		position = 0,
		keyName = "onlyCurrent",
		name = "Display only currently fished fish",
		description = "Configures whether only current fished fish's fishing spots are displayed"
	)
	default boolean onlyCurrentSpot()
	{
		return false;
	}

	@ConfigItem(
			position = 0,
			keyName = "smartSquare",
			name = "smartSquare",
			description = "smartSquare",
			section = solidSquare
	)
	default boolean smartSquare()
	{
		return true;
	}


	@ConfigItem(
			position = 0,
			keyName = "squareSize",
			name = "squareSize",
			description = "squareSize",
			section = solidSquare
	)
	default int squareSize()
	{
		return 2;
	}


	@ConfigItem(
			keyName = "squareColor",
			name = "squareColor",
			description = "squareColor",
			position = 5,
			section = solidSquare
	)
	default Color squareColor()
	{
		return Color.RED;
	}

	@ConfigItem(
		keyName = "overlayColor",
		name = "Overlay Color",
		description = "Color of overlays",
		position = 4
	)
	default Color timerColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
		keyName = "minnowsOverlayColor",
		name = "flyingFishColor",
		description = "Color of overlays for Minnows",
		position = 5
	)
	default Color flyingFishColor()
	{
		return Color.RED;
	}
}
