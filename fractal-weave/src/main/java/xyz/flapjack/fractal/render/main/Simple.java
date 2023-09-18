package xyz.flapjack.fractal.render.main;

/* Custom. */
import xyz.flapjack.fractal.render.Engine;

/* Open. */
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class Simple extends Engine {
    /**
     * Draws a rectangle.
     * @param posX      the X position.
     * @param posY      the Y position.
     * @param width     the width.
     * @param height    the height.
     * @param colour    the colour.
     */
    public static void drawRect(final int posX, final int posY, final int width, final int height, final int colour) {
        update();

        startGl();

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        setColour(colour);

        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2d(posX, posY);
        GL11.glVertex2d(posX + width, posY);
        GL11.glVertex2d(posX + width, posY + height);
        GL11.glVertex2d(posX, posY + height);
        GL11.glEnd();

        stopGl();
    }

    /**
     * Draws a rounded rectangle | "all", "top", "base".
     * @param posX          the X position.
     * @param posY          the Y position.
     * @param width         the width.
     * @param height        the height.
     * @param colour        the colour.
     * @param radius        the radius of the corners.
     * @param lineThickness the thickness of the outline.
     * @param type          the type of rounded rectangle to draw.
     */
    public static void drawRoundedRect(int posX, int posY, int width, int height, final int colour, final int radius, final float lineThickness, final Rect type) {
        update();

        GL11.glPushAttrib(0);
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        posX *= 2.0D;
        posY *= 2.0D;
        width *= 2.0D;
        height *= 2.0D;

        startGl();

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        setColour(colour);

        if (type == Rect.ALL_OUTLINE || type == Rect.TOP_OUTLINE || type == Rect.BASE_OUTLINE) {
            GL11.glLineWidth(lineThickness);
            GL11.glBegin(GL11.GL_LINE_LOOP);
        } else {
            GL11.glBegin(GL11.GL_POLYGON);
        }

        int i;

        switch (type) {
            case ALL_OUTLINE, ALL -> {
                for (i = 0; i <= 90; i++) {
                    GL11.glVertex2d(posX + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, posY + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
                }
                for (i = 90; i <= 180; i++) {
                    GL11.glVertex2d(posX + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, (posY + height) - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
                }
                for (i = 0; i <= 90; i++) {
                    GL11.glVertex2d((posX + width) - radius + Math.sin(i * Math.PI / 180.0D) * radius, (posY + height) - radius + Math.cos(i * Math.PI / 180.0D) * radius);
                }
                for (i = 90; i <= 180; i++) {
                    GL11.glVertex2d((posX + width) - radius + Math.sin(i * Math.PI / 180.0D) * radius, posY + radius + Math.cos(i * Math.PI / 180.0D) * radius);
                }
            }
            case TOP_OUTLINE, TOP -> {
                for (i = 0; i <= 90; i++) {
                    GL11.glVertex2d(posX + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, posY + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
                }
                for (i = 90; i <= 180; i++) {
                    GL11.glVertex2d(posX + 1 + Math.sin(i * Math.PI / 180.0D) * 1 * -1.0D, (posY + height) - 1 + Math.cos(i * Math.PI / 180.0D) * 1 * -1.0D);
                }
                for (i = 0; i <= 90; i++) {
                    GL11.glVertex2d((posX + width) - 1 + Math.sin(i * Math.PI / 180.0D) * 1, (posY + height) - 1 + Math.cos(i * Math.PI / 180.0D) * 1);
                }
                for (i = 90; i <= 180; i++) {
                    GL11.glVertex2d((posX + width) - radius + Math.sin(i * Math.PI / 180.0D) * radius, posY + radius + Math.cos(i * Math.PI / 180.0D) * radius);
                }
            }
            case BASE_OUTLINE, BASE -> {
                for (i = 0; i <= 90; i++) {
                    GL11.glVertex2d(posX + 1 + Math.sin(i * Math.PI / 180.0D) * 1 * -1.0D, posY + 1 + Math.cos(i * Math.PI / 180.0D) * 1 * -1.0D);
                }
                for (i = 90; i <= 180; i++) {
                    GL11.glVertex2d(posX + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, (posY + height) - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
                }
                for (i = 0; i <= 90; i++) {
                    GL11.glVertex2d((posX + width) - radius + Math.sin(i * Math.PI / 180.0D) * radius, (posY + height) - radius + Math.cos(i * Math.PI / 180.0D) * radius);
                }
                for (i = 90; i <= 180; i++) {
                    GL11.glVertex2d((posX + width) - 1 + Math.sin(i * Math.PI / 180.0D) * 1, posY + 1 + Math.cos(i * Math.PI / 180.0D) * 1);
                }
            }
        }

        GL11.glEnd();
        stopGl();

        GL11.glScaled(2.0D, 2.0D, 2.0D);
        GL11.glPopAttrib();
    }

    /**
     * Draws a rounded rectangle | "all", "top", "base".
     * @param posX      the X position.
     * @param posY      the Y position.
     * @param width     the width.
     * @param height    the height.
     * @param colour    the colour.
     * @param radius    the radius of the corners.
     * @param type      the type of rounded rectangle to draw.
     */
    public static void drawRoundedRect(int posX, int posY, int width, int height, final int colour, final int radius, final Rect type) {
        drawRoundedRect(posX, posY, width, height, colour, radius, 0, type);
    }

    public enum Rect {
        ALL,
        TOP,
        BASE,
        ALL_OUTLINE,
        TOP_OUTLINE,
        BASE_OUTLINE;
    }

    /**
     * Draws a circle.
     * @param posX      the X position.
     * @param posY      the Y position.
     * @param radius    the radius.
     * @param colour    the colour.
     */
    public static void drawCircle(int posX, int posY, final int radius, final int colour) {
        update();

        posX -= radius / 2;
        posY -= radius / 2;

        startGl();

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        setColour(colour);

        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        double angle = 0D;
        for (int i = 0; i < 360; i++) {
            angle = i * 4 * (Math.PI * 2) / 360;
            GL11.glVertex2d(posX + (radius * Math.cos(angle)) + radius, posY + (radius * Math.sin(angle)) + radius);
        }

        GL11.glEnd();

        stopGl();
    }

    /**
     * Begin a scissor section.
     * @param posX      the X position.
     * @param posY      the Y position.
     * @param width     the width.
     * @param height    the height.
     */
    public static void beginScissor(int posX, int posY, int width, int height) {
        posY = scaledResolution.getScaledHeight() - posY;

        posX *= scaledResolution.getScaleFactor();
        posY *= scaledResolution.getScaleFactor();
        width *= scaledResolution.getScaleFactor();
        height *= scaledResolution.getScaleFactor();

        enableGl(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(posX, posY - height, width, height);
    }

    /**
     * Begin a scissor section.
     * @param posX      the X position.
     * @param posY      the Y position.
     * @param width     the width.
     * @param height    the height.
     * @param flag      flags the method as being a non-interrupting scissor.
     */
    public static void beginScissor(int posX, int posY, int width, int height, final Object flag) {
        posY = scaledResolution.getScaledHeight() - posY;

        posX *= scaledResolution.getScaleFactor();
        posY *= scaledResolution.getScaleFactor();
        width *= scaledResolution.getScaleFactor();
        height *= scaledResolution.getScaleFactor();

        GL11.glScissor(posX, posY - height, width, height);
    }

    /**
     * End the scissor section.
     */
    public static void endScissor() {
        disableGl(GL11.GL_SCISSOR_TEST);
    }

    /**
     * Generate a glVertex3D from a vector.
     * @param vector3d the given vector.
     */
    public static void glVertex3D(final Vec3 vector3d) {
        GL11.glVertex3d(vector3d.xCoord, vector3d.yCoord, vector3d.zCoord);
    }

    /**
     * Get the render position.
     * @param x the X position.
     * @param y the Y position.
     * @param z the Z position.
     * @return a new Vec3 instance.
     */
    public static Vec3 getRenderPos(double x, double y, double z) {
        x -= mcInstance.getRenderManager().viewerPosX;
        y -= mcInstance.getRenderManager().viewerPosY;
        z -= mcInstance.getRenderManager().viewerPosZ;

        return new Vec3(x, y, z);
    }

    /**
     * Draw a dimensional rectangle, allowing 2D ESP projections.
     * @param left      left.
     * @param top       top.
     * @param right     right.
     * @param bottom    bottom.
     * @param colour    colour.
     */
    public static void drawDimensionalRectangle(float left, float top, float right, float bottom, final int colour) {
        if (left < right)
        {
            float i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            float j = top;
            top = bottom;
            bottom = j;
        }

        float float1 = (float) (colour >> 16 & 255) / 255.0F;
        float float2 = (float) (colour >> 8 & 255) / 255.0F;
        float float3 = (float) (colour & 255) / 255.0F;
        float float4 = (float) (colour >> 24 & 255) / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(float1, float2, float3, float4);

        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();

        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /**
     * Draw a dimensional rectangle, allowing 2D ESP projections.
     * @param left      left.
     * @param top       top.
     * @param right     right.
     * @param bottom    bottom.
     * @param f1        f1.
     * @param f2        f2.
     * @param f3        f3.
     */
    public static void drawDimensionalRectangle(float left, float top, float right, float bottom, final float f1, final float f2, final float f3) {
        if (left < right)
        {
            float i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            float j = top;
            top = bottom;
            bottom = j;
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f1, f2, f3);

        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();

        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}