package net.runelite.client.plugins.peenscreen;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.awt.*;

@Slf4j
@PluginDescriptor(
	name = "Green Screen",
	tags = "nomscripts",
	enabledByDefault = false
)
public class PeenScreenPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private PeenScreenConfig config;

	@Inject
	private PeenScreenOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Provides
	PeenScreenConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PeenScreenConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("peenscreen"))
		{
			overlay.setLayer(config.overlayLayer());
		}
	}
}
