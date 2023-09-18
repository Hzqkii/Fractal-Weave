package xyz.flapjack.fractal.modules;

/* Custom. */
import xyz.flapjack.fractal.interfaces.clickgui.components.HUDComponent;
import xyz.flapjack.fractal.modules.impl.movement.*;
import xyz.flapjack.fractal.modules.impl.settings.*;
import xyz.flapjack.fractal.modules.impl.combat.*;
import xyz.flapjack.fractal.modules.impl.player.*;
import xyz.flapjack.fractal.modules.impl.visual.*;
import xyz.flapjack.fractal.modules.impl.world.*;
import xyz.flapjack.Access;

/* Open. */
import java.util.ArrayList;

public class ModuleManager {
    public ArrayList<Module> modules = new ArrayList<>();
    public ArrayList<HUDComponent> hudComponents = new ArrayList<>();

    public ModuleManager() {
        addModule(new AimAssist());
        addModule(new AutoClicker());
        addModule(new Blockhit());
        addModule(new DoubleClicker());
        addModule(new Reach());
        addModule(new Velocity());
        addModule(new WTap());

        addModule(new Sprint());
        addModule(new Strafe());

        addModule(new AutoHeal());
        addModule(new AutoPlace());
        addModule(new BridgeAssist());
        addModule(new ClickTweak());
        addModule(new Hotkey());
        addModule(new MLG());
        addModule(new Refill());
        addModule(new RightClicker());

        addModule(new ClickInterface());
        addModule(new Configs());

        addModule(new AntiDebuff());
        addModule(new ClientArrayList());
        addModule(new Chams());
        addModule(new ESP());
        addModule(new Health());

        addModule(new Antibot());
    }

    /**
     * Start init after client init.
     */
    public void init() {
        for (Module module: this.modules) {
            module.init();
        }
    }

    /**
     * Gets all the modules in the category.
     * @param category the category.
     * @return the modules in the category.
     */
    public ArrayList<Module> getModules(final Access.Category category) {
        ArrayList<Module> returnModules = new ArrayList<>();

        for (Module module: modules) {
            if (module.category.equals(category)) {
                returnModules.add(module);
            }
        }

        return returnModules;
    }

    /**
     * Gets the module dependant on its title.
     * @param title the module title.
     * @return the module.
     */
    public Module getModule(final String title) {
        for (Module module: modules) {
            if (module.title.equalsIgnoreCase(title)) {
                return module;
            }
        }

        return null;
    }

    /**
     * Gets a HUDComponent based on module.
     * @param module the module.
     * @return the HUDComponent.
     */
    public HUDComponent getHudComponent(final Module module) {
        for (HUDComponent component: this.hudComponents) {
            if (component.module.equalsIgnoreCase(module.title)) {
                return component;
            }
        }

        return null;
    }

    /**
     * Adds a module to the list of modules.
     * @param module the module to add.
     */
    private void addModule(final Module module) {
        modules.add(module);
    }
}
