# Audit Packet: A-023 - Eldritch knowledge persistence and lifecycle

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-023
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare the Forge 1.12.2 port's `PlayerKnowledgeCapability`, provider/interface, `PlayerKnowledge`, and lifecycle integration with TC4 `PlayerKnowledge` and `ResearchManager` save/load semantics for Eldritch progression: research/discovery completion, aspects and pools, scans, warp categories/counter, and note-related data where applicable. Audit clone/death, logout, dimension/change sync lifecycle, serialization/defaults/clamping, server authority, and migration from TC4 saves.
- Anti-scope: Packet wire mechanics except where needed to establish lifecycle usage; product changes; central Goal Ledger changes; generic research schema, recipe, scan-registration, or GUI parity beyond direct persistence/API consequences.
- Oracle and comparison direction: S-003/S-004 Thaumcraft 4.2.3.5 `PlayerKnowledge`, `ResearchManager`, `EventHandlerEntity`, `ResearchNoteData`, `WarpEvents`, `CommandThaumcraft`, `PacketSyncResearch`, `PacketSyncAspects`, `AspectList`, and `ItemResource` -> S-005 Forge 1.12.2 port.
- Migration assumption: This audit treats in-place migration of original TC4 worlds, including player `.thaum` and `.thaumback` sidecars, as in scope. If the product explicitly supports fresh 1.12.2 worlds only, A-023-F01 is conditional and should be excluded or demoted.
- Read/write permissions: Product files and central ledger files read-only; only this report writable.
- Stop conditions: Compare persisted fields and lifecycle paths; distinguish verified defects from adaptations and test debt; do not infer parity from compile success.

## Coverage Performed

- Port capability surfaces: `src/main/java/thaumcraft/common/lib/capabilities/PlayerKnowledgeCapability.java`, `PlayerKnowledgeProvider.java`, and `IPlayerKnowledge.java`.
- Port knowledge and lifecycle surfaces: `src/main/java/thaumcraft/common/lib/research/PlayerKnowledge.java`, `ResearchManager.java`, `ResearchNoteData.java`, `src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java`, and relevant research/aspect synchronization handlers and callers.
- TC4 oracle classes were decompiled from `Thaumcraft-1.7.10-4.2.3.5.jar` with CFR, including `PlayerKnowledge`, `ResearchManager`, `EventHandlerEntity`, `ResearchNoteData`, `WarpEvents`, `CommandThaumcraft`, `PacketSyncResearch`, `PacketSyncAspects`, `AspectList`, and `ItemResource`.
- Forge lifecycle ordering was checked with `javap` against `SaveHandler`, `PlayerList`, and `EntityPlayerMP` from Forge 1.12.2-14.23.5.2847.
- Existing focused capability and research tests were inspected, including `PlayerKnowledgeCapabilityTest` and research clue/note tests.
- Notes are audited as item NBT, not as `PlayerKnowledge` fields; cross-version inventory-stack conversion is outside the proven persistence path.

## Result Summary

- One critical conditional defect is confirmed for in-place TC4 migration: the port does not import original TC4 player sidecars.
- One high client semantic defect is confirmed: synchronized client capability state is not visible through legacy username-based research/aspect APIs on a dedicated multiplayer client.
- One medium malformed-save invariant defect is confirmed: capability NBT deserialization bypasses warp-category clamps.
- One low-to-medium API behavior defect is confirmed: negative aspect debits create discoveries and consume overdrawn pools unlike TC4.
- Current-format capability round-trip and normal Forge clone/save lifecycle otherwise preserve the audited fields.

## Atomic Findings

### A-023-F01 - TC4 player sidecar progression is not migrated

