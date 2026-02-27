package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.base.Canvas;
import net.eric_nicolas.sword.ui.base.PaintContext;
import net.eric_nicolas.sword.ui.base.WindowPalette;

import java.awt.Dimension;

/**
 * TGroupBox - Container for grouping related controls (checkboxes, radio buttons).
 */
public class GroupBox extends Canvas {

    protected String text;
    public int value;

    public GroupBox() {
        this(0, 0, 150, 100, null);
    }

    public GroupBox(int x, int y, int width, int height, String text) {
        super(x, y, width, height);
        this.text  = text;
        this.value = 0;
    }

    public GroupBox(int x, int y, int width, int height) {
        this(x, y, width, height, null);
    }

    @Override
    protected void paint(PaintContext ctx) {
        int x = 0;
        int y = 0;
        int w = bounds.width();
        int h = bounds.height();
        WindowPalette pal = ctx.palette();

        if (text != null && !text.isEmpty()) {
            // Draw titled group box: frame starts at y+8 so title sits over it
            ctx.setColor(pal.dark);
            ctx.drawRect(x, y + 8, w - 1, h - 9);

            // Title background (16 px tall strip)
            Dimension sz = ctx.measureText(text);
            ctx.setColor(pal.face);
            ctx.fillRect(x + 10, y, sz.width + 6, 16);

            // Title text centred in the 16-px strip
            ctx.setColor(pal.black);
            int dy = (16 - sz.height) / 2;
            ctx.drawString(x + 13, y + Math.max(0, dy), text);
        } else {
            ctx.setColor(pal.dark);
            ctx.drawRect(x, y, w - 1, h - 1);
        }
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
