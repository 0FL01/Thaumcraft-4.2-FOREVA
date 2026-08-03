# Audit Packet: A-017 — Outer entities and special drops

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-017
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare Outer/Eldritch mobs, bosses, projectiles, and special item drops against Thaumcraft 4.2.3.5: `EntityEldritchGuardian`, `EntityEldritchCrab`, `EntityEldritchWarden`, `EntityEldritchGolem`, `EntityEldritchOrb`, relevant shared boss behavior, `EntitySpecialItem`, `EntityPermanentItem`, `EntityFollowingItem`, and drop helpers. Audit attributes, AI/tasks, attacks/effects/cooldowns, spawn/despawn, boss phases, loot/chances/meta/NBT, XP, special-item pickup/physics/persistence, and server/client behavior.
- Anti-scope: Registries, world generation, research declarations, unrelated cultist behavior, unrelated projectiles, product edits, and central Goal Ledger edits.
- Oracle and comparison direction: S-003/S-004 Thaumcraft 4.2.3.5 bytecode -> S-005 Forge 1.12.2 port.
- Questions: Preserve exact attributes, method semantics, effect identities, damage-source handling, AI priorities and range gates, synchronized status handling, spawn initialization, home/despawn behavior, phase transitions, loot metadata/NBT/chances, XP, item-entity physics and persistence, team relationships, and side authority.
- Expected evidence: CFR decompilation from the exact TC4 jar, official MCP stable_12 1.7.10 SRG mappings where names are ambiguous, current-source comparison, focused existing-test inspection, and Forge 1.12 bytecode inspection for inherited lifecycle semantics.
- Read/write permissions: Product and central ledger files read-only; only this report writable.
- Effort/tool budget: Targeted source, bytecode, mapping, and test inspection; no product build or runtime smoke for this read-only packet.
- Stop conditions: Every named entity/drop surface is compared, all semantic deltas and positive controls are atomized, uncertain lifecycle claims are explicitly bounded, and no path outside this report is changed.
- Continuation predecessor: none.

## Coverage Performed

- Port entity surfaces inspected:
  - `src/main/java/thaumcraft/common/entities/EntitySpecialItem.java`
  - `src/main/java/thaumcraft/common/entities/EntityPermanentItem.java`
  - `src/main/java/thaumcraft/common/entities/EntityFollowingItem.java`
  - `src/main/java/thaumcraft/common/entities/projectile/EntityEldritchOrb.java`
  - `src/main/java/thaumcraft/common/entities/projectile/EntityGolemOrb.java`
  - `src/main/java/thaumcraft/common/entities/monster/EntityEldritchGuardian.java`
  - `src/main/java/thaumcraft/common/entities/monster/EntityEldritchCrab.java`
  - `src/main/java/thaumcraft/common/entities/monster/boss/EntityThaumcraftBoss.java`
  - `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchWarden.java`
  - `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchGolem.java`
- Supporting port surfaces inspected:
  - `src/main/java/thaumcraft/common/entities/ai/combat/AILongRangeAttack.java`
  - `src/main/java/thaumcraft/common/entities/ai/combat/AIAttackOnCollide.java`
  - `src/main/java/thaumcraft/common/lib/utils/EntityUtils.java`
  - `src/main/java/thaumcraft/common/lib/utils/BlockUtils.java`
  - `src/main/java/thaumcraft/common/tiles/TileEldritchCrabSpawner.java`
  - `src/main/java/thaumcraft/client/renderers/entity/RenderEldritchWarden.java`
- Oracle surfaces inspected: CFR output under `/home/stfu/.local/share/opencode/tool-output/tc4-a17-cfr/thaumcraft/common/entities/**` for the matching TC4 classes and helpers.
- Mapping evidence inspected: official MCP stable_12 1.7.10 `methods.csv` and `fields.csv` under `/home/stfu/.local/share/opencode/tool-output/mcp-stable-12-1.7.10/`.
- Forge inherited behavior inspected with `javap`: 1.12 `EntityLiving.onInitialSpawn`, `EntityLivingBase.isMovementBlocked`, and `EntityItem` NBT/lifespan update behavior.
- Existing focused tests inspected:
  - `src/test/java/thaumcraft/common/entities/OuterProgressionEntityParityTest.java`
  - `src/test/java/thaumcraft/common/entities/OuterProgressionEntityStaticGuardTest.java`
  - `src/test/java/thaumcraft/common/entities/monster/EntityEldritchGuardianStaticGuardTest.java`
  - `src/test/java/thaumcraft/common/entities/monster/boss/EntityEldritchWardenStaticGuardTest.java`
  - `src/test/java/thaumcraft/common/entities/monster/boss/EntityEldritchGolemBeamChargeStaticGuardTest.java`
  - `src/test/java/thaumcraft/common/entities/projectile/ProjectileImpactFxStaticGuardTest.java`
- Uncovered validation: No in-game combat, multiplayer scaling, audio, visual, serialization, long-duration persistence, or probabilistic spawn sampling was performed.

## Mapping Adjudication

The following official MCP stable_12 1.7.10 mappings control ambiguous decompiler names in this packet:

| Locator | SRG | 1.7.10 semantic name |
| --- | --- | --- |
| `methods.csv:334` | `func_142014_c` | `isOnSameTeam` |
| `methods.csv:2119` | `func_70110_aj` | `setInWeb` |
| `methods.csv:2225` | `func_70627_aG` | `getTalkInterval` |
| `methods.csv:2243` | `func_70648_aU` | `canBreatheUnderwater` |
| `methods.csv:2252` | `func_70658_aO` | `getTotalArmorValue` |
| `methods.csv:4357` | `func_82162_bC` | `enchantEquipment` |
| `methods.csv:4358` | `func_82164_bB` | `addRandomArmor` |
| `methods.csv:4795` | `func_98052_bS` | `canPickUpLoot` |
| `fields.csv:3618` | `field_76369_e` | Drown damage source |
| `fields.csv:3659` | `field_76437_t` | Weakness potion |
| `fields.csv:4572` | `field_82727_n` | Wither damage source |
| `fields.csv:4576` | `field_82731_v` | Wither potion |

