# Java Port - Implementation Notes

## Current Status

Phase 2 complete. Core infrastructure, all main gadgets, scrollbars, and three sample applications are working.

---

## Implemented Classes

### `net.eric_nicolas.sword.ui`

- **`Point`** — Immutable 2D point with arithmetic helpers (plus, minus, min, max)
- **`Rect`** — Rectangle (top-left origin + width/height); intersect, union, contains, grow
- **`Duple<X, Y>`** — Immutable typed pair; implements `equals` / `hashCode` so it can be used as a map or cache key
- **`Triple<X, Y, Z>`** — Immutable typed triplet; implements `equals` / `hashCode` (via `Objects.hash`) so it can be used as a map or cache key; companion to `Duple`
- **`Cache<K, V>`** — Bounded FIFO cache backed by a `LinkedHashMap` with `removeEldestEntry`; configurable `maxSize`; operations: `get`, `put`, `contains`, `size`, `maxSize`, `clear`
- **`TexHelper`** — Static helper that parses text-mode TeX strings and lays them out as cached `TeXIcon` objects via JLaTeXMath. Input is plain text with optional `\math{...}` inline math blocks; newlines produce line breaks. Conversion pipeline: `toLatex()` wraps text in `\text{...}` and emits math verbatim inside `\begin{array}{l}...\end{array}`. Icons are stored in a static `Cache<Duple<String,Float>, TeXIcon>` (FIFO, 32 entries) keyed by `(text, fontSize)` — color-independent; color is applied via `TeXIcon.setForeground` at paint time. Public API: `getOrCreateIcon(text, fontSize)`, `measure(text, fontSize)`, `clearCache()`.

### `net.eric_nicolas.sword.ui.events`

- **`Event`** — Base event: `what` type constant, `EV_NOTHING` sentinel
- **`EventMouse`** — Mouse event: `where` (Point), button mask, modifiers; `withOffset(dx,dy)` for coordinate translation
- **`EventKeyboard`** — Keyboard event: key code, char, modifiers
- **`EventCommand`** — Command event: `commandId` for routing UI actions up the hierarchy
- **`EventLwjglAdapter`** — Converts GLFW raw input callbacks → S.W.O.R.D `EventMouse` / `EventKeyboard`

### `net.eric_nicolas.sword.ui.base`

- **`ScreenArea`** — Root of the visual hierarchy: `father` reference, status bitmask flags (`SF_VISIBLE`, `SF_SELECTED`, `SF_MOUSE_IN`, `SF_DOWN`, `SF_FOCUSED`, `SF_MODIFIED`), virtual event handlers (`mouseLDown`, `mouseMove`, `keyDown`, `command`, …), drawing area with `bounds`, `clipRect`, `draw()` / `paint()`, `getAbsolutePosition()`, `contains()`
- **`Widget`** — Extends ScreenArea; adds `enabled` boolean (`isEnabled()` / `setEnabled()`)
- **`Canvas`** — Transparent container for Widget children stored in `LinkedList<Widget>`; dispatches events in reverse (topmost first) z-order
- **`Window`** — Overlapping window: left sidebar (drag grip + optional close button), outer resize border; internal `Canvas`; `bringToFront()` / `remove()`. Options: `setResizable(bool)` (thick border + resize handles vs. 1-px outline), `setClosable(bool)` (show/hide × button), `setPalette(WindowPalette)` (colour scheme for this window and all its widgets). `setOnResize(Runnable)` callback fired on resize. `draw()` injects the window's palette into the `PaintContext` before delegating to `super.draw()`, `canvas.draw()`, and `drawOverlay()` so the entire hierarchy uses the same palette automatically.
- **`Screen`** — Plain class (not a `ScreenArea`): manages `LinkedList<Window>` with z-ordering; dispatches events topmost-first; routes unhandled commands to the registered `IntPredicate` command handler. Windows hold a direct `screen` reference (`Window.getScreen()`) set by `Screen.add()`; `Screen` is never in the `ScreenArea.father` chain. Application-level commands (those not consumed by any window) are queued in `pendingCommands` and drained by `processPendingCommands()` from the main loop — outside GLFW callbacks — so that modal handlers like `execDialog()` can safely pump the GLFW event loop.
- **`WindowPalette`** — Set of five coordinated colours (`black`, `dark`, `medium`, `face`, `white`) that defines the visual appearance of a window and all its widgets. Three pre-built instances: `STANDARD` (neutral grays, default), `GREEN` (slightly green-tinted, used by Menu/MenuChoice), `BLUE` (slightly blue-tinted, used by Dialog). Assigned via `Window.setPalette()`; injected into `PaintContext` by `Window.draw()` and propagated automatically through `withOrigin()`.
- **`PaintContext`** — `Graphics2D` wrapper with local-coordinate translation via `withOrigin(Point)`, palette propagation via `withPalette(WindowPalette)`, font-size propagation via `withFontSize(float)`, and HiDPI support via `withDpr(int)` (device pixel ratio, default 1). All text is drawn through `TexHelper`: `drawString(x, y, text)` calls `getOrCreateIcon`, sets foreground to the current colour, and paints directly via `icon.paintIcon(null, g, ...)` at the given coordinates; `measureText(text)` returns the icon dimensions for layout. No AWT `Font` or `FontMetrics` exposure.
- **`Application`** — Application shell (plain class, not a ScreenArea): owns a `Screen` and a `LwjglDriver`; extend and override `createMenuChoices()` + `handleCommand()`

