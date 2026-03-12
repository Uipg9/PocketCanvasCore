# PocketCanvasCore

A lightweight Fabric library mod for **Minecraft 1.21.11** that wraps Minecraft's rendering into simple, one‑line static methods. Zero texture files — every shape is drawn procedurally. Client‑side only.

## Target

| Property | Value |
|---|---|
| Minecraft | 1.21.11 |
| Fabric Loader | ≥ 0.18.0 |
| Fabric API | 0.141.3+1.21.11 |
| Java | 21 |
| Yarn Mappings | 1.21.11+build.4 |

## Features

### PocketCanvas — Primitive 2D Drawing API

All methods are static. Pass your `DrawContext` and draw.

| Method | Description |
|---|---|
| `drawRect` | Solid filled rectangle |
| `drawRoundedRect` | Filled rectangle with rounded corners (scanline‑arc) |
| `drawRoundedBorder` | 1‑pixel rounded‑corner outline |
| `drawTriangle` | Cardinal‑direction triangle (UP / DOWN / LEFT / RIGHT) |
| `drawTriangleVertices` | Arbitrary triangle from three vertices |
| `drawCircle` | Filled circle (scanline) |
| `drawAnnulus` | Filled ring / donut |
| `drawAnnulusArc` | Partial arc of a ring (for progress indicators) |
| `drawDropShadow` | Layered rounded‑rect shadow |
| `drawGradientV` | Vertical gradient (top → bottom) |
| `drawGradientH` | Horizontal gradient (left → right) |
| `drawLine` | 1‑pixel Bresenham line |
| `drawLine` (thick) | Multi‑pixel thick line |
| `drawDivider` | Horizontal 1‑pixel divider |

**Colour utilities:** `withAlpha`, `lerpColor`, `darken`, `lighten`

### PocketTheme — Design Tokens

30+ pre‑defined ARGB colour constants organised into:

- **Backgrounds** — `BACKGROUND_MAIN`, `BACKGROUND_PANEL`, `BACKGROUND_ELEVATED`
- **Accents** — `ACCENT_PRIMARY`, `ACCENT_SECONDARY`, `ACCENT_TERTIARY`
- **Text** — `TEXT_MAIN`, `TEXT_MUTED`, `TEXT_ACCENT`
- **Borders** — `BORDER_DEFAULT`, `BORDER_FOCUS`
- **Gameplay** — Villager skin/robe/nose/eyes, crop dirt/stalk/wheat colours
- **Progress** — `PROGRESS_TRACK`, `PROGRESS_FILL`

### PocketComponents — Higher‑Level UI Widgets

| Method | Description |
|---|---|
| `drawProgressBar` | Rounded‑rect progress bar with track + fill |
| `drawLabelledProgressBar` | Progress bar with centred percentage label |
| `drawPanel` | Dark panel with optional shadow and border |
| `drawProgressRing` | Circular progress ring (annulus arc) |
| `drawTooltipPanel` | Compact tooltip surface |
| `drawDivider` | Themed horizontal divider |

### PocketIcon — Procedural Icons

| Method | Description |
|---|---|
| `drawVillagerIcon` | Stylised villager head + body (rectangles & rounded rects) |
| `drawCropIcon` | Farmland patch with growing stalk and wheat head |

## Installation

### As a dependency in your Fabric mod

Add to your `build.gradle`:

```groovy
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    modImplementation "com.github.Uipg9:PocketCanvasCore:1.0.0"
}
```

Or drop the built JAR into your `mods/` folder alongside your mod.

## Quick Start

```java
import com.pocketcanvascore.render.PocketCanvas;
import com.pocketcanvascore.render.PocketTheme;
import com.pocketcanvascore.render.PocketComponents;
import com.pocketcanvascore.render.PocketIcon;
import net.minecraft.client.gui.DrawContext;

// Inside any render method that receives a DrawContext:

// Draw a dark rounded panel with shadow & border
PocketComponents.drawPanel(context, 10, 10, 200, 120, 6);

// Draw a filled rectangle
PocketCanvas.drawRect(context, 20, 20, 80, 40, PocketTheme.ACCENT_PRIMARY);

// Draw a rounded rectangle
PocketCanvas.drawRoundedRect(context, 20, 70, 80, 30, 4, PocketTheme.BACKGROUND_ELEVATED);

// Draw a filled circle
PocketCanvas.drawCircle(context, 160, 50, 20, PocketTheme.ACCENT_SECONDARY);

// Draw a progress bar at 65%
PocketComponents.drawProgressBar(context, 20, 110, 170, 8, 0.65f);

// Draw a circular progress ring at 42%
PocketComponents.drawProgressRing(context, 160, 90, 16, 3, 0.42f);

// Draw a villager icon at 2× scale
PocketIcon.drawVillagerIcon(context, 240, 10, 2f);

// Draw a crop icon at 75% growth
PocketIcon.drawCropIcon(context, 240, 60, 2f, 0.75f);

// Colour manipulation
int fadedAccent = PocketCanvas.withAlpha(PocketTheme.ACCENT_PRIMARY, 128);
int darkBg = PocketCanvas.darken(PocketTheme.BACKGROUND_PANEL, 0.6f);
int blend = PocketCanvas.lerpColor(PocketTheme.ACCENT_PRIMARY, PocketTheme.ACCENT_SECONDARY, 0.5f);
```

## How It Works

Every shape is rendered using **scanline fill** via `DrawContext.fill()`. This approach:

- Works reliably with the 1.21.11 rendering pipeline (no dependency on vertex buffers, `RenderSystem` blend state, or shader programs)
- Produces pixel‑perfect results at any GUI scale
- Has zero texture overhead — everything is procedural

Rounded corners use a cached integer square‑root lookup (`getScanlineDx`) with an LRU cache of 64 radii. Triangles use classic scanline rasterisation. Circles, annuli, and arcs iterate over rows computing horizontal extents from the circle equation.

## Building

```bash
./gradlew build
```

The output JAR is in `build/libs/`.

## License

MIT — see [LICENSE](LICENSE).
