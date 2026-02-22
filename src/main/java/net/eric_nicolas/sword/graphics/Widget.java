package net.eric_nicolas.sword.graphics;

import net.eric_nicolas.sword.mechanism.TAtom;

/**
 * Widget - Base class for all gadget components.
 * Widgets live inside a Canvas; compile-time safety is enforced by Canvas.add(Widget).
 */
public class Widget extends TZone {

    public Widget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    /**
     * Attach this widget to a parent in the object tree without using the
     * TAtom sibling-list machinery.  Only Canvas.add() should call this.
     */
    void setParent(TAtom parent) {
        _Father = parent;
    }
}
