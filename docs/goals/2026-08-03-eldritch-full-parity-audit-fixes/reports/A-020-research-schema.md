# Audit Packet: A-020 — Research Schema and Eldritch Metadata

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-020
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare the Forge 1.12.2 port's `thaumcraft.api.research.ResearchItem`, `ResearchPage`, and `ResearchCategories` semantics with the exact Thaumcraft 4.2.3.5 classes, emphasizing every schema construct used by the 16 `ELDRITCH` research declarations. Audit constructors and defaults, research aspect costs, complexity, all flags, parents and hidden parents, item/entity/aspect triggers, warp association, page constructors/types/order, category registration, duplicate handling, coordinate collision behavior, replacement behavior, and category display bounds.
- Declaration corpus: `ELDRITCHMINOR`, `ELDRITCHMAJOR`, `OCULUS`, `ENTEROUTER`, `OUTERREV`, `PRIMPEARL`, `PRIMNODE`, `ADVALCHEMYFURNACE`, `PRIMALCRUSHER`, `VOIDMETAL`, `ESSENTIARESERVOIR`, `CAP_void`, `ARMORVOIDFORTRESS`, `FOCUSPRIMAL`, `SANITYCHECK`, and `ROD_primal_staff`.
- Anti-scope: Research-manager gating and completion logic, GUI and renderer behavior, persistence and networking, recipe body parity, recipe rendering, note-solving behavior, and product changes.
- Oracle and comparison direction: S-003/S-004 exact Thaumcraft 4.2.3.5 jar bytecode, decompiled with CFR 0.152, -> S-005 Forge 1.12.2 port.
- Read/write permissions: Product files and central Goal Ledger files read-only; only this report writable.
- Stop conditions: All three schema classes and all 16 declarations are compared; every semantic delta has both-side evidence; positive parity, adaptations, and test debt are explicit; no product or central-ledger path is changed.

## Coverage Performed

- Port schema surfaces:
  - `src/main/java/thaumcraft/api/research/ResearchItem.java`
  - `src/main/java/thaumcraft/api/research/ResearchPage.java`
  - `src/main/java/thaumcraft/api/research/ResearchCategories.java`
  - `src/main/java/thaumcraft/api/research/ResearchCategoryList.java`
  - `src/main/java/thaumcraft/api/research/ResearchEntry.java`, only to classify TC6 compatibility projection
  - `src/main/java/thaumcraft/api/research/ResearchCategory.java` and `ResearchCategoriesCompat.java`, only to classify TC6 compatibility additions
  - `src/main/java/thaumcraft/api/aspects/AspectList.java`, only to establish duplicate-aspect accumulation and primary-tag ordering
  - `src/main/java/thaumcraft/api/ThaumcraftApi.java`, only `addWarpToItem`, `addWarpToResearch`, and `getWarp`
- Port declaration surfaces:
  - `src/main/java/thaumcraft/common/config/research/ConfigResearch.java:18-49,52-72,91-116`
  - `src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java:16-367`
- Exact TC4 oracle surfaces decompiled from `Thaumcraft-1.7.10-4.2.3.5.jar`:
  - `thaumcraft/api/research/ResearchItem.class`
  - `thaumcraft/api/research/ResearchPage.class`
  - `thaumcraft/api/research/ResearchCategories.class`
  - `thaumcraft/api/research/ResearchCategoryList.class`
  - `thaumcraft/api/aspects/AspectList.class`, restricted to ordering and `add`
  - `thaumcraft/api/ThaumcraftApi.class`, restricted to warp registration/lookup
  - `thaumcraft/common/config/ConfigResearch.class`, `initCategories()` and `initEldritchResearch()`
- Existing tests inspected:
  - `src/test/java/thaumcraft/common/config/ConfigResearchStaticGraphTest.java`
  - `src/test/java/thaumcraft/common/config/ConfigResearchReferenceFingerprintStaticGuardTest.java`
  - `src/test/java/thaumcraft/common/config/ConfigResearchAspectTriggerCoverageTest.java`
  - `src/test/java/thaumcraft/common/lib/research/ResearchClueAndNotesRuntimeTest.java`
  - focused warp consumers found under `src/test/java/thaumcraft/common/**`
- Uncovered scope: No recipe implementation or page renderer was compared. No manager gating, completion, UI, persistence, or runtime consumer conclusion is made here.

## Audit Result

No verified semantic discrepancy exists in the TC4 research-schema behavior exercised by the 16 Eldritch declarations.

