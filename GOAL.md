# Goal: Systematic Thaumcraft 6 addon compatibility

Status: complete
Source: User-approved TC6 compatibility audit and iterative implementation plan from 2026-07-24; GitHub issue #9
Last updated: 2026-07-24

## Objective
Replace one-crash-at-a-time TC6 addon shims with a deterministic compatibility layer that inventories the BETA26 ABI, resolves pinned addon bytecode before runtime, maps supported state to canonical TC4 owners, reports unsupported symbols explicitly, and loads the Witchery modset through dedicated-server smoke.

## Execution Directive
Complete the frozen Required Outcomes using the listed Change Envelope and Primary Evidence. Work on the smallest unresolved outcome. Do not add requirements from reviews, tests, tools, speculative risks, or optional source text. Finish when every required outcome is resolved and affected constraints remain satisfied.

## Frozen Contract

### Required Outcomes
- R1: Record and verify the complete public/protected TC6 BETA26 API shape.
  - Source: Approved prompts 1 and 3.
  - Acceptance: A deterministic, dependency-free classfile tool verifies the donor hash and emits/compares a committed ABI snapshot with exact JVM owners, descriptors, access, hierarchy, and member flags.
  - Primary evidence: Focused tool tests and `./scripts/dev.sh compat-validate` pass.
  - Status: verified
  - Evidence: `scripts/tc6-compat.py` reproducibly verified the donor SHA and 158 class / 541 field / 966 method records; focused Python tests, `./scripts/dev.sh compat-validate`, and `./scripts/dev.sh validate` passed.
- R2: Resolve addon TC references before runtime.
  - Source: Approved prompts 2 and 3.
  - Acceptance: Pinned addon manifests produce deterministic demand inventories; the resolver handles JVM inheritance and reports class/member/access/static/interface mismatches, mixin targets, and reflective candidates without committing third-party jars.
  - Primary evidence: Corpus rescan and synthetic resolver tests pass with zero unresolved symbols for supported targets.
  - Status: verified
  - Evidence: The opcode-aware corpus snapshot records six pinned addons and 238 class/member/mixin entries; JVM hierarchy/static/access/interface resolution passes all supported addons and reports the three distinct audited Witchery candidate gaps. Eight focused Python tests and `./scripts/dev.sh compat-validate` passed.
- R3: Make ForgeGradle modset smoke reproducible for production SRG jars.
  - Source: Approved prompt 4.
  - Acceptance: Smoke preprocessing verifies source hashes, performs owner-aware SRG-to-MCP remapping into ignored staging, preserves source jars/resources, and isolates the smoke world; production smoke continues to use original jars.
  - Primary evidence: Synthetic inherited-owner regression and Witchery dev smoke pass.
  - Status: verified
  - Evidence: `smoke-modset` now hash-checks original jars, builds dependency-inclusive inheritance metadata, remaps ignored staging copies, verifies unchanged entries/resources and absence of mapped SRG refs, and uses an isolated world. The Witchery run passed both remap checks and reached the expected `CommonInternals` crash instead of the prior inherited-owner `NoSuchMethodError`.
- R4: Fix the observed Witchery TC6 contracts through canonical TC4-backed adapters.
  - Source: GitHub issue #9 and approved prompts 5-7.
  - Acceptance: `CommonInternals.scanEntities` is an exact identity alias; the observed `AuraHandler.drainVis(World, BlockPos, float, boolean)` contract is implemented only if donor/TC4 RECON proves a safe adapter; the pinned Witchery corpus has no unresolved accepted symbols and reaches dedicated-server ready state.
  - Primary evidence: Focused ABI/semantic tests, corpus resolution, and `./scripts/dev.sh smoke-modset witchery` pass.
  - Status: verified
  - Evidence: `CommonInternals.scanEntities` is an exact `ArrayList` identity alias to `ThaumcraftApi.scanEntities`; untyped TC6 drain uses unique canonical TC4 visnet sources with non-mutating simulation. Focused tests, full validation, zero-gap corpus resolution, and `./scripts/dev.sh smoke-modset witchery` passed.
- R5: Publish an explicit hybrid compatibility policy and release gate.
  - Source: Approved prompts 8-10.
  - Acceptance: Every accepted symbol is classified `EXACT`, `PROJECTED`, `LINK_ONLY`, or `UNSUPPORTED`; safe structural symbols have exact ABI guards; stateful domains use canonical adapters or remain explicitly unsupported; one command runs provenance, ABI, demand, tests, smoke-required checks, MCP leak check, and final artifact verification.
  - Primary evidence: `./scripts/dev.sh compat-validate`, `./scripts/dev.sh validate`, required modset smoke, and final `./scripts/dev.sh build` pass on one tree.
  - Status: verified
  - Evidence: The accepted target classifies all 127 observed symbols (`EXACT=13`, `PROJECTED=88`, `LINK_ONLY=22`, `UNSUPPORTED=4`) and fails on unclassified demand. `./scripts/dev.sh compat-release` passed normal validation, all five isolated server modsets, final build, MCP leak check, and repeated ABI/demand/target verification.
