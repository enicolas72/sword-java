package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.Point;
import net.eric_nicolas.sword.ui.Rect;
import net.eric_nicolas.sword.ui.base.*;
import net.eric_nicolas.sword.ui.events.Event;
import net.eric_nicolas.sword.ui.events.EventMouse;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ListIterator;

/**
 * Scroller - Scrollable viewport container.
 * Port of TScroller from C++ S.W.O.R.D.
 *
 * The content widget renders into a viewport-sized buffer (viewW × viewH).
 * The virtual content size (contentW × contentH) governs the scrollbar ranges
 * and is independent of the buffer size — the content widget is responsible
 * for rendering the correct region based on the current scroll offset, which
 * Scroller notifies via an onScroll callback.
 *
 * Mouse events inside the viewport are forwarded to the content widget as
 * viewport-local coordinates (origin = top-left of the viewport).
 */
public class Scroller extends Canvas {

    private final Widget content;
    private final int viewW, viewH;  // viewport dimensions (no scrollbar strips)
    private int contentW, contentH;  // virtual content size → scrollbar range
    private final Scrollbar hbar, vbar;
    private int scrollX, scrollY;
    private BufferedImage contentBuffer;
    private boolean contentDirty = true;
    private Runnable onScroll;

    /**
     * @param x        position relative to parent
     * @param y        position relative to parent
     * @param viewW    viewport width  (scrollbar thickness NOT included)
     * @param viewH    viewport height (scrollbar thickness NOT included)
     * @param content  the widget that renders the scrollable content; its
     *                 bounds will be set to (0, 0, viewW, viewH)
     * @param contentW initial virtual content width  (for scrollbar range)
     * @param contentH initial virtual content height (for scrollbar range)
     * @param addHbar  add a horizontal scrollbar
     * @param addVbar  add a vertical scrollbar
     */
    public Scroller(int x, int y, int viewW, int viewH,
                    Widget content, int contentW, int contentH,
                    boolean addHbar, boolean addVbar) {
        super(x, y,
              viewW + (addVbar ? Scrollbar.THICKNESS : 0),
              viewH + (addHbar ? Scrollbar.THICKNESS : 0));
        this.content  = content;
        this.viewW    = viewW;
        this.viewH    = viewH;
        this.contentW = Math.max(1, contentW);
        this.contentH = Math.max(1, contentH);

        // Content renders into a viewW × viewH buffer; its own coordinate
        // system starts at (0,0), independent of the virtual scroll position.
        content.setBounds(new Rect(0, 0, viewW, viewH));

        if (addHbar) {
            hbar = new Scrollbar(0, viewH, viewW, true);
            hbar.setRange(this.contentW, viewW);
            hbar.setStep(Math.max(1, viewW / 10));
            hbar.setOnChange(() -> {
                scrollX = hbar.getPosition();
                contentDirty = true;
                if (onScroll != null) onScroll.run();
            });
            add(hbar);
        } else {
            hbar = null;
        }

        if (addVbar) {
            vbar = new Scrollbar(viewW, 0, viewH, false);
            vbar.setRange(this.contentH, viewH);
            vbar.setStep(Math.max(1, viewH / 10));
            vbar.setOnChange(() -> {
                scrollY = vbar.getPosition();
                contentDirty = true;
                if (onScroll != null) onScroll.run();
            });
            add(vbar);
        } else {
            vbar = null;
        }
    }

    // ===== Public API =====

    /**
     * Register a callback to be invoked whenever the scroll position changes
     * due to user interaction with the scrollbars.
     */
    public void setOnScroll(Runnable onScroll) { this.onScroll = onScroll; }

    public int getScrollX() { return scrollX; }
    public int getScrollY() { return scrollY; }