### `net.eric_nicolas.sword.ui.driver`

- **`LwjglDriver`** — GLFW window + OpenGL 3.3 compositor. Each `Window` renders into its own `BufferedImage` (via `Window.renderToBuffer()` / Java2D); `LwjglDriver` uploads these as OpenGL textures and composites them in z-order each frame using a textured-quad shader. Provides `forceRepaint()` (no-op; continuous loop), `quit()`, and a `frameStep` `Runnable` registered with `Screen` for modal dialog loops. Requires `-XstartOnFirstThread` and `-Djava.awt.headless=true` on macOS.
- **`EventLwjglAdapter`** — Converts GLFW raw input (cursor pos, mouse button, key, char callbacks) → S.W.O.R.D `EventMouse` / `EventKeyboard`. GLFW key codes 65–90 and 48–57 match Java `VK_` values directly.

### `net.eric_nicolas.sword.ui.widgets`

- **`Label`** — Non-interactive text label. Text is rendered via `PaintContext.drawString` (→ `TexHelper`) so plain text and `\math{...}` inline math are both supported. `setFontSize(float)` / `getFontSize()` control the JLaTeXMath point size (default 12 pt); centering uses `ctx.measureText()`.
- **`AbstractButton`** — Base for clickable buttons: 3D raised/pressed frame, scan-code support, command routing
- **`Button`** — Standard push button with centred text
- **`ItemBox`** — Base for selection controls (no button frame); click toggles `SF_DOWN`
- **`CheckBox`** — Checkbox; bitmask integration with `GroupBox` for `getData()`
- **`RadioBox`** — Radio button; mutually exclusive within `GroupBox`
- **`GroupBox`** — Container for `CheckBox`/`RadioBox`; manages group `value` and titled frame
- **`EditLine`** — Single-line text input: cursor, click-to-position, keyboard navigation, max length. Cursor x-position computed via `TexHelper.measure(prefix, fontSize).width`.
- **`Menu`** — Menu bar (`mainMenu=true`) or dropdown; horizontal/vertical layouts, hotkey support. Choice widths computed via `TexHelper.measure()`.
- **`MenuChoice`** — Menu item: text, hotkey, command; `separator=true` for dividers
- **`Dialog`** — Window subclass with result codes (`CM_OK`, `CM_CANCEL`, `CM_YES`, `CM_NO`); `execDialog()` runs a GLFW-based modal loop via `Screen.getFrameStep()`. CM_OK/CM_CANCEL are dispatched directly to `Dialog.command()` within GLFW callbacks (not queued), so `dialogResult` is set immediately and the modal loop exits on the next iteration.
- **`StandardButtons`** — Factory for standard OK / Cancel / Yes / No button instances
- **`Scrollbar`** — Port of `TLift`: H/V scrollbar with arrow buttons, thumb drag, page click; `setRange(contentSize, viewSize)`, `getPosition()`, `setOnChange(Runnable)`. Drag capture: `mouseLUp`/`mouseMove` return true while dragging even outside bounds.
- **`Scroller`** — Port of `TScroller`: scrollable viewport backed by a viewport-sized `BufferedImage`. Virtual content size (governs scrollbar range) is independent of the buffer. Mouse events are forwarded as viewport-local coordinates. Public API: `setContentSize`, `setScrollPosition`, `getScrollX/Y`, `setOnScroll`, `resize(newViewW, newViewH)` (live viewport resize).

### `net.eric_nicolas.sword.samples`

