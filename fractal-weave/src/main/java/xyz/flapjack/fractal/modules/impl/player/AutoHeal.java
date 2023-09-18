package xyz.flapjack.fractal.modules.impl.player;

/* Custom. */
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.ContainerPlayer;
import xyz.flapjack.fractal.modules.impl.util.Random;
import xyz.flapjack.fractal.events.impl.TickEvent;
import xyz.flapjack.fractal.bridge.impl.Player;
import xyz.flapjack.fractal.events.Subscribed;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;

/* Open. */
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemSoup;
import java.util.ArrayList;

public class AutoHeal extends Module {
    private boolean healing = false;
    private long lastUsed = 0L;

    public AutoHeal() {
        super("Auto Heal", "A heal system.", Category.Player, "menu", "bindable");

        this.registerSetting(new Setting("Delay", this, null, 60, 20, 100));
        this.registerSetting(new Setting("Cooldown", this, null, 50, 0, 250));
        this.registerSetting(new Setting("Health threshold", "The amount of health you have to be at, before triggering.", this, null, 8, 0, 20));

        Setting soup = new Setting("Soup", this, null, true);
        this.registerSetting(soup);

        this.registerSetting(new Setting("Drop bowls", this, soup, true));
        this.registerSetting(new Setting("Only in inventory", this, soup, true));
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
        checks.add(this.mcInstance.thePlayer.getHealth() < (int) this.getVal("Health threshold"));
        checks.add(System.currentTimeMillis() > this.lastUsed + (int) this.getVal("Cooldown"));
        checks.add(this.enabled);
        checks.add(!healing);

        if (this.massCheck(checks)) {
            return;
        }

        if ((boolean) this.getVal("Only in inventory")) {
            if ((this.mcInstance.currentScreen != null) && ((this.mcInstance.thePlayer.inventoryContainer != null) && (this.mcInstance.thePlayer.inventoryContainer instanceof ContainerPlayer) && (this.mcInstance.currentScreen instanceof GuiInventory))) {
                return;
            }
        }

        this.soup();

        this.lastUsed = System.currentTimeMillis();
    }

    /**
     * Get soup.
     */
    private void soup() {
        ArrayList<Integer> available = this.loop(Type.SOUP);

        if (available.isEmpty()) {
            return;
        }

        this.use(available.get(0));
    }

    /**
     * Searches for available hotkey slots.
     * @param type the type of item to search for.
     * @return the arraylist of available slots.
     */
    private ArrayList<Integer> loop(final Type type) {
        ArrayList<Integer> available = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            if (type == Type.SOUP) {
                if (this.mcInstance.thePlayer.inventory.getStackInSlot(i) != null) {
                    if (this.mcInstance.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemSoup) {
                        available.add(i);
                    }
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

                if ((boolean) this.getVal("Drop bowls")) {
                    Thread.sleep((int) Random.simpleRandom(80, 100));

                    KeyBinding.setKeyBindState(mcInstance.gameSettings.keyBindDrop.getKeyCode(), true);
                    KeyBinding.onTick(mcInstance.gameSettings.keyBindDrop.getKeyCode());

                    Thread.sleep((int) Random.simpleRandom(90, 120));

                    KeyBinding.setKeyBindState(mcInstance.gameSettings.keyBindDrop.getKeyCode(), false);
                    KeyBinding.onTick(mcInstance.gameSettings.keyBindDrop.getKeyCode());
                }

                Thread.sleep((int) Random.simpleRandom(50, 100));

                this.mcInstance.thePlayer.inventory.currentItem = initialSlot;

                this.healing = false;
            } catch (Exception ignored) { }
        }).start();
    }

    /**
     * Types of items.
     */
    private enum Type {
        SOUP,
        GAPPLE;
    }
}
