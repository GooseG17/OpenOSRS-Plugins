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
package net.runelite.client.plugins.playerindicatorscombatlevel;

import com.google.inject.Provides;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.FriendChatManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.cluescrolls.clues.BeginnerMapClue;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.runelite.api.FriendsChatRank.UNRANKED;
import static net.runelite.api.MenuAction.*;

@PluginDescriptor(
	name = "Nom Player Indicators",
	description = "Highlight players on-screen and/or on the minimap",
	tags = {"highlight", "minimap", "overlay", "players", "nomscripts"}
)
@Slf4j
public class PlayerIndicatorsPluginn extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PlayerIndicatorsConfigg config;

	@Inject
	private PlayerIndicatorsOverlayy playerIndicatorsOverlayy;

	@Inject
	private PlayerIndicatorsTileOverlayy playerIndicatorsTileOverlayy;

	@Inject
	private PlayerIndicatorsMinimapOverlayy playerIndicatorsMinimapOverlayy;

	@Inject
	private Client client;

	@Inject
	private FriendChatManager clanManager;

	@Provides
	PlayerIndicatorsConfigg provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PlayerIndicatorsConfigg.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(playerIndicatorsOverlayy);
		overlayManager.add(playerIndicatorsTileOverlayy);
		overlayManager.add(playerIndicatorsMinimapOverlayy);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(playerIndicatorsOverlayy);
		overlayManager.remove(playerIndicatorsTileOverlayy);
		overlayManager.remove(playerIndicatorsMinimapOverlayy);
	}

	@Subscribe
	public void onClientTick(ClientTick clientTick)
	{
		if (client.isMenuOpen())
		{
			return;
		}

		MenuEntry[] menuEntries = client.getMenuEntries();
		boolean modified = false;

		for (MenuEntry entry : menuEntries)
		{
			int type = entry.getType();

			if (type >= MENU_ACTION_DEPRIORITIZE_OFFSET)
			{
				type -= MENU_ACTION_DEPRIORITIZE_OFFSET;
			}

			if (type == WALK.getId()
				|| type == SPELL_CAST_ON_PLAYER.getId()
				|| type == ITEM_USE_ON_PLAYER.getId()
				|| type == PLAYER_FIRST_OPTION.getId()
				|| type == PLAYER_SECOND_OPTION.getId()
				|| type == PLAYER_THIRD_OPTION.getId()
				|| type == PLAYER_FOURTH_OPTION.getId()
				|| type == PLAYER_FIFTH_OPTION.getId()
				|| type == PLAYER_SIXTH_OPTION.getId()
				|| type == PLAYER_SEVENTH_OPTION.getId()
				|| type == PLAYER_EIGTH_OPTION.getId()
				|| type == RUNELITE_PLAYER.getId())
			{
				Player[] players = client.getCachedPlayers();
				Player player = null;

				int identifier = entry.getIdentifier();

				// 'Walk here' identifiers are offset by 1 because the default
				// identifier for this option is 0, which is also a player index.
				if (type == WALK.getId())
				{
					identifier--;
				}

				if (identifier >= 0 && identifier < players.length)
				{
					player = players[identifier];
				}

				if (player == null)
				{
					continue;
				}

				Decorations decorations = getDecorations(player);

				if (decorations == null)
				{
					continue;
				}

				String oldTarget = entry.getTarget();
				String newTarget = decorateTarget(oldTarget, decorations);

				entry.setTarget(newTarget);
				modified = true;
			}
		}

		if (modified)
		{
			client.setMenuEntries(menuEntries);
		}
	}

	private Decorations getDecorations(Player player)
	{
		int image = -1;
		Color color = null;

		if (config.highlightFriends() && player.isFriend())
		{
			color = config.getFriendColor();
		}
		else if (config.drawClanMemberNames() && player.isFriendsChatMember())
		{
			color = config.getClanMemberColor();

			FriendsChatRank rank = clanManager.getRank(player.getName());
			if (rank != UNRANKED)
			{
				image = clanManager.getIconNumber(rank);
			}
		}
		else if (config.highlightTeamMembers()
			&& player.getTeam() > 0 && client.getLocalPlayer().getTeam() == player.getTeam())
		{
			color = config.getTeamMemberColor();
		}
		else if (config.highlightNonClanMembers() && !player.isFriendsChatMember())
		{
			color = config.getNonClanMemberColor();
		}

		if (image == -1 && color == null)
		{
			return null;
		}

		return new Decorations(image, color);
	}

	private String decorateTarget(String oldTarget, Decorations decorations)
	{
		String newTarget = oldTarget;

		if (decorations.getColor() != null && config.colorPlayerMenu())
		{
			// strip out existing <col...
			int idx = oldTarget.indexOf('>');
			if (idx != -1)
			{
				newTarget = oldTarget.substring(idx + 1);
			}

			newTarget = ColorUtil.prependColorTag(newTarget, decorations.getColor());
		}

		if (decorations.getImage() != -1 && config.showClanRanks())
		{
			newTarget = "<img=" + decorations.getImage() + ">" + newTarget;
		}

		return newTarget;
	}

	@Value
	private static class Decorations
	{
		private final int image;
		private final Color color;
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired scriptPostFired)
	{
		if (scriptPostFired.getScriptId() == ScriptID.PVP_WIDGET_BUILDER)
		{
			appendAttackLevelRangeText();
		}
	}


	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == WidgetID.PVP_GROUP_ID)
		{
			appendAttackLevelRangeText();
		}
	}

	public boolean withinRange(Player p) {
		return p.getCombatLevel() >= minCombatLevel && p.getCombatLevel() <= maxCombatLevel;
	}

	private int minCombatLevel = 0;
	private int maxCombatLevel = 0;

	private void combatAttackRange(final int combatLevel, final int wildernessLevel)
	{
		minCombatLevel = Math.max(3, combatLevel - wildernessLevel);
		maxCombatLevel = Math.min(Experience.MAX_COMBAT_LEVEL, combatLevel + wildernessLevel);
	}

	private final Pattern WILDERNESS_LEVEL_PATTERN = Pattern.compile(".*?(\\d+)-(\\d+).*");
	private void appendAttackLevelRangeText() {
		final Widget wildernessLevelWidget = client.getWidget(WidgetInfo.PVP_WILDERNESS_LEVEL);
		final Widget pvpWorldWidget = client.getWidget(90,58);

		String wildernessLevelText = "";
		if (pvpWorldWidget != null && !pvpWorldWidget.isHidden()) {
			wildernessLevelText = pvpWorldWidget.getText();
		}
		if (wildernessLevelText.isEmpty() && (wildernessLevelWidget != null && !wildernessLevelWidget.isHidden())) {
			wildernessLevelText = wildernessLevelWidget.getText();
		}
		if (wildernessLevelText.isEmpty()) {
			minCombatLevel = 0;
			maxCombatLevel = 0;
			return;
		}

		log.info("text " + wildernessLevelText);
		log.info("wildy level " + minCombatLevel + " " + maxCombatLevel);
		final Matcher m = WILDERNESS_LEVEL_PATTERN.matcher(wildernessLevelText);
		if (!m.matches()) {
			return;
		}
//		final int wildernessLevel = Integer.parseInt(m.group(1));
//		final int combatLevel = Objects.requireNonNull(client.getLocalPlayer()).getCombatLevel();
//		combatAttackRange(combatLevel, wildernessLevel);
		minCombatLevel = Integer.parseInt(m.group(1));
		maxCombatLevel = Integer.parseInt(m.group(2));
	}
}
