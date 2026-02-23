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
 * The content widget is rendered into an off-screen BufferedImage (virtual
 * content size).  The visible portion (viewport) is blitted to screen.
 * Optional H/V Scrollbar children control the scroll offset.
 *
 * Content coordinates vs. screen coordinates
 * ------------------------------------------
 * The content widget is rendered stand-alone (father = null, bounds at
 * virtual origin (0,0)).  Mouse events entering the viewport are translated
 * from screen space to content space before being forwarded:
 *
 *   contentX = (screenX - scrollerAbsX) + scrollX
 *   contentY = (screenY - scrollerAbsY) + scrollY
 */
public class Scroller extends Canvas {

    private final Widget content;
    private final int contentW, contentH;
    private final Scrollbar hbar, vbar;
    private int scrollX, scrollY;
    private BufferedImage contentBuffer;
    private boolean contentDirty = true;

    /**
     * @param x        position relative to parent
     * @param y        position relative to parent
     * @param viewW    viewport width  (scrollbar thickness NOT included)
     * @param viewH    viewport height (scrollbar thickness NOT included)
     * @param content  the widget providing scrollable content; its bounds
     *                 will be reset to (0, 0, contentW, contentH)
     * @param contentW virtual content width
     * @param contentH virtual content height
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
        this.contentW = contentW;
        this.contentH = contentH;

        // Content is rendered stand-alone into the off-screen buffer;
        // position it at the virtual origin.
        content.setBounds(new Rect(0, 0, contentW, contentH));

        if (addHbar) {
            hbar = new Scrollbar(0, viewH, viewW, true);
            hbar.setRange(contentW, viewW);
            hbar.setStep(Math.max(1, viewW / 10));
            hbar.setOnChange(() -> { scrollX = hbar.getPosition(); contentDirty = true; });
            add(hbar);
        } else {
            hbar = null;
        }

        if (addVbar) {
            vbar = new Scrollbar(viewW, 0, viewH, false);
            vbar.setRange(contentH, viewH);
            vbar.setStep(Math.max(1, viewH / 10));
            vbar.setOnChange(() -> { scrollY = vbar.getPosition(); contentDirty = true; });
            add(vbar);
        } else {
            vbar = null;
        }
    }

    /** Force content to be re-rendered on the next draw(). */
    public void invalidateContent() { contentDirty = true; }

    // ===== Geometry helpers =====

    private int viewportW() {
        return bounds.width()  - (vbar != null ? Scrollbar.THICKNESS : 0);
    }

    private int viewportH() {
        return bounds.height() - (hbar != null ? Scrollbar.THICKNESS : 0);
    }

    private boolean inViewport(Point p) {
        Point abs = getAbsolutePosition();
        int vw = viewportW();
        int vh = viewportH();
        return p.x() >= abs.x() && p.x() < abs.x() + vw
            && p.y() >= abs.y() && p.y() < abs.y() + vh;
    }

    // ===== Rendering =====

    @Override
    public void draw(PaintContext ctx) {
        if (!isVisible()) return;

        Point absPos = getAbsolutePosition();
        PaintContext localCtx = ctx.withOrigin(absPos);

        // Fill full background (also covers corner between scrollbars)
        localCtx.setClip(0, 0, bounds.width(), bounds.height());
        localCtx.setColor(bgColor);
        localCtx.fillRect(0, 0, bounds.width(), bounds.height());

        int vw = viewportW();
        int vh = viewportH();

        // Lazy-allocate off-screen buffer
        if (contentBuffer == null) {
            contentBuffer = new BufferedImage(contentW, contentH, BufferedImage.TYPE_INT_RGB);
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

        // Blit viewport portion (guard against scroll > content)
        int sx = Math.min(scrollX, Math.max(0, contentW - vw));
        int sy = Math.min(scrollY, Math.max(0, contentH - vh));
        int sw = Math.min(vw, contentW - sx);
        int sh = Math.min(vh, contentH - sy);
        if (sw > 0 && sh > 0) {
            BufferedImage view = contentBuffer.getSubimage(sx, sy, sw, sh);
            localCtx.setClip(0, 0, vw, vh);
            localCtx.drawImage(view, 0, 0);
        }

        // Draw scrollbars (children of this Canvas)
        for (Widget widget : getWidgets()) {
            widget.draw(ctx);
        }
    }

    // ===== Event dispatch =====

    @Override
    public boolean handleEvent(Event event) {
        if (event.what == Event.EV_NOTHING) return false;

        // Try scrollbars first (reverse order = last added first)
        ListIterator<Widget> it = getWidgets().listIterator(getWidgets().size());
        while (it.hasPrevious()) {
            Widget w = it.previous();
            if (w.handleEvent(event)) {
                event.what = Event.EV_NOTHING;
                return true;
            }
        }

        // Forward mouse events inside the viewport to the content widget,
        // translating screen coordinates to content coordinates.
        if (event instanceof EventMouse mouseEvent && inViewport(mouseEvent.where)) {
            Point absPos = getAbsolutePosition();
            // Adjusted coord: (localX + scrollX, localY + scrollY)
            // = mouseEvent.where + (scrollX - absPos.x, scrollY - absPos.y)
            EventMouse adjusted = mouseEvent.withOffset(
                    scrollX - absPos.x(),
                    scrollY - absPos.y());
            if (content.handleEvent(adjusted)) {
                event.what = Event.EV_NOTHING;
                contentDirty = true;
                return true;
            }
        }

        return false;
    }
}
