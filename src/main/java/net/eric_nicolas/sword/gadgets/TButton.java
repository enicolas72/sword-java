package net.eric_nicolas.sword.gadgets;

import net.eric_nicolas.sword.graphics.*;
import net.eric_nicolas.sword.mechanism.*;
import java.awt.Graphics2D;

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
    protected void paint(Graphics2D g) {
        int width = bounds.width();
        int height = bounds.height();

        // Draw button background
        if (hasStatus(SF_DISABLED)) {
            g.setColor(TColors.MEDIUM_GRAY);
        } else if (pressed) {
            g.setColor(TColors.DARK_GRAY);
        } else {
            g.setColor(TColors.FACE_GRAY);
        }
        g.fillRect(bounds.a.x, bounds.a.y, width, height);

        // Draw 3D frame
        drawFrame(g, pressed);

        // Draw inside content (override in subclasses)
        drawInside(g, pressed ? 1 : 0);
    }

    protected void drawFrame(Graphics2D g, boolean pressed) {
        int x = bounds.a.x;
        int y = bounds.a.y;
        int w = bounds.width();
        int h = bounds.height();

        if (pressed) {
            // Pressed: dark on top/left
            g.setColor(TColors.DARK_GRAY);
            g.drawLine(x, y, x + w - 1, y);
            g.drawLine(x, y, x, y + h - 1);
            g.setColor(TColors.LIGHT_GRAY);
            g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
            g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
        } else {
            // Normal: light on top/left, dark on bottom/right
            g.setColor(TColors.LIGHT_GRAY);
            g.drawLine(x, y, x + w - 2, y);
            g.drawLine(x, y, x, y + h - 2);
            g.setColor(TColors.DARK_GRAY);
            g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
            g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
        }
    }

    protected void drawInside(Graphics2D g, int offset) {
        // Override in subclasses
    }

    @Override
    protected boolean mouseLDown(TEvent event) {
        if (contains(event.where.x, event.where.y) && !hasStatus(SF_DISABLED)) {
            pressed = true;
            return true;
        }
        return false;
    }

    @Override
    protected boolean mouseLUp(TEvent event) {
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
            TEvent event = TEvent.createCommand(cmd);
            ((TObject) current).handleEvent(event);
        }
    }

    public long getCommand() {
        return command;
    }

    public boolean isPressed() {
        return pressed;
    }
}
