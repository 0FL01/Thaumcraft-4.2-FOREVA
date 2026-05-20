# BACKEND_NEXT_STEPS.md

## Purpose

This is the backend/common/server-side action roadmap that should guide the next implementation and documentation updates. It is ordered to reduce false completion claims first, then convert existing implementation into validated behavior.

## Immediate

### 1. Make research table packets server-authoritative

**Objective:** Prevent remote/forged client packets from mutating research note state or consuming/freeing aspects incorrectly.

**Files/systems affected:**

- `PacketAspectPlaceToServer`
- `PacketAspectCombinationToServer`
- `TileResearchTable`
- `ContainerResearchTable`
- `ResearchManager`
- `ResearchNoteData`

**Risk:** Critical. Research table solving is core progression and currently accepts too much client-declared state.

**What must be implemented:**

- Require `player.openContainer instanceof ContainerResearchTable`.
- Verify the open container’s tile equals packet coordinates.
- Verify `TileResearchTable.isUsableByPlayer(player)`.
- Reject q/r cells not present in the current note hex grid.
- Verify placed aspect is discovered and available.
- Verify bonus aspects are actually present before treating client `ab1/ab2` flags as usable.
- Consume aspect pool, bonus aspect, and ink atomically after all validation passes.
- Reject null/invalid aspects and malformed note state without mutation.

**Validation expected:**

- Unit or integration test that malicious packets cannot mutate a closed/far/foreign table.
- Test invalid q/r does not create a new note cell.
- Test insufficient aspect pool and missing bonus aspect are rejected.
- Test valid placement still works through an open container.

**Documentation must not claim before completion:**

- “research table runtime contract restored”
- “research/progression server-safe”
- “Stage 9-e complete”
- “backend substantially complete”

### 2. Make scanning server-authoritative

**Objective:** Stop clients from awarding scans/aspects/clues by sending arbitrary item ids, entity ids, or phenomena strings.

**Files/systems affected:**

- `PacketScannedToServer`
- `ItemThaumometer`
- `ScanManager`
- scan event handlers
- player knowledge capability sync packets

**Risk:** Critical/high. Scans feed aspect discovery and hidden/lost progression clues.

**What must be implemented:**

- Verify the player is holding/using a thaumometer or accepted scan item on the server.
- Recompute block/entity raytrace server-side with range and line-of-sight.
- For entity scans, verify entity exists, is targetable, and is in range.
- For item/block scans, derive `ScanResult` from server-observed block/item state, not arbitrary payload.
- For phenomena scans, whitelist server-observable phenomena and reject arbitrary strings.
- Keep `ScanManager.completeScan(...)` as rule engine after authoritative target reconstruction.

**Validation expected:**

- Valid block/entity/node scan succeeds.
- Same packet without held thaumometer fails.
- Arbitrary item id/meta scan fails.
- Far entity id scan fails.
- Unknown phenomena string fails.
- Hidden clue scan grants only `@KEY` and not full research.

**Documentation must not claim before completion:**

- “scan progression complete”
- “hidden/lost research runtime validated”
- “server-authoritative progression”

### 3. Wire and validate normal Thaumonomicon/research action route

**Objective:** Ensure legitimate players can progress through the normal gameplay route, not just through backend helper methods or direct packets.

**Files/systems affected:**

- `ItemThaumonomicon`
- `GuiResearchBrowser`
- `GuiResearchRecipe`
- `GuiResearchTable`
- `PacketPlayerCompleteToServer`
- `ResearchManager.createResearchNoteForPlayer(...)`
- `ItemResearchNotes`

**Risk:** Critical/high. Research data can be present but unusable if the GUI/client route is not wired.

**What must be implemented:**

- Primary research action sends a validated request that creates a note, not direct completion.
- Secondary research action sends a validated request that charges aspect pool.
- Client UI should not be trusted for rule decisions.
- Server should reject invalid key/type/dimension/user/prerequisite/cost cases.

**Validation expected:**

- Fresh player with prerequisites can create a note from Thaumonomicon.
- Player without prerequisites cannot create/complete it.
- Secondary research consumes correct aspect costs.
- Duplicate or forged completion requests are rejected.
- UI route and direct packet tests both exist.

**Documentation must not claim before completion:**

- “Thaumonomicon progression complete”
- “research browser route restored”
- “Stage 9-e accepted”

