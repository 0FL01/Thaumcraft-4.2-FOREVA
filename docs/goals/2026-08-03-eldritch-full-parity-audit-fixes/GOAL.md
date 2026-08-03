# Goal: Full Eldritch parity audit fixes

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Contract-Version: 2.1
Contract-Status: frozen
Source-Registry: SOURCES.md
Recon-Ledger: RECON.md
Last-Updated: 2026-08-03

## Objective

The admitted production-relevant Eldritch parity defects are corrected in bounded committed checkpoints, preserve controls remain true, and all frozen validation gates pass.

## Execution Directive

Complete only frozen admitted outcomes through their linked findings and bounded change envelope. A confirmed delta is not automatically actionable. Do not add requirements from reviews, tests, speculative risks, cleanup, hardening, or adjacent discoveries. Preserve every listed parity control. Finish when admitted findings and constraints pass current evidence, then stop.

## Scope Promotion

- Policy: production_gate
- Authority: S-001, S-002, S-008, S-009
- Production envelope: `RECON.md` Production Relevance Envelope
- Freeze decision: S-009 admits exactly F-018, F-035, F-038, F-039, F-085, F-087, F-088, and F-105. They are deterministic supported-path defects with the highest progression/security/gameplay impact that fit 8 findings and 10 of 12 checkpoints; all other candidates are deferred.

## Required Outcomes

## R-001 — Void armor repairs once per cadence

- Covers: F-018
- Acceptance: At an authoritative tick divisible by 20, a damaged equipped base Void or Void Robe stack loses exactly one damage point, while a stored damaged stack still loses exactly one.
- Primary evidence: `./scripts/dev.sh gradle test --tests thaumcraft.common.items.armor.ItemVoidArmorParityTest`
- Mandatory broader gates: `./scripts/dev.sh validate --smoke`
- Change envelope/budget: 1 fix checkpoint; only `ItemVoidArmor`, `ItemVoidRobeArmor`, and the focused existing armor test; Resource Governor applies.
- Stop/Replan if: the fix changes stored-stack repair, cadence, server authority, living-owner guard, or unrelated Forge inventory lifecycle.

## R-002 — Generated Eldritch crystals retain oriented support

- Covers: F-035
- Acceptance: Crystal metas 0..6 validate their recorded support; meta 7 accepts its exact oriented non-air Eldritch support and survives the shipped `GenCommon` placement-before-orientation lifecycle.
- Primary evidence: `./scripts/dev.sh gradle test --tests thaumcraft.common.lib.world.dim.OuterDungeonGenerationStaticGuardTest`
- Mandatory broader gates: `./scripts/dev.sh validate --smoke`
- Change envelope/budget: 1 fix checkpoint; only `BlockCrystal`, directly necessary `GenCommon` placement ordering, and focused existing Outer generation evidence; Resource Governor applies.
- Stop/Replan if: a fix requires making all Eldritch blocks side-solid or changes drops, decoration probability, rendering, NBT, or non-crystal dungeon generation.

## R-003 — Guardian armor uses the mapped TC4 method

- Covers: F-038
- Acceptance: `EntityEldritchGuardian.getTotalArmorValue()` returns 4 and no parity implementation misuses the constant as a custom spawn cap.
- Primary evidence: `./scripts/dev.sh gradle test --tests thaumcraft.common.entities.monster.EntityEldritchGuardianStaticGuardTest`
- Mandatory broader gates: `./scripts/dev.sh validate --smoke`
- Change envelope/budget: 1 fix checkpoint; only the Guardian armor/spawn-cap methods and focused existing Guardian guard; Resource Governor applies.
- Stop/Replan if: isolation volume, natural spawn logic, attributes other than armor, or unrelated entity behavior must change.

## R-004 — Guardian XP and talk interval use their mapped roles

- Covers: F-039
- Acceptance: Guardian kill XP is 20 and `getTalkInterval()` is 500 ticks; ambient/death sound identities and drops remain unchanged.
- Primary evidence: `./scripts/dev.sh gradle test --tests thaumcraft.common.entities.monster.EntityEldritchGuardianStaticGuardTest`
- Mandatory broader gates: `./scripts/dev.sh validate --smoke`
- Change envelope/budget: 1 fix checkpoint; only Guardian XP/talk methods and focused existing Guardian guard; Resource Governor applies.
- Stop/Replan if: the correction requires changing loot, sounds, AI, other attributes, or shared entity classes.

## R-005 — Dedicated clients expose synchronized research and aspects

- Covers: F-085
- Acceptance: After valid client research/aspect sync, username-based APIs and shipped browser/table/recipe callers observe the same client capability state, including `ELDRITCHMINOR`, without mutating server state.
- Primary evidence: `./scripts/dev.sh gradle test --tests thaumcraft.common.lib.research.ResearchClueAndNotesRuntimeTest --tests thaumcraft.common.lib.research.ScanProgressionRuntimeTest --tests thaumcraft.common.lib.network.playerdata.PlayerDataPacketClientBoundaryStaticGuardTest`
- Mandatory broader gates: `./scripts/dev.sh validate --smoke`; `./scripts/dev.sh build`
- Change envelope/budget: 2 fix checkpoints; `ResearchManager` cache bridge, `PacketSyncResearch`, `PacketSyncAspects`, and directly focused existing research/client-boundary tests only; Resource Governor applies.
- Stop/Replan if: server-side capability ownership, disk loading, reconnect persistence, packet direction, or unrelated browser rendering must change.

## R-006 — Research-table endpoints are server-immutable

