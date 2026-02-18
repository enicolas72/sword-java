package net.eric_nicolas.sword.mechanism;

/**
 * TKeyEvent - Keyboard input event (key down/up).
 */
public class TKeyEvent extends TEvent {

    public int keyCode;
    public char keyChar;
    public int modifiers;

    public TKeyEvent(int what, int keyCode, char keyChar, int modifiers) {
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
