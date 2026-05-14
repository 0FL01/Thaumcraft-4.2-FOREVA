# Stage 4 — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 4 закрывает серверную и common-часть блоков и tile entities Thaumcraft 4.2.3.5 в порте на Forge 1.12.2: блоки должны быть зарегистрированы, ставиться в мир, сохраняться в NBT, синхронизироваться, открывать серверные контейнеры, выполнять интерактивное поведение и работать без клиентского GUI/рендер-полиша.

По PRD Stage 4 соответствует `docs/PRD.md:262-287`: блоки и tile entities должны быть placeable, persistent, interactive и server-functional; текущие baseline-зоны — Crucible, Infusion Matrix lifecycle, Arcane Bore mining-loop, Portable Hole wrapper, Warding wrapper; известные риски — Arcane Bore runtime/manual mining validation, `TileArcaneBoreBase` original-behavior verification, Thaumatorium recipe programming/content flow, Focal Manipulator upgrade UI/content flow, Infusion Matrix lifecycle/instability, Crucible interactions, Essentia transport, tile NBT/sync.

Stage 4 не может считаться завершенной сейчас: есть blocker/high gaps в регистрациях ItemBlock, контейнерах, essentia transport, Thaumatorium top, Arcane Bore vis/essentia loop и отсутствующих/заглушенных блоках/тайлах.

Принятое прагматичное решение для закрытия Stage 4: Stage 4 делится на `core closure` и `transport closure`. `core closure` закрывает серверную работоспособность уже существующих Stage 4 устройств и разблокирует Phase 8; `transport closure` добавляет минимальную server-only essentia-сеть, без которой нельзя валидировать машины. Reference-блоки, не требующиеся для этих двух closure-наборов, не блокируют pre-Phase8 переход при условии явного deferral в этом документе.

## 2. Scope фазы

В scope Stage 4 входят:

- Блоки и ItemBlock/metadata-представления для Stage 4 устройств: `BlockStoneDevice`, `BlockWoodenDevice`, `BlockMetalDevice`, `BlockJar`, `BlockHole`, `BlockWarded`, а также отдельные reference-блоки для essentia/печей/труб/зеркал, если они требуются серверному gameplay.
- Tile entities и persistent state: `TileCrucible`, `TileInfusionMatrix`, `TilePedestal`, `TileArcaneBore`, `TileArcaneBoreBase`, `TileThaumatorium`, `TileThaumatoriumTop`, `TileFocalManipulator`, `TileAlchemyFurnace`, `TileAlembic`, `TileCentrifuge`, `TileEssentiaReservoir`, `TileMirrorEssentia`, `TileHole`, `TileWarded`, jars и related workbench/table tiles.
- Регистрации block/tile/itemblock: `ConfigBlocks`, Forge registry events, `GameRegistry.registerTileEntity`, GUI id routing.
- Server interactions: wand activation, block activation, container button clicks, inventory slots, shift-click, block break drops, redstone/power checks, fluid/water interaction, entity collision, crucible smelting, infusion crafting, thaumatorium programming/crafting, focal upgrades, arcane bore mining.
- NBT/sync: сохранение инвентарей, aspects/essentia, recipe lists, orientation/facing, wrapper block state, tile wrappers, update packets, block events.
- Essentia transport: suction, input/output sides, tubes/reservoirs/alembics/thaumatorium top/nozzles/mirrors, `IEssentiaTransport` and `IAspectContainer` behavior.
- Multiblocks/wrappers: Infusion Matrix with pedestal/pillars/stabilizers, Thaumatorium bottom/top, Portable Hole restore flow, Warding restore/ownership wrapper.
- Acceptance after closure: server-visible scenarios must work before GUI polish; GUI-dependent completion may be deferred only with explicit documentation, not by leaving server containers empty.

Out of scope except as direct dependencies:

- Phase 8 client GUI screens, TESR/model/FX/shader polish. Dependency: server containers and packets must exist before client screens can work.
- Phase 9 full recipe/research content completion. Dependency: Stage 4 machines must expose correct server hooks so content can be programmed/used when recipes exist.

## 3. Источники сравнения

Current source and docs:

- `docs/PRD.md:262-287` — Stage 4 goal, baseline, risks, acceptance.
- `src/main/java/thaumcraft/common/Thaumcraft.java:137-185` — current tile entity registrations.
- `src/main/java/thaumcraft/common/Thaumcraft.java:241-253` — current block/item registry flow.
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:48-143` — current block instances.
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:180-231` — current registered blocks/itemblocks.
- `src/main/java/thaumcraft/common/CommonProxy.java:45-99` — current server GUI routing.
- `src/main/java/thaumcraft/api/TileThaumcraft.java:10-40` — current shared NBT/update packet base.
- `src/main/java/thaumcraft/common/blocks/BlockStoneDevice.java:69-142` — current stone-device tile creation and activation.
- `src/main/java/thaumcraft/common/blocks/BlockWoodenDevice.java:47-95` — current wooden-device tile creation and activation.
- `src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java:55-130` — current metal-device tile creation and activation.
- `src/main/java/thaumcraft/common/blocks/BlockHole.java:24-90` and `src/main/java/thaumcraft/common/tiles/TileHole.java:16-165` — Portable Hole wrapper baseline.
- `src/main/java/thaumcraft/common/blocks/BlockWarded.java:30-188` and `src/main/java/thaumcraft/common/tiles/TileWarded.java:14-76` — Warding wrapper baseline.
- `src/main/java/thaumcraft/common/tiles/TileArcaneBore.java:40-421` and `src/main/java/thaumcraft/common/tiles/TileArcaneBoreBase.java:15-62` — Arcane Bore baseline.
- `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:45-753` and `src/main/java/thaumcraft/common/tiles/TilePedestal.java` — Infusion baseline.
- `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:36-507` and `src/main/java/thaumcraft/common/tiles/TileThaumatoriumTop.java:1-3` — Thaumatorium baseline.
- `src/main/java/thaumcraft/common/tiles/TileFocalManipulator.java:16-291` — Focal Manipulator baseline.
- `src/main/java/thaumcraft/common/container/ContainerArcaneBore.java:10-32`, `src/main/java/thaumcraft/common/container/ContainerThaumatorium.java:10-32`, `src/main/java/thaumcraft/common/container/ContainerFocalManipulator.java:10-32` — current server containers.
- `src/main/java/thaumcraft/common/tiles/TileAlchemyFurnace.java:20-424`, `src/main/java/thaumcraft/common/tiles/TileAlembic.java:12-188`, `src/main/java/thaumcraft/common/tiles/TileCentrifuge.java:15-244`, `src/main/java/thaumcraft/common/tiles/TileEssentiaReservoir.java:12-211`, `src/main/java/thaumcraft/common/tiles/TileMirrorEssentia.java:1-3` — essentia/alchemy tiles.
- `src/main/resources/assets/thaumcraft/sounds.json:5` — only matched Stage 4 resource hit for searched device terms.

