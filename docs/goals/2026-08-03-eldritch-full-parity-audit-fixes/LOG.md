# Material Log: Full Eldritch parity audit fixes

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Log-Policy: append-only; correct prior entries with a new event

## E-0001 — 2026-08-03 — ledger_initialized

- State-Revision: 1
- Outcomes/Findings: none
- Git before/after: `master` / `a1a2973e4fd4b38b4e49789391ecc4292c998373`; worktree clean before ledger scaffold
- Action: Created the durable Goal Ledger before RECON or implementation.
- Evidence: S-001 and S-002; `.opencode/skills/goal-repo-docs-opencode/scripts/goal_init.py`
- Result: draft
- Decision/Reason: Use `confirmed_in_scope` because the user requested the bounded full audit and then explicitly approved iterative implementation and commits. Conditional legacy-save/dimension migration remains deferred per the approved plan's separate-decision caveat.
- Next: Resume all completed audit sessions and persist one immutable packet per A-001..A-029 before normalization.

## E-0002 — 2026-08-03 — audit_charter_registered

- State-Revision: 1
- Outcomes/Findings: A-001..A-029
- Git before/after: `master` / `a1a2973e4fd4b38b4e49789391ecc4292c998373`; only ledger scaffold expected dirty
- Action: Registered the TC4 oracle, port baseline, prior advisory goal, promotion policy, audit boundary, 29-shard map, preserve adaptations, and validation gates.
- Evidence: `SOURCES.md`, `RECON.md`, S-003 SHA-256, baseline Git status/HEAD.
- Result: RECON remains collecting until every prior conversational result is persisted and normalized.
- Decision/Reason: Files, not task responses or the previous completed goal, will be authoritative for implementation.
- Next: Materialize report packets through resumed subagent sessions; verify no product paths change.

## E-0003 — 2026-08-03 — reentry_reconciled

- State-Revision: 2
- Outcomes/Findings: A-001..A-029
- Git before/after: recorded baseline `a1a2973`; current `6737687`
- Action: Rehydrated after the interrupted network/subagent wave, inventoried report packets, and reconciled the changed Git anchor.
- Evidence: 13 report files exist; `git log` shows `d73897e0 docs(agents): define temporary workspace` and `6737687a add general`; their diff is limited to `.gitignore`, `.opencode/agents/general.md`, `.tmp/.gitkeep`, and `AGENTS.md`.
- Result: No product or goal conflict. The new anchor is `6737687ada4cb582a280165fd895cfa5438102ff`.
- Decision/Reason: Treat the two commits as concurrent repository-infrastructure work and preserve them unchanged.
- Next: Resume only missing A-011..A-013, A-016..A-019 and then A-021..A-029 packet sessions.

## E-0004 — 2026-08-03 — second_reentry_reconciled

- State-Revision: 3
- Outcomes/Findings: A-001..A-029
- Git before/after: recorded anchor `6737687`; current `866fea24`
- Action: Rehydrated after the second network interruption and inventoried durable packets.
- Evidence: A-001..A-020 now exist; concurrent commit `866fea24 add general` modifies only `.opencode/agents/general.md`.
- Result: No product or goal conflict; 20 of 29 assignments are durable.
- Decision/Reason: Preserve the unrelated agent configuration and adopt `866fea24` as the current anchor.
- Next: Persist A-021..A-029, then normalize all packets.

## E-0005 — 2026-08-03 — audit_packets_terminal

- State-Revision: 4
- Outcomes/Findings: A-001..A-029
- Git before/after: `866fea24` / `866fea24`; only untracked goal artifacts and active pointer
- Action: Completed durable materialization of every previously executed audit assignment.
- Evidence: 29 report files under `reports/`, 6474 total lines; packet headers are terminal and no product path changed.
- Result: The conversational audit is now recoverable from repository files; report fan-out is closed.
- Decision/Reason: Begin normalization from packets rather than task summaries. Conditional `.thaum` and `DIM-42` migration candidates remain visible but deferred pending the separate support decision named by the approved plan.
- Next: Normalize all local findings and controls, adjudicate duplicates/conflicts, freeze GOAL, lint, and commit the ledger before the first product checkpoint.