- Type: defect, conditional on in-place world migration
- Severity: critical
- Confidence: high
- Source/oracle locator: TC4 `EventHandlerEntity.playerLoad` and `ResearchManager.loadPlayerData`; port `EventHandlerEntity.java:670-690`, `ResearchManager.java:879-907`, and `PlayerKnowledgeCapability.java:325-373`.
- Observed: TC4 load handling clears username maps, locates `<playerDirectory>/<username>.thaum`, migrates UUID `.thaum` and pre-1.7 `players/<name>.thaum` locations, falls back to `<name>.thaumback`, and loads `THAUMCRAFT.RESEARCH`, `THAUMCRAFT.ASPECTS`, three scan lists, `Thaumcraft.shielding`, and `Thaumcraft.eldritch` permanent, temporary, sticky, and counter values. When sticky warp is absent, TC4 splits the legacy combined warp value. The port load handler only grants auto-unlocks, while `loadFreshCapabilityFromPlayerData` reads only `ForgeCaps/thaumcraft:player_knowledge` from the current UUID `.dat`.
- Expected: An in-place upgrade should translate existing TC4 sidecar fields into the port capability, preserving research/discovery completion, aspect pools, scans, runic shielding, and Eldritch warp progression.
- Effect: A TC4 player whose progression exists only in `.thaum`/`.thaumback` logs into the port with fresh capability defaults. Research and discovery completion, aspect pools, scanned objects/entities/phenomena, runic shielding, permanent/temporary/sticky warp, and Eldritch warp counter are lost; primal pools may be initialized as if the player were new.
- Reproduction: Upgrade a TC4 world containing a player with research, scan, aspect, and warp sidecar data but no Forge capability tag; log in and inspect the capability. The port has no sidecar import path.
- Assumption: This is a required defect only if the supported migration contract includes existing TC4 player sidecars. Under a fresh-world-only contract, classify it as an unsupported migration feature rather than a product regression.
- Candidate disposition: required if in-place migration is supported; otherwise conditional/documented scope exclusion.
- Test gap: No `.thaum` or `.thaumbak` fixture verifies field translation, legacy combined-warp splitting, backup fallback, or idempotent migration.

### A-023-F02 - Dedicated-client username APIs diverge from synchronized capability state

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: Port `ResearchManager.getResearchData(String)` at `ResearchManager.java:205-225`, `findPlayer` at `:913-926`, cache update at `:231-235`; client handlers `PacketSyncResearch.java:55-65` and `PacketSyncAspects.java:70-77`; GUI consumers `GuiResearchTable.java:81-83` and `GuiResearchRecipe.java:78-80`.
- Observed: String/TC4 API lookups resolve a server player or process-local `playerDataCache` and require a local `MinecraftServer`. Client sync mutates only the client `PlayerKnowledgeCapability`; it does not populate the client-side username cache. Original TC4 packet handling updated the username-backed knowledge maps used by these APIs.
- Expected: After synchronization, legacy username-based research and aspect lookups on the client should observe the same progression as the synchronized capability.
- Effect: On a remote dedicated client, `ResearchManager.isResearchComplete(player.getName(), key)`, `ThaumcraftApiHelper.isResearchComplete`, and related aspect helper paths can return false or empty despite the capability containing the correct state. Research-table upgrade checks can disable completed upgrades, and concealed completed pages can remain hidden. Server authority and saved state remain correct; the defect is client semantic visibility.
- Reproduction: On a dedicated server, complete `RESEARCHER1`, `RESEARCHER2`, or `RESEARCHDUPE`, join with a remote client, compare capability and string-API results, and open the affected research GUI.
- Candidate disposition: required unless all client callers are deliberately migrated away from username-based APIs.
- Test gap: No split-process dedicated client/server lifecycle test checks username API results after research/aspect synchronization.

### A-023-F03 - Capability NBT deserialization bypasses warp-category clamps

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: Setter clamps in `PlayerKnowledgeCapability.java:67-118`; direct assignments in `deserializeNBT` at `:361-365`; TC4 category loading through `PlayerKnowledge` setters; warp consumers `WarpEvents.java:56-68,155-167`.
- Observed: Public setters clamp permanent, temporary, and sticky warp to nonnegative values, but `deserializeNBT` assigns saved warp categories and counter directly. TC4 `ResearchManager.loadPlayerData` loads the three categories through clamping setters; the counter is direct.
- Expected: Capability deserialization should preserve the same nonnegative category invariant as normal mutation and TC4 loading.
- Effect: Corrupted, manually edited, or future-migrated ForgeCaps containing negative warp values retain those values and resave them. Negative categories can reduce total/actual warp and suppress Eldritch thresholds/events.
- Reproduction: Deserialize capability NBT with a negative warp category and inspect its getter; the negative value survives instead of clamping to zero.
- Assumption: Normal port-generated saves do not produce negative categories, so exposure requires malformed input or a faulty migration source.
- Candidate disposition: required defensive invariant.
- Test gap: Existing tests cover setter clamping but not malformed NBT deserialization.

