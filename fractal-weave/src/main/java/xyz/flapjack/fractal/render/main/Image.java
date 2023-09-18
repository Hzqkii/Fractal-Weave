package xyz.flapjack.fractal.render.main;

//* Custom. */
import xyz.flapjack.fractal.render.Engine;

/* Open. */
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.awt.*;

public class Image extends Engine {
    private static final Map<String, ResourceLocation> loadedTextures = new HashMap<>();

    /**
     * Renders an image to the screen.
     * @param location  the location of the texture in resources.
     * @param posX      the X position.
     * @param posY      the Y position.
     * @param width     the desired width.
     * @param height    the desired height.
     * @param alpha     the desired alpha.
     */
    public static void drawImage(final String location, final int posX, final int posY, final int width, final int height, final float alpha) {
        update();

        try {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            setColour(new Color(255, 255, 255, Math.round(255 * alpha)).getRGB());

            ResourceLocation checkLocation = loadedTextures.get(location);

            if (checkLocation == null) {
                InputStream stream = Image.class.getResourceAsStream(location);

                if (stream == null) {
                    return;
                }

                BufferedImage image = ImageIO.read(stream);
                ResourceLocation resource = new ResourceLocation("assets/fractal", "temp_" + System.nanoTime());
                DynamicTexture texture = new DynamicTexture(image);

                Minecraft.getMinecraft().getTextureManager().loadTexture(resource, texture);
                Minecraft.getMinecraft().getTextureManager().bindTexture(resource);

                loadedTextures.put(location, resource);
            } else {
                Minecraft.getMinecraft().getTextureManager().bindTexture(checkLocation);
            }

            Gui.drawModalRectWithCustomSizedTexture(posX, posY, 0.0f, 0.0f, width, height, width, height);

            GlStateManager.enableAlpha();
            GL11.glDisable(GL11.GL_BLEND);

        } catch (Exception ignored) { }
    }
}
