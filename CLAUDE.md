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
- **Graphics:** Pure AWT (Graphics2D, Canvas) - no Swing
- **Architecture:** Single AWT window with manually drawn overlapping windows (preserving original C++ approach)

### Package Structure

```
net.eric_nicolas.sword.ui              → Point, Rect
net.eric_nicolas.sword.ui.events       → Event, EventMouse, EventKeyboard,
                                         EventCommand, EventAwtAdapter
net.eric_nicolas.sword.ui.base         → TObject, TZone, Widget, Window, Canvas,
                                         Desktop, TColors, PaintContext, TApp
net.eric_nicolas.sword.ui.widgets      → Button, CheckBox, RadioBox, GroupBox,
                                         EditLine, Label, Menu, MenuChoice,
                                         Dialog, StandardButtons, AbstractButton,
                                         ItemBox, Scrollbar, Scroller
net.eric_nicolas.sword.samples         → Hello, Dialog, Mandel (sample applications)
```

### Translation Guidelines

**Stay close to C++ patterns - do not force Java idioms:**

1. **Object Hierarchy:**
   - TAtom sibling tree is removed; children are managed in `LinkedList` inside `Canvas` and `Desktop`
   - Single `father` reference (in `TObject`) is retained for command routing up the hierarchy
   - No Collections framework for parent traversal — walk `father()` manually

2. **Event Handling:**
   - C++ macro event tables replaced by virtual method overrides in `TObject` subclasses
   - Override `mouseLDown`, `mouseLUp`, `mouseMove`, `keyDown`, `keyUp`, `command` as needed
   - Event constants use EV_ prefix (EV_MOUSE_LDOWN, EV_KEY_DOWN, EV_COMMAND, etc.)
   - Command IDs use CM_ prefix (CM_QUIT, CM_OK, CM_CANCEL, etc.)

3. **Status/Options Flags:**
   - Remaining bitmask flags live in `TObject`: `SF_VISIBLE`, `SF_SELECTED`, `SF_MOUSE_IN`, `SF_DOWN`, `SF_FOCUSED`, `SF_MODIFIED`
   - Use `setStatus`/`clearStatus`/`hasStatus`
   - Widget-specific boolean state uses plain fields instead of flags: `Widget.enabled`, `Menu.mainMenu`, `MenuChoice.separator`

4. **Naming:**
   - Core classes keep the T prefix: `TObject`, `TZone`, `TApp`, `TColors`
   - Gadget classes use plain names: `Button`, `Menu`, `Dialog`, `EditLine`, `Scrollbar`, etc.
   - Java conventions: camelCase for methods/fields, UPPER_CASE for constants

5. **AWT Integration:**
   - AWT events (MouseEvent, KeyEvent) are wrapped into `EventMouse`/`EventKeyboard` by `EventAwtAdapter`
   - The AWT `Canvas` in `TApp` receives all AWT input and dispatches to `Desktop.handleEvent()`
   - `PaintContext` wraps `Graphics2D`; all drawing goes through it
   - `TApp.forceRepaint()` redraws the full desktop to a back buffer then blits to screen

### Maven Build Commands

```bash
mvn clean compile          # Compile all sources
mvn test                   # Run tests
mvn package                # Create JAR
```

### Running the Samples

```bash
java -cp target/classes net.eric_nicolas.sword.samples.Hello
java -cp target/classes net.eric_nicolas.sword.samples.Dialog
java -cp target/classes net.eric_nicolas.sword.samples.Mandel
```

### Current Status

**Phase 2 complete.** Core infrastructure, all main gadgets, and scrolling are working.

✅ Overlapping windows with drag and z-ordering
✅ Event system (mouse, keyboard, commands) routed through object hierarchy
✅ Gadgets: Button, CheckBox, RadioBox, GroupBox, EditLine, Label, Menu, MenuChoice, Dialog
✅ Scrollbar (TLift port): arrow buttons, thumb drag, page click, H/V orientations
✅ Scroller (TScroller port): viewport-sized buffer, zoom-aware content/scrollbar sync
✅ Sample applications: Hello, Dialog, Mandel (Mandelbrot fractal viewer with zoom + pan)
✅ ~57 unit tests

**Deferred:**
- Window resize handles / close button
- TGauge (progress bar)
- Modal dialog event loop
- COMMON / DRIVERS subsystems (file system, error handling)
- IMAGE / MATH toolboxes

### Key Adaptations

**C++ → Java Mappings:**

| C++ | Java |
|-----|------|
| `TAtom` sibling tree (`_Next`, `_Son`…) | `LinkedList<Widget>` in `Canvas`/`Desktop` |
| `_Father` pointer | `TObject.father` reference |
| `TShell` (trivial TObject subclass) | Removed; `TApp` extends `TObject` directly |
| `void*` | `Object` or specific types |
| Manual memory management | Garbage collection |
| Header/implementation split | Single `.java` file per class |
| Macro event tables | Virtual method overrides in `TObject` |
| `sfDisabled` status flag | `Widget.enabled` boolean |
| `opMainMenu` / `opSeparator` option flags | `Menu.mainMenu` / `MenuChoice.separator` booleans |
| Button `BO_DISABLED`, `BO_NO_CASE` options | Removed; use `setEnabled(false)` after construction |
| libgrx20 graphics calls | AWT `Graphics2D` via `PaintContext` |
| `TLift` | `Scrollbar` |
| `TScroller` | `Scroller` |

**Graphics Layer:**
- Single AWT `Canvas` in `TApp` covers the full window
- `Desktop` holds all top-level `Window` objects in a `LinkedList`; each `Window` owns a `Canvas` holding `Widget` children
- Double-buffered rendering: `TApp` draws to a `BufferedImage`, then blits to screen
- Mouse/keyboard events wrapped by `EventAwtAdapter` and dispatched through the object hierarchy

**Scrollbar / Scroller design:**
- `Scrollbar` renders its own arrow buttons and thumb; handles drag capture (returns true from `mouseMove`/`mouseLUp` while dragging, even outside bounds)
- `Scroller` is a `Canvas`; its content widget renders into a viewport-sized `BufferedImage`; the virtual content size (for scrollbar ranges) is independent of the buffer
- Zoom-aware wiring: `scroller.setOnScroll(→ widget.setOffset)` + `widget.setOnZoomChange(→ scroller.setContentSize/setScrollPosition)`

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
