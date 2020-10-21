package net.runelite.client.plugins.miscplugins.killswitch.killtimers;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.miscplugins.killswitch.KillTimer;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.AsyncBufferedImage;

import static net.runelite.api.AnimationID.IDLE;

public class AnimationKillTimer extends KillTimer {

    public AnimationKillTimer(Client client, Plugin plugin, InfoBoxManager infoBoxManager, boolean shouldCheck, int seconds, AsyncBufferedImage image) {
        super(client, plugin, infoBoxManager, shouldCheck, seconds, image);
    }

    @Override
    public void checkIfShouldReset() {
        if (!shouldCheck) return;
        Player local = client.getLocalPlayer();
        if (local != null && local.getAnimation() != IDLE) {
            resetTimer();
        }
    }
}
