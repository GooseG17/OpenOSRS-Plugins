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

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetHiddenChanged;
import net.runelite.api.events.WidgetMenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.menus.WidgetMenuOption;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.KeyEvent;
import net.runelite.client.input.KeyListener;
import java.awt.event.MouseEvent;
import net.runelite.client.input.MouseListener;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@PluginDescriptor(
	name = "WidgetFiller",
	description = "WidgetFiller",
	tags = {"highlight", "items", "overlay", "tagging", "mark", "tags", "nomscripts"},
	enabledByDefault = false
)
public class WidgetFiller extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	@Inject
	@Getter
	private WidgetFillerConfig config;

	@Inject
	private WidgetFillerOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Getter
	private List<String> chatList = new CopyOnWriteArrayList<>();

	@Provides
	WidgetFillerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(WidgetFillerConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}


	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("widgetfiller"))
		{
			chatList = Text.fromCSV(config.getHighlightItems());
		}
	}

	@Getter
	private AtomicReference<Widget> xpDropIconRef = new AtomicReference<>(null);
	@Subscribe
	public void onWidgetHiddenChanged(WidgetHiddenChanged event)
	{
		Widget widget = event.getWidget();

		int group = WidgetInfo.TO_GROUP(widget.getId());

		if (group != WidgetID.EXPERIENCE_DROP_GROUP_ID)
		{
			return;
		}

		if (widget.isHidden())
		{
			return;
		}
		if (widget.getSpriteId() > 0)
		{
			xpDropIconRef.set(widget);
		}
	}
}
