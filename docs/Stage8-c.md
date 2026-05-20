# Stage 8-c — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 8-c закрывает только клиентский рендер блоков и tile entity через Tile Entity Special Renderers / TESR, связанные с ними model-классы, регистрацию рендеров и ресурсы, необходимые для визуального паритета Thaumcraft 4.2.3.5 на Forge 1.12.2.

Цель не включает GUI, entity renderers, частицы/шейдеры как самостоятельную фазу или контент/рецепты Stage 9. Частицы/beam упоминаются только там, где TESR напрямую вызывает визуальный эффект или зависит от helper-класса.

Основание scope: `docs/PRD.md:117-138` относит tile entity special renderers, block/item renderer registration и model classes к клиентскому слою; `docs/PRD.md:140-158` требует валидные ресурсы; `docs/PRD.md:531-539` требует runtime-проверку tile renderers и missing texture/model ошибок.

## 2. Scope фазы

В scope Stage 8-c входят:

- TESR-регистрация в клиентском proxy/регистрационном коде Forge 1.12.2.
- TESR-классы для jar/brain jar/node jar, alembic, crucible, infusion matrix, pedestals, pillars, arcane bore/base, thaumatorium, focal manipulator, nodes/energized nodes/node stabilizer/converter/vis relay, banners/labels/seals where tile/block-render dependent, portable hole, warded blocks.
- TESR или tile/block render для рабочих устройств, которые в 1.7.10 были tile-rendered и присутствуют в текущих tile registrations: arcane workbench, research/deconstruction/table visuals, bellows, tubes, mirrors, reservoirs, mana pod, ethereal bloom, arcane lamps, flux scrubber, crystals, eldritch portal/obelisk/lock/nothing/cap/crab spawner.
- Block/item model registration and baked model/resource mapping only where required for tile/block visual parity.
- Required resources under `src/main/resources/assets/thaumcraft/**`: blockstates, `models/block`, TESR model textures/OBJ/MTL, relevant item models for block items whose inventory form must use the same visual renderer.
- Smoke/manual scenarios: placing/loading all tile-rendered blocks in a world, checking missing model/texture logs, opening chunks containing active/filled states, verifying no client crash.

Out of scope:

- Entity renderer parity except item renderers directly tied to tile visuals, such as reference `ItemJarFilledRenderer`, `ItemJarNodeRenderer`, `ItemNodeRenderer`.
- GUI screens, except noting dependencies when a tile visually displays an item/state set through a GUI.
- Standalone particles/shaders, except direct TESR dependencies such as node/halo/rift visuals.
- Stage 9 recipes/research/content registration, except as dependency for manual placement/acquisition scenarios.

## 3. Источники сравнения

Current source and docs:

- `docs/PRD.md:117-138` — client-layer responsibilities include item/block renderer registration, TESR, model classes, particles/beams/shaders.
- `docs/PRD.md:140-158` — resource layer and rule to add missing resources.
- `docs/PRD.md:531-539` — client smoke checklist for tile renderers and missing texture/model errors.
- `src/main/java/thaumcraft/client/ClientProxy.java:24-34` — current display/model registration only assigns one inventory `ModelResourceLocation` to every item meta.
- `src/main/java/thaumcraft/client/ClientProxy.java:36-44` — no TESR/block-render registration, only keybinding placeholder and one event handler.
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:293-390` — current registered tile entities that need client render coverage.
- `src/main/java/thaumcraft/common/blocks/BlockJar.java:70-73` — jar block is `ENTITYBLOCK_ANIMATED`, so missing TESR makes it visually non-parity-critical and likely invisible/broken.
- `src/main/java/thaumcraft/common/blocks/BlockStoneDevice.java:59-81` — stone device metas create alchemy furnace, pedestal, infusion matrix/pillar, wand pedestal, node stabilizer/converter, spa, focal manipulator, flux scrubber.
- `src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java:50-68` — metal device metas create crucible, alembic, charger, grates, lamps, thaumatorium/top, vis relay.
- `src/main/java/thaumcraft/common/blocks/BlockWoodenDevice.java:41-55` — wooden device metas create bellows, sensor, pressure plate, arcane bore/base, banner.
- `src/main/resources/assets/thaumcraft/` — current resource tree has no `blockstates/`, no `models/block/`, and only two files under `textures/models/`.

Reference source/material:

- `thaumcraft_src/thaumcraft/client/ClientProxy.class` — decompiled/disassembled reference contains `registerDisplayInformation()`, `setupItemRenderers()`, `setupBlockRenderers()`, `setupTileRenderers()`, `registerTileEntitySpecialRenderer()`, `registerBlockRenderer()`.
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileJarRenderer.class` — jar, brain jar, node jar liquid/brain/node visuals.
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileAlembicRenderer.class`, `TileCrucibleRenderer.class`, `TileThaumatoriumRenderer.class` — alchemy device TESR.
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileRunicMatrixRenderer.class`, `TilePedestalRenderer.class`, `TileInfusionPillarRenderer.class`, `TileWandPedestalRenderer.class` — infusion visuals.
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileArcaneBoreRenderer.class`, `TileArcaneBoreBaseRenderer.class` — arcane bore visuals.
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileNodeRenderer.class`, `TileNodeEnergizedRenderer.class`, `TileNodeStabilizerRenderer.class`, `TileNodeConverterRenderer.class`, `TileVisRelayRenderer.class` — node/aura device visuals.
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileHoleRenderer.class`, `TileWardedRenderer.class` — portable hole and warding visual wrappers.
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileBannerRenderer.class` — banner visual.
- `thaumcraft_src/thaumcraft/client/renderers/tile/ItemJarFilledRenderer.class`, `ItemJarNodeRenderer.class`, `ItemNodeRenderer.class` — item renderers directly coupled to tile visual parity.
- `thaumcraft_src/assets/thaumcraft/textures/models/**` and `thaumcraft_src/assets/thaumcraft/textures/blocks/**` — original asset source of truth per `AGENTS.md:14-16`.

Lightweight analysis commands run:

- `git status --short` — showed unrelated untracked `docs/Stage8-a.md` and `docs/Stage8-b.md`; `docs/Stage8-c.md` was absent before this document.
- `find src/main/java/thaumcraft/client -type f | sort` — confirmed current client tree only has `ClientProxy.java` and three GUI classes plus `GuiFactory`.
- `find thaumcraft_src/thaumcraft/client/renderers/tile -maxdepth 1 -type f | sort` — listed reference tile/item renderer class files.
- `javap -classpath thaumcraft_src -p -c thaumcraft.client.ClientProxy` — inspected reference renderer registration structure.
- `javap -classpath thaumcraft_src -p thaumcraft.client.renderers.tile.<Renderer>` — inspected reference TESR APIs for key renderers.
- `find src/main/resources/assets/thaumcraft -type f \( -path '*/blockstates/*' -o -path '*/models/block/*' -o -path '*/textures/blocks/*' -o -path '*/textures/models/*' \)` — confirmed current tile/block visual resources are effectively absent except `textures/models/moneychanger.png` and `textures/models/wizard.png`.

## 4. Текущее состояние Stage 8-c

Текущее состояние: Stage 8-c не реализован и не может считаться завершенным.

Основные факты:

- `src/main/java/thaumcraft/client/ClientProxy.java:24-34` регистрирует только generic inventory model для всех items/metas через `ModelLoader.setCustomModelResourceLocation`; это не заменяет TESR, blockstates или baked block models.
- `src/main/java/thaumcraft/client/ClientProxy.java:36-44` не вызывает `ClientRegistry.bindTileEntitySpecialRenderer`, не имеет аналога reference `setupTileRenderers()`, не имеет block renderer/model registration для tile/block visuals.
- `src/main/java/thaumcraft/client/renderers/**` отсутствует полностью; current tree содержит только `src/main/java/thaumcraft/client/ClientProxy.java` и GUI classes.
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:323-390` регистрирует десятки tile entities, включая большинство reference TESR targets, но без клиентских render bindings.
- `src/main/java/thaumcraft/common/blocks/BlockJar.java:70-73` прямо требует `ENTITYBLOCK_ANIMATED`, однако current `TileJarRenderer` отсутствует.
- `src/main/resources/assets/thaumcraft/blockstates/` отсутствует; `src/main/resources/assets/thaumcraft/models/block/` отсутствует; `src/main/resources/assets/thaumcraft/textures/blocks/` отсутствует; `src/main/resources/assets/thaumcraft/textures/models/` содержит только `moneychanger.png` и `wizard.png`, тогда как reference has 478 block/model texture files under the comparable source paths.
- `src/main/java/thaumcraft/common/tiles/TileCrucible.java:195-200` содержит Phase 8 placeholder for client-side crucible effects; это не самостоятельная particle gap, но визуальный TESR crucible state still depends on liquid/aspect/heat rendering.
- `src/main/java/thaumcraft/common/tiles/TileBanner.java:1-3` is only an empty tile marker; all banner orientation/model/texture visual behavior must come from renderer/block/item state or is currently absent.

Reference registration summary from `thaumcraft_src/thaumcraft/client/ClientProxy.class`:

- `registerDisplayInformation()` calls `setupItemRenderers()`, `setupEntityRenderers()`, `setupBlockRenderers()`, `setupTileRenderers()`.
- `setupTileRenderers()` binds reference TESRs for alembic, arcane bore/base/lamp/workbench, banner, bellows, centrifuge, chest hungry, crucible, crystals, deconstruction table, eldritch tiles, essentia reservoir/crystalizer, ethereal bloom, hole, infusion matrix/pillar, jar, workbench charger, mana pod, mirror/mirror essentia, nodes, pedestals, research/table/thaumatorium, tube buffer/oneway/valve, vis relay, warded, focal manipulator, advanced alchemy furnace, flux scrubber.
- `setupBlockRenderers()` assigns custom render IDs and handlers for gas, arcane furnace, metal/stone/wooden devices, taint, cosmetic opaque, tubes, taint fibres, jars, ores, hungry chest, tables, candles, lifter, crystals, warded, eldritch, reservoirs, loot urn/crate.

## 5. Gap list

### GAP-1: Нет системы регистрации TESR и block/tile visual renderers

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/client/ClientProxy.java:24-34`
- `src/main/java/thaumcraft/client/ClientProxy.java:36-44`
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:293-390`

**Референс:**
- `thaumcraft_src/thaumcraft/client/ClientProxy.class`

**Что не совпадает:**

В reference `ClientProxy.registerDisplayInformation()` вызывает отдельные setup paths для item, entity, block и tile renderers. Для Stage 8-c важны `setupTileRenderers()` и tile/block-related pieces of `setupItemRenderers()`/`setupBlockRenderers()`. Current `ClientProxy.registerDisplayInformation()` только назначает generic inventory model на все item metas и не выполняет ни одной TESR binding. При этом common layer уже регистрирует tile entities в `ConfigBlocks.java:323-390`, поэтому клиент будет загружать tiles без соответствующих special renderers.

**Что нужно доделать:**

Добавить Forge 1.12.2-compatible registration path for tile/block visual renderers. Для TESR это должен быть `ClientRegistry.bindTileEntitySpecialRenderer(TileClass.class, new Renderer())` или equivalent central helper, вызываемый из client lifecycle at the same point as model registration.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Add a scoped client registration method in `src/main/java/thaumcraft/client/ClientProxy.java`, for example `setupTileRenderers()` called from `registerDisplayInformation()` or appropriate client model/render event.
- Register every Stage 8-c TESR target listed in `ConfigBlocks.java:323-390` that has reference TESR coverage.
- Add item renderer/model registration only for tile visual inventory parity: filled jar, node jar, airy/node item, banner/block item variants, and any block item that cannot be represented by JSON.
- Keep entity renderer setup out of this chunk.
- Verify no client-only class is referenced from common/server code paths per `docs/PRD.md:133-138`.

**Критерии приемки:**
- [ ] `ClientProxy.registerDisplayInformation()` or equivalent client lifecycle invokes Stage 8-c TESR registration.
- [ ] Every registered current tile entity with reference Stage 8-c TESR has a corresponding `ClientRegistry.bindTileEntitySpecialRenderer` or documented no-TESR JSON replacement.
- [ ] Server run does not load client-only renderer classes.

**Риски / зависимости:**

Dependency: Forge 1.12.2 rendering lifecycle must be used correctly. Some reference block renderers were 1.7.10 `ISimpleBlockRenderingHandler`; these need either baked model/JSON replacement or Forge 1.12 custom model path, not a direct API copy.

### GAP-2: Отсутствует весь client renderer/model package для TESR

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/client/ClientProxy.java:1-106`
- `src/main/java/thaumcraft/client/renderers/` absent

