package net.eric_nicolas.sword.mechanism;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class TEventAdapter {

    public static TMouseEvent ofMousePressedEvent(MouseEvent e) {
        int what;
        if (e.getButton() == MouseEvent.BUTTON1) what = TEvent.EV_MOUSE_LDOWN;
        else if (e.getButton() == MouseEvent.BUTTON3) what = TEvent.EV_MOUSE_RDOWN;
        else what = TEvent.EV_MOUSE_MDOWN;
        return new TMouseEvent(what, e.getX(), e.getY(), getButtons(e), getModifiers(e));
    }

    public static TMouseEvent ofMouseReleasedEvent(MouseEvent e) {
        int what;
        if (e.getButton() == MouseEvent.BUTTON1) what = TEvent.EV_MOUSE_LUP;
        else if (e.getButton() == MouseEvent.BUTTON3) what = TEvent.EV_MOUSE_RUP;
        else what = TEvent.EV_MOUSE_MUP;
        return new TMouseEvent(what, e.getX(), e.getY(), getButtons(e), getModifiers(e));
    }

    public static TMouseEvent ofMouseMouseEvent(MouseEvent e) {
        return new TMouseEvent(TEvent.EV_MOUSE_MOVE, e.getX(), e.getY(), 0, getModifiers(e));
    }

    public static TMouseEvent ofMouseDraggedEvent(MouseEvent e) {
        return new TMouseEvent(TEvent.EV_MOUSE_MOVE, e.getX(), e.getY(), getButtons(e), getModifiers(e));
    }

    public static TKeyEvent ofKeyPressedEvent(KeyEvent e, char ch) {
        return new TKeyEvent(TEvent.EV_KEY_DOWN, e.getKeyCode(), ch, getModifiers(e));
    }

    public static TKeyEvent ofKeyReleasedEvent(KeyEvent e) {
        return new TKeyEvent(TEvent.EV_KEY_UP, e.getKeyCode(), '\0', getModifiers(e));
    }

    private static int getButtons(MouseEvent e) {
        int buttons = 0;
        if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) buttons |= TEvent.MB_LEFT;
        if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0) buttons |= TEvent.MB_RIGHT;
        if ((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0) buttons |= TEvent.MB_MIDDLE;
        return buttons;
    }

    private static int getModifiers(InputEvent e) {
        int modifiers = 0;
        if (e.isShiftDown()) modifiers |= TEvent.KM_SHIFT;
        if (e.isControlDown()) modifiers |= TEvent.KM_CTRL;
        if (e.isAltDown()) modifiers |= TEvent.KM_ALT;
        return modifiers;
    }
}
