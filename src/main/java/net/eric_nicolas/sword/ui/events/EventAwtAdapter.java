package net.eric_nicolas.sword.ui.events;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class EventAwtAdapter {

    public static EventMouse ofMousePressedEvent(MouseEvent e) {
        int what;
        if (e.getButton() == MouseEvent.BUTTON1) what = EventMouse.EV_MOUSE_LDOWN;
        else if (e.getButton() == MouseEvent.BUTTON3) what = EventMouse.EV_MOUSE_RDOWN;
        else what = EventMouse.EV_MOUSE_MDOWN;
        return new EventMouse(what, e.getX(), e.getY(), getButtons(e), getModifiers(e));
    }

    public static EventMouse ofMouseReleasedEvent(MouseEvent e) {
        int what;
        if (e.getButton() == MouseEvent.BUTTON1) what = EventMouse.EV_MOUSE_LUP;
        else if (e.getButton() == MouseEvent.BUTTON3) what = EventMouse.EV_MOUSE_RUP;
        else what = EventMouse.EV_MOUSE_MUP;
        return new EventMouse(what, e.getX(), e.getY(), getButtons(e), getModifiers(e));
    }

    public static EventMouse ofMouseMouseEvent(MouseEvent e) {
        return new EventMouse(EventMouse.EV_MOUSE_MOVE, e.getX(), e.getY(), 0, getModifiers(e));
    }

    public static EventMouse ofMouseDraggedEvent(MouseEvent e) {
        return new EventMouse(EventMouse.EV_MOUSE_MOVE, e.getX(), e.getY(), getButtons(e), getModifiers(e));
    }

    public static EventKeyboard ofKeyPressedEvent(KeyEvent e, char ch) {
        return new EventKeyboard(EventKeyboard.EV_KEY_DOWN, e.getKeyCode(), ch, getModifiers(e));
    }

    public static EventKeyboard ofKeyReleasedEvent(KeyEvent e) {
        return new EventKeyboard(EventKeyboard.EV_KEY_UP, e.getKeyCode(), '\0', getModifiers(e));
    }

    private static int getButtons(MouseEvent e) {
        int buttons = 0;
        if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) buttons |= EventMouse.MB_LEFT;
        if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0) buttons |= EventMouse.MB_RIGHT;
        if ((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0) buttons |= EventMouse.MB_MIDDLE;
        return buttons;
    }

    private static int getModifiers(InputEvent e) {
        int modifiers = 0;
        if (e.isShiftDown()) modifiers |= EventKeyboard.KM_SHIFT;
        if (e.isControlDown()) modifiers |= EventKeyboard.KM_CTRL;
        if (e.isAltDown()) modifiers |= EventKeyboard.KM_ALT;
        return modifiers;
    }
}
