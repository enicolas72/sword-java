package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.base.Canvas;
import net.eric_nicolas.sword.ui.base.PaintContext;
import net.eric_nicolas.sword.ui.base.WindowPalette;

import java.awt.Font;

/**
 * TGroupBox - Container for grouping related controls (checkboxes, radio buttons).
 * Handles data exchange for child ItemBox controls.
 */
public class GroupBox extends Canvas {

    protected String text;
    protected Font groupFont;
    public int value; // For radio button groups

    /**
     * Default constructor.
     */
    public GroupBox() {
        this(0, 0, 150, 100, null);
    }

    /**
     * Constructor with position, size, and optional title.
     */
    public GroupBox(int x, int y, int width, int height, String text) {
        super(x, y, width, height);
        this.text = text;
        this.value = 0;
        this.groupFont = new Font("SansSerif", Font.PLAIN, 12);
    }

    /**
     * Constructor without title.
     */
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
            // Draw titled group box with frame
            ctx.setColor(pal.dark);
            ctx.drawRect(x, y + 8, w - 1, h - 9);

            // Draw title background
            ctx.setColor(pal.face);
            ctx.setFont(groupFont);
            int textWidth = ctx.getFontMetrics().stringWidth(text);
            ctx.fillRect(x + 10, y, textWidth + 6, 16);

            // Draw title text
            ctx.setColor(pal.black);
            ctx.drawString(x + 13, y + 12, text);
        } else {
            // Draw simple frame
            ctx.setColor(pal.dark);
            ctx.drawRect(x, y, w - 1, h - 1);
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