### 4. Fix or formalize research table `bonusAspects` persistence

**Objective:** Remove ambiguous NBT behavior where amounts are computed but only aspect tags are written.

**Files/systems affected:**

- `TileResearchTable.readCustomNBT(...)`
- `TileResearchTable.writeCustomNBT(...)`
- `AspectList` semantics
- any tests around research table persistence

**Risk:** High/medium. Save/load can alter available bonus aspect state.

**What must be implemented:**

Choose one:

- Persist amount explicitly and round-trip it; or
- enforce/document one-bit-per-aspect semantics and prevent accumulation above one.

**Validation expected:**

- NBT round-trip test for bonus aspects.
- Save/reload manual or integration scenario.

**Documentation must not claim before completion:**

- “research table NBT parity”
- “research table runtime contract validated”

### 5. Reclassify current tests and docs by evidence type

**Objective:** Prevent future progress docs from treating static guard coverage as runtime validation.

**Files/systems affected:**

- `docs/GOAL_PROGRESS.md`
- `docs/Stage9-a.md`
- `docs/Stage9-d.md`
- `docs/Stage9-e.md`
- test names/descriptions where needed

**Risk:** High project-management risk. False closure causes wrong prioritization.

**What must be implemented:**

- Label static/source/corpus tests as `static guarded only`.
- Label actual behavior tests as `runtime/integration`.
- Update Stage docs to distinguish implementation-present vs runtime-validated.

**Validation expected:** documentation review only.

**Documentation must not claim before completion:**

- “guarded” without qualifier;
- “validated” for source-shape tests;
- “complete” without behavior evidence.

## Near-term

### 6. Crucible and thaumatorium runtime burst

**Objective:** Convert existing alchemy/thaumatorium code from “present” to “validated enough to trust.”

**Files/systems affected:**

- `TileCrucible`
- `TileThaumatorium`
- `TileThaumatoriumTop`
- `ContainerThaumatorium`
- `ConfigRecipesCrucibleSlice`
- `ThaumcraftCraftingManager`

**Risk:** High. Alchemy is a core gameplay loop and interacts with research gates, aspects, inventories, essentia, and NBT.

**What must be implemented/validated:**

- Crucible valid craft with research complete.
- Crucible invalid craft without research.
- Catalyst stack consumption and output ejection.
- Water drain and aspect removal.
- Player attribution through thrown items.
- Thaumatorium recipe programming.
- Exact/partial/extra essentia behavior.
- Output blocking behavior.
- Save/load before and after craft.
- Top/bottom inventory and essentia transport.

**Documentation before completion:** “crucible/thaumatorium implementation present; runtime validation open.”

**Documentation after completion:** list exact scenarios and commands, then mark only those behaviors `runtime validated`.

### 7. Infusion runtime burst

**Objective:** Validate infusion matrix, infusion recipes, infusion enchantments, and instability behavior.

**Files/systems affected:**

- `TileInfusionMatrix`
- `TilePedestal`
- `ThaumcraftCraftingManager`
- `ConfigRecipesInfusionSlice`
- `InfusionRecipe`
- `InfusionEnchantmentRecipe`

**Risk:** High. Infusion is complex and balance-sensitive.

**What must be implemented/validated:**

- Matrix structure detection.
- Recipe start with valid center/item/pedestals.
- Ingredient consumption order and symmetry behavior.
- Essentia drain and missing-essentia failure.
- Output production.
- Instability events.
- Enchantment cost scaling against reference.
- Save/reload mid-craft.

**Documentation before completion:** “infusion code present; runtime parity unverified.”

**Documentation after completion:** “representative infusion scenarios runtime validated,” with scenario list.

### 8. Recipe craftability and registry audit burst

**Objective:** Verify that recipe corpus and research-page handles correspond to actual craftable/registered behavior.

**Files/systems affected:**

- `ConfigRecipes`
- `ConfigRecipesSpecialSlice`
- recipe slices
- `ConfigResearch.recipes`
- Forge recipe registry

**Risk:** Medium/high. Corpus and page handles can hide missing craftability.

**What must be implemented/validated:**

- Dump registered recipe ids and compare to expected keys.
- Identify display-only fallback handles.
- Test representative normal, special, NBT, arcane, crucible, and infusion recipes.
- Verify no duplicate registry names.