- Constructor behavior, defaults, aspect-cost storage, effective complexity, fluent flags, parent arrays, hidden-parent arrays, trigger arrays, pages, warp keys/amounts, category registration, duplicate rejection, collision rejection, and accepted-entry bounds updates match the TC4 oracle.
- Every declaration has the same key, category, aspects and insertion order, coordinates, declared complexity, effective complexity, icon, ordered pages and page types, flags, ordered parents, ordered hidden parents, triggers, and research/item warp associations as TC4.
- One source-order difference remains: the split port initializer registers `ELDRITCHMAJOR` before `ELDRITCHMINOR`, while TC4 registers minor then major. The entries have unique keys and coordinates and are stored in a `HashMap`, so no schema effect was established. This is indexed below as an informational benign delta, not a product defect.

## Atomic Findings

### A-020-F01 — The two Eldritch roots are registered in reverse source order

- Type: benign_delta
- Severity: informational
- Confidence: high
- Source/oracle locator: S-003/S-004 decompiled `thaumcraft.common.config.ConfigResearch.initEldritchResearch()` lines 394-395; S-005 `ConfigResearch.java:67-68` and `ConfigResearchEldritch.java:16-31,350-366`.
- Observed: The port invokes `initEldritchResearchTextOnlyBaseline()` first, registering `ELDRITCHMAJOR`, then invokes `initEldritchResearchBaseline()`, registering `ELDRITCHMINOR` before the remaining entries.
- Expected: TC4's monolithic method registers `ELDRITCHMINOR`, then `ELDRITCHMAJOR`, then the remaining entries.
- Effect: None established. Both keys and coordinates are distinct; registration succeeds in either order; category research storage is a `HashMap`; all parent lookups occur after initialization in the audited schema surface.
- Regression hazard: Do not infer permission to change root metadata, graph edges, flags, or unlock behavior. Registration order could become observable if ordered callbacks or insertion-ordered storage are introduced later.
- Test gap: Existing fingerprint and graph tests normalize or ignore declaration order.
- Candidate disposition: preserve as documented informational delta; no schema fix is justified by current evidence.

### A-020-F02 — Complete Eldritch schema metadata lacks a focused regression oracle

- Type: test_debt
- Severity: medium
- Confidence: high
- Source/oracle locator: S-005 `ConfigResearchReferenceFingerprintStaticGuardTest.java:22-49,52-67` and `ConfigResearchStaticGraphTest.java:28-33,38-98,122-143`.
- Observed: Existing tests constrain the 201 key/category pairs, text-page-key corpus, graph references, cycles, localization, and assets, but do not snapshot the 16-entry metadata matrix below.
- Expected: A focused static or runtime contract should constrain declared/effective complexity, exact ordered aspect amounts, coordinates, icons, page types/order and recipe handle types, every flag, parents/hidden parents and order, item/entity triggers, absence of aspect triggers/siblings/virtual/auto-unlock, warp associations, category collision behavior, and category bounds.
- Effect: Most schema drift would compile and can pass the current corpus/graph tests. In particular, changed flags, aspect amounts, recipe-page types, triggers, coordinates without collision, or warp values are not caught by the fingerprint.
- Regression hazard: A future guard should compare semantic values rather than formatting or initializer method boundaries, and should preserve the documented benign root-order delta unless insertion order becomes an explicit contract.
- Candidate disposition: preserve as explicit test debt or add a focused oracle when this schema is next changed.

## ResearchItem Schema Semantics

The port at `ResearchItem.java:13-269` matches the decompiled TC4 class for the TC4-visible contract at original lines 19-256.

### Constructors and Defaults

| Surface | TC4 semantics | Port result |
|---|---|---|
| Two-argument constructor `(key, category)` | Stores key/category; creates empty `AspectList`; both icons null; coordinates `(0,0)`; complexity remains Java default `0`; calls `setVirtual()` | Match; additionally projects values into the TC6-compatible base entry |
| Resource-icon full constructor | Stores the supplied `AspectList` by reference; resource icon set, item icon null; coordinates copied; complexity clamped to inclusive range `1..3` | Match |
| Item-icon full constructor | Stores the supplied `AspectList` and `ItemStack` by reference; resource icon null; coordinates copied; complexity clamped to inclusive range `1..3` | Match |
| Boolean defaults | `special`, `secondary`, `round`, `stub`, `virtual`, `concealed`, `hidden`, `lost`, and `autoUnlock` default false | Match |
| Array/page defaults | parents, hidden parents, siblings, item/entity/aspect triggers, and pages default null | Match |
| Full-constructor complexity 0 | Clamped upward to 1 | Match; applies to both Eldritch root declarations |
| Full-constructor complexity above 3 | Clamped downward to 3 | Match |
| `setComplexity(int)` | Direct assignment with no clamp | Match |

The two root declarations therefore retain declared complexity `0` but have effective runtime complexity `1`. This non-obvious behavior is exact TC4 parity, not a port correction.

### Mutators, Accessors, and Primary Aspect

