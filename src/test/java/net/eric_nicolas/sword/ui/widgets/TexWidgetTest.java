package net.eric_nicolas.sword.ui.widgets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TexWidget — state management (no rendering invoked).
 */
class TexWidgetTest {

    private TexWidget widget;

    @BeforeEach
    void setUp() {
        widget = new TexWidget(10, 20, 200, 100, "Hello World !", 24f);
    }

    // ===== TeX content =====

    @Test
    void testGetTex() {
        assertEquals("Hello World !", widget.getTex());
    }

    @Test
    void testSetTex() {
        widget.setTex("\\math{E = mc^2}");
        assertEquals("\\math{E = mc^2}", widget.getTex());
    }

    @Test
    void testSetTexEmpty() {
        widget.setTex("");
        assertEquals("", widget.getTex());
    }

    @Test
    void testSetTexWithNewline() {
        widget.setTex("Line one\nLine two");
        assertEquals("Line one\nLine two", widget.getTex());
    }

    @Test
    void testSetTexWithMathBlock() {
        String src = "Gauss:\n\\math{\\int_{-\\infty}^{+\\infty} e^{-x^2}\\,dx = \\sqrt{\\pi}}";
        widget.setTex(src);
        assertEquals(src, widget.getTex());
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
