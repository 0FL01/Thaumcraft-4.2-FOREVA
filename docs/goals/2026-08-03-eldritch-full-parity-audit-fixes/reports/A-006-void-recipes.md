# Audit Packet: A-006 — Void recipes

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-006
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare all 16 Eldritch Void recipe handles and definitions: `VoidMetal`, `VoidSeed`, `WandCapVoidInert`, `WandCapVoid`, `VoidRobeHelm`, `VoidRobeChest`, `VoidRobeLegs`, and the nine normal Void equipment recipes `VoidHelm`, `VoidChest`, `VoidLegs`, `VoidBoots`, `VoidShovel`, `VoidPick`, `VoidAxe`, `VoidHoe`, and `VoidSword`.
- Port surfaces: `ConfigRecipesCrucibleSlice.java`, `ConfigRecipesArcaneSlice.java`, `ConfigRecipesInfusionSlice.java`, `ConfigRecipesInfusionEquipmentSlice.java`, `ConfigRecipesSpecialSlice.java`, and handle/research-page wiring only.
- Anti-scope: Item, armor, tool, wand-cap, or other runtime behavior; unrelated recipes; product edits; central Goal Ledger edits.
- Oracle and comparison direction: TC4 4.2.3.5 `ConfigRecipes.class` and `ConfigResearch.class` (S-003/S-004) -> Forge 1.12.2 port (S-005).
- Questions: For every recipe, compare output item/meta/count/NBT, input arrangement and ore/wildcard semantics, aspects or vis, instability, research key, recipe type, handle/page semantics, and registration order where behaviorally relevant.
- Expected evidence: Fresh CFR decompilation of relevant original methods, exact port locators, and SRG-to-MCP mapping evidence for obfuscated vanilla fields.
- Read/write permissions: Product and central ledger files read-only; only this report writable.
- Stop conditions: All 16 definitions and their handles/pages classified, or an unresolved mapping prevents a confident comparison.

## Coverage Performed

- Original gameplay oracle: `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`, SHA-256 `311f0ce37e451d13bdc73b94f83a5d1d333ca6cc1f4e3e1594a08edd0dfbca20`.
- Original research oracle: `thaumcraft_src/thaumcraft/common/config/ConfigResearch.class`, SHA-256 `7ed2e392b82f75f6636abd57477a2de97566b5d395a0731b4e1e5d64e45aa50d`.
- Original methods decompiled: `ConfigRecipes.initializeAlchemyRecipes`, `initializeArcaneRecipes`, `initializeInfusionRecipes`, `initializeNormalRecipes`, `init`, and `oreDictRecipe`; `ConfigResearch.initEldritchResearch`.
- Durable decompile locator inspected: `run/recon-r4-cfr/thaumcraft/common/config/ConfigRecipes.java:118-119,237,329,382-384,468-476` and `run/recon-r4-cfr/thaumcraft/common/config/ConfigResearch.java:394,406,408,410`.
- Port definitions inspected: `src/main/java/thaumcraft/common/config/recipes/ConfigRecipesCrucibleSlice.java:234-244`, `ConfigRecipesArcaneSlice.java:305-312`, `ConfigRecipesInfusionSlice.java:45-56`, `ConfigRecipesInfusionEquipmentSlice.java:129-148`, and `ConfigRecipesSpecialSlice.java:536-636`.
- Port handle/page wiring inspected: `src/main/java/thaumcraft/common/config/ConfigRecipes.java:77-78,149-153,410-416,536-540,639-648` and `src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java:25-27,191-203,240-243,262-266`.
- Port lifecycle inspected: recipe registry event at `src/main/java/thaumcraft/common/Thaumcraft.java:248-252`; recipe and research initialization at `Thaumcraft.java:199-207`.
- Test corpus inspected: `ConfigRecipesReferenceKeyCorpusStaticGuardTest`, `ConfigResearchRecipeKeyCoverageTest`, `ConfigResearchRecipeLookupTypeAuditTest`, `ConfigRecipesResearchHandleParityStaticGuardTest`, and `ConfigRecipesSpecialRecipeLifecycleStaticGuardTest`.
- Uncovered scope: Runtime crucible execution, arcane workbench execution, infusion execution, ordinary crafting execution, client recipe rendering, and all item runtime were excluded by assignment.

