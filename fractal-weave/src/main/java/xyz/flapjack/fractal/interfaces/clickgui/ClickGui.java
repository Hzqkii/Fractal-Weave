package xyz.flapjack.fractal.interfaces.clickgui;

/* Custom. */
import xyz.flapjack.fractal.interfaces.clickgui.components.peripherals.GuiImage;
import xyz.flapjack.fractal.interfaces.clickgui.components.impl.Frame;
import xyz.flapjack.fractal.interfaces.clickgui.components.effects.*;
import xyz.flapjack.fractal.interfaces.clickgui.components.*;
import xyz.flapjack.fractal.render.main.Simple;
import xyz.flapjack.fractal.Fractal;
import xyz.flapjack.Access;

/* Open. */
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import java.util.ArrayList;
import java.awt.*;

public class ClickGui extends GuiScreen {
    private ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

    private final ArrayList<UIComponent> components = new ArrayList<>();
    public final ArrayList<HUDComponent> hudComponents = new ArrayList<>();
    public ArrayList<Frame> frames = new ArrayList<>();

    private final BlurEffect blurEffect = new BlurEffect();

    private boolean opened;
    private boolean layout;

    private float guiAlpha;
    private int mouseWheel;
    private int lastMouseWheel;

    public boolean mouseDown = false;
    public boolean dragging = false;
    public boolean typing = false;

    private boolean[] moving = new boolean[] { false, false, false, false, false };
    private final int[] codes = new int[5];
    private float[] angles = new float[] { 0f, 0f };

    public Tooltip tooltip = new Tooltip();

    public ClickGui() {
        this.opened = false;
        this.layout = false;

        this.guiAlpha = 0f;

        this.components.add(0, new GuiImage());
        this.components.add(1, new BlurEffect());
        this.components.add(2, new ObscurationEffect());
        this.components.add(3, new TintEffect());

        int posX = 30;
        int posY = 20;

        int iter = 1;

        for (Access.Category category: Access.Category.values()) {
            this.frames.add(new Frame(category, this, posX, posY, true, iter));

            posX += Frame.width + 10;
            iter++;
        }
    }

    /**
     * Renders the ClickGui on the screen.
     * @param posX      the x position of the mouse
     * @param posY      the y position of the mouse
     * @param ticks     the number of ticks since the game started
     */
    @Override
    public void drawScreen(final int posX, final int posY, final float ticks) {
        GlStateManager.pushMatrix();

        if (this.layout) {
            for (HUDComponent component: this.hudComponents) {
                component.update(posX, posY, this.opened, this.mouseWheel);
                component.renderShadow(this.guiAlpha);
            }

            this.components.get(0).update(posX, posY, this.opened, this.mouseWheel);
            this.components.get(0).render(guiAlpha);

            this.components.forEach((component) -> {
                component.update(posX, posY, this.opened, this.mouseWheel);
            });

            for (HUDComponent component: this.hudComponents) {
                component.render(this.guiAlpha);
            }
        } else {
            this.tooltip.disable();

            if (Mouse.hasWheel()) {
                this.mouseWheel = Mouse.getDWheel() / 10;
            }

            if (this.lastMouseWheel != this.mouseWheel) {
                this.process(this.mouseWheel, posX, posY);

                this.mouseWheel = this.lastMouseWheel;
            }

            this.scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            this.guiAlpha += ((this.opened  ? 1 : 0) - this.guiAlpha) / (this.opened ? 15 : 5);

            for (Frame frame: this.frames) {
                frame.update(posX, posY, this.opened, this.mouseWheel);
                frame.renderShadow(this.guiAlpha);
            }

            this.components.get(0).update(posX, posY, this.opened, this.mouseWheel);
            this.components.get(0).render(guiAlpha);

            this.components.forEach((component) -> {
                component.update(posX, posY, this.opened, this.mouseWheel);
            });

            this.components.forEach((component) -> {
                component.render(this.guiAlpha);
            });

            for (Frame frame: this.frames) {
                frame.render(this.guiAlpha);
            }

            if (this.tooltip.enabled) {
                this.renderTooltip();
            } else {
                this.tooltip.opacity = 0;
            }
        }

        this.drawWatermark();

        if (this.guiAlpha < 0.5 && !this.opened) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }

        GlStateManager.popMatrix();

