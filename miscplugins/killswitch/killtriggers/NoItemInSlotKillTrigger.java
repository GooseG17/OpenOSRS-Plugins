package net.runelite.client.plugins.miscplugins.killswitch.killtriggers;

import net.runelite.api.*;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.miscplugins.killswitch.KillTrigger;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

public class NoItemInSlotKillTrigger extends KillTrigger {
    private final Plugin plugin;
    private final Client client;
    private final InfoBoxManager infoBoxManager;
    private final ItemManager itemManager;
    private final int slot;
    public NoItemInSlotKillTrigger(boolean shouldCheck, Plugin plugin, Client client, InfoBoxManager infoBoxManager, ItemManager itemManager, int slot) {
        super(shouldCheck);
        this.plugin = plugin;
        this.client = client;
        this.infoBoxManager = infoBoxManager;
        this.itemManager = itemManager;
        this.slot = slot;
    }

    public boolean shouldKillInput(ItemContainerChanged event) {
        if (!shouldCheck) return false;
        if (slot < 0 || slot > 27) return false;
        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);

        if (inventory == null) return false;

        Item item = inventory.getItem(slot);
        boolean shouldKill = item == null;
        if (shouldKill) {
            shouldCheck = false;
            System.out.println(this.getClass().getSimpleName() + " kill triggered!");
        } else {
            updateCounter(item);
        }
        return shouldKill;
    }

    private Counter counter;
    private void updateCounter(Item item) {
        if (counter != null) {
            counter.setCount(item.getQuantity());
            return;
        }
        counter = new Counter(itemManager.getImage(item.getId()), plugin, item.getQuantity());
        infoBoxManager.addInfoBox(counter);
    }
}
