/*
 * Copyright (c) 2019, Adam <Adam@sigterm.info>
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

import lombok.Getter;
import lombok.Setter;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;

final class ResettableTimer extends InfoBox
{
	private static final Duration FERMENT_TIME = Duration.ofMillis(13_800);

	private final long maxSeconds;
	@Getter
	private boolean paused;
	private long pausedSeconds;
	private Instant timeWhenEnds;

	ResettableTimer(BufferedImage image, Plugin plugin, long seconds)
	{
		super(image, plugin);
		this.maxSeconds = seconds;
		reset();
	}


	@Override
	public String getText()
	{
		if (paused) {
			reset(pausedSeconds);
		}

		int seconds = secondsLeft();

		int minutes = (seconds % 3600) / 60;
		int secs = seconds % 60;

		return String.format("%d:%02d", minutes, secs);
	}

	@Override
	public Color getTextColor()
	{
		int seconds = secondsLeft();
		return seconds <= 10 ? Color.RED : Color.WHITE;
	}

	@Override
	public boolean cull()
	{
		int seconds = secondsLeft();
		return seconds <= 0;
	}

	void reset()
	{
		pausedSeconds = maxSeconds;
		timeWhenEnds = Instant.now().plus(Duration.ofSeconds(maxSeconds));
	}

	void reset(long seconds)
	{
		timeWhenEnds = Instant.now().plus(Duration.ofSeconds(seconds));
	}

	public int secondsLeft()
	{
		return (int) Duration.between(Instant.now(), timeWhenEnds).getSeconds();
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
		pausedSeconds = secondsLeft();
	}
}
