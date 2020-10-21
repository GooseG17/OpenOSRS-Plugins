/*
 * Copyright (c) 2018, Woox <https://github.com/wooxsolo>
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
package net.runelite.client.plugins.demonicgorilla;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.zulrah.ImagePanelComponent;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.http.api.item.ItemEquipmentStats;
import net.runelite.http.api.item.ItemStats;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class DemonicGorillaWeaponOverlay extends Overlay
{

    private Client client;
    private DemonicGorillaPlugin plugin;

    @Inject
    private ItemManager itemManager;

    @Inject
    public DemonicGorillaWeaponOverlay(Client client, DemonicGorillaPlugin plugin)
    {
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.plugin = plugin;
    }

    private BufferedImage getWeaponImage(HeadIcon headIcon)
    {
        Item bestItem = null;
        int highestAttackBonus = 0;
        ItemContainer container = client.getItemContainer(InventoryID.INVENTORY);
        ItemContainer container2 = client.getItemContainer(InventoryID.EQUIPMENT);
        if (container == null || container2 == null) return null;
        List<Item> allItems = new ArrayList<>();
        Collections.addAll(allItems, container.getItems());
        Collections.addAll(allItems, container2.getItems());
        for (Item item : allItems) {
            final ItemStats stats = itemManager.getItemStats(item.getId(), false);
            if (stats == null) continue;
            ItemEquipmentStats e = stats.getEquipment();
            if (e == null) continue;
            ItemComposition composition = itemManager.getItemComposition(item.getId());
            if (!Arrays.asList(composition.getInventoryActions()).contains("Wield")) continue;
            int currBonus = 0;
            switch (headIcon)
            {
                case MAGIC:
                    currBonus = IntStream.of(e.getAstab(), e.getAslash(), e.getAcrush(), e.getArange()).max().orElse(-1);
                    if (composition.getName().contains("rclight") || composition.getName().contains("defender"))
                    {
                        currBonus = 200;
                    }
                    break;
                case RANGED:
                    currBonus = IntStream.of(e.getAstab(), e.getAslash(), e.getAcrush(), e.getAmagic()).max().orElse(-1);
                    if (composition.getName().contains("rclight") || composition.getName().contains("defender"))
                    {
                        currBonus = 200;
                    }
                    break;
                case MELEE:
                    currBonus = IntStream.of(e.getAmagic(), e.getArange()).max().orElse(-1);
                    break;
            }
            if (currBonus > highestAttackBonus)
            {
                highestAttackBonus = currBonus;
                bestItem = item;
            }
        }
        if (bestItem == null || Arrays.asList(container2.getItems()).contains(bestItem)) return null;
        return itemManager.getImage(bestItem.getId());
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Player player = client.getLocalPlayer();
        if (player == null) return null;
        for (DemonicGorilla gorilla : plugin.getGorillas().values())
        {
            if (!player.equals(gorilla.getNpc().getInteracting()))
            {
                continue;
            }

            BufferedImage weaponImage = getWeaponImage(gorilla.getOverheadIcon());
            if (weaponImage == null) return null;
            ImagePanelComponent imagePanelComponent = new ImagePanelComponent();
            imagePanelComponent.setTitle("Switch!");
            imagePanelComponent.setImage(weaponImage);
            return imagePanelComponent.render(graphics);
        }

        return null;
    }
}