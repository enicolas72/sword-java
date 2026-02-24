# Java Port - Implementation Notes

## Current Status

Phase 2 complete. Core infrastructure, all main gadgets, scrollbars, and three sample applications are working.

---

## Implemented Classes

### `net.eric_nicolas.sword.ui`

- **`Point`** — Immutable 2D point with arithmetic helpers (plus, minus, min, max)
- **`Rect`** — Rectangle (top-left origin + width/height); intersect, union, contains, grow

### `net.eric_nicolas.sword.ui.events`

- **`Event`** — Base event: `what` type constant, `EV_NOTHING` sentinel
- **`EventMouse`** — Mouse event: `where` (Point), button mask, modifiers; `withOffset(dx,dy)` for coordinate translation
- **`EventKeyboard`** — Keyboard event: key code, char, modifiers
- **`EventCommand`** — Command event: `commandId` for routing UI actions up the hierarchy
- **`EventAwtAdapter`** — Converts AWT `MouseEvent`/`KeyEvent` → S.W.O.R.D events

### `net.eric_nicolas.sword.ui.base`

- **`TZone`** — Root of the visual hierarchy: `father` reference, status bitmask flags (`SF_VISIBLE`, `SF_SELECTED`, `SF_MOUSE_IN`, `SF_DOWN`, `SF_FOCUSED`, `SF_MODIFIED`), virtual event handlers (`mouseLDown`, `mouseMove`, `keyDown`, `command`, …), drawing area with `bounds`, `clipRect`, `draw()` / `paint()`, `getAbsolutePosition()`, `contains()`
- **`Widget`** — Extends TZone; adds `enabled` boolean (`isEnabled()` / `setEnabled()`)
- **`Canvas`** — Transparent container for Widget children stored in `LinkedList<Widget>`; dispatches events in reverse (topmost first) z-order
- **`Window`** — Overlapping window: left sidebar (drag grip + optional close button), outer resize border; internal `Canvas`; `bringToFront()` / `remove()`. Options: `setResizable(bool)` (thick border + resize handles vs. 1-px outline), `setClosable(bool)` (show/hide × button). `setOnResize(Runnable)` callback fired on resize. `drawOverlay()` redraws inner chrome after canvas children so widget fills never obscure it.
- **`Screen`** — Manages `LinkedList<Window>` with z-ordering; dispatches events; routes unhandled commands to the registered `IntPredicate` command handler
- **`TColors`** — Static colour palette (standard + UI theme: `FACE_GRAY`, `MEDIUM_GRAY`, `DESKTOP_BG`, …)
- **`PaintContext`** — `Graphics2D` wrapper with local-coordinate translation via `withOrigin(Point)`; covers draw/fill/clip/image primitives
- **`TApp`** — Application shell (plain class, not a TZone): owns a `Screen` and an `AwtDriver`; extend and override `createMenuChoices()` + `handleCommand()`

### `net.eric_nicolas.sword.ui.driver`

- **`AwtDriver`** — AWT event loop, `Frame`, back-buffered `Canvas`, `forceRepaint()`; translates AWT events via `EventAwtAdapter` and forwards them to `Screen`; calls the registered `hotKeyHandler` before desktop dispatch for global menu shortcuts
- **`EventAwtAdapter`** — Converts AWT `MouseEvent`/`KeyEvent` → S.W.O.R.D events

### `net.eric_nicolas.sword.ui.widgets`