**Референс:**
- `thaumcraft_src/thaumcraft/client/renderers/tile/*.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/*.class`

**Что не совпадает:**

Current source tree has no `thaumcraft.client.renderers.tile`, no `thaumcraft.client.renderers.block`, and no TESR model classes. Reference Stage 8-c uses many renderer/model classes, including `TileJarRenderer`, `TileAlembicRenderer`, `TileCrucibleRenderer`, `TileRunicMatrixRenderer`, `TileArcaneBoreRenderer`, `TileThaumatoriumRenderer`, `TileFocalManipulatorRenderer`, `TileNodeRenderer`, `TileHoleRenderer`, `TileWardedRenderer`, `TileBannerRenderer`, plus model classes such as `ModelJar`, `ModelBrain`, `ModelBore`, `ModelBoreBase`, `ModelBoreEmit`, `ModelCube`, `ModelArcaneWorkbench`, `ModelBanner`.

### Checkpoint 2026-05-16 — magical leaves model/render baseline

Статус: частично продвинут.

Что сделано:

- Revalidated existing magical leaves baseline from commit `e210acd`:
  - `BlockMagicalLeaves#getRenderLayer() -> CUTOUT_MIPPED`
  - `BlockMagicalLeavesItem#getMetadata() -> damage & 1`
  - presence of `blockmagicalleaves` blockstate/model files and greatwood/silverwood leaf textures.
- Добавлен `BlockMagicalLeavesRenderContractTest`, фиксирующий эти render-layer/metadata contracts и наличие required asset paths для защиты от regressions.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Этот checkpoint добавляет coverage guard и не закрывает основной TESR backlog Stage 8-c (jar/crucible/alembic/infusion/nodes/etc.).

**Что нужно доделать:**

Port or reimplement the renderer/model classes required by Stage 8-c, preserving original visual behavior where feasible and adapting OpenGL/Tessellator/model APIs to 1.12.2.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Create `src/main/java/thaumcraft/client/renderers/tile/` for TESR classes.
- Create `src/main/java/thaumcraft/client/renderers/models/` for model classes used by TESR.
- For each class, inspect reference bytecode/source first; use `javap`/CFR against `thaumcraft_src` or `Thaumcraft-1.7.10-4.2.3.5.jar`.
- Use 1.12.2 `TileEntitySpecialRenderer<T>` generic render method and `GlStateManager`/`Tessellator`/`BufferBuilder` equivalents.
- Avoid porting entity-only renderer classes in this chunk.

**Критерии приемки:**
- [ ] `src/main/java/thaumcraft/client/renderers/tile/` exists and contains Stage 8-c renderer classes.
- [ ] All renderer classes compile against Forge 1.12.2 APIs without raw 1.7.10 renderer API calls.
- [ ] Model classes required by these TESRs exist and are used by renderers.

**Риски / зависимости:**

High risk: direct decompile-to-port can pull obsolete 1.7.10 names such as `IIcon`, `RenderBlocks`, `IModelCustom`, `ISimpleBlockRenderingHandler`. These must be adapted, not copied blindly.

### GAP-3: Jar, brain jar, node jar, labels/filter visuals and jar item renderers are absent

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/blocks/BlockJar.java:70-73`
- `src/main/java/thaumcraft/common/tiles/TileJarFillable.java:19-25`
- `src/main/java/thaumcraft/common/tiles/TileJarFillable.java:33-49`
- `src/main/java/thaumcraft/common/tiles/TileJarFillable.java:130-132`

**Референс:**
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileJarRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/ItemJarFilledRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/ItemJarNodeRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/ModelJar.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/ModelBrain.class`
- `thaumcraft_src/assets/thaumcraft/textures/models/jar*.png` and related jar/label assets if present in the original asset tree

**Что не совпадает:**

`BlockJar` uses `EnumBlockRenderType.ENTITYBLOCK_ANIMATED`, so its world render depends on a TESR. Current has tile state for aspect, amount, filter, facing and lid, but no renderer consumes it. Reference `TileJarRenderer` exposes `renderLiquid(TileJarFillable, ...)` and `renderBrain(TileJarBrain, ...)`, and also delegates node jar rendering via `TileNodeRenderer`. The current resource tree lacks jar model textures under `src/main/resources/assets/thaumcraft/textures/models/` and has no item renderers for filled/node jar inventory parity.

**Что нужно доделать:**

Port jar TESR and direct item renderers so filled jars show glass, liquid color/height, labels/aspect filter, brain jar contents, and node jar node visuals in-world and in inventory.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Add `TileJarRenderer`, `ItemJarFilledRenderer`, `ItemJarNodeRenderer`, `ModelJar`, `ModelBrain` or 1.12-compatible replacements.
- Register TESR for `TileJar` or all jar subclasses registered at `ConfigBlocks.java:323-327`; acceptance must cover `TileJarFillable`, `TileJarBrain`, `TileJarNode`, `TileJarFillableVoid`.
- Register item rendering/model mapping for filled jar and node jar item forms if JSON cannot express dynamic liquid/node state.
- Copy original jar/brain/label/aspect-related model textures from `thaumcraft_src/assets/thaumcraft/textures/models/` and `textures/blocks/` as needed.
- Smoke scenario: place empty jar, filled jar, void jar, brain jar, node jar; set `aspect`, `amount`, `aspectFilter`, `facing`, and verify correct visuals/no missing textures.

