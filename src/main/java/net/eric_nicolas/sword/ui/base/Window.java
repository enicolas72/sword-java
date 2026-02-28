package net.eric_nicolas.sword.ui.base;

import net.eric_nicolas.sword.ui.Point;
import net.eric_nicolas.sword.ui.Rect;
import net.eric_nicolas.sword.ui.events.Event;
import net.eric_nicolas.sword.ui.events.EventMouse;

import java.awt.AlphaComposite;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * TWindow - Overlapped window with left-sidebar controls and outer resize border.
 * Layout (resizable window, all measurements in pixels):
 *   ┌──────────────────────────────────────────┐  ← BORDER (resize zone)
 *   │ ┌────┬──────────────────────────────────┐ │
 *   │ │grip│                                  │ │
 *   │ │ X  │        content / canvas          │ │
 *   │ │    │                                  │ │
 *   │ └────┴──────────────────────────────────┘ │
 *   └──────────────────────────────────────────┘
 *        ↑ SIDEBAR_W
 * When resizable=false the thick 5-pixel resize border is replaced by a
 * plain 1-pixel outline (eb()=1 instead of BORDER=5), and resize hit-testing
 * and corner tick marks are disabled.
 * When closable=false the close button (×) is not drawn and clicking the
 * sidebar never triggers removal.
 * Sidebar (left strip, inside the effective border):
 *   - Grip lines (drag handle) at the top
 *   - Close button (×) below the grip (only if closable)
 *   - The rest of the sidebar is also a drag area
 * Resize border (BORDER pixels wide on every side, resizable windows only):
 *   - Edge zones: drag to resize in one axis
 *   - Corner zones (CORNER px from each corner): drag to resize in two axes
 *   - Corner boundaries are marked by short black tick marks on the border
 * Inner chrome (sidebar separator + content border) is redrawn AFTER canvas
 * children so it is never hidden by widget background fills.
 */
public class Window extends ScreenArea {

    // Public so samples and Scroller can compute content dimensions
    public static final int BORDER    = 5;   // resize border thickness (pixels)
    public static final int SIDEBAR_W = 16;  // left sidebar width

    private static final int CORNER   = 12;  // corner hit-zone extension (pixels from corner)
    private static final int CLOSE_Y  = 18;  // close button top, relative to (eb, eb)
    private static final int CLOSE_SZ = 12;  // close button size
    private static final int MIN_W    = BORDER * 2 + SIDEBAR_W + 40;
    private static final int MIN_H    = BORDER * 2 + 40;

    // Resize direction flags (bitmask)
    private static final int DIR_N = 1, DIR_E = 2, DIR_S = 4, DIR_W = 8;

    protected String title;
    protected Canvas canvas;

    /** True if the window can be resized by dragging its border. */
    protected boolean resizable = true;

    /** True if the window shows a close button and can be closed by it. */
    protected boolean closable = true;

    // Drag state
    private boolean dragging;
    private Point dragOffset;

    // Resize state
    private boolean resizing;
    private int resizeDir;
    private Point resizeStart;
    private Rect resizeStartBounds;

    // Resize notification callback
    private Runnable onResize;

    // Screen this window lives on (set by Screen.add; null when not on screen)
    private Screen screen;

    // Colour palette for this window and all widgets it contains
    private WindowPalette palette = WindowPalette.STANDARD;

    // Per-window render buffer for OpenGL compositing
    private BufferedImage renderBuffer;

    public Window(int x, int y, int width, int height, String title) {
        super(x, y, width, height);
        this.title = title;
        this.canvas = new Canvas(0, 0, 1, 1);
        this.canvas.setParent(this);
        updateCanvasBounds();
    }

    // ===== Options =====

