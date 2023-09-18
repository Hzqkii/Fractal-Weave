package xyz.flapjack.fractal.render.shader.impl;

/* Custom. */
import xyz.flapjack.fractal.render.shader.ShaderUtils;

public class Marble extends ShaderUtils {
    public Marble() {
        super("/assets/fractal/shaders/marble/marble.fsh", "/assets/fractal/shaders/marble/vertex.vsh");
    }
}
