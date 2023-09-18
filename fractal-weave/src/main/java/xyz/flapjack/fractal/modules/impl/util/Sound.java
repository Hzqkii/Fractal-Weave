package xyz.flapjack.fractal.modules.impl.util;

/* Open. */
import javax.sound.sampled.*;
import java.io.InputStream;

public class Sound implements LineListener {
    public boolean isPlaybackCompleted;

    /**
     * Plays a sound on initialization.
     * @param location the target sound location.
     */
    public Sound(final String location) {
        try {
            InputStream inputStream = this.getClass().getResourceAsStream(location);

            if (inputStream == null) {
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(inputStream);
            AudioFormat audioFormat = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);

            Clip audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);
            audioClip.start();
        } catch (Exception ignored) { }
    }

    @Override
    public void update(final LineEvent event) {
        if (LineEvent.Type.STOP == event.getType()) {
            isPlaybackCompleted = true;
        }
    }
}