Reference material:

- `thaumcraft_src/thaumcraft/common/blocks/BlockStoneDevice.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockWoodenDevice.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockMetalDevice.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockArcaneFurnace.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockTube.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockEssentiaReservoir.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockMirror.class`
- `thaumcraft_src/thaumcraft/common/container/ContainerArcaneBore.class`
- `thaumcraft_src/thaumcraft/common/container/ContainerThaumatorium.class`
- `thaumcraft_src/thaumcraft/common/container/ContainerFocalManipulator.class`
- `thaumcraft_src/thaumcraft/common/container/SlotLimitedByClass.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileArcaneBore.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileArcaneBoreBase.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileInfusionMatrix.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileCrucible.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileThaumatorium.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileThaumatoriumTop.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileFocalManipulator.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileAlchemyFurnace.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileAlembic.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileEssentiaReservoir.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileMirrorEssentia.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileTube.class`, `TileTubeBuffer.class`, `TileTubeFilter.class`, `TileTubeOneway.class`, `TileTubeRestrict.class`, `TileTubeValve.class`
- `thaumcraft_src/thaumcraft/common/config/ConfigBlocks.class`

Reference inspection commands run:

- `javap -classpath thaumcraft_src -public thaumcraft.common.container.ContainerThaumatorium thaumcraft.common.container.ContainerFocalManipulator thaumcraft.common.container.ContainerArcaneBore thaumcraft.common.tiles.TileArcaneBoreBase thaumcraft.common.tiles.TileThaumatorium thaumcraft.common.tiles.TileEssentiaReservoir thaumcraft.common.tiles.TileAlembic`
- `javap -classpath thaumcraft_src -public thaumcraft.common.tiles.TileArcaneBore thaumcraft.common.tiles.TileInfusionMatrix thaumcraft.common.tiles.TileCrucible thaumcraft.common.tiles.TileAlchemyFurnace thaumcraft.common.blocks.BlockMetalDevice thaumcraft.common.blocks.BlockStoneDevice thaumcraft.common.blocks.BlockWoodenDevice`
- `javap -classpath thaumcraft_src -public thaumcraft.common.tiles.TileArcaneFurnace thaumcraft.common.tiles.TileArcaneFurnaceNozzle thaumcraft.common.blocks.BlockArcaneFurnace thaumcraft.common.tiles.TileTube thaumcraft.common.blocks.BlockTube thaumcraft.common.blocks.BlockEssentiaReservoir`
- `javap -classpath thaumcraft_src -public thaumcraft.common.tiles.TileThaumatoriumTop`
- `javap -classpath thaumcraft_src -public thaumcraft.common.tiles.TileMirrorEssentia thaumcraft.common.blocks.BlockMirror`
- `javap -classpath thaumcraft_src -public thaumcraft.common.config.ConfigBlocks`
- `cfr --silent true thaumcraft_src/thaumcraft/common/tiles/TileArcaneBoreBase.class`
- `cfr --silent true thaumcraft_src/thaumcraft/common/tiles/TileArcaneBore.class`
- `cfr --silent true thaumcraft_src/thaumcraft/common/container/ContainerThaumatorium.class`
- `cfr --silent true thaumcraft_src/thaumcraft/common/container/ContainerFocalManipulator.class`
- `cfr --silent true thaumcraft_src/thaumcraft/common/container/ContainerArcaneBore.class`

## 4. Текущее состояние Stage 4

Что уже есть:

- Tile registrations для большого набора Stage 4 tiles есть в `src/main/java/thaumcraft/common/Thaumcraft.java:137-185`, включая jars, table/workbench, pedestal, alchemy furnace, infusion matrix/pillar, focal manipulator, crucible, arcane bore/base, alembic, centrifuge, essentia reservoir, mirror essentia, hole, warded, thaumatorium/top.
- Базовые block instances регистрируются через `ConfigBlocks.init()` и `ConfigBlocks.getAllBlocks()` в `src/main/java/thaumcraft/common/config/ConfigBlocks.java:48-143` и `src/main/java/thaumcraft/common/config/ConfigBlocks.java:180-206`.
- `BlockStoneDevice` создает core tiles по metadata и открывает GUI для alchemy furnace, focal manipulator, pedestal interaction и wand activation для infusion matrix в `src/main/java/thaumcraft/common/blocks/BlockStoneDevice.java:69-142`.
- `BlockWoodenDevice` создает bellows, bore base, bore and opens bore GUI/wand activation in `src/main/java/thaumcraft/common/blocks/BlockWoodenDevice.java:47-95`.
- `BlockMetalDevice` creates crucible, alembic, thaumatorium/top and handles crucible entity collision, fluid interaction, and thaumatorium GUI opening in `src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java:55-130`.
- `TileCrucible` has water capability, heat, aspect storage, smelting, recipe matching, spill/remnant behavior and NBT in `src/main/java/thaumcraft/common/tiles/TileCrucible.java:51-529`.
- `TileInfusionMatrix` has multiblock validation, crafting start/cycle/finish, instability events, essentia container, NBT and sync in `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:69-753`.
- `TileArcaneBore` has inventory, orientation, mining loop, fake player block break, fortune/silk, drops and NBT in `src/main/java/thaumcraft/common/tiles/TileArcaneBore.java:59-421`.
- `TileThaumatorium` has bottom tile NBT, input inventory, recipe hashes, essentia suction/fill, output insertion and crafting loop in `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:55-507`.
- `TileFocalManipulator` has server-side upgrade start, XP cost, vis drain, focus upgrade application, inventory and NBT in `src/main/java/thaumcraft/common/tiles/TileFocalManipulator.java:28-291`.
- Portable Hole wrapper stores original block/tile NBT and restores it in `src/main/java/thaumcraft/common/tiles/TileHole.java:28-165`; `BlockHole` is non-colliding/invisible/no-drop in `src/main/java/thaumcraft/common/blocks/BlockHole.java:24-90`.
- Warding wrapper stores original block metadata/light/owner and delegates many block behaviors in `src/main/java/thaumcraft/common/tiles/TileWarded.java:21-76` and `src/main/java/thaumcraft/common/blocks/BlockWarded.java:59-188`.

Main Stage 4 risks in current state:

- Some reference blocks/classes are absent entirely, especially tubes, dedicated reservoir/mirror/arcane furnace/magic box/chest hungry/lifter/block-specific itemblocks.
- Several registered tiles are non-functional stubs despite being registered, especially `TileThaumatoriumTop`, `TileMirrorEssentia`, `TileArcaneFurnace`, `TileArcaneFurnaceNozzle`.
- Core server containers for Arcane Bore, Thaumatorium and Focal Manipulator are empty shells with no slots, button handling, recipe programming or shift-click behavior.
- Metadata ItemBlocks for `stone_device`, `wooden_device`, `metal_device`, `table`, `magical_log` and several reference Stage 4 blocks are missing, making many device variants not placeable via normal item registry.
- Essentia transport is incomplete: reference tubes are absent, top/nozzle/mirror transport is stubbed, reservoir side logic is suspicious, and Arcane Bore base does not implement `IEssentiaTransport`.
- Runtime/manual validation is still required for all high-risk scenarios listed in PRD; no build or smoke command was run for this documentation-only analysis.

## 5. Gap list

### GAP-1: Device metadata ItemBlocks missing for placeability

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:48-143`
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:209-231`
- `src/main/java/thaumcraft/common/blocks/BlockStoneDevice.java:89-94`
- `src/main/java/thaumcraft/common/blocks/BlockWoodenDevice.java:63-67`
- `src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java:72-76`

**Референс:**
- `thaumcraft_src/thaumcraft/common/blocks/BlockStoneDeviceItem.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockWoodenDeviceItem.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockMetalDeviceItem.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockTableItem.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockMagicalLogItem.class`
- `thaumcraft_src/thaumcraft/common/config/ConfigBlocks.class`

**Что не совпадает:**

Current registers only selected ItemBlocks in `ConfigBlocks.registerItemBlocks`; `stone_device`, `wooden_device`, `metal_device`, `table`, `magical_log`, and several other metadata block item classes are not registered. The blocks exist and expose subblocks, but without ItemBlock registration normal inventory placement/crafting output cannot produce those block variants. Reference has dedicated metadata ItemBlock classes for these device blocks.

**Что нужно доделать:**

Add proper metadata-preserving ItemBlock registrations for Stage 4 device blocks and verify registry names match current block registry names and original intent.

**Как доделать:**
- Add/port ItemBlock classes for `BlockStoneDevice`, `BlockWoodenDevice`, `BlockMetalDevice`, `BlockTable`, `BlockMagicalLog` if needed.
- Update `ConfigBlocks.registerItemBlocks` to register explicit itemblocks with registry names `thaumcraft:stone_device`, `thaumcraft:wooden_device`, `thaumcraft:metal_device`, etc.
- Ensure `getMetadata`, translation keys and subtype handling preserve metadata.
- Validate placing all Stage 4 device variants creates expected `TileEntity` via `createTileEntity`.

**Критерии приемки:**
- [ ] `stone_device`, `wooden_device`, and `metal_device` have registered ItemBlocks preserving metadata.
- [ ] Crucible, Alembic, Thaumatorium, Arcane Bore, Bore Base, Pedestal, Infusion Matrix, Infusion Pillar, Alchemy Furnace and Focal Manipulator are placeable from item stacks.
- [ ] Registry names do not silently change from existing block registry names.

**Риски / зависимости:**

Dependency: Phase 9 recipes/research will be unable to grant/place Stage 4 devices until ItemBlocks exist. Do not rename current block registry ids without migration rationale.

### GAP-2: Arcane Bore server container has no slots or transfer behavior

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/container/ContainerArcaneBore.java:10-32`
- `src/main/java/thaumcraft/common/CommonProxy.java:57-60`
- `src/main/java/thaumcraft/common/tiles/TileArcaneBore.java:137-241`

**Референс:**
- `thaumcraft_src/thaumcraft/common/container/ContainerArcaneBore.class`
- `thaumcraft_src/thaumcraft/common/container/SlotLimitedByClass.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileArcaneBore.class`

**Что не совпадает:**

Current container only implements `canInteractWith` and adds no tile slots or player inventory slots. Reference container adds one `ItemFocusExcavation` slot, one `ItemPickaxe` slot, binds player inventory, and implements shift-click routing. Current `TileArcaneBore` inventory accepts focus/pickaxe in `src/main/java/thaumcraft/common/tiles/TileArcaneBore.java:216-220`, but the GUI/container cannot insert them.

**Что нужно доделать:**

Port server container slot layout and transfer behavior so Arcane Bore can be configured server-side independent of Phase 8 GUI polish.

**Как доделать:**
- Add `SlotLimitedByClass` equivalent or use existing slot restrictions with `isItemValidForSlot`.
- Update `ContainerArcaneBore` to add tile slot 0 for `FocusExcavation` and slot 1 for `ItemPickaxe`.
- Bind player inventory/hotbar and implement `transferStackInSlot` parity.
- Keep GUI id 2 routing in `CommonProxy` unchanged.

**Критерии приемки:**
- [ ] Server container has two tile slots and player inventory slots.
- [ ] Shift-click focus/pickaxe routes to correct slots and rejects invalid items.
- [ ] Bore mining scenario can be configured without direct NBT/editing.

**Риски / зависимости:**

Dependency: Phase 8 client GUI can remain absent, but server container must be functional for eventual GUI to bind. Requires item class names to match current port (`FocusExcavation` vs reference `ItemFocusExcavation`).

### GAP-3: Thaumatorium server container cannot program recipes

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/container/ContainerThaumatorium.java:10-32`
- `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:39-48`
- `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:184-238`
- `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:464-477`
- `src/main/java/thaumcraft/common/CommonProxy.java:82-85`

**Референс:**
- `thaumcraft_src/thaumcraft/common/container/ContainerThaumatorium.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileThaumatorium.class`
- `thaumcraft_src/thaumcraft/api/crafting/CrucibleRecipe.class`

**Что не совпадает:**

Current container has no input slot, no player inventory slots, no `updateRecipes`, no button handling and no recipe toggle flow. Reference container populates recipe candidates from `ThaumcraftApi.getCraftingRecipes()`, checks player research, toggles hashes/aspect costs/player names on button click, calls tile dirty/update, and wires `thaumatorium.eventHandler`. Current tile has recipe storage and crafting loop, but the only way to add/remove recipes is missing.

**Что нужно доделать:**

Port the server-side recipe programming container and slot layout. This is Stage 4 because the acceptance explicitly says server-visible interactions must exist before client GUI polish.

**Как доделать:**
- Update `ContainerThaumatorium` with tile input slot 0 and player inventory/hotbar slots.
- Store `EntityPlayer`, set/unset `TileThaumatorium.eventHandler` on open/close.
- Implement recipe candidate rebuild from `ThaumcraftApi.getCraftingRecipes()` and current research API equivalent.
- Implement `enchantItem`/button handling to toggle recipe hashes, `recipeEssentia`, `recipePlayer`, and reset `currentCraft`.
- Implement `transferStackInSlot` parity.

