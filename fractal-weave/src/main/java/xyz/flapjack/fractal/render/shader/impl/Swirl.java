package xyz.flapjack.fractal.render.shader.impl;

/* Custom. */
import xyz.flapjack.fractal.render.shader.ShaderUtils;

public class Swirl extends ShaderUtils {
    public Swirl() {
        super("/assets/fractal/shaders/swirl/swirl.fsh", "/assets/fractal/shaders/swirl/vertex.vsh");
    }
}
