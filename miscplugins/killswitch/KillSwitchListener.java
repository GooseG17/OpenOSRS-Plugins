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

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.MouseAdapter;

import javax.inject.Inject;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

@Slf4j
class KillSwitchListener extends MouseAdapter implements KeyListener
{
	@Inject
	private KillSwitchPlugin plugin;

	@Inject
	private KillSwitchConfig config;

	@Inject
	private Client client;

	@Override
	public void keyTyped(KeyEvent e)
	{
		disableInput(e);
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		disableInput(e);
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		disableInput(e);
	}

	@Override
	public MouseEvent mouseClicked(MouseEvent e)
	{
		disableInput(e);
		return e;
	}

	@Override
	public MouseEvent mousePressed(MouseEvent e)
	{
		disableInput(e);
		return e;
	}

	@Override
	public MouseEvent mouseReleased(MouseEvent e)
	{
		disableInput(e);
		return e;
	}

	@Override
	public MouseEvent mouseEntered(MouseEvent e)
	{
		if (config.pauseOnExit()) {
//			log.info("Resuming timers");
			for (KillTimer killTimer : plugin.getKillTimers()) {
				killTimer.setPaused(false);
			}
		}
		disableInput(e);
		return e;
	}

	@Override
	public MouseEvent mouseExited(MouseEvent e)
	{
		if (config.pauseOnExit()) {
//			log.info("Pausing timers");
			for (KillTimer killTimer : plugin.getKillTimers()) {
				killTimer.setPaused(true);
			}
		}
		disableInput(e);
		return e;
	}

	@Override
	public MouseEvent mouseDragged(MouseEvent e)
	{
		disableInput(e);
		return e;
	}

	@Override
	public MouseEvent mouseMoved(MouseEvent e)
	{
		disableInput(e);
		return e;
	}
	
	private void disableInput(InputEvent event) {
		if (client.getGameState() == GameState.LOGIN_SCREEN)
		{
			return;
		}
		if (!config.killSwitchActive())
		{
			return;
		}
		if (!plugin.isStopInput())
		{
			return;
		}
		event.consume();
	}
}
