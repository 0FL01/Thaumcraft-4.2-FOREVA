# Stage 5 — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 5 должна восстановить серверное и common-layer поведение предметов Thaumcraft 4.2.3.5 в порте на Forge 1.12.2: items, tools, armor, baubles, relics, wands, wand caps/rods, foci, NBT-семантику, Baubles-интеграцию, ремонтопригодность, use actions, vis costs, side effects и базовые игровые сценарии.

PRD задает цель Stage 5 как сохранение поведения items/equipment и прямо фиксирует, что фаза не закрыта: `docs/PRD.md:289-315`. В PRD также перечислены текущие baselines и known risks: focus cost/world/entity scenarios, FX deferred to Phase 8, bauble tick/storage validation, Hover Harness, repairability, Primal Crusher, wand/focus upgrade costs and side effects: `docs/PRD.md:295-309`.

Stage 5 не может считаться завершенной, пока остаются blocker/high gaps ниже.

## 2. Scope фазы

В scope Stage 5 входят:

- Регистрации предметов и registry names: `src/main/java/thaumcraft/common/config/ConfigItems.java:45-813`, `src/main/java/thaumcraft/common/Thaumcraft.java:247-253`.
- Wand core: `ItemWandCasting`, `ItemWandRod`, `ItemWandCap`, `WandManager`, `WandRodPrimalOnUpdate`, wand NBT keys `rod`, `cap`, `focus`, vis storage, staff/sceptre semantics, focus switching, area mode NBT `areax/areay/areaz/aread`.
- Foci: Shock, Fire, Frost, Excavation, Primal, Warding, Hellbat, Pech, Trade, Portable Hole under `src/main/java/thaumcraft/common/items/wands/foci/`.
- Focus upgrades: `FocusUpgradeType`, focus NBT key `upgrade`, rank choices, `canApplyUpgrade`, cooldowns, frugal/potency/enlarge/extend/treasure/silktouch/custom upgrades.
- Baubles: runic ring/amulet/girdle, Vis Amulet, Hover Girdle, bauble blanks, focus pouch bauble, Baubles API integration.
- Armor: thaumium/void/fortress/robes/cultist/goggles/traveller boots/hover harness, runic charge, vis discount, robe dye NBT, repair items.
- Tools/weapons/relic equipment: thaumium, void, elemental tools/swords, bone bow, crimson sword, primal arrows, Primal Crusher.
- Relics and utility items: Thaumometer, Thaumonomicon, Hand Mirror, Resonator, Sanity Checker, resources, essence/crystal essence, nuggets, eldritch objects, loot bags, taint bottle, death/pure buckets, bath salts, compass stone, inkwell, key, mana bean, research notes, sanity soap, triple meat treat, zombie brain.
- Resources/config relevant to items: item models/textures/lang/sounds enough for registered items to be usable and identifiable. Client FX and render polish is dependency-labeled Phase 8 where visual-only.
- Acceptance scenarios: foci perform server behavior; wands/baubles/relics stay common-layer safe; repairability matches original intent; NBT persists compatible data; Baubles inventory/ticks work; costs and side effects match reference.

Out of scope except dependencies: Stage 6 entity AI/render parity, Stage 7 worldgen/runtime portal parity, Stage 8 item rendering/FX polish, Stage 9 recipe/research content. These phases are dependencies only where Stage 5 behavior calls into them.

## 3. Источники сравнения

Текущая реализация:

- `src/main/java/thaumcraft/common/config/ConfigItems.java:45-813`
- `src/main/java/thaumcraft/common/Thaumcraft.java:247-253`
- `src/main/java/thaumcraft/common/items/**`
- `src/main/java/thaumcraft/api/wands/**`
- `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:37-360`
- `src/main/resources/assets/thaumcraft/`
- `docs/PRD.md:289-315`

Reference:

- `thaumcraft_src/thaumcraft/common/config/ConfigItems.class`
- `thaumcraft_src/thaumcraft/common/items/**.class`
- `thaumcraft_src/thaumcraft/api/wands/**.class`
- `thaumcraft_src/assets/thaumcraft/textures/items/**`
- `thaumcraft_src/assets/thaumcraft/mcmod.info`, `thaumcraft_src/assets/thaumcraft/sounds.json` where item sounds are referenced
- `Thaumcraft-1.7.10-4.2.3.5.jar` as fallback binary reference

Lightweight analysis commands run:

- `git status --short`
- `cfr --silent true --outputdir .stage5_ref ...` against Stage 5 reference classes for static comparison; temporary output was used only to inspect decompiled methods and should not be treated as source of truth.
- `grep`/`glob` searches for Stage 5 classes, TODO/TBD stubs, registrations, assets, NBT and Baubles methods.

No build or runtime smoke was run because this task is document-only and no implementation code was changed.

## 4. Текущее состояние Stage 5

Current baselines exist, but they are uneven and not parity-validated:

- `ConfigItems` registers a broad item set and sets repair materials for tool/armor materials: `src/main/java/thaumcraft/common/config/ConfigItems.java:181-813`.
- Item registry event registers `ConfigItems.getAllItems()`: `src/main/java/thaumcraft/common/Thaumcraft.java:247-253`.
- Wand/focus NBT helpers exist for `rod`, `cap`, `focus`, `vis_`-prefixed vis keys and focus upgrades: `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:39-248`, `src/main/java/thaumcraft/api/wands/ItemFocusBasic.java:123-193`.
- Focus server classes exist for all 10 current foci: `src/main/java/thaumcraft/common/config/ConfigItems.java:55-65`, `src/main/java/thaumcraft/common/config/ConfigItems.java:233-291`.
- PRD-mentioned foci have common/server baselines, but they still require scenario parity checks: `docs/PRD.md:295-309`.
- Vis Amulet has NBT vis storage and wand transfer baseline: `src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java:48-170`.
- Runic shield handling exists outside item classes: `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:37-360`.
- Hover Harness and Hover Girdle are explicit stubs: `src/main/java/thaumcraft/common/items/armor/ItemHoverHarness.java:31-33`, `src/main/java/thaumcraft/common/items/baubles/ItemGirdleHover.java:31-34`.
- Relics and many utility items still contain `TBD` placeholders: examples include `ItemHandMirror`, `ItemResonator`, `ItemSanityChecker`, `ItemThaumonomicon`, `ItemBottleTaint`, `ItemLootBag`, `ItemEldritchObject`, `ItemSanitySoap`, `ItemResearchNotes`, `ItemManaBean`, `ItemCompassStone`, `ItemBathSalts`, `ItemBucketDeath`.
- Current resources are incomplete for Stage 5 items: `src/main/resources/assets/thaumcraft/` contains `sounds.json`, `sounds/`, and `textures/models/`, but no `models/item/`, no `textures/items/`, and no `lang/`; original item textures exist under `thaumcraft_src/assets/thaumcraft/textures/items/`.

## 5. Gap list

### GAP-1: Wand core не соответствует оригинальному поведению и NBT-семантике

**Статус:** частично реализовано  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:39-403`
- `src/main/java/thaumcraft/common/items/wands/WandManager.java:109-230`
- `src/main/java/thaumcraft/common/items/wands/WandRodPrimalOnUpdate.java:1-12`

**Референс:**
- `thaumcraft_src/thaumcraft/common/items/wands/ItemWandCasting.class`
- `thaumcraft_src/thaumcraft/common/items/wands/WandManager.class`
- `thaumcraft_src/thaumcraft/common/items/wands/WandRodPrimalOnUpdate.class`

**Что не совпадает:**

Текущий wand хранит vis как `vis_` + aspect tag: `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:39-43`, `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:85-94`. Reference хранит primal vis прямо по aspect tag (`aer`, `terra`, и т.п.) в NBT. Для fresh-world target canonical write format должен совпадать с reference semantics.

Текущий `ItemWandCasting` не реализует reference `IArchitect`, `IWandable`/`WandTriggerRegistry` right-click routing, warded block removal, staff detection/runes, staff attack attribute NBT, full focus cooldown flow and creative tab presets. В текущем коде right click сразу вызывает focus и всегда начинает active hand: `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:320-342`; reference сначала проверяет block/tile `IWandable`, `WandTriggerRegistry`, cooldown and focus activation.

`getFocusTreasure` всегда возвращает 0: `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:219-226`; reference читает `FocusUpgradeType.treasure`. `WandRodPrimalOnUpdate` является заглушкой: `src/main/java/thaumcraft/common/items/wands/WandRodPrimalOnUpdate.java:1-12`.

**Что нужно доделать:**

Восстановить wand core как поведенческий центр Stage 5: canonical NBT semantics, focus cooldown, focus potency/treasure/enlarge/extend helpers, staff/sceptre/rune behavior, block/tile trigger routing and server/client side boundaries.

**Как доделать:**
- `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java`: привести vis NBT к reference semantics and stop writing `vis_` keys; восстановить `getAllVis`, `getAspectsWithRoom`, `storeAllVis`, `storeVis`, `consumeVis`, focus helper methods, staff/sceptre/rune methods, creative presets.
- `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java`: портировать `onItemUseFirst`/right-click routing через `IWandable` and `WandTriggerRegistry` with 1.12 signatures.
- `src/main/java/thaumcraft/common/items/wands/WandManager.java`: восстановить cooldown maps/API and any Stage 5 focus switching behavior missing from reference; keep altar/jar/thaumatorium triggers as Stage 4 dependency if already elsewhere.
- `src/main/java/thaumcraft/common/items/wands/WandRodPrimalOnUpdate.java`: портировать primal rod update effects or document exact server/client deferral if purely FX.
- Verify all public `thaumcraft.api.wands.*` signatures remain Forge 1.12-compatible.

**Критерии приемки:**
- [ ] Original wand NBT keys for rod/cap/focus/upgrades/vis are the canonical fresh-world write/read format.
- [ ] Wand right-click first routes block/tile `IWandable` and wand triggers before focus activation.
- [ ] Focus cooldown, focus potency/treasure/frugal/enlarge/extend, staff/sceptre and rune behavior match reference in server scenarios.

**Риски / зависимости:**

Dependency: block/tile `IWandable` targets are Stage 4, but wand dispatch belongs to Stage 5. FX/sounds are Phase 8 dependency only where visual.

### GAP-2: Focus upgrade ranks, cooldowns and activation contracts are incomplete

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/items/wands/foci/FocusFire.java:38-107`
- `src/main/java/thaumcraft/common/items/wands/foci/FocusFrost.java:36-88`
- `src/main/java/thaumcraft/common/items/wands/foci/FocusShock.java:42-145`
- `src/main/java/thaumcraft/common/items/wands/foci/FocusPrimal.java:1-68`
- `src/main/java/thaumcraft/api/wands/ItemFocusBasic.java:111-197`

