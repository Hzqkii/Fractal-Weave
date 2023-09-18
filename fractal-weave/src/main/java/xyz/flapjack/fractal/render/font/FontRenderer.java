package xyz.flapjack.fractal.render.font;

/* Open. */
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import java.awt.*;

public class FontRenderer {
    public final Font lightFont, plainFont, boldFont, italicFont, boldItalicFont;

    private final String colourCodes = "0123456789abcdefklmnor";
    private final int[] colours = new int[32];

    /**
     * Construct the font renderer.
     * @param lightFont         parameter.
     * @param plainFont         parameter.
     * @param boldFont          parameter.
     * @param italicFont        parameter.
     * @param boldItalicFont    parameter.
     */
    public FontRenderer(final Font lightFont, final Font plainFont, final Font boldFont, final Font italicFont, final Font boldItalicFont) {
        this.lightFont = lightFont;
        this.plainFont = plainFont;
        this.boldFont = boldFont;
        this.italicFont = italicFont;
        this.boldItalicFont = boldItalicFont;

        this.initcolours();
    }

    /**
     * Overload method for default fonts.
     * @param font the target font.
     */
    public FontRenderer(final Font font) {
        this(font, font, font, font, font);
    }

    /**
     * Draws a string to the screen using the provided FontStyle.
     * @param text      the string to draw.
     * @param x         the X position.
     * @param y         the Y position.
     * @param colour    the colour.
     * @param style     the font style to render with
     */
    public void drawString(final String text, final double x, final double y, final int colour, final Font.Style style) {
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();

        Font font = this.plainFont;
        switch (style) {
            case Light -> font = this.lightFont;
            case Bold -> font = this.boldFont;
            case Italic -> font = this.italicFont;
            case BoldItalic -> font = this.boldItalicFont;
        }

        this.renderString(font, text, (float)x, (float)y, colour);
    }

    /**
     * Draws a string to the screen using the provided FontStyle, with a shadow.
     * @param text      the string to draw.
     * @param x         the X position.
     * @param y         the Y position.
     * @param colour    the colour.
     * @param style     the font style to render with
     */
    public void drawStringWithShadow(String text, double x, double y, int colour, Font.Style style) {
        this.drawString(text, x + 0.5f, y + 0.5f, 0xFF000000, style);
        this.drawString(text, x, y, colour, style);
    }

    /**
     * Draws a string to the screen using the provided FontStyle, centered.
     * @param text      the string to draw.
     * @param x         the X position.
     * @param y         the Y position.
     * @param colour    the colour.
     * @param style     the font style to render with
     */
    public void drawCenteredString(String text, double x, double y, int colour, Font.Style style) {
        this.drawString(text, x - (this.getWidth(text) / 2), y, colour, style);
    }

    /**
     * Draws a string to the screen using the provided FontStyle, centered with shadow.
     * @param text      the string to draw.
     * @param x         the X position.
     * @param y         the Y position.
     * @param colour    the colour.
     * @param style     the font style to render with
     */
    public void drawCenteredStringWithShadow(String text, double x, double y, int colour, Font.Style style) {
        this.drawCenteredString(text, x + 0.5f, y + 0.5f, 0xFF000000, style);
        this.drawCenteredString(text, x, y, colour, style);
    }

    /**
     * Draws a string to the screen.
     * @param text      the string to draw.
     * @param x         the X position.
     * @param y         the Y position.
     * @param colour    the colour.
     */
    public void drawString(String text, double x, double y, int colour) {
        this.drawString(text, x, y, colour, Font.Style.Plain);
    }

    /**
     * Draws a string to the screen, with a shadow.
     * @param text      the string to draw.
     * @param x         the X position.
     * @param y         the Y position.
     * @param colour    the colour.
     */
    public void drawStringWithShadow(String text, double x, double y, int colour) {
        this.drawString(text, x + 0.5f, y + 0.5f, 0xFF000000);
        this.drawString(text, x, y, colour);
    }

