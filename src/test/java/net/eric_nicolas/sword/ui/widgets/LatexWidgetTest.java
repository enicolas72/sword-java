package net.eric_nicolas.sword.ui.widgets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LatexWidget — state management (no rendering invoked).
 */
class LatexWidgetTest {

    private LatexWidget widget;

    @BeforeEach
    void setUp() {
        widget = new LatexWidget(10, 20, 200, 100, "E = mc^2", 24f);
    }

    // ===== Latex string =====

    @Test
    void testGetLatex() {
        assertEquals("E = mc^2", widget.getLatex());
    }

    @Test
    void testSetLatex() {
        widget.setLatex("x^2 + y^2 = r^2");
        assertEquals("x^2 + y^2 = r^2", widget.getLatex());
    }

    @Test
    void testSetLatexEmpty() {
        widget.setLatex("");
        assertEquals("", widget.getLatex());
    }

    // ===== Font size =====

    @Test
    void testGetFontSize() {
        assertEquals(24f, widget.getFontSize(), 0.001f);
    }

    @Test
    void testSetFontSize() {
        widget.setFontSize(36f);
        assertEquals(36f, widget.getFontSize(), 0.001f);
    }

    @Test
    void testSetFontSizeSmall() {
        widget.setFontSize(8f);
        assertEquals(8f, widget.getFontSize(), 0.001f);
    }

    // ===== Bounds =====

    @Test
    void testInitialBounds() {
        assertEquals(200, widget.getBounds().width());
        assertEquals(100, widget.getBounds().height());
    }

    @Test
    void testInitialOrigin() {
        assertEquals(10, widget.getBounds().origin().x());
        assertEquals(20, widget.getBounds().origin().y());
    }

    // ===== Visibility =====

    @Test
    void testInitiallyVisible() {
        assertTrue(widget.isVisible());
    }

    @Test
    void testSetInvisible() {
        widget.setVisible(false);
        assertFalse(widget.isVisible());
    }
}
