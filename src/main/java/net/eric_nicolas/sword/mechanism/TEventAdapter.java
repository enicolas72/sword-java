package net.eric_nicolas.sword.mechanism;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class TEventAdapter {

    public static TEvent ofMousePressedEvent(MouseEvent e) {
        int eventType;
        if (e.getButton() == MouseEvent.BUTTON1) eventType = TEvent.EV_MOUSE_LDOWN;
        else if (e.getButton() == MouseEvent.BUTTON3) eventType = TEvent.EV_MOUSE_RDOWN;
        else eventType = TEvent.EV_MOUSE_MDOWN;

        return new TEvent(eventType, e.getX(), e.getY(), getButtons(e), getModifiers(e));
    }

    public static TEvent ofMouseReleasedEvent(MouseEvent e) {
        int eventType;
        if (e.getButton() == MouseEvent.BUTTON1) eventType = TEvent.EV_MOUSE_LUP;
        else if (e.getButton() == MouseEvent.BUTTON3) eventType = TEvent.EV_MOUSE_RUP;
        else eventType = TEvent.EV_MOUSE_MUP;

        return new TEvent(eventType, e.getX(), e.getY(), getButtons(e), getModifiers(e));
    }

    public static TEvent ofMouseMouseEvent(MouseEvent e) {
        return new TEvent(TEvent.EV_MOUSE_MOVE, e.getX(), e.getY(), 0, getModifiers(e));
    }

    public static TEvent ofMouseDraggedEvent(MouseEvent e) {
        return new TEvent(TEvent.EV_MOUSE_MOVE, e.getX(), e.getY(), getButtons(e), getModifiers(e));
    }

    public static TEvent ofKeyPressedEvent(KeyEvent e, char ch) {
        return new TEvent(TEvent.EV_KEY_DOWN, e.getKeyCode(), ch, getModifiers(e));
    }

    public static TEvent ofKeyReleasedEvent(KeyEvent e) {
        return new TEvent(TEvent.EV_KEY_UP, e.getKeyCode(), '\0', getModifiers(e));
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
