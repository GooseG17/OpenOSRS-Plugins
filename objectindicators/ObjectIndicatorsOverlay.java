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

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.plugins.SquareOverlay;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

@Slf4j
class ObjectIndicatorsOverlay extends Overlay
{
	private final Client client;
	private final ObjectIndicatorsConfig config;
	private final ObjectIndicatorsPlugin plugin;

	@Inject
	private ObjectIndicatorsOverlay(Client client, ObjectIndicatorsConfig config, ObjectIndicatorsPlugin plugin)
	{
		this.client = client;
		this.config = config;
		this.plugin = plugin;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.LOW);
		setLayer(SquareOverlay.OVERLAY_LAYER2);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (config.checkAnimation())
		{
			Player p = client.getLocalPlayer();
			if (p != null && p.getAnimation() != -1) return null;
		}

		for (ColorTileObject colorTileObject : plugin.getObjects())
		{
			TileObject object = colorTileObject.getTileObject();
			Color color = colorTileObject.getColor();
//			int hash = plugin.getObjectComposition(object.getId()).getId();
//			OverlayUtil.renderTextLocation(graphics, object.getCanvasLocation(), object.getId() + "", Color.WHITE);

			if (config.checkHash())
			{
				if (!plugin.getAddedIds().contains(object.getId())) continue;
			}

			if (object.getPlane() != client.getPlane())
			{
				continue;
			}

			if (color == null || !config.rememberObjectColors())
			{
				// Fallback to the current config if the object is marked before the addition of multiple colors
				color = config.markerColor();
			}

			final Shape polygon;
			Shape polygon2 = null;

			if (object instanceof GameObject)
			{
				polygon = ((GameObject) object).getConvexHull();
			}
			else if (object instanceof WallObject)
			{
				polygon = ((WallObject) object).getConvexHull();
				polygon2 = ((WallObject) object).getConvexHull2();
			}
			else if (object instanceof DecorativeObject)
			{
				polygon = ((DecorativeObject) object).getConvexHull();
				polygon2 = ((DecorativeObject) object).getConvexHull2();
			}
			else if (object instanceof GroundObject)
			{
				polygon = ((GroundObject) object).getConvexHull();
			}
			else
			{
				polygon = object.getCanvasTilePoly();
			}

//			if (polygon != null)
//			{
//				OverlayUtil.renderPolygon(graphics, polygon, color);
//			}
//
//			if (polygon2 != null)
//			{
//				OverlayUtil.renderPolygon(graphics, polygon2, color);
//			}
			drawPolygon(graphics, polygon, color);
			drawPolygon(graphics, polygon2, color);
		}

		return null;
	}


	private void drawPolygon(Graphics2D g, Shape s, Color color)
	{
		if (s == null) return;
		if (config.fillSolidColor())
		{
			g.setColor(color);
			if (config.solidSquare() > 0)
			{
				Rectangle bounds = s.getBounds();
				SquareOverlay.drawCenterSquare(g, bounds.getCenterX(), bounds.getCenterY(), config.solidSquare(), color);
			}
			else
			{
				g.fill(s);
			}
		}
		else
		{
			OverlayUtil.renderPolygon(g, s, color);
		}
	}
}