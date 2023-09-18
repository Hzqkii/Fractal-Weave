package xyz.flapjack.fractal.interfaces.clickgui.components.impl;

/* Custom. */
import org.lwjgl.input.Keyboard;
import xyz.flapjack.fractal.interfaces.clickgui.components.UIComponent;
import xyz.flapjack.fractal.render.main.Simple;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;

/* Open. */
import java.awt.*;

public class SubBindComponent extends UIComponent {
    public static int iWidth = 115;
    public static int iHeight = 15;
    public static Color textColour = new Color(180, 180, 180);
    public static Color standardColour = new Color(17, 17, 17);
    public static Color hoverColour = new Color(30, 30, 30);

    public Module module;

    public String displayText;
    private boolean selected;
    private Color colour;

    public SubBindComponent(final MenuComponent menuOwner, final int iteration, final Module module, final Setting setting) {
        super(iWidth, iHeight, menuOwner, iteration);

        this.module = module;
        this.setting = setting;

        this.displayText = "Trigger: ";
        this.colour = standardColour;

        this.height = iHeight;
        this.width = iWidth;
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

        this.colour = standardColour;
        if (this.hovered) {
            this.colour = hoverColour;
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
    public void keyPressed(char char1, int key) {
        if (!this.selected) {
            return;
        }

        if (key == 14 || key == 42) {
            this.setting.displayBind = "None";
            this.setting.keyBind = 0;
        } else {
            this.setting.displayBind = String.valueOf(char1).toUpperCase();
            this.setting.keyBind = Keyboard.getEventKey();
        }
        this.selected = false;
    }

    @Override
    public void mouseDown(int posX, int posY, int button) {
        this.selected = button == 0 && this.hovered;
    }

    public void draw(final float alpha) {
        Color colour = new Color(this.colour.getRed(), this.colour.getGreen(), this.colour.getBlue());

        if (this.iteration == this.menuOwner.offsets.length - 1) {
            Simple.drawRoundedRect(this.posX, this.posY, this.width, this.height, colour.getRGB(), (int) Instance.getModule("Click GUI").getVal("Border radius"), Simple.Rect.BASE);
        } else {
            Simple.drawRect(this.posX, this.posY, this.width, this.height, colour.getRGB());
        }

        colour = new Color(textColour.getRed(), textColour.getGreen(), textColour.getBlue());

        Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").drawString(this.setting.getName(), this.posX + 5, this.posY + 4, colour.getRGB());
        Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").drawString(this.displayText + this.setting.displayBind, this.posX + iWidth - (5 + Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").getWidth(this.displayText + this.setting.displayBind)), this.posY + 4, colour.getRGB());
    }
}
