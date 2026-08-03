# Normalization Inventory: A-011..A-015

Goal-ID: `goal-20260803-eldritch-full-parity-audit-fixes`
Scope: lossless normalization support only; this file does not amend `GOAL.md`, `RECON.md`, `SOURCES.md`, or product files.
Authority/oracle: S-003/S-004 TC4 4.2.3.5 -> S-005 Forge 1.12.2 port; S-007 is advisory for platform/display adaptation only.
Promotion policy: `confirmed_in_scope` (S-002), except the separately undecided legacy-world migration boundary.

## Disposition Rules

- `required`: confirmed in-charter parity/compatibility defect, or the minimum regression evidence for one.
- `preserve`: positive parity, safety, synchronization, lifecycle, or data-preserving adaptation explicitly recommended by the packet and not contradicted by the approved fix plan.
- `constraint`: boundary or safety condition that must remain true while resolving a coupled finding.
- `deferred`: a true scope/persistence question, not silently promoted.
- `duplicate`: retained local item whose facts are covered by another item; no report claim is deleted.
- Severity is copied from the report where present. `high`, `medium`, and `low` retain the report meaning; migration severity is conditional as reported.

## Confirmed In Scope

### A-011 Primal runtime

| Local ID / type / severity | Normalized observed -> expected; exact delta | Affected symbols and report locator | Recommended disposition |
|---|---|---|---|
| A-011-F01 defect / high | `EntityThrowable.shoot(Entity,...)` adds shooter X/Z and airborne Y motion and omits TC4's horizontal muzzle offset. TC4 spawn is eye Y minus `0.1` and horizontal `0.16`; port is eye Y minus `0.1` only. TC4 direction speed is approximately `0.5` with inaccuracy `1.0`; port adds shooter motion (jumping adds about `+0.42` Y). | `EntityPrimalOrb.java:27-32`; TC4 `EntityPrimalOrb.<init>`, `EntityThrowable`; report A-011:51-66. | required; preserve speed `0.5`, inaccuracy `1.0`, gravity `0.001`, ownership, seeker data, server-only spawn. |
| A-011-F02 defect / high | Entity-hit and explicit water entity-type results: TC4 passes `mop.blockX/Y/Z`, which remain `(0,0,0)`; port falls back to `floor(hitVec)`. Ordinary non-seeker special roll is `nextInt(100) <= 1` = `2%`, split into `1%` taint / `1%` node. Port can create a victim-location node after explosion; TC4 normally attempts origin and does nothing. | `EntityPrimalOrb.java:136-147`; TC4 `EntityPrimalOrb.onImpact`, 1.7 `MovingObjectPosition(Entity)`; A-011:68-84. | required; retain block-impact coordinates, seeker suppression, probabilities, and explosion-before-special order. |
| A-011-F03 defect / low | `createNodeAt` accepts `air || oldBlock.isReplaceable`; TC4 accepts air only. Replaceable non-air block is replaced by port, retained by TC4; existing `TileNode` update is not divergent. | `ThaumcraftWorldGenerator.java:128-145`; TC4 `createNodeAt`; A-011:86-97. | required, coupled to R-GEN; narrow orb/node correction and retain 1.12 state/tile synchronization. |
| A-011-F04 defect / medium | Crusher port accepts `ForgeHooks.isToolEffective`, fixed set, or materials `ROCK`, `IRON`, `ANVIL`, `GROUND`, `GRASS`, `SAND`, `CLAY`; TC4 accepts only tool effectiveness or fixed set. A custom material-only block therefore enters the port 3x3 AOE but not TC4. | `ItemPrimalCrusher.java:88-109,119-127`; TC4 `ItemPrimalCrusher.onBlockDestroyed/isEffectiveAgainst`; A-011:99-110. | required; retain fixed exceptions and negative-hardness rejection. |
| A-011-F05 adaptation drift / medium | For a full 3x3, TC4 has one standard primary `BreakEvent`, no helper events; port posts center again plus eight secondary events: `2` primary + `8` secondary = `10` deliveries. Both charge one durability before each helper harvest; canceled secondary retains already-applied durability. Secondary protection checks are appropriate, center repost is not. | `BlockUtils.java:125-135`; `PlayerInteractionManager.tryHarvestBlock`; TC4 `BlockUtils.harvestBlock`; A-011:112-128. | required adaptation refinement; retain secondary checks, fake-player, XP, drops, conversion, durability ordering; remove only center duplicate. |
| A-011-F06 defect / medium | Dual block/tile `IWandable`: TC4 dispatch is block -> stop; port is tile -> stop. Focus dispatch, including Primal firing, is reached only if neither consumes. | `ItemWandCasting.java:597-607`; TC4 `onItemRightClick`; A-011:130-141. | required; preserve `ActionResult`, null fallthrough, coordinates, and focus cooldown boundary. |
| A-011-F08 defect / low | Every 20 entity ticks while damaged: survival repair remains one point on both sides; creative TC4 `damageItem(-1, player)` mutates zero, while port directly sets `max(0, damage-1)` and repairs one. | `ItemPrimalCrusher.java:149-153`; TC4 `ItemPrimalCrusher.onUpdate`, 1.7 `ItemStack.damageItem`; A-011:160-171. | required; retain cadence, zero lower bound, `Unbreakable`, server authority. |
| A-011-F10 defect / low | Instant offhand focus use normally has no active hand; port chooses `MAIN_HAND`, while semantic 1.12 behavior should swing the hand containing `wandStack`. Orb, cost, and cooldown are otherwise correct. | `FocusPrimal.java:56`; TC4 `player.swingItem`; A-011:190-201. | required; use identity-aware `getHandHoldingWand`, not active hand alone. |
| A-011-F11 defect / low | TC4 consumes one `rand.nextFloat()` for every drop before registration/replacement checks; port consumes it only for registered replacement candidates. Threshold remains `r <= (0.2 + 0.075 * fortune) * registeredRate`. Mixed drops shift all later RNG outcomes. | `Utils.java:83-95`; TC4 `Utils.findSpecialMiningResult`; A-011:203-214. | required; preserve copied count, item/meta key, fortune, sound, server mutation. |
| A-012-F01 defect / high | Worn armor receives port `onUpdate` plus `onArmorTick` at `%20==0`, repairing `2`; TC4 1.7 armor inventory is not ordinary-updated and repairs `1`. Stored inventory remains `1` per 20 on both. | `ItemVoidArmor.java:41-49,68-72`; `ItemVoidRobeArmor.java:120-128`; 1.7 `InventoryPlayer.func_70429_k`; 1.12 `decrementAnimations`; A-012:37-57. | required; preserve server guard, living owner, one point, 20 ticks, stored/equipped behavior without changing shared inventory. |
| A-012-F02 defect / medium | Port declares `itemBootsVoidRobe`, FEET, ordinary Void material factor `10`, durability `130`, protection `3`, enchantability `10`, epic, `5%` vis discount, warp `2`, repair meta `16`, robe effects/dye NBT. TC4 has only helmet/chest/legs. Aggregate changes `15%/6 warp` -> `20%/8`; dye recipe accepts boots but color handler omits them. | `ConfigItems.java:123-126,662-684`; `ItemVoidRobeArmor.java:40-205`; `ClientProxy.java:768-774`; TC4 `ConfigItems`; A-012:59-85. | required subject to persisted-ID boundary question; do not weaken legitimate three-piece behavior. |
| A-012-F03 defect / medium | MAINHAND modifiers: TC4 Void sword `7.0`, pickaxe `5.0`, axe `6.0`, shovel `4.0`; port sword `6.0`, pickaxe `4.0`, axe `6.0`, shovel `4.5`; deltas `-1.0`, `-1.0`, `0`, `+0.5`. 1.12 cooldown/attribute plumbing remains required platform behavior. | constructors `ItemVoidSword.java:28-30`, `ItemVoidPickaxe.java:19-21`, `ItemVoidAxe.java:19-21`, `ItemVoidShovel.java:19-21`; `ThaumcraftApi.java:38`; A-012:87-108. | required; item-specific correction, never alter shared Void material. |
| A-012-F04 defect / low | Successful Void Hoe entity hit costs `0` in TC4 1.7; inherited 1.12 `ItemHoe.hitEntity` makes port cost `1`. Both apply Weakness for `80` ticks under the listed PvP/living conditions. | `ItemVoidHoe.java:37-41`; TC4 `ItemVoidHoe.onLeftClickEntity`; 1.7/1.12 `ItemHoe`; A-012:110-122. | required; preserve Weakness, PvP gate, parent return, tilling durability, self-repair. |
| A-012-F05 compatibility defect / low | TC4 exposes `public static WandCap WAND_CAP_VOID`; port registers canonical `WandCap` under `caps.get("void")` but has no `ConfigItems.WAND_CAP_VOID`. Internal NBT runtime works; source linkage can fail compile or with `NoSuchFieldError`. | TC4 `ConfigItems`; `ConfigItems.java:56-64`; `Thaumcraft.java:365-369`; A-012:124-137. | required only if common-class compatibility is in scope; otherwise deferred as explicit boundary decision. |
| A-013-F01 defect / high | Port container has slots but no listener/property sender or client updater. TC4 sends five fields on listener join/change: IDs `0 cook`, `1 burn`, `2 current burn`, `3 vis`, `4 smelt`; port sends none. GUI directly reads client tile. | `ContainerAlchemyFurnace.java:12-74`; `GuiAlchemyFurnace.java:38-50`; `TileAlchemyFurnace.java:410-435`; TC4 container methods; A-013:71-89. | required; retain slots, GUI, five values, 16-bit safety, distance, 1.12 listener API. |
| A-013-F02 defect / medium | Port shift-click tests input slot before fuel. Coal is both fuel and `ENERGY 2`; TC4 fuel-first routes to slot `1`, port routes to aspect slot `0`, so burning does not start. | `ContainerAlchemyFurnace.java:50-72`; `TileAlchemyFurnace.java:382-390`; `ConfigAspects.java:179`; A-013:91-103. | required; preserve fuel-first, fallback input, transfer ranges, empty-stack behavior. |
| A-013-F03 defect / medium | Both advertise DOWN `[1]`, UP `[]`, horizontal `[0]`; TC4 accepts fuel insertion when `side != UP`, so DOWN works. Port rejects every `direction == DOWN`, contradicting its advertised slot. Bottom extraction remains bucket-only. | `TileAlchemyFurnace.java:24-26,393-406`; TC4 `func_94128_d/func_102007_a/func_102008_b`; A-013:105-120. | required; preserve no UP slot, horizontal input, and bottom extraction. |
| A-013-F04 defect / medium | Every fifth server tick TC4 executes `int pt = heat--` unconditionally. At heat `0` plus four FIRE, TC4 `-1+4=3`; port guarded decay gives `0+4=4`. With no vis, TC4 accumulates negative debt; port remains `0`, then charges immediately. | `TileAlchemyFurnaceAdvanced.java:88-109`; TC4 `func_145845_h`; `VisNetworkChargingRuntimeTest:222-240`; A-013:122-138. | required; preserve 5-tick cadence, 50-unit FIRE/ENTROPY/WATER requests, `<= maxPower`, server mutation. |
| A-013-F07 defect / low | Formation FX: TC4 loops `aa=-1..1`, `bb=0..1`, `cc=-1..1` = `18` sparkle packets; port sends only center and `center.up()` = `2`. One sound and radius `32` remain. | `WandManager.java:1164-1173`; TC4 `createAdvancedAlchemicalFurnace`; A-013:170-182. | required; restore packet volume/positions without changing formation state, authority, sound. |
| A-013-F10 defect / low | Basic furnace activation: TC4 opens GUI `9` only for meta `0`, correct tile, and `!player.isSneaking()`. Port opens for sneaking too. | `BlockStoneDevice.java:192-200`; TC4 `func_149727_a`; A-013:220-232. | required; retain GUI ID, server creation, client interaction handling. |
| A-013-F11 defect / low | Existing alembic with AIR, filter FIRE, amount `<32`, furnace AIR: TC4 transfers one AIR at normal `20/40` interval because first pass does not inspect filter; port transfers none. Empty/matching filters remain parity. | `TileAlchemyFurnace.java:102-143`; `TileAlembic.java:19-55`; TC4 `func_145845_h`; A-013:234-246. | required if TC4 runtime parity includes NBT/addon-visible states; record reachability uncertainty separately. |
| A-014-F01 defect / medium | Reservoir no-coordinate callback: TC4 returns `null`, permitting focused-wand fallthrough; port returns held `wandstack`, and `ItemWandCasting` treats non-null as immediate success. Coordinate-facing callback remains separate. | `TileEssentiaReservoir.java:269-270`; `ItemWandCasting.java:592-622`; TC4 reservoir and wand callback; A-014:39-51. | required; change only no-coordinate return and preserve coordinate facing/swing/sync/return `0`. |
| A-014-F02 defect / low | TC4 `containerContains(tag)` delegates `AspectList.getAmount(tag)`; null returns `0`. Port null explicitly sums all stored aspects. Non-null queries remain exact; internal total users must remain total. | `TileEssentiaReservoir.java:145-153,186-195,222-235`; `AspectList.java:119-124`; A-014:53-65. | required; public null -> `0`, replace internal total calls with `visSize()`/private total, preserve capacity/suction/total amount. |
| A-015-F02 behavior divergence / medium | TC4 has no checker right-click override, so inherited result is `PASS` with no message. Port server-side sums permanent+sticky+temporary, sends one `tc.sanity` action-bar `Current Warp: <total>`, and returns `SUCCESS`, including offhand. | `ItemSanityChecker.java:30-42`; exact TC4 class; A-015:77-90. | required for strict parity; boundary only if product explicitly chooses this extension. |
| A-015-F03 visual divergence / medium | TC4 draws `20x76` gauge directly. Port wraps it in `scale(0.625F,0.625F,1)`: nominal gauge becomes `12.5x47.5`, fill width `8` -> `5`, origin `(1,1)` -> `(0.625,0.625)`. | `RenderEventHandler.java:297-360` (scale at `330`); TC4 `renderSanityHud`; A-015:92-105. | required; remove only scale, retain coordinates/UV/colors/category arithmetic and Forge matrix setup. |
| A-015-F04 render-order divergence / low | TC4 runs sanity HUD at render-tick `END`, after ordinary overlay. Port draws inside `RenderGameOverlayEvent.Text` before Forge's later left/right demo and F3 strings, allowing text over gauge. | `RenderEventHandler.java:284-295`; Forge `GuiIngameForge.renderHUDText`; TC4 `renderTick`; A-015:107-118. | required; use post-overlay ordering, once/frame, safe GL restore. |
| A-015-F05 robustness divergence / low | Public setters and packet application clamp warp, but port `deserializeNBT` assigns four integers directly. Malformed negative ForgeCaps can yield negative server warp while client packet copy clamps to zero. TC4 load passes values through clamping setters. | `PlayerKnowledgeCapability.java:61-109,337-373`; `PacketSyncWarp.java:54-68`; TC4 `ResearchManager.loadPlayerData`; A-015:120-131. | required; clamp or route all four (`warpPerm`, `warpSticky`, `warpTemp`, `warpCounter`) through setters. |

