# Normalization Evidence: A-021 through A-025

Goal-ID: `goal-20260803-eldritch-full-parity-audit-fixes`
Promotion policy: `confirmed_in_scope` (S-002; confirmed in-charter defects are recommended `required` unless explicitly deferred, duplicated, or a preserve/control item).
Oracle: Thaumcraft 4.2.3.5 (`Thaumcraft-1.7.10-4.2.3.5.jar`, `thaumcraft_src/**`) -> Forge 1.12.2 port. TC6 is not a gameplay oracle.
Scope: research gating/discovery, note generation/solving/table interactions, knowledge lifecycle/persistence, research networking, and Eldritch browser behavior.

This file is evidence only. It does not alter `RECON.md`, `GOAL.md`, `SOURCES.md`, reports, product files, or the central ledger.

## Disposition Rules

- `required`: confirmed in-scope defect or approved hardening/security issue; map to an R-group for later contract synthesis.
- `preserve`: verified TC4 parity or an intentional Forge adaptation that must not regress.
- `deferred`: candidate depends on the separate legacy-world support decision.
- `duplicate`: the report is retained, but its atomic claim is linked to the first normalized entry rather than counted twice.
- `test_debt`: material missing evidence. It is not independently a product requirement unless needed to prove a required finding.
- `benign_delta`: architecture/platform change with no observed parity defect.

Severity uses the reports' levels. `critical`, `high`, `medium`, `low-medium`, and `low` are retained where supplied; security findings inherited from TC4 remain `required` despite parity.

## Atomic Inventory

### N-001 / A-021-F01: Entity trigger namespace collision

- Type: `defect`; recommended disposition: `required`; severity: `P2`; confidence: high.
- Sources: S-003/S-004/S-005; A-021 report `A-021-F01`, and duplicate confirmation A-022 `A-022.1`.
- Oracle and direction: TC4 `ResearchManager.createClue`, CFR lines 96-100, uses exact `clue.equals(string)`; port `ResearchManager.java:818-850`, called by `createClue` at `:553-598`.
- Observed: `entityTriggerMatches` lowercases and expands legacy dotted, namespaced, and path-only forms. `Thaumcraft.PrimalOrb` correctly maps to `thaumcraft:primalorb`, but path-only equality also makes it match `othermod:primalorb`.
- Expected: Preserve the `Thaumcraft.PrimalOrb` -> `thaumcraft:primalorb` Forge adaptation without erasing namespace identity; an unrelated namespace must not match.
- Exact delta/repro: With `ROD_primal_staff` registered and its full key and `@ROD_primal_staff` clue absent, `ResearchManager.createClue(world, player, "othermod:primalorb", null)` can grant `@ROD_primal_staff`; TC4 cannot. Full completion still requires `FOCUSPRIMAL` and eight hidden rod parents, so this is premature clue discovery, not direct completion.
- Affected behavior: Eldritch `ROD_primal_staff`, entity trigger `Thaumcraft.PrimalOrb`; `expandEntityTriggerForms` lines 832-850; `ResearchManagerEntityTriggerMatchTest`.
- Hazard/control: retain case normalization and legacy conversion only when canonical namespace remains equivalent; do not use path-only equality.
- Primary validation: focused command in A-021 report, `./scripts/dev.sh gradle test --tests thaumcraft.common.lib.research.ResearchClueAndNotesRuntimeTest --tests thaumcraft.common.lib.research.ScanProgressionRuntimeTest --tests thaumcraft.common.lib.research.ResearchManagerEntityTriggerMatchTest --tests thaumcraft.common.lib.research.ResearchManagerFindMatchingResearchStaticGuardTest --tests thaumcraft.common.lib.network.playerdata.PacketPlayerCompleteRuntimeTest`, passed, but lacks wrong-namespace same-path and end-to-end `createClue` assertions.
- Duplicate: N-002 is the same semantic defect from A-022 and is retained below as a duplicate report locator.
- R-group: `R-G1` (gating/discovery identity).

### N-002 / A-022.1: Duplicate entity-trigger normalization claim

- Type: `defect`; recommended disposition: `duplicate`; severity: high; confidence: high.
- Sources/report: S-003/S-004/S-005; A-022 `A-022.1`, port `ResearchManager.java:createClue:573-580`, `entityTriggerMatches:818-830`, `expandEntityTriggerForms:832-850`; TC4 `ResearchManager.class` `createClue` exact string branch.
- Exact evidence retained: trigger `Thaumcraft.PrimalOrb` expands to `thaumcraft.primalorb`, `thaumcraft:primalorb`, and `primalorb`; runtime `thaumcraft:primalorb` expands to `thaumcraft:primalorb` and `primalorb`; `othermod:primalorb` also intersects. Existing control covers valid legacy conversion, vanilla short names, mismatches, null, and blank input, but no wrong-namespace/same-path or registry-aware canonical identity assertion.
- Reason: same root cause, affected symbol, expected behavior, and regression hazard as N-001. Preserve the A-022 locator and gap; implementation coverage belongs to N-001 only.
- R-group: `R-G1` via N-001.

### N-003 / A-022.2: Ring start changes note topology

