package net.eric_nicolas.sword.gadgets;

import net.eric_nicolas.sword.graphics.*;
import net.eric_nicolas.sword.mechanism.*;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.KeyEvent;

/**
 * TMenu - Menu container with choices.
 * Horizontal menu bar that can contain menu choices.
 */
public class TMenu extends TStdWindow {

    public static final int OP_MAIN_MENU = 0x0200;

    protected int textWidth;
    protected int hotTextWidth;
    protected TMenu fatherMenu;
    protected Font menuFont;

    /**
     * Default constructor.
     */
    public TMenu() {
        this("Menu", 0);
    }

    /**
     * Constructor with title and options.
     */
    public TMenu(String title, int options) {
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
        TRect myBounds = getBounds();
        int height = 24; // Fixed height for main menu bar
        int x = 5;

        // Calculate total width needed
        java.awt.image.BufferedImage tempImage = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g = tempImage.createGraphics();
        g.setFont(menuFont);
        java.awt.FontMetrics fm = g.getFontMetrics();

        TAtom child = son();
        while (child != null) {
            if (child instanceof TMenuChoice) {
                TMenuChoice choice = (TMenuChoice) child;
                if (!choice.hasOption(TMenuChoice.OP_SEPARATOR)) {
                    String displayText = choice.text != null ? choice.text.replace("&", "") : "";
                    int w = fm.stringWidth(displayText) + 20; // Add padding

                    // Handle submenu
                    if (choice.getSubMenu() != null) {
                        choice.getSubMenu().fatherMenu = this;
                        choice.getSubMenu().initChoices();
                    }

                    // Position choice horizontally
                    TRect choiceBounds = new TRect(
                        myBounds.a.x + x,
                        myBounds.a.y + 2,
                        myBounds.a.x + x + w,
                        myBounds.a.y + height - 2
                    );
                    choice.setBounds(choiceBounds);

                    x += w;
                }
            }
            child = child.next();
        }
        g.dispose();

        // Set menu bounds to span the width
        myBounds.b.x = myBounds.a.x + x + 5;
        myBounds.b.y = myBounds.a.y + height;
        setBounds(myBounds);
    }

    /**
     * Initialize menu choices vertically (for dropdown menus).
     */
    protected void initChoicesVertical() {
        int width = compWidth();
        int height = compHeight();

        // Resize menu
        TRect newBounds = getBounds();
        newBounds.b.x = newBounds.a.x + width;
        newBounds.b.y = newBounds.a.y + height;
        setBounds(newBounds);

        // Position menu choices
        TAtom child = son();
        int y = 26; // Start below title bar
        TRect myBounds = getBounds();
        while (child != null) {
            if (child instanceof TMenuChoice) {
                TMenuChoice choice = (TMenuChoice) child;

                // Handle submenu
                if (choice.getSubMenu() != null) {
                    choice.getSubMenu().fatherMenu = this;
                    choice.getSubMenu().initChoices();
                }

                // Set choice height
                int h = choice.hasOption(TMenuChoice.OP_SEPARATOR) ? 6 : 20;

                // Position choice
                TRect choiceBounds = new TRect(
                    myBounds.a.x + 7,
                    myBounds.a.y + y,
                    myBounds.a.x + width - 8,
                    myBounds.a.y + y + h
                );
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
            if (child instanceof TMenuChoice) {
                TMenuChoice choice = (TMenuChoice) child;
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
            if (child instanceof TMenuChoice) {
                TMenuChoice choice = (TMenuChoice) child;
                if (choice.hasOption(TMenuChoice.OP_SEPARATOR)) {
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
    protected boolean mouseLDown(TMouseEvent event) {
        if (!contains(event.where.x, event.where.y)) {
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
    protected boolean keyDown(TKeyEvent event) {
        // Handle keyboard navigation
        switch (event.keyCode) {
            case KeyEvent.VK_ESCAPE:
                closeMenu();
                return true;

            case KeyEvent.VK_UP:
                TMenuChoice current = activeChoice();
                TMenuChoice prev = (current != null) ? current.prevChoice() : lastChoice();
                if (prev == null) prev = lastChoice();
                if (prev != null) prev.down();
                return true;

            case KeyEvent.VK_DOWN:
                current = activeChoice();
                TMenuChoice next = (current != null) ? current.nextChoice() : firstChoice();
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
            TMenuChoice choice = firstChoice();
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
                if (child instanceof TMenuChoice) {
                    ((TMenuChoice) child).up();
                }
                child = child.next();
            }

            // Close parent menu if this is a submenu
            if (fatherMenu != null) {
                fatherMenu.closeMenu();
            }
        }
    }

    public TMenuChoice activeChoice() {
        TAtom child = son();
        while (child != null) {
            if (child instanceof TMenuChoice) {
                TMenuChoice choice = (TMenuChoice) child;
                if (!choice.hasStatus(TObject.SF_DISABLED) && choice.hasStatus(TMenuChoice.SF_MENU_CHOICE_DOWN)) {
                    return choice;
                }
            }
            child = child.next();
        }
        return null;
    }

    public TMenuChoice firstChoice() {
        TAtom child = son();
        while (child != null) {
            if (child instanceof TMenuChoice) {
                TMenuChoice choice = (TMenuChoice) child;
                if (!choice.hasStatus(TObject.SF_DISABLED)) {
                    return choice;
                }
            }
            child = child.next();
        }
        return null;
    }

    public TMenuChoice lastChoice() {
        TAtom child = son();
        if (child != null) child = child.last();
        while (child != null) {
            if (child instanceof TMenuChoice) {
                TMenuChoice choice = (TMenuChoice) child;
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
            ctx.fillRect(bounds.a.x, bounds.a.y, bounds.width(), bounds.height());

            // Draw bottom border
            ctx.setColor(TColors.DARK_GRAY);
            ctx.drawLine(bounds.a.x, bounds.b.y - 1, bounds.b.x - 1, bounds.b.y - 1);
        } else {
            // Dropdown menu - draw with frame and title bar
            ctx.setColor(TColors.FACE_GRAY);
            ctx.fillRect(bounds.a.x, bounds.a.y, bounds.width(), bounds.height());

            // Draw frame
            ctx.setColor(TColors.DARK_GRAY);
            ctx.drawRect(bounds.a.x, bounds.a.y, bounds.width() - 1, bounds.height() - 1);

            // Draw title bar
            ctx.setColor(TColors.DARK_GRAY);
            ctx.fillRect(bounds.a.x + 1, bounds.a.y + 1, bounds.width() - 2, 20);

            ctx.setColor(TColors.WHITE);
            ctx.setFont(menuFont);
            ctx.drawString(bounds.a.x + 5, bounds.a.y + 15, title);
        }
    }
}
