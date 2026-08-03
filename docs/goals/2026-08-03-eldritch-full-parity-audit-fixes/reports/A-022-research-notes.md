# A-022 Research Notes

## Scope and result

Read-only I03 parity audit of research-note generation, puzzle solving, note NBT and table interactions, and entity-trigger identity normalization relevant to Eldritch research. Compared the port with decompiled Thaumcraft 4.2.3.5 classes from `thaumcraft_src/**` and `Thaumcraft-1.7.10-4.2.3.5.jar`.

Five discrepancies were found. The first two affect puzzle identity/layout; the remaining three affect material cost, copy placement, or note presentation.

## Findings

### A-022.1 Namespace normalization is broader than TC4

- **Severity:** High
- **Confidence:** High
- **Port:** `src/main/java/thaumcraft/common/lib/research/ResearchManager.java`, `createClue` lines 573-580 calls `entityTriggerMatches`; `entityTriggerMatches` lines 818-830 compares expanded forms; `expandEntityTriggerForms` lines 832-850 adds the lowercase input, strips a colon namespace, converts dot syntax to colon syntax, and adds the local path.
- **Exact branch/value:** Trigger `"Thaumcraft.PrimalOrb"` expands to `thaumcraft.primalorb`, `thaumcraft:primalorb`, and `primalorb`. Runtime clue `"thaumcraft:primalorb"` expands to `thaumcraft:primalorb` and `primalorb`. The local-path intersection makes the match succeed. `othermod:primalorb` also expands to `primalorb` and therefore matches.
- **TC4 evidence:** CFR decompilation of `thaumcraft_src/thaumcraft/common/lib/research/ResearchManager.class`, `createClue`: for a `String` clue it loops entity triggers and uses `if (!clue.equals(string)) continue;`. No namespace stripping or local-path fallback exists.
- **Eldritch consequence:** The intended legacy-to-modern match for `ROD_primal_staff` works, but an unrelated registered entity with the same local path can grant `@ROD_primal_staff` after scanning. This is an entity identity error, not a generic discovery-policy issue.
- **Control:** `src/test/java/thaumcraft/common/lib/research/ResearchManagerEntityTriggerMatchTest.java` covers `Thaumcraft.PrimalOrb` -> `thaumcraft:primalorb`, positive vanilla short names, different entities, null, and blank inputs.
- **Gap:** No negative test for `othermod:primalorb` versus `Thaumcraft.PrimalOrb`; no registry-aware canonical identity test.

### A-022.2 Ring start is consumed differently

- **Severity:** Medium
- **Confidence:** High
- **Port:** `src/main/java/thaumcraft/common/lib/utils/HexUtils.java`, `distributeRingRandomly` lines 50-59. It computes `spacing = ring.size() / entries`, samples `int start = random.nextInt(ring.size())`, initializes `float pos = start`, then selects `ring.get(Math.round(pos) % ring.size())`.
- **Exact branch/value:** The sampled `start` changes the first endpoint and rotates all subsequent endpoint positions. `ResearchManager.createNote` invokes this at lines 614-616 with `radius = 1 + Math.min(3, research.getComplexity())` and `research.tags.size()` entries. For Eldritch complexity 1/2/3, the radius is 1/2/3 and blank removal later requests `complexity * 2` blanks at lines 630-666.
- **TC4 evidence:** `javap -classpath thaumcraft_src -c -p thaumcraft.common.lib.utils.HexUtils` shows `Random.nextInt(ring.size())` into a local, followed by `fconst_0; fstore pos`; the loop selects `ring.get(Math.round(pos))` with no use of the sampled local and no modulo. CFR output likewise shows `float pos = 0.0f`.
- **Eldritch consequence:** Every generated Eldritch note has different endpoint coordinates from TC4 for the same RNG state. Because blank-removal validity checks neighboring type-1 endpoints, the later hole layout can also differ, changing exact puzzle topology and difficulty. Endpoint count, radius formula, spacing formula, and the blank-removal connectivity guard otherwise match.
- **Control:** `ResearchManager.createNote` retains the TC4 radius/endpoint/blank formulas; `src/main/java/thaumcraft/common/lib/utils/HexUtils.java` retains the same six-neighbor ring geometry. `src/test/java/thaumcraft/common/lib/research/ResearchNoteDataTest.java` verifies NBT round-trip but not generated coordinates.
- **Gap:** No deterministic seeded TC4-vs-port endpoint or full-grid fixture; no complexity 1/2/3 topology distribution test.

### A-022.3 Note creation consumes paper in the port