## Atomic Findings

### A-017-F01 — Eldritch Orb applies Wither instead of Weakness

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR `thaumcraft/common/entities/projectile/EntityEldritchOrb.java:65-83`, especially `:71-74`; MCP `fields.csv:3659`; S-005 `src/main/java/thaumcraft/common/entities/projectile/EntityEldritchOrb.java:35-59`, especially `:51-52`.
- Observed: The port adds `new PotionEffect(MobEffects.WITHER, 160, 0)` to every affected non-undead living entity.
- Expected: TC4 adds potion `field_76437_t` for 160 ticks at amplifier 0; the authoritative 1.7.10 mapping identifies it as Weakness.
- Exact delta: eight seconds of Weakness -> eight seconds of Wither. Radius 2, indirect damage, undead exclusion, duration, and amplifier otherwise match.
- Effect/reproduction: Impact a living non-undead target and inspect its active potion and subsequent health. The port adds damaging Wither instead of the original attack-weakening effect.
- Regression hazards: Preserve the server thrower gate, expanded AABB, thrower exclusion, living/non-undead filter, `attackDamage * 0.666`, fizz sound, tick-100 termination, status byte 16, and 30-wisp client burst.
- Test gap: `ProjectileImpactFxStaticGuardTest.java:48-50` checks only the status-16 wisp path. It passes with the wrong potion.
- Candidate disposition: required.

### A-017-F02 — Guardian sonic attack applies Blindness instead of Wither

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR `EntityEldritchGuardian.java:297-326`, especially `:313-324`; MCP `fields.csv:4576`; S-005 `EntityEldritchGuardian.java:279-315`, especially `:300-313` and `:306-307`.
- Observed: The visible 10% sonic branch applies `MobEffects.BLINDNESS` for 400 ticks at amplifier 0.
- Expected: TC4 applies `Potion.field_82731_v` for the same duration/amplifier; the 1.7.10 mapping identifies it as Wither.
- Exact delta: 20 seconds of Wither -> 20 seconds of Blindness.
- Effect/reproduction: Force the sonic random branch against a visible player and inspect effect identity/health. The port removes TC4 damage over time and substitutes a vision impairment.
- Regression hazards: Preserve the `nextFloat() <= 0.1` branch probability, line-of-sight gate, `PacketFXSonic`, 32-block packet radius, permanent warp amount `1 + nextInt(3)`, screech volume/pitch, and the 90% Orb branch.
- Test gap: `EntityEldritchGuardianStaticGuardTest` guards fog and melee ignition only; no sonic-effect assertion exists.
- Candidate disposition: required.

### A-017-F03 — Guardian armor method was mistranslated as a spawn-cap override

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR `EntityEldritchGuardian.java:130-132`; MCP `methods.csv:2252`; S-005 `EntityEldritchGuardian.java:87-90`.
- Observed: The port overrides `getMaxSpawnedInChunk()` to return 4 and does not override intrinsic armor.
- Expected: TC4 `func_70658_aO()` returns 4; the 1.7.10 mapping identifies that method as `getTotalArmorValue()`.
- Exact delta: intrinsic armor 4 -> 0. The added maximum-spawned-in-chunk value 4 is likely neutral because it matches the vanilla default, but it is attached to the wrong semantic method.
- Effect/reproduction: Instantiate a Guardian and inspect `getTotalArmorValue`; incoming ordinary damage receives no TC4 armor reduction.
- Regression hazards: Do not alter natural isolation logic in `getCanSpawnHere`, the 32/16/32 search volume, or the existing default-equivalent chunk cap unless separately justified.
- Test gap: No test checks Guardian armor or distinguishes armor from spawn density.
- Candidate disposition: required.

### A-017-F04 — Guardian talk interval was mistranslated as 500 XP

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-003/S-004 constructor `EntityEldritchGuardian.java:98-112` and `func_70627_aG:191-193`; MCP `methods.csv:2225`; S-005 constructor `EntityEldritchGuardian.java:44-50` and `getExperiencePoints:165-168`.
- Observed: The constructor sets `experienceValue = 20`, but the port overrides `getExperiencePoints` to return 500. It has no 500-tick talk-interval override.
- Expected: TC4 base XP is 20 and `func_70627_aG()` returns 500 as `getTalkInterval`.
- Exact delta: kill XP 20 -> 500, a 25x increase; minimum ambient-sound interval 500 -> vanilla default of approximately 80 ticks.
- Effect/reproduction: Kill a Guardian and total spawned XP; separately inspect/call its talk interval. Progression rewards and ambient cadence both differ.
- Regression hazards: Preserve the constructor XP value 20 and ambient/death sound identities while moving the 500 constant to the correct semantic method.
- Test gap: No XP or talk-interval test exists.
- Candidate disposition: required.

### A-017-F05 — Warden rejects the wrong damage-source set

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR `EntityEldritchWarden.java:296-311`; MCP `fields.csv:3618,4572`; S-005 `EntityEldritchWarden.java:294-304`.
- Observed: Outside normal superclass invulnerability, the port rejects field-frenzy damage, `DamageSource.OUT_OF_WORLD`, and all `DamageSource.MAGIC` damage.
- Expected: TC4 rejects normal entity invulnerability plus Drown and Wither damage sources. Field frenzy is included by the TC4 invulnerability override.
- Exact delta: Drown + Wither immunity -> Out-of-World + generic Magic immunity; explicit field-frenzy immunity remains equivalent.
- Effect/reproduction: Outside spawn/frenzy, attack a Warden separately with Drown, Wither, Magic, and Out-of-World sources. The port is newly immune to all magic and void damage and loses TC4's explicit Wither immunity. Natural drowning is already prevented by unchanged boss air handling, but direct Drown-source semantics still differ.
- Regression hazards: Preserve spawn-timer/frenzy invulnerability, superclass damage cap/anger behavior, and the once-only frenzy trigger when absorption reaches zero.
- Test gap: No damage-source matrix test exists.
- Candidate disposition: required.

