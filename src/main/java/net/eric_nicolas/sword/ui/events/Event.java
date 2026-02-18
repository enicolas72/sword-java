package net.eric_nicolas.sword.ui.events;

/**
 * TEvent - Base event class for the S.W.O.R.D event system.
 * Subclasses: TMouseEvent, TKeyEvent, TCmdEvent.
 */
public class Event {

    // Event types
    public static final int EV_NOTHING    = 0;
    public static final int EV_TIME       = 11;
    public static final int EV_DATE       = 12;
    public static final int EV_BROADCAST  = 13;

    public int what;
    public final long when;

    protected Event(int what) {
        this.what = what;
        this.when = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "TEvent{what=" + what + "}";
    }
}