- Every fluent flag setter only sets its corresponding boolean true and returns the same `ResearchItem`; there are no clear/toggle methods. All getters match.
- `setParents`, `setParentsHidden`, `setSiblings`, `setPages`, and all trigger setters retain the supplied arrays directly without copying, validation, normalization, deduplication, or reordering. Getters return the retained references.
- Port `setParents` and `setSiblings` additionally synchronize the TC6-compatible base fields. The TC4-visible arrays remain the exact supplied arrays.
- `registerResearchItem()` delegates to `ResearchCategories.addResearch(this)` and returns the same object on both sides, including when registration is ignored or rejected.
- `getResearchPrimaryTag()` scans `tags.getAspects()` in `AspectList` insertion order and replaces the result only for a strictly greater amount. Ties retain the first inserted highest aspect. Port and original both use `LinkedHashMap`-backed `AspectList` order.
- `tags == null` returns no primary aspect. Eldritch declarations supply non-null lists.
- Name and text keys remain `tc.research_name.<key>` and `tc.research_text.<key>`.

## ResearchPage Schema Semantics

Port `ResearchPage.java:15-127` matches decompiled TC4 `ResearchPage.java:25-138` after mapped Minecraft method names.

### Defaults and Constructor Matrix

| Constructor | Page type | Stored fields and output behavior | Used by Eldritch |
|---|---|---|---|
| `ResearchPage(String text)` | `TEXT` | `text` set; all other payload fields retain null defaults | yes |
| `ResearchPage(String research, String text)` | `TEXT_CONCEALED` | `research` and `text` set | no |
| `ResearchPage(IRecipe recipe)` | `NORMAL_CRAFTING` | stores recipe; captures `recipe.getRecipeOutput()` | yes |
| `ResearchPage(IRecipe[] recipes)` | `NORMAL_CRAFTING` | stores array; leaves `recipeOutput` null | no Eldritch array page |
| `ResearchPage(IArcaneRecipe[] recipes)` | `ARCANE_CRAFTING` | stores array; leaves `recipeOutput` null | no |
| `ResearchPage(CrucibleRecipe[] recipes)` | `CRUCIBLE_CRAFTING` | stores array; leaves `recipeOutput` null | no |
| `ResearchPage(InfusionRecipe[] recipes)` | `INFUSION_CRAFTING` | stores array; leaves `recipeOutput` null | no |
| `ResearchPage(List recipes)` | `COMPOUND_CRAFTING` | stores list; leaves `recipeOutput` null | yes, `ADVALCHEMYFURNACE` |
| `ResearchPage(IArcaneRecipe recipe)` | `ARCANE_CRAFTING` | stores recipe; captures recipe output | yes |
| `ResearchPage(CrucibleRecipe recipe)` | `CRUCIBLE_CRAFTING` | stores recipe; captures recipe output | yes |
| `ResearchPage(ItemStack input)` | `SMELTING` | stores input; resolves furnace output | no |
| `ResearchPage(InfusionRecipe recipe)` | `INFUSION_CRAFTING` | stores recipe; output is recipe output when it is an `ItemStack`, otherwise recipe input | yes |
| `ResearchPage(InfusionEnchantmentRecipe recipe)` | `INFUSION_ENCHANTMENT` | stores recipe; leaves `recipeOutput` null | no |
| `ResearchPage(ResourceLocation image, String caption)` | `IMAGE` | stores image and caption in `text` | no |
| `ResearchPage(AspectList aspects)` | `ASPECTS` | stores aspect list | no |

- Field defaults match: type starts as `TEXT`; text, concealed-research key, image, aspects, recipe, and recipe output start null.
- `PageType` enum membership and order match exactly: `TEXT`, `TEXT_CONCEALED`, `IMAGE`, `CRUCIBLE_CRAFTING`, `ARCANE_CRAFTING`, `ASPECTS`, `NORMAL_CRAFTING`, `INFUSION_CRAFTING`, `COMPOUND_CRAFTING`, `INFUSION_ENCHANTMENT`, `SMELTING`.
- `getTranslatedText()` starts with `""`; when text is non-null, it performs localization and only falls back to the literal text if the localization result is empty. The port's 1.12 `I18n` call is the mapped replacement for TC4 `StatCollector`.
- Constructor overload selection is semantically important. The typed port recipe helpers preserve the same singular recipe interfaces and therefore the same page types shown below.

## ResearchCategories Schema and Collision Semantics

Port `ResearchCategories.java:13-90` matches decompiled TC4 `ResearchCategories.java:21-76` for the original API path.

### Category Registration and Lookup

