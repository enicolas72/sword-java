package net.eric_nicolas.sword.mechanism;

/**
 * TEvent - Event structure for S.W.O.R.D event system.
 * Wraps AWT events into the S.W.O.R.D event model.
 */
public class TEvent {

    // Event types - preserve C++ constants
    public static final int EV_NOTHING = 0;
    public static final int EV_MOUSE_LDOWN = 1;
    public static final int EV_MOUSE_LUP = 2;
    public static final int EV_MOUSE_RDOWN = 3;
    public static final int EV_MOUSE_RUP = 4;
    public static final int EV_MOUSE_MDOWN = 5;
    public static final int EV_MOUSE_MUP = 6;
    public static final int EV_MOUSE_MOVE = 7;
    public static final int EV_KEY_DOWN = 8;
    public static final int EV_KEY_UP = 9;
    public static final int EV_COMMAND = 10;
    public static final int EV_TIME = 11;
    public static final int EV_DATE = 12;
    public static final int EV_BROADCAST = 13;

    // Mouse button constants
    public static final int MB_LEFT = 1;
    public static final int MB_RIGHT = 2;
    public static final int MB_MIDDLE = 4;

    // Modifier keys
    public static final int KM_SHIFT = 1;
    public static final int KM_CTRL = 2;
    public static final int KM_ALT = 4;

    // Event data
    public int what;           // Event type
    public TPoint where;       // Mouse position (for mouse events)
    public int message;        // Additional data (key code, command ID, etc.)
    public char keyChar;       // Character typed (for keyTyped events)
    public int modifiers;      // Modifier keys (shift, ctrl, alt)
    public int buttons;        // Mouse button state
    public long when;          // Event timestamp

    /**
     * Default constructor - creates empty event.
     */
    public TEvent() {
        this.what = EV_NOTHING;
        this.where = new TPoint();
        this.message = 0;
        this.modifiers = 0;
        this.buttons = 0;
        this.when = System.currentTimeMillis();
    }

    /**
     * Constructor with event type.
     *
     * @param what Event type
     */
    public TEvent(int what) {
        this();
        this.what = what;
    }

    /**
     * Constructor for mouse events.
     *
     * @param what Event type
     * @param x    Mouse X coordinate
     * @param y    Mouse Y coordinate
     * @param buttons Mouse button state
     * @param modifiers Modifier keys
     */
    public TEvent(int what, int x, int y, int buttons, int modifiers) {
        this.what = what;
        this.where = new TPoint(x, y);
        this.message = 0;
        this.buttons = buttons;
        this.modifiers = modifiers;
        this.when = System.currentTimeMillis();
    }

    /**
     * Constructor for keyboard events.
     *
     * @param what Event type
     * @param keyCode Key code
     * @param modifiers Modifier keys
     */
    public TEvent(int what, int keyCode, int modifiers) {
        this.what = what;
        this.where = new TPoint();
        this.message = keyCode;
        this.buttons = 0;
        this.modifiers = modifiers;
        this.when = System.currentTimeMillis();
    }

    /**
     * Constructor for command events.
     *
     * @param commandId Command identifier
     */
    public static TEvent createCommand(int commandId) {
        TEvent event = new TEvent(EV_COMMAND);
        event.message = commandId;
        return event;
    }

    /**
     * Check if this is a mouse event.
     *
     * @return true if mouse event
     */
    public boolean isMouseEvent() {
        return what >= EV_MOUSE_LDOWN && what <= EV_MOUSE_MOVE;
    }

    /**
     * Check if this is a keyboard event.
     *
     * @return true if keyboard event
     */
    public boolean isKeyEvent() {
        return what == EV_KEY_DOWN || what == EV_KEY_UP;
    }

    /**
     * Check if a specific modifier key is pressed.
     *
     * @param modifier Modifier key constant (KM_SHIFT, KM_CTRL, KM_ALT)
     * @return true if pressed
     */
    public boolean hasModifier(int modifier) {
        return (modifiers & modifier) != 0;
    }

    /**
     * Check if a specific mouse button is pressed.
     *
     * @param button Button constant (MB_LEFT, MB_RIGHT, MB_MIDDLE)
     * @return true if pressed
     */
    public boolean hasButton(int button) {
        return (buttons & button) != 0;
    }

    @Override
    public String toString() {
        return "TEvent{what=" + what + ", where=" + where +
               ", message=" + message + ", modifiers=" + modifiers +
               ", buttons=" + buttons + "}";
    }
}
