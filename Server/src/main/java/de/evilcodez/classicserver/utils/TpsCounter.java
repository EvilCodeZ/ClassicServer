package de.evilcodez.classicserver.utils;

import java.util.ArrayList;
import java.util.List;

public class TpsCounter {

    private final List<Float> history;
    private long timer;
    private int ticks;

    public TpsCounter() {
        this.history = new ArrayList<>();
    }

    public void update() {
        ++ticks;
        final long now = System.currentTimeMillis();
        if(now - timer >= 1000L) {
            timer = now;
            history.add((float) ticks);
            ticks = 0;
        }
    }

    public float getTPS() {
        return history.get(history.size() - 1);
    }

    public float getAverageTPS(int seconds) {
        double tps = 0.0D;
        seconds = Math.min(seconds, history.size());
        for (int i = 0; i < seconds; i++) {
            tps += (double) history.get(history.size() - 1 - i);
        }
        tps /= (double) seconds;
        return (float) tps;
    }
}