- **Severity:** Medium
- **Confidence:** High
- **Port:** `src/main/java/thaumcraft/common/lib/research/ResearchManager.java`, `createResearchNoteForPlayer` lines 378-405. It first checks ink at lines 386-387, checks paper at lines 389-391, creates the note, consumes ink at line 398, then executes `player.inventory.clearMatchingItems(Items.PAPER, -1, 1, null)` at line 399.
- **Exact branch/value:** One paper is removed only after successful note creation; missing paper returns before ink/paper mutation. The path is used by `src/main/java/thaumcraft/common/lib/network/playerdata/PacketPlayerCompleteToServer.java` line 79 for the non-direct (`type == 1`) workflow.
- **TC4 evidence:** CFR decompilation of `ResearchManager.createResearchNoteForPlayer` checks `consumeInkFromPlayer(player, false)` and `player.field_71071_by.func_146026_a(Items.field_151121_aF)`, then consumes ink and creates/adds the note. There is no paper-consumption call.
- **Eldritch consequence:** Primary Eldritch research solved through a note, and secondary Eldritch research when note workflow is selected by difficulty, costs one paper more than TC4.
- **Control:** `src/test/java/thaumcraft/common/lib/network/playerdata/PacketPlayerCompleteRuntimeTest.java` lines 80-96 verifies note creation, no direct completion, paper count zero, and ink damage one. `ResearchClueProgressionStaticGuardTest` verifies the note workflow branch and prerequisite gating.
- **Gap:** Existing test encodes the port behavior instead of comparing TC4 material consumption; no parity assertion that paper remains available after note creation.

### A-022.4 Duplicate note destination differs

- **Severity:** Low
- **Confidence:** High
- **Port:** `src/main/java/thaumcraft/common/tiles/TileResearchTable.java`, `duplicate` lines 236-289. It requires metadata 64 at lines 239-241, checks feather/paper at 250, charges `research.tags.getAmount(aspect) + data.copies` at lines 255-271, increments `data.copies` and updates the table stack at lines 276-278, then copies one note to player inventory or drops it at lines 280-284.
- **Exact branch/value:** The source table note remains one item in slot 1; the newly created `duplicate` has count 1 and is delivered to the player inventory, otherwise dropped. The copy cost is the research tag amount plus the current `copies` count before increment.
- **TC4 evidence:** CFR decompilation of `TileResearchTable.duplicate` charges the same `rr.tags.getAmount(aspect) + this.data.copies`, consumes feather and paper, increments `data.copies`, updates NBT, then executes `++this.contents[1].field_77994_a`. TC4 therefore retains copies in the table slot rather than delivering a separate inventory item.
- **Eldritch consequence:** Repeated copies of completed Eldritch notes use player inventory slots or create world drops in the port; TC4 accumulates them in the table slot. Aspect costs and escalating copy count otherwise match.
- **Control:** `GuiResearchTable` exposes duplication only when `RESEARCHDUPE` and the note is complete; `ContainerResearchTable` routes button 5 to `TileResearchTable.duplicate`; table authority tests cover packet/table routing and placement validation.
- **Gap:** No duplicate runtime test checks destination slot, stack count, full-inventory behavior, or successive copy costs.

### A-022.5 Primary aspect selection uses first tag instead of highest amount

- **Severity:** Low
- **Confidence:** High
- **Port:** `src/main/java/thaumcraft/common/lib/research/ResearchManager.java`, `getResearchPrimaryTag` lines 857-862 returns the first non-null aspect. `createNote` obtains it at line 603 and writes its color at line 610.
- **Exact branch/value:** The helper does not inspect `research.tags.getAmount(aspect)`. It returns the first aspect in `AspectList.getAspects()`, preserving insertion order. `AspectList` is a `LinkedHashMap` (`src/main/java/thaumcraft/api/aspects/AspectList.java:13`), so this is deterministic.
- **TC4 evidence:** CFR decompilation of `thaumcraft_src/thaumcraft/api/research/ResearchItem.class`, `getResearchPrimaryTag`, initializes `highest = 0`, loops `tags.getAspects()`, skips amounts `<= highest`, and assigns the aspect plus amount when a strictly higher value is found. The current port's `ResearchItem.getResearchPrimaryTag` at lines 257-267 already matches this TC4 method, but `ResearchManager` bypasses it.
- **Eldritch consequence:** Note tint/color NBT differs, without changing endpoint generation or puzzle connectivity. With the current Eldritch tag declarations: `OCULUS` first `MIND 3` versus highest `TRAVEL 6`/`ELDRITCH 6` (TC4 retains the first maximum, `TRAVEL`); `VOIDMETAL` first `METAL 3` versus highest `VOID 5`; `ESSENTIARESERVOIR` first `WATER 5` versus merged `VOID 8`; `ROD_primal_staff` first `AIR 9` versus highest `MAGIC 12`.
- **Control:** `src/test/java/thaumcraft/common/lib/research/ResearchNoteDataTest.java` verifies color NBT round-trip and hex serialization. `ResearchItem.getResearchPrimaryTag` itself preserves the correct highest-amount algorithm.
- **Gap:** No test selects a multi-aspect research item and asserts the TC4 primary color; no Eldritch-specific expected color fixture.

