package xyz.flapjack.fractal.modules.impl.combat;

/* Custom. */
import xyz.flapjack.fractal.events.impl.RenderEvent;
import xyz.flapjack.fractal.events.impl.TickEvent;
import xyz.flapjack.fractal.modules.impl.util.*;
import xyz.flapjack.fractal.events.Subscribed;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;

/* Open. */
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.BlockLiquid;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemAxe;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.*;
import org.lwjgl.input.Mouse;
import java.util.ArrayList;

public class AimAssist extends Module {
    private float reportedYaw = 0f;
    private float yaw = 0f;

    private final float[] angles = new float[5];
    private float modifier = 1.0f;
    private long time = 0;

    private EntityPlayer lastEnemy = null;
    private EntityPlayer enemy = null;
    private boolean locked = false;

    private int lastEventX = 0;
    private int lastEventY = 0;

    private int renderScale;

    public AimAssist() {
        super("Aim Assist", "Helps aim at enemies.", Category.Combat, "menu", "bindable");

        this.registerSetting(new Setting("Mode", this, null, new String[]{ "Exponential", "Linear" }));

        this.registerSetting(new Setting("Speed", this, null, 6, 1, 10));
        this.registerSetting(new Setting("Distance", this, null, 10, 1, 20));
        this.registerSetting(new Setting("FOV", this, null, 90, 1, 360));

        Setting aimOn = new Setting("Aim on player", this, null, true);
        this.registerSetting(aimOn);
        this.registerSetting(new Setting("Multipoint", "Aims to the closest point on the enemies hitbox, to maximize reach.", this, aimOn, true));
        this.registerSetting(new Setting("Only while clicking", this, null, true));
        Setting mouse = new Setting("Only on mouse move", "Aim assist will only effect your mouse movement if you are already moving your mouse.", this, null, false);
        this.registerSetting(mouse);
        this.registerSetting(new Setting("Sensitivity", "This is how much your aim will be effected.", this, mouse, 2, 1, 5));

        this.registerSetting(new Setting("Weapon only", this, null, false));

        this.registerSetting(new Setting("Break blocks", this, null, true));
        this.registerSetting(new Setting("Target invisibles", this, null, true));
        this.registerSetting(new Setting("Lock target", this, null, true));
    }

    @Subscribed(eventType = TickEvent.class)
    public void onClientTick(final TickEvent event) {
        this.renderScale = 1;
    }

