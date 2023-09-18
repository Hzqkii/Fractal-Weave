package xyz.flapjack.fractal;

/* Custom. */
import xyz.flapjack.fractal.interfaces.clickgui.ClickGui;
import xyz.flapjack.fractal.events.impl.StartGameEvent;
import xyz.flapjack.fractal.keyboard.KeyboardManager;
import xyz.flapjack.fractal.events.impl.RenderEvent;
import xyz.flapjack.fractal.render.font.FontManager;
import xyz.flapjack.fractal.modules.ModuleManager;
import xyz.flapjack.fractal.command.CommandBind;
import xyz.flapjack.fractal.events.Subscribed;
import xyz.flapjack.fractal.bridge.impl.Chat;
import xyz.flapjack.Access;

/* Weave. */
import net.weavemc.loader.api.command.CommandBus;
import net.weavemc.loader.api.ModInitializer;

/* Open. */
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.client.Minecraft;
import java.net.URLConnection;
import java.net.URL;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class Fractal extends Access implements ModInitializer {
    public static Fractal INSTANCE;

    private KeyboardManager keyboardManager;
    private ModuleManager moduleManager;
    private FontManager fontManager;
    private ClickGui clickGui;

    public Color themeColour = new Color(5, 152, 98);

    @Override
    public void preInit() {
        INSTANCE = this;

        CommandBus.register(new CommandBind());
        Instance.getEventBus().subscribe(this);

        try {
            URL url = new URL(Github.version);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();

            if (!content.toString().equals(Client.version)) {
                JFrame frame = new JFrame();
                frame.setAlwaysOnTop(true);

                JOptionPane.showMessageDialog(frame, "You're on an outdated version of Fractal. This is not recommended.", "Fractal", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ignored) {
            JFrame frame = new JFrame();
            frame.setAlwaysOnTop(true);

            JOptionPane.showMessageDialog(frame, "Unable to connect to github.", "Fractal", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Subscribed(eventType = StartGameEvent.class)
    public void inst(final StartGameEvent event) {
        this.keyboardManager = new KeyboardManager();
        this.moduleManager = new ModuleManager();
        this.fontManager = new FontManager();
        this.clickGui = new ClickGui();

        this.moduleManager.init();

        this.settingSetup();
        this.configSetup();
    }

    @Subscribed(eventType = RenderEvent.class)
    public void renderTick(final RenderEvent event) {
        if (Client.firstLaunch) {
            Chat.sendChatMessage(EnumChatFormatting.DARK_AQUA +
                    "[" +
                    EnumChatFormatting.WHITE +
                    "Fractal" +
                    EnumChatFormatting.DARK_AQUA +
                    "]" +
                    EnumChatFormatting.WHITE +
                    " Welcome to Fractal!");

            Chat.sendChatMessage(EnumChatFormatting.DARK_AQUA +
                    "[" +
                    EnumChatFormatting.WHITE +
                    "Fractal" +
                    EnumChatFormatting.DARK_AQUA +
                    "]" +
                    EnumChatFormatting.WHITE +
                    " Press the INSERT key to get started.");

            Client.firstLaunch = false;
        }
    }

    /**
     * Gets the current ClickGui.
     * @return the current ClickGui.
     */
    public ClickGui getClickGui() {
        return this.clickGui;
    }

    /**
     * Gets the current FontManager.
     * @return the current FontManager.
     */
    public FontManager getFontManager() {
        return this.fontManager;
    }

    /**
     * Gets the current KeyboardManager.
     * @return the current KeyboardManager.
     */
    public KeyboardManager getKeyboardManager() {
        return this.keyboardManager;
    }

    /**
     * Gets the current ModuleManager.
     * @return the current ModuleManager.
     */
    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    /**
     * Set up the config system.
     */
    private void configSetup() {
        String dir = Utils.getLocalMinecraftPath();

        File folder = new File(dir, "fractal");
        File defaultConfig = new File(dir, "\\fractal\\default.txt");

        if (!folder.exists()) {
            Client.firstLaunch = true;

            folder.mkdir();
        }

        /*
         * Will load default config, and save it to the config location.
         * This system will soon be implemented into a cloud config system, allowing for codes sharing.
         */
        if (!defaultConfig.exists()) {
            try {
                defaultConfig.createNewFile();

                FileWriter writer = new FileWriter(defaultConfig);
                writer.write(readFile(this.getClass().getResourceAsStream("/assets/fractal/misc/default.txt")));
                writer.close();
            } catch (Exception ignored) {  }
        }
    }

    /**
     * Modify the game settings before launching.
     */
    private void settingSetup() {
        Minecraft mcInstance = Minecraft.getMinecraft();

        mcInstance.gameSettings.showDebugInfo = false;
    }

    /**
     * Reads a file from input stream.
     * @param input the input stream.
     * @return the string.
     */
    private String readFile(final InputStream input) throws IOException {
        StringBuilder sb = new StringBuilder();

        InputStreamReader isr = new InputStreamReader(input);
        BufferedReader br = new BufferedReader(isr);

        String l;
        while ((l = br.readLine()) != null) {
            sb.append(l).append("\n");
        }

        return sb.toString();
    }
}
