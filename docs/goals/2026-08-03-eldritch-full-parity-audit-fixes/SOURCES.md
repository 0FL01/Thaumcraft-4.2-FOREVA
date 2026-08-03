# Sources: Full Eldritch parity audit fixes

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Last-Updated: 2026-08-03

## Authority Order

1. Latest explicit user instruction.
2. User-cited source specification.
3. Applicable repository instructions and existing contracts.
4. Frozen Goal Ledger.

## Source Registry

## S-001 — User objective

- Kind: user_instruction
- Authority: authoritative
- Locator: conversation message m0001
- Version/Date/Commit: 2026-08-03
- Fingerprint: not_available
- Relevant scope: Full audit of the Thaumonomicon Eldritch section, comparison of the Forge 1.12.2 port to the original, and bug discovery through a search probe followed by atomized parallel comparison.
- Budget Grant: none
- Durable excerpt/snapshot: "сделать оркестрировать полный аудит раздела `Eldrich` в Таумониконе, сравнить порт с оригиналом, мы ищем баги; строй карту после search probe, а далее вызывай массово @general (атомизировано) для сравнения"
- Notes: `Eldrich` was treated as the user's spelling of canonical category `ELDRITCH` / display name `Eldritch`.

## S-002 — User approval to implement

- Kind: user_instruction
- Authority: authoritative
- Locator: conversation message m0010, approving the post-RECON plan in m0009
- Version/Date/Commit: 2026-08-03
- Fingerprint: not_available
- Relevant scope: Authorizes iterative implementation of the confirmed audit findings, use of the Goal Ledger workflow, and checkpoint-scoped commits.
- Budget Grant: none
- Durable excerpt/snapshot: "Утверждаю, начинай итеративную разработку (вызов goal skill) и коммиты"
- Notes: Under goal-repo-docs v2.1 this approval is candidate-selection authority but not a finite capacity grant. Promotion is re-adjudicated through `production_gate`; conditional legacy-world migration remains deferred.

## S-003 — Thaumcraft 4 gameplay oracle

- Kind: binary_archive
- Authority: oracle
- Locator: `Thaumcraft-1.7.10-4.2.3.5.jar`
- Version/Date/Commit: Thaumcraft 4.2.3.5
- Fingerprint: SHA-256 `3dba9786966974701578a658d1bb369bf35bdf5f363079f5ac9c4910a39113be`
- Relevant scope: Authoritative original gameplay, client behavior, registries, research declarations, recipes, and packaged resources.
- Budget Grant: none
- Durable excerpt/snapshot: Exact methods are decompiled per report with CFR; the jar remains read-only.
- Notes: Comparison direction is TC4 4.2.3.5 -> Forge 1.12.2 port.

## S-004 — Extracted TC4 classes and assets

- Kind: repository_reference_tree
- Authority: oracle
- Locator: `thaumcraft_src/thaumcraft/**` and `thaumcraft_src/assets/thaumcraft/**`
- Version/Date/Commit: extracted from S-003; repository HEAD `a1a2973e4fd4b38b4e49789391ecc4292c998373`
- Fingerprint: representative class hashes: `ConfigResearch.class` SHA-256 `7ed2e392b82f75f6636abd57477a2de97566b5d395a0731b4e1e5d64e45aa50d`; `ConfigRecipes.class` SHA-256 `311f0ce37e451d13bdc73b94f83a5d1d333ca6cc1f4e3e1594a08edd0dfbca20`
- Relevant scope: Reproducible class/resource locators used by audit reports.
- Budget Grant: none
- Durable excerpt/snapshot: Paths and methods are recorded in each report packet.
- Notes: Read-only. S-003 controls if an extracted artifact conflicts.

## S-005 — Current Forge 1.12.2 port