    /**
     * Enable or disable resizing.  When disabled the thick border zone is
     * replaced by a 1-pixel outline; resize hit-testing is turned off.
     * Triggers a canvas layout update.
     */
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
        if (canvas != null) updateCanvasBounds();
    }

    /**
     * Enable or disable the close button.  When disabled the × button is not
     * drawn and clicking the sidebar never triggers removal.
     */
    public void setClosable(boolean closable) {
        this.closable = closable;
    }

    public boolean isResizable() { return resizable; }
    public boolean isClosable()  { return closable; }

    public void setPalette(WindowPalette p) { this.palette = p; }
    public WindowPalette getPalette()       { return palette; }

    // ===== Layout =====

    /**
     * Effective border width: full BORDER for resizable windows,
     * 1 pixel (thin outline only) for non-resizable windows.
     */
    protected int eb() { return resizable ? BORDER : 1; }

    /** Width of the usable content area (inside border and sidebar). */
    public int getContentWidth() {
        return Math.max(1, bounds.width() - eb() * 2 - SIDEBAR_W);
    }

    /** Height of the usable content area (inside border). */
    public int getContentHeight() {
        return Math.max(1, bounds.height() - eb() * 2);
    }

    /** Update canvas bounds to match the current window size. */
    protected void updateCanvasBounds() {
        int eb = eb();
        canvas.setBounds(new Rect(eb + SIDEBAR_W, eb,
                                  getContentWidth(), getContentHeight()));
    }

    /**
     * Override setBounds to keep canvas layout in sync whenever window
     * bounds change (e.g. from Menu.initChoices rebuilding the menu size).
     */
    @Override
    public void setBounds(Rect r) {
        super.setBounds(r);
        if (canvas != null) updateCanvasBounds();
    }

    /**
     * Register a callback invoked after every resize operation.
     * Not called for pure drag (position-only) moves.
     */
    public void setOnResize(Runnable r) { this.onResize = r; }

    public Canvas getCanvas() { return canvas; }

    // ===== Hit testing =====

    /**
     * Returns a DIR_* bitmask if (mx,my) is inside a resize zone, 0 otherwise.
     * Always returns 0 for non-resizable windows.
     * mx/my are window-local (origin = window top-left).
     */
    private int hitResize(int mx, int my) {
        if (!resizable) return 0;

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

        // Extend corner detection
        if (dir == DIR_W || dir == DIR_E) {
            if (my < CORNER)           dir |= DIR_N;
            else if (my >= h - CORNER) dir |= DIR_S;
        }
        if (dir == DIR_N || dir == DIR_S) {
            if (mx < CORNER)           dir |= DIR_W;
            else if (mx >= w - CORNER) dir |= DIR_E;
        }

        return dir;
    }

    /** True if (mx,my) is over the close button (window-local coordinates).
     *  Always false for non-closable windows. */
    private boolean hitClose(int mx, int my) {
        if (!closable) return false;
        int cbx = eb() + 2;
        int cby = eb() + CLOSE_Y;
        return mx >= cbx && mx < cbx + CLOSE_SZ
            && my >= cby && my < cby + CLOSE_SZ;
    }

    /** True if (mx,my) is in the sidebar drag area (whole sidebar minus close button). */
    private boolean hitDrag(int mx, int my) {
        int eb = eb();
        return mx >= eb && mx < eb + SIDEBAR_W
            && my >= eb && my < bounds.height() - eb
            && !hitClose(mx, my);
    }

    // ===== Painting =====

    @Override
    protected void paint(PaintContext ctx) {
        int w = bounds.width();
        int h = bounds.height();
        int eb = eb();

        WindowPalette pal = ctx.palette();

        // ── Outer border ─────────────────────────────────────────────────────
        // Resizable: full BORDER-wide zone drawn as dark rect + corner ticks.
        // Non-resizable: plain 1-pixel outline, no corner ticks.
        ctx.setColor(pal.dark);
        ctx.drawRect(0, 0, w - 1, h - 1);

        if (resizable) {
            // Corner tick marks: short perpendicular lines at the corner zone
            // boundaries, dividing each border edge into "edge" and "corner" zones.
            ctx.setColor(pal.black);
            // Top edge
            ctx.drawLine(CORNER,         0,          CORNER,         BORDER - 1);
            ctx.drawLine(w-1-CORNER,     0,          w-1-CORNER,     BORDER - 1);
            // Bottom edge
            ctx.drawLine(CORNER,         h - BORDER, CORNER,         h - 1);
            ctx.drawLine(w-1-CORNER,     h - BORDER, w-1-CORNER,     h - 1);
            // Left edge
            ctx.drawLine(0,              CORNER,     BORDER - 1,     CORNER);
            ctx.drawLine(0,              h-1-CORNER, BORDER - 1,     h-1-CORNER);
            // Right edge
            ctx.drawLine(w - BORDER,     CORNER,     w - 1,          CORNER);
            ctx.drawLine(w - BORDER,     h-1-CORNER, w - 1,          h-1-CORNER);
        }

        // ── Left sidebar ─────────────────────────────────────────────────────
        ctx.setColor(pal.medium);
        ctx.fillRect(eb, eb, SIDEBAR_W, h - 2 * eb);

        // Drag grip: three horizontal embossed lines
        int gx1 = eb + 3;
        int gx2 = eb + SIDEBAR_W - 4;
        for (int i = 0; i < 3; i++) {
            int gy = eb + 4 + i * 3;
            ctx.setColor(pal.dark);
            ctx.drawLine(gx1, gy, gx2, gy);
            ctx.setColor(pal.white);
            ctx.drawLine(gx1, gy + 1, gx2, gy + 1);
        }

        // Close button (box with ×) – only when closable
        if (closable) {
            int cbx = eb + 2;
            int cby = eb + CLOSE_Y;
            ctx.setColor(pal.face);
            ctx.fillRect(cbx, cby, CLOSE_SZ, CLOSE_SZ);
            ctx.setColor(pal.dark);
            ctx.drawRect(cbx, cby, CLOSE_SZ - 1, CLOSE_SZ - 1);
            ctx.drawLine(cbx + 2, cby + 2, cbx + CLOSE_SZ - 3, cby + CLOSE_SZ - 3);
            ctx.drawLine(cbx + CLOSE_SZ - 3, cby + 2, cbx + 2, cby + CLOSE_SZ - 3);
        }
    }

    /**
     * Draw the sidebar separator and content-area border on top of canvas
     * children.  Called from draw() after canvas.draw() so widget background
     * fills cannot obscure these lines.
     */
    protected void drawOverlay(PaintContext ctx) {
        Point absPos = getAbsolutePosition();
        PaintContext localCtx = ctx.withOrigin(absPos);
        int w = bounds.width();
        int h = bounds.height();
        int eb = eb();
        localCtx.setClip(0, 0, w, h);
        localCtx.setColor(ctx.palette().dark);
        // Sidebar right-edge separator
        localCtx.drawLine(eb + SIDEBAR_W, eb,
                          eb + SIDEBAR_W, h - eb - 1);
        // Content area border
        localCtx.drawRect(eb + SIDEBAR_W, eb,
                          w - eb * 2 - SIDEBAR_W - 1,
                          h - eb * 2 - 1);
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

        // Resize border (hitResize returns 0 when resizable=false)
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
            // Direct field update for drag: no size change, no canvas resize needed
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

            // setBounds() triggers updateCanvasBounds() via override
            setBounds(new Rect(nx, ny, nw, nh));
            if (onResize != null) onResize.run();
            return true;
        }

        return false;
    }

    // ===== Drawing & dispatch =====

    @Override
    public void draw(PaintContext ctx) {
        // Inject this window's palette into the context so all widgets in the
        // hierarchy automatically paint with the correct colour scheme.
        PaintContext palCtx = ctx.withPalette(palette);
        super.draw(palCtx);    // fills background, calls paint()
        canvas.draw(palCtx);   // draws widgets
        drawOverlay(palCtx);   // redraws inner chrome on top of widgets
    }

    @Override
    public boolean handleEvent(Event event) {
        if (canvas.handleEvent(event)) {
            event.what = Event.EV_NOTHING;
            return true;
        }
        return super.handleEvent(event);
    }

    // ===== Per-window render buffer (used by OpenGL compositor) =====

    /** Render at DPR=1 (non-HiDPI fallback). */
    public void renderToBuffer() { renderToBuffer(1); }

    /**
     * Render this window's full visual tree into a physical-resolution buffer.
     * {@code dpr} is the device pixel ratio (1 on standard displays, 2 on
     * HiDPI/Retina).  The buffer is created at {@code (width*dpr) × (height*dpr)}
     * physical pixels; a {@code g.scale(dpr,dpr)} transform is applied so that
     * all widget drawing code stays in logical pixels.  Text is rendered at
     * {@code fontSize*dpr} by {@link PaintContext#drawString} which bypasses the
     * scale transform and places glyphs at true physical coordinates, giving
     * crisp sub-pixel output on high-density displays.
     */
    public void renderToBuffer(int dpr) {
        int w  = bounds.width();
        int h  = bounds.height();
        int pw = w * dpr;
        int ph = h * dpr;
        if (renderBuffer == null
                || renderBuffer.getWidth()  != pw
                || renderBuffer.getHeight() != ph) {
            renderBuffer = new BufferedImage(pw, ph, BufferedImage.TYPE_INT_ARGB);
        }
        java.awt.Graphics2D g = renderBuffer.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Clear the buffer to transparent at physical size.
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, pw, ph);
        g.setComposite(AlphaComposite.SrcOver);

        // Scale so widget logical-pixel coordinates map to physical pixels.
        if (dpr > 1) g.scale(dpr, dpr);

        // PaintContext.withOrigin() accumulates, so starting at (-ox, -oy) means
        // each element's absPos + (-ox, -oy) = its position in the buffer.
        int ox = bounds.origin().x();
        int oy = bounds.origin().y();
        draw(PaintContext.ofAWT(g).withDpr(dpr).withOrigin(new Point(-ox, -oy)));
        g.dispose();
    }

    public BufferedImage getRenderBuffer() { return renderBuffer; }

    public boolean isDragging() { return dragging; }

    // ===== Window management =====

    /** Called by Screen.add(); not for external use. */
    void setScreen(Screen s) { this.screen = s; }

    public Screen getScreen() { return screen; }

    public void bringToFront() {
        if (screen != null) screen.bringToFront(this);
    }

    public void remove() {
        if (screen != null) {
            screen.remove(this);
            screen = null;
        }
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
