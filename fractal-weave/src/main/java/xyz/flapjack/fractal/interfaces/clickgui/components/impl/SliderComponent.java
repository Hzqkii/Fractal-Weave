package xyz.flapjack.fractal.interfaces.clickgui.components.impl;

/* Custom. */
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import xyz.flapjack.fractal.interfaces.clickgui.components.UIComponent;
import xyz.flapjack.fractal.render.main.Simple;
import xyz.flapjack.fractal.modules.Setting;

/* Open. */
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.awt.*;

public class SliderComponent extends UIComponent {
    public static int iWidth = 115;
    public static int iHeight = 20;
    public static Color standardColour = new Color(17, 17, 17);
    public static Color hoverColour = new Color(35, 35, 41);

    public String title;

    public float value;
    public float drawValue;
    public float min;
    public float max;
    public boolean dragging;

    private Color colour;
    private Color sliderColour;

    private final String type;

    private Thread keyListener;
    private long held = 0l;

    public SliderComponent(final MenuComponent menuOwner, final int iteration, final Setting setting, final String type) {
        super(iWidth, iHeight, menuOwner, iteration);

        this.setting = setting;

        this.title = setting.getName();

        this.type = type;

        this.colour = standardColour;
        this.sliderColour = hoverColour;
        this.height = iHeight;
        this.width = iWidth;
        this.dragging = false;

        switch (setting.getType()) {
            case "slider-i" -> {
                this.value = (float) setting.intValue;
                this.min = (float) setting.intMin;
                this.max = (float) setting.intMax;

            }
            case "slider-d" -> {
                this.value = (float) setting.doubleValue;
                this.min = (float) setting.doubleMin;
                this.max = (float) setting.doubleMax;

            }
        }

        this.drawValue = 0.0f;

        this.extension = setting.extension;
    }

    @Override
    public void render(float alpha) {
        if (this.setting.condition == null) {
            this.draw(alpha);

            return;
        }

        if (this.setting.condition.booleanValue) {
            this.draw(alpha);
        }
    }

    @Override
    public void update(int posX, int posY, boolean opened, int offset) {
        int selfOffset = MenuComponent.iHeight;

        for (int i = 0; i < this.iteration; i++) {
            selfOffset += this.menuOwner.offsets[i];
        }

        setPos(this.menuOwner.posX, this.menuOwner.posY + selfOffset);
        this.hovered = over(posX, posY);

        if (!this.menuOwner.frameOwner.owner.mouseDown) {
            this.dragging = false;
        }

        if (this.dragging) {
            if (this.type.equals("slider-i")) {
                this.value = Math.round((((float) posX - (this.posX)) / ((float) this.width)) * this.max);
            } else {
                BigDecimal converted = new BigDecimal((((float) posX - (this.posX)) / ((float) this.width)) * this.max).setScale(2, RoundingMode.HALF_UP);
                this.value = converted.floatValue();
            }

            if (this.value < this.min) {
                this.value = this.min;
            } else if (this.value > this.max) {
                this.value = this.max;
            }

            switch (this.setting.getType()) {
                case "slider-i" -> {
                    this.setting.intValue = (int) this.value;

                }
                case "slider-d" -> {
                    this.setting.doubleValue = (double) this.value;

                }
            }
        }

        if (this.hovered) {
            this.colour = hoverColour;
            this.sliderColour = standardColour;
        } else {
            this.colour = standardColour;
            this.sliderColour = hoverColour;
        }

        if (this.setting.condition == null) {
            return;
        }

        if (this.setting.condition.booleanValue) {
            this.menuOwner.offsets[this.iteration] = this.height;
        } else {
            this.menuOwner.offsets[this.iteration] = 0;

            reset();
        }
    }

