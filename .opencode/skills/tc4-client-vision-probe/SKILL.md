---
name: tc4-client-vision-probe
description: "Use when a TC4 client GUI or renderer bug must be verified visually in the headless Forge 1.12.2 client. Covers temporary probe mods, launching an integrated server, constructing real GUI fixtures, saving labeled screenshots, and inspecting PNGs with vision."
---

# TC4 Client Vision Probe

## When to use

Use this skill when source comparison and tests are insufficient to prove a
visual fix, for example:

- tiny or missing Unicode text;
- clipped or overlapping translated labels;
- incorrect GUI layering, tint, alpha, UVs, or animation;
- a TESR, HUD, or GUI that needs a real `Minecraft`, player, or world;
- a fix that compiles but still needs an in-game screenshot.

This workflow was used to verify the TC4 Unicode fixes in the Focal
Manipulator, Arcane Workbench, Thaumatorium, Golem GUI, Traveling Trunk, and
notification HUD.

Do not add probe code to `src/main`. Keep it disposable under `.tmp/` and
`run/mods/`.

## Core workflow

```text
compile product classes
  -> create temporary client-only Forge mod
  -> start the normal headless smoke client
  -> launch an integrated world on the client thread
  -> wait for mc.player and mc.world
  -> construct the real GUI/tile/entity state
  -> display it on the client thread
  -> wait for at least one rendered frame
  -> save a labeled PNG with ScreenShotHelper
  -> open the PNG with the read/vision tool
  -> inspect readability, clipping, layering, color, and geometry
  -> clean all temporary state
```

`smoke-server` alone cannot render a GUI. Use `smoke-client` and launch an
integrated server from the probe when the screen requires a player or world.

## 1. Establish the acceptance matrix

Before writing the probe, list the states that distinguish the bug:

| Dimension | Useful values |
|---|---|
| Language | `en_us`, `ru_ru`, affected locale |
| Force Unicode | `false`, `true` |
| GUI scale | `1`, `2`, `Auto` |
| Screen state | selected/unselected, enough/missing resources, short/long text |
| Renderer state | known/unknown aspect, near/far target, completed/locked research |

For the Russian font bug, the decisive configuration was:

```properties
lang:ru_ru
guiScale:2
forceUnicodeFont:false
tutorialStep:none
```

Minecraft still sets `fontRenderer.getUnicodeFlag()` to `true` for Russian.
Log the runtime flag if the diagnosis depends on it; do not infer it only from
the option.

## 2. Compile current product code first

The probe compiles against `build/classes/java/main`, so update those classes
before launching it:

```bash
./scripts/dev.sh compileJava
```

A focused Gradle test that executes `compileJava` is also sufficient.

## 3. Create a disposable client-only probe mod

Suggested layout:

```text
.tmp/gui-visual-probe/
├── src/probe/GuiVisualProbe.java
└── classes/

run/mods/gui-visual-probe.jar
```

Minimal lifecycle skeleton:

```java
package probe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;

@Mod(
        modid = "guivisualprobe",
        name = "GUI Visual Probe",
        version = "1",
        clientSideOnly = true)
public class GuiVisualProbe {

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();
        Thread runner = new Thread(() -> {
            try {
                Thread.sleep(1000L);
                schedule(mc, () -> mc.launchIntegratedServer(
                        "GuiVisualProbe",
                        "GuiVisualProbe",
                        new WorldSettings(1L, GameType.CREATIVE,
                                false, false, WorldType.DEFAULT)));

                while (mc.player == null || mc.world == null) {
                    Thread.sleep(250L);
                }
                Thread.sleep(1000L);

                GuiScreen screen = createScreen(mc);
                showAndCapture(mc, screen, "reported-state");
            } catch (Exception error) {
                error.printStackTrace();
            }
        }, "gui-visual-probe");
        runner.setDaemon(true);
        runner.start();
    }

    private static GuiScreen createScreen(Minecraft mc) throws Exception {
        // Construct the real TC4 GUI and its tile/entity fixture here.
        throw new UnsupportedOperationException("supply fixture");
    }

    private static void showAndCapture(
            Minecraft mc, GuiScreen screen, String label) throws Exception {
        schedule(mc, () -> mc.displayGuiScreen(screen));
        Thread.sleep(1200L);
        schedule(mc, () -> System.out.println(
                "[GUI-VISUAL] " + label + " "
                        + ScreenShotHelper.saveScreenshot(
                                mc.gameDir,
                                mc.displayWidth,
                                mc.displayHeight,
                                mc.getFramebuffer()).getUnformattedText()));
        Thread.sleep(1200L);
    }

    private static void schedule(Minecraft mc, Runnable action)
            throws Exception {
        mc.addScheduledTask(action).get();
    }
}
```

