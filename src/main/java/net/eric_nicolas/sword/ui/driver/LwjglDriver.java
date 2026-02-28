package net.eric_nicolas.sword.ui.driver;

import net.eric_nicolas.sword.ui.base.Screen;
import net.eric_nicolas.sword.ui.base.Window;
import net.eric_nicolas.sword.ui.events.EventKeyboard;
import net.eric_nicolas.sword.ui.events.EventMouse;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.IntPredicate;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * LwjglDriver - OpenGL-backed rendering and event loop via LWJGL 3 / GLFW.
 *
 * Each S.W.O.R.D Window renders its content into its own BufferedImage
 * (using existing Java2D / PaintContext logic). The driver uploads each
 * image as an OpenGL texture and composites them onto the screen in
 * z-order. Windows currently being dragged are drawn with alpha = 0.7,
 * giving a translucent ghost effect.
 *
 * macOS requirement: GLFW must run on the main thread. Start the JVM with
 *   -XstartOnFirstThread
 * The Maven exec plugin is already configured to pass this argument.
 */
public class LwjglDriver {

    private static final float DRAG_ALPHA = 0.7f;

    private final Screen screen;
    private final int width;
    private final int height;
    private final IntPredicate hotKeyHandler;

    private int dpr;  // device pixel ratio (1 on standard displays, 2 on Retina/HiDPI)
    private int fbW;  // physical framebuffer width
    private int fbH;  // physical framebuffer height

    private long glfwWindow;
    private boolean running;

    // OpenGL: shader, quad geometry
    private int shaderProgram;
    private int vao, vbo;

    // Uniform locations
    private int uPos, uSize, uScreen, uAlpha, uTexture;

    // Per-window OpenGL texture IDs
    private final Map<Window, Integer> textures = new HashMap<>();

    // Live mouse state (position and button mask) for move events
    private double mouseX, mouseY;
    private int heldButtons;

    public LwjglDriver(String title, int width, int height,
                       Screen screen, IntPredicate hotKeyHandler) {
        this.screen        = screen;
        this.width         = width;
        this.height        = height;
        this.hotKeyHandler = hotKeyHandler;

        // Prevent the AWT native toolkit (LWCToolkit) from initialising on macOS.
        // LWCToolkit installs its own NSApplicationDelegate which intercepts AppKit
        // mouse/keyboard events before GLFW's NSWindow can receive them, breaking
        // all GLFW input callbacks.  Headless mode uses a pure-software toolkit
        // that has no native display connection and leaves GLFW in full control.
        // BufferedImage, Graphics2D, Font and FontMetrics all work in headless mode.
        System.setProperty("java.awt.headless", "true");

        // Prevent Java2D from creating its own Metal/OpenGL context on the main
        // thread, which would race with GLFW's CGL context and produce a black
        // window on macOS.  Force the software (Ductus) pipeline instead.
        System.setProperty("sun.java2d.opengl", "false");
        System.setProperty("sun.java2d.metal",  "false");

        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) throw new RuntimeException("Failed to initialize GLFW");

        // OpenGL 3.3 Core Profile (required on macOS: forward-compat flag)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE,        GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE,             GLFW_FALSE);
        // Keep window hidden until the GL context is fully set up; shown in run().
        glfwWindowHint(GLFW_VISIBLE,               GLFW_FALSE);

        glfwWindow = glfwCreateWindow(width, height, title, NULL, NULL);
        if (glfwWindow == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create GLFW window");
        }

        glfwMakeContextCurrent(glfwWindow);
        glfwSwapInterval(1); // VSync: cap to monitor refresh rate

        GL.createCapabilities();