    /**
     * Draws a string to the screen, centered.
     * @param text      the string to draw.
     * @param x         the X position.
     * @param y         the Y position.
     * @param colour    the colour.
     */
    public void drawCenteredString(String text, double x, double y, int colour) {
        this.drawString(text, x - (this.getWidth(text) / 2), y, colour);
    }

    /**
     * Draws a string to the screen, centered with a shadow.
     * @param text      the string to draw.
     * @param x         the X position.
     * @param y         the Y position.
     * @param colour    the colour.
     */
    public void drawCenteredStringWithShadow(String text, double x, double y, int colour) {
        this.drawCenteredString(text, x + 0.5f, y + 0.5f, 0xFF000000);
        this.drawCenteredString(text, x, y, colour);
    }

    /**
     * Draws a string to the screen, dependant of its width.
     * @param text      the string to draw.
     * @param x         the X position.
     * @param y         the Y position.
     * @param colour    the colour.
     */
    public void drawDependantString(String text, double x, double y, int colour) {
        this.drawString(text, x - this.getWidth(text), y, colour);
    }

    /**
     * Gets the height of the text.
     * @param text  target text.
     * @return      height value.
     */
    public float getHeight(String text) {
        return getHeight(this.plainFont, text);
    }

    /**
     * Gets the height of the given string based on the Font passed.
     * @param font  font to use for measuring dimensions
     * @param text  the string.
     * @return      the height.
     */
    public float getHeight(Font font, String text) {
        float[] bounds = getBounds(font, text);
        return bounds[1] / 2.0f - 2.0f;
    }

    public float getHeight() {
        return this.getHeight("I");
    }

    /**
     * Gets the current
     * @param text  target text.
     * @return      height value.
     */
    public float getWidth(String text) {
        return this.getWidth(this.plainFont, text);
    }

    /**
     * Gets the width of the given string based on the Font passed.
     * @param font  font to use for measuring dimensions
     * @param text  the string.
     * @return      width value.
     */
    public float getWidth(Font font, String text) {
        float[] bounds = getBounds(font, text);
        return bounds[0] + 2.0f;
    }

    /**
     * Gets the bounds of the given text. Takes account of Minecraft's chat formatting.
     * @param text  the string.
     * @return      bounds.
     */
    private float[] getBounds(Font font, String text) {
        float height = 0.0f;
        float width = 0.0f;
        boolean bold = false;
        boolean italic = false;
        Font currentFont = font;

        int i = 0;
        while (i < text.length()) {
            char character = text.charAt(i);
            if (character > 256) {
                i++;
                continue;
            }

            /* Found Minecraft Chat Format, Check if it's Bold or Italicized */
            if (character == '\u00A7' && i + 1 < text.length()) {
                int colourCodeIndex = colourCodes.indexOf(Character.toLowerCase(text.charAt(i + 1)));

                if (colourCodeIndex == 21 || (colourCodeIndex >= 0 && colourCodeIndex <= 15)) {
                    bold = false;
                    italic = false;
                    currentFont = font;
                } else if (colourCodeIndex == 17) {
                    bold = true;
                    currentFont = italic ? this.boldItalicFont : this.boldFont;
                } else if (colourCodeIndex == 20) {
                    italic = true;
                    currentFont = bold ? this.boldItalicFont : this.italicFont;
                }
                i += 2;
                continue;
            }

            i++;

            Font.CharacterData charData = currentFont.getCharacters()[character];

            height = Math.max(height, charData.height());
            width += (charData.width() - 8.0f) / 2.0f;
        }

        return new float[]{width, height};
    }

    /**
     * Renders a string to a position.
     * @param text      the string to be rendered.
     * @param x         the X position.
     * @param y         the Y position.
     * @param colour    the colour of the text.
     */
    private void renderString(Font font, String text, float x, float y, int colour) {
        if (text.length() == 0)
            return;

        x = Math.round(x * 10.0F) / 10.0F;
        y = Math.round(y * 10.0F) / 10.0F;

        GL11.glPushMatrix();
        GlStateManager.scale(0.5, 0.5, 1.0);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);

