package xyz.flapjack.fractal.events.mixin;

/* Custom. */
import xyz.flapjack.fractal.events.module.NameTagEvent;
import xyz.flapjack.fractal.events.module.ChamsEvent;
import xyz.flapjack.fractal.events.module.EspEvent;
import xyz.flapjack.fractal.events.EventBus;
import xyz.flapjack.Access;

/* Open. */
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;

@Mixin(RendererLivingEntity.class)
public class MixinRenderLivingEntity<T extends EntityLivingBase> {
    EventBus eventBus = Access.Instance.getEventBus();

    /**
     * Injects a PRE Chams event into the renderModel method.
     * This is for allowing chams to effect the depth, colour and possible shader of the player.
     *
     * @param entity argument.
     * @param f1     argument.
     * @param f2     argument.
     * @param f3     argument.
     * @param f4     argument.
     * @param f5     argument.
     * @param f6     argument.
     * @param ci     argument.
     */
    @Inject(method = "renderModel", at = @At("HEAD"))
    public void injectChamsEventPre(final EntityLivingBase entity,
                                    final float f1,
                                    final float f2,
                                    final float f3,
                                    final float f4,
                                    final float f5,
                                    final float f6,
                                    final CallbackInfo ci) {
        eventBus.call(new ChamsEvent(entity, ChamsEvent.State.Pre));
    }

    /**
     * Injects a POST chams event into the renderModel method.
     * This is for allowing chams to effect the depth, colour and possible shader of the player.
     *
     * @param entity argument.
     * @param f1     argument.
     * @param f2     argument.
     * @param f3     argument.
     * @param f4     argument.
     * @param f5     argument.
     * @param f6     argument.
     * @param ci     argument.
     */
    @Inject(method = "renderModel", at = @At(value = "TAIL"))
    public void injectChamsEventPost(final EntityLivingBase entity,
                                     final float f1,
                                     final float f2,
                                     final float f3,
                                     final float f4,
                                     final float f5,
                                     final float f6,
                                     final CallbackInfo ci) {
        eventBus.call(new ChamsEvent(entity, ChamsEvent.State.Post));
    }

    /**
     * Injects an ESPEvent into the doRender method.
     * This is for ESP to use partialTicks, and smooth rendered movement.
     *
     * @param entity       argument.
     * @param x            argument.
     * @param y            argument.
     * @param z            argument.
     * @param entityYaw    argument.
     * @param partialTicks argument.
     * @param ci           argument.
     */
    @Inject(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V", at = @At(value = "RETURN", shift = At.Shift.BEFORE))
    public void atReturn(final EntityLivingBase entity,
                         final double x,
                         final double y,
                         final double z,
                         final float entityYaw,
                         final float partialTicks,
                         final CallbackInfo ci) {
        eventBus.call(new EspEvent(entity, partialTicks));
    }

    /**
     * Injects a NameTagEvent into the renderName method.
     * This is to override the default nametag rendering for the nametag module.
     *
     * @param t1 argument.
     * @param d1 argument.
     * @param d2 argument.
     * @param d3 argument.
     * @param ci argument.
     */
    @Inject(method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V", at = @At("HEAD"), cancellable = true)
    public void atHead(final T t1,
                       final double d1,
                       final double d2,
                       final double d3,
                       final CallbackInfo ci) {
        eventBus.call(new NameTagEvent());
    }
}
