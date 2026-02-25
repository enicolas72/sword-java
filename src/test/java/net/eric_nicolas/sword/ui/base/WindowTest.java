package net.eric_nicolas.sword.ui.base;

import net.eric_nicolas.sword.ui.Rect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Window — layout, options, palette, canvas synchronisation.
 */
class WindowTest {

    private Window window;

    @BeforeEach
    void setUp() {
        window = new Window(50, 50, 300, 200, "Test Window");
    }

    // ===== Title =====

    @Test
    void testInitialTitle() {
        assertEquals("Test Window", window.getTitle());
    }

    @Test
    void testSetTitle() {
        window.setTitle("New Title");
        assertEquals("New Title", window.getTitle());
    }

    // ===== Palette =====

    @Test
    void testInitialPaletteIsStandard() {
        assertSame(WindowPalette.STANDARD, window.getPalette());
    }

    @Test
    void testSetPalette() {
        window.setPalette(WindowPalette.BLUE);
        assertSame(WindowPalette.BLUE, window.getPalette());
    }

    // ===== Resizable flag =====

    @Test
    void testResizableDefaultTrue() {
        assertTrue(window.isResizable());
    }

    @Test
    void testSetResizableFalse() {
        window.setResizable(false);
        assertFalse(window.isResizable());
    }

    @Test
    void testSetResizableToggle() {
        window.setResizable(false);
        window.setResizable(true);
        assertTrue(window.isResizable());
    }

    // ===== Closable flag =====

    @Test
    void testClosableDefaultTrue() {
        assertTrue(window.isClosable());
    }

    @Test
    void testSetClosableFalse() {
        window.setClosable(false);
        assertFalse(window.isClosable());
    }

    // ===== Content area dimensions (resizable, border=5) =====

    @Test
    void testContentWidthResizable() {
        // width=300, border=5 each side, sidebar=16 → 300 - 10 - 16 = 274
        assertEquals(274, window.getContentWidth());
    }

    @Test
    void testContentHeightResizable() {
        // height=200, border=5 each side → 200 - 10 = 190
        assertEquals(190, window.getContentHeight());
    }

    // ===== Content area dimensions (non-resizable, border=1) =====

    @Test
    void testContentWidthNonResizable() {
        window.setResizable(false);
        // width=300, border=1 each side, sidebar=16 → 300 - 2 - 16 = 282
        assertEquals(282, window.getContentWidth());
    }

    @Test
    void testContentHeightNonResizable() {
        window.setResizable(false);
        // height=200, border=1 each side → 200 - 2 = 198
        assertEquals(198, window.getContentHeight());
    }

    // ===== Canvas synchronisation =====

    @Test
    void testCanvasNotNull() {
        assertNotNull(window.getCanvas());
    }

    @Test
    void testCanvasBoundsMatchContentArea() {
        Rect cb = window.getCanvas().getBounds();
        assertEquals(window.getContentWidth(),  cb.width());
        assertEquals(window.getContentHeight(), cb.height());
    }

    @Test
    void testCanvasOriginAtSidebarPlusBorder() {
        int eb = Window.BORDER;
        Rect cb = window.getCanvas().getBounds();
        assertEquals(eb + Window.SIDEBAR_W, cb.origin().x());
        assertEquals(eb,                    cb.origin().y());
    }

    @Test
    void testCanvasBoundsUpdateOnSetBounds() {
        window.setBounds(new Rect(50, 50, 400, 300));
        int expectedW = 400 - Window.BORDER * 2 - Window.SIDEBAR_W;
        int expectedH = 300 - Window.BORDER * 2;
        assertEquals(expectedW, window.getCanvas().getBounds().width());
        assertEquals(expectedH, window.getCanvas().getBounds().height());
    }

    @Test
    void testCanvasBoundsUpdateOnSetResizable() {
        // Switching to non-resizable adjusts canvas because border changes
        window.setResizable(false);
        assertEquals(window.getContentWidth(), window.getCanvas().getBounds().width());
    }
}
