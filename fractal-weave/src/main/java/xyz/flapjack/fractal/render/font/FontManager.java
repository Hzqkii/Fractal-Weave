package xyz.flapjack.fractal.render.font;

/* Custom. */
import xyz.flapjack.fractal.Fractal;

/* Open. */
import net.minecraft.client.renderer.GlStateManager;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
import java.io.InputStream;
import java.util.HashMap;
import org.lwjgl.opengl.GL11;

public class FontManager extends Fractal {
    private final HashMap<String, Font> fonts = new HashMap<>();
    private final HashMap<String, FontRenderer> fontRenderers = new HashMap<>();
    private final Font defaultFont;

    private final FontRenderer defaultFontRenderer;

    /**
     * Loads fonts into the font pool.
     */
    public FontManager() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        ConcurrentLinkedQueue<Font.TextureData> textureQueue = new ConcurrentLinkedQueue<>();

        this.defaultFont = new Font(executor, textureQueue, new java.awt.Font("Verdana", java.awt.Font.PLAIN, 18));

        this.load("Montserrat", "/assets/fractal/font/font-montserrat.ttf", new int[] {14, 16, 18, 19, 20, 22, 24, 36, 48, 72, 96}, executor, textureQueue);
        this.load("Light", "/assets/fractal/font/font-light.ttf", new int[] {14, 16, 18, 19, 20, 22, 24, 36, 48, 72, 96}, executor, textureQueue);

        this.defaultFontRenderer = new FontRenderer(this.getFont("Light 18"), this.getFont("Medium 18"), this.getFont("Bold 18"), this.getFont("Medium 18"), this.getFont("Bold 18"));
        this.fontRenderers.put("Default", this.defaultFontRenderer);

        executor.shutdown();

        while (!executor.isTerminated()) {
            try {
                Thread.sleep(10L);
            } catch (Exception ignored) {}

            while (!textureQueue.isEmpty()) {
                Font.TextureData textureData = textureQueue.poll();

                GlStateManager.bindTexture(textureData.textureId());
                GL11.glTexParameteri(3553, 10241, 9728);
                GL11.glTexParameteri(3553, 10240, 9728);
                GL11.glTexImage2D(3553, 0, 6408, textureData.width(), textureData.height(), 0, 6408, 5121, textureData.buffer());
            }
        }
    }

    /**
     * Gets a font from the private storage.
     * @param key   the desired font key.
     * @return      the requested font.
     */
    public Font getFont(String key) {
        return this.fonts.getOrDefault(key, this.defaultFont);
    }

    /**
     * Get default font renderer.
     * @param size  the size of font.
     * @return      the renderer.
     */
    public FontRenderer getDefaultFontRenderer(int size) {
        return new FontRenderer(
                this.getFont("Light " + size),
                this.getFont("Medium " + size),
                this.getFont("Bold " + size),
                this.getFont("Medium " + size),
                this.getFont("Bold " + size)
        );
    }

    /**
     * Gets the default font renderer
     * @return the default font renderer
     */
    public FontRenderer getDefaultFontRenderer() {
        return this.defaultFontRenderer;
    }

    /**
     * Gets the specified font renderer.
     * @param key   the name of the font.
     * @return      the default font renderer
     */
    public FontRenderer getFontRenderer(final String key) {
        return this.fontRenderers.get(key);
    }

    /**
     * Loads a font from resources and stores the font data.
     * @param key           the desired key for the font name.
     * @param location      the location of the font.
     * @param metrics       the metrics of the font.
     * @param executor      the current services.
     * @param textureQueue  the current texture queue.
     */
    private void load(String key, String location, int[] metrics, ThreadPoolExecutor executor, ConcurrentLinkedQueue<Font.TextureData> textureQueue) {
        try {
            for (int i: metrics) {
                InputStream stream = this.getClass().getResourceAsStream(location);

                if (stream == null) {
                    return;
                }

                java.awt.Font loaded = java.awt.Font.createFont(0, stream);
                loaded = loaded.deriveFont(java.awt.Font.PLAIN, (float) i);

                this.fonts.put(String.format("%s %d", key, i), new Font(executor, textureQueue, loaded));
                this.fontRenderers.put(String.format("%s %d", key, i), new FontRenderer(this.getFont(String.format("%s %d", key, i))));
            }
        } catch (Exception ignored) { }
    }
}