### A-017-F06 — Guardian movement is completely suppressed outside its home radius

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR Guardian task setup `EntityEldritchGuardian.java:101-110`, especially `:104-105`, with no `isMovementBlocked` override; S-005 `EntityEldritchGuardian.java:255-258`; Forge 1.12 `EntityLivingBase.isMovementBlocked` and living-update bytecode.
- Observed: The port returns `!isWithinHomeDistanceCurrentPosition()` from `isMovementBlocked`.
- Expected: TC4 leaves ordinary movement enabled and uses `EntityAIMoveTowardsRestriction` at priority 5 to return an out-of-range Guardian to its home.
- Exact delta: outside-home movement/AI enabled -> movement inputs and normal AI update suppressed.
- Effect/reproduction: Knock or teleport a key-room Guardian beyond its radius. It cannot execute its return-home task and remains frozen unless an external force moves it back.
- Regression hazards: Preserve home NBT, despawn-if-no-home behavior, home restriction, and `EntityAIMoveTowardsRestriction`; do not solve this by removing the home boundary itself.
- Test gap: No home-boundary displacement/recovery test exists.
- Candidate disposition: required.

### A-017-F07 — Warden client never receives spawn timer status 18

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR `EntityEldritchWarden.handleStatusUpdate:359-372`; S-005 send at `EntityEldritchWarden.java:171-175`, receive at `:320-331`, client smoke at `:186-191`, and `RenderEldritchWarden.doRender:23-31`.
- Observed: The server sends entity state 18 when the spawn timer is 150, but the port handler only processes 15, 16, and 17.
- Expected: TC4 state 18 sets the client `spawnTimer` to 150.
- Exact delta: synchronized client timer 150 -> unchanged client default 0.
- Effect/reproduction: Observe a newly spawned Warden from a client. The renderer does not sink/emerge the model and the client smoke branch sees no active timer.
- Regression hazards: Preserve arm states 15/16/17, server timer countdown/invulnerability, renderer interpolation, and server authority; add only the missing client state transition.
- Test gap: `EntityEldritchWardenStaticGuardTest.java:21-31` checks the send, smoke call, and packet declarations but not an `id == 18` receive branch.
- Candidate disposition: required.

### A-017-F08 — Shared boss aggro and multiplayer scaling semantics were rewritten

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR `EntityThaumcraftBoss.java:127-171`; S-005 `EntityThaumcraftBoss.updateAggroAndPlayerScaling:192-245`.
- Observed: The port scans every live aggro entry, removes every stale/distant entry, counts all living player entries, selects the actual highest qualifying aggro target, and applies +50 maximum health and +0.5 attack per extra player up to five.
- Expected: TC4 uses an order-dependent loop over only entries exceeding current aggro by 25 and 10%; it resolves the previous `hei` before assigning the candidate ID, assigns `ld = ei` rather than candidate aggro, and counts only traversed resolved players. It can therefore fail to retarget and commonly does not apply the nominal two-player buff.
- Exact delta: buggy/order-dependent target selection and scaling -> deterministic corrected selection and scaling. Modifier UUIDs and numeric amounts themselves match TC4.
- Effect/reproduction: Let two players establish different aggro values, wait for the 20-tick update, then inspect target, maximum health, and attack damage. Port bosses reliably retarget/scale where TC4 may not.
- Regression hazards: This may be a desirable bug fix, but it is an undocumented gameplay change under a parity objective. Do not change modifier UUIDs, +50/+0.5 amounts, five-player cap, proportional-health rescaling, 128-block stale distance, or 20-tick cadence without separate evidence.
- Test gap: No deterministic multiplayer aggro/scaling test exists.
- Candidate disposition: required under confirmed parity scope, subject to explicit orchestrator adjudication if intentional TC4 bug fixes are allowed.

### A-017-F09 — Shared bosses lost cobweb immunity

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR `EntityThaumcraftBoss.java:191-192`; MCP `methods.csv:2119`; S-005 `EntityThaumcraftBoss.java` has no `setInWeb` override.
- Observed: Port bosses inherit vanilla `setInWeb` behavior.
- Expected: TC4 overrides `func_70110_aj` with an empty body; the mapping identifies it as `setInWeb`.
- Exact delta: ignore web application -> acquire vanilla in-web slowdown.
- Effect/reproduction: Put a Warden or Golem in cobwebs or invoke `setInWeb`, then inspect movement. The port can be web-slowed; TC4 bosses cannot.
- Regression hazards: Preserve ordinary collision, push suppression during spawn, and all other boss invulnerability behavior.
- Test gap: No boss web-immunity test exists.
- Candidate disposition: required.

### A-017-F10 — Crab hard-difficulty spider potion spawn logic is omitted

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR `EntityEldritchCrab.onInitialSpawn:108-124`; S-005 `EntityEldritchCrab.java:98-106` and `TileEldritchCrabSpawner.java:107-117`.
- Observed: The port selects helmet state and then invokes the superclass; it never creates `EntitySpider.GroupData` or applies the hard-difficulty random potion.
- Expected: TC4 creates spider group data when absent and, on Hard, rolls `0.1 * localDifficulty` for a permanent random spider potion before delegating.
- Exact delta: probabilistic permanent spider potion on Hard -> no equivalent Crab spawn potion.
- Effect/reproduction: Spawn many Crabs on Hard at high local difficulty and inspect active effects. This also affects the dedicated Crab spawner because it calls initial spawn before forcing the helmet off.
- Regression hazards: Preserve Hard 100% helmet selection, non-Hard 33% selection, spawner post-initialization `setHelm(false)`, and inherited spawn data flow.
- Test gap: No deterministic local-difficulty initial-spawn test exists.
- Candidate disposition: required.

### A-017-F11 — Port actively slows helmeted Crabs unlike TC4 runtime behavior