- Type: `defect`; recommended disposition: `required`; severity: medium; confidence: high.
- Sources/report: S-003/S-004/S-005; A-022 `A-022.2`.
- Oracle: TC4 `HexUtils.distributeRingRandomly`, `javap -classpath thaumcraft_src -c -p`; sampled `Random.nextInt(ring.size())` is stored but not used, then `float pos = 0.0f` and `ring.get(Math.round(pos))` is used.
- Observed: port `HexUtils.java:50-59` computes `spacing = ring.size() / entries`, samples `int start = random.nextInt(ring.size())`, initializes `float pos = start`, and selects `ring.get(Math.round(pos) % ring.size())`.
- Expected/exact delta: The sampled start must not rotate the TC4 endpoint sequence. Port start rotation changes every endpoint for the same RNG state. `ResearchManager.createNote` calls it at `:614-616`; radius is `1 + Math.min(3, research.getComplexity())`, and later blank removal requests `complexity * 2` blanks at `:630-666`.
- Eldritch impact: complexity 1/2/3 notes use radius 1/2/3; endpoint coordinates and potentially hole layout/connectivity differ, changing exact puzzle topology and difficulty. Endpoint count, radius, spacing, blank-removal validity, and six-neighbor geometry otherwise match.
- Controls: `ResearchManager.createNote` formulas and `HexUtils` ring geometry are preserved; `ResearchNoteDataTest` only verifies NBT round-trip, not coordinates.
- Test debt linked: N-017.
- R-group: `R-G2` (note generation/solving parity).

### N-004 / A-022.3: Note creation consumes paper

- Type: `defect`; recommended disposition: `required`; severity: medium; confidence: high.
- Sources/report: S-003/S-004/S-005; A-022 `A-022.3`.
- Oracle/branch: TC4 `ResearchManager.createResearchNoteForPlayer` checks `consumeInkFromPlayer(player, false)` and paper availability, then consumes ink and creates/adds the note; no paper-consumption call.
- Observed: port `ResearchManager.java:378-405` checks ink `:386-387`, paper `:389-391`, creates the note, consumes ink `:398`, then removes one paper with `player.inventory.clearMatchingItems(Items.PAPER, -1, 1, null)` at `:399`. Missing paper returns before mutation.
- Expected/exact delta: Successful note creation consumes ink but leaves paper available, as TC4 does. The port consumes exactly one paper after successful creation.
- Affected path: `PacketPlayerCompleteToServer.java:79`, non-direct workflow `type == 1`; primary and difficulty-selected secondary Eldritch research costs one extra paper.
- Control/hazard: preserve ink checks, note creation, and the note-vs-direct branch; do not regress missing-resource atomicity. Existing `PacketPlayerCompleteRuntimeTest:80-96` asserts the port's paper count zero and therefore is not a TC4 parity proof.
- Test debt linked: N-018.
- R-group: `R-G2`.

### N-005 / A-022.4: Duplicate note destination differs

- Type: `defect`; recommended disposition: `required`; severity: low; confidence: high.
- Sources/report: S-003/S-004/S-005; A-022 `A-022.4`.
- Oracle/branch: TC4 `TileResearchTable.duplicate` charges `rr.tags.getAmount(aspect) + this.data.copies`, consumes feather/paper, increments `data.copies`, updates NBT, then executes `++this.contents[1].field_77994_a`.
- Observed: port `TileResearchTable.duplicate` at `:236-289` requires metadata 64 (`:239-241`), checks feather/paper (`:250`), charges `research.tags.getAmount(aspect) + data.copies` (`:255-271`), increments `data.copies` and table state (`:276-278`), then creates a count-1 duplicate for player inventory or drops it (`:280-284`).
- Expected/exact delta: Retain the source table note in slot 1 and increment its stack count; do not deliver the new copy to inventory/drop. Cost and pre-increment copy escalation are otherwise equal.
- Eldritch impact: repeated completed-note copies use player slots or world drops rather than accumulating in the table slot.
- Controls/hazards: `GuiResearchTable` only exposes duplication with `RESEARCHDUPE` and complete note; `ContainerResearchTable` button 5 routes to the tile. Preserve aspect cost, feather/paper consumption, metadata 64, and copy counter.
- Test debt linked: N-019.
- R-group: `R-G2`.

### N-006 / A-022.5: Primary note color selects first tag

- Type: `defect`; recommended disposition: `required`; severity: low; confidence: high.
- Sources/report: S-003/S-004/S-005; A-022 `A-022.5`.
- Oracle: TC4 `ResearchItem.getResearchPrimaryTag` initializes `highest = 0`, skips amounts `<= highest`, and selects the first strictly higher amount.
- Observed: `ResearchManager.getResearchPrimaryTag` at `:857-862` returns the first non-null aspect and does not inspect `research.tags.getAmount(aspect)`. `AspectList` is a `LinkedHashMap` (`AspectList.java:13`), so insertion order is deterministic. `createNote` reads it at `:603` and writes color at `:610`.
- Expected/exact delta: Use the highest-amount aspect, retaining first-in-order tie behavior. The port's `ResearchItem.getResearchPrimaryTag` at `:257-267` already implements this but `ResearchManager` bypasses it.
- Eldritch examples: `OCULUS` first `MIND 3` vs highest `TRAVEL 6`/`ELDRITCH 6` (TC4 chooses first maximum `TRAVEL`); `VOIDMETAL` first `METAL 3` vs `VOID 5`; `ESSENTIARESERVOIR` first `WATER 5` vs merged `VOID 8`; `ROD_primal_staff` first `AIR 9` vs `MAGIC 12`.
- Controls: color NBT round-trip and hex serialization in `ResearchNoteDataTest`; preserve tag order and duplicate-aspect merge behavior.
- Test debt linked: N-020.
- R-group: `R-G2`.

