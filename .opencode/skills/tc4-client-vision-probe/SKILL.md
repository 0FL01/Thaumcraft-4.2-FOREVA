---
name: tc4-client-vision-probe
description: "Use when a TC4 client GUI or renderer fix needs reproducible visual evidence in the headless Forge 1.12.2 client. Covers collision-safe temporary probe mods, integrated-world startup, client-thread fixtures, named screenshots, vision inspection, and exact cleanup."
---

# TC4 client vision probe

## When to use

Use this workflow when source/tests cannot prove a visual result:

- tiny, clipped, overlapping, or untranslated text;
- wrong tint, alpha, UV, layering, transforms, or animation state;
- a GUI/TESR/HUD requiring a real Minecraft client, player, or world;
- compile/build succeeds but an actual rendered frame is still required.

Keep probes outside `src/main`: use a unique `.tmp/` directory and one uniquely
named JAR under `run/mods/`.

`smoke-client PASSED` proves client startup only. The screenshot must be opened
with vision and judged against explicit acceptance criteria.

## Safety contract

The shared `run/` directory can contain user options, worlds, mods, and
screenshots. A probe must:

1. generate a unique run ID;
2. refuse collisions;
3. record the existing mod set;
4. back up and restore `run/options.txt` according to whether it originally
   existed;
5. remove only its exact JAR, world, and named screenshots;
6. never run `rm -rf run/screenshots`, `run/mods`, or `run/saves`;
7. use a cleanup trap in one orchestration shell, so interruption also restores
   state.

`run/` is ignored by Git, so `git status` cannot detect accidental data loss.

## 1. Define the visual matrix

List states that distinguish success from failure:

| Dimension | Examples |
|---|---|
| language | `en_us`, `ru_ru`, affected locale |
| font | automatic Unicode, forced Unicode, bitmap |
| GUI scale | 1, 2, Auto |
| screen state | selected/unselected, full/empty, short/long |
| renderer state | near/far, known/unknown, completed/locked |

For Russian, `forceUnicodeFont:false` does not imply a bitmap renderer.
Minecraft normally sets `fontRenderer.getUnicodeFlag()` true from locale
coverage. Log the runtime flag when font mode is part of the diagnosis.

## 2. Allocate collision-safe paths

Create a persistent session directory and state file. This is safe across
separate OpenCode Bash calls; source `state.env` in every later shell block.

```bash
set -euo pipefail

run_id="tc4-vision-$(date +%Y%m%d-%H%M%S)-$$"
probe="$PWD/.tmp/$run_id"
probe_jar="$PWD/run/mods/$run_id.jar"
staged_jar="$probe/$run_id.jar"
world_name="TC4Vision-$run_id"
world_dir="$PWD/run/saves/$world_name"
options="$PWD/run/options.txt"
options_backup="$probe/options.original"

test ! -e "$probe"
mkdir -p "$probe/src/probe" "$probe/classes"
declare -p run_id probe probe_jar staged_jar world_name world_dir \
  options options_backup > "$probe/state.env"
printf '[TC4-VISION-SESSION] %s\n' "$probe"
```

Keep the printed path. Later blocks begin with:

```bash
source "/absolute/path/printed/above/state.env"
```

Keep `$probe` and its evidence manifest until the task report is complete.

Existing mods are not automatically isolated by `smoke-client`. Record them
and report the mod set. If a controlled empty mod directory is required, build
a separate preservation/restore step that moves the whole directory and is
covered by the launch block's trap; never delete it.

## 3. Compile current product classes

```text
./scripts/dev.sh compileJava
```

The probe compiles against `build/classes/java/main`. Do not run a stale probe
against old product classes.

## 4. Create a client-only probe mod

The worker thread may orchestrate delays, but every Minecraft state read,
fixture construction, screen change, and screenshot must execute on the client
thread. Readiness polling needs a deadline.

