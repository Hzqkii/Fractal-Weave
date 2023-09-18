package xyz.flapjack.fractal.modules.impl.settings;

/* Custom. */
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;
import xyz.flapjack.fractal.Fractal;

/* Open. */
import java.util.ArrayList;
import java.util.Random;
import java.awt.*;

public class ClickInterface extends Module {
    private int lastRed = 0;
    private int lastGreen = 0;
    private int lastBlue = 0;

    public ClickInterface() {
        super("Click GUI", "The interface for the cheat.", Category.Settings, "menu", "trigger");

        ArrayList<String> options = new ArrayList<>();
        for (Images value: Images.values()) {
            options.add(value.toString());
        }
        String[] phrases = {"Nice visuals? :o", "A random prompt", "Developed by Flapjack"};

        this.registerSetting(new Setting("Move while open", this, null, true));
        this.registerSetting(new Setting("Mouse parallax", this, null, false));

        this.registerSetting(new Setting("Border radius", this, null, 5, 0, 25));
        this.registerSetting(new Setting("Tooltips", "Shows these.", this, null, true));

        Setting obscurationEffect = new Setting("Obscuration effect", this, null, true);
        this.registerSetting(obscurationEffect);
        this.registerSetting(new Setting("Obscuration strength", this, obscurationEffect, 100, 0, 255));

        Setting tintEffect = new Setting("Tint effect", this, null, true);
        this.registerSetting(tintEffect);
        this.registerSetting(new Setting("Tint strength", this, tintEffect, 10, 0, 50));

        this.registerSetting(new Setting("Blur effect", this, null, true));
        this.registerSetting(new Setting("Click GUI watermark", this, null, phrases[new Random().nextInt(phrases.length)]));

        Setting animeImage = new Setting("Click GUI image", this, null, false);
        this.registerSetting(animeImage);
        this.registerSetting(new Setting("Image", this, animeImage, options));

        this.registerSetting(new Setting("Red", this, null, 5, 0, 255));
        this.registerSetting(new Setting("Green", this, null, 152, 0, 255));
        this.registerSetting(new Setting("Blue", this, null, 98, 0, 255));

        Setting rainbow = new Setting("Rainbow", this, null, false);
        this.registerSetting(rainbow);
        this.registerSetting(new Setting("Rainbow delay", this, rainbow, 5, 1, 10));
    }

    @Override
    public void init() {
        new Thread(() -> {
            try {
                while (true) {
                    /*
                     * Here to update the global theming property when sliders are adjusted.
                     */
                    int red = (int) this.getVal("Red");
                    int green = (int) this.getVal("Green");
                    int blue = (int) this.getVal("Blue");

                    Fractal instance = Instance.getInstance();

                    if ((boolean) this.getVal("Rainbow")) {
                        int delay = (int) this.getVal("Rainbow delay") + 10;
                        
                        float hue = (System.currentTimeMillis() % (delay * 1000)) / (delay * 1000f);

                        instance.themeColour = new Color(Color.HSBtoRGB(hue, 1, 1));
                    } else {
                        if (this.lastRed != red) {
                            instance.themeColour = new Color(red, instance.themeColour.getGreen(), instance.themeColour.getBlue());
                            this.lastRed = red;
                        }

                        if (this.lastGreen != green) {
                            instance.themeColour = new Color(instance.themeColour.getRed(), green, instance.themeColour.getBlue());
                            this.lastGreen = green;
                        }

                        if (this.lastBlue != blue) {
                            instance.themeColour = new Color(instance.themeColour.getRed(), instance.themeColour.getGreen(), blue);
                            this.lastBlue = blue;
                        }
                    }

                    Thread.sleep(100);
                }
            } catch (Exception exception) {
                System.out.println(exception);
            }
        }).start();
    }
}
