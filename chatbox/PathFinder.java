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
package net.runelite.client.plugins.chatbox;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.chatbox.dax_path.WebWalkerServerApi;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

@PluginDescriptor(
	name = "Path Finder",
	description = "Path Finder",
	tags = {"highlight", "web", "walker", "tagging", "mark", "tags","nomscripts"},
	enabledByDefault = false
)
public class PathFinder extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	@Inject
	@Getter
	private PathFinderConfig config;

	@Inject
	private PathFinderOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private WebWalkerServerApi api;

	@Getter
	@Inject
	private ScheduledExecutorService executorService;

	@Provides
	PathFinderConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PathFinderConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		updatePath();
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}

	@Getter
	private AtomicReference<WorldPoint[]> pathReference = new AtomicReference<>(null);

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("pathfinder") && event.getKey().contains(PathFinderConfig.UPDATE))
		{
			if (config.refreshDelaySeconds() != 0) return;
			if (setFixedPath()) return;
			if (timerUp()) updatePath();
		}
	}

	private Instant lastUpdate = Instant.now();
	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		if (config.refreshDelaySeconds() > 0 && timerUp()) {
			updatePath();
		}
	}

	private void updatePath() {
		if (Strings.isNullOrEmpty(config.endPosition())) return;
		lastUpdate = Instant.now();
		executorService.execute(()-> {
			if (Strings.isNullOrEmpty(config.startPosition()))
				pathReference.set(api.getPath(parsePoint(config.endPosition())));
			else pathReference.set(api.getPath(parsePoint(config.startPosition()), parsePoint(config.endPosition())));
		});
	}

	private WorldPoint parsePoint(String csv) {
		List<String> numbers = Text.fromCSV(csv);
		if (numbers.size() != 3) return null;
		int x = Optional.ofNullable(numbers.get(0)).map(Ints::tryParse).orElse(0);
		int y = Optional.ofNullable(numbers.get(1)).map(Ints::tryParse).orElse(0);
		int z = Optional.ofNullable(numbers.get(2)).map(Ints::tryParse).orElse(0);
		return new WorldPoint(x,y,z);
	}

	private boolean setFixedPath() {
		if (Strings.isNullOrEmpty(config.fixedPath())) return false;

		List<String> numbers = Text.fromCSV(config.fixedPath());
		if (numbers.size() % 3 != 0) return false;
		List<WorldPoint> worldPoints = new ArrayList<>();
		for (int i = 0; i < numbers.size(); i+=3) {
			int x = Optional.ofNullable(numbers.get(i)).map(Ints::tryParse).orElse(0);
			int y = Optional.ofNullable(numbers.get(i+1)).map(Ints::tryParse).orElse(0);
			int z = Optional.ofNullable(numbers.get(i+2)).map(Ints::tryParse).orElse(0);
			worldPoints.add(new WorldPoint(x,y,z));
		}
		pathReference.set(worldPoints.toArray(new WorldPoint[0]));
		return true;
	}
	private boolean timerUp() {
		return lastUpdate.plusSeconds(config.refreshDelaySeconds()+5).isBefore(Instant.now());
	}
}
