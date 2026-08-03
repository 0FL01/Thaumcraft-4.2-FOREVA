# Audit Packet: A-024 — Eldritch Research Networking

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes  
Assignment-ID: A-024  
Status: complete  
Report-Revision: 1  
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare Eldritch-relevant research completion, research-table, incremental-sync, and full-sync networking workflows in the Forge 1.12.2 port with Thaumcraft 4.2.3.5, emphasizing authority, prerequisites/visibility, difficulty and aspect costs, direct versus note completion, note ownership/consumption, sibling/unlock callbacks, replay behavior, sync direction/timing, and malformed payload resilience.
- Oracle: `Thaumcraft-1.7.10-4.2.3.5.jar`, decompiled with CFR 0.152; port sources under `src/main/java/`.
- Read/write permissions: Product files, original jars, and central Goal Ledger files were read-only. Only this report was writable.
- Result: Four findings are recorded below. Two are high-severity server-authority bugs; two are low-severity protocol robustness/replay issues. Two high findings are inherited from the TC4 workflow, not dismissed as acceptable parity.

## Coverage Performed

Port surfaces inspected:

- `src/main/java/thaumcraft/common/lib/network/playerdata/PacketPlayerCompleteToServer.java`
- `src/main/java/thaumcraft/common/lib/network/playerdata/PacketResearchComplete.java`
- `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncResearch.java`
- `src/main/java/thaumcraft/common/lib/network/playerdata/PacketAspectPlaceToServer.java`
- `src/main/java/thaumcraft/common/lib/network/playerdata/PacketAspectCombinationToServer.java`
- `src/main/java/thaumcraft/common/lib/network/PacketBase.java`
- `src/main/java/thaumcraft/common/lib/network/PacketHandler.java`
- `src/main/java/thaumcraft/common/lib/research/ResearchManager.java`
- `src/main/java/thaumcraft/common/tiles/TileResearchTable.java`
- `src/main/java/thaumcraft/common/items/ItemResearchNotes.java`
- `src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java`
- `src/main/java/thaumcraft/client/gui/GuiResearchTable.java`
- `src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java`
- `src/main/java/thaumcraft/client/ClientProxy.java`
- Eldritch declarations in `ConfigResearchEldritch.java` and defaults in `Config.java`.

TC4 classes decompiled and compared:

- `thaumcraft.common.lib.network.playerdata.PacketPlayerCompleteToServer`
- `thaumcraft.common.lib.network.playerdata.PacketResearchComplete`
- `thaumcraft.common.lib.network.playerdata.PacketSyncResearch`
- `thaumcraft.common.lib.research.ResearchManager`
- `thaumcraft.common.items.ItemResearchNotes`
- `thaumcraft.common.tiles.TileResearchTable`
- `thaumcraft.common.lib.events.EventHandlerNetwork`
- `thaumcraft.common.config.ConfigResearch`

Existing tests inspected include `PacketPlayerCompleteRuntimeTest`, `PlayerDataPacketSerializationTest`, `ResearchTableAuthorityRuntimeTest`, and `ResearchClueProgressionStaticGuardTest`. No product code or central-ledger file was changed.

## Audit Result

### A-024-F01 — Fixed research-table endpoints can be forged into ordinary cells

