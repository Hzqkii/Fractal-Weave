package xyz.flapjack.fractal.events.impl;

/* Custom. */
import xyz.flapjack.fractal.events.FractalEvent;

/* Open. */
import org.lwjgl.input.Keyboard;

public final class KeyboardEvent extends FractalEvent {
    public final int keyCode;
    public final char keyChar;
    public final boolean keyState;

    /**
     * Specified keyboard information.
     */
    public KeyboardEvent() {
        this.keyCode = Keyboard.getEventKey();
        this.keyChar = Keyboard.getEventCharacter();
        this.keyState = Keyboard.getEventKeyState();
    }
}