        x -= 2.0f;
        y -= 2.0f;
        x += 0.5f;
        y += 0.5f;
        x *= 2.0f;
        y *= 2.0f;

        double multiplier = 255.0;

        Color currentcolour = new Color(colour);
        GL11.glColor4d(currentcolour.getRed() / multiplier, currentcolour.getGreen() / multiplier, currentcolour.getBlue() / multiplier, (colour >> 24 & 0xFF) / 255.0);

        Font currentFont = font;
        boolean bold = false;
        boolean italic = false;

        try {
            int i = 0;
            while (i < text.length()) {
                char character = text.charAt(i);
                if (character > 256) {
                    i++;
                    continue;
                }

                if (character == '\u00A7' && i + 1 < text.length()) {
                    int colourCodeIndex = colourCodes.indexOf(Character.toLowerCase(text.charAt(i + 1)));

                    if (colourCodeIndex == 21 || (colourCodeIndex >= 0 && colourCodeIndex <= 15)) {
                        bold = false;
                        italic = false;
                        currentFont = font;

                        if (colourCodeIndex <= 15) {
                            int colourCode = this.colours[colourCodeIndex];
                            float red = (colourCode >> 16 & 0xFF) / 255.0f;
                            float green = (colourCode >> 8 & 0xFF) / 255.0f;
                            float blue = (colourCode & 0xFF) / 255.0f;

                            GL11.glColor4d(red, green, blue, 1.0f);
                        } else {
                            GL11.glColor4d(
                                    currentcolour.getRed() / multiplier,
                                    currentcolour.getGreen() / multiplier,
                                    currentcolour.getBlue() / multiplier,
                                    (colour >> 24 & 0xFF) / 255.0
                            );
                        }
                    } else if (colourCodeIndex == 17) {
                        bold = true;
                        currentFont = italic ? this.boldItalicFont : this.boldFont;
                    } else if (colourCodeIndex == 20) {
                        italic = true;
                        currentFont = bold ? this.boldItalicFont : this.italicFont;
                    }

                    i += 2;
                    continue;
                }

                i++;

                this.drawChar(character, currentFont.getCharacters(), x, y);

                if (character >= currentFont.getCharacters().length) continue;

                Font.CharacterData charData = currentFont.getCharacters()[character];
                x += charData.width() - 8.0f;
            }

        } catch (StringIndexOutOfBoundsException ignored) {
        }

        GL11.glPopMatrix();
        GlStateManager.disableBlend();
        GlStateManager.bindTexture(0);
        GlStateManager.resetColor();
    }

    /**
     * Draws the given character.
     * @param character     the character to draw.
     * @param charDataArray the character data.
     * @param x             the X position.
     * @param y             the Y position.
     */
    private void drawChar(char character, Font.CharacterData[] charDataArray, float x, float y) {
        if (character >= charDataArray.length) {
            return;
        }

        Font.CharacterData charData = charDataArray[character];
        charData.bind();

        GL11.glBegin(6);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2d(x, y);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2d(x, y + charData.height());
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2d(x + charData.width(), y + charData.height());
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2d(x + charData.width(), y);
        GL11.glEnd();
    }

    private void initcolours() {
        for (int i = 0; i < 32; i++) {
            int t = (i >> 3 & 1) * 85;
            int r = (i >> 2 & 1) * 170 + t;
            int g = (i >> 1 & 1) * 170 + t;
            int b = (i & 1) * 170 + t;

            if (i == 6) {
                r += 85;
            }

            if (i >= 16) {
                r /= 4;
                g /= 4;
                b /= 4;
            }

            this.colours[i] = ((r & 255) << 16 | (g & 255) << 8 | (b & 255));
        }
    }
}