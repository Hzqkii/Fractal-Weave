package xyz.flapjack.fractal.modules.impl.visual;

/* Custom. */
import xyz.flapjack.fractal.events.impl.RenderOverlayEvent;
import xyz.flapjack.fractal.render.main.Simple;
import xyz.flapjack.fractal.events.Subscribed;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;
import xyz.flapjack.fractal.Fractal;

/* Open. */
import net.minecraft.client.gui.ScaledResolution;
import java.util.ArrayList;
import java.awt.*;

public class ClientArrayList extends Module {
    private static final Color standardColour = new Color(17, 17, 17);

    public ClientArrayList() {
        super("Arraylist", "Displays enabled modules.", Category.Visual, "menu", "bindable");


        Setting padding = new Setting("Padding", this, null, true);
        this.registerSetting(padding);
        this.registerSetting(new Setting("Amount", this, padding, 5, 0, 30));

        Setting margin = new Setting("Sidebar", this, null, true);
        this.registerSetting(margin);
    }

    @Subscribed(eventType = RenderOverlayEvent.class)
    public void renderOverlayTick(final RenderOverlayEvent event) {
        if (!this.enabled) {
            return;
        }

        ScaledResolution scaledResolution = new ScaledResolution(this.mcInstance);
        ArrayList<String[]> modules = new ArrayList<>();

        for (Module module: Instance.getModules()) {
            String descriptor = "";

            /*
             * Specialised details per module.
             */
            switch (module.title) {
                case "Aim Assist" -> descriptor = "S: " + (int) module.getVal("Speed");
                case "Auto Clicker", "Right Clicker" -> descriptor = "C: " + (int) module.getVal("CPS");
                case "Blockhit", "WTap" -> descriptor = "F: " + (int) module.getVal("Frequency");
                case "Double Clicker" -> descriptor = "C: " + (int) module.getVal("Chance");
                case "Reach" -> descriptor = "D: " +  String.format("%.2f", (double) module.getVal("Min distance")) + " - " + String.format("%.2f", (double) module.getVal("Max distance"));
                case "Velocity" -> descriptor = "H: " + (int) module.getVal("Horizontal") + "% V: " + module.getVal("Vertical") + "%";
                case "Fastplace" -> descriptor = "D: " + (int) module.getVal("Delay");
                case "Chams", "ESP", "Antibot" -> descriptor = "M: " + (String) module.getVal("Mode");
            }

            if(module.enabled) {
                modules.add(new String[] {module.title + " " + descriptor, module.title, descriptor});
            }
        }

        boolean swapped = true;
        while (swapped) {
            swapped = false;
            for (int i = 0; i < modules.size() - 1; i++) {
                if (Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").getWidth(modules.get(i)[0]) < Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").getWidth(modules.get(i + 1)[0])) {
                    String[] temp = modules.get(i);

                    modules.set(i, modules.get(i + 1));
                    modules.set(i + 1, temp);

                    swapped = true;
                }
            }
        }

        int padding = (int) this.getVal("Amount");
        int posY = padding;

        Fractal instance = Instance.getInstance();

        for (String[] string: modules) {
            Module module = getModule(string[1]);
            if (module == null) {
                return;
            }

            int width = (int) Math.ceil(instance.getFontManager().getFontRenderer("Montserrat 14").getWidth(string[0])) + 10;

            int posX = scaledResolution.getScaledWidth() - (int) (instance.getFontManager().getFontRenderer("Montserrat 14").getWidth(string[0]) + 10) - padding;
            Simple.drawRect(posX, posY, width, 11, new Color(standardColour.getRed(), standardColour.getGreen(), standardColour.getBlue(), 100).getRGB());

            if ((boolean) this.getVal("Sidebar")) {
                posX = scaledResolution.getScaledWidth() - padding;

                Simple.drawRect(posX, posY, 2, 11, new Color(instance.themeColour.getRed(), instance.themeColour.getGreen(), instance.themeColour.getBlue()).getRGB());
            }

            instance.getFontManager().getFontRenderer("Montserrat 14").drawStringWithShadow(string[1], scaledResolution.getScaledWidth() - (instance.getFontManager().getFontRenderer("Montserrat 14").getWidth(string[0]) + 5) - padding, posY + 3, new Color(instance.themeColour.getRed(), instance.themeColour.getGreen(), instance.themeColour.getBlue()).getRGB());
            instance.getFontManager().getFontRenderer("Montserrat 14").drawStringWithShadow(string[2], scaledResolution.getScaledWidth() - (instance.getFontManager().getFontRenderer("Montserrat 14").getWidth(string[2]) + 5) - padding, posY + 3, new Color(200, 200, 200).getRGB());

            posY += 11;
        }
    }
}