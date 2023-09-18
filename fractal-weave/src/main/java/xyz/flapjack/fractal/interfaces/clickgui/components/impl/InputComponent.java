package xyz.flapjack.fractal.interfaces.clickgui.components.impl;

/* Custom. */
import xyz.flapjack.fractal.interfaces.clickgui.components.UIComponent;
import xyz.flapjack.fractal.render.main.Simple;
import xyz.flapjack.fractal.modules.Setting;

/* Open. */
import java.awt.*;

public class InputComponent extends UIComponent {
    public static int iWidth = 115;
    public static int iHeight = 25;
    public static Color standardColour = new Color(17, 17, 17);
    public static Color hoverColour = new Color(35, 35, 41);
    public static Color textHoverColour = new Color(150, 150, 150);

    public String title;
    public boolean selected;

    public String value;

    private Color colour;
    private Color barColour;

    public InputComponent(final MenuComponent menuOwner, final int iteration, final Setting setting) {
        super(iWidth, iHeight, menuOwner, iteration);

        this.setting = setting;

        this.title = setting.getName();
        this.value = setting.stringValue;

        this.colour = standardColour;
        this.barColour = hoverColour;
        this.height = iHeight;
        this.width = iWidth;

        this.extension = setting.extension;
    }

    @Override
    public void render(float alpha) {
        Color colour = new Color(this.colour.getRed(), this.colour.getGreen(), this.colour.getBlue(), Math.round(255 * alpha));

        if (this.iteration == this.menuOwner.offsets.length - 1) {
            Simple.drawRoundedRect(this.posX, this.posY, this.width, this.height, colour.getRGB(), (int) Instance.getModule("Click GUI").getVal("Border radius"), Simple.Rect.BASE);
        } else {
            Simple.drawRect(this.posX, this.posY, this.width, this.height, colour.getRGB());
        }

        colour = new Color(255, 255, 255, Math.round(255 * alpha));
        Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").drawString(this.title, this.posX + 5, this.posY + 4, colour.getRGB());

        colour = new Color(textHoverColour.getRed(), textHoverColour.getGreen(), textHoverColour.getBlue(), Math.round(255 * alpha));
        Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").drawString(this.value, this.posX + 5, this.posY + (this.height - 15) + 4, colour.getRGB());

        colour = new Color(this.barColour.getRed(), this.barColour.getGreen(), this.barColour.getBlue(), Math.round(255 * alpha));
        Simple.drawRect(this.posX + 5, this.posY + height - 3, this.width - 10, 1, colour.getRGB());
    }

    @Override
    public void update(int posX, int posY, boolean opened, int offset) {
        this.setting.stringValue = this.value;

        int selfOffset = MenuComponent.iHeight;

        for (int i = 0; i < this.iteration; i++) {
            selfOffset += this.menuOwner.offsets[i];
        }

        setPos(this.menuOwner.posX, this.menuOwner.posY + selfOffset);
        this.hovered = over(posX, posY);

        if (this.hovered) {
            this.colour = hoverColour;
            this.barColour = standardColour;
        } else {
            this.colour = standardColour;
            this.barColour = hoverColour;
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

        String heuristic = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!\"'|£$%^&*()-=:<>/ ";

        if (key == 14) {
            if (this.value.length() > 0) {
                this.value = this.value.substring(0, this.value.length() - 1);
            }
            return;
        }

        if (Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").getWidth(this.value) < 105 && heuristic.contains(String.valueOf(char1).toUpperCase())) {
            this.value += char1;
        }
    }

    @Override
    public void mouseDown(int posX, int posY, int button) {
        this.selected = button == 0 && this.hovered;

        if (this.selected) {
            this.menuOwner.frameOwner.owner.typing = true;
        }
    }

    @Override
    public void config() {
        this.value = this.setting.stringValue;
    }
}
