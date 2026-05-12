# Thaumcraft 4.2.3.5 (1.7.10) -> 1.12.2 Port

Reverse-engineering and port of Azanor's Thaumcraft 4.2.3.5 from Minecraft
1.7.10 to 1.12.2.

Tech stack: Java 8, MinecraftForge 1.12.2, Gradle, Baubles (hard dependency),
CodeChicken Lib (bundled in JAR).

All detailed docs in **`docs/PRD.md`**: architecture, source inventory, porting
phases, complexity assessment, risks, success criteria.

## Architectural Decisions (Closed)

See `docs/PRD.md §6`. All resolved:

| Decision | Choice |
|----------|--------|
| API packaging | **Separate mod** (`ThaumcraftAPI-1.12.2.jar`) |
| Rendering | **Hybrid**: JSON models + CCL TESR + OBJ |
| DepLoader | **Delete** (Baubles via CurseMaven) |
| Potion hack | **Remove** (use `RegistryEvent`) |
| Player data | **Capabilities** on `EntityPlayer` |
| Config | **Both**: `Configuration` now, `@Config` in Phase 10 |
| Packages | **Keep** `thaumcraft.common.*` / `thaumcraft.client.*` |
| Lang files | **`en_US` only** until strings stabilise |

## Workspace Overview

- `Thaumcraft-1.7.10-4.2.3.5.jar` -- original compiled JAR (942 classes)
- `thaumcraft_src/` -- unpacked JAR contents
- `Dockerfile` -- dev container (Java 8 + CFR + git + build tools)
- `docs/PRD.md` -- product requirements doc with phased porting plan
- `docs/REPAIR.md` -- decomposed repair plan for stub remediation (3r-6r)
- `AGENTS.md` -- this file (concise navigation, points to docs/PRD.md)
- `build.gradle` -- ForgeGradle 2.3, Forge 14.23.5.2847, Baubles via CurseMaven
- `gradlew` / `gradle/wrapper/` -- Gradle 4.10.3
- `src/main/java/` -- mod source (port output, ~530 Java source files)
- `.gradle_home/` -- Gradle/Forge cache (**excluded from git**)

## Dependencies

See `docs/PRD.md §3` for full dependency graph and versions.

| Dep | 1.7.10 | 1.12.2 |
|-----|--------|--------|
| **Baubles** | Hard (auto-downloaded) | Hard (CurseMaven) |
| **CodeChickenLib** | Bundled under `thaumcraft.codechicken.*` | Same (port bundled code) |

## Development Status

See `docs/PRD.md §4` for per-phase deliverables and `docs/PRD.md §5` for complexity.

| Phase | Scope | Status |
|-------|-------|--------|
| 0 | Forge MDK, Gradle, build chain | ✅ Done |
| 1 | API + CCL + TrueType | ✅ Done |
| 2 | Registration, config, networking, events | ✅ Done |
| 3 | Core systems (capabilities, wands, research, vis, potions, enchants) | ⚠️ **Stubs: WarpEvents missing, 27+ HIGH issues** |
| 4 | Blocks (71) + Tile Entities (80) | ⚠️ **Stubs: 45/61 TE empty, 6 Tube files missing (block metadata ✅)** |
| 5 | Items, Tools, Armor, Baubles, Relics (~110) | ⚠️ **Stubs: 10 foci, 5 relics, 4 baubles non-functional** |
| 6 | Entities, Mobs, Golems (~128 + 44 AI) | ⚠️ **Stubs: 8/44 AI return false, projectiles no damage (Combat ✅, Inventory ✅, Fluid/Pech ✅)** |
| 7 | World Gen (biomes, dimension, trees, structures) | ✅ Done |
| 7r | World Gen Remediation (room gens, village, persistence) | ✅ Done |
| 3r-6r | Remediation (see docs/REPAIR.md) | ❌ **~59 critical/high issues** |
| 8 | Client GUI + Rendering (~140 classes) | ❌ |
| 9 | Recipes + Research (~450 registrations) | ❌ |
| 10 | Polish (JEI, Config, Sound) | ❌ |

## Where To Look

Original source (for CFR decompilation reference):

