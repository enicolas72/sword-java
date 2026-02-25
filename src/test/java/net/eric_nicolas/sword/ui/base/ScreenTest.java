package net.eric_nicolas.sword.ui.base;

import net.eric_nicolas.sword.ui.events.EventCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unit tests for Screen — window management, command dispatch, quitting flag.
 */
class ScreenTest {

    private Screen screen;

    @BeforeEach
    void setUp() {
        screen = new Screen(800, 600);
    }

    // ===== Desktop background colour =====

    @Test
    void testDesktopBackgroundColor() {
        assertEquals(new Color(35, 50, 76), Screen.DESKTOP_BG);
    }

    // ===== Window management =====

    @Test
    void testInitiallyEmpty() {
        assertTrue(screen.getWindows().isEmpty());
    }

    @Test
    void testAddWindow() {
        screen.add(new Window(0, 0, 100, 100, "W"));
        assertEquals(1, screen.getWindows().size());
    }

    @Test
    void testAddSetsScreenReference() {
        Window w = new Window(0, 0, 100, 100, "W");
        screen.add(w);
        assertSame(screen, w.getScreen());
    }

    @Test
    void testAddMultipleWindows() {
        screen.add(new Window(0,  0,  100, 100, "W1"));
        screen.add(new Window(10, 10, 100, 100, "W2"));
        screen.add(new Window(20, 20, 100, 100, "W3"));
        assertEquals(3, screen.getWindows().size());
    }

    @Test
    void testBringToFrontMakesWindowLast() {
        Window w1 = new Window(0,  0,  100, 100, "W1");
        Window w2 = new Window(10, 10, 100, 100, "W2");
        screen.add(w1);
        screen.add(w2);
        screen.bringToFront(w1);   // package-private: same package as this test
        var wins = screen.getWindows();
        assertSame(w1, wins.get(wins.size() - 1));
    }

    @Test
    void testBringToFrontPreservesCount() {
        Window w1 = new Window(0, 0, 100, 100, "W1");
        Window w2 = new Window(0, 0, 100, 100, "W2");
        screen.add(w1);
        screen.add(w2);
        screen.bringToFront(w1);
        assertEquals(2, screen.getWindows().size());
    }

    @Test
    void testRemoveWindow() {
        Window w = new Window(0, 0, 100, 100, "W");
        screen.add(w);
        screen.remove(w);
        assertTrue(screen.getWindows().isEmpty());
    }

    @Test
    void testGetWindowsIsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class,
            () -> screen.getWindows().add(new Window(0, 0, 10, 10, "X")));
    }

    // ===== Quitting flag =====

    @Test
    void testInitiallyNotQuitting() {
        assertFalse(screen.isQuitting());
    }

    @Test
    void testSetQuitting() {
        screen.setQuitting(true);
        assertTrue(screen.isQuitting());
    }

    // ===== Pending command dispatch =====

    @Test
    void testCommandHandlerReceivesQueuedCommand() {
        AtomicInteger received = new AtomicInteger(-1);
        screen.setCommandHandler(cmd -> { received.set(cmd); return true; });

        // Dispatch a command event (no windows → reaches command queue)
        screen.handleEvent(new EventCommand(42));
        screen.processPendingCommands();

        assertEquals(42, received.get());
    }

    @Test
    void testProcessPendingCommandsCalledOnce() {
        AtomicInteger count = new AtomicInteger(0);
        screen.setCommandHandler(cmd -> { count.incrementAndGet(); return true; });

        screen.handleEvent(new EventCommand(1));
        screen.processPendingCommands();
        screen.processPendingCommands();  // second call: queue already empty

        assertEquals(1, count.get());
    }

    @Test
    void testNoCommandHandlerDoesNotQueue() {
        // No handler set — event still returns true (consumed), not an NPE
        EventCommand ev = new EventCommand(99);
        // With no handler, command is NOT queued; handleEvent returns false
        assertFalse(screen.handleEvent(ev));
    }
}
