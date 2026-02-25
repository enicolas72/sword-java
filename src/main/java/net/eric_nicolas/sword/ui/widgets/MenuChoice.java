package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.base.PaintContext;
import net.eric_nicolas.sword.ui.base.ScreenArea;
import net.eric_nicolas.sword.ui.base.WindowPalette;
import net.eric_nicolas.sword.ui.base.Widget;
import net.eric_nicolas.sword.ui.base.Window;
import net.eric_nicolas.sword.ui.events.EventCommand;
import net.eric_nicolas.sword.ui.events.EventMouse;

import java.awt.Font;
import java.util.List;

/**
 * TMenuChoice - A single menu item with text, hotkey, and command.
 * Lives in a Menu's Canvas.
 */
public class MenuChoice extends Widget {

    public static final int SF_MENU_CHOICE_DOWN = 0x0100;

    protected boolean separator;
    protected String text;
    protected String hotText;
    protected int globalScanCode;
    protected int command;
    protected Menu subMenu;
    protected int localScanCode;
    protected Font menuFont;

    /**
     * Constructor for separator.
     */
    public MenuChoice() {
        super(0, 0, 100, 6);
        defaults();
        separator = true;
    }

    /**
     * Constructor for menu item with command.
     */
    public MenuChoice(String text, int globalScanCode, int command, int status) {
        super(0, 0, 100, 20);
        defaults();
        init(text, globalScanCode, command, null, status);
    }

    /**
     * Constructor for menu item with command (default status).
     */
    public MenuChoice(String text, int globalScanCode, int command) {
        this(text, globalScanCode, command, 0);
    }

    /**
     * Constructor for menu item with submenu.
     */
    public MenuChoice(String text, Menu subMenu, int status) {
        super(0, 0, 100, 20);
        defaults();
        init(text, 0, 0, subMenu, status);
    }

    protected void defaults() {
        separator = false;
        text = null;
        hotText = null;
        subMenu = null;
        globalScanCode = 0;
        command = 0;
        localScanCode = 0;
        menuFont = new Font("SansSerif", Font.PLAIN, 12);
    }

    protected void init(String text, int globalScanCode, int command, Menu subMenu, int status) {
        this.text = text;
        this.globalScanCode = globalScanCode;
        this.command = command;
        this.subMenu = subMenu;
        setStatus(status);

        if (globalScanCode != 0) {
            hotText = makeHotText(globalScanCode);
        }

        if (text != null) {
            int ampIndex = text.indexOf('&');
            if (ampIndex >= 0 && ampIndex < text.length() - 1) {
                localScanCode = Character.toLowerCase(text.charAt(ampIndex + 1));
            }
        }
    }

    protected String makeHotText(int scanCode) {
        return "";
    }

    /** Walk up Canvas → Menu to find the containing menu. */
    private Menu containingMenu() {
        ScreenArea canvas = father();          // the Menu's Canvas
        if (canvas == null) return null;
        ScreenArea menu = canvas.father();     // the Menu (Window)
        return menu instanceof Menu m ? m : null;
    }

    @Override
    protected void paint(PaintContext ctx) {
        int width = bounds.width();
        int height = bounds.height();
        WindowPalette pal = ctx.palette();

        if (separator) {
            ctx.setColor(pal.dark);
            ctx.drawLine(0, 2, width - 1, 2);
            ctx.setColor(pal.face);
            ctx.drawLine(0, 3, width - 1, 3);
        } else {
            if (hasStatus(SF_MENU_CHOICE_DOWN)) {
                ctx.setColor(pal.dark);
            } else {
                ctx.setColor(pal.face);
            }
            ctx.fillRect(0, 0, width, height);

            ctx.setFont(menuFont);
            if (!isEnabled()) {
                ctx.setColor(pal.medium);
            } else if (hasStatus(SF_MENU_CHOICE_DOWN)) {
                ctx.setColor(pal.white);
            } else {
                ctx.setColor(pal.black);
            }

            String displayText = text != null ? text.replace("&", "") : "";
            ctx.drawString(5, 14, displayText);

            if (subMenu != null) {
                ctx.drawString(width - 20, 14, ">>");
            } else if (hotText != null && !hotText.isEmpty()) {
                ctx.drawString(width - 50, 14, hotText);
            }
        }
    }

    @Override
    protected boolean mouseLDown(EventMouse event) {
        if (contains(event.where) && isEnabled()) {
            activate();
            return true;
        }
        return false;
    }

    @Override
    protected boolean mouseMove(EventMouse event) {
        boolean wasIn = hasStatus(SF_MOUSE_IN);
        boolean isIn = contains(event.where);

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
        if (isEnabled() && !hasStatus(SF_MENU_CHOICE_DOWN)) {
            Menu menu = containingMenu();
            if (menu != null) {
                for (MenuChoice mc : menu.getChoices()) {
                    if (mc != this) mc.up();
                }
            }
            setStatus(SF_MENU_CHOICE_DOWN);
        }
    }

    protected void up() {
        if (isEnabled() && hasStatus(SF_MENU_CHOICE_DOWN)) {
            clearStatus(SF_MENU_CHOICE_DOWN);
        }
    }

    protected void activate() {
        if (command != 0) {
            sendCommand(command);
            Menu menu = containingMenu();
            if (menu != null && menu.fatherMenu != null) {
                menu.closeMenu();
            }
        } else if (subMenu != null) {
            // Show submenu (not implemented yet)
        }
    }

    protected void sendCommand(int cmd) {
        ScreenArea current = this;
        while (current != null && !(current instanceof Window)) {
            current = current.father();
        }
        if (current instanceof Window w && w.getScreen() != null) {
            w.getScreen().handleEvent(new EventCommand(cmd));
        }
    }

    public MenuChoice nextChoice() {
        Menu menu = containingMenu();
        if (menu == null) return null;
        List<MenuChoice> choices = menu.getChoices();
        int idx = choices.indexOf(this);
        for (int i = idx + 1; i < choices.size(); i++) {
            if (choices.get(i).isEnabled()) return choices.get(i);
        }
        return null;
    }

    public MenuChoice prevChoice() {
        Menu menu = containingMenu();
        if (menu == null) return null;
        List<MenuChoice> choices = menu.getChoices();
        int idx = choices.indexOf(this);
        for (int i = idx - 1; i >= 0; i--) {
            if (choices.get(i).isEnabled()) return choices.get(i);
        }
        return null;
    }

    public int getCommand() {
        return command;
    }

    public int getGlobalScanCode() {
        return globalScanCode;
    }

    public Menu getSubMenu() {
        return subMenu;
    }
}