| Path | Content | docs/PRD.md § |
|------|---------|----------|
| `thaumcraft_src/thaumcraft/api/` | Public API (67 classes) | §2 |
| `thaumcraft_src/thaumcraft/common/blocks/` | 71 block classes | §2, §4 |
| `thaumcraft_src/thaumcraft/common/tiles/` | 80 tile entity classes | §2, §4 |
| `thaumcraft_src/thaumcraft/common/items/` | All item/equipment/bauble classes | §2, §5 |
| `thaumcraft_src/thaumcraft/common/entities/` | All entity/AI classes | §2, §6 |
| `thaumcraft_src/thaumcraft/common/container/` | 26 container classes | §2 |
| `thaumcraft_src/thaumcraft/common/lib/` | World, research, network, events, crafting, potions, enchants | §2 |
| `thaumcraft_src/thaumcraft/common/lib/world/` | World gen, biomes, Eldritch dimension | §2, §7 |
| `thaumcraft_src/thaumcraft/common/config/` | Config classes | §2 |
| `thaumcraft_src/thaumcraft/codechicken/` | Bundled CCL (render, vec, lighting) | §2, §1 |
| `thaumcraft_src/thaumcraft/client/` | GUIs, renderers, models, FX, shaders | §2, §8 |
| `thaumcraft_src/truetyper/` | TrueType font renderer | §2, §1 |
| `thaumcraft_src/assets/thaumcraft/` | Textures, models, sounds, lang, shaders | §2 |

## Development Toolchain

All work inside Docker container (Java 8 required by Forge 1.12.2):

| Tool | Version | Purpose |
|------|---------|---------|
| `java`/`javac` | 1.8.0_482 (Temurin) | JDK |
| `cfr` | 0.152 | `.class` → `.java` decompiler |
| `rg` (ripgrep) | 14.1.0 | Fast code search |
| `python3` | 3.12 | Code generation scripts |

```bash
# Build
docker run --rm -v $(pwd):/workspace/thaumcraft \
  -v $(pwd)/.gradle_home:/home/ubuntu/.gradle \
  --user 1000:1000 --entrypoint ./gradlew thaumcraft-dev build

# Decompile a .class file
docker run --rm -v $(pwd):/workspace/thaumcraft thaumcraft-dev \
  -c 'cfr thaumcraft_src/thaumcraft/common/Thaumcraft.class'

# Find symbol in original source
docker run --rm -v $(pwd):/workspace/thaumcraft thaumcraft-dev \
  -c "rg -l 'class AspectList' thaumcraft_src/"

# Interactive session with X11
docker run --rm -it -v $(pwd):/workspace/thaumcraft \
  -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix thaumcraft-dev
```

### ForgeGradle Build Caching

`.gradle_home/` caches Forge/MCP/Gradle artifacts — mount as bind volume.
Use environment variable `GRADLE_USER_HOME` if needed inside container
(default: `/home/ubuntu/.gradle`).

## Development Practices

- **Decompile with CFR** for reference, port per-package
- **CFR first, then adapt**: Always decompile original `.class` before writing port
- **Keep original package names** (`thaumcraft.common.*` / `thaumcraft.client.*`)
- **Keep original field/method names** for traceability
- **BUILD SUCCESSFUL after every micro-step**
- **Commit after each micro-step** with conventional commit prefix
- **No ad-hoc regex fixes** for broken signatures — regenerate from raw source
- **Python generators** (`gen*.py`) for bulk class creation, deleted after use
- **No emoji in output**, GitHub-flavored markdown, monospace for CLI

### Automated Code Fixing Prohibitions

- **Never fix broken method signatures with ad‑hoc regex replacements.**
  If a transformed file contains a dangling `@Override` followed by a method
  body without its declaration, **stop immediately**. 
- **Required recovery:**
  1. Re-run `transform_1_7_10_to_1_12_2` on original raw source from `_p4_raw/`
  2. Apply only known, repeatable targeted fixes through the central porting utility
  3. If a file requires more than two standalone regex fixes, port manually
- **Audit past damage:** Files affected by `fix_compile_errors.py` must be
  regenerated from raw source before further development.

### Commit Style

```
<type>(<scope>): <description>

  Changes:
  - <bullet>
  - <bullet>
```

Types: `feat`, `fix`, `chore`, `docs`, `refactor`, `test`
