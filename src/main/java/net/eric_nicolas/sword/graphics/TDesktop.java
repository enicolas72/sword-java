package net.eric_nicolas.sword.graphics;

import net.eric_nicolas.sword.mechanism.*;
import java.awt.Graphics2D;

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
            TEvent event = TEvent.createCommand(commandId);
            return application.handleEvent(event);
        }
        return false;
    }

    @Override
    protected void paint(Graphics2D g) {
        // Desktop draws only background (filled in parent draw method)
    }
}