### N-007 / A-023-F01: TC4 sidecar progression is not migrated

- Type: `defect` (conditional migration); recommended disposition: `deferred`; severity: critical; confidence: high.
- Sources/report: S-003/S-004/S-005; A-023 `A-023-F01`; migration assumption at report lines 14 and 47.
- Oracle/lifecycle: TC4 `EventHandlerEntity.playerLoad` and `ResearchManager.loadPlayerData`; locates `<playerDirectory>/<username>.thaum`, UUID `.thaum`, pre-1.7 `players/<name>.thaum`, and fallback `<name>.thaumback`; loads `THAUMCRAFT.RESEARCH`, `THAUMCRAFT.ASPECTS`, three scan lists, `Thaumcraft.shielding`, and `Thaumcraft.eldritch` permanent/temporary/sticky/counter, splitting legacy combined warp when sticky is absent.
- Observed: port `EventHandlerEntity.java:670-690` only grants auto-unlocks; `ResearchManager.java:879-907` and `PlayerKnowledgeCapability.java:325-373` read only `ForgeCaps/thaumcraft:player_knowledge` from the current UUID `.dat`.
- Expected if supported: in-place upgrade translates research/discovery completion, aspect pools, scans, runic shielding, and Eldritch warp progression from `.thaum`/`.thaumback` into capability state, idempotently and with legacy combined-warp splitting.
- Effect/repro: a TC4 player with only sidecar progression logs in with fresh defaults; all listed progression, scans, shielding, warp, counter, and possible primal pools are lost. Reproduce by upgrading such a world and inspecting capability after login.
- Reason deferred: user-approved plan explicitly leaves `.thaum` migration pending a separate support decision. If fresh 1.12.2 worlds only are supported, this is an unsupported migration feature/scope exclusion, not a product regression.
- Test debt linked: N-021, but no migration implementation is promoted by this evidence.
- R-group: `R-G4` (migration decision, deferred).

### N-008 / A-023-F02 and A-025-F01: Dedicated-client username/cache visibility divergence

- Type: `defect`; recommended disposition: `required`; severity: high; confidence: high.
- Sources/report: S-003/S-004/S-005; A-023 `A-023-F02`; duplicate A-025 `A-025-F01`.
- Oracle/lifecycle: TC4 `PacketSyncResearch` updates client research manager before browser map population; TC4 `ResearchManager` reads synchronized client state (CFR `PacketSyncResearch:65-68`, `ResearchManager:389-401`).
- Observed: port `ResearchManager.getResearchData(String)` at `:205-225` and `findPlayer` at `:913-926` require local `MinecraftServer` or process-local `playerDataCache`, updated at `:231-235`. `PacketSyncResearch.java:55-65` and `PacketSyncAspects.java:70-77` mutate client capability only. `GuiResearchBrowser.java:246,482,695` gates Eldritch with `isResearchComplete(this.player, "ELDRITCHMINOR")`; `GuiResearchTable.java:81-83` and `GuiResearchRecipe.java:78-80` also consume affected username APIs. `PacketResearchComplete.java:41-48` updates capability, not cache.
- Expected/exact delta: After sync, username-based research/aspect APIs and browser category gating must observe synchronized state, as TC4 does.
- Effect/repro: on a remote dedicated client, `ResearchManager.isResearchComplete(player.getName(), key)` and `ThaumcraftApiHelper.isResearchComplete` can return false/empty despite capability state; completed upgrades can disable and Eldritch tab can remain invisible after pre-grant or while browser is closed/open. Server authority and saved state remain correct.
- Controls/hazards: preserve capability synchronization and server authority; avoid making client sync mutate server state. Candidate fixes must cover research and aspect lookup/cache semantics consistently.
- Manual validation: dedicated client/server absent, pre-granted, and live-grant `ELDRITCHMINOR` cases, including browser open/closed and reconnect. No automated split-process GUI test exists.
- Duplicate: A-025-F01 is the same client state/cache root cause and is retained, not counted separately.
- Test debt linked: N-022.
- R-group: `R-G3` (client sync/browser state).

### N-009 / A-023-F03: Capability deserialization bypasses warp clamps

- Type: `defect`; recommended disposition: `required`; severity: medium; confidence: high.
- Sources/report: S-003/S-004/S-005; A-023 `A-023-F03`.
- Oracle/branches: port public setters `PlayerKnowledgeCapability.java:67-118` clamp permanent, temporary, and sticky warp nonnegative; `deserializeNBT` directly assigns at `:361-365`. TC4 `ResearchManager.loadPlayerData` uses clamping setters; counter is direct. Consumers: `WarpEvents.java:56-68,155-167`.
- Expected/exact delta: deserialization must enforce nonnegative category values just like normal mutation and TC4 loading; only the warp counter may retain direct assignment semantics.
- Effect/repro: malformed, manually edited, or future-migrated `ForgeCaps` with negative categories retain and resave negatives, reducing total/actual warp and suppressing Eldritch thresholds/events. Normal generated saves do not produce negatives; malformed input or faulty migration is required.
- Hazard: preserve counter behavior and all valid category values.
- Test debt linked: N-023.
- R-group: `R-G4` (knowledge invariant).

