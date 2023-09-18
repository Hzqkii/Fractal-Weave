package xyz.flapjack.fractal.modules.impl.player;

/* Custom. */
import xyz.flapjack.fractal.modules.impl.util.Random;
import xyz.flapjack.fractal.events.impl.RenderEvent;
import xyz.flapjack.fractal.bridge.impl.Player;
import xyz.flapjack.fractal.events.Subscribed;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;

/* Open. */
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import java.util.ArrayList;

public class MLG extends Module {
    private double playerPosY;

    public MLG() {
        super("MLG", "Breaks your landing.", Category.Player, "menu", "bindable");

        this.registerSetting(new Setting("Height", this, null, 10, 3, 75));
        this.registerSetting(new Setting("Delay", this, null, 10, 0, 500));

        Setting pitch = new Setting("Only on pitch", this, null, true);
        this.registerSetting(pitch);
        this.registerSetting(new Setting("Pitch angle", this, pitch, 45, 0, 180));
    }

    @Subscribed(eventType = RenderEvent.class)
    public void renderTick(final RenderEvent event) {
        if (!this.enabled) {
            return;
        }

        if (this.mcInstance.thePlayer.onGround) {
            this.playerPosY = this.mcInstance.thePlayer.posY;
        }

        if ((boolean) this.getVal("Only on pitch")) {
            if ((-this.mcInstance.thePlayer.rotationPitch) > ((int) this.getVal("Pitch angle") - 90)) {
                return;
            }
        }

        if (this.mcInstance.objectMouseOver != null) {
            if ((this.playerPosY - this.mcInstance.thePlayer.posY) > ((int) this.getVal("Height"))) {
                BlockPos current = this.mcInstance.objectMouseOver.getBlockPos();
                BlockPos forward = new BlockPos(current.getX(), current.getY() - 1, current.getZ());

                if (!this.mcInstance.theWorld.isAirBlock(forward)) {
                    ArrayList<Integer> available = this.loop();

                    if (available.isEmpty()) {
                        return;
                    }

                    this.use(available.get(0));
                }
            }
        }
    }

    /**
     * Searches for available hotkey slots.
     * @return the arraylist of available slots.
     */
    private ArrayList<Integer> loop() {
        ArrayList<Integer> available = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            if (this.mcInstance.thePlayer.inventory.getStackInSlot(i) != null) {
                if (this.mcInstance.thePlayer.inventory.getStackInSlot(i).getDisplayName().equalsIgnoreCase("WATER BUCKET")) {
                    available.add(i);
                }
            }
        }

        return available;
    }

    /**
     * Use an item in a slot.
     * @param slot the target slot.
     */
    private void use(final int slot) {
        new Thread(() -> {
            try {
                int initialSlot = this.mcInstance.thePlayer.inventory.currentItem;

                this.mcInstance.thePlayer.inventory.currentItem = slot;

                Thread.sleep((long) Random.nextRandom(5, 10));
                Player.mouse(1, true);
                KeyBinding.onTick(mcInstance.gameSettings.keyBindUseItem.getKeyCode());

                Thread.sleep((int) Random.simpleRandom((int) this.getVal("Delay") - 10, (int) this.getVal("Delay") + 10));
                Player.mouse(1, false);

                Thread.sleep((long) Random.nextRandom(5, 20));
                Player.mouse(1, true);
                KeyBinding.onTick(mcInstance.gameSettings.keyBindUseItem.getKeyCode());

                Thread.sleep((long) Random.nextRandom(5, 20));
                Player.mouse(1, false);

                Thread.sleep((int) Random.simpleRandom(10, 20));

                this.mcInstance.thePlayer.inventory.currentItem = initialSlot;
            } catch (Exception ignored) { }
        }).start();
    }
}
