# Goal: TC4 mob drop parity restoration

Status: active
Source: User-approved mob drop parity audit and implementation instruction dated 2026-08-04
Last updated: 2026-08-04

## Objective

Restore the confirmed mob drop, XP, global death reward, and inventory-spill regressions from the completed TC4 4.2.3.5 audit in small tested commits, without changing already matching behavior or valid Forge 1.12.2 adaptations.

## Execution Directive

Complete the frozen Required Outcomes using the listed Change Envelope and Primary Evidence. Work on the smallest unresolved outcome. Do not add requirements from reviews, tests, tools, speculative risks, or optional source text. Finish when every required outcome is resolved and affected constraints remain satisfied.

## Frozen Contract

### Required Outcomes

- R1: Restore audited class-local mob drops.
  - Source: Approved RECON findings for Brainy/Giant Brainy Zombies, Taint Spider, Fire Bat, and Taint Pig.
  - Acceptance: Brainy zombie custom drops and original rare zombie rewards execute instead of the inherited zombie loot table; Giant Brainy additionally restores its four-way rare reward and 2.0F brain offset; Taint Spider reaches its 1/6 taint-resource path; non-summoned Fire Bats use inherited Gunpowder count/Looting behavior while summoned bats drop none; Taint Pig has the original outer 1/3 gate and 50/50 resource selection.
  - Primary evidence: Focused runtime tests invoke the effective `dropLoot` route and verify item/count/metadata, recent-hit/child/summoned gates, and the loot-table bypass repair.
  - Status: verified
  - Evidence: Brainy/Giant now bypass inherited zombie loot tables through the Forge fallback, retain equipment ordering, restore child suppression and original rare selections, and use the Giant's 2.0F brain offset. Taint Spider uses its custom fallback, Fire Bat uses inherited Gunpowder count/Looting with summoned suppression, and Taint Pig restores the nested gate. Focused effective-`dropLoot` runtime tests and `./scripts/dev.sh validate --smoke` passed on 2026-08-04.

- R2: Restore audited XP and talk intervals.
  - Source: Approved mapping audit for Cultist Portal, Taint Spore, and Taint Spore Swarmer.
  - Acceptance: Portal XP is 30 and talk interval 540; Spore and Swarmer XP remains size-based and their talk interval is 200, including construction and NBT size restoration.
  - Primary evidence: Focused runtime tests read effective XP and talk intervals for default, resized, and NBT-loaded entities.
  - Status: verified
  - Evidence: Cultist Portal now sets XP 30 and exposes talk interval 540. Taint Spore and Swarmer retain size-driven XP through construction, resizing, and NBT load while exposing talk interval 200. Focused runtime/static tests and `./scripts/dev.sh validate --smoke` passed on 2026-08-04.

- R3: Restore audited global death rewards.
  - Source: Approved RECON findings for aspect-orb eligibility and champion whitelist inheritance.
  - Acceptance: Aspect orbs use the original post-conversion `recentlyHit > 0` eligibility, including tainted mobs, FakePlayers, tame-owned hits, and delayed deaths; champion whitelist entries retain existing string/IMC formats and maximum tier while matching eligible subclasses with the original assignability direction.
  - Primary evidence: Focused helper/runtime tests verify conversion precedence, recently-hit positive/zero cases, inherited and non-inherited whitelist matches, token formats, and maximum tier selection.
  - Status: verified
  - Evidence: Aspect-orb eligibility now follows reflected `recentlyHit > 0` after conversion precedence, without tainted-mob, final-source, or FakePlayer exclusions. Champion matching preserves the string map and exact forms while walking class ancestry and resolving registry/bare vanilla entries with correct assignability and maximum tier. Focused helper/runtime/static tests and `./scripts/dev.sh validate --smoke` passed on 2026-08-04.

- R4: Restore audited low-risk drop edge cases.
  - Source: Approved RECON findings for Pech equipment chance, Giant Taintacle duplicate suppression, and Traveling Trunk backing slots.
  - Acceptance: Pech main-hand baseline drop chance is 0.2F with the wand override still 0.1F; Giant Taintacle checks 48 blocks on all axes before its unique drop; trunk spilling processes all 36 backing slots, including hidden persisted slots.
  - Primary evidence: Focused literal/runtime tests verify both Pech chances, vertical suppression at 32 blocks, and visible/hidden trunk slots being emitted and cleared.
  - Status: pending
  - Evidence:

- R5: Close the audited objective with current repository gates and scoped commits.
  - Source: User instruction to implement iteratively with commits and `AGENTS.md` common/server workflow.
  - Acceptance: R1-R4 are committed independently; focused suites, `./scripts/dev.sh validate --smoke`, `git diff --check`, and final `./scripts/dev.sh build` pass; the distributable jar is current.
  - Primary evidence: Commit history, successful commands, and `build/libs/Thaumcraft-1.0.0-universal.jar`.
  - Status: pending
  - Evidence:

### Constraints

- C1: Keep `thaumcraft_src/**`, `Thaumcraft-1.7.10-4.2.3.5.jar`, and `Thaumcraft-1.12.2-6.1.BETA26.jar` read-only.
- C2: Preserve Java 8, Forge 1.12.2, MCP `stable_39`, public `thaumcraft.api.*` signatures, registry/config/NBT identifiers, and existing dependency versions.
- C3: Use TC4 4.2.3.5 as the gameplay oracle and retain valid Forge 1.12.2 lifecycle adaptations.
- C4: Common/server checkpoints require focused tests and `./scripts/dev.sh validate --smoke`; the final code change requires `./scripts/dev.sh build`.
- C5: Keep changes small, reversible, and checkpoint-scoped; do not edit unrelated files.

### Non-goals

- Seed-exact RNG ordering for Pech, Cultist, or Guardian rare drops when marginal behavior already matches.
- Broad 1.7-versus-1.12 equipment durability rewrites.
- Unrelated AI, rendering, combat, sound, taint ecology, trade, or compatibility-wrapper findings.
- Changing explicit 1.12 `DeathLootTable` NBT behavior or introducing a replacement loot framework.
- Expanding player taint-conversion behavior beyond the audited mob reward scope.

## Change Envelope

- Target: The eleven approved parity defects grouped into R1-R4.
- Expected paths, symbols, and direct consumers:
  - R1: `EntityBrainyZombie`, `EntityGiantBrainyZombie`, `EntityTaintSpider`, `EntityFireBat`, `EntityTaintPig`, and focused mob drop tests.
  - R2: `EntityCultistPortal`, `EntityTaintSpore`, `EntityTaintSporeSwarmer`, and focused XP/talk tests.
  - R3: `EntityUtils`, `EventHandlerEntity`, existing aspect-orb guards, and focused global reward tests.
  - R4: `EntityPech`, `EntityTaintacleGiant`, `InventoryTrunk`, and focused edge-case tests.
  - Goal state: this document.
- Allowed artifacts: Minimal Java source changes, focused JUnit tests, and this goal document.
- Forbidden artifacts: Reference-source/binary edits, dependency/platform upgrades, new gameplay subsystems, broad refactors/formatting, generated build/log output, or unrelated fixes.
- User or harness budget: KISS/YAGNI/Pareto; one reviewable commit per independent checkpoint, with no broad WorldServer/event-bus test framework unless a frozen outcome cannot otherwise be proven.

## Current Checkpoint

- Closes: R4.
- Smallest next action: Restore Pech baseline equipment chance, Giant Taintacle all-axis suppression range, and all-36-slot trunk spilling with focused edge coverage.
- Expected evidence: Focused Pech/giant/trunk tests and `./scripts/dev.sh validate --smoke` pass before the R4 commit.
- Stop or replan if: Hidden-slot verification requires changing visible inventory size or the trunk NBT contract.

## Current State

- Resolved: R1 class-local drops, R2 XP/talk mappings, and R3 global death rewards are verified.
- Last relevant evidence: Focused `EventHandlerDropParityRuntimeTest`, updated aspect-orb guard, champion regression suites, full validation, and dedicated-server smoke passed on 2026-08-04.
- Blocker: None.
- Next: R4 low-risk drop edge implementation.

## Material Decisions

- 2026-08-04: TC4 4.2.3.5 is the gameplay oracle; mappings distinguish talk intervals from XP fields/methods.
- 2026-08-04: Null loot-table overrides are limited to classes whose inherited vanilla loot table bypasses an existing TC4 custom drop method.
- 2026-08-04: Brainy rare rewards use one mod-local hook after `super.dropLoot`; equipment is not called manually.
- 2026-08-04: Existing string champion whitelist and IMC contracts are preserved; only matching becomes inheritance-aware.
- 2026-08-04: Marginally equivalent rare-drop RNG ordering and unrelated audit discoveries remain out of scope.

## Checkpoint History

- 2026-08-04: SEARCH PROBE, canonical mob inventory, parallel family audits, direct mapping reconciliation, and independent plan review completed without source edits. R1-R5 frozen from the user-approved minimal plan.
- 2026-08-04: R1 verified. Effective Forge drop routing and the audited Brainy/Giant, Taint Spider, Fire Bat, and Taint Pig item distributions were restored; focused tests and `./scripts/dev.sh validate --smoke` passed.
- 2026-08-04: R2 verified. Portal XP/talk mapping and Spore/Swarmer size-XP plus talk intervals were restored; focused tests and `./scripts/dev.sh validate --smoke` passed.
- 2026-08-04: R3 verified. Original recently-hit aspect-orb eligibility and inheritance-aware champion whitelist matching were restored without replacing public/config contracts; focused tests and `./scripts/dev.sh validate --smoke` passed.

## Completion

- Resolved outcomes:
- Commands and artifacts:
- Constraint and diff-scope check:
- Final status:
