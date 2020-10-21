/*
 * Copyright (c) 2018, Kamiel, <https://github.com/Kamielvf>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
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
package net.runelite.client.plugins.screenmarkers;

import lombok.Getter;
import lombok.NonNull;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.widgetfiller.UsefulWidgets;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ImageComponent;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ScreenMarkerOverlay extends Overlay
{
	@Getter
	private final ScreenMarker marker;
	private final ScreenMarkerRenderable screenMarkerRenderable;
	private final Client client;

	@Inject
	ScreenMarkerOverlay(@NonNull ScreenMarker marker, Client client)
	{
		this.marker = marker;
		this.client = client;
		this.screenMarkerRenderable = new ScreenMarkerRenderable();
		setPosition(OverlayPosition.DETACHED);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
		setPriority(OverlayPriority.HIGH);
	}

	@Override
	public String getName()
	{
		return "marker" + marker.getId();
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!marker.isVisible())
		{
			return null;
		}

		Dimension preferredSize = getPreferredSize();
		if (preferredSize == null)
		{
			// overlay has no preferred size in the renderer configuration!
			return null;
		}

		Player p = client.getLocalPlayer();
		if (p != null) {
			if (marker.isCheckAnimation() && p.getAnimation() == -1) {
				return null;
			}
			if (marker.isCheckInteraction() && p.getInteracting() == null) {
				return null;
			}


			if (marker.isCheckMovement())
			{
//				if (client.getLocalDestinationLocation() == null) {
//					return null;
//				}
				final WorldPoint playerPos = client.getLocalPlayer().getWorldLocation();
				if (playerPos != null)
				{
					final LocalPoint playerTrueLocation = LocalPoint.fromWorld(client, playerPos);
					if (playerTrueLocation != null)
					{
						final Polygon poly1 = Perspective.getCanvasTilePoly(client, playerTrueLocation);
						final Polygon poly2 = p.getCanvasTilePoly();
						if (poly1 != null && poly2 != null && poly1.getBounds().getLocation().distance(poly2.getBounds().getLocation()) < 10) {
							return null;
						}
					}
				}
			}

			if (client.getBoostedSkillLevel(Skill.HITPOINTS) < marker.getMinHealth() || client.getBoostedSkillLevel(Skill.HITPOINTS) > marker.getMaxHealth()) {
				return null;
			}
			if (client.getBoostedSkillLevel(Skill.PRAYER) < marker.getMinPrayer() || client.getBoostedSkillLevel(Skill.PRAYER) > marker.getMaxPrayer()) {
				return null;
			}
		}

		if (client.getEnergy() < marker.getMinEnergy() || client.getEnergy() > marker.getMaxEnergy()) {
			return null;
		}

		ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);
		if (itemContainer != null) {
			final Item[] items = itemContainer.getItems();
			int size = 0;
			for (int i = 0; i < 28; i++)
			{
				if (i < items.length)
				{
					final Item item = items[i];
					if (item.getQuantity() > 0)
					{
						size++;
					}
				}
			}

			if (size < marker.getMinInventory() || size > marker.getMaxInventory()) {
				return null;
			}
		}

		if (marker.isCheckBank() && invisible(UsefulWidgets.BANK_CONTAINER))
		{
			return null;
		}
		if (marker.isCheckCraft() && invisible(UsefulWidgets.CRAFT_INTERFACE))
		{
			return null;
		}
		if (marker.isCheckChatOptions() && invisible(UsefulWidgets.DIALOG_OPTION))
		{
			return null;
		}
		if (marker.isCheckContinue() && invisible(UsefulWidgets.DIALOG_NPC_CONT))
		{
			return null;
		}
		if (marker.isCheckLevel() && invisible(UsefulWidgets.LEVEL_UP))
		{
			return null;
		}
		screenMarkerRenderable.setBorderThickness(marker.getBorderThickness());
		screenMarkerRenderable.setColor(marker.getColor());
		screenMarkerRenderable.setFill(marker.getFill());
		screenMarkerRenderable.setStroke(new BasicStroke(marker.getBorderThickness()));
		screenMarkerRenderable.setPreferredSize(preferredSize);
		return screenMarkerRenderable.render(graphics);
	}

	private boolean invisible(UsefulWidgets w) {
		Widget widget = client.getWidget(w.getGroupId(),w.getChildId());
		if (widget == null) return true;
		return widget.isHidden();
	}
}