**Референс:**
- `thaumcraft_src/thaumcraft/common/items/wands/foci/ItemFocusFire.class`
- `thaumcraft_src/thaumcraft/common/items/wands/foci/ItemFocusFrost.class`
- `thaumcraft_src/thaumcraft/common/items/wands/foci/ItemFocusShock.class`
- `thaumcraft_src/thaumcraft/common/items/wands/foci/ItemFocusPrimal.class`
- `thaumcraft_src/thaumcraft/api/wands/ItemFocusBasic.class`

**Что не совпадает:**

Fire, Frost and Shock currently implement server actions, but omit several reference contracts:

- `FocusFire` has no `getPossibleUpgradesByRank`, no `canApplyUpgrade`, and no `getAnimation`: `src/main/java/thaumcraft/common/items/wands/foci/FocusFire.java:98-107` ends after sorting/enchant methods.
- `FocusFrost` has no `getActivationCooldown` and no `getPossibleUpgradesByRank`: `src/main/java/thaumcraft/common/items/wands/foci/FocusFrost.java:79-88`.
- `FocusShock` has no `getActivationCooldown`, no `getAnimation`, no `canApplyUpgrade`, no `getPossibleUpgradesByRank`: `src/main/java/thaumcraft/common/items/wands/foci/FocusShock.java:136-145`.
- `ItemFocusBasic.acceptsEnchant` exists in current API at `src/main/java/thaumcraft/api/wands/ItemFocusBasic.java:195-197`, but reference focus-specific enchant acceptance/upgrade rules need class-by-class validation.

Reference foci define rank-specific upgrade arrays, cooldowns and mutually-exclusive upgrade gates for fireball/firebeam, scattershot/iceboulder, chainlightning/earthshock and primal seeker.

**Что нужно доделать:**

Complete upgrade rank and activation metadata for every focus, not only server projectile/entity behavior.

**Как доделать:**
- Add reference-matching `getActivationCooldown`, `getAnimation`, `getPossibleUpgradesByRank`, `canApplyUpgrade` to Fire/Frost/Shock/Primal.
- Verify custom upgrade IDs and texture paths remain stable: current IDs 9-20 appear in foci; compare all against reference classes.
- Ensure wand uses cooldowns when activating foci, not only method definitions.
- Add focused manual checks for rank application and duplicate/mutually-exclusive upgrades.

**Критерии приемки:**
- [ ] Every focus returns the same upgrade options per rank as reference.
- [ ] Mutually-exclusive upgrades cannot be applied incorrectly.
- [ ] Cooldown and animation behavior matches reference where common/server-visible.

**Риски / зависимости:**

Dependency: Focal Manipulator UI/content is Stage 4/9/8 adjacent, but the item-side upgrade contract is Stage 5.

### GAP-3: PRD-listed focus baselines still need parity fixes and scenario validation

**Статус:** требует проверки  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/items/wands/foci/FocusPech.java:33-84`
- `src/main/java/thaumcraft/common/items/wands/foci/FocusHellbat.java:41-129`
- `src/main/java/thaumcraft/common/items/wands/foci/FocusTrade.java:41-228`
- `src/main/java/thaumcraft/common/items/wands/foci/FocusExcavation.java:47-205`
- `src/main/java/thaumcraft/common/items/wands/foci/FocusPortableHole.java:40-155`
- `src/main/java/thaumcraft/common/items/wands/foci/FocusWarding.java:46-247`

**Референс:**
- `thaumcraft_src/thaumcraft/common/items/wands/foci/ItemFocusPech.class`
- `thaumcraft_src/thaumcraft/common/items/wands/foci/ItemFocusHellbat.class`
- `thaumcraft_src/thaumcraft/common/items/wands/foci/ItemFocusTrade.class`
- `thaumcraft_src/thaumcraft/common/items/wands/foci/ItemFocusExcavation.class`
- `thaumcraft_src/thaumcraft/common/items/wands/foci/ItemFocusPortableHole.class`
- `thaumcraft_src/thaumcraft/common/items/wands/foci/ItemFocusWarding.class`

**Что не совпадает:**

These classes have meaningful common/server code, but static comparison shows they are not validated against all reference scenarios:

- `FocusTrade` stores picked block NBT and architect areas, but rank options differ from reference order/coverage unless verified against each rank: `src/main/java/thaumcraft/common/items/wands/foci/FocusTrade.java:100-145`.
- `FocusExcavation` consumes vis multiple times during enlarge mining and XP handling; static code needs world/drop/silk/fortune scenario checks: `src/main/java/thaumcraft/common/items/wands/foci/FocusExcavation.java:63-171`.
- `FocusPortableHole` depends on current `ConfigBlocks.blockHole` and recursive creation; original visual renderer is deferred, but server block lifecycle still needs runtime validation: `src/main/java/thaumcraft/common/items/wands/foci/FocusPortableHole.java:44-116`.
- `FocusWarding` has owner/area placement logic and `acceptsEnchant` but no runtime validation for warding wrapper ownership/NBT: `src/main/java/thaumcraft/common/items/wands/foci/FocusWarding.java:51-247`.
- Pech/Hellbat spawn projectile/entity behavior depends on Stage 6 entities but focus-side cost, spawn parameters and upgrade side effects are Stage 5.

**Что нужно доделать:**

Run class-by-class reference parity review for these six foci and then runtime/manual tests for cost consumption, invalid target behavior, side effects and entity/world outcomes.

**Как доделать:**
- For each focus, compare `getVisCost`, `getActivationCooldown`, `getPossibleUpgradesByRank`, `canApplyUpgrade`, `getMaxAreaSize`, NBT keys and world/entity mutation methods against the corresponding reference class.
- Create manual test matrix: insufficient vis, creative mode, each custom upgrade, invalid target, valid target, server-only world mutation, client visual no-op.
- Fix mismatches in the focus classes and `ItemWandCasting`/`WandManager` if activation/cooldown is the shared root cause.

**Критерии приемки:**
- [ ] Each PRD-listed focus consumes the same vis cost as reference for base and upgraded forms.
- [ ] Each focus performs expected server mutation/entity spawn/drop behavior in a dedicated server smoke/manual world.
- [ ] Client-only FX absence is documented as Phase 8 and does not hide missing server behavior.

**Риски / зависимости:**

Dependency: Pech/Hellbat spawned entity behavior is Stage 6; Portable Hole/Warding visual feedback is Phase 8. Stage 5 still owns focus activation, costs and server-side spawn/block mutations.

### GAP-4: Focus Pouch inventory and bauble pouch behavior are absent

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/items/wands/ItemFocusPouch.java:9-29`
- `src/main/java/thaumcraft/common/items/wands/ItemFocusPouchBauble.java:1-41`
- `src/main/java/thaumcraft/common/config/ConfigItems.java:221-231`

**Референс:**
- `thaumcraft_src/thaumcraft/common/items/wands/ItemFocusPouch.class`
- `thaumcraft_src/thaumcraft/common/items/wands/ItemFocusPouchBauble.class`

**Что не совпадает:**

