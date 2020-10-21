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

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.SquareOverlay;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;
import java.util.*;

@Slf4j
public class PathFinderOverlay extends Overlay
{
	private final PathFinder plugin;
	private final Client client;
	private final PathFinderConfig config;
	@Inject
	public PathFinderOverlay(PathFinder plugin, Client client) {
		this.plugin = plugin;
		this.client = client;
		this.config = plugin.getConfig();
		setPosition(OverlayPosition.DETACHED);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
		setPriority(OverlayPriority.HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		WorldPoint[] path = plugin.getPathReference().get();
		Player player = client.getLocalPlayer();
		if (path == null || player == null) return null;
		WorldPoint p = player.getWorldLocation();
		for (int i = 0; i < path.length; i++) {
			if (path[i].distanceTo(p) < config.distance()) {
				if (!config.onlyMinimap()) SquareOverlay.drawTile(client,graphics,path[i],config.getToStartColor(),config.solidSquare());
				SquareOverlay.drawOnMinimap(client,graphics,path[i],config.getToStartColor(),config.solidSquare());
				break;
			}
		}

		for (int i = path.length - 1; i >= 0; i--) {
			if (path[i].distanceTo(p) < config.distance()) {
				if (!config.onlyMinimap()) SquareOverlay.drawTile(client,graphics,path[i],config.getToEndColor(),config.solidSquare());
				SquareOverlay.drawOnMinimap(client,graphics,path[i],config.getToEndColor(),config.solidSquare());
				break;
			}
		}
		return null;
	}

//	private void drawReachableTiles(Graphics2D g) {
//
//		Player player = client.getLocalPlayer();
//		if (player == null) return;
//		WorldArea area = player.getWorldArea();
//		if (area == null) return;
//		log.info("-1,-1"+area.canTravelInDirection(client, 0, -1));
//
//		HashSet<WorldPoint> worldPoints = new HashSet<>();
//		final Queue<WorldPoint> queue = new ArrayDeque<>(20);
//		queue.add(player.getWorldLocation());
//
//		while (!queue.isEmpty()) {
//			if (worldPoints.size() >= 50) break;
//			WorldPoint curr = queue.poll();
//			if (worldPoints.contains(curr)) continue;
//			WorldArea currArea = new WorldArea(curr,1,1);
//			for (int dx = -1; dx <= 1; dx++) {
//				for (int dy = -1; dy < 1; dy++) {
//					if (dx == 0 && dy == 0) continue;
//					if (currArea.canTravelInDirection(client, dx, dy)) {
//						WorldPoint neighbour = curr.dx(dx).dy(dy);
//						queue.add(neighbour);
//						worldPoints.add(neighbour);
//					}
//				}
//			}
//		}
//		log.info("Size " + worldPoints.size());
//		worldPoints.forEach(worldPoint -> {
//			SquareOverlay.drawCenterSquare(g, client, worldPoint, config.solidSquare(), config.getToStartColor());
//			log.info("Drawing " + worldPoint);
//		});
//	}
}
