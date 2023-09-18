package xyz.flapjack.fractal.modules.impl.combat;

/* Custom. */
import xyz.flapjack.fractal.events.impl.RenderEvent;
import xyz.flapjack.fractal.modules.impl.util.*;
import xyz.flapjack.fractal.events.Subscribed;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;

/* Open. */
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import java.util.ArrayList;

public class WTap extends Module {
    private int hits = 0;
    private boolean hit = false;

    private long time = 0;
    private boolean down = false;

    public WTap() {
        super("WTap", "Presses W strategically.", Category.Combat, "menu", "bindable");

        this.registerSetting(new Setting("Type", this, null, new String[] { "WTap", "STap" } ));

        this.registerSetting(new Setting("Hold length", this, null, 3, 1, 10));
        this.registerSetting(new Setting("Frequency", this, null, 2, 1, 5));
    }

    @Subscribed(eventType = RenderEvent.class)
    public void renderTick(final RenderEvent event) {
        ArrayList<Boolean> checks = new ArrayList<>();
        checks.add(this.mcInstance.inGameHasFocus);
        checks.add(this.enabled);

        if (this.massCheck(checks)) {
            return;
        }

        if (this.getVal("Type").equals("WTap")) {
            if (!Keyboard.isKeyDown(this.mcInstance.gameSettings.keyBindForward.getKeyCode())) {
                return;
            }
        } else {
            if (!Keyboard.isKeyDown(this.mcInstance.gameSettings.keyBindBack.getKeyCode())) {
                return;
            }
        }

        EntityPlayer enemy = getModule("Aim Assist").getPrivateEnemy();
        if (enemy == null) {
            return;
        }

        if (this.hits >= (int) this.getVal("Frequency") && System.currentTimeMillis() > this.time + (int) this.getVal("Hold length") * 50L && Distance.distanceToEntity(enemy) < 3.5f) {
            this.time = System.currentTimeMillis() + (long) Random.nextRandom(-25, 125);

            this.trigger();
        } else {
            if (System.currentTimeMillis() > this.time + ((int) this.getVal("Hold length") * 50L) && this.down) {
                this.time = System.currentTimeMillis();

                this.release();
            } else {
                if (Distance.distanceToEntity(enemy) < 3.5f && enemy.hurtTime > 0 && !this.hit) {
                    this.hits++;

                    this.hit = true;
                } else {
                    if (enemy.hurtTime < 1) {
                        this.hit = false;
                    }
                }
            }
        }
    }

    /**
     * Releases the W key.
     */
    private void release() {
        this.down = false;
        this.hits = 0;

        int key = this.getVal("Type").equals("WTap") ? this.mcInstance.gameSettings.keyBindForward.getKeyCode() : this.mcInstance.gameSettings.keyBindBack.getKeyCode();

        KeyBinding.setKeyBindState(key, false);
    }

    /**
     * Holds the W key.
     */
    private void trigger() {
        this.down = true;
        this.hits = 0;

        int key = this.getVal("Type").equals("WTap") ? this.mcInstance.gameSettings.keyBindForward.getKeyCode() : this.mcInstance.gameSettings.keyBindBack.getKeyCode();

        KeyBinding.setKeyBindState(key, true);
        KeyBinding.onTick(key);
    }
}
