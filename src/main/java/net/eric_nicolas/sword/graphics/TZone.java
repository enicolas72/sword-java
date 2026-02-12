package net.eric_nicolas.sword.graphics;

import net.eric_nicolas.sword.mechanism.*;
import java.awt.Graphics2D;
import java.awt.Color;

/**
 * TZone - Base drawing area with clipping support.
 */
public class TZone extends TObject {

    protected TRect bounds;
    protected TRect clipRect;
    protected Color bgColor;
    protected Color fgColor;

    public TZone(int x, int y, int width, int height) {
        super();
        this.bounds = new TRect(x, y, x + width, y + height);
        this.clipRect = new TRect(bounds);
        this.bgColor = TColors.WINDOW_BG;
        this.fgColor = TColors.BLACK;
        setOption(OP_DRAWABLE);
    }

    public TRect getBounds() {
        return new TRect(bounds);
    }

    public void setBounds(TRect r) {
        bounds = new TRect(r);
        clipRect = new TRect(r);
    }

    public TRect getClipRect() {
        return new TRect(clipRect);
    }

    public void setClipRect(TRect r) {
        clipRect = new TRect(r);
        clipRect.intersect(bounds);
    }

    public void draw(Graphics2D g) {
        if (!isVisible()) return;

        // Set clipping
        g.setClip(clipRect.a.x, clipRect.a.y, clipRect.width(), clipRect.height());

        // Draw background
        g.setColor(bgColor);
        g.fillRect(bounds.a.x, bounds.a.y, bounds.width(), bounds.height());

        // Draw content
        paint(g);

        // Draw children
        if (_Son != null) {
            TAtom child = _Son;
            while (child != null) {
                if (child instanceof TZone) {
                    ((TZone) child).draw(g);
                }
                child = child.next();
            }
        }
    }

    protected void paint(Graphics2D g) {
        // Override in subclasses
    }

    public void setBackgroundColor(Color color) {
        this.bgColor = color;
    }

    public void setForegroundColor(Color color) {
        this.fgColor = color;
    }

    public boolean contains(int x, int y) {
        return bounds.contains(x, y);
    }

    @Override
    public void insertIn(TAtom father) {
        // Adjust bounds to be relative to parent's position
        if (father instanceof TZone) {
            TZone parentZone = (TZone) father;
            // Child bounds are specified relative to parent, so add parent's origin
            int dx = parentZone.bounds.a.x;
            int dy = parentZone.bounds.a.y;
            bounds.offset(dx, dy);
            clipRect.offset(dx, dy);
        }

        // Call parent insertIn to update the tree structure
        super.insertIn(father);
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