### N-010 / A-023-F04: Negative aspect debits differ from TC4

- Type: `defect`; recommended disposition: `required`; severity: low-medium; confidence: high.
- Sources/report: S-003/S-004/S-005; A-023 `A-023-F04`.
- Oracle: TC4 `PlayerKnowledge.addAspectPool` only adds new entries for positive changes; `AspectList.reduce` refuses overdraw.
- Observed: port `PlayerKnowledgeCapability.addAspectPool` at `:187-195` creates an aspect entry before applying any delta and clamps a negative result to zero.
- Expected/exact delta: `addAspectPool(ELDRITCH, -1)` on unseen/zero must be a no-op with no discovered zero entry; debiting a two-point pool by five must leave two rather than consume to zero. Overdraw must be rejected, not clamped after mutation.
- Effect/repro: unseen negative debit persists a zero entry and can affect discovery/research gates; addon/common API callers and operator negative-aspect commands can overdraw, although built-in paths generally precheck.
- Hazard/control: preserve positive pool additions, table/combination balance checks, and server authority.
- Test debt linked: N-024.
- R-group: `R-G4`.

### N-011 / A-024-F01: Fixed research-table endpoints can be forged into ordinary cells

- Type: `defect` and `security`; recommended disposition: `required`; severity: high; confidence: high.
- Sources/report: S-003/S-004/S-005; A-024 `A-024-F01`.
- Port branches: `PacketAspectPlaceToServer.java:80-101` authenticates `MessageContext` sender, player id, dimension, active `ContainerResearchTable`, matching live tile, and distance. `TileResearchTable.java:319-327` accepts supplied coordinate without current-cell-type validation; placement `:354-372` can replace type-1 fixed endpoint with type-0 when supplied aspect is `null`.
- Expected/exact delta: authenticated packets must not mutate fixed endpoint cells; endpoint minimum/invariant must remain server-enforced. GUI conventions (`GuiResearchTable.java:160-165` type-0 aspect, `:686-693` type-2 null erase) are not authorization.
- Effect/repro: forge authenticated `PacketAspectPlaceToServer` null requests for every endpoint except one; `ResearchManager.java:755-781` collects type-1 endpoints, traverses remaining cells, removes non-endpoint remains, and completes when `main.size()==0`. Completion can occur without valid path/aspect cost, though normal ink consumption remains; unlocks, warp, callbacks, and sync follow.
- TC4 relation: CFR shows TC4 also unconditionally replaced the coordinate. This is inherited TC4 behavior and is in scope for hardening, not an acceptable parity control.
- Test debt linked: N-025.
- R-group: `R-G5` (server authority/security).

### N-012 / A-024-F02: Completion packet omits hidden/lost clue visibility

- Type: `defect` and `security`; recommended disposition: `required`; severity: high; confidence: high.
- Sources/report: S-003/S-004/S-005; A-024 `A-024-F02`.
- Port branch: `PacketPlayerCompleteToServer.java:63-80` checks nonempty/existing key, sender/name, dimension, duplicate completion, and `ResearchManager.doesPlayerHaveRequisites`; `ResearchManager.java:363-375` checks ordinary and hidden parents but not `@KEY` clue ownership. Browser visibility `GuiResearchBrowser.java:392-395` is stricter.
- Expected/exact delta: Server direct purchase and note creation/completion must require the corresponding clue for hidden/lost research, not rely on client UI visibility.
- Eldritch repro/exact values: `ConfigResearchEldritch.java:93-114` defines lost/secondary `PRIMPEARL` parent `ELDRITCHMINOR`; with parent complete and no `@PRIMPEARL`, forge `PacketPlayerCompleteToServer("PRIMPEARL", own username, current dimension, type 0)`. Default difficulty is zero (`Config.java:121,278`), so configured primal costs are deducted and research granted. `OUTERREV` is analogous after `ENTEROUTER`; on hard difficulty, type 1 can create and solve a hidden/lost note without clue.
- Effect: bypasses Eldritch scan/trigger progression and dependent unlocks; warp, siblings, callbacks, and sync execute normally.
- TC4 relation: CFR packet has the same omission. It is inherited vulnerability, not safe parity; approved hardening includes it.
- Test debt linked: N-026.
- R-group: `R-G5`.

### N-013 / A-024-F03: Repeated note requests replay learn sound

- Type: `defect`; recommended disposition: `required`; severity: low; confidence: high.
- Sources/report: S-003/S-004/S-005; A-024 `A-024-F03`.
- Observed branch: `ResearchManager.java:382-385` returns an existing matching incomplete note without consuming resources; `PacketPlayerCompleteToServer.java:78-80` treats it as successful type-1 completion; `:55-58` broadcasts `TCSounds.LEARN` for every successful request.
- Expected/exact delta: Decide and enforce idempotent replay feedback or explicitly bound the success event; repeated valid requests must not create/charge additional notes. Report recommends deduplicating success feedback if abuse matters.
- Effect/repro: repeatedly send valid type-1 completion with existing incomplete note; each can replay sound/event, causing spam/misleading feedback, but no duplicate research or note multiplication was established.
- TC4 relation: same narrow behavior including sound, but inherited replay behavior remains observable and is not silently accepted under hardening.
- Test debt linked: N-027.
- R-group: `R-G5`.