        if ((boolean) Access.Instance.getModule("Click GUI").getVal("Move while open")) {
            Minecraft instance = Minecraft.getMinecraft();

            this.codes[0] = instance.gameSettings.keyBindForward.getKeyCode();
            this.codes[1] = instance.gameSettings.keyBindBack.getKeyCode();
            this.codes[2] = instance.gameSettings.keyBindLeft.getKeyCode();
            this.codes[3] = instance.gameSettings.keyBindRight.getKeyCode();
            this.codes[4] = instance.gameSettings.keyBindJump.getKeyCode();

            if (!this.typing) {
                for (int i = 0; i < 5; i++) {
                    if (Keyboard.isKeyDown(this.codes[i]) && !this.moving[i]) {
                        this.trigger(this.codes[i]);
                        this.moving[i] = true;
                    } else if (!Keyboard.isKeyDown(this.codes[i]) && moving[i]) {
                        this.release(this.codes[i]);
                        this.moving[i] = false;
                    }
                }
            } else {
                for (int i = 0; i < 5; i++) {
                    if (this.moving[i]) {
                        this.release(this.codes[i]);
                        this.moving[i] = false;
                    }
                }
            }
        }

        if ((boolean) Access.Instance.getModule("Click GUI").getVal("Mouse parallax")) {
            Minecraft instance = Minecraft.getMinecraft();
            ScaledResolution scaledResolution = new ScaledResolution(instance);

            double x = (scaledResolution.getScaledWidth_double() / 2) - (Mouse.getX() - 475);
            double y = (scaledResolution.getScaledHeight_double() / 2) - (Mouse.getY() - 255);

            x /= 80;
            y /= 80;

            float targetYaw = (float) (this.angles[0] - x);
            float targetPitch = (float) (this.angles[1] + y);

            if (targetPitch > 90) {
                targetPitch = 90;
            } else if (targetPitch < -90) {
                targetPitch = -90;
            }

            instance.thePlayer.rotationYaw += (targetYaw - instance.thePlayer.rotationYaw) / 5;
            instance.thePlayer.rotationPitch += (targetPitch - instance.thePlayer.rotationPitch) / 5;
        }
    }

    /**
     * Called when the left mouse button is clicked.
     * @param posX      the x position of the mouse
     * @param posY      the y position of the mouse
     * @param button    the button that was clicked
     */
    @Override
    public void mouseClicked(final int posX, final int posY, final int button) {
        if (button == 0) {
            this.mouseDown = true;
        }

        this.typing = false;

        if (this.layout) {
            for (HUDComponent component: this.hudComponents) {
                component.update(posX, posY, this.opened, 0);

                if (component.over(posX, posY)) {
                    if (button == 0) {
                        component.dragging = true;
                        component.mouseDragX = posX - component.posX;
                        component.mouseDragY = posY - component.posY;
                    }
                }
            }
        } else {
            for (Frame frame: this.frames) {
                frame.update(posX, posY, this.opened, 0);

                if (frame.over(posX, posY)) {
                    switch (button) {
                        case 0 -> {
                            if (frame.draggable) {
                                this.dragging = true;
                                frame.dragging = true;
                                frame.mouseDragX = posX - frame.posX;
                                frame.mouseDragY = posY - frame.posY;
                            }
                            return;
                        }
                        case 1 -> {
                            frame.toggle();
                        }
                    }
                }
                if (frame.opened && frame.smoothOffset < 5) {
                    if (!frame.modules.isEmpty()) {
                        for (UIComponent component: frame.modules) {
                            component.mouseDown(posX, posY, button);
                        }
                    }
                }
            }
        }
    }

    /**
     * Called when the left mouse button is released.
     * @param posX      the x position of the mouse
     * @param posY      the y position of the mouse
     * @param button    the button that was released
     */
    @Override
    public void mouseReleased(final int posX, final int posY, final int button) {
        this.mouseDown = false;

        if (this.layout) {
            for (HUDComponent component: this.hudComponents) {
                if (component.over(posX, posY)) {
                    if (button == 1) {
                        component.dragging = false;
                    }
                }
            }
        } else {
            for (Frame frame: this.frames) {
                if (button != 1) {
                    this.dragging = false;
                    frame.dragging = false;
                }

                if (frame.opened && !frame.modules.isEmpty()) {
                    for (UIComponent component: frame.modules) {
                        component.mouseUp(posX, posY, button);
                    }
                }
            }
        }
    }

    /**
     * Called when a key is typed.
     * @param char1 the character that was typed
     * @param key   the key code of the key that was typed
     */
    @Override
    public void keyTyped(final char char1, final int key) {
        switch (key) {
            case Keyboard.KEY_RSHIFT, 1 -> {
                triggerClose();

                return;
            }
        }

        if (!this.layout) {
            for (Frame frame: this.frames) {
                if (frame.opened) {
                    frame.keyPressed(char1, key);
                }
            }
        }
    }

    /**
     * Releases the W key.
     */
    private void release(final int keyCode) {
        KeyBinding.setKeyBindState(keyCode, false);
        KeyBinding.onTick(keyCode);
    }

    /**
     * Holds the W key.
     */
    private void trigger(final int keyCode) {
        KeyBinding.setKeyBindState(keyCode, true);
        KeyBinding.onTick(keyCode);
    }

    /**
     * Triggers the ClickGui to close.
     */
    public void triggerClose() {
        this.opened = false;
    }

    /**
     * Triggers the ClickGui to open.
     */
    public void triggerOpen() {
        this.opened = true;
        this.guiAlpha = 0f;

        Minecraft instance = Minecraft.getMinecraft();

        this.moving = new boolean[] { false, false, false, false, false };
        this.angles = new float[] { instance.thePlayer.rotationYaw, instance.thePlayer.rotationPitch };

        Minecraft.getMinecraft().displayGuiScreen(Fractal.INSTANCE.getClickGui());
    }

    /**
     * Triggers the ClickGui to go into layout mode.
     */
    public void triggerLayout() {
        this.layout = true;
    }

    /**
     * Initializes the ClickGui.
     */
    @Override
    public void initGui() {
        super.initGui();
    }

    /**
     * Called when the ClickGui is closed.
     */
    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    /**
     * Returns whether the game should be paused when the ClickGui is open.
     * @return true if the game should be paused, false otherwise
     */
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    /**
     * Draws a watermark in the GUI.
     */
    private void drawWatermark() {
        String watermark = (String) Access.Instance.getModule("Click GUI").getVal("Click GUI watermark");
        Access.Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").drawDependantString(watermark + " - FractalClient v1.2", this.scaledResolution.getScaledWidth() - 3, 5, new Color(255, 255, 255, Math.round(255 * this.guiAlpha)).getRGB());
    }

    /**
     * Process a scroll event.
     * @param wheel the scroll amount.
     */
    private void process(final int wheel, final int posX, final int posY) {
        for (Frame frame: this.frames) {
            frame.scrolled(wheel, posX, posY);
        }
    }

    /**
     * Renders a tooltip.
     */
    private void renderTooltip() {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        int height = (int) (Access.Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").getHeight() + 4);
        int width = (int) (Access.Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").getWidth(this.tooltip.tooltipText) + 10);

        int x = this.tooltip.tooltipX + 10;
        int y = this.tooltip.tooltipY;

        if (x + width > scaledResolution.getScaledWidth()) {
            x = this.tooltip.tooltipX - (10 + width);
        }
        if (y + height > scaledResolution.getScaledHeight()) {
            y = this.tooltip.tooltipY - height;
        }

        Simple.drawRoundedRect(x, y, width, height, new Color(17, 17, 17, (int) this.tooltip.opacity).getRGB(), 5, Simple.Rect.ALL);
        Access.Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").drawString(this.tooltip.tooltipText, x + 5, y + 2, new Color(255, 255, 255, (int) this.tooltip.opacity).getRGB());

        this.tooltip.opacity += (255 - this.tooltip.opacity) / 20;
    }

    /**
     * Tooltip container.
     */
    public static class Tooltip {
        public String tooltipText;
        public int tooltipX;
        public int tooltipY;
        public float opacity;

        public boolean enabled;

        /**
         * Sets a new tooltip.
         * @param tooltipText   the new tooltip text.
         * @param tooltipX      the new tooltip posX.
         * @param tooltipY      the new tooltip posY.
         */
        public void set(final String tooltipText, final int tooltipX, final int tooltipY) {
            this.tooltipText = tooltipText;
            this.tooltipX = tooltipX;
            this.tooltipY = tooltipY;

            this.enabled = true;
        }

        /**
         * Disables the tooltip.
         */
        public void disable() {
            this.enabled = false;
        }
    }
}
