# Audit Packet: A-011 - Primal runtime

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-011
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare Primal gameplay runtime against Thaumcraft 4.2.3.5 for `FocusPrimal`, `EntityPrimalOrb`, `WandRodPrimalOnUpdate`, `ItemPrimalCrusher`, and directly necessary wand, targeting, world-generation, harvest, and special-drop helpers.
- Required surfaces: Input/use lifecycle, random costs, cooldown, target selection, projectile launch/motion/collision, damage and terrain effects, spawn synchronization and server authority, rod recharge, Crusher combat/durability/repair, AOE orientation and eligibility, harvest/drop semantics, and edge cases.
- Anti-scope: Recipes, research declarations/metadata, and registry declarations; unrelated foci, tools, projectiles, and world generation; product edits; central Goal Ledger edits.
- Oracle and comparison direction: S-003/S-004 Thaumcraft 4.2.3.5 bytecode -> S-005 Forge 1.12.2 port.
- Expected evidence: Exact CFR decompilation of the four TC4 classes and only directly necessary helpers, current source comparison, Forge 1.12 dependency inspection where API behavior determines semantics, and focused existing-test execution.
- Read/write permissions: Product files and central Goal Ledger files read-only; only this report writable.
- Stop conditions: Every requested runtime surface has exact evidence or an explicit test gap, platform adaptations are separated from defects, positive parity is recorded, and no product path is changed.

## Coverage Performed

- Port classes:
  - `src/main/java/thaumcraft/common/items/wands/foci/FocusPrimal.java`
  - `src/main/java/thaumcraft/common/entities/projectile/EntityPrimalOrb.java`
  - `src/main/java/thaumcraft/common/items/wands/WandRodPrimalOnUpdate.java`
  - `src/main/java/thaumcraft/common/items/equipment/ItemPrimalCrusher.java`
- Direct port helpers:
  - `ItemWandCasting`: vis consumption, focus dispatch, cooldown lifecycle, hand resolution, rod update authority.
  - `WandManager`: millisecond cooldown storage.
  - `BlockUtils`: Crusher AOE harvest and following-drop conversion.
  - `EventHandlerWorld`: special-mining drop replacement.
  - `Utils`: special-mining RNG and replacement semantics.
  - `ThaumcraftWorldGenerator`: rare orb node placement.
- Exact TC4 oracle classes decompiled from S-003:
  - `thaumcraft/common/items/wands/foci/ItemFocusPrimal.class`
  - `thaumcraft/common/entities/projectile/EntityPrimalOrb.class`
  - `thaumcraft/common/items/wands/WandRodPrimalOnUpdate.class`
  - `thaumcraft/common/items/equipment/ItemPrimalCrusher.class`
  - Direct helpers `ItemWandCasting`, `WandManager`, `BlockUtils`, `EntityUtils`, `EventHandlerWorld`, `Utils`, and `ThaumcraftWorldGenerator`.
- Platform dependency behavior inspected:
  - Forge 1.12.2 `EntityThrowable`, `RayTraceResult`, `PlayerInteractionManager`, `ItemStack`, `Item`, `Block`, and `ForgeHooks` from the configured `forgeBin` jar.
  - Vanilla 1.7.10 `EntityThrowable`, `MovingObjectPosition`, and `ItemStack` source behavior needed to resolve constructor launch, entity-hit coordinates, and negative durability repair.
- Existing focused tests inspected and executed:
  - `EntityPrimalOrbParityTest`
  - `EntityPrimalOrbParityStaticGuardTest`
  - `ItemPrimalCrusherParityTest`
  - `WandRodPrimalOnUpdateStaticGuardTest`
- Uncovered validation: No forced-RNG in-game run, moving-shooter cast, submerged collision, protected AOE break, offhand visual check, or dual-`IWandable` addon fixture was executed.

## Atomic Findings

### A-011-F01 - Orb launch inherits shooter motion and loses the TC4 muzzle offset

