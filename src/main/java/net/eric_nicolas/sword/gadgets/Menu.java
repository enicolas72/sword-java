package net.eric_nicolas.sword.gadgets;

import net.eric_nicolas.sword.graphics.*;
import net.eric_nicolas.sword.mechanism.*;
import net.eric_nicolas.sword.ui.Point;
import net.eric_nicolas.sword.ui.Rect;
import net.eric_nicolas.sword.ui.events.EventKeyboard;
import net.eric_nicolas.sword.ui.events.EventMouse;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.KeyEvent;

/**
 * TMenu - Menu container with choices.
 * Horizontal menu bar that can contain menu choices.
 */
public class Menu extends Window {

    public static final int OP_MAIN_MENU = 0x0200;

    protected int textWidth;
    protected int hotTextWidth;
    protected Menu fatherMenu;
    protected Font menuFont;

    /**
     * Default constructor.
     */
    public Menu() {
        this("Menu", 0);
    }

    /**
     * Constructor with title and options.
     */
    public Menu(String title, int options) {
        super((options & OP_MAIN_MENU) != 0 ? 0 : 5,
              (options & OP_MAIN_MENU) != 0 ? 0 : 5,
              100, 50, title, options);
        defaults();
        init(title, options);
    }

    protected void defaults() {
        textWidth = 0;
        hotTextWidth = 0;
        fatherMenu = null;
        menuFont = new Font("SansSerif", Font.PLAIN, 12);
    }

    protected void init(String title, int options) {
        setOption(options);
        setBackgroundColor(TColors.FACE_GRAY);
    }

    /**
     * Initialize menu choices - calculate sizes and positions.
     */
    public void initChoices() {
        if (hasOption(OP_MAIN_MENU)) {
            initChoicesHorizontal();
        } else {
            initChoicesVertical();
        }
    }

    /**
     * Initialize menu choices horizontally (for main menu bar).
     */
    protected void initChoicesHorizontal() {
        // Main menu is a horizontal bar
        Rect myBounds = getBounds();
        int height = 24; // Fixed height for main menu bar
        int x = 5;

        // Calculate total width needed
        java.awt.image.BufferedImage tempImage = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g = tempImage.createGraphics();
        g.setFont(menuFont);
        java.awt.FontMetrics fm = g.getFontMetrics();

        TAtom child = son();
        while (child != null) {
            if (child instanceof MenuChoice choice) {
                if (!choice.hasOption(MenuChoice.OP_SEPARATOR)) {
                    String displayText = choice.text != null ? choice.text.replace("&", "") : "";
                    int width = fm.stringWidth(displayText) + 20; // Add padding

                    // Handle submenu
                    if (choice.getSubMenu() != null) {
                        choice.getSubMenu().fatherMenu = this;
                        choice.getSubMenu().initChoices();
                    }

                    // Position choice horizontally
                    Rect choiceBounds = new Rect(Point.plus(myBounds.origin(), x, 2), width, height - 4);
                    choice.setBounds(choiceBounds);

                    x += width;
                }
            }
            child = child.next();
        }
        g.dispose();

        // Set menu bounds to span the width
        myBounds = new Rect(myBounds.origin(), x + 5, height);
        setBounds(myBounds);
    }

    /**
     * Initialize menu choices vertically (for dropdown menus).
     */
    protected void initChoicesVertical() {
        int width = compWidth();
        int height = compHeight();

        // Resize menu
        Rect newBounds = getBounds();
        newBounds = new Rect(newBounds.origin(), width, height);
        setBounds(newBounds);

        // Position menu choices
        TAtom child = son();
        int y = 26; // Start below title bar
        Rect myBounds = getBounds();
        while (child != null) {
            if (child instanceof MenuChoice choice) {
                // Handle submenu
                if (choice.getSubMenu() != null) {
                    choice.getSubMenu().fatherMenu = this;
                    choice.getSubMenu().initChoices();
                }

                // Set choice height
                int h = choice.hasOption(MenuChoice.OP_SEPARATOR) ? 6 : 20;

                // Position choice
                Rect choiceBounds = new Rect(Point.plus(myBounds.origin(), 7, y), width - 15, h);
                choice.setBounds(choiceBounds);

                y += h;
            }
            child = child.next();
        }
    }

    /**
     * Compute menu width based on choices.
     */
    protected int compWidth() {
        textWidth = 0;
        hotTextWidth = 40; // Minimum for ">>" or hotkeys

        // Create temporary graphics for text measurement
        java.awt.image.BufferedImage tempImage = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g = tempImage.createGraphics();
        g.setFont(menuFont);
        FontMetrics fm = g.getFontMetrics();

        TAtom child = son();
        while (child != null) {
            if (child instanceof MenuChoice choice) {
                if (choice.text != null) {
                    String displayText = choice.text.replace("&", "");
                    int w = fm.stringWidth(displayText);
                    if (w > textWidth) {
                        textWidth = w;
                    }
                }
            }
            child = child.next();
        }
        g.dispose();

        return textWidth + hotTextWidth + 40;
    }

