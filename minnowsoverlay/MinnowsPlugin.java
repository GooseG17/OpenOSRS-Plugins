/*
 * Copyright (c) 2017, Seth <Sethtroll3@gmail.com>
 * Copyright (c) 2018, Levi <me@levischuck.com>
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

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.FishingSpot;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Instant;
import java.util.*;

@PluginDescriptor(
	name = "Minnows",
	description = "Show fishing stats and mark fishing spots",
	tags = {"overlay", "skilling", "nom"}
)
@Slf4j
public class MinnowsPlugin extends Plugin
{

	@Getter(AccessLevel.PACKAGE)
	private final Map<Integer, MinnowSpot> minnowSpots = new HashMap<>();

	@Getter(AccessLevel.PACKAGE)
	private final List<NPC> fishingSpots = new ArrayList<>();

	@Getter(AccessLevel.PACKAGE)
	private FishingSpot currentSpot;

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private MinnowsConfig config;

	@Inject
	private MinnowsOverlay spotOverlay;

	@Provides
	MinnowsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MinnowsConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(spotOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
//		spotOverlay.setHidden(true);
		overlayManager.remove(spotOverlay);
		minnowSpots.clear();
		currentSpot = null;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		GameState gameState = gameStateChanged.getGameState();
		if (gameState == GameState.CONNECTION_LOST || gameState == GameState.LOGIN_SCREEN || gameState == GameState.HOPPING)
		{
			fishingSpots.clear();
			minnowSpots.clear();
		}
	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged event)
	{
		if (event.getSource() != client.getLocalPlayer())
		{
			return;
		}

		final Actor target = event.getTarget();

		if (!(target instanceof NPC))
		{
			return;
		}

		final NPC npc = (NPC) target;
		FishingSpot spot = FishingSpot.findSpot(npc.getId());

		if (spot == null)
		{
			return;
		}

		currentSpot = spot;
	}


	@Subscribe
	public void onGameTick(GameTick event)
	{

		inverseSortSpotDistanceFromPlayer();

		for (NPC npc : fishingSpots)
		{
			if (FishingSpot.findSpot(npc.getId()) == FishingSpot.MINNOW)
			{
				final int id = npc.getIndex();
				final MinnowSpot minnowSpot = minnowSpots.get(id);

				// create the minnow spot if it doesn't already exist
				// or if it was moved, reset it
				if (minnowSpot == null
						|| !minnowSpot.getLoc().equals(npc.getWorldLocation()))
				{
					minnowSpots.put(id, new MinnowSpot(npc.getWorldLocation(), Instant.now()));
				}
			}
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		final NPC npc = event.getNpc();

		if (FishingSpot.findSpot(npc.getId()) == null)
		{
			return;
		}

		fishingSpots.add(npc);
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		final NPC npc = npcDespawned.getNpc();

		fishingSpots.remove(npc);

		MinnowSpot minnowSpot = minnowSpots.remove(npc.getIndex());
		if (minnowSpot != null)
		{
			log.debug("Minnow spot {} despawned", npc);
		}
	}


	private void inverseSortSpotDistanceFromPlayer()
	{
		if (fishingSpots.isEmpty())
		{
			return;
		}

		final LocalPoint cameraPoint = new LocalPoint(client.getCameraX(), client.getCameraY());
		fishingSpots.sort(
				Comparator.comparing(
						// Negate to have the furthest first
						(NPC npc) -> -npc.getLocalLocation().distanceTo(cameraPoint))
						// Order by position
						.thenComparing(NPC::getLocalLocation, Comparator.comparing(LocalPoint::getX)
								.thenComparing(LocalPoint::getY))
						// And then by id
						.thenComparing(NPC::getId)
		);
	}
}
