package xyz.flapjack.fractal.events;

/* Open. */
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribed {
    Class<? extends FractalEvent> eventType();
}
