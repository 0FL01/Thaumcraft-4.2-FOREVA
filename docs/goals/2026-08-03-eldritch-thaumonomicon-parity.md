# Goal: Eldritch Thaumonomicon parity restoration

Status: active
Source: User instructions dated 2026-08-03 and the completed Eldritch RECON against Thaumcraft 4.2.3.5
Last updated: 2026-08-03

## Objective

Restore the audited Eldritch Thaumonomicon gameplay surface to the original Thaumcraft 4.2.3.5 behavior on Forge 1.12.2, close every confirmed in-scope P0/P1 regression recorded by the RECON, preserve the already exact research graph/recipes/resources, and leave the work in tested, runtime-smoked, reviewable commits.

## Execution Directive

Complete the frozen Required Outcomes using the listed Change Envelope and Primary Evidence. Work on the smallest unresolved outcome. Do not add requirements from reviews, tests, tools, speculative risks, or optional source text. Finish when every required outcome is resolved and affected constraints remain satisfied.

## Frozen Contract

### Required Outcomes

- R1: Restore Eldritch research runtime semantics.
  - Source: RECON findings for research engine/UI and the user instruction to implement the audit from beginning to end.
  - Acceptance: All supported `researchDifficulty` values choose the same purchase/note path as the TC4 GUI/server contract; clue and normal completion restore their original client feedback; matching and persisted completion membership no longer retain the audited TC4-incompatible states.
  - Primary evidence: Focused `PacketPlayerComplete*`, `ResearchManager*`, and client notification tests pass for difficulties `-1`, `0`, and positive values, followed by `./scripts/dev.sh validate --smoke` for the checkpoint.
  - Status: verified
  - Evidence: Difficulty-aware packet workflow is covered for `-1`, `0`, and positive values. Incremental packets restore clue notification/sound, non-virtual completion popup/highlights, and live browser refresh. Matching at difficulty `-1`, unknown-key queries, and TC4 save filters are covered. All focused tests and `./scripts/dev.sh validate --smoke` passed on 2026-08-03.

- R2: Restore Primal Focus and Primal Orb behavior.
  - Source: RECON findings for `FocusPrimal` and `EntityPrimalOrb`.
  - Acceptance: A cast launches the orb at the original velocity; normal and underwater impacts, special-effect probability and eligibility, node/taint placement, hand animation, focus color, and original trail/impact feedback match the TC4 behavior except for documented Forge 1.12 representation changes.
  - Primary evidence: Focused projectile/focus runtime and static parity tests pass, including launch motion, water impact, probability boundary, negative-coordinate placement, hand routing, color, and FX dispatch; checkpoint server smoke passes.
  - Status: pending
  - Evidence:

- R3: Restore Primal Crusher harvesting behavior.
  - Source: RECON findings for `ItemPrimalCrusher`.
  - Acceptance: The original effective-block matrix, 3x3 plane, player-aware secondary harvesting, Silk Touch/Fortune/XP callbacks, following drops, and durability accounting are preserved through a Forge 1.12-compatible path.
  - Primary evidence: Focused Crusher runtime tests prove effective and excluded blocks, enchanted secondary harvest, drops/XP callbacks, and wear; checkpoint server smoke passes.
  - Status: pending
  - Evidence:

- R4: Restore Outer Lands void and portal behavior.
  - Source: RECON findings for `BlockEldritchNothing` and `TeleporterThaumcraft`.
  - Acceptance: Eldritch Nothing remains a non-air, unbreakable, no-collision Outer void block with original exposure state and delayed void damage without allocating one TileEntity per generated cell; repeated portal transfers reuse the original long-lived destination cache semantics.
  - Primary evidence: Focused block/world and teleporter tests prove material/air identity, hardness/resistance/light, exposure state, collision, damage cadence, absence of mass TileEntities, and cache reuse; checkpoint server smoke passes.
  - Status: pending
  - Evidence:

- R5: Restore Outer progression entity and drop behavior.
  - Source: RECON findings for Eldritch Guardians, protected boss drops, Eldritch Crabs, altar spawning, and Warden room ownership.
  - Acceptance: Eldritch Eyes use the original rare-drop path; Primordial Pearls use explosion-immune non-despawning special drops; Guardian ward regeneration and altar spawn gates, Crab attachment/combat/drop behavior, and Warden room home match TC4.
  - Primary evidence: Focused entity runtime/static parity tests cover rare Eye drops, protected Pearl entities, ward regeneration, altar population/light checks, Crab phases and drops, and post-spawn Warden home; checkpoint server smoke passes.
  - Status: pending
  - Evidence:

- R6: Restore Void equipment behavior and balance.
  - Source: RECON findings for Void tools, Void armor, Fortress armor, Void robes, and `CAP_void` consumers.
  - Acceptance: Void tools apply Weakness with original duration, use original durability/enchantability/attack intent and repair ingredients; Void robes use the original material; armor protection is remapped correctly for 1.12 slot ordering; the audited creative/self-repair and robe NBT deltas no longer alter valid TC4 gameplay state.
  - Primary evidence: Focused item and armor tests prove potion identity, material tuples, attack modifiers, repair inputs, per-slot durability/protection, robe vis/warp behavior, and repair cadence; checkpoint server smoke passes.
  - Status: pending
  - Evidence:

- R7: Restore Eldritch aspects and player-facing localization.
  - Source: RECON findings for `ConfigAspects`, aspect display keys, advanced furnace naming, Focus Primal, and Void equipment names.
  - Acceptance: Ender Pearl, Far, Nether Star, Dragon Egg, End Portal, and End Portal Frame expose the TC4 Eldritch scan aspects; port-only direct tags are retained only where final resolved object aspects match the original recipe-derived result; missing aspect/block display keys and audited item names resolve to the canonical English values.
  - Primary evidence: Focused aspect registry tests compare final resolved tags and metadata to a checked TC4 fixture, localization tests resolve every audited key, and checkpoint server smoke passes.
  - Status: pending
  - Evidence:

- R8: Restore Essentia Reservoir and Sanity Checker parity.
  - Source: RECON findings for `TileEssentiaReservoir`, `BlockEssentiaReservoir`, wand interaction, and `ItemSanityChecker`.
  - Acceptance: Reservoir aspect assignment copies caller state, extraction contracts, destruction spill/explosion/retry behavior, and wand callback count match TC4; the Sanity Checker does not emit an immediately overwritten extra actionbar message. Existing valid 1.12 sync and finite-fluid adaptations remain intact.
  - Primary evidence: Focused reservoir runtime tests cover aliasing, extraction, spill counts/explosion, retries, and single wand callback; Sanity Checker interaction tests pass; checkpoint server smoke passes.
  - Status: pending
  - Evidence:

- R9: Preserve the already exact Thaumonomicon declarations and close the full objective.
  - Source: RECON results: 16/16 research entries, 24/24 recipe handles/definitions, 55/55 English research keys, and 9/9 referenced PNG files match the original.
  - Acceptance: Those four parity corpora remain exact after R1-R8; all focused tests, the repository validation pipeline, dedicated-server smoke, and final jar build pass; every implementation checkpoint is committed with the repository message format.
  - Primary evidence: Existing graph/recipe/resource guards, `./scripts/dev.sh validate --smoke`, and final `./scripts/dev.sh build` pass on a clean worktree except for the active goal update.
  - Status: pending
  - Evidence:

### Constraints

- C1: `thaumcraft_src/**`, `Thaumcraft-1.7.10-4.2.3.5.jar`, and `Thaumcraft-1.12.2-6.1.BETA26.jar` remain read-only.
- C2: Preserve Java 8, Forge 1.12.2, MCP `stable_39`, dependencies, mod id, registry names, packet ids, config keys, NBT keys, GUI ids, and dimension id.
- C3: Do not change public `thaumcraft.api.*` signatures when a Forge 1.12-compatible implementation change is available.
- C4: Compare gameplay semantics to TC4 4.2.3.5; use the TC6 jar only for 1.12 rendering/model conventions, never as the gameplay oracle.
- C5: Keep valid 1.12 adaptations identified by RECON, including server-authoritative mutation, synchronous maze generation, safe portal arrival, finite-fluid level translation, thread scheduling, and explicit block/tile synchronization.
- C6: Common/server runtime checkpoints require `./scripts/dev.sh validate --smoke`; compile success alone is insufficient.
- C7: Make small reversible commits using the repository commit-message format; do not combine independent research, Primal, Outer, equipment, aspect, and device changes in one commit.

### Non-goals

- Auditing or fixing Thaumonomicon categories outside Eldritch.
- Rewriting the research graph, the 24 exact recipes, or the byte-identical research PNG assets.
- Restoring unsafe TC4 implementation details such as client-side authoritative NBT mutation, background world-generation threads, forced adjacent chunk loads, or original null-pointer behavior.
- Reverting safe 1.12 combat, hand, registry, packet-wire, pathfinding, or capability adaptations unless an R1-R8 acceptance condition explicitly requires a semantic correction.
- Adding translations for the 21 original non-English locale files.
- Fixing RECON unknowns or benign deltas that were not included in R1-R8.