## Commands and Tools

Commands run during the audit:

```text
git status --short
javap -p thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class --methodname initializeAlchemyRecipes --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class --methodname initializeArcaneRecipes --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class --methodname initializeInfusionRecipes --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class --methodname initializeNormalRecipes --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class --methodname init --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class --methodname oreDictRecipe --silent true
javap -p thaumcraft_src/thaumcraft/common/config/ConfigResearch.class
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigResearch.class --methodname initEldritchResearch --silent true
jar tf run/validate/forge-1.7.10-userdev.jar
unzip -l run/validate/forge-1.7.10-src.zip
jar tf run/validate/minecraft-1.7.10-client.jar
git status --short
```

- Targeted repository searches resolved every target key, its exact port constructor arguments, its handle publisher, and its Eldritch research page consumer.
- Mapping search resolved `field_151014_N` at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/fields.csv:1905` and the corresponding bidirectional SRG entries at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/{mcp-srg,srg-mcp}.srg:7865`.
- No build, tests, or runtime smoke were run because the assignment was a read-only source/decompile audit. The audit worktree was clean before and after the original audit. During packet persistence, the orchestrator-owned active-goal and goal directory were already untracked; no product path was changed.

## Atomic Findings

### A-006-F01 — VoidSeed uses an ender pearl instead of wheat seeds

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-003/S-004; `run/recon-r4-cfr/thaumcraft/common/config/ConfigRecipes.java:119`; original method `ConfigRecipes.initializeAlchemyRecipes`.
- Observed: `src/main/java/thaumcraft/common/config/recipes/ConfigRecipesCrucibleSlice.java:240-244` registers `VoidSeed` as a crucible recipe with output `new ItemStack(ConfigItems.itemResource, 1, 17)`, research key `VOIDMETAL`, catalyst `new ItemStack(Items.ENDER_PEARL)`, and aspects `DARKNESS 8`, `VOID 8`, `ELDRITCH 2`.
- Expected: TC4 registers the same output, research key, and aspect list, but its catalyst is `new ItemStack(Items.field_151014_N)`. MCP mapping evidence resolves `field_151014_N` to `WHEAT_SEEDS`: `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/fields.csv:1905` contains `field_151014_N,WHEAT_SEEDS,2`, while `srg-mcp.srg:7865` maps `net/minecraft/init/Items/field_151014_N` to `net/minecraft/init/Items/WHEAT_SEEDS`.
- Exact deltas: Catalyst only: TC4 wheat seeds, count 1, default metadata 0, no NBT; port ender pearl, count 1, default metadata 0, no NBT. Output `itemResource:17 x1` with no NBT, research key `VOIDMETAL`, crucible recipe type, and aspect costs are identical.
- Corroboration: The same original `ConfigRecipes.class` uses `Items.field_151079_bi` for actual ender pearls in `InfusionMatrix`, `FocusPortableHole`, `Mirror`, and `MirrorEssentia` (`run/recon-r4-cfr/thaumcraft/common/config/ConfigRecipes.java:221,339,360,362`), so `field_151014_N` is not an obfuscated ender-pearl reference. The port maps those `field_151079_bi` uses to `Items.ENDER_PEARL`.
- Affected paths/symbols: `ConfigRecipesCrucibleSlice.initializeCrucibleRecipeBaseline`, local `recipeVoidSeed`; downstream handle key `VoidSeed`; `ELDRITCHMINOR` recipe page.
- Gameplay effect: Players must consume a materially rarer ender pearl instead of one wheat seed to create each Void Seed. This changes the entry cost of the Void Metal progression and can block early access despite all essentia costs remaining correct.
- Evidence/reproduction: Decompile the original method with the CFR command above; inspect original line 119, port lines 240-244, and mapping line 1905.
- Regression hazards: A correction must change only the catalyst. Preserve output meta 17/count 1/no NBT, research `VOIDMETAL`, `DARKNESS 8 + VOID 8 + ELDRITCH 2`, the `VoidSeed` handle, and its `ELDRITCHMINOR` page. Do not replace the catalyst with an ore key or wildcard stack. Keep `VoidMetal` consuming `itemResource:17` separately.
- Candidate disposition: required under the charter's `confirmed_in_scope` policy.

