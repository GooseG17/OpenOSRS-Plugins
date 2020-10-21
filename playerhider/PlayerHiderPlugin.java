/*
 * Copyright (c) 2020, ThatGamerBlue <thatgamerblue@gmail.com>
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
package net.runelite.client.plugins.playerhider;

import com.google.common.primitives.Ints;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;

/*
Mental breakdown 2: electric boogaloo

Alexa, play sea shanty two.
Peace to:
	r189, he.cc
*/
@PluginDescriptor(
	name = "Player Hider",
	description = "Hides your player for streamers.",
	tags = {"twitch, nomscripts"},
	enabledByDefault = false
)
public class PlayerHiderPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private PlayerHiderConfig config;

	@Inject
	private EventBus eventBus;


	@Provides
	private PlayerHiderConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PlayerHiderConfig.class);
	}

	@Override
	public void startUp()
	{
		changeLook();
		randomiseStats();
	}

	@Override
	public void shutDown()
	{

	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("playerhider"))
		{
			changeLook();
			if (event.getKey().equals("playerStats")) {
				randomiseStats();
			}
			if (event.getKey().equals("thumbnail")) {
				thumbnailStats();
			}
			if (event.getKey().equals("toggleStatChange") && config.toggleStatChange()) {
				changeStat(config.skillToChange(), config.statLevel());
			}
		}
	}

	private void changeLook() {
		Player player = client.getLocalPlayer();
		if (player == null || player.getPlayerComposition() == null) return;

		if (config.playerModel() > 0) {
			player.getPlayerComposition().setTransformedNpcId(config.playerModel());
//			player.setIdlePoseAnimation(-1);
//			player.setPoseAnimation(-1);
		}
	}

	private void randomiseStats() {
		if (!config.playerStats()) return;
		for (Skill value : Skill.values()) {
			changeStat(value, 69);
		}
	}

	private void changeStat(Skill skill, int level) {
		level = Ints.constrainToRange(level, 1, Experience.MAX_REAL_LEVEL);
		int xp = Experience.getXpForLevel(level);

		client.getBoostedSkillLevels()[skill.ordinal()] = level;
		client.getRealSkillLevels()[skill.ordinal()] = level;
		client.getSkillExperiences()[skill.ordinal()] = xp;

		client.queueChangedSkill(skill);
	}


	private void thumbnailStats() {
		Widget statsContainer = client.getWidget(WidgetInfo.SKILLS_CONTAINER);
		if (statsContainer == null || statsContainer.getStaticChildren() == null) return;
		for (Widget child : statsContainer.getStaticChildren()) {
			if (child != null && child.getChildren() != null) {
				for (Widget childChild : child.getChildren()) {
					if (childChild != null && child.getText() != null) {
						childChild.setText("9?");
					}
				}
			}
		}
	}


	@Subscribe
	public void onStatChanged(StatChanged statChanged)
	{
		randomiseStats();
		changeLook();
	}

	@Subscribe
	public void onPlayerSpawned(PlayerSpawned event)
	{
		final Player local = client.getLocalPlayer();
		final Player player = event.getPlayer();

		if (player.equals(local))
		{
			changeLook();
		}
	}

	@Subscribe
	private void onBeforeRender(BeforeRender event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		changeLook();
	}

}