- Type: defect
- Severity: high
- Confidence: high
- Port evidence: `EntityPrimalOrb.java:27-32` calls `super(world, shooter)` and then `shoot(shooter, shooter.rotationPitch, shooter.rotationYaw, 0.0F, 0.5F, 1.0F)`. Forge 1.12 `EntityThrowable.shoot(Entity,...)` adds `shooter.motionX` and `motionZ`, and adds `motionY` when the shooter is airborne.
- Original evidence: TC4 `EntityPrimalOrb.<init>` only calls `super(world, shooter)`, sets `seeker`, and records the owner ID. Its overrides return gravity `0.001F` and throwable velocity `0.5F`. The 1.7 throwable constructor computes direction at speed `0.5F` with inaccuracy `1.0F`, does not add shooter motion, and subtracts a horizontal `0.16F` muzzle offset before launch.
- Exact lifecycle/numeric delta:
  - TC4 spawn: shooter eye position minus `0.1` Y and minus `0.16` horizontally in facing direction.
  - Port spawn: shooter eye position minus `0.1` Y, with no horizontal `0.16` offset.
  - TC4 velocity: direction normalized and scaled to approximately `0.5`, plus Gaussian inaccuracy only.
  - Port velocity: the same approximately `0.5` directional component plus shooter X/Z velocity and, if airborne, shooter Y velocity.
- Reproduction/effect: Fire horizontally while jumping. The port adds the normal jump velocity, approximately `+0.42` Y, to the orb; TC4 does not. Sprinting, falling, flying, or knockback similarly changes range and collision timing only in the port.
- Regression hazards: Preserve speed `0.5`, inaccuracy `1.0`, gravity `0.001`, thrower ownership, seeker spawn data, and server-only spawning. A correction must not replace direction-only launch with another API that again inherits shooter movement.
- Test gap: `EntityPrimalOrbParityTest:31-43` uses a stationary player and accepts only total speed `0.4..0.6`; it cannot detect inherited velocity or spawn offset.
- Candidate disposition: required.

### A-011-F02 - Entity and forced-water impacts can create nodes at the victim instead of TC4's zero coordinates

- Type: defect
- Severity: high
- Confidence: high
- Port evidence: `EntityPrimalOrb.java:136-147` takes `result.getBlockPos()`, then falls back to `new BlockPos(result.hitVec)` when the result is an entity hit.
- Original evidence: TC4 `EntityPrimalOrb.onImpact` passes `mop.blockX`, `blockY`, and `blockZ` directly to `createRandomNodeAt`. The 1.7 `MovingObjectPosition(Entity)` constructor sets type/entity/hit vector but leaves all three block-coordinate fields at their zero defaults.
- Exact lifecycle/numeric delta:
  - Ordinary non-seeker special roll remains `nextInt(100) <= 1.0F`, which is 2 outcomes of 100, or 2%.
  - The following boolean splits this into 1% taint and 1% node branches.
  - TC4 entity-hit node branch attempts `(0,0,0)`; in a normal Overworld this is occupied and node creation does nothing.
  - Port entity-hit node branch attempts `floor(hitVec)` at the struck living entity and may create a node there after the explosion.
  - The explicit water call on both sides constructs an entity-type ray result for the orb itself. TC4 again supplies zero block coordinates; the port falls back to the orb's current hit vector.
- Reproduction/effect: Use deterministic RNG to force `nextInt(100)` to 0 or 1 and the node-side boolean on an entity impact over air/replaceable space. The port can place a node at the victim; TC4 normally attempts origin and places none.
- Regression hazards: Preserve the original node/taint probability, seeker suppression, explosion-before-special ordering, and actual block-impact coordinates. Do not globally force all node calls to origin; only non-block ray results carry the legacy zero-coordinate behavior.
- Test gap: No current test invokes entity impact with forced RNG and observes `createRandomNodeAt` destination or resulting `TileNode`.
- Candidate disposition: required.

### A-011-F03 - Rare orb nodes may replace non-air replaceable blocks

- Type: defect
- Severity: low
- Confidence: high
- Port evidence: `ThaumcraftWorldGenerator.createNodeAt` at `ThaumcraftWorldGenerator.java:128-145` places `blockAiry` when the destination is air **or** `oldBlock.isReplaceable(world, pos)`.
- Original evidence: TC4 `ThaumcraftWorldGenerator.createNodeAt` places `blockAiry` only when `world.isAirBlock(x,y,z)` is true, then updates the tile only if the existing/resulting tile is a `TileNode`.
- Exact delta: Node placement eligibility is `air` in TC4 versus `air || replaceable` in the port. Existing `TileNode` update behavior is retained and is not the delta.
- Reproduction/effect: At a forced node-special impact whose destination contains an explosion-resistant but replaceable mod block, TC4 leaves that block untouched; the port replaces it with a node.
- Regression hazards: This helper is shared world-generation infrastructure. Any resolution must preserve intended 1.12 block-state/tile synchronization and should not change unrelated generation without explicit adjudication.
- Test gap: No deterministic test covers rare-impact node placement into a replaceable non-air state.
- Candidate disposition: required for strict parity, but coordinate with shared world-generation users.