### A-006-F02 — No focused guard covers the VoidSeed catalyst

- Type: test_debt
- Severity: low
- Confidence: high
- Source/oracle locator: S-005 test corpus under `src/test/java/thaumcraft/common/config/`.
- Observed: Existing static guards cover target key presence, handle type/coverage, and late-bound special-recipe publication. No test asserts that `VoidSeed` uses `Items.WHEAT_SEEDS`, and no runtime crucible test exercises the catalyst.
- Expected: Minimum regression evidence for A-006-F01 should distinguish wheat seeds from ender pearls while retaining the exact output, key, and aspects.
- Exact deltas: Missing catalyst assertion/behavior test; existing tests can pass with the current incorrect `Items.ENDER_PEARL` input.
- Affected paths/symbols: `ConfigRecipesCrucibleSlice.initializeCrucibleRecipeBaseline`; likely focused config-recipe parity test surface.
- Evidence/reproduction: Search `src/test/**/*.java` for `VoidSeed` combined with `WHEAT_SEEDS` or `ENDER_PEARL`; no match. `ConfigRecipesReferenceKeyCorpusStaticGuardTest` checks only the `VoidSeed` key.
- Regression hazards: A brittle source-only assertion could pass without proving the registered `CrucibleRecipe` catalyst. Prefer the smallest test capable of inspecting the produced recipe when practical; do not broaden into item runtime.
- Candidate disposition: required only as minimum verification evidence for A-006-F01; not an independent gameplay change.

## Positive Parity

All values below match TC4. Unless explicitly stated, outputs and concrete `ItemStack` inputs have count 1, default metadata 0, and no NBT.

