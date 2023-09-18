package xyz.flapjack.fractal.interfaces.clickgui.components.effects;

/* Custom. */
import xyz.flapjack.fractal.interfaces.clickgui.components.UIComponent;
import xyz.flapjack.fractal.render.main.Simple;

/* Open. */
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.Minecraft;
import java.awt.*;

public class ObscurationEffect extends UIComponent {
    public ObscurationEffect() {
        super(0, 0, 0, 0);
    }

    @Override
    public void render(final float alpha) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

        if (this.enabled) {
            Simple.drawRect(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), new Color(0, 0, 0, Math.round((int) Instance.getModule("Click GUI").getVal("Obscuration strength") * alpha)).getRGB());
        }
    }

    @Override
    public void update(int posX, int posY, boolean opened, int offset) {
        this.enabled = (boolean) Instance.getModule("Click GUI").getVal("Obscuration effect");
    }
}
