/*
 * Copyright (c) 2018, DennisDeV <https://github.com/DevDennis>
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
package net.runelite.client.plugins.miscplugins.checkbots;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.miscplugins.DiscordWebhook;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.concurrent.ScheduledExecutorService;

@PluginDescriptor(
	name = "Check Bots",
	description = "Shows current ping",
	tags = {"nomscripts", "ping"},
	enabledByDefault = false
)
public class CheckBotsPlugin extends Plugin
{
	static final String CONFIG_GROUP = "checkbots";

	@Inject
	private Client client;

	@Inject
	private CheckBotsConfig config;

	@Inject
	private ScheduledExecutorService executorService;

	private HashMap<String, Integer> names = new HashMap<>();

	@Provides
	CheckBotsConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CheckBotsConfig.class);
	}

	@Override
	protected void startUp()
	{
		names = new HashMap<>();
	}

	@Override
	protected void shutDown()
	{
	}


	private Instant timedScreenshotInstant = null;

	@Subscribe
	public void onGameTick(GameTick event)
	{

		if (config.frequency() > 0) {
			if (timedScreenshotInstant == null) timedScreenshotInstant = Instant.now().plus(config.frequency(), ChronoUnit.MINUTES);

			if (timedScreenshotInstant.isBefore(Instant.now())) {
				timedScreenshotInstant = null;
				for (Player player : client.getPlayers()) {
					if (player == null || player.getName() == null) continue;
					names.merge(Text.toJagexName(player.getName()),1,Integer::sum);
				}
				sendWebhook();
			}
		}
	}

	private void sendWebhook() {
		if (config.discordWebhook().isEmpty()) return;
		StringJoiner sj = new StringJoiner(",");
		names.forEach((k,v) -> sj.add(k + "," + v));
		executorService.execute(()-> DiscordWebhook.sendString(sj.toString(), config.discordWebhook()));
	}
}
