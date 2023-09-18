package xyz.flapjack.fractal.events.impl;

/* Custom. */
import xyz.flapjack.fractal.events.FractalEvent;

public final class TickEvent extends FractalEvent {
    public final Type type;

    /**
     * Specifies the type of TickEvent.
     * @param type argument.
     */
    public TickEvent(final Type type) {
        this.type = type;
    }

    public enum Type {
        Pre,
        Post;
    }
}
