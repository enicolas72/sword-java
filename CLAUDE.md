# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

S.W.O.R.D (System of Windows for the ORganisation of the Desktop) v2.10 is a C++ object-oriented GUI framework from 1996. It provides a cross-platform interface for building desktop applications with a NeXT-style GUI across DOS, Windows, and Unix platforms (Linux, Solaris, SunOS).

**Working directory:** The main code is in `210_original/` subdirectory, not the repository root.

---

## Java Port Project

### Objective

Create a Java port of the S.W.O.R.D C++ framework that preserves the original architecture and design patterns. The port targets modern Java (21) while maintaining the framework's core concepts.

### Technology Stack

- **Java:** 21 (LTS)
- **Build System:** Maven
- **Graphics:** Pure AWT (Graphics2D, Canvas) - no Swing
- **Architecture:** Single window with manually drawn overlapping windows (preserving original C++ approach)

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
                                         ItemBox
net.eric_nicolas.sword.samples         → Hello, Dialog (sample applications)
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
   - Use `setStatus`/`clearStatus`/`hasStatus` and `setOption`/`clearOption`/`hasOption`
   - Widget-specific boolean state uses plain fields instead of flags: `Widget.enabled`, `Menu.mainMenu`, `MenuChoice.separator`

4. **Naming:**
   - Core classes keep the T prefix: `TObject`, `TZone`, `TApp`, `TColors`
   - Gadget classes use plain names: `Button`, `Menu`, `Dialog`, `EditLine`, etc.
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
```

### Current Status

**Phase 2 largely complete.** Core infrastructure and all main gadgets are working.

✅ Overlapping windows with drag and z-ordering
✅ Event system (mouse, keyboard, commands) routed through object hierarchy
✅ Gadgets: Button, CheckBox, RadioBox, GroupBox, EditLine, Label, Menu, MenuChoice, Dialog
✅ Sample applications: Hello, Dialog, Mandel (Mandelbrot fractal viewer)
✅ ~58 unit tests

**Deferred:**
- Window resize / close button
- Scrollbars (TScroller, TLift, TGauge)
- Modal dialog event loop
- COMMON / DRIVERS subsystems (file system, error handling)
- IMAGE / MATH toolboxes
- IMAGE viewer sample
- Complex samples (COLORS)

### Key Adaptations

**C++ → Java Mappings:**

| C++ | Java |
|-----|------|
| `TAtom` sibling tree (`_Next`, `_Son`…) | `LinkedList<Widget>` in `Canvas`/`Desktop` |
| `_Father` pointer | `TObject.father` reference |
| `void*` | `Object` or specific types |
| Manual memory management | Garbage collection |
| Header/implementation split | Single `.java` file per class |
| Macro event tables | Virtual method overrides in `TObject` |
| `sfDisabled` status flag | `Widget.enabled` boolean |
| `opMainMenu` / `opSeparator` option flags | `Menu.mainMenu` / `MenuChoice.separator` booleans |
| libgrx20 graphics calls | AWT `Graphics2D` via `PaintContext` |
| Multiple inheritance | Interfaces where needed |

**Graphics Layer:**
- Single AWT `Canvas` in `TApp` covers the full window
- `Desktop` holds all top-level `Window` objects in a `LinkedList`; each `Window` owns a `Canvas` holding `Widget` children
- Double-buffered rendering: `TApp` draws to a `BufferedImage`, then blits to screen
- Mouse/keyboard events wrapped by `EventAwtAdapter` and dispatched through the object hierarchy

---

## C++ Original - Build Commands

All build commands must be run from within the `210 original/` directory or its subdirectories.

### Building Libraries
```bash
cd "210 original/SRC/<subsystem>/"
make depend      # Generate dependency files
make clean       # Remove intermediate files
make all         # Full rebuild (clean + depend + build)
```

### Building Sample Programs
```bash
cd "210 original/SAMPLES/<sample_name>/"
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

Each subsystem's makefile includes the appropriate `.DEF` file to set compiler, flags, and library paths.

### Environment Variables

Required for runtime:
```bash
SWORDPATH=/path/to/sword    # Framework installation path
GRXFONT=/path/to/FONTS      # Font directory
GRX20DRV=STDVGA             # Graphics driver (DJGPP v2)
GO32=path_to_driver         # Graphics driver (DJGPP v1)
```

## Architecture

The framework uses a **layered component model**:

