package xyz.flapjack.fractal.interfaces.clickgui.components.impl;

/* Custom. */
import xyz.flapjack.fractal.interfaces.clickgui.components.UIComponent;
import xyz.flapjack.fractal.render.main.Simple;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.Fractal;

/* Open. */
import java.awt.*;

public class ToggleComponent extends UIComponent {
    public static int iWidth = 115;
    public static int iHeight = 20;
    public static Color standardColour = new Color(17, 17, 17);
    public static Color hoverColour = new Color(35, 35, 41);
    public static Color circleFieldColour = new Color(30, 30, 30);

    public String title;

    public boolean enabled;

    private Color colour;

    public ToggleComponent(final MenuComponent menuOwner, final int iteration, final Setting setting) {
        super(iWidth, iHeight, menuOwner, iteration);

        this.setting = setting;

        this.title = setting.getName();
        this.enabled = setting.booleanValue;

        this.colour = standardColour;
        this.height = iHeight;
        this.width = iWidth;

        this.extension = setting.extension;
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

        if (this.setting.condition == null) {
            return;
        }

        if (this.setting.condition.booleanValue) {
            this.menuOwner.offsets[this.iteration] = this.height;
        } else {
            this.menuOwner.offsets[this.iteration] = 0;
        }
    }

    @Override
    public void mouseDown(int posX, int posY, int button) {
        if (this.hovered) {
            this.enabled = !this.enabled;
            this.setting.toggle();
        }
    }

    public void draw(final float alpha) {
        Color colour = new Color(this.colour.getRed(), this.colour.getGreen(), this.colour.getBlue(), Math.round(255 * alpha));

        if (this.iteration == this.menuOwner.offsets.length - 1) {
            Simple.drawRoundedRect(this.posX, this.posY, this.width, this.height, colour.getRGB(), (int) Instance.getModule("Click GUI").getVal("Border radius"), Simple.Rect.BASE);
        } else {
            Simple.drawRect(this.posX, this.posY, this.width, this.height, colour.getRGB());
        }

        if (this.hovered) {
            colour = new Color(standardColour.getRed(), standardColour.getGreen(), standardColour.getBlue(), Math.round(255 * alpha));
        } else {
            colour = new Color(circleFieldColour.getRed(), circleFieldColour.getGreen(), circleFieldColour.getBlue(), Math.round(255 * alpha));
        }

        Simple.drawRect(this.posX + this.width - 10, this.posY + 7, 5, 6, colour.getRGB());
        Simple.drawCircle(this.posX + this.width - 13, this.posY + 8, 3, colour.getRGB());
        Simple.drawCircle(this.posX + this.width - 8, this.posY + 8, 3, colour.getRGB());

        Fractal instance = Instance.getInstance();
        if (this.enabled) {
            colour = new Color(instance.themeColour.getRed(), instance.themeColour.getGreen(), instance.themeColour.getBlue(), Math.round(255 * alpha));
            Simple.drawCircle(this.posX + this.width - 7, this.posY + 9, 2, colour.getRGB());
        } else {
            if (!this.hovered) {
                colour = new Color(standardColour.getRed(), standardColour.getGreen(), standardColour.getBlue(), Math.round(255 * alpha));
            } else {
                colour = new Color(circleFieldColour.getRed(), circleFieldColour.getGreen(), circleFieldColour.getBlue(), Math.round(255 * alpha));
            }

            Simple.drawCircle(this.posX + this.width - 12, this.posY + 9, 2, colour.getRGB());
        }

        colour = new Color(255, 255, 255, Math.round(255 * alpha));
        Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").drawString(this.title, this.posX + 5, this.posY + 7, colour.getRGB());
    }

    @Override
    public void config() {
        this.enabled = this.setting.booleanValue;
    }
}
