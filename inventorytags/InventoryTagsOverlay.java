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
package net.runelite.client.plugins.inventorytags;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.SquareOverlay;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.util.AsyncBufferedImage;

import static net.runelite.client.plugins.inventorytags.InventoryTagsPlugin.*;

public class InventoryTagsOverlay extends WidgetItemOverlay
{
	private final Client client;
	private final ItemManager itemManager;
	private final InventoryTagsPlugin plugin;
	private final InventoryTagsConfig config;
	@Inject
	private InventoryTagsOverlay(Client client, ItemManager itemManager, InventoryTagsPlugin plugin, InventoryTagsConfig config)
	{
		this.client = client;
		this.itemManager = itemManager;
		this.plugin = plugin;
		this.config = config;
		showOnEquipment();
		showOnInventory();
		showOnBank();
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget)
	{
		Rectangle bounds = itemWidget.getCanvasBounds();
		if (SquareOverlay.shouldHide(client,bounds,plugin, config.delay())) return;
		final String group = plugin.getTag(itemId);
		if (group != null)
		{
			if (!shouldPaintGroup(group)) return;
			final Color color = plugin.getGroupNameColor(group);
			if (color != null)
			{
				if (config.solidSquareSize() > 0) {
					SquareOverlay.drawCenterSquare(graphics,bounds,config.solidSquareSize(),color);
				} else {
					final BufferedImage outline = itemManager.getItemOutline(itemId, itemWidget.getQuantity(), color);

					if (config.fillSolidColor()) {
						final AsyncBufferedImage image = itemManager.getImage(itemId, itemWidget.getQuantity(), false);
						graphics.drawImage(drawColorImage(image, color), (int) bounds.getX(), (int) bounds.getY(), null);
					} else {
						graphics.drawImage(outline, (int) bounds.getX(), (int) bounds.getY(), null);
					}
				}
			}
		}
	}

	private boolean shouldPaintGroup(String group) {
		int min = 0;
		int max = 28;
		switch (group)
		{
			case SETNAME_GROUP_1:
				min = config.getGroup1Min();
				max = config.getGroup1Max();
				break;
			case SETNAME_GROUP_2:
				min = config.getGroup2Min();
				max = config.getGroup2Max();
				break;
			case SETNAME_GROUP_3:
				min = config.getGroup3Min();
				max = config.getGroup3Max();
				break;
			case SETNAME_GROUP_4:
				min = config.getGroup4Min();
				max = config.getGroup4Max();
				break;
			case SETNAME_GROUP_5:
				min = config.getGroup5Min();
				max = config.getGroup5Max();
				break;
			case SETNAME_GROUP_6:
				min = config.getGroup6Min();
				max = config.getGroup6Max();
				break;
		}
		ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);
		if (itemContainer != null) {
			int groupSum = Arrays.stream(itemContainer.getItems()).filter(item -> {
				if (item.getQuantity() <= 0) return false;
				String tag = plugin.getTag(item.getId());
				return group.equals(tag);
			}).mapToInt(item -> config.stacks() ? item.getQuantity() : 1).sum();
			if (groupSum < min || groupSum > max) return false;
		}
		return true;
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
}
