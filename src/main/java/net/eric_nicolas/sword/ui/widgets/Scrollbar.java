package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.Point;
import net.eric_nicolas.sword.ui.base.*;
import net.eric_nicolas.sword.ui.events.EventMouse;

/**
 * Scrollbar - Horizontal or vertical scrollbar widget.
 * Port of TLift from C++ S.W.O.R.D.
 *
 * Supports arrow-click (single step), track-click (page), and thumb drag.
 * Call setRange(contentSize, viewSize) to configure, getPosition() to read.
 * Register an onChange Runnable to be notified when the position changes.
 */
public class Scrollbar extends Widget {

    public static final int THICKNESS = 16;
    private static final int MIN_THUMB = 12;

    private final boolean horizontal;
    private int contentSize = 100;
    private int viewSize = 100;
    private int position = 0;
    private int step = 10;

    // Thumb drag state
    private boolean dragging = false;
    private int dragStartPos;
    private int dragStartPixel;

    private Runnable onChange;

    /**
     * @param x          position (local to parent)
     * @param y          position (local to parent)
     * @param length     bar length (width if horizontal, height if vertical)
     * @param horizontal true for horizontal, false for vertical
     */
    public Scrollbar(int x, int y, int length, boolean horizontal) {
        super(x, y,
              horizontal ? length : THICKNESS,
              horizontal ? THICKNESS : length);
        this.horizontal = horizontal;
    }

    public void setRange(int contentSize, int viewSize) {
        this.contentSize = Math.max(contentSize, 1);
        this.viewSize = Math.max(viewSize, 1);
        int maxPos = Math.max(0, this.contentSize - this.viewSize);
        if (position > maxPos) position = maxPos;
    }

    public int getPosition() { return position; }

    public void setPosition(int pos) {
        int maxPos = Math.max(0, contentSize - viewSize);
        position = Math.max(0, Math.min(pos, maxPos));
    }

    public void setStep(int step) { this.step = step; }

    public void setOnChange(Runnable onChange) { this.onChange = onChange; }

    // ===== Geometry helpers =====

    private int barLength() {
        return horizontal ? bounds.width() : bounds.height();
    }

    private int trackLength() {
        return Math.max(0, barLength() - 2 * THICKNESS);
    }

    private int thumbLength() {
        int track = trackLength();
        if (contentSize <= viewSize) return track;
        return Math.max(MIN_THUMB, track * viewSize / contentSize);
    }

    private int thumbOffset() {
        int maxPos = contentSize - viewSize;
        if (maxPos <= 0) return 0;
        int track = trackLength();
        int thumb = thumbLength();
        return (int) ((long) (track - thumb) * position / maxPos);
    }

    // ===== Painting =====

    @Override
    protected void paint(PaintContext ctx) {
        int bar = barLength();
        boolean scrollable = contentSize > viewSize;
        WindowPalette pal = ctx.palette();

        // Track groove (sunken background between the two buttons)
        ctx.setColor(pal.medium);
        if (horizontal) ctx.fillRect(THICKNESS, 1, bar - 2 * THICKNESS, THICKNESS - 2);
        else             ctx.fillRect(1, THICKNESS, THICKNESS - 2, bar - 2 * THICKNESS);

        // Thumb
        if (scrollable) {
            int tp = THICKNESS + thumbOffset();
            int tl = thumbLength();
            ctx.setColor(pal.face);
            if (horizontal) {
                ctx.fillRect(tp, 1, tl, THICKNESS - 2);
                ctx.setColor(pal.dark);
                ctx.drawRect(tp, 1, tl - 1, THICKNESS - 3);
            } else {
                ctx.fillRect(1, tp, THICKNESS - 2, tl);
                ctx.setColor(pal.dark);
                ctx.drawRect(1, tp, THICKNESS - 3, tl - 1);
            }
        }

        // Arrow buttons
        drawArrowButton(ctx, 0, false);
        drawArrowButton(ctx, bar - THICKNESS, true);
    }

