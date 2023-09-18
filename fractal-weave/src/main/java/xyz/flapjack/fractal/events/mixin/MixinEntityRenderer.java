package xyz.flapjack.fractal.events.mixin;

/* Custom. */
import xyz.flapjack.fractal.events.EventBus;
import xyz.flapjack.fractal.modules.impl.util.Random;
import xyz.flapjack.fractal.events.impl.RenderEvent;
import xyz.flapjack.fractal.events.impl.MouseEvent;
import xyz.flapjack.fractal.modules.Module;
import xyz.flapjack.Access;

/* Open. */
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.entity.Entity;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.util.*;
import com.google.common.base.Predicates;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mixin;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.List;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    EventBus eventBus = Access.Instance.getEventBus();
    
    @Shadow private Minecraft mc;

    @Shadow private Entity pointedEntity;
    @Shadow private boolean cloudFog;
    @Shadow private float farPlaneDistance;
    @Shadow private FloatBuffer fogColorBuffer;
    @Shadow private float fogColorRed;
    @Shadow private float fogColorGreen;
    @Shadow private float fogColorBlue;

    @Shadow private float bossColorModifier;
    @Shadow private float bossColorModifierPrev;

    @Shadow private float fogColor2;
    @Shadow private float fogColor1;

    @Shadow private double cameraZoom;
    @Shadow private double cameraYaw;
    @Shadow private double cameraPitch;
    @Shadow private boolean debugView;

    @Shadow private int rendererUpdateCount;
    @Shadow private int debugViewDirection;

    private boolean coinToss = false;

    /**
     * Injects a MouseEvent into the getMouseOver method.
     * This is to target the minecraft reach function.
     * @param f1            argument.
     * @param callbackInfo  argument.
     */
    @Inject(method = "getMouseOver", at = @At("HEAD"), cancellable = true)
    public void injectGetMouseOverEvent(final float f1, final CallbackInfo callbackInfo) {
        MouseEvent event = new MouseEvent(MouseEvent.Type.Over);
        eventBus.call(event);

        if (event.isCancelled) {
            callbackInfo.cancel();
        }
    }

    /**
     * Injects a RenderEvent into the renderWorldPass method.
     * This is for the generic render event.
     * @param i1            argument.
     * @param f1            argument.
     * @param l1            argument.
     * @param callbackInfo  argument.
     */
    @Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target="Lnet/minecraft/client/renderer/EntityRenderer;renderHand(FI)V", shift = At.Shift.AFTER))
    public void injectRenderHandEvent(final int i1, final float f1, final long l1, final CallbackInfo callbackInfo) {
        eventBus.call(new RenderEvent());
    }


    /**
     * @author Fractal.
     * @reason Mixin injection.
     */
    @Overwrite
    public void getMouseOver(float f1) {
        Module module = Access.Instance.getModule("Reach");
        Entity entity = this.mc.getRenderViewEntity();

        if(entity == null || this.mc.theWorld == null) {
            return;
        }

        this.mc.mcProfiler.startSection("pick");
        this.mc.pointedEntity = null;

        double d0 = this.mc.playerController.getBlockReachDistance();
        this.coinToss = false;

        if (module.enabled) {
            if (module.getVal("Condition").equals("Global")) {
                d0 = this.getReach(module);
            } else if (module.getVal("Condition").equals("Strafing")) {
                if (Keyboard.isKeyDown(this.mc.gameSettings.keyBindLeft.getKeyCode())
                        || Keyboard.isKeyDown(this.mc.gameSettings.keyBindRight.getKeyCode())) {
                    d0 = this.getReach(module);
                } else  {
                    this.coinToss = false;
                }
            } else if (module.getVal("Condition").equals("Sprinting")) {
                if (this.mc.thePlayer.isSprinting()) {
                    d0 = this.getReach(module);
                } else  {
                    this.coinToss = false;
                }
            } else if (module.getVal("Condition").equals("Moving")) {
                if (Keyboard.isKeyDown(this.mc.gameSettings.keyBindLeft.getKeyCode())
                        || Keyboard.isKeyDown(this.mc.gameSettings.keyBindRight.getKeyCode())
                        || Keyboard.isKeyDown(this.mc.gameSettings.keyBindForward.getKeyCode())
                        || Keyboard.isKeyDown(this.mc.gameSettings.keyBindBack.getKeyCode())) {
                    d0 = this.getReach(module);
                } else  {
                    this.coinToss = false;
                }
            }
        }

        MovingObjectPosition mouseOver = this.mc.objectMouseOver;

        if ((mouseOver != null && mouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) || !this.coinToss) {
            d0 = this.mc.playerController.getBlockReachDistance();
        }

        this.mc.objectMouseOver = entity.rayTrace(d0, f1);

        double d1 = d0;
        Vec3 vec3 = entity.getPositionEyes(f1);

        boolean flag = false;
        if(this.mc.playerController.extendedReach()) {
            d0 = 6.0D;
            d1 = 6.0D;
        } else if(d0 > 3.0D && (!module.enabled || !this.coinToss)) {
            flag = true;
        }

        if(this.mc.objectMouseOver != null) {
            d1 = this.mc.objectMouseOver.hitVec.distanceTo(vec3);
        }

        Vec3 vec31 = entity.getLook(f1);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);

        this.pointedEntity = null;
        Vec3 vec33 = null;
        float f = 1.0F;

        List<Entity> list = this.mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, filter -> filter != null && filter.canBeCollidedWith()));
        double d2 = d1;

        for (Entity entity1: list) {
            float hitboxExpand = entity1.getCollisionBorderSize();

            if (module.enabled && (boolean) module.getVal("Hitbox")) {
                hitboxExpand += (int) module.getVal("Expand amount");
            }

            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(hitboxExpand, hitboxExpand, hitboxExpand);
            MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

            if (axisalignedbb.isVecInside(vec3)) {
                if (d2 >= 0.0D) {
                    this.pointedEntity = entity1;

                    vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                    d2 = 0.0D;
                }
            } else if (movingobjectposition != null) {
                double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                if (d3 < d2 || d2 == 0.0D) {
                    if (entity1 == entity.ridingEntity) {
                        if (d2 == 0.0D) {
                            this.pointedEntity = entity1;

                            vec33 = movingobjectposition.hitVec;
                        }
                    } else {
                        this.pointedEntity = entity1;

                        vec33 = movingobjectposition.hitVec;
                        d2 = d3;
                    }
                }
            }
        }

        if (this.pointedEntity != null && flag && vec3.distanceTo(vec33) > (3.0D)) {
            this.pointedEntity = null;

            this.mc.objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, Objects.requireNonNull(vec33), null, new BlockPos(vec33));
        }

        if(this.pointedEntity != null && (d2 < d1 || this.mc.objectMouseOver == null)) {
            this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity, vec33);

            if(this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame) {
                this.mc.pointedEntity = this.pointedEntity;
            }
        }

        this.mc.mcProfiler.endSection();
    }


    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    private void atHead(int int1, float partialTicks, CallbackInfo ci) {
        Entity entity = this.mc.getRenderViewEntity();
        boolean flag = false;

        if (entity instanceof EntityPlayer) {
            flag = ((EntityPlayer) entity).capabilities.isCreativeMode;
        }

        GL11.glFog(GL11.GL_FOG_COLOR, this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
        GL11.glNormal3f(0.0F, -1.0F, 0.0F);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entity, partialTicks);

        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.blindness) && (!Access.Instance.getModule("Anti Debuff").getVal("Effect").equals("Nausea") && !Access.Instance.getModule("Anti Debuff").enabled)) {
            float f1 = 5.0F;
            int i = ((EntityLivingBase)entity).getActivePotionEffect(Potion.blindness).getDuration();

            if (i < 20) {
                f1 = 5.0F + (this.farPlaneDistance - 5.0F) * (1.0F - (float)i / 20.0F);
            }

            GlStateManager.setFog(9729);

            if (int1 == -1) {
                GlStateManager.setFogStart(0.0F);
                GlStateManager.setFogEnd(f1 * 0.8F);
            } else {
                GlStateManager.setFogStart(f1 * 0.25F);
                GlStateManager.setFogEnd(f1);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance) {
                GL11.glFogi(34138, 34139);
            }
        } else if (this.cloudFog) {
            GlStateManager.setFog(2048);
            GlStateManager.setFogDensity(0.1F);
        } else if (block.getMaterial() == Material.water) {
            GlStateManager.setFog(2048);

            if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.waterBreathing)) {
                GlStateManager.setFogDensity(0.01F);
            } else {
                GlStateManager.setFogDensity(0.1F - (float) EnchantmentHelper.getRespiration(entity) * 0.03F);
            }
        } else if (block.getMaterial() == Material.lava) {
            GlStateManager.setFog(2048);
            GlStateManager.setFogDensity(2.0F);
        } else {
            float f = this.farPlaneDistance;
            GlStateManager.setFog(9729);

            if (int1 == -1)
            {
                GlStateManager.setFogStart(0.0F);
                GlStateManager.setFogEnd(f);
            } else {
                GlStateManager.setFogStart(f * 0.75F);
                GlStateManager.setFogEnd(f);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance) {
                GL11.glFogi(34138, 34139);
            }

            if (this.mc.theWorld.provider.doesXZShowFog((int)entity.posX, (int)entity.posZ)) {
                GlStateManager.setFogStart(f * 0.05F);
                GlStateManager.setFogEnd(Math.min(f, 192.0F) * 0.5F);
            }
        }

        GlStateManager.enableColorMaterial();
        GlStateManager.enableFog();
        GlStateManager.colorMaterial(1028, 4608);

        ci.cancel();
    }


    @Inject(method = "updateFogColor", at = @At("HEAD"), cancellable = true)
    private void atHead(float partialTicks, CallbackInfo ci) {
        World world = this.mc.theWorld;
        Entity entity = this.mc.getRenderViewEntity();

        float f = 0.25F + 0.75F * (float) this.mc.gameSettings.renderDistanceChunks / 32.0F;
        f = 1.0F - (float) Math.pow(f, 0.25D);

        Vec3 vec3 = world.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);

        float f1 = (float) vec3.xCoord;
        float f2 = (float) vec3.yCoord;
        float f3 = (float) vec3.zCoord;

        Vec3 vec31 = world.getFogColor(partialTicks);
        this.fogColorRed = (float) vec31.xCoord;
        this.fogColorGreen = (float) vec31.yCoord;
        this.fogColorBlue = (float) vec31.zCoord;

        if (this.mc.gameSettings.renderDistanceChunks >= 4) {
            double d0 = -1.0D;

            Vec3 vec32 = MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) > 0.0F ? new Vec3(d0, 0.0D, 0.0D) : new Vec3(1.0D, 0.0D, 0.0D);

            float f5 = (float)entity.getLook(partialTicks).dotProduct(vec32);

            if (f5 < 0.0F) {
                f5 = 0.0F;
            }

            if (f5 > 0.0F) {
                float[] afloat = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);

                if (afloat != null) {
                    f5 = f5 * afloat[3];
                    this.fogColorRed = this.fogColorRed * (1.0F - f5) + afloat[0] * f5;
                    this.fogColorGreen = this.fogColorGreen * (1.0F - f5) + afloat[1] * f5;
                    this.fogColorBlue = this.fogColorBlue * (1.0F - f5) + afloat[2] * f5;
                }
            }
        }

        this.fogColorRed += (f1 - this.fogColorRed) * f;
        this.fogColorGreen += (f2 - this.fogColorGreen) * f;
        this.fogColorBlue += (f3 - this.fogColorBlue) * f;

        float f8 = world.getRainStrength(partialTicks);

        if (f8 > 0.0F) {
            float f4 = 1.0F - f8 * 0.5F;
            float f10 = 1.0F - f8 * 0.4F;
            this.fogColorRed *= f4;
            this.fogColorGreen *= f4;
            this.fogColorBlue *= f10;
        }

        float f9 = world.getThunderStrength(partialTicks);

        if (f9 > 0.0F) {
            float f11 = 1.0F - f9 * 0.5F;
            this.fogColorRed *= f11;
            this.fogColorGreen *= f11;
            this.fogColorBlue *= f11;
        }

        Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entity, partialTicks);

        if (this.cloudFog) {
            Vec3 vec33 = world.getCloudColour(partialTicks);

            this.fogColorRed = (float) vec33.xCoord;
            this.fogColorGreen = (float) vec33.yCoord;
            this.fogColorBlue = (float) vec33.zCoord;
        }  else if (block.getMaterial() == Material.water) {
            float f12 = (float) EnchantmentHelper.getRespiration(entity) * 0.2F;

            if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(Potion.waterBreathing)) {
                f12 = f12 * 0.3F + 0.6F;
            }

            this.fogColorRed = 0.02F + f12;
            this.fogColorGreen = 0.02F + f12;
            this.fogColorBlue = 0.2F + f12;
        } else if (block.getMaterial() == Material.lava) {
            this.fogColorRed = 0.6F;
            this.fogColorGreen = 0.1F;
            this.fogColorBlue = 0.0F;
        }

        float f13 = this.fogColor2 + (this.fogColor1 - this.fogColor2) * partialTicks;

        this.fogColorRed *= f13;
        this.fogColorGreen *= f13;
        this.fogColorBlue *= f13;

        double d1 = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks) * world.provider.getVoidFogYFactor();

        if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(Potion.blindness) && (!Access.Instance.getModule("Anti Debuff").getVal("Effect").equals("Nausea") && !Access.Instance.getModule("Anti Debuff").enabled)) {
            int i = ((EntityLivingBase) entity).getActivePotionEffect(Potion.blindness).getDuration();

            if (i < 20) {
                d1 *= 1.0F - (float) i / 20.0F;
            } else {
                d1 = 0.0D;
            }
        }

        if (d1 < 1.0D) {
            if (d1 < 0.0D) {
                d1 = 0.0D;
            }

            d1 = d1 * d1;

            this.fogColorRed = (float) ((double) this.fogColorRed * d1);
            this.fogColorGreen = (float) ((double) this.fogColorGreen * d1);
            this.fogColorBlue = (float) ((double) this.fogColorBlue * d1);
        }

        if (this.bossColorModifier > 0.0F) {
            float f14 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;

            this.fogColorRed = this.fogColorRed * (1.0F - f14) + this.fogColorRed * 0.7F * f14;
            this.fogColorGreen = this.fogColorGreen * (1.0F - f14) + this.fogColorGreen * 0.6F * f14;
            this.fogColorBlue = this.fogColorBlue * (1.0F - f14) + this.fogColorBlue * 0.6F * f14;
        }

        if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(Potion.nightVision)) {
            float f15 = this.getNightVisionBrightness((EntityLivingBase) entity, partialTicks);
            float f6 = 1.0F / this.fogColorRed;

            if (f6 > 1.0F / this.fogColorGreen) {
                f6 = 1.0F / this.fogColorGreen;
            }

            if (f6 > 1.0F / this.fogColorBlue) {
                f6 = 1.0F / this.fogColorBlue;
            }

            this.fogColorRed = this.fogColorRed * (1.0F - f15) + this.fogColorRed * f6 * f15;
            this.fogColorGreen = this.fogColorGreen * (1.0F - f15) + this.fogColorGreen * f6 * f15;
            this.fogColorBlue = this.fogColorBlue * (1.0F - f15) + this.fogColorBlue * f6 * f15;
        }

        if (this.mc.gameSettings.anaglyph) {
            float f16 = (this.fogColorRed * 30.0F + this.fogColorGreen * 59.0F + this.fogColorBlue * 11.0F) / 100.0F;
            float f17 = (this.fogColorRed * 30.0F + this.fogColorGreen * 70.0F) / 100.0F;
            float f7 = (this.fogColorRed * 30.0F + this.fogColorBlue * 70.0F) / 100.0F;

            this.fogColorRed = f16;
            this.fogColorGreen = f17;
            this.fogColorBlue = f7;
        }

        GlStateManager.clearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);

        ci.cancel();
    }

    @Shadow
    private FloatBuffer setFogColorBuffer(float red, float green, float blue, float alpha) {
        return null;
    }

    @Shadow
    private float getNightVisionBrightness(EntityLivingBase entitylivingbaseIn, float partialTicks) {
        return 0.0F;
    }

    @Shadow
    private float getFOVModifier(float partialTicks, boolean bool1) {
        return 0.0F;
    }
    @Shadow
    private void hurtCameraEffect(float partialTicks) {
        return;
    }

    @Shadow
    private void orientCamera(float partialTicks) {
        return;
    }

    @Shadow
    private void setupViewBobbing(float partialTicks) {
        return;
    }

    /**
     * Gets the target reach of the module.
     * @param module    the reach module instance.
     * @return          the target reach of the module
     */
    private double getReach(final Module module) {
        if (Random.nextRandom(0, 100) < (int) module.getVal("Chance")) {
            this.coinToss = true;
            return Random.nextRandom((double) module.getVal("Min distance"), (double) module.getVal("Max distance"));
        }

        this.coinToss = false;
        return 3.0D;
    }
}