## E-0006 — 2026-08-03 — normalization_adjudication

- State-Revision: 5
- Outcomes/Findings: normalization of A-001..A-029
- Git before/after: `866fea24` / `866fea24`; product tree unchanged
- Action: Accepted six lossless normalization proposals and resolved the material policy/adaptation conflicts needed for a non-blocking frozen contract.
- Evidence: `evidence/normalize-A001-A005.md`, `normalize-A006-A010.md`, `normalize-A011-A015.md`, `normalize-A016-A020.md`, `normalize-A021-A025.md`, and `normalize-A026-A029.md`.
- Decision/Reason: Direct TC4 `.thaum` and `DIM-42` migration remain `deferred`, not blocking, because the approved plan explicitly required a separate support decision. The extra Void Robe boots are a required fresh-game parity correction implemented non-destructively as a hidden compatibility/tombstone registration rather than silently deleting a persisted ID. Restore the common `WAND_CAP_VOID` alias, strict Sanity Checker no-action behavior, NBT/addon-visible furnace branch parity, and literal TC4 boss/crab behavior where reports found gameplay drift; these are in the confirmed parity charter. Preserve synchronous maze publication, server authority, secondary protection checks, safe dead-projectile return, save/state hardening, finite-fluid translation, and other enumerated Forge adaptations. A-016's lock render-distance preservation claim is superseded by A-019's direct renderer/range evidence; restore 96-block visual parity. The uppercase locale alias finding is required: retain lowercase Forge locale and exact English content, but do not preserve an alias that overrides resource-pack precedence. Dynamic JEI wrappers, limited-crafting policy, alternate `#` scan route, and addon-only derivation deltas remain deferred/constraints absent a native Eldritch defect.
- Result: No scope-changing question remains for fresh-world Eldritch parity and approved hardening; normalized synthesis may proceed without product edits.
- Next: Write and reconcile stable F-* entries, independently verify high-severity anchors, freeze traceable outcomes, lint, and commit the ledger.

## E-0007 — 2026-08-03 — goal_toolkit_v2_1_replaced

- State-Revision: 6
- Outcomes/Findings: active goal structural migration; no product finding admitted
- Git before/after: `866fea24` / `41ecbd1d`; user-owned `SKILL.md` preserved outside the commit
- Action: Replaced the Goal Ledger toolkit from `goal-repo-docs-opencode-v2.1.0.zip` except `SKILL.md`, verified every packaged checksum, compiled the Python scripts, deleted the archive, and committed the toolkit structure.
- Evidence: archive SHA-256 `8aab151dd74b4b5952b57d63c40fad67a6df8e3bb24a5478c00e0c16c26b81ab`; installed `VERSION` `2.1.0`; `sha256sum -c SHA256SUMS` all OK; commit `41ecbd1d`.
- Result: v2.1 forbids the old `confirmed_in_scope` promotion and requires Production Admission plus a finite pre-RECON governor. The historical audit consumed 29 assignments in 3 waves, exceeding skill-default 12/1. The old 134-entry draft also cannot retain its automatic required dispositions.
- Decision/Reason: Migrate the active goal to `production_gate`; keep all exact reports/proposals/draft as evidence; do not roll back or discard findings; do not edit product files until an authoritative exact finite grant permits the historical audit capacity and selected implementation capacity.
- Next: obtain the exact numeric grant, complete v2.1 admission fields and capacity ranking, freeze/lint/commit the ledger, then resume iterative product checkpoints.

## E-0008 — 2026-08-03 — priority_budget_granted

