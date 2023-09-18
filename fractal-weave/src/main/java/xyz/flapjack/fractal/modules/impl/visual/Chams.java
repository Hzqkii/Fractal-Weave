package xyz.flapjack.fractal.modules.impl.visual;

/* Custom. */
import xyz.flapjack.fractal.events.module.ChamsEvent;
import xyz.flapjack.fractal.render.main.Effect;
import xyz.flapjack.fractal.events.Subscribed;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;

/* Open. */
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import java.awt.*;

public class Chams extends Module {
    public Chams() {
        super("Chams", "Shows players.", Category.Visual, "menu", "bindable");

        this.registerSetting(new Setting("Mode", this, null, new String[] {"Material", "Colour", "Shader"}));

        this.registerSetting(new Setting("Red", this, null, 255, 0, 255));
        this.registerSetting(new Setting("Green", this, null, 255, 0, 255));
        this.registerSetting(new Setting("Blue", this, null, 255, 0, 255));
        this.registerSetting(new Setting("Alpha", this, null, 255, 0, 255));

        this.registerSetting(new Setting("Shader type", this, null, new String[] {"Swirl", "Marble"}));
    }

    @Subscribed(eventType = ChamsEvent.class)
    public void onChamsEvent(final ChamsEvent event) {
        if (!this.enabled) {
            return;
        }

        if (event.entity == this.mcInstance.thePlayer || !(event.entity instanceof EntityPlayer)) {
            return;
        }

        if (event.state == ChamsEvent.State.Pre) {
            this.preTick();
        } else {
            this.postTick();
        }
    }

    /**
     * Pre-tick is called on the Pre state of the renderModel method.
     */
    private void preTick() {
        GL11.glEnable(32823);
        GL11.glPolygonOffset(1.0f, -1100000.0f);

        if (getModule("Chams").getVal("Mode").equals("Colour")) {
            Color colour = new Color((int) getModule("Chams").getVal("Red"),
                    (int) getModule("Chams").getVal("Green"),
                    (int) getModule("Chams").getVal("Blue"),
                    (int) getModule("Chams").getVal("Alpha"));

            float float1 = (float) (colour.getRGB() >> 16 & 255) / 255.0F;
            float float2 = (float) (colour.getRGB() >> 8 & 255) / 255.0F;
            float float3 = (float) (colour.getRGB() & 255) / 255.0F;
            float float4 = (float) (colour.getRGB() >> 24 & 255) / 255.0F;

            GlStateManager.disableTexture2D();
            GlStateManager.color(float1, float2, float3, float4);
        } else if (getModule("Chams").getVal("Mode").equals("Shader")) {
            GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
            GL11.glEnable(GL11.GL_STENCIL_TEST);
            GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
            GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
            GL11.glStencilMask(0xFF);
        }
    }

    /**
     * Post-tick is called on the Post state of the renderModel method.
     */
    private void postTick() {
        if (getModule("Chams").getVal("Mode").equals("Colour")) {
            GlStateManager.enableTexture2D();
        } else if (getModule("Chams").getVal("Mode").equals("Shader")) {
            GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            String type = (String) getModule("Chams").getVal("Shader type");

            switch (type) {
                case "Swirl" -> {
                    Effect.swirlProgram.render();
                }
                case "Marble" -> {
                    Effect.marbleProgram.render();
                }
            }

            GL11.glDisable(GL11.GL_STENCIL_TEST);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        GL11.glDisable(32823);
        GL11.glPolygonOffset(1.0f, 1100000.0f);
    }
}