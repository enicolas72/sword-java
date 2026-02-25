# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

S.W.O.R.D (System of Windows for the ORganisation of the Desktop) v2.10 is a C++ object-oriented GUI framework from 1996. It provides a cross-platform interface for building desktop applications with a NeXT-style GUI across DOS, Windows, and Unix platforms (Linux, Solaris, SunOS).

The original C++ source is in `210_original/`.

---

## Java Port Project

### Objective

Create a Java port of the S.W.O.R.D C++ framework that preserves the original architecture and design patterns. The port targets modern Java (21) while maintaining the framework's core concepts.

### Technology Stack

- **Java:** 21 (LTS)
- **Build System:** Maven
- **Windowing / Input:** LWJGL 3 + GLFW (OpenGL 3.3 Core Profile)
- **Rendering:** Java2D (`Graphics2D` / `BufferedImage`) for off-screen per-window painting; OpenGL compositor blits results to screen as textured quads
- **Architecture:** GLFW window; each S.W.O.R.D Window renders to its own `BufferedImage`, composited in z-order via OpenGL each frame

#### macOS requirements

GLFW must run on the AppKit main thread (`-XstartOnFirstThread`) and Java2D must not initialise its native toolkit (`-Djava.awt.headless=true`) to avoid AWT intercepting GLFW's AppKit events. Both flags are pre-configured in the Maven exec plugin.

### Package Structure

```
net.eric_nicolas.sword.ui              → Point, Rect
net.eric_nicolas.sword.ui.events       → Event, EventMouse, EventKeyboard, EventCommand
net.eric_nicolas.sword.ui.driver       → LwjglDriver, EventLwjglAdapter  (all LWJGL/GLFW coupling here)
net.eric_nicolas.sword.ui.base         → ScreenArea, Widget, Window, Canvas,
                                         Screen, TColors, PaintContext, Application
net.eric_nicolas.sword.ui.widgets      → Button, CheckBox, RadioBox, GroupBox,
                                         EditLine, Label, Menu, MenuChoice,
                                         Dialog, StandardButtons, AbstractButton,
                                         ItemBox, Scrollbar, Scroller
net.eric_nicolas.sword.samples         → Hello, Dialog, Mandel (sample applications)
```

### Translation Guidelines

**Stay close to C++ patterns - do not force Java idioms:**

1. **Object Hierarchy:**
   - TAtom sibling tree is removed; children are managed in `LinkedList` inside `Canvas` and `Screen`
   - Single `father` reference (in `ScreenArea`) is retained for coordinate translation and event dispatch within the window hierarchy
   - `Screen` is NOT in the `father` chain — Windows hold a direct `screen` reference (see below)
   - No Collections framework for parent traversal — walk `father()` manually

2. **Event Handling:**
   - C++ macro event tables replaced by virtual method overrides in `ScreenArea` subclasses
   - Override `mouseLDown`, `mouseLUp`, `mouseMove`, `keyDown`, `keyUp`, `command` as needed
   - `Application` subclasses override `handleCommand(int)` (not `command`) and call `super.handleCommand(commandId)`
   - Event constants use EV_ prefix (EV_MOUSE_LDOWN, EV_KEY_DOWN, EV_COMMAND, etc.)
   - Command IDs use CM_ prefix (CM_QUIT, CM_OK, CM_CANCEL, etc.)

3. **Status/Options Flags:**
   - Bitmask flags live in `ScreenArea`: `SF_VISIBLE`, `SF_SELECTED`, `SF_MOUSE_IN`, `SF_DOWN`, `SF_FOCUSED`, `SF_MODIFIED`
   - Use `setStatus`/`clearStatus`/`hasStatus`
   - Widget-specific boolean state uses plain fields instead of flags: `Widget.enabled`, `Menu.mainMenu`, `MenuChoice.separator`, `Window.resizable`, `Window.closable`

4. **Naming:**
   - Core classes: `ScreenArea`, `Application`, `TColors` (TColors keeps its T prefix as a pure constants class)
   - Gadget classes use plain names: `Button`, `Menu`, `Dialog`, `EditLine`, `Scrollbar`, etc.
   - Java conventions: camelCase for methods/fields, UPPER_CASE for constants

5. **Driver Integration:**
   - All LWJGL/GLFW code lives in `ui.driver`; the framework core has zero LWJGL imports (AWT imports allowed only in `ui.driver` and `Window.renderToBuffer`)
   - `LwjglDriver` owns the GLFW window and OpenGL compositor; runs the main event+render loop
   - `EventLwjglAdapter` converts GLFW callbacks → SWORD `EventMouse`/`EventKeyboard`
   - `Application` wires things up via lambdas: `screen.setCommandHandler(this::handleCommand)` and a hotkey predicate passed to `LwjglDriver`
   - `PaintContext` wraps `Graphics2D`; all drawing goes through it (off-screen, into `Window.renderBuffer`)
   - `LwjglDriver.forceRepaint()` is a no-op; the continuous render loop redraws every frame
   - Application-level commands are **queued** in `Screen.pendingCommands` (filled inside GLFW callbacks) and drained by `Screen.processPendingCommands()` in the main loop — this allows `Dialog.execDialog()` to safely pump GLFW events in a nested loop without violating GLFW's no-reentrant-poll rule