## Parity controls confirmed

- `ResearchNoteData` fields and NBT schema match TC4: `key`, `color`, `complete`, `copies`, and `hexgrid` entries `hexq`, `hexr`, `type`, `aspect` (`ResearchManager.java:688-745`; TC4 CFR `getData`/`updateData`).
- Puzzle completion matches TC4 in `ResearchManager.checkResearchCompletion` lines 748-783 and `checkConnections` lines 786-809: type-1 anchors form `main`; DFS traverses six neighbors; both aspects must be discovered; adjacent aspects must be a component relationship in either direction; disconnected non-anchor cells are removed; `complete` is set and NBT updated.
- Complexity/radius and blank counts match TC4: radius `1 + min(3, complexity)`; blanks `complexity * 2`.
- `TileResearchTable.placeAspect` retains table-ink validation, discovered-aspect validation, pool/bonus consumption, researcher refund probabilities (`RESEARCHER1` 25%, `RESEARCHER2` 50%, `RESEARCHER2` 10% placement refund), type transitions, completion metadata 64, and block update signaling.
- `ItemResearchNotes` retains successful completed-note consumption, prerequisite rejection, sibling unlocking, unknown discovery metadata 24/42, failed discovery fragment count `7 + world.rand.nextInt(3)`, and note metadata threshold 64.
- `PacketPlayerCompleteToServer` retains difficulty routing: direct purchase when tags exist and `Config.researchDifficulty == -1` or `Config.researchDifficulty == 0 && research.isSecondary()`, otherwise note creation; it also retains dimension, username, key, research, and prerequisite checks.
- Eldritch research declarations and relevant values match decompiled TC4, including `ROD_primal_staff` trigger `"Thaumcraft.PrimalOrb"`, complexity 3, and tags AIR/EARTH/FIRE/WATER/ORDER/ENTROPY/TOOL 9 and MAGIC 12 (`src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java:313-347`).

## Audit commands and validation

- `git status --short` before audit and after audit: clean before code work; no product files were changed.
- Targeted `glob`, `grep`, and `read` inspections of the current research, table, scan, packet, Eldritch configuration, and test paths.
- `javap -classpath thaumcraft_src -p` for `ResearchManager`, `ResearchNoteData`, `ItemResearchNotes`, `TileResearchTable`, `ContainerResearchTable`, `ScanManager`, `ConfigResearch`, `ResearchItem`, `AspectList`, and `HexUtils`.
- CFR method decompilation of TC4 `ResearchManager` (`createResearchNoteForPlayer`, `createNote`, `checkResearchCompletion`, `checkConnections`, `getData`, `updateData`, `createClue`, `findMatchingResearch`, `getResearchSlot`, `consumeInkFromPlayer`, `consumeInkFromTable`), `ItemResearchNotes`, `TileResearchTable`, `ScanManager`, `ConfigResearch.initEldritchResearch`, `PacketPlayerCompleteToServer`, `GuiResearchBrowser`, and `ResearchItem`.
- `javap -classpath thaumcraft_src -c -p thaumcraft.common.lib.utils.HexUtils` to verify the ring-start bytecode.
- No tests, build, or runtime smoke were run: this was a read-only source/decompilation audit and no product code was edited.
- No commit was created. This report is the only requested file addition; no product or central-ledger edits were made.

## Remaining test plan

- Add a namespace-collision test proving `Thaumcraft.PrimalOrb` does not match `othermod:primalorb` while still matching `thaumcraft:primalorb`.
- Add seeded TC4/port fixtures for ring endpoint order and complete complexity 1/2/3 grids.
- Add note-material assertions that distinguish TC4 paper retention from current paper consumption.
- Add completed-note duplication tests for table destination, count, full inventory, and escalating Eldritch copy cost.
- Add primary-color fixtures for all multi-aspect Eldritch notes, including duplicate-aspect merge behavior.
