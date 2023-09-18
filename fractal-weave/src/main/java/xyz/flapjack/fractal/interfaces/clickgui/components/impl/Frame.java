package xyz.flapjack.fractal.interfaces.clickgui.components.impl;

/* Custom. */
import xyz.flapjack.fractal.interfaces.clickgui.components.UIComponent;
import xyz.flapjack.fractal.interfaces.clickgui.ClickGui;
import xyz.flapjack.fractal.render.main.Simple;
import xyz.flapjack.fractal.modules.Module;

/* Open. */
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.Minecraft;
import java.util.ArrayList;
import java.awt.*;

public class Frame extends UIComponent {
    public ArrayList<UIComponent> modules = new ArrayList<>();

    public static int width = 115;
    public static int height = 20;
    public static Color standardColour = new Color(35, 35, 41);
    public static Color hoverColour = new Color(35, 35, 41);

    public final Category category;
    public final ClickGui owner;
    public final boolean draggable;
    public boolean dragging;
    public boolean opened;
    public boolean isExpanded;

    public final int originPosX;
    public final int originPosY;

    public final int zLevel;

    public int mouseDragX;
    public int mouseDragY;
    private int lastMouseX;
    private int lastMouseY;

    public int[] offsets;
    public final int[] scissor = new int[4];
    public int offset = 0;
    public int smoothOffset = 0;

    private Color colour;
    private float smoothIn = -500;

    private float rotation = 0f;

    public Frame(final Category category, final ClickGui owner, final int posX, final int posY, final boolean draggable, final int iteration) {
        super(width, height, posX, posY);

        this.category = category;
        this.owner = owner;
        this.draggable = draggable;
        this.posX = posX;
        this.posY = posY;

        ArrayList<Module> categoryModules = Instance.getModules(category);

        this.dragging = false;
        this.opened = true;
        this.isExpanded = false;
        this.originPosX = posX;
        this.originPosY = posY;
        this.zLevel = iteration;
        this.mouseDragX = 0;
        this.mouseDragY = 0;

        this.offsets = new int[categoryModules.size()];
        this.colour = null;

        int iter = 0;

        for (Module module: categoryModules) {
            switch (module.component) {
                case "menu" -> {
                    this.modules.add(new MenuComponent(this, iter, module, module == categoryModules.get(categoryModules.size() - 1)));
                }
            }

            iter++;
        }
    }

    @Override
    public void render(float alpha) {
        int iterationHeight = 0;
        for (int height: this.offsets) {
            iterationHeight += height;
        }

        if (iterationHeight > 400) {
            iterationHeight = 400;
        }

        this.colour = standardColour;
        if (over(this.lastMouseX, this.lastMouseY)) {
            this.colour = hoverColour;
        };

        GlStateManager.pushMatrix();

        Simple.drawRoundedRect(this.posX, this.posY, width, height, new Color(this.colour.getRed(), this.colour.getGreen(), this.colour.getBlue(), Math.round(255 * alpha)).getRGB(), (int) Instance.getModule("Click GUI").getVal("Border radius"), (this.opened ? Simple.Rect.TOP : Simple.Rect.ALL));
        this.getFont(24).drawString(this.category.toString(), this.posX + 5, this.posY + 4, new Color(255, 255, 255).getRGB());

        Simple.beginScissor(this.posX, this.posY + height, width, height + iterationHeight);

        if (this.opened) {
            for (UIComponent component: this.modules) {
                component.render(alpha);
            }
        };

        Simple.endScissor();
        GlStateManager.popMatrix();
    }

    @Override
    public void renderShadow(float alpha) {
        int iterationHeight = 0;
        for (int height: this.offsets) {
            iterationHeight += height;
        }

        if (iterationHeight > 400) {
            iterationHeight = 400;
        }

        GlStateManager.pushMatrix();

        Simple.drawRoundedRect(this.posX, this.posY, width, height, new Color(0, 0, 0, Math.round(150 * alpha)).getRGB(), (int) Instance.getModule("Click GUI").getVal("Border radius"), (this.opened ? Simple.Rect.TOP : Simple.Rect.ALL));

        Simple.beginScissor(this.posX, this.posY + height, width, height + iterationHeight);

        this.scissor[0] = this.posX;
        this.scissor[1] = this.posY + height;
        this.scissor[2] = width;
        this.scissor[3] = height + iterationHeight;

        if (this.opened) {
            for (UIComponent component: this.modules) {
                component.renderShadow(alpha);
            }
        };

        Simple.endScissor();
        GlStateManager.popMatrix();
    }

    @Override
    public void update(int posX, int posY, boolean opened, int offset) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        this.lastMouseX = posX;
        this.lastMouseY = posY;

        if (this.dragging && this.draggable) {
            if (this.posX - (posX - this.mouseDragX) > this.rotation) {
                this.rotation = this.posX - (posX - this.mouseDragX);
            }

            this.posX = posX - this.mouseDragX;
            this.posY = posY - this.mouseDragY;
        }

        if (this.dragging) {
            if (this.posX < 5) {
                this.posX = 5;
            }
            if (this.posX > scaledResolution.getScaledWidth() - width - 5) {
                this.posX = scaledResolution.getScaledWidth() - width - 5;
            }
            if (this.posY < 5) {
                this.posY = 5;
            }
            if (this.posY > scaledResolution.getScaledHeight() - height - 5) {
                this.posY = scaledResolution.getScaledHeight() - height - 5;
            }
        }

        this.smoothOffset += (this.offset - this.smoothOffset) / 5;
        this.smoothIn += (0 - this.smoothIn) / 10;
        for (UIComponent component: this.modules) {
            component.update(posX, posY, opened, this.smoothOffset + (int) smoothIn);
        }
    }

    @Override
    public void keyPressed(char char1, int key) {
        for (UIComponent component: this.modules) {
            component.keyPressed(char1, key);
        }
    }

    /**
     * Toggles the frame opening.
     */
    public void toggle() {
        this.opened = !this.opened;
        this.smoothIn = -500;
    }

    /**
     * Process a scroll wheel event
     * @param wheel the amount scrolled.
     */
    public void scrolled(final int wheel, final int posX, final int posY) {
        int iterationHeight = 0;
        for (int height: this.offsets) {
            iterationHeight += height;
        }

        if (posX >= this.posX && posY >= this.posY && posX <= (width + this.posX) && posY <= (iterationHeight + height + this.posY)) {
            this.offset += wheel;

            if (this.offset + iterationHeight < 350) {
                this.offset = 350 - iterationHeight;
            }

            if (this.offset > 4) {
                this.offset = 4;
            }
        }
    }
}
