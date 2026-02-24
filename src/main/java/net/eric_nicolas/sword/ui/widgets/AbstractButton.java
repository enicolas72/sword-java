package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.base.*;
import net.eric_nicolas.sword.ui.events.EventCommand;
import net.eric_nicolas.sword.ui.events.EventMouse;

/**
 * TButton - Base class for clickable buttons.
 */
public class AbstractButton extends Widget {

    protected long command;
    protected int scanCode;
    protected boolean pressed;

    /**
     * Default constructor.
     */
    public AbstractButton() {
        this(0, 0, 80, 25, 0, 0);
    }

    /**
     * Constructor with position, size, and command.
     */
    public AbstractButton(int x, int y, int width, int height, long command, int scanCode) {
        super(x, y, width, height);
        defaults();
        init(command, scanCode);
    }

    protected void defaults() {
        command = 0;
        scanCode = 0;
        pressed = false;
    }

    protected void init(long command, int scanCode) {
        this.command = command;
        this.scanCode = scanCode;
        setBackgroundColor(TColors.FACE_GRAY);
    }

    @Override
    protected void paint(PaintContext ctx) {
        int width = bounds.width();
        int height = bounds.height();

        // Draw button background
        if (!isEnabled()) {
            ctx.setColor(TColors.MEDIUM_GRAY);
        } else if (pressed) {
            ctx.setColor(TColors.DARK_GRAY);
        } else {
            ctx.setColor(TColors.FACE_GRAY);
        }
        ctx.fillRect(0, 0, width, height);

        // Draw 3D frame
        drawFrame(ctx, pressed);

        // Draw inside content (override in subclasses)
        drawInside(ctx, pressed ? 1 : 0);
    }

    protected void drawFrame(PaintContext ctx, boolean pressed) {
        int x = 0;
        int y = 0;
        int w = bounds.width();
        int h = bounds.height();

        if (pressed) {
            // Pressed: dark on top/left
            ctx.setColor(TColors.DARK_GRAY);
            ctx.drawLine(x, y, x + w - 1, y);
            ctx.drawLine(x, y, x, y + h - 1);
            ctx.setColor(TColors.LIGHT_GRAY);
            ctx.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
            ctx.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
        } else {
            // Normal: light on top/left, dark on bottom/right
            ctx.setColor(TColors.LIGHT_GRAY);
            ctx.drawLine(x, y, x + w - 2, y);
            ctx.drawLine(x, y, x, y + h - 2);
            ctx.setColor(TColors.DARK_GRAY);
            ctx.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
            ctx.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
        }
    }

    protected void drawInside(PaintContext ctx, int offset) {
        // Override in subclasses
    }

    @Override
    protected boolean mouseLDown(EventMouse event) {
        if (contains(event.where) && isEnabled()) {
            pressed = true;
            return true;
        }
        return false;
    }

    @Override
    protected boolean mouseLUp(EventMouse event) {
        if (pressed) {
            pressed = false;
            if (contains(event.where) && isEnabled()) {
                action();
            }
            return true;
        }
        return false;
    }

    /**
     * Perform button action - override in subclasses or send command.
     */
    protected void action() {
        if (command != 0) {
            sendCommand((int) command);
        }
    }

    /**
     * Send command to the Screen this widget lives on.
     * Walks the father chain to find the nearest Window ancestor, then
     * dispatches via its Screen so the full window z-order gets a chance
     * to handle the command (e.g. a modal Dialog intercepts CM_OK/CM_CANCEL).
     */
    protected void sendCommand(int cmd) {
        ScreenArea current = this;
        while (current != null && !(current instanceof Window)) {
            current = current.father();
        }
        if (current instanceof Window w && w.getScreen() != null) {
            w.getScreen().handleEvent(new EventCommand(cmd));
        }
    }

    public long getCommand() {
        return command;
    }

    public boolean isPressed() {
        return pressed;
    }
}