**Критерии приемки:**
- [ ] Putting catalyst/input into Thaumatorium updates available recipe list server-side.
- [ ] Button click toggles recipe programming and persists through NBT keys `recipes` and `OutputPlayer`.
- [ ] Programmed recipe runs through `TileThaumatorium.update()` and outputs to adjacent inventory or world.

**Риски / зависимости:**

Dependency: Research/content completeness is Phase 9, but the container must call current research APIs correctly and must not bypass research when recipes are available.

### GAP-4: Focal Manipulator server container cannot start upgrades

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/container/ContainerFocalManipulator.java:10-32`
- `src/main/java/thaumcraft/common/tiles/TileFocalManipulator.java:69-115`
- `src/main/java/thaumcraft/common/tiles/TileFocalManipulator.java:245-272`
- `src/main/java/thaumcraft/common/CommonProxy.java:96-99`

**Референс:**
- `thaumcraft_src/thaumcraft/common/container/ContainerFocalManipulator.class`
- `thaumcraft_src/thaumcraft/common/container/SlotLimitedByClass.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileFocalManipulator.class`

**Что не совпадает:**

Current tile can execute `startCraft(id, player)`, drain vis and apply focus upgrades, but current container has no focus slot, no player inventory, no button handler and no shift-click. Reference container has a focus-only slot, player inventory, `enchantItem` button handling that calls `table.startCraft(button, p)`, and failure sound behavior.

**Что нужно доделать:**

Port server container slot and button behavior so upgrade selection from GUI can drive `TileFocalManipulator.startCraft`.

**Как доделать:**
- Add focus-limited slot using `ItemFocusBasic` and player inventory/hotbar slots.
- Implement button handling to call `startCraft(button, player)` and server failure feedback.
- Implement shift-click routing between focus slot and inventory.
- Verify `getField` sync for `size`, `upgrade`, `rank` remains sufficient.

**Критерии приемки:**
- [ ] Focus can be inserted and removed through server container.
- [ ] Upgrade button id calls `startCraft` and consumes XP only when accepted.
- [ ] Upgrade progresses and applies to focus after vis is drained.

**Риски / зависимости:**

Dependency: Phase 8 GUI is needed for visual upgrade selection, but server-side button handling and slots are Stage 4.

### GAP-5: Thaumatorium top tile is a stub, breaking top-half inventory and essentia transport

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/tiles/TileThaumatoriumTop.java:1-3`
- `src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java:61-62`
- `src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java:121-129`
- `src/main/java/thaumcraft/common/Thaumcraft.java:184-185`

**Референс:**
- `thaumcraft_src/thaumcraft/common/tiles/TileThaumatoriumTop.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileThaumatorium.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockMetalDevice.class`

**Что не совпадает:**

Reference `TileThaumatoriumTop` implements `IAspectContainer`, `IEssentiaTransport`, and `ISidedInventory`, delegates to the bottom `TileThaumatorium`, and makes the two-block Thaumatorium work from both halves. Current top tile is an empty `TileThaumcraft`, so automation, essentia suction, inventory access and many side interactions through the top block are missing.

**Что нужно доделать:**

Port top tile delegation behavior to bottom tile at `pos.down()` and preserve NBT/sync expectations.

**Как доделать:**
- Implement `TileThaumatoriumTop` interfaces from reference using Forge 1.12 `EnumFacing` and current inventory methods.
- Cache/resolve bottom `TileThaumatorium` in update or per-call safely.
- Delegate inventory, aspect container and transport methods to bottom tile.
- Verify `BlockMetalDevice` top activation still opens GUI at `pos.down()`.

**Критерии приемки:**
- [ ] Top block exposes same input inventory behavior as bottom where reference does.
- [ ] Essentia transport connected to top half reaches bottom thaumatorium state.
- [ ] Breaking/reloading the multiblock does not orphan recipe/input state.

**Риски / зависимости:**

High risk of automation regressions if top side semantics differ from reference. Must manually validate with jars/alembics/tubes adjacent to top and bottom.