### N-014 / A-024-F04: Sync research count is unbounded/malformed

- Type: `defect` and `security/robustness`; recommended disposition: `required`; severity: low; confidence high for clearing, medium for disconnect consequence.
- Sources/report: S-003/S-004/S-005; A-024 `A-024-F04`.
- Port branch: `PacketSyncResearch.java:28-33` reads raw signed `int` count and loops without sanity bound/readable-bytes validation. Negative count produces empty set; client handler `:57-64` clears research before adding keys; truncated positive count throws during decode/handling.
- Expected/exact delta: validate bounded count and readable payload before clearing client state; reject malformed packets atomically. Preserve valid full-sync behavior.
- Exact comparison: TC4 used signed `short` count, naturally bounding positive range. Port signed-int expansion increases malformed allocation/loop surface without required architecture benefit.
- Effect/repro: negative count transiently clears client research view; oversized/truncated payload can abort processing or disconnect depending on Forge decoder. Client-only, no direct server authority mutation.
- Control: `ByteBufUtils.readUTF8String` bounds individual UTF-8 strings through two-byte VarInt; preserve valid key encoding and full sync.
- Test debt linked: N-028.
- R-group: `R-G3` and `R-G5` (client sync plus protocol robustness).

### N-015 / A-025-F02: Tooltip layout dimensions diverge

- Type: `defect`; recommended disposition: `required`; severity: high; confidence: high.
- Sources/report: S-003/S-004/S-005; A-025 `A-025-F02`.
- Observed: `GuiResearchBrowser.java:561-576` adds primary, secondary, and warp increments directly to `tooltipHeight`, then uses enlarged value for description and rows `:579-633`; widths use `/2` at `:560,564,568,638`.
- Expected/exact delta: TC4 keeps base text height separate from extra-height accumulator and positions rows at CFR `GuiResearchBrowser:548-567,569-628`; width divisions are `/1.9`, with missing-tooltip layout `/1.5`. Port uses accumulated extra height in later vertical layout and `/2` widths.
- Effect: primary/secondary/warp tooltips displace descriptions/cost rows, narrow wrapping, or undersize backing rectangles; secondary aspect cost displacement is 29 px, primary and warp rows add 9 px each.
- Manual validation: incomplete primary-cost research, `PRIMNODE` with enough/insufficient aspects, completed warped node; compare row order, wrapping, backing.
- Test debt linked: N-029.
- R-group: `R-G6` (browser rendering).

### N-016 / A-025-F03: Forbidden/warp aura quad and UV differ

- Type: `defect`; recommended disposition: `required`; severity: medium; confidence: high.
- Sources/report: S-003/S-004/S-005; A-025 `A-025-F03`.
- Observed: `GuiResearchBrowser.java:821-835` draws 16x16 and samples V `5/8..6/8`.
- Expected/exact delta: TC4 CFR `GuiResearchBrowser:802-812` calls `UtilsFX.renderAnimatedQuadStrip(80, 0.66, 32, 5, frame, ..., 0x440055)`; `UtilsFX:256-270` divides frame by 32 on both axes and renders centered 80 px strip. Port must restore 80 px centered animated strip, size `0.66`, frame/UV divisor 32, strip parameter 5, tint `0x440055`.
- Effect/manual validation: warped/forbidden Eldritch nodes show small/wrongly sampled aura. Observe `OCULUS`, `PRIMNODE`, `CAP_void`, `FOCUSPRIMAL`, `ROD_primal_staff` over animation frames.
- Test debt linked: N-030.
- R-group: `R-G6`.

### N-017 / A-025-F04: Locked item icons are not dimmed

- Type: `defect`; recommended disposition: `required`; severity: medium; confidence: high.
- Sources/report: S-003/S-004/S-005; A-025 `A-025-F04`.
- Observed: `GuiResearchBrowser.java:426-428` sets GL color for locked rendering but does not control `RenderItem.renderWithColor`; local 1.12 `RenderItem.renderItemModelIntoGUI` resets GL color to white (CFR `:333-345`).
- Expected/exact delta: TC4 `GuiResearchBrowser:417-421` disables item render color while drawing locked item and restores it `:461-463`; port must use equivalent explicit item-render dimming.
- Effect: locked item-backed Eldritch nodes can appear bright/full color. Representative `VOIDMETAL`, `ADVALCHEMYFURNACE`, `ESSENTIARESERVOIR`.
- Manual validation: compare locked item icon luminance with resource-backed locked and completed nodes. No live item-render smoke was run.
- Test debt linked: N-031.
- R-group: `R-G6`.

### N-018 / A-025-F05: Completion highlight binds vanilla particle sheet

