package xyz.flapjack.fractal.events.impl;

/* Custom. */
import xyz.flapjack.fractal.events.FractalEvent;

public final class MouseEvent extends FractalEvent {
    public final Type type;

    public int rightClickDelay = 4;
    public int leftClickCounter = 10;

    /**
     * Specifies the type of Mouse event triggered.
     * @param type argument.
     */
    public MouseEvent(final Type type) {
        this.type = type;
    }

    public enum Type {
        Over,
        Left,
        Right;
    }
}