- `researchCategories` is a mutable public `LinkedHashMap<String, ResearchCategoryList>` on both sides.
- Original three-argument `registerCategory(key, icon, background)` inserts a new category only if the key is absent. A duplicate category key is silently ignored; it does not replace the category, icon, background, research map, or bounds.
- `ResearchCategoryList.research` is a `HashMap<String, ResearchItem>` on both sides.
- A new category's four integer bounds use Java default `0`; registration does not initialize them to sentinel extremes.
- `getResearchList(key)` is a direct map lookup.
- `getCategoryName(key)` localizes `tc.research_category.<key>`.
- Global `getResearch(key)` iterates categories in category insertion order, then each category's research-map values, and returns the first equal key. The API permits the same research key in different categories because insertion checks are category-local; such cross-category duplication would make global lookup first-match dependent. No such duplicate exists in the audited corpus.

### Research Addition, Duplicate, Collision, and Replacement Rules

The exact operation order in `addResearch(ri)` is:

1. Resolve `ri.category`; a missing category silently prevents registration.
2. Check the target category map for `ri.key`; an existing key silently prevents registration.
3. If the incoming research is nonvirtual, compare its `(displayColumn, displayRow)` against every existing entry in that category.
4. On the first coordinate match, log a fatal overlap message and return without insertion or bounds changes.
5. Otherwise insert the same `ResearchItem` instance under its key.
6. Expand each min/max display bound independently using the accepted entry's coordinates.

Consequences preserved exactly from TC4:

- Category and research duplicates are first-wins; there is no replacement path.
- Duplicate-key rejection happens before coordinate collision checking.
- Coordinate collision checking is category-local.
- Incoming virtual research bypasses coordinate checking and may overlap any existing entry.
- Incoming nonvirtual research is checked against every existing entry, including virtual entries. Thus a virtual entry accepted first can block a later nonvirtual entry at the same coordinates, while the reverse insertion order permits the virtual overlap. This asymmetric behavior matches TC4.
- Accepted virtual entries update category bounds just like nonvirtual entries.
- Rejected, duplicate, and missing-category entries do not update bounds.
- Bounds begin at zero, so a category containing entries only on one side of zero can retain zero as the opposite bound. Eldritch spans both signs where relevant and finishes at columns `-5..6`, rows `-3..6`.
- The 16 Eldritch coordinates are all unique; no declaration is virtual; all 16 register without collision under either root registration order.

### ELDRITCH Category Registration

| Field | TC4 | Port | Result |
|---|---|---|---|
| Key | `ELDRITCH` | `ELDRITCH` | match |
| Icon | `thaumcraft:textures/misc/r_eldritch.png` | same | match |
| Background | `thaumcraft:textures/gui/gui_researchbackeldritch.png` | same | match |
| Secondary background/formula/research key | not part of TC4 registration | absent on original three-argument path | compatible addition not used here |
| Final accepted entries | 16 | 16 | match |
| Final bounds | col `-5..6`, row `-3..6` | same | match |

## Full 16-Entry Metadata Matrix

Notation: `decl/eff` means constructor argument and effective clamped complexity. Aspect order shown is insertion order. `R:` denotes a resource icon; `I:` denotes an item stack with count/meta where material. `none` means null/false/absent, not an empty trigger or parent array.

### Identity, Cost, Position, Complexity, and Icon