- Type: defect
- Severity: medium
- Confidence: medium-high
- Source/oracle locator: S-003/S-004 CFR attributes `EntityEldritchCrab.java:88-93`, `setHelm:131-142`, and helmet break `:180-186`; S-005 `EntityEldritchCrab.setHelm:58-64`.
- Observed: Every port `setHelm(true)` changes movement speed to 0.275; `setHelm(false)` changes it to 0.3.
- Expected: Although the TC4 attribute initializer contains `hasHelm() ? 0.275 : 0.3`, the watcher is false during attribute initialization. TC4 `setHelm` changes only the watcher bit; speed is set to 0.3 explicitly only when the helmet breaks. Normal helmeted spawns therefore retain 0.3.
- Exact delta: helmeted runtime speed 0.3 -> 0.275, approximately 8.3% slower.
- Effect/reproduction: Initialize a helmeted Crab and inspect its movement-speed base value after spawn.
- Regression hazards: Lifecycle order is material. A fix must preserve watcher synchronization, NBT restoration, and the break transition. This may be an intentional cleanup of dormant original intent, so implementation should record the parity-versus-bug-fix decision.
- Test gap: No runtime lifecycle/attribute test exists; static source matching is insufficient.
- Candidate disposition: required under strict parity, subject to orchestrator adjudication.

### A-017-F12 — Guardian Eldritch team relationship is missing

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR `EntityEldritchGuardian.java:328-330`; MCP `methods.csv:334`; S-005 Guardian has no `isOnSameTeam` override.
- Observed: The port uses vanilla team behavior.
- Expected: TC4 reports every `IEldritchMob` as being on the Guardian's team.
- Exact delta: `IEldritchMob` ally -> not inherently allied.
- Effect/reproduction: Query team membership between a Guardian and Warden/Golem/other Eldritch mob. Friendly-target and friendly-fire consumers can behave asymmetrically.
- Regression hazards: Preserve explicit target tasks against players and cultists; team membership must not be broadened beyond the original interface.
- Test gap: No Guardian team-membership test exists.
- Candidate disposition: required.

### A-017-F13 — Crab same-species team relationship is missing

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR `EntityEldritchCrab.java:231-233`; MCP `methods.csv:334`; S-005 Crab has no `isOnSameTeam` override.
- Observed: The port uses vanilla team behavior.
- Expected: TC4 reports another `EntityEldritchCrab` as being on the same team.
- Exact delta: Crab ally relationship -> no inherent same-species ally relationship.
- Effect/reproduction: Query two Crabs' team membership or exercise a friendly-fire/team-aware targeting consumer.
- Regression hazards: Preserve player/cultist target tasks and do not generalize this relationship to every Eldritch mob.
- Test gap: No Crab team-membership test exists.
- Candidate disposition: required.

### A-017-F14 — Boss initial spawn invokes new vanilla mutations and changes home rounding

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR `EntityThaumcraftBoss.java:96-99`; S-005 `EntityThaumcraftBoss.java:108-113`; Forge 1.12 `EntityLiving.onInitialSpawn` bytecode.
- Observed: The port calls `super.onInitialSpawn`, then uses `new BlockPos(this)` for the home. Forge 1.12 applies a Gaussian 5% follow-range modifier and random left-handed state in that superclass; `BlockPos(Entity)` floors coordinates.
- Expected: TC4 casts x/y/z directly to `int`, sets home radius 24, returns the incoming data, and does not call the superclass.
- Exact delta: exact base follow range -> random Gaussian modifier; no handedness mutation -> random handedness; negative fractional coordinates truncate toward zero -> floor toward negative infinity.
- Effect/reproduction: Spawn bosses repeatedly and inspect follow range/handedness; spawn at a negative fractional coordinate and inspect home position.
- Regression hazards: Preserve radius 24, subclass initialization ordering, and explicit Warden home replacement by `TileEldritchLock`. Avoid suppressing unrelated subclass initialization.
- Test gap: No boss initial-spawn attribute or coordinate-rounding test exists.
- Candidate disposition: required.

### A-017-F15 — Warden follow range is 48 instead of TC4's inherited 40

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR Warden attributes `EntityEldritchWarden.java:129-135` and boss attributes `EntityThaumcraftBoss.java:76-80`; S-005 Warden `EntityEldritchWarden.java:71-78` and boss `EntityThaumcraftBoss.java:91-96`.
- Observed: The port explicitly sets Warden follow range to 48, then can add the superclass random spawn modifier from A-017-F14.
- Expected: TC4 Warden sets only maximum health 200, movement speed 0.33, and attack damage 10, inheriting exact boss follow range 40.
- Exact delta: 40 -> 48 before the additional random spawn modifier, a 20% base increase.
- Effect/reproduction: Spawn a Warden and inspect follow range/detection distance.
- Regression hazards: Preserve maximum health 200, speed 0.33, attack 10, AI attack range 24, and boss baseline follow range for other subclasses.
- Test gap: No Warden attribute matrix test exists.
- Candidate disposition: required.

### A-017-F16 — Warden ward cap uses base rather than effective maximum health

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR `EntityEldritchWarden.java:160-169,315-319`; S-005 `EntityEldritchWarden.java:81-85,119-128`.
- Observed: Initial absorption and regeneration ceiling use `MAX_HEALTH.getBaseValue() * 0.66`, remaining 132 even after health modifiers.
- Expected: TC4 uses the effective maximum-health attribute value for both initial absorption and the regenerated ceiling.
- Exact delta: ward ceiling scales with modified maximum health -> ward ceiling fixed from base health.
- Effect/reproduction: Apply one +50 multiplayer health modifier, remove absorption, and allow 25-tick regeneration. TC4's ceiling rises with effective health; the port remains at 132.
- Regression hazards: Preserve the 66% ratio, +1 per 25 ticks, hurt-resistance gate, initial additive ward, and health-scaling modifier identities.
- Test gap: No modified-health ward test exists.
- Candidate disposition: required.

