package de.evilcodez.classicserver.utils;

public class Timer {

    public int elapsedTicks;
    public float renderPartialTicks;
    public float partialTicks;
    private long lastSyncSysClock;
    public float timerSpeed;

    public Timer(float tps) {
        this.timerSpeed = 1000.0F / tps;
        this.lastSyncSysClock = System.currentTimeMillis();
    }

    public void updateTimer() {
        long i = System.currentTimeMillis();
        this.partialTicks = (float) (i - this.lastSyncSysClock) / this.timerSpeed;
        this.lastSyncSysClock = i;
        this.renderPartialTicks += this.partialTicks;
        this.elapsedTicks = (int) this.renderPartialTicks;
        this.renderPartialTicks -= (float) this.elapsedTicks;
    }
}