| Handle | Verified parity |
| --- | --- |
| `VoidMetal` | Crucible recipe; research `VOIDMETAL`; output `itemResource:16`; catalyst `itemResource:17`; `METAL 8`. Port: `ConfigRecipesCrucibleSlice.java:234-238`; original: decompiled `ConfigRecipes.java:118`. |
| `WandCapVoidInert` | Shaped arcane recipe; research `CAP_void`; output `itemWandCap:8`; pattern `NNN` / `N N`; `N = nuggetVoid`; vis formula `ENTROPY capCost*3`, `ORDER capCost*3`, `FIRE capCost*2`, `AIR capCost*2`. With the registered Void cap craft cost 9 (`Thaumcraft.java:369`), effective costs are 27, 27, 18, 18. Port: `ConfigRecipesArcaneSlice.java:305-312`; original: decompiled `ConfigRecipes.java:237`. |
| `WandCapVoid` | Infusion recipe; research `CAP_void`; output `itemWandCap:7`; instability 8; center `itemWandCap:8`; four ordered components of `itemResource:14`; aspects `ENERGY`, `VOID`, `ELDRITCH`, and `AURA`, each `capCost*2` (18 each at craft cost 9). Port: `ConfigRecipesInfusionSlice.java:45-56`; original: decompiled `ConfigRecipes.java:329`. |
| `VoidRobeHelm` | Infusion recipe; research `ARMORVOIDFORTRESS`; output `itemHelmVoidRobe`; instability 6; center `itemHelmVoid`; ordered components `itemGoggles`, fabric `itemResource:7`, fabric, `itemResource:14`, fabric, fabric; aspects `METAL 16`, `SENSES 16`, `ARMOR 16`, `CLOTH 16`, `MAGIC 16`, `ELDRITCH 16`, `VOID 16`. Port: `ConfigRecipesInfusionEquipmentSlice.java:129-134`; original: decompiled `ConfigRecipes.java:382`. |
| `VoidRobeChest` | Infusion recipe; research `ARMORVOIDFORTRESS`; output `itemChestVoidRobe`; instability 6; center `itemChestVoid`; ordered components `itemChestRobe`, `itemResource:16`, `itemResource:2`, `itemResource:14`, `itemResource:7`, leather; aspects `METAL 24`, `ARMOR 24`, `CLOTH 24`, `MAGIC 16`, `ELDRITCH 16`, `VOID 24`. Port: `ConfigRecipesInfusionEquipmentSlice.java:136-141`; original: decompiled `ConfigRecipes.java:383`. |
| `VoidRobeLegs` | Infusion recipe; research `ARMORVOIDFORTRESS`; output `itemLegsVoidRobe`; instability 6; center `itemLegsVoid`; ordered components `itemLegsRobe`, `itemResource:16`, `itemResource:2`, `itemResource:14`, `itemResource:7`, leather; aspects `METAL 20`, `ARMOR 20`, `CLOTH 20`, `MAGIC 16`, `ELDRITCH 16`, `VOID 20`. Port: `ConfigRecipesInfusionEquipmentSlice.java:143-148`; original: decompiled `ConfigRecipes.java:384`. |
| `VoidHelm` | Normal shaped ore recipe; output `itemHelmVoid`; pattern `III` / `I I`; `I = ingotVoid`. Port: `ConfigRecipesSpecialSlice.java:536-544`; original: decompiled `ConfigRecipes.java:468`. |
| `VoidChest` | Normal shaped ore recipe; output `itemChestVoid`; pattern `I I` / `III` / `III`; `I = ingotVoid`. Port: `ConfigRecipesSpecialSlice.java:546-555`; original: decompiled `ConfigRecipes.java:469`. |
| `VoidLegs` | Normal shaped ore recipe; output `itemLegsVoid`; pattern `III` / `I I` / `I I`; `I = ingotVoid`. Port: `ConfigRecipesSpecialSlice.java:557-566`; original: decompiled `ConfigRecipes.java:470`. |
| `VoidBoots` | Normal shaped ore recipe; output `itemBootsVoid`; pattern `I I` / `I I`; `I = ingotVoid`. Port: `ConfigRecipesSpecialSlice.java:568-576`; original: decompiled `ConfigRecipes.java:471`. |
| `VoidShovel` | Normal shaped ore recipe; output `itemShovelVoid`; pattern `I` / `S` / `S`; `I = ingotVoid`, `S = stickWood`. Port: `ConfigRecipesSpecialSlice.java:578-588`; original: decompiled `ConfigRecipes.java:472`. |
| `VoidPick` | Normal shaped ore recipe; output `itemPickVoid`; pattern `III` / ` S ` / ` S `; `I = ingotVoid`, `S = stickWood`. Port: `ConfigRecipesSpecialSlice.java:590-600`; original: decompiled `ConfigRecipes.java:473`. |
| `VoidAxe` | Normal shaped ore recipe; output `itemAxeVoid`; pattern `II` / `SI` / `S `; `I = ingotVoid`, `S = stickWood`. Port: `ConfigRecipesSpecialSlice.java:602-612`; original: decompiled `ConfigRecipes.java:474`. |
| `VoidHoe` | Normal shaped ore recipe; output `itemHoeVoid`; pattern `II` / `S ` / `S `; `I = ingotVoid`, `S = stickWood`. Port: `ConfigRecipesSpecialSlice.java:614-624`; original: decompiled `ConfigRecipes.java:475`. |
| `VoidSword` | Normal shaped ore recipe; output `itemSwordVoid`; pattern `I` / `I` / `S`; `I = ingotVoid`, `S = stickWood`. Port: `ConfigRecipesSpecialSlice.java:626-636`; original: decompiled `ConfigRecipes.java:476`. |

