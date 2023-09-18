package xyz.flapjack.fractal.events.mixin;

/* Custom. */
import xyz.flapjack.Access;
import xyz.flapjack.fractal.events.EventBus;
import xyz.flapjack.fractal.events.impl.PacketEvent;

/* Weave. */

/* Open. */
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {
    EventBus eventBus = Access.Instance.getEventBus();

    /**
     * Injects a PacketEvent into the sendPacket method.
     * This is to allow for modules such as Blink to modify packets.
     * @param packet        argument.
     * @param callbackInfo  argument.
     */
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void injectPacketEvent(final Packet<?> packet, final CallbackInfo callbackInfo) {
        PacketEvent event = new PacketEvent(packet);
        eventBus.call(event);

        if (event.isCancelled) {
            callbackInfo.cancel();
        }
    }
}
