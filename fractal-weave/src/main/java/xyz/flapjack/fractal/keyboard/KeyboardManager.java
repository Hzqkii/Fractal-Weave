package xyz.flapjack.fractal.keyboard;

/* Custom. */
import xyz.flapjack.fractal.interfaces.clickgui.ClickGui;
import xyz.flapjack.fractal.events.impl.KeyboardEvent;
import xyz.flapjack.fractal.events.Subscribed;
import xyz.flapjack.fractal.modules.Module;
import xyz.flapjack.Access;

/* Open. */
import net.minecraft.client.Minecraft;

public class KeyboardManager {
    public KeyboardManager() {
        Access.Instance.getEventBus().subscribe(this);
    }

    @Subscribed(eventType = KeyboardEvent.class)
    public void process(final KeyboardEvent event) {
        if (event.keyCode != 0 && event.keyCode != 14) {
            for (Module module: Access.Instance.getModules()) {
                if (module.keyBind == event.keyCode && event.keyState) {
                    module.enabled = !module.enabled;
                }
            }
        }

        if (Access.Instance.getModule("Click GUI").keyBind == event.keyCode && event.keyState) {
            if (Minecraft.getMinecraft().currentScreen instanceof ClickGui) {
                Access.Instance.getInstance().getClickGui().triggerClose();
            } else {
                Access.Instance.getInstance().getClickGui().triggerOpen();
            }
        }
    }
}