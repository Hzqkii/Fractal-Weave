package xyz.flapjack.fractal.modules.impl.player;

/* Custom. */
import xyz.flapjack.fractal.modules.impl.util.Random;
import xyz.flapjack.fractal.events.impl.TickEvent;
import xyz.flapjack.fractal.events.Subscribed;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;

/* Open. */
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import java.util.ArrayList;
import java.awt.*;

public class BridgeAssist extends Module {
    private static final Color standardColour = new Color(17, 17, 17);

    private boolean state = false;
    private boolean bridging = false;

    private long time = 0L;

    public BridgeAssist() {
        super("Bridge Assist", "Assists bridging.", Category.Player, "menu", "bindable");

        this.registerSetting(new Setting("Only on blocks", this, null, true));
        this.registerSetting(new Setting("Only on shift", this, null, true));
        this.registerSetting(new Setting("Shift during jumps", this, null, true));

        Setting pitch = new Setting("Only on pitch", this, null, true);
        this.registerSetting(pitch);
        this.registerSetting(new Setting("Pitch angle", this, pitch, 45, 0, 180));

        this.registerSetting(new Setting("Shift length", this, null, 50, 0, 100));
    }

    @Subscribed(eventType = TickEvent.class)
    public void clientTick(final TickEvent event) {
        if (this.mcInstance.thePlayer == null) {
            return;
        }

        /*
         * These checks are helled back, as they require thePlayer to not be null.
         */
        ArrayList<Boolean> checks = new ArrayList<>();
        checks.add(!(Keyboard.isKeyDown(this.mcInstance.gameSettings.keyBindSneak.getKeyCode()) && !((boolean) this.getVal("Only on shift"))));
        checks.add(this.enabled);

        if (this.massCheck(checks)) {
            return;
        }

        if ((boolean) this.getVal("Only on blocks")) {
            if (this.mcInstance.thePlayer.getCurrentEquippedItem() == null) {
                return;
            }

            if (!(this.mcInstance.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock)) {
                return;
            }
        }

        if ((boolean) this.getVal("Only on shift") && !Keyboard.isKeyDown(this.mcInstance.gameSettings.keyBindSneak.getKeyCode())) {
            return;
        }

        if ((boolean) this.getVal("Only on pitch")) {
            if ((-this.mcInstance.thePlayer.rotationPitch) > ((int) this.getVal("Pitch angle") - 90)) {
                if (this.state && !Keyboard.isKeyDown(this.mcInstance.gameSettings.keyBindSneak.getKeyCode())) {
                    this.setState(false);
                }

                if (Keyboard.isKeyDown(this.mcInstance.gameSettings.keyBindSneak.getKeyCode())) {
                    this.setState(true);
                }

                return;
            }
        }

        if (this.mcInstance.thePlayer.capabilities.isFlying) {
            return;
        }

        if (this.mcInstance.thePlayer.onGround && this.overAir()) {
            int range = (int) Random.simpleRandom(1, 3);
            int delay = (int) this.getVal("Shift length");

            this.time = System.currentTimeMillis() + (long) Random.nextRandom(delay - range, delay + range);

            this.setState(true);
            this.bridging = true;
        } else if (this.mcInstance.thePlayer.isSneaking() && !Keyboard.isKeyDown(this.mcInstance.gameSettings.keyBindSneak.getKeyCode())) {
            this.setState(false);
            this.bridging = false;
        } else if (!Keyboard.isKeyDown(this.mcInstance.gameSettings.keyBindSneak.getKeyCode())) {
            this.setState(false);
            this.bridging = true;
        } else if (this.mcInstance.thePlayer.isSneaking() && System.currentTimeMillis() > this.time) {
            this.setState(false);
            this.bridging = true;
        }

        if (this.bridging && this.mcInstance.thePlayer.capabilities.isFlying) {
            this.setState(false);
        } else if (this.bridging && this.overAir() && (boolean) this.getVal("Shift during jumps")) {
            this.setState(true);
        } else {
            this.setState(false);
        }

        this.setState(this.bridging && this.overAir());
    }

    /**
     * Detect whether the player is over an air block.
     * @return the boolean.
     */
    private boolean overAir() {
        BlockPos target = new BlockPos(MathHelper.floor_double(this.mcInstance.thePlayer.posX),
                MathHelper.floor_double(this.mcInstance.thePlayer.posY - 1.0D),
                MathHelper.floor_double(this.mcInstance.thePlayer.posZ));
        return this.mcInstance.theWorld.isAirBlock(target);
    }

    /**
     * Sets the keybind state of the shift key.
     * @param state the target state.
     */
    private void setState(final boolean state) {
        this.state = state;
        KeyBinding.setKeyBindState(this.mcInstance.gameSettings.keyBindSneak.getKeyCode(), state);
    }
}
