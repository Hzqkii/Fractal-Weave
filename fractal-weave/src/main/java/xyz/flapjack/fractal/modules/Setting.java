package xyz.flapjack.fractal.modules;

/* Custom. */
import xyz.flapjack.fractal.modules.impl.settings.impl.Method;

/* Open. */
import java.util.ArrayList;
import java.util.Arrays;

public class Setting {
    protected String name;
    protected String type;

    public Module owner;
    public Setting condition;

    public boolean booleanValue;
    public String stringValue;
    public ArrayList<String> options;
    public String optionValue;

    public int intValue;
    public int intMax;
    public int intMin;

    public double doubleValue;
    public double doubleMax;
    public double doubleMin;

    public int keyBind;
    public String displayBind;
    public int iteration;

    public Method method;

    public String extension;

    /**
     * Construct a setting instance.
     * @param name the name of the setting.
     * @param moduleOwner the owner of the setting.
     * @param condition the condition of the setting.
     */
    public Setting(final String name, final Module moduleOwner, final Setting condition) {
        this.name = name;
        this.owner = moduleOwner;
        this.condition = condition;
    }

    /*
    Button type.
     */
    public Setting(final String name, final Module moduleOwner, final Setting condition, final Method method) {
        this(name, moduleOwner, condition);

        this.type = "button";
        this.method = method;
    }
    public Setting(final String name, final String description, final Module moduleOwner, final Setting condition, final Method method) {
        this(name, moduleOwner, condition, method);

        this.extension = description;
    }

    /*
    Switch type.
     */
    public Setting(final String name, final Module moduleOwner, final Setting condition, final boolean booleanValue) {
        this(name, moduleOwner, condition);

        this.type = "toggle";
        this.booleanValue = booleanValue;
    }
    public Setting(final String name, final String description, final Module moduleOwner, final Setting condition, final boolean booleanValue) {
        this(name, moduleOwner, condition, booleanValue);

        this.extension = description;
    }

    /*
    Text input type.
     */
    public Setting(final String name, final Module moduleOwner, final Setting condition, final String stringValue) {
        this(name, moduleOwner, condition);

        this.type = "input";
        this.stringValue = stringValue;
    }
    public Setting(final String name, final String description, final Module moduleOwner, final Setting condition, final String stringValue) {
        this(name, moduleOwner, condition, stringValue);

        this.extension = description;
    }

    /*
    Integer slider type.
     */
    public Setting(final String name, final Module moduleOwner, final Setting condition, final int intValue, final int intMin, final int intMax) {
        this(name, moduleOwner, condition);

        this.type = "slider-i";
        this.intValue = intValue;
        this.intMin = intMin;
        this.intMax = intMax;
    }
    public Setting(final String name, final String description, final Module moduleOwner, final Setting condition, final int intValue, final int intMin, final int intMax) {
        this(name, moduleOwner, condition, intValue, intMin, intMax);

        this.extension = description;
    }

    /*
     * Double slider type.
     */
    public Setting(final String name, final Module moduleOwner, final Setting condition, final double doubleValue, final double doubleMin, final double doubleMax) {
        this(name, moduleOwner, condition);

        this.type = "slider-d";
        this.doubleValue = doubleValue;
        this.doubleMin = doubleMin;
        this.doubleMax = doubleMax;
    }
    public Setting(final String name, final String description, final Module moduleOwner, final Setting condition, final double doubleValue, final double doubleMin, final double doubleMax) {
        this(name, moduleOwner, condition, doubleValue, doubleMin, doubleMax);

        this.extension = description;
    }

    /*
     * Dropdown type.
     */
    public Setting(final String name, final Module moduleOwner, final Setting condition, final String[] optionalValues) {
        this(name, moduleOwner, condition);

        ArrayList<String> arraylist = new ArrayList<>(Arrays.asList(optionalValues));
        this.type = "dropdown";

        this.options = arraylist;
        this.optionValue = arraylist.get(0);
    }
    public Setting(final String name, final String description, final Module moduleOwner, final Setting condition, final String[] optionalValues) {
        this(name, moduleOwner, condition, optionalValues);

        this.extension = description;
    }

    /*
     * Alternative constructor for dropdown type.
     */
    public Setting(final String name, final Module moduleOwner, final Setting condition, final ArrayList<String> optionalValues) {
        this(name, moduleOwner, condition);
        this.type = "dropdown";

        this.options = optionalValues;

        try {
            this.optionValue = optionalValues.get(0);
        } catch (Exception ignored) {
            this.optionValue = "None";
        }
    }
    public Setting(final String name, final String description, final Module moduleOwner, final Setting condition, final ArrayList<String> optionalValues) {
        this(name, moduleOwner, condition, optionalValues);

        this.extension = description;
    }

    /*
     * Alternative constructor for sub bind type.
     */
    public Setting(final String name, final Module moduleOwner, final Setting condition, final Object object) {
        this(name, moduleOwner, condition);
        this.type = "bind";
        this.displayBind = "None";

        this.iteration = (int) object;
    }

    /**
     * Toggles the component, as long as it's a toggleable type.
     */
    public void toggle() {
        if (this.type.equalsIgnoreCase("toggle")) {
            this.booleanValue = !this.booleanValue;
        }
    }

    /**
     * Returns the name of the setting.
     * @return the name of the setting.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the type of the setting.
     * @return the type of the setting.
     */
    public String getType() {
        return this.type;
    }
}