- Type: `defect`; recommended disposition: `required`; severity: low; confidence: high.
- Sources/report: S-003/S-004/S-005; A-025 `A-025-F05`.
- Observed: `GuiResearchBrowser.java:51` defines `new ResourceLocation("textures/particle/particles.png")`, used at `:435` and `:505`.
- Expected/exact delta: TC4 uses `ParticleEngine.particleTexture`, resolving to `thaumcraft:textures/misc/particles.png`. Port must bind the Thaumcraft sheet. Port asset is 256x256 SHA-256 `1fb548c3bc2bb99e7a1472c32814560cc0d637c9ba59ebb20314a251a37ae9b3`; vanilla sheet is 128x128 SHA-256 `c66a9868209b3e3b47782628b1158f69ddbce727e02a6248d9cecde6e0f4935a`.
- Effect/manual validation: completed-node/tab sparkle uses wrong artwork/atlas coordinates. No live visual smoke.
- R-group: `R-G6`.

## Preserve, Architecture, and Constraint Inventory

### N-019: Research gating/discovery parity controls

- Type: `parity`; recommended disposition: `preserve`; severity: n/a; confidence: high; source A-021 parity matrix and A-021 trigger/parent parity.
- Preserve exact behavior: `doesPlayerHaveRequisites` requires every `parents` and `parentsHidden` entry complete (`ResearchManager.java:346-375`); clue creation is not prerequisite-gated; hidden discovery requires hidden/tagged/incomplete/satisfied prerequisites and world-time/50 seeded selection; lost research is clue-driven, not hidden-discovery selected; concealed is not excluded from matching eligibility; matching excludes hidden/lost/auto-unlock/virtual/stub and difficulty-excluded secondary; full key versus `@KEY` visibility, item/block stack identity/meta/wildcard/NBT/ore behavior, positive aspect trigger, `@FOCUSPRIMAL` special `r == 42`, completion and sibling caller behavior all match.
- Eldritch declarations preserve: `FOCUSPRIMAL` concealed parent `ELDRITCHMINOR`; `ROD_primal_staff` hidden entity `Thaumcraft.PrimalOrb`, item `focusPrimal`, parent and eight hidden rods; `OUTERREV` lost block metadata 5/10 parent `ENTEROUTER`; `PRIMPEARL` lost item metadata 3 parent `ELDRITCHMINOR`; `PRIMALCRUSHER` concealed parent and hidden `VOIDMETAL`, `ELEMENTALPICK`, `ELEMENTALSHOVEL`.
- Hazard: N-001 is the sole trigger matcher exception; do not broaden fixes into discovery policy changes.
- R-group: `R-G1` preserve control.

### N-020: Note schema, puzzle, table, and workflow parity controls

- Type: `parity`; recommended disposition: `preserve`; severity: n/a; confidence: high; source A-022.
- Preserve exact note NBT: `key`, `color`, `complete`, `copies`, `hexgrid` entries `hexq`, `hexr`, `type`, `aspect` (`ResearchManager.java:688-745`). Preserve completion: type-1 anchors form `main`; DFS six-neighbor traversal; both aspects discovered; adjacent relationship either direction; disconnected non-anchor removal; completion/NBT update (`:748-809`).
- Preserve values/branches: radius `1 + min(3, complexity)`, blanks `complexity * 2`; table ink validation, discovered-aspect validation, pool/bonus consumption, `RESEARCHER1` 25% refund, `RESEARCHER2` 50% refund and 10% placement refund, type transitions, metadata 64, block update; completed-note consumption/prerequisite rejection/sibling unlocking; unknown metadata 24/42; failed discovery fragments `7 + world.rand.nextInt(3)`; difficulty routing direct purchase when tags and `researchDifficulty == -1` or `== 0 && research.isSecondary()`, otherwise note creation; packet dimension/username/key/research/prerequisite checks.
- Hazard: implement N-003-N-006 without changing schema, completion connectivity, costs other than paper parity, or workflow routing.
- R-group: `R-G2` preserve control.

### N-021: Current capability persistence and Forge lifecycle parity

- Type: `parity`; recommended disposition: `preserve`; severity: n/a; confidence: high; source A-023.
- Preserve current-format serialization/restoration of research, aspects/pools, three scan lists, permanent/temporary/sticky warp, warp counter, initialization, runic charge; save filtering of unknown/auto-unlocked/redundant `@` clues.
- Preserve lifecycle: capability attachment and owner; clone copies complete serialized capability and cache while both players exist; dimension transfer retains capability and join handling full-syncs; Forge reads capabilities before load event and writes before save; logout requires no manual copy.
- Boundary: these controls establish normal Forge lifecycle parity but do not cover `.thaum` migration. Notes remain item data with `key`, `color`, `complete`, `copies`, `hexgrid`; cross-version inventory conversion is unproven.
- R-group: `R-G4` preserve control.

### N-022: Server authority, valid packet, and full-sync architecture controls

