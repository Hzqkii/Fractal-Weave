package xyz.flapjack.fractal.modules.impl.combat;

/* Custom. */
import xyz.flapjack.fractal.modules.impl.util.Random;
import xyz.flapjack.fractal.events.impl.RenderEvent;
import xyz.flapjack.fractal.modules.impl.util.Sound;
import xyz.flapjack.fractal.bridge.impl.Player;
import xyz.flapjack.fractal.events.Subscribed;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;


/* Open. */
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.block.BlockLiquid;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraft.item.ItemAxe;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.lwjgl.input.Mouse;
import java.util.ArrayList;

public class AutoClicker extends Module {
    private long lastClick = 0L;
    private long lastHold = 0L;
    private long lastBreak = 0L;

    private boolean held = false;

    private final float[] clicks = new float[50];
    private float modifier = 1;

    private float lastModifier = 1;
    private long time = 0L;

    private boolean dropped = false;
    private long lastDropped = 0L;

    private long lastSound = 0L;

    private boolean clicking = false;

    public AutoClicker() {
        super("Auto Clicker", "Clicks the mouse for you.", Category.Combat, "menu", "bindable");

        this.registerSetting(new Setting("CPS", this, null, 15, 1, 25));

        this.registerSetting(new Setting("Random range", "How large the range above and below the selected CPS is (+- value).", this, null, 5, 2, 10));
        this.registerSetting(new Setting("Drop chance", "The chance your CPS drops to a lower CPS for a time.", this, null, 5, 1, 20));

        this.registerSetting(new Setting("Jitter", this, null, 10, 1, 15));

        Setting breakBlocks = new Setting("Break blocks", this, null, true);
        this.registerSetting(breakBlocks);
        this.registerSetting(new Setting("Break blocks delay", this, breakBlocks, 15, 0, 100));

        this.registerSetting(new Setting("Weapon only", this, null, true));
        this.registerSetting(new Setting("Hit select", this, null, false));

        Setting sound = new Setting("Click sound", this, null, false);
        this.registerSetting(sound);
        this.registerSetting(new Setting("Delay", this, sound, 100, 0, 500));
    }

    @Subscribed(eventType = RenderEvent.class)
    public void renderTick(final RenderEvent event) {
        ArrayList<Boolean> checks = new ArrayList<>();
        checks.add(this.mcInstance.inGameHasFocus);
        checks.add(Mouse.isButtonDown(0));
        checks.add(this.enabled);

        if (this.massCheck(checks)) {
            return;
        }

        if ((boolean) this.getVal("Hit select") && getModule("Aim Assist").getPrivateEnemy() != null) {
            if (getModule("Aim Assist").getPrivateEnemy().hurtTime > 1) {
                return;
            }
        }

        if ((boolean) this.getVal("Weapon only") && this.mcInstance.thePlayer.getCurrentEquippedItem() != null) {
            Item held = this.mcInstance.thePlayer.getCurrentEquippedItem().getItem();

            if (!(held instanceof ItemSword) && !(held instanceof ItemAxe)) {
                return;
            }
        } else if ((boolean) this.getVal("Weapon only")) {
            return;
        }

        if ((boolean) this.getVal("Break blocks")) {
            if (this.lastBreak + (int) getModule("Auto Clicker").getVal("Break blocks delay") + (long) Random.nextRandom(-10, 10) > System.currentTimeMillis()) {
                return;
            }
        }

        if ((boolean) this.getVal("Break blocks") && this.mcInstance.objectMouseOver != null) {
            BlockPos pos = this.mcInstance.objectMouseOver.getBlockPos();
            if (pos != null) {
                Block block = this.mcInstance.theWorld.getBlockState(pos).getBlock();

                if (block != Blocks.air && !(block instanceof BlockLiquid) && (block != null)) {
                    if (!this.held) {
                        new Thread(() -> {
                            try {
                                Thread.sleep(10);

                                Player.mouse(0, false);

                                Thread.sleep(95);

                                Player.mouse(0, true);

                            } catch (Exception ignored) { }
                        }).start();

                        this.held = true;
                    }

                    return;
                }

                if (this.held) {
                    this.lastBreak = System.currentTimeMillis();
                    this.held = false;

                    return;
                }
            }
        }

        this.held = false;
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

            if (Random.nextRandom(0, 100) < (int) this.getVal("Drop chance")) {
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

        int range = (int) (Random.nextRandom(2, (int) this.getVal("Random range")));
        int max = Math.min(Math.max((int) this.getVal("CPS") + range, 0), 30);
        int min = Math.min(Math.max((int) this.getVal("CPS") - range, 0), 30);

        double speed = this.modifier / Random.nextRandom(min, max);
        double length = (speed / Random.nextRandom(min, max)) * ((100 + (Random.nextRandom(1, 20))) / 100);

        float x = 0f;
        float x2 = 0;

        for (float f1: this.clicks) {
            x += f1;
            x2 += (f1 * f1);
        }

        float deviation = (x2 / this.clicks.length) - (float) Math.pow((x / this.clicks.length), 2);

        if (deviation < 4.0 && System.currentTimeMillis() > this.time + (100 + Random.simpleRandom(0, 250))) {
            this.time = System.currentTimeMillis();
            this.modifier = (float) (Random.simpleRandom(90, 105) / 100);
        }

        for (int i = this.clicks.length - 1; i > 0; i--) {
            this.clicks[i] = this.clicks[i - 1];
        }
        this.clicks[0] = (float) speed;

        if (Random.nextRandom(0, 100) < 10) {
            this.lastModifier = this.modifier;
            this.modifier += (Random.nextRandom(0, 15) / 100) * (Random.nextRandom(0, 2) == 1 ? -1 : 1);
        }

        if (System.currentTimeMillis() - this.lastClick > speed * 1000f) {
            this.lastClick = System.currentTimeMillis();

            if (this.lastHold < this.lastClick) {
                this.lastHold = this.lastClick;
            }

            Player.mouse(0, true);
            KeyBinding.onTick(mcInstance.gameSettings.keyBindAttack.getKeyCode());

            this.clicking = true;

            if ((boolean) this.getVal("Click sound")) {
                if (System.currentTimeMillis() > this.lastSound) {
                    new Thread(() -> {
                        Sound sound = new Sound("/assets/fractal/sounds/double-click.wav");
                    }).start();

                    this.lastSound = System.currentTimeMillis() + (int) this.getVal("Delay");
                }
            }
        } else if (System.currentTimeMillis() - this.lastHold > length * 1000) {
            Player.mouse(0, false);

            this.clicking = false;
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
     * @param target        the original rotation angle.
     * @param originalValue the last original rotation angle.
     * @return              the new value.
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