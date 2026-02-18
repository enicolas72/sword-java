package net.eric_nicolas.sword.gadgets;

import net.eric_nicolas.sword.graphics.*;
import net.eric_nicolas.sword.mechanism.*;

import java.awt.Font;

/**
 * TMenuChoice - A single menu item with text, hotkey, and command.
 */
public class TMenuChoice extends TZone {

    public static final int OP_SEPARATOR = 0x0100;
    public static final int SF_MENU_CHOICE_DOWN = 0x0100;

    protected String text;
    protected String hotText;
    protected int globalScanCode;
    protected int command;
    protected TMenu subMenu;
    protected int localScanCode;
    protected Font menuFont;

    /**
     * Constructor for separator.
     */
    public TMenuChoice() {
        super(0, 0, 100, 6);
        defaults();
        setOption(OP_SEPARATOR);
    }

    /**
     * Constructor for menu item with command.
     */
    public TMenuChoice(String text, int globalScanCode, int command, int status, int options) {
        super(0, 0, 100, 20);
        defaults();
        init(text, globalScanCode, command, null, status, options);
    }

    /**
     * Constructor for menu item with command (default status/options).
     */
    public TMenuChoice(String text, int globalScanCode, int command) {
        this(text, globalScanCode, command, 0, 0);
    }

    /**
     * Constructor for menu item with submenu.
     */
    public TMenuChoice(String text, TMenu subMenu, int status) {
        super(0, 0, 100, 20);
        defaults();
        init(text, 0, 0, subMenu, status, 0);
    }

    protected void defaults() {
        text = null;
        hotText = null;
        subMenu = null;
        globalScanCode = 0;
        command = 0;
        localScanCode = 0;
        menuFont = new Font("SansSerif", Font.PLAIN, 12);
    }

    protected void init(String text, int globalScanCode, int command, TMenu subMenu, int status, int options) {
        this.text = text;
        this.globalScanCode = globalScanCode;
        this.command = command;
        this.subMenu = subMenu;
        setOption(options);
        setStatus(status);

        // Calculate hotkey text
        if (globalScanCode != 0) {
            hotText = makeHotText(globalScanCode);
        }

        // Calculate local scan code (for & shortcuts like &Quit -> Q)
        if (text != null) {
            int ampIndex = text.indexOf('&');
            if (ampIndex >= 0 && ampIndex < text.length() - 1) {
                localScanCode = Character.toLowerCase(text.charAt(ampIndex + 1));
            }
        }
    }

    /**
     * Convert scan code to hotkey text (simplified version).
     */
    protected String makeHotText(int scanCode) {
        // This is a simplified version - in full implementation would handle
        // various key combinations like Ctrl+X, Alt+F, F1-F10, etc.
        return "";
    }

    @Override
    protected void paint(PaintContext ctx) {
        int width = bounds.width();
        int height = bounds.height();

        if (hasOption(OP_SEPARATOR)) {
            // Draw separator
            ctx.setColor(TColors.DARK_GRAY);
            ctx.drawLine(bounds.a.x, bounds.a.y + 2, bounds.a.x + width - 1, bounds.a.y + 2);
            ctx.setColor(TColors.LIGHT_GRAY);
            ctx.drawLine(bounds.a.x, bounds.a.y + 3, bounds.a.x + width - 1, bounds.a.y + 3);
        } else {
            // Draw menu choice background
            if (hasStatus(SF_MENU_CHOICE_DOWN)) {
                ctx.setColor(TColors.DARK_GRAY);
            } else {
                ctx.setColor(TColors.FACE_GRAY);
            }
            ctx.fillRect(bounds.a.x, bounds.a.y, width, height);

            // Draw text
            ctx.setFont(menuFont);
            if (hasStatus(SF_DISABLED)) {
                ctx.setColor(TColors.MEDIUM_GRAY);
            } else if (hasStatus(SF_MENU_CHOICE_DOWN)) {
                ctx.setColor(TColors.WHITE);
            } else {
                ctx.setColor(TColors.BLACK);
            }

            // Remove & from displayed text
            String displayText = text != null ? text.replace("&", "") : "";
            ctx.drawString(bounds.a.x + 5, bounds.a.y + 14, displayText);

            // Draw hotkey or >> for submenus
            if (subMenu != null) {
                ctx.drawString(bounds.a.x + width - 20, bounds.a.y + 14, ">>");
            } else if (hotText != null && !hotText.isEmpty()) {
                ctx.drawString(bounds.a.x + width - 50, bounds.a.y + 14, hotText);
            }
        }
    }