### A-011-F04 - Crusher AOE eligibility is broadened from registered/fixed blocks to whole material classes

- Type: defect
- Severity: medium
- Confidence: high
- Port evidence: `ItemPrimalCrusher.java:88-109,119-127` accepts `ForgeHooks.isToolEffective`, the fixed block set, or any state using `ROCK`, `IRON`, `ANVIL`, `GROUND`, `GRASS`, `SAND`, or `CLAY`.
- Original evidence: TC4 `ItemPrimalCrusher.onBlockDestroyed` and `isEffectiveAgainst` accept only `ForgeHooks.isToolEffective(stack, block, meta)` or membership in the fixed `isEffective` block set. No material fallback exists.
- Exact delta: For a block outside the fixed set and without a pickaxe/shovel harvest-tool registration, TC4 AOE eligibility is false regardless of material; the port makes it true for the seven listed material classes.
- Reproduction/effect: Register a custom `Material.ROCK` block without a harvest tool, place it as the selected or neighboring block, and break with the Crusher while not sneaking. The port triggers/includes it in the 3x3 break; TC4 does not.
- Regression hazards: Do not remove the original fixed exceptions such as ice, rails, taint blocks, dirt-family blocks, and obsidian. Preserve independent target eligibility and negative-hardness rejection.
- Test gap: `ItemPrimalCrusherParityTest:47-69` checks selected vanilla set membership and speed, but no unregistered material-only block.
- Candidate disposition: required.

### A-011-F05 - 1.12 protection adaptation duplicates the primary break event and adds secondary break events

- Type: adaptation_drift
- Severity: medium
- Confidence: high
- Port evidence: `BlockUtils.java:125-135` invokes `ForgeHooks.onBlockBreakEvent` for each AOE position. Forge 1.12 `PlayerInteractionManager.tryHarvestBlock` already invokes that hook before it calls `ItemStack.onBlockDestroyed`, so the center position reaches the hook a second time inside the Crusher loop.
- Original evidence: TC4 `BlockUtils.harvestBlock` plays event 2001, removes the block, calls normal harvest/drop behavior, and optionally converts fresh drops. It does not post another Forge break event. The normal primary break has already passed the standard event before the item callback.
- Exact lifecycle delta for a full 3x3 plane:
  - TC4: one standard primary `BreakEvent`; no helper-generated events for center or eight secondaries.
  - Port: two primary `BreakEvent` deliveries and one for each of eight secondaries, for ten total event deliveries.
  - Both implementations charge one durability before each helper harvest call.
  - If a port secondary event is canceled, that block remains but the already-applied durability cost is retained.
- Effect: Protection/logging/quest mods observe additional events and may execute primary side effects twice. Conversely, the added secondary events prevent the Crusher from bypassing 1.12 protection hooks, which is a legitimate platform safety goal.
- Adaptation classification: Posting protection events for secondaries is appropriate 1.12 behavior; reposting the already-authorized center event is not required by that goal and is semantic drift.
- Regression hazards: Preserve secondary protection checks, fake-player handling, XP behavior, harvest drops, following-drop conversion, and durability-before-attempt parity. Avoid suppressing all secondary events merely to remove the center duplicate.
- Test gap: No test counts Forge events, cancels a secondary, or verifies center-event multiplicity and durability.
- Candidate disposition: adjudicate as a required adaptation refinement.

### A-011-F06 - Wandable dispatch priority is tile-first instead of TC4 block-first

- Type: defect
- Severity: medium
- Confidence: high
- Port evidence: `ItemWandCasting.java:597-607` asks an `IWandable` tile first, returns if consumed, and only then asks an `IWandable` block.
- Original evidence: TC4 `ItemWandCasting.onItemRightClick` asks the block first and returns its non-null result before looking up and asking the tile.
- Exact lifecycle delta: For a target where both block and tile implement `IWandable` and both can return a result, TC4 dispatch is `block -> stop`; port dispatch is `tile -> stop`. Focus dispatch, including Primal orb firing, occurs only when neither handler consumes the click.
- Reproduction/effect: A dual-`IWandable` addon block can invoke a different handler in the port and can suppress a Primal focus cast in circumstances where TC4's block handler would control the interaction.
- Regression hazards: Preserve 1.12 `ActionResult` consumption, null-result fallthrough, block/tile coordinate identity, and focus cooldown only when focus dispatch is reached.
- Test gap: No built-in dual-implementation fixture or addon compatibility smoke covers priority.
- Candidate disposition: required for API/runtime parity.