- Kind: repository
- Authority: evidence
- Locator: repository root, primarily `src/main/**` and `src/test/**`
- Version/Date/Commit: branch `master`, baseline HEAD `a1a2973e4fd4b38b4e49789391ecc4292c998373`
- Fingerprint: Git commit above
- Relevant scope: Observed implementation under audit and implementation target.
- Budget Grant: none
- Durable excerpt/snapshot: Per-report paths, symbols, and line locators.
- Notes: Java 8, Forge 1.12.2, MCP stable_39 conventions are preserved.

## S-006 — Prior completed Eldritch goal

- Kind: repository_document
- Authority: advisory
- Locator: `docs/goals/2026-08-03-eldritch-thaumonomicon-parity.md`
- Version/Date/Commit: repository HEAD `a1a2973e4fd4b38b4e49789391ecc4292c998373`
- Fingerprint: SHA-256 `947358f58616fba37c869b82a949abe9d529c82f24efcb8540879d566c55cfa6`
- Relevant scope: Historical implementation decisions and intentionally preserved Forge 1.12 adaptations.
- Budget Grant: none
- Durable excerpt/snapshot: The document's completion claim is not accepted as proof against the new independent audit.
- Notes: Useful preserve controls include server authority, synchronous maze generation, safe portal arrival, finite-fluid translation, and explicit tile synchronization.

## S-007 — Thaumcraft 6 donor archive

- Kind: binary_archive
- Authority: advisory
- Locator: `Thaumcraft-1.12.2-6.1.BETA26.jar`
- Version/Date/Commit: Thaumcraft 6.1.BETA26
- Fingerprint: SHA-256 `9425f8643581b27ff8845b087c8bc6fc10425a32942f1a3f0e265ce6b38f7b5f`
- Relevant scope: Forge 1.12 rendering/model/API adaptation patterns only.
- Budget Grant: none
- Durable excerpt/snapshot: none
- Notes: Never a gameplay or Eldritch research-graph oracle.

## S-008 — User-directed goal toolkit replacement

- Kind: user_instruction
- Authority: authoritative
- Locator: conversation message m0048
- Version/Date/Commit: 2026-08-03; toolkit commit `41ecbd1d`
- Fingerprint: archive SHA-256 before deletion `8aab151dd74b4b5952b57d63c40fad67a6df8e3bb24a5478c00e0c16c26b81ab`; installed `VERSION` `2.1.0`
- Relevant scope: Replace the repository Goal Ledger toolkit structure from `goal-repo-docs-opencode-v2.1.0.zip` without overwriting the already updated `SKILL.md`, delete the archive, migrate the active goal structure, and continue the objective.
- Budget Grant: none
- Durable excerpt/snapshot: "делай именно replace без rollback, после архив .zip - удалить ... накатывай патч на goal структуру (кроме SKILL.md) и продолжай работу по цели"
- Notes: Authorizes the structural replacement and continuation. It does not enumerate the finite v2.1 capacity values required to exceed skill-default limits.

## S-009 — User-selected priority budget

- Kind: user_instruction
- Authority: authoritative
- Locator: conversation structured answer m0064
- Version/Date/Commit: 2026-08-03
- Fingerprint: not_available
- Relevant scope: Finite v2.1 capacity grant for the already completed 29-assignment/3-wave audit and a priority implementation pass capped at eight admitted findings and twelve total fix checkpoints.
- Budget Grant: audit_assignments=29; audit_waves=3; required_findings=8; total_fix_checkpoints=12; scope_amendments=0; implementation_subagent_waves=0
- Durable excerpt/snapshot: User selected `Приоритетные 8` from the exact offered grant above.
- Notes: This is a cap, not automatic admission. Production Gate ranking still determines which eight candidates become required; all remaining candidates stay durable and are deferred, primarily `over_capacity`.

## Source Capture Rules

- Repository file: record path and Git commit.
- Binary/archive: record path, version, and SHA-256.
- Web/CRW: record query or URL, retrieval timestamp, snapshot/excerpt path, and hash when practical.
- Chat-only claim: copy the exact material instruction into a durable source entry before relying on it.
- Never store secrets or indiscriminate full dumps.
