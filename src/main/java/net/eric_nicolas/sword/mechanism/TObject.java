package net.eric_nicolas.sword.mechanism;

import net.eric_nicolas.sword.ui.events.EventCommand;
import net.eric_nicolas.sword.ui.events.Event;
import net.eric_nicolas.sword.ui.events.EventKeyboard;
import net.eric_nicolas.sword.ui.events.EventMouse;

/**
 * TObject - Core application object with event handling.
 * Extends TAtom with event dispatching, command processing, and status management.
 */
public class TObject extends TAtom {

    // Option flags (bitmasks)
    public static final int OP_DRAWABLE = 0x0001;
    public static final int OP_SELECTABLE = 0x0002;
    public static final int OP_CHANGE_AREA_WAIT = 0x0004;
    public static final int OP_GET_ALL_EVENTS = 0x0008;
    public static final int OP_WIN_SIZEABLE = 0x0010;
    public static final int OP_WIN_CLOSEBOX = 0x0020;
    public static final int OP_WIN_MINIMIZEBOX = 0x0040;
    public static final int OP_WIN_MAXIMIZEBOX = 0x0080;

    // Status flags (bitmasks)
    public static final int SF_MOUSE_IN = 0x0001;
    public static final int SF_SELECTED = 0x0002;
    public static final int SF_DOWN = 0x0004;
    public static final int SF_VISIBLE = 0x0008;
    public static final int SF_DISABLED = 0x0010;
    public static final int SF_MODIFIED = 0x0020;
    public static final int SF_FOCUSED = 0x0040;

    // Global commands
    public static final int CM_SELECT = 1;
    public static final int CM_UNSELECT = 2;
    public static final int CM_FILE_NEW = 10;
    public static final int CM_FILE_OPEN = 11;
    public static final int CM_FILE_SAVE = 12;
    public static final int CM_FILE_SAVE_AS = 13;
    public static final int CM_FILE_QUIT = 14;

    protected int options;
    protected int status;

    public TObject() {
        super();
        this.options = 0;
        this.status = SF_VISIBLE;
    }

    /**
     * Handle event - deals to children first (in reverse z-order), then treats locally.
     */
    public boolean handleEvent(Event event) {
        // Deal event to children (in reverse order - last to first, front to back)
        if (_Son != null && event.what != Event.EV_NOTHING) {
            TAtom child = _Son.last(); // Start from last child (topmost in z-order)
            while (child != null && event.what != Event.EV_NOTHING) {
                if (child instanceof TObject) {
                    if (((TObject) child).handleEvent(event)) {
                        // Event was handled, clear it to stop propagation
                        event.what = Event.EV_NOTHING;
                        return true;
                    }
                }
                child = child.previous(); // Go backwards (front to back)
            }
        }

        // If event not handled by children, treat it here
        if (event.what == Event.EV_NOTHING) {
            return false;
        }

        boolean handled = false;
        switch (event.what) {
            case EventMouse.EV_MOUSE_LDOWN: handled = mouseLDown((EventMouse) event); break;
            case EventMouse.EV_MOUSE_LUP:  handled = mouseLUp((EventMouse) event); break;
            case EventMouse.EV_MOUSE_RDOWN: handled = mouseRDown((EventMouse) event); break;
            case EventMouse.EV_MOUSE_RUP:  handled = mouseRUp((EventMouse) event); break;
            case EventMouse.EV_MOUSE_MOVE: handled = mouseMove((EventMouse) event); break;
            case EventKeyboard.EV_KEY_DOWN:   handled = keyDown((EventKeyboard) event); break;
            case EventKeyboard.EV_KEY_UP:     handled = keyUp((EventKeyboard) event); break;
            case EventCommand.EV_COMMAND:    handled = command(((EventCommand) event).commandId); break;
            default: handled = false;
        }

        if (handled) {
            event.what = Event.EV_NOTHING;
        }
        return handled;
    }

    protected boolean mouseLDown(EventMouse event) { return false; }
    protected boolean mouseLUp(EventMouse event) { return false; }
    protected boolean mouseRDown(EventMouse event) { return false; }
    protected boolean mouseRUp(EventMouse event) { return false; }
    protected boolean mouseMove(EventMouse event) { return false; }
    protected boolean keyDown(EventKeyboard event) { return false; }
    protected boolean keyUp(EventKeyboard event) { return false; }
    protected boolean command(int commandId) { return false; }

    public boolean hasOption(int flag) { return (options & flag) != 0; }
    public void setOption(int flag) { options |= flag; }
    public void clearOption(int flag) { options &= ~flag; }
    public boolean hasStatus(int flag) { return (status & flag) != 0; }
    public void setStatus(int flag) { status |= flag; }
    public void clearStatus(int flag) { status &= ~flag; }
    public boolean isVisible() { return hasStatus(SF_VISIBLE); }
    public void setVisible(boolean visible) {
        if (visible) setStatus(SF_VISIBLE);
        else clearStatus(SF_VISIBLE);
    }
    public boolean isSelected() { return hasStatus(SF_SELECTED); }
    public void setSelected(boolean selected) {
        if (selected) setStatus(SF_SELECTED);
        else clearStatus(SF_SELECTED);
    }

    public void setData(Object data) {}
    public Object getData() { return null; }
    public long dataSize() { return 0; }
}