### A-011-F07 - Early return removes TC4's possible same-tick second submerged impact

- Type: adaptation_drift
- Severity: low
- Confidence: high
- Port evidence: `EntityPrimalOrb.java:43-47` calls impact when inside water and returns immediately when the server impact marked the orb dead.
- Original evidence: TC4 `EntityPrimalOrb.onUpdate` calls `onImpact(new MovingObjectPosition(this))` in water and always continues into `super.onUpdate()`. The 1.7 throwable update does not stop merely because `isDead` was set and may report another movement-segment block/entity collision.
- Exact delta:
  - First forced-water result is entity type, so both use radius `2`, 2% special chance, and server death.
  - TC4 can then collide with a block in the same tick. Because the orb is still inside water, that block result uses radius `4` and 11% special chance, creating a possible second explosion/effect roll.
  - Port emits only the first impact because of the explicit return.
- Effect: A submerged orb adjacent to a collision surface can produce two TC4 impacts but one port impact.
- Adaptation classification: The early return prevents processing a dead projectile and is a reasonable safety cleanup, but it is not strict gameplay parity.
- Regression hazards: Removing the return can intentionally restore duplicate explosions; this should be an explicit gameplay decision rather than an incidental control-flow change.
- Test gap: No submerged movement-segment collision test exists.
- Candidate disposition: preserve as safety adaptation unless strict duplicate-impact parity is explicitly required.

### A-011-F08 - Damaged Crushers repair in creative mode in the port

- Type: defect
- Severity: low
- Confidence: high
- Port evidence: `ItemPrimalCrusher.java:149-153` directly sets damage to `max(0, damage - 1)` server-side every 20 entity ticks.
- Original evidence: TC4 `ItemPrimalCrusher.onUpdate` calls `stack.damageItem(-1, (EntityLivingBase)entity)` every 20 ticks when damaged and carried by a living entity. Vanilla 1.7 `damageItem` performs no mutation for a creative player.
- Exact delta: Survival authoritative repair remains one durability per 20 ticks, or one per second. For creative players, TC4 repair is zero while port repair is one per 20 ticks.
- Reproduction/effect: Give a creative player a Crusher with damage 10 and wait 20 server ticks. Port damage becomes 9; TC4 remains 10.
- Regression hazards: Preserve survival repair cadence, lower bound zero, `Unbreakable` behavior, and server authority. The port's server-only mutation is a valid networking adaptation even though TC4 also executed the local call client-side.
- Test gap: No creative-versus-survival repair test exists.
- Candidate disposition: required.

### A-011-F09 - Seeker zero-distance guard and search shell are benign 1.12 safety deltas

- Type: benign_delta
- Severity: low
- Confidence: high
- Port evidence: `EntityPrimalOrb.java:74-87` steers only when squared distance is greater than `0.01D`; `findSeekerTarget` searches `getEntityBoundingBox().grow(16.0D)`.
- Original evidence: TC4 divides steering deltas by the selected squared distance with no lower bound. Its helper searches a zero-sized point AABB expanded by exactly 16 blocks.
- Exact delta:
  - TC4 overlap/near-overlap: division is attempted for any selected target, including zero distance.
  - Port: no steering update at squared distance `<= 0.01`, corresponding to linear distance `<= 0.1`.
  - TC4 search shell is point-centered `+/-16`; port grows the orb's 0.25-wide/high bounding box, extending the candidate shell slightly beyond 16 and asymmetrically in Y.
- Effect: The port avoids NaN/infinite motion at overlap and can acquire entities in the thin bounding-box extension beyond the TC4 point shell.
- Adaptation classification: The lower bound is a defensive numeric safety adaptation. The shell extension follows the 1.12 AABB API and has negligible ordinary effect.
- Regression hazards: Preserve nearest-by-squared-distance selection, no line-of-sight or hostility filter, owner/dead exclusion, 20-tick delay, acceleration `0.2`, and per-axis clamp `[-0.2,0.2]`.
- Test gap: No exact-overlap or 16-block boundary test exists.
- Candidate disposition: preserve and document.

