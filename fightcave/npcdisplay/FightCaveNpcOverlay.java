/*
 * Copyright (c) 2019, Ganom <https://github.com/Ganom>
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

package net.runelite.client.plugins.fightcave.npcdisplay;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.fightcave.FightCaveConfig;
import net.runelite.client.plugins.fightcave.FightCaveContainer;
import net.runelite.client.plugins.fightcave.FightCavePlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
public class FightCaveNpcOverlay extends Overlay
{
	private final FightCavePlugin plugin;
	private final Client client;
	private final FightCaveConfig config;
	@Inject
	FightCaveNpcOverlay(final Client client, final FightCavePlugin plugin, final FightCaveConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPosition(OverlayPosition.DYNAMIC);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		for (FightCaveContainer container : plugin.getFightCaveContainer())
		{
			NPC npc = container.getNpc();
			switch (npc.getId())
			{
				case NpcID.TZKIH:
				case NpcID.TZKIH_2190:
				case NpcID.TZKIH_3116:
				case NpcID.TZKIH_3117:
					if (config.isHighlightDrainer())
					{
						renderNpcOverlay(graphics, npc, getShortName(npc), config.getDrainerColor());
						renderNpcOverlay(graphics, npc, config.getDrainerColor());
					}
					break;
				case NpcID.TZKEK:
				case NpcID.TZKEK_2192:
				case NpcID.TZKEK_3118:
				case NpcID.TZKEK_3119:
				case NpcID.TZKEK_3120:
					if (config.isHighlightBlob())
					{
						renderNpcOverlay(graphics, npc, getShortName(npc), config.getBlobColor());
						renderNpcOverlay(graphics, npc, config.getBlobColor());
					}
					break;
				case NpcID.TOKXIL_3121:
				case NpcID.TOKXIL_3122:
					if (config.isHighlightRange())
					{
						renderNpcOverlay(graphics, npc, getShortName(npc), config.getRangeColor());
						renderNpcOverlay(graphics, npc, config.getRangeColor());
					}
					break;
				case NpcID.YTMEJKOT:
				case NpcID.YTMEJKOT_3124:
					if (config.isHighlightMelee())
					{
						renderNpcOverlay(graphics, npc, getShortName(npc), config.getMeleeColor());
						renderNpcOverlay(graphics, npc, config.getMeleeColor());
					}
					break;
				case NpcID.KETZEK:
				case NpcID.KETZEK_3126:
					if (config.isHighlightMage())
					{
						renderNpcOverlay(graphics, npc, getShortName(npc), config.getMageColor());
						renderNpcOverlay(graphics, npc, config.getMageColor());
					}
					break;
				case NpcID.TZTOKJAD:
				case NpcID.TZTOKJAD_6506:
					if (config.isHighlightJad())
					{
						renderNpcOverlay(graphics, npc, getShortName(npc), config.getJadColor());
						renderNpcOverlay(graphics, npc, config.getJadColor());
					}
					break;
				case NpcID.YTHURKOT:
				case NpcID.YTHURKOT_7701:
				case NpcID.YTHURKOT_7705:
					if (config.isHighlightHealer())
					{
						renderNpcOverlay(graphics, npc, getShortName(npc), config.getHealerColor());
						renderNpcOverlay(graphics, npc, config.getHealerColor());
					}
					break;
			}

		}

		return null;
	}

	private void renderNpcOverlay(Graphics2D graphics, NPC actor, String name, Color color)
	{
		Point minimapLocation = actor.getMinimapLocation();
		if (minimapLocation != null)
		{
			OverlayUtil.renderMinimapLocation(graphics, minimapLocation, color.darker());

			if (config.drawMinimapNames())
			{
				OverlayUtil.renderTextLocation(graphics, minimapLocation, name, color);
			}
		}
	}

	private String getShortName(NPC npc)
	{
		switch (npc.getId())
		{
			case NpcID.TZKIH:
			case NpcID.TZKIH_2190:
			case NpcID.TZKIH_3116:
			case NpcID.TZKIH_3117:
				return "Drainer";
			case NpcID.TZKEK:
			case NpcID.TZKEK_2192:
			case NpcID.TZKEK_3118:
			case NpcID.TZKEK_3119:
			case NpcID.TZKEK_3120:
				return "Blob";
			case NpcID.TOKXIL_3121:
			case NpcID.TOKXIL_3122:
				return "Range";
			case NpcID.YTMEJKOT:
			case NpcID.YTMEJKOT_3124:
				return "Melee";
			case NpcID.KETZEK:
			case NpcID.KETZEK_3126:
				return "Mage";
			case NpcID.TZTOKJAD:
			case NpcID.TZTOKJAD_6506:
				return "Jad";
			case NpcID.YTHURKOT:
			case NpcID.YTHURKOT_7701:
			case NpcID.YTHURKOT_7705:
				return "Healer";
		}
		return "";
	}

	private void renderNpcOverlay(Graphics2D graphics, NPC actor, Color color)
	{
		switch (config.renderStyle())
		{
			case SOUTH_WEST_TILE:
				LocalPoint lp1 = LocalPoint.fromWorld(client, actor.getWorldLocation());
				if (lp1 == null)
				{
					break;
				}
				Polygon tilePoly1 = Perspective.getCanvasTilePoly(client, lp1);

				renderPoly(graphics, color, tilePoly1);
				break;

			case TILE:
				int size = 1;
				NPCComposition composition = actor.getTransformedComposition();
				if (composition != null)
				{
					size = composition.getSize();
				}
				LocalPoint lp = actor.getLocalLocation();
				Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);

				renderPoly(graphics, color, tilePoly);
				break;

			case HULL:
				Shape objectClickbox = actor.getConvexHull();

				renderPoly(graphics, color, objectClickbox);
				break;
		}

		if (config.drawNames() && actor.getName() != null)
		{
//			String npcName = Text.removeTags(actor.getName());
			String npcName = getShortName(actor);
			Point textLocation = actor.getCanvasTextLocation(graphics, npcName, actor.getLogicalHeight() + 40);

			if (textLocation != null)
			{
				OverlayUtil.renderTextLocation(graphics, textLocation, npcName, color);
			}
		}
	}

	private void renderPoly(Graphics2D graphics, Color color, Shape polygon)
	{
		if (polygon != null)
		{
			graphics.setColor(color);
			graphics.setStroke(new BasicStroke(2));
			graphics.draw(polygon);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
			graphics.fill(polygon);
		}
	}
}