- State-Revision: 7
- Outcomes/Findings: Production Admission capacity; candidate set F-001..F-106 in the normalization draft
- Git before/after: `41ecbd1d` / `41ecbd1d`; no product changes
- Action: Presented exact full-versus-priority v2.1 grants; user selected the priority budget.
- Evidence: S-009 exact grant `audit_assignments=29; audit_waves=3; required_findings=8; total_fix_checkpoints=12; scope_amendments=0; implementation_subagent_waves=0`.
- Result: Historical RECON capacity is authorized. Implementation admission remains capped at eight findings; report test debt will be charged to those findings rather than promoted separately.
- Decision/Reason: Rank deterministic supported-path and authenticated authority defects by impact and bounded cost. Admit equipped Void armor double repair (F-018), generated Outer crystal support loss (F-035), Guardian armor mistranslation (F-038), Guardian 25x XP/talk mistranslation (F-039), dedicated-client research/aspect cache divergence (F-085), fixed note endpoint packet mutation (F-087), hidden/lost clue packet bypass (F-088), and built-in phenomenon scan interception (F-105). Reserve two checkpoints for F-085 and F-087 and one for each other finding, total ten. All other candidates remain durable and are deferred by their failed gate or `over_capacity`.
- Next: migrate the normalized draft to v2.1 fields, freeze 8/10 admission, lint, commit the ledger, and start the smallest admitted checkpoint.

## E-0009 — 2026-08-03 — contract_frozen_v2_1

- State-Revision: 8
- Outcomes/Findings: R-001..R-008; F-018, F-035, F-038, F-039, F-085, F-087, F-088, F-105
- Git before/after: `41ecbd1d` / `41ecbd1d`; product tree unchanged
- Action: Migrated all 134 normalized entries to v2.1 admission fields, retained exact local crosswalks, admitted exactly eight findings, deferred every other candidate, and directly re-read each admitted current-source branch before freeze.
- Evidence: `RECON.md` v2.1; direct reads of Void armor callbacks, crystal support/GenCommon ordering, Guardian methods, research sync/cache, endpoint placement, completion prerequisites, and scan handler dispatch; S-009.
- Result: Required count 8/8; fix checkpoints 10/12; audit assignments 29/29; waves 3/3; no required test-debt findings; implementation subagent waves 0.
- Decision/Reason: Each admitted item is a deterministic supported-path gameplay/progression/authority defect. All lower-ranked confirmed deltas are durable but deferred with `over_capacity` or their specific failed gate. No review or test may promote them during execution.
- Next: stamp/lint, commit the frozen ledger, then begin R-001/F-018 with write-ahead counter increment.

## E-0010 — 2026-08-03 — frozen_ledger_lint_clean

- State-Revision: 9
- Outcomes/Findings: R-001..R-008 / F-018, F-035, F-038, F-039, F-085, F-087, F-088, F-105
- Git before/after: `41ecbd1d` / `41ecbd1d`; goal artifacts untracked; user-owned `SKILL.md` dirty and excluded
- Action: Stamped and linted the frozen v2.1 contract and normalized RECON.
- Evidence: `python3 .opencode/skills/goal-repo-docs-opencode/scripts/goal_lint.py docs/goals/2026-08-03-eldritch-full-parity-audit-fixes --stamp`; repeated without `--stamp`.
- Result: Both runs passed with zero errors and zero warnings; contract/recon/source/report hashes are recorded in STATE.
- Decision/Reason: The ledger is eligible for the S-002-required pre-implementation commit. The user-owned toolkit `SKILL.md` remains outside this commit.
- Next: Commit only `.opencode/active-goal` and `docs/goals/2026-08-03-eldritch-full-parity-audit-fixes/**`, then begin R-001/F-018.

## E-0011 — 2026-08-03 — checkpoint_start

