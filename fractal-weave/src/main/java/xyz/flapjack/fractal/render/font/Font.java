package xyz.flapjack.fractal.render.font;

/* Open. */
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import net.minecraft.util.MathHelper;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.awt.*;
import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Font {
    private CharacterData[] characters;

    /**
     * Constructs the font instance.
     * @param executor      the current executor.
     * @param textureQueue  the current textureQueue.
     * @param font          the current font.
     */
    public Font(ExecutorService executor, ConcurrentLinkedQueue<TextureData> textureQueue, java.awt.Font font) {
        int[] textureIds = new int[256];
        for (int i = 0; i < 256; i++)
            textureIds[i] = GL11.glGenTextures();

        executor.execute(() -> this.characters = this.setup(font, new CharacterData[256], textureIds, textureQueue));
    }

    /**
     * Setup instance.
     * @param font          parameter.
     * @param charData      parameter.
     * @param textureIds    parameter.
     * @param textureQueue  parameter.
     * @return              parameter.
     */
    private CharacterData[] setup(java.awt.Font font, CharacterData[] charData, int[] textureIds, ConcurrentLinkedQueue<TextureData> textureQueue) {
        java.awt.Font plainFont = font.deriveFont(java.awt.Font.PLAIN);
        BufferedImage image = new BufferedImage(1, 1, 2);
        Graphics2D graphics = (Graphics2D) image.getGraphics();

        graphics.setFont(plainFont);

        FontMetrics metrics = graphics.getFontMetrics();

        for (int i = 0; i < charData.length; i++) {
            char character = (char) i;
            Rectangle2D bounds = metrics.getStringBounds(String.valueOf(character), graphics);

            float width = (float) bounds.getWidth() + 8.0f;
            float height = (float) bounds.getHeight() + 0.0f;

            image = new BufferedImage(MathHelper.ceiling_double_int(width), MathHelper.ceiling_double_int(height), 2);
            graphics = (Graphics2D) image.getGraphics();

            graphics.setFont(plainFont);
            graphics.setColor(new Color(255, 255, 255, 0));
            graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
            graphics.setColor(Color.WHITE);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            graphics.drawString(String.valueOf(character), 4, metrics.getAscent());

            int textureId = textureIds[i];

            this.createTexture(textureId, image, textureQueue);

            charData[i] = new CharacterData(textureId, character, image.getWidth(), image.getHeight());
        }

        return charData;
    }

    /**
     * Create a texture.
     * @param textureId         parameter.
     * @param image             parameter.
     * @param textureQueue      parameter.
     */
    private void createTexture(int textureId, BufferedImage image, ConcurrentLinkedQueue<TextureData> textureQueue) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);

        for (int i = 0; i < image.getHeight(); i++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[i * image.getWidth() + x];
                buffer.put((byte) (pixel >> 16 & 0xFF));
                buffer.put((byte) (pixel >> 8 & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) (pixel >> 24 & 0xFF));
            }
        }

        buffer.flip();
        textureQueue.add(new TextureData(textureId, image.getWidth(), image.getHeight(), buffer));
    }

    /**
     * Character data record.
     * @param textureId     parameter.
     * @param character     parameter.
     * @param width         parameter.
     * @param height        parameter.
     */
    record CharacterData(int textureId, char character, float width, float height) {
        public void bind() {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);
        }
    }

    /**
     * Texture data record.
     * @param textureId parameter.
     * @param width     parameter.
     * @param height    parameter.
     * @param buffer    parameter.
     */
    record TextureData(int textureId, int width, int height, ByteBuffer buffer) { }

    /**
     * Gets the current characters.
     * @return the characters.
     */
    public CharacterData[] getCharacters() {
        return this.characters;
    }

    /**
     * Types of styling.
     */
    enum Style {
        Light,
        Plain,
        Bold,
        Italic,
        BoldItalic
    }
}