    /**
     * Compute menu height based on choices.
     */
    protected int compHeight() {
        int h = 32; // Title bar + margins

        TAtom child = son();
        while (child != null) {
            if (child instanceof MenuChoice choice) {
                if (choice.hasOption(MenuChoice.OP_SEPARATOR)) {
                    h += 6;
                } else {
                    h += 20;
                }
            }
            child = child.next();
        }

        return h;
    }

    @Override
    protected boolean mouseLDown(EventMouse event) {
        if (!contains(event.where)) {
            // Only close if this is a submenu (has a father menu)
            // Top-level application menus stay open
            if (fatherMenu != null) {
                closeMenu();
                return true;
            }
        }
        return super.mouseLDown(event);
    }

    @Override
    protected boolean keyDown(EventKeyboard event) {
        // Handle keyboard navigation
        switch (event.keyCode) {
            case KeyEvent.VK_ESCAPE:
                closeMenu();
                return true;

            case KeyEvent.VK_UP:
                MenuChoice current = activeChoice();
                MenuChoice prev = (current != null) ? current.prevChoice() : lastChoice();
                if (prev == null) prev = lastChoice();
                if (prev != null) prev.down();
                return true;

            case KeyEvent.VK_DOWN:
                current = activeChoice();
                MenuChoice next = (current != null) ? current.nextChoice() : firstChoice();
                if (next == null) next = firstChoice();
                if (next != null) next.down();
                return true;

            case KeyEvent.VK_ENTER:
                current = activeChoice();
                if (current != null) {
                    current.activate();
                } else {
                    closeMenu();
                }
                return true;
        }

        // Check local shortcuts (single letter after &)
        if (event.keyCode >= KeyEvent.VK_A && event.keyCode <= KeyEvent.VK_Z) {
            char key = Character.toLowerCase((char) event.keyCode);
            MenuChoice choice = firstChoice();
            while (choice != null) {
                if (choice.localScanCode == key) {
                    choice.down();
                    choice.activate();
                    return true;
                }
                choice = choice.nextChoice();
            }
        }

        return super.keyDown(event);
    }

    public void closeMenu() {
        if (!hasOption(OP_MAIN_MENU)) {
            // Remove from desktop
            remove();

            // Deactivate all choices
            TAtom child = son();
            while (child != null) {
                if (child instanceof MenuChoice) {
                    ((MenuChoice) child).up();
                }
                child = child.next();
            }

            // Close parent menu if this is a submenu
            if (fatherMenu != null) {
                fatherMenu.closeMenu();
            }
        }
    }

    public MenuChoice activeChoice() {
        TAtom child = son();
        while (child != null) {
            if (child instanceof MenuChoice choice) {
                if (!choice.hasStatus(TObject.SF_DISABLED) && choice.hasStatus(MenuChoice.SF_MENU_CHOICE_DOWN)) {
                    return choice;
                }
            }
            child = child.next();
        }
        return null;
    }

    public MenuChoice firstChoice() {
        TAtom child = son();
        while (child != null) {
            if (child instanceof MenuChoice choice) {
                if (!choice.hasStatus(TObject.SF_DISABLED)) {
                    return choice;
                }
            }
            child = child.next();
        }
        return null;
    }

    public MenuChoice lastChoice() {
        TAtom child = son();
        if (child != null) child = child.last();
        while (child != null) {
            if (child instanceof MenuChoice choice) {
                if (!choice.hasStatus(TObject.SF_DISABLED)) {
                    return choice;
                }
            }
            child = child.previous();
        }
        return null;
    }

    @Override
    protected void paint(PaintContext ctx) {
        if (hasOption(OP_MAIN_MENU)) {
            // Main menu - draw as horizontal bar
            ctx.setColor(TColors.FACE_GRAY);
            ctx.fillRect(0, 0, bounds.width(), bounds.height());

            // Draw bottom border
            ctx.setColor(TColors.DARK_GRAY);
            ctx.drawLine(0, -1, bounds.width() - 1, bounds.height() - 1);
        } else {
            // Dropdown menu - draw with frame and title bar
            ctx.setColor(TColors.FACE_GRAY);
            ctx.fillRect(0, 0, bounds.width(), bounds.height());

            // Draw frame
            ctx.setColor(TColors.DARK_GRAY);
            ctx.drawRect(0, 0, bounds.width() - 1, bounds.height() - 1);

            // Draw title bar
            ctx.setColor(TColors.DARK_GRAY);
            ctx.fillRect(1, 1, bounds.width() - 2, 20);

            ctx.setColor(TColors.WHITE);
            ctx.setFont(menuFont);
            ctx.drawString(5, 15, title);
        }
    }
}
