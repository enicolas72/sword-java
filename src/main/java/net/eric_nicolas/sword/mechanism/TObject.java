package net.eric_nicolas.sword.mechanism;

import net.eric_nicolas.sword.ui.events.EventCommand;
import net.eric_nicolas.sword.ui.events.Event;
import net.eric_nicolas.sword.ui.events.EventKeyboard;
import net.eric_nicolas.sword.ui.events.EventMouse;

/**
 * TObject - Core application object with event handling and parent reference.
 */
public class TObject {

    // Status flags (bitmasks)
    public static final int SF_MOUSE_IN = 0x0001;
    public static final int SF_SELECTED = 0x0002;
    public static final int SF_DOWN = 0x0004;
    public static final int SF_VISIBLE = 0x0008;
    public static final int SF_MODIFIED = 0x0020;
    public static final int SF_FOCUSED = 0x0040;

    protected TObject father;
    protected int status;

    public TObject() {
        this.father = null;
        this.status = SF_VISIBLE;
    }

    public TObject father() {
        return father;
    }

    public boolean handleEvent(Event event) {
        // If event not handled by children, treat it here
        if (event.what == Event.EV_NOTHING) {
            return false;
        }

        boolean handled = switch (event.what) {
            case EventMouse.EV_MOUSE_LDOWN -> mouseLDown((EventMouse) event);
            case EventMouse.EV_MOUSE_LUP -> mouseLUp((EventMouse) event);
            case EventMouse.EV_MOUSE_RDOWN -> mouseRDown((EventMouse) event);
            case EventMouse.EV_MOUSE_RUP -> mouseRUp((EventMouse) event);
            case EventMouse.EV_MOUSE_MOVE -> mouseMove((EventMouse) event);
            case EventKeyboard.EV_KEY_DOWN -> keyDown((EventKeyboard) event);
            case EventKeyboard.EV_KEY_UP -> keyUp((EventKeyboard) event);
            case EventCommand.EV_COMMAND -> command(((EventCommand) event).commandId);
            default -> false;
        };

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
}
