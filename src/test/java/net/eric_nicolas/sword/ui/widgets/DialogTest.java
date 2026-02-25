package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.base.WindowPalette;
import net.eric_nicolas.sword.ui.widgets.Dialog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TDialog - dialog result codes.
 */
class DialogTest {

    private Dialog dialog;

    @BeforeEach
    void setUp() {
        dialog = new Dialog(100, 100, 300, 200, "Test Dialog");
    }

    @Test
    void testInitialState() {
        assertEquals(0, dialog.getDialogResult());
    }

    @Test
    void testOKResult() {
        dialog.doQuitDialog(Dialog.CM_OK);
        assertEquals(Dialog.CM_OK, dialog.getDialogResult());
    }

    @Test
    void testCancelResult() {
        dialog.doQuitDialog(Dialog.CM_CANCEL);
        assertEquals(Dialog.CM_CANCEL, dialog.getDialogResult());
    }

    @Test
    void testYesResult() {
        dialog.doQuitDialog(Dialog.CM_YES);
        assertEquals(Dialog.CM_YES, dialog.getDialogResult());
    }

    @Test
    void testNoResult() {
        dialog.doQuitDialog(Dialog.CM_NO);
        assertEquals(Dialog.CM_NO, dialog.getDialogResult());
    }

    @Test
    void testTitle() {
        assertEquals("Test Dialog", dialog.getTitle());
    }

    @Test
    void testPaletteIsBlue() {
        assertSame(WindowPalette.BLUE, dialog.getPalette());
    }

    @Test
    void testNotResizable() {
        assertFalse(dialog.isResizable());
    }
}