### A-023-F04 - Negative aspect debits differ from TC4 and can create discoveries

- Type: defect
- Severity: low-to-medium
- Confidence: high
- Source/oracle locator: Port `PlayerKnowledgeCapability.addAspectPool` at `PlayerKnowledgeCapability.java:187-195`; TC4 `PlayerKnowledge.addAspectPool` and `AspectList.reduce`.
- Observed: The port creates an aspect entry before applying any delta and clamps a negative result to zero. TC4 only adds new entries for positive changes; a negative change to an unseen/zero aspect does nothing, and `AspectList.reduce` refuses an overdraw rather than consuming the remaining pool.
- Expected: Negative aspect changes should preserve TC4's no-op behavior for unseen/zero aspects and reject overdraw.
- Effect: `addAspectPool(ELDRITCH, -1)` on an unseen aspect records it as discovered at zero. A pool of two debited by five becomes zero rather than remaining two. The zero entry is persisted and can affect discovery/research gates.
- Reproduction: Invoke the capability method with an unseen aspect and a negative delta, then serialize; or debit a two-point pool by five and inspect the result.
- Assumption: Built-in research, combination, and table paths generally precheck discovery and balances, limiting ordinary exposure. Operator negative-aspect commands and addon/common API callers can still reach this behavior.
- Candidate disposition: required API semantic correction.
- Test gap: No test covers unseen negative changes or aspect-pool overdraw.

## Positive Parity and Lifecycle Controls

### A-023-PC01 - Current-format capability round-trip

- Research/clues, aspects and pools, three scan lists, permanent/temporary/sticky warp, warp counter, initialization state, and runic charge are serialized and restored in the current capability format.
- Research save filtering for unknown, auto-unlocked, and redundant `@` clues matches the original behavior.

### A-023-PC02 - Clone, logout, and dimension lifecycle

- Capability attachment covers players and sets the owner. Clone handling copies the complete serialized capability and cache; Forge fires clone while both original and replacement are available.
- Dimension transfer retains the same player capability and target-world join handling triggers full synchronization.
- Forge reads player NBT/capabilities before the load-from-file event and writes them before save-to-file; logout ordering does not require a separate manual capability copy.
- These controls establish normal lifecycle parity, but do not provide legacy sidecar migration.

### A-023-PC03 - Notes are item data, not knowledge capability data

- Port `ResearchNoteData` retains the TC4 note tags `key`, `color`, `complete`, `copies`, `hexgrid`, `hexq`, `hexr`, `type`, and `aspect` with matching semantics.
- Note survival depends on the old inventory `ItemStack` being successfully converted across the world migration; no separate player-knowledge migration path applies to these tags.

### A-023-PC04 - Server authority remains intact for built-in progression

- Built-in completion, spending, and persistence paths remain server-authoritative. Client scan updates can be optimistic for presentation, but server validation controls progression.
- Packet encoding is intentionally not adjudicated here; only its lifecycle consequence, client capability mutation without username-cache mutation, is in scope.

## Unknowns and Test Debt

- No legacy `.thaum`/`.thaumback` migration fixture or runtime upgrade test.
- No dedicated split-process client username-API integration test.
- No malformed-capability NBT test for negative warp categories.
- No aspect underflow or unseen-negative-debit test.
- Clone coverage is static; no runtime death, End return, dimension-transfer, or logout persistence test was run.
- No cross-version inventory/registry-ID migration test proves research-note ItemStack survival.
- No build, unit-test, or runtime smoke command was run because this was a read-only static audit with no product edits.

## Validation Record

- `git status --short` was clean before and after the audit.
- Original classes were inspected with `/usr/local/bin/cfr`.
- Forge lifecycle ordering was inspected with `javap -c -p` against the Forge 1.12.2-14.23.5.2847 classes.
- No files outside this report were changed; no commit was created.