### A-015-F01: true boundary question, not silently promoted

`A-015-F01` is a migration defect/candidate, high for migrated TC4 worlds and none for fresh worlds. TC4 resolves UUID/current, legacy UUID, `.thaum` and `.thaumback`, imports `Thaumcraft.eldritch`, `.temp`, `.sticky`, `.counter`, and when sticky is absent splits the old value equally into permanent and sticky. The port reads only `ForgeCaps/thaumcraft:player_knowledge` (`warpPerm`, `warpSticky`, `warpTemp`, `warpCounter`).

Locators: TC4 `EventHandlerEntity.playerLoad/playerSave`, `ResearchManager.loadPlayerData/savePlayerData`, `PlayerKnowledge.setWarpPerm/setWarpSticky/setWarpTemp`; port `EventHandlerEntity.java:674-700`, `PlayerKnowledgeCapability.java:323-373`, `PlayerKnowledgeProvider.java:49-78`; report A-015:56-75. Recommended safe adaptation: one-time server importer at player load, exact UUID/legacy/backup resolution, exact four keys, no-sticky half-to-both conversion, authoritative ForgeCaps precedence, explicit marker, existing join `PacketSyncWarp`, no source-file deletion. Disposition: `deferred` / `blocking_question` until support for opening TC4 worlds and migration precedence is explicitly approved. Required fixtures if approved: current UUID, legacy UUID, backup fallback, no-sticky conversion, existing capability precedence, repeat-login idempotence.

