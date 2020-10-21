package net.runelite.client.plugins.miscplugins.killswitch.killtimers;

import net.runelite.api.Client;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.miscplugins.killswitch.KillTimer;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.AsyncBufferedImage;

public class ExperienceKillTimer extends KillTimer {

    public ExperienceKillTimer(Client client, Plugin plugin, InfoBoxManager infoBoxManager, boolean shouldCheck, int seconds, AsyncBufferedImage image) {
        super(client, plugin, infoBoxManager, shouldCheck, seconds, image);
    }

    public void checkIfShouldReset() {
        if (!shouldCheck) return;
        resetTimer();
    }
}
