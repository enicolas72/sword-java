package net.eric_nicolas.sword.ui.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Canvas — child widget management.
 */
class CanvasTest {

    private Canvas canvas;

    @BeforeEach
    void setUp() {
        canvas = new Canvas(0, 0, 200, 150);
    }

    @Test
    void testInitiallyEmpty() {
        assertTrue(canvas.getWidgets().isEmpty());
    }

    @Test
    void testAddWidget() {
        canvas.add(new Widget(0, 0, 10, 10));
        assertEquals(1, canvas.getWidgets().size());
    }

    @Test
    void testAddSetsParent() {
        Widget w = new Widget(0, 0, 10, 10);
        canvas.add(w);
        assertSame(canvas, w.father());
    }

    @Test
    void testAddMultipleWidgets() {
        canvas.add(new Widget(0,  0, 10, 10));
        canvas.add(new Widget(10, 0, 10, 10));
        canvas.add(new Widget(20, 0, 10, 10));
        assertEquals(3, canvas.getWidgets().size());
    }

    @Test
    void testGetWidgetsIsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class,
            () -> canvas.getWidgets().add(new Widget(0, 0, 1, 1)));
    }

    @Test
    void testNestedCanvas() {
        Canvas inner = new Canvas(0, 0, 50, 50);
        canvas.add(inner);
        assertEquals(1, canvas.getWidgets().size());
        assertSame(canvas, inner.father());
    }

    @Test
    void testCanvasBoundsSetCorrectly() {
        assertEquals(200, canvas.getBounds().width());
        assertEquals(150, canvas.getBounds().height());
    }

    @Test
    void testWidgetOrderPreserved() {
        Widget w1 = new Widget(0, 0, 10, 10);
        Widget w2 = new Widget(10, 0, 10, 10);
        canvas.add(w1);
        canvas.add(w2);
        assertSame(w1, canvas.getWidgets().get(0));
        assertSame(w2, canvas.getWidgets().get(1));
    }
}
