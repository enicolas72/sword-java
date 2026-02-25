package net.eric_nicolas.sword.ui.widgets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Scrollbar — range, position clamping, dimensions.
 */
class ScrollbarTest {

    private Scrollbar hBar;
    private Scrollbar vBar;

    @BeforeEach
    void setUp() {
        hBar = new Scrollbar(0, 0, 200, true);
        vBar = new Scrollbar(0, 0, 200, false);
    }

    // ===== Initial state =====

    @Test
    void testInitialPositionZero() {
        assertEquals(0, hBar.getPosition());
    }

    @Test
    void testHorizontalDimensions() {
        assertEquals(200,                  hBar.getBounds().width());
        assertEquals(Scrollbar.THICKNESS,  hBar.getBounds().height());
    }

    @Test
    void testVerticalDimensions() {
        assertEquals(Scrollbar.THICKNESS,  vBar.getBounds().width());
        assertEquals(200,                  vBar.getBounds().height());
    }

    // ===== setRange =====

    @Test
    void testSetRangeEqualSizesPositionZero() {
        hBar.setRange(100, 100);   // contentSize == viewSize → maxPos = 0
        assertEquals(0, hBar.getPosition());
    }

    @Test
    void testSetRangeClampsExistingPosition() {
        hBar.setRange(200, 50);   // maxPos = 150
        hBar.setPosition(100);
        assertEquals(100, hBar.getPosition());

        hBar.setRange(120, 50);   // maxPos now = 70; old position=100 → clamped
        assertEquals(70, hBar.getPosition());
    }

    @Test
    void testSetRangeMinimumOne() {
        // Zero or negative content/view sizes are floored to 1
        hBar.setRange(0, 0);
        assertEquals(0, hBar.getPosition()); // no NPE, no crash
    }

    // ===== setPosition =====

    @Test
    void testSetPositionWithinRange() {
        hBar.setRange(200, 100);   // maxPos = 100
        hBar.setPosition(50);
        assertEquals(50, hBar.getPosition());
    }

    @Test
    void testSetPositionClampedToMax() {
        hBar.setRange(200, 100);   // maxPos = 100
        hBar.setPosition(999);
        assertEquals(100, hBar.getPosition());
    }

    @Test
    void testSetPositionClampedToZero() {
        hBar.setRange(200, 100);
        hBar.setPosition(-50);
        assertEquals(0, hBar.getPosition());
    }

    @Test
    void testSetPositionAtMax() {
        hBar.setRange(200, 100);   // maxPos = 100
        hBar.setPosition(100);
        assertEquals(100, hBar.getPosition());
    }

    // ===== Vertical bar =====

    @Test
    void testVerticalBarInitialPosition() {
        assertEquals(0, vBar.getPosition());
    }

    @Test
    void testVerticalBarSetPosition() {
        vBar.setRange(300, 100);   // maxPos = 200
        vBar.setPosition(150);
        assertEquals(150, vBar.getPosition());
    }
}
