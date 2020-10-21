/*
 * Copyright (c) 2018, Kamiel, <https://github.com/Kamielvf>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
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
package net.runelite.client.plugins.screenmarkers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenMarker
{
	private long id;
	private String name;
	private int borderThickness;
	private Color color;
	private Color fill;
	private boolean visible;
	private boolean checkAnimation;
	private boolean checkMovement;
	private boolean checkInteraction;
	private boolean checkBank;
	private boolean checkCraft;
	private boolean checkStam;

	private boolean checkChatOptions;
	private boolean checkContinue;
	private boolean checkLevel;
	private int minHealth;
	private int maxHealth;
	private int minPrayer;
	private int maxPrayer;
	private int minInventory;
	private int maxInventory;
	private int minEnergy;
	private int maxEnergy;

	public ScreenMarker(long id, String name, int borderThickness, Color color, Color fill, boolean visible) {
		this.id = id;
		this.name = name;
		this.borderThickness = borderThickness;
		this.color = color;
		this.fill = fill;
		this.visible = visible;
		this.minHealth = 0;
		this.maxHealth = 200;
		this.minPrayer = 0;
		this.maxPrayer = 200;
		this.minInventory = 0;
		this.maxInventory = 28;
		this.minEnergy = 0;
		this.maxEnergy = 100;
	}
}
