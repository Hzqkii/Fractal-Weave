package xyz.flapjack.fractal.events;

public class FractalEvent {
    public boolean isCancelled = false;
    public boolean isRecognised = true;

    /**
     * Sets an event cancel state.
     * @param isCancelled argument.
     */
    public void setState(final boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}