### GAP-6: Arcane Bore base lacks reference essentia suction and Bore lacks vis/ENTROPY speed loop

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/tiles/TileArcaneBoreBase.java:15-62`
- `src/main/java/thaumcraft/common/tiles/TileArcaneBore.java:278-421`
- `src/main/java/thaumcraft/common/blocks/BlockWoodenDevice.java:47-95`

**Референс:**
- `thaumcraft_src/thaumcraft/common/tiles/TileArcaneBoreBase.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileArcaneBore.class`

**Что не совпадает:**

Reference `TileArcaneBoreBase` implements `IEssentiaTransport`, has `drawEssentia()`, suctions `Aspect.ENTROPY` at 128 from all non-output faces, and returns `renderExtendedTube() == true`. Current base only stores orientation and handles wand rotation. Reference `TileArcaneBore` drains aura vis and uses base-drawn ENTROPY to maintain `speedyTime`; current bore mining loop runs from redstone, focus and pickaxe only and does not consume vis/essentia.

**Что нужно доделать:**

Restore original energy/essentia cost semantics for Bore operation while adapting to current 1.12 aura/vis APIs.

**Как доделать:**
- Implement `IEssentiaTransport` on `TileArcaneBoreBase` and reference suction behavior for `Aspect.ENTROPY`.
- Add `drawEssentia()` or equivalent used by `TileArcaneBore`.
- Compare and port `speedyTime`/vis drain pacing from reference `TileArcaneBore`.
- Persist any restored fields with original NBT key `SpeedyTime` if still needed.
- Runtime-test a bore with/without ENTROPY/vis and redstone.

**Критерии приемки:**
- [ ] Bore base connects to essentia transport network and suctions ENTROPY from valid sides.
- [ ] Bore operation consumes correct vis/essentia budget or intentionally documented 1.12-equivalent.
- [ ] Bore does not mine indefinitely for free when reference would stall.

**Риски / зависимости:**

Dependency: Current Phase 3 aura/vis APIs must be used for vis drain. Essentia transport gaps in GAP-7 can block full validation.

### GAP-7: Essentia transport network is incomplete and missing tubes

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:24-33`
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:180-206`
- `src/main/java/thaumcraft/common/Thaumcraft.java:167-181`
- `src/main/java/thaumcraft/common/tiles/TileAlembic.java:126-187`
- `src/main/java/thaumcraft/common/tiles/TileCentrifuge.java:164-226`
- `src/main/java/thaumcraft/common/tiles/TileEssentiaReservoir.java:144-210`
- `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:161-182`

**Референс:**
- `thaumcraft_src/thaumcraft/common/blocks/BlockTube.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockTubeItem.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileTube.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileTubeBuffer.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileTubeFilter.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileTubeOneway.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileTubeRestrict.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileTubeValve.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileAlembic.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileEssentiaReservoir.class`

**Что не совпадает:**

Reference has a full tube block and tube tile family. Current source has no `BlockTube` and no tube tile classes. Current network can only interact through direct adjacent `ThaumcraftApiHelper.getConnectableTile` calls in machines, so normal essentia routing scenarios cannot match original. `TileEssentiaReservoir.canInputFrom` and `canOutputTo` currently return `face == this.facing || true` in `src/main/java/thaumcraft/common/tiles/TileEssentiaReservoir.java:149-157`, making side restrictions effectively disabled and suspicious versus reference side/facing behavior.

**Что нужно доделать:**

Port the tube block/tile family and verify all current `IEssentiaTransport` implementations against reference side/suction contracts.

**Как доделать:**
- Add `BlockTube` and metadata ItemBlock if Stage 4 closure includes transport placeability.
- Add/port `TileTube`, `TileTubeBuffer`, `TileTubeFilter`, `TileTubeOneway`, `TileTubeRestrict`, `TileTubeValve`.
- Register block and tile entities with stable registry names.
- Audit `TileAlembic`, `TileCentrifuge`, `TileThaumatorium`, `TileEssentiaReservoir`, `TileArcaneBoreBase`, `TileArcaneFurnaceNozzle` against reference transport methods.
- Manually validate alembic -> tube -> jar/reservoir/thaumatorium scenarios.

**Критерии приемки:**
- [ ] Tube block variants are placeable and registered.
- [ ] Essentia moves through tubes with correct suction direction and restrictions.
- [ ] Reservoir side/facing behavior matches reference or documented compatible adaptation.

**Риски / зависимости:**

High integration risk: multiple machines depend on transport. Client tube highlight/render is Phase 8 dependency, but server transport and collision/placeability are Stage 4.

### GAP-8: Mirror Essentia is registered but fully stubbed

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/tiles/TileMirrorEssentia.java:1-3`
- `src/main/java/thaumcraft/common/Thaumcraft.java:169-170`
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:180-206`

**Референс:**
- `thaumcraft_src/thaumcraft/common/tiles/TileMirrorEssentia.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockMirror.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockMirrorItem.class`

**Что не совпадает:**

Reference `TileMirrorEssentia` stores link coordinates/dimension/facing, validates/restores links, and implements `IAspectSource`. Current class is an empty tile. Current `ConfigBlocks` also lacks `blockMirror` even though `TileMirror` and `TileMirrorEssentia` are registered. Essentia mirror gameplay is therefore absent.

**Что нужно доделать:**

Port mirror block registration and `TileMirrorEssentia` link/aspect behavior.

**Как доделать:**
- Add `BlockMirror` and itemblock with normal and essentia variants.
- Implement `TileMirrorEssentia` NBT keys for linked state and destination.
- Implement link validation/restoration and aspect container/source delegation to linked mirror.
- Register block and ensure placement/activation flow links mirrors as reference does.

**Критерии приемки:**
- [ ] Essentia mirror blocks are placeable and create correct tile variants.
- [ ] Linked mirrors persist through world reload using original NBT semantics.
- [ ] Essentia can be inserted/extracted through linked mirrors in server scenario.

**Риски / зависимости:**

May depend on item behavior outside Stage 4 for linking. If a linking item is Phase 5, label that dependency explicitly while still implementing tile/block server behavior.

### GAP-9: Arcane Furnace and Nozzle are stubs / block absent

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/tiles/TileArcaneFurnace.java:1-6`
- `src/main/java/thaumcraft/common/tiles/TileArcaneFurnaceNozzle.java:1-3`
- `src/main/java/thaumcraft/common/Thaumcraft.java:164-165`
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:48-143`

**Референс:**
- `thaumcraft_src/thaumcraft/common/blocks/BlockArcaneFurnace.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileArcaneFurnace.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileArcaneFurnaceNozzle.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileAlchemyFurnaceAdvanced.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileAlchemyFurnaceAdvancedNozzle.class`

**Что не совпадает:**

Current `TileArcaneFurnace` only ticks empty; nozzle is an empty tile. Reference furnace has inventory-like methods, cook time, max cook time, speed, facing, item insertion/ejection, update loop and block event behavior. Reference nozzle implements `IEssentiaTransport`. Current `ConfigBlocks` has no `blockArcaneFurnace`, so the registered tiles have no obvious block placement path.

**Что нужно доделать:**

Determine whether Arcane Furnace belongs to required Stage 4 block/tile scope for this port checkpoint. If yes, port block, tile, nozzle, registration and server behavior; if intentionally deferred, document with hard dependency and remove false completion claims.

**Как доделать:**
- Port `BlockArcaneFurnace` placeability/collision/break/update server behavior.
- Implement `TileArcaneFurnace` inventory/cook/eject/addItems methods and NBT.
- Implement nozzle `IEssentiaTransport` behavior.
- Register block/itemblock and confirm tile creation.

**Критерии приемки:**
- [ ] Arcane Furnace block is registered/placeable if retained in Stage 4 scope.
- [ ] Furnace processes/ejects items according to reference server behavior.
- [ ] Nozzle participates in essentia transport if reference scenario requires it.

**Риски / зависимости:**

Could overlap with content/progression in Phase 9, but block/tile/server functionality is Stage 4 if the feature is present. Do not leave registered stub tiles as apparent parity.

### GAP-10: Infusion Matrix lifecycle is broad but unvalidated against original instability/runtime behavior

**Статус:** требует проверки  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:97-105`
- `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:180-258`
- `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:261-365`
- `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:471-579`
- `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:613-657`
- `src/main/java/thaumcraft/common/blocks/BlockStoneDevice.java:128-135`

**Референс:**
- `thaumcraft_src/thaumcraft/common/tiles/TileInfusionMatrix.class`
- `thaumcraft_src/thaumcraft/common/tiles/TilePedestal.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockStoneDevice.class`

**Что не совпадает:**

Current implementation is substantial, but static analysis cannot confirm parity for recipe matching order, pedestal scan geometry, symmetry/stabilizer scoring, instability event distribution, XP drain, NBT output object encoding and block event effects. Reference exposes the same major methods (`validLocation`, `craftingStart`, `craftCycle`, `craftingFinish`), but behavior-sensitive details need runtime/manual validation. PRD explicitly lists Infusion Matrix lifecycle and instability as risk in `docs/PRD.md:279`.

**Что нужно доделать:**

Perform method-level reference comparison for the current implementation and then runtime validate core infusion scenarios.

