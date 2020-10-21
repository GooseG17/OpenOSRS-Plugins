package net.runelite.client.plugins.miscplugins.killswitch;

public abstract class KillTrigger {
    protected boolean shouldCheck;

    public KillTrigger(boolean shouldCheck) {
        this.shouldCheck = shouldCheck;
    }
}
