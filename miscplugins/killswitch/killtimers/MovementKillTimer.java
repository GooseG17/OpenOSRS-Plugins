package net.runelite.client.plugins.miscplugins.killswitch.killtimers;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.miscplugins.killswitch.KillTimer;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.AsyncBufferedImage;

public class MovementKillTimer extends KillTimer {

    private WorldPoint lastPosition;

    public MovementKillTimer(Client client, Plugin plugin, InfoBoxManager infoBoxManager, boolean shouldCheck, int seconds, AsyncBufferedImage image) {
        super(client, plugin, infoBoxManager, shouldCheck, seconds, image);
    }

    @Override
    public void checkIfShouldReset() {
        if (!shouldCheck) return;
        Player local = client.getLocalPlayer();
        if (local != null && local.getWorldLocation() != null) {
            if (!local.getWorldLocation().equals(lastPosition)) {
                lastPosition = local.getWorldLocation();
                resetTimer();
            }
        }
    }
}
