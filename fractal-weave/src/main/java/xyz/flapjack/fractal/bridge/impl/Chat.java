package xyz.flapjack.fractal.bridge.impl;

/* Custom. */
import xyz.flapjack.fractal.bridge.Bridge;

/* Open. */
import net.minecraft.util.ChatComponentText;
import net.minecraft.client.Minecraft;

public class Chat extends Bridge {
    /**
     * Sends a chat message only to the player.
     * @param text the text to send.
     */
    public static void sendChatMessage(final String text) {
        Minecraft mcInstance = Minecraft.getMinecraft();

        mcInstance.thePlayer.addChatMessage(new ChatComponentText(text));
    }
}
