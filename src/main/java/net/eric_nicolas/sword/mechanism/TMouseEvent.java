package net.eric_nicolas.sword.mechanism;

/**
 * TMouseEvent - Mouse input event (move, click, drag).
 */
public class TMouseEvent extends TEvent {

    public TPoint where;
    public int buttons;
    public int modifiers;

    public TMouseEvent(int what, int x, int y, int buttons, int modifiers) {
        super(what);
        this.where = new TPoint(x, y);
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
