package xyz.flapjack.fractal.events.module;

/* Custom. */
import xyz.flapjack.fractal.events.FractalEvent;

/* Open. */
import net.minecraft.entity.EntityLivingBase;

public final class EspEvent extends FractalEvent {
    public final EntityLivingBase entity;
    public final float partialTicks;

    /**
     * The custom ESP event, called via doRender mixin injection.
     * @param entity the target entity to render the ESP outline on.
     * @param partialTicks the current client partialTick.
     */
    public EspEvent(final EntityLivingBase entity, final float partialTicks) {
        this.entity = entity;
        this.partialTicks = partialTicks;
    }
}
