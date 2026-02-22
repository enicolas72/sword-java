package net.eric_nicolas.sword.graphics;

import net.eric_nicolas.sword.mechanism.TAtom;
import net.eric_nicolas.sword.ui.Point;

import java.util.LinkedList;

/**
 * Canvas - Transparent container for Widget components.
 * Children are stored in a private LinkedList<Widget>; the TAtom sibling
 * structure is not used for Canvas children.
 * Canvas itself extends Widget so it can be added to another Canvas via add().
 */
public class Canvas extends Widget {

    private final LinkedList<Widget> widgets = new LinkedList<>();

    public Canvas(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    /** Add a Widget as a child of this canvas. */
    public void add(Widget widget) {
        widget.setParent(this);
        widgets.add(widget);
    }

    @Override
    public void draw(PaintContext ctx) {
        if (!isVisible()) return;

        // Transparent — no background fill; parent content shows through.
        Point absPos = getAbsolutePosition();
        PaintContext localCtx = ctx.withOrigin(absPos);
        localCtx.setClip(0, 0, bounds.width(), bounds.height());
        paint(localCtx);

        for (Widget widget : widgets) {
            widget.draw(ctx);
        }
    }

    @Override
    public void setData(Object data) {
        for (Widget widget : widgets) {
            widget.setData(data);
        }
    }

    @Override
    public void getData(Object data) {
        for (Widget widget : widgets) {
            widget.getData(data);
        }
    }
}
