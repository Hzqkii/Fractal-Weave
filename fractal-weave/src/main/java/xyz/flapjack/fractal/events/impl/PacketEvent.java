package xyz.flapjack.fractal.events.impl;

/* Custom. */
import xyz.flapjack.fractal.events.FractalEvent;

/* Open. */
import net.minecraft.network.Packet;

public final class PacketEvent extends FractalEvent {
    public final Packet<?> packet;

    /**
     * Specified packet information.
     * @param packet argument.
     */
    public PacketEvent(final Packet<?> packet) {
        this.packet = packet;
    }
}
