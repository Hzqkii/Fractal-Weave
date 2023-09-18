package xyz.flapjack.fractal.modules.impl.settings;

/* Custom. */

import xyz.flapjack.fractal.modules.Module;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.impl.settings.impl.hud.Edit;

public class Debug extends Module {
    public Debug() {
        super("Debug", "Client debug option.", Category.Settings, "menu", "none");

        this.registerSetting(new Setting("Output system", this, null, new Edit()));
    }

    @Override
    public void init() {
        /*
         * Todo: Finish HUD editor.
         */
    }
}
