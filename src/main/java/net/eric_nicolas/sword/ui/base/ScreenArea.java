package net.eric_nicolas.sword.ui.base;

import net.eric_nicolas.sword.ui.Point;
import net.eric_nicolas.sword.ui.Rect;
import net.eric_nicolas.sword.ui.events.Event;
import net.eric_nicolas.sword.ui.events.EventCommand;
import net.eric_nicolas.sword.ui.events.EventKeyboard;
import net.eric_nicolas.sword.ui.events.EventMouse;

import java.awt.Color;

/**
 * ScreenArea - Core application object with event handling, parent reference,
 * status flags, and drawing area with clipping support.
 * Children walk the father chain for coordinate translation; all drawing uses
 * local (0,0)-based coordinates via PaintContext.
 */
public class ScreenArea {

    // Status flags (bitmasks)
    public static final int SF_MOUSE_IN  = 0x0001;
    public static final int SF_SELECTED  = 0x0002;
    public static final int SF_DOWN      = 0x0004;
    public static final int SF_VISIBLE   = 0x0008;
    public static final int SF_MODIFIED  = 0x0020;
    public static final int SF_FOCUSED   = 0x0040;

    protected ScreenArea father;
    protected int status;

    protected Rect bounds;
    protected Rect clipRect;
    protected Color bgColor;
    protected Color fgColor;

    public ScreenArea(int x, int y, int width, int height) {
        this.father = null;
        this.status = SF_VISIBLE;
        this.bounds = new Rect(x, y, width, height);
        this.clipRect = new Rect(bounds);
        this.bgColor = null;  // null means: inherit palette face colour
        this.fgColor = TColors.BLACK;
    }

    // ===== Parent reference =====

    public ScreenArea father() {
        return father;
    }

    /** Package-private: set parent reference. */
    void setParent(ScreenArea parent) {
        father = parent;
    }

    // ===== Event handling =====

    public boolean handleEvent(Event event) {
        if (event.what == Event.EV_NOTHING) return false;

        boolean handled = switch (event.what) {
            case EventMouse.EV_MOUSE_LDOWN -> mouseLDown((EventMouse) event);
            case EventMouse.EV_MOUSE_LUP   -> mouseLUp((EventMouse) event);
            case EventMouse.EV_MOUSE_RDOWN -> mouseRDown((EventMouse) event);
            case EventMouse.EV_MOUSE_RUP   -> mouseRUp((EventMouse) event);
            case EventMouse.EV_MOUSE_MOVE  -> mouseMove((EventMouse) event);
            case EventKeyboard.EV_KEY_DOWN -> keyDown((EventKeyboard) event);
            case EventKeyboard.EV_KEY_UP   -> keyUp((EventKeyboard) event);
            case EventCommand.EV_COMMAND   -> command(((EventCommand) event).commandId);
            default -> false;
        };

        if (handled) event.what = Event.EV_NOTHING;
        return handled;
    }

    protected boolean mouseLDown(EventMouse event) { return false; }
    protected boolean mouseLUp(EventMouse event)   { return false; }
    protected boolean mouseRDown(EventMouse event) { return false; }
    protected boolean mouseRUp(EventMouse event)   { return false; }
    protected boolean mouseMove(EventMouse event)  { return false; }
    protected boolean keyDown(EventKeyboard event) { return false; }
    protected boolean keyUp(EventKeyboard event)   { return false; }
    protected boolean command(int commandId)       { return false; }

    // ===== Status flags =====

    public boolean hasStatus(int flag) { return (status & flag) != 0; }
    public void setStatus(int flag)    { status |= flag; }
    public void clearStatus(int flag)  { status &= ~flag; }

    public boolean isVisible() { return hasStatus(SF_VISIBLE); }
    public void setVisible(boolean visible) {
        if (visible) setStatus(SF_VISIBLE); else clearStatus(SF_VISIBLE);
    }

    public boolean isSelected() { return hasStatus(SF_SELECTED); }
    public void setSelected(boolean selected) {
        if (selected) setStatus(SF_SELECTED); else clearStatus(SF_SELECTED);
    }

    // ===== Bounds =====

    public Rect getBounds() { return new Rect(bounds); }

    public void setBounds(Rect r) {
        bounds = new Rect(r);
        clipRect = new Rect(r);
    }

    public Rect getClipRect() { return new Rect(clipRect); }

    public void setClipRect(Rect r) {
        clipRect = new Rect(r);
        clipRect = Rect.intersect(clipRect, bounds);
    }

    // ===== Drawing =====

    public void draw(PaintContext ctx) {
        if (!isVisible()) return;

        Point absPos = getAbsolutePosition();
        PaintContext localCtx = ctx.withOrigin(absPos);
        localCtx.setClip(0, 0, bounds.width(), bounds.height());
        localCtx.setColor(bgColor != null ? bgColor : localCtx.palette().face);
        localCtx.fillRect(0, 0, bounds.width(), bounds.height());
        paint(localCtx);
    }

    protected void paint(PaintContext ctx) {}

    // ===== Colors =====

    public void setBackgroundColor(Color color) { this.bgColor = color; }
    public void setForegroundColor(Color color) { this.fgColor = color; }

    // ===== Geometry =====

    public boolean contains(int x, int y) {
        Point absPos = getAbsolutePosition();
        Rect absRect = new Rect(absPos, bounds.width(), bounds.height());
        return absRect.contains(x, y);
    }

    public boolean contains(Point p) { return contains(p.x(), p.y()); }

    /**
     * Compute absolute position by walking up the parent chain.
     * In C++ this is MakeGlobal().
     */
    protected Point getAbsolutePosition() {
        Point p = new Point(bounds.origin());
        ScreenArea parent = father;
        while (parent != null) {
            p = Point.plus(p, parent.bounds.origin());
            parent = parent.father;
        }
        return p;
    }
}
