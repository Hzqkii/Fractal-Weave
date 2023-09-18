package xyz.flapjack.fractal.modules.impl.world;

/* Custom. */
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;

public class Antibot extends Module {
    public Antibot() {
        super("Antibot", "Blacklists bot from modules.", Category.World, "menu", "bindable");

        this.registerSetting(new Setting("Mode", this, null, new String[] {"Hypixel"}));
    }
}