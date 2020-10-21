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

package net.runelite.client.plugins.fightcave;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.Prayer;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

@Singleton
@Slf4j
public class FightCaveOverlay extends Overlay
{
	private final FightCavePlugin plugin;
	private final Client client;

	@Inject
	FightCaveOverlay(final Client client, final FightCavePlugin plugin)
	{
		this.client = client;
		this.plugin = plugin;
//		setPosition(OverlayPosition.BOTTOM_LEFT);
//		setPriority(OverlayPriority.MED);
//		setLayer(OverlayLayer.ABOVE_SCENE);
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGHEST);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		ImagePanelComponent imagePanelComponent = null;
		for (FightCaveContainer npc : plugin.getFightCaveContainer())
		{
			if (npc.getNpc() == null)
			{
				continue;
			}

			final int ticksLeft = npc.getTicksUntilAttack();
			final FightCaveContainer.AttackStyle attackStyle = npc.getAttackStyle();

			if (ticksLeft <= 0)
			{
				continue;
			}

			final String ticksLeftStr = String.valueOf(ticksLeft);
			final int font = plugin.getFontStyle().getFont();
			final boolean shadows = plugin.isShadows();
			Color color = (ticksLeft <= 1 ? Color.WHITE : attackStyle.getColor());
			final Point canvasPoint = npc.getNpc().getCanvasTextLocation(graphics, Integer.toString(ticksLeft), 0);

			BufferedImage pray = FightCaveImageManager.getProtectionPrayerBufferedImage(npc.getAttackStyle());

			renderTextLocation(graphics, "Anim " + npc.getNpc().getAnimation(), 10, font, color, canvasPoint, shadows);
			if (npc.getNpcName().toLowerCase().contains("jad"))
			{
				color = (ticksLeft <= 1 || ticksLeft == 8 ? attackStyle.getColor() : Color.WHITE);

				Prayer prayer = null;
				switch (npc.getAttackStyle())
				{
					case MAGE:
						prayer = Prayer.PROTECT_FROM_MAGIC;
						break;
					case RANGE:
						prayer = Prayer.PROTECT_FROM_MISSILES;
						break;
					case MELEE:
						prayer = Prayer.PROTECT_FROM_MELEE;
						break;
				}
				if (prayer != null && !client.isPrayerActive(prayer))
				{
					log.info("Switch prayer to " + prayer.name() + "!");
					imagePanelComponent = new ImagePanelComponent();
					imagePanelComponent.setTitle("Switch to " + prayer.name() + "!");

					if (pray != null)
					{
						imagePanelComponent.setImage(pray);
					}
					else
					{
						imagePanelComponent.setImage(new BufferedImage(1,1,BufferedImage.TYPE_3BYTE_BGR));
					}
				}
			}

			if (pray != null)
				renderImageLocation(graphics, npc.getNpc().getCanvasImageLocation(ImageUtil.resizeImage(pray, 36, 36), 0), pray, 12, 30);
			renderTextLocation(graphics, ticksLeftStr, plugin.getTextSize(), font, color, canvasPoint, shadows);
		}


//		if (plugin.isTickTimersWidget())
//		{
//
//			if (!plugin.getMageTicks().isEmpty())
//			{
//				widgetHandler(graphics,
//					Prayer.PROTECT_FROM_MAGIC,
//					plugin.getMageTicks().get(0) == 1 ? Color.WHITE : Color.CYAN,
//					Integer.toString(plugin.getMageTicks().get(0)),
//					plugin.isShadows()
//				);
//			}
//			if (!plugin.getRangedTicks().isEmpty())
//			{
//				widgetHandler(graphics,
//					Prayer.PROTECT_FROM_MISSILES,
//					plugin.getRangedTicks().get(0) == 1 ? Color.WHITE : Color.GREEN,
//					Integer.toString(plugin.getRangedTicks().get(0)),
//					plugin.isShadows()
//				);
//			}
//			if (!plugin.getMeleeTicks().isEmpty())
//			{
//				widgetHandler(graphics,
//					Prayer.PROTECT_FROM_MELEE,
//					plugin.getMeleeTicks().get(0) == 1 ? Color.WHITE : Color.RED,
//					Integer.toString(plugin.getMeleeTicks().get(0)),
//					plugin.isShadows()
//				);
//			}
//		}
		if (imagePanelComponent != null)
		{
			return imagePanelComponent.render(graphics);
		}
		return null;
	}

	private void widgetHandler(Graphics2D graphics, Prayer prayer, Color color, String ticks, boolean shadows)
	{
		if (prayer != null)
		{
			Rectangle bounds = renderPrayerOverlay(graphics, client, prayer, color);

			if (bounds != null)
			{
				renderTextLocation(graphics, ticks, 16, plugin.getFontStyle().getFont(), color, centerPoint(bounds), shadows);
			}
		}
	}


	public static Rectangle renderPrayerOverlay(Graphics2D graphics, Client client, Prayer prayer, Color color)
	{
//		Widget widget = client.getWidget(prayer.getWidgetInfo());
//
//		if (widget == null || client.getVar(VarClientInt.PLAYER_INTERFACE_CONTAINER_OPENED) != 5)
//		{
//			return null;
//		}
//
//		Rectangle bounds = widget.getBounds();
//		renderPolygon(graphics, rectangleToPolygon(bounds), color);
//		return bounds;
		return null;
	}

	private void renderImageLocation(Graphics2D graphics, Point imgLoc, BufferedImage image, int xOffset, int yOffset)
	{
		int x = imgLoc.getX() + xOffset;
		int y = imgLoc.getY() - yOffset;

		graphics.drawImage(image, x, y, null);
	}

	private void renderTextLocation(Graphics2D graphics, String txtString, int fontSize, int fontStyle, Color fontColor, Point canvasPoint, boolean shadows)
	{
		graphics.setFont(new Font("Arial", fontStyle, fontSize));
		if (canvasPoint != null)
		{
			final Point canvasCenterPoint = new Point(
				canvasPoint.getX() - 3,
				canvasPoint.getY() + 6);
			final Point canvasCenterPoint_shadow = new Point(
				canvasPoint.getX() - 2,
				canvasPoint.getY() + 7);
			if (shadows)
			{
				OverlayUtil.renderTextLocation(graphics, canvasCenterPoint_shadow, txtString, Color.BLACK);
			}
			OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
		}
	}

	private Point centerPoint(Rectangle rect)
	{
		int x = (int) (rect.getX() + rect.getWidth() / 2);
		int y = (int) (rect.getY() + rect.getHeight() / 2);
		return new Point(x, y);
	}

	public static void main(String[] args)
	{
		List<String> names = Arrays.asList("Drainer", "Blob","Range", "Melee", "Mage", "Jad", "Healer");
		int i = 7;
		for (String name : names)
		{
			String s = "@ConfigItem(\n" +
				"\t\tposition = " + i++ + ",\n" +
				"\t\tkeyName = \"highlight" + name + "\",\n" +
				"\t\tname = \"Highlight" + name + "\",\n" +
				"\t\tdescription = \"Adds screen markers for " + name.toLowerCase() + ".\"\n" +
				"\t)\n" +
				"\tdefault boolean isHighlight" + name + "()\n" +
				"\t{\n" +
				"\t\treturn false;\n" +
				"\t}\n" +
				"\n" +
				"\t@ConfigItem(\n" +
				"\t\tposition = " + i++ + ",\n" +
				"\t\tkeyName = \"color" + name + "\",\n" +
				"\t\tname = \"" + name + " Color\",\n" +
				"\t\tdescription = \"Color for " + name.toLowerCase() + "\"\n" +
				"\t)\n" +
				"\tdefault Color get" + name + "Color()\n" +
				"\t{\n" +
				"\t\treturn Color.GREEN;\n" +
				"\t}";
			System.out.println(s);
		}

	}
}
