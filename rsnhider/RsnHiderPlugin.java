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
package net.runelite.client.plugins.rsnhider;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ScriptID;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

import java.util.Random;

/*
Mental breakdown 2: electric boogaloo

Alexa, play sea shanty two.
Peace to:
	r189, he.cc
*/
@PluginDescriptor(
	name = "RSN Hider",
	description = "Hides your rsn for streamers.",
	tags = {"twitch, nomscripts"},
	enabledByDefault = false
)
public class RsnHiderPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private RsnHiderConfig config;

	private static String fakeRsn;

	private static final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	@Provides
	private RsnHiderConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RsnHiderConfig.class);
	}

	@Override
	public void startUp()
	{
		fakeRsn = randomAlphaNumeric(12);
	}

	@Override
	public void shutDown()
	{
		clientThread.invokeLater(() -> client.runScript(ScriptID.CHAT_PROMPT_INIT));
	}

	private int counter = 0;
	private int modNumber = 10;
	@Subscribe
	private void onGameTick(GameTick event)
	{
		counter++;
		if (counter % modNumber == 0) {
			fakeRsn = randomAlphaNumeric(12);
			updateChatbox(true);
			modNumber = new Random().nextInt(60) + 300;
		}
	}

	@Subscribe
	private void onBeforeRender(BeforeRender event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (config.hideWidgets())
		{
			// do every widget
			for (Widget widgetRoot : client.getWidgetRoots())
			{
				processWidget(widgetRoot);
			}
		}
		else
		{
			// just do the chatbox
			updateChatbox(false);
		}
	}

	/**
	 * Recursively traverses widgets looking for text containing the players name, replacing it if necessary
	 * @param widget The root widget to process
	 */
	private void processWidget(Widget widget)
	{
		if (widget == null)
		{
			return;
		}

		if (widget.getText() != null)
		{
			widget.setText(replaceRsn(widget.getText()));
		}

		for (Widget child : widget.getStaticChildren())
		{
			processWidget(child);
		}

		for (Widget dynamicChild : widget.getDynamicChildren())
		{
			processWidget(dynamicChild);
		}

		for (Widget nestedChild : widget.getNestedChildren())
		{
			processWidget(nestedChild);
		}
	}

	private void updateChatbox(boolean force)
	{
		Widget chatboxTypedText = client.getWidget(WidgetInfo.CHATBOX_INPUT);
		if (chatboxTypedText == null || chatboxTypedText.isHidden())
		{
			return;
		}
		String[] chatbox = chatboxTypedText.getText().split(":", 2);

		//noinspection ConstantConditions
		String playerRsn = Text.toJagexName(client.getLocalPlayer().getName());
		if (force)
		{
			chatbox[0] = fakeRsn;
		}
		else if (Text.standardize(chatbox[0]).contains(Text.standardize(playerRsn)))
		{
			chatbox[0] = fakeRsn;
		}

		chatboxTypedText.setText(chatbox[0] + ":" + chatbox[1]);
	}

	@Subscribe
	private void onChatMessage(ChatMessage event)
	{
		//noinspection ConstantConditions
		if (client.getLocalPlayer().getName() == null)
		{
			return;
		}

		String replaced = replaceRsn(event.getMessage());
		event.setMessage(replaced);
		event.getMessageNode().setValue(replaced);

		if (event.getName() == null)
		{
			return;
		}

		boolean isLocalPlayer =
			Text.standardize(event.getName()).equalsIgnoreCase(Text.standardize(client.getLocalPlayer().getName()));

		if (isLocalPlayer)
		{
			event.setName(fakeRsn);
			event.getMessageNode().setName(fakeRsn);
		}
	}

	@Subscribe
	private void onOverheadTextChanged(OverheadTextChanged event)
	{
		event.getActor().setOverheadText(replaceRsn(event.getOverheadText()));
	}

	private String replaceRsn(String textIn)
	{
		//noinspection ConstantConditions
		String playerRsn = Text.toJagexName(client.getLocalPlayer().getName());
		String standardized = Text.standardize(playerRsn);
		while (Text.standardize(textIn).contains(standardized))
		{
			int idx = textIn.replace("\u00A0", " ").toLowerCase().indexOf(playerRsn.toLowerCase());
			int length = playerRsn.length();
			String partOne = textIn.substring(0, idx);
			String partTwo = textIn.substring(idx + length);
			textIn = partOne + fakeRsn + partTwo;
		}
		return textIn;
	}

	private static String randomAlphaNumeric(int count)
	{
		return names[new Random().nextInt(names.length)];
//		StringBuilder builder = new StringBuilder();
//		int i = count;
//		while (i-- != 0)
//		{
//			int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
//			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
//		}
//		return builder.toString();
	}

	private static final String[] names = {"Cliche","Ezekiel","Wary","Lorenzo","Leela","Addition","Pellet","Elder Maul","Constrain","Enrique","Stablecoin","Joann","Altruism","Primarina","Roofies","Cudgel","Caltech","Concur","Squander","Harrisburg","Pasture","Annette","Ballot","Consist","Inhabit","Rochester","Mondai","Skwovet","Although","Stroller","Appraise","Retainer","Participle","Canister","Vacuole","Sieve","Bicker","Percussion","Negation","Crustacean","Proportion","Graig","Propeller","Caracal","Mausoleum","Tapestry","Derride","Pleasanton","Refinery","TPA","Marinate","Impulsion","Brokerage","Integrand","Settlement","Regimen","Ledgers","Mitigates","Penuche","Computate","Barbecued","Derivant","Corkscrew","Annihilus","Supremal","Carlyn","Simplest","Divisible","Innovatory","GCB","Incumber","Columbium","Octahedron","Insulant","US News","Corrodes","Vision Fund","Course Hero","Cosmoid","UC San Diego","Fibrosis","Stopple","Aiichiro","Altorese","Amatsuyu","Amayori","Asougi","Bertolt","Celseus","Chamov","Chizome","Dagrule","Dankichi","Echizen","Edgeshot","Ell","Ethnobalt","Enta","Furuachi","Gapri","Ginoza","Hamsterviel","Kadomon","Kazaream","Ketsuryugan","Kanamori","Kolar","Kumanaku","Kurogiri","Kihachi","Larcade","Mashirao","Mutsumi","Naruzou","Noboyuki","Nochizawa","Otoribashi","Piodon","Saituo","Sasakibe","Senkuu","Shario","Shestal","Shukuro","Sozosuke","Tahomaru","Tetsutarou","Togane","Toujou","Ullmina","Sakonji","Shumerman","Yahaba","Ryuuguuin","Yylfordt","Zereschrute","Zorome","Adenela","Akanegakubo","Almaflora","Amaha","Animusphere","Archisorte","Ariadoa","Aristella","Ayazato","Billaba","Chaos Makina","Callistis","Dimaria","Elumeshia","Elshia","Fenette","Filvis","Floren","Fredercia","Fukiyose","Gigako","Ginshuu","Hatsushima","Harribel","Hoshiyama","Hotogi","Ichigyo","Indura","Inomori","Ikuno","Kagamihara","Kainasu","Kashiwabara","Kominami","Kuniko","Makinohara","Masuyo","Nanakura","Marnya","Manbagi","Matsuko","Nayotake","Nefeltari","Nerimaki","Nonoka","Olgamally","Oosuki","Oryou","Raph-Chan","Ratotille","Rethusa","Rinemu","Sanbica","Sasahara","Sanjouno","Sekijou","Seresdina","Shimako","Soryuuin","Suzumeno","Shtolienen","Szeski","Tsunehisa","Ushikai","Uror","Wakikaoru","Yaoyoi","Yurizaki","Yofiilis","Yonsa","Adeltrud","Oryou","Hobren","Scheta","Nergius","Entokia","Sulinea","Almera","Nellisen","Viksul","Fu Za","Kuberi","Salacha","Ivenda","Johngalli","Mikitaka","Anjuro","Tubach","Gallida","Tinasha","Omar Agah","Pentra Venj","Imogen Rife","Shuro Chi","Aoyama Yuga","Hakuto Kunai","Kido Eita","Kufa Vampir","Licht Bach","Lida Tenya","LArc Berg","Nagi Souma","Natsuo Fujii","Riku Doura","Rurou Arthur","Satan Jacob","Sato Rikido","Shoji Mezo","Taiju Oki","Watari Ryota","Waver Velvet","Ahagon Umiko","Alicia Rue","Amane Misa","Amano Hina","Chloe Rollo","CZ","Dola Schwi","Elka Veresau","Honda Koharu","Ichigyo Ruri","Iki Hiyori","Izawa Shizue","Kanna Hatano","Katana Hero","Kojima Kanna","Koneko Tojo","Lecia Ivyred","Luna Elegant","Maki Oze","Mari Tamaki","Mao Nanjo","Matoi Ryuko","Miko Lino","Misaki Ogura","Nakiri Arisu","Nia Teppelin","Nui No Kata","Otori Koharu","Onna Kishi","Rin Azuma","Rizu Ogata","Saya Endou","Sayaka Midou","Sanshokuin","Sakie Satou","Shia Haulia","Scarlet Ezra","Tomoe Koga","Tio Klarus","Yuka Sonobe","Yu Kirino","Yuuko Aioi","Yuri Carlton","Yurika Koga","Zuberg Alice","Byoukidere","Granbelm","Gun Gale","Hangyakusei","Ixtal","Kudere","Oreshura","Senyru Girl","Tenki no Ko","Utinal Ring","Yottadere"};
}