**Критерии приемки:**
- [ ] Jar world render appears for all jar metas and does not disappear as `ENTITYBLOCK_ANIMATED`.
- [ ] Filled jar liquid color/height tracks `TileJarFillable.aspect` and `TileJarFillable.amount`.
- [ ] Brain jar and node jar render their special contents in world and item forms.

**Риски / зависимости:**

Dependency: aspect texture lookup must be available. If aspect icons are handled by Stage 8-d/e, document the dependency but still provide graceful fallback/no crash in TESR.

### GAP-4: Crucible and alembic alchemy TESR visuals are absent or stub-dependent

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java:57-60`
- `src/main/java/thaumcraft/common/tiles/TileCrucible.java:37-49`
- `src/main/java/thaumcraft/common/tiles/TileCrucible.java:195-200`
- `src/main/java/thaumcraft/common/tiles/TileCrucible.java:222-228`
- `src/main/java/thaumcraft/common/tiles/TileAlembic.java:17-25`
- `src/main/java/thaumcraft/common/tiles/TileAlembic.java:198-210`

**Референс:**
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileCrucibleRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileAlembicRenderer.class`
- `thaumcraft_src/assets/thaumcraft/textures/models/alembic.obj` or equivalent alembic model assets if present
- `thaumcraft_src/assets/thaumcraft/textures/blocks/al_furnace_*.png`

**Что не совпадает:**

Reference `TileCrucibleRenderer` has `renderEntityAt()` and `renderFluid()`. Current `TileCrucible` tracks heat, aspects and fluid height, but no TESR renders fluid/aspect color or heated contents. Current `TileCrucible.java:195-200` leaves client-side effects as Phase 8 placeholder, which means active visuals are incomplete even if a static block model exists. Reference `TileAlembicRenderer` loads a custom model and uses alembic state; current `TileAlembic` tracks aspect, amount, facing, `aboveAlembic`, and `aboveFurnace`, but `update()` is empty and no renderer calls `getAppearance()`.

**Что нужно доделать:**

Port crucible and alembic TESR with liquid/aspect state, orientation, stack appearance, and required resources.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Add `TileCrucibleRenderer` using `TileCrucible.getFluidHeight()`, `heat`, `aspects`, and water/aspect coloring.
- Add `TileAlembicRenderer` using `TileAlembic.facing`, `aspect`, `amount`, `aboveAlembic`, `aboveFurnace`; ensure appearance is recalculated/synced safely.
- Register renderers for `TileCrucible` and `TileAlembic`.
- Copy needed alchemy model textures/OBJ/MTL from `thaumcraft_src/assets/thaumcraft/textures/models/` and block textures from `textures/blocks/`.
- Smoke scenario: place crucible empty/water/heated/aspect-filled; stack alembics above furnace/alembic and rotate with wand.

**Критерии приемки:**
- [ ] Crucible renders fluid surface and aspect/heat color for filled states.
- [ ] Alembic renders model orientation and visible essentia state.
- [ ] No client crash or missing texture/model errors when alchemy devices load in chunks.

**Риски / зависимости:**

Dependency: particle effects for boiling/bubbles may belong to Stage 8-d/e, but TESR must still render static/dynamic tile state and not rely on absent particles for basic visual parity.

### GAP-5: Infusion matrix, pedestals, wand pedestal and pillars lack animated item/halo/structure rendering

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/blocks/BlockStoneDevice.java:70-81`
- `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:48-56`
- `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:180-194`
- `src/main/java/thaumcraft/common/tiles/TilePedestal.java:25-28`
- `src/main/java/thaumcraft/common/tiles/TilePedestal.java:67-82`
- `src/main/java/thaumcraft/common/tiles/TilePedestal.java:150-152`

**Референс:**
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileRunicMatrixRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TilePedestalRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileInfusionPillarRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileWandPedestalRenderer.class`
- `thaumcraft_src/assets/thaumcraft/textures/models/pillar.obj`
- `thaumcraft_src/assets/thaumcraft/textures/models/pillar.png`

**Что не совпадает:**

Current infusion mechanics expose active/crafting/startup state and pedestal item inventory, but no renderer displays the floating matrix cube/halo, active/crafting visual state, pillar model, or floating items on pedestals. Reference `TileRunicMatrixRenderer` has matrix cube models and `drawHalo`; `TilePedestalRenderer` and `TileWandPedestalRenderer` render held items; `TileInfusionPillarRenderer` uses custom pillar model resources.

**Что нужно доделать:**

Port the infusion TESR set and resources so active/crafting infusion structures have visible animated matrix, pedestal item display, wand pedestal display, and pillar geometry.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Add `TileRunicMatrixRenderer`, `TilePedestalRenderer`, `TileWandPedestalRenderer`, `TileInfusionPillarRenderer`, `ModelCube` or equivalent.
- Register `TileInfusionMatrix`, `TilePedestal`, `TileWandPedestal`, `TileInfusionPillar`.
- Use `TileInfusionMatrix.active`, `crafting`, `startUp`, `count` for animated states.
- Use pedestal inventory from `TilePedestal.getStackInSlot(0)` and client events `11/12` for visual pops if reference behavior requires it.
- Copy pillar/matrix/pedestal textures from reference assets.
- Smoke scenario: build valid infusion altar, place items on pedestals, activate matrix, start crafting, reload chunk.

**Критерии приемки:**
- [ ] Matrix inactive/active/crafting states visibly differ and match reference behavior closely.
- [ ] Pedestals and wand pedestal render held items above the block.
- [ ] Pillars render with correct model/texture and no missing asset log entries.

**Риски / зависимости:**

Dependency: full infusion crafting recipe availability is Stage 9, but manual smoke can use test commands/NBT or existing items to validate display states.

