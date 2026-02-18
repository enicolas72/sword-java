package net.eric_nicolas.sword.ui.events;

import net.eric_nicolas.sword.ui.Point;

/**
 * TMouseEvent - Mouse input event (move, click, drag).
 */
public class EventMouse extends Event {

    public static final int EV_MOUSE_LDOWN = 1;
    public static final int EV_MOUSE_LUP  = 2;
    public static final int EV_MOUSE_RDOWN = 3;
    public static final int EV_MOUSE_RUP  = 4;
    public static final int EV_MOUSE_MDOWN = 5;
    public static final int EV_MOUSE_MUP  = 6;
    public static final int EV_MOUSE_MOVE = 7;
    // Mouse button constants
    public static final int MB_LEFT   = 1;
    public static final int MB_RIGHT  = 2;
    public static final int MB_MIDDLE = 4;
    public Point where;
    public int buttons;
    public int modifiers;

    EventMouse(int what, int x, int y, int buttons, int modifiers) {
        super(what);
        this.where = new Point(x, y);
        this.buttons = buttons;
        this.modifiers = modifiers;
    }

    public boolean hasModifier(int modifier) {
        return (modifiers & modifier) != 0;
    }

    public boolean hasButton(int button) {
        return (buttons & button) != 0;
    }

    @Override
    public String toString() {
        return "TMouseEvent{what=" + what + ", where=" + where +
               ", buttons=" + buttons + ", modifiers=" + modifiers + "}";
    }
}