### A-011-F10 - Offhand Primal casts normally swing the main hand

- Type: defect
- Severity: low
- Confidence: high
- Port evidence: `FocusPrimal.java:56` uses `player.isHandActive() ? player.getActiveHand() : EnumHand.MAIN_HAND`.
- Original evidence: TC4 has only the held/current hand and calls `player.swingItem()` unconditionally after the cast attempt.
- Exact lifecycle delta: Instant 1.12 focus right-click does not normally establish an active hand, so an offhand wand falls through to `MAIN_HAND`; the semantic 1.12 adaptation should swing whichever hand contains `wandStack`.
- Effect: Orb spawn, cost, and cooldown are correct, but third/first-person feedback animates the wrong arm for offhand use.
- Regression hazards: Use the established identity-aware `ItemWandCasting.getHandHoldingWand` behavior rather than choosing the currently active hand, which can belong to another stack.
- Test gap: `EntityPrimalOrbParityStaticGuardTest:33-34` positively locks the incorrect expression and has no offhand runtime assertion.
- Candidate disposition: required.

### A-011-F11 - Special-mining replacement consumes a different RNG sequence

- Type: defect
- Severity: low
- Confidence: high
- Port evidence: `Utils.java:83-95` calls `rand.nextFloat()` only after finding both a registered chance and replacement, due to short-circuit evaluation.
- Original evidence: TC4 `Utils.findSpecialMiningResult` copies the drop, immediately executes `float r = rand.nextFloat()`, and only then tests whether the drop key is registered and whether `r <= chance * rate`.
- Exact delta: Every drop consumes one RNG float in TC4; only registered special-mining drops consume one in the port. The replacement threshold itself remains `r <= (0.2 + 0.075 * fortune) * registeredRate` on both sides.
- Reproduction/effect: Process an unregistered drop followed by a registered drop under a fixed world seed. TC4 uses the second float for the registered drop; the port uses the first, potentially changing replacement and all later world-RNG outcomes. Aggregate per-eligible-drop probability is unchanged, but deterministic drop sequences are not.
- Regression hazards: Preserve copied stack count multiplication, item+metadata lookup key, fortune contribution, replacement sound, and server-authoritative event mutation.
- Test gap: No mixed registered/unregistered multi-drop deterministic-RNG test exists.
- Candidate disposition: required for deterministic parity.

### A-011-F12 - Existing tests lock selected source fragments but miss the verified edge contracts

- Type: test_debt
- Severity: medium
- Confidence: high
- Evidence:
  - `EntityPrimalOrbParityTest` proves stationary launch speed and thrower identity only.
  - `EntityPrimalOrbParityStaticGuardTest` checks launch call presence, effects, terrain calls, and the current hand expression, but does not execute impact or targeting behavior.
  - `ItemPrimalCrusherParityTest` proves selected effective-set entries, ordinary speed, one unprotected stone AOE, following-drop count, and secondary durability.
  - `WandRodPrimalOnUpdateStaticGuardTest` checks source strings rather than actual threshold/cadence mutation.
- Missing controls: Moving/airborne launch, legacy spawn offset, forced entity-hit node destination, replaceable node eligibility, water double-impact decision, overlap/boundary seeker behavior, dual-`IWandable` priority, material-only Crusher blocks, duplicate/canceled break events, creative repair, offhand swing, and mixed-drop RNG sequence.
- Regression hazards: Deterministic fakes or seeded RNG are required; do not add flaky probabilistic tests or weaken server-side checks merely to make fixtures easier.
- Candidate disposition: add focused tests alongside adjudicated fixes; preserve explicitly deferred adaptations.

## Positive Parity Controls

### A-011-PC01 - Focus input, cost, cooldown, and upgrades

- Both return focus color `0xA5A1C1` (`10854849`) and sorting prefix `FP`.
- Both costs seed `Random` with `System.currentTimeMillis() / 200`, creating 200 ms buckets. WATER, AIR, EARTH, FIRE, ORDER, and ENTROPY each independently cost one of `50, 100, 150, 200, 250` internal units, or `0.5..2.5` vis.
- Both apply cap/gear/sceptre/frugal consumption modifiers through `consumeAllVis`; all six discounted amounts must be available before any is debited.
- Both set the `500` millisecond cooldown before entering the focus callback. Insufficient vis still consumes the cooldown and still swings; it does not spawn an orb or debit partial vis.
- Both spawn only on the authoritative server after successful all-aspect consumption. The port avoids constructing a discarded client orb; this allocation/lifecycle change has no verified gameplay effect.
- Both retain rank upgrades: frugal at ranks 1, 2, 4, and 5; frugal or seeker at rank 3. Focus enchant acceptance remains true.
- Original passes the wand stack to `getVisCost` while the port passes the focus stack. The method ignores its argument, so this is behaviorally equivalent.

