/*
 * Copyright (c) 2018, Cameron <https://github.com/noremac201>, SoyChai <https://github.com/SoyChai>
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
package net.runelite.client.plugins.dmgxpdrop;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.SpriteID;
import net.runelite.api.Varbits;
import net.runelite.api.events.WidgetHiddenChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.stream.IntStream;

@PluginDescriptor(
	name = "XP Drop To Damage",
	description = "Shows exp drop as damage instead",
	tags = {"experience", "levels", "tick", "prayer", "nomscripts"},
	enabledByDefault =  false
)
public class XpToDamagePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private XpToDamageConfig config;


	@Provides
	XpToDamageConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(XpToDamageConfig.class);
	}

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


		final IntStream spriteIDs =
				Arrays.stream(widget.getParent().getDynamicChildren()).mapToInt(Widget::getSpriteId);

		if (config.hideHitpoints())
		{
			if (widget.getSpriteId() == SpriteID.SKILL_HITPOINTS || spriteIDs.anyMatch(id ->
					id == SpriteID.SKILL_HITPOINTS)) {
				widget.setHidden(true);
				return;
			}
		}

		String text = widget.getText();

		if (text != null)
		{
			int color;
			try {
				int damage = Integer.parseInt(text) / 4;
				if (damage >= config.minimumDamage()) {
					color = config.getDamageThresholdColor().getRGB();
				} else {
					int defaultColorIdx = client.getVar(Varbits.EXPERIENCE_DROP_COLOR);
					color = DefaultColors.values()[defaultColorIdx].getColor().getRGB();
				}
				widget.setText(damage + "");
				widget.setTextColor(color);
			} catch (Exception e) {

			}
		}
	}
}