```java
package probe;

import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;

@Mod(modid = "tc4visionprobe", name = "TC4 Vision Probe",
        version = "1", clientSideOnly = true)
public class Tc4VisionProbe {
    private static final String RUN_ID = "REPLACE_RUN_ID";
    private static final String WORLD = "REPLACE_WORLD_NAME";

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();
        Thread runner = new Thread(() -> {
            try {
                Thread.sleep(1000L);
                call(mc, () -> {
                    mc.launchIntegratedServer(WORLD, WORLD,
                            new WorldSettings(1L, GameType.CREATIVE,
                                    false, false, WorldType.DEFAULT));
                    return null;
                });

                long deadline = System.currentTimeMillis() + 60000L;
                while (!call(mc, () -> mc.player != null && mc.world != null)) {
                    if (System.currentTimeMillis() >= deadline) {
                        throw new IllegalStateException("integrated world timeout");
                    }
                    Thread.sleep(250L);
                }

                GuiScreen screen = call(mc, () -> createScreen(mc));
                capture(mc, screen, "reported-state");
            } catch (Throwable error) {
                System.err.println("[TC4-VISION-FAIL] " + RUN_ID);
                error.printStackTrace();
            }
        }, RUN_ID);
        runner.setDaemon(true);
        runner.start();
    }

    private static GuiScreen createScreen(Minecraft mc) throws Exception {
        // Construct the real GUI and tile/entity fixture on the client thread.
        throw new UnsupportedOperationException("supply fixture");
    }

    private static void capture(Minecraft mc, GuiScreen screen, String label)
            throws Exception {
        call(mc, () -> {
            mc.displayGuiScreen(screen);
            return null;
        });
        Thread.sleep(1200L); // allow a normal rendered frame
        call(mc, () -> {
            String file = RUN_ID + "-" + label + ".png";
            String result = ScreenShotHelper.saveScreenshot(
                    mc.gameDir, file, mc.displayWidth, mc.displayHeight,
                    mc.getFramebuffer()).getUnformattedText();
            System.out.println("[TC4-VISION] " + label + " " + file
                    + " :: " + result);
            return null;
        });
    }

    private static <T> T call(Minecraft mc, Callable<T> action)
            throws Exception {
        return mc.addScheduledTask(action).get();
    }
}
```

Replace both constants with the shell-generated names before compiling. The
named screenshot overload avoids timestamp ambiguity and does not require
one-second sleeps between differently labeled captures.

After the run, require:

- no `[TC4-VISION-FAIL]` marker;
- one `[TC4-VISION]` marker per expected label;
- every named PNG exists and is non-empty.

## 5. Build real fixtures

Prefer the actual product GUI/renderer. Populate real tile/entity fields on the
client thread and use worst-case data.

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

For entity GUIs:

```java
EntityGolemBase golem = new EntityGolemBase(mc.world);
golem.setCore((byte) 2);
return new GuiGolem(mc.player, golem);
```

When normal crafting state is prohibitively difficult, a probe-only subclass
may override the real container's preview getters. Reflection is acceptable in
the disposable fixture only. Keep the actual GUI and its normal draw methods;
never move probe shortcuts into product code.

Capture separate labeled states rather than mutating one screenshot claim:

```java
capture(mc, unselected, "thaumatorium-unselected");
capture(mc, selectedPartial, "thaumatorium-selected-partial");
```

## 6. Compile the probe from the canonical cache

The project cache is not necessarily `$HOME/.gradle`:

```bash
source "/absolute/probe/path/state.env"
gradle_home="${THAUMCRAFT_GRADLE_HOME:-$PWD/.gradle_home}"
forge=$(find "$gradle_home/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39" \
  -maxdepth 1 -name 'forgeSrc-1.12.2-14.23.5.2847.jar' -print -quit)
guava=$(find "$gradle_home/caches/modules-2/files-2.1/com.google.guava/guava/21.0" \
  -name '*.jar' -print -quit)

test -n "$forge" && test -f "$forge"
test -n "$guava" && test -f "$guava"
javac -version 2>&1 | grep -Eq 'javac 1\.8(\.|$)'

javac -source 8 -target 8 \
  -cp "$forge:$guava:build/classes/java/main" \
  -d "$probe/classes" \
  "$probe/src/probe/Tc4VisionProbe.java"
jar cf "$staged_jar" -C "$probe/classes" .
```

Stop if Java 8 is unavailable; compile inside `${THAUMCRAFT_DOCKER_IMAGE:-thaumcraft-dev}`
with the same workspace and Gradle-cache mounts rather than using a newer
`javac` silently. Exact fallback:

```bash
source "/absolute/probe/path/state.env"
probe_rel="${probe#$PWD/}"
gradle_home="${THAUMCRAFT_GRADLE_HOME:-$PWD/.gradle_home}"
docker run --rm \
  -v "$PWD:/workspace/thaumcraft" \
  -v "$gradle_home:/home/ubuntu/.gradle" \
  -e PROBE_REL="$probe_rel" \
  -e STAGED_NAME="$(basename "$staged_jar")" \
  --user "$(id -u):$(id -g)" \
  --entrypoint /bin/bash \
  "${THAUMCRAFT_DOCKER_IMAGE:-thaumcraft-dev}" -lc '
    set -eu
    cd /workspace/thaumcraft
    forge=$(find /home/ubuntu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39 -maxdepth 1 -name "forgeSrc-1.12.2-14.23.5.2847.jar" -print -quit)
    guava=$(find /home/ubuntu/.gradle/caches/modules-2/files-2.1/com.google.guava/guava/21.0 -name "*.jar" -print -quit)
    javac -source 8 -target 8 -cp "$forge:$guava:build/classes/java/main" -d "$PROBE_REL/classes" "$PROBE_REL/src/probe/Tc4VisionProbe.java"
    jar cf "$PROBE_REL/$STAGED_NAME" -C "$PROBE_REL/classes" .
  '
```

## 7. Configure and launch in one trapped shell

