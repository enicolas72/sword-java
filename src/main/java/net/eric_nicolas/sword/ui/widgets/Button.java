package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.base.PaintContext;

import java.awt.Dimension;

/**
 * TStdButton - Standard button with text label.
 */
public class Button extends AbstractButton {

    protected String text;

    public Button() {
        this(0, 0, 80, 25, 0, 0, "Button");
    }

    public Button(int x, int y, int width, int height, long command, int scanCode, String text) {
        super(x, y, width, height, command, scanCode);
        this.text = text != null ? text : "Button";
    }

    @Override
    protected void drawInside(PaintContext ctx, int offset) {
        if (text == null || text.isEmpty()) return;

        ctx.setColor(!isEnabled() ? ctx.palette().dark : ctx.palette().black);
        Dimension sz = ctx.measureText(text);
        int dx = (bounds.width()  - sz.width)  / 2 + offset;
        int dy = (bounds.height() - sz.height) / 2 + offset;
        ctx.drawString(dx, dy, text);
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