    /**
     * Draw one arrow button.
     * @param pos  start coord of the button along the bar axis
     * @param inc  true = increment direction (right/down), false = decrement (left/up)
     */
    private void drawArrowButton(PaintContext ctx, int pos, boolean inc) {
        WindowPalette pal = ctx.palette();

        // Background
        ctx.setColor(pal.face);
        if (horizontal) ctx.fillRect(pos, 0, THICKNESS, THICKNESS);
        else             ctx.fillRect(0, pos, THICKNESS, THICKNESS);

        // Border
        ctx.setColor(pal.dark);
        if (horizontal) ctx.drawRect(pos, 0, THICKNESS - 1, THICKNESS - 1);
        else             ctx.drawRect(0, pos, THICKNESS - 1, THICKNESS - 1);

        // Triangle arrow
        ctx.setColor(pal.black);
        int[] xp, yp;
        int m = THICKNESS / 2;   // midpoint = 8
        int a = 4, b = 12;       // near and far edges of the triangle

        if (horizontal) {
            if (!inc) {
                // Left arrow: peak left, base right
                xp = new int[]{b + pos, a + pos, b + pos};
                yp = new int[]{a,       m,       b      };
            } else {
                // Right arrow: peak right, base left
                xp = new int[]{a + pos, b + pos, a + pos};
                yp = new int[]{a,       m,       b      };
            }
        } else {
            if (!inc) {
                // Up arrow: peak top, base bottom
                xp = new int[]{a, m,       b};
                yp = new int[]{b + pos, a + pos, b + pos};
            } else {
                // Down arrow: peak bottom, base top
                xp = new int[]{a,       m,       b      };
                yp = new int[]{a + pos, b + pos, a + pos};
            }
        }
        ctx.fillPolygon(xp, yp, 3);
    }

    // ===== Event handling =====

    @Override
    protected boolean mouseLDown(EventMouse event) {
        if (!contains(event.where)) return false;

        Point absPos = getAbsolutePosition();
        int coord = horizontal
                ? (event.where.x() - absPos.x())
                : (event.where.y() - absPos.y());
        int bar = barLength();

        if (coord < THICKNESS) {
            // Dec arrow
            adjustPosition(-step);
        } else if (coord >= bar - THICKNESS) {
            // Inc arrow
            adjustPosition(step);
        } else if (contentSize > viewSize) {
            int tp = THICKNESS + thumbOffset();
            int tl = thumbLength();
            if (coord < tp) {
                // Page dec (click before thumb)
                adjustPosition(-viewSize);
            } else if (coord < tp + tl) {
                // Thumb drag
                dragging = true;
                dragStartPos = position;
                dragStartPixel = coord;
            } else {
                // Page inc (click after thumb)
                adjustPosition(viewSize);
            }
        }
        return true;
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
        if (!dragging) return false;

        Point absPos = getAbsolutePosition();
        int coord = horizontal
                ? (event.where.x() - absPos.x())
                : (event.where.y() - absPos.y());

        int delta = coord - dragStartPixel;
        int track = trackLength();
        int thumb = thumbLength();
        int pixelRange = track - thumb;
        if (pixelRange <= 0) return true;

        int maxPos = Math.max(1, contentSize - viewSize);
        int newPos = dragStartPos + (int) ((long) delta * maxPos / pixelRange);
        int clamped = Math.max(0, Math.min(newPos, maxPos));
        if (clamped != position) {
            position = clamped;
            notifyChange();
        }
        return true;
    }

    private void adjustPosition(int delta) {
        int maxPos = Math.max(0, contentSize - viewSize);
        int newPos = Math.max(0, Math.min(position + delta, maxPos));
        if (newPos != position) {
            position = newPos;
            notifyChange();
        }
    }

    private void notifyChange() {
        if (onChange != null) onChange.run();
    }
}
