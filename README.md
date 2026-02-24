# S.W.O.R.D - Java Port

Java port of the **S.W.O.R.D** (System of Windows for the ORganisation of the Desktop) C++ GUI framework (v2.10, 1996). Preserves the original architecture and design patterns while targeting modern Java 21 with pure AWT rendering.

---

## Current Status

**Phase 2 complete.** Core infrastructure, all gadgets, scrolling, and a Mandelbrot viewer are working.

### What's implemented

- Overlapping windows with drag, resize, close, and z-ordering
- Custom widget painting (ScreenArea-based drawing areas)
- Event system (mouse, keyboard, commands) routed through object hierarchy
- Gadgets: Button, CheckBox, RadioBox, GroupBox, EditLine, Label, Menu, MenuChoice, Dialog, Scrollbar, Scroller
- Sample applications: Hello (custom windows), Dialog (full gadget demo), Mandel (Mandelbrot viewer with zoom + scrollbars)
- 54 unit tests covering geometry, object state, and gadget behavior

### What's deferred

- TGauge (progress bar)
- Modal dialog event loop
- COMMON / DRIVERS subsystems (file system, error handling)
- IMAGE / MATH toolboxes

---

## Project Structure

```
src/main/java/net/eric_nicolas/sword/
├── ui/               - Geometry (Point, Rect)
│   ├── events/       - Event, EventMouse, EventKeyboard, EventCommand
│   ├── driver/       - AwtDriver, EventAwtAdapter  (all AWT coupling here)
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

### With Maven

```bash
mvn clean compile     # compile
mvn test              # run tests
mvn package           # build JAR
```

### Run the samples

```bash
java -cp target/classes net.eric_nicolas.sword.samples.Hello
java -cp target/classes net.eric_nicolas.sword.samples.Dialog
java -cp target/classes net.eric_nicolas.sword.samples.Mandel
```

Or with Maven:

```bash
mvn exec:java -Dexec.mainClass=net.eric_nicolas.sword.samples.Hello
```

### With javac directly

```bash
mkdir -p target/classes
javac -d target/classes --release 21 $(find src/main/java/net -name "*.java")
java -cp target/classes net.eric_nicolas.sword.samples.Hello
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
ui.driver/        (AwtDriver, EventAwtAdapter – AWT isolation layer)
    ↓
AWT Graphics2D    (replaces libgrx20)
```

Note: `Screen` is a standalone class (not a `ScreenArea` subclass). It sits alongside the hierarchy and holds `Window` instances; windows reference their Screen directly via `Window.getScreen()`.

Key adaptations from C++:
- TAtom linked sibling tree removed; children managed in `LinkedList` in Canvas/Screen
- `TObject` and `TZone` merged into a single `ScreenArea` class (mechanism + graphics in one)
- `TDesktop` → `Screen` (plain class, not in the ScreenArea hierarchy); `TApp` → `Application` (plain class)
- All AWT coupling isolated in `ui.driver`; framework core has zero AWT imports
- Macro event tables replaced by virtual method overrides in `ScreenArea` subclasses
- `father` parent reference retained within the window hierarchy for coordinate translation; Screen sits outside this chain
- C++ bitmask option/status flags replaced by plain booleans where it simplifies the API (`Widget.enabled`, `Menu.mainMenu`, `MenuChoice.separator`)

See [IMPLEMENTATION.md](IMPLEMENTATION.md) for detailed notes on all classes, design decisions, and known limitations.

---

## Documentation

- `CLAUDE.md` - Architecture documentation and development guidelines
- `IMPLEMENTATION.md` - Class inventory, architecture notes, test coverage
- `210_original/DOCS/` - Original C++ manual and reference