- Type: `benign_delta`; recommended disposition: `preserve`; severity: n/a; confidence: high; source A-024.
- Preserve platform adaptation: `PacketBase` schedules packet work on server thread; `PacketPlayerCompleteToServer` authenticates `MessageContext` sender rather than trusting payload player; rejects empty/unknown keys, invalid types, duplicate completion, username mismatch, and payload/server dimension mismatch.
- Preserve stronger port behavior: direct completion preflights all positive aspect costs atomically in `consumeResearchCost` (`PacketPlayerCompleteToServer.java:107-134`), preventing negative pools/partial payment; direct type 0 and note type 1 remain distinct; completion and note paths retain siblings, warp, callbacks, cache updates, and `PacketResearchComplete`; note shrink occurs only after successful completion; possession remains tradeable.
- Preserve architecture: packet registration is server/client directional; full sync `EventHandlerEntity.java:291-309` sends wipe, aspects, research, scans, warp on join/clone, replacing TC4 login sequence without exposing server mutation through client sync. Valid UTF-8 names/keys are bounded by `ByteBufUtils.readUTF8String`.
- Non-benign exceptions: N-011/N-012 are inherited TC4 authority defects requiring hardening; N-014 is protocol robustness.
- R-group: `R-G3`/`R-G5` preserve controls.

### N-023: Browser graph and interaction parity controls

- Type: `parity`; recommended disposition: `preserve`; severity: n/a; confidence: high; source A-025.
- Preserve all 16 Eldritch keys, coordinates, complexity, costs, ordinary/hidden parents, flags; hidden/concealed/lost/stub/round/secondary/special assignments; edge filtering, cross-category edge suppression, hidden-parent handling, siblings; extents columns `-5..6`, rows `-3..6`; pan formulas/map-coordinate preservation; `@KEY` reveal and completed/hidden/lost visibility; direct purchase/note workflow, aspect sufficiency, packet types 0/1, popup, completed node to recipe page 0.
- Preserve matching assets: category icon, GUI frame, Eldritch background, node textures and checked asset hashes. Recipe-page internals are outside A-025 scope.
- R-group: `R-G6` preserve control.

### N-024: Benign capability architecture delta

- Type: `benign_delta`; recommended disposition: `preserve`; severity: n/a; confidence: high; source A-023.
- Forge capability architecture replaces TC4 username-backed `PlayerKnowledge` maps for current-format storage and lifecycle. This is an intentional platform adaptation, not itself a parity defect: normal clone/save/load, server authority, and current capability round-trip are confirmed. The client username/cache defect is separately N-008; legacy sidecar import is separately deferred N-007.
- R-group: `R-G4`/`R-G3` preserve control.

## Material Test Debt and Required Evidence

### N-025: Entity-trigger regression fixture

- Type: `test_debt`; recommended disposition: `required` as minimum evidence for N-001; severity: P2; sources A-021 and A-022.
- Add wrong-namespace same-path assertion (`Thaumcraft.PrimalOrb` must reject `othermod:primalorb`), retain positive legacy/Forge match, and add end-to-end Eldritch `createClue` fixture for `ROD_primal_staff`.

### N-026: Seeded note topology fixtures

- Type: `test_debt`; recommended disposition: `required` as minimum evidence for N-003; severity: medium; source A-022.
- Add seeded TC4/port endpoint-order fixtures and complete-grid topology fixtures for complexity 1/2/3, including blank-removal connectivity.

### N-027: Note material-consumption parity fixture

- Type: `test_debt`; recommended disposition: `required` as minimum evidence for N-004; severity: medium; source A-022.
- Distinguish TC4 paper retention from current paper consumption while preserving ink charge and missing-resource branch.

### N-028: Duplicate-note table destination fixture

- Type: `test_debt`; recommended disposition: `required` as minimum evidence for N-005; severity: low; source A-022.
- Check destination slot, source stack count, full-inventory behavior, successive copy cost escalation, and metadata 64/completion gate.

### N-029: Primary-color fixtures

- Type: `test_debt`; recommended disposition: `required` as minimum evidence for N-006; severity: low; source A-022.
- Test multi-aspect Eldritch notes, strict highest selection, first maximum tie behavior, and duplicate-aspect merge (`OCULUS`, `VOIDMETAL`, `ESSENTIARESERVOIR`, `ROD_primal_staff`).

### N-030: `.thaum` migration fixture

- Type: `test_debt`; recommended disposition: `deferred` with N-007; severity: critical conditional; source A-023.
- If support is approved later, cover UUID/name/legacy paths, `.thaumback` fallback, research/aspects/scans/shielding/warp/counter, combined-warp split, idempotence, and note-stack conversion. No migration work is promoted now.

### N-031: Client username-state integration fixture

- Type: `test_debt`; recommended disposition: `required` as minimum evidence for N-008; severity: high; sources A-023/A-025.
- Dedicated split-process client/server test after research and aspect sync; compare capability and string APIs; cover absent, pre-granted, live-granted `ELDRITCHMINOR`, browser open/closed, reconnect, research-table upgrade, and concealed page visibility.

### N-032: Capability malformed-NBT fixture

- Type: `test_debt`; recommended disposition: `required` as minimum evidence for N-009; severity: medium; source A-023.
- Deserialize negative permanent/temporary/sticky warp and assert zero clamp; verify counter direct semantics and valid round-trip.

### N-033: Aspect debit invariant fixture

- Type: `test_debt`; recommended disposition: `required` as minimum evidence for N-010; severity: low-medium; source A-023.
- Cover unseen negative change, zero-entry persistence, overdraw, positive addition, and table/combination callers.

### N-034: Research-table endpoint authority fixture

