package xyz.flapjack.fractal.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class Engine {
    public static ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
    public static Minecraft mcInstance = Minecraft.getMinecraft();

    /**
     * Update the screen resolution.
     */
    public static void update() {
        scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
    }

    /**
     * Sets colour based on int value.
     * @param colour the int value.
     */
    public static void setColour(final int colour) {
        GlStateManager.color((float) (colour >> 16 & 255) / 255.0F,
                (float) (colour >> 8 & 255) / 255.0F,
                (float) (colour & 255) / 255.0F,
                (float) (colour >> 24 & 255) / 255.0F);
    }

    /**
     * Enable target GL code.
     * @param target the target code.
     */
    public static void enableGl(final int target) {
        GL11.glEnable(target);
    }

    /**
     * Disable target GL code.
     * @param target the target code.
     */
    public static void disableGl(final int target) {
        GL11.glDisable(target);
    }

    /**
     * Start basic GL.
     */
    public static void startGl() {
        enableGl(GL11.GL_BLEND);
        disableGl(GL11.GL_TEXTURE_2D);
        disableGl(GL11.GL_CULL_FACE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    /**
     * Stop basic Gl.
     */
    public static void stopGl() {
        disableGl(GL11.GL_BLEND);
        enableGl(GL11.GL_TEXTURE_2D);
        enableGl(GL11.GL_CULL_FACE);
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
    }
}
