package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.base.PaintContext;

/**
 * TItemBox - Base class for checkbox and radio button items.
 */
public class ItemBox extends AbstractButton {

    protected String text;

    public ItemBox() {
        this(0, 0, 100, "");
    }

    public ItemBox(int x, int y, int width, String text) {
        super(x, y, width, 20, 0, 0);
        this.text = text != null ? text : "";
    }

    @Override
    protected void paint(PaintContext ctx) {
        drawInside(ctx, 0);
    }

    @Override
    protected void drawInside(PaintContext ctx, int offset) {
        // Override in subclasses (CheckBox, RadioBox)
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
