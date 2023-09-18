package xyz.flapjack.fractal.interfaces.clickgui.components;

public class HUDComponent extends UIComponent{
    public final String module;

    public boolean dragging = false;
    public int mouseDragX;
    public int mouseDragY;

    /**
     * Creates a HUD component.
     * @param width the width.
     * @param height the height.
     * @param posX the posX.
     * @param posY the posY.
     * @param module the module.
     */
    public HUDComponent(final int width, final int height, final int posX, final int posY, final String module) {
        super(width, height, posX, posY);

        this.module = module;

        Instance.getInstance().getModuleManager().hudComponents.add(this);
    }

    @Override
    public void mouseUp(int posX, int posY, int button) {
        this.dragging = false;
    }
}
