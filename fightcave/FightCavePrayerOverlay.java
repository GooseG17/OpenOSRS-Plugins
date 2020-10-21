/*
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

import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;

@Singleton
public class FightCavePrayerOverlay extends Overlay
{
	private final FightCavePlugin plugin;
	private final Client client;

	@Inject
	FightCavePrayerOverlay(final Client client, final FightCavePlugin plugin)
	{
		this.client = client;
		this.plugin = plugin;
		setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
		setPriority(OverlayPriority.HIGHEST);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		for (FightCaveContainer npc : plugin.getFightCaveContainer())
		{
			if (npc.getNpc() == null)
			{
				continue;
			}

			final int ticksLeft = npc.getTicksUntilAttack();
			final FightCaveContainer.AttackStyle attackStyle = npc.getAttackStyle();

			if (ticksLeft <= 0)
			{
				continue;
			}

			if (npc.getNpcName().toLowerCase().contains("jad"))
			{
				BufferedImage pray = FightCaveImageManager.getProtectionPrayerBufferedImage(attackStyle);

				Prayer prayer = null;
				switch (npc.getAttackStyle())
				{
					case MAGE:
						prayer = Prayer.PROTECT_FROM_MAGIC;
						break;
					case RANGE:
						prayer = Prayer.PROTECT_FROM_MISSILES;
						break;
					case MELEE:
						prayer = Prayer.PROTECT_FROM_MELEE;
						break;
				}
				if (prayer != null && !client.isPrayerActive(prayer))
				{
					ImagePanelComponent imagePanelComponent = new ImagePanelComponent();
					imagePanelComponent.setTitle("Switch!");

					if (pray != null)
					{
						imagePanelComponent.setImage(pray);
					}
					else
					{
						imagePanelComponent.setImage(new BufferedImage(1,1,BufferedImage.TYPE_3BYTE_BGR));
					}
					return imagePanelComponent.render(graphics);
				}
			}
		}
		return null;
	}
}
