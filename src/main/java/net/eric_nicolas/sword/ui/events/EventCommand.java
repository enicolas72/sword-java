package net.eric_nicolas.sword.ui.events;

/**
 * TCmdEvent - Command event routed through the object hierarchy.
 */
public class EventCommand extends Event {

    public static final int EV_COMMAND    = 10;
    public int commandId;

    public EventCommand(int commandId) {
        super(EV_COMMAND);
        this.commandId = commandId;
    }

    @Override
    public String toString() {
        return "TCmdEvent{commandId=" + commandId + "}";
    }
}