| Research | Ordered aspect costs | `(column,row)` | Complexity `decl/eff` | Icon |
|---|---|---:|---:|---|
| `ELDRITCHMINOR` | empty | `(1,0)` | `0/1` | R: `thaumcraft:textures/misc/r_eldritchminor.png` |
| `ELDRITCHMAJOR` | empty | `(-1,0)` | `0/1` | R: `thaumcraft:textures/misc/r_eldritchmajor.png` |
| `OCULUS` | `MIND 3`, `DARKNESS 3`, `EXCHANGE 3`, `TRAVEL 6`, `ELDRITCH 6` | `(-2,2)` | `1/1` | I: Eldritch Object x1 meta 0 |
| `ENTEROUTER` | empty | `(-3,4)` | `1/1` | R: `thaumcraft:textures/misc/r_outer.png` |
| `OUTERREV` | `ELDRITCH 4`, `MIND 4` | `(-5,3)` | `1/1` | R: `thaumcraft:textures/misc/r_outerrev.png` |
| `PRIMPEARL` | `AIR 8`, `EARTH 8`, `FIRE 8`, `WATER 8`, `ORDER 8`, `ENTROPY 8` | `(0,4)` | `1/1` | I: Eldritch Object x1 meta 3 |
| `PRIMNODE` | `AURA 1`, `MAGIC 1`, `ORDER 1`, `ENTROPY 1` | `(0,6)` | `1/1` | R: `thaumcraft:textures/misc/r_nodes_2.png` |
| `ADVALCHEMYFURNACE` | `AURA 1`, `MAGIC 1`, `ORDER 1`, `ENTROPY 1` | `(-2,6)` | `1/1` | I: Metal Device x1 meta 3 |
| `PRIMALCRUSHER` | `MINE 6`, `TOOL 6`, `ENTROPY 6`, `VOID 6`, `WEAPON 6`, `ELDRITCH 6`, `GREED 6` | `(2,5)` | `2/2` | I: Primal Crusher x1 meta 0 |
| `VOIDMETAL` | `METAL 3`, `ELDRITCH 3`, `DARKNESS 3`, `VOID 5` | `(2,-2)` | `2/2` | I: Resource x1 meta 16 |
| `ESSENTIARESERVOIR` | declaration: `WATER 5`, `VOID 3`, `EXCHANGE 3`, `MAGIC 5`, `VOID +5`; effective: `WATER 5`, `VOID 8`, `EXCHANGE 3`, `MAGIC 5` | `(4,-3)` | `2/2` | I: Essentia Reservoir x1 meta 0 |
| `CAP_void` | `VOID 5`, `ELDRITCH 5`, `TOOL 3`, `MAGIC 3`, `AURA 3` | `(5,-1)` | `3/3` | I: Wand Cap x1 meta 7 |
| `ARMORVOIDFORTRESS` | `ARMOR 5`, `ELDRITCH 3`, `CLOTH 3`, `DARKNESS 3`, `VOID 5` | `(0,-3)` | `3/3` | I: Void Robe Helmet x1 meta 0 |
| `FOCUSPRIMAL` | `AIR 6`, `WATER 6`, `FIRE 6`, `EARTH 6`, `ORDER 6`, `ENTROPY 6`, `MAGIC 6` | `(4,1)` | `2/2` | I: Primal Focus x1 meta 0 |
| `SANITYCHECK` | `MIND 5`, `ELDRITCH 3`, `SENSES 5` | `(2,2)` | `1/1` | I: Sanity Checker x1 meta 0 |
| `ROD_primal_staff` | `AIR 9`, `EARTH 9`, `FIRE 9`, `WATER 9`, `ORDER 9`, `ENTROPY 9`, `TOOL 9`, `MAGIC 12` | `(6,2)` | `3/3` | I: Wand Rod x1 meta 100 |

`ESSENTIARESERVOIR` is intentionally listed with both declaration and effective costs. `AspectList.add` accumulates duplicate aspects on both sides, so its effective `VOID` cost is 8 while retaining VOID's first insertion position.

### Flags

| Research | special | secondary | round | stub | virtual | concealed | hidden | lost | auto-unlock |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| `ELDRITCHMINOR` | yes | no | yes | no | no | no | yes | no | no |
| `ELDRITCHMAJOR` | yes | no | yes | yes | no | no | yes | no | no |
| `OCULUS` | yes | no | yes | no | no | yes | no | no | no |
| `ENTEROUTER` | no | no | yes | yes | no | no | yes | no | no |
| `OUTERREV` | yes | yes | no | no | no | no | no | yes | no |
| `PRIMPEARL` | yes | yes | no | no | no | no | no | yes | no |
| `PRIMNODE` | no | yes | no | no | no | no | no | no | no |
| `ADVALCHEMYFURNACE` | no | yes | no | no | no | no | no | no | no |
| `PRIMALCRUSHER` | no | no | no | no | no | yes | no | no | no |
| `VOIDMETAL` | no | no | no | no | no | no | no | no | no |
| `ESSENTIARESERVOIR` | no | no | no | no | no | no | no | no | no |
| `CAP_void` | no | no | no | no | no | yes | no | no | no |
| `ARMORVOIDFORTRESS` | no | yes | no | no | no | yes | no | no | no |
| `FOCUSPRIMAL` | no | no | no | no | no | yes | no | no | no |
| `SANITYCHECK` | no | no | no | no | no | no | no | no | no |
| `ROD_primal_staff` | no | no | no | no | no | no | yes | no | no |

No Eldritch declaration is virtual or auto-unlocked. No declaration uses siblings or aspect triggers.

### Parents, Hidden Parents, Triggers, and Warp