### Maven Build Commands

```bash
mvn clean compile          # Compile all sources
mvn test                   # Run tests
mvn package                # Create JAR
```

### Running the Samples

Preferred (Maven handles classpath and JVM flags automatically):

```bash
mvn exec:java -Dexec.mainClass=net.eric_nicolas.sword.samples.Hello
mvn exec:java -Dexec.mainClass=net.eric_nicolas.sword.samples.Dialog
mvn exec:java -Dexec.mainClass=net.eric_nicolas.sword.samples.Mandel
```

On macOS, direct `java` invocations require two extra flags:

```bash
java -XstartOnFirstThread -Djava.awt.headless=true \
     -cp target/classes:<lwjgl-jars> net.eric_nicolas.sword.samples.Hello
```

### Current Status

**Phase 2 complete.** Core infrastructure, all main gadgets, scrolling, and modal dialogs are working.

✅ Overlapping windows with drag, resize, close, and z-ordering
✅ Window options: `resizable` (thick border + resize handles vs. 1-px outline) and `closable` (shows/hides close button)
✅ Event system (mouse, keyboard, commands) routed through object hierarchy
✅ Gadgets: Button, CheckBox, RadioBox, GroupBox, EditLine, Label, Menu, MenuChoice, Dialog
✅ Modal dialog event loop (`execDialog()` — GLFW-based mini-loop via `Screen.frameStep`)
✅ Scrollbar (TLift port): arrow buttons, thumb drag, page click, H/V orientations
✅ Scroller (TScroller port): viewport-sized buffer, zoom-aware content/scrollbar sync; `resize()` for live viewport resize
✅ Sample applications: Hello, Dialog (with working modal dialog), Mandel (Mandelbrot fractal viewer with zoom + pan; resize reveals more of the plane)
✅ ~54 unit tests

**Deferred:**
- TGauge (progress bar)
- COMMON / DRIVERS subsystems (file system, error handling)
- IMAGE / MATH toolboxes

### Key Adaptations

**C++ → Java Mappings:**

| C++ | Java |
|-----|------|
| `TAtom` sibling tree (`_Next`, `_Son`…) | `LinkedList<Widget>` in `Canvas`/`Screen` |
| `_Father` pointer | `ScreenArea.father` reference (widget hierarchy only; not Screen) |
| `TObject` + `TZone` (two-layer split) | Single `ScreenArea` class |
| `TDesktop` | `Screen` (plain class, not a `ScreenArea` subclass) |
| `TShell` (trivial TObject subclass) | Removed; `Application` is a plain class |
| `void*` | `Object` or specific types |
| Manual memory management | Garbage collection |
| Header/implementation split | Single `.java` file per class |
| Macro event tables | Virtual method overrides in `ScreenArea` |
| `sfDisabled` status flag | `Widget.enabled` boolean |
| `opMainMenu` / `opSeparator` option flags | `Menu.mainMenu` / `MenuChoice.separator` booleans |
| Button `BO_DISABLED`, `BO_NO_CASE` options | Removed; use `setEnabled(false)` after construction |
| libgrx20 graphics calls | Java2D `Graphics2D` off-screen + LWJGL/OpenGL compositor (isolated in `ui.driver`) |
| `TLift` | `Scrollbar` |
| `TScroller` | `Scroller` |

**Graphics / Driver Layer:**
- `LwjglDriver` owns the GLFW window and OpenGL 3.3 compositor; renders each S.W.O.R.D `Window` as a textured quad (per-window `BufferedImage` → OpenGL texture, uploaded every frame)
- `Screen` is a plain class (not a `ScreenArea`) that holds `LinkedList<Window>`; each `Window` owns a `Canvas` holding `Widget` children
- Windows hold a direct `Window.screen` reference (set by `Screen.add()`); `Window.getScreen()` is used for window management and command dispatch
- The `ScreenArea.father` chain terminates at the top-level `Window` (father = null); Screen is never in the father chain
- Mouse/keyboard events wrapped by `EventLwjglAdapter` (in `ui.driver`) and dispatched to `Screen.handleEvent()` from GLFW callbacks
- Command dispatch from widgets: `sendCommand()` walks the `father` chain to the nearest `Window`, then calls `window.getScreen().handleEvent(cmd)`. Commands handled by a `Window` (e.g., `CM_OK` by `Dialog`) are consumed immediately within the GLFW callback. Commands unhandled by any window are **queued** in `Screen.pendingCommands` and dispatched to `Application.handleCommand` by `processPendingCommands()` between frames.
- Global menu hotkeys are intercepted by `Application`'s hotkey predicate before desktop dispatch; matched via `Menu.processHotKey(keyCode)` which calls `MenuChoice.sendCommand()`

