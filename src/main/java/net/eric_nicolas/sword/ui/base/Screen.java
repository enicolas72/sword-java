package net.eric_nicolas.sword.ui.base;

import net.eric_nicolas.sword.ui.events.Event;
import net.eric_nicolas.sword.ui.events.EventCommand;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.IntPredicate;

/**
 * Screen - Main application screen / desktop background.
 * Manages the z-ordered stack of Windows; dispatches events topmost-first.
 * Screen is NOT a ScreenArea: it sits above the ScreenArea hierarchy and is
 * not part of any parent chain. Windows hold a direct reference to their
 * Screen (Window.getScreen()) for window-management operations.
 *
 * Application-level command dispatch is intentionally deferred: when a
 * window-unhandled EV_COMMAND arrives (always from inside a GLFW callback),
 * the command ID is queued rather than dispatched immediately.
 * LwjglDriver calls processPendingCommands() from the main loop, outside any
 * GLFW callback, so that the command handler (e.g. execDialog) can safely
 * call glfwPollEvents() without violating GLFW's no-reentrant-poll rule.
 */
public class Screen {

    private final int width;
    private final int height;
    private final LinkedList<Window> windows = new LinkedList<>();
    private IntPredicate commandHandler;

    // Deferred application-level command queue (filled inside GLFW callbacks,
    // drained by processPendingCommands() from the main loop).
    private final Deque<Integer> pendingCommands = new ArrayDeque<>();
    private boolean processingCommands = false;

    // One-frame step registered by the driver; used by Dialog.execDialog()
    // to pump the event/render loop from inside a modal command handler.
    private Runnable frameStep;

    // Set to true by LwjglDriver.quit() so modal loops can detect shutdown.
    private boolean quitting = false;

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

    /**
     * Register the driver's one-frame step (poll + processPendingCommands +
     * render + swap).  Used by Dialog.execDialog() for its modal loop.
     */
    public void setFrameStep(Runnable step) { this.frameStep = step; }

    public Runnable getFrameStep() { return frameStep; }

    /** True once the driver has requested application shutdown. */
    public boolean isQuitting() { return quitting; }
    public void setQuitting(boolean q) { this.quitting = q; }

    /** Read-only view of the window stack (back-to-front order). */
    public java.util.List<Window> getWindows() {
        return java.util.Collections.unmodifiableList(windows);
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

        // Application-level command: queue for processing outside GLFW callbacks.
        // Window-handled commands (CM_OK, CM_CANCEL, …) never reach here.
        if (event.what == EventCommand.EV_COMMAND && commandHandler != null) {
            pendingCommands.add(((EventCommand) event).commandId);
            event.what = Event.EV_NOTHING;
            return true;
        }

        return false;
    }

    /**
     * Drain the application-level command queue.
     * Must be called from the main loop, never from inside a GLFW callback.
     * Re-entrant calls (e.g. from inside a modal execDialog frameStep) are
     * silently ignored; their queued commands are processed after the modal
     * handler returns.
     */
    public void processPendingCommands() {
        if (processingCommands) return;
        processingCommands = true;
        try {
            Integer cmd;
            while ((cmd = pendingCommands.poll()) != null) {
                if (commandHandler != null) commandHandler.test(cmd);
            }
        } finally {
            processingCommands = false;
        }
    }
}
