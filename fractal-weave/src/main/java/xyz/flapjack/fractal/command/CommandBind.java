package xyz.flapjack.fractal.command;

/* Custom. */
import net.minecraft.util.EnumChatFormatting;
import xyz.flapjack.fractal.bridge.impl.Chat;
import xyz.flapjack.fractal.modules.Module;
import xyz.flapjack.Access;

/* Weave. */
import net.weavemc.loader.api.command.Command;

public class CommandBind extends Command {
    public CommandBind() {
        super("bind");
    }

    @Override
    public void handle(String[] args) {
        if (args == null) {
            Chat.sendChatMessage(EnumChatFormatting.DARK_AQUA +
                    "[" +
                    EnumChatFormatting.WHITE +
                    "Fractal" +
                    EnumChatFormatting.DARK_AQUA +
                    "]" +
                    EnumChatFormatting.WHITE + " Invalid arguments.");
            return;
        }

        if (args.length < 2) {
            Chat.sendChatMessage(EnumChatFormatting.DARK_AQUA +
                    "[" +
                    EnumChatFormatting.WHITE +
                    "Fractal" +
                    EnumChatFormatting.DARK_AQUA +
                    "]" +
                    EnumChatFormatting.WHITE + " Invalid argument, specify a module and bind: /bind <Module> <key>");
            return;
        }

        if (args[0] == null || args [1] == null) {
            Chat.sendChatMessage(EnumChatFormatting.DARK_AQUA +
                    "[" +
                    EnumChatFormatting.WHITE +
                    "Fractal" +
                    EnumChatFormatting.DARK_AQUA +
                    "]" +
                    EnumChatFormatting.WHITE + " Invalid argument, specify a module and bind: /bind <Module> <key>");
            return;
        }

        try {
            for (Module module: Access.Instance.getModules()) {
                if (module.title.replace(" ", "").equalsIgnoreCase(args[0])) {
                    for (Codes code: Codes.values()) {
                        if (code.character.equalsIgnoreCase(args[1])) {
                            module.keyBind = code.key;
                            module.displayBind = code.character;

                            Chat.sendChatMessage(EnumChatFormatting.DARK_AQUA +
                                    "[" +
                                    EnumChatFormatting.WHITE +
                                    "Fractal" +
                                    EnumChatFormatting.DARK_AQUA +
                                    "]" +
                                    EnumChatFormatting.WHITE + String.format(" Rebinded %s to %s.", module.title, code.character));

                            return;
                        }
                    }
                }
            }

            Chat.sendChatMessage(EnumChatFormatting.DARK_AQUA +
                    "[" +
                    EnumChatFormatting.WHITE +
                    "Fractal" +
                    EnumChatFormatting.DARK_AQUA +
                    "]" +
                    EnumChatFormatting.WHITE + " No module found.");
        } catch (Exception ignored) {
            Chat.sendChatMessage(EnumChatFormatting.DARK_AQUA +
                    "[" +
                    EnumChatFormatting.WHITE +
                    "Fractal" +
                    EnumChatFormatting.DARK_AQUA +
                    "]" +
                    EnumChatFormatting.WHITE + " Invalid argument, only keys 0-9, letters a-z and F keys are allowed.");
        }
    }

    private enum Codes {
        KEY_1(2, "1"),
        KEY_2(3, "2"),
        KEY_3(4, "3"),
        KEY_4(5, "4"),
        KEY_5(6, "5"),
        KEY_6(7, "6"),
        KEY_7(8, "7"),
        KEY_8(9, "8"),
        KEY_9(10, "9"),
        KEY_0(11, "0"),
        KEY_MINUS(12, "-"),
        KEY_EQUALS(13, "="),
        KEY_Q(16, "Q"),
        KEY_W(17, "W"),
        KEY_E(18, "E"),
        KEY_R(19, "R"),
        KEY_T(20, "T"),
        KEY_Y(21, "Y"),
        KEY_U(22, "U"),
        KEY_I(23, "I"),
        KEY_O(24, "O"),
        KEY_P(25, "P"),
        KEY_A(30, "A"),
        KEY_S(31, "S"),
        KEY_D(32, "D"),
        KEY_F(33, "F"),
        KEY_G(34, "G"),
        KEY_H(35, "H"),
        KEY_J(36, "J"),
        KEY_K(37, "K"),
        KEY_L(38, "L"),
        KEY_Z(44, "Z"),
        KEY_X(45, "X"),
        KEY_C(46, "C"),
        KEY_V(47, "V"),
        KEY_B(48, "B"),
        KEY_N(49, "N"),
        KEY_M(50, "M"),
        KEY_F1(59, "F1"),
        KEY_F2(60, "F2"),
        KEY_F3(61, "F3"),
        KEY_F4(62, "F4"),
        KEY_F5(63, "F5"),
        KEY_F6(64, "F6"),
        KEY_F7(65, "F7"),
        KEY_F8(66, "F8"),
        KEY_F9(67, "F9"),
        KEY_F10(68, "F10");

        public final int key;
        public final String character;

        Codes(final int key, final String char1) {
            this.key = key;
            this.character = char1;
        }
    }
}
