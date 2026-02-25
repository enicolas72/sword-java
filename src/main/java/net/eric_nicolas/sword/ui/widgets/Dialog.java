package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.base.Screen;
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
        if (!modal) {
            remove();
        }
        return true;
    }

    /**
     * Execute dialog modally and return result code (CM_OK, CM_CANCEL, etc.).
     *
     * Implementation note: this method is called from inside
     * Screen.processPendingCommands(), which is itself called from the main
     * loop (NOT from inside a GLFW callback).  We therefore pump the driver's
     * frameStep (glfwPollEvents + render + swap) safely in a nested loop.
     *
     * CM_OK / CM_CANCEL are dispatched directly by Screen to Dialog.command()
     * inside the glfwPollEvents() call, setting dialogResult without going
     * through the command queue — so the modal loop sees the result on the
     * very next iteration.
     */
    public int execDialog() {
        modal = true;
        dialogResult = 0;

        Screen s = getScreen();
        Runnable step = (s != null) ? s.getFrameStep() : null;

        while (dialogResult == 0 && getScreen() != null && !isQuitting(s)) {
            if (step == null) break;  // no driver registered (e.g. in unit tests)
            step.run();
        }

        remove();
        modal = false;
        return dialogResult;
    }

    private static boolean isQuitting(Screen s) {
        return s != null && s.isQuitting();
    }

    public int getDialogResult() {
        return dialogResult;
    }
}
