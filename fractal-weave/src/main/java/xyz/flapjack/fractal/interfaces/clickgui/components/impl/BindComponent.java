package xyz.flapjack.fractal.interfaces.clickgui.components.impl;

/* Custom. */
import org.lwjgl.input.Keyboard;
import xyz.flapjack.fractal.interfaces.clickgui.components.UIComponent;
import xyz.flapjack.fractal.render.main.Simple;
import xyz.flapjack.fractal.modules.Module;

/* Open. */
import java.awt.*;

public class BindComponent extends UIComponent {
    public static int iWidth = 115;
    public static int iHeight = 15;
    public static Color textColour = new Color(180, 180, 180);
    public static Color standardColour = new Color(17, 17, 17);
    public static Color hoverColour = new Color(30, 30, 30);

    public Module module;
    public String displayText;
    private boolean selected;
    private Color colour;

    public BindComponent(final MenuComponent menuOwner, final int iteration, final Module module) {
        super(iWidth, iHeight, menuOwner, iteration);

        this.module = module;

        this.displayText = (module.type.equals("trigger") ? "Trigger Key: " : "Keybind: ");
        this.colour = standardColour;

        this.height = iHeight;
        this.width = iWidth;
    }

    @Override
    public void render(float alpha) {
        Color colour = new Color(this.colour.getRed(), this.colour.getGreen(), this.colour.getBlue());

        if (this.iteration == this.menuOwner.offsets.length - 1) {
            Simple.drawRoundedRect(this.posX, this.posY, this.width, this.height, colour.getRGB(), (int) Instance.getModule("Click GUI").getVal("Border radius"), Simple.Rect.BASE);
        } else {
            Simple.drawRect(this.posX, this.posY, this.width, this.height, colour.getRGB());
        }

        colour = new Color(textColour.getRed(), textColour.getGreen(), textColour.getBlue());
        Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").drawString(this.displayText + this.module.displayBind, this.posX + 5, this.posY + 4, colour.getRGB());
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
    }

    @Override
    public void keyPressed(char char1, int key) {
        if (!this.selected) {
            return;
        }

        if (key == 14 || key == 42) {
            this.module.displayBind = "None";
            this.module.keyBind = 0;
        } else {
            this.module.displayBind = String.valueOf(char1).toUpperCase();
            this.module.keyBind = Keyboard.getEventKey();
        }
        this.selected = false;
    }

    @Override
    public void mouseDown(int posX, int posY, int button) {
        this.selected = button == 0 && this.hovered;
    }
}