- **`Hello`** — Multiple overlapping draggable windows; each window contains a `Label` (font size 22 pt) rendering "Hello World !" on the first line and the Gaussian integral formula via `\math{...}` on the second line
- **`Dialog`** — Demonstrates `Dialog`, `Button`, `CheckBox`, `RadioBox`, `GroupBox`, `EditLine`, `Label`
- **`Mandel`** — Mandelbrot fractal viewer with zoom + pan. `MandelWidget` renders only the current viewport into a `BufferedImage`, tracking `zoom` and `offsetX/Y`. Virtual world size is fixed at `baseW × baseH` (set at construction) and scales with zoom (`virtualW = baseW * zoom`), so resizing the window reveals more of the complex plane rather than stretching the view. Left-click zooms in 2× (virtual world doubles, thumb halves); right-click undoes zoom. Wired to `Scroller` via `onZoomChange` / `onScroll` callbacks so scrollbars always reflect zoom level and enable full panning.

---

## Architecture Notes

### What Was Changed from the Original C++

| Aspect | C++ Original | Java Port |
|--------|-------------|-----------|
| Naming | All classes T-prefixed | Plain names used everywhere: `ScreenArea`, `Application`, `WindowPalette`, … No T-prefixed classes remain |
| Tree structure | TAtom: `_Next/_Previous/_Son/_Father` sibling chain | Only `father` parent ref in ScreenArea; children in `LinkedList` |
| Child storage | TAtom linked tree | `LinkedList<Widget>` in Canvas, `LinkedList<Window>` in Screen |
| Event tables | C++ macros `DEFINE_EVENTS_TABLE` | Virtual method overrides in ScreenArea subclasses |
| Graphics backend | libgrx20 calls | Java2D `Graphics2D` off-screen + LWJGL/OpenGL compositor |
| Driver coupling | N/A | Isolated in `ui.driver` (LwjglDriver + EventLwjglAdapter) |
| Data exchange | `SetData()/GetData()/DataSize()` | Removed |
| `TShell` | Trivial TObject subclass | Removed; `Application` is a plain class |
| `TObject` + `TZone` | Separate mechanism/graphics layers | Merged into `ScreenArea` |
| `TDesktop` | Background application desktop | `Screen` (plain class, not a ScreenArea subclass) |
| `sfDisabled` flag | Status bitmask | `Widget.enabled` boolean |
| `opMainMenu` / `opSeparator` | Option bitmasks | `Menu.mainMenu` / `MenuChoice.separator` booleans |
| `opWinSizeable` / `opWinCloseBox` | Option bitmasks | `Window.resizable` / `Window.closable` booleans |
| Button `BO_*` options | Constructor parameter | Removed; use `setEnabled(false)` after construction |
| Packages | Flat subsystem names | `ui.base`, `ui.widgets`, `ui.events`, `ui.driver` |

### Key Design Decisions

1. **No TAtom**: The linked sibling tree is removed. Children live in explicit `LinkedList` containers in Canvas and Screen.
2. **TObject merged into ScreenArea**: The former mechanism/graphics split is collapsed. `ScreenArea` is the single root for all visual objects; it holds `father`, `status`, event dispatch, bounds, and drawing.
3. **Application is a plain class**: Not a ScreenArea subclass. Owns a `Screen` and a `LwjglDriver`; registers command/hotkey handlers via lambdas.
4. **Driver isolated in `ui.driver`**: `LwjglDriver` holds the GLFW window and OpenGL compositor. `EventLwjglAdapter` translates GLFW callbacks. No LWJGL/AWT imports outside this package.
5. **Screen is not a ScreenArea**: Screen sits above the ScreenArea hierarchy. `ScreenArea.father` terminates at the top-level Window (father = null). Windows hold a direct `Screen` reference (`Window.getScreen()`) used for window management and command routing.
6. **Parent reference only**: `ScreenArea.father` enables coordinate translation and event routing within the window hierarchy, but no sibling navigation.
7. **Method-override event dispatch**: `ScreenArea.handleEvent` dispatches via a `switch` to overridable methods; no macro tables.
8. **PaintContext**: Local-coordinate translation is managed in `PaintContext`; callers always draw in their own (0,0)-based coordinate space.
9. **Window palette propagation**: `Window.draw()` calls `ctx.withPalette(palette)` before passing to `super.draw()`, `canvas.draw()`, and `drawOverlay()`. `PaintContext.withOrigin()` copies the palette, so every `paint()` method in the hierarchy receives `ctx.palette()` with the correct colour scheme — no extra wiring required. All widget paint methods read `ctx.palette().black/dark/medium/face/white` instead of static `TColors` constants.
10. **TexHelper / PaintContext text pipeline**: All text drawing goes through `TexHelper`. The expensive JLaTeXMath parse+layout step is cached in a `Cache<Duple<String,Float>, TeXIcon>` (color-independent key). `PaintContext.drawString(x, y, text)` calls `getOrCreateIcon`, sets `TeXIcon.setForeground` to the current colour, and paints directly via `icon.paintIcon`. `PaintContext.measureText(text)` reads icon dimensions directly. The `fontSize` is carried as a field on `PaintContext`; `withFontSize(float)` returns a derived context (just like `withPalette`). `Label` exposes `setFontSize(float)` so individual labels can override the default 12 pt.
11. **Scrollbar drag capture**: Like Window title-bar drag, Scrollbar returns `true` from `mouseMove`/`mouseLUp` while `dragging==true` regardless of contains, so the thumb follows the mouse even outside the bar.
12. **Scroller viewport buffer**: Content renders at viewport size (not virtual size), so only the visible slice is computed. The scrollbar range tracks the virtual size independently. Scroll offset is forwarded to the content widget via callback.

