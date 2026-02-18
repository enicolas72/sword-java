package net.eric_nicolas.sword.graphics;

import net.eric_nicolas.sword.mechanism.*;

/**
 * TDesktop - Main application desktop/background.
 */
public class TDesktop extends TZone {

    private TObject application;

    public TDesktop(int width, int height) {
        super(0, 0, width, height);
        setBackgroundColor(TColors.DESKTOP_BG);
    }

    /**
     * Set the application object for command routing.
     */
    public void setApplication(TObject app) {
        this.application = app;
    }

    @Override
    protected boolean command(int commandId) {
        // Route commands to application if not handled locally
        if (application != null) {
            return application.handleEvent(new TCmdEvent(commandId));
        }
        return false;
    }

    @Override
    protected boolean mouseLDown(TMouseEvent event) {
        // Desktop should not consume mouse events - let windows on top handle them
        // Only consume if clicking on empty space (not on any child window)
        return false;
    }

    @Override
    protected void paint(PaintContext ctx) {
        // Desktop draws only background (filled in parent draw method)
    }
}
