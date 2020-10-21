package net.runelite.client.plugins.miscplugins.killswitch.killtriggers;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.miscplugins.killswitch.KillTrigger;

import java.time.Instant;

import static net.runelite.api.AnimationID.IDLE;

public class StationaryKillTrigger extends KillTrigger {
    public StationaryKillTrigger(boolean shouldCheck) {
        super(shouldCheck);
    }

    private WorldPoint worldPoint;

    public boolean shouldKillInput(Client client) {
        if (!shouldCheck) return false;
        Player local = client.getLocalPlayer();
        if (local != null && local.getWorldLocation() != null) {
            if (worldPoint == null) worldPoint = local.getWorldLocation();

            boolean shouldKill = !local.getWorldLocation().equals(worldPoint);
            if (shouldKill) {
                shouldCheck = false;
                System.out.println(this.getClass().getSimpleName() + " kill triggered!");
            }
            return shouldKill;
        }
        return false;
    }
}
