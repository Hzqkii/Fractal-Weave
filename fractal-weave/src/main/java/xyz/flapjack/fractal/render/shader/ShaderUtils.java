package xyz.flapjack.fractal.render.shader;

/* Custom. */
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import xyz.flapjack.fractal.render.Engine;

/* Open. */
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL20.*;

public abstract class ShaderUtils extends Engine {
    protected final int program;

    protected Framebuffer buffer = new Framebuffer(1, 1, false);

    /**
     * Creates an instance of the ShaderUtil.
     * @param fragmentShaderPath    the target path to the fragment shader.
     * @param vertexShaderPath      the target path to the vertex shader.
     */
    public ShaderUtils(final String fragmentShaderPath, final String vertexShaderPath) {
        this.program = this.createProgram(fragmentShaderPath, vertexShaderPath);
    }

    /**
     * Renders a simple GL shader to a screen sized box.
     */
    public void render() {
        update();

        glUseProgram(this.program);

        GL20.glUniform2f(this.program, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());

        float posX = -10f;
        float posY = -10f;
        float width = 20f;
        float height = 20f;

        GL11.glBegin(GL_QUADS);
        GL11.glVertex2f(posX, posY);
        GL11.glVertex2f(posX + width, posY);
        GL11.glVertex2f(posX + width, posY + height);
        GL11.glVertex2f(posX, posY + height);
        GL11.glEnd();

        glUseProgram(0);
    }

    /**
     * Creates a shader program.
     * @param fragmentShaderPath    the path to the fsh / frag file.
     * @param vertexShaderPath      the path to the vsh file.
     * @return                      the integer compiled program.
     */
    public int createProgram(final String fragmentShaderPath, final String vertexShaderPath) {
        int program = glCreateProgram();

        try {
            int fragShader = createShader(ShaderUtils.class.getResourceAsStream(fragmentShaderPath), GL_FRAGMENT_SHADER);
            glAttachShader(program, fragShader);

            int vertexShader = createShader(ShaderUtils.class.getResourceAsStream(vertexShaderPath), GL_VERTEX_SHADER);
            glAttachShader(program, vertexShader);
        } catch (IOException ignored) {
            return 0;
        }

        glLinkProgram(program);

        return program;
    }

    /**
     * Reads a shader from a file stream.
     * @param input the input file.
     * @return      the file as a string.
     */
    private String readShader(final InputStream input) throws IOException {
        StringBuilder sb = new StringBuilder();

        InputStreamReader isr = new InputStreamReader(input);
        BufferedReader br = new BufferedReader(isr);

        String l;
        while ((l = br.readLine()) != null) {
            sb.append(l).append("\n");
        }

        return sb.toString();
    }

    /**
     * Creates the GL shader.
     * @param input the input stream.
     * @param type  the type of shader.
     * @return      the shader program.
     */
    public int createShader(final InputStream input, final int type) throws IOException {
        int shader = glCreateShader(type);

        glShaderSource(shader, readShader(input));
        glCompileShader(shader);

        return shader;
    }
}
