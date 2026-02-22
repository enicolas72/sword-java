package net.eric_nicolas.sword.graphics;

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
        ctx.drawRect(bounds.a(), bounds.width() - 1, bounds.height() - 1);

        // Title bar background
        ctx.setColor(TColors.DARK_GRAY);
        ctx.fillRect(bounds.a().plus(1, 1), bounds.width() - 2, 20);

        // Title text
        ctx.setColor(TColors.WHITE);
        ctx.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12));
        ctx.drawString(bounds.a().plus(5, 15), title);

        // Window background (below title bar)
        ctx.setColor(bgColor);
        ctx.fillRect(bounds.a().plus(1, 21), bounds.width() - 2, bounds.height() - 22);
    }
}
