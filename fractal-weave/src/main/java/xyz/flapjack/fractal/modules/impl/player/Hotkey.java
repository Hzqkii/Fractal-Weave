package xyz.flapjack.fractal.modules.impl.player;

/* Custom. */
import xyz.flapjack.fractal.events.impl.KeyboardEvent;
import xyz.flapjack.fractal.modules.impl.util.Random;
import xyz.flapjack.fractal.bridge.impl.Player;
import xyz.flapjack.fractal.events.Subscribed;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;

/* Open. */
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSoup;
import java.util.ArrayList;

public class Hotkey extends Module {
    public Hotkey() {
        super("Hotkey", "Hotkey to items.", Category.Player, "menu", "bindable");

        this.registerSetting(new Setting("Delay", this, null, 60, 15, 200));

        Setting potions = new Setting("Potions", this, null, false);
        this.registerSetting(potions);
        this.registerSetting(new Setting("Random potion", this, potions, false));
        this.registerSetting(new Setting("Smart splash", this, potions, false));
        this.registerSetting(new Setting("Potion", this, potions, 0));

        Setting soups = new Setting("Soups", this, null, false);
        this.registerSetting(soups);
        this.registerSetting(new Setting("Random soup", this, soups, false));
        this.registerSetting(new Setting("Smart drink", this, soups, false));
        this.registerSetting(new Setting("Soup", this, soups, 1));

        Setting rod = new Setting("Rods", this, null, false);
        this.registerSetting(rod);
        this.registerSetting(new Setting("Random rod", this, rod, false));
        this.registerSetting(new Setting("Rod", this, rod, 2));

        Setting blocks = new Setting("Blocks", this, null, false);
        this.registerSetting(blocks);
        this.registerSetting(new Setting("Block", this, blocks, 3));
    }

    @Subscribed(eventType = KeyboardEvent.class)
    public void keyPress(final KeyboardEvent event) {
        if (!this.enabled) {
            return;
        }

        if (!event.keyState) {
            return;
        }

        if ((int) this.getVal("Potion") == event.keyCode && (boolean) this.getVal("Potions")) {
            this.potion();
        } else if ((int) this.getVal("Soup") == event.keyCode && (boolean) this.getVal("Soups")) {
            this.soup();
        } else if ((int) this.getVal("Rod") == event.keyCode && (boolean) this.getVal("Rods")) {
            this.rod();
        } else if  ((int) this.getVal("Block") == event.keyCode && (boolean) this.getVal("Blocks")) {
            this.block();
        }
    }

    /**
     * Splash a potion.
     */
    private void potion() {
        ArrayList<Integer> available = this.loop(Type.POTION);

        if (available.isEmpty()) {
            return;
        }

        if ((boolean) this.getVal("Smart splash")) {
            int potions = Math.round((20 - this.mcInstance.thePlayer.getHealth()) / 4);

            if (potions == 0) {
                return;
            }

            for (int i = 0; i <= potions; i++) {
                if (!available.isEmpty()) {
                    if ((boolean) this.getVal("Random potion")) {
                        int chosen = (int) Random.simpleRandom(0, available.size() - 1);

                        this.use(available.get(chosen));
                        available.remove(chosen);
                    } else {
                        this.use(available.get(0));
                        available.remove(0);
                    }
                }
            }
        } else {
            if ((boolean) this.getVal("Random potion")) {
                int chosen = (int) Random.simpleRandom(0, available.size() - 1);

                this.use(available.get(chosen));
            } else {
                this.use(available.get(0));
            }
        }
    }

    /**
     * Drink a soup.
     */
    private void soup() {
        ArrayList<Integer> available = this.loop(Type.SOUP);

        if (available.isEmpty()) {
            return;
        }

        if ((boolean) this.getVal("Smart drink")) {
            int potions = Math.round((20 - this.mcInstance.thePlayer.getHealth()) / 4);

            if (potions == 0) {
                return;
            }

            for (int i = 0; i <= potions; i++) {
                if (!available.isEmpty()) {
                    if ((boolean) this.getVal("Random soup")) {
                        int chosen = (int) Random.simpleRandom(0, available.size() - 1);

                        this.use(available.get(chosen));
                        available.remove(chosen);
                    } else {
                        this.use(available.get(0));
                        available.remove(0);
                    }
                }
            }
        } else {
            if ((boolean) this.getVal("Random soup")) {
                int chosen = (int) Random.simpleRandom(0, available.size() - 1);

                this.use(available.get(chosen));
            } else {
                this.use(available.get(0));
            }
        }
    }

    /**
     * Get rod.
     */
    private void rod() {
        ArrayList<Integer> available = this.loop(Type.ROD);

        if (available.isEmpty()) {
            return;
        }

        if ((boolean) this.getVal("Random rod")) {
            int chosen = (int) Random.simpleRandom(0, available.size() - 1);

            this.use(available.get(chosen));
        } else {
            this.use(available.get(0));
        }
    }

    /**
     * Get block.
     */
    private void block() {
        ArrayList<Integer> available = this.loop(Type.BLOCK);

        if (available.isEmpty()) {
            return;
        }

        this.mcInstance.thePlayer.inventory.currentItem = available.get(0);
    }

    /**
     * Searches for available hotkey slots.
     * @param type  the type of item to search for.
     * @return      the arraylist of available slots.
     */
    private ArrayList<Integer> loop(final Type type) {
        ArrayList<Integer> available = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            if (type == Type.POTION) {
                if (this.mcInstance.thePlayer.inventory.getStackInSlot(i) != null) {
                    if (this.mcInstance.thePlayer.inventory.getStackInSlot(i).getDisplayName().equalsIgnoreCase("SPLASH POTION OF HEALING")) {
                        available.add(i);
                    }
                }
            } else if (type == Type.SOUP) {
                if (this.mcInstance.thePlayer.inventory.getStackInSlot(i) != null) {
                    if (this.mcInstance.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemSoup) {
                        available.add(i);
                    }
                }
            } else if (type == Type.ROD) {
                if (this.mcInstance.thePlayer.inventory.getStackInSlot(i) != null) {
                    if (this.mcInstance.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemFishingRod) {
                        available.add(i);
                    }
                }
            } else if (type == Type.BLOCK) {
                if (this.mcInstance.thePlayer.inventory.getStackInSlot(i) != null) {
                    if (this.mcInstance.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) {
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

                Thread.sleep((int) Random.simpleRandom(10, 20));

                this.mcInstance.thePlayer.inventory.currentItem = initialSlot;
            } catch (Exception ignored) { }
        }).start();
    }

    /**
     * Types of items.
     */
    private enum Type {
        POTION,
        SOUP,
        ROD,
        BLOCK;
    }
}
