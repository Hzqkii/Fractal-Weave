package xyz.flapjack.fractal.interfaces.clickgui.components;

/* Custom. */
import xyz.flapjack.fractal.interfaces.clickgui.components.impl.MenuComponent;
import xyz.flapjack.fractal.interfaces.clickgui.components.impl.Frame;
import xyz.flapjack.fractal.render.font.FontRenderer;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.Fractal;
import xyz.flapjack.Access;

public class UIComponent extends Access {
    public boolean enabled;
    public boolean hovered = false;

    public Frame frameOwner;
    public MenuComponent menuOwner;

    public int[] offsets;

    public int iteration;
    public int width;
    public int height;
    public int posX;
    public int posY;

    protected String extension;

    public Setting setting;

    /**
     * Creates a UI component.
     * @param width     the width.
     * @param height    the height.
     * @param posX      the posX.
     * @param posY      the posY.
     */
    public UIComponent(final int width, final int height, final int posX, final int posY) {
        this.width = width;
        this.height = height;
        this.posX = posX;
        this.posY = posY;
    }

    public UIComponent(final int width, final int height, final MenuComponent menuOwner, final int iteration) {
        this(width, height, 0, 0);

        this.menuOwner = menuOwner;
        this.iteration = iteration;
    }

    public UIComponent(final int width, final int height, final Frame frameOwner, final int iteration) {
        this(width, height, 0, 0);

        this.frameOwner = frameOwner;
        this.iteration = iteration;
    }

    /**
     * Renders the effect on the screen.
     * @param alpha the alpha value to display.
     */
    public void render(final float alpha) {
        return;
    };

    /**
     * Updates the position of the UI component.
     * @param posX the posX.
     * @param posY the posY.
     */
    public void update(final int posX, final int posY, final boolean opened, final int offset) {
        return;
    };

    /**
     * Key press event.
     * @param char1 the character pressed.
     * @param key   the key int pressed.
     */
    public void keyPressed(final char char1, final int key) {
        return;
    };

    /**
     * Detects a mouse press.
     * @param posX      the position of the mouse.
     * @param posY      the position of the mouse.
     * @param button    the button pressed.
     */
    public void mouseDown(final int posX, final int posY, final int button) {
        return;
    };

    /**
     * Detects a mouse release.
     * @param posX      the position of the mouse.
     * @param posY      the position of the mouse.
     * @param button    the button pressed.
     */
    public void mouseUp(final int posX, final int posY, final int button) {
        return;
    };

    /**
     * Renders the effect on the screen.
     * @param alpha the alpha value to display.
     */
    public void renderShadow(final float alpha) {
        return;
    }

    /**
     * Sets the position of the UI component.
     * @param posX the posX.
     * @param posY the posY.
     */
    public void setPos(final int posX, final int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Detects whether the mouse is over the component.
     * @param posX  the mouse X position.
     * @param posY  the mouse Y position.
     * @return      a boolean whether the mouse os over.
     */
    public boolean over(final int posX, final int posY) {
        if (this.setting != null) {
            if (this.setting.condition != null) {
                if (!this.setting.condition.booleanValue) {
                    return false;
                }
            }
        }

        boolean over = (posX > this.posX &&
                posY > (this.posY) &&
                posX < (this.width + this.posX) &&
                posY < (this.height + this.posY));

        if (over & this.extension != null && (boolean) Instance.getModule("Click GUI").getVal("Tooltips")) {
            this.menuOwner.frameOwner.owner.tooltip.set(this.extension, posX, posY);
        }

        return over;
    }

    /**
     * Detects whether the mouse is over the component, with specified positional arguments.
     * @param posX      the mouse X position.
     * @param posY      the mouse Y position.
     * @param itemPosX  the item X position.
     * @param itemPosY  the item Y position.
     * @param width     the item width.
     * @param height    the item height.
     * @return          a boolean whether the mouse os over.
     */
    public boolean over(final int posX, final int posY, final int itemPosX, final int itemPosY, final int width, final int height) {
        if (this.setting != null) {
            if (this.setting.condition != null) {
                if (!this.setting.condition.booleanValue) {
                    return false;
                }
            }
        }

        boolean over = (posX > itemPosX &&
                posY > (itemPosY) &&
                posX < (width + itemPosX) &&
                posY < (height + itemPosY));

        if (over & this.extension != null && (boolean) Instance.getModule("Click GUI").getVal("Tooltips")) {
            this.menuOwner.frameOwner.owner.tooltip.set(this.extension, posX, posY);
        }

        return over;
    }

    /**
     * Resets the module.
     */
    public void reset() {
        return;
    }

    /**
     * Triggers the module to update itself based on the new settings.
     */
    public void config() {
        return;
    }

    /**
     * Gets the FontRenderer instance.
     * @param size  the font size.
     * @return      the FontRenderer.
     */
    protected FontRenderer getFont(final int size) {
        return Fractal.INSTANCE.getFontManager().getFontRenderer("Montserrat " + size);
    }
}