- **`Label`** — Non-interactive text label
- **`AbstractButton`** — Base for clickable buttons: 3D raised/pressed frame, scan-code support, command routing
- **`Button`** — Standard push button with centred text
- **`ItemBox`** — Base for selection controls (no button frame); click toggles `SF_DOWN`
- **`CheckBox`** — Checkbox; bitmask integration with `GroupBox` for `getData()`
- **`RadioBox`** — Radio button; mutually exclusive within `GroupBox`
- **`GroupBox`** — Container for `CheckBox`/`RadioBox`; manages group `value` and titled frame
- **`EditLine`** — Single-line text input: cursor, click-to-position, keyboard navigation, max length
- **`Menu`** — Menu bar (`mainMenu=true`) or dropdown; horizontal/vertical layouts, hotkey support
- **`MenuChoice`** — Menu item: text, hotkey, command; `separator=true` for dividers
- **`Dialog`** — Window subclass with result codes (`CM_OK`, `CM_CANCEL`, `CM_YES`, `CM_NO`); `execDialog()` stub
- **`StandardButtons`** — Factory for standard OK / Cancel / Yes / No button instances
- **`Scrollbar`** — Port of `TLift`: H/V scrollbar with arrow buttons, thumb drag, page click; `setRange(contentSize, viewSize)`, `getPosition()`, `setOnChange(Runnable)`. Drag capture: `mouseLUp`/`mouseMove` return true while dragging even outside bounds.
- **`Scroller`** — Port of `TScroller`: scrollable viewport backed by a viewport-sized `BufferedImage`. Virtual content size (governs scrollbar range) is independent of the buffer. Mouse events are forwarded as viewport-local coordinates. Public API: `setContentSize`, `setScrollPosition`, `getScrollX/Y`, `setOnScroll`, `resize(newViewW, newViewH)` (live viewport resize).

### `net.eric_nicolas.sword.samples`

- **`Hello`** — Multiple overlapping draggable windows with a custom `THello` widget drawing "Hello World!"
- **`Dialog`** — Demonstrates `Dialog`, `Button`, `CheckBox`, `RadioBox`, `GroupBox`, `EditLine`, `Label`
- **`Mandel`** — Mandelbrot fractal viewer with zoom + pan. `MandelWidget` renders only the current viewport into a `BufferedImage`, tracking `zoom` and `offsetX/Y`. Virtual world size is fixed at `baseW × baseH` (set at construction) and scales with zoom (`virtualW = baseW * zoom`), so resizing the window reveals more of the complex plane rather than stretching the view. Left-click zooms in 2× (virtual world doubles, thumb halves); right-click undoes zoom. Wired to `Scroller` via `onZoomChange` / `onScroll` callbacks so scrollbars always reflect zoom level and enable full panning.

---

## Architecture Notes

### What Was Changed from the Original C++

| Aspect | C++ Original | Java Port |
|--------|-------------|-----------|
| Naming | All classes T-prefixed | T-prefix on core classes; gadgets/geometry omit it |
| Tree structure | TAtom: `_Next/_Previous/_Son/_Father` sibling chain | Only `father` parent ref in TZone; children in `LinkedList` |
| Child storage | TAtom linked tree | `LinkedList<Widget>` in Canvas, `LinkedList<Window>` in Screen |
| Event tables | C++ macros `DEFINE_EVENTS_TABLE` | Virtual method overrides in TZone subclasses |
| Graphics backend | libgrx20 calls | AWT `Graphics2D` via `PaintContext` wrapper |
| AWT coupling | N/A | Isolated in `ui.driver` (AwtDriver + EventAwtAdapter) |
| Data exchange | `SetData()/GetData()/DataSize()` | Removed |
| `TShell` | Trivial TObject subclass | Removed; `TApp` is a plain class |
| `TObject` + `TZone` | Separate mechanism/graphics layers | Merged into `TZone` |
| `TDesktop` | Background application desktop | Renamed to `Screen` |
| `sfDisabled` flag | Status bitmask | `Widget.enabled` boolean |
| `opMainMenu` / `opSeparator` | Option bitmasks | `Menu.mainMenu` / `MenuChoice.separator` booleans |
| `opWinSizeable` / `opWinCloseBox` | Option bitmasks | `Window.resizable` / `Window.closable` booleans |
| Button `BO_*` options | Constructor parameter | Removed; use `setEnabled(false)` after construction |
| Packages | Flat subsystem names | `ui.base`, `ui.widgets`, `ui.events`, `ui.driver` |

