package net.eric_nicolas.sword.ui.base;

import net.eric_nicolas.sword.ui.events.Event;
import net.eric_nicolas.sword.ui.events.EventMouse;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.IntPredicate;

/**
 * Screen - Main application screen / desktop background.
 * Manages the z-ordered stack of Windows; dispatches events topmost-first.
 *
 * The application registers a command handler via setCommandHandler() so that
 * commands (e.g. CM_QUIT) bubble up from menus and buttons to the app.
 */
public class Screen extends TZone {

    private final LinkedList<Window> windows = new LinkedList<>();
    private IntPredicate commandHandler;

    public Screen(int width, int height) {
        super(0, 0, width, height);
        setBackgroundColor(TColors.DESKTOP_BG);
    }

    /**
     * Register the application-level command handler.
     * Called with the commandId; returns true if handled.
     */
    public void setCommandHandler(IntPredicate handler) {
        this.commandHandler = handler;
    }

    /** Add a Window (or Menu, Dialog…) to the screen. */
    public void add(Window window) {
        window.setParent(this);
        windows.add(window);
    }

    /** Move window to top (last drawn = on top). */
    void bringToFront(Window window) {
        windows.remove(window);
        windows.addLast(window);
    }

    /** Remove window from the screen. */
    void remove(Window window) {
        windows.remove(window);
    }

    @Override
    public void draw(PaintContext ctx) {
        super.draw(ctx);
        for (Window window : windows) {
            window.draw(ctx);
        }
    }

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
        if (commandHandler != null) {
            return commandHandler.test(commandId);
        }
        return false;
    }

    @Override
    protected boolean mouseLDown(EventMouse event) {
        return false;
    }

    @Override
    protected void paint(PaintContext ctx) {
        // Background fill is handled by draw() in the base class
    }
}
