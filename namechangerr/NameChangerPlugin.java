/*
 * Copyright (c) 2018, DennisDeV <https://github.com/DevDennis>
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
package net.runelite.client.plugins.namechangerr;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ScriptID;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

import javax.inject.Inject;

@PluginDescriptor(
	name = "Name Changer",
	description = "I am the Mod now",
	tags = {"nomscripts"},
	enabledByDefault = false
)
public class NameChangerPlugin extends Plugin
{
	static final String CONFIG_GROUP = "namechange";

	@Inject
	private Client client;

	@Inject
	private NameChangerConfig config;

	@Inject
	private ClientThread clientThread;

	@Provides
	NameChangerConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NameChangerConfig.class);
	}

	@Override
	protected void startUp()
	{

	}

	@Override
	protected void shutDown()
	{
		clientThread.invokeLater(() -> client.runScript(ScriptID.CHAT_PROMPT_INIT));
	}

	@Subscribe
	private void onBeforeRender(BeforeRender event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (!config.nameChange().isEmpty())
		{
			updateChatbox();
		}
	}

	private void updateChatbox()
	{
		Widget chatboxTypedText = client.getWidget(WidgetInfo.CHATBOX_INPUT);
		if (chatboxTypedText == null || chatboxTypedText.isHidden())
		{
			return;
		}
		String[] chatbox = chatboxTypedText.getText().split(":", 2);

		chatbox[0] = config.icon().toString() + config.nameChange();

		chatboxTypedText.setText(chatbox[0] + ":" + chatbox[1]);
	}
}
