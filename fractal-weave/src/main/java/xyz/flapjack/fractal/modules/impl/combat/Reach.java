package xyz.flapjack.fractal.modules.impl.combat;

/* Custom. */
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;

public class Reach extends Module {
    public Reach() {
        super("Reach", "Modifies the default reach.", Category.Combat, "menu", "bindable");

        this.registerSetting(new Setting("Min distance", this, null, 3D, 3D, 6D));
        this.registerSetting(new Setting("Max distance", this, null, 3D, 3D, 6D));

        this.registerSetting(new Setting("Chance", this, null, 50, 1, 100));
        this.registerSetting(new Setting("Condition", this, null, new String[] {"Global", "Strafing", "Sprinting", "Moving"}));

        Setting hitbox = new Setting("Hitbox", this, null, false);
        this.registerSetting(hitbox);
        this.registerSetting(new Setting("Expand amount", this, hitbox, 0, 0, 100));
    }
}
