package net.eric_nicolas.sword.gadgets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TDialog - dialog result codes.
 */
class TDialogTest {

    private TDialog dialog;

    @BeforeEach
    void setUp() {
        dialog = new TDialog(100, 100, 300, 200, "Test Dialog");
    }

    @Test
    void testInitialState() {
        assertEquals(0, dialog.getDialogResult());
    }

    @Test
    void testOKResult() {
        dialog.doQuitDialog(TDialog.CM_OK);
        assertEquals(TDialog.CM_OK, dialog.getDialogResult());
    }

    @Test
    void testCancelResult() {
        dialog.doQuitDialog(TDialog.CM_CANCEL);
        assertEquals(TDialog.CM_CANCEL, dialog.getDialogResult());
    }

    @Test
    void testYesResult() {
        dialog.doQuitDialog(TDialog.CM_YES);
        assertEquals(TDialog.CM_YES, dialog.getDialogResult());
    }

    @Test
    void testNoResult() {
        dialog.doQuitDialog(TDialog.CM_NO);
        assertEquals(TDialog.CM_NO, dialog.getDialogResult());
    }

    @Test
    void testTitle() {
        assertEquals("Test Dialog", dialog.getTitle());
    }
}
