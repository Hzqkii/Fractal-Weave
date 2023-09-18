package xyz.flapjack.fractal.interfaces.clickgui.components.effects;

/* Custom. */
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import xyz.flapjack.fractal.interfaces.clickgui.components.UIComponent;
import xyz.flapjack.fractal.render.main.Effect;

public class BlurEffect extends UIComponent {
    public BlurEffect() {
        super(0, 0, 0, 0);
    }

    @Override
    public void render(final float alpha) {
        if (this.enabled) {
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            Effect.blurProgram.render(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 3, Math.round(alpha));
        }
    }

    @Override
    public void update(int posX, int posY, boolean opened, int offset) {
        this.enabled = (boolean) Instance.getModule("Click GUI").getVal("Blur effect");
    }
}
