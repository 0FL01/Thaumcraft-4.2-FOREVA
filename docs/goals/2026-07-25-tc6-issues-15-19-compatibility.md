# Goal: TC6 compatibility for issues #15-#19

Status: complete
Source: GitHub issues #15-#19 and user instructions dated 2026-07-25
Last updated: 2026-07-25

## Objective

Make the mods reported in issues #15-#19 load together with this TC4 port through a bounded, donor-verified TC6 BETA26 compatibility layer, with the final client smoke launching every pinned issue mod in one modset.

## Execution Directive

Complete the frozen Required Outcomes using the listed Change Envelope and Primary Evidence. Work on the smallest unresolved outcome. Do not add requirements from reviews, tests, tools, speculative risks, or optional source text. Finish when every required outcome is resolved and affected constraints remain satisfied.

## Frozen Contract

### Required Outcomes

- R1: Balkon's Expansion compatibility from issue #15.
  - Source: https://github.com/0FL01/Thaumcraft-4.2-FOREVA/issues/15
  - Acceptance: The pinned WeaponMod and Balkon's Expansion modset completes dedicated-server startup without Thaumcraft linkage or lifecycle failures.
  - Primary evidence: `./scripts/dev.sh smoke-modset balkonsexpansion`
  - Status: verified
  - Evidence: `./scripts/dev.sh smoke-modset balkonsexpansion` reached server ready state on 2026-07-25.

- R2: Solar Flux compatibility from issue #16.
  - Source: https://github.com/0FL01/Thaumcraft-4.2-FOREVA/issues/16
  - Acceptance: The pinned Solar Flux modset completes dedicated-server startup, including its Thaumcraft init and post-init integration.
  - Primary evidence: `./scripts/dev.sh smoke-modset solarflux`
  - Status: verified
  - Evidence: `./scripts/dev.sh smoke-modset solarflux` reached server ready state on 2026-07-25; the required integration marker and `Registering TC researches...` confirm init and post-init execution.

- R3: Botania CEu compatibility from issue #17.
  - Source: https://github.com/0FL01/Thaumcraft-4.2-FOREVA/issues/17
  - Acceptance: The pinned Botania modset completes dedicated-server startup and its aspect registry subscriber runs without the null Warp Ward brew failure.
  - Primary evidence: `./scripts/dev.sh smoke-modset botania`
  - Status: verified
  - Evidence: `./scripts/dev.sh smoke-modset botania` reached server ready state after its TC aspect subscriber completed on 2026-07-25.

- R4: Just Enough Magiculture compatibility from issue #18.
  - Source: https://github.com/0FL01/Thaumcraft-4.2-FOREVA/issues/18
  - Acceptance: The pinned JEM dependency set completes client mod loading and JEM post-init without missing Thaumcraft implementation classes or members.
  - Primary evidence: `./scripts/dev.sh smoke-client-modset justenoughmagiculture`
  - Status: verified
  - Evidence: `THAUMCRAFT_SMOKE_TIMEOUT=60s ./scripts/dev.sh smoke-client-modset justenoughmagiculture` reported successful Forge loading after JEM registered its custom JER entries on 2026-07-25.

- R5: JAOPCA/WrapUp compatibility from issue #19.
  - Source: https://github.com/0FL01/Thaumcraft-4.2-FOREVA/issues/19
  - Acceptance: The pinned JAOPCA and WrapUp modset completes dedicated-server startup without the TC6 `ResearchEntry` return-descriptor failure.
  - Primary evidence: `./scripts/dev.sh smoke-modset jaopca`
  - Status: verified
  - Evidence: `./scripts/dev.sh smoke-modset jaopca` reached server ready state with both TC4 and TC6 research lookup descriptors on 2026-07-25.

- R6: Systemic TC6 compatibility floor for the issue corpus.
  - Source: User requirement that the solution be a "Silver bullet" rather than per-mod hacks; approved implementation plan.
  - Acceptance: Donor ABI and pinned-addon demand validation reports no unclassified or unresolved symbol in the reviewed issue linkage floor, with semantics recorded as `EXACT`, `PROJECTED`, `LINK_ONLY`, or `UNSUPPORTED`.
  - Primary evidence: `./scripts/dev.sh compat-validate`
  - Status: verified
  - Evidence: `./scripts/dev.sh compat-validate` passed on 2026-07-25 for 11 supported addons, 540 demand entries, 310 classified symbols, and the reviewed 510-entry donor-to-target deferred-gap snapshot.

- R7: Combined final issue-mod smoke.
  - Source: User instruction: the goal is closed only when all smoke tests pass and the final test launches all mods from the issues together.
  - Acceptance: One pinned client modset containing every dependency and addon used by R1-R5 reports successful Forge mod loading with no crash marker or new crash report.
  - Primary evidence: `./scripts/dev.sh smoke-client-modset issues-15-19`
  - Status: verified
  - Evidence: `THAUMCRAFT_SMOKE_TIMEOUT=90s ./scripts/dev.sh smoke-client-modset issues-15-19` reported successful Forge loading on 2026-07-25, including the required Solar Flux and JEM integration markers.

### Constraints