### GAP-6: Arcane bore/base and vis relay/workbench charger visuals are absent

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/blocks/BlockWoodenDevice.java:47-55`
- `src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java:57-68`
- `src/main/java/thaumcraft/common/tiles/TileArcaneBore.java:42-55`
- `src/main/java/thaumcraft/common/tiles/TileArcaneBore.java:62-73`
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:348-349`
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:363-364`

**Референс:**
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileArcaneBoreRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileArcaneBoreBaseRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileVisRelayRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileMagicWorkbenchChargerRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/ModelBore.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/ModelBoreBase.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/ModelBoreEmit.class`
- `thaumcraft_src/assets/thaumcraft/textures/models/vis_relay.obj`
- `thaumcraft_src/assets/thaumcraft/textures/models/vis_relay.png`

**Что не совпадает:**

Current bore tile tracks orientation, base orientation, focus/pickaxe presence, rotation and upgrades, but no TESR displays the bore model, rotating head, focus/pickaxe attachment, base direction, or relay/charger visual links. Reference bore renderers use dedicated bore models; reference relay/charger renderers are separate TESRs.

**Что нужно доделать:**

Port arcane bore/base TESRs and related relay/charger renderers with model resources and orientation handling.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Add `TileArcaneBoreRenderer`, `TileArcaneBoreBaseRenderer`, `TileVisRelayRenderer`, `TileMagicWorkbenchChargerRenderer`.
- Add bore model classes and copy model textures/resources.
- Register renderers for `TileArcaneBore`, `TileArcaneBoreBase`, `TileVisRelay`, `TileMagicWorkbenchCharger`.
- Use `TileArcaneBore.orientation`, `baseOrientation`, `topRotation`, `hasFocus`, `hasPickaxe` for visual state.
- Smoke scenario: place bore base/bore in all orientations, insert focus/pickaxe, reload chunk, place relay/charger near valid consumers.

**Критерии приемки:**
- [ ] Bore/base orientation matches block/tile orientation after placement and reload.
- [ ] Bore rotating/emitter state reflects `topRotation`, focus, and pickaxe state.
- [ ] Vis relay and workbench charger render without missing model/texture errors.

**Риски / зависимости:**

Dependency: full arcane bore gameplay validation may be Phase 7/common, but renderer can be validated with static tile state and inventory contents.

### GAP-7: Thaumatorium and focal manipulator TESRs/resources are absent

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java:64-65`
- `src/main/java/thaumcraft/common/blocks/BlockStoneDevice.java:79-80`
- `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:36-54`
- `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:116-121`
- `src/main/java/thaumcraft/common/tiles/TileFocalManipulator.java:16-27`
- `src/main/java/thaumcraft/common/tiles/TileFocalManipulator.java:28-67`

**Референс:**
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileFocalManipulatorRenderer.class`
- `thaumcraft_src/assets/thaumcraft/textures/models/thaumatorium.obj`
- `thaumcraft_src/assets/thaumcraft/textures/models/thaumatorium.mtl`
- `thaumcraft_src/assets/thaumcraft/textures/models/thaumatorium.png`

**Что не совпадает:**

Reference `TileThaumatoriumRenderer` has custom model and `EntityItem` display for current/queued output. Current tile has `inputStack`, recipe lists, `currentCraft`, `facing`, suction and output recipe methods, but no renderer. Reference `TileFocalManipulatorRenderer` uses arcane workbench model and item display while current tile tracks focus item, vis cost `aspects`, `size`, `upgrade`, and `rank`, but no visual feedback.

**Что нужно доделать:**

Port thaumatorium and focal manipulator TESRs, including displayed item/focus, progress/vis state where reference displays it, and model resources.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Add `TileThaumatoriumRenderer` and `TileFocalManipulatorRenderer`.
- Add/copy `ModelArcaneWorkbench` or equivalent if focal manipulator uses it.
- Register `TileThaumatorium` and `TileFocalManipulator` renderers.
- Copy thaumatorium OBJ/MTL/PNG resources.
- Smoke scenario: place thaumatorium bottom/top, set facing and recipe/current output; place focal manipulator with focus item and active `size/aspects` state.

**Критерии приемки:**
- [ ] Thaumatorium model renders with correct orientation and no missing OBJ/texture errors.
- [ ] Thaumatorium displays relevant input/output/current recipe item state where reference does.
- [ ] Focal manipulator displays focus/item and active crafting visual state without client crash.

**Риски / зависимости:**

Dependency: recipe availability and GUI setup are Stage 9/GUI-related, but renderer validation can use manually prepared tile NBT/state.

### GAP-8: Node, airy/node item, energized node and node device visuals are absent

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:330`
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:341-342`
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:379`
- `src/main/java/thaumcraft/common/tiles/TileNode.java:20-31`
- `src/main/java/thaumcraft/common/tiles/TileNode.java:128-143`
- `src/main/java/thaumcraft/client/ClientProxy.java:92-105`

**Референс:**
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileNodeRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileNodeEnergizedRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileNodeStabilizerRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileNodeConverterRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/ItemNodeRenderer.class`
- `thaumcraft_src/assets/thaumcraft/textures/models/ripple*.png`

**Что не совпадает:**

Current node tile has aspect lists, base aspects, type, modifier, fuel and balanced state, but no renderer for node color/type/modifier/size. Reference `TileNodeRenderer.renderNode(...)` accepts aspects, node type and node modifier, and is reused by item/node jar renderers. Current `ClientProxy` particle/beam overrides at `ClientProxy.java:92-105` are placeholders; while standalone particles are out of scope, node TESR must still render core node visual. Energized node, stabilizer and converter also have registered tile entities but no visual renderer.

**Что нужно доделать:**