### A-017-F17 — Warden 500-tick talk interval is missing

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR `EntityEldritchWarden.java:395-397`; MCP `methods.csv:2225`; S-005 Warden has no `getTalkInterval` override.
- Observed: The port inherits the vanilla interval.
- Expected: TC4 returns 500 ticks.
- Exact delta: minimum ambient cadence 500 -> vanilla default of approximately 80 ticks.
- Effect/reproduction: Inspect `getTalkInterval` or sample ambient calls over time.
- Regression hazards: Preserve the Warden ambient/death sounds and avoid changing Guardian's separate A-017-F04 correction.
- Test gap: No Warden sound-interval test exists.
- Candidate disposition: required.

### A-017-F18 — Warden and Golem champion names are never generated

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR Warden `generateName:113-119` and Golem `generateName:96-102`; S-005 `EntityThaumcraftBoss.generateName:298` and `EntityUtils.makeChampion:321-336`.
- Observed: `EntityUtils.makeChampion` invokes `generateName` for a boss, but both port subclasses inherit an empty base implementation.
- Expected: TC4 Warden formats its localized name with its title and champion modifier; TC4 Golem formats its localized name with the champion modifier.
- Exact delta: generated boss-specific champion custom name -> no generated custom name.
- Effect/reproduction: Apply a champion modifier to a Warden or Golem and inspect the custom display name.
- Regression hazards: Preserve title index/list/NBT, champion modifier selection, non-boss naming behavior, and localized formatting rather than hard-coded English.
- Test gap: No champion-boss naming test exists.
- Candidate disposition: required.

### A-017-F19 — Long-range AI refuses to pursue targets beyond firing range

- Type: defect
- Severity: low-medium
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR `AILongRangeAttack.java:28-44`; S-005 `AILongRangeAttack.java:25-45`, especially `:40-42`.
- Observed: The port rejects task execution when squared distance exceeds `maxRange * maxRange` (24 blocks for audited users).
- Expected: TC4 adds only the minimum-distance rejection and otherwise lets vanilla ranged AI approach until the target enters firing range.
- Exact delta: ranged task pursues beyond 24 blocks -> ranged task declines and lower-priority melee pursuit can run. Warden/Golem melee task speed is 1.1 versus ranged speed 1.0.
- Effect/reproduction: Place a target more than 24 blocks away and inspect active task/path speed until it enters range.
- Regression hazards: Preserve the minimum distances (Guardian 8, Warden/Golem 3), max firing distance 24, attack cooldown bounds, dead-target clearing, and task priorities.
- Test gap: No AI task-selection/range-transition test exists.
- Candidate disposition: required.

### A-017-F20 — Warden teleport search order and portal trail differ

- Type: defect
- Severity: low-medium
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR `EntityEldritchWarden.teleportHome:241-294`; S-005 `EntityEldritchWarden.java:250-292`.
- Observed: The port always tries randomized x/z offsets around home first, falls back to exact home, and emits only teleport sounds.
- Expected: TC4 tests the exact home column first, randomizes x/z only while that landing is invalid, and emits 128 portal particles along the successful path before sounds.
- Exact delta: exact-home-first -> random-offset-first; 128 portal particles -> none.
- Effect/reproduction: Trigger field frenzy with a valid exact home landing. Port placement can differ and the teleport trail is absent.
- Regression hazards: Preserve `EnderTeleportEvent`, cancellation, collision checks, 20-attempt bound, old-position rollback, and both teleport sounds.
- Test gap: No deterministic landing-order test or manual visual validation exists.
- Candidate disposition: required.

### A-017-F21 — Normal Warden field blocks receive frenzy-style scheduled cleanup

- Type: defect
- Severity: low-medium
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR normal field placement `EntityEldritchWarden.java:197-206` and frenzy placement `:215-239`; S-005 `fillEldritchField:204-215` and `performFieldFrenzy:217-248`.
- Observed: The port schedules every normal footprint airy-meta-11 block for removal after `250 + nextInt(150)` ticks, as well as scheduling frenzy-ring blocks.
- Expected: TC4 normal footprint placement does not explicitly schedule cleanup; only frenzy-ring placements use the 250-399 tick schedule.
- Exact delta: normal field relies on original block ticking -> deterministic 250-399 tick scheduled cleanup.
- Effect/reproduction: Keep a Warden stationary and inspect the lifetime of normal footprint field blocks independently of frenzy rings.
- Regression hazards: Preserve airy metadata/type 11, four footprint samples, ring scheduling, server-only mutation, and ring geometry/FX.
- Test gap: No field-lifetime test exists.
- Candidate disposition: required.

### A-017-F22 — Golem Orb impact omits the TC4 burst effect

- Type: defect
- Severity: low-medium
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR `EntityGolemOrb.java:76-83`; S-005 `EntityGolemOrb.java:53-66`.
- Observed: Port impact performs damage, shock sound, and entity death without a burst call.
- Expected: TC4 additionally invokes `Thaumcraft.proxy.burst(world, x, y, z, 1.0f)`.
- Exact delta: one scale-1.0 impact burst -> no burst.
- Effect/reproduction: Observe a Golem Orb entity/block impact on a client. Damage and sound occur, but the burst is absent.
- Regression hazards: Preserve red/non-red damage multipliers 1.0/0.6, server entity-hit damage gate, shock pitch, and immediate death.
- Test gap: Existing projectile/renderer guards do not assert this burst.
- Candidate disposition: required.

### A-017-F23 — Crab uses Guardian hurt audio instead of generic hostile hurt

- Type: defect
- Severity: low
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR `EntityEldritchCrab.java:207-213`; S-005 `EntityEldritchCrab.java:133-135`.
- Observed: The port returns `SoundEvents.ENTITY_GUARDIAN_HURT`.
- Expected: TC4 returns `game.hostile.hurt`.
- Exact delta: generic hostile hurt sound -> vanilla Guardian hurt sound.
- Effect/reproduction: Damage a Crab and compare audio.
- Regression hazards: Preserve Crab talk/death/claw sounds and sound category.
- Test gap: No audio identity test exists.
- Candidate disposition: required.

### A-017-F24 — Crab spider footstep sound is missing

