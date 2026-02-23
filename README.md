# S.W.O.R.D - Java Port

Java port of the **S.W.O.R.D** (System of Windows for the ORganisation of the Desktop) C++ GUI framework (v2.10, 1996). Preserves the original architecture and design patterns while targeting modern Java 21 with pure AWT rendering.

---

## Current Status

**Phase 2 largely complete.** Core infrastructure plus gadgets (buttons, dialogs, menus, text editing) are working.

### What's implemented

- Overlapping windows with drag and z-ordering
- Custom widget painting (TZone-based drawing areas)
- Event system (mouse, keyboard, commands) routed through object hierarchy
- Gadgets: Button, CheckBox, RadioBox, GroupBox, EditLine, Label, Menu, MenuChoice, Dialog
- Sample applications: Hello (custom windows), Dialog (full gadget demo), Mandel (Mandelbrot viewer with click-to-zoom)
- ~58 unit tests covering geometry, object state, and gadget behavior

### What's deferred

- Window resize handles / close button
- Scrollbars (TScroller, TLift, TGauge)
- Modal dialog event loop
- COMMON / DRIVERS subsystems (file system, error handling)
- IMAGE / MATH toolboxes
- Complex samples (MANDEL, IMAGE viewer)

---

## Project Structure

```
src/main/java/net/eric_nicolas/sword/
├── ui/               - Geometry (Point, Rect)
│   ├── events/       - Event, EventMouse, EventKeyboard, EventCommand, EventAwtAdapter
│   ├── base/         - Core layer: TObject, TZone, Widget, Window, Canvas, Desktop,
│   │                   TColors, PaintContext, TApp
│   └── widgets/      - UI components: Button, CheckBox, RadioBox, GroupBox, EditLine,
│                       Label, Menu, MenuChoice, Dialog, StandardButtons
└── samples/          - Example applications (Hello, Dialog)
```

The original C++ source is in `210 original/`.

---

## Building and Running

### Requirements

- Java 21+
- Maven 3.6+ (optional)

### With Maven

```bash
cd java   # or from repo root

mvn clean compile     # compile
mvn test              # run tests
mvn package           # build JAR
```

### Run the Hello sample

```bash
cd java
mvn exec:java -Dexec.mainClass=net.eric_nicolas.sword.samples.Hello
# or
java -cp target/classes net.eric_nicolas.sword.samples.Hello
```

### Run the Dialog sample

```bash
cd java
java -cp target/classes net.eric_nicolas.sword.samples.Dialog
```

### Run the Mandel sample

```bash
cd java
java -cp target/classes net.eric_nicolas.sword.samples.Mandel
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
samples/          (Hello, Dialog)
    ↓
ui.base/          (TApp – AWT event loop, desktop, menu)
    ↓
ui.widgets/       (Button, EditLine, Menu, Dialog…)
    ↓
ui.base/          (Window, TZone, Canvas, Desktop)
    ↓
ui.base/          (TObject – flags, parent ref, event dispatch)
    ↓
ui/ + ui.events/  (Point, Rect, Event hierarchy)
    ↓
AWT Graphics2D    (replaces libgrx20)
```

Key adaptations from C++:
- TAtom linked sibling tree removed; children managed in `LinkedList` in Canvas/Desktop
- Macro event tables replaced by virtual method overrides in TObject subclasses
- libgrx20 replaced by AWT `Graphics2D` via `PaintContext` coordinate wrapper
- Single `_Father` parent reference retained for command routing up the hierarchy
- C++ bitmask option/status flags replaced by plain booleans where it simplifies the API (`Widget.enabled`, `Menu.mainMenu`, `MenuChoice.separator`)

See [IMPLEMENTATION.md](IMPLEMENTATION.md) for detailed notes on all classes, design decisions, and known limitations.

---

## Documentation

- `CLAUDE.md` - Architecture documentation and development guidelines
- `IMPLEMENTATION.md` - Class inventory, architecture notes, test coverage
- `210 original/DOCS/` - Original C++ manual and reference