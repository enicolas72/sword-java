package net.eric_nicolas.sword.graphics;

import net.eric_nicolas.sword.mechanism.*;
import java.awt.Graphics2D;
import java.awt.Color;

/**
 * TWindow - Overlapped window with title and frame.
 */
public class TWindow extends TZone {

    protected String title;
    protected boolean dragging;
    protected TPoint dragOffset;

    public TWindow(int x, int y, int width, int height, String title) {
        super(x, y, width, height);
        this.title = title;
        this.dragging = false;
        this.dragOffset = new TPoint();
        setOption(OP_WIN_SIZEABLE | OP_WIN_CLOSEBOX);
    }

    @Override
    protected void paint(PaintContext ctx) {
        // Draw frame
        ctx.setColor(TColors.WINDOW_FRAME);
        ctx.drawRect(bounds.a.x, bounds.a.y, bounds.width() - 1, bounds.height() - 1);

        // Draw title bar
        ctx.setColor(TColors.DARK_GRAY);
        ctx.fillRect(bounds.a.x + 1, bounds.a.y + 1, bounds.width() - 2, 20);

        // Draw title text with smaller font
        ctx.setColor(TColors.WHITE);
        ctx.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12));
        ctx.drawString(bounds.a.x + 5, bounds.a.y + 15, title);
    }

    @Override
    protected boolean mouseLDown(TMouseEvent event) {
        if (contains(event.where.x, event.where.y)) {
            // Bring window to front
            bringToFront();

            // Check if clicking title bar
            if (event.where.y >= bounds.a.y &&
                event.where.y < bounds.a.y + 20) {
                dragging = true;
                dragOffset.x = event.where.x - bounds.a.x;
                dragOffset.y = event.where.y - bounds.a.y;
                return true;
            }
            return true; // Consume event even if not on title bar
        }
        return false;
    }

    @Override
    protected boolean mouseLUp(TMouseEvent event) {
        if (dragging) {
            dragging = false;
            return true;
        }
        return false;
    }

    @Override
    protected boolean mouseMove(TMouseEvent event) {
        if (dragging) {
            // Calculate new position (relative to parent/desktop)
            int newX = event.where.x - dragOffset.x;
            int newY = event.where.y - dragOffset.y;

            // Update window bounds (stored as relative coordinates)
            int width = bounds.width();
            int height = bounds.height();
            bounds.a.set(newX, newY);
            bounds.b.set(newX + width, newY + height);
            clipRect = new TRect(bounds);

            // Children don't need to be moved - they maintain relative positions
            return true;
        }
        return false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