    @Override
    protected boolean mouseLDown(TMouseEvent event) {
        if (contains(event.where.x, event.where.y) && !hasStatus(SF_DISABLED)) {
            activate();
            return true;
        }
        return false;
    }

    @Override
    protected boolean mouseMove(TMouseEvent event) {
        boolean wasIn = hasStatus(SF_MOUSE_IN);
        boolean isIn = contains(event.where.x, event.where.y);

        if (isIn != wasIn) {
            if (isIn) {
                setStatus(SF_MOUSE_IN);
                becomeActiveZone();
            } else {
                clearStatus(SF_MOUSE_IN);
                leaveActiveZone();
            }
            return true;
        }
        return false;
    }

    protected void becomeActiveZone() {
        down();
    }

    protected void leaveActiveZone() {
        up();
    }

    protected void down() {
        if (!hasStatus(SF_DISABLED) && !hasStatus(SF_MENU_CHOICE_DOWN)) {
            // Lift all other choices
            TAtom myFather = father();
            if (myFather != null && myFather instanceof TMenu) {
                TAtom sibling = myFather.son();
                while (sibling != null) {
                    if (sibling instanceof TMenuChoice && sibling != this) {
                        ((TMenuChoice) sibling).up();
                    }
                    sibling = sibling.next();
                }
            }
            // Set this choice down
            setStatus(SF_MENU_CHOICE_DOWN);
        }
    }

    protected void up() {
        if (!hasStatus(SF_DISABLED) && hasStatus(SF_MENU_CHOICE_DOWN)) {
            clearStatus(SF_MENU_CHOICE_DOWN);
        }
    }

    protected void activate() {
        if (command != 0) {
            // Trigger command
            sendCommand(command);
            // Only close the menu if it's a submenu (not a top-level menu)
            TAtom myFather = father();
            if (myFather != null && myFather instanceof TMenu) {
                TMenu menu = (TMenu) myFather;
                // Only close if this menu has a father menu (i.e., it's a submenu)
                // Top-level menus stay open
                if (menu.fatherMenu != null) {
                    menu.closeMenu();
                }
            }
        } else if (subMenu != null) {
            // Show submenu (not implemented yet)
        }
    }

    /**
     * Send command up the hierarchy.
     */
    protected void sendCommand(int cmd) {
        // Find the desktop first
        TAtom current = this;
        while (current != null && !(current instanceof TDesktop)) {
            current = current.father();
        }

        // If we found desktop, send command from there so it reaches TApp
        if (current != null && current instanceof TDesktop) {
            ((TObject) current).handleEvent(new TCmdEvent(cmd));
        }
    }

    public TMenuChoice nextChoice() {
        TAtom next = next();
        while (next != null) {
            if (next instanceof TMenuChoice && !((TMenuChoice) next).hasStatus(SF_DISABLED)) {
                return (TMenuChoice) next;
            }
            next = next.next();
        }
        return null;
    }

    public TMenuChoice prevChoice() {
        TAtom prev = previous();
        while (prev != null) {
            if (prev instanceof TMenuChoice && !((TMenuChoice) prev).hasStatus(SF_DISABLED)) {
                return (TMenuChoice) prev;
            }
            prev = prev.previous();
        }
        return null;
    }

    public int getCommand() {
        return command;
    }

    public int getGlobalScanCode() {
        return globalScanCode;
    }

    public TMenu getSubMenu() {
        return subMenu;
    }
}