    /**
     * Update the virtual content dimensions and synchronise scrollbar ranges.
     * The current scroll position is clamped to the new valid range.
     */
    public void setContentSize(int w, int h) {
        contentW = Math.max(1, w);
        contentH = Math.max(1, h);
        if (hbar != null) {
            hbar.setRange(contentW, viewW);
            scrollX = Math.min(scrollX, Math.max(0, contentW - viewW));
            hbar.setPosition(scrollX);
        }
        if (vbar != null) {
            vbar.setRange(contentH, viewH);
            scrollY = Math.min(scrollY, Math.max(0, contentH - viewH));
            vbar.setPosition(scrollY);
        }
    }

    /**
     * Programmatically move the scroll position (e.g. after a zoom).
     * Updates the scrollbar thumb positions without triggering onScroll.
     */
    public void setScrollPosition(int x, int y) {
        scrollX = Math.max(0, Math.min(x, Math.max(0, contentW - viewW)));
        scrollY = Math.max(0, Math.min(y, Math.max(0, contentH - viewH)));
        if (hbar != null) hbar.setPosition(scrollX);
        if (vbar != null) vbar.setPosition(scrollY);
        contentDirty = true;
    }

    /** Force content to be re-rendered on the next draw(). */
    public void invalidateContent() { contentDirty = true; }

    // ===== Geometry helpers =====

    private boolean inViewport(Point p) {
        Point abs = getAbsolutePosition();
        return p.x() >= abs.x() && p.x() < abs.x() + viewW
            && p.y() >= abs.y() && p.y() < abs.y() + viewH;
    }

    // ===== Rendering =====

    @Override
    public void draw(PaintContext ctx) {
        if (!isVisible()) return;

        Point absPos = getAbsolutePosition();
        PaintContext localCtx = ctx.withOrigin(absPos);

        // Fill background (covers corner cell between scrollbars too)
        localCtx.setClip(0, 0, bounds.width(), bounds.height());
        localCtx.setColor(bgColor);
        localCtx.fillRect(0, 0, bounds.width(), bounds.height());

        // Lazy-allocate viewport-sized buffer
        if (contentBuffer == null
                || contentBuffer.getWidth()  != viewW
                || contentBuffer.getHeight() != viewH) {
            contentBuffer = new BufferedImage(viewW, viewH, BufferedImage.TYPE_INT_RGB);
            contentDirty = true;
        }

        // Re-render content when dirty
        if (contentDirty) {
            Graphics2D bg = contentBuffer.createGraphics();
            PaintContext bufCtx = PaintContext.ofAWT(bg);
            content.draw(bufCtx);
            bg.dispose();
            contentDirty = false;
        }

        // Blit the full viewport buffer (content renders exactly what's visible)
        localCtx.setClip(0, 0, viewW, viewH);
        localCtx.drawImage(contentBuffer, 0, 0);

        // Draw scrollbars (Canvas children)
        for (Widget widget : getWidgets()) {
            widget.draw(ctx);
        }
    }

    // ===== Event dispatch =====

    @Override
    public boolean handleEvent(Event event) {
        if (event.what == Event.EV_NOTHING) return false;

        // Try scrollbars first (reverse = last-added first)
        ListIterator<Widget> it = getWidgets().listIterator(getWidgets().size());
        while (it.hasPrevious()) {
            Widget w = it.previous();
            if (w.handleEvent(event)) {
                event.what = Event.EV_NOTHING;
                return true;
            }
        }

        // Forward viewport mouse events to content as viewport-local coordinates.
        // Content's own absPos = (0,0) (father=null, bounds start at origin),
        // so subtracting scrollerAbsPos gives coords in [0, viewW) × [0, viewH).
        if (event instanceof EventMouse mouseEvent && inViewport(mouseEvent.where)) {
            Point absPos = getAbsolutePosition();
            EventMouse adjusted = mouseEvent.withOffset(-absPos.x(), -absPos.y());
            if (content.handleEvent(adjusted)) {
                event.what = Event.EV_NOTHING;
                contentDirty = true;
                return true;
            }
        }

        return false;
    }
}