```
Application Layer (TApp, TDialog, TEdition)
    ↓
Gadgets Layer (Buttons, Menus, Controls)
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

### Core Architectural Concepts

1. **Linked Tree Structure:** All objects derive from `TAtom` which provides:
   - `_Next`, `_Previous` - Sibling navigation
   - `_Son` - First child
   - `_Father` - Parent reference
   - Objects form hierarchical parent-child trees

2. **Event-Driven Design:** Objects derive from `TObject` which adds:
   - Event dispatching via macro-based event tables
   - Command routing and handling
   - Status and options flags for state management

3. **Overlapping Window Management:** Efficient rendering with clipping regions for overlapped windows

## Directory Structure

```
210 original/
├── SRC/           # Source code organized by subsystem
├── INCLUDE/       # Public API headers (mirror SRC structure)
├── LIB/           # Pre-compiled libraries per platform
├── SAMPLES/       # Example programs
├── TOOLS/         # Utility programs
├── DOCS/          # Documentation (MANUAL.DOC, MANUAL.PS)
├── FONTS/         # Font files for rendering
└── DATAS/         # Color palette files
```

## Subsystems (in SRC/)

### COMMON
Core utilities, path manipulation, error handling, debug facilities
- Files: BASIC.CC, DEBUG.CC, ERROR.CC
- Global variables: SwordPath, AppPath, Version

### MECANISM
Core object system and infrastructure
- **TAtom** - Base class with linked list/tree structure
- **TObject** - Application object with event handling
- **TEvent** - Event structure (mouse, keyboard, commands)
- **TKeyboard** - Keyboard input processing
- **TPoint/TRect** - Geometry primitives
- **TSetup** - Configuration management
- **TClipBoard** - Data exchange

### DRIVERS
Hardware abstraction: file system, disk access, time/date retrieval

### GRAPHICS
Screen rendering and window management
- **TWindow** - Overlapped windows with drag/resize/minimize/maximize
- **TZone** - Base drawing area with clipping
- **TDesktop** - Application background/desktop
- **TFont/TColors** - Font and color systems
- **TDrawings** - Primitive drawing operations
- Links to libgrx20 for low-level graphics

### GADGETS
Pre-built UI components:
- **TStdWin** - Standard window with title bar
- **TDialog** - Modal/modeless dialogs
- **TButton** - Clickable buttons
- **TMenu** - Hierarchical menus with hotkeys
- **TEdition** - Text editing control
- **TScroller** - Scrollable container
- **TGauge** - Progress bar
- **TLift/TIntLift** - Scrollbars

### TOOLBOX
**IMAGE Module:** Load/display images (BMP, GIF, PPM, TARGA)
**MATH Module:** Complex numbers, matrices, vectors, FFT, polynomial math

### TOOLS
High-level application framework
- **TApp** - Main application with event loop (Run() method)
- **TMsgBox** - Message boxes
- **TCmdWin** - Command windows

## Key Patterns

### Event Tables

Use macro-based event table declarations (similar to MFC):

```cpp
DEFINE_EVENTS_TABLE(MyClass, ParentClass)
  COMMAND(cmHelloWindow, OnHelloClick)
  MOUSELDOWN()
  KEYDOWN()
END_EVENTS_TABLE
```

### Options and Status Flags

Objects use bitmask flags:
- **Options:** opDrawable, opSelectable, opWinSizeable, opWinCloseBox
- **Status:** sfMouseIn, sfSelected, sfDown, sfVisible, sfDisabled

### Data Exchange

Standard pattern for object state management:
```cpp
virtual void SetData(void *Ptr);   // Load state
virtual void GetData(void *Ptr);   // Retrieve state
virtual long DataSize(void);       // Query size
```

## Sample Programs

Located in `SAMPLES/` directory:
- **HELLO** - Basic window with custom drawing
- **DIALOG** - Dialog box and controls
- **IMAGE** - Image viewer
- **MANDEL** - Mandelbrot fractal generator
- **COLORS** - Color palette viewer

Each sample has its own makefile and demonstrates specific framework features.

## Documentation

- `DOCS/README.1ST` - Installation and quick start
- `DOCS/MANUAL.DOC` - Complete manual (Word 7 format)
- `DOCS/MANUAL.PS` - Manual (PostScript)
- `DOCS/COPYING.EN` - License terms

## Known Issues

From TODO.TXT:
- **MATH Toolbox:** Matrix/Vector incompatible with ANSI C++
- **IMAGE Toolbox:** Cannot save images; missing TIFF/RAS/PICT support
- **GADGETS:** Edition control Delete key doesn't remove selection
- **GRAPHICS:** XORed lines issue when dragging/resizing windows with GRX20

## Platform Requirements

- **CPU:** 386SX16 minimum (Turbo C++), 386DX20 (DJGPP)
- **Memory:** 1MB (Turbo C++), 4MB (DJGPP)
- **Graphics:** Requires libgrx (v1.03 or v2.0) and mouse
- **Display:** All graphics modes supported (640x480 minimum)

## Important Notes

- This is legacy C++ code from 1996 - not C++11/14/17 compliant
- Uses non-standard C++ patterns (far pointers, DOS-specific APIs)
- All paths use backslashes or are converted via FixPath() utility
- Graphics backend (libgrx) must be properly configured for the target platform
