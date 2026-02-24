package net.eric_nicolas.sword.ui.base;

import net.eric_nicolas.sword.ui.events.Event;
import net.eric_nicolas.sword.ui.events.EventCommand;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.IntPredicate;

/**
 * Screen - Main application screen / desktop background.
 * Manages the z-ordered stack of Windows; dispatches events topmost-first.
 *
 * Screen is NOT a ScreenArea: it sits above the ScreenArea hierarchy and is
 * not part of any parent chain. Windows hold a direct reference to their
 * Screen (Window.getScreen()) for window-management operations.
 */
public class Screen {

    private final int width;
    private final int height;
    private final LinkedList<Window> windows = new LinkedList<>();
    private IntPredicate commandHandler;

    public Screen(int width, int height) {
        this.width = width;
        this.height = height;
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
        window.setScreen(this);
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

    public void draw(PaintContext ctx) {
        ctx.setColor(TColors.DESKTOP_BG);
        ctx.fillRect(0, 0, width, height);
        for (Window window : windows) {
            window.draw(ctx);
        }
    }

    public boolean handleEvent(Event event) {
        if (event.what == Event.EV_NOTHING) return false;

        // Dispatch to windows topmost-first
        ListIterator<Window> it = windows.listIterator(windows.size());
        while (it.hasPrevious()) {
            if (it.previous().handleEvent(event)) {
                event.what = Event.EV_NOTHING;
                return true;
            }
        }

        // Unhandled command: forward to application command handler
        if (event.what == EventCommand.EV_COMMAND && commandHandler != null) {
            boolean handled = commandHandler.test(((EventCommand) event).commandId);
            if (handled) event.what = Event.EV_NOTHING;
            return handled;
        }

        return false;
    }
}
