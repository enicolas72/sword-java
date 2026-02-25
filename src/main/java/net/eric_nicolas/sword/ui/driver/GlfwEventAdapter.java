package net.eric_nicolas.sword.ui.driver;

import net.eric_nicolas.sword.ui.events.EventKeyboard;
import net.eric_nicolas.sword.ui.events.EventMouse;

import static org.lwjgl.glfw.GLFW.*;

/**
 * GlfwEventAdapter - Converts GLFW input callbacks into S.W.O.R.D events.
 *
 * Key codes: GLFW letters (65–90) and digits (48–57) intentionally match
 * java.awt.event.KeyEvent VK_A–VK_Z and VK_0–VK_9, so widgets that compare
 * keyCode to those constants continue to work unchanged.
 */
public class GlfwEventAdapter {

    // ===== Mouse =====

    public static EventMouse mouseMove(double x, double y, int buttons) {
        return new EventMouse(EventMouse.EV_MOUSE_MOVE, (int) x, (int) y, buttons, 0);
    }

    public static EventMouse mouseDown(int glfwButton, double x, double y, int glfwMods) {
        int what = switch (glfwButton) {
            case GLFW_MOUSE_BUTTON_LEFT   -> EventMouse.EV_MOUSE_LDOWN;
            case GLFW_MOUSE_BUTTON_RIGHT  -> EventMouse.EV_MOUSE_RDOWN;
            case GLFW_MOUSE_BUTTON_MIDDLE -> EventMouse.EV_MOUSE_MDOWN;
            default -> -1;
        };
        if (what < 0) return null;
        return new EventMouse(what, (int) x, (int) y, glfwButtonMask(glfwButton), glfwMods(glfwMods));
    }

    public static EventMouse mouseUp(int glfwButton, double x, double y, int glfwMods) {
        int what = switch (glfwButton) {
            case GLFW_MOUSE_BUTTON_LEFT   -> EventMouse.EV_MOUSE_LUP;
            case GLFW_MOUSE_BUTTON_RIGHT  -> EventMouse.EV_MOUSE_RUP;
            case GLFW_MOUSE_BUTTON_MIDDLE -> EventMouse.EV_MOUSE_MUP;
            default -> -1;
        };
        if (what < 0) return null;
        return new EventMouse(what, (int) x, (int) y, 0, glfwMods(glfwMods));
    }

    // ===== Keyboard =====

    public static EventKeyboard keyDown(int glfwKey, char keyChar, int glfwMods) {
        int vk = glfwKeyToVK(glfwKey);
        if (vk < 0) return null;
        return new EventKeyboard(EventKeyboard.EV_KEY_DOWN, vk, keyChar, glfwMods(glfwMods));
    }

    public static EventKeyboard keyUp(int glfwKey, int glfwMods) {
        int vk = glfwKeyToVK(glfwKey);
        if (vk < 0) return null;
        return new EventKeyboard(EventKeyboard.EV_KEY_UP, vk, '\0', glfwMods(glfwMods));
    }

    // ===== Helpers =====

    private static int glfwButtonMask(int glfwButton) {
        return switch (glfwButton) {
            case GLFW_MOUSE_BUTTON_LEFT   -> EventMouse.MB_LEFT;
            case GLFW_MOUSE_BUTTON_RIGHT  -> EventMouse.MB_RIGHT;
            case GLFW_MOUSE_BUTTON_MIDDLE -> EventMouse.MB_MIDDLE;
            default -> 0;
        };
    }

    private static int glfwMods(int mods) {
        int result = 0;
        if ((mods & GLFW_MOD_SHIFT)   != 0) result |= EventKeyboard.KM_SHIFT;
        if ((mods & GLFW_MOD_CONTROL) != 0) result |= EventKeyboard.KM_CTRL;
        if ((mods & GLFW_MOD_ALT)     != 0) result |= EventKeyboard.KM_ALT;
        return result;
    }

    /**
     * Map GLFW key codes to the VK_ values used throughout the widget layer.
     * A–Z (65–90) and 0–9 (48–57) are identical in both systems.
     * Special keys are mapped to their java.awt.event.KeyEvent equivalents
     * (hardcoded as integers so this class has no AWT dependency).
     */
    private static int glfwKeyToVK(int key) {
        // A-Z and 0-9: GLFW codes match Java VK codes directly.
        if ((key >= 48 && key <= 57) || (key >= 65 && key <= 90)) return key;

        return switch (key) {
            case GLFW_KEY_SPACE     ->  32; // VK_SPACE
            case GLFW_KEY_ENTER     ->  10; // VK_ENTER
            case GLFW_KEY_ESCAPE    ->  27; // VK_ESCAPE
            case GLFW_KEY_TAB       ->   9; // VK_TAB
            case GLFW_KEY_BACKSPACE ->   8; // VK_BACK_SPACE
            case GLFW_KEY_DELETE    -> 127; // VK_DELETE
            case GLFW_KEY_UP        ->  38; // VK_UP
            case GLFW_KEY_DOWN      ->  40; // VK_DOWN
            case GLFW_KEY_LEFT      ->  37; // VK_LEFT
            case GLFW_KEY_RIGHT     ->  39; // VK_RIGHT
            case GLFW_KEY_HOME      ->  36; // VK_HOME
            case GLFW_KEY_END       ->  35; // VK_END
            case GLFW_KEY_PAGE_UP   ->  33; // VK_PAGE_UP
            case GLFW_KEY_PAGE_DOWN ->  34; // VK_PAGE_DOWN
            default -> -1; // unknown / ignored
        };
    }
}
