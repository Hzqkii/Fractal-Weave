package xyz.flapjack.fractal.modules.impl.settings.impl.hud;

/* Custom. */
import xyz.flapjack.fractal.modules.impl.settings.impl.Method;
import xyz.flapjack.fractal.modules.Module;

public class Edit extends Method {
    @Override
    public void execute(Module module) {
        Instance.getInstance().getClickGui().triggerLayout();
    }
}
