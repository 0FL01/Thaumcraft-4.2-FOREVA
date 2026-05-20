# RUNTIME_VALIDATION_GAPS.md

## Purpose

This register lists places where current docs or tests can create a false feeling of readiness. Each area needs concrete runtime/e2e validation before documentation can claim parity or completion.

| Area | Current misleading signal | Why it is insufficient | Concrete validation still missing |
|---|---|---|---|
| Overall progress | `validate` and `validate --smoke` passed in `GOAL_PROGRESS.md`. | Build/server load stability does not exercise gameplay behavior, malicious packets, save/load, or craftability. | Behavior-specific scenarios for progression, crafting, tiles, worldgen, and persistence. |
| Stage 9 recipe systems | Large recipe corpus and static key tests. | Corpus/key presence does not prove Forge registry availability, craftability, input consumption, or research gating. | Live registry dump, representative craft tests, duplicate id checks, fallback/display-only classification. |
| ConfigResearch corpus | Six categories and 201 research entries. | Data presence does not prove graph reachability, page rendering, recipe resolution, or gameplay unlock route. | Graph validation plus live/e2e scenarios for primary, secondary, hidden, lost, sibling, and recipe-gated research. |
| Static guard tests | Tests named `StaticGuard`, `RuntimeContract`, or source-reader tests pass. | Many read `.java` source and assert strings/patterns. They protect source shape, not runtime behavior. | Tests that instantiate systems or run controlled server/container/packet scenarios. |
| Packet count guard | `PacketHandler.REFERENCE_PACKET_COUNT = 39` and discriminator registrations match. | Packet ID count does not validate payload semantics, authorization, distance checks, or malicious input rejection. | Adversarial packet tests for scan, research completion, aspect placement, aspect combination, item/focus key packets where relevant. |
| `PacketPlayerCompleteToServer` | Static guard checks prerequisite/type/cost strings exist. | Better than before, but does not prove normal GUI route or reject all malformed/malicious payloads. | Send valid/invalid packets under controlled player knowledge/inventory states; verify note creation, secondary cost, sibling behavior. |
| Research table | `TileResearchTable` and `ContainerResearchTable` exist; static runtime-contract guard exists. | Packets can call tile logic without active container/distance/tile identity checks. q/r/aspect mutation is too client-directed. | Open-container positive test; closed/far/wrong-tile/invalid-hex/insufficient-aspect malicious packet rejection tests. |
| Research note NBT | `ResearchNoteData` round-trip tests exist. | Research table `bonusAspects` persistence is separate and may drop amounts. Note model tests do not prove table gameplay. | Bonus aspect NBT round-trip; table solve e2e; save/reload during partially solved note. |
| Scan flow | `ScanManager.completeScan(...)` validates duplicates/aspects and forwards clues. | `PacketScannedToServer` accepts client-declared item/entity/phenomena targets without full server-side physical scan authority. | Server raytrace/range/held thaumometer tests; arbitrary item/entity/phenomena payload rejection. |
| Thaumonomicon GUI route | GUI classes exist and `ItemThaumonomicon` opens a GUI. | GUI screens are mostly static/simple and server container for Thaumonomicon is null; normal research action packet route is not proven. | Primary note creation from UI, secondary purchase from UI, invalid click rejection, sync behavior. |
| Arcane crafting | Runtime integration test exists and code is mature. | Stronger than most areas, but edge cases still exist: recipe collision, wand state changes, take-result recheck. | Collision tests; vis/cost revalidation at take time; shift-click/remainder edge cases. |
| Crucible | `TileCrucible` has real smelting/aspect/fluid logic. | Runtime behavior depends on player attribution, research gates, fluid replacement, aspect overflow, item collision timing. | Valid/invalid recipe scenarios; thrown-player attribution; insufficient aspects; water drain; no-aspect item behavior; save/load. |
| Thaumatorium | `TileThaumatorium`, `TileThaumatoriumTop`, and `ContainerThaumatorium` exist. | Programming/fill/craft/output/reload behavior not proven; `completeRecipe()` clearing essentia needs reference/runtime verification. | Program recipe, fill exact/partial/extra essentia, output blocked/unblocked, top/bottom transport, save/reload. |
| Infusion | `TileInfusionMatrix` and infusion recipes exist. | Multiblock, pedestal scan, essentia drain, instability, enchant scaling, and mid-craft persistence are behavior-sensitive. | Start/craft/fail scenarios; ingredient consumption; instability event distribution; enchant cost test; save/reload mid-craft. |
| Focal manipulator / Arcane Bore | Production code and containers exist. | Presence does not prove runtime upgrade/mining behavior or save/load. | Upgrade start/finish/cost scenario; bore mining with focus/pickaxe, vis/essentia drain, inventory/full output, NBT reload. |
| Player knowledge capability | Capability tests pass. | This is a relatively strong area, but does not prove all systems update/sync it correctly. | Integration tests that scans/research/packets mutate capability and sync expected packets. |
| API boundary | Public classes/signatures exist. | Semantics can still be wrong: array key cache, complex tag merge, smelting bonus count-zero behavior. | Behavioral tests against expected reference semantics and addon-facing use cases. |
| Outer Lands | World provider, chunk provider, maze handler, ring/altar code exist. | Scaffold/hook presence does not prove ring -> maze -> portal -> dimension -> return path. `makePortal()` no-op needs validation/fix. | Fresh-world ring generation, maze persistence, portal activation, safe teleport destination, return path, async save/load race. |
| Stage 6 entities | Many entity classes and static checkpoint tests. | AI/combat/inventory/pathfinding behavior cannot be proven by class presence or source guards. | Dedicated-server scenarios for golems, trunk, pech, bosses, projectiles, cultist portal reward path. |
| Dedicated server side safety | Server smoke passed for some checkpoints. | Smoke is point-in-time and may not cover all classloading paths; common/API still needs client import discipline. | Dedicated server smoke after common/API changes; classloading/static scan for client-only imports. |
| Save/load semantics | Many tiles implement `readCustomNBT`/`writeCustomNBT`. | Method existence is not symmetry proof, and mid-operation reload is often untested. | Round-trip and in-world reload scenarios for each complex tile and player/research state. |

