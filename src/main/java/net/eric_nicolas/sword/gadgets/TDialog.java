package net.eric_nicolas.sword.gadgets;

import net.eric_nicolas.sword.graphics.*;
import net.eric_nicolas.sword.mechanism.*;

/**
 * TDialog - Standard dialog box for modal and modeless dialogs.
 * Supports data exchange with controls via SetData/GetData pattern.
 */
public class TDialog extends TStdWindow {

    // Dialog result codes
    public static final int CM_OK = 3101;
    public static final int CM_CANCEL = 3102;
    public static final int CM_YES = 3103;
    public static final int CM_NO = 3104;

    protected int dialogResult;
    protected boolean modal;

    /**
     * Default constructor.
     */
    public TDialog() {
        this(0, 0, 200, 150, "Dialog");
    }

    /**
     * Constructor with position, size, and title.
     */
    public TDialog(int x, int y, int width, int height, String title) {
        super(x, y, width, height, title, OP_WIN_CLOSEBOX);
        defaults();
        init(title);
    }

    protected void defaults() {
        dialogResult = 0;
        modal = false;
    }

    protected void init(String title) {
        setBackgroundColor(TColors.FACE_GRAY);
    }

    @Override
    protected boolean command(int commandId) {
        if (commandId == CM_OK || commandId == CM_CANCEL ||
            commandId == CM_YES || commandId == CM_NO) {
            return doQuitDialog(commandId);
        }
        return super.command(commandId);
    }

    /**
     * Handle dialog quit commands (OK, Cancel, Yes, No).
     */
    protected boolean doQuitDialog(int result) {
        dialogResult = result;
        if (modal) {
            // For modal dialogs, just store result
            // The modal loop will handle closing
            return true;
        } else {
            // For modeless dialogs, close immediately
            remove();
            return true;
        }
    }

    /**
     * Execute dialog modally and return result code.
     * @param data Optional data object for SetData/GetData exchange
     * @return Dialog result code (CM_OK, CM_CANCEL, etc.)
     */
    public int execDialog(Object data) {
        if (data != null) {
            setData(data);
        }

        modal = true;
        dialogResult = 0;

        // Simple modal loop - wait for dialog result
        while (dialogResult == 0 && _Father != null) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }

        if (data != null) {
            getData(data);
        }

        // Remove dialog from desktop
        remove();

        modal = false;
        return dialogResult;
    }

    /**
     * Set data to all child controls.
     */
    @Override
    public void setData(Object data) {
        TAtom child = son();
        while (child != null) {
            if (child instanceof TZone) {
                ((TZone) child).setData(data);
            }
            child = child.next();
        }
    }

    /**
     * Get data from all child controls.
     */
    @Override
    public void getData(Object data) {
        TAtom child = son();
        while (child != null) {
            if (child instanceof TZone) {
                ((TZone) child).getData(data);
            }
            child = child.next();
        }
    }

    /**
     * Get total data size needed for all child controls.
     */
    @Override
    public long dataSize() {
        long size = 0;
        TAtom child = son();
        while (child != null) {
            if (child instanceof TZone) {
                size += ((TZone) child).dataSize();
            }
            child = child.next();
        }
        return size;
    }

    public int getDialogResult() {
        return dialogResult;
    }
}
