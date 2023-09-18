package xyz.flapjack.fractal.events.module;

/* Custom. */
import net.minecraft.entity.EntityLivingBase;
import xyz.flapjack.fractal.events.FractalEvent;

public final class ChamsEvent extends FractalEvent {
    public final EntityLivingBase entity;
    public final State state;

    /**
     * The custom chams event, called via renderModel mixin injection.
     * @param entity argument.
     * @param state argument.
     */
    public ChamsEvent(final EntityLivingBase entity, final State state) {
        this.entity = entity;
        this.state = state;
    }

    /**
     * State definitions.
     */
    public enum State {
        Post,
        Pre;
    }
}
