package net.eric_nicolas.sword.ui.widgets;

import net.eric_nicolas.sword.ui.Point;
import net.eric_nicolas.sword.ui.Rect;
import net.eric_nicolas.sword.ui.base.PaintContext;
import net.eric_nicolas.sword.ui.base.TColors;
import net.eric_nicolas.sword.ui.base.Widget;
import net.eric_nicolas.sword.ui.base.Window;
import net.eric_nicolas.sword.ui.events.EventKeyboard;
import net.eric_nicolas.sword.ui.events.EventMouse;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * TMenu - Menu container with choices.
 * Horizontal menu bar that can contain MenuChoice widgets in its Canvas.
 */
public class Menu extends Window {

    protected boolean mainMenu;
    protected int textWidth;
    protected int hotTextWidth;
    protected Menu fatherMenu;
    protected Font menuFont;

    public Menu() {
        this("Menu", false);
    }

    public Menu(String title, boolean mainMenu) {
        super(mainMenu ? 0 : 5, mainMenu ? 0 : 5, 100, 50, title);
        this.mainMenu = mainMenu;
        defaults();
        init(title);
    }

    protected void defaults() {
        textWidth = 0;
        hotTextWidth = 0;
        fatherMenu = null;
        menuFont = new Font("SansSerif", Font.PLAIN, 12);
    }

    protected void init(String title) {
        setBackgroundColor(TColors.FACE_GRAY);
    }

    /** All MenuChoices living in this menu's canvas. */
    List<MenuChoice> getChoices() {
        List<MenuChoice> result = new ArrayList<>();
        for (Widget w : getCanvas().getWidgets()) {
            if (w instanceof MenuChoice mc) result.add(mc);
        }
        return result;
    }

    public void initChoices() {
        if (mainMenu) {
            initChoicesHorizontal();
        } else {
            initChoicesVertical();
        }
    }

    protected void initChoicesHorizontal() {
        Rect myBounds = getBounds();
        int height = 24;
        int x = 5;

        java.awt.image.BufferedImage tempImage = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g = tempImage.createGraphics();
        g.setFont(menuFont);
        java.awt.FontMetrics fm = g.getFontMetrics();

        for (MenuChoice choice : getChoices()) {
            if (!choice.separator) {
                String displayText = choice.text != null ? choice.text.replace("&", "") : "";
                int width = fm.stringWidth(displayText) + 20;

                if (choice.getSubMenu() != null) {
                    choice.getSubMenu().fatherMenu = this;
                    choice.getSubMenu().initChoices();
                }

                Rect choiceBounds = new Rect(Point.plus(myBounds.origin(), x, 2), width, height - 4);
                choice.setBounds(choiceBounds);
                x += width;
            }
        }
        g.dispose();

        myBounds = new Rect(myBounds.origin(), x + 5, height);
        setBounds(myBounds);
    }

    protected void initChoicesVertical() {
        int width = compWidth();
        int height = compHeight();

        Rect newBounds = getBounds();
        newBounds = new Rect(newBounds.origin(), width, height);
        setBounds(newBounds);

        int y = 26;
        Rect myBounds = getBounds();
        for (MenuChoice choice : getChoices()) {
            if (choice.getSubMenu() != null) {
                choice.getSubMenu().fatherMenu = this;
                choice.getSubMenu().initChoices();
            }

            int h = choice.separator ? 6 : 20;
            Rect choiceBounds = new Rect(Point.plus(myBounds.origin(), 7, y), width - 15, h);
            choice.setBounds(choiceBounds);
            y += h;
        }
    }

    protected int compWidth() {
        textWidth = 0;
        hotTextWidth = 40;

        java.awt.image.BufferedImage tempImage = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g = tempImage.createGraphics();
        g.setFont(menuFont);
        FontMetrics fm = g.getFontMetrics();

        for (MenuChoice choice : getChoices()) {
            if (choice.text != null) {
                String displayText = choice.text.replace("&", "");
                int w = fm.stringWidth(displayText);
                if (w > textWidth) textWidth = w;
            }
        }
        g.dispose();

        return textWidth + hotTextWidth + 40;
    }

    protected int compHeight() {
        int h = 32;
        for (MenuChoice choice : getChoices()) {
            h += choice.separator ? 6 : 20;
        }
        return h;
    }

    @Override
    protected boolean mouseLDown(EventMouse event) {
        if (!contains(event.where)) {
            if (fatherMenu != null) {
                closeMenu();
                return true;
            }
        }
        return super.mouseLDown(event);
    }

    @Override
    protected boolean keyDown(EventKeyboard event) {
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

        if (event.keyCode >= KeyEvent.VK_A && event.keyCode <= KeyEvent.VK_Z) {
            char key = Character.toLowerCase((char) event.keyCode);
            for (MenuChoice choice : getChoices()) {
                if (choice.localScanCode == key) {
                    choice.down();
                    choice.activate();
                    return true;
                }
            }
        }

        return super.keyDown(event);
    }

    public void closeMenu() {
        if (!mainMenu) {
            remove();
            for (MenuChoice mc : getChoices()) {
                mc.up();
            }
            if (fatherMenu != null) {
                fatherMenu.closeMenu();
            }
        }
    }

    public MenuChoice activeChoice() {
        for (MenuChoice mc : getChoices()) {
            if (mc.isEnabled() && mc.hasStatus(MenuChoice.SF_MENU_CHOICE_DOWN)) {
                return mc;
            }
        }
        return null;
    }

    public MenuChoice firstChoice() {
        for (MenuChoice mc : getChoices()) {
            if (mc.isEnabled()) return mc;
        }
        return null;
    }

    public MenuChoice lastChoice() {
        List<MenuChoice> choices = getChoices();
        for (int i = choices.size() - 1; i >= 0; i--) {
            if (choices.get(i).isEnabled()) return choices.get(i);
        }
        return null;
    }

    @Override
    protected void paint(PaintContext ctx) {
        if (mainMenu) {
            ctx.setColor(TColors.FACE_GRAY);
            ctx.fillRect(0, 0, bounds.width(), bounds.height());

            ctx.setColor(TColors.DARK_GRAY);
            ctx.drawLine(0, -1, bounds.width() - 1, bounds.height() - 1);
        } else {
            ctx.setColor(TColors.FACE_GRAY);
            ctx.fillRect(0, 0, bounds.width(), bounds.height());

            ctx.setColor(TColors.DARK_GRAY);
            ctx.drawRect(0, 0, bounds.width() - 1, bounds.height() - 1);

            ctx.setColor(TColors.DARK_GRAY);
            ctx.fillRect(1, 1, bounds.width() - 2, 20);

            ctx.setColor(TColors.WHITE);
            ctx.setFont(menuFont);
            ctx.drawString(5, 15, title);
        }
    }
}
