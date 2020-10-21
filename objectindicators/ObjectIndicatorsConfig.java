/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
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

package net.runelite.client.plugins.objectindicators;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("objectindicators")
public interface ObjectIndicatorsConfig extends Config
{
	@Alpha
	@ConfigItem(
		keyName = "markerColor",
		name = "Marker color",
		description = "Configures the color of object marker",
		position = 0
	)
	default Color markerColor()
	{
		return Color.YELLOW;
	}

	@ConfigItem(
		keyName = "rememberObjectColors",
		name = "Remember color per object",
		description = "Color objects using the color from time of marking",
		position = 1
	)
	default boolean rememberObjectColors()
	{
		return false;
	}

	@ConfigItem(
			keyName = "fillSolidColor",
			name = "Fill solid color",
			description = "Configures if fill solid color",
			position = 2
	)
	default boolean fillSolidColor()
	{
		return true;
	}

	@ConfigItem(
			keyName = "checkHash",
			name = "checkHash",
			description = "checkHash",
			position = 3
	)
	default boolean checkHash()
	{
		return true;
	}

	@ConfigItem(
			keyName = "solidSquare",
			name = "solidSquare",
			description = "solidSquare",
			position = 4
	)
	default int solidSquare()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "checkAnimation",
			name = "checkAnimation",
			description = "checkAnimation",
			position = 5
	)
	default boolean checkAnimation()
	{
		return true;
	}
}
