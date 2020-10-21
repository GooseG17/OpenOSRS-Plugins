/*Nomnom
 * Copyright (c) 2017, Devin French <https://github.com/devinfrench>
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
 nomnom */
package net.runelite.client.plugins.zulrah.overlays;

import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.client.plugins.zulrah.ImagePanelComponent;
import net.runelite.client.plugins.zulrah.ZulrahInstance;
import net.runelite.client.plugins.zulrah.ZulrahPlugin;
import net.runelite.client.plugins.zulrah.phase.ZulrahPhase;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ZulrahPrayerOverlay extends Overlay
{
	private final Client client;
	private final ZulrahPlugin plugin;

	@Inject
	ZulrahPrayerOverlay(@Nullable Client client, ZulrahPlugin plugin)
	{
		super(plugin);
		setPosition(OverlayPosition.BOTTOM_LEFT);
		setPriority(OverlayPriority.MED);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.client = client;
		this.plugin = plugin;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		ZulrahInstance instance = plugin.getInstance();

		if (instance == null)
		{
			return null;
		}

		ZulrahPhase currentPhase = instance.getPhase();
		if (currentPhase == null)
		{
			return null;
		}

		Prayer prayer = currentPhase.isJad() ? null : currentPhase.getPrayer();
		if (prayer == null)
		{
			BufferedImage prayerImage = ZulrahImageManager.getProtectionPrayerBufferedImage(Prayer.THICK_SKIN);
			ImagePanelComponent imagePanelComponent = new ImagePanelComponent();
			imagePanelComponent.setTitle("Off!");
			imagePanelComponent.setImage(prayerImage);
			return imagePanelComponent.render(graphics);
		}
		if (client.isPrayerActive(prayer))
		{
			return null;
		}

		BufferedImage prayerImage = ZulrahImageManager.getProtectionPrayerBufferedImage(prayer);
		ImagePanelComponent imagePanelComponent = new ImagePanelComponent();
		imagePanelComponent.setTitle("Switch!");
		imagePanelComponent.setImage(prayerImage);
		return imagePanelComponent.render(graphics);
	}
}
