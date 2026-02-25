package net.eric_nicolas.sword.ui.base;

/**
 * Widget - Base class for all gadget components.
 * Widgets live inside a Canvas; compile-time safety is enforced by Canvas.add(Widget).
 */
public class Widget extends ScreenArea {

    protected boolean enabled = true;

    public Widget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
