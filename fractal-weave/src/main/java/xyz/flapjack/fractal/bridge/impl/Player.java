package xyz.flapjack.fractal.bridge.impl;

/* Custom. */
import xyz.flapjack.fractal.bridge.Bridge;

/* Open. */
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.Minecraft;

public class Player extends Bridge {
    /**
     * Checks whether the player is in game.
     * @return if the player is in game.
     */
    public static boolean inGame() {
        Minecraft mcInstance = Minecraft.getMinecraft();

        return mcInstance.thePlayer != null && mcInstance.theWorld != null;
    }

    /**
     * Controls the player's mouse.
     * @param button the mouse button.
     * @param status the status of the key.
     */
    public static void mouse(final int button, final boolean status) {
        Minecraft mcInstance = Minecraft.getMinecraft();

        if (button == 0) {
            KeyBinding.setKeyBindState(mcInstance.gameSettings.keyBindAttack.getKeyCode(), status);
        } else {
            KeyBinding.setKeyBindState(mcInstance.gameSettings.keyBindUseItem.getKeyCode(), status);
        }
    }
}
