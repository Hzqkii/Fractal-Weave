package xyz.flapjack.fractal.modules.impl.settings.impl;

/* Custom. */
import xyz.flapjack.fractal.modules.Module;
import xyz.flapjack.Access;

public abstract class Method extends Access {
    /**
     * Executes overriden method.
     */
    public abstract void execute(final Module module);
}
