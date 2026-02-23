package net.eric_nicolas.sword.ui.base;

import net.eric_nicolas.sword.ui.events.Event;
import net.eric_nicolas.sword.ui.events.EventCommand;
import net.eric_nicolas.sword.ui.events.EventMouse;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * TDesktop - Main application desktop/background.
 * Windows are stored in a private LinkedList<Window>; the TAtom sibling
 * structure is not used for Desktop children.
 */
public class Desktop extends TZone {

    private final LinkedList<Window> windows = new LinkedList<>();
    private TObject application;

    public Desktop(int width, int height) {
        super(0, 0, width, height);
        setBackgroundColor(TColors.DESKTOP_BG);
    }

    public void setApplication(TObject app) {
        this.application = app;
    }

    /** Add a Window as a child of this desktop. */
    public void add(Window window) {
        window.setParent(this);
        windows.add(window);
    }

    /** Move window to top (last in list = drawn last = on top). */
    void bringToFront(Window window) {
        windows.remove(window);
        windows.addLast(window);
    }

    /** Remove window from the desktop. */
    void remove(Window window) {
        windows.remove(window);
    }

    @Override
    public void draw(PaintContext ctx) {
        super.draw(ctx);  // fill background, paint() (no-op), _Son (empty)
        for (Window window : windows) {
            window.draw(ctx);
        }
    }

    /**
     * Dispatch events to windows in reverse order (topmost first),
     * then fall through to local handling via super.
     */
    @Override
    public boolean handleEvent(Event event) {
        if (event.what != Event.EV_NOTHING) {
            ListIterator<Window> it = windows.listIterator(windows.size());
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
    protected boolean command(int commandId) {
        if (application != null) {
            return application.handleEvent(new EventCommand(commandId));
        }
        return false;
    }

    @Override
    protected boolean mouseLDown(EventMouse event) {
        return false;
    }

    @Override
    protected void paint(PaintContext ctx) {
        // Desktop draws only background (filled in parent draw method)
    }
}
