package net.eric_nicolas.sword.graphics;

import net.eric_nicolas.sword.mechanism.TAtom;
import net.eric_nicolas.sword.ui.Point;

/**
 * Canvas - Transparent container for Widget components.
 * Does not fill its background, making it see-through over the parent's content.
 * Only Widgets can be added to a Canvas (enforced at compile time via add()).
 * Canvas itself extends Widget so that a Canvas (e.g. GroupBox) can be added to
 * another Canvas via add().
 */
public class Canvas extends Widget {

    public Canvas(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    /** Add a Widget as a child of this canvas. */
    public void add(Widget widget) {
        widget.insertIn(this);
    }

    @Override
    public void draw(PaintContext ctx) {
        if (!isVisible()) return;

        // Transparent — no background fill; parent content shows through.
        Point absPos = getAbsolutePosition();
        PaintContext localCtx = ctx.withOrigin(absPos);
        localCtx.setClip(0, 0, bounds.width(), bounds.height());
        paint(localCtx);

        TAtom child = _Son;
        while (child != null) {
            if (child instanceof Widget widget) {
                widget.draw(ctx);
            }
            child = child.next();
        }
    }

    @Override
    public void setData(Object data) {
        TAtom child = _Son;
        while (child != null) {
            if (child instanceof Widget widget) {
                widget.setData(data);
            }
            child = child.next();
        }
    }

    @Override
    public void getData(Object data) {
        TAtom child = _Son;
        while (child != null) {
            if (child instanceof Widget widget) {
                widget.getData(data);
            }
            child = child.next();
        }
    }
}
