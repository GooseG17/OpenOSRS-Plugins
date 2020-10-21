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
package net.runelite.client.plugins.nightmarezone;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.infobox.Timer;
import net.runelite.client.util.AsyncBufferedImage;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class NightmareZoneItemOverlay extends WidgetItemOverlay
{
	private final ItemManager itemManager;
	private final NightmareZoneConfig config;
	private final NightmareZonePlugin plugin;
	private final Client client;
	private HashMap<Point, Timer> timerHashMap = new HashMap<>();
	@Inject
	private NightmareZoneItemOverlay(Client client, ItemManager itemManager, NightmareZonePlugin plugin,
									 NightmareZoneConfig config)
	{
		this.config = config;
		this.client = client;
		this.itemManager = itemManager;
		this.plugin = plugin;
		showOnEquipment();
		showOnInventory();
	}

	private boolean highlightAbsorption;
	private List<Integer> ABSORPTION = Arrays.asList(ItemID.ABSORPTION_1,ItemID.ABSORPTION_2,ItemID.ABSORPTION_3,ItemID.ABSORPTION_4);
	private List<Integer> OVERLOAD = Arrays.asList(ItemID.OVERLOAD_1,ItemID.OVERLOAD_2,ItemID.OVERLOAD_3,ItemID.OVERLOAD_4);
	private List<Integer> PRAYER = Arrays.asList(
			ItemID.PRAYER_POTION1,ItemID.PRAYER_POTION2,ItemID.PRAYER_POTION3,ItemID.PRAYER_POTION4,
			ItemID.SUPER_RESTORE1,ItemID.SUPER_RESTORE2,ItemID.SUPER_RESTORE3,ItemID.SUPER_RESTORE4);
	private List<Integer> BOOSTS = Arrays.asList(ItemID.SUPER_RANGING_1,ItemID.SUPER_RANGING_2,ItemID.SUPER_RANGING_3,ItemID.SUPER_RANGING_4,
			ItemID.SUPER_MAGIC_POTION_1,ItemID.SUPER_MAGIC_POTION_2,ItemID.SUPER_MAGIC_POTION_3,ItemID.SUPER_MAGIC_POTION_4,
			ItemID.SUPER_STRENGTH1,ItemID.SUPER_STRENGTH2,ItemID.SUPER_STRENGTH3,ItemID.SUPER_STRENGTH4,
			ItemID.SUPER_ATTACK1,ItemID.SUPER_ATTACK2,ItemID.SUPER_ATTACK3,ItemID.SUPER_ATTACK4,
			ItemID.SUPER_COMBAT_POTION1,ItemID.SUPER_COMBAT_POTION2,ItemID.SUPER_COMBAT_POTION3,ItemID.SUPER_COMBAT_POTION4);
	private List<Integer> CAKES = Arrays.asList(ItemID.DWARVEN_ROCK_CAKE,ItemID.DWARVEN_ROCK_CAKE_7510,ItemID.LOCATOR_ORB);
	private List<Integer> WHITELIST = Arrays.asList(
			ItemID.ABSORPTION_1,ItemID.ABSORPTION_2,ItemID.ABSORPTION_3,ItemID.ABSORPTION_4,
			ItemID.OVERLOAD_1,ItemID.OVERLOAD_2,ItemID.OVERLOAD_3,ItemID.OVERLOAD_4,
			ItemID.SUPER_RANGING_1,ItemID.SUPER_RANGING_2,ItemID.SUPER_RANGING_3,ItemID.SUPER_RANGING_4,
			ItemID.SUPER_MAGIC_POTION_1,ItemID.SUPER_MAGIC_POTION_2,ItemID.SUPER_MAGIC_POTION_3,ItemID.SUPER_MAGIC_POTION_4,
			ItemID.SUPER_COMBAT_POTION1,ItemID.SUPER_COMBAT_POTION2,ItemID.SUPER_COMBAT_POTION3,ItemID.SUPER_COMBAT_POTION4,
			ItemID.DWARVEN_ROCK_CAKE,ItemID.DWARVEN_ROCK_CAKE_7510,ItemID.LOCATOR_ORB
	);
	
	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget)
	{
		
		ItemComposition itemComposition = itemManager.getItemComposition(itemId);
		if (!WHITELIST.contains(itemId)) return;
		Color color = config.absorptionColorBelowThreshold();
		{
			int absorptionPoints = client.getVar(Varbits.NMZ_ABSORPTION);
			if (absorptionPoints < config.absorptionMinThreshold()) {
				highlightAbsorption = true;
			} else if (absorptionPoints >= config.absorptionMaxThreshold()) {
				highlightAbsorption = false;
			}
			if (!highlightAbsorption && ABSORPTION.contains(itemId)) return;
			color = config.absorptionColorBelowThreshold();
		}
		boolean boosted =
				client.getBoostedSkillLevel(Skill.STRENGTH) - client.getRealSkillLevel(Skill.STRENGTH) > config.boostAmount() ||
						client.getBoostedSkillLevel(Skill.MAGIC) - client.getRealSkillLevel(Skill.MAGIC) > config.boostAmount() ||
						client.getBoostedSkillLevel(Skill.RANGED) - client.getRealSkillLevel(Skill.RANGED) > config.boostAmount() ||
						client.getBoostedSkillLevel(Skill.ATTACK) - client.getRealSkillLevel(Skill.ATTACK) > config.boostAmount();

		if (BOOSTS.contains(itemId)) {
			if (boosted) return;
			color = config.boostColor();
		}
		if (OVERLOAD.contains(itemId)) {
			if (boosted) return;
			if (client.getBoostedSkillLevel(Skill.HITPOINTS) < 50) return;
			color = config.boostColor();
		}
		if (CAKES.contains(itemId)) {
			if (client.getBoostedSkillLevel(Skill.HITPOINTS) == 1) return;
			color = config.cakeColor();
		}

		Rectangle bounds = itemWidget.getCanvasBounds();
		if (config.solidSquare() > 0)
		{
			if (config.delay() > 0 &&
				bounds.contains(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY()))
			{
				timerHashMap.put(itemWidget.getCanvasLocation(), new Timer(config.delay(), ChronoUnit.MILLIS, new BufferedImage(1, 1, 1), plugin));
			}

			if (timerHashMap.containsKey(itemWidget.getCanvasLocation()))
			{
				Timer timer = timerHashMap.get(itemWidget.getCanvasLocation());
				Duration timeLeft = Duration.between(Instant.now(), timer.getEndTime());
				if (timeLeft.isNegative())
				{
					timerHashMap.remove(itemWidget.getCanvasLocation());
				}
			}
			else
			{
				drawCenterSquare(graphics, bounds.getCenterX(), bounds.getCenterY(), config.solidSquare(), color);
			}
		}
	}

	private BufferedImage drawColorImage(AsyncBufferedImage img, Color color)
	{
		int width = img.getWidth();
		int height = img.getHeight();
		BufferedImage solidVers = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) solidVers.getGraphics();
		g2.setColor(color);
		g2.fillRect(0, 0, width, height);

		g2.setComposite(AlphaComposite.DstIn);
		g2.drawImage(img, 0, 0, width, height, 0, 0, width, height, null);
		return solidVers;
	}

	private void drawCenterSquare(Graphics2D g, int centerX, int centerY, int size, Color color)
	{
		g.setColor(color);
		g.fillRect(centerX - size / 2, centerY - size / 2, size, size);
	}
	private void drawCenterSquare(Graphics2D g, double centerX, double centerY, int size, Color color)
	{
		drawCenterSquare(g, (int)centerX, (int)centerY, size, color);
	}
}
