package xyz.flapjack.fractal.modules.impl.player;

/* Custom. */
import xyz.flapjack.fractal.events.impl.RenderEvent;
import xyz.flapjack.fractal.modules.impl.util.Random;
import xyz.flapjack.fractal.bridge.impl.Player;
import xyz.flapjack.fractal.events.Subscribed;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;

/* Open. */
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.EnumFacing;
import net.minecraft.item.ItemBlock;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Mouse;
import java.util.ArrayList;

public class AutoPlace extends Module {
    private long lastClick = 0L;

    public AutoPlace() {
        super("Auto Place", "Places blocks.", Category.Player, "menu", "bindable");

        this.registerSetting(new Setting("Faces", this, null, new String[] { "All", "Side", "Top" }));
        this.registerSetting(new Setting("Delay", this, null, 50, 35, 200));
    }

    @Subscribed(eventType = RenderEvent.class)
    public void renderTick(final RenderEvent event) {
        if (this.mcInstance.thePlayer == null) {
            return;
        }
        if (this.mcInstance.thePlayer.getCurrentEquippedItem() == null) {
            return;
        }

        /*
         * These checks are helled back, as they require thePlayer to not be null.
         */
        ArrayList<Boolean> checks = new ArrayList<>();
        checks.add(this.mcInstance.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock);
        checks.add(Mouse.isButtonDown(1));
        checks.add(this.enabled);

        if (this.massCheck(checks)) {
            return;
        }

        MovingObjectPosition obj = this.mcInstance.objectMouseOver;
        if (obj == null) {
            return;
        }

        if (obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos pos = obj.getBlockPos();

            if (this.mcInstance.theWorld.getBlockState(pos) == null
                    || this.mcInstance.theWorld.getBlockState(pos) instanceof BlockAir
                    || this.mcInstance.theWorld.getBlockState(pos) instanceof BlockLiquid) {
                return;
            }

            if (this.getVal("Faces").equals("Side")) {
                if (obj.sideHit == EnumFacing.DOWN || obj.sideHit == EnumFacing.UP) {
                    return;
                }
            } else if (this.getVal("Faces").equals("Top")) {
                if (obj.sideHit == EnumFacing.NORTH
                        || obj.sideHit == EnumFacing.EAST
                        || obj.sideHit == EnumFacing.SOUTH
                        || obj.sideHit == EnumFacing.WEST) {
                    return;
                }
            }

            if (System.currentTimeMillis() > this.lastClick + (int) this.getVal("Delay")) {
                this.send();

                this.lastClick = System.currentTimeMillis();
            }
        }
    }

    /**
     * Sends a click.
     */
    private void send() {
        new Thread(() -> {
            try {
                Player.mouse(1, true);
                this.mcInstance.thePlayer.swingItem();
                KeyBinding.onTick(this.mcInstance.gameSettings.keyBindUseItem.getKeyCode());

                Thread.sleep((int) Random.simpleRandom(1, 5));

                this.mcInstance.getItemRenderer().resetEquippedProgress();
                Player.mouse(1, false);
            } catch (Exception ignored) { }
        }).start();
    }
}