Current `ItemFocusPouch` is only a one-stack item with subtype metadata and no inventory: `src/main/java/thaumcraft/common/items/wands/ItemFocusPouch.java:11-29`. Reference has an 18-slot NBT inventory under key `Inventory`, opens GUI id 5, returns rare rarity, and stores slot NBT with `Slot`. Current `WandManager.changeFocus` only scans `player.inventory.mainInventory`: `src/main/java/thaumcraft/common/items/wands/WandManager.java:115-145`, so foci in pouches are invisible.

**Что нужно доделать:**

Port focus pouch storage, GUI/container integration hooks, and focus switching lookup through pouch inventories and bauble pouch.

**Как доделать:**
- `ItemFocusPouch`: implement `getInventory`, `setInventory`, NBT key `Inventory`, slot count 18, open GUI id consistent with existing GUI ids.
- `ItemFocusPouchBauble`: ensure it inherits/uses the same inventory and correct Bauble slot behavior.
- `WandManager.changeFocus`: include focus pouch inventories and Baubles pouch when building the sorted focus list.
- Add container/GUI dependency notes if GUI is Phase 8, but server inventory NBT must exist in Stage 5.

**Критерии приемки:**
- [ ] Focus pouch persists 18 slots under original `Inventory`/`Slot` NBT semantics.
- [ ] Wand focus cycling can select foci stored in normal and Baubles focus pouches.
- [ ] Removing/changing focus returns the previous focus to the correct inventory without duplication or loss.

**Риски / зависимости:**

Dependency: client GUI display is Phase 8, but server inventory and NBT are Stage 5 blockers.

### GAP-5: Hover Harness and Hover Girdle flight behavior are stubs

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/items/armor/ItemHoverHarness.java:12-33`
- `src/main/java/thaumcraft/common/items/armor/Hover.java:13-41`
- `src/main/java/thaumcraft/common/items/baubles/ItemGirdleHover.java:11-50`
- `src/main/java/thaumcraft/common/config/ConfigItems.java:690-694`, `src/main/java/thaumcraft/common/config/ConfigItems.java:721-725`

**Референс:**
- `thaumcraft_src/thaumcraft/common/items/armor/ItemHoverHarness.class`
- `thaumcraft_src/thaumcraft/common/items/armor/Hover.class`
- `thaumcraft_src/thaumcraft/common/items/baubles/ItemGirdleHover.class`

**Что не совпадает:**

Current `ItemHoverHarness.onArmorTick` contains only `// Flight/hover mechanics - TBD`: `src/main/java/thaumcraft/common/items/armor/ItemHoverHarness.java:31-33`. Current `Hover` only sets step height for boots-like behavior and stores a Boolean map: `src/main/java/thaumcraft/common/items/armor/Hover.java:17-40`. Reference `Hover` toggles flight, persists NBT `hover`, consumes `Aspect.ENERGY` from a jar stored in harness NBT key `jar`, uses `charge`, resets fall damage/float counter, and applies Hover Girdle efficiency. Current `ItemGirdleHover.onWornTick` is also a `TBD`: `src/main/java/thaumcraft/common/items/baubles/ItemGirdleHover.java:31-34`.

**Что нужно доделать:**

Port hover toggle, fuel storage/consumption, NBT, server authority and Baubles Girdle modifier.

**Как доделать:**
- `Hover`: implement `toggleHover`, `handleHoverArmor`, `expendCharge`, NBT keys `hover`, `jar`, `charge`, and server/client packet boundary adapted to existing network stack.
- `ItemHoverHarness`: call `Hover.handleHoverArmor` and expose jar storage behavior if reference supports item use with jars.
- `ItemGirdleHover`: apply reference efficiency modifier and ensure correct `BaubleType.BELT`.
- Add manual validation: equip harness with ENERGY jar, toggle hover, deplete fuel, unequip, reconnect, check fall damage.

**Критерии приемки:**
- [ ] Harness can toggle hover only with valid ENERGY fuel and persists hover state in NBT.
- [ ] Server fall damage/float counter and fuel depletion match reference.
- [ ] Hover Girdle changes hover efficiency/speed exactly as reference.

**Риски / зависимости:**

Dependency: flight toggle input and visual/sound feedback may involve Phase 8 client work, but armor tick, NBT and fuel consumption are Stage 5.

### GAP-6: Baubles blank/runic metadata, discounts and charge semantics do not match reference

**Статус:** реализовано неправильно  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/items/baubles/ItemBaubleBlanks.java:16-86`
- `src/main/java/thaumcraft/common/items/baubles/ItemRingRunic.java:1-73`
- `src/main/java/thaumcraft/common/items/baubles/ItemAmuletRunic.java:1-62`
- `src/main/java/thaumcraft/common/items/baubles/ItemGirdleRunic.java:1-61`
- `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:53-360`

**Референс:**
- `thaumcraft_src/thaumcraft/common/items/baubles/ItemBaubleBlanks.class`
- `thaumcraft_src/thaumcraft/common/items/baubles/ItemRingRunic.class`
- `thaumcraft_src/thaumcraft/common/items/baubles/ItemAmuletRunic.class`
- `thaumcraft_src/thaumcraft/common/items/baubles/ItemGirdleRunic.class`

**Что не совпадает:**

Current `ItemBaubleBlanks` exposes metadata 0-8 in creative tab: `src/main/java/thaumcraft/common/items/baubles/ItemBaubleBlanks.java:41-47`; reference creative list exposes only base blank metadata 0, 1, 2. Current BaubleType mapping returns ring only for meta 3, amulet for meta 4, belt for meta 5, trinket otherwise: `src/main/java/thaumcraft/common/items/baubles/ItemBaubleBlanks.java:60-66`; reference maps metadata 1 and 3-8 to ring, 2 to belt, default to amulet. Current `getVisDiscount` and `getRunicCharge` always return 0: `src/main/java/thaumcraft/common/items/baubles/ItemBaubleBlanks.java:50-58`, while reference gives primal-specific discounts for metas 3-8 and runic charge for specific variants.

Runic event handling exists, but item metadata and per-item charge values need reference verification; `EventHandlerRunic` also contains Phase 8 packet comments at `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:107-113`, so visual/sync behavior may be incomplete.

**Что нужно доделать:**

Correct Baubles metadata semantics and connect runic charge/vis discount values to EventHandlerRunic.

**Как доделать:**
- `ItemBaubleBlanks`: align creative subitems, BaubleType mapping, `getVisDiscount`, `getRunicCharge` with reference.
- `ItemRingRunic`, `ItemAmuletRunic`, `ItemGirdleRunic`: verify metadata variants, runic charge, special effects and rarity/tooltips against reference.
- `EventHandlerRunic`: verify recharge cost, shield wait, emergency/kinetic/healing/charged behavior, packet sync boundary and damage handling.

**Критерии приемки:**
- [ ] Every bauble metadata value has the same slot type and stats as reference.
- [ ] Runic charge maximum/recharge/damage absorption works with armor and Baubles together.
- [ ] Vis discounts from baubles affect wand costs exactly once and with correct aspect-specific values.

**Риски / зависимости:**

Dependency: packet visuals and HUD sync are Phase 8; server charge math is Stage 5.

### GAP-7: Vis Amulet lacks relay charging parity and needs storage/tick validation

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java:48-170`
- `src/main/java/thaumcraft/common/items/wands/WandManager.java:63-107`

**Референс:**
- `thaumcraft_src/thaumcraft/common/items/baubles/ItemAmuletVis.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileVisRelay.class`

**Что не совпадает:**

Current Vis Amulet transfers vis to the main-hand wand every 5 ticks: `src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java:123-137`. Reference also pulls vis from nearby `TileVisRelay` into the amulet and triggers relay consume effects. Current implementation has no relay path. Current `WandManager.consumeVisFromInventory` checks amulets then main/offhand then inventory: `src/main/java/thaumcraft/common/items/wands/WandManager.java:63-107`; reference checks amulet then inventory in reverse order and uses crafting consumption for inventory wands. This may change cost/discount semantics.

**Что нужно доделать:**

Restore relay charging and validate amulet storage/tick behavior with Baubles 1.12.

**Как доделать:**
- Port or connect `TileVisRelay.nearbyPlayers` equivalent if Stage 4 tiles expose it; otherwise add a documented Stage 4 dependency.
- Validate NBT aspect keys remain original primal tags, not prefixed keys.
- Align `consumeVisFromInventory` order and crafting flag behavior with reference unless 1.12 offhand support is intentionally added and documented.
- Add tests/manual checks for lesser/normal amulet capacity, transfer rate 5 real vis per 5 ticks, relay fill and Baubles autosync.

**Критерии приемки:**
- [ ] Vis Amulet fills held wand and fills from nearby relay with original capacity/rate.
- [ ] NBT aspect values persist under original aspect tags.
- [ ] `consumeVisFromInventory` uses amulet and inventory wands with reference-compatible cost modifiers.

**Риски / зависимости:**