- Type: exploitable_server_authority_bug
- Severity: high
- Confidence: high
- Port evidence: `PacketAspectPlaceToServer.java:80-101` authenticates the `MessageContext` sender and checks player id, dimension, active `ContainerResearchTable`, matching live tile, and distance. However, `TileResearchTable.java:319-327` accepts the supplied coordinate without validating the current cell type. Its placement path at `:354-372` can replace a type-1 fixed endpoint with a type-0 aspect cell when the supplied aspect is `null`.
- Completion evidence: `ResearchManager.java:755-781` collects type-1 endpoints, traverses the remaining cells, removes non-endpoint remains, and marks the note complete when `main.size()==0`. A forged null request for every endpoint except one therefore leaves the minimum endpoint set and can complete the note without a valid path or aspect placement cost. The table still consumes ink as part of its normal completion path.
- Client/original comparison: `GuiResearchTable.java:160-165` sends aspects only for type-0 cells and `:686-693` sends null for type-2 erasure. These are client conventions, not server authorization. CFR shows TC4 `TileResearchTable.placeAspect` likewise unconditionally replaced the supplied coordinate and ran completion. This is inherited TC4 behavior and remains a security defect in the port; parity does not make it safe.
- Repro: Open a research note at a table, forge authenticated `PacketAspectPlaceToServer` requests targeting fixed endpoint coordinates with a null aspect, repeat while retaining one endpoint, and observe completion without solving the displayed graph.
- Impact: Bypasses the Eldritch research puzzle, discovered-aspect placement, and normal aspect costs; successful completion triggers ordinary research unlocks, warp, callbacks, and client sync, making the forged result appear legitimate.
- Test gap: `ResearchTableAuthorityRuntimeTest` covers routing, invalid hex, undiscovered aspects, pool atomicity, and some placement behavior, but does not assert fixed-endpoint immutability or a minimum endpoint invariant.
- Disposition: high-priority server-side validation fix required; do not rely on GUI cell-type restrictions.

### A-024-F02 — Completion packet does not enforce hidden/lost clue visibility

- Type: exploitable_server_authority_bug
- Severity: high
- Confidence: high
- Port evidence: `PacketPlayerCompleteToServer.java:63-80` checks nonempty/existing key, sender/name and dimension consistency, duplicate completion, and `ResearchManager.doesPlayerHaveRequisites`. `ResearchManager.java:363-375` checks ordinary parents and hidden parents only; it does not require the `@KEY` clue for hidden/lost research.
- Visibility evidence: `GuiResearchBrowser.java:392-395` hides lost/hidden entries unless the player has the corresponding clue. The UI gate is therefore stricter than the server completion endpoint.
- Eldritch repro: `ConfigResearchEldritch.java:93-114` defines `PRIMPEARL` as lost/secondary with `ELDRITCHMINOR` as its parent. With `ELDRITCHMINOR` complete but no `@PRIMPEARL` clue, forge `PacketPlayerCompleteToServer("PRIMPEARL", own username, current dimension, type 0)`. Default difficulty is zero (`Config.java:121,278`), so the secondary is directly purchasable; the server deducts the configured primal costs and grants research. `OUTERREV` is analogous after `ENTEROUTER`. On hard difficulty, type 1 can create a hidden/lost note without the clue and it can then be solved normally.
- Impact: Bypasses Eldritch scan/trigger progression and unlocks dependent research. Warp, sibling processing, callbacks, and sync execute normally after the forged completion.
- TC4 comparison: CFR `PacketPlayerCompleteToServer` also checked only completion and `doesPlayerHaveRequisites`, not hidden/lost clue ownership. The port preserves the original vulnerability; the original behavior is evidence of parity, not a valid authority model.
- Test gap: `ResearchClueProgressionStaticGuardTest` covers clue creation and ordinary prerequisites but not packet-level clue enforcement. `PacketPlayerCompleteRuntimeTest` has no forged hidden/lost completion case.
- Disposition: high-priority server-side visibility/clue validation required, including both direct purchase and note creation/completion paths.

### A-024-F03 — Repeated note requests replay the learn sound without consuming anything

- Type: replay/availability_bug
- Severity: low
- Confidence: high
- Evidence: `ResearchManager.java:382-385` returns an existing matching incomplete note from `createResearchNoteForPlayer` without consuming resources. `PacketPlayerCompleteToServer.java:78-80` treats that as successful type-1 completion, and `:55-58` broadcasts `TCSounds.LEARN` for every successful request.
- Repro: With an existing incomplete note for a research key, repeatedly send a valid type-1 `PacketPlayerCompleteToServer` request. Each request can produce the success sound while creating no additional note and charging no new resources.
- Impact: Low-cost sound/event spam and misleading feedback; no duplicate research or note multiplication was established.
- TC4 comparison: CFR shows the same narrow behavior, including learn-sound behavior when note creation does not produce a new note. The port narrows no gameplay authority here, but inherited replay behavior remains observable.
- Test gap: No test repeats a valid note request with an existing incomplete note or asserts sound/event idempotence.
- Disposition: low priority; deduplicate success feedback or make replay semantics explicit if abuse matters.

