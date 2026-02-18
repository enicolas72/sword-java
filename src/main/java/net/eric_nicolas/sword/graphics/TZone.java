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
        clipRect.intersect(bounds);
    }

    public void draw(PaintContext ctx) {
        if (!isVisible()) return;

        // Get absolute position for drawing
        Point absPos = getAbsolutePosition();

        // Set clipping
        ctx.setClip(absPos.x, absPos.y, bounds.width(), bounds.height());

        // Draw background
        ctx.setColor(bgColor);
        ctx.fillRect(absPos.x, absPos.y, bounds.width(), bounds.height());

        // Temporarily adjust bounds for painting
        Rect originalBounds = new Rect(bounds);
        bounds.a.x = absPos.x;
        bounds.a.y = absPos.y;
        bounds.b.x = absPos.x + originalBounds.width();
        bounds.b.y = absPos.y + originalBounds.height();

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
        return x >= absPos.x && x < absPos.x + bounds.width() &&
               y >= absPos.y && y < absPos.y + bounds.height();
    }

    /**
     * Compute absolute position by walking up parent chain.
     * In C++ this is MakeGlobal().
     */
    protected Point getAbsolutePosition() {
        int x = bounds.a.x;
        int y = bounds.a.y;

        TAtom parent = father();
        while (parent instanceof TZone) {
            TZone parentZone = (TZone) parent;
            x += parentZone.bounds.a.x;
            y += parentZone.bounds.a.y;
            parent = parent.father();
        }

        return new Point(x, y);
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
