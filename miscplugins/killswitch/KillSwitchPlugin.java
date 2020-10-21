/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2018, Abexlry <abexlry@gmail.com>
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
package net.runelite.client.plugins.miscplugins.killswitch;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.miscplugins.DiscordWebhook;
import net.runelite.client.plugins.miscplugins.killswitch.killtimers.*;
import net.runelite.client.plugins.miscplugins.killswitch.killtriggers.MessageKillTrigger;
import net.runelite.client.plugins.miscplugins.killswitch.killtriggers.NoItemInSlotKillTrigger;
import net.runelite.client.plugins.miscplugins.killswitch.killtriggers.StationaryKillTrigger;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import javax.inject.Inject;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;

@PluginDescriptor(
	name = "Kill Switch",
	description = "Allows use of WASD keys for camera movement with 'Press Enter to Chat', and remapping number keys to F-keys",
	tags = {"ahk", "ghost", "mouse","nomscripts"},
	enabledByDefault = false
)
public class KillSwitchPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private KeyManager keyManager;
	@Inject
	private MouseManager mouseManager;

	@Inject
	private KillSwitchListener inputListener;

	@Inject
	private KillSwitchConfig config;

	@Inject
	private InfoBoxManager infoBoxManager;
	@Inject
	private ItemManager itemManager;


	private AnimationKillTimer animationKillTimer;

	private ExperienceKillTimer experienceKillTimer;

	private InteractionKillTimer interactionKillTimer;

	private MovementKillTimer movementKillTimer;

	private RuntimeKillTimer runtimeKillTimer;

	@Getter
	private List<KillTimer> killTimers;

	private MessageKillTrigger messageKillTrigger;
	private StationaryKillTrigger stationaryKillTrigger;
	private List<NoItemInSlotKillTrigger> noItemInSlotKillTriggers;

	@Inject
	private ScheduledExecutorService executorService;


	@Override
	protected void startUp() throws Exception
	{
		initTimers();
		initTriggers();
		keyManager.registerKeyListener(inputListener);
		mouseManager.registerMouseListener(inputListener);
	}

	@Override
	protected void shutDown() throws Exception
	{
		keyManager.unregisterKeyListener(inputListener);
		mouseManager.unregisterMouseListener(inputListener);
	}

	@Provides
	KillSwitchConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(KillSwitchConfig.class);
	}


	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("killswitch"))
		{
			if (event.getKey().equals("killSwitchActive")) {
				stopInput = false;
				sentWebhook = false;
				initTimers();
				initTriggers();
				lastNumInfoBoxes = 0;
				if (!config.killSwitchActive()) {
					removeTimers();
				}
			}
		}
	}


	@Getter
	private boolean stopInput;
	@Getter
	private boolean sentWebhook;

	private void sendWebhook() {
		if (!stopInput || sentWebhook) return;
		if (config.discordWebhook().isEmpty()) return;
		sentWebhook = true;
		executorService.execute(()-> DiscordWebhook.sendString("Killswitch triggered!", config.discordWebhook()));
	}


	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN || event.getGameState() == GameState.LOGGING_IN) {
			removeTimers();
			lastNumInfoBoxes = 0;
			initTimers();
			initTriggers();
			stopInput = false;
			sentWebhook = false;
		}
	}

	@Subscribe
	public void onStatChanged(StatChanged statChanged)
	{
		experienceKillTimer.checkIfShouldReset();
	}

	@Schedule(
			period = 5000,
			unit = ChronoUnit.MILLIS
	)
	public void updateSchedule()
	{
		sendWebhook();
	}

	private int lastNumInfoBoxes = 0;
	@Subscribe
	public void onGameTick(GameTick event)
	{
		final Player local = client.getLocalPlayer();

		if (!config.killSwitchActive() || stopInput)
		{
			removeTimers();
			return;
		}
		if (local == null) return;

		animationKillTimer.checkIfShouldReset();
		interactionKillTimer.checkIfShouldReset();
		movementKillTimer.checkIfShouldReset();

		if (stationaryKillTrigger.shouldKillInput(client)) stopInput = true;
//		for (KillTimer killTimer : killTimers) {
//			if (killTimer.shouldKillInput()) stopInput = true;
//		}
		if (lastNumInfoBoxes > 0 && infoBoxManager.getInfoBoxes().size() < lastNumInfoBoxes) {
			System.out.println("Kill timer activated!");
			stopInput = true;
		}
		lastNumInfoBoxes = infoBoxManager.getInfoBoxes().size();
	}

	private void removeTimers()
	{
		killTimers.forEach(KillTimer::removeTimer);
		infoBoxManager.removeIf(i->true);
	}

	private void initTimers() {
		animationKillTimer = new AnimationKillTimer(client, this, infoBoxManager,
				config.checkAnimation(), config.checkAnimationTime(), itemManager.getImage(ItemID.ASYN_REMAINS));
		experienceKillTimer = new ExperienceKillTimer(client, this,infoBoxManager,
				config.checkExperience(), config.checkExperienceTime(), itemManager.getImage(ItemID.LAMP_OF_KNOWLEDGE));
		interactionKillTimer = new InteractionKillTimer(client, this,infoBoxManager,
				config.checkInteraction(), config.checkInteractionTime(), itemManager.getImage(ItemID.CHRISTMAS_CRACKER));
		movementKillTimer = new MovementKillTimer(client, this,infoBoxManager,
				config.checkMovement(), config.checkMovementTime(), itemManager.getImage(ItemID.FIYR_REMAINS));

		runtimeKillTimer = new RuntimeKillTimer(client, this, infoBoxManager, config.checkMaxRun(), config.checkMaxRuntime(), itemManager.getImage(ItemID.WATCH));

		killTimers = Arrays.asList(
				animationKillTimer,
				experienceKillTimer,
				interactionKillTimer,
				movementKillTimer,
				runtimeKillTimer
		);

	}

	private void initTriggers() {
		messageKillTrigger = new MessageKillTrigger(config.filteredWords(), config.filteredRegex());
		stationaryKillTrigger = new StationaryKillTrigger(config.checkStationary());

		noItemInSlotKillTriggers = Arrays.asList(
				new NoItemInSlotKillTrigger(config.checkItemSlots(), this, client, infoBoxManager,itemManager,config.checkEmptySlot()),
				new NoItemInSlotKillTrigger(config.checkItemSlots(), this, client, infoBoxManager,itemManager,config.checkEmptySlot1()),
				new NoItemInSlotKillTrigger(config.checkItemSlots(), this, client, infoBoxManager,itemManager,config.checkEmptySlot2())
		);
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (messageKillTrigger.shouldKillInput(event)) stopInput = true;
	}

