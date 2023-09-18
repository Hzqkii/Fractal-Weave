package xyz.flapjack.fractal.modules.impl.util;

/* Open. */
import java.util.concurrent.ThreadLocalRandom;

public class Random {
    /**
     * Generates a random number nested (apparently this increases randomness).
     * @param min   the minimum value.
     * @param max   the maximum value.
     * @return      a random number.
     */
    public static double nextRandom(final int min, final int max) {
        double result = 0;

        for (int i = 0; i < simpleRandom(1, 500); i++) {
            result = Math.round(simpleRandom(min, max));
        }

        return result;
    }

    /**
     * Generates a random number nested (apparently this increases randomness).
     * @param min   the minimum value.
     * @param max   the maximum value.
     * @return      a random number.
     */
    public static double nextRandom(final double min, final double max) {
        double result = 0;

        for (int i = 0; i < simpleRandom(1, 500); i++) {
            result = (double) Math.round(simpleRandom(min, max) * 1000) / 1000;
        }

        return result;
    }

    public static double nextRandom(final float min, final float max) {
        float result = 0;

        for (int i = 0; i < simpleRandom(1, 500); i++) {
            result = (float) simpleRandom(min, max);
        }

        return result;
    }

    /**
     * Simple random number generator.
     * @param min   the minimum value.
     * @param max   the maximum value.
     * @return      a random number.
     */
    public static double simpleRandom(final int min, final int max) {
        int x = min;
        int y = max;

        if (min == max) {
            return min;
        } else if (min > max) {
            x = max;
            y = min;
        }

        return ThreadLocalRandom.current().nextDouble(x, y);
    }

    /**
     * Simple random number generator.
     * @param min   the minimum value.
     * @param max   the maximum value.
     * @return      a random number.
     */
    public static double simpleRandom(final float min, final float max) {
        float x = min;
        float y = max;

        if (min == max) {
            return min;
        } else if (min > max) {
            x = max;
            y = min;
        }

        return ThreadLocalRandom.current().nextFloat(x, y);
    }

    /**
     * Simple random number generator.
     * @param min   the minimum value.
     * @param max   the maximum value.
     * @return      a random number.
     */
    public static double simpleRandom(final double min, final double max) {
        double x = min;
        double y = max;

        if (min == max) {
            return min;
        } else if (min > max) {
            x = max;
            y = min;
        }

        return ThreadLocalRandom.current().nextDouble(x, y);
    }
}
