package net.runelite.client.plugins.miscplugins.killswitch.killtimers;

import net.runelite.api.Client;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.miscplugins.killswitch.KillTimer;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.AsyncBufferedImage;

public class RuntimeKillTimer extends KillTimer {

    public RuntimeKillTimer(Client client, Plugin plugin, InfoBoxManager infoBoxManager, boolean shouldCheck, int seconds, AsyncBufferedImage image) {
        super(client, plugin, infoBoxManager, shouldCheck, seconds, image);
        resetTimer();
    }

    // This should never be called
    @Override
    public void checkIfShouldReset() {

    }
}
