package net.eric_nicolas.sword.ui.events;

/**
 * TKeyEvent - Keyboard input event (key down/up).
 */
public class EventKeyboard extends Event {

    public static final int EV_KEY_DOWN   = 8;
    public static final int EV_KEY_UP     = 9;
    // Modifier key constants
    public static final int KM_SHIFT = 1;
    public static final int KM_CTRL  = 2;
    public static final int KM_ALT   = 4;
    public int keyCode;
    public char keyChar;
    public int modifiers;

    EventKeyboard(int what, int keyCode, char keyChar, int modifiers) {
        super(what);
        this.keyCode = keyCode;
        this.keyChar = keyChar;
        this.modifiers = modifiers;
    }

    public boolean hasModifier(int modifier) {
        return (modifiers & modifier) != 0;
    }

    @Override
    public String toString() {
        return "TKeyEvent{what=" + what + ", keyCode=" + keyCode +
               ", keyChar=" + keyChar + ", modifiers=" + modifiers + "}";
    }
}