| Research | Ordered parents | Ordered hidden parents | Triggers | Warp association |
|---|---|---|---|---|
| `ELDRITCHMINOR` | none | none | none | none |
| `ELDRITCHMAJOR` | none | none | none | none |
| `OCULUS` | `CRIMSON`, `ELDRITCHMAJOR` | none | none | research `OCULUS` = 6 |
| `ENTEROUTER` | `OCULUS` | none | none | none |
| `OUTERREV` | `ENTEROUTER` | none | item: Eldritch Block x1 meta 5; Eldritch Block x1 meta 10 | none |
| `PRIMPEARL` | `ELDRITCHMINOR` | none | item: Eldritch Object x1 meta 3 | none |
| `PRIMNODE` | `PRIMPEARL` | none | none | research `PRIMNODE` = 1 |
| `ADVALCHEMYFURNACE` | `PRIMPEARL`, `DISTILESSENTIA`, `VISPOWER` | none | none | none |
| `PRIMALCRUSHER` | `PRIMPEARL` | `VOIDMETAL`, `ELEMENTALPICK`, `ELEMENTALSHOVEL` | none | none |
| `VOIDMETAL` | `THAUMIUM`, `ELDRITCHMINOR` | none | none | none |
| `ESSENTIARESERVOIR` | `VOIDMETAL`, `CENTRIFUGE`, `INFUSION` | none | none | none |
| `CAP_void` | `CAP_thaumium`, `VOIDMETAL` | none | none | research `CAP_void` = 1 |
| `ARMORVOIDFORTRESS` | `VOIDMETAL`, `ENCHFABRIC`, `ELDRITCHMAJOR` | none | none | none |
| `FOCUSPRIMAL` | `ELDRITCHMINOR` | none | none | research `FOCUSPRIMAL` = 2; Primal Focus item meta 0 = 1 |
| `SANITYCHECK` | `ELDRITCHMINOR` | none | none | none |
| `ROD_primal_staff` | `FOCUSPRIMAL` | `ROD_silverwood_staff`, `ROD_bone_staff`, `ROD_greatwood_staff`, `ROD_blaze_staff`, `ROD_reed_staff`, `ROD_obsidian_staff`, `ROD_quartz_staff`, `ROD_ice_staff` | entity: `Thaumcraft.PrimalOrb`; item: Primal Focus x1 meta 0 | research `ROD_primal_staff` = 3; Wand Rod item meta 100 = 1 |

Warp is not a `ResearchItem` field. On both sides `ThaumcraftApi.addWarpToResearch` stores the exact string key in `warpMap`; item warp keys use item identity plus metadata and ignore count/NBT. The five research-warp calls and two item-warp calls above match exactly.

### Ordered Page and Type Matrix

| Research | Ordered pages: type and payload |
|---|---|
| `ELDRITCHMINOR` | 1. `TEXT` `tc.research_page.ELDRITCHMINOR.1`; 2. `CRUCIBLE_CRAFTING` handle `VoidSeed` |
| `ELDRITCHMAJOR` | 1. `TEXT` `tc.research_page.ELDRITCHMAJOR.1`; 2. `TEXT` `tc.research_page.ELDRITCHMAJOR.2` |
| `OCULUS` | 1. `TEXT` `tc.research_page.OCULUS.1`; 2. `INFUSION_CRAFTING` handle `EldritchEye`; 3. `TEXT` `tc.research_page.OCULUS.2` |
| `ENTEROUTER` | 1. `TEXT` `tc.research_page.ENTEROUTER.1` |
| `OUTERREV` | 1. `TEXT` `tc.research_page.OUTERREV.1` |
| `PRIMPEARL` | 1. `TEXT` `tc.research_page.PRIMPEARL.1`; 2. `TEXT` `tc.research_page.PRIMPEARL.2` |
| `PRIMNODE` | 1. `TEXT` `tc.research_page.PRIMNODE.1` |
| `ADVALCHEMYFURNACE` | 1. `TEXT` `tc.research_page.ADVALCHEMYFURNACE.1`; 2. `ARCANE_CRAFTING` handle `AdvAlchemyConstruct`; 3. `TEXT` `tc.research_page.ADVALCHEMYFURNACE.2`; 4. `COMPOUND_CRAFTING` list handle `AdvAlchemyFurnace` |
| `PRIMALCRUSHER` | 1. `TEXT` `tc.research_page.PRIMALCRUSHER.1`; 2. `INFUSION_CRAFTING` handle `PrimalCrusher`; 3. `TEXT` `tc.research_page.PRIMALCRUSHER.2` |
| `VOIDMETAL` | 1. `TEXT` `tc.research_page.VOIDMETAL.1`; 2. `CRUCIBLE_CRAFTING` handle `VoidMetal`; 3. `TEXT` `tc.research_page.VOIDMETAL.2`; 4-12. `NORMAL_CRAFTING` handles in exact order `VoidAxe`, `VoidSword`, `VoidPick`, `VoidShovel`, `VoidHoe`, `VoidHelm`, `VoidChest`, `VoidLegs`, `VoidBoots` |
| `ESSENTIARESERVOIR` | 1. `TEXT` `tc.research_page.ESSENTIARESERVOIR.1`; 2. `INFUSION_CRAFTING` handle `EssentiaReservoir`; 3. `TEXT` `tc.research_page.ESSENTIARESERVOIR.2` |
| `CAP_void` | 1. `TEXT` `tc.research_page.CAP_void.1`; 2. `ARCANE_CRAFTING` handle `WandCapVoidInert`; 3. `INFUSION_CRAFTING` handle `WandCapVoid` |
| `ARMORVOIDFORTRESS` | 1. `TEXT` `tc.research_page.ARMORVOIDFORTRESS.1`; 2. `INFUSION_CRAFTING` handle `VoidRobeHelm`; 3. `INFUSION_CRAFTING` handle `VoidRobeChest`; 4. `INFUSION_CRAFTING` handle `VoidRobeLegs` |
| `FOCUSPRIMAL` | 1. `TEXT` `tc.research_page.FOCUSPRIMAL.1`; 2. `ARCANE_CRAFTING` handle `FocusPrimal` |
| `SANITYCHECK` | 1. `TEXT` `tc.research_page.SANITYCHECK.1`; 2. `INFUSION_CRAFTING` handle `SanityCheck` |
| `ROD_primal_staff` | 1. `TEXT` `tc.research_page.ROD_primal_staff.1`; 2. `INFUSION_CRAFTING` handle `WandRodPrimalStaff` |

