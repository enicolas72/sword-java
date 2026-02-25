# S.W.O.R.D - Java Port

Java port of the **S.W.O.R.D** (System of Windows for the ORganisation of the Desktop) C++ GUI framework (v2.10, 1996). Preserves the original architecture and design patterns while targeting modern Java 21.

---

## Current Status

**Phase 2 complete.** Core infrastructure, all gadgets, scrolling, modal dialogs, and a Mandelbrot viewer are working.

### What's implemented

- Overlapping windows with drag, resize, close, and z-ordering
- Custom widget painting (ScreenArea-based drawing areas)
- Event system (mouse, keyboard, commands) routed through object hierarchy
- Gadgets: Button, CheckBox, RadioBox, GroupBox, EditLine, Label, Menu, MenuChoice, Dialog, Scrollbar, Scroller
- **Window palette system** — three colour schemes: STANDARD (gray), GREEN (Menu/MenuChoice), BLUE (Dialog); propagates automatically via `PaintContext`
- **Modal dialog event loop** (`execDialog()` — GLFW-based mini-loop, fully functional)
- Sample applications: Hello (custom windows), Dialog (full gadget demo), Mandel (Mandelbrot viewer with zoom + scrollbars)
- 54 unit tests covering geometry, object state, and gadget behavior

### What's deferred

- TGauge (progress bar)
- COMMON / DRIVERS subsystems (file system, error handling)
- IMAGE / MATH toolboxes

---

## Project Structure

```
src/main/java/net/eric_nicolas/sword/
├── ui/               - Geometry (Point, Rect)
│   ├── events/       - Event, EventMouse, EventKeyboard, EventCommand
│   ├── driver/       - LwjglDriver, EventLwjglAdapter  (all LWJGL/GLFW coupling here)
│   ├── base/         - Core layer: ScreenArea, Widget, Canvas, Window, Screen,
│   │                   TColors, PaintContext, Application
│   └── widgets/      - UI components: Button, CheckBox, RadioBox, GroupBox,
│                       EditLine, Label, Menu, MenuChoice, Dialog, StandardButtons,
│                       AbstractButton, ItemBox, Scrollbar, Scroller
└── samples/          - Example applications (Hello, Dialog, Mandel)
```

The original C++ source is in `210_original/`.

---

## Building and Running

### Requirements

- Java 21+
- Maven 3.6+
- LWJGL 3 (declared in pom.xml; downloaded automatically by Maven)

### With Maven

```bash
mvn clean compile     # compile
mvn test              # run tests
mvn package           # build JAR
mvn exec:java         # run Hello sample (pom.xml default)
```

The exec plugin is pre-configured with the required JVM flags:
- `-XstartOnFirstThread` — macOS requires GLFW on the main thread
- `-Djava.awt.headless=true` — prevents AWT from conflicting with GLFW's AppKit event loop

### Run a specific sample with Maven

```bash
mvn exec:java -Dexec.mainClass=net.eric_nicolas.sword.samples.Hello
mvn exec:java -Dexec.mainClass=net.eric_nicolas.sword.samples.Dialog
mvn exec:java -Dexec.mainClass=net.eric_nicolas.sword.samples.Mandel
```

### Run directly with java (macOS)

```bash
java -XstartOnFirstThread -Djava.awt.headless=true \
     -cp target/classes:$(ls ~/.m2/repository/org/lwjgl/**/*.jar | tr '\n' ':') \
     net.eric_nicolas.sword.samples.Hello
```

---

## Architecture Overview

The port preserves the original layered model:

```
samples/          (Hello, Dialog, Mandel)
    ↓
ui.base/          (Application – application shell, menu, Screen)
    ↓
ui.widgets/       (Button, EditLine, Menu, Dialog, Scrollbar…)
    ↓
ui.base/          (Window, Canvas)
    ↓
ui.base/          (ScreenArea – flags, parent ref, event dispatch, drawing)
    ↓
ui/ + ui.events/  (Point, Rect, Event hierarchy)
    ↓
ui.driver/        (LwjglDriver, EventLwjglAdapter – native isolation layer)
    ↓
LWJGL 3 / GLFW / OpenGL 3.3 + Java2D off-screen (replaces libgrx20)
```

**Rendering**: each `Window` renders its visual tree into its own `BufferedImage` via Java2D. `LwjglDriver` uploads these as OpenGL textures and composites them in z-order each frame using a textured-quad shader.

**Events**: GLFW callbacks translate raw input to S.W.O.R.D events via `EventLwjglAdapter`, then dispatch through `Screen.handleEvent()`. Application-level commands are deferred to `Screen.processPendingCommands()` (called between frames, outside GLFW callbacks) so that handlers such as `execDialog()` can safely pump the GLFW event loop.

Note: `Screen` is a standalone class (not a `ScreenArea` subclass). It sits alongside the hierarchy and holds `Window` instances; windows reference their Screen directly via `Window.getScreen()`.

Key adaptations from C++:
- TAtom linked sibling tree removed; children managed in `LinkedList` in Canvas/Screen
- `TObject` and `TZone` merged into a single `ScreenArea` class (mechanism + graphics in one)
- `TDesktop` → `Screen` (plain class, not in the ScreenArea hierarchy); `TApp` → `Application` (plain class)
- All native coupling isolated in `ui.driver`; framework core has zero AWT/LWJGL imports
- Macro event tables replaced by virtual method overrides in `ScreenArea` subclasses
- `father` parent reference retained within the window hierarchy for coordinate translation; Screen sits outside this chain
- C++ bitmask option/status flags replaced by plain booleans where it simplifies the API (`Widget.enabled`, `Menu.mainMenu`, `MenuChoice.separator`)

See [IMPLEMENTATION.md](IMPLEMENTATION.md) for detailed notes on all classes, design decisions, and known limitations.

---

## Documentation

- `CLAUDE.md` - Architecture documentation and development guidelines
- `IMPLEMENTATION.md` - Class inventory, architecture notes, test coverage
- `210_original/DOCS/` - Original C++ manual and reference
