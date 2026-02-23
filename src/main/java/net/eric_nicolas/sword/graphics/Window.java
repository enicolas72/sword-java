package net.eric_nicolas.sword.graphics;

import net.eric_nicolas.sword.ui.Point;
import net.eric_nicolas.sword.ui.Rect;
import net.eric_nicolas.sword.ui.events.Event;
import net.eric_nicolas.sword.ui.events.EventMouse;

/**
 * TWindow - Overlapped window with title and frame.
 * Each Window owns a Canvas that spans its whole surface; insert Widgets into that.
 */
public class Window extends TZone {

    protected String title;
    protected boolean dragging;
    protected Point dragOffset;
    protected Canvas canvas;

    public Window(int x, int y, int width, int height, String title) {
        super(x, y, width, height);
        this.title = title;
        this.dragging = false;
        this.dragOffset = null;
        this.canvas = new Canvas(0, 0, width, height);
        this.canvas.setParent(this);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    protected void paint(PaintContext ctx) {
        // Draw frame
        ctx.setColor(TColors.DARK_GRAY);
        ctx.drawRect(0, 0, bounds.width() - 1, bounds.height() - 1);

        // Draw title bar
        ctx.setColor(TColors.DARK_GRAY);
        ctx.fillRect(1, 1, bounds.width() - 2, 20);

        // Draw title text with smaller font
        ctx.setColor(TColors.WHITE);
        ctx.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12));
        ctx.drawString(5, 15, title);
    }

    @Override
    protected boolean mouseLDown(EventMouse event) {
        if (contains(event.where)) {
            // Bring window to front
            bringToFront();

            // Check if clicking title bar
            if (event.where.y() >= bounds.origin().y() &&
                event.where.y() < bounds.origin().y() + 20) {
                dragging = true;
                dragOffset = Point.minus(event.where, bounds.origin());
                return true;
            }
            return true; // Consume event even if not on title bar
        }
        return false;
    }

    @Override
    protected boolean mouseLUp(EventMouse event) {
        if (dragging) {
            dragging = false;
            return true;
        }
        return false;
    }

    @Override
    protected boolean mouseMove(EventMouse event) {
        if (dragging) {
            // Calculate new position (relative to parent/desktop)
            Point newP = Point.minus(event.where, dragOffset);

            // Update window bounds (stored as relative coordinates)
            int width = bounds.width();
            int height = bounds.height();
            bounds = new Rect(newP, width, height);
            clipRect = new Rect(bounds);

            // Children don't need to be moved - they maintain relative positions
            return true;
        }
        return false;
    }

    @Override
    public void draw(PaintContext ctx) {
        super.draw(ctx);   // fill background + paint() (frame/title)
        canvas.draw(ctx);  // draw widgets inside this window
    }

    @Override
    public boolean handleEvent(Event event) {
        if (canvas.handleEvent(event)) {
            event.what = Event.EV_NOTHING;
            return true;
        }
        return super.handleEvent(event);
    }

    public void bringToFront() {
        if (father instanceof Desktop desktop) {
            desktop.bringToFront(this);
        }
    }

    public void remove() {
        if (father instanceof Desktop desktop) {
            father = null;
            desktop.remove(this);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
