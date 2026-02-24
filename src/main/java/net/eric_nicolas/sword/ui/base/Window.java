package net.eric_nicolas.sword.ui.base;

import net.eric_nicolas.sword.ui.Point;
import net.eric_nicolas.sword.ui.Rect;
import net.eric_nicolas.sword.ui.events.Event;
import net.eric_nicolas.sword.ui.events.EventMouse;

/**
 * TWindow - Overlapped window with left-sidebar controls and outer resize border.
 *
 * Layout (all measurements in pixels):
 *
 *   ┌──────────────────────────────────────────┐  ← BORDER (resize zone)
 *   │ ┌────┬──────────────────────────────────┐ │
 *   │ │grip│                                  │ │
 *   │ │ X  │        content / canvas          │ │
 *   │ │    │                                  │ │
 *   │ └────┴──────────────────────────────────┘ │
 *   └──────────────────────────────────────────┘
 *        ↑ SIDEBAR_W
 *
 * Sidebar (left strip, inside the border):
 *   - Grip lines (drag handle) at the top
 *   - Close button (×) below the grip
 *   - The rest of the sidebar is also a drag area
 *
 * Resize border (BORDER pixels wide on every side):
 *   - Edge zones: drag to resize in one axis
 *   - Corner zones (CORNER px from each corner): drag to resize in two axes
 */
public class Window extends TZone {

    // Public so samples and Scroller can compute content dimensions
    public static final int BORDER    = 5;   // resize border thickness (pixels)
    public static final int SIDEBAR_W = 16;  // left sidebar width

    private static final int CORNER   = 12;  // corner hit-zone extension (pixels from corner)
    private static final int CLOSE_Y  = 18;  // close button top, relative to (BORDER, BORDER)
    private static final int CLOSE_SZ = 12;  // close button size
    private static final int MIN_W    = BORDER * 2 + SIDEBAR_W + 40;
    private static final int MIN_H    = BORDER * 2 + 40;

    // Resize direction flags (bitmask)
    private static final int DIR_N = 1, DIR_E = 2, DIR_S = 4, DIR_W = 8;

    protected String title;
    protected Canvas canvas;

    // Drag state
    private boolean dragging;
    private Point dragOffset;

    // Resize state
    private boolean resizing;
    private int resizeDir;
    private Point resizeStart;
    private Rect resizeStartBounds;

    public Window(int x, int y, int width, int height, String title) {
        super(x, y, width, height);
        this.title = title;
        this.canvas = new Canvas(0, 0, 1, 1);
        this.canvas.setParent(this);
        updateCanvasBounds();
    }

    // ===== Layout =====

    /** Width of the usable content area (inside border and sidebar). */
    public int getContentWidth() {
        return Math.max(1, bounds.width() - BORDER * 2 - SIDEBAR_W);
    }

    /** Height of the usable content area (inside border). */
    public int getContentHeight() {
        return Math.max(1, bounds.height() - BORDER * 2);
    }

    private void updateCanvasBounds() {
        canvas.setBounds(new Rect(BORDER + SIDEBAR_W, BORDER,
                                  getContentWidth(), getContentHeight()));
    }

    public Canvas getCanvas() { return canvas; }

    // ===== Hit testing =====

    /**
     * Returns a DIR_* bitmask if (mx,my) is inside a resize zone, 0 otherwise.
     * mx/my are window-local (origin = window top-left).
     */
    private int hitResize(int mx, int my) {
        int w = bounds.width();
        int h = bounds.height();

        boolean inL = mx < BORDER;
        boolean inR = mx >= w - BORDER;
        boolean inT = my < BORDER;
        boolean inB = my >= h - BORDER;

        if (!inL && !inR && !inT && !inB) return 0;

        int dir = 0;
        if (inL) dir |= DIR_W;
        if (inR) dir |= DIR_E;
        if (inT) dir |= DIR_N;
        if (inB) dir |= DIR_S;

        // Extend corner detection: when on an edge near a window corner, add the
        // perpendicular direction so the corner zone is easier to grab.
        if (dir == DIR_W || dir == DIR_E) {
            if (my < CORNER)          dir |= DIR_N;
            else if (my >= h - CORNER) dir |= DIR_S;
        }
        if (dir == DIR_N || dir == DIR_S) {
            if (mx < CORNER)          dir |= DIR_W;
            else if (mx >= w - CORNER) dir |= DIR_E;
        }

        return dir;
    }

    /** True if (mx,my) is over the close button (window-local coords). */
    private boolean hitClose(int mx, int my) {
        int cbx = BORDER + 2;
        int cby = BORDER + CLOSE_Y;
        return mx >= cbx && mx < cbx + CLOSE_SZ
            && my >= cby && my < cby + CLOSE_SZ;
    }

    /** True if (mx,my) is in the sidebar drag area (whole sidebar minus close button). */
    private boolean hitDrag(int mx, int my) {
        return mx >= BORDER && mx < BORDER + SIDEBAR_W
            && my >= BORDER && my < bounds.height() - BORDER
            && !hitClose(mx, my);
    }

    // ===== Painting =====

