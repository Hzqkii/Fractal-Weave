package xyz.flapjack.fractal.interfaces.clickgui.components.impl;

/* Custom. */
import xyz.flapjack.Access;
import xyz.flapjack.fractal.interfaces.clickgui.components.UIComponent;
import xyz.flapjack.fractal.render.main.Simple;
import xyz.flapjack.fractal.modules.Setting;
import xyz.flapjack.fractal.modules.Module;
import xyz.flapjack.fractal.Fractal;

/* Open. */
import java.util.ArrayList;
import java.awt.*;

public class MenuComponent extends UIComponent {
    public ArrayList<UIComponent> settings = new ArrayList<>();

    public static int iWidth = 115;
    public static int iHeight = 15;
    public static Color standardColour = new Color(22, 22, 25);
    public static Color hoverColour = new Color(30, 30, 30);

    public String title;
    public Module module;

    public boolean opened;
    public boolean frameEnd;
    public boolean infoDisplayed;

    private Color colour;

    public MenuComponent(Frame frameOwner, int iteration, Module module, boolean frameEnd) {
        super(iWidth, iHeight, frameOwner, iteration);

        this.module = module;
        this.frameEnd = frameEnd;
        this.title = module.title;
        this.opened = false;
        this.infoDisplayed = false;
        this.colour = standardColour;

        if (module.type.equals("trigger") || module.type.equals("bindable")) {
            this.offsets = new int[module.settings.size() + 2];
        } else {
            this.offsets = new int[module.settings.size() + 1];
        }

        this.height = iHeight;
        this.width = iWidth;

        int iter = 1;

        this.settings.add(new DescriptionComponent(this, 0, module.description));
        this.offsets[0] = DescriptionComponent.iHeight;

        for (Setting setting : module.settings) {
            switch (setting.getType()) {
                case "toggle" -> {
                    this.settings.add(new ToggleComponent(this, iter, setting));
                    this.offsets[iter] = ToggleComponent.iHeight;
                }
                case "input" -> {
                    this.settings.add(new InputComponent(this, iter, setting));
                    this.offsets[iter] = InputComponent.iHeight;
                }
                case "slider-i" -> {
                    this.settings.add(new SliderComponent(this, iter, setting, "slider-i"));
                    this.offsets[iter] = SliderComponent.iHeight;
                }
                case "slider-d" -> {
                    this.settings.add(new SliderComponent(this, iter, setting, "slider-d"));
                    this.offsets[iter] = SliderComponent.iHeight;
                }
                case "dropdown" -> {
                    this.settings.add(new DropdownComponent(this, iter, setting));
                    this.offsets[iter] = DropdownComponent.iHeight;
                }
                case "button" -> {
                    this.settings.add(new ButtonComponent(this, iter, setting));
                    this.offsets[iter] = ButtonComponent.iHeight;
                }
                case "bind" -> {
                    this.settings.add(new SubBindComponent(this, iter, module, setting));
                    this.offsets[iter] = SubBindComponent.iHeight;
                }
            }

            iter++;
        }

        if (module.type.equals("trigger") || module.type.equals("bindable")) {
            this.settings.add(new BindComponent(this, iter, module));
            this.offsets[iter] = BindComponent.iHeight;
        }
    }

    @Override
    public void render(float alpha) {
        Color colour = new Color(this.colour.getRed(), this.colour.getGreen(), this.colour.getBlue(), Math.round(255 * alpha));

        if (this.frameEnd) {
            Simple.drawRoundedRect(this.posX, this.posY, this.width, this.height, standardColour.getRGB(), (int) Instance.getModule("Click GUI").getVal("Border radius"), Simple.Rect.BASE);

            if (this.opened) {
                Simple.drawRect(this.posX, this.posY, this.width, iHeight, colour.getRGB());
            } else {
                Simple.drawRoundedRect(this.posX, this.posY, this.width, iHeight, colour.getRGB(), (int) Instance.getModule("Click GUI").getVal("Border radius"), Simple.Rect.BASE);
            }
        } else {
            Simple.drawRect(this.posX, this.posY, this.width, this.height, standardColour.getRGB());
            Simple.drawRect(this.posX, this.posY, this.width, iHeight, colour.getRGB());
        }

        colour = new Color(255, 255, 255, Math.round(255 * alpha));

        Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").drawCenteredString(this.title, this.posX + (this.width / 2.0f), this.posY + 4, colour.getRGB());
        Instance.getInstance().getFontManager().getFontRenderer("Montserrat 14").drawDependantString((this.opened ? "-" : "+"), this.posX + this.width - 5, this.posY + 4, colour.getRGB());

        if (this.opened) {
            for (UIComponent component: this.settings) {
                component.render(alpha);
            }
        } else {
            for (UIComponent component: this.settings) {
                component.reset();
            }
        }

        if (!this.frameOwner.opened) {
            for (UIComponent component: this.settings) {
                component.reset();
            }
        }
    }

    @Override
    public void renderShadow(float alpha) {
        Color colour = new Color(0, 0, 0, Math.round(150 * alpha));

        if (this.frameEnd) {
            Simple.drawRoundedRect(this.posX, this.posY, this.width, this.height, colour.getRGB(), (int) Access.Instance.getModule("Click GUI").getVal("Border radius"), Simple.Rect.BASE);
        } else {
            Simple.drawRect(this.posX, this.posY, this.width, this.height, colour.getRGB());
        }
    }

    @Override
    public void update(int posX, int posY, boolean opened, int scrollOffset) {
        int offset = Frame.height;

        for (int i = 0; i < this.iteration; i++) {
            offset += this.frameOwner.offsets[i];
        }

        setPos(this.frameOwner.posX, this.frameOwner.posY + offset + scrollOffset);
        this.hovered = over(posX, posY);

        this.colour = standardColour;
        if (!this.opened && this.hovered) {
            this.colour = hoverColour;
        }

        if (this.module.enabled) {
            this.colour = Fractal.INSTANCE.themeColour;
        }

        offset = iHeight;

        if (this.opened) {
            for (int height: this.offsets) {
                offset += height;
            }

            for (UIComponent component: this.settings) {
                component.update(posX, posY, opened, scrollOffset);
            }
        }
        this.frameOwner.offsets[this.iteration] = offset;
        this.height = offset;
    }

    @Override
    public void keyPressed(char char1, int key) {
        if (this.opened) {
            for (UIComponent component: this.settings) {
                component.keyPressed(char1, key);
            }
        }
    }

    @Override
    public void mouseDown(int posX, int posY, int button) {
        if (this.hovered) {
            if (button == 0) {
                this.module.toggle();
            } else {
                this.opened = !this.opened;
            }
        }

        if (this.opened) {
            for (UIComponent setting: this.settings) {
                setting.mouseDown(posX, posY, button);
            }
        }
    }

    @Override
    public void config() {
        for (UIComponent component: this.settings) {
            component.config();
        }
    }

    @Override
    public boolean over(int posX, int posY) {
        return (posX > this.posX &&
                posY > (this.posY) &&
                posX < (this.width + this.posX) &&
                posY < (iHeight + this.posY));
    }
}
