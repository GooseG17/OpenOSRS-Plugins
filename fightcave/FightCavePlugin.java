/*
 * Copyright (c) 2018, Jordan Atwood <jordan.atwood423@gmail.com>
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

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.*;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.NPCManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.fightcave.npcdisplay.FightCaveNpcOverlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.components.LineComponent;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginDescriptor(
	name = "Fight Cave",
	description = "Displays current and upcoming wave monsters in the Fight Caves",
	tags = {"bosses", "combat", "minigame", "overlay", "pve", "pvm", "jad", "fire", "cape", "wave","nomscripts"},
	enabledByDefault = true
)
@Singleton
@Slf4j
public class FightCavePlugin extends Plugin
{
	public static final int MAX_WAVE = 63;
	@Getter(AccessLevel.PACKAGE)
	static final List<EnumMap<WaveMonster, Integer>> WAVES = new ArrayList<>();
	private static final Pattern WAVE_PATTERN = Pattern.compile(".*Wave: (\\d+).*");
	private static final int FIGHT_CAVE_REGION = 9551;
	private static final int MAX_MONSTERS_OF_TYPE_PER_WAVE = 2;

	public static final int TZTOK_JAD_RANGE_ATTACK = 2652;
	public static final int TZTOK_JAD_MELEE_ATTACK = 2655;
	public static final int TZTOK_JAD_MAGIC_ATTACK = 2656;
	public static final int TOK_XIL_RANGE_ATTACK = 2633;
	public static final int TOK_XIL_MELEE_ATTACK = 2628;
	public static final int KET_ZEK_MELEE_ATTACK = 2644;
	public static final int KET_ZEK_MAGE_ATTACK = 2647;
	public static final int MEJ_KOT_MELEE_ATTACK = 2637;
	public static final int MEJ_KOT_HEAL_ATTACK = 2639;

	@Inject
	private ChatMessageManager chatMessageManager;

	static
	{
		final WaveMonster[] waveMonsters = WaveMonster.values();

		// Add wave 1, future waves are derived from its contents
		final EnumMap<WaveMonster, Integer> waveOne = new EnumMap<>(WaveMonster.class);
		waveOne.put(waveMonsters[0], 1);
		WAVES.add(waveOne);

		for (int wave = 1; wave < MAX_WAVE; wave++)
		{
			final EnumMap<WaveMonster, Integer> prevWave = WAVES.get(wave - 1).clone();
			int maxMonsterOrdinal = -1;

			for (int i = 0; i < waveMonsters.length; i++)
			{
				final int ordinalMonsterQuantity = prevWave.getOrDefault(waveMonsters[i], 0);

				if (ordinalMonsterQuantity == MAX_MONSTERS_OF_TYPE_PER_WAVE)
				{
					maxMonsterOrdinal = i;
					break;
				}
			}

			if (maxMonsterOrdinal >= 0)
			{
				prevWave.remove(waveMonsters[maxMonsterOrdinal]);
			}

			final int addedMonsterOrdinal = maxMonsterOrdinal >= 0 ? maxMonsterOrdinal + 1 : 0;
			final WaveMonster addedMonster = waveMonsters[addedMonsterOrdinal];
			final int addedMonsterQuantity = prevWave.getOrDefault(addedMonster, 0);

			prevWave.put(addedMonster, addedMonsterQuantity + 1);

			WAVES.add(prevWave);
		}
	}

	@Inject
	private FightCaveImageManager fightCaveImageManager;
	@Inject
	private Client client;
	@Inject
	private NPCManager npcManager;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private WaveOverlay waveOverlay;
	@Inject
	private FightCaveOverlay fightCaveOverlay;
	@Inject
	private FightCaveNpcOverlay fightCaveNpcOverlay;
	@Inject
	private FightCavePrayerOverlay fightCavePrayerOverlay;
	@Inject
	private FightCaveConfig config;

	@Getter(AccessLevel.PUBLIC)
	private Set<FightCaveContainer> fightCaveContainer = new HashSet<>();
	@Getter(AccessLevel.PACKAGE)
	private int currentWave = -1;
	@Getter(AccessLevel.PACKAGE)
	private boolean validRegion;
	@Getter(AccessLevel.PACKAGE)
	private List<Integer> mageTicks = new ArrayList<>();
	@Getter(AccessLevel.PACKAGE)
	private List<Integer> rangedTicks = new ArrayList<>();
	@Getter(AccessLevel.PACKAGE)
	private List<Integer> meleeTicks = new ArrayList<>();

	static String formatMonsterQuantity(final WaveMonster monster, final int quantity)
	{
		return String.format("%dx %s", quantity, monster);
	}


	@Getter(AccessLevel.PACKAGE)
	private WaveDisplayMode waveDisplay;
	@Getter(AccessLevel.PACKAGE)
	private boolean tickTimersWidget;
	@Getter(AccessLevel.PACKAGE)
	private FightCaveConfig.FontStyle fontStyle;
	@Getter(AccessLevel.PACKAGE)
	private int textSize;
	@Getter(AccessLevel.PACKAGE)
	private boolean shadows;

	@Provides
	FightCaveConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FightCaveConfig.class);
	}

	@Override
	public void startUp()
	{
		updateConfig();

		if (client.getGameState() == GameState.LOGGED_IN && regionCheck())
		{
			validRegion = true;
			overlayManager.add(waveOverlay);
			overlayManager.add(fightCaveOverlay);
			overlayManager.add(fightCaveNpcOverlay);
			overlayManager.add(fightCavePrayerOverlay);
		}
	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(waveOverlay);
		overlayManager.remove(fightCaveOverlay);
		overlayManager.remove(fightCaveNpcOverlay);
		overlayManager.remove(fightCavePrayerOverlay);
		currentWave = -1;
	}

	private static final int JAD_MAGIC_SOUND_ID = 159;
	private static final int JAD_MAGIC_SOUND_ID_2 = 162;
	private static final int JAD_HIT_SOUND = 163;

	@Subscribe
	public void onSoundEffectPlayed(SoundEffectPlayed event)
	{
//		if (event.getSource() == null)
//		{
//			log.info("SOUND " + event.getSoundId() +"  " + event.getDelay());
//		}
//		else
//		{
//			log.info(event.getSource().getName() + "SOUND " + event.getSoundId() +"  " + event.getDelay());
//		}

		if (event.getSoundId() == JAD_MAGIC_SOUND_ID)
		{
			log.info("Pray MAGIC");
			sendChatMessage("Pray MAGIC-------------");
		}
	}

	@Subscribe
	public void onAreaSoundEffectPlayed(AreaSoundEffectPlayed event)
	{

//		String text =
//				"Id: " + event.getSoundId() +
//						" - S: " + (event.getSource() != null ? event.getSource().getName() : "<none>") +
//						" - L: " + event.getSceneX() + "," + event.getSceneY() +
//						" - R: " + event.getRange() +
//						" - D: " + event.getDelay();
//
//		log.info(text);
//		sendChatMessage(text);
	}

	private void sendChatMessage(String chatMessage)
	{
		final String message = new ChatMessageBuilder()
				.append(ChatColorType.HIGHLIGHT)
				.append(chatMessage)
				.build();

		chatMessageManager.queue(
				QueuedMessage.builder()
						.type(ChatMessageType.CONSOLE)
						.runeLiteFormattedMessage(message)
						.build());
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("fightcave"))
		{
			return;
		}

//		List<Integer> list = Arrays.asList(
//				JAD_MAGIC_SOUND_ID,
//				2677,
//				2401,
//				162,163,2675,163,2766,159,2675,2767,509
//		);
//		int r = list.get(new Random().nextInt(list.size()));
//		log.info("Playing sound "+r);
//		client.playSoundEffect(r);
		updateConfig();
	}

	@Subscribe
	private void onChatMessage(ChatMessage event)
	{
		if (!validRegion)
		{
			return;
		}

		final Matcher waveMatcher = WAVE_PATTERN.matcher(event.getMessage());

		if (event.getType() != ChatMessageType.GAMEMESSAGE || !waveMatcher.matches())
		{
			return;
		}

		currentWave = Integer.parseInt(waveMatcher.group(1));
		log.info("Current wave " + currentWave);
		if (currentWave != MAX_WAVE) {
			log.info("Clearing wave container");
			fightCaveContainer.clear();
		} else {
			if (fightCaveContainer.stream().noneMatch(npc -> npc.getNpcName().toLowerCase().contains("jad"))) {
				log.info("Clearing wave container on max wave, no jad around");
				fightCaveContainer.clear();
			}
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (regionCheck())
		{
			validRegion = true;
			overlayManager.add(waveOverlay);
			overlayManager.add(fightCaveOverlay);
			overlayManager.add(fightCaveNpcOverlay);
			overlayManager.add(fightCavePrayerOverlay);
		}
		else
		{
			validRegion = false;
			overlayManager.remove(waveOverlay);
			overlayManager.remove(fightCaveOverlay);
			overlayManager.remove(fightCaveNpcOverlay);
			overlayManager.remove(fightCavePrayerOverlay);
			fightCaveContainer.clear();
		}
	}

	@Subscribe
	private void onNpcSpawned(NpcSpawned event)
	{
		if (!validRegion)
		{
			return;
		}

		NPC npc = event.getNpc();

		if (getCurrentWave() == MAX_WAVE) {
			log.info(npc + " has spawned on max wave!");
		}

		switch (npc.getId())
		{
			case NpcID.TZTOKJAD:
			case NpcID.TZTOKJAD_6506:
				log.info("Jad has spawned!");
				fightCaveContainer.removeIf(c ->{
					NPC nn = c.getNpc();
					if (nn == null) return true;
					if (nn.isDead()) {
						log.info("Removing dead jad");
						return true;
					}
					String name = nn.getName();
					if (name == null) {
						log.info("Removing null npc");
						return true;
					}
					String lower = name.toLowerCase();
					if (lower.contains("jad") || name.isEmpty()) {
						log.info("Removing old jad");
						return true;
					}
					return false;
				});
			case NpcID.TZKIH:
			case NpcID.TZKIH_2190:
			case NpcID.TZKIH_3116:
			case NpcID.TZKIH_3117:
			case NpcID.TZKEK:
			case NpcID.TZKEK_2192:
			case NpcID.TZKEK_3118:
			case NpcID.TZKEK_3119:
			case NpcID.TZKEK_3120:
			case NpcID.YTHURKOT:
			case NpcID.YTHURKOT_7701:
			case NpcID.YTHURKOT_7705:
			case NpcID.TOKXIL_3121:
			case NpcID.TOKXIL_3122:
			case NpcID.YTMEJKOT:
			case NpcID.YTMEJKOT_3124:
			case NpcID.KETZEK:
			case NpcID.KETZEK_3126: //Orange one
				log.info(npc.getName() + " has spawned!");
				fightCaveContainer.add(new FightCaveContainer(npc));
				break;
		}
	}

	@Subscribe
	private void onNpcDespawned(NpcDespawned event)
	{
		if (!validRegion)
		{
			return;
		}

		NPC npc = event.getNpc();

		switch (npc.getId())
		{
			case NpcID.TZTOKJAD:
			case NpcID.TZTOKJAD_6506:
			case NpcID.TZKIH:
			case NpcID.TZKIH_2190:
			case NpcID.TZKIH_3116:
			case NpcID.TZKIH_3117:
			case NpcID.TZKEK:
			case NpcID.TZKEK_2192:
			case NpcID.TZKEK_3118:
			case NpcID.TZKEK_3119:
			case NpcID.TZKEK_3120:
			case NpcID.YTHURKOT:
			case NpcID.YTHURKOT_7701:
			case NpcID.YTHURKOT_7705:
			case NpcID.TOKXIL_3121:
			case NpcID.TOKXIL_3122:
			case NpcID.YTMEJKOT:
			case NpcID.YTMEJKOT_3124:
			case NpcID.KETZEK:
			case NpcID.KETZEK_3126:
				log.info(npc.getName() + " has despawned!");
				fightCaveContainer.removeIf(c -> c.getNpc() == npc);
				break;
		}
	}

	@Subscribe
	private void onGameTick(GameTick Event)
	{
		if (!validRegion)
		{
			return;
		}

		mageTicks.clear();
		rangedTicks.clear();
		meleeTicks.clear();

		for (FightCaveContainer npc : fightCaveContainer)
		{
			if (npc.getTicksUntilAttack() >= 0)
			{
				npc.setTicksUntilAttack(npc.getTicksUntilAttack() - 1);
			}

			for (int anims : npc.getAnimations())
			{

				if (npc.getNpcName().toLowerCase().contains("jad"))
				{
					log.info("Jad animation " + npc.getNpc().getAnimation());
				}
				if (anims == npc.getNpc().getAnimation())
				{
					if (npc.getTicksUntilAttack() < 1)
					{
						npc.setTicksUntilAttack(npc.getAttackSpeed());
					}

					switch (anims)
					{
						case TZTOK_JAD_RANGE_ATTACK:
							log.info("Set Jad ranged attack");
							npc.setAttackStyle(FightCaveContainer.AttackStyle.RANGE);
							break;
						case TZTOK_JAD_MAGIC_ATTACK:
							log.info("Set Jad magic attack");
							npc.setAttackStyle(FightCaveContainer.AttackStyle.MAGE);
							break;
						case TZTOK_JAD_MELEE_ATTACK:
							log.info("Set Jad melee attack");
							npc.setAttackStyle(FightCaveContainer.AttackStyle.MELEE);
							break;
					}
				}
			}

			if (npc.getNpcName().toLowerCase().contains("jad"))
			{
				continue;
			}

			switch (npc.getAttackStyle())
			{
				case RANGE:
					if (npc.getTicksUntilAttack() > 0)
					{
						rangedTicks.add(npc.getTicksUntilAttack());
					}
					break;
				case MELEE:
					if (npc.getTicksUntilAttack() > 0)
					{
						meleeTicks.add(npc.getTicksUntilAttack());
					}
					break;
				case MAGE:
					if (npc.getTicksUntilAttack() > 0)
					{
						mageTicks.add(npc.getTicksUntilAttack());
					}
					break;
			}
		}

		Collections.sort(mageTicks);
		Collections.sort(rangedTicks);
		Collections.sort(meleeTicks);
	}

	private boolean regionCheck()
	{
		if (ArrayUtils.contains(client.getMapRegions(), FIGHT_CAVE_REGION))
		{
			log.info("In fight cave region");
			return true;
		}
		return false;
	}

	private void updateConfig()
	{
		this.waveDisplay = config.waveDisplay();
//		this.tickTimersWidget = config.tickTimersWidget();
		this.tickTimersWidget = false;
		this.fontStyle = config.fontStyle();
		this.textSize = config.textSize();
		this.shadows = config.shadows();
	}
}
