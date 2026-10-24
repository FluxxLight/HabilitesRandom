package com.habilitesrandom.client.ui;

/** Short, deterministic UI motion helpers. Screens own their clocks; no global ticker exists. */
public final class UiAnimation {
    public static final long OPEN_DURATION = 220L;
    public static final long CLOSE_DURATION = 130L;
    public static final long HOVER_DURATION = 140L;

    private UiAnimation() {
    }

    public static float progress(long startedAt, long duration) {
        if (duration <= 0) {
            return 1.0F;
        }
        long elapsed = System.currentTimeMillis() - startedAt;
        return clamp01(elapsed / (float) duration);
    }

    public static float easeOutCubic(float value) {
        float t = clamp01(value);
        float inverse = 1.0F - t;
        return 1.0F - inverse * inverse * inverse;
    }

    public static float easeInOut(float value) {
        float t = clamp01(value);
        return t < 0.5F ? 4.0F * t * t * t : 1.0F - (float) Math.pow(-2.0F * t + 2.0F, 3.0F) / 2.0F;
    }

    public static float pulse(long now, long periodMillis) {
        double phase = (now % Math.max(1L, periodMillis)) / (double) Math.max(1L, periodMillis);
        return (float) ((Math.sin(phase * Math.PI * 2.0) + 1.0) * 0.5);
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }
}