## Static guard tests that must not be interpreted as parity proof

These categories are useful and should remain, but documentation must label them correctly:

- `*StaticGuardTest`: source-shape or literal pattern guard.
- `ConfigRecipes*StaticGuardTest`: recipe key/shape/source guard, not craftability proof.
- `ConfigResearchStaticGraphTest`: graph/corpus guard, not progression proof.
- `Packet*StaticGuardTest`: source contains checks, not malicious payload proof.
- renderer/FX static guards: visual/client source shape, not backend readiness.
- tests using `Files.readString(...)`, source readers, regex over Java files, or `source.contains(...)`: static guarded only.

## Useful smoke signals, but insufficient alone

- `compileJava`: proves compilation.
- `test`: proves the current test suite only.
- `jar`/`check-jar`: packaging/leak checks.
- `validate --smoke`: server load stability for the tested checkpoint.
- research entry count: corpus size signal only.
- packet discriminator count: packet table shape only.
- recipe key count: corpus/registry shape only.

## Minimum runtime/e2e scenarios to add first

1. Fresh player: scan valid block/entity -> aspect/clue sync.
2. Forged scan packet: arbitrary item/entity/phenomena rejected.
3. Thaumonomicon primary research: prerequisites met -> note created; prerequisites missing -> rejected.
4. Research table: valid aspect placement through open container -> note updated.
5. Research table malicious packet: closed/far/invalid q/r -> no mutation.
6. Research note: solve note -> completed note grants research and consumes stack correctly.
7. Crucible: known recipe succeeds/fails based on research/aspects.
8. Thaumatorium: program/fill/craft/save-load.
9. Infusion: start/craft/essentia drain/ingredient consume/save-load.
10. Outer Lands: ring/maze/portal/safe destination/persistence.