- Type: defect
- Severity: low
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR `EntityEldritchCrab.java:219-221`; S-005 Crab has no `playStepSound` override.
- Observed: The port inherits its default step behavior.
- Expected: TC4 plays `mob.spider.step` at volume 0.15 and pitch 1.0.
- Exact delta: explicit spider step -> no matching explicit step.
- Effect/reproduction: Let a Crab walk over audible terrain and compare cadence/sound identity.
- Regression hazards: Preserve movement and claw attack sounds; use the registered 1.12 equivalent rather than a raw legacy string.
- Test gap: No step-audio test exists.
- Candidate disposition: required.

### A-017-F25 — Coordinate-only FollowingItem spawn serialization is intentionally more complete

- Type: benign_delta
- Severity: low
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR `EntityFollowingItem.java:120-143`; S-005 `EntityFollowingItem.java:109-126` and production caller `BlockUtils.java:154-173`.
- Observed: The port always serializes target ID (`-1` when absent), target coordinates, type, and reads the complete payload without a catch.
- Expected/original: TC4 writes the payload only when an entity target exists; coordinate-only instances send no bytes, and the reader catches the resulting exception.
- Exact delta: broken/empty coordinate-target spawn payload -> complete coordinate-target payload. The current production `BlockUtils` caller always supplies a player entity target, so that path remains equivalent.
- Effect/reproduction: Spawn through the coordinate-only constructor and synchronize to a client. Port clients receive intended coordinates/type instead of relying on failed reads.
- Regression hazards: Preserve field order and widths, entity-target behavior, type byte, no-clip targeting, and addon constructor compatibility. Do not regress this improvement solely for byte-for-byte parity unless network compatibility requires it.
- Test gap: No spawn-data round-trip test covers entity-target and coordinate-target constructors.
- Candidate disposition: preserve as benign platform/runtime repair.

### A-017-F26 — PermanentItem is practically persistent but theoretically finite

- Type: benign_delta
- Severity: low
- Confidence: high
- Source/oracle locator: S-003/S-004 CFR `EntityPermanentItem.java:32-38`; S-005 `EntityPermanentItem.java:13-34`; Forge 1.12 `EntityItem.onUpdate`, `writeEntityToNBT`, and `readEntityFromNBT` bytecode.
- Observed: Constructors and NBT reload set Forge lifespan to `Integer.MAX_VALUE`; Forge persists that value and expires when age reaches lifespan.
- Expected/original: TC4 resets item age to 0 whenever `age + 5 >= lifespan`, making expiration unreachable indefinitely.
- Exact delta: impossible expiration -> expiration after approximately 2.147 billion ticks, roughly 3.4 years of continuously loaded 20-TPS ticking.
- Effect/reproduction: Only an accelerated boundary test is practical. Ordinary chunk unload/restart persistence is robust because lifespan is restored after NBT read.
- Regression hazards: Preserve explosion immunity, pickup behavior, item NBT, and restart persistence. Avoid per-tick churn unless strict infinite lifetime is required.
- Test gap: No age/lifespan boundary or NBT-reload test exists.
- Candidate disposition: preserve unless the orchestrator requires literal infinite lifetime.

## Positive Parity

### A-017-PC01 — Special item and special-drop helper

- `EntitySpecialItem.java:8-45` preserves size 0.25 x 0.25, random yaw, initial x/z motion in approximately `[-0.1, 0.1)`, initial y motion 0.2, upward damping by 0.9, +0.04 compensation before vanilla item gravity, and explosion immunity.
- Omitting TC4's mutable `yOffset = height / 2` assignment is a valid 1.12 entity-positioning adaptation.
- `EntityUtils.entityDropSpecialItem:99-113` preserves nonempty-stack rejection, `EntitySpecialItem`, source position plus offset, pickup delay 10, motion `(0, 0.1, 0)`, `captureDrops`, and normal world spawning.
- `OuterProgressionEntityParityTest.java:43-58` directly covers helper entity class, motion, spawning, and explosion immunity.
- Preserve while fixing boss and Guardian drops.

### A-017-PC02 — FollowingItem normal homing behavior

- Entity-target construction preserves target x/z and bounding-box center y, no-clip travel, slowdown 20 decreasing to 1, normalized velocity while distance exceeds 0.5, 0.1 motion damping and target reset near destination, and no-clip clearing.
- Falling behavior preserves gravity compensation, client sparkle for ordinary types, crucible bubble for type 10, and NBT key `type`.
- Current production replacement in `BlockUtils.java:154-173` passes a live player target, preserving TC4's working serialization path. A-017-F25 applies only to the coordinate-only constructor/addon surface.

### A-017-PC03 — PermanentItem practical persistence

- It inherits special-item explosion immunity and ordinary pickup behavior.
- Forge lifespan is set to `Integer.MAX_VALUE` in all constructors and restored after NBT read, so server restarts do not reset it to a normal item lifespan.
- Practical gameplay persistence matches; only the multi-year theoretical boundary in A-017-F26 differs.

### A-017-PC04 — Eldritch Orb mechanics other than potion identity

- Gravity 0, collision border 0.1, death after tick 100, server/thrower gate, radius-2 expanded AABB, thrower exclusion, living/non-undead filtering, indirect damage at `attackDamage * 0.666`, fizz volume 0.5 and pitch `2.6 +/- 0.8`, tick reset to 100, entity status 16, and 30 typed wisp bursts match.
- A-017-F01 is the only verified Orb gameplay delta.

### A-017-PC05 — Guardian core behavior

- Size 0.8 x 2.25; health 50; follow range 40; speed 0.28; attack 7; XP field 20; swim navigation; AI priorities/ranges; magic damage halving; home NBT; despawn only without home; undead attribute; eye height 2.1; spawn isolation volume grown 32/16/32; and unrestricted light level match.
- Fog runs server-side every 100 ticks outside the Outer dimension and outside Easy, using squared ranges 256 on Normal and 576 on Hard.
- Burning empty-hand melee ignition preserves chance `difficultyId * 0.3` and duration `2 * difficultyId`.
- Outer spawn absorption 25 and regeneration +1 every 25 ticks while not hurt match.
- Ranged behavior preserves 90% Orb versus 10% visible sonic, alternating arm status 15/16, dual-arm status 17, projectile offsets/velocity lead, sounds, packet radius, and sonic warp `1..3`.
- Exceptions are A-017-F02, F03, F04, F06, and F12.

