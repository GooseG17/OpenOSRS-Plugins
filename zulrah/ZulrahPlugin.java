/*Nomnom
 * Copyright (c) 2017, Aria <aria@ar1as.space>
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * Copyright (c) 2017, Devin French <https://github.com/devinfrench>
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
 nomnom */
package net.runelite.client.plugins.zulrah;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.zulrah.overlays.*;
import net.runelite.client.plugins.zulrah.patterns.*;
import net.runelite.client.plugins.zulrah.phase.ZulrahPhase;
import net.runelite.client.plugins.zulrah.phase.ZulrahType;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor(
	name = "Zulrah plugin",
	tags = {"nomscripts"}
)
@Slf4j
public class ZulrahPlugin extends Plugin
{
	@Inject
    Client client;

	@Inject
    ZulrahConfig config;

	@Inject
    ZulrahOverlay overlay;

	@Inject
    ZulrahCurrentPhaseOverlay currentPhaseOverlay;

	@Inject
	ZulrahNextPhaseOverlay nextPhaseOverlay;

	@Inject
    ZulrahPrayerOverlay zulrahPrayerOverlay;

	@Inject
    ZulrahRecoilOverlay zulrahRecoilOverlay;

	@Inject
    OverlayManager overlayManager;

	private final ZulrahPattern[] patterns = new ZulrahPattern[]
	{
		new ZulrahPatternA(),
		new ZulrahPatternB(),
		new ZulrahPatternC(),
		new ZulrahPatternD()
	};

	private ZulrahInstance instance;

	public boolean needRecoil = false;

	@Provides
    ZulrahConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ZulrahConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
		overlayManager.add(currentPhaseOverlay);
		overlayManager.add(nextPhaseOverlay);
		overlayManager.add(zulrahPrayerOverlay);
		overlayManager.add(zulrahRecoilOverlay);
	}


	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		overlayManager.remove(currentPhaseOverlay);
		overlayManager.remove(nextPhaseOverlay);
		overlayManager.remove(zulrahPrayerOverlay);
		overlayManager.remove(zulrahRecoilOverlay);
	}

	private NPC zulrah;
	private WorldPoint worldPoint;

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		ZulrahImageManager.getZulrahBufferedImage(ZulrahType.MAGIC);
		if (zulrah == null || instance == null)
		{
			return;
		}
		if (zulrah.getId() == -1)
		{
			dispose();
			return;
		}

		ZulrahPhase currentPhase = ZulrahPhase.valueOf(zulrah, instance.getStartLocation());
		if (instance.getPhase() == null)
		{
			instance.setPhase(currentPhase);
		}
		else if (!instance.getPhase().equals(currentPhase))
		{
			ZulrahPhase previousPhase = instance.getPhase();
			instance.setPhase(currentPhase);
			instance.nextStage();

			log.info("Zulrah phase has moved from {} -> {}, stage: {}", previousPhase, currentPhase, instance.getStage());
			log.info("Start location " + worldPoint);
			log.info("World location " + zulrah.getWorldLocation());
		}

		ZulrahPattern pattern = instance.getPattern();
		if (pattern == null)
		{
			int potential = 0;
			ZulrahPattern potentialPattern = null;

			for (ZulrahPattern p : patterns)
				{
					if (p.stageMatches(instance.getStage(), instance.getPhase()))
						{
							potential++;
							potentialPattern = p;
						}
				}

			if (potential == 1) {
				log.info("Zulrah pattern identified: {}", potentialPattern);

				instance.setPattern(potentialPattern);
			}
		}
		else if (pattern.canReset(instance.getStage()) && (instance.getPhase() == null || instance.getPhase().equals(pattern.get(0))))
		{
			log.info("Zulrah pattern has reset.");

			instance.reset();
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{

		if (!config.enabled() || client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if ("Zulrah".equals(event.getNpc().getName()))
		{
			log.info("Zulrah spawned");
			zulrah = event.getNpc();
			worldPoint = zulrah.getWorldLocation();
			log.info("Local location " + zulrah.getLocalLocation());
			log.info("World location " + zulrah.getWorldLocation());

			if (instance == null)
			{
				instance = new ZulrahInstance(zulrah);
				log.info("Zulrah encounter has started.");
				log.info(instance.getStartLocation().toString());
			}
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (!config.enabled() || client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if ("Zulrah".equals(event.getNpc().getName()))
		{
			log.info("Zulrah despawned");
		}
	}

	private void dispose()
	{
		log.info("Instance has closed");
		zulrah = null;
		instance = null;
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() == ChatMessageType.SPAM || event.getType() == ChatMessageType.GAMEMESSAGE)
		{
			if (event.getMessage().contains("Your Ring of Recoil has shattered."))
			{
				needRecoil = true;
			}
			if (event.getMessage().contains("more points of damage before a ring will shatter"))
			{
				needRecoil = false;
			}
		}
	}

	public ZulrahInstance getInstance()
	{
		return instance;
	}
}