    @Subscribed(eventType = RenderEvent.class)
    public void onRenderTick(final RenderEvent event) {
        this.renderScale++;

        if (this.renderScale > 7) {
            return;
        }

        ArrayList<Boolean> checks = new ArrayList<>();
        checks.add(this.mcInstance.inGameHasFocus);
        checks.add(this.enabled);

        if (this.massCheck(checks)) {
            return;
        }

        if ((boolean) this.getVal("Only on mouse move")) {
            if (Mouse.getEventX() == this.lastEventX && Mouse.getEventY() == this.lastEventY) {
                return;
            }
        }

        if ((boolean) this.getVal("Weapon only") && this.mcInstance.thePlayer.getCurrentEquippedItem() != null) {
            Item held = this.mcInstance.thePlayer.getCurrentEquippedItem().getItem();

            if (!(held instanceof ItemSword) && !(held instanceof ItemAxe)) {
                return;
            }
        } else if ((boolean) this.getVal("Weapon only")) {
            return;
        }

        this.lastEventX = Mouse.getEventX();
        this.lastEventY = Mouse.getEventY();

        if ((boolean) this.getVal("Only while clicking")) {
            if (!Mouse.isButtonDown(0)) {
                this.lastEnemy = null;
                this.locked = false;

                return;
            }
        }

        if ((boolean) this.getVal("Break blocks") && this.mcInstance.objectMouseOver != null) {
            if (!this.locked) {
                BlockPos pos = this.mcInstance.objectMouseOver.getBlockPos();
                if (pos != null) {
                    Block block = this.mcInstance.theWorld.getBlockState(pos).getBlock();

                    if (block != Blocks.air && !(block instanceof BlockLiquid) && (block != null)) {
                        return;
                    }
                }
            }
        }

        this.enemy = this.getEnemy();

        MovingObjectPosition mouseOver = this.mcInstance.objectMouseOver;

        if (mouseOver != null && mouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            if (mouseOver.entityHit instanceof EntityPlayer) {
                if (mouseOver.entityHit == this.enemy && !((boolean) this.getVal("Aim on player"))) {
                    return;
                }
            }
        }

        if (enemy == null) {
            return;
        }

        final int randomRange = (int) Random.nextRandom(0, 2);

        double fovFromEntity = this.fovFromEntity(enemy);

        double speedHorizontal = (this.getVal("Mode").equals("Exponential") ? (fovFromEntity / 10) : (fovFromEntity > 0) ? 1 : -1) * (Random.nextRandom((double) ((int) this.getVal("Speed")) * 2 - randomRange, ((double) ((int) this.getVal("Speed")) + randomRange) * 2.5) / 50);
        if ((boolean) this.getVal("Only on mouse move")) {
            speedHorizontal *= (int) this.getVal("Sensitivity") * Math.abs(speedHorizontal) > 0 ? 10 : 1;
        }

        double complimentHorizontal = (-(speedHorizontal + fovFromEntity / (101.0D - Random.nextRandom((double) ((int) this.getVal("Speed")) - randomRange, (double) ((int) this.getVal("Speed")) + randomRange))));

        float targetYaw = (float) (this.mcInstance.thePlayer.rotationYaw + (complimentHorizontal * this.modifier));

        float x = 0f;
        float x2 = 0;

        for (float f1: this.angles) {
            x += f1;
            x2 += (f1 * f1);
        }

        float deviation = (x2 / this.angles.length) - (float) Math.pow((x / this.angles.length), 2);

        if (deviation < 2.0 && System.currentTimeMillis() > this.time + (1500 + Random.simpleRandom(0, 250))) {
            this.time = System.currentTimeMillis();
            this.modifier = (float) (Random.simpleRandom(95, 105) / 100);
        }

        for (int i = this.angles.length - 1; i > 0; i--) {
            this.angles[i] = this.angles[i - 1];
        }
        this.angles[0] = targetYaw;

        if (!(fovFromEntity < 1.0D + randomRange && fovFromEntity > -1.0D - randomRange)) {
            this.yaw += (float) (targetYaw + (Random.nextRandom(-5, 5) / 100) - this.yaw);

            this.mcInstance.thePlayer.rotationYaw = this.gcd(this.yaw, this.reportedYaw);
        }

        this.reportedYaw = this.mcInstance.thePlayer.rotationYaw;
    }

    /**
     * Returns the FOV from the entity.
     * @param entity    the target entity.
     * @return          the FOV from the entity.
     */
    private double fovFromEntity(final EntityPlayer entity) {
        return (((double) this.mcInstance.thePlayer.rotationYaw - ((
                (boolean) this.getVal("Multipoint") && (boolean) this.getVal("Aim on player"))
                ? yawOnTarget(hitboxEval(entity))
                : fovToEntity(entity))
        ) % 360.0D + 540.0D) % 360.0D - 180.0D;
    }

    /**
     * Returns the FOV of you from the entity.
     * @param entity    the target entity.
     * @return          the FOV from the entity.
     */
    private float fovToEntity(final EntityPlayer entity) {
        double posX = entity.posX;
        double posZ = entity.posZ;

        double x = (float) posX - this.mcInstance.thePlayer.posX;
        double z = (float) posZ - this.mcInstance.thePlayer.posZ;

        double yaw = Math.atan2(x, z) * 57.5;

        return (float) (yaw * -1.0D);
    }