**Как доделать:**
- Compare decompiled reference `TileInfusionMatrix` against current method-by-method.
- Verify NBT keys `active`, `crafting`, `instability`, `recipein`, `recipeout`, `recipeinput`, `recipeinst`, `recipetype`, `recipexp`, `recipeplayer`, `countdelay`, `itemcount` remain compatible.
- Test activation, normal infusion recipe, enchantment infusion, missing essentia, missing item, instability side effects and world reload during crafting.
- Verify `TilePedestal.receiveClientEvent` and block events used by matrix do not require missing server behavior.

**Критерии приемки:**
- [ ] Valid multiblock activates and invalid multiblock refuses/deactivates.
- [ ] At least one infusion recipe completes and persists safely across reload.
- [ ] Instability events occur server-side and do not crash/headlessly depend on client code.

**Риски / зависимости:**

Dependency: Phase 9 recipe content is needed for broad coverage; Stage 4 can use a minimal known recipe/test fixture but cannot claim full parity without content scenario evidence.

### GAP-11: Crucible has server baseline but needs original-behavior and runtime validation

**Статус:** требует проверки  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/tiles/TileCrucible.java:71-86`
- `src/main/java/thaumcraft/common/tiles/TileCrucible.java:121-207`
- `src/main/java/thaumcraft/common/tiles/TileCrucible.java:248-294`
- `src/main/java/thaumcraft/common/tiles/TileCrucible.java:328-402`
- `src/main/java/thaumcraft/common/tiles/TileCrucible.java:412-423`
- `src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java:82-103`
- `src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java:105-110`

**Референс:**
- `thaumcraft_src/thaumcraft/common/tiles/TileCrucible.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockMetalDevice.class`

**Что не совпадает:**

Current Crucible has a strong baseline, but behavior-sensitive areas need comparison/runtime proof: Forge 1.12 fluid capability replacement for original `IFluidHandler`, heat thresholds and bellows acceleration, water drain amounts, aspect overflow/decomposition, entity collision timing, recipe event player attribution, special item ejection and block events. Client event hooks are explicitly commented as Phase 8 in `src/main/java/thaumcraft/common/tiles/TileCrucible.java:196-200` and `src/main/java/thaumcraft/common/tiles/TileCrucible.java:510-518`, which is acceptable only for visuals.

**Что нужно доделать:**

Complete reference comparison and runtime server scenarios for Crucible interactions.

**Как доделать:**
- Compare decompiled `TileCrucible` with current heat/smelt/spill methods.
- Validate bucket fill/drain via Forge fluid capability.
- Validate item collision for aspect decomposition and crucible recipe output.
- Validate overflow/spill and shift-wand spill remnants.
- Keep Phase 8 visual comments documented as client-only deferral.

**Критерии приемки:**
- [ ] Heated water crucible processes aspect-bearing items and recipes.
- [ ] Empty/invalid items are rejected without item loss.
- [ ] Spill/remnant and overflow behavior matches reference server effects.

**Риски / зависимости:**

Dependency: Phase 9 crucible recipes/aspect tags needed for broad recipe validation; a minimal current recipe/aspect case is sufficient for Stage 4 server smoke.

### GAP-12: Alchemy Furnace and Alembic transport behavior is incomplete/suspicious

**Статус:** частично реализовано  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/common/tiles/TileAlchemyFurnace.java:98-132`
- `src/main/java/thaumcraft/common/tiles/TileAlchemyFurnace.java:134-201`
- `src/main/java/thaumcraft/common/tiles/TileAlchemyFurnace.java:203-246`
- `src/main/java/thaumcraft/common/tiles/TileAlembic.java:12-188`
- `src/main/java/thaumcraft/common/container/ContainerAlchemyFurnace.java`

**Референс:**
- `thaumcraft_src/thaumcraft/common/tiles/TileAlchemyFurnace.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileAlembic.class`
- `thaumcraft_src/thaumcraft/common/container/ContainerAlchemyFurnace.class`

**Что не совпадает:**

Current alchemy furnace baseline smelts aspects and pushes to alembics, but reference `TileAlembic` also implements `IWandable`, has `aspectFilter`, `aboveAlembic`, `aboveFurnace`, `getAppearance`, packet handling and wand interaction. Current `TileAlembic` lacks those fields/methods and has simplified transport. This may break filtering, stacking visuals that also affect server connection, and wand-side interaction.

**Что нужно доделать:**

Port missing Alembic server-relevant behavior and validate alchemy furnace + stacked alembic flow.

**Как доделать:**
- Compare reference `TileAlembic` implementation and port `aspectFilter`, wand interaction and appearance state if server-relevant.
- Verify `TileAlchemyFurnace.pushAspectsToAlembics` with multiple stacked alembics and full/filtered states.
- Ensure NBT preserves filter/aspect/amount/facing fields.
- Validate `ContainerAlchemyFurnace` slots/fields and shift-click behavior against reference.

**Критерии приемки:**
- [ ] Alchemy Furnace smelts aspect-bearing items into stacked alembics.
- [ ] Alembic extraction respects aspect/filter/server transport behavior.
- [ ] NBT reload preserves alembic contents and filter state.

**Риски / зависимости:**

Visual appearance is Phase 8, but fields that control connection/filter state are Stage 4 if they affect server transport.

### GAP-13: Portable Hole wrapper baseline requires tile/complex block runtime validation

**Статус:** требует проверки  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/common/blocks/BlockHole.java:24-90`
- `src/main/java/thaumcraft/common/tiles/TileHole.java:28-165`

**Референс:**
- `thaumcraft_src/thaumcraft/common/blocks/BlockHole.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileHole.class`

**Что не совпадает:**

Current wrapper stores block registry name plus old numeric id fallback and tile NBT, then restores after countdown. This is a reasonable 1.12 adaptation, but static analysis cannot prove parity for multi-block opening planes, scheduled updates, neighbor notifications, tile entity restore order and modded tile compatibility. PRD lists Portable Hole wrapper as baseline but not validated in `docs/PRD.md:270-287`.

**Что нужно доделать:**

Run manual/server scenarios for air blocks, normal blocks, blocks with tile entities and chained holes in all directions.

**Как доделать:**
- Compare reference `TileHole` countdown/plane behavior with current `createOpeningPlane` and `restoreBlock`.
- Test portable hole creation through current focus behavior as a dependency scenario.
- Validate tile NBT restore for a chest-like inventory tile and for a Thaumcraft tile if safe.
- Confirm no item duplication/loss on restore.

**Критерии приемки:**
- [ ] Hole opens a 3x3 plane and chain length as reference expects.
- [ ] Stored tile entity restores with inventory/NBT intact.
- [ ] Countdown expiry restores blocks and neighbor updates without crash.

**Риски / зависимости:**

Dependency: Focus Portable Hole trigger is Phase 5, but wrapper tile/block restore behavior is Stage 4.

### GAP-14: Warding wrapper baseline requires owner/security and block-delegation validation

**Статус:** требует проверки  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/common/blocks/BlockWarded.java:30-188`
- `src/main/java/thaumcraft/common/tiles/TileWarded.java:14-76`
- `src/main/java/thaumcraft/common/items/wands/foci/FocusWarding.java`