## Preserve Controls and Benign Deltas

The following are first-class `preserve` items. Their exact local IDs, values, and hazards remain authoritative in the cited report; the condensed inventory retains every control and no recommendation changes them.

### A-011

- `A-011-F07` adaptation drift / low: submerged water impact marks orb dead then port returns; TC4 continues `super.onUpdate()` and may produce a same-tick second block collision. First entity impact uses radius `2`, `2%`; subsequent submerged block uses radius `4`, `11%` split `5.5%/5.5%`. Preserve early return as safety unless strict duplicate-impact parity is explicitly selected. Locator `EntityPrimalOrb.java:43-47`; A-011:143-158.
- `A-011-F09` benign delta / low: port skips seeker steering at squared distance `<=0.01` (linear `<=0.1`) versus TC4 division at any distance; port `grow(16.0D)` on a `0.25` AABB extends the nominal point shell slightly/asymmetrically. Preserve nearest squared-distance, exclusions, 20-tick delay, acceleration `0.2`, clamp `[-0.2,0.2]`. `EntityPrimalOrb.java:74-87`; A-011:173-188.
- `A-011-PC01..PC07` positive parity: focus color `0xA5A1C1` (`10854849`), prefix `FP`, seeded 200 ms costs `50/100/150/200/250` per six aspects (`0.5..2.5` vis), atomic debit/cooldown `500` ms, rank upgrades; orb gravity `0.001`, border `0.1`, lifetime `>5000`, jitter after tick `20`, seeker protocol and targeting; explosion radii `2`/`4`, exact `2%` and `11%` rolls, taint ten attempts, null source, 36 impact FX; rod periods `200` and `50`, one vis `100` units, strict `<10%`, capacities thresholds `750/1750/2500`; Crusher `PRIMALVOID` level `5`, uses `500`, efficiency `8`, attack contribution `4`, enchantability `20`, base attack `7.5`, classes/effectiveness, rarity/warp/repair; AOE sneak bypass, side planes, `aa/bb=-1..1`, max nine, hardness/modification checks and one durability per attempt; harvest/drop conversion, creative/survival behavior, special multiplier `.2 + fortune*.075`, survival repair one per 20. Full values and symbols: A-011:230-293.
- Platform constraints: preserve server authority for vis, recharge, spawn/terrain, harvest, drops, repair; spawn-data seeker/owner; 1.12 state/pos/facing/hand/sound/action results; secondary protection events but no center duplicate; player check, node synchronization, and reach-attribute adaptation. A-011:294-312.

