/*
 * Copyright (c) 2018, Nickolaj <https://github.com/fire-proof>
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
package net.runelite.client.plugins.nightmarezone;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("nightmareZone")
public interface NightmareZoneConfig extends Config
{
	@ConfigItem(
			keyName = "moveoverlay",
			name = "Override NMZ overlay",
			description = "Overrides the overlay so it doesn't conflict with other RuneLite plugins"
	)
	default boolean moveOverlay()
	{
		return true;
	}

	@ConfigItem(
			keyName = "powersurgenotification",
			name = "Power surge notification",
			description = "Toggles notifications when a power surge power-up appears"
	)
	default boolean powerSurgeNotification()
	{
		return false;
	}

	@ConfigItem(
			keyName = "recurrentdamagenotification",
			name = "Recurrent damage notification",
			description = "Toggles notifications when a recurrent damage power-up appears"
	)
	default boolean recurrentDamageNotification()
	{
		return false;
	}

	@ConfigItem(
			keyName = "zappernotification",
			name = "Zapper notification",
			description = "Toggles notifications when a zapper power-up appears"
	)
	default boolean zapperNotification()
	{
		return false;
	}

	@ConfigItem(
			keyName = "ultimateforcenotification",
			name = "Ultimate Force notification",
			description = "Toggles notifications when an ultimate force power-up appears"
	)
	default boolean ultimateForceNotification()
	{
		return false;
	}

	@ConfigItem(
			keyName = "overloadnotification",
			name = "Overload notification",
			description = "Toggles notifications when your overload runs out"
	)
	default boolean overloadNotification()
	{
		return true;
	}

	@ConfigItem(
			keyName = "absorptionnotification",
			name = "Absorption notification",
			description = "Toggles notifications when your absorption points gets below your threshold"
	)
	default boolean absorptionNotification()
	{
		return true;
	}

	@ConfigItem(
			keyName = "absorptioncoloroverthreshold",
			name = "Color above threshold",
			description = "Configures the color for the absorption widget when above the threshold"
	)
	default Color absorptionColorAboveThreshold()
	{
		return Color.YELLOW;
	}

	@ConfigItem(
			keyName = "absorptioncolorbelowthreshold",
			name = "Color below threshold",
			description = "Configures the color for the absorption widget when below the threshold"
	)
	default Color absorptionColorBelowThreshold()
	{
		return Color.RED;
	}

	@ConfigItem(
			keyName = "itemOverlay",
			name = "itemOverlay",
			description = "Override other overlays and displays on items"
	)
	default boolean itemOverlay()
	{
		return true;
	}


	@ConfigItem(
			keyName = "boostAmount",
			name = "boostAmount",
			description = "boostAmount"
	)
	default int boostAmount()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "absorptionminthreshold",
			name = "Absorption Min Threshold",
			description = "The amount of absorption points to enable item overlay on absorptions"
	)
	default int absorptionMinThreshold()
	{
		return 100;
	}

	@ConfigItem(
			keyName = "absorptionthreshold",
			name = "Absorption Max Threshold",
			description = "The amount of absorption points to disable item overlay on absorptions"
	)
	default int absorptionMaxThreshold()
	{
		return 900;
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
			keyName = "delay",
			name = "delay",
			description = "delay"
	)
	default int delay()
	{
		return 3000;
	}


	@ConfigItem(
			keyName = "boostColor",
			name = "boostColor",
			description = "boostColor"
	)
	default Color boostColor()
	{
		return Color.GREEN;
	}
	@ConfigItem(
			keyName = "cakeColor",
			name = "cakeColor",
			description = "cakeColor"
	)
	default Color cakeColor()
	{
		return Color.RED;
	}
}