Port node renderer and direct dependent item renderer, plus energized/stabilizer/converter device TESRs/resources.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Add `TileNodeRenderer` with static `renderNode` equivalent adapted to 1.12.2.
- Add `TileNodeEnergizedRenderer`, `TileNodeStabilizerRenderer`, `TileNodeConverterRenderer`, `ItemNodeRenderer`.
- Register renderers for `TileNode`, `TileNodeEnergized`, `TileNodeStabilizer`, `TileNodeConverter`.
- Copy ripple/node textures from `thaumcraft_src/assets/thaumcraft/textures/models/`.
- Smoke scenario: place normal, hungry/dark/pure/tainted/unstable nodes with modifiers; place node stabilizer/converter and energized node; verify no shader/particle hard dependency crash.

**Критерии приемки:**
- [ ] Node visual size/color/type changes based on `TileNode` aspects/type/modifier.
- [ ] Item/node jar code can reuse node rendering without duplicate incompatible logic.
- [ ] Energized/stabilizer/converter tiles have visible renderer and no missing texture errors.

**Риски / зависимости:**

Dependency: shader/particle parity may be Stage 8-d/e. TESR must degrade gracefully if shader helpers are not yet ported.

### GAP-9: Portable hole and warded block visual wrappers are absent

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:370-371`
- `src/main/java/thaumcraft/common/tiles/TileHole.java:16-24`
- `src/main/java/thaumcraft/common/tiles/TileHole.java:53-70`
- `src/main/java/thaumcraft/common/tiles/TileWarded.java:14-20`
- `src/main/java/thaumcraft/common/tiles/TileWarded.java:21-39`
- `src/main/java/thaumcraft/common/tiles/TileWarded.java:47-72`

**Референс:**
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileHoleRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileWardedRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/block/BlockWardedRenderer.class`
- `thaumcraft_src/assets/thaumcraft/textures/models/ripple*.png`

**Что не совпадает:**

Current portable hole tile stores old block, tile NBT, countdown, count and direction, but there is no renderer for the reference plane/ripple visual. Current warded tile stores a wrapped block, metadata, light and owner, but there is no TESR/block renderer to draw the stored block facade or connected-side behavior. Reference `TileWardedRenderer` caches icons and renders stored block sides; reference `TileHoleRenderer` has per-face plane draw methods.

**Что нужно доделать:**

Port or adapt visual wrappers so portable hole planes and warded block facades render correctly in world and do not expose placeholder/invisible blocks.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Add `TileHoleRenderer` and `TileWardedRenderer` with 1.12.2 block texture/model access.
- Add block/model rendering path for `BlockWarded` if TESR alone cannot render facade sides in 1.12.2.
- Register `TileHole` and `TileWarded` renderers.
- Copy ripple/warded visual resources from reference assets.
- Smoke scenario: create portable hole in each face direction, wait countdown, reload during countdown; place warded blocks wrapping stone/glass/log/non-full blocks and verify facade/light behavior.

**Критерии приемки:**
- [ ] Portable hole renders directional opening plane/ripple for all six directions.
- [ ] Warded block visually matches stored block state closely enough for facade parity.
- [ ] Renderer handles stored tile/block missing registry fallback without client crash.

**Риски / зависимости:**

High risk: 1.7.10 `IIcon` logic in reference `TileWardedRenderer` must be replaced with 1.12.2 baked model/texture access. Portable Hole/Warding visuals are explicitly deferred in `AGENTS.md:57`, so this gap must remain open until implemented and manually validated.

### GAP-10: Banner/label/seal and other in-scope tile display renderers are missing

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/blocks/BlockWoodenDevice.java:54-55`
- `src/main/java/thaumcraft/common/tiles/TileBanner.java:1-3`
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:367-368`
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:352-358`
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:361-362`
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:374-375`

**Референс:**
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileBannerRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileBellowsRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileTubeBufferRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileTubeOnewayRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileTubeValveRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileMirrorRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileArcaneLampRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileResearchTableRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileTableRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileArcaneWorkbenchRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileDeconstructionTableRenderer.class`

**Что не совпадает:**

Reference has many tile renderers beyond the headline jars/alchemy/infusion/bore/node/hole/warded list. Current corresponding tiles are registered, but no renderer exists. Banner is especially incomplete: current `TileBanner` is an empty marker, so banner text/symbol/orientation/cloth model must be supplied by block/item state or renderer and currently is not. Tubes/valves/buffers, mirrors, lamps, workbench/table/research/deconstruction displays also have reference renderers but current client tree lacks any equivalent.

**Что нужно доделать:**

Audit each current tile registration against reference `setupTileRenderers()` and implement the remaining TESRs that are tile/block visual parity, or explicitly document if a 1.12 JSON/baked model fully replaces the TESR.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Add renderers for banner, bellows, tube buffer/oneway/valve, mirror/mirror essentia, arcane lamp/growth, table/research table/arcane workbench/deconstruction table, sensor/lifter if visual-only block models do not cover them.
- Register these renderers or JSON replacements.
- Add `ModelBanner` and required textures for banners/seals/labels if reference behavior uses dynamic patterns.
- Copy workbench/table/mirror/lamp/tube/bellows textures/resources from `thaumcraft_src/assets/thaumcraft`.
- Smoke scenario: place every wooden/stone/metal device meta from `BlockWoodenDevice.java:64-72`, `BlockStoneDevice.java:90-101`, `BlockMetalDevice.java:76-87` and reload chunk.

**Критерии приемки:**
- [ ] Every current block meta that creates a tile entity has either a TESR or a confirmed baked/static model replacement.
- [ ] Banners/labels/seals render visible orientation/content according to reference or documented available current tile data.
- [ ] Tubes/mirrors/lamps/workbench/table displays render without missing assets or client crash.

**Риски / зависимости:**

Risk: current common tiles may not yet expose all visual state that reference renderers expect. If missing state is found, fix common tile sync in the implementation checkpoint, but keep public/API contracts stable.

