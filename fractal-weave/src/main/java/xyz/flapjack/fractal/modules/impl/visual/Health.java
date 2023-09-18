package xyz.flapjack.fractal.modules.impl.visual;

/* Custom. */
import xyz.flapjack.fractal.events.impl.RenderOverlayEvent;
import xyz.flapjack.fractal.render.main.Simple;
import xyz.flapjack.fractal.events.Subscribed;
import xyz.flapjack.fractal.modules.Module;

/* Open. */
import net.minecraft.client.gui.ScaledResolution;
import java.awt.*;

public class Health extends Module {
    private static final Color standardColour = new Color(17, 17, 17);

    public Health() {
        super("Health", "Displays your health.", Category.Visual, "menu", "bindable");
    }

    @Subscribed(eventType = RenderOverlayEvent.class)
    public void renderOverlayTick(final RenderOverlayEvent event) {
        if (!this.enabled) {
            return;
        }

        ScaledResolution scaledResolution = new ScaledResolution(this.mcInstance);

        Simple.drawRoundedRect(((int) (double) scaledResolution.getScaledWidth() / 2) + 5, (int) ((double) scaledResolution.getScaledHeight() / 2) - 5, (int) Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").getWidth(String.valueOf(this.mcInstance.thePlayer.getHealth()).substring(0, 2)) + 5, 10, new Color(standardColour.getRed(), standardColour.getGreen(), standardColour.getBlue(), 100).getRGB(), 5, Simple.Rect.ALL);
        Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").drawString(String.valueOf(this.mcInstance.thePlayer.getHealth()).substring(0, 2), ((double) scaledResolution.getScaledWidth() / 2) + 7.5, ((double) scaledResolution.getScaledHeight() / 2) - (Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").getHeight() / 2), new Color(255, 255, 255).getRGB());
    }
}