### Key Design Decisions

1. **No TAtom**: The linked sibling tree is removed. Children live in explicit `LinkedList` containers in Canvas and Screen.
2. **TObject merged into TZone**: The former mechanism/graphics split is collapsed. `TZone` is the single root for all visual objects; it holds `father`, `status`, event dispatch, bounds, and drawing.
3. **TApp is a plain class**: No longer extends TZone/TObject. Owns a `Screen` and an `AwtDriver`; registers command/hotkey handlers via lambdas.
4. **AWT isolated in `ui.driver`**: `AwtDriver` holds the `Frame`, `Canvas`, back buffer, and all AWT listeners. `EventAwtAdapter` translates AWT events. No AWT imports outside this package.
5. **Parent reference only**: `TZone.father` enables upward command routing, but no sibling navigation.
6. **Method-override event dispatch**: `TZone.handleEvent` dispatches via a `switch` to overridable methods; no macro tables.
7. **PaintContext**: Local-coordinate translation is managed in `PaintContext`; callers always draw in their own (0,0)-based coordinate space.
8. **Scrollbar drag capture**: Like Window title-bar drag, Scrollbar returns `true` from `mouseMove`/`mouseLUp` while `dragging==true` regardless of contains, so the thumb follows the mouse even outside the bar.
9. **Scroller viewport buffer**: Content renders at viewport size (not virtual size), so only the visible slice is computed. The scrollbar range tracks the virtual size independently. Scroll offset is forwarded to the content widget via callback.

---

## What Works

- ✅ Object hierarchy (parent reference chain, command routing up)
- ✅ Event dispatching (mouse, keyboard, commands)
- ✅ Window creation, management, z-ordering, drag
- ✅ Overlapping window rendering
- ✅ Custom zone/widget painting
- ✅ AWT event conversion
- ✅ Button (standard push button, 3D pressed effect)
- ✅ CheckBox / RadioBox / GroupBox
- ✅ EditLine (text input, cursor, keyboard navigation)
- ✅ Menu / MenuChoice (hotkeys, separators, H+V layouts)
- ✅ Dialog (result codes OK/Cancel/Yes/No)
- ✅ Label
- ✅ Scrollbar (arrow, thumb drag, page click, H/V)
- ✅ Scroller (viewport buffer, zoom-aware content/scrollbar sync)
- ✅ Mandel sample (fractal, zoom history, pan with scrollbars)

## Known Limitations

- **No window focus styling**: Active window not visually distinct
- **Dialog modal loop**: `execDialog()` is a stub; modal blocking not yet implemented
- **No COMMON subsystem**: No path utilities, error handling, or debug facilities
- **No DRIVERS subsystem**: No file system or time/date access
- **No TGauge**: Progress bar not ported
- **No IMAGE/MATH toolboxes**: Deferred
- **Mandel render on EDT**: Fractal re-render on zoom/scroll blocks the UI; async rendering is out of scope

---

## Test Coverage

7 test classes, 54 tests (JUnit 5):

| Test Class | What It Tests |
|------------|--------------|
| `PointTest` | Constructor, copy, arithmetic |
| `RectTest` | Constructors, geometry ops, intersect/union |
| `TZoneTest` | Bounds, absolute position with parent chain, contains, visibility, status flags |
| `CheckBoxTest` | Checked state, bitmask groups, disabled state |
| `TRadioBoxTest` | Mutual exclusion, getValue, disabled state |
| `EditLineTest` | setText, max length, null handling |
| `DialogTest` | Result codes (OK/Cancel/Yes/No), title |

---

## File Statistics

- Java source files: 33 (src/main)
- Test files: 7 (src/test)
- Total tests: 54
- Packages: 6 (ui, ui.events, ui.base, ui.widgets, ui.driver, samples)
- Classes: 33
