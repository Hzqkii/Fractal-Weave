package xyz.flapjack.fractal.modules.impl.movement;

/* Custom. */
import xyz.flapjack.fractal.events.Subscribed;
import xyz.flapjack.fractal.events.impl.RenderEvent;
import xyz.flapjack.fractal.modules.impl.util.*;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;

/* Weave. */

/* Open. */
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import java.util.ArrayList;

public class Strafe extends Module {
    private long lastStrafe = 0L;

    private boolean isStrafe = false;
    private int keyCode = 0;

    public Strafe() {
        super("Strafe", "Automatically strafes.", Category.Movement, "menu", "bindable");

        this.registerSetting(new Setting("Chance", this, null, 25, 1, 100));

        this.registerSetting(new Setting("Only on attack", this, null, true));

        this.registerSetting(new Setting("Range", this, null, 2.5D, 0D, 4D));
        this.registerSetting(new Setting("Length", this, null, 10, 5, 25));
    }

    @Subscribed(eventType = RenderEvent.class)
    public void renderTick(final RenderEvent event) {
        ArrayList<Boolean> checks = new ArrayList<>();
        checks.add(this.mcInstance.inGameHasFocus);
        checks.add(this.enabled);

        /*
         * Inject a possibility of module being disabled, however strafe still pressing.
         */
        if (this.isStrafe && System.currentTimeMillis() > this.lastStrafe + (int) this.getVal("Length")) {
            this.release(this.keyCode);
        }

        if (this.massCheck(checks)) {
            return;
        }

        if ((boolean) this.getVal("Only on attack") && !Mouse.isButtonDown(0)) {
            return;
        }

        if (getModule("Aim Assist").getPrivateEnemy() == null) {
            return;
        }

        if (Distance.distanceToEntity(getModule("Aim Assist").getPrivateEnemy()) < (double) this.getVal("Range")) {
            if (System.currentTimeMillis() < this.lastStrafe + (int) this.getVal("Length")) {
                return;
            }

            if (Random.simpleRandom(0, 100) > (int) this.getVal("Chance")) {
                return;
            }

            if (this.keyCode == 0) {
                this.keyCode = Random.simpleRandom(0, 100) < 50 ? this.mcInstance.gameSettings.keyBindRight.getKeyCode() : this.mcInstance.gameSettings.keyBindLeft.getKeyCode();
            }

            this.lastStrafe = System.currentTimeMillis();

            if (this.isStrafe) {
                this.release(this.keyCode);
            } else {
                this.trigger(this.keyCode);
            }
        }
    }

    /**
     * Releases the W key.
     */
    private void release(final int keyCode) {
        if (Keyboard.isKeyDown(this.keyCode)) {
            return;
        }

        KeyBinding.setKeyBindState(keyCode, false);
        KeyBinding.onTick(keyCode);

        this.isStrafe = false;
        this.keyCode = 0;
    }

    /**
     * Holds the W key.
     */
    private void trigger(final int keyCode) {
        if (Keyboard.isKeyDown(this.keyCode)) {
            return;
        }

        KeyBinding.setKeyBindState(keyCode, true);
        KeyBinding.onTick(keyCode);

        this.isStrafe = true;
    }
}
