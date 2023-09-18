package xyz.flapjack.fractal.events.impl;

/* Custom. */
import xyz.flapjack.fractal.events.FractalEvent;

public final class RenderOverlayEvent extends FractalEvent {
    public final Type type;

    /**
     * Specifies the type of Mouse event triggered.
     * @param type argument.
     */
    public RenderOverlayEvent(final Type type) {
        this.type = type;
    }

    public enum Type {
        Pre,
        Post;
    }
}
