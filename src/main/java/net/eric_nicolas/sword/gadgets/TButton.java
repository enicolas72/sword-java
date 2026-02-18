package net.eric_nicolas.sword.gadgets;

import net.eric_nicolas.sword.graphics.*;
import net.eric_nicolas.sword.mechanism.*;
import net.eric_nicolas.sword.ui.events.EventCommand;
import net.eric_nicolas.sword.ui.events.EventMouse;

/**
 * TButton - Base class for clickable buttons.
 */
public class TButton extends TZone {

    // Button options
    public static final int BO_DISABLED = 0x0001;
    public static final int BO_IMMEDIATE = 0x0002;
    public static final int BO_REPETITION = 0x0004;
    public static final int BO_NO_CASE = 0x0008;

    protected long command;
    protected int scanCode;
    protected int buttonOptions;
    protected boolean pressed;

    /**
     * Default constructor.
     */
    public TButton() {
        this(0, 0, 80, 25, 0, 0, 0);
    }

    /**
     * Constructor with position, size, command, and options.
     */
    public TButton(int x, int y, int width, int height, long command, int scanCode, int options) {
        super(x, y, width, height);
        defaults();
        init(command, scanCode, options);
    }

    protected void defaults() {
        command = 0;
        scanCode = 0;
        buttonOptions = 0;
        pressed = false;
    }

    protected void init(long command, int scanCode, int options) {
        this.command = command;
        this.scanCode = scanCode;
        this.buttonOptions = options;

        // Set disabled status based on options
        if ((options & BO_DISABLED) != 0) {
            setStatus(SF_DISABLED);
        }

        setBackgroundColor(TColors.FACE_GRAY);
    }

    @Override
    protected void paint(PaintContext ctx) {
        int width = bounds.width();
        int height = bounds.height();

        // Draw button background
        if (hasStatus(SF_DISABLED)) {
            ctx.setColor(TColors.MEDIUM_GRAY);
        } else if (pressed) {
            ctx.setColor(TColors.DARK_GRAY);
        } else {
            ctx.setColor(TColors.FACE_GRAY);
        }
        ctx.fillRect(bounds.a.x, bounds.a.y, width, height);

        // Draw 3D frame
        drawFrame(ctx, pressed);

        // Draw inside content (override in subclasses)
        drawInside(ctx, pressed ? 1 : 0);
    }

    protected void drawFrame(PaintContext ctx, boolean pressed) {
        int x = bounds.a.x;
        int y = bounds.a.y;
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
        if (contains(event.where.x, event.where.y) && !hasStatus(SF_DISABLED)) {
            pressed = true;
            return true;
        }
        return false;
    }

    @Override
    protected boolean mouseLUp(EventMouse event) {
        if (pressed) {
            pressed = false;
            if (contains(event.where.x, event.where.y) && !hasStatus(SF_DISABLED)) {
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
     * Send command up the hierarchy.
     */
    protected void sendCommand(int cmd) {
        // Find the desktop or dialog
        TAtom current = this;
        while (current != null && !(current instanceof TDesktop) && !(current instanceof TDialog)) {
            current = current.father();
        }

        // Send command event
        if (current != null && current instanceof TObject) {
            ((TObject) current).handleEvent(new EventCommand(cmd));
        }
    }

    public long getCommand() {
        return command;
    }

    public boolean isPressed() {
        return pressed;
    }
}