### A-011-PC02 - Orb movement and seeker targeting core

- Gravity is `0.001F`, collision border is `0.1F`, and lifetime terminates when `ticksExisted > 5000`.
- After tick 20, non-seekers add independently seeded jitter `(nextFloat-nextFloat)*0.01` to each axis using seed `entityId + count`.
- Seekers search living entities in a nominal 16-block box, exclude owner entity ID and dead entities, choose nearest by squared distance, aim at bounding-box minimum Y plus `height*0.9`, add `delta/distanceSquared*0.2`, and clamp every motion component to `[-0.2,0.2]`.
- No hostility, team, player, visibility, or line-of-sight filter exists on either side. The closest eligible living entity can be an ally or another player.
- Seeker and owner ID are transmitted as one boolean followed by one integer through additional spawn data.

### A-011-PC03 - Orb damage, explosion, terrain, and feedback

- Orbs deal no direct projectile hit damage or status effect. Damage and block effects come from `createExplosion(null, ..., radius, true)`.
- Ordinary impact radius is `2.0F`; a block-type impact while inside water uses `4.0F`.
- Ordinary non-seeker special chance uses `nextInt(100) <= 1`, exactly 2%; the taint/node boolean gives 1% each. Submerged block impact uses `<=10`, exactly 11%, split to 5.5% each. Seekers suppress both terrain specials.
- Client flight feedback remains six `wispFX4` calls plus one `wispFX2` call per update. Client impact feedback remains 6 x 6, or 36, `wispFX3` calls.
- Taint explosion retains ten attempts; each requires the random boolean, skips already-tainted biomes, writes taint biome through the synchronized helper, finds surface height, and places taint fibres type 0 only above a non-air supporting block.
- Explosion source remains null on both sides, so the thrower is not attributed as explosion owner.

### A-011-PC04 - Rod recharge

- Element-specific rods test every `200` player ticks and add one whole vis, `100` internal units, only while that aspect is below `maxVis/10`.
- The primal staff tests every `50` ticks, builds the set of all primal aspects below `maxVis/10`, chooses exactly one using `player.world.rand`, and adds one whole vis.
- Threshold comparison is strict `<`; an aspect exactly at 10% does not recharge.
- `addVis(...,1,true)` clamps at maximum capacity. No aspect above the threshold is selected.
- `ItemWandCasting.onUpdate` invokes rod updates only on the server and only for player-held inventory stacks. This preserves authoritative NBT mutation.
- Registered capacities imply exact internal thresholds: capacity 75 wand `750`, capacity 175 staff `1750`, and capacity 250 primal staff `2500` internal units.

### A-011-PC05 - Crusher base tool and combat properties

- Tool material remains `PRIMALVOID`: harvest level 5, maximum uses 500, efficiency 8.0, material attack contribution 4.0, and enchantability 20.
- ItemTool base attack contribution remains 3.5, yielding 7.5 attack damage before attributes/effects. The port's `-2.8F` attack speed is the required 1.12 combat-attribute representation; TC4 has no attack-cooldown attribute.
- Tool classes remain exactly pickaxe and shovel. Harvest is allowed for every material except WOOD, LEAVES, and CLOTH.
- Destroy speed is efficiency 8.0 for ROCK, IRON, and ANVIL, plus inherited fixed-set effectiveness; ordinary non-effective states use the inherited speed.
- Rarity EPIC, warp 2, repair with item-resource meta 15, and enchantability 20 match.
- Inherited ItemTool combat durability remains two points per entity hit.

### A-011-PC06 - Crusher AOE geometry, controls, and durability