### A-012

- `A-012-PC01..PC02`: Void material `VOID`, level `4`, uses `150`, efficiency `8`, attack contribution `3`, enchantability `10`; all tools max `150`, hoe enchantability `5`; repair charm meta `15`, armor ingot meta `16`; stored tool repair one per 20; creative negative repair does not mutate. A-012:141-153.
- `A-012-PC03..PC05`: Weakness sword `60`, other tools `80`, PvP gates and server potion mutation; base armor slot semantic protection head/chest/legs/feet `3/7/6/3`, durability `110/160/150/130`, warp `1`; legitimate robes head/chest/legs durability `110/160/150`, protection `3/7/6`, epic, runic `0`, discount `5%`, warp `2`, special armor formulas and head-only revealer. A-012:155-183.
- `A-012-PC06..PC07`: dye recipe single robe + dye, NBT/count `1`, default color `6961280` (`0x6A3880`), legacy RGB/brightness algorithm, wash one cauldron level; Void cap tag `void`, modifier `.8`, item meta `7`, inert meta `8`, cost `9`, texture, `CAP_void`, NBT `cap`, fallback iron, discount order/clamp `.1`. A-012:185-201.
- Platform constraints: armor array ordering `{3,6,7,3}` maps Forge slots to TC4 semantic `{3,7,6,3}`; `EntityEquipmentSlot`; `ItemStack.EMPTY`; dye API; server-authoritative potion/repair; 1.12 cooldown/sweep; color handlers; empty `super.onArmorTick`. A-012:203-213.

