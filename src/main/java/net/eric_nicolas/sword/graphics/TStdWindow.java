package net.eric_nicolas.sword.graphics;

import java.awt.Graphics2D;

/**
 * TStdWindow - Standard window with title bar and frame.
 * Simplified version - extends TWindow to add standard window features.
 */
public class TStdWindow extends TWindow {

    public TStdWindow(int x, int y, int width, int height, String title, int options) {
        super(x, y, width, height, title);
        setOption(options);
    }

    public TStdWindow(int x, int y, int width, int height, String title) {
        this(x, y, width, height, title, 0);
    }

    @Override
    protected void paint(PaintContext ctx) {
        // Standard window drawing - frame and title bar
        // Frame
        ctx.setColor(TColors.DARK_GRAY);
        ctx.drawRect(bounds.a.x, bounds.a.y, bounds.width() - 1, bounds.height() - 1);

        // Title bar background
        ctx.setColor(TColors.DARK_GRAY);
        ctx.fillRect(bounds.a.x + 1, bounds.a.y + 1, bounds.width() - 2, 20);

        // Title text
        ctx.setColor(TColors.WHITE);
        ctx.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12));
        ctx.drawString(bounds.a.x + 5, bounds.a.y + 15, title);

        // Window background (below title bar)
        ctx.setColor(bgColor);
        ctx.fillRect(bounds.a.x + 1, bounds.a.y + 21, bounds.width() - 2, bounds.height() - 22);
    }
}
