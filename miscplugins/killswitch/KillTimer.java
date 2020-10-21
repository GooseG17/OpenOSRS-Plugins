package net.runelite.client.plugins.miscplugins.killswitch;

import net.runelite.api.Client;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.ui.overlay.infobox.Timer;
import net.runelite.client.util.AsyncBufferedImage;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public abstract class KillTimer {
    protected Client client;
    private InfoBoxManager infoBoxManager;
    protected boolean shouldCheck;
    protected final ResettableTimer timer;
    protected int seconds;

    public KillTimer(Client client, Plugin plugin, InfoBoxManager infoBoxManager, boolean shouldCheck, int seconds, AsyncBufferedImage image) {
        this.client = client;
        this.infoBoxManager = infoBoxManager;
        this.shouldCheck = shouldCheck;
        if (seconds <= 0) seconds = 1;
        this.seconds = seconds;
        this.timer = new ResettableTimer(image,plugin,seconds);
        if (shouldCheck) infoBoxManager.addInfoBox(timer);
    }

    public abstract void checkIfShouldReset();

    protected void resetTimer() {
        if (!shouldCheck) return;
        timer.reset();
//        if (!infoBoxManager.getInfoBoxes().contains(timer)) infoBoxManager.addInfoBox(timer);
    }


    public void removeTimer() {
        infoBoxManager.removeInfoBox(timer);
    }

//    public boolean shouldKillInput() {
//        if (!shouldCheck) return false;
//        if (paused) {
//            timer.reset(Math.max(1,this.pausedDuration));
//            return false;
//        }
//        if (timer.cull()) {
//            shouldCheck = false;
//            System.out.println(this.getClass().getSimpleName() + " kill triggered!");
//            return true;
//        }
//        return false;
//    }



//    private long pausedDuration;
    public void setPaused(boolean paused) {
        timer.setPaused(paused);
//        this.paused = paused;
//        if (paused) {
//            this.pausedDuration = timer.secondsLeft();
//        }
    }
}
