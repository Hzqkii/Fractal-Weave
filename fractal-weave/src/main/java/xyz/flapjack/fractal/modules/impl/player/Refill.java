package xyz.flapjack.fractal.modules.impl.player;

/* Custom. */
import xyz.flapjack.fractal.modules.impl.util.Random;
import xyz.flapjack.fractal.events.impl.TickEvent;
import xyz.flapjack.fractal.events.Subscribed;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;

/* Open. */
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSoup;
import java.util.ArrayList;

public class Refill extends Module {
    private boolean refilling = false;

    public Refill() {
        super("Refill", "A heal system.", Category.Player, "menu", "bindable");

        this.registerSetting(new Setting("Potions", this, null, true));
        this.registerSetting(new Setting("Soups", this, null, true));

        this.registerSetting(new Setting("Delay", this, null, 45, 20, 500));
    }

    @Subscribed(eventType = TickEvent.class)
    public void clientTick(final TickEvent event) {
        if (this.mcInstance.thePlayer == null) {
            return;
        }

        /*
         * These checks are helled back, as they require thePlayer to not be null.
         */
        if (!this.enabled) {
            return;
        }

        if (!((this.mcInstance.currentScreen != null) && ((this.mcInstance.thePlayer.inventoryContainer != null) && (this.mcInstance.thePlayer.inventoryContainer instanceof ContainerPlayer) && (this.mcInstance.currentScreen instanceof GuiInventory)))) {
            return;
        }

        if (this.refilling) {
            return;
        }

        this.refill(this.getAmount());
    }

    /**
     * Gets the amount of soup needed.
     * @return the amount of soup.
     */
    private int getAmount() {
        int slots = 0;

        for (int i = 0; i< 9; i++) {
            if (this.mcInstance.thePlayer.inventory.getStackInSlot(i) == null) {
                slots++;
            }
        }

        return slots;
    }

    /**
     * Refills the players hotbar.
     * @param amount the target amount of soup.
     */
    private void refill(int amount) {
        if (amount == 0) {
            return;
        }

        ArrayList<Integer> slots = new ArrayList<>();
        ContainerPlayer inventory = (ContainerPlayer) this.mcInstance.thePlayer.openContainer;

        for (int i = 0; i < inventory.getInventory().size(); i++) {
            if (amount == 0) {
                break;
            }

            ItemStack item = inventory.getInventory().get(i);

            if (item != null) {
                if (!((i >= 36) && (i <= 44))) {
                    if ((boolean) this.getVal("Potions")) {
                        if (item.getItem() instanceof ItemPotion) {
                            slots.add(i);
                            amount--;
                        }
                    } else if ((boolean) this.getVal("Soups")) {
                        if (item.getItem() instanceof  ItemSoup) {
                            slots.add(i);
                            amount--;
                        }
                    }
                }
            }
        }

        this.refilling = true;

        new Thread(() -> {
            try {
                for (int slot: slots) {
                    this.mcInstance.playerController.windowClick(this.mcInstance.thePlayer.openContainer.windowId,
                            slot,
                            0,
                            1,
                            this.mcInstance.thePlayer);

                    Thread.sleep((long) Random.nextRandom((int) this.getVal("Delay") - 10, (int) this.getVal("Delay") + 10));
                }

                this.refilling = false;

            } catch (Exception ignored) { }
        }).start();
    }
}
