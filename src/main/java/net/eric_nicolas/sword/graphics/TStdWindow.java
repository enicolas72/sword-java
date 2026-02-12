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
    protected void paint(Graphics2D g) {
        // Standard window drawing - frame and title bar
        // Frame
        g.setColor(TColors.DARK_GRAY);
        g.drawRect(bounds.a.x, bounds.a.y, bounds.width() - 1, bounds.height() - 1);

        // Title bar background
        g.setColor(TColors.DARK_GRAY);
        g.fillRect(bounds.a.x + 1, bounds.a.y + 1, bounds.width() - 2, 20);

        // Title text
        g.setColor(TColors.WHITE);
        g.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12));
        g.drawString(title, bounds.a.x + 5, bounds.a.y + 15);

        // Window background (below title bar)
        g.setColor(bgColor);
        g.fillRect(bounds.a.x + 1, bounds.a.y + 21, bounds.width() - 2, bounds.height() - 22);
    }
}
