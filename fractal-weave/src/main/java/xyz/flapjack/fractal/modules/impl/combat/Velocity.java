package xyz.flapjack.fractal.modules.impl.combat;

/* Custom. */
import xyz.flapjack.fractal.modules.impl.util.Random;
import xyz.flapjack.fractal.events.impl.TickEvent;
import xyz.flapjack.fractal.bridge.impl.Player;
import xyz.flapjack.fractal.events.Subscribed;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;

/* Open. */
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import java.util.ArrayList;

public class Velocity extends Module {
    private boolean triggered = false;
    private boolean jumping = false;

    private int ticks = 0;
    private long time = 0L;

    public Velocity() {
        super("Velocity", "Modifies the knockback.", Category.Combat, "menu", "bindable");

        this.registerSetting(new Setting("Mode", this, null, new String[] {"Normal", "Jump"}));

        this.registerSetting(new Setting("Chance", this, null, 100, 0, 100));

        this.registerSetting(new Setting("Horizontal", this, null, 100, 0, 150));
        this.registerSetting(new Setting("Vertical", this, null, 100, 0, 150));

        this.registerSetting(new Setting("Tick delay", "Will wait a certain amount of ticks before applying the velocity.", this, null, 2, 0, 10));

        this.registerSetting(new Setting("Target required", this, null, true));
        this.registerSetting(new Setting("Movement required", this, null, false));
        this.registerSetting(new Setting("Disable on S", this, null, false));
    }

    @Subscribed(eventType = TickEvent.class)
    public void clientTick(final TickEvent event) {
        ArrayList<Boolean> checks = new ArrayList<>();
        checks.add(Player.inGame());
        checks.add(this.enabled);

        if (this.massCheck(checks)) {
            return;
        }

        if (this.triggered) {
            this.ticks++;
        }

        /*
         * Jump mode.
         */
        if (this.getVal("Mode").equals("Jump") && this.jumping && System.currentTimeMillis() - Random.nextRandom(10, 40) > this.time) {
            KeyBinding.setKeyBindState(mcInstance.gameSettings.keyBindJump.getKeyCode(), false);

            this.jumping = false;
        }

        if (this.ticks > (int) this.getVal("Tick delay") && this.triggered) {
            if (this.getVal("Mode").equals("Jump")) {
                KeyBinding.setKeyBindState(mcInstance.gameSettings.keyBindJump.getKeyCode(), true);
                KeyBinding.onTick(this.mcInstance.gameSettings.keyBindJump.getKeyCode());

                this.time = System.currentTimeMillis();
                this.jumping = true;
            }

            this.mcInstance.thePlayer.motionX *= (double) ((int) this.getVal("Horizontal")) / 100;
            this.mcInstance.thePlayer.motionY *= (double) ((int) this.getVal("Vertical")) / 100;
            this.mcInstance.thePlayer.motionZ *= (double) ((int) this.getVal("Horizontal")) / 100;

            this.ticks = 0;
            this.triggered = false;
        }

        if (this.mcInstance.thePlayer.maxHurtTime > 0 && this.mcInstance.thePlayer.hurtTime == this.mcInstance.thePlayer.maxHurtTime) {
            /*
             * Individual state checks.
             */

            if ((boolean) this.getVal("Target required")) {
                if (this.mcInstance.objectMouseOver == null || this.mcInstance.objectMouseOver.entityHit == null) {
                    return;
                }
            }

            if ((boolean) this.getVal("Movement required")) {
                if (!Keyboard.isKeyDown(this.mcInstance.gameSettings.keyBindForward.getKeyCode())
                        && !Keyboard.isKeyDown(this.mcInstance.gameSettings.keyBindBack.getKeyCode())
                        && !Keyboard.isKeyDown(this.mcInstance.gameSettings.keyBindLeft.getKeyCode())
                        && !Keyboard.isKeyDown(this.mcInstance.gameSettings.keyBindRight.getKeyCode())) {
                    return;
                }
            }

            if ((boolean) this.getVal("Disable on S")) {
                if (Keyboard.isKeyDown(this.mcInstance.gameSettings.keyBindBack.getKeyCode())) {
                    return;
                }
            }

            if (Random.nextRandom(0, 100) < (int) this.getVal("Chance") + 1) {
                if ((int) this.getVal("Tick delay") == 0) {
                    this.mcInstance.thePlayer.motionX *= (double) ((int) this.getVal("Horizontal")) / 100;
                    this.mcInstance.thePlayer.motionY *= (double) ((int) this.getVal("Vertical")) / 100;
                    this.mcInstance.thePlayer.motionZ *= (double) ((int) this.getVal("Horizontal")) / 100;

                    this.ticks = 0;
                } else {
                    this.triggered = true;
                }
            }
        }
    }
}