All page counts, order, overload-selected types, text keys, and recipe handle keys match the original. Recipe body and renderer behavior are explicitly outside this report.

## Positive Parity

### A-020-PC01 — ResearchItem TC4-visible schema

- All constructors, default values, complexity clamping, direct-reference storage, flags, parents, hidden parents, siblings, triggers, pages, registration return behavior, localization key construction, and primary-tag tie behavior match.
- Both declared-complexity-zero Eldritch roots become effective complexity 1.
- Every unused Eldritch feature remains absent: virtual, auto-unlock, siblings, and aspect triggers.

### A-020-PC02 — ResearchPage schema and overload selection

- Field defaults, all constructor-to-page-type mappings, recipe output capture rules, enum membership/order, and translated-text fallback match.
- Every Eldritch recipe page resolves through the same singular/list interface as TC4 and therefore retains the same page type and payload role.

### A-020-PC03 — Category, duplicate, collision, and replacement behavior

- The original `ELDRITCH` category registration path is first-wins and non-replacing on both sides.
- Research duplicate keys are first-wins within the category; missing categories are ignored; nonvirtual coordinate collisions are fatal-logged and rejected; virtual incoming entries bypass collision checks; accepted entries update bounds.
- All 16 entries have unique keys and coordinates and produce equal final bounds.

### A-020-PC04 — Complete declaration metadata

- The full four-part matrix above matches TC4: identity/cost/position/icon, flags, graph/triggers/warp, and pages/types/order.
- Aspect insertion order is preserved, including first-insertion position with duplicate `VOID` accumulation on `ESSENTIARESERVOIR`.
- Parent and hidden-parent order is preserved, including all eight hidden staff prerequisites.

### A-020-PC05 — Warp association

- Research warp values match: `OCULUS 6`, `PRIMNODE 1`, `CAP_void 1`, `FOCUSPRIMAL 2`, `ROD_primal_staff 3`.
- Item warp values match: Primal Focus meta 0 = 1 and Wand Rod meta 100 = 1.
- No other one of the 16 entries receives research warp.

## Intentional and Benign Adaptations

- `ResearchItem` extends the port's TC6-compatible `ResearchEntry`. `syncBase` projects key, category, coordinates, and icon; `setParents`/`setSiblings` project those arrays. Original public TC4 fields and semantics remain canonical for the audited declarations.
- Port `ResearchItem` adds `onResearchComplete`, unused by all 16 declarations. It does not alter original defaults or registration.
- `ResearchCategories` extends `ResearchCategoriesCompat` and adds `getResearchCategory` plus TC6-shaped category overloads returning `ResearchCategory`. The original three-argument TC4 registration path used for `ELDRITCH` is unchanged.
- Build-time bytecode augmentation for TC6 `ResearchEntry` void setters is separate from the TC4 fluent `ResearchItem` methods and does not change these instances' TC4 behavior.
- Minecraft 1.7 `StatCollector` calls map to 1.12 `I18n`; obfuscated recipe/furnace methods map to stable 1.12 names.
- Recipe map casts became typed, fail-fast helpers (`recipeI`, `recipeArcane`, `recipeCrucible`, `recipeInfusion`, `recipeList`). Successful lookups preserve exact handle identity/type and page selection. Missing/wrong handles now fail earlier; no successful Eldritch page changes.
- Original item field names `itemFocusPrimal` and `itemHelmetVoidRobe` map to port fields `focusPrimal` and `itemHelmVoidRobe`; icon, trigger, and warp item roles remain equivalent.
- The original monolithic Eldritch initializer is split. This produces A-020-F01's benign root registration-order transposition without changing accepted metadata.
- Outside the Eldritch-used page surface, 1.12 furnace lookup may represent no smelting result as `ItemStack.EMPTY` rather than TC4 null. No one of the 16 declarations uses the smelting-page constructor.