**Documentation before completion:** “recipe corpus/static guarded; craftability not fully proven.”

**Documentation after completion:** classify recipes by craftable/display-only/deferred.

### 9. API behavior cleanup burst

**Objective:** Fix suspicious API behavior that can affect addons and internal lookups.

**Files/systems affected:**

- `ThaumcraftApi.getCraftingRecipeKey(...)`
- `ThaumcraftApi.registerComplexObjectTag(...)`
- `ThaumcraftApi.addSmeltingBonus(...)`

**Risk:** Medium. API signatures exist, but semantics may be wrong.

**What must be implemented/validated:**

- Replace `int[]` key cache with value-equality key.
- Verify complex object tag merge uses incoming aspect amounts correctly.
- Verify count-zero smelting bonus output is intentional or fix it.
- Compare with original reference before changing public behavior.

**Documentation before completion:** “API signatures present; semantic parity needs verification.”

**Documentation after completion:** record exact reference comparison and tests.

### 10. Dedicated server side-safety/classloading burst

**Objective:** Prove common/API code does not accidentally load client-only classes on a dedicated server.

**Files/systems affected:**

- `thaumcraft.api.*`
- `thaumcraft.common.*`
- proxy classes
- packet/effect classes

**Risk:** Medium. Smoke may catch many issues, but side-only imports in common/API need ongoing care.

**What must be implemented/validated:**

- Dedicated server smoke after common/API changes.
- Static scan for client-only imports in common/API.
- Justify every client import behind `@SideOnly` or proxy boundary.

**Documentation before completion:** “server smoke passed for last checkpoint only; not a permanent side-safety proof.”

## Later

### 11. Outer Lands portal/maze runtime burst

**Objective:** Validate the full Outer Lands backend path.

**Files/systems affected:**

- `WorldProviderOuter`
- `ChunkProviderOuter`
- `ThaumcraftWorldGenerator`
- `WorldGenEldritchRing`
- `TileEldritchAltar`
- `BlockEldritchPortal`
- `TeleporterThaumcraft`
- `MazeHandler`
- room generators

**Risk:** High but can wait until progression blockers are handled unless currently blocking tests.

**What must be implemented/validated:**

- Fresh-world ring generation.
- MazeThread generation and `labyrinth.dat` persistence.
- Portal activation after required conditions.
- Dimension transfer to Outer Lands.
- Safe spawn/destination in void-like terrain.
- Return/portal behavior.
- Save/load race handling.

**Documentation before completion:** “Outer Lands scaffold/hook present; runtime path unvalidated.”

### 12. Stage 6 entity/golem runtime matrix

**Objective:** Validate AI/combat/inventory behavior that static guards cannot prove.

**Files/systems affected:** golems, traveling trunk, pech, cultist portal, bosses, projectiles.

**Risk:** Medium/high. Entity parity is broad and expensive; do after core progression/crafting blockers.

**Validation expected:** dedicated-server manual or automated scenario matrix.

**Documentation before completion:** “entity implementations and guards present; Stage 6 not complete.”

### 13. Broad save/load compatibility burst

**Objective:** Validate persistent state for complex backend systems.

**Files/systems affected:** player knowledge, research notes, research table, crucible, thaumatorium, infusion matrix, focal manipulator, bore, Outer Lands maze.

**Risk:** High. NBT bugs usually evade static guards.

**Validation expected:** save/reload scenario tests for each system.

**Documentation before completion:** “NBT methods exist; save/load behavior not fully validated.”

### 14. GUI/visual parity after backend blockers

**Objective:** Address visuals only where they unblock backend progression or final parity.

**Risk:** Lower for backend. Do not let visual polish hide backend blockers.

**Documentation before completion:** “manual visual parity skipped or pending; backend status tracked separately.”

## What not to do yet

- Do not add more source-shape tests as a substitute for progression/crafting runtime tests.
- Do not mark Stage 9-e complete because 201 research entries exist.
- Do not mark Stage 9-d complete because crucible/thaumatorium classes and recipes exist.
- Do not spend major effort on renderer polish until research/progression C2S authority is fixed.
- Do not change public API signatures without reference comparison and explicit justification.
- Do not silently change packet ids, NBT keys, GUI ids, dimension ids, or registry names.