    /**
     * Evaluates the entity's hitbox, and gets the closest point.
     * @param entity    the target entity.
     * @return          the closest point.
     */
    private Vec3 hitboxEval(final EntityPlayer entity) {
        AxisAlignedBB hitbox = entity.getEntityBoundingBox();

        double x = this.mcInstance.thePlayer.posX;
        double y = this.mcInstance.thePlayer.posY + this.mcInstance.thePlayer.getEyeHeight();
        double z = this.mcInstance.thePlayer.posZ;

        double closestX = Math.max(hitbox.minX, Math.min(x, hitbox.maxX));
        double closestY = Math.max(hitbox.minY, Math.min(y, hitbox.maxY));
        double closestZ = Math.max(hitbox.minZ, Math.min(z, hitbox.maxZ));

        return new Vec3(closestX, closestY, closestZ);
    }

    /**
     * Calculates the needed yaw to reach the targeted point.
     * @param targetPosition    the target position on the hitbox.
     * @return                  the yaw.
     */
    private float yawOnTarget(final Vec3 targetPosition) {
        double x = targetPosition.xCoord - this.mcInstance.thePlayer.posX;
        double z = targetPosition.zCoord - this.mcInstance.thePlayer.posZ;

        double yaw = Math.atan2(x, z) * 57.5;

        return (float) (yaw * -1.0D);
    }

    /**
     * Normalizes sensitivity rotations.
     * @param target    the original rotation angle.
     * @param type      the last original rotation angle.
     * @return          the new value.
     */
    private float gcd(final float target, final float type) {
        float patched = 0f;
        float delta;

        float value = (mcInstance.gameSettings.mouseSensitivity * 0.6f) + 0.2f;
        float gcd = (value * value * value) * 1.2f;

        try {
            if (type == this.reportedYaw) {
                delta = target - this.reportedYaw;
                delta -= delta % gcd;
                patched = this.reportedYaw + delta;
            }
        } catch (Exception ignored) {
            patched = 0f;
        }

        return patched;
    }

    /**
     * Gets the target enemy based off of module settings.
     * @return The target EntityPlayer.
     */
    private EntityPlayer getEnemy() {
        EntityPlayer result = null;
        double resultFov = 360;

        for (EntityPlayer player: this.mcInstance.theWorld.playerEntities) {
            if (player != this.mcInstance.thePlayer) {
                if (getModule("Antibot").enabled) {
                    if (Antibot.isBot(player)) {
                        break;
                    }
                }

                if (Math.abs(this.mcInstance.thePlayer.posY - player.posY) < 15) {
                    double distance = Distance.distanceToEntity(player);
                    if (player.isInvisible()) {
                        if ((boolean) this.getVal("Target invisibles")) {
                            if (resultFov > Math.abs(this.fovFromEntity(player)) && Math.abs(this.fovFromEntity(player)) < (int) this.getVal("FOV") && distance < (int) this.getVal("Distance")) {
                                result = player;
                                resultFov = Math.abs(this.fovFromEntity(player));
                            }
                        }
                    } else {
                        if (resultFov > Math.abs(this.fovFromEntity(player)) && Math.abs(this.fovFromEntity(player)) < (int) this.getVal("FOV") && distance < (int) this.getVal("Distance")) {
                            result = player;
                            resultFov = Math.abs(this.fovFromEntity(player));
                        }
                    }
                }
            }
        }

        /*
         * Locks the target, while conserving the Distance and FOV limits.
         */
        if ((boolean) this.getVal("Lock target") && this.lastEnemy != null) {
            if (Distance.distanceToEntity(this.lastEnemy) < (int) this.getVal("Distance") && this.fovToEntity(this.lastEnemy) < (int) this.getVal("FOV")) {
                this.locked = true;
                return this.lastEnemy;
            }
        }

        this.locked = false;
        this.lastEnemy = result;

        return result;
    }

    /**
     * Overrides the enemy getter, to allow all modules to use the same enemy.
     * @return the target entity.
     */
    @Override
    public EntityPlayer getPrivateEnemy() {
        return this.enemy;
    }
}