Dependency: `TileVisRelay` availability is Stage 4; client consume effects are Phase 8.

### GAP-8: Relics are stubs or simplified and do not preserve original gameplay

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java:20-58`
- `src/main/java/thaumcraft/common/items/relics/ItemThaumonomicon.java:15-48`
- `src/main/java/thaumcraft/common/items/relics/ItemHandMirror.java:15-42`
- `src/main/java/thaumcraft/common/items/relics/ItemResonator.java:13-37`
- `src/main/java/thaumcraft/common/items/relics/ItemSanityChecker.java:13-37`

**Референс:**
- `thaumcraft_src/thaumcraft/common/items/relics/ItemThaumometer.class`
- `thaumcraft_src/thaumcraft/common/items/relics/ItemThaumonomicon.class`
- `thaumcraft_src/thaumcraft/common/items/relics/ItemHandMirror.class`
- `thaumcraft_src/thaumcraft/common/items/relics/ItemResonator.class`
- `thaumcraft_src/thaumcraft/common/items/relics/ItemSanityChecker.class`

**Что не совпадает:**

`ItemThaumometer` currently completes scan logic server-side immediately on right click: `src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java:35-56`. Reference uses a 25-tick use action, client-side target lock, scan validity checks for entity/block/node/scan event handlers, sounds and server packet completion.

`ItemThaumonomicon` contains a GUI TODO: `src/main/java/thaumcraft/common/items/relics/ItemThaumonomicon.java:42-47`. `ItemHandMirror` contains TODO-only link/transport behavior: `src/main/java/thaumcraft/common/items/relics/ItemHandMirror.java:30-40`; reference stores NBT `linkX`, `linkY`, `linkZ`, `linkDim`, `dimname`, opens GUI id 16 and transports item stacks to linked mirror. `ItemResonator` and `ItemSanityChecker` are placeholders: `src/main/java/thaumcraft/common/items/relics/ItemResonator.java:28-35`, `src/main/java/thaumcraft/common/items/relics/ItemSanityChecker.java:28-35`.

**Что нужно доделать:**

Port relic behavior, NBT and GUI/server boundaries rather than returning successful placeholders.

**Как доделать:**
- `ItemThaumometer`: restore use duration, target stability, scan event handler path, node scan result handling, client/server packet boundary.
- `ItemHandMirror`: implement mirror linking NBT, linked-dimension validation, GUI opening, static `transport` behavior and error handling.
- `ItemThaumonomicon`: open correct GUI and preserve metadata/subitem behavior; content pages can be Stage 9 dependency but item action belongs here.
- `ItemResonator`: port node/aura resonance behavior and durability/sound effects.
- `ItemSanityChecker`: report actual warp state using current warp data APIs.

**Критерии приемки:**
- [ ] Each relic has no `TBD` placeholder in Stage 5 scope.
- [ ] Hand Mirror link NBT and transport works across dimensions or fails with reference-compatible cleanup.
- [ ] Thaumometer scan requires the original use scenario and completes valid targets only.

**Риски / зависимости:**

Dependency: Thaumonomicon content is Stage 9 and GUI rendering is Phase 8, but item use/opening and NBT are Stage 5.

### GAP-9: Tools and armor lack broad original behavior beyond repair material baselines

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigItems.java:184-201`, `src/main/java/thaumcraft/common/config/ConfigItems.java:769-812`
- `src/main/java/thaumcraft/common/items/equipment/ItemVoidSword.java:8-17`
- `src/main/java/thaumcraft/common/items/equipment/ItemVoidPickaxe.java:7-10`
- `src/main/java/thaumcraft/common/items/equipment/ItemElementalAxe.java:9-14`
- `src/main/java/thaumcraft/common/items/equipment/ItemElementalShovel.java:9-24`
- `src/main/java/thaumcraft/common/items/armor/ItemBootsTraveller.java:26-33`
- `src/main/java/thaumcraft/common/items/armor/ItemRobeArmor.java:23-24`
- `src/main/java/thaumcraft/common/items/armor/ItemVoidRobeArmor.java:23-24`

**Референс:**
- `thaumcraft_src/thaumcraft/common/items/equipment/ItemVoid*.class`
- `thaumcraft_src/thaumcraft/common/items/equipment/ItemElemental*.class`
- `thaumcraft_src/thaumcraft/common/items/equipment/ItemThaumium*.class`
- `thaumcraft_src/thaumcraft/common/items/armor/Item*.class`
- `thaumcraft_src/thaumcraft/common/items/armor/RecipesRobeArmorDyes.class`
- `thaumcraft_src/thaumcraft/common/items/armor/RecipesVoidRobeArmorDyes.class`

**Что не совпадает:**

Current repair materials are configured centrally: `src/main/java/thaumcraft/common/config/ConfigItems.java:769-812`, but many item classes only delegate to vanilla behavior and miss original active/passive effects. Reference void tools implement warp gear, void self-repair/update behavior, rarity and entity-hit effects. Current simple classes like `ItemVoidPickaxe` only define constructor inheritance: `src/main/java/thaumcraft/common/items/equipment/ItemVoidPickaxe.java:7-10`. `ItemElementalAxe` currently only declares `oreDictLogs` and constructor: `src/main/java/thaumcraft/common/items/equipment/ItemElementalAxe.java:9-14`, while reference has special axe behavior. `ItemElementalShovel` stores orientation NBT helpers only: `src/main/java/thaumcraft/common/items/equipment/ItemElementalShovel.java:16-24`.

Traveler boots call simplified hover/step helper: `src/main/java/thaumcraft/common/items/armor/ItemBootsTraveller.java:31-33`; reference changes movement/jump/fall behavior in more cases. Robe dye recipes are hard stubs: `src/main/java/thaumcraft/common/items/armor/RecipesRobeArmorDyes.java:14`, `src/main/java/thaumcraft/common/items/armor/RecipesVoidRobeArmorDyes.java:14`.

**Что нужно доделать:**

Perform item-by-item parity pass for all tool and armor classes, not just repair materials.

**Как доделать:**
- Void tools/sword/armor: port `IWarpingGear`, self-repair, rarity, entity-hit and tooltip behavior.
- Elemental tools: port special axe/tree, shovel orientation/area, pick/hoe behaviors and costs if applicable.
- Boots/Goggles/Robes/Fortress/Cultist armor: compare movement, vis discount, runic, dye NBT and special interactions.
- Repair: validate actual anvil repair inputs for each material after `configureRepairMaterials`.

**Критерии приемки:**
- [ ] Every Stage 5 tool/armor class has method parity reviewed against its reference class.
- [ ] Void gear warp/self-repair and elemental tool special actions work in server scenarios.
- [ ] Anvil repair accepts only intended inputs for all thaumium/void/elemental/robe/cultist/hover materials.

**Риски / зависимости:**

Dependency: model/texture rendering is Phase 8. Runtime entity interactions may depend on Stage 6 entities but item-side effects remain Stage 5.

### GAP-10: Primal Crusher baseline is partial and not validated

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/items/equipment/ItemPrimalCrusher.java:26-145`
- `src/main/java/thaumcraft/common/config/ConfigItems.java:527-531`

**Референс:**
- `thaumcraft_src/thaumcraft/common/items/equipment/ItemPrimalCrusher.class`

**Что не совпадает:**

Current class includes tool classes, harvest speed, area mining, repairability, enchantability, warp and update hooks: `src/main/java/thaumcraft/common/items/equipment/ItemPrimalCrusher.java:53-145`. However PRD explicitly calls Primal Crusher validation a known risk: `docs/PRD.md:307-309`. Static comparison still needs exact checks for harvest level/material (`PRIMALVOID` in reference), drop/area mining side effects, durability damage, void repair behavior and warp.

**Что нужно доделать:**

Complete static and runtime parity validation for Primal Crusher as a special case, then fix mismatches.

**Как доделать:**
- Compare reference methods: harvestability, destroy speed, `onBlockStartBreak`, block destroyed, repair item, enchantability, warp and update.
- Validate mining stone/ore/obsidian, area mining edges, unbreakable blocks, protected blocks, durability damage, fortune/silk interaction if applicable.
- Ensure material stats in `ConfigItems` match reference `PRIMALVOID`, not generic elemental material unless intentionally equivalent.

**Критерии приемки:**
- [ ] Primal Crusher mines the same blocks at the same effective speed as reference.
- [ ] Area mining affects the same block set and damage/repair behavior.
- [ ] Warp/enchantability/material stats match reference.

**Риски / зависимости:**

Dependency: protected-block behavior may depend on Stage 4 block wrappers; server mining behavior is Stage 5.

### GAP-11: Utility/basic item behavior contains many Stage 5 placeholders

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/items/ItemBottleTaint.java:24-32`
- `src/main/java/thaumcraft/common/items/ItemBucketDeath.java:29-38`
- `src/main/java/thaumcraft/common/items/ItemBathSalts.java:22-30`
- `src/main/java/thaumcraft/common/items/ItemCompassStone.java:23-32`
- `src/main/java/thaumcraft/common/items/ItemKey.java:42-45`
- `src/main/java/thaumcraft/common/items/ItemManaBean.java:37-39`
- `src/main/java/thaumcraft/common/items/ItemResearchNotes.java:42-47`
- `src/main/java/thaumcraft/common/items/ItemSanitySoap.java:25-32`
- `src/main/java/thaumcraft/common/items/ItemLootBag.java:43-50`
- `src/main/java/thaumcraft/common/items/ItemEldritchObject.java:59-76`
- `src/main/java/thaumcraft/common/items/ItemEssence.java:67`