### A-013

- `A-013-F05` benign/adaptation / medium: port syncs on any heat/power change, normally every five ticks; TC4 updates only when `pt/50 != heat/50`. Preserve explicit dirty/block/tile synchronization and relight behavior; defer cadence optimization absent measured scale harm. `TileAlchemyFurnaceAdvanced.java:88-109,143-184`; A-013:140-153.
- `A-013-F06` benign/data-preserving / medium: port migrates up to `50` stored essentia during formation; TC4 discards aspect list, while both drop input/fuel. Preserve no duplication/loss, rollback, and do not translate basic burn/cook to advanced heat. `WandManager.java:1061-1120`; A-013:155-168.
- `A-013-F08` benign/lifecycle / low: port persists/syncs `processed` and `destroy`; TC4 resets/omits them. Preserve server-only teardown authority and one-tick restoration. `TileAlchemyFurnaceAdvanced.java:41-62,73-83`; A-013:184-200.
- `A-013-F09` benign/save compatibility / low: port persists `CurrentBurnTime` and honors saved `Vis`; TC4 recomputes burn from current fuel and resets vis from aspects. Preserve legacy keys, normal aspect truth, and WIP-save compatibility. `TileAlchemyFurnace.java:217-262`; A-013:202-218.
- `A-013-F12` benign/structure-integrity / low: formed creative/pick exposure `1 -> 0`, pick item -> `EMPTY`; preserve hidden internal block unless contract explicitly chooses literal creative parity. `BlockAlchemyFurnace.java:78-80,225-229`; A-013:248-261.
- `A-013-F13` benign/compatibility / low: port idempotently recognizes formed layout with zero cost and migrates one guarded old lower layout; TC4 returns false for formed and has no old layout. Preserve research gate, zero-charge idempotence, strict safety, cost, and inventory accounting. `WandManager.java:891-934,952-1008,1031-1120`; A-013:263-279.
- `A-013-F14` benign/safety / low: port re-resolves stale nozzle centers, clears references, and returns null/zero for empty source; TC4 can retain stale reference and unsafe empty access. Preserve outward-only, output-only, first-aspect, exact removal, zero suction, no extended tube. `TileAlchemyFurnaceAdvancedNozzle.java:12-168`; A-013:281-294.
- `A-013-PC01..PC09`: formation rings/metas and cost FIRE/WATER/ORDER `50` each; teardown/drop mapping and restoring guard; processing one item, capacity `500`, costs `2*aspect`, ENTROPY/WATER aspect total, cooldown formula; charge requests `50` every five ticks and render threshold `heat>100`, vis/light bounds; nozzle first aspect, boolean comparator, no suction; basic two-slot smelting, fuel, boost, alembics and formula; GUI ID `9`, exact slot/gauge coordinates; original NBT/update and relight; namespaced block/tile identities and no slave. Exact controls and symbols: A-013:296-359.
- Platform constraints: ForgeDirection side translation, tick/update APIs, empty stacks, block states, packet/update tags, notify/relight/comparator/restoration guards, TESR/OBJ transforms, and preservation of data-integrity deltas. A-013:361-372.

### A-014

- `A-014-PC01..PC03`: capacity `256`, mixed non-null aspects, partial add/remainder, all-or-nothing take, unsupported bulk take, copy semantics; configured-face transport, suction `24` below full/`0` full, minimum `24`, one-unit five-tick pull checks; default down face, null sentinel for `ForgeDirection.UNKNOWN`, placement/opposite orientation, wand orientation/sync. A-014:69-94.
- `A-014-PC04..PC07`: golem source/destination selection and amount limits; NBT `Aspects` and byte `face`, update sync and client animation; comparator `floor(total/max*14)+1`, spill `total/16`, post-increment `spills+1`, goo/gas, full eight-quanta flux state represented by 1.12 level `7`; model/TESR/TEISR bounds, six rotations, liquid `3/16..13/16`, height `10/16`, alpha `.9`, brightness `200`, exact assets. A-014:96-128.
- Platform constraints: null `EnumFacing` unknown sentinel, block states/TESR/TEISR, finite-fluid level `7` representing eight quanta, dirty + `notifyBlockUpdate`, safe `instanceof`, optional NBT readers. A-014:130-137.

### A-015

- `A-015-PC01..PC02`: checker stack `1`, no subtype, max damage `0`, uncommon, tab, no NBT/durability; selected main-hand/current hotbar only, both perspectives, rendered-frame recalculation, no item tick/key/animation. A-015:135-148.
- `A-015-PC03..PC06`: categories permanent/sticky/temp only, sum and >100 scaling, integer gap/heights, layer order and exact overflow threshold; exact HUD coordinates `(1,1)`, `20x76`, UVs `152/200/176/216`, colors temp `(1,.5,1)`, sticky `(.75,0,.75)`, permanent `(.5,0,.5)`, byte-identical textures; generated item texture and no original text/input. A-015:150-181.
- `A-015-PC07..PC08`: capability packet aggregation is semantically equivalent for normal nonnegative values; normal new-1.12 ForgeCaps save/load/clone/join persistence includes three categories and counter. A-015:183-194.
- Platform constraints: generated model/icon, capability replacing maps, one combined packet, Forge overlay/GL state setup, and offhand possession not activating the main-hand HUD. A-015:196-203.

## Duplicates, Conflicts, and Tight Coupling

### Duplicates / overlaps retained

- `A-012-F02` overlaps the registration delta referenced by A-009-F03. Retain A-012's runtime aggregate, dye, warp, and tint facts; do not delete or reclassify the A-009 claim here.
- `A-014-F01` and `A-011-F06` both concern `IWandable`/focus fallthrough, but are not duplicates: A-011 is block-versus-tile priority; A-014 is reservoir no-coordinate return value.
- `A-011-F05` and `A-013-F05` both concern 1.12 synchronization/protection hardening, but are distinct: break-event multiplicity versus tile update cadence/dirtying.
- `A-012-F01` and `A-011-F08` both concern repair cadence but are distinct: equipped armor double callback versus creative Crusher negative repair.
- `A-015-F01` overlaps the central migration unknown already recorded in RECON; retain the exact importer contract and do not promote without a support decision.

### Conflicts / adjudication