## Test Debt

- A-020-F02: No focused test snapshots the complete 16-entry metadata/page matrix.
- `ConfigResearchReferenceFingerprintStaticGuardTest` uses sorted sets of key/category and text page keys. It cannot detect order, coordinates, complexity, icons, aspects/amounts, flags, graph order, triggers, recipe-page types, or warp changes.
- `ConfigResearchStaticGraphTest` catches duplicate keys, unexpected categories, missing parent references, cycles, missing localization, and missing assets, but not exact schema parity.
- `ConfigResearchAspectTriggerCoverageTest` validates that aspects referenced by trigger calls exist globally; it does not assert that all Eldritch entries correctly have no aspect triggers.
- Existing research runtime tests exercise selected flags and trigger mechanics on synthetic `TEST` entries. They do not prove the registered Eldritch instances contain this matrix.
- No focused test directly constrains `ResearchCategories.addResearch` first-wins replacement behavior, virtual/nonvirtual collision asymmetry, rejection-without-bounds-update, or exact Eldritch final bounds.
- No focused test constrains all five Eldritch research-warp and two item-warp registrations as one table.
- Positive parity recommendation: add one semantic schema test that initializes recipe handles and research, then asserts all values without depending on source formatting or split initializer order; add a small isolated category collision test for duplicate, nonvirtual, and virtual cases.

## Evidence

- Direct source correspondence:
  - Port `ResearchItem.java:45-269` vs CFR original `ResearchItem.java:45-255`.
  - Port `ResearchPage.java:15-127` vs CFR original `ResearchPage.java:25-137`.
  - Port `ResearchCategories.java:14-89` vs CFR original `ResearchCategories.java:21-75`.
  - Port `ResearchCategoryList.java:8-20` vs CFR original `ResearchCategoryList.java:14-26`.
  - Port `ConfigResearchEldritch.java:16-367` vs CFR original `ConfigResearch.java:393-416`.
  - Port category registration `ConfigResearch.java:112-115` vs CFR original `ConfigResearch.java:67-74`.
  - Port warp implementation `ThaumcraftApi.java:312-330` vs CFR original `ThaumcraftApi.java:277-295`.
- CFR output used during audit: `/home/stfu/.cache/thaumcraft-i01-tc4-cfr/`. This is transient external evidence and was not added to the repository.
- CFR emitted missing Minecraft dependency warnings while decompiling the jar, but all relevant class structure, constants, branches, constructor chains, and method bodies decompiled completely. Mapped method names were checked against the port equivalents.
- The original and port both use additive `AspectList.add`, establishing effective `ESSENTIARESERVOIR` VOID 8 from additions 3 and 5.

## Commands and Results

All commands were run from `/home/stfu/ai/dont/thaumcraft` unless the output path says otherwise.

```text
git status --short
jar tf Thaumcraft-1.7.10-4.2.3.5.jar | grep 'ResearchItem.class'
cfr Thaumcraft-1.7.10-4.2.3.5.jar --outputdir /home/stfu/.cache/thaumcraft-i01-tc4-cfr --silent true
git diff --exit-code -- src/main/java/thaumcraft/api/research/ResearchItem.java src/main/java/thaumcraft/api/research/ResearchPage.java src/main/java/thaumcraft/api/research/ResearchCategories.java src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java
git status --short
```

- Targeted repository reads and searches covered all files listed under Coverage Performed and all 16 original/port declarations.
- Result: no verified semantic discrepancy; one informational source-order delta; complete positive-parity matrix recorded; test debt identified.
- Product validation: no compile, test, or build run because the audit was read-only and this persistence step changes documentation only.
- Runtime smoke: not required and not run because no product/runtime path changed.
- Product diff: none.
- Central Goal Ledger diff by this assignment: none.

## Handoff

- Terminal status: complete.
- Material finding index: A-020-F01 informational root registration-order transposition; A-020-F02 missing complete Eldritch schema regression oracle.
- Positive parity index: A-020-PC01 `ResearchItem`; A-020-PC02 `ResearchPage`; A-020-PC03 category/duplicate/collision semantics; A-020-PC04 full 16-entry metadata; A-020-PC05 warp association.
- Adaptation index: TC6 `ResearchEntry` projection; TC6 category overloads; bytecode setter shim; 1.12 localization/mappings; typed recipe handles; renamed item fields; split initializer; unused smelting-empty sentinel difference.
- Exact continuation point: none; A-020 research-schema scope is exhausted and ready for orchestrator normalization.
