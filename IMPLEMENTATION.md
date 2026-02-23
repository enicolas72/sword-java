# Java Port - Implementation Notes

## Current Status

Phase 1 (core infrastructure + Hello sample) is complete. Phase 2 (gadgets + Dialog sample) is largely complete.

---

## Implemented Classes

### UI Package (`net.eric_nicolas.sword.ui`)

- **`Point`** - Immutable 2D point with arithmetic operations (plus, minus, min, max)
- **`Rect`** - Rectangle (top-left + width/height) with intersect, union, contains

### Events Package (`net.eric_nicolas.sword.ui.events`)

- **`Event`** - Base event with type constant and timestamp
- **`EventMouse`** - Mouse event: position, button state, modifiers
- **`EventKeyboard`** - Keyboard event: key code, character, modifiers
- **`EventCommand`** - Command event for routing UI actions through object hierarchy
- **`EventAwtAdapter`** - Factory converting AWT events → S.W.O.R.D events

### Mechanism Package (`net.eric_nicolas.sword.mechanism`)

- **`TObject`** - Core application object: options/status bitmask flags, parent reference, virtual event handlers

### Graphics Package (`net.eric_nicolas.sword.graphics`)

- **`TColors`** - Static color palette (standard colors + UI theme colors)
- **`PaintContext`** - AWT Graphics2D wrapper with local coordinate translation
- **`TZone`** - Base drawing area: bounds, clipping, visibility, parent/child relationships
- **`Widget`** - Base class for gadget components (extends TZone)
- **`Canvas`** - Transparent container for Widgets, stored in `LinkedList<Widget>`
- **`Window`** - Overlapping window with draggable title bar, frame, and internal Canvas
- **`Desktop`** - Main desktop managing windows in `LinkedList<Window>` with z-order

### Gadgets Package (`net.eric_nicolas.sword.widgets`)

- **`Label`** - Non-interactive text label
- **`AbstractButton`** - Base for clickable buttons: 3D frame, pressed state, command routing
- **`Button`** - Standard button with centered text label
- **`ItemBox`** - Base for selection controls (no button frame)
- **`CheckBox`** - Checkbox with bitmask support for GroupBox data exchange
- **`RadioBox`** - Radio button for mutually exclusive selection within GroupBox
- **`GroupBox`** - Container for CheckBox/RadioBox with titled frame and value management
- **`EditLine`** - Single-line text input: cursor, keyboard navigation, click-to-position
- **`Menu`** - Menu bar/dropdown with horizontal or vertical layout
- **`MenuChoice`** - Menu item: text, hotkey, command, optional submenu, separator support
- **`Dialog`** - Modal/modeless dialog with result codes (OK, Cancel, Yes, No)
- **`StandardButtons`** - Factory providing standard button instances (OK, Cancel, Yes, No)

### Tools Package (`net.eric_nicolas.sword.tools`)

- **`TShell`** - Base shell object extending TObject
- **`TApp`** - Main application: AWT event loop, back buffer, desktop, menu management

### Samples Package (`net.eric_nicolas.sword.samples`)

- **`Hello`** - Custom THello widget in multiple overlapping draggable windows
- **`Dialog`** - Demonstrates Dialog, Button, CheckBox, RadioBox, EditLine

---

## Architecture Notes

### What Was Changed from the Original C++ Design

| Aspect | C++ Original | Java Port |
|--------|-------------|-----------|
| Naming | All classes T-prefixed | T-prefix on core mechanism classes; gadgets/geometry omit it |
| Tree structure | TAtom: `_Next/_Previous/_Son/_Father` sibling chain | Only `_Father` (parent ref) in TObject; children in `LinkedList` |
| Child storage | TAtom linked tree | `LinkedList<Widget>` in Canvas, `LinkedList<Window>` in Desktop |
| Event tables | C++ macros `DEFINE_EVENTS_TABLE` | Virtual method overrides in TObject subclasses |
| Graphics | libgrx20 calls | AWT `Graphics2D` via `PaintContext` wrapper |
| Data exchange | `SetData()/GetData()/DataSize()` | Not implemented (removed in recent refactor) |
| Packages | Flat subsystem names | Added `ui` and `ui.events` packages |

### Key Design Decisions

1. **No TAtom**: The linked sibling tree (TAtom) was removed. Children are stored in explicit `LinkedList` containers in Canvas and Desktop.
2. **Parent reference only**: TObject retains `_Father` for upward traversal (command routing), but no sibling navigation.
3. **Method override event dispatch**: Instead of macro event tables, event handling uses `switch` dispatch calling overridable methods (`mouseLDown()`, `keyDown()`, `command()`, etc.).
4. **PaintContext**: Coordinate translation is wrapped in PaintContext, passed down the paint chain, preserving the "draw in local coordinates" pattern.
5. **LinkedList for z-order**: Desktop uses LinkedList<Window> to support bring-to-front semantics without TAtom tree manipulation.

---

## What Works

- ✅ Object hierarchy (parent reference chain)
- ✅ Event dispatching (mouse, keyboard, commands)
- ✅ Window creation, management, z-ordering
- ✅ Window dragging via title bar
- ✅ Overlapping window rendering with clipping
- ✅ Custom zone/widget painting
- ✅ AWT event conversion
- ✅ Buttons (standard, with pressed 3D effect)
- ✅ CheckBox (with bitmask group support)
- ✅ RadioBox (mutually exclusive within GroupBox)
- ✅ GroupBox (container with titled frame)
- ✅ EditLine (text input with cursor and keyboard navigation)
- ✅ Menu (horizontal/vertical layouts with hotkeys)
- ✅ MenuChoice (items, separators, hotkeys)
- ✅ Dialog (modal/modeless, result codes)
- ✅ Label (text display)

## Known Limitations

- **No window resize**: Resize handles not implemented
- **No close/min/max buttons**: Title bar decorations only
- **No window focus styling**: Active window not visually distinct
- **Submenu display**: MenuChoice submenu structure exists but display logic incomplete
- **Dialog modal loop**: `execDialog()` is a stub; modal blocking not yet implemented
- **No COMMON subsystem**: No path utilities, error handling, or debug facilities
- **No DRIVERS subsystem**: No file system or time/date access
- **No scrollbars**: TScroller, TGauge, TLift not ported
- **No IMAGE/MATH toolboxes**: Deferred

---

## Test Coverage

8 test classes, ~58 tests (JUnit 5):

| Test Class | What It Tests |
|------------|--------------|
| `PointTest` | Constructor, copy, arithmetic |
| `RectTest` | Constructors, geometry ops, intersect/union |
| `TObjectTest` | Flag management (status/options), visibility, selection |
| `TZoneTest` | Bounds, absolute position with parent chain, contains, visibility |
| `CheckBoxTest` | Checked state, bitmask groups, disabled state |
| `TRadioBoxTest` | Mutual exclusion, getValue, disabled state |
| `EditLineTest` | setText, max length, null handling |
| `DialogTest` | Result codes (OK/Cancel/Yes/No), title |

---

## File Statistics

- Java source files: 31 (src/main)
- Test files: 8 (src/test)
- Total tests: ~58
- Packages: 7
- Classes: 31