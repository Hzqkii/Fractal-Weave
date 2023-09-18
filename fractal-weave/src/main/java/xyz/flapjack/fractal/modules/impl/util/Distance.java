package xyz.flapjack.fractal.modules.impl.util;

/* Open. */
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class Distance {
    /**
     * Calculates the distance to the entity.
     * @param entity    the target entity.
     * @return          the distance to the entity.
     */
    public static double distanceToEntity(final EntityPlayer entity) {
        Minecraft mcInstance = Minecraft.getMinecraft();

        float offsetX = (float) (entity.posX - mcInstance.thePlayer.posX);
        float offsetZ = (float) (entity.posZ - mcInstance.thePlayer.posZ);

        return MathHelper.sqrt_double(offsetX * offsetX + offsetZ * offsetZ);
    }

    /**
     * Calculates the distance to the specified positions.
     * @param posX      the target posX.
     * @param posZ      the target posZ.
     * @return          the distance to the positions.
     */
    public static double distanceToPoses(final double posX, final double posZ) {
        Minecraft mcInstance = Minecraft.getMinecraft();

        float offsetX = (float) (posX - mcInstance.thePlayer.posX);
        float offsetZ = (float) (posZ - mcInstance.thePlayer.posZ);

        return MathHelper.sqrt_double(offsetX * offsetX + offsetZ * offsetZ);
    }
}