### A-024-F04 — `PacketSyncResearch` accepts unbounded/malformed counts

- Type: malformed_payload_robustness_bug
- Severity: low
- Confidence: high for client-state clearing; medium for exact disconnect behavior.
- Evidence: `PacketSyncResearch.java:28-33` reads a raw signed `int` count and loops that many times without a sanity bound or readable-bytes validation. A negative count yields an empty set. The client handler at `:57-64` clears client research before adding the received keys. A truncated positive count throws during decode/handling.
- Impact: A negative count can transiently clear the client research view; a truncated/oversized payload can abort packet processing or disconnect depending on Forge decoder handling. This packet is client-only and cannot directly mutate server research authority.
- TC4 comparison: CFR `PacketSyncResearch` used a signed `short` count and also lacked explicit malformed handling, so positive counts were naturally bounded to the short range. The port's signed-int expansion increases the malformed allocation/loop surface and is not required by the architecture change.
- Test gap: `PlayerDataPacketSerializationTest.java:30-35,165-168` covers valid round trips only; no negative, oversized, zero, or truncated-count cases are covered.
- Disposition: add a bounded count and decode validation before clearing client state; treat malformed packets as rejected rather than partial sync.

## Controls and Positive Parity

- `PacketBase` schedules packet work on the server thread. `PacketPlayerCompleteToServer` uses the authenticated `MessageContext` sender as the authority rather than trusting the payload player.
- `PacketPlayerCompleteToServer` rejects empty/unknown keys, invalid types, duplicate completion, username mismatch, and payload/server dimension mismatch before mutation. These checks are stronger than the TC4 packet's payload-world/player resolution model.
- Direct completion checks difficulty/type eligibility and preflights all positive aspect costs atomically in `consumeResearchCost` (`PacketPlayerCompleteToServer.java:107-134`), preventing negative pools or partial payment. This is stronger than the original direct subtraction path.
- Direct completion and note creation are distinct: type 0 consumes costs and completes; type 1 creates a note only when not directly purchasable. Completion rejects already completed research.
- `ResearchManager.addResearch` and the packet's sibling loop (`PacketPlayerCompleteToServer.java:94-103`) preserve primary completion, eligible sibling unlocks, warp, callbacks, cache updates, and `PacketResearchComplete` notification. `ItemResearchNotes.java:136-145` follows the same sibling behavior.
- `ItemResearchNotes` performs live server-side completion/prerequisite checks and shrinks the held note only after successful completion (`:112-148`). Note ownership is possession-based and tradeable in both TC4 and the port; no owner binding was added, preserving parity.
- `PacketHandler` registers completion requests server-side and research-complete/sync packets client-side. `PacketResearchComplete` is client-bound; `ClientProxy.java:1296-1324` applies incremental completion with null-safe UI handling.
- The port's full-sync architecture is `EventHandlerEntity.java:291-309`, which sends wipe, aspects, research, scans, and warp on entity join/clone. This replaces TC4 `EventHandlerNetwork.playerLoggedIn`, which sent the corresponding login sequence. No server-authority mutation is exposed by the client sync packets.
- Forge `ByteBufUtils.readUTF8String` bounds UTF-8 string length through its two-byte VarInt encoding. Invalid names, keys, dimensions, and types fail without an established mutation path.

## Validation and Limitations

- Commands/evidence: `git status --short` was checked before this report; the audit relied on targeted source inspection and CFR decompilation of the listed TC4 classes.
- Tests/build: not run. This was a read-only audit and no product code changed.
- Runtime smoke: skipped and not required because no common/server product code was modified.
- Limitations: Exploit repros are protocol/source-level reproductions, not live multiplayer captures. The exact client disconnect consequence for truncated `PacketSyncResearch` depends on Forge's network error handling. No claim of visual or runtime parity is made from this audit.
