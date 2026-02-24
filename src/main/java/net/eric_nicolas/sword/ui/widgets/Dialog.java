package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.base.TColors;
import net.eric_nicolas.sword.ui.base.Window;

/**
 * TDialog - Standard dialog box for modal and modeless dialogs.
 */
public class Dialog extends Window {

    // Dialog result codes
    public static final int CM_OK = 3101;
    public static final int CM_CANCEL = 3102;
    public static final int CM_YES = 3103;
    public static final int CM_NO = 3104;

    protected int dialogResult;
    protected boolean modal;

    public Dialog() {
        this(0, 0, 200, 150, "Dialog");
    }

    public Dialog(int x, int y, int width, int height, String title) {
        super(x, y, width, height, title);
        dialogResult = 0;
        modal = false;
        setBackgroundColor(TColors.FACE_GRAY);
        setResizable(false);
    }

    @Override
    protected boolean command(int commandId) {
        if (commandId == CM_OK || commandId == CM_CANCEL ||
            commandId == CM_YES || commandId == CM_NO) {
            return doQuitDialog(commandId);
        }
        return super.command(commandId);
    }

    protected boolean doQuitDialog(int result) {
        dialogResult = result;
        if (modal) {
            return true;
        } else {
            remove();
            return true;
        }
    }

    /** Execute dialog modally and return result code (CM_OK, CM_CANCEL, etc.). */
    public int execDialog() {
        modal = true;
        dialogResult = 0;

        java.awt.EventQueue queue = java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue();

        while (dialogResult == 0 && father != null) {
            try {
                java.awt.AWTEvent event = queue.getNextEvent();
                Object source = event.getSource();
                if (source instanceof java.awt.Component) {
                    ((java.awt.Component) source).dispatchEvent(event);
                } else if (source instanceof java.awt.MenuComponent) {
                    ((java.awt.MenuComponent) source).dispatchEvent(event);
                }
            } catch (InterruptedException e) {
                break;
            }
        }

        remove();
        modal = false;
        return dialogResult;
    }

    public int getDialogResult() {
        return dialogResult;
    }
}
