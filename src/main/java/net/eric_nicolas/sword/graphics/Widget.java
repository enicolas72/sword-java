package net.eric_nicolas.sword.graphics;

/**
 * Widget - Base class for all gadget components.
 * Widgets live inside a Canvas; compile-time safety is enforced by Canvas.add(Widget).
 */
public class Widget extends TZone {

    public Widget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
}
