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
        this.bounds = new Rect(x, y, width, height);
        this.clipRect = new Rect(bounds);
        this.bgColor = TColors.WINDOW_BG;
        this.fgColor = TColors.BLACK;
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

        // Build a context translated to this zone's absolute position.
        // All drawing in paint() uses local (0,0)-relative coordinates.
        Point absPos = getAbsolutePosition();
        PaintContext localCtx = ctx.withOrigin(absPos);

        // Set clipping to this zone's screen rectangle
        localCtx.setClip(0, 0, bounds.width(), bounds.height());

        // Draw background
        localCtx.setColor(bgColor);
        localCtx.fillRect(0, 0, bounds.width(), bounds.height());

        // Draw content in local coordinates
        paint(localCtx);
    }

    protected void paint(PaintContext ctx) {
        // Override in subclasses
    }

    /** Package-private: set parent reference. */
    void setParent(TObject parent) {
        father = parent;
    }

    public void setBackgroundColor(Color color) {
        this.bgColor = color;
    }

    public void setForegroundColor(Color color) {
        this.fgColor = color;
    }

    public boolean contains(int x, int y) {
        Point absPos = getAbsolutePosition();
        Rect absRect = new Rect(absPos, bounds.width(), bounds.height());
        return absRect.contains(x, y);
    }

    public boolean contains(Point p) {
        return contains(p.x(), p.y());
    }

    /**
     * Compute absolute position by walking up parent chain.
     * In C++ this is MakeGlobal().
     */
    protected Point getAbsolutePosition() {
        Point p = new Point(bounds.origin());

        TObject parent = father();
        while (parent instanceof TZone parentZone) {
            p = Point.plus(p, parentZone.bounds.origin());
            parent = parent.father();
        }

        return p;
    }
}