### A-017-PC06 — Guardian loot and NBT

- Two independent 50% Wisp Essence drops preserve exact aspect NBT: `UNDEAD` 2 and `ELDRITCH` 2.
- Rare Eldritch Object meta 0 preserves player-hit gating and exact formula `rand.nextInt(200) - looting < 5`.
- Inlining the rare drop in `dropFewItems` is a required 1.12 adaptation because the legacy rare-drop hook is no longer invoked equivalently.

### A-017-PC07 — Crab core combat, phase, and loot

- Size 0.8 x 0.6, XP 6, health 20, attack 4, helmet armor 5, swim/door navigation, AI priorities, Hard 100% helmet and other difficulties 33%, `Flags` NBT, helmet break at <=50% health, cultist chest break visual, unhelmeted airborne attachment above a target within squared distance 4, ride attacks every 10-19 ticks, 20% dismount, claw sound, exact spider-eye chance, arthropod attribute, poison immunity, and talk interval 160 match.
- Exceptions are A-017-F10, F11, F13, F23, and F24.

### A-017-PC08 — Shared boss base

- XP 50, knockback resistance 0.95, follow range baseline 40, home NBT, radius 24, spawn timer AI suppression, spawn-time push/damage suppression, no despawn, unchanged air supply, heal 1 every 30 ticks, 35-damage cap, 200-tick anger buffs/message, and `IEldritchMob` team relationship match.
- `BossInfoServer` is the correct 1.12 replacement for `IBossDisplayData`.
- Drops preserve one special Eldritch Object meta 3 at half height and one loot bag meta 2 at height 1.5.
- Exceptions are A-017-F08, F09, F14, and F18.

### A-017-PC09 — Warden core combat and frenzy

- Size 1.5 x 3.5, maximum health 200, attack 10, speed 0.33, armor +4, eye height 3.1, title list/index/NBT, server spawn timer 150, initial 66% ward, swim/task priorities, and inherited boss drops match except listed deltas.
- Ranged selection preserves 80% Orb and 20% visible sonic. Sonic knockback, Wither 400, Weakness 400, warp `3..5`, sound, and FX match.
- Field frenzy preserves one-time activation after ward loss, 150-tick counter, invulnerability, home teleport trigger, rings every 10 ticks while below 121, radius/divisor geometry, 30% arc versus 70% sparkle, and decrement timing.
- Exceptions are A-017-F05, F07, and F15-F21.

### A-017-PC10 — Golem core combat and headless phase

- Size 1.75 x 3.5, fire immunity, maximum health 250, attack 10, speed 0.3, armor +6, eye heights 3.0/3.33, spawn timer 100, heal 2 during spawn, and inherited boss drops match.
- Lethal damage greater than current health while headed preserves headless transition, explosion strength 2, prevented lethal hit, ranged task priority 2 with 5/5 cooldown, and NBT/task restoration guard.
- Melee preserves 10-tick cooldown, 0.75 attack multiplier, +0.2 y motion, and headless knockback.
- Beam behavior preserves charge 1..150, status 19, shot drain 15-19, speed 0.66, inaccuracy 5, target lead, client sparks/vent/arcs, and terrain interactions with `BlockLoot` and hardness <=0.15.
- Iron Golem hurt/death/step sounds match. Exceptions are champion naming and A-017-F22's projectile burst.

### A-017-PC11 — Golem Orb mechanics other than impact burst

- Gravity 0, border 0.1, target/red spawn data, lifetime 160/240, homing acceleration 0.2 divided by squared distance, component clamp +/-0.25, red/non-red damage 1.0/0.6 of attack, shock/death, reflected look-vector motion scaled 0.9, and zap sound match.
- A-017-F22 is the only verified direct Golem Orb delta.

### A-017-PC12 — Melee AI helper

- `AIAttackOnCollide` preserves target liveness/class checks, mutex 3, path setup and penalties, home restriction, visibility/long-memory behavior, distance thresholds 256/1024, 10-tick attack cooldown, held-item swing, and final mob attack.
- A-017-F19 concerns only `AILongRangeAttack`'s added maximum-range execution gate.

## Intentional Adaptations To Preserve

- `EntityDataManager` replaces fixed numeric DataWatcher slots while retaining value semantics and synchronization.
- `BossInfoServer` replaces `IBossDisplayData`; player tracking and health percentage updates remain server-authoritative.
- Registered `SoundEvent`s and 1.12 status/network APIs replace raw legacy sound strings and FML APIs.
- Removal of legacy mutable `yOffset` assignments is valid for 1.12 entity positioning.
- Guardian rare-drop logic remains inline because the 1.12 death-loot path no longer invokes TC4's rare-drop hook equivalently.
- Forge `captureDrops` handling in `EntityUtils.entityDropSpecialItem` is preserved.
- PermanentItem lifespan restoration after NBT load is a practical persistence adaptation; A-017-F26 records only its theoretical finite boundary.
- A-017-F25's complete coordinate-target spawn payload is a benign correction and should not be regressed without a concrete compatibility requirement.
- Do not change entity registrations, world generation, research declarations, IDs, registry names, metadata identities, NBT keys, packet IDs, or dimension behavior while implementing this packet.

## Unknowns and Conflicts

- A-017-F08 and A-017-F11 appear to correct or implement apparent original intent, but they still change observable TC4 behavior. The audit has no authority to silently prefer those bug fixes over parity; the orchestrator must record an explicit adjudication if they are retained.
- A-017-F14's negative-coordinate home delta is real but rarely visible because Outer boss rooms use positive/integer-aligned placement in the audited path. Random follow-range mutation remains generally observable.
- A-017-F21 may interact with the port's translated `BlockAiry` random-tick behavior. The direct entity-level scheduling delta is proven; implementation should recheck the block's effective cleanup contract before removing only the explicit schedule.
- A-017-F26 is not a practical progression blocker. Literal infinity versus approximately 3.4 continuously loaded years should be adjudicated against code simplicity and persisted-data behavior.
- No other source conflict remains. Mapping-backed potion, SRG method, and damage-source identities are unambiguous.

