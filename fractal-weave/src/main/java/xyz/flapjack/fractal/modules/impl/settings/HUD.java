package xyz.flapjack.fractal.modules.impl.settings;

/* Custom. */
import xyz.flapjack.fractal.modules.impl.settings.impl.hud.Edit;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;

public class HUD extends Module {
    public HUD() {
        super("HUD", "In-game display.", Category.Settings, "menu", "none");

        this.registerSetting(new Setting("Edit layout", this, null, new Edit()));
    }

    @Override
    public void init() {
        /*
         * Todo: Finish HUD editor.
         */
    }
}
