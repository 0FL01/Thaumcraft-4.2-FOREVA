# Thaumcraft 4.2.3.5 -> 1.12.2 Port -- Pre-Phase8 Mine List

## Purpose

This file is now an active, concise pre-Phase8 blocker list. Historical repair
planning noise was removed after the Phase 0-7 RECON.

Phase 8 should not start until every **Pre-Phase8** item below is fixed or
explicitly accepted as a risk. Client-only rendering/GUI/FX work belongs to
Phase 8. Recipe/research-content work belongs to Phase 9.

## RECON Baseline

- `compileJava` passes.
- Phase 0 tooling is OK.
- Phase 1/2 bootstrap exists, but docs must not imply that every planned
  packaging/detail is complete.
- Phases 3-7 are not cleanly closed: server gameplay and dimension/runtime
  blockers remain.

## Already Closed -- Do Not Re-Audit As Blockers

| Area | Current baseline |
|------|------------------|
| Sound | `TCSounds` registers 66 `SoundEvent`s; entity sound pass done. |
| AI | 44 AI source files exist under `src/main/java/thaumcraft/common/entities/ai/`. |
| Projectiles | 12 projectile source files exist; main projectile behavior pass is largely done. |
| Entity GUI IDs | Golem/Pech/TravelingTrunk server IDs are entity-bound in `CommonProxy`. |
| Foci partial | `FocusFire`, `FocusFrost`, `FocusShock`, `FocusPrimal` have server actions. |
| Outer Lands runtime | Provider type, void chunk baseline, maze populate dispatch, MazeThread centering, and structure query hooks are wired. |
| Worldgen partial | Biomes, trees, village components, room-gen classes, and maze persistence exist. |

## P0 -- Must Fix Before Phase 8

### P0.1 -- Server containers and GUI binding

Many server containers are currently unusable because they return `false` from
`canInteractWith`. Several `CommonProxy` mappings also instantiate empty no-arg
containers instead of binding player/tile/entity state.

| Finding | Evidence |
|---------|----------|
| No-arg server container mappings | `src/main/java/thaumcraft/common/CommonProxy.java:46`, `src/main/java/thaumcraft/common/CommonProxy.java:60` |
| Base ghost container hard-locks interaction | `src/main/java/thaumcraft/common/container/ContainerGhostSlots.java:47` |
| Arcane workbench hard-locked | `src/main/java/thaumcraft/common/container/ContainerArcaneWorkbench.java:7` |
| Focal manipulator hard-locked | `src/main/java/thaumcraft/common/container/ContainerFocalManipulator.java:8` |
| Other hard-locked containers | Research table, alchemy furnace, deconstruction table, thaumatorium, spa, hand mirror, hover harness, focus pouch, magic box, arcane bore. |

Exit criteria:
- All pre-client server containers have valid `canInteractWith` checks.
- `CommonProxy.getServerGuiElement` binds tile/entity/player inventories where
  needed.
- Client GUI classes can remain Phase 8 work, but server containers must not be
  hard-locked.

### P0.2 -- Vis network and node recharge

The vis system is structurally present, but recharge is still effectively a
stub.

| Finding | Evidence |
|---------|----------|
| `TileNode.update()` does nothing | `src/main/java/thaumcraft/common/tiles/TileNode.java:93` |
| `VisNetHandler.onWorldTick()` delegates regen to node update that is empty | `src/main/java/thaumcraft/api/visnet/VisNetHandler.java:42` |
| Wand recharge is simplified and bypasses real visnet behavior | `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:202` |

Exit criteria:
- Source nodes regenerate/decay according to a defined server-side rule.
- Wands no longer get unconditional environmental vis unless that behavior is
  intentionally accepted as a temporary compatibility simplification.

### P0.3 -- Crucible and core TE server interactions

`TileCrucible` has substantial logic, but it is not complete enough to mark the
alchemy/server TE baseline done.

| Finding | Evidence |
|---------|----------|
| `attemptSmelt(EntityItem)` exists, but no caller was found in current source | `src/main/java/thaumcraft/common/tiles/TileCrucible.java:324` |
| Aspect container mutators/checks are stubs | `src/main/java/thaumcraft/common/tiles/TileCrucible.java:444`, `src/main/java/thaumcraft/common/tiles/TileCrucible.java:449`, `src/main/java/thaumcraft/common/tiles/TileCrucible.java:454`, `src/main/java/thaumcraft/common/tiles/TileCrucible.java:474` |
| Multiple major TE classes remain empty/no-op | `TileAlchemyFurnace`, `TileThaumatorium`, `TileInfusionMatrix`, `TileArcaneBore`, `TileBellows`, `TileCentrifuge`, `TileFocalManipulator` |

