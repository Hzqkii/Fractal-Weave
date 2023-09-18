package xyz.flapjack.fractal.modules.impl.visual;

/* Custom. */
import xyz.flapjack.fractal.modules.impl.util.Distance;
import xyz.flapjack.fractal.modules.impl.util.Antibot;
import xyz.flapjack.fractal.events.module.EspEvent;
import xyz.flapjack.fractal.bridge.impl.Player;
import xyz.flapjack.fractal.render.main.Simple;
import xyz.flapjack.fractal.events.Subscribed;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;

/* Open. */
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import java.awt.*;

public class ESP extends Module {
    public ESP() {
        super("Player ESP", "Renders a box around players.", Category.Visual, "menu", "bindable");

        String[] options = {"2D"};

        this.registerSetting(new Setting("Mode", this, null, options));

        Setting box = new Setting("Box", this, null, true);
        this.registerSetting(box);
        this.registerSetting(new Setting("Name", this, null, true));
        this.registerSetting(new Setting("Health", this, null, true));

        this.registerSetting(new Setting("Invisibles", this, null, true));
        this.registerSetting(new Setting("Hide Bots", this, null, true));
        this.registerSetting(new Setting("Expand", this, box, 20, 0, 35));

        this.registerSetting(new Setting("Red", this, null, 245, 0, 255));
        this.registerSetting(new Setting("Green", this, null, 245, 0, 255));
        this.registerSetting(new Setting("Blue", this, null, 245, 0, 255));
    }

    @Subscribed(eventType = EspEvent.class)
    public void onEspEvent(final EspEvent event) {
        if (!this.enabled) {
            return;
        }

        if (!Player.inGame()) {
            return;
        }

        if (!(event.entity instanceof EntityPlayer)) {
            return;
        }

        /*
         * Module specific logic.
         */
        if ((boolean) getModule("Player ESP").getVal("Invisibles") && event.entity.isInvisible()) {
            return;
        }
        if ((boolean) getModule("Player ESP").getVal("Hide bots")) {
            if (Antibot.isBot((EntityPlayer) event.entity)) {
                return;
            }
        }

        /*
         * Turns the ESP colour red when the target player is taking damage.
         */
        Module module = getModule("Player ESP");
        Color colour = event.entity.hurtTime > 0 ? Color.red : new Color((int) module.getVal("Red"), (int) module.getVal("Green"), (int) module.getVal("Blue"));

        this.renderDimensionalBox((EntityPlayer) event.entity, colour, event.partialTicks);
    }

    /**
     * Render a dimensional box around the entity.
     * @param player the entity.
     * @param colour the colour of the box.
     * @param partialTicks the partial ticks of the client.
     */
    private void renderDimensionalBox(final EntityPlayer player, final Color colour, final float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();

        double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks - this.mcInstance.getRenderManager().viewerPosX;
        double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks - this.mcInstance.getRenderManager().viewerPosY;
        double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks - this.mcInstance.getRenderManager().viewerPosZ;

        GL11.glTranslated(x, y - 0.2D, z);
        GL11.glRotated(-this.mcInstance.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);

        GL11.glScalef(0.03F, 0.03F, 0.03F);

        Color backgroundColour = new Color(0, 0, 0, 50);

        int xv = (int) getModule("Player ESP").getVal("Expand");
        int yv = 0;
        int yb = 75;

        float distancev = (float) Distance.distanceToEntity(player) / 2;

        if (distancev < 0.2f) {
            distancev = 0.2f;
        } else if (distancev > 1f) {
            distancev = 1f;
        }

        float width = 0.9f * distancev;

        if ((boolean) getModule("Player ESP").getVal("Box")) {
            Simple.drawDimensionalRectangle(-(xv) - (width / 2), yv + (width / 2), -(xv) + (width / 2), yb - (width / 2), backgroundColour.getRGB());
            Simple.drawDimensionalRectangle((xv) - (width / 2), yv + (width / 2), (xv) + (width / 2), yb - (width / 2), backgroundColour.getRGB());
            Simple.drawDimensionalRectangle(-(xv) - (width / 2), yv + (width / 2), (xv) + (width / 2), yv - (width / 2), backgroundColour.getRGB());
            Simple.drawDimensionalRectangle(-(xv) - (width / 2), yb + (width / 2), (xv) + (width / 2), yb - (width / 2), backgroundColour.getRGB());

            width = 0.5f * distancev;
            Simple.drawDimensionalRectangle(-(xv) - (width / 2), yv + (width / 2), -(xv) + (width / 2), yb - (width / 2), colour.getRGB());
            Simple.drawDimensionalRectangle((xv) - (width / 2), yv + (width / 2), (xv) + (width / 2), yb - (width / 2), colour.getRGB());
            Simple.drawDimensionalRectangle(-(xv) - (width / 2), yv + (width / 2), (xv) + (width / 2), yv - (width / 2), colour.getRGB());
            Simple.drawDimensionalRectangle(-(xv) - (width / 2), yb + (width / 2), (xv) + (width / 2), yb - (width / 2), colour.getRGB());
        }

        if ((boolean) getModule("Player ESP").getVal("Health")) {
            float health = (player.getHealth() / player.getMaxHealth()) * player.height + 0.5f;

            width = 1.2f * distancev;
            Simple.drawDimensionalRectangle((xv + 5) - (width / 2), yv - (width / 2), (xv + 5) + (width / 2), yb + (width / 2), backgroundColour.getRGB());

            width = 0.8f * distancev;
            Simple.drawDimensionalRectangle((xv + 5) - (width / 2), yv - (width / 2), (xv + 5) + (width / 2), yb + (width / 2), 1.0f - health, health, 0f);
        }

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();

        GL11.glTranslated(x, y - 0.2D, z);
        GL11.glRotated(-this.mcInstance.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glRotated(180, 0.0D, 0.0D, 1.0D);

        GlStateManager.disableDepth();

        GL11.glScalef(0.006F, 0.006F, 0.006F);

        if ((boolean) getModule("Player ESP").getVal("Name")) {
            Instance.getInstance().getFontManager().getFontRenderer("Montserrat 48").drawCenteredStringWithShadow(player.getDisplayName().getFormattedText(), 0, -(yb * 5) - 100, colour.getRGB());
        }

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }
}