package net.eric_nicolas.sword.mechanism;

/**
 * TEvent - Base event class for the S.W.O.R.D event system.
 * Subclasses: TMouseEvent, TKeyEvent, TCmdEvent.
 */
public class TEvent {

    // Event types
    public static final int EV_NOTHING    = 0;
    public static final int EV_MOUSE_LDOWN = 1;
    public static final int EV_MOUSE_LUP  = 2;
    public static final int EV_MOUSE_RDOWN = 3;
    public static final int EV_MOUSE_RUP  = 4;
    public static final int EV_MOUSE_MDOWN = 5;
    public static final int EV_MOUSE_MUP  = 6;
    public static final int EV_MOUSE_MOVE = 7;
    public static final int EV_KEY_DOWN   = 8;
    public static final int EV_KEY_UP     = 9;
    public static final int EV_COMMAND    = 10;
    public static final int EV_TIME       = 11;
    public static final int EV_DATE       = 12;
    public static final int EV_BROADCAST  = 13;

    // Mouse button constants
    public static final int MB_LEFT   = 1;
    public static final int MB_RIGHT  = 2;
    public static final int MB_MIDDLE = 4;

    // Modifier key constants
    public static final int KM_SHIFT = 1;
    public static final int KM_CTRL  = 2;
    public static final int KM_ALT   = 4;

    public int what;
    public final long when;

    protected TEvent(int what) {
        this.what = what;
        this.when = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "TEvent{what=" + what + "}";
    }
}
