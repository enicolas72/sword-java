package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.Rect;
import net.eric_nicolas.sword.ui.base.PaintContext;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Label — text, font size, bounds, and visibility.
 */
class LabelTest {

    // ===== Construction =====

    @Test
    void testDefaultText() {
        Label label = new Label();
        assertEquals("Text", label.getText());
    }

    @Test
    void testConstructorText() {
        Label label = new Label(0, 0, 100, 20, "Hello");
        assertEquals("Hello", label.getText());
    }

    @Test
    void testNullTextNormalized() {
        Label label = new Label(0, 0, 100, 20, null);
        assertEquals("", label.getText());
    }

    @Test
    void testEmptyText() {
        Label label = new Label(0, 0, 100, 20, "");
        assertEquals("", label.getText());
    }

    // ===== setText =====

    @Test
    void testSetText() {
        Label label = new Label(0, 0, 100, 20, "Old");
        label.setText("New");
        assertEquals("New", label.getText());
    }

    @Test
    void testSetTextWithMathNotation() {
        Label label = new Label(0, 0, 200, 40, "");
        String tex = "Result: \\math{E = mc^2}";
        label.setText(tex);
        assertEquals(tex, label.getText());
    }

    @Test
    void testSetTextWithNewline() {
        Label label = new Label(0, 0, 200, 50, "");
        String multiline = "Line 1\nLine 2";
        label.setText(multiline);
        assertEquals(multiline, label.getText());
    }

    // ===== Font size =====

    @Test
    void testDefaultFontSize() {
        Label label = new Label();
        assertEquals(PaintContext.DEFAULT_FONT_SIZE, label.getFontSize(), 0.001f);
    }

    @Test
    void testSetFontSize() {
        Label label = new Label();
        label.setFontSize(22f);
        assertEquals(22f, label.getFontSize(), 0.001f);
    }

    @Test
    void testLargeFontSize() {
        Label label = new Label();
        label.setFontSize(48f);
        assertEquals(48f, label.getFontSize(), 0.001f);
    }

    // ===== Bounds =====

    @Test
    void testBounds() {
        Label label = new Label(10, 20, 150, 30, "Test");
        Rect b = label.getBounds();
        assertEquals(10,  b.origin().x());
        assertEquals(20,  b.origin().y());
        assertEquals(150, b.width());
        assertEquals(30,  b.height());
    }

    // ===== Visibility =====

    @Test
    void testVisibleByDefault() {
        Label label = new Label(0, 0, 100, 20, "Test");
        assertTrue(label.isVisible());
    }
}