**Референс:**
- `thaumcraft_src/thaumcraft/common/blocks/BlockWarded.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileWarded.class`

**Что не совпадает:**

Current Warded wrapper delegates many block behaviors and stores owner hash/light/original block. Static analysis cannot confirm parity for owner checks, unward permission, explosion/harvest behavior, redstone/pathing delegation and stored tile exclusion. Current `BlockWarded.canHarvestBlock` returns true in `src/main/java/thaumcraft/common/blocks/BlockWarded.java:153-155`, which is suspicious for a warded unbreakable wrapper unless actual break prevention is fully handled elsewhere.

**Что нужно доделать:**

Verify reference behavior and run manual scenarios around warding/unwarding by owner/non-owner, explosions and stored block delegation.

**Как доделать:**
- Compare decompiled `BlockWarded`/`TileWarded` with current methods.
- Audit `FocusWarding` interaction path for ownership and safe restoration.
- Test ward/unward normal block, block with special collision/light, explosion attempt and non-owner break attempt.
- Ensure no drops occur from wrapper and original block restores only through authorized path.

**Критерии приемки:**
- [ ] Owner can unward and original block/light restore.
- [ ] Non-owner cannot bypass ward by harvest/explosion/pathing edge cases.
- [ ] Delegated collision/light/plant/beacon/enchant behavior matches stored block.

**Риски / зависимости:**

Dependency: Focus Warding item behavior is Phase 5, but wrapper block/tile security and persistence are Stage 4.

### GAP-15: Missing Stage 4 block/tile classes from reference inventory

**Статус:** отсутствует  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:9-33`
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:180-206`
- `src/main/java/thaumcraft/common/tiles/TileArcaneFurnace.java:1-6`
- `src/main/java/thaumcraft/common/tiles/TileMirrorEssentia.java:1-3`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigBlocks.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockChestHungry.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockMagicBox.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockLifter.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockEssentiaReservoir.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileChestHungry.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileMagicBox.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileLifter.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileBrainbox.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileMemory.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileEssentiaCrystalizer.class`

**Что не совпадает:**

Reference `ConfigBlocks` exposes many Stage 4 block fields absent from current `ConfigBlocks`: `blockChestHungry`, `blockMagicBox`, `blockLifter`, `blockMirror`, `blockTube`, `blockEssentiaReservoir`, `blockAlchemyFurnace`, among others. Some corresponding current tiles exist (`TileLifter`, `TileEssentiaReservoir`, `TileMagicWorkbenchCharger`) but no block registration path exists; some tiles are absent entirely. This means reference block/tile feature coverage is incomplete.

**Что нужно доделать:**

Classify each missing reference block/tile as Stage 4 required, Phase 5/6/7 dependency, or explicit deferral. Required Stage 4 block/tile features must be ported or Stage 4 remains incomplete.

**Как доделать:**
- Build a reference-to-current matrix for every `ConfigBlocks` block field and tile class.
- Port required block/tile pairs with registry names and ItemBlocks.
- For non-Stage-4 or content-gated items, document exact dependency and not as completed.
- Prioritize server-functional machines/storage/transport over cosmetic-only blocks.

**Критерии приемки:**
- [ ] Every reference block/tile relevant to Stage 4 is either implemented or explicitly deferred with dependency.
- [ ] No registered tile remains unreachable by any block/item placement path unless intentionally internal.
- [ ] Missing class list is updated after each checkpoint.

**Риски / зависимости:**

Scope creep risk is high. Keep Stage 4 focused on blocks/tile/server behavior; do not pull in client rendering or full content progression beyond what is needed for placeability/interactions.

### GAP-16: Tile NBT/sync coverage is inconsistent and needs audit

**Статус:** требует проверки  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/api/TileThaumcraft.java:10-40`
- `src/main/java/thaumcraft/common/tiles/TileCrucible.java:90-100`
- `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:747-752`
- `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:501-506`
- `src/main/java/thaumcraft/common/tiles/TileFocalManipulator.java:285-290`
- `src/main/java/thaumcraft/common/tiles/TileEssentiaReservoir.java:27-59`
- `src/main/java/thaumcraft/common/tiles/TileAlembic.java:23-45`

**Референс:**
- `thaumcraft_src/thaumcraft/api/TileThaumcraft.class`
- `thaumcraft_src/thaumcraft/common/tiles/*.class`

**Что не совпадает:**

Current base `TileThaumcraft` writes custom NBT to update packet but does not override `getUpdateTag`; only Crucible adds explicit `getUpdateTag/handleUpdateTag`. Some tiles call `notifyBlockUpdate`, some only `markDirty`, and stub tiles have no state. For 1.12 chunk initial sync and client/server state, this inconsistency can affect any tile with visible or GUI-synced fields. Stage 4 acceptance includes NBT/sync risk in `docs/PRD.md:282`.

**Что нужно доделать:**

Audit all Stage 4 tiles for save NBT, update packet, initial chunk sync and container field sync.

**Как доделать:**
- Decide whether `TileThaumcraft` should provide common `getUpdateTag/handleUpdateTag` for all subclasses.
- Verify each tile persists original keys and only adds 1.12 registry-name compatibility where needed.
- Ensure server mutations that affect GUI/client state call `markDirty` and `notifyBlockUpdate` or container field updates as appropriate.
- Test world save/reload for Crucible, Infusion Matrix mid-craft, Thaumatorium programmed recipe, Focal Manipulator mid-upgrade, Bore inventory/orientation, Hole/Warded wrappers and essentia containers.

**Критерии приемки:**
- [ ] Every Stage 4 tile has documented NBT keys and reload scenario.
- [ ] Initial chunk load sync works for non-GUI state required by server-visible interaction.
- [ ] Container fields and block updates keep server/client state consistent enough for Phase 8 GUI.

**Риски / зависимости:**

Changing base tile sync can affect many phases. Keep changes minimal and validate broadly.

### GAP-17: Runtime smoke/manual Stage 4 scenarios have not been executed

**Статус:** требует проверки  
**Критичность:** medium

**Текущая реализация:**
- `docs/PRD.md:173-185`
- `docs/PRD.md:262-287`
- `AGENTS.md` validation instructions in repository root

