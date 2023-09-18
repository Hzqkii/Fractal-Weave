package xyz.flapjack.fractal.interfaces.clickgui.components.impl;

/* Custom. */
import xyz.flapjack.fractal.interfaces.clickgui.components.UIComponent;
import xyz.flapjack.fractal.render.main.Simple;
import xyz.flapjack.fractal.modules.Setting;

/* Open. */
import java.util.ArrayList;
import java.awt.*;

public class DropdownComponent extends UIComponent {
    public static int iWidth = 95;
    public static int iHeight = 10;
    public static Color regularColour = new Color(17, 17, 17);
    public static Color standardColour = new Color(22, 22, 25);
    public static Color hoverColour = new Color(35, 35, 41);
    public static Color textHoverColour = new Color(255, 255, 255);
    public static Color menuComponentBackground = new Color(17, 17, 17);

    public String title;
    public ArrayList<String> options;
    public String currentOption;
    public String hoveredOption;

    public boolean opened;
    public int lastMouseX;
    public int lastMouseY;
    public int lastOffset;

    private Color colour;

    public DropdownComponent(final MenuComponent menuOwner, final int iteration, final Setting setting) {
        super(iWidth, iHeight, menuOwner, iteration);

        this.setting = setting;

        this.title = setting.getName();
        this.options = setting.options;
        this.currentOption = setting.optionValue;
        this.hoveredOption = setting.optionValue;
        this.opened = false;

        this.colour = standardColour;
        this.height = iHeight;
        this.width = iWidth;

        if (iteration != 0) {
            this.lastOffset = this.menuOwner.offsets[iteration - 1];
        }
    }

    @Override
    public void render(float alpha) {
        if (this.setting.condition == null) {
            this.draw(alpha);

            return;
        }

        if (this.setting.condition.booleanValue) {
            this.draw(alpha);
        }
    }

    @Override
    public void update(int posX, int posY, boolean opened, int offset) {
        this.lastMouseX = posX;
        this.lastMouseY = posY;

        int selfOffset = MenuComponent.iHeight;

        for (int i = 0; i < this.iteration; i++) {
            selfOffset += this.menuOwner.offsets[i];
        }

        setPos(this.menuOwner.posX, this.menuOwner.posY + selfOffset);
        this.hovered = over(posX, posY);

        if (this.hovered) {
            this.colour = hoverColour;
        } else {
            this.colour = standardColour;
        }

        this.options = this.setting.options;

        if (this.opened) {
            this.height = this.options.size() * iHeight + 15;
        } else {
            this.height = iHeight + 4;
        }

        if (this.setting.condition == null) {
            this.menuOwner.offsets[this.iteration] = this.height + 5;

            return;
        }

        if (this.setting.condition.booleanValue) {
            this.menuOwner.offsets[this.iteration] = this.height + 5;

            if (this.iteration != 0) {
                this.menuOwner.offsets[this.iteration - 1] = this.lastOffset + 5;
            }
        } else {
            this.menuOwner.offsets[this.iteration] = 0;

            if (this.iteration != 0) {
                this.menuOwner.offsets[this.iteration - 1] = this.lastOffset;
            }
        }
    }

    @Override
    public void mouseDown(int posX, int posY, int button) {
        if (this.setting.condition != null) {
            if (this.over(posX, posY) && this.setting.condition.booleanValue) {
                if (!this.hoveredOption.equals(this.currentOption) && button == 0) {
                    this.currentOption = this.hoveredOption;
                }

                this.opened = !this.opened;
            }
        } else {
            if (this.over(posX, posY)) {
                if (!this.hoveredOption.equals(this.currentOption) && button == 0) {
                    this.currentOption = this.hoveredOption;
                }

                this.opened = !this.opened;
            }
        }

        this.setting.optionValue = this.currentOption;
    }

    @Override
    public void config() {
        this.currentOption = this.setting.optionValue;
    }

    /**
     * Renders this component to the screen.
     * @param alpha the alpha value of the GUI.
     */
    public void draw(final float alpha) {
        int offset = 3;

        Color colour = new Color(regularColour.getRed(), regularColour.getGreen(), regularColour.getBlue(), Math.round(255 * alpha));
        if (this.iteration == this.menuOwner.offsets.length - 1) {
            Simple.drawRoundedRect(this.posX, this.posY - (this.menuOwner.settings.get(this.iteration - 1) instanceof ToggleComponent ? 5 : 0), this.width + 20, this.height + 10 + offset, colour.getRGB(), (int) Instance.getModule("Click GUI").getVal("Border radius"), Simple.Rect.BASE);
        } else {
            Simple.drawRect(this.posX, this.posY - (this.menuOwner.settings.get(this.iteration - 1) instanceof ToggleComponent ? 5 : 0), this.width + 20, this.height + 10 + offset, colour.getRGB());
        }

        if (this.opened) {
            this.colour = hoverColour;
            this.hoveredOption = this.currentOption;

            colour = new Color(this.colour.getRed(), this.colour.getGreen(), this.colour.getBlue(), Math.round(255 * alpha));
            Simple.drawRoundedRect(this.posX + 10, this.posY + offset, this.width, this.height, colour.getRGB(), 5, Simple.Rect.ALL);

            colour = new Color(menuComponentBackground.getRed(), menuComponentBackground.getGreen(), menuComponentBackground.getBlue(), Math.round(255 * alpha));
            Simple.drawRoundedRect(this.posX + 11, this.posY + 1 + offset, this.width - 2, this.height - 2, colour.getRGB(), 5, Simple.Rect.ALL);

            int iter = 0;

            Simple.endScissor();

            Simple.beginScissor(this.menuOwner.frameOwner.scissor[0] + 10, this.menuOwner.frameOwner.scissor[1], this.menuOwner.frameOwner.scissor[2] - 10, this.menuOwner.frameOwner.scissor[3]);

            colour = new Color(180, 180, 180);
            Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").drawStringWithShadow(this.title + " - " + this.currentOption, this.posX + 15, this.posY + iter + 4 + offset, colour.getRGB());

            iter += 10;

            for (String option: this.options) {
                colour = new Color(180, 180, 180);

                if (this.hovered && this.lastMouseY > this.posY + iter + 2 + offset && this.lastMouseY < this.posY + iter + 12 + offset) {
                    colour = textHoverColour;
                    this.hoveredOption = option;
                }

                Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").drawStringWithShadow(option, this.posX + 15, this.posY + iter + 4 + offset, colour.getRGB());

                iter += 10;
            }

            Simple.endScissor();
            Simple.beginScissor(this.menuOwner.frameOwner.scissor[0], this.menuOwner.frameOwner.scissor[1], this.menuOwner.frameOwner.scissor[2], this.menuOwner.frameOwner.scissor[3]);
        } else {
            colour = new Color(this.colour.getRed(), this.colour.getGreen(), this.colour.getBlue(), Math.round(255 * alpha));
            Simple.drawRoundedRect(this.posX + 10, this.posY + offset, this.width, this.height, colour.getRGB(), 5, Simple.Rect.ALL);

            colour = new Color(menuComponentBackground.getRed(), menuComponentBackground.getGreen(), menuComponentBackground.getBlue(), Math.round(255 * alpha));
            Simple.drawRoundedRect(this.posX + 11, this.posY + 1 + offset, this.width - 2, this.height - 2, colour.getRGB(), 5, Simple.Rect.ALL);

            colour = new Color(255, 255, 255);
            Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").drawStringWithShadow(this.title + " - " + this.currentOption, this.posX + 15, this.posY + 4 + offset, colour.getRGB());
        }
    }
}