- State-Revision: 10
- Outcomes/Findings: R-001 / F-018
- Git before/after: `f27fddfb` / `f27fddfb`; only user-owned `SKILL.md` dirty
- Action: Started the single allowed F-018 fix checkpoint and incremented `checkpoints_started` to 1/1.
- Hypothesis: The port's 1.12 inventory lifecycle invokes both `onUpdate` and `onArmorTick` for equipped Void armor; retaining one equipped repair path and the ordinary stored-stack path restores TC4's one-point cadence.
- Expected Evidence: Focused lifecycle assertions show exactly one repair at tick multiples of 20 for equipped and stored pieces; no global inventory behavior changes.
- Stop/Replan: Stop if base and robe armor require different callbacks or if a global Forge lifecycle change would be needed.
- Next: Read the frozen outcome/finding and direct product/test paths, then apply the smallest callback fix.

## E-0012 — 2026-08-03 — checkpoint_closed

- State-Revision: 11
- Outcomes/Findings: R-001 / F-018
- Git before/after: `f27fddfb` / checkpoint commit pending
- Action: Removed the redundant `onArmorTick` repair callbacks from base Void armor and Void Robe armor while retaining ordinary `onUpdate`; added a focused real-inventory lifecycle assertion for equipped base, equipped robe, and stored base stacks.
- Evidence: `./scripts/dev.sh gradle test --tests thaumcraft.common.items.armor.ItemVoidArmorParityTest` -> BUILD SUCCESSFUL; `./scripts/dev.sh validate --smoke` -> PASS (`git-status compile+test+reobf check-jar smoke-server`); product diff check clean.
- Result: At tick 20 each supported stack repairs exactly `5 -> 4`; stored-stack repair, server/living-owner guards, cadence, and creative no-repair behavior remain intact.
- Decision/Reason: Close F-018 and derive R-001 verified. The change is confined to its one-checkpoint envelope and does not alter global Forge inventory lifecycle.
- Next: Commit the R-001 checkpoint excluding user-owned `SKILL.md`, then start R-002/F-035.

## E-0013 — 2026-08-03 — checkpoint_start

- State-Revision: 12
- Outcomes/Findings: R-002 / F-035
- Git before/after: `aa0ab9eb` / `aa0ab9eb`; only user-owned `SKILL.md` dirty
- Prior checkpoint commit: R-001/F-018 -> `aa0ab9eb fix(armor): restore Void repair cadence`.
- Action: Started the single allowed F-035 fix checkpoint and incremented `checkpoints_started` to 1/1.
- Hypothesis: Metadata/orientation-aware support validation plus orientation-before-validation in the shipped generator will preserve generated meta-7 crystals and exact-face attachment semantics without changing global block solidity.
- Expected Evidence: Focused Outer generation evidence covers meta-7 placement survival and exact support removal; no crystal drop/probability/rendering delta.
- Stop/Replan: Stop if the narrow lifecycle cannot be restored inside `BlockCrystal` and directly necessary `GenCommon` ordering.
- Next: Read the frozen outcome/finding and direct crystal/generation/test paths, then make the minimum support-lifecycle change.

## E-0014 — 2026-08-03 — checkpoint_closed

- State-Revision: 13
- Outcomes/Findings: R-002 / F-035
- Git before/after: `aa0ab9eb` / checkpoint commit pending
- Action: Removed the non-original placement-time stay check and replaced any-neighbor retention with recorded-orientation validation: metas 0..6 require the exact side-solid support face; meta 7 requires the exact opposite support to remain non-air.
- Evidence: `./scripts/dev.sh gradle test --tests thaumcraft.common.lib.world.dim.OuterDungeonGenerationStaticGuardTest` -> BUILD SUCCESSFUL; `./scripts/dev.sh validate --smoke` -> PASS; focused product diff check clean.
- Result: The shipped `GenCommon` placement-before-orientation sequence is retained and no longer drops meta-7 crystals before orientation assignment; later support changes use TC4 metadata/orientation semantics.
- Decision/Reason: Close F-035 and derive R-002 verified. No `GenCommon` topology/probability edit or global Eldritch side-solid change was needed.
- Next: Commit the R-002 checkpoint excluding user-owned `SKILL.md`, then start R-003/F-038.