- C1: Preserve the existing public TC4 API descriptors while adding TC6-compatible alternatives.
- C2: Do not edit `thaumcraft_src/**` or either donor Thaumcraft jar.
- C3: Keep Java 8, Forge 1.12.2, Gradle, Baubles, mod id, existing registry identities, NBT/config/packet/GUI/dimension ids stable unless an explicit migration preserves shipped state.
- C4: Production compatibility code must implement generic TC6 contracts; it must not branch on the presence or identity of a reported addon.
- C5: Runtime-affecting checkpoints require the corresponding server or client smoke; compile success alone is insufficient.
- C6: Third-party jars remain local under `.smoke/`; only manifests, code, tests, scripts, and documentation may enter the diff.

### Non-goals

- Full semantic parity with every Thaumcraft 6 subsystem or every unobserved TC6 addon.
- Donor-wide support for all `thaumcraft.common.*` implementation classes.
- Replacing canonical TC4 gameplay state with a second TC6 registry or research store.
- Unrelated port cleanup or dependency upgrades.

## Change Envelope

- Target: TC6 ABI, registry, research, item/material, scanning/recipe, and implementation-class projections directly required by the five pinned issue modsets.
- Expected paths, symbols, and direct consumers:
  - `scripts/tc6-compat.py`, its tests, ABI snapshots/policy, smoke manifests, and `scripts/dev.sh`.
  - `thaumcraft.api.*` shims consumed by the pinned addons.
  - `ThaumcraftSixCompatibility`, canonical registry/config/research owners, and exact demanded `thaumcraft.common.*` class-token bridges.
  - Focused static/artifact guards and this goal document.
  - `Dockerfile` only if a reproducible virtual display is required for R4/R7.
- Allowed artifacts: Java source, Python/shell validation code, tests, manifests, compatibility documentation, and development-only client-smoke infrastructure.
- Forbidden artifacts: committed third-party jars, a second persistent gameplay store, addon-name conditionals, silent exception swallowing, dependency/platform upgrades, and edits to read-only reference material.
- User or harness budget: No fixed file or time budget; stop before material scope beyond the observed issue paths unless a frozen outcome proves it necessary.

## Current Checkpoint

- Closes: complete.
- Smallest next action: none; completion is terminal.
- Expected evidence: all R1-R7 evidence is current and successful.
- Stop or replan if: a newer user instruction supersedes this completed contract.

## Current State

- Resolved: R1-R7 pass. Every dedicated issue modset and the combined client modset load successfully; the supported corpus has no unresolved or unclassified linkage-floor symbols.
- Last relevant evidence: The combined client smoke passed with both required integration markers, followed by successful `validate`, `compat-validate`, and final `build` gates.
- Blocker: None.
- Next: None.

## Material Decisions

- 2026-07-25: Compatibility is bounded by the donor ABI plus the pinned issue corpus; full TC6 parity is explicitly not claimed.
- 2026-07-25: The final closure test is a combined client modset containing all issue mods, in addition to dedicated diagnostic smokes.
- 2026-07-25: The unauthorized JEM diff was quarantined rather than reused or silently discarded.
- 2026-07-25: TC4 fluent research setters and TC6 void setters collide at Java source level; two donor descriptors are added with a narrow ASM post-compile bridge while preserving both public ABIs.

## Checkpoint History

- 2026-07-25: RECON complete; plan approved; unauthorized subagent changes quarantined in `stash@{0}`.
- 2026-07-25: Baseline modsets reproduced all five paths. Client smoke now uses container-local dummy Xorg; installing `xrandr` fixed LWJGL 2 mode discovery. Solar Flux silently skipped its Thaumcraft class, so manifests can require fixed integration log markers.
- 2026-07-25: R1 and R3 verified. Balkon's and Botania dedicated smokes reached ready state after projecting TC6 item interfaces/materials, restoring `warpward`, and adding the demanded aspect helper.
- 2026-07-25: R5 verified with an inherited static return-descriptor bridge; the issue-era JAOPCA/WrapUp modset reached ready state.
- 2026-07-25: R2 verified. Solar Flux's required compat marker, research post-init log, and server ready state were observed after adding the coherent TC6 research/scanning/theorycraft/recipe projection.
- 2026-07-25: R4 verified. JEM's demanded entity class tokens use unregistered aliases to canonical TC4 mobs; projected item/block fields are non-null, and the client smoke reached successful Forge loading. The fluid model now uses Forge's normalized lowercase fluid key, removing the pre-existing FML fatal model lookup.
- 2026-07-25: R6 verified. All issue corpus rows are supported and artifact-resolved; every distinct demand has a reviewed semantic level, and the complete donor-to-target API gap snapshot is gated without claiming full TC6 parity.
- 2026-07-25: R7 verified. The combined nine-jar issue client modset reached successful Forge loading with Solar Flux and JEM integration markers present; closure validation and final build passed.

## Completion

- Resolved outcomes: R1-R7 verified.
- Commands and artifacts: five dedicated issue smokes, `./scripts/dev.sh validate`, `./scripts/dev.sh compat-validate`, `THAUMCRAFT_SMOKE_TIMEOUT=90s ./scripts/dev.sh smoke-client-modset issues-15-19`, and `./scripts/dev.sh build` passed; the universal jar was rebuilt.
- Constraint and diff-scope check: TC4 descriptors and canonical state owners are preserved; no donor/reference jar, dependency version, or committed third-party binary changed. Remaining donor API gaps are explicitly bounded in `tc6-current-gaps.txt`.
- Final status: complete.
