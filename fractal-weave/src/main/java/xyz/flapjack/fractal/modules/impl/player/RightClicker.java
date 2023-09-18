package xyz.flapjack.fractal.modules.impl.player;

/* Custom. */
import xyz.flapjack.fractal.modules.impl.util.Random;
import xyz.flapjack.fractal.events.impl.RenderEvent;
import xyz.flapjack.fractal.bridge.impl.Player;
import xyz.flapjack.fractal.events.Subscribed;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;

/* Open. */
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.*;
import org.lwjgl.input.Mouse;
import java.util.ArrayList;

public class RightClicker extends Module {
    private long lastClick = 0L;
    private long lastHold = 0L;

    private float modifier = 1;

    private float lastModifier = 1;

    private boolean dropped = false;
    private long lastDropped = 0L;

    private long delay = 0L;

    public RightClicker() {
        super("Right Clicker", "Clicks the mouse for you.", Category.Player, "menu", "bindable");

        this.registerSetting(new Setting("CPS", this, null, 15, 1, 25));
        this.registerSetting(new Setting("Jitter", this, null, 10, 0, 15));

        this.registerSetting(new Setting("Right click delay", this, null, 250, 0, 1000));

        this.registerSetting(new Setting("Projectiles", this, null, true));
        this.registerSetting(new Setting("Blocks", this, null, true));
        this.registerSetting(new Setting("Rods", this, null, false));
    }

    @Subscribed(eventType = RenderEvent.class)
    public void renderTick(final RenderEvent event) {
        ArrayList<Boolean> checks = new ArrayList<>();
        checks.add(this.mcInstance.inGameHasFocus);
        checks.add(this.enabled);

        if (this.massCheck(checks)) {
            return;
        }

        if (this.mcInstance.thePlayer.getCurrentEquippedItem() == null) {
            return;
        }

        if (!Mouse.isButtonDown(1)) {
            this.delay = System.currentTimeMillis() + (int) this.getVal("Right click delay");

            return;
        }

        if (System.currentTimeMillis() < this.delay) {
            return;
        }

        if (!(boolean) this.getVal("Projectiles")) {
            Item held = this.mcInstance.thePlayer.getCurrentEquippedItem().getItem();

            if (held instanceof ItemEgg || held instanceof ItemEnderPearl || held instanceof ItemExpBottle) {
                return;
            }
        }

        if (!(boolean) this.getVal("Blocks")) {
            Item held = this.mcInstance.thePlayer.getCurrentEquippedItem().getItem();

            if (held instanceof ItemBlock) {
                return;
            }
        }

        if (!(boolean) this.getVal("Rods")) {
            Item held = this.mcInstance.thePlayer.getCurrentEquippedItem().getItem();

            if (held instanceof ItemFishingRod) {
                return;
            }
        }

        if (this.mcInstance.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow
            || this.mcInstance.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword
            || this.mcInstance.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemAxe
            || this.mcInstance.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemPotion
            || this.mcInstance.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBucketMilk) {
            return;
        }

        if (this.mcInstance.thePlayer.getCurrentEquippedItem() != null) {
            Item held = this.mcInstance.thePlayer.getCurrentEquippedItem().getItem();

            if (held instanceof ItemFood) {
                return;
            }
        }

        this.click();
    }

    /**
     * Allow a mouse click.
     */
    private void click() {
        if (System.currentTimeMillis() > this.lastDropped) {
            this.dropped = false;
        } else {
            if (this.dropped) {
                return;
            }

            if (Random.nextRandom(0, 100) < 3) {
                this.dropped = true;
                this.lastDropped = System.currentTimeMillis() + (long) Random.nextRandom(5, 35);

                return;
            }
        }

        this.jitter((int) this.getVal("Jitter") + (int) (Random.simpleRandom(0, 2) * ((int) Random.simpleRandom(0, 2) == 1 ? 1 : -1)));

        this.modifier = this.lastModifier;
        if (Random.nextRandom(0, 100) < 10) {
            this.modifier = 1f;
        }

        int range = (int) (Random.nextRandom(2, 5));
        int max = (int) this.getVal("CPS") + range;
        int min = (int) this.getVal("CPS") - range;

        double speed = this.modifier / Random.nextRandom(min, max);
        double length = (speed / Random.nextRandom(min, max)) * ((100 + (Random.nextRandom(1, 20))) / 100);

        if (Random.nextRandom(0, 100) < 10) {
            this.lastModifier = this.modifier;
            this.modifier += (Random.nextRandom(0, 15) / 100) * (Random.nextRandom(0, 2) == 1 ? -1 : 1);
        }

        if (System.currentTimeMillis() - this.lastClick > speed * 1000) {
            this.lastClick = System.currentTimeMillis();

            if (this.lastHold < this.lastClick) {
                this.lastHold = this.lastClick;
            }

            Player.mouse(1, true);
            KeyBinding.onTick(this.mcInstance.gameSettings.keyBindUseItem.getKeyCode());
        } else if (System.currentTimeMillis() - this.lastHold > length * 1000) {
            Player.mouse(1, false);
        }
    }

    /**
     * Jitters the mouse based on input, and applies a GCD patch.
     * @param amount X and Y amount.
     */
    private void jitter(final int amount) {
        if (amount == 0) {
            return;
        }

        EntityPlayerSP player = this.mcInstance.thePlayer;

        float yaw = (float) ((double) player.rotationYaw + ((Random.nextRandom(0, amount) * ((int) Random.simpleRandom(0, 2) == 1 ? 1 : -1)) / 75));
        float pitch = (float) ((double) player.rotationPitch + ((Random.nextRandom(0, amount) * ((int) Random.simpleRandom(0, 2) == 1 ? 1 : -1)) / 75));

        if (pitch > 90) {
            pitch = 90;
        }

        if (pitch < -90) {
            pitch = -90;
        }

        player.rotationYaw = this.gcd(yaw, player.rotationYaw);
        player.rotationPitch = this.gcd(pitch, player.rotationPitch);
    }

    /**
     * Normalizes sensitivity rotations.
     * @param target the original rotation angle.
     * @param originalValue the last original rotation angle.
     * @return the new value.
     */
    private float gcd(final float target, final float originalValue) {
        float patched = 0f;
        float delta = 0f;

        float value = (mcInstance.gameSettings.mouseSensitivity * 0.6f) + 0.2f;
        float gcd = (value * value * value) * 1.2f;

        try {
            delta = target - originalValue;
            delta -= delta % gcd;
            patched = originalValue + delta;
        } catch (Exception _ignored) {
            patched = 0f;
        }

        return patched;
    }
}