    @Override
    protected void paint(PaintContext ctx) {
        int w = bounds.width();
        int h = bounds.height();

        // ── Outer resize border ──────────────────────────────────────────────
        ctx.setColor(TColors.DARK_GRAY);
        ctx.drawRect(0, 0, w - 1, h - 1);

        // Corner markers: small filled squares to visually signal "resize here"
        int cs = BORDER - 1;
        ctx.setColor(TColors.MEDIUM_GRAY);
        ctx.fillRect(1,         1,         cs, cs);  // NW
        ctx.fillRect(w - cs - 1, 1,         cs, cs);  // NE
        ctx.fillRect(1,         h - cs - 1, cs, cs);  // SW
        ctx.fillRect(w - cs - 1, h - cs - 1, cs, cs); // SE

        // ── Left sidebar ─────────────────────────────────────────────────────
        ctx.setColor(TColors.MEDIUM_GRAY);
        ctx.fillRect(BORDER, BORDER, SIDEBAR_W, h - 2 * BORDER);

        // Sidebar right-edge separator
        ctx.setColor(TColors.DARK_GRAY);
        ctx.drawLine(BORDER + SIDEBAR_W, BORDER,
                     BORDER + SIDEBAR_W, h - BORDER - 1);

        // Drag grip: three horizontal embossed lines
        int gx1 = BORDER + 3;
        int gx2 = BORDER + SIDEBAR_W - 4;
        for (int i = 0; i < 3; i++) {
            int gy = BORDER + 4 + i * 3;
            ctx.setColor(TColors.DARK_GRAY);
            ctx.drawLine(gx1, gy, gx2, gy);
            ctx.setColor(TColors.WHITE);
            ctx.drawLine(gx1, gy + 1, gx2, gy + 1);
        }

        // Close button (box with ×)
        int cbx = BORDER + 2;
        int cby = BORDER + CLOSE_Y;
        ctx.setColor(TColors.FACE_GRAY);
        ctx.fillRect(cbx, cby, CLOSE_SZ, CLOSE_SZ);
        ctx.setColor(TColors.DARK_GRAY);
        ctx.drawRect(cbx, cby, CLOSE_SZ - 1, CLOSE_SZ - 1);
        ctx.drawLine(cbx + 2, cby + 2, cbx + CLOSE_SZ - 3, cby + CLOSE_SZ - 3);
        ctx.drawLine(cbx + CLOSE_SZ - 3, cby + 2, cbx + 2, cby + CLOSE_SZ - 3);

        // ── Content area border ───────────────────────────────────────────────
        ctx.setColor(TColors.DARK_GRAY);
        ctx.drawRect(BORDER + SIDEBAR_W, BORDER,
                     w - BORDER * 2 - SIDEBAR_W - 1,
                     h - BORDER * 2 - 1);
    }

    // ===== Event handling =====

    @Override
    protected boolean mouseLDown(EventMouse event) {
        if (!contains(event.where)) return false;

        bringToFront();

        Point absPos = getAbsolutePosition();
        int mx = event.where.x() - absPos.x();
        int my = event.where.y() - absPos.y();

        // Close button
        if (hitClose(mx, my)) {
            remove();
            return true;
        }

        // Resize border
        int dir = hitResize(mx, my);
        if (dir != 0) {
            resizing = true;
            resizeDir = dir;
            resizeStart = event.where;
            resizeStartBounds = new Rect(bounds);
            return true;
        }

        // Sidebar drag area
        if (hitDrag(mx, my)) {
            dragging = true;
            dragOffset = Point.minus(event.where, bounds.origin());
            return true;
        }

        // Content area — consume to prevent events falling through to windows below
        return true;
    }

    @Override
    protected boolean mouseLUp(EventMouse event) {
        if (dragging) { dragging = false; return true; }
        if (resizing) { resizing = false; return true; }
        return false;
    }

    @Override
    protected boolean mouseMove(EventMouse event) {
        if (dragging) {
            Point newOrigin = Point.minus(event.where, dragOffset);
            bounds = new Rect(newOrigin, bounds.width(), bounds.height());
            clipRect = new Rect(bounds);
            return true;
        }

        if (resizing) {
            int dx = event.where.x() - resizeStart.x();
            int dy = event.where.y() - resizeStart.y();

            int nx = resizeStartBounds.origin().x();
            int ny = resizeStartBounds.origin().y();
            int nw = resizeStartBounds.width();
            int nh = resizeStartBounds.height();

            if ((resizeDir & DIR_E) != 0) nw = Math.max(MIN_W, nw + dx);
            if ((resizeDir & DIR_S) != 0) nh = Math.max(MIN_H, nh + dy);
            if ((resizeDir & DIR_W) != 0) {
                int newW = Math.max(MIN_W, nw - dx);
                nx += (nw - newW);
                nw = newW;
            }
            if ((resizeDir & DIR_N) != 0) {
                int newH = Math.max(MIN_H, nh - dy);
                ny += (nh - newH);
                nh = newH;
            }

            bounds = new Rect(nx, ny, nw, nh);
            clipRect = new Rect(bounds);
            updateCanvasBounds();
            return true;
        }

        return false;
    }

    // ===== Drawing & dispatch =====

    @Override
    public void draw(PaintContext ctx) {
        super.draw(ctx);  // fills background, calls paint()
        canvas.draw(ctx); // draws widgets
    }

    @Override
    public boolean handleEvent(Event event) {
        if (canvas.handleEvent(event)) {
            event.what = Event.EV_NOTHING;
            return true;
        }
        return super.handleEvent(event);
    }

    // ===== Window management =====

    public void bringToFront() {
        if (father instanceof Screen screen) {
            screen.bringToFront(this);
        }
    }

    public void remove() {
        if (father instanceof Screen screen) {
            father = null;
            screen.remove(this);
        }
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
