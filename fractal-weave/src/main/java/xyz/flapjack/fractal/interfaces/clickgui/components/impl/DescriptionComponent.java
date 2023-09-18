package xyz.flapjack.fractal.interfaces.clickgui.components.impl;

/* Custom. */
import xyz.flapjack.fractal.interfaces.clickgui.components.UIComponent;
import xyz.flapjack.fractal.render.main.Simple;

/* Open. */
import java.awt.*;

public class DescriptionComponent extends UIComponent {
    public static int iWidth = 115;
    public static int iHeight = 10;
    public static Color textColour = new Color(180, 180, 180);

    public String displayText;

    public DescriptionComponent(final MenuComponent menuOwner, final int iteration, final String displayText) {
        super(iWidth, iHeight, menuOwner, iteration);

        this.displayText = displayText;

        this.height = iHeight;
        this.width = iWidth;
    }

    @Override
    public void render(float alpha) {
        Color colour = new Color(textColour.getRed(), textColour.getGreen(), textColour.getBlue());

        Simple.drawRect(this.posX, this.posY, this.width, this.height, new Color(17, 17, 17).getRGB());
        Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").drawCenteredStringWithShadow(this.displayText, this.posX + (this.width / 2.0f), this.posY + 3, colour.getRGB());
    }

    @Override
    public void update(int posX, int posY, boolean opened, int offset) {
        int selfOffset = MenuComponent.iHeight;

        for (int i = 0; i < this.iteration; i++) {
            selfOffset += this.menuOwner.offsets[i];
        }

        setPos(this.menuOwner.posX, this.menuOwner.posY + selfOffset);
        this.height = (int) (Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").getHeight() + 4);

        this.menuOwner.offsets[this.iteration] = this.height;
    }
}