    @Override
    public void keyPressed(char char1, int key) {
        if (!this.hovered) {
            return;
        }

        this.stopKeyListener();

        Minecraft mcInstance = Minecraft.getMinecraft();

        int keyPressed = Keyboard.getEventKey();

        if (keyPressed != mcInstance.gameSettings.keyBindLeft.getKeyCode()
                && keyPressed != Keyboard.KEY_LEFT
                && keyPressed != mcInstance.gameSettings.keyBindRight.getKeyCode()
                && keyPressed != Keyboard.KEY_RIGHT) {

            return;
        }

        this.keyListener = new Thread(() -> {
            try {
                while (true) {
                    boolean keyPressedInternal = Keyboard.isKeyDown(keyPressed);

                    if (!keyPressedInternal) {
                        this.stopKeyListener();

                        return;
                    }

                    if (System.currentTimeMillis() - 1200 > this.held) {
                        if (keyPressed == mcInstance.gameSettings.keyBindLeft.getKeyCode() || keyPressed == Keyboard.KEY_LEFT) {
                            this.value--;
                        } else if (keyPressed == mcInstance.gameSettings.keyBindRight.getKeyCode() || keyPressed == Keyboard.KEY_RIGHT) {
                            this.value++;
                        } else {
                            this.stopKeyListener();
                        }

                        if (this.value < this.min) {
                            this.value = this.min;
                        } else if (this.value > this.max) {
                            this.value = this.max;
                        }

                        Thread.sleep(100);
                    }
                }

            } catch (Exception ignored) { }
        });

        this.keyListener.start();
    }

    /**
     * Stops the KeyListener thread.
     */
    public void stopKeyListener() {
        if (this.keyListener == null) {
            return;
        }

        this.keyListener.stop();
    }

    @Override
    public void mouseDown(int posX, int posY, int button) {
        if (this.setting.condition == null) {
            if (over(posX, posY) && this.menuOwner.frameOwner.owner.mouseDown) {
                this.dragging = true;
            }
        } else {
            if (this.setting.condition.booleanValue) {
                if (over(posX, posY) && this.menuOwner.frameOwner.owner.mouseDown) {
                    this.dragging = true;
                }
            }
        }
    }

    @Override
    public void reset() {
        this.drawValue = 0;
    }

    @Override
    public void config() {
        switch (this.setting.getType()) {
            case "slider-i" -> {
                this.value = this.setting.intValue;

            }
            case "slider-d" -> {
                this.value = (float) this.setting.doubleValue;
            }
        }
    }

    /**
     * Renders this component to the screen.
     * @param alpha the alpha value of the GUI.
     */
    public void draw(final float alpha) {
        Color colour = new Color(this.colour.getRed(), this.colour.getGreen(), this.colour.getBlue(), Math.round(255 * alpha));
        if (this.iteration == this.menuOwner.offsets.length - 1) {
            Simple.drawRoundedRect(this.posX, this.posY, this.width, this.height, colour.getRGB(), (int) Instance.getModule("Click GUI").getVal("Border radius"), Simple.Rect.BASE);
        } else {
            Simple.drawRect(this.posX, this.posY, this.width, this.height, colour.getRGB());
        }

        colour = new Color(255, 255, 255, Math.round(255 * alpha));
        Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").drawString(this.title, this.posX + 5, this.posY + 5, colour.getRGB());
        Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").drawDependantString(String.valueOf((this.type.equals("slider-i")) ? (int) this.value : this.value), this.posX + this.width - 5, this.posY + 5, colour.getRGB());

        float width = (this.value / this.max) * (float) (this.width - 10);
        this.drawValue += (width - this.drawValue) / 20;

        colour = new Color(this.sliderColour.getRed(), this.sliderColour.getGreen(), this.sliderColour.getBlue(), Math.round(255 * alpha));
        Simple.drawRect(this.posX + 5, this.posY + this.height - 5, this.width - 10, 1, colour.getRGB());

        if (this.drawValue > 0) {
            Simple.drawRect(this.posX + 5, this.posY + this.height - 5, Math.round(this.drawValue), 1, Instance.getInstance().themeColour.getRGB());
        }
    }
}