### Handle and Page Semantics

- Original normal recipes are `ShapedOreRecipe` instances returned by `ConfigRecipes.oreDictRecipe`, registered in the crafting manager, and stored under the nine exact `Void*` keys. The port registers equivalent `ShapedOreRecipe` instances in the Forge 1.12 registry and captures those same instances through `SpecialRecipesBridge.addSpecialResearchRecipeHandle`.
- Forge registry events occur before post-initialization. `ConfigRecipes.init()` then republishes `specialResearchRecipeHandles` into `ConfigResearch.recipes` before `ConfigResearch.init()` constructs research pages. The pages therefore receive registered `IRecipe` objects, not fallback or detached recipe instances.
- `VoidMetal` and `VoidSeed` are returned from the crucible slice as typed handles and republished under their exact keys. Arcane and infusion targets are directly published by their registration helpers. Typed lookups in `ConfigResearch` enforce `CrucibleRecipe`, `IArcaneRecipe`, `InfusionRecipe`, and `IRecipe` page expectations.
- Eldritch page ordering matches TC4 exactly: `ELDRITCHMINOR` shows `VoidSeed`; `VOIDMETAL` shows `VoidMetal`, then Axe, Sword, Pick, Shovel, Hoe, Helm, Chest, Legs, Boots; `CAP_void` shows inert then infused cap; `ARMORVOIDFORTRESS` shows Helm, Chest, Legs.
- The relative order of all nine normal Void recipes is preserved. The crucible slice registers `VoidMetal` and `VoidSeed` later among alchemy recipes than TC4 did, but no other crucible recipe in the inspected port uses either exact catalyst. This ordering delta does not alter recipe selection or page binding and is therefore not a behavioral discrepancy.
- 1.12 registry names on normal recipes are required platform adaptation. They do not change output, matching, or research handle identity.

## Hazards and Test Gaps

- A-006-F01 is easy to miss because the original catalyst remains obfuscated in CFR output and the port compiles successfully with the semantically wrong readable constant.
- Recipe-key and handle-coverage guards do not validate constructor arguments. All current key/handle tests can pass while `VoidSeed` consumes the wrong item.
- Component order was checked for infusion recipes because it controls pedestal sequence/display semantics; all four target infusion recipes preserve it.
- Ore dictionary strings and shaped whitespace were checked literally for all nine normal recipes and the inert cap. Replacing these with concrete items, wildcards, or trimmed patterns would regress accepted alternatives or shape.
- No target output or input carries NBT in TC4. Adding NBT or wildcard metadata would be a behavioral expansion.
- No runtime smoke or manual crafting evidence was gathered. Static/decompile evidence is sufficient to identify the catalyst defect and constructor parity, but not to prove current runtime registration or UI presentation.
- Item runtime remains unaudited here by design and must not be inferred from recipe parity.

## Unknowns and Conflicts

- None. The `field_151014_N` mapping is resolved with direct SRG/MCP evidence and corroborated by the original's distinct ender-pearl field.

## Handoff

- Terminal status: complete
- Material finding index: `A-006-F01` medium/high-confidence defect; `A-006-F02` low/high-confidence test debt; 15 positive-parity recipe definitions; all 16 handle/page bindings verified.
- Exact continuation point: Normalize A-006-F01 and A-006-F02 into `RECON.md`; preserve the 15 matched definitions and exact page ordering as positive controls.
- Smallest next action if promoted: Change only the `VoidSeed` catalyst from `Items.ENDER_PEARL` to `Items.WHEAT_SEEDS`, add focused regression evidence for the registered catalyst, then run the common/server validation required by the frozen goal.
