package xyz.flapjack.fractal.render.main;

/* Custom. */
import xyz.flapjack.fractal.render.shader.impl.*;
import xyz.flapjack.fractal.render.Engine;

public class Effect extends Engine {
    public static final BlurProgram blurProgram = new BlurProgram();
    public static final Swirl swirlProgram = new Swirl();
    public static final Marble marbleProgram = new Marble();
}