Important properties:

- `launchIntegratedServer`, `displayGuiScreen`, and screenshot capture execute
  on the client thread via `addScheduledTask`.
- The worker thread only handles delays and orchestration.
- The screenshot is delayed after opening the screen, allowing normal texture,
  container, and framebuffer rendering.
- Every screenshot prints a stable marker and human label to the log.

## 4. Construct real screen fixtures

Prefer the actual GUI class over a fake drawing surface. The screenshot must
exercise the same method that contains the bug.

### Tile GUI

Typical pattern:

```java
TileFocalManipulator tile = new TileFocalManipulator();
tile.setWorld(mc.world);
tile.setInventorySlotContents(0, new ItemStack(ConfigItems.focusFire));
tile.size = 1200;
tile.rank = 5;
tile.upgrade = FocusUpgradeType.frugal.id;
tile.aspects = new AspectList()
        .add(Aspect.AIR, 12)
        .add(Aspect.FIRE, 12)
        .add(Aspect.WATER, 12)
        .add(Aspect.EARTH, 12)
        .add(Aspect.ORDER, 12)
        .add(Aspect.ENTROPY, 12);

return new GuiFocalManipulator(mc.player.inventory, tile);
```

Choose worst-case data: six rows, two- or three-digit values, long translated
text, partially filled bars, or the maximum number of controls.

### Entity GUI

```java
EntityGolemBase golem = new EntityGolemBase(mc.world);
golem.setCore((byte) 2); // long translated blurb
return new GuiGolem(mc.player, golem);
```

### Container result that is difficult to craft naturally

For a visual-only fixture, subclass the real container and override only the
preview getters. Replace the GUI's private preview container by reflection.
Keep the real GUI and normal draw method.

```java
final class FakeArcaneContainer extends ContainerArcaneWorkbench {
    private final AspectList cost;

    FakeArcaneContainer(InventoryPlayer inventory, TileArcaneWorkbench tile) {
        super(inventory, tile);
        cost = new AspectList().add(Aspect.AIR, 16).add(Aspect.FIRE, 17);
    }

    @Override public void refreshResult() {}
    @Override public ItemStack getArcanePreviewResult() {
        return new ItemStack(Items.DIAMOND);
    }
    @Override public AspectList getArcanePreviewCost() {
        return cost;
    }
}
```

Reflection is acceptable only inside the disposable probe. Never copy this
fixture technique into product code.

### Multiple visual states

Capture each state separately and label it:

```java
showAndCapture(mc, unselected, "thaumatorium-unselected");
showAndCapture(mc, selectedPartial, "thaumatorium-selected-partial");
showAndCapture(mc, fullCapacity, "thaumatorium-full-capacity");
```

Sleep for more than one second between captures because Minecraft screenshot
filenames have second-level timestamp resolution.

## 5. Compile and package the probe

```bash
probe=.tmp/gui-visual-probe
forge="$HOME/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar"
guava=$(find "$HOME/.gradle/caches/modules-2/files-2.1/com.google.guava/guava" \
        -name '*.jar' | head -1)

rm -rf "$probe/classes"
mkdir -p "$probe/classes" run/mods

javac -source 8 -target 8 \
  -cp "$forge:$guava:build/classes/java/main" \
  -d "$probe/classes" \
  "$probe/src/probe/GuiVisualProbe.java"

jar cf run/mods/gui-visual-probe.jar -C "$probe/classes" .
```

Guava is needed because `Minecraft.addScheduledTask` returns a
`ListenableFuture`, even if the source does not name that type directly.

## 6. Run the headless client

Back up any existing run configuration before replacing it:

```bash
options_backup=.tmp/gui-visual-probe-options.txt
test ! -f run/options.txt || cp run/options.txt "$options_backup"

cat > run/options.txt <<'EOF'
lang:ru_ru
guiScale:2
forceUnicodeFont:false
tutorialStep:none
EOF

rm -rf run/saves/GuiVisualProbe run/screenshots
mkdir -p run/screenshots

./scripts/dev.sh smoke-client
```

The repository script starts Xorg with the dummy display and software OpenGL.
It also checks crash markers and successful Forge loading.

Find the probe evidence:

```bash
grep -n -a 'GUI-VISUAL' run/smoke-client.log
find run/screenshots -maxdepth 1 -type f -name '*.png' -printf '%p\n' | sort
```

