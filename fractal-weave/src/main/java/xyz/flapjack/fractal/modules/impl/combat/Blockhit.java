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
import org.lwjgl.input.Mouse;
import java.util.ArrayList;

public class Blockhit extends Module {
    private int hits = 0;
    private boolean hit = false;

    private long time = 0;
    private boolean down = false;

    public Blockhit() {
        super("Blockhit", "Blocks during combat.", Category.Combat, "menu", "bindable");

        this.registerSetting(new Setting("Hold length", this, null, 3, 1, 10));
        this.registerSetting(new Setting("Frequency", this, null, 2, 1, 5));

        this.registerSetting(new Setting("Smart", "Will only blockhit when you are not attacking.", this, null, true));
    }

    @Subscribed(eventType = RenderEvent.class)
    public void renderTick(final RenderEvent event) {
        ArrayList<Boolean> checks = new ArrayList<>();
        checks.add(this.mcInstance.inGameHasFocus);
        checks.add(this.enabled);

        if (this.massCheck(checks)) {
            return;
        }

        EntityPlayer enemy = getModule("Aim Assist").getPrivateEnemy();
        if (enemy == null) {
            return;
        }

        if (!Mouse.isButtonDown(0) && !Mouse.isButtonDown(1)) {
            this.release();
        }

        if (!Mouse.isButtonDown(0)) {
            return;
        }

        if ((boolean) this.getVal("Smart") && (boolean) getModule("Auto Clicker").getPriv("clicking")) {
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
     * Releases the RMB.
     */
    private void release() {
        this.down = false;
        this.hits = 0;

        KeyBinding.setKeyBindState(this.mcInstance.gameSettings.keyBindUseItem.getKeyCode(), false);
    }

    /**
     * Holds the RMB.
     */
    private void trigger() {
        this.down = true;
        this.hits = 0;

        KeyBinding.setKeyBindState(this.mcInstance.gameSettings.keyBindUseItem.getKeyCode(), true);
        KeyBinding.onTick(this.mcInstance.gameSettings.keyBindUseItem.getKeyCode());
    }
}