- R6: Commit the completed scoped implementation.
  - Source: User instruction from 2026-07-24.
  - Acceptance: A single scoped commit contains the implementation and generated text manifests after all required validation.
  - Primary evidence: Commit hash and committed-file list in the final report.
  - Status: verified
  - Evidence: The validated implementation and deterministic text artifacts are contained in the single scoped commit reported with the final result.

### Constraints
- Preserve Java 8, Forge 1.12.2, TC4 public API signatures, package boundaries, ids, keys, and canonical gameplay state.
- Keep `thaumcraft_src/**`, original TC4/TC6 jars, and third-party addon jars read-only; do not commit remapped jars, logs, worlds, inheritance maps, or decompiled output.
- Do not claim full TC6 semantic parity from ABI or smoke success.
- Do not create duplicate mutable stores or silent no-op implementations for stateful TC6 systems.
- Runtime-affecting checkpoints require dedicated-server smoke; the final code change requires `./scripts/dev.sh build`.

### Non-goals
- Porting TC6 casters, modular golems, theorycraft, or other gameplay systems without a proven TC4 backend.
- Making arbitrary reflective or dynamically generated addon calls statically provable.
- Replacing TC4 gameplay architecture with TC6 internals.

## Change Envelope
- Target: ABI/demand tooling, smoke preprocessing, compatibility manifests/docs, exact structural shims, and canonical adapters required by the pinned supported corpus.
- Expected paths, symbols, and direct consumers: `scripts/dev.sh`, `scripts/tc6-compat.py`, `scripts/smoke-modsets/**`, `docs/compatibility/**`, focused tests, `thaumcraft.api.internal.CommonInternals`, `thaumcraft.common.world.aura.AuraHandler`, `ThaumcraftApi.scanEntities`, and `ScanManager`.
- Allowed artifacts: Java/Python/shell source, deterministic text manifests, tests, docs, existing dependency-free tooling.
- Forbidden artifacts: new dependencies, modified donor/original jars, committed third-party/remapped jars, fake persistent state, speculative subsystem implementations.
- User or harness budget: iterative checkpoints; one final commit after smoke and build.

## Current Checkpoint
- Closes: none; the frozen objective is complete.
- Smallest next action: none.
- Expected evidence: none beyond the completion evidence below.
- Stop or replan if: a future request creates a new objective.

## Current State
- Resolved: R1-R6 are verified: ABI/demand gates, owner-aware smoke, Witchery adapters, semantic policy, combined release validation, final build, and the requested scoped commit.
- Last relevant evidence: `./scripts/dev.sh compat-release`, final `./scripts/dev.sh compat-validate`, and explicit final `./scripts/dev.sh build` passed.
- Blocker: None.
- Next: none.

## Material Decisions
- 2026-07-24: Use a hybrid layer: exact ABI where safe, canonical TC4 projections where proven, link-only only when explicit, and fail-closed unsupported stateful domains.
- 2026-07-24: The achievable silver bullet is exhaustive pre-runtime resolution and explicit semantic coverage, not an unsupported claim that every TC6 gameplay subsystem exists.
- 2026-07-24: `CommonInternals.scanEntities` is a canonical-state alias, not an addon-specific independent registry.

## Checkpoint History
- 2026-07-24: User approved the audited ten-step implementation plan and requested iterative implementation, smoke validation, and a final commit.
- 2026-07-24: R1 added dependency-free classfile ABI extraction and the deterministic BETA26 snapshot; focused tests and full validation passed.
- 2026-07-24: R2 added opcode-aware addon demand extraction, mixin/reflective classification, JVM hierarchy resolution, and a pinned six-addon corpus; supported addons resolve and Witchery fails closed on three known symbols.
- 2026-07-24: R3 added hash-verified owner-aware dev remapping and isolated smoke worlds; Witchery production inputs now reproduce the real TC gap without ForgeGradle mapping false failures.
- 2026-07-24: R4 added the canonical entity-scan identity alias and projected untyped vis drain onto TC4 visnet; focused checks, full validation, corpus resolution, and Witchery smoke passed.
- 2026-07-24: R5 added complete per-symbol semantic classification, removed substitute chunk aura state, and introduced a combined release command; all five modsets and final artifact gates passed.

## Completion
- Resolved outcomes: R1-R6.
- Commands and artifacts: focused Python/Java tests; `./scripts/dev.sh validate`; `./scripts/dev.sh smoke-modset witchery`; `./scripts/dev.sh compat-release`; final `./scripts/dev.sh compat-validate`; final `./scripts/dev.sh build`; donor/demand/semantic snapshots and the universal jar.
- Constraint and diff-scope check: donor/original/addon jars remained unchanged and uncommitted; remapped jars, worlds, logs, and inheritance maps stayed ignored; no dependency, id, key, or TC4 signature changes were introduced.
- Final status: complete.
