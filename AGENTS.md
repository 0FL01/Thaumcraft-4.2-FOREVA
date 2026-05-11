# Thaumcraft 4.2.3.5 (1.7.10) -> 1.12.2 Port

Reverse-engineering and port of Azanor's Thaumcraft 4.2.3.5 from Minecraft
1.7.10 to 1.12.2.

Tech stack: Java 8, MinecraftForge 1.12.2, Gradle, Baubles (hard dependency),
CodeChicken Lib (bundled in JAR).

## Workspace Overview

- `Thaumcraft-1.7.10-4.2.3.5.jar` -- original compiled JAR (942 classes)
- `thaumcraft_src/` -- unpacked JAR contents
- `Dockerfile` -- dev container (Java 8 + CFR + git + build tools)
- `.dockerignore` -- excludes heavy/derivable files from Docker builds
- `AGENTS.md` -- this file

## Development Toolchain

All development runs inside a Docker container with Java 8 (required by Forge
1.12.2) and all necessary tools:

| File | Description |
|------|-------------|
| `Dockerfile` | Based on `eclipse-temurin:8-jdk` (JDK 8u482, Ubuntu 24.04 Noble) |
| `.dockerignore` | Excludes `.class`, `.git`, JAR, IDE files from build context |

### Tools Inside the Container

| Tool | Version | Purpose |
|------|---------|---------|
| `java`/`javac` | 1.8.0_482 (Temurin) | Required by Forge 1.12.2 |
| `cfr` | 0.152 | `.class` → `.java` decompiler |
| `git` | 2.43.0 | Version control |
| `make`/`gcc`/`g++` | - | Native library compilation |
| `python3` | 3.12 | Scripting |
| `rg` (ripgrep) | 14.1.0 | Fast code search in source |
| `jq` | 1.7 | JSON processing |
| `gradlew` | guard script | Reminds to generate wrapper if missing |

Also includes OpenGL (libGL/libGLU/libGLX/Mesa DRI), X11, ALSA and OpenAL
libraries — enough for `gradlew runClient` with X11 forwarding.

### Typical Usage

```bash
# Interactive session (mount project, X11 forwarding for client testing)
docker run --rm -it \
  -v $(pwd):/workspace/thaumcraft \
  -e DISPLAY=$DISPLAY \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  thaumcraft-dev

# Build the mod
docker run --rm -v $(pwd):/workspace/thaumcraft thaumcraft-dev -c './gradlew build'

# Decompile a .class file
docker run --rm -v $(pwd):/workspace/thaumcraft thaumcraft-dev \
  -c 'cfr thaumcraft_src/thaumcraft/common/Thaumcraft.class'

# Find which class defines a symbol
docker run --rm -v $(pwd):/workspace/thaumcraft thaumcraft-dev \
  -c "rg -l 'class AspectList' thaumcraft_src/"
```

## Dependencies (Confirmed)

### For 1.7.10 (original)

| Dependency | Type | Notes |
|-----------|------|-------|
| **Baubles** 1.0.1.10 | Hard dependency (auto-downloaded) | Baubles API is used for rings/amulets/girdles. Azanor's own mod. Auto-downloaded by `DepLoader` (FMLCorePlugin) at launch. |
| **CodeChickenLib** | **Bundled** (NOT external) | Complete repackaged copy under `thaumcraft.codechicken.*`. Includes vec math, render pipeline, lighting, colour, raytracer. No external CCL mod required. |

No other dependencies. The bundled `DepLoader` (in `META-INF/MANIFEST.MF` as `FMLCorePlugin`) exists solely to auto-download Baubles.

### For 1.12.2 (port target)

| Dependency | Type | Available Version | Notes |
|-----------|------|-------------------|-------|
| **Baubles** | Hard dependency | `Baubles-1.12-1.5.2` (CurseForge ID 227083, file 2518667) | Official Azanor release. Must be listed in `build.gradle` via CurseMaven. |
| **CodeChickenLib** | Bundled (port bundled code) | N/A -- port the existing `thaumcraft.codechicken.*` classes to 1.12.2 Forge rendering | The bundled render pipeline (CCRenderState, CCModel, Vertex5) must be adapted to 1.12.2's model system, or replaced with vanilla Forge `IBakedModel`/`TESR` equivalents. |

Confirmed sources: `mcmod.info` (dependencies: `["Baubles"]`), `dependancies.info` (Baubles 1.0.1.10 auto-download URL), `META-INF/MANIFEST.MF` (FMLCorePlugin: DepLoader), Thaumcraft 6 (1.12.2) official source code build.gradle (single dependency: `curse.maven:baubles-227083:2518667`).

## Where To Look

| Path | Why |
|------|-----|
| `thaumcraft_src/thaumcraft/common/Thaumcraft.class` | Main mod entry point |
| `thaumcraft_src/thaumcraft/common/CommonProxy.class` | Server proxy (registration, network, config) |
| `thaumcraft_src/thaumcraft/client/ClientProxy.class` | Client proxy (renderers, GUI, sounds) |
| `thaumcraft_src/thaumcraft/api/` | Public API (67 classes) -- port as standalone API mod |
| `thaumcraft_src/thaumcraft/common/blocks/` | 71 block classes |
| `thaumcraft_src/thaumcraft/common/tiles/` | 80 tile entity classes |
| `thaumcraft_src/thaumcraft/common/items/` | All item classes |
| `thaumcraft_src/thaumcraft/common/entities/` | All entity/entity-AI classes |
| `thaumcraft_src/thaumcraft/common/lib/world/` | World gen, biomes, Eldritch dimension |
| `thaumcraft_src/thaumcraft/common/lib/research/` | Research system (hex grid, scanning) |
| `thaumcraft_src/thaumcraft/common/lib/network/` | Packet-based networking |
| `thaumcraft_src/thaumcraft/codechicken/` | Bundled CodeChicken Lib (render, vec, lighting) -- NOT an external dep |
| `thaumcraft_src/assets/thaumcraft/` | Textures, models, sounds, lang, shaders |
| `thaumcraft_src/truetyper/` | TrueType font renderer |
| `META-INF/MANIFEST.MF` | FMLCorePlugin: `DepLoader` (auto-download deps) |

## Architectural Invariants

- **API-first design**: `thaumcraft.api` is a standalone set of interfaces/classes
  that other mods depend on. Must be ported separately (or as a thin API jar).
- **Aspect system**: Every item/block/entity has an `AspectList`. Aspects are
  the core crafting currency. Smoothstone-tier aspects (primal: Aer, Terra,
  Ignis, Aqua, Ordo, Perditio) compose into compound aspects.
- **Vis network**: A world-wide graph of `TileVisNode` tiles connected via
  `VisNetHandler`. Chunk-based vis regeneration. Wands drain vis from nodes.
- **Wand system**: `ItemWandCasting` + caps/rods/staffs. Foci plug into
  `ItemFocusBasic`. Wand capabilities stored in NBT (Vis, cap, rod).
- **Research system**: Hex-grid research map. Players unlock `ResearchItem`
  entries. Scanning (`ScanManager`) discovers aspects on entities/items/
  phenomena. Research is synced via packets.
- **Infusion crafting**: Multi-block ritual: `TileInfusionMatrix` + pedestals +
  pillars. Instability mechanics. Runic augment recipes.
- **Aura/Taint pollution**: Flux accumulates in chunks, spawns taint biomes
  and taint mobs. `EventHandlerWorld` manages flux effects.
- **Golems**: Autonomous `EntityGolemBase` with pluggable AI cores
  (gather/harvest/fish/guard etc.) and upgrades.
- **Eldritch dimension**: `WorldProviderOuter` -- maze-based dimension with
  cell-maze generator (`MazeGenerator`), bosses, and the eldritch tab.
- **Network**: Simple packet-based system (`PacketHandler`) using FML's
  `SimpleNetworkWrapper`. Separate packets for FX, player data, and server
  commands.
- **Bundled dependencies**: CodeChicken Lib classes are repackaged under
  `thaumcraft.codechicken.*`. DepLoader auto-downloads Baubles at launch.
  Port must handle both API and rendering dependencies.
- **No external CCL**: Unlike most 1.7.10 mods, Thaumcraft does NOT require
  an external CodeChickenLib mod. Everything is bundled in its JAR.
- **Custom shaders**: GLSL post-processing shaders for blur, desaturation,
  bloom, and color-convolution effects.

## Package Map

### API (`thaumcraft.api`) -- port first, as independent module

| Package | Classes | Content |
|---------|---------|---------|
| `api` | 15 | `ThaumcraftApi`, `ThaumcraftApiHelper`, `ItemApi`, interfaces (runic, warp, vis discount, scribe, architect, goggles, repairable) |
| `api.aspects` | 7 | `Aspect`, `AspectList`, containers/transport interfaces |
| `api.crafting` | 7 | `ShapedArcaneRecipe`, `InfusionRecipe`, `CrucibleRecipe`, stabiliser |
| `api.wands` | 8 | `WandCap`, `WandRod`, `StaffRod`, `FocusUpgradeType`, `ItemFocusBasic`, `IWandFocus` |
| `api.research` | 6 | `ResearchItem`, `ResearchPage`, `ResearchCategories`, `ScanResult`, `IScanEventHandler` |
| `api.nodes` | 4 | `INode`, `NodeType`, `NodeModifier`, `IRevealer` |
| `api.internal` | 3 | `IInternalMethodHandler`, `DummyInternalMethodHandler`, `WeightedRandomLoot` |
| `api.visnet` | 2 | `VisNetHandler`, `TileVisNode` |
| `api.potions` | 2 | `PotionFluxTaint`, `PotionVisExhaust` |

### Common (server/serialization logic)

| Package | Count | Content |
|---------|-------|---------|
| `common.tiles` | 80 | All `TileEntity` subclasses (infusion, crucible, jars, mirrors, tubes, nodes, workbench, bore, lamps, etc.) |
| `common.blocks` | 71 | `Block` subclasses with ItemBlock variants |
| `common.entities.monster` | 30 | Taint mobs, cultists, wisps, pech, eldritch guardians, etc. |
| `common.entities` | 11 | `EntityAspectOrb`, `EntityFollowingItem`, `EntityItemGrate` |
| `common.entities.golems` | 16 | `EntityGolemBase`, `EntityTravelingTrunk`, `GolemHelper`, `Marker` |
| `common.entities.projectile` | 12 | `EntityAlumentum`, `EntityPrimalArrow`, `EntityFrostShard`, `EntityDart` |
| `common.entities.monster.boss` | 6 | `EntityCultistLeader`, `EntityEldritchGolem`, `EntityEldritchWarden` |
| `common.entities.monster.mods` | 16 | Champion modifier system (armored, vampiric, warded, etc.) |
| `common.entities.ai.*` | ~40 | Golem/combat/fluid/inventory/interact AI tasks |
| `common.container` | 26 | Container classes for all GUIs |
| `common.items` | 22 | `ItemResource`, `ItemShard`, `ItemEssence`, `ItemCrystalEssence` etc. |
| `common.items.armor` | 15 | Thaumium, Void, Fortress, Cultist, robes, goggles, boots |
| `common.items.equipment` | 19 | Elemental/Thaumium/Void tools and swords |
| `common.items.baubles` | 6 | Runic rings/amulets/girdles, hover girdle |
| `common.items.wands` | 8 | `ItemWandCasting`, `ItemWandCap`, `ItemWandRod`, `WandManager` |
| `common.items.wands.foci` | 10 | Focuses: shock, fire, frost, excavation, primal, warding, hellbat, pech, trade, portable hole |
| `common.items.relics` | 5 | Thaumometer, Thaumonomicon, Hand Mirror, Resonator, Sanity Checker |
| `common.config` | 9 | Config classes, materials (Airy, Taint) |
| `common.lib` | 13 | `InternalMethodHandler`, `CreativeTabThaumcraft`, `FakeThaumcraftPlayer`, `WarpEvents`, networking, refs |
| `common.lib.events` | 10 | `EventHandlerEntity`, `EventHandlerWorld`, `ServerTickEventsFML`, `EventHandlerRunic`, `KeyHandler` |
| `common.lib.network` | ~30 | Packet classes for FX, player data sync, server commands |
| `common.lib.research` | 5 | `ResearchManager`, `PlayerKnowledge`, `ScanManager` |
| `common.lib.world` | 15 | World generators, trees, structures |
| `common.lib.world.biomes` | 5 | Magical Forest, Taint, Eerie, Eldritch biomes + `BiomeHandler` |
| `common.lib.world.dim` | 19 | Eldritch Outer Lands dimension (maze gen, chunk provider, rooms) |
| `common.lib.crafting` | 6 | `ThaumcraftCraftingManager`, recipes (wand, sceptre, runic, NBT) |
| `common.lib.potions` | 7 | Custom potions (Blurred Vision, Death Gaze, Thaumarhia, etc.) |
| `common.lib.enchantment` | 5 | Frugal, Haste, Potency, Repair, Wand Fortune |

### Client (rendering + GUI)

| Package | Count | Content |
|---------|-------|---------|
| `client.gui` | 20+ | All GUI classes (research, workbench, infusion, golem, etc.) |
| `client.renderers.block` | 23 | Custom block renderers (candles, jars, taint, tubes, etc.) |
| `client.renderers.tile` | 52 | Tile entity renderers (nodes, crucible, mirrors, etc.) |
| `client.renderers.entity` | 42 | Entity renderers (all mobs, golems, projectiles) |
| `client.renderers.models` | 27 + 6 gear + 15 entity | Model classes and sub-models |
| `client.renderers.item` | 5 | Item renderers (wand, thaumometer, banner, bow, trunk) |
| `client.fx.particles` | 23 | Custom particle FX classes (wisp, sparkle, burst, bubble, etc.) |
| `client.fx.beams` | 6 | Beam rendering (wand, bore, power, golem boss) |
| `client.fx.bolt` | 5 | Lightning bolt rendering |
| `client.lib` | 9 | `UtilsFX`, `PlayerNotifications`, `TCFontRenderer`, `RenderEventHandler`, tooltips |

### Bundled CodeChicken Lib (`thaumcraft.codechicken.*`)

| Package | Content |
|---------|---------|
| `codechicken.core.launch` | `DepLoader` -- auto-downloads Baubles at startup (remove/adapt for 1.12.2) |
| `codechicken.lib.vec` | `Vector3`, `Matrix4`, `Quat`, `Rotation`, `Transformation`, `Cuboid6` |
| `codechicken.lib.render` | `CCRenderState`, `CCModel`, `Vertex5` -- custom rendering pipeline |
| `codechicken.lib.lighting` | `LightMatrix`, `LightModel` |
| `codechicken.lib.math` | `MathHelper` |
| `codechicken.lib.colour` | `ColourRGBA`, `ColourARGB` |
| `codechicken.lib.raytracer` | `RayTracer`, `ExtendedMOP` |

### TrueType Font Renderer

| Package | Content |
|---------|---------|
| `truetyper` | `TrueTypeFont`, `FontLoader`, `FontHelper`, `Formatter` |

## Porting Strategy

### Phase 1: Foundation
- Set up Forge 1.12.2 MDK workspace (Gradle)
- Port `thaumcraft.api` to a standalone mod jar (or submodule)
- Port `thaumcraft.codechicken.*` -- remove DepLoader (not needed in 1.12.2),
  adapt rendering pipeline to Forge's new model system
- Port `truetyper` font renderer

### Phase 2: Core Systems
- Aspect system (`api.aspects.Aspect`, `AspectList`, registration)
- Vis network (`VisNetHandler`, `TileVisNode`) -- adapt for 1.12.2 chunk system
- Research system (`ResearchManager`, `ScanManager`, hex grid)
- Wand system (`ItemWandCasting`, caps, rods, foci)
- Infusion crafting and arcane crafting

### Phase 3: Content
- Blocks and TileEntities (port each block class)
- Items, tools, armor, baubles
- Entities and mobs (adapt AI system to 1.12.2)
- World generation (biomes, trees, structures)
- Eldritch dimension (world provider, maze generator)

### Phase 4: Client
- GUIs (adapt to 1.12.2 GUI system)
- Renderers (TESR, entity renderers, model system)
- FX and particles (use 1.12.2 particle system)
- Shaders (adapt post-processing to 1.12.2)

### Phase 5: Polish
- JEI integration for crafting recipes
- Baubles API integration (Baubles is available for 1.12.2)
- Config GUI
- Final testing and balancing

## 1.7.10 -> 1.12.2 Mapping Notes

| 1.7.10 | 1.12.2 | Notes |
|--------|--------|-------|
| `IIconRegister` / `Icon` | `ModelLoader.setCustomModelResourceLocation` / `IBakedModel` | Model system completely replaced |
| `RenderBlocks` | `ISmartBlockModel` / `BakedQuad` | Custom block rendering must be rewritten |
| `SimpleNetworkWrapper` (FML) | Same API (still works) | Network code ports cleanly |
| `EntityAI` / `Path` | Same base, some method renames | AI mostly compatible |
| `WorldGenerator` | Same base interface | Compatible |
| `CraftingManager` | `IRecipe` / `RecipeRegistry` | Recipe system changed |
| `ItemStack` constructors | Deprecated; use `new ItemStack(Item, int, int)` -> `new ItemStack(IItemProvider, int)` | Method signature changes |
| `Block` constructor | `Material` + `MapColor` | Minor API change |
| `@SideOnly(Side.CLIENT)` | Removed; use dist executor pattern | Different approach |
| `ChunkCoordinates` | `BlockPos` | Coordinate system centralized |
| `NBTTagCompound` | Same | Compatible |
| `EntityPlayer` `addStat` | `EntityPlayer` `addStat` | Same |
| `ICommand` | Same | Compatible |
| `TextureMap` | `TextureAtlasSprite` | Rendering system changed |
| `OpenGL` matrix push/pop | `GlStateManager` | Slightly different method names |
| `FontRenderer` | Same base | `TrueTypeFont` may need adaptation |
| Potion IDs (0-31) | Registry-based | Must use `PotionType` registry |
| Biome IDs | Registry-based | Must use `Biome` registry |

## Development Practices

- Decompile `.class` files with CFR/Procyon for reference implementation
- Port per-package, keeping original package names for API; rename internals
  to `thaumcraft.*` (drop the `common`/`client` prefix convention vs flatten)
- Keep original field/method names where possible for traceability
- Add `@Override` annotations when overriding Forge methods
- Use `EnumHelper` / `ObfuscationReflectionHelper` for Forge internals if needed

## Commit Style

Full commit messages with conventional commit prefix:

<type>(<scope>): <description>

- blank line
- indented body with `Changes:` and 2-4 concrete bullets

Types: `feat`, `fix`, `chore`, `docs`, `refactor`, `test`