- Sneaking bypasses AOE and uses the inherited one-block destruction path.
- `onBlockStartBreak` stores the ray-hit side. Sides 0/1 mine the X/Z plane, 2/3 mine X/Y, and 4/5 mine Z/Y.
- Non-sneaking eligible breaks iterate `aa=-1..1` and `bb=-1..1`, including the primary block, for a maximum of nine positions.
- Both reject negative-hardness targets, apply player modification checks, independently test each target's tool/fixed-set eligibility, charge one durability before each attempted helper harvest, and return true.
- A real full 3x3 plane costs nine durability including the center. The existing test deliberately makes the center air and therefore observes eight following drops and eight damage.
- The port safely restricts AOE execution to `EntityPlayer`; TC4 later casts the entity to player and could fail for a non-player caller. This is a benign defensive adaptation.

### A-011-PC07 - Crusher harvest, following drops, special drops, and repair cadence

- AOE harvest retains normal block harvest callbacks and enchantment-aware drops, then converts fresh age-0 `EntityItem` drops into color-2 `EntityFollowingItem` instances carrying copied stacks and original motion.
- Creative harvesting removes blocks without harvest drops; survival checks `canHarvestBlock` before drops. The 1.12 helper additionally preserves break-event XP where available.
- Special-mining handling remains limited to the main-hand Crusher, Elemental Pickaxe, or dowsing Excavation focus. Crusher fortune is read from the held enchantment.
- Base special-mining multiplier remains `0.2F + fortune*0.075F`; replacement preserves input count multiplication and plays the experience-orb pickup sound for changed server drops.
- Survival self-repair remains one durability every 20 ticks. A stack at zero damage does not repair below zero.

## Adaptations To Preserve Or Adjudicate

- Preserve server authority for vis debit, rod recharge, orb spawning/terrain mutation, Crusher harvesting, drop mutation, and durability repair. TC4 client-side incidental mutation calls are not a reason to duplicate authoritative NBT changes.
- Preserve additional spawn-data synchronization for `seeker` and owner entity ID under Forge 1.12 networking.
- Preserve 1.12 block states, `BlockPos`, `EnumFacing`, `EnumHand`, sound categories, and `ActionResult` plumbing where payload and lifecycle remain equivalent.
- Preserve seeker zero-distance protection unless strict reproduction of invalid motion is explicitly selected (`A-011-F09`).
- Preserve secondary AOE protection-event enforcement, but avoid duplicate center delivery when resolving `A-011-F05`.
- Adjudicate the dead-projectile early return as an intentional safety adaptation rather than accidentally restoring TC4 double impacts (`A-011-F07`).
- Preserve the defensive player type check for Crusher AOE and state/tile synchronization in node creation.
- The Crusher ray trace now uses the 1.12 reach attribute rather than TC4's helper with fixed range 10. During a legitimate normal break both identify the actively targeted block face; extended-reach addon behavior should remain an explicit compatibility consideration.

## Hazards And Unknowns

- Rare terrain effects are low-frequency but high-impact. Fixes require deterministic RNG tests; manual sampling cannot establish parity.
- The node helper is shared. Narrow the orb-specific semantic correction before changing global node generation eligibility.
- `BlockUtils.harvestBlock` is shared by systems outside the Crusher. Center-event deduplication must be caller-aware or otherwise proven not to suppress legitimate events.
- Offhand hand resolution must use the actual wand stack identity. Active hand alone is insufficient for instant-use callbacks.
- Following-drop conversion scans nearby age-0 entities. Existing tests prove intended drops are converted but do not prove unrelated simultaneous fresh drops are excluded; this remains a residual integration hazard, not a verified TC4 delta in this packet.
- No visual claim is made for offhand animation or wisp rendering because no manual client run was performed.

## Test Debt Index

- `A-011-F01`: moving, jumping, falling, and stationary launch vector plus exact spawn position.
- `A-011-F02/F03`: forced node branch for block, entity, water, air, solid, and replaceable destinations.
- `A-011-F04`: custom material-only blocks without tool registration.
- `A-011-F05`: event counts and cancellation for center and secondary positions, including durability and XP.
- `A-011-F06`: block+tile dual-`IWandable` priority and focus fallthrough.
- `A-011-F07`: submerged forced impact followed by same-tick block collision.
- `A-011-F08`: survival/creative repair and server/client authority.
- `A-011-F09`: exact overlap and edge-of-search target acquisition.
- `A-011-F10`: offhand wand swing runtime/visual contract.
- `A-011-F11`: seeded mixed eligible/ineligible drop sequence.
- Positive controls needing behavior tests: insufficient-vis cooldown, all-six atomic debit, seeker nearest-target rules, full nine-block durability, sneak bypass, and exact rod threshold/cadence.

## Commands And Results

