package net.eric_nicolas.sword.tools;

import net.eric_nicolas.sword.mechanism.*;

/**
 * TShell - Base shell object for applications.
 * Simplified version - extends TObject for event handling.
 * The TApp class handles the actual event loop.
 */
public class TShell extends TObject {

    public TShell() {
        super();
    }

    /**
     * Default implementations for event handling.
     * Subclasses can override these as needed.
     */
    @Override
    protected boolean keyDown(TKeyEvent event) {
        // Default: do nothing, let subclasses handle
        return false;
    }

    @Override
    protected boolean command(int commandId) {
        // Default: do nothing, let subclasses handle
        return false;
    }
}