**Референс:**
- `thaumcraft_src/thaumcraft/common/items/ItemBottleTaint.class`
- `thaumcraft_src/thaumcraft/common/items/ItemBucketDeath.class`
- `thaumcraft_src/thaumcraft/common/items/ItemBathSalts.class`
- `thaumcraft_src/thaumcraft/common/items/ItemCompassStone.class`
- `thaumcraft_src/thaumcraft/common/items/ItemKey.class`
- `thaumcraft_src/thaumcraft/common/items/ItemManaBean.class`
- `thaumcraft_src/thaumcraft/common/items/ItemResearchNotes.class`
- `thaumcraft_src/thaumcraft/common/items/ItemSanitySoap.class`
- `thaumcraft_src/thaumcraft/common/items/ItemLootBag.class`
- `thaumcraft_src/thaumcraft/common/items/ItemEldritchObject.class`
- `thaumcraft_src/thaumcraft/common/items/ItemEssence.class`

**Что не совпадает:**

Multiple current item classes return success/pass with `TBD` comments instead of original behavior. Examples: taint bottle projectile is missing, death water placement is missing, bath salts/sanity soap warp behavior is missing, compass stone targeting is missing, key door behavior is missing, mana bean behavior is missing, research notes are missing, loot bag generation is missing, eldritch object placement/research behavior is missing.

**Что нужно доделать:**

Port utility item use actions, NBT, entity/block interactions and loot/research side effects from reference.

**Как доделать:**
- For each listed item, decompile/reference its original class and port exact `onItemRightClick`/`onItemUse`/tooltip/subitem behavior.
- Reuse existing Stage 3/4 systems for warp, research, worldgen, blocks and fluids; if missing, label only that direct dependency.
- Remove all `TBD` placeholders inside Stage 5 item scope.

**Критерии приемки:**
- [ ] No utility/basic Stage 5 item contains `TBD` placeholder behavior.
- [ ] Each item has reference-matching right-click/use behavior and NBT semantics.
- [ ] Loot/research/warp/fluid side effects are validated in manual scenarios or documented as direct dependencies.

**Риски / зависимости:**

Dependency: research/content unlocks are Stage 9 and fluids/blocks may be Stage 4, but item action entry points are Stage 5.

