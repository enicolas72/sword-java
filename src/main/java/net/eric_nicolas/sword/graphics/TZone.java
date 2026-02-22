package net.eric_nicolas.sword.graphics;

import net.eric_nicolas.sword.mechanism.*;
import net.eric_nicolas.sword.ui.Point;
import net.eric_nicolas.sword.ui.Rect;

import java.awt.Color;

/**
 * TZone - Base drawing area with clipping support.
 */
public class TZone extends TObject {

    protected Rect bounds;
    protected Rect clipRect;
    protected Color bgColor;
    protected Color fgColor;

    public TZone(int x, int y, int width, int height) {
        super();
        this.bounds = new Rect(x, y, x + width, y + height);
        this.clipRect = new Rect(bounds);
        this.bgColor = TColors.WINDOW_BG;
        this.fgColor = TColors.BLACK;
        setOption(OP_DRAWABLE);
    }

    public Rect getBounds() {
        return new Rect(bounds);
    }

    public void setBounds(Rect r) {
        bounds = new Rect(r);
        clipRect = new Rect(r);
    }

    public Rect getClipRect() {
        return new Rect(clipRect);
    }

    public void setClipRect(Rect r) {
        clipRect = new Rect(r);
        clipRect = Rect.intersect(clipRect, bounds);
    }

    public void draw(PaintContext ctx) {
        if (!isVisible()) return;

        // Get absolute position for drawing
        Point absPos = getAbsolutePosition();

        // Set clipping
        ctx.setClip(absPos, bounds.width(), bounds.height());

        // Draw background
        ctx.setColor(bgColor);
        ctx.fillRect(absPos, bounds.width(), bounds.height());

        // Temporarily adjust bounds for painting
        Rect originalBounds = new Rect(bounds);
        bounds = new Rect(
            new Point(absPos),
                Point.plus(absPos, originalBounds.width(), originalBounds.height()));

        // Draw content
        paint(ctx);

        // Restore relative bounds
        bounds = originalBounds;

        // Draw children
        if (_Son != null) {
            TAtom child = _Son;
            while (child != null) {
                if (child instanceof TZone) {
                    ((TZone) child).draw(ctx);
                }
                child = child.next();
            }
        }
    }

    protected void paint(PaintContext ctx) {
        // Override in subclasses
    }

    public void setBackgroundColor(Color color) {
        this.bgColor = color;
    }

    public void setForegroundColor(Color color) {
        this.fgColor = color;
    }

    public boolean contains(int x, int y) {
        Point absPos = getAbsolutePosition();
        return x >= absPos.x() && x < absPos.x() + bounds.width() &&
               y >= absPos.y() && y < absPos.y() + bounds.height();
    }

    public boolean contains(Point p) {
        Point absPos = getAbsolutePosition();
        return p.x() >= absPos.x() && p.x() < absPos.x() + bounds.width() &&
                p.y() >= absPos.y() && p.y() < absPos.y() + bounds.height();
    }

    /**
     * Compute absolute position by walking up parent chain.
     * In C++ this is MakeGlobal().
     */
    protected Point getAbsolutePosition() {
        Point p = new Point(bounds.a());

        TAtom parent = father();
        while (parent instanceof TZone parentZone) {
            p = Point.plus(p, parentZone.bounds.a());
            parent = parent.father();
        }

        return p;
    }

    /**
     * Data exchange methods - override in subclasses that need data exchange.
     */
    public void setData(Object data) {
        // Override in subclasses
    }

    public void getData(Object data) {
        // Override in subclasses
    }

    public long dataSize() {
        // Override in subclasses
        return 0;
    }
}
