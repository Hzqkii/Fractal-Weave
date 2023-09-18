package xyz.flapjack.fractal.interfaces.clickgui.components.effects;

/* Custom. */
import xyz.flapjack.fractal.interfaces.clickgui.components.UIComponent;
import xyz.flapjack.fractal.render.main.Simple;
import xyz.flapjack.fractal.Fractal;

/* Open. */
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.Minecraft;
import java.awt.*;

public class TintEffect extends UIComponent {
    public TintEffect() {
        super(0, 0, 0, 0);
    }

    @Override
    public void render(final float alpha) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

        if (this.enabled) {
            Fractal instance = Instance.getInstance();

            Simple.drawRect(0,
                    0,
                    scaledResolution.getScaledWidth(),
                    scaledResolution.getScaledHeight(),
                    new Color(instance.themeColour.getRed(),
                            instance.themeColour.getGreen(),
                            instance.themeColour.getBlue(),
                            Math.round((int) Instance.getModule("Click GUI").getVal("Tint strength") * alpha)).getRGB());
        }
    }

    @Override
    public void update(int posX, int posY, boolean opened, int offset) {
        this.enabled = (boolean) Instance.getModule("Click GUI").getVal("Tint effect");
    }
}
