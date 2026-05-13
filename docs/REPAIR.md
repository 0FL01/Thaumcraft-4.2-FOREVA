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
| Container hard-locks | `CommonProxy` server IDs bind player/tile/entity context; container `canInteractWith` checks no longer return unconditional `false`. |
| Phase 3 core baseline | Aura nodes persist base aspects and regenerate missing vis; wand centi-vis units, discounts, and no-passive-recharge behavior are restored. |
| Research/potions partial | Online username research/aspect lookup uses capabilities/cache; Infectious Vis Exhaust and Thaumarhia server effects are restored. |

## P0 -- Must Fix Before Phase 8

### P0.1 -- Crucible and core TE server interactions

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

### P0.2 -- Remaining focus server actions

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

### P1.1 -- Baubles, relics, and remaining wand integration

| Finding | Evidence |
|---------|----------|
| Runic ring tick is TBD | `src/main/java/thaumcraft/common/items/baubles/ItemRingRunic.java:48` |
| Vis amulet storage is TBD | `src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java:22` |
| Thaumometer scan action is placeholder | `src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java:29` |
| Inventory vis consumption ignores bauble storage | `src/main/java/thaumcraft/common/items/wands/WandManager.java:62` |

### P1.2 -- Research compatibility and enchantments

| Finding | Evidence |
|---------|----------|
| Offline research/aspect loading is cache-only, not original `.thaum`/`.thaumbak` compatible | `src/main/java/thaumcraft/common/lib/research/ResearchManager.java:95`, `src/main/java/thaumcraft/common/lib/research/PlayerKnowledge.java:14` |
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

1. Fix remaining six focus server actions.
2. Fix crucible ingestion/aspect container and classify major empty TEs.
3. Fix bauble vis storage/consumption, relic actions, and research offline compatibility.
4. Fix enchantment applicability and high-impact bauble actions.
5. Fix boss/special mob TODOs that are server-visible.
6. Run `compileJava`, then update `AGENTS.md` and `docs/PRD.md` statuses before Phase 8.