**Window chrome:**
- Left sidebar (SIDEBAR_W=16 px): grip lines at top, optional close button (×) below
- Outer resize border (BORDER=5 px, resizable windows) or 1-px outline (non-resizable); `eb()` helper returns the effective border width
- Corner zones (CORNER=12 px from each corner) marked by black tick lines; drag to resize in two axes
- `setResizable(false)` → no thick border, no resize hit-testing (used by Dialog)
- `setClosable(false)` → no close button drawn, sidebar click does nothing (used by Menu)
- `drawOverlay()` redraws the sidebar separator + content border AFTER canvas children, so widget background fills never hide them
- `setOnResize(Runnable)` callback fired only during actual resize (not drag); used by samples to reflow content

**Scrollbar / Scroller design:**
- `Scrollbar` renders its own arrow buttons and thumb; handles drag capture (returns true from `mouseMove`/`mouseLUp` while dragging, even outside bounds)
- `Scroller` is a `Canvas`; its content widget renders into a viewport-sized `BufferedImage`; the virtual content size (for scrollbar ranges) is independent of the buffer
- `Scroller.resize(newViewW, newViewH)` resizes viewport, content widget, and scrollbar dimensions/ranges in one call
- Zoom-aware wiring: `scroller.setOnScroll(→ widget.setOffset)` + `widget.setOnZoomChange(→ scroller.setContentSize/setScrollPosition)`

**Mandel virtual world:**
- `MandelWidget` stores `baseW`, `baseH` fixed at construction; `virtualW() = baseW * zoom` (not `bounds.width() * zoom`)
- Resizing the viewport reveals more of the complex plane instead of stretching the existing view
- Complex-plane units per pixel depend only on zoom level, not viewport size

---

## C++ Original - Build Commands

All build commands must be run from within the `210_original/` directory or its subdirectories.

### Building Libraries
```bash
cd "210_original/SRC/<subsystem>/"
make depend      # Generate dependency files
make clean       # Remove intermediate files
make all         # Full rebuild (clean + depend + build)
```

### Building Sample Programs
```bash
cd "210_original/SAMPLES/<sample_name>/"
make
make exe         # DOS only: strip and convert to .EXE
```

### Platform Selection

Build system uses platform-specific definition files included by makefiles:
- `DJGPP_V1.DEF` - GNU C++ v1 (DJGPP v1.12)
- `DJGPP_V2.DEF` - GNU C++ v2
- `LINUX.DEF` - Linux
- `SOLARIS.DEF` - Solaris Unix
- `SUNOS.DEF` - SunOS
- `TURBOC.DEF` - Turbo C++ (DOS)

### Environment Variables

Required for runtime:
```bash
SWORDPATH=/path/to/sword    # Framework installation path
GRXFONT=/path/to/FONTS      # Font directory
GRX20DRV=STDVGA             # Graphics driver (DJGPP v2)
GO32=path_to_driver         # Graphics driver (DJGPP v1)
```

## C++ Architecture

The framework uses a **layered component model**:

```
Application Layer (TApp, TDialog, TEdition)
    ↓
Gadgets Layer (Buttons, Menus, Controls, Scrollbars)
    ↓
Graphics Layer (Windows, Zones, Drawing)
    ↓
Mechanism Layer (Objects, Events, Atoms)
    ↓
Drivers Layer (Disk, Dir, Time, Hardware)
    ↓
Common Layer (Utilities, Error Handling)
    ↓
Graphics Backend (libgrx/libgrx20)
```

### C++ Subsystems (in SRC/)

**MECANISM:** TAtom (linked tree), TObject (events), TEvent, TPoint/TRect, TKeyboard, TClipBoard

**GRAPHICS:** TWindow, TZone, TDesktop, TFont, TColors, TDrawings, libgrx20 backend

**GADGETS:** TButton, TMenu, TDialog, TEdition, TScroller, TLift/TIntLift, TGauge, TStdWin, TStatText

**TOOLS:** TApp (main event loop), TMsgBox, TCmdWin

**TOOLBOX/IMAGE:** Load/display images (BMP, GIF, PPM, TARGA)

**TOOLBOX/MATH:** Complex numbers, matrices, FFT, polynomial math

**COMMON:** Path utilities, error handling, debug facilities

**DRIVERS:** File system, disk, time/date
