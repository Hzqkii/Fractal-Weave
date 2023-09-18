package xyz.flapjack.fractal.interfaces.clickgui.components.peripherals;

/* Custom. */
import xyz.flapjack.fractal.interfaces.clickgui.components.UIComponent;
import xyz.flapjack.fractal.render.main.Image;

/* Open. */
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.Minecraft;

public class GuiImage extends UIComponent {
    public Images image = Images.Neko;
    public ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

    public int posX;
    public int posY;
    public int targetX;
    public boolean enabled = true;

    /**
     * Initializes the position of the image.
     */
    public GuiImage() {
        super(0, 0, 0, 0);

        this.posX = this.scaledResolution.getScaledWidth() + this.image.width + 5;
        this.posY = this.scaledResolution.getScaledHeight() - this.image.height;
    }

    @Override
    public void render(float alpha) {
        if (this.enabled) {
            Image.drawImage("/assets/fractal/" + this.image.location, this.posX, this.posY, this.image.width, this.image.height, 1);
        }
    }

    @Override
    public void update(int posX, int posY, boolean opened, int offset) {
        this.enabled = (boolean) Instance.getModule("Click GUI").getVal("Click GUI image");
        String option = (String) Instance.getModule("Click GUI").getVal("Image");

        for (Images image: Images.values()) {
            if (image.toString().equalsIgnoreCase(option)) {
                this.image = image;
            }
        }

        this.scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

        this.targetX = opened ? this.scaledResolution.getScaledWidth() - this.image.width : this.scaledResolution.getScaledWidth() + this.image.width + 5;

        this.posX += ((this.targetX - this.posX) / 5);
        this.posY = this.scaledResolution.getScaledHeight() - this.image.height;
    }

    /**
     * Reset the value of the image position.
     */
    public void reset() {
        this.scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

        this.posX = this.scaledResolution.getScaledWidth() + this.image.width + 5;
        this.posY = this.scaledResolution.getScaledHeight() - this.image.height;
    }
}