- Covers: F-087
- Acceptance: Authenticated aspect-placement packets cannot replace type-1 fixed endpoints, cannot reduce the endpoint invariant, and cannot complete a note without a valid path; normal type-0 placement and type-2 erasure remain valid.
- Primary evidence: `./scripts/dev.sh gradle test --tests thaumcraft.common.lib.network.playerdata.ResearchTableAuthorityRuntimeTest`
- Mandatory broader gates: `./scripts/dev.sh validate --smoke`
- Change envelope/budget: 2 fix checkpoints; endpoint validation in `TileResearchTable`/packet dispatch and the focused existing authority test only; Resource Governor applies.
- Stop/Replan if: authentication, dimension/container/tile/distance checks, ordinary aspect spending/refunds, or the puzzle algorithm beyond the fixed-endpoint invariant must change.

## R-007 — Hidden and lost research requires its clue server-side

- Covers: F-088
- Acceptance: Direct and note requests for `PRIMPEARL` and `OUTERREV` without the matching `@KEY` clue are rejected before cost, note, completion, warp, callback, or sync; valid clues and ordinary research remain accepted.
- Primary evidence: `./scripts/dev.sh gradle test --tests thaumcraft.common.lib.network.playerdata.PacketPlayerCompleteRuntimeTest`
- Mandatory broader gates: `./scripts/dev.sh validate --smoke`
- Change envelope/budget: 1 fix checkpoint; `PacketPlayerCompleteToServer`, the smallest reusable clue predicate if required, and the focused existing packet test; Resource Governor applies.
- Stop/Replan if: ordinary/hidden parents, valid clue discovery, difficulty routing, aspect charging, siblings, or note ownership semantics must change.

## R-008 — Built-in scan handler preserves phenomenon dispatch

- Covers: F-105
- Acceptance: The built-in `ScanManager.scanPhenomena(ItemStack,...)` is side-effect-free and returns null so later registered phenomenon handlers receive fallback scans; the explicit End Portal bridge and normal item/entity scan APIs remain unchanged.
- Primary evidence: `./scripts/dev.sh gradle test --tests thaumcraft.common.lib.research.ScanProgressionRuntimeTest --tests thaumcraft.common.lib.research.ScanManagerThaumometerNotificationStaticGuardTest`
- Mandatory broader gates: `./scripts/dev.sh validate --smoke`
- Change envelope/budget: 1 fix checkpoint; only the `IScanEventHandler` phenomenon override and focused existing scan tests; Resource Governor applies.
- Stop/Replan if: explicit `ScanManager.scanItem/scanEntity/scanPhenomena(player,key)`, End Portal handling, scan precedence, awards, or unrelated addon policy must change.

## Preserve Controls

- F-127 — exact declaration, recipe, registry, localization, and research-art matrices.
- F-128 — runtime, Outer, entity, aspect, visual, schema, progression, and networking parity controls.
- F-129 — complete `FollowingItem` coordinate payload adaptation.
- F-130 — practical `Integer.MAX_VALUE` item-lifespan restoration.
- F-131 — server authority and approved Forge 1.12 platform/data-safety adaptations.

## Constraints

- F-133 — crafting/JEI/limited-crafting and alternate/addon scan policy remains unchanged.

## Non-goals

- Deferred RECON candidates, `.thaum`/`DIM-42` migration, unsupported/addon-only paths, generic hardening, cleanup, and adjacent bugs.

## Change Envelope

- Target behavior/artifact: the eight admitted shipped Eldritch defects only.
- Expected paths, symbols, and direct consumers: `ItemVoidArmor`, `ItemVoidRobeArmor`, `BlockCrystal`, optional direct `GenCommon` ordering, `EntityEldritchGuardian`, `ResearchManager`, `PacketSyncResearch`, `PacketSyncAspects`, `TileResearchTable`, `PacketAspectPlaceToServer`, `PacketPlayerCompleteToServer`, `ScanManager`, and focused existing tests named by R-001..R-008.
- Allowed artifacts: minimal product/test/docs changes and checkpoint commits.
- Forbidden artifacts: dependency upgrades, broad refactors, migration work, generated dumps, speculative hardening.
- API/platform/dependency boundaries: Java 8, Forge 1.12.2-14.23.5.2847, MCP stable_39, TC4 4.2.3.5 oracle.
- User or harness budget: finite Resource Governor below, authorized by S-009.

## Resource Governor

- Max Required Findings: 8
- Frozen Required Finding Count: 8
- Max Total Fix Checkpoints: 12
- Frozen Total Fix Checkpoints: 10
- Max Candidate Reproduction Attempts Per Finding: 1
- Max Material Replans Per Required Finding: 2
- Max Implementation Subagent Waves: 0
- Max Closure Review Passes: 1
- Max Scope Amendments: 0
- Adjacent Finding Auto-Promotions: 0
- Post-Closure Work Items: 0
- Budget Authority: S-009

## Validation Ladder

- Targeted evidence: the exact commands in R-001..R-008, run once after each corresponding diff.
- Affected package gate: focused Gradle package tests.
- Runtime/manual evidence: `./scripts/dev.sh validate --smoke` for each common/server checkpoint; no client-visual finding was admitted.
- Workspace/CI/build gate: `./scripts/dev.sh build` at closure.
- Final preserve-control check: one frozen closure pass.

## Commit Policy

- Required: yes, S-002
- Granularity: one bounded outcome/checkpoint per reversible commit.
- Message convention: concise Conventional Commit-style subject naming the subsystem.

## Closure Contract

- Every required F-* has a successful terminal state with evidence.
- Every R-* derives to a successful terminal state.
- Preserve controls are current after the final relevant diff.
- Constraints and change envelope remain satisfied.
- Resource counters remain within the frozen governor.
- Required validation and commits pass.
- Goal lint and hashes pass.
- Deferred candidates are not silently promoted.
- Completion is terminal.