- `A-011-F05`: preserve secondary protection events, remove only duplicate center event. This explicitly accepts the safety goal while correcting lifecycle drift.
- `A-011-F07`: preserve dead-projectile early return as safety; restoring TC4's possible second impact is a boundary choice, not an incidental fix.
- `A-013-F05`: preserve broad synchronization/data safety; defer performance reduction until measured harm.
- `A-013-F06`, `F08`, `F09`, `F12`, `F13`, `F14`: report recommendations are data/lifecycle/structure-preserving adaptations and are therefore preserved.
- `A-012-F02`: remove invented boots only after the persisted `thaumcraft:itembootsvoidrobe` policy is decided; TC4 has no compatibility basis, but silent deletion is not authorized.
- `A-012-F05`: restore `ConfigItems.WAND_CAP_VOID` only if common-package linkage is inside the accepted compatibility boundary; `thaumcraft.api.*` is the supported public boundary.
- `A-013-F11`: exact source discrepancy is confirmed; ordinary UI reachability is unknown, but NBT/addon-visible state makes the branch in scope unless explicitly excluded.
- `A-015-F02`: remove added right-click action for strict parity; retaining it is a product extension requiring owner approval.
- `A-015-F01`: deferred/blocking migration boundary. This is the only report recommendation that cannot be adjudicated as preserve or required from the current approved plan.

### Tightly coupled R-groups (provisional; no central outcome edit)

- `R-G-WAND`: A-011-F06 + A-014-F01; preserve null fallthrough and block-first order, then verify Primal focus and reservoir focus behavior together.
- `R-G-NODE`: A-011-F02 + A-011-F03; coordinate entity/water legacy zero coordinates with air-only node eligibility and preserve block-impact coordinates/tile sync.
- `R-G-CRUSHER`: A-011-F04 + A-011-F05 + A-011-F08 + A-011-F11; AOE eligibility, protection-event count, creative repair, and deterministic RNG must not regress harvest/drop semantics.
- `R-G-VOID`: A-012-F01..F04; armor callback repair and tool constructor/combat callbacks share lifecycle-sensitive equipment tests. A-012-F02/F05 remain boundary-gated.
- `R-G-FURNACE-BASIC`: A-013-F01..F03 + F10 + F11; container routing, sided insertion, GUI activation, and alembic transfer require one basic-furnace runtime matrix.
- `R-G-FURNACE-ADVANCED`: A-013-F04 + F07; heat debt and formation FX are independent behavior changes but share formation/charging checkpoint evidence. Preserve F05/F06/F08/F09/F12/F13/F14.
- `R-G-SANITY`: A-015-F02..F05; right-click semantics, HUD scale/order, and NBT clamping need separate common/client tests, with F01 isolated behind migration approval.

## Material Test Debt Inventory

The following local test-debt items are material and must remain linked to the finding/control they prove. Existing passing tests do not close these gaps.

