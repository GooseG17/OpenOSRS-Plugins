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
package net.runelite.client.plugins.widgetfiller;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.SquareOverlay;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.infobox.Timer;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

@Slf4j
public class WidgetFillerOverlay extends Overlay
{
	private final WidgetFiller plugin;
	private final Client client;
	private HashMap<Point, Timer> timerHashMap = new HashMap<>();
	private final WidgetFillerConfig config;
	@Inject
	public WidgetFillerOverlay(WidgetFiller plugin, Client client) {
		this.plugin = plugin;
		this.client = client;
		this.config = plugin.getConfig();
		setPosition(OverlayPosition.DETACHED);
		setLayer(SquareOverlay.OVERLAY_LAYER2);
		setPriority(OverlayPriority.HIGH);
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

	@Override
	public Dimension render(Graphics2D graphics) {

		Widget xpDrop = plugin.getXpDropIconRef().get();
		if (config.fillXpDrop() && xpDrop != null && !xpDrop.isHidden()) {
			SquareOverlay.drawCenterSquare(graphics,xpDrop.getBounds(),config.solidSquare(),config.getXpDropColor());
		}

		Widget chatList = client.getWidget(WidgetInfo.CHATBOX_MESSAGE_LINES);
		Widget chatBox = client.getWidget(WidgetInfo.CHATBOX_PARENT);
		if (chatBox != null && chatList != null && chatList.getChildren() != null)
		for (Widget child : chatList.getChildren()) {
			if (!chatBox.contains(child.getCanvasLocation())) break;
			if (child == null || child.isHidden()) continue;
			if (plugin.getChatList().stream().map(String::toLowerCase).anyMatch(s -> child.getText().toLowerCase().contains(s))) {
				Rectangle bounds = child.getBounds();
				if (shouldHide(child)) continue;
				if (config.solidSquare() > 0)
				drawCenterSquare(graphics, bounds.getCenterX(),bounds.getCenterY(),config.solidSquare(),config.getHighlightChatColor());
				else fillPolygon(graphics, rectangleToPolygon(bounds), config.getHighlightChatColor());
			}
		}

		for (int i = 0; i < 5; i++) {
			Color color = null;
			Widget w = null;
			switch (i) {
				case 0:
					color = config.getGroup0Color();
					w = client.getWidget(config.parent(), config.child());
					break;
				case 1:
					color = config.getGroup1Color();
					w = client.getWidget(config.widget1().getGroupId(), config.widget1().getChildId());
					break;
				case 2:
					color = config.getGroup2Color();
					w = client.getWidget(config.widget2().getGroupId(), config.widget2().getChildId());
					break;
				case 3:
					color = config.getGroup3Color();
					w = client.getWidget(config.widget3().getGroupId(), config.widget3().getChildId());
					break;
				case 4:
					color = config.getGroup4Color();
					w = client.getWidget(config.widget4().getGroupId(), config.widget4().getChildId());
					break;
			}

			if (w == null || color == null) continue;
			if (config.checkVisible() && w.isHidden()) continue;
			Rectangle bounds = w.getBounds();
			if (bounds == null) continue;

			if (config.solidSquare() > 0)
			{
				if (!shouldHide(w))
				{
					drawCenterSquare(graphics, bounds.getCenterX(), bounds.getCenterY(), config.solidSquare(), color);
				}
			}
			else
			{
				fillPolygon(graphics, rectangleToPolygon(bounds), color);
			}
		}
		return null;
	}

	public static void fillPolygon(Graphics2D graphics, Polygon poly, Color color)
	{
		graphics.setColor(color);
		graphics.fillPolygon(poly);
	}

	public static Polygon rectangleToPolygon(Rectangle rect) {
		int[] xpoints = {rect.x, rect.x + rect.width, rect.x + rect.width, rect.x};
		int[] ypoints = {rect.y, rect.y, rect.y + rect.height, rect.y + rect.height};
		return new Polygon(xpoints, ypoints, 4);
	}

	private boolean shouldHide(Widget w) {
		Rectangle bounds = w.getBounds();
		if (bounds == null) return false;

		if (config.delay() > 0 &&
				bounds.contains(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY()))
		{
			timerHashMap.put(w.getCanvasLocation(), new Timer(config.delay(), ChronoUnit.MILLIS, new BufferedImage(1, 1, 1), plugin));
			return true;
		}

		if (timerHashMap.containsKey(w.getCanvasLocation()))
		{
			Timer timer = timerHashMap.get(w.getCanvasLocation());
			Duration timeLeft = Duration.between(Instant.now(), timer.getEndTime());
			if (timeLeft.isNegative())
			{
				timerHashMap.remove(w.getCanvasLocation());
				return false;
			}
			return true;
		}
		return false;
	}
}