This is the only block that mutates shared `run/` state. Execute it as one Bash
call. List every expected screenshot explicitly; add more labels as needed.

```bash
set -euo pipefail
source "/absolute/probe/path/state.env"
mkdir -p "$PWD/run/mods" "$PWD/run/screenshots"

screenshots=(
  "$PWD/run/screenshots/$run_id-reported-state.png"
)

test -f "$staged_jar"
test ! -e "$probe_jar"
test ! -e "$world_dir"
for image in "${screenshots[@]}"; do test ! -e "$image"; done
printf '%s\n' "${screenshots[@]}" > "$probe/screenshots.txt"
find "$PWD/run/mods" -maxdepth 1 -type f -printf '%f\n' \
  | sort > "$probe/preexisting-mods.txt"

had_options=0
if test -e "$options"; then
  had_options=1
  cp -p "$options" "$options_backup"
fi

cleanup() {
  rm -f "$probe_jar"
  rm -rf "$world_dir"
  if test "$had_options" -eq 1; then
    cp -p "$options_backup" "$options"
  else
    rm -f "$options"
  fi
}
trap cleanup EXIT
trap 'exit 130' INT TERM

cp "$staged_jar" "$probe_jar"
cat > "$options" <<'EOF'
lang:ru_ru
guiScale:2
forceUnicodeFont:false
tutorialStep:none
EOF
cp "$options" "$probe/effective-options.txt"

./scripts/dev.sh smoke-client

! grep -Fq '[TC4-VISION-FAIL]' run/smoke-client.log
test "$(grep -Fc '[TC4-VISION]' run/smoke-client.log)" -eq "${#screenshots[@]}"
for image in "${screenshots[@]}"; do test -s "$image"; done

cleanup
trap - EXIT INT TERM
```

Find only this run's evidence:

```bash
source "/absolute/probe/path/state.env"
grep -n -a -E 'TC4-VISION|TC4-VISION-FAIL' run/smoke-client.log
cat "$probe/screenshots.txt"
```

Do not generalize a successful client marker into visual success.

## 8. Inspect with vision and record evidence

Open every expected PNG with the local file-read/vision tool. Check concrete
acceptance properties: readability, complete lines, overlap, clipping,
anchoring, state-specific elements, tint/alpha, layering, and centering.

Optional crop, only when available:

```bash
if command -v magick >/dev/null 2>&1; then
  magick "run/screenshots/$run_id-reported-state.png" \
    -crop 160x100+300+120 +repage "$probe/crop.png"
fi
```

Create a session-local manifest before cleanup:

```bash
source "/absolute/probe/path/state.env"
mapfile -t screenshots < "$probe/screenshots.txt"
{
  printf 'run_id=%s\n' "$run_id"
  printf 'mods_manifest=%s\n' "$probe/preexisting-mods.txt"
  sha256sum "$probe/effective-options.txt"
  sha256sum "${screenshots[@]}"
} > "$probe/evidence.txt"
```

Add label-by-label vision verdicts to that file and summarize them in the final
report. Images may remain session-local; do not claim durable repository proof
after deleting them. If durable evidence is required, intentionally commit an
approved artifact or note rather than relying on ignored `run/` state.

## 9. Cleanup and validation

Shared options/JAR/world state was already restored by the trapped launch
block. After the report/evidence is recorded, remove only the explicit image
list and this session directory:

```bash
source "/absolute/probe/path/state.env"
mapfile -t screenshots < "$probe/screenshots.txt"
rm -f -- "${screenshots[@]}"
rm -rf "$probe"
git status --short
```

Visual proof supplements normal gates:

```text
git diff --check
./scripts/dev.sh validate
./scripts/dev.sh build
```

For client-only GUI/renderer/model changes, server smoke is not routine. Use
`validate --smoke` only when common/server loading or behavior also changed.

## Common failures

- **Black/partial image:** capture happened before a normal frame; delay after
  `displayGuiScreen`.
- **Main menu instead of GUI:** screen opened during startup and was replaced;
  schedule it after integrated-world readiness.
- **No client ticks in short smoke:** use the bounded worker plus scheduled
  callables, not an event-bus tick subscriber.
- **Missing `ListenableFuture`:** compile with the canonical Guava JAR.
- **No useful data:** populate real fixtures or override only probe preview
  getters.
- **Tutorial overlay:** set `tutorialStep:none` in the temporary options.
- **Unexpected rendering/mod interaction:** inspect the recorded preexisting
  mod manifest and rerun with an explicitly preserved isolated mod directory.

## Anti-patterns

- Do not mutate Minecraft state or construct fixtures on the worker thread.
- Do not poll player/world forever.
- Do not clear shared run directories.
- Do not restore an old backup whose ownership/run ID is unknown.
- Do not use fake canvases when the real GUI can be constructed.
- Do not infer font mode from options alone.
- Do not claim visual parity from build, smoke logs, or uninspected PNGs.
- Do not inspect one easy state and generalize to all state branches.