Exit criteria:
- Crucible item ingestion is triggered server-side.
- Crucible aspect container methods reflect stored aspects.
- Empty/no-op TE classes are either ported enough for server gameplay or moved
  to a clearly marked Phase 9/data-bound bucket.

### P0.4 -- Remaining focus server actions

Six focus items are still no-op server actions and were incorrectly labelled as
Phase 8 client work.

| Focus | Evidence |
|-------|----------|
| Portable Hole | `src/main/java/thaumcraft/common/items/wands/foci/FocusPortableHole.java:29` |
| Trade | `src/main/java/thaumcraft/common/items/wands/foci/FocusTrade.java:29` |
| Pech | `src/main/java/thaumcraft/common/items/wands/foci/FocusPech.java:29` |
| Hellbat | `src/main/java/thaumcraft/common/items/wands/foci/FocusHellbat.java:29` |
| Excavation | `src/main/java/thaumcraft/common/items/wands/foci/FocusExcavation.java:29` |
| Warding | `src/main/java/thaumcraft/common/items/wands/foci/FocusWarding.java:29` |

Exit criteria:
- Server actions, vis costs, and safe side checks are implemented.
- Client FX/GUI feedback can remain Phase 8.

## P1 -- Should Fix Before Phase 8 Unless Explicitly Deferred

### P1.1 -- Baubles, relics, and wand discounts

| Finding | Evidence |
|---------|----------|
| Runic ring tick is TBD | `src/main/java/thaumcraft/common/items/baubles/ItemRingRunic.java:48` |
| Vis amulet storage is TBD | `src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java:22` |
| Thaumometer scan action is placeholder | `src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java:29` |
| Wand discount/enchantment logic is simplified | `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:124` |
| Inventory vis consumption ignores bauble storage | `src/main/java/thaumcraft/common/items/wands/WandManager.java:20` |

### P1.2 -- Potions and enchantments

| Finding | Evidence |
|---------|----------|
| Infectious Vis Exhaust does nothing and is never ready | `src/main/java/thaumcraft/common/lib/potions/PotionInfectiousVisExhaust.java:29`, `src/main/java/thaumcraft/common/lib/potions/PotionInfectiousVisExhaust.java:33` |
| Thaumarhia behavior does not match intended flux-goo style effect | `src/main/java/thaumcraft/common/lib/potions/PotionThaumarhia.java:32` |
| Frugal is registered as a digger enchantment and lacks focus-specific applicability | `src/main/java/thaumcraft/common/lib/enchantment/EnchantmentFrugal.java:9` |

### P1.3 -- Boss and special mob behavior

| Finding | Evidence |
|---------|----------|
| Cultist leader equip/ranged/aura behavior is TODO | `src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistLeader.java:24`, `src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistLeader.java:36`, `src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistLeader.java:44` |
| Eldritch golem beam/headless behavior is TODO | `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchGolem.java:39`, `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchGolem.java:51` |
| Eldritch warden ranged/frenzy behavior is TODO | `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchWarden.java:34`, `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchWarden.java:46` |
| Inhabited zombie death spawn is TODO | `src/main/java/thaumcraft/common/entities/monster/EntityInhabitedZombie.java:13` |
| Pech loot remains stubbed | `src/main/java/thaumcraft/common/entities/monster/EntityPech.java:389` |

### P1.4 -- Tools, armor, and repairability

| Finding | Evidence |
|---------|----------|
| Primal Crusher still extends `ItemSword` | `src/main/java/thaumcraft/common/items/equipment/ItemPrimalCrusher.java:8` |
| Primal Crusher repair check always fails | `src/main/java/thaumcraft/common/items/equipment/ItemPrimalCrusher.java:16` |
| Multiple tools/armor classes still return `false` from repair checks | Audit target: `src/main/java/thaumcraft/common/items/equipment/`, `src/main/java/thaumcraft/common/items/armor/` |

## Deferred Buckets

| Bucket | Defer items |
|--------|-------------|
| Phase 8 client | GUI screens, renderers, TESR, entity renders, particle FX, shader effects, client-only sound/visual feedback. |
| Phase 9 data/content | Research entries, research-note hex logic, recipe registrations, infusion recipe completeness, research-table content flow. |
| Phase 10 polish | JEI, config GUI, optional compatibility, localization breadth, performance tuning after systems exist. |

## Recommended Execution Order

1. Fix server containers and `CommonProxy` bindings.
2. Fix remaining six focus server actions.
3. Fix vis node recharge and wand/bauble vis integration.
4. Fix crucible ingestion/aspect container and classify major empty TEs.
5. Fix potions/enchantments and high-impact relic/bauble actions.
6. Fix boss/special mob TODOs that are server-visible.
7. Run `compileJava`, then update `AGENTS.md` and `docs/PRD.md` statuses before Phase 8.
