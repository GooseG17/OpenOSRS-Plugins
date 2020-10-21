/*
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
  */
package net.runelite.client.plugins.fightcave;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class FightCaveImageManager
{
	private static final BufferedImage[] prayerBufferedImages = new BufferedImage[4];

	public static BufferedImage getProtectionPrayerBufferedImage(FightCaveContainer.AttackStyle prayer)
	{
		switch (prayer)
		{
			case MAGE:
				if (prayerBufferedImages[0] == null)
				{
					prayerBufferedImages[0] = getBufferedImage("/prayers/protect_from_magic.png");
				}
				return prayerBufferedImages[0];
			case RANGE:
				if (prayerBufferedImages[1] == null)
				{
					prayerBufferedImages[1] = getBufferedImage("/prayers/protect_from_missiles.png");
				}
				return prayerBufferedImages[1];
			case MELEE:
				if (prayerBufferedImages[2] == null)
				{
					prayerBufferedImages[2] = getBufferedImage("/prayers/protect_from_melee.png");
				}
				return prayerBufferedImages[2];
		}

		if (prayerBufferedImages[3] == null)
		{
			prayerBufferedImages[3] = getBufferedImage("/prayers/prayer_off.png");
		}
		return prayerBufferedImages[3];
//		return null;
	}

	private static BufferedImage getBufferedImage(String path)
	{
		BufferedImage image = null;
		try
		{
			InputStream in = FightCavePlugin.class.getResourceAsStream(path);
			image = ImageIO.read(in);
		}
		catch (IOException e)
		{
			log.info("Error loading image {}", e);
		}
		return image;
	}
}
