package net.eric_nicolas.sword.ui.base;

import net.eric_nicolas.sword.ui.Point;
import net.eric_nicolas.sword.ui.Rect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TZone - coordinate system and geometry.
 */
class ScreenAreaTest {

    private ScreenArea parent;
    private ScreenArea child;
    private ScreenArea grandchild;

    @BeforeEach
    void setUp() {
        parent = new ScreenArea(100, 100, 200, 150);
        child = new ScreenArea(10, 20, 50, 40);
        grandchild = new ScreenArea(5, 5, 20, 15);
    }

    @Test
    void testInitialBounds() {
        ScreenArea zone = new ScreenArea(10, 20, 100, 80);
        Rect bounds = zone.getBounds();

        assertEquals(10, bounds.origin().x());
        assertEquals(20, bounds.origin().y());
        assertEquals(100, bounds.width());  // x + width
        assertEquals(80, bounds.height());  // y + height
    }

    @Test
    void testAbsolutePositionWithNoParent() {
        Point abs = child.getAbsolutePosition();
        assertEquals(10, abs.x());
        assertEquals(20, abs.y());
    }

    @Test
    void testAbsolutePositionWithOneParent() {
        child.setParent(parent);
        Point abs = child.getAbsolutePosition();

        // Child at (10, 20) + parent at (100, 100) = (110, 120)
        assertEquals(110, abs.x());
        assertEquals(120, abs.y());
    }

    @Test
    void testAbsolutePositionWithGrandparent() {
        child.setParent(parent);
        grandchild.setParent(child);
        Point abs = grandchild.getAbsolutePosition();

        // Grandchild at (5, 5) + child at (10, 20) + parent at (100, 100) = (115, 125)
        assertEquals(115, abs.x());
        assertEquals(125, abs.y());
    }

    @Test
    void testContains() {
        child.setParent(parent);

        // Child is at relative (10, 20), absolute (110, 120)
        // Size is 50x40, so absolute bounds are (110, 120) to (160, 160)
        assertTrue(child.contains(110, 120));  // Top-left
        assertTrue(child.contains(135, 140));  // Center
        assertTrue(child.contains(159, 159));  // Just inside
        assertFalse(child.contains(160, 160)); // Bottom-right (exclusive)
        assertFalse(child.contains(100, 120)); // Left of zone
    }

    @Test
    void testIsVisible() {
        assertTrue(parent.isVisible());
        parent.setVisible(false);
        assertFalse(parent.isVisible());
    }

    // ===== Status flag tests (formerly TObjectTest) =====

    @Test
    void testInitialStatus() {
        ScreenArea obj = new ScreenArea(0, 0, 10, 10);
        assertTrue(obj.hasStatus(ScreenArea.SF_VISIBLE));
        assertFalse(obj.hasStatus(ScreenArea.SF_SELECTED));
    }

    @Test
    void testSetAndClearStatus() {
        ScreenArea obj = new ScreenArea(0, 0, 10, 10);
        obj.setStatus(ScreenArea.SF_SELECTED);
        assertTrue(obj.hasStatus(ScreenArea.SF_SELECTED));
        assertTrue(obj.hasStatus(ScreenArea.SF_VISIBLE)); // unaffected

        obj.clearStatus(ScreenArea.SF_SELECTED);
        assertFalse(obj.hasStatus(ScreenArea.SF_SELECTED));
    }

    @Test
    void testMultipleStatusFlags() {
        ScreenArea obj = new ScreenArea(0, 0, 10, 10);
        obj.setStatus(ScreenArea.SF_SELECTED);
        obj.setStatus(ScreenArea.SF_FOCUSED);
        assertTrue(obj.hasStatus(ScreenArea.SF_SELECTED));
        assertTrue(obj.hasStatus(ScreenArea.SF_FOCUSED));
        assertTrue(obj.hasStatus(ScreenArea.SF_VISIBLE));
    }

    @Test
    void testSetVisibleToggle() {
        ScreenArea obj = new ScreenArea(0, 0, 10, 10);
        obj.setVisible(false);
        assertFalse(obj.isVisible());
        obj.setVisible(true);
        assertTrue(obj.isVisible());
    }

    @Test
    void testIsSelected() {
        ScreenArea obj = new ScreenArea(0, 0, 10, 10);
        assertFalse(obj.isSelected());
        obj.setSelected(true);
        assertTrue(obj.isSelected());
        obj.setSelected(false);
        assertFalse(obj.isSelected());
    }

    @Test
    void testSetBackgroundColor() {
        parent.setBackgroundColor(TColors.RED);
        // Just verify no exception thrown
        assertDoesNotThrow(() -> parent.setBackgroundColor(TColors.BLUE));
    }

    @Test
    void testBoundsWidth() {
        assertEquals(200, parent.getBounds().width());
    }

    @Test
    void testBoundsHeight() {
        assertEquals(150, parent.getBounds().height());
    }

    @Test
    void testSetBounds() {
        Rect newBounds = new Rect(50, 60, 100, 100);
        parent.setBounds(newBounds);

        Rect bounds = parent.getBounds();
        assertEquals(50, bounds.origin().x());
        assertEquals(60, bounds.origin().y());
        assertEquals(100, bounds.width());
        assertEquals(100, bounds.height());
    }
}
