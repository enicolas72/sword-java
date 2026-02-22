package net.eric_nicolas.sword.gadgets;

import net.eric_nicolas.sword.graphics.*;

/**
 * TDialog - Standard dialog box for modal and modeless dialogs.
 * Supports data exchange with controls via SetData/GetData pattern.
 */
public class Dialog extends Window {

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
    public Dialog() {
        this(0, 0, 200, 150, "Dialog");
    }

    /**
     * Constructor with position, size, and title.
     */
    public Dialog(int x, int y, int width, int height, String title) {
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

        // Modal event loop - process AWT events until dialog closes
        // We need to pump events manually to avoid blocking the EDT
        java.awt.EventQueue queue = java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue();

        while (dialogResult == 0 && _Father != null) {
            try {
                // Process one event from the queue
                java.awt.AWTEvent event = queue.getNextEvent();
                Object source = event.getSource();

                // Dispatch the event to its source
                if (source instanceof java.awt.Component) {
                    ((java.awt.Component) source).dispatchEvent(event);
                } else if (source instanceof java.awt.MenuComponent) {
                    ((java.awt.MenuComponent) source).dispatchEvent(event);
                }
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

    @Override
    public void setData(Object data) {
        canvas.setData(data);
    }

    @Override
    public void getData(Object data) {
        canvas.getData(data);
    }

    @Override
    public long dataSize() {
        return canvas.dataSize();
    }

    public int getDialogResult() {
        return dialogResult;
    }
}