### GAP-11: Required textures, OBJ/MTL files, blockstates and baked model mappings are missing

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/resources/assets/thaumcraft/blockstates/` absent
- `src/main/resources/assets/thaumcraft/models/block/` absent
- `src/main/resources/assets/thaumcraft/textures/blocks/` absent
- `src/main/resources/assets/thaumcraft/textures/models/moneychanger.png`
- `src/main/resources/assets/thaumcraft/textures/models/wizard.png`
- `src/main/java/thaumcraft/client/ClientProxy.java:24-34`

**Референс:**
- `thaumcraft_src/assets/thaumcraft/textures/blocks/**`
- `thaumcraft_src/assets/thaumcraft/textures/models/**`
- `thaumcraft_src/assets/thaumcraft/textures/models/thaumatorium.obj`
- `thaumcraft_src/assets/thaumcraft/textures/models/thaumatorium.mtl`
- `thaumcraft_src/assets/thaumcraft/textures/models/pillar.obj`
- `thaumcraft_src/assets/thaumcraft/textures/models/vis_relay.obj`
- `thaumcraft_src/assets/thaumcraft/textures/models/vcrystal.obj`

**Что не совпадает:**

Current resources still cannot support full Stage 8-c parity: the port still lacks broad TESR/OBJ/manual validation evidence and many model-texture consumers remain runtime-unverified. However, a later checkpoint closed the most obvious baked-resource baseline holes by mirroring the original `textures/blocks/**` corpus into runtime resources and moving item/block `ModelLoader` registration onto a dedicated `ModelRegistryEvent` path. The remaining gap is no longer "no block textures exist", but rather "full block/tile visual parity is still not proven."

**Что нужно доделать:**

Copy original required assets and add 1.12.2 blockstates/model JSONs or custom model loaders as needed for block/item model parity around TESR-backed blocks.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Copy required assets from `thaumcraft_src/assets/thaumcraft/` into `src/main/resources/assets/thaumcraft/`; do not recreate assets from scratch.
- Add blockstate JSONs for tile/block-rendered blocks where Forge requires a valid model even if TESR handles dynamic parts.
- Add `models/block` and `models/item` entries for block item forms that should not use TESR item renderer.
- For OBJ assets, choose a Forge 1.12.2-compatible loading path or manually port the model classes using copied textures.
- Run client smoke/manual placement and inspect log for `missing model`, `missing texture`, OBJ loader errors, `FileNotFoundException`, and `ModelLoader` warnings.

**Критерии приемки:**
- [ ] No Stage 8-c block/item emits missing model or missing texture errors during client smoke placement scenarios.
- [ ] Required original textures/OBJ/MTL files are present under `src/main/resources/assets/thaumcraft/` with original-compatible paths.
- [ ] Block item models for tile-rendered blocks show acceptable inventory visuals or have registered item renderers.

**Риски / зависимости:**

Risk: Forge 1.12 OBJ loading requires model loader setup and `.obj` path conventions that differ from the 1.7.10 `AdvancedModelLoader` path. This should be solved per renderer/model, not by changing dependency versions.

**Checkpoint 2026-05-20 — block texture corpus baseline:**
The runtime tree now mirrors the original `thaumcraft_src/assets/thaumcraft/textures/blocks/**` file set, and the previously broken baked-model references to `alchemyblockadv` and `taint_fibres` are no longer missing from port resources. This improves inventory/block-item and some world-surface fallback coverage, but it is still not evidence of full Stage 8-c renderer parity or client-smoke cleanliness.

**Checkpoint 2026-05-20 — CCL item-render crash guard:**
A later client-render checkpoint restored the no-arg `CCModel.render()` contract so it no longer feeds `null` vertex operations into `CCRenderPipeline.rebuild()`. This specifically protects TESR-style item renderers such as the thaumometer from inventory/HWYLA crash paths that previously ended in `CCRenderPipeline.rebuild()` NPEs. The fix is non-GUI validated only; a real client smoke or manual reproduction-clear run is still required before claiming the broader render surface clean.

**Checkpoint 2026-05-20 — client asset/model load burst:**
Another follow-up checkpoint closed the next reproduced client log cluster without claiming full render parity: arcane door "open" models no longer reference nonexistent 1.12 `door_*_open` vanilla parents, missing `blockfluidpure`/`blockfluiddeath`/`blockfluxgoo`/`blockfluxgas` blockstates were added, and `blockarcanefurnace.json` now exposes every `facing=...,type=...` permutation that Forge's model loader enumerates. The same burst hardened the crystal TEISR item path by injecting a real `TileEntityRendererDispatcher` and origin `BlockPos` for inventory-only tiles, plus a null-safe `TileCrystalRenderer` seed fallback. This removes a concrete class of startup/inventory crashes and model-loader errors, but a real client smoke/manual pass is still required before declaring the Stage 8-c surface clean.

**Checkpoint 2026-05-20 — champion mob client FX hook restored:**
Another client-only regression was traced to event routing, not to particle assets or modifier implementations. In 1.7.10 champion particles were emitted from the client `RenderEventHandler` living-tick path via `ChampionModifier.mods[t].effect.showFX(...)`; the 1.12 port had kept only scan-overlay cleanup in that handler, so champion mobs silently lost their visual modifier particles. The current port restores that client hook while preserving the scan cleanup branch, but a real client smoke/manual pass is still required before claiming broad FX-surface cleanliness.

**Checkpoint 2026-05-21 — thaumometer handheld/inventory placement baseline:**
The thaumometer TEISR now recenters the flat `scanner.obj` body around its actual vertical midpoint before the builtin/entity display transforms are applied, instead of rendering the model from its lower face. The builtin/item display JSON was also tightened so first-person hand routes use a simple handheld rotation closer to the original 1.7.10 presentation and ground/fixed routes no longer reuse the smaller placeholder scale. This is a render-placement checkpoint only: it should reduce the observed inventory/ground/hand drift and make the gold rim/body visible again, but a real client rerun is still required before claiming full thaumometer render parity.

**Checkpoint 2026-05-21 — thaumometer transform-aware render parity route:**
The initial placement-only approximation was not sufficient because the 1.12 port had collapsed the original `IItemRenderer` branches into a blind `TileEntityItemStackRenderer`, which meant the scanner never knew whether it was rendering GUI, dropped-world, third-person, or first-person views. The current port now wraps `thaumcraft:itemthaumometer_tesr` at `ModelBakeEvent` time with a dedicated baked-model perspective bridge so the active `TransformType` is preserved and the TEISR can once again branch by context. `ItemThaumometerRenderer` now has explicit GUI, third-person, dropped/fixed, and first-person handheld setup paths, with the first-person route restoring the original arm-rendered scanner posture instead of relying on JSON display transforms alone. Manual client validation is still required before claiming the whole thaumometer surface closed, but this removes the core architectural blocker that prevented true 1.7.10-style render parity.

**Checkpoint 2026-05-21 — thaumometer vertex-format corruption fix:**
Further live visual feedback showed the remaining thaumometer failure was not a transform problem at all but a corrupted CCL vertex-write path: the OBJ scanner body rendered through `CCRenderState.startDrawing()`/`writeVert()` while the blue screen quad rendered through an independent `BufferBuilder` path, which matched the symptom cluster of "body explodes, lens survives". The underlying bug was a 1.7.10-era hard-coded attribute write order being fed into Forge 1.12 `BufferBuilder` formats, including a `BLOCK` format that does not even expose a normal slot. `CCRenderState` now walks the active `VertexFormat` element list when emitting each vertex, no longer pre-advances `color/lightmap` before the first vertex, and the thaumometer OBJ pass now uses `DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL` plus explicit normal loading. This closes the identified "vertex explosion / spaghetti render" defect class for the thaumometer body path, pending real client confirmation.

## 6. Итоговый checklist закрытия Stage 8-c

- [ ] Add a Stage 8-c client registration path for TESR and required tile/block item visuals.
- [ ] Port/register jar, filled jar, brain jar, node jar and direct item renderers.
- [ ] Port/register crucible and alembic TESRs.
- [ ] Port/register infusion matrix, pedestal, wand pedestal and pillar TESRs.
- [ ] Port/register arcane bore/base, vis relay and workbench charger TESRs.
- [ ] Port/register thaumatorium and focal manipulator TESRs.
- [ ] Port/register node, energized node, node stabilizer and node converter renderers.
- [ ] Port/register portable hole and warded block renderers.
- [ ] Audit and close remaining reference tile renderer coverage for banners, bellows, tubes, mirrors, lamps, tables/workbenches, reservoirs, crystals, eldritch tile visuals and other tile/block render-driven devices in current `ConfigBlocks.TILE_REGISTRATIONS`.
- [ ] Copy all required assets from `thaumcraft_src/assets/thaumcraft/` into `src/main/resources/assets/thaumcraft/`.
- [ ] Add required 1.12.2 blockstates, block models, item models, custom model loader registrations or TESR item renderers.
- [ ] Remove or resolve Stage 8-c TODO/stub placeholders that affect tile/block visuals, including static client visuals for crucible state.
- [ ] Run `./scripts/dev.sh compileJava` after implementation.
- [ ] Run client smoke if display/X11 is available: `./scripts/dev.sh smoke-client`.
- [ ] Manually place/load every Stage 8-c block/tile category and inspect logs for missing model/texture and renderer crashes.
- [ ] Update `docs/Stage8-c.md` after implementation with closed gaps and validation evidence.

## 7. Definition of Done

Stage 8-c считается ПОЛНОСТЬЮ завершенной только если:

- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 8-c реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 8-c;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 8-c проверены вручную или тестами;
- ./docs/Stage8-c.md обновлен и не содержит критичных открытых вопросов.

Дополнительные Stage 8-c acceptance scenarios:

- [ ] Fresh client world loads with Thaumcraft enabled and no Stage 8-c renderer registration crash.
- [ ] Chunk containing all jar variants loads and renders correctly.
- [ ] Chunk containing crucible/alembic/thaumatorium/focal manipulator loads and renders correctly.
- [ ] Chunk containing full infusion structure loads and renders inactive/active/crafting states correctly.
- [ ] Chunk containing arcane bore/base and vis relay loads and renders orientation/state correctly.
- [ ] Chunk containing node variants and node devices loads and renders without shader/particle hard crash.
- [ ] Portable hole and warded block visual wrappers are manually tested for all major directions/stored block categories.
- [ ] Client log has no unresolved missing texture/model errors for Stage 8-c blocks/items/resources.

## 8. Открытые вопросы

### Q1: Porting approach per renderer

**Resolution:** портировать renderer-by-renderer, а не механически копировать reference code. Каждый renderer надо переносить через современные 1.12.2 эквиваленты: `BufferBuilder`/`Tessellator`, baked models, `BlockRendererDispatcher`, TESR-safe transforms, resource locations и explicit GL state cleanup. Removed 1.7.10 APIs (`IIcon`, `ISimpleBlockRenderingHandler`, `IModelCustom`) заменяются, не шиммируются слепо.

Формулировка: "Each renderer must be audited against the exact reference class before porting. Removed 1.7.10 APIs must be replaced with 1.12.2 equivalents, not shimmed blindly. If a TESR depends on Stage 8-d/e helpers such as beams, halos, bubbles, particles, or shader-like overlays, it must either degrade safely or document a hard dependency on that later chunk."

### Q2: Stage 8-d/e dependency для TESR

**Resolution:** Stage 8-c TESRs, которые ссылаются на node halos, crucible bubbles или infusion beams, должны либо degrade gracefully без этих helper'ов, либо явно документировать dependency. Это не делает отсутствие core TESR приемлемым — базовый рендер тайла должен работать самостоятельно.

### Q3: Stage 9 dependency для manual scenarios

**Resolution:** если Stage 9 recipes/research ещё не позволяют легально получить блоки/items, тесты надо делать через creative inventory, test command, direct tile placement или controlled NBT. Не ждать полного progression path.

Формулировка: "If Stage 9 recipes/research are still missing, manual scenarios should use creative placement, test commands or direct tile NBT/state to validate renderers rather than waiting for progression."