Do not count `smoke-client PASSED` as visual proof. It only proves successful
client loading. The PNG must still be inspected.

## 7. Inspect screenshots with vision

Open each PNG with the local file read tool. Image reads invoke vision and are
the primary visual assertion.

Inspect concrete properties:

- Are glyphs actually legible rather than merely present?
- Is every translated line visible?
- Does text overlap slots, icons, gauges, or the player inventory?
- Are counts anchored to the intended icon corner?
- Are selected-only elements absent in the unselected screenshot?
- Are alpha/tint states visible and correctly layered?
- Is the longest line clipped?
- Does the GUI remain centered at the selected scale?

For a small region, create a disposable crop and inspect it separately:

```bash
magick run/screenshots/example.png \
  -crop 160x100+300+120 +repage .tmp/gui-visual-probe/crop.png
```

Then read `.tmp/gui-visual-probe/crop.png` with vision.

If text is technically visible but difficult to read in the screenshot, the
check failed. Compile/smoke success is not a substitute for readability.

## 8. Iterate safely

After a visual failure:

1. Record the exact issue: clipped last line, blurred half-scale digits,
   overlap, wrong state, or screenshot timing.
2. Change the smallest product layout or probe fixture.
3. Recompile product classes.
4. Recompile the temporary probe if its fixture changed.
5. Capture only the failing state when possible.
6. Re-open the new PNG with vision.

Example from the Golem GUI:

- Unicode scale `0.75` fit but remained hard to read.
- Scale `1.0` was readable but clipped the final wrapped line.
- Scale `0.875` plus measured wrap width was readable and fit the complete
  Russian blurb above the inventory.

This is why source math alone is insufficient for dense translated layouts.

## 9. Common failure modes

### Black or partially rendered screenshot

Cause: capturing directly inside `FMLLoadCompleteEvent` before a normal frame.

Fix: schedule GUI opening after startup, wait about 1.2 seconds, then schedule
the screenshot on the client thread.

### Screenshot shows the main menu instead of the probe GUI

Cause: the normal startup flow replaced a screen opened too early.

Fix: delay from a daemon worker, then call `displayGuiScreen` through
`addScheduledTask` after the integrated world is available.

### Client tick subscriber never captures

Cause: lifecycle/tick bus timing in the short smoke session.

Fix: use the daemon-worker plus `addScheduledTask(...).get()` orchestration.

### Probe compiles with `ListenableFuture` missing

Cause: Guava absent from the `javac` classpath.

Fix: add the cached Guava JAR as shown above.

### GUI constructor has no useful data

Cause: normal recipes/research/container synchronization did not occur in the
synthetic world.

Fix: populate real tile/entity fields, add a synthetic API recipe, or replace
only the preview container in the temporary probe.

### Screenshot filenames overwrite or gain suffixes

Cause: captures occurred in the same second.

Fix: sleep longer than one second and always use log labels instead of relying
only on filenames.

### Tutorial card obscures the GUI

Fix: add `tutorialStep:none` to the temporary `run/options.txt`.

## 10. Cleanup

Remove all disposable artifacts after evidence is recorded:

```bash
options_backup=.tmp/gui-visual-probe-options.txt
if test -f "$options_backup"; then
  cp "$options_backup" run/options.txt
else
  rm -f run/options.txt
fi

rm -rf \
  .tmp/gui-visual-probe \
  "$options_backup" \
  run/mods/gui-visual-probe.jar \
  run/screenshots \
  run/saves/GuiVisualProbe
```

Restore options before deleting the backup.

Finally run:

```bash
git status --short
```

Only intentional product/test/docs files may remain.

## Product validation after visual proof

Visual proof supplements normal gates; it does not replace them:

```bash
git diff --check
./scripts/dev.sh validate --smoke
./scripts/dev.sh build
```

Report separately:

- configurations visually inspected;
- screenshot states inspected with vision;
- focused tests and validation commands;
- states covered only statically and not captured;
- final artifact path.

## Anti-patterns

- Do not claim visual success from compile or smoke logs alone.
- Do not screenshot before a normal rendered frame.
- Do not launch worlds or change screens from the worker thread directly.
- Do not use a fake canvas when the real GUI can be constructed.
- Do not leave probe mods, saves, options, or screenshots in the workspace.
- Do not silently use production code reflection; reflection belongs only in
  the disposable visual fixture.
- Do not inspect one easy state and generalize to selected/unselected,
  resource-full/resource-empty, or short/long translations.
- Do not infer the active font renderer from the Force Unicode option alone.
