package net.eric_nicolas.sword.graphics;

import net.eric_nicolas.sword.mechanism.TAtom;
import net.eric_nicolas.sword.ui.Point;
import net.eric_nicolas.sword.ui.events.Event;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

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

    public List<Widget> getWidgets() {
        return Collections.unmodifiableList(widgets);
    }

    /** Add a Widget as a child of this canvas. */
    public void add(Widget widget) {
        widget.setParent(this);
        widgets.add(widget);
    }

    /**
     * Dispatch events to LinkedList children (reverse order = topmost first),
     * then fall through to Canvas's own local handling via super.
     * super.handleEvent has no TAtom children to visit (_Son is null), so it
     * goes straight to the local switch.
     */
    @Override
    public boolean handleEvent(Event event) {
        if (event.what != Event.EV_NOTHING) {
            ListIterator<Widget> it = widgets.listIterator(widgets.size());
            while (it.hasPrevious()) {
                if (it.previous().handleEvent(event)) {
                    event.what = Event.EV_NOTHING;
                    return true;
                }
            }
        }
        return super.handleEvent(event);
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