## Change Envelope

- Target: Confirmed Eldritch runtime, progression, equipment, aspect, localization, and device regressions from the completed RECON.
- Expected paths, symbols, and direct consumers:
  - Research: `GuiResearchBrowser`, `ResearchManager`, `PacketPlayerCompleteToServer`, `PacketResearchComplete`, player knowledge persistence, and focused tests.
  - Primal: `FocusPrimal`, `EntityPrimalOrb`, `ItemPrimalCrusher`, directly required harvest/FX helpers, and focused tests.
  - Outer: `BlockEldritchNothing`, its state/model/tile routing as needed, `TeleporterThaumcraft`, Eldritch Guardian/Crab/Warden, altar spawn logic, special-drop helpers, and focused tests.
  - Equipment: `ConfigItems`, Void tool/armor/robe classes, armor material definitions and direct consumers, and focused tests.
  - Aspects/resources: `ConfigAspects`, `en_us.lang`, final-tag fixtures/tests, and only directly affected localization consumers.
  - Devices: Essentia Reservoir block/tile/item interaction, Sanity Checker interaction, and focused tests.
  - Goal state: this document.
- Allowed artifacts: Java source, lang/blockstate/model resources when required by an accepted behavior, focused tests/fixtures, and this goal document.
- Forbidden artifacts: edits to reference binaries/source, dependency or platform upgrades, broad formatting, generated build/log output, unrelated category fixes, or a second gameplay state system.
- User or harness budget: No fixed file/time budget. Each checkpoint must close one R-item or a tightly coupled part and end in a reviewable commit after focused validation and required smoke.

## Current Checkpoint

- Closes: R2.
- Smallest next action: Add a projectile launch-motion test for the shooter constructor, then restore the original `0.5F` launch velocity through the 1.12 `shoot` API.
- Expected evidence: A focused runtime test observes non-zero forward motion with the original velocity while preserving owner and seeker spawn data.
- Stop or replan if: Forge 1.12 applies launch motion elsewhere and a second `shoot` call would duplicate velocity.

## Current State

- Resolved: R1 is verified. Research difficulty workflows, incremental feedback, matching, and persisted membership now follow TC4 semantics while retaining server authority and the proxy boundary.
- Last relevant evidence: Focused research/capability tests and `./scripts/dev.sh validate --smoke` passed on 2026-08-03.
- Blocker: None.
- Next: Commit R1 matching/persistence, then begin R2 with the Primal Orb launch regression.

## Material Decisions

- 2026-08-03: The gameplay oracle is TC4 4.2.3.5; TC6 BETA26 is not accepted as Eldritch behavior evidence.
- 2026-08-03: Work is bounded to confirmed P0/P1 findings summarized after RECON plus the directly listed low-cost parity corrections inside R1-R8; unknowns and benign adaptations cannot expand the goal.
- 2026-08-03: Existing tests that assert an audited regression, notably Wither on Void tools and static primary/secondary packet gating, must be replaced with behavioral parity evidence rather than preserved.
- 2026-08-03: Commits are required and remain checkpoint-scoped.

## Checkpoint History

- 2026-08-03: SEARCH PROBE located the Forge 1.12.2 port and TC4 4.2.3.5 binary/resource baseline. Nine atomic audits completed with no workspace edits and produced the frozen R1-R9 scope.
- 2026-08-03: R1 difficulty workflow checkpoint passed focused packet tests and `./scripts/dev.sh validate --smoke`; valid `-1`, `0`, and positive difficulty paths now follow the same effective workflow as `GuiResearchBrowser`.
- 2026-08-03: R1 feedback checkpoint restored the TC4 clue notification/sound, queued research-complete overlay, Thaumonomicon highlights, and live browser refresh. Focused guards and `./scripts/dev.sh build` passed; manual in-game overlay inspection remains part of final client limitations rather than a semantic blocker.
- 2026-08-03: R1 verified after restoring exact `findMatchingResearch` difficulty precedence, rejecting unknown plain completion keys, and filtering unknown, auto-unlock, and redundant clue keys from persisted capability state. Focused tests and `./scripts/dev.sh validate --smoke` passed.

## Completion

- Resolved outcomes:
- Commands and artifacts:
- Constraint and diff-scope check:
- Final status:
