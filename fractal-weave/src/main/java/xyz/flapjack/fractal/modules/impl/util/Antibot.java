package xyz.flapjack.fractal.modules.impl.util;

/* Open. */
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import java.util.ArrayList;

public class Antibot {
    /**
     * Checks weather a target is a bot or not.
     * @param player    the player to check.
     * @return          boolean indicating whether the player is a bot or not.
     */
    public static boolean isBot(final EntityPlayer player) {
        Minecraft mcInstance = Minecraft.getMinecraft();
        String special = String.valueOf('\u00a7');

        if (player != null) {
            EntityPlayer thePlayer = mcInstance.thePlayer;
            if (!player.equals(thePlayer)) {
                String name = player.getDisplayName().getFormattedText();

                return name.equalsIgnoreCase(special + "r" + player.getName() + special + "r")
                        || name.contains("[NPC]")
                        || networkCheck(player);
            }
        }

        return false;
    }

    /**
     * Watchdog antibot.
     * @param target    the target player.
     * @return          boolean indicating whether the player is a bot or not.
     */
    private static boolean networkCheck(final EntityPlayer target) {
        Minecraft mcInstance = Minecraft.getMinecraft();

        ArrayList<EntityPlayer> list = new ArrayList<>();

        for (EntityPlayer player: mcInstance.theWorld.playerEntities) {
            NetworkPlayerInfo info = mcInstance.getNetHandler().getPlayerInfo(player.getUniqueID());

            if (info == null) {
                list.add(player);
            } else {
                try {
                    list.remove(player);
                } catch (Exception ignored) { }
            }
        }

        return list.contains(target);
    }
}