- Type: `test_debt`; recommended disposition: `required` as minimum evidence for N-011; severity: high; source A-024.
- Forge authenticated null placement/erase requests against fixed endpoints; assert endpoint immutability and minimum endpoint invariant, no invalid completion, and unchanged normal type-0/type-2 behavior.

### N-035: Hidden/lost packet clue fixture

- Type: `test_debt`; recommended disposition: `required` as minimum evidence for N-012; severity: high; source A-024.
- Forge direct type-0 and note type-1 requests for `PRIMPEARL` and `OUTERREV` without `@KEY`; assert rejection, no cost/note/unlock/warp/callback/sync; retain valid clue and ordinary-parent cases.

### N-036: Replay feedback fixture

- Type: `test_debt`; recommended disposition: `required` as minimum evidence for N-013; severity: low; source A-024.
- Repeat type-1 request with an existing incomplete note; assert no duplicate/charge and chosen sound/event idempotence policy.

### N-037: Sync malformed-payload fixtures

- Type: `test_debt`; recommended disposition: `required` as minimum evidence for N-014; severity: low; source A-024.
- Cover negative, zero, bounded valid, oversized, and truncated counts; assert rejection before client-state clearing and valid round-trip. Record actual Forge disconnect behavior rather than assuming it.

### N-038: Browser visual/manual matrix

- Type: `test_debt`; recommended disposition: `required` as minimum evidence for N-015 through N-018; severity: high/medium/low by linked finding; source A-025.
- No automated client smoke was run. Required observations: dedicated-client gate for N-008; tooltip cases and screenshot/layout comparison for N-015; warp aura screenshots for N-016; locked item luminance for N-017; completion highlight artwork/animation for N-018. Build/client visual validation cannot be claimed from source inspection alone.

### N-039: Lifecycle and item migration tests

- Type: `test_debt`; recommended disposition: `deferred` for migration portions and `required` for current lifecycle evidence; severity: medium; source A-023.
- Clone coverage is static; no runtime death, End return, dimension transfer, or logout persistence test was run. No cross-version inventory/registry-ID test proves research-note stack survival. The lifecycle tests may proceed for current Forge behavior; `.thaum` and cross-version item migration remain tied to the separate support decision.

## Duplicate Relationships

| Duplicate | Canonical normalized entry | Relationship |
|---|---|---|
| A-022.1 | N-001 (A-021-F01) | Exact same namespace/path collision, matcher methods, Eldritch trigger, and missing wrong-namespace test. A-022 details are retained in N-002. |
| A-025-F01 | N-008 (A-023-F02) | Same dedicated-client capability-versus-username-cache divergence, with A-025 adding the Eldritch tab gate and manual cases. |

No other A-021 through A-025 claims are merged: note topology, materials, duplication, color, migration, warp clamps, aspect overdraw, endpoint forging, clue bypass, replay, malformed sync, and four browser rendering defects have distinct affected methods and acceptance evidence.

## R-Group Mapping

- `R-G1` Research gating/discovery identity: N-001 required; N-002 duplicate; N-019 preserve.
- `R-G2` Research-note generation/solving/table parity: N-003, N-004, N-005, N-006 required; N-020 preserve; N-026-N-029 proof debt.
- `R-G3` Client synchronization and username/browser state: N-008 required; N-014 required; N-022 and N-024 preserve/architecture controls; N-031 and relevant N-037 proof debt.
- `R-G4` Knowledge invariants and lifecycle/migration: N-007 deferred; N-009 and N-010 required; N-021 and N-024 preserve; N-030/N-039 migration debt deferred where applicable.
- `R-G5` Server authority and protocol hardening: N-011, N-012, N-013, N-014 required; N-022 preserve; N-025 and N-034-N-037 proof debt.
- `R-G6` Eldritch browser rendering: N-015, N-016, N-017, N-018 required; N-023 preserve; N-038 manual/client evidence.

## Completeness Matrix

| Report | Claims inventoried | Required | Preserve/benign | Deferred | Duplicate | Test debt linked | Status |
|---|---:|---:|---:|---:|---:|---:|---|
| A-021 | 1 | 1 | 0 | 0 | 0 | N-025 | complete |
| A-022 | 5 | 4 | 1 | 0 | 1 | N-025-N-029 | complete; A-022.1 duplicate of A-021 |
| A-023 | 4 | 3 | 2 | 1 | 0 | N-030-N-033, N-039 | complete; migration explicitly deferred |
| A-024 | 4 | 4 | 1 | 0 | 0 | N-034-N-037 | complete; inherited security defects retained in scope |
| A-025 | 5 | 4 | 1 | 0 | 1 | N-031, N-038 | complete; F01 duplicate of A-023 |
| **Total report claims** | **19** | **16 distinct required** | **6 preserve/benign controls** | **1 claim** | **2 duplicate report views** | **all material gaps linked** | **lossless inventory complete** |

Counting note: the total preserves every report claim (including both duplicate views). Distinct required defects are N-001, N-003-N-006, and N-008-N-018 (16); N-007 is the single migration claim deferred pending support decision. Preserve/benign entries include N-019-N-024. All reported validation commands, skipped runtime/manual gates, reproduction branches, lifecycle paths, keys, packet fields, affected symbols, and report locators are retained above or in the linked immutable report packets.
