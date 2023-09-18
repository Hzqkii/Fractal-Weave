package xyz.flapjack.fractal.modules.impl.visual;

/* Custom. */
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;

public class AntiDebuff extends Module {
    public AntiDebuff() {
        super("Anti Debuff", "Remove all visual effects.", Category.Visual, "menu", "bindable");

        this.registerSetting(new Setting("Effect", this, null, new String[] {"Everything", "Blindness", "Nausea"}));
    }
}
