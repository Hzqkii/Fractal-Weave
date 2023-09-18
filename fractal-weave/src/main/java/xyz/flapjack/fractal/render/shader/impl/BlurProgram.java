package xyz.flapjack.fractal.render.shader.impl;

/* Custom. */
import xyz.flapjack.fractal.render.shader.ShaderUtils;

/* Open. */
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL20;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUseProgram;

public final class BlurProgram extends ShaderUtils {
    public BlurProgram() {
        super("/assets/fractal/shaders/blur/blur.frag", "/assets/fractal/shaders/blur/vertex.vsh");
    }

    /**
     * Renders the blur effect to the screen.
     * @param posX          the X position of the top left point of the clip.
     * @param posY          the Y position of the top left point of the clip.
     * @param width         the width of the clip.
     * @param height        the height of the clip.
     * @param radius        the blur radius.
     * @param compression   the blur compression.
     */
    public void render(final float posX, final float posY, final float width, final float height, final int radius, final int compression) {
        update();

        this.buffer.deleteFramebuffer();
        this.buffer = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, false);

        glUseProgram(this.program);

        GL20.glUniform1i(glGetUniformLocation(this.program, "texture"), 0);
        GL20.glUniform2f(glGetUniformLocation(this.program, "texelSize"), 1.0f / Minecraft.getMinecraft().displayWidth, 1.0f / Minecraft.getMinecraft().displayHeight);

        GL20.glUniform1f(glGetUniformLocation(this.program, "radius"), MathHelper.ceiling_float_int(2 * radius));
        this.buffer.framebufferClear();
        this.buffer.bindFramebuffer(false);

        GL20.glUniform2f(glGetUniformLocation(this.program, "direction"), compression, 0.0f);
        glBindTexture(GL_TEXTURE_2D, Minecraft.getMinecraft().getFramebuffer().framebufferTexture);

        glClear(GL_STENCIL_BUFFER_BIT);
        glEnable(GL_STENCIL_TEST);
        glStencilFunc(GL_ALWAYS, 1, 0xFF);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        glStencilMask(0xFF);

        glBegin(GL_QUADS);
        glVertex2f(posX, posY);
        glVertex2f(posX, posY + height);
        glVertex2f(posX + width, posY + height);
        glVertex2f(posX + width, posY);
        glEnd();

        glStencilFunc(GL_EQUAL, 1, 0xFF);

        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(0, 0);
        glTexCoord2f(0, 0);
        glVertex2f(0, (float) scaledResolution.getScaledHeight_double());
        glTexCoord2f(1, 0);
        glVertex2f((float) scaledResolution.getScaledWidth_double(), (float) scaledResolution.getScaledHeight_double());
        glTexCoord2f(1, 1);
        glVertex2f((float) scaledResolution.getScaledWidth_double(), 0);
        glEnd();

        glDisable(GL_STENCIL_TEST);

        this.buffer.unbindFramebuffer();

        glUseProgram(this.program);

        GL20.glUniform1i(glGetUniformLocation(this.program, "texture"), 0);
        GL20.glUniform2f(glGetUniformLocation(this.program, "texelSize"), 1.0f / Minecraft.getMinecraft().displayWidth, 1.0f / Minecraft.getMinecraft().displayHeight);
        GL20.glUniform1f(glGetUniformLocation(this.program, "radius"), MathHelper.ceiling_float_int(2 * radius));

        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);

        GL20.glUniform2f(glGetUniformLocation(this.program, "direction"), 0.0f, compression);
        glBindTexture(GL_TEXTURE_2D, this.buffer.framebufferTexture);

        glClear(GL_STENCIL_BUFFER_BIT);
        glEnable(GL_STENCIL_TEST);
        glStencilFunc(GL_ALWAYS, 1, 0xFF);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        glStencilMask(0xFF);

        glBegin(GL_QUADS);
        glVertex2f(posX, posY);
        glVertex2f(posX, posY + height);
        glVertex2f(posX + width, posY + height);
        glVertex2f(posX + width, posY);
        glEnd();

        glStencilFunc(GL_EQUAL, 1, 0xFF);

        float texX = (float) this.buffer.framebufferWidth / (float) this.buffer.framebufferTextureWidth;
        float texY = (float) this.buffer.framebufferHeight / (float) this.buffer.framebufferTextureHeight;

        glBegin(GL_QUADS);
        glTexCoord2f(0f, 0f);
        glVertex3f(0f, (float) scaledResolution.getScaledHeight_double(), 0f);
        glTexCoord2f(texX, 0f);
        glVertex3f((float) scaledResolution.getScaledWidth_double(), (float) scaledResolution.getScaledHeight_double(), 0f);
        glTexCoord2f(texX, texY);
        glVertex3f((float) scaledResolution.getScaledWidth_double(), 0f, 0f);
        glTexCoord2f(0f, texY);
        glVertex3f(0f, 0f, 0f);
        glEnd();

        glDisable(GL_STENCIL_TEST);

        glUseProgram(0);
    }
}