        // Query physical framebuffer size (may be 2× logical on Retina/HiDPI).
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer fbWbuf = stack.mallocInt(1);
            IntBuffer fbHbuf = stack.mallocInt(1);
            glfwGetFramebufferSize(glfwWindow, fbWbuf, fbHbuf);
            fbW = fbWbuf.get(0);
            fbH = fbHbuf.get(0);
            dpr = Math.max(1, fbW / width);
            glViewport(0, 0, fbW, fbH);
        }

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        shaderProgram = buildShaderProgram();
        setupQuadGeometry();

        uPos     = glGetUniformLocation(shaderProgram, "uPos");
        uSize    = glGetUniformLocation(shaderProgram, "uSize");
        uScreen  = glGetUniformLocation(shaderProgram, "uScreen");
        uAlpha   = glGetUniformLocation(shaderProgram, "uAlpha");
        uTexture = glGetUniformLocation(shaderProgram, "uTexture");

        setupCallbacks();

        // Register the one-frame step used by Dialog.execDialog() for its modal loop.
        // The lambda is safe to call from outside GLFW callbacks (main loop or modal loop).
        screen.setFrameStep(() -> {
            glfwPollEvents();
            screen.processPendingCommands();
            renderFrame();
            glfwSwapBuffers(glfwWindow);
        });
    }

    // ===== Event loop =====

    public void run() {
        running = true;
        glfwShowWindow(glfwWindow);

        // macOS 10.14+ (Mojave and later): the NSGLContext does not fire its
        // internal -update until the run loop processes the first expose event.
        // Without this prime, glfwSwapBuffers silently no-ops and the window
        // stays black.  One extra render+swap+poll before the main loop is the
        // standard GLFW workaround.
        renderFrame();
        glfwSwapBuffers(glfwWindow);
        glfwPollEvents();

        while (running && !glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents();               // fires GLFW callbacks → SWORD events
            screen.processPendingCommands(); // drain app-level commands outside callbacks
            renderFrame();
            glfwSwapBuffers(glfwWindow);
        }
        cleanup();
    }

    public void quit() {
        running = false;
        screen.setQuitting(true);
        glfwSetWindowShouldClose(glfwWindow, true);
    }

    /** No-op: the continuous render loop re-draws every frame automatically. */
    public void forceRepaint() {}

    // ===== Rendering =====

    private void renderFrame() {
        // Clear screen to desktop background colour
        float r = Screen.DESKTOP_BG.getRed()   / 255f;
        float g = Screen.DESKTOP_BG.getGreen() / 255f;
        float b = Screen.DESKTOP_BG.getBlue()  / 255f;
        glClearColor(r, g, b, 1f);
        glClear(GL_COLOR_BUFFER_BIT);

        glUseProgram(shaderProgram);
        glUniform2f(uScreen, fbW, fbH);
        glUniform1i(uTexture, 0);
        glActiveTexture(GL_TEXTURE0);
        glBindVertexArray(vao);

        cleanupRemovedWindows();

        for (Window win : screen.getWindows()) {
            win.renderToBuffer(dpr);

            int texId = ensureTexture(win);
            uploadTexture(texId, win.getRenderBuffer());

            glUniform2f(uPos,  win.getBounds().origin().x() * dpr, win.getBounds().origin().y() * dpr);
            glUniform2f(uSize, win.getBounds().width()       * dpr, win.getBounds().height()      * dpr);
            glUniform1f(uAlpha, win.isDragging() ? DRAG_ALPHA : 1f);

            glBindTexture(GL_TEXTURE_2D, texId);
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        }

        glBindVertexArray(0);
        glUseProgram(0);
    }

    private int ensureTexture(Window win) {
        return textures.computeIfAbsent(win, w -> {
            int id = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, id);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            return id;
        });
    }

    private void uploadTexture(int texId, BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();

        // Gather ARGB pixels from the BufferedImage.
        int[] argb = img.getRGB(0, 0, w, h, null, 0, w);

        // Pack into a byte buffer as RGBA for OpenGL.
        ByteBuffer buf = MemoryUtil.memAlloc(w * h * 4);
        for (int pixel : argb) {
            buf.put((byte) ((pixel >> 16) & 0xFF)); // R
            buf.put((byte) ((pixel >>  8) & 0xFF)); // G
            buf.put((byte) ( pixel        & 0xFF)); // B
            buf.put((byte) ((pixel >> 24) & 0xFF)); // A
        }
        buf.flip();

        glBindTexture(GL_TEXTURE_2D, texId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        MemoryUtil.memFree(buf);
    }

    /** Delete GPU textures for windows that are no longer on the screen. */
    private void cleanupRemovedWindows() {
        HashSet<Window> live = new HashSet<>(screen.getWindows());
        var it = textures.entrySet().iterator();
        while (it.hasNext()) {
            var entry = it.next();
            if (!live.contains(entry.getKey())) {
                glDeleteTextures(entry.getValue());
                it.remove();
            }
        }
    }

    // ===== OpenGL setup =====

    private int buildShaderProgram() {
        String vertSrc = """
                #version 330 core
                layout(location = 0) in vec2 aPosition;
                layout(location = 1) in vec2 aTexCoord;
                uniform vec2 uPos;
                uniform vec2 uSize;
                uniform vec2 uScreen;
                out vec2 vTexCoord;
                void main() {
                    vec2 screenPixel = uPos + aPosition * uSize;
                    float ndcX =  screenPixel.x / uScreen.x * 2.0 - 1.0;
                    float ndcY = 1.0 - screenPixel.y / uScreen.y * 2.0;
                    gl_Position = vec4(ndcX, ndcY, 0.0, 1.0);
                    vTexCoord = aTexCoord;
                }
                """;
        String fragSrc = """
                #version 330 core
                in vec2 vTexCoord;
                uniform sampler2D uTexture;
                uniform float uAlpha;
                out vec4 fragColor;
                void main() {
                    fragColor = texture(uTexture, vTexCoord);
                    fragColor.a *= uAlpha;
                }
                """;

        int vert = compileShader(GL_VERTEX_SHADER,   vertSrc, "vertex");
        int frag = compileShader(GL_FRAGMENT_SHADER, fragSrc, "fragment");

        int prog = glCreateProgram();
        glAttachShader(prog, vert);
        glAttachShader(prog, frag);
        glLinkProgram(prog);
        if (glGetProgrami(prog, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("Shader link error:\n" + glGetProgramInfoLog(prog));
        }
        glDeleteShader(vert);
        glDeleteShader(frag);
        return prog;
    }

    private int compileShader(int type, String src, String label) {
        int id = glCreateShader(type);
        glShaderSource(id, src);
        glCompileShader(id);
        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Shader compile error (" + label + "):\n" + glGetShaderInfoLog(id));
        }
        return id;
    }

    private void setupQuadGeometry() {
        // Unit quad (triangle strip), counter-clockwise winding:
        //   TL (0,0) → TR (1,0) → BL (0,1) → BR (1,1)
        // texcoord Y is NOT flipped here: Java BufferedImage and OpenGL tex
        // both have (0,0) at top-left when we upload with glTexImage2D as-is
        // (OpenGL default is bottom-left, but our upload order matches screen-Y-down).
        float[] quad = {
            0, 0,  0, 0,   // top-left
            1, 0,  1, 0,   // top-right
            0, 1,  0, 1,   // bottom-left
            1, 1,  1, 1,   // bottom-right
        };

        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, quad, GL_STATIC_DRAW);

        int stride = 4 * Float.BYTES;
        glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 2L * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
    }

    // ===== GLFW callbacks =====

    private void setupCallbacks() {
        glfwSetCursorPosCallback(glfwWindow, (win, x, y) -> {
            mouseX = x;
            mouseY = y;
            EventMouse ev = EventLwjglAdapter.mouseMove(x, y, heldButtons);
            screen.handleEvent(ev);
        });

        glfwSetMouseButtonCallback(glfwWindow, (win, button, action, mods) -> {
            EventMouse ev;
            if (action == GLFW_PRESS) {
                ev = EventLwjglAdapter.mouseDown(button, mouseX, mouseY, mods);
                if (button == GLFW_MOUSE_BUTTON_LEFT)   heldButtons |= EventMouse.MB_LEFT;
                if (button == GLFW_MOUSE_BUTTON_RIGHT)  heldButtons |= EventMouse.MB_RIGHT;
                if (button == GLFW_MOUSE_BUTTON_MIDDLE) heldButtons |= EventMouse.MB_MIDDLE;
            } else {
                ev = EventLwjglAdapter.mouseUp(button, mouseX, mouseY, mods);
                if (button == GLFW_MOUSE_BUTTON_LEFT)   heldButtons &= ~EventMouse.MB_LEFT;
                if (button == GLFW_MOUSE_BUTTON_RIGHT)  heldButtons &= ~EventMouse.MB_RIGHT;
                if (button == GLFW_MOUSE_BUTTON_MIDDLE) heldButtons &= ~EventMouse.MB_MIDDLE;
            }
            if (ev != null) screen.handleEvent(ev);
        });

        glfwSetKeyCallback(glfwWindow, (win, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                EventKeyboard ev = EventLwjglAdapter.keyDown(key, '\0', mods);
                if (ev == null) return;
                if (hotKeyHandler != null && hotKeyHandler.test(ev.keyCode)) return;
                screen.handleEvent(ev);
            } else if (action == GLFW_RELEASE) {
                EventKeyboard ev = EventLwjglAdapter.keyUp(key, mods);
                if (ev != null) screen.handleEvent(ev);
            }
        });

        // charCallback fires for printable text input (after keyboard layout).
        // This supplements keyCallback for character-input widgets (EditLine).
        glfwSetCharCallback(glfwWindow, (win, codepoint) -> {
            char ch = (char) codepoint;
            int vk = Character.toUpperCase(ch);
            EventKeyboard ev = new EventKeyboard(EventKeyboard.EV_KEY_DOWN, vk, ch, 0);
            // Do NOT pass through hotKeyHandler here — the keyCallback already did.
            screen.handleEvent(ev);
        });

        glfwSetWindowCloseCallback(glfwWindow, win -> quit());
    }

    // ===== Teardown =====

    private void cleanup() {
        textures.values().forEach(id -> glDeleteTextures(id));
        textures.clear();

        glDeleteProgram(shaderProgram);
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);

        glfwDestroyWindow(glfwWindow);
        glfwTerminate();
        System.exit(0);
    }
}
