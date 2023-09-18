package xyz.flapjack.fractal.modules;

/* Custom. */
import xyz.flapjack.Access;

/* Open. */
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Module extends Access {
    public ArrayList<Setting> settings = new ArrayList<>();

    protected Minecraft mcInstance = Minecraft.getMinecraft();

    public String title;
    public String description;
    public String component;
    public String type;

    public Category category;
    public boolean enabled;

    public int keyBind;
    public String displayBind;

    /**
     * Creates a module instance.
     * @param title         the title of the module for display and instance referencing.
     * @param description   the description of the module.
     * @param category      the category of the module.
     * @param component     the type of component for the module.
     * @param type          the type of module.
     */
    public Module(final String title, final String description, final Category category, final String component, final String type) {
        this.title = title;
        this.description = description;
        this.component = component;
        this.type = type;

        this.category = category;
        this.enabled = false;

        this.displayBind = "None";

        Access.Instance.getEventBus().subscribe(this);
    }

    /**
     * Gets a private setting value from the target module.
     * @param property  the property name.
     * @return          the value of the property.
     */
    public Object getVal(final String property) {
        for (Setting setting: this.settings) {
            if (setting.getName().equalsIgnoreCase(property)) {
                switch (setting.getType()) {
                    case "toggle" -> {
                        return setting.booleanValue;
                    }
                    case "input" -> {
                        return setting.stringValue;
                    }
                    case "slider-i" -> {
                        return setting.intValue;
                    }
                    case "slider-d" -> {
                        return setting.doubleValue;
                    }
                    case "dropdown" -> {
                        return setting.optionValue;
                    }
                    case "bind" -> {
                        return setting.keyBind;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Sets a module property.
     * @param property      the target property.
     * @param value         the target value.
     */
    public void setVal(final String property, final String value) {
        try {
            for (Setting setting: this.settings) {
                if (setting.getName().equals(property)) {
                    switch (setting.getType()) {
                        case "toggle" -> {
                            setting.booleanValue = Boolean.parseBoolean(value);
                        }
                        case "input" -> {
                            setting.stringValue = value;
                        }
                        case "slider-i" -> {
                            setting.intValue = Integer.parseInt(value);
                        }
                        case "slider-d" -> {
                            setting.doubleValue = Double.parseDouble(value);
                        }
                        case "dropdown" -> {
                            setting.optionValue = value;
                        }
                        case "bind" -> {
                            setting.keyBind = Integer.parseInt(value);
                            setting.displayBind = value;
                        }
                    }
                }
            }

        } catch (Exception ignored) { }
    }

    /**
     * Gets a private value.
     * @param property  the target value name.
     * @return          the value.
     */
    public Object getPriv(final String property) {
        try {
            Field item = this.getClass().getDeclaredField(property);
            item.setAccessible(true);

            return (Object) item.get(this);
        } catch (Exception ignored) {
            return new Object();
        }
    }

    /**
     * Safe class for aim assist enemy.
     * @return the target enemy.
     */
    public EntityPlayer getPrivateEnemy() {
        return null;
    }

    /**
     * Only for modules with running threads.
     */
    public void init() {
        return;
    }

    /**
     * Toggles the module state.
     */
    public void toggle() {
        this.enabled = !this.enabled;
    }

    /**
     * Gets a module by title.
     * @param title the title of the module.
     * @return      the module.
     */
    public static Module getModule(final String title) {
        return Instance.getInstance().getModuleManager().getModule(title);
    }

    /**
     * Register a new setting.
     * @param setting the setting.
     */
    protected void registerSetting(final Setting setting) {
        this.settings.add(setting);
    }

    /**
     * Performs mass check algorithms.
     * @param checks    the checks to perform.
     * @return          the result of the checks.
     */
    protected boolean massCheck(final ArrayList<Boolean> checks) {
        for (boolean bool: checks) {
            if (!bool) {
                return true;
            }
        }

        return false;
    }

    /**
     * Performs mass check algorithms.
     * @param checks    the checks to perform.
     * @return          the result of the checks.
     */
    protected int massCheck(final HashMap<Boolean, Boolean> checks, final String s) {
        int incorrect = 0;

        for (Map.Entry<Boolean, Boolean> entry : checks.entrySet()) {
            Boolean check = entry.getKey();
            Boolean key = entry.getValue();

            if (check != key) {
                incorrect++;
            }
        }

        return incorrect;
    }

    /**
     * Gets a setting object from the title.
     * @param target    the setting title.
     * @return          the setting.
     */
    public Setting getSetting(final String target) {
        for (Setting setting: this.settings) {
            if (setting.getName().equalsIgnoreCase(target)) {
                return setting;
            }
        }

        return null;
    }
}
