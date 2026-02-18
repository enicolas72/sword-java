package net.eric_nicolas.sword.mechanism;

/**
 * TCmdEvent - Command event routed through the object hierarchy.
 */
public class TCmdEvent extends TEvent {

    public int commandId;

    public TCmdEvent(int commandId) {
        super(EV_COMMAND);
        this.commandId = commandId;
    }

    @Override
    public String toString() {
        return "TCmdEvent{commandId=" + commandId + "}";
    }
}