//	@Subscribe
//	public void onMenuOptionClicked(MenuOptionClicked event)
//	{
//		String option = event.getMenuOption();
//		String target = event.getMenuTarget();
//
//		System.out.println("Option " + option + " " + target);
//
//		int itemId = event.getId();
//
//		if (itemId == -1)
//		{
//			return;
//		}
//
//		ItemComposition itemComposition = client.getItemDefinition(itemId);
//	}

//	@Subscribe
//	public void onMenuEntryAdded(MenuEntryAdded menuEntryAdded)
//	{
//		if (menuEntryAdded.getOption().startsWith("Release") && !client.getWidget(WidgetInfo.BANK_CONTAINER).isHidden()) {
//			System.out.println("No more items in bank");
//			stopInput = true;
//		}
//	}

//	@Subscribe
//	public void onMenuOptionClicked(MenuOptionClicked event)
//	{
//
//		if (!config.checkEmptyBank()) return;
//
//		int itemId = event.getId();
//
//		if (itemId == -1)
//		{
//			return;
//		}
//
//		String option = event.getMenuOption();
//		String target = event.getMenuTarget();
//		ItemComposition itemComposition = client.getItemDefinition(itemId);
//
//		if (option.equals("Release") && !client.getWidget(WidgetInfo.BANK_CONTAINER).isHidden())
//		{
//			System.out.println("No more items in bank " + itemComposition.getName());
//			stopInput = true;
//		}
//
//	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		for (NoItemInSlotKillTrigger itemInSlotKillTrigger : noItemInSlotKillTriggers) {
			if (itemInSlotKillTrigger.shouldKillInput(event)) stopInput = true;
		}
	}


	@Subscribe
	public void onMenuOpened(MenuOpened event)
	{
		if (!config.checkEmptyBank()) return;

		MenuEntry firstEntry = event.getFirstEntry();
		if (firstEntry == null)
		{
			return;
		}
		int widgetId = firstEntry.getParam1();
		int itemId = firstEntry.getIdentifier();
		if (itemId == -1)
		{
			return;
		}

		ItemComposition itemComposition = client.getItemDefinition(itemId);

		if (firstEntry.getOption().equals("Release") && !client.getWidget(WidgetInfo.BANK_CONTAINER).isHidden())
		{
			System.out.println("No more items in bank " + itemComposition.getName());
			executorService.execute(()-> {

				int sleep = 5000 + new Random().nextInt(5000);
				System.out.println("Killing after " + sleep);
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (config.checkEmptyBank() && config.killSwitchActive())
				stopInput = true;
			});
		}
	}
}
