package xyz.flapjack.fractal.events.mixin;

/* Custom. */
import xyz.flapjack.fractal.events.EventBus;
import xyz.flapjack.fractal.events.impl.*;
import xyz.flapjack.Access;

/* Open. */
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.gui.GuiIngame;

@Mixin(GuiIngame.class)
public class MixinGuiInGame {
    EventBus eventBus = Access.Instance.getEventBus();

    /**
     * Injects a PRE RenderOverlay event into the renderGameOverlay method.
     * This is for the generic renderOverlay event.
     * @param f1            argument.
     * @param callbackInfo  argument.
     */
    @Inject(method = "renderGameOverlay", at = @At("HEAD"))
    public void injectRenderGameOverlayEventPre(final float f1, final CallbackInfo callbackInfo) {
        eventBus.call(new RenderOverlayEvent(RenderOverlayEvent.Type.Pre));
    }

    /**
     * Injects a POST RenderOverlay event into the renderGameOverlay method.
     * This is for the generic renderOverlay event.
     * @param f1            argument.
     * @param callbackInfo  argument.
     */
    @Inject(method = "renderGameOverlay", at = @At("RETURN"))
    public void injectRenderGameOverlayEventPost(final float f1, final CallbackInfo callbackInfo) {
        eventBus.call(new RenderOverlayEvent(RenderOverlayEvent.Type.Post));
    }
}