---

## What Works

- ✅ Object hierarchy (parent reference chain, command routing up)
- ✅ Event dispatching (mouse, keyboard, commands)
- ✅ Window creation, management, z-ordering, drag
- ✅ Overlapping window rendering
- ✅ Custom zone/widget painting
- ✅ GLFW input event conversion (EventLwjglAdapter)
- ✅ OpenGL compositing (LwjglDriver — per-window BufferedImage textures)
- ✅ Modal dialog event loop (execDialog — GLFW-based mini-loop)
- ✅ Button (standard push button, 3D pressed effect)
- ✅ CheckBox / RadioBox / GroupBox
- ✅ EditLine (text input, cursor, keyboard navigation)
- ✅ Menu / MenuChoice (hotkeys, separators, H+V layouts)
- ✅ Dialog (result codes OK/Cancel/Yes/No)
- ✅ Label (text + `\math{...}` inline formulas via TexHelper; variable font size)
- ✅ Scrollbar (arrow, thumb drag, page click, H/V)
- ✅ Scroller (viewport buffer, zoom-aware content/scrollbar sync)
- ✅ Mandel sample (fractal, zoom history, pan with scrollbars)
- ✅ Window palette system (`WindowPalette`: STANDARD / GREEN / BLUE; propagated via PaintContext)
- ✅ TexHelper (JLaTeXMath rendering, lazy FIFO cache, all text rendered via PaintContext)

## Known Limitations

- **No window focus styling**: Active window not visually distinct
- **No COMMON subsystem**: No path utilities, error handling, or debug facilities
- **No DRIVERS subsystem**: No file system or time/date access
- **No TGauge**: Progress bar not ported
- **No IMAGE/MATH toolboxes**: Deferred
- **Mandel render blocks frame**: Fractal re-render on zoom/scroll blocks the GLFW frame; async rendering is out of scope
- **`[JRSAppKitAWT markAppIsDaemon]` warning on macOS**: Harmless. GLFW initializes `NSApplication` before Java's headless setup completes natively; partial headless mode is sufficient for off-screen Java2D rendering alongside GLFW.

---

## Test Coverage

19 test classes, 217 tests (JUnit 5):

| Test Class | What It Tests |
|------------|--------------|
| `PointTest` | Constructor, copy, arithmetic |
| `RectTest` | Constructors, geometry ops, intersect/union |
| `DupleTest` | Getters, equals/hashCode, null components, use as map key |
| `TripleTest` | Getters, equals/hashCode, null components, use as map key |
| `CacheTest` | put/get, FIFO eviction, size limit, contains, clear |
| `ScreenAreaTest` | Bounds, absolute position with parent chain, contains(Point), visibility, status flags, father reference |
| `CanvasTest` | Child widget management, parent wiring, unmodifiable list, widget order |
| `WindowPaletteTest` | Colour values for STANDARD/GREEN/BLUE, custom constructor, tint direction |
| `WindowTest` | Title, palette, resizable/closable flags, content dimensions, canvas sync on bounds change |
| `ScreenTest` | Desktop colour, add/remove/bringToFront, screen reference wiring, quitting flag, pending command queue |
| `CheckBoxTest` | Checked state, bitmask groups, disabled state |
| `TRadioBoxTest` | Mutual exclusion, getValue, disabled state |
| `EditLineTest` | setText, max length, null handling, enabled/disabled |
| `DialogTest` | Result codes (OK/Cancel/Yes/No), title, BLUE palette, non-resizable |
| `ButtonTest` | Text, null default, enabled/disabled, bounds |
| `MenuTest` | GREEN palette, not closable/resizable, choices list |
| `GroupBoxTest` | Title text, value field, null title, all constructors |
| `ScrollbarTest` | Initial state, H/V dimensions, setRange clamping, setPosition clamping |
| `LabelTest` | getText/setText, \math{} notation, getFontSize/setFontSize, bounds, visibility |

---

## File Statistics

- Java source files: 39 (src/main)
- Test files: 19 (src/test)
- Total tests: 217
- Packages: 6 (ui, ui.events, ui.base, ui.widgets, ui.driver, samples)
- Classes: 39