## Test Debt

- `OuterProgressionEntityParityTest.java:43-58,61-71` covers only special-drop helper motion/explosion immunity and initial Guardian ward.
- `OuterProgressionEntityStaticGuardTest.java:15-56` covers selected Guardian rare drop/ward/spawn fragments, Crab attachment/plate break/spider-eye fragments, boss helper use, and Warden home initialization ordering. It does not execute those behaviors.
- `EntityEldritchGuardianStaticGuardTest.java:14-26` guards fog and melee fire only.
- `EntityEldritchWardenStaticGuardTest.java:15-31` guards status-18 sending, smoke source text, sonic packet, and frenzy packet declarations, but misses status-18 receiving, effect identity, immunity, attributes, ward scaling, teleport, and field lifetime.
- `EntityEldritchGolemBeamChargeStaticGuardTest.java:14-43` guards beam-charge FX declarations only; it does not execute charge, phase, attack, or impact behavior.
- `ProjectileImpactFxStaticGuardTest.java:48-50` guards only the Eldritch Orb wisp burst.
- Missing focused evidence by finding:
  - A-017-F01/F02: deterministic potion identity, duration, amplifier, and damage-over-time tests.
  - A-017-F03/F04/F17: armor, XP, and talk-interval assertions.
  - A-017-F05: Warden Drown/Wither/Magic/Out-of-World matrix.
  - A-017-F06: displaced home-bound Guardian recovery.
  - A-017-F07: client status-18 timer transition and render/smoke behavior.
  - A-017-F08/F16: deterministic multiplayer aggro, health/damage scaling, target switching, and effective-health ward ceiling.
  - A-017-F09: boss web immunity.
  - A-017-F10/F11/F14/F15: deterministic initial-spawn/local-difficulty and attribute lifecycle tests.
  - A-017-F12/F13: exact team-membership matrices.
  - A-017-F18: champion Warden/Golem localized name output.
  - A-017-F19: task selection above/below minimum and maximum ranges.
  - A-017-F20/F21: deterministic teleport landing and field-lifetime tests plus manual visual validation.
  - A-017-F22-F24: impact burst and sound identity checks.
  - A-017-F25: spawn-data round trips for both constructors.
  - A-017-F26: accelerated age/lifespan and NBT reload boundary test.

## Commands And Results

All commands were run from the repository root unless an absolute path is shown. Temporary decompilation/mapping artifacts were kept outside product paths under OpenCode tool output.

```text
git status --short
cfr Thaumcraft-1.7.10-4.2.3.5.jar --outputdir /home/stfu/.local/share/opencode/tool-output/tc4-a17-cfr --silent true
curl -fsSLo /home/stfu/.local/share/opencode/tool-output/mcp_stable-12-1.7.10.zip https://maven.minecraftforge.net/de/oceanlabs/mcp/mcp_stable/12-1.7.10/mcp_stable-12-1.7.10.zip
unzip -oq /home/stfu/.local/share/opencode/tool-output/mcp_stable-12-1.7.10.zip -d /home/stfu/.local/share/opencode/tool-output/mcp-stable-12-1.7.10
grep -E 'func_142014_c|func_70110_aj|func_70627_aG|func_70648_aU|func_70658_aO|func_82162_bC|func_82164_bB|func_98052_bS' /home/stfu/.local/share/opencode/tool-output/mcp-stable-12-1.7.10/methods.csv
grep -E 'field_76369_e|field_76437_t|field_82727_n|field_82731_v' /home/stfu/.local/share/opencode/tool-output/mcp-stable-12-1.7.10/fields.csv
javap -classpath /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar -p -c net.minecraft.entity.EntityLiving
javap -classpath /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar -p -c net.minecraft.entity.EntityLivingBase
javap -classpath /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar -p -c net.minecraft.entity.item.EntityItem
git status --short
```

- Mapping result: All ambiguous potion, damage-source, team, web, armor, and talk-interval names used by findings were resolved from official 1.7.10 mappings.
- Bytecode result: 1.12 inherited behavior confirmed superclass random follow-range/handedness mutation, `isMovementBlocked` AI suppression, and persisted integer item lifespan/age expiration.
- Audit result: 24 defects, two benign deltas, and 12 positive parity controls recorded.
- Tests/build: Not run. This was a read-only audit and report materialization; existing tests were inspected only for coverage.
- Runtime smoke: Not required and not run because no product/runtime path changed.
- In-game reproduction/manual visual validation: Not run.
- Product diff: none.

## Handoff

- Terminal status: complete.
- Material finding index: A-017-F01 Orb potion; F02 Guardian sonic potion; F03 Guardian armor method; F04 Guardian XP/talk method; F05 Warden immunity matrix; F06 Guardian home freeze; F07 Warden status 18; F08 boss aggro/scaling; F09 boss web immunity; F10 Crab spawn potion; F11 Crab helmet speed; F12 Guardian team; F13 Crab team; F14 boss initial spawn; F15 Warden follow range; F16 Warden ward scaling; F17 Warden talk interval; F18 champion boss names; F19 ranged pursuit; F20 Warden teleport; F21 Warden field cleanup; F22 Golem Orb burst; F23 Crab hurt sound; F24 Crab step sound; F25 FollowingItem serialization; F26 PermanentItem lifespan.
- Positive parity index: A-017-PC01 special item/helper; PC02 FollowingItem; PC03 PermanentItem; PC04 Eldritch Orb; PC05 Guardian core; PC06 Guardian drops; PC07 Crab core; PC08 boss base; PC09 Warden; PC10 Golem; PC11 Golem Orb; PC12 melee AI.
- Exact continuation point: none; packet is ready for orchestrator normalization and adjudication of A-017-F08, F11, F25, and F26.
- Smallest next action if continued: Normalize every finding, preserve control, adaptation, and test gap into central RECON without product edits until the contract is frozen.
