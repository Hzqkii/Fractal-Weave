package xyz.flapjack.fractal.events.mixin;

/* Custom. */
import xyz.flapjack.Access;
import xyz.flapjack.fractal.events.impl.*;

/* Weave. */
import xyz.flapjack.fractal.events.EventBus;

/* Mixin. */
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    EventBus eventBus = Access.Instance.getEventBus();

    /**
     * Shadowed necessary variables.
     */
    @Shadow public GuiScreen currentScreen;
    @Shadow private int rightClickDelayTimer;
    @Shadow private int leftClickCounter;

    /**
     * Injects a StartGameEvent into the startGame method.
     * This is to initialize our client, with a valid Minecraft instance and opengl context.
     * @param callbackInfo argument.
     */
    @Inject(method = "startGame", at = @At("TAIL"))
    public void injectStartGameEvent(final CallbackInfo callbackInfo) {
        eventBus.call(new StartGameEvent());
    }

    /**
     * Injects a PRE TickEvent into the runTick method.
     * This is for the generic tick event.
     * @param callbackInfo argument.
     */
    @Inject(method = "runTick", at = @At("HEAD"))
    public void injectTickEventPre(final CallbackInfo callbackInfo) {
        eventBus.call(new TickEvent(TickEvent.Type.Pre));
    }

    /**
     * Injects a POST TickEvent into the runTick method.
     * This is for the generic tick event.
     * @param callbackInfo argument.
     */
    @Inject(method = "runTick", at = @At("RETURN"))
    public void injectTickEventPost(final CallbackInfo callbackInfo) {
        eventBus.call(new TickEvent(TickEvent.Type.Post));
    }

    /**
     * Injects a KeyboardEvent into the dispatchKey method.
     * This is to capture keyboard events, such as key presses and releases, for keybinding.
     * @param callbackInfo argument.
     */
    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dispatchKeypresses()V", shift = At.Shift.AFTER))
    public void injectKeyboardEvent(final CallbackInfo callbackInfo) {
        if (currentScreen == null) {
            eventBus.call(new KeyboardEvent());
        }
    }

    /**
     * Injects a MouseEvent into the clickMouse method.
     * This is to target the built-in left click delay, for the delay remover module.
     * @param callbackInfo argument.
     */
    @Inject(method = "clickMouse", at = @At("TAIL"))
    public void injectMouseClickEvent(final CallbackInfo callbackInfo) {
        MouseEvent event = new MouseEvent(MouseEvent.Type.Left);
        eventBus.call(event);

        if (event.isRecognised) {
            leftClickCounter = event.leftClickCounter;
        }
    }

    /**
     * Injects a MouseEvent into the rightClickMouse method.
     * This is to target the built-in right click delay, for the fastplace module.
     * @param callbackInfo argument.
     */
    @Inject(method = "rightClickMouse", at = @At("TAIL"))
    public void injectRightClickMouseEvent(final CallbackInfo callbackInfo) {
        MouseEvent event = new MouseEvent(MouseEvent.Type.Right);
        eventBus.call(event);

        if (event.isRecognised) {
            rightClickDelayTimer = event.rightClickDelay;
        }
    }
}