### GAP-12: Item registrations exist, but resource/model/lang coverage is missing

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigItems.java:203-762`
- `src/main/resources/assets/thaumcraft/`

**Референс:**
- `thaumcraft_src/assets/thaumcraft/textures/items/**`
- `thaumcraft_src/assets/thaumcraft/textures/foci/**` where referenced by focus upgrade icons
- `thaumcraft_src/assets/thaumcraft/lang/**` if present in original jar/resources

**Что не совпадает:**

`ConfigItems` registers many items with registry names and translation keys, but current resource tree lacks `models/item`, `textures/items` and `lang`. The original item textures exist under `thaumcraft_src/assets/thaumcraft/textures/items/`, including foci, baubles, tools, armor, relics and utility items. In 1.12.2, missing item models/lang causes registered items to appear as missing models/unlocalized names even if server behavior exists.

**Что нужно доделать:**

Copy/adapt original item resources and add 1.12 item model JSONs/locale keys for Stage 5 registered items.

**Как доделать:**
- Add `src/main/resources/assets/thaumcraft/textures/items/**` from `thaumcraft_src/assets/thaumcraft/textures/items/**` for Stage 5 items.
- Add `src/main/resources/assets/thaumcraft/models/item/*.json` with stable registry-name model paths for every Stage 5 registered item.
- Add `src/main/resources/assets/thaumcraft/lang/en_us.lang` or compatible 1.12 lang file entries matching current translation keys.
- Keep Phase 8-only dynamic renderers/FX separate, but static item models/lang are required for usable Stage 5 registrations.

**Критерии приемки:**
- [ ] Every item registered in `ConfigItems` has a model or explicit renderer plan and does not show missing model in client smoke.
- [ ] Every Stage 5 translation key resolves in lang resources.
- [ ] Resources copied from original paths preserve visual identity and registry-name linkage.

**Риски / зависимости:**

Dependency: advanced item renderers and focus FX are Phase 8. Static item resources are still needed for Stage 5 acceptance because resources/config/registrations are in scope.

### GAP-13: Robe dye recipes are explicit stubs

**Статус:** отсутствует  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/common/items/armor/RecipesRobeArmorDyes.java:1-15`
- `src/main/java/thaumcraft/common/items/armor/RecipesVoidRobeArmorDyes.java:1-15`

**Референс:**
- `thaumcraft_src/thaumcraft/common/items/armor/RecipesRobeArmorDyes.class`
- `thaumcraft_src/thaumcraft/common/items/armor/RecipesVoidRobeArmorDyes.class`

**Что не совпадает:**

Both recipe classes return `false` with `TBD` comments: `src/main/java/thaumcraft/common/items/armor/RecipesRobeArmorDyes.java:14`, `src/main/java/thaumcraft/common/items/armor/RecipesVoidRobeArmorDyes.java:14`. Reference robe classes also have color NBT/display handling and item-use behavior. Current `ItemRobeArmor`/`ItemVoidRobeArmor` do not show equivalent dye NBT support in the inspected lines.

**Что нужно доделать:**

Port robe dye crafting and color NBT behavior.

**Как доделать:**
- Implement recipe matching/crafting result for robe and void robe dyes in Forge 1.12 recipe APIs.
- Port robe color getter/setter/clear behavior and display NBT semantics from reference.
- Register recipes only in the appropriate recipe registration layer; if Stage 9 owns final recipe registration, document that dependency but keep item NBT support in Stage 5.

**Критерии приемки:**
- [ ] Robe dye recipes match and produce colored robe stacks.
- [ ] Color NBT persists and can be cleared/changed as in reference.
- [ ] Void robe and normal robe behavior are both covered.

**Риски / зависимости:**

Dependency: recipe registration may be Stage 9; item NBT and recipe implementation classes are Stage 5.

### GAP-14: Stage 5 runtime/manual validation is not present

**Статус:** требует проверки  
**Критичность:** high

**Текущая реализация:**
- `docs/PRD.md:295-315`
- `AGENTS.md` runtime smoke validation rules

**Референс:**
- Original gameplay scenarios from `thaumcraft_src/thaumcraft/common/items/**.class`
- Original assets/resources under `thaumcraft_src/assets/thaumcraft/**`

**Что не совпадает:**

PRD explicitly says Stage 5 is not closed or validated: `docs/PRD.md:295-299`. Current code has no evidence in `docs/Stage5.md` before this document because the file was absent. Compile success, if any, would not prove parity per PRD status model: `docs/PRD.md:173-185`.

**Что нужно доделать:**

After blocker/high implementation gaps are fixed, run focused build and runtime/manual validation for Stage 5.

**Как доделать:**
- Run `./scripts/dev.sh compileJava` after implementation changes.
- Run `./scripts/dev.sh build` and `./scripts/dev.sh check-jar` before any checkpoint jar claim.
- Run `./scripts/dev.sh smoke-server` for common/server item behavior changes.
- Run `./scripts/dev.sh smoke-client` if item models/lang/client GUI or Baubles visual/sync paths are touched and display is available.
- Manual scenario matrix: wand focus activation/costs; focus pouch storage; Vis Amulet transfer/relay; Hover Harness; runic baubles; tools/armor repair; Primal Crusher mining; relic use; utility item use.

**Критерии приемки:**
- [ ] Compile/build/check-jar pass or failures are documented as pre-existing/environmental.
- [ ] Server smoke reaches normal ready state with no crash markers after Stage 5 changes.
- [ ] Manual Stage 5 scenario matrix is recorded with pass/fail evidence.

**Риски / зависимости:**

Dependency: client smoke may be environment-limited. If skipped, reason must be concrete and Stage 5 visual/client-dependent acceptance cannot be claimed complete.

## 6. Итоговый checklist закрытия Stage 5

- [ ] GAP-1 closed: wand NBT/core/focus activation/cooldown parity restored.
- [ ] GAP-2 closed: focus upgrade ranks/cooldowns/contracts complete.
- [ ] GAP-3 closed: PRD-listed focus baselines validated in world/entity scenarios.
- [ ] GAP-4 closed: focus pouch and bauble pouch inventory NBT works.
- [ ] GAP-5 closed: Hover Harness and Hover Girdle behavior works with fuel and NBT.
- [ ] GAP-6 closed: Baubles metadata, runic charge and vis discounts match reference.
- [ ] GAP-7 closed: Vis Amulet transfer, storage and relay behavior match reference or direct Stage 4 dependency is resolved.
- [ ] GAP-8 closed: relic placeholders replaced with original behavior.
- [ ] GAP-9 closed: all tools/armor behavior and repairability reviewed and corrected.
- [ ] GAP-10 closed: Primal Crusher stat/mining/warp behavior validated.
- [ ] GAP-11 closed: utility/basic item placeholders replaced.
- [ ] GAP-12 closed: Stage 5 item resources/models/lang are present.
- [ ] GAP-13 closed or explicitly handed to Stage 9 for registration while Stage 5 item NBT support is complete.
- [ ] GAP-14 closed: build, check-jar, smoke and manual scenario evidence recorded.
- [ ] No Stage 5 `TBD`, TODO or placeholder remains in `src/main/java/thaumcraft/common/items/**` or related Stage 5 common code.
- [ ] No forbidden files modified: `thaumcraft_src/**` and `Thaumcraft-1.7.10-4.2.3.5.jar` remain read-only.

## 7. Definition of Done

Stage 5 считается ПОЛНОСТЬЮ завершенной только если:
- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 5 реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 5;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 5 проверены вручную или тестами;
- ./docs/Stage5.md обновлен и не содержит критичных открытых вопросов.

## 8. Решения по ранее открытым вопросам

Эти решения закрывают неопределенность для реализации, но сами по себе не закрывают GAP-1, GAP-4, GAP-7, GAP-8, GAP-12 или runtime validation.

### 8.1 Wand vis NBT strategy

Принято решение использовать reference-compatible wand vis keys как canonical write format:

- новые записи wand vis должны писаться напрямую по `Aspect.getTag()` (`aer`, `terra`, `ignis`, `aqua`, `ordo`, `perditio`), без текущего префикса `vis_`;
- чтение и запись должны использовать reference key; `vis_` не является supported fresh-world format;
- не писать оба формата, чтобы не оставить постоянный риск рассинхрона.

Текущая причина для изменения: ported wand сейчас пишет `vis_`-ключи: `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:42`, `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:85-94`. Reference wand хранит значения под direct aspect tags. `ItemAmuletVis` уже использует direct aspect tags и должен остаться совместимым: `src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java:52-63`.

### 8.2 Item GUI ids

Принято решение перед реализацией item GUI callers ввести constants для GUI ids и прекратить добавлять новые raw literals.

Текущие занятые ids в порте:

- `0` Arcane Workbench: `src/main/java/thaumcraft/common/CommonProxy.java:49-52`
- `1` Research Table: `src/main/java/thaumcraft/common/CommonProxy.java:53-56`
- `2` Arcane Bore: `src/main/java/thaumcraft/common/CommonProxy.java:57-60`
- `3` Alchemy Furnace: `src/main/java/thaumcraft/common/CommonProxy.java:61-64`
- `4` Deconstruction Table: `src/main/java/thaumcraft/common/CommonProxy.java:65-68`
- `5` Focus Pouch: `src/main/java/thaumcraft/common/CommonProxy.java:69`
- `6` Golem: `src/main/java/thaumcraft/common/CommonProxy.java:70-73`
- `7` Pech: `src/main/java/thaumcraft/common/CommonProxy.java:74-77`
- `8` Traveling Trunk: `src/main/java/thaumcraft/common/CommonProxy.java:78-81`
- `9` Thaumatorium: `src/main/java/thaumcraft/common/CommonProxy.java:82-85`
- `10` current Hand Mirror alias: `src/main/java/thaumcraft/common/CommonProxy.java:86`
- `11` current Hover Harness alias: `src/main/java/thaumcraft/common/CommonProxy.java:87`
- `12` current Magic Box conflict with original Thaumonomicon id: `src/main/java/thaumcraft/common/CommonProxy.java:88-91`
- `13` Spa: `src/main/java/thaumcraft/common/CommonProxy.java:92-95`
- `14` Focal Manipulator: `src/main/java/thaumcraft/common/CommonProxy.java:96-99`

Reference ids confirmed from original classes:

- Focus Pouch: `5`
- Thaumonomicon: `12`
- Hand Mirror: `16`
- Hover Harness: `17`

Implementation direction:

- keep Focus Pouch on id `5`;
- add `16` for Hand Mirror and keep current `10` only as a compatibility alias until callers are normalized;
- add `17` for Hover Harness and keep current `11` only as a compatibility alias until callers are normalized;
- resolve the current `12` conflict before implementing Thaumonomicon GUI opening. Preferred direction is to reserve original `12` for Thaumonomicon and move/alias Magic Box only if that container is still required by active code;
- client screens remain Phase 8 because `ClientProxy.getClientGuiElement` currently returns `null`: `src/main/java/thaumcraft/client/ClientProxy.java:48-50`. Stage 5 may still implement server-side item open hooks, NBT and containers where they affect common behavior.

### 8.3 TileVisRelay / Vis Amulet relay boundary

Принято решение считать usable 1.12 `TileVisRelay` equivalent отсутствующим на текущем состоянии codebase.

Facts:

- current `TileVisRelay` is an empty `TileThaumcraft`/`ITickable` stub: `src/main/java/thaumcraft/common/tiles/TileVisRelay.java:1-6`;
- it is registered as `thaumcraft:vis_relay`: `src/main/java/thaumcraft/common/Thaumcraft.java:171`;
- current `ItemAmuletVis` only transfers stored vis into the main-hand wand every 5 ticks: `src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java:123-137`;
- `TileVisNode` and `VisNetHandler` exist, but relay-specific `nearbyPlayers` and consume-effect behavior are not present in current source: `src/main/java/thaumcraft/api/visnet/TileVisNode.java:15-40`, `src/main/java/thaumcraft/api/visnet/VisNetHandler.java:59-83`.

Closure options:

- minimum closure for this decision: document GAP-7 relay charging as blocked by Stage 4 tile/block parity and do not claim Vis Amulet relay parity;
- implementation closure for GAP-7: port a minimal `TileVisRelay extends TileVisNode` with `nearbyPlayers` and `triggerConsumeEffect` no-op/server-safe behavior, then wire `ItemAmuletVis` relay fill. Visual consume effects remain Phase 8;
- full parity closure: restore relay block/tile/network behavior and validate it with runtime/manual scenarios before closing GAP-7.

### 8.4 Stage 5 vs Phase 8 resource boundary

Принято решение не откладывать static item resources на Phase 8.

Stage 5 owns:

- original item textures copied/adapted from `thaumcraft_src/assets/thaumcraft/textures/items/**` into `src/main/resources/assets/thaumcraft/textures/items/**`;
- 1.12 item model JSON coverage under `src/main/resources/assets/thaumcraft/models/item/**` for registered Stage 5 items, or an explicit renderer plan for items that cannot use static generated/item models;
- lang keys matching current `setTranslationKey("thaumcraft.*")` values from `ConfigItems`, not only raw original 1.7.10 `item.Item*.name` keys.

Phase 8 owns:

- dynamic item renderers;
- focus/Thaumometer scan FX;
- GUI screen rendering/polish;
- hover/relay/focus visual and sound polish where it is not needed for server/common behavior.

Current resource gap remains real: current asset tree has no `models/item`, no `textures/items`, and no `lang`, while original item textures/lang exist under `thaumcraft_src/assets/thaumcraft/**`: `src/main/resources/assets/thaumcraft/`, `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:380-455`, `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:520-581`.

### 8.5 Pragmatic checkpoint order

Recommended checkpoint order for Stage 5 implementation:

1. `port: settle Stage5 compatibility decisions` — GUI constants/aliases, wand NBT strategy, documented TileVisRelay boundary.
2. `port: restore wand and pouch NBT compatibility` — canonical wand vis NBT, 18-slot Focus Pouch `Inventory`/`Slot` NBT, Baubles focus pouch shared inventory.
3. `port: restore Vis Amulet relay boundary` — either documented Stage 4 blocker or minimal `TileVisRelay` plus amulet relay fill.
4. `client/resources: add Stage5 item static resources` — original item textures, item model JSONs, adapted lang keys.

### 8.6 2026-05-14 implementation checkpoint

Additional Stage 5 work in the current uncommitted diff:

- Wand core now forwards architect area preview methods through `IArchitect`, uses staff runes for focus potency, keeps frugal upgrade-only, and passes the embedded focus stack into focus animation lookup: `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:48`, `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:247-277`, `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:494-503`, `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:542-560`.
- Focus Pouch subtype semantics were aligned with reference while keeping the 18-slot `Inventory`/`Slot` NBT baseline: `src/main/java/thaumcraft/common/items/wands/ItemFocusPouch.java:22-31`, `src/main/java/thaumcraft/common/items/wands/ItemFocusPouch.java:51-86`.
- Hover Harness fuel consumption now restricts stored fuel to jar item stacks rather than any essentia container and turns hover off when the last ENERGY is consumed: `src/main/java/thaumcraft/common/items/armor/Hover.java:87-113`.
- Robe and Void Robe color NBT, cauldron color clearing, vis discount, void robe warp value, and shared dye recipe logic were restored at common-layer level: `src/main/java/thaumcraft/common/items/armor/ItemRobeArmor.java:22-109`, `src/main/java/thaumcraft/common/items/armor/ItemVoidRobeArmor.java:23-95`, `src/main/java/thaumcraft/common/items/armor/RecipesRobeArmorDyes.java:13-109`, `src/main/java/thaumcraft/common/items/armor/RecipesVoidRobeArmorDyes.java:5-10`.

Validation evidence for this checkpoint:

- `./scripts/dev.sh compileJava` passed.
- `./scripts/dev.sh build` passed.
- `./scripts/dev.sh check-jar` passed.
- `./scripts/dev.sh smoke-server` passed and reached `Done (` with no crash markers.
- `./scripts/dev.sh smoke-client` was attempted because `DISPLAY` was set, but failed before mod initialization with LWJGL display mode discovery: `java.lang.ArrayIndexOutOfBoundsException: 0` in `org.lwjgl.opengl.LinuxDisplay.getAvailableDisplayModes`. Treat this as an environment/display failure, not Stage 5 parity evidence.

Stage 5 remains open after this checkpoint. Remaining high-risk work includes Death Bucket fluid placement because the current port has no `ConfigBlocks.blockFluidDeath`, Hand Mirror cross-dimension transport, full Hover client toggle/sound/movement parity, static item resources/models/lang, recipe registration ownership, and manual in-world scenario validation for every focus, bauble, relic, utility item and armor/tool behavior.

### 8.7 2026-05-14 RECON and wand/pouch compatibility checkpoint

Fresh RECON after merging the Stage 5 work into `master` showed that several GAP descriptions above are stale relative to current source. Important current facts:

- Focus Pouch storage is no longer absent: it has 18-slot `Inventory`/`Slot` NBT, server container slots and wand focus cycling through inventory/Baubles pouches. Remaining work was client GUI routing and protecting the pouch slot while its own GUI is open.
- Wand vis NBT is no longer `vis_`-prefixed: current writes use direct aspect tags via empty `TAG_VIS_PREFIX`.
- Hand Mirror and Death Bucket are implemented at common/server entry-point level, but still need in-world/manual scenario validation before closing their GAP rows.
- `ClientProxy` was accidentally routing many unrelated GUI ids to `GuiHandMirror`; this was a live client/container regression, not Phase 8 polish.

Implemented in the current checkpoint:

- Added a dedicated `GuiFocusPouch` for GUI id `5`: `src/main/java/thaumcraft/client/gui/GuiFocusPouch.java:9-36`.
- Corrected client GUI routing so only Focus Pouch and Hand Mirror return their own GUIs; unrelated unimplemented GUI ids now return `null` instead of `GuiHandMirror`: `src/main/java/thaumcraft/client/ClientProxy.java:50-70`.
- Added Focus Pouch protected-slot handling matching the reference container intent: the source pouch player slot is locked, shift-click from it is rejected, and direct slot clicks are rejected: `src/main/java/thaumcraft/common/container/ContainerFocusPouch.java:20-32`, `src/main/java/thaumcraft/common/container/ContainerFocusPouch.java:78-101`, `src/main/java/thaumcraft/common/container/ContainerFocusPouch.java:104-119`.
- Restored wand vis helper surface used by reference/addon-facing common code: `getAllVis`, `getAspectsWithRoom`, `storeAllVis`, `storeVis`, and single-aspect `consumeVis`: `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:124-153`, `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:249-257`.
- Matched reference non-primal vis-add behavior by returning `0` for non-primal aspects in `addVis`/`addRealVis`: `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:160-181`.
- Made `setFocus` null-safe and removed the current sneaking no-op that suppressed focus right-click activation unlike the reference flow: `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:277-285`, `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:478-487`.

Validation evidence for this checkpoint:

- `./scripts/dev.sh compileJava` passed after Focus Pouch GUI/container changes.
- `./scripts/dev.sh compileJava` passed again after wand helper changes.
- `./scripts/dev.sh build` passed.
- `./scripts/dev.sh check-jar` passed.
- `./scripts/dev.sh smoke-server` passed and reached `Done (` with no crash markers.
- `./scripts/dev.sh smoke-client` was attempted because `DISPLAY=:0`, but failed before mod initialization with the same environment/display failure as the prior checkpoint: `java.lang.ArrayIndexOutOfBoundsException: 0` in `org.lwjgl.opengl.LinuxDisplay.getAvailableDisplayModes`. This is not Stage 5 parity evidence.

Stage 5 remains open after this checkpoint. Next RECON-backed implementation targets are: Hover Harness common container/state authority, Stage 5 static item resources/models/lang, and remaining utility/relic simplifications (`ItemKey`, Thaumometer timed scan, Research Notes, Mana Bean, Resonator, Compass Stone).

### 8.8 2026-05-14 Hover Harness common-layer checkpoint

Implemented in the current checkpoint:

- Replaced the Hover Harness container stub with a one-slot jar container that loads and stores harness NBT key `jar`, binds player inventory, protects the source harness slot, and accepts only `BlockJarItem` stacks with `Aspect.ENERGY`: `src/main/java/thaumcraft/common/container/ContainerHoverHarness.java:29-64`, `src/main/java/thaumcraft/common/container/ContainerHoverHarness.java:73-113`, `src/main/java/thaumcraft/common/container/ContainerHoverHarness.java:116-190`.
- Added a minimal `GuiHoverHarness` and wired `GUI_HOVER_HARNESS` to it so the common container has a usable client entry point; visual polish remains Phase 8: `src/main/java/thaumcraft/client/gui/GuiHoverHarness.java:9-30`, `src/main/java/thaumcraft/client/ClientProxy.java:53-72`.
- Changed Hover state authority to load persisted `hover` NBT into the server/client map only when no map entry exists, then use the map as authoritative. This removes the old `NBT || map` latch that could re-enable hover after a false packet: `src/main/java/thaumcraft/common/items/armor/Hover.java:41-49`, `src/main/java/thaumcraft/common/items/armor/Hover.java:65-89`.
- Hardened `PacketFlyToServer` so server-side hover changes require the authenticated player id and an equipped chest `ItemHoverHarness`; enabling hover also passes fuel validation through `Hover.setHover`: `src/main/java/thaumcraft/common/lib/network/misc/PacketFlyToServer.java:37-47`.

Validation evidence for this checkpoint:

- `./scripts/dev.sh compileJava` passed.
- `./scripts/dev.sh build` passed.
- `./scripts/dev.sh check-jar` passed.
- `./scripts/dev.sh smoke-server` passed and reached `Done (` with no crash markers.
- `./scripts/dev.sh smoke-client` was attempted because `DISPLAY=:0`, but failed before mod initialization with the same environment/display failure: `java.lang.ArrayIndexOutOfBoundsException: 0` in `org.lwjgl.opengl.LinuxDisplay.getAvailableDisplayModes`. This remains environment-limited, not Stage 5 parity evidence.

GAP-5 is advanced but not closed. Remaining Hover work includes the actual client H-key toggle path, reference on/off/periodic sounds, client horizontal motion damping with haste/girdle modifiers, anti-float counter reset parity if safely portable, tooltip parity for stored jar aspects/discounts, and manual in-world fuel/toggle/fall validation.

### 8.9 2026-05-14 static item resource checkpoint

Implemented in the current checkpoint:

- Copied original static item textures from `thaumcraft_src/assets/thaumcraft/textures/items/**` into the port resource tree: `src/main/resources/assets/thaumcraft/textures/items/`.
- Copied original focus upgrade icons from `thaumcraft_src/assets/thaumcraft/textures/foci/**` into the port resource tree: `src/main/resources/assets/thaumcraft/textures/foci/`.
- Added 1.12 item model JSON coverage for all 93 `ConfigItems` registry paths plus Bone Bow pulling-state models under `src/main/resources/assets/thaumcraft/models/item/`; example base model: `src/main/resources/assets/thaumcraft/models/item/focusfire.json:1-6`, bow override model: `src/main/resources/assets/thaumcraft/models/item/itembowbone.json:1-28`.
- Added `en_us.lang` entries for the current `item.thaumcraft.*.name` translation keys and static GUI labels used by Stage 5 containers: `src/main/resources/assets/thaumcraft/lang/en_us.lang:1-101`.
- Registered base item model resource locations for all `ConfigItems` entries on the client side: `src/main/java/thaumcraft/client/ClientProxy.java:24-33`.

Validation evidence for this checkpoint:

- Local resource consistency check found 96 item model JSON files and `0` missing texture references.
- `./scripts/dev.sh compileJava` passed.
- `./scripts/dev.sh build` passed.
- `./scripts/dev.sh check-jar` passed.
- `./scripts/dev.sh smoke-server` passed and reached `Done (` with no crash markers.
- `./scripts/dev.sh smoke-client` was attempted because `DISPLAY=:0`, but failed before mod initialization with the same environment/display failure: `java.lang.ArrayIndexOutOfBoundsException: 0` in `org.lwjgl.opengl.LinuxDisplay.getAvailableDisplayModes`. Client-side missing model confirmation remains environment-blocked.

GAP-12 is advanced but not fully closed by runtime evidence. Remaining resource/client work includes true client smoke validation, metadata-specific model variants/tints for dynamic aspect/subtype items, wand dynamic composition, armor equipped-layer polish, and any Phase 8 renderers that cannot be represented by static generated item models.

### 8.10 2026-05-14 ItemKey and owned-tile checkpoint

Implemented in the current checkpoint:

- Replaced the `ItemKey` `TBD` entry point with reference-backed pressure-plate linking/access behavior for current `blockWoodenDevice` metas `2`/`3`: unlinked keys create linked key copies for the owner or iron keys held by gold-access players, linked keys grant iron/gold access strings using the original `0<name>` / `1<name>` format, and key chat/tooltips use original `tc.key*` language keys: `src/main/java/thaumcraft/common/items/ItemKey.java:41-155`.
- Restored reference-compatible key subtype/glint behavior and added the current 1.12 lang keys for the two subtypes: `src/main/java/thaumcraft/common/items/ItemKey.java:41-54`, `src/main/resources/assets/thaumcraft/lang/en_us.lang:33-35`.
- Restored `TileOwned` owner/access-list persistence with original NBT keys `owner`, `access`, and entry key `name`: `src/main/java/thaumcraft/common/tiles/TileOwned.java:8-47`.
- Changed `TileArcanePressurePlate` to extend `TileOwned`, persist original `setting`, and refresh client block state after tile update packets: `src/main/java/thaumcraft/common/tiles/TileArcanePressurePlate.java:7-29`.
- Assigned `TileOwned.owner` when placing wooden device item blocks so fresh pressure plates have an owner path for key linking: `src/main/java/thaumcraft/common/blocks/ItemBlocks/BlockWoodenDeviceItem.java:29-34`.
- Added original English `tc.key1` through `tc.key11` strings used by key messages/tooltips: `src/main/resources/assets/thaumcraft/lang/en_us.lang:104-114`.

Validation evidence for this checkpoint:

- `./scripts/dev.sh compileJava` passed.
- `./scripts/dev.sh build` passed.
- `./scripts/dev.sh check-jar` passed with `Jar check PASSED: no MCP-named Minecraft field/method references found in /home/stfu/ai/dont/thaumcraft/build/libs/Thaumcraft-1.0.0-universal.jar`.
- `./scripts/dev.sh smoke-server` passed and reached `Done (` with no crash markers in `run/smoke-server.log`.
- `./scripts/dev.sh smoke-client` was attempted because this checkpoint touches item lang/tooltips, but failed before mod initialization with the known local LWJGL display failure: `java.lang.ExceptionInInitializerError` caused by `java.lang.ArrayIndexOutOfBoundsException: 0` in `org.lwjgl.opengl.LinuxDisplay.getAvailableDisplayModes`. This is an environment/display blocker, not Stage 5 parity evidence.

GAP-11 is advanced but not closed. Remaining ItemKey dependencies are the missing Arcane Door block path in the current port, pressure-plate redstone/settings runtime parity in `BlockWoodenDevice`, and manual in-world validation for linked-key creation, access grants, owner/gold-key paths, inventory-full drops, and tooltip localization.

### 8.11 2026-05-14 RECON and Compass Stone checkpoint

Fresh RECON after the ItemKey checkpoint found no literal `TBD` markers remaining under `src/main/java/thaumcraft/common/items/**`, but several Stage 5 items still had no-op or simplified behavior. The smallest isolated GAP-11 target was `ItemCompassStone`, whose reference class does not right-click but tracks nearby sinister/DARK nodes client-side and switches to the active sinister-stone icon while the player is looking toward a recent DARK node.

Implemented in the current checkpoint:

- Restored the reference-style `ItemCompassStone.sinisterNodes` cache, 10-second stale-entry pruning, 256-block look-cone check, rare rarity, and active item-model property for visible sinister nodes: `src/main/java/thaumcraft/common/items/ItemCompassStone.java:21-86`.
- Updated `TileNode` client ticks so DARK nodes add their `WorldCoordinates` to `ItemCompassStone.sinisterNodes` every 50 ticks, matching the original client-side discovery path: `src/main/java/thaumcraft/common/tiles/TileNode.java:144-156`.
- Added a 1.12 item-model override for the active sinister stone texture copied from the original assets: `src/main/resources/assets/thaumcraft/models/item/itemcompassstone.json:1-14`, `src/main/resources/assets/thaumcraft/models/item/itemcompassstone_active.json:1-6`.

Validation evidence for this checkpoint:

- `./scripts/dev.sh compileJava` passed after the Compass Stone/TileNode changes.
- Local item-model texture consistency check found 97 item model JSON files and `0` missing texture references.
- `./scripts/dev.sh build` passed.
- `./scripts/dev.sh check-jar` passed with `Jar check PASSED: no MCP-named Minecraft field/method references found in /home/stfu/ai/dont/thaumcraft/build/libs/Thaumcraft-1.0.0-universal.jar`.
- `./scripts/dev.sh smoke-server` passed and reached `Done (` with no crash markers in `run/smoke-server.log`.
- `./scripts/dev.sh smoke-client` was attempted because this checkpoint touches client item-model/property behavior, but failed before mod initialization with the known local LWJGL display failure: `java.lang.ExceptionInInitializerError` caused by `java.lang.ArrayIndexOutOfBoundsException: 0` in `org.lwjgl.opengl.LinuxDisplay.getAvailableDisplayModes`. This remains an environment/display blocker, not Compass Stone parity evidence.

GAP-11 is advanced but not closed. Remaining utility/relic no-op or simplified targets from RECON include `ItemBucketPure`, `ItemResonator`, `ItemBathSalts`, `ItemResearchNotes`, `ItemEldritchObject`, `ItemEssence`, `ItemLootBag`, `ItemManaBean`, `ItemSanitySoap`, and the remaining relic/tool/bauble validation work listed above.

### 8.12 2026-05-14 Loot Bag checkpoint

Implemented in the current checkpoint:

- Replaced one-item inventory insertion with the original loot-bag flow: 8-12 generated loot drops at the player, coin sound, stack consumption, weighted common/uncommon/rare table selection, rare gear chance for non-common bags, enchanted book handling, and thaumium/void gear candidates where the current port has matching items: `src/main/java/thaumcraft/common/items/ItemLootBag.java:75-188`.
- Restored loot-bag metadata rarity and tooltip behavior using the original `tc.lootbag` text: `src/main/java/thaumcraft/common/items/ItemLootBag.java:57-73`, `src/main/resources/assets/thaumcraft/lang/en_us.lang:27-29`, `src/main/resources/assets/thaumcraft/lang/en_us.lang:107`.
- Added 1.12 item-model overrides for uncommon and rare bag textures copied from the original assets: `src/main/resources/assets/thaumcraft/models/item/itemlootbag.json:1-20`, `src/main/resources/assets/thaumcraft/models/item/itemlootbag_unc.json:1-6`, `src/main/resources/assets/thaumcraft/models/item/itemlootbag_rare.json:1-6`.

Validation evidence for this checkpoint:

- `./scripts/dev.sh compileJava` passed after the Loot Bag changes.
- Local item-model texture consistency check found 99 item model JSON files and `0` missing texture references.
- `./scripts/dev.sh build` passed.
- `./scripts/dev.sh check-jar` passed with `Jar check PASSED: no MCP-named Minecraft field/method references found in /home/stfu/ai/dont/thaumcraft/build/libs/Thaumcraft-1.0.0-universal.jar`.
- `./scripts/dev.sh smoke-server` reached `Done (` with no crash markers in `run/smoke-server.log`; the wrapper command was manually aborted after ready-state logging.
- `./scripts/dev.sh smoke-client` was not rerun for this checkpoint because the same local LWJGL display blocker was already reproduced immediately before this checkpoint (`ArrayIndexOutOfBoundsException` in `LinuxDisplay.getAvailableDisplayModes`). Client model override validation remains environment-blocked.

GAP-11 is advanced but not closed. Remaining utility/relic no-op or simplified targets from RECON include `ItemBucketPure`, `ItemResonator`, `ItemBathSalts`, `ItemResearchNotes`, `ItemEldritchObject`, `ItemEssence`, `ItemManaBean`, `ItemSanitySoap`, and the remaining relic/tool/bauble validation work listed above.