| Local debt / linked items | Required missing evidence | Report locator |
|---|---|---|
| A-011-F12 and per-finding controls | Deterministic moving/airborne launch and exact offset; forced block/entity/water/air/solid/replaceable node destinations; material-only custom block; center/secondary event counts/cancellation/durability/XP; dual block+tile `IWandable`; submerged second collision; creative/survival repair; overlap/16-block seeker boundary; offhand swing; mixed registered/unregistered RNG sequence; insufficient-vis cooldown, six-aspect atomic debit, nearest seeker, nine-block durability, sneak bypass, rod threshold/cadence. | A-011:216-228, 314-326 |
| A-012-F01 | Equipped callback composition at tick 20 plus stored-stack control, exactly one repair each. | A-012:221-224 |
| A-012-F02 | Exactly three robe pieces, aggregate `15%`/`6`, retained dye tint routing, after persisted-ID decision. | A-012:223-224 |
| A-012-F03 | Exact MAINHAND modifiers sword `7.0`, pickaxe `5.0`, axe `6.0`, shovel `4.0`, independent 1.12 attack speed. | A-012:225 |
| A-012-F04 | Successful Hoe entity hit durability while retaining Weakness `80` and callback return. | A-012:226 |
| A-012-F05 | Field/binary guard and identity with `WandCap.caps.get("void")`, modifier `.8`, meta `7`, cost `9`, if accepted. | A-012:227 |
| A-012 positive controls | Direct robe dye blend, NBT/count preservation, default `6961280`, removal, wash; full client/server callback composition. | A-012:228-229 |
| A-013-F01 | Listener initial/change sends for all five fields and client updates/live gauges. | A-013:374-379, 391-393 |
| A-013-F02 | Coal/Alumentum fuel-plus-aspects route and fallback. | A-013:379, 394 |
| A-013-F03 | Face/slot insertion-extraction matrix: DOWN fuel, no UP, horizontal input, bucket-only bottom extraction. | A-013:380, 395 |
| A-013-F04 | First powered cycle (`3`), prolonged starvation, negative debt recovery. | A-013:381, 396 |
| A-013-F05/F06/F08/F09 | Notification/dirty count, save continuity, scale/performance; formation rollback/world mutation; chunk unload/reload cooldown and teardown; current-fuel GUI scaling. | A-013:382-384 |
| A-013-F07 | Deterministic `18` sparkle positions plus one sound. | A-013:383, 397 |
| A-013-F10 | Client/server sneak versus non-sneak activation. | A-013:385, 398 |
| A-013-F11 | Existing aspect plus different filter transfer. | A-013:386, 399 |
| A-013-F14 and parity | Stale center removal/replacement/re-resolution; real teardown/drop cascade; manual rendering remains unrun. | A-013:387-389 |
| A-014-F01 | Focused wand reservoir target: coordinate callback once, no-coordinate null, focus continues. | A-014:146-150 |
| A-014-F02 | Mixed contents: null query `0`, non-null exact, capacity/suction/total still `visSize`. | A-014:148-150 |
| A-014 transport/comparator/golem/render controls | Automatic-pull matrix, comparator boundaries `0/1/255/256`, nearest mixed deposit, all six orientations/colors/heights/item view/manual visual. | A-014:150-153 |
| A-015-F01 | TC4 file fixtures: current/legacy UUID, backup, no-sticky split, capability precedence, repeat-login idempotence; only if migration approved. | A-015:66-75 |
| A-015-F02 | Independent inherited PASS/no-message assertion versus extension guard. | A-015:89-90 |
| A-015-F03 | Framebuffer/screenshot dimensions, nominal `20x76` versus scaled `12.5x47.5`. | A-015:103-105 |
| A-015-F04 | F3/demo overlay overlap and post-overlay ordering. | A-015:117-118, 212 |
| A-015-F05 | Negative serialized NBT fixture proving server/client clamp consistency. | A-015:130-131, 210-213 |
| A-015 positive controls | Independent oracle tests for item/model, equip/perspective, exact arithmetic/UV/color/layers, and normal capability sync/persistence; no manual visual claim. | A-015:204-214 |

## Completeness Matrix

Every local F, PC, adaptation, and material test-debt item in A-011..A-015 is indexed below. `Req` means confirmed in scope; `Keep` means preserve; `Def` means deferred/boundary; `Debt` means material test debt represented above.

| Report | F items | PC items | Adaptation / benign items | Test debt |
|---|---|---|---|---|
| A-011 | F01 Req, F02 Req, F03 Req, F04 Req, F05 Req refinement, F06 Req, F07 Keep, F08 Req, F09 Keep, F10 Req, F11 Req, F12 Debt | PC01-PC07 Keep | F05/F07/F09; server authority, spawn data, 1.12 plumbing, player guard, sync/reach constraints Keep | F12 + per-finding list indexed A-011:216-228 and table |
| A-012 | F01 Req, F02 Req/Def persisted-ID gate, F03 Req, F04 Req, F05 Req/Def common-boundary gate | PC01-PC07 Keep | armor ordering, slots, EMPTY, dye API, authority, cooldown/sweep, color handlers, empty super Keep | F01-F05 and positive controls table |
| A-013 | F01 Req, F02 Req, F03 Req, F04 Req, F05 Keep/defer optimization, F06 Keep, F07 Req, F08 Keep, F09 Keep, F10 Req, F11 Req, F12 Keep, F13 Keep, F14 Keep | PC01-PC09 Keep | F05-F06/F08-F09/F12-F14 plus Forge/state/packet/TESR adaptations Keep | all surface rows A-013 table, including seven minimum regression tests |
| A-014 | F01 Req, F02 Req | PC01-PC07 Keep | null unknown sentinel, state/TESR/TEISR, finite-fluid level 7, notify/dirty, safe `instanceof`, optional NBT Keep | F01/F02 plus transport/comparator/golem/render rows |
| A-015 | F01 Def/Blocking, F02 Req unless extension approved, F03 Req, F04 Req, F05 Req | PC01-PC08 Keep | model/capability/packet/overlay/offhand adaptations Keep | F01-F05 plus positive-control rows |

Completeness result: all local `F-*`, `PC-*`, report-listed adaptation/benign items, unknowns/constraints, and material test-debt entries from A-011 through A-015 have a retained source ID, report locator, disposition recommendation, and matrix index. No product, report, or central-ledger file is changed by this normalization artifact.
