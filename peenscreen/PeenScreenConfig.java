package net.runelite.client.plugins.peenscreen;

import java.awt.Color;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.ui.overlay.OverlayLayer;

@ConfigGroup("peenscreen")
public interface PeenScreenConfig extends Config
{
	@Alpha
	@ConfigItem(
		keyName = "color",
		name = "Color",
		description = "The color of the greenscreen"
	)
	default Color greenscreenColor()
	{
		return Color.BLACK;
	}


	@ConfigItem(
			keyName = "layer",
			name = "Layer",
			description = "The layer of the greenscreen"
	)
	default OverlayLayer overlayLayer()
	{
		return OverlayLayer.ABOVE_MAP;
	}
}
