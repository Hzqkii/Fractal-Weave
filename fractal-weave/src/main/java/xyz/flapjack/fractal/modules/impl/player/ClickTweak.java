package xyz.flapjack.fractal.modules.impl.player;

/* Custom. */
import net.minecraft.item.ItemBlock;
import xyz.flapjack.fractal.events.impl.MouseEvent;
import xyz.flapjack.fractal.events.Subscribed;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;

/* Open. */
import java.util.ArrayList;

public class ClickTweak extends Module {
    public ClickTweak() {
        super("Click Tweaks", "Removes click delay.", Category.Player, "menu", "bindable");

        Setting fastplace = new Setting("Fastplace", this, null, false);
        this.registerSetting(fastplace);
        this.registerSetting(new Setting("Right delay", this, fastplace, 1, 0, 4));
        this.registerSetting(new Setting("Only on blocks", this, fastplace, true));

        Setting clickDelay = new Setting("Click delay", this, null, true);
        this.registerSetting(clickDelay);
        this.registerSetting(new Setting("Left delay", this, clickDelay, 0, 0, 10));
    }

    @Subscribed(eventType = MouseEvent.class)
    public void onMouseEvent(final MouseEvent event) {
        ArrayList<Boolean> checks = new ArrayList<>();
        checks.add(this.mcInstance.inGameHasFocus);
        checks.add(this.enabled);

        if (this.massCheck(checks)) {
            return;
        }

        if ((boolean) getVal("Fastplace")) {
            if ((boolean) this.getVal("Only on blocks")) {
                if (this.mcInstance.thePlayer.getCurrentEquippedItem() != null) {
                    if (this.mcInstance.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock) {
                        event.rightClickDelay = (int) getVal("Right delay");
                    }
                }
            } else {
                event.rightClickDelay = (int) getVal("Right delay");
            }
        }

        if ((boolean) getVal("Click delay")) {
            event.leftClickCounter = (int) getVal("Left delay");
        }
    }
}