All shell commands were run from the repository root. CFR was version 0.152. Original and donor archives remained read-only; decompiler output was written outside the repository.

```text
git status --short
unzip -Z1 Thaumcraft-1.7.10-4.2.3.5.jar | grep -Ei '(Primal|Crusher)'
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --outputdir /home/stfu/.local/share/opencode/tool-output/thaumcraft-a11/original --jarfilter 'thaumcraft\.common\.(items\.wands\.foci\.ItemFocusPrimal|entities\.projectile\.EntityPrimalOrb|items\.wands\.WandRodPrimalOnUpdate|items\.equipment\.ItemPrimalCrusher)' --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --outputdir /home/stfu/.local/share/opencode/tool-output/thaumcraft-a11/original --jarfilter 'thaumcraft\.common\.(items\.wands\.(ItemWandCasting|WandManager)|lib\.utils\.(BlockUtils|EntityUtils)|lib\.events\.(EventHandlerWorld|EventHandlerEntity))' --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --outputdir /home/stfu/.local/share/opencode/tool-output/thaumcraft-a11/original --jarfilter 'thaumcraft\.common\.lib\.world\.ThaumcraftWorldGenerator$' --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --outputdir /home/stfu/.local/share/opencode/tool-output/thaumcraft-a11/original --jarfilter 'thaumcraft\.common\.lib\.utils\.Utils$' --silent true
/usr/local/bin/cfr /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar --outputdir /home/stfu/.local/share/opencode/tool-output/thaumcraft-a11/forge112 --jarfilter 'net\.minecraft\.(entity\.projectile\.EntityThrowable|util\.math\.RayTraceResult|server\.management\.PlayerInteractionManager|item\.ItemStack|item\.Item)' --silent true
/usr/local/bin/cfr /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar --outputdir /home/stfu/.local/share/opencode/tool-output/thaumcraft-a11/forge112 --jarfilter 'net\.minecraft\.block\.Block$' --silent true
/usr/local/bin/cfr /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar --outputdir /home/stfu/.local/share/opencode/tool-output/thaumcraft-a11/forge112 --jarfilter 'net\.minecraftforge\.common\.ForgeHooks$' --silent true
./scripts/dev.sh gradle test --tests thaumcraft.common.entities.projectile.EntityPrimalOrbParityTest --tests thaumcraft.common.entities.projectile.EntityPrimalOrbParityStaticGuardTest --tests thaumcraft.common.items.equipment.ItemPrimalCrusherParityTest --tests thaumcraft.common.items.wands.WandRodPrimalOnUpdateStaticGuardTest
git status --short
```

- Exact TC4 decompilation: pass. All requested classes and directly necessary helper methods were recovered.
- Focused tests: pass, `BUILD SUCCESSFUL` in 5 seconds; 9 actionable Gradle tasks, 3 executed and 6 up-to-date.
- Test interpretation: The passing tests establish their narrow positive controls but do not invalidate A-011-F01 through F12 because the missing edge cases are not exercised.
- Build: not run. This audit/report changes no Java or resource product file; AGENTS.md requires a final build only when code changes.
- Runtime smoke: not required and not run because no common/server product path changed.
- Manual client validation: not run; offhand visual behavior remains source-verified only.
- Commit: none.
- Product diff: none.

## Handoff

- Terminal status: complete.
- Defect index: `A-011-F01` inherited shooter motion/muzzle offset; `A-011-F02` entity/water node coordinates; `A-011-F03` replaceable node eligibility; `A-011-F04` material-broadened Crusher AOE; `A-011-F06` reversed wandable priority; `A-011-F08` creative repair; `A-011-F10` offhand swing; `A-011-F11` special-drop RNG sequence.
- Adaptation index: `A-011-F05` protection events need center deduplication while retaining secondary checks; `A-011-F07` dead-projectile early return changes duplicate submerged impacts; `A-011-F09` seeker numeric/AABB safety delta is preserve-worthy.
- Test debt index: `A-011-F12` plus the per-finding controls listed above.
- Positive parity index: `A-011-PC01` focus; `PC02` movement/targeting core; `PC03` explosion/terrain/feedback; `PC04` rod recharge; `PC05` Crusher base/combat; `PC06` Crusher AOE geometry/durability; `PC07` harvest/drops/repair.
- Exact continuation point: none. The packet is ready for orchestrator normalization and adaptation adjudication without further A-011 audit work.