**Референс:**
- Original runtime behavior in `Thaumcraft-1.7.10-4.2.3.5.jar`
- Reference classes under `thaumcraft_src/thaumcraft/common/**`

**Что не совпадает:**

This analysis did not run build, server smoke, or manual gameplay validation. PRD explicitly says compile status is not parity status and only validated behavior should be considered closed. Because Stage 4 includes registries, block placement, containers, tile persistence, automation, and multiblocks, runtime smoke is required before closure.

**Что нужно доделать:**

After blocker/high implementation gaps are closed, run focused validation and manual/server scenarios.

**Как доделать:**
- Run `./scripts/dev.sh compileJava` after implementation changes.
- Run `./scripts/dev.sh smoke-server` for registry/tile/block load validation.
- Run `./scripts/dev.sh check-jar` after a build intended for normal Forge.
- Manually validate the Stage 4 scenario checklist in section 6.

**Критерии приемки:**
- [ ] Server reaches normal ready state with no crash markers.
- [ ] Stage 4 manual scenarios pass or failures are logged as gaps.
- [ ] `docs/Stage4.md` is updated after validation with remaining limitations.

**Риски / зависимости:**

Some manual scenarios depend on Phase 8 GUI or Phase 9 content. Those dependencies must be isolated: use server containers/test fixtures where possible and document any unavoidable GUI/content dependency.

## 6. Итоговый checklist закрытия Stage 4

### 6.1 Core closure — pre-Phase8 unblock

- [ ] Register metadata-preserving ItemBlocks for `stone_device`, `wooden_device`, `metal_device` and other required Stage 4 metadata blocks.
- [ ] Make Arcane Bore server container functional: restricted focus/pickaxe slots, player inventory, shift-click.
- [ ] Restore Arcane Bore base ENTROPY essentia transport and Bore vis/essentia consumption parity or documented compatible equivalent.
- [ ] Make Thaumatorium server container functional: input slot, recipe candidate list, research-aware recipe toggling, shift-click.
- [ ] Implement `TileThaumatoriumTop` delegation for inventory, aspects and essentia transport.
- [ ] Make Focal Manipulator server container functional: focus slot, button handling, shift-click and failure feedback.
- [ ] Audit NBT and update sync for touched core tiles: Bore/Base, Thaumatorium/Top, Focal Manipulator and metadata block placement paths.

### 6.2 Transport closure — minimal server essentia network

- [ ] Port or explicitly defer all missing Stage 4 block/tile pairs from reference `ConfigBlocks`, prioritizing tubes, reservoir block and alembic/server transport.
- [ ] Port tube block/tile family or document exact Stage 4 blocker if postponed.
- [ ] Register and validate `BlockEssentiaReservoir` or document an exact deferral; fix `TileEssentiaReservoir` one-face side semantics and active fill behavior if retained in Stage 4.
- [ ] Audit and validate alchemy furnace + alembic + centrifuge + reservoir transport behavior.

### 6.3 Explicit deferrals allowed for pre-Phase8 transition

- [ ] Implement or explicitly defer `TileMirrorEssentia` and mirror block; mirror gameplay may move to a later Stage 4 transport/content checkpoint if linking item flow is outside current scope.
- [ ] Implement or explicitly defer `BlockArcaneFurnace`, `TileArcaneFurnace`, and `TileArcaneFurnaceNozzle`; do not count registered stub tiles as parity.
- [ ] Explicitly classify `blockChestHungry`, `blockMagicBox`, `blockLifter`, `TileMagicBox`, `TileChestHungry`, `TileLifter`, `TileBrainbox`, `TileMemory`, and `TileEssentiaCrystalizer` as required, dependency-gated, or deferred before claiming full Stage 4 parity.

### 6.4 Validation closure

- [ ] Audit and validate `TileInfusionMatrix` against reference lifecycle, NBT, instability and reload behavior.
- [ ] Audit and validate `TileCrucible` against reference heating, fluid, smelting, spill and entity collision behavior.
- [ ] Validate Portable Hole wrapper restore behavior with normal blocks and tile entities.
- [ ] Validate Warding wrapper owner/security/restoration and stored block delegation.
- [ ] Audit NBT and update sync for all Stage 4 tiles.
- [ ] Run `./scripts/dev.sh compileJava` after implementation changes.
- [ ] Run `./scripts/dev.sh smoke-server` after registry/tile/block changes.
- [ ] Run manual Stage 4 scenarios: place all required blocks, open server containers, save/reload, break/recover inventories, operate Crucible, Infusion Matrix, Arcane Bore, Thaumatorium, Focal Manipulator, essentia network, Portable Hole, Warding.
- [ ] Update `docs/Stage4.md` after implementation/validation and remove resolved blocker/high gaps.

## 7. Definition of Done

Stage 4 считается ПОЛНОСТЬЮ завершенной только если:
- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 4 реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 4;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 4 проверены вручную или тестами;
- ./docs/Stage4.md обновлен и не содержит критичных открытых вопросов.

## 8. Открытые вопросы

Решенные вопросы:

- Полный набор reference-блоков из `thaumcraft_src/thaumcraft/common/config/ConfigBlocks.class` не входит целиком в pre-Phase8 `core closure`. Для Stage 4 используется прагматичное разделение: сначала `core closure`, затем минимальный `transport closure`. `blockTube` и `blockEssentiaReservoir` остаются приоритетом transport closure; `blockMirror`, `blockArcaneFurnace`, `blockChestHungry`, `blockMagicBox`, `blockLifter` и связанные tiles должны быть портированы или явно deferred отдельным checkpoint-решением.
- Для Arcane Bore canonical 1.12 replacement — существующий `thaumcraft.api.visnet.VisNetHandler.drainVis(... Aspect.ENTROPY ...)` с дополнительным Bore Base ENTROPY essentia fallback/boost, а не essentia-only режим. Если текущая vis-сеть не дает живых sources, чинить нужно vis-layer или документировать его как blocker, а не вводить Bore-only bypass.
- Minimal recipe/research fixtures для Stage 4 validation должны быть dev/test-only или manual-only fixtures, не полноценная Phase 9 регистрация. Минимум: один `CrucibleRecipe` для Crucible/Thaumatorium, один `InfusionRecipe` для Infusion Matrix, research unlock через текущий `ResearchManager` или пустой research key. Обычный `ConfigRecipes.init()` остается Phase 9 scope до отдельного content checkpoint.

Открытые вопросы после принятого решения:

- Какие именно transport pieces войдут в первый transport checkpoint: только base tubes + reservoir + alembic audit или сразу все `TileTube*` variants.
- Где хранить dev/manual validation fixtures так, чтобы они не попадали в normal content registration и не маскировали Phase 9 gaps.