## E-0015 — 2026-08-03 — checkpoint_start

- State-Revision: 14
- Outcomes/Findings: R-003 / F-038
- Git before/after: `90c689b0` / `90c689b0`; only user-owned `SKILL.md` dirty
- Prior checkpoint commit: R-002/F-035 -> `90c689b0 fix(outer): preserve oriented crystal support`.
- Action: Started the single allowed F-038 fix checkpoint and incremented `checkpoints_started` to 1/1.
- Hypothesis: The SRG-mapped TC4 constant is Guardian intrinsic armor, while the port attached it to the default-equivalent spawn-cap method.
- Expected Evidence: Focused source evidence requires armor 4 and no custom spawn-cap override; smoke confirms common/server compatibility.
- Stop/Replan: Stop if mapped 1.12 armor requires a broader attribute/lifecycle change.
- Next: Read the frozen outcome/finding and direct Guardian/test paths, then replace only the mistranslated method.

## E-0016 — 2026-08-03 — checkpoint_closed

- State-Revision: 15
- Outcomes/Findings: R-003 / F-038
- Git before/after: `90c689b0` / checkpoint commit pending
- Action: Replaced the mistranslated Guardian `getMaxSpawnedInChunk()` override with `getTotalArmorValue()` returning 4 and added focused source assertions rejecting the spawn-cap misuse.
- Evidence: `./scripts/dev.sh gradle test --tests thaumcraft.common.entities.monster.EntityEldritchGuardianStaticGuardTest` -> BUILD SUCCESSFUL; `./scripts/dev.sh validate --smoke` -> PASS; focused diff check clean.
- Result: Guardian intrinsic armor is restored from 0 to 4 while inherited spawn-cap behavior remains unchanged.
- Decision/Reason: Close F-038 and derive R-003 verified; no other Guardian attributes, AI, isolation, or entity behavior changed.
- Next: Commit the R-003 checkpoint excluding user-owned `SKILL.md`, then start R-004/F-039.

## E-0017 — 2026-08-03 — checkpoint_start

- State-Revision: 16
- Outcomes/Findings: R-004 / F-039
- Git before/after: `7b2f1909` / `7b2f1909`; only user-owned `SKILL.md` dirty
- Prior checkpoint commit: R-003/F-038 -> `7b2f1909 fix(entities): restore Guardian armor`.
- Action: Started the single allowed F-039 fix checkpoint and incremented `checkpoints_started` to 1/1.
- Hypothesis: The port already stores XP 20 in the constructor; the stray method returns the TC4 talk interval as XP. Removing it and adding the mapped talk method restores both values.
- Expected Evidence: Focused source evidence proves XP 20, no XP override, and talk interval 500; smoke confirms common/server compatibility.
- Stop/Replan: Stop if either value is controlled by a shared lifecycle outside the Guardian class.
- Next: Apply only the Guardian XP/talk mapping correction and its focused assertions.

## E-0018 — 2026-08-03 — checkpoint_closed

- State-Revision: 17
- Outcomes/Findings: R-004 / F-039 -> verified
- Git before/after: `7b2f1909` / product/test plus STATE/LOG ready for checkpoint commit
- Action: Removed `getExperiencePoints(...) -> 500`, retained constructor `experienceValue = 20`, and added `getTalkInterval() -> 500`; extended the focused Guardian guard.
- Evidence: `./scripts/dev.sh gradle test --tests thaumcraft.common.entities.monster.EntityEldritchGuardianStaticGuardTest` -> BUILD SUCCESSFUL in 17s; `./scripts/dev.sh validate --smoke` -> PASS; `git diff --check` -> PASS.
- Preserve Result: Guardian ambient/hurt/death sound and drop methods were unchanged; R-003 armor method remains guarded.
- Budget: F-039 consumed 1/1 fix checkpoint and 0 material replans.
- Next: Commit the R-004 checkpoint excluding user-owned `SKILL.md`, then begin R-005/F-085 checkpoint 1/2.
