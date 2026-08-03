# Normalized Evidence: A-026..A-029

Goal-ID: `goal-20260803-eldritch-full-parity-audit-fixes`
Normalization date: 2026-08-03
Scope: evidence-only normalization; no product, report, `RECON.md`, `GOAL.md`, `SOURCES.md`, or central-ledger edits.

## Authority and Method

- Source order: user instruction, `SOURCES.md`, repository instructions/contracts, then the frozen goal ledger. The comparison oracle is S-003 (`Thaumcraft-1.7.10-4.2.3.5.jar`, SHA-256 `3dba9786966974701578a658d1bb369bf35bdf5f363079f5ac9c4910a39113be`); S-004 is its extracted read-only tree; S-005 is the port; S-007 is advisory Forge/TC6 adaptation evidence only.
- Report packets: `reports/A-026-research-page-rendering.md`, `reports/A-027-crafting-visibility.md`, `reports/A-028-localization-loader.md`, and `reports/A-029-scan-engine.md`.
- Normalized IDs below are local to this evidence file and do not amend the central `RECON.md`. Report IDs and report paths remain the authoritative locators.
- Disposition vocabulary follows the goal schema: `required`, `preserve`, `constraint`, `deferred`, `invalidated`, `duplicate`, `blocking_question`.

## Finding Index

| Local ID | Report locator | Type | Recommended severity | Recommended disposition | Outcome group |
|---|---|---|---|---|---|
| F-026-01 | A-026-F01 | defect | high | required | R-PAGE-01 |
| F-026-02 | A-026-F02 | defect | medium | required | R-PAGE-02 |
| F-026-03 | A-026-F03 | defect | medium | required | R-PAGE-03 |
| F-026-04 | A-026-F04 | defect | medium | required | R-PAGE-04 |
| F-026-05 | A-026-F05 | defect | low | required | R-PAGE-05 |
| F-026-06 | A-026-F06 | defect | low | required | R-PAGE-06 |
| F-026-07 | A-026-F07 | defect | low | required | R-PAGE-07 |
| F-026-08 | A-026-F08 | defect | low | required | R-PAGE-08 |
| F-027-01 | A-027-F01 | benign_delta / hidden-valid recipe | moderate | deferred | R-CRAFT-01 |
| F-027-02 | A-027-F02 | benign_delta / platform edge | low | constraint | R-CRAFT-02 |
| F-027-03 | A-027-F03 | parity | informational | preserve | PC-CRAFT-01 |
| F-027-04 | A-027-F04 | parity | informational | preserve | PC-CRAFT-02 |
| F-028-01 | A-028-F01 | defect | high | required | R-LOC-01 |
| F-028-02 | A-028-F02 | defect | low | required | R-LOC-02 |
| F-028-03 | A-028-P01..P06 | parity / constraint | informational | preserve / constraint | PC-LOC-01 |
| F-029-01 | A-029-F01 | defect | high | required | R-SCAN-01 |
| F-029-02 | A-029-F02 | defect | medium | required | R-SCAN-02 |
| F-029-03 | A-029-F03 | defect | medium | deferred | R-SCAN-03 |
| F-029-04 | A-029 bounded delta 1 | benign_delta | low / unknown reach | deferred | R-SCAN-04 |
| F-029-05 | A-029 bounded delta 2 | benign_delta | low / addon-dependent | deferred | R-SCAN-04 |
| F-029-06 | A-029 bounded delta 3 | benign_delta | low / unknown reach | deferred | R-SCAN-04 |
| F-029-07 | A-029 bounded delta 4 | benign_delta | low / unknown reach | deferred | R-SCAN-04 |
| F-029-08 | A-029 bounded delta 5 | benign_delta | low / unknown reach | deferred | R-SCAN-04 |
| F-029-09 | A-029 parity controls | parity | informational | preserve | PC-SCAN-01 |

## A-026 Page Rendering

### F-026-01 Arcane vis row truncation

- Type/disposition/severity/confidence: defect; `required`; high; high.
- Sources/reports: S-003/S-004/S-005; A-026, `reports/A-026-research-page-rendering.md`, A-026-F01.
- Oracle: TC4 `GuiResearchRecipe.java:545-560` iterates the full aspect collection.
- Observed: `src/main/java/thaumcraft/client/gui/GuiResearchRecipe.java:293,500-515` calls `drawAspectCostRow` with `perRow = 5` and `visible = Math.min(perRow, aspects.size())`. `FOCUSPRIMAL` page 2 has six aspects at 25 vis each; only five icons render and the displayed total is 125.
- Expected: all six 25-vis entries render with the TC4 placement behavior and complete 150-vis requirement.
- Exact delta: 6 entries/150 vis required versus 5 entries/125 vis displayed; one icon is omitted.
- Hazards: retain existing arcane layout and all other page types. No renderer test counts entries/totals. Manual smoke: client, open Eldritch `FOCUSPRIMAL` page 2, compare six icons and 150 vis with TC4.
- Outcome: R-PAGE-01; focused renderer assertion plus manual client comparison.

### F-026-02 Recipe click-through/history/return absent

- Type/disposition/severity/confidence: defect; `required`; medium; high.
- Sources/reports: S-003/S-004/S-005; A-026, A-026-F02.
- Observed: `GuiResearchRecipe.java:818-853` renders ordinary tooltips but has no recipe history/reference lookup, click-through, or return control and no `recipe.clickthrough`/`recipe.return` behavior. Affected pages: `OCULUS` p2; `ADVALCHEMYFURNACE` p2,p4; `PRIMALCRUSHER` p2; `VOIDMETAL` p2,p4-p12; `ESSENTIARESERVOIR` p2; `CAP_void` p2-p3; `ARMORVOIDFORTRESS` p2-p4; `FOCUSPRIMAL` p2; `SANITYCHECK` p2; `ROD_primal_staff` p2.
- Oracle: TC4 `GuiResearchRecipe.java:407-413` adds click-through tooltip; `1059-1087` calls `ThaumcraftApi.getCraftingRecipeKey`, stores history, and navigates; `1240-1275` renders/handles return. Port API remains at `ThaumcraftApi.java:177-214`.
- Expected/delta: resolvable recipe hover exposes TC4 action, click opens referenced research, history is retained, and return restores the original page; current behavior supplies none of these four interactions.
- Hazards: preserve existing recipe rendering and typed lookup. No navigation/history test. Manual smoke: hover/click affected input/output, open target, use return, verify original page restored.
- Outcome: R-PAGE-02.

### F-026-03 Compound ground overlay transform

- Type/disposition/severity/confidence: defect; `required`; medium; high.
- Sources/reports: S-003/S-004/S-005; A-026-F03.
- Observed: `GuiResearchRecipe.java:404` uses `drawOverlayScaled` at `y + 78 + max(3-dx,3-dz)*8 + dx*4 + dz*4`, with no `dy`. For `dx=3,dz=3`, result is `y+102`.
- Oracle/expected: TC4 applies `y + 108 + yoff*(1-sz)`, then `-119 + max(3-dx,3-dz)*8 + dx*4 + dz*4 + dy*50` at scale 1 for `dy <= 3`; this page's ground overlay is `y+63`. Port is exactly `+39` px at ground and cannot represent vertical-layer displacement.
- Hazards/manual debt: retain structure-layer transforms. No coordinate assertion or screenshot. Manual smoke: inspect `ADVALCHEMYFURNACE` p4 at each compound layer against TC4.
- Outcome: R-PAGE-03.

### F-026-04 Normal workbench element overlay missing

- Type/disposition/severity/confidence: defect; `required`; medium; high.
- Sources/reports: S-003/S-004/S-005; A-026-F04.
- Observed: all nine `VOIDMETAL` normal recipes, p4-p12, draw only the normal workbench backing at `GuiResearchRecipe.java:254`.
- Oracle/expected: TC4 draws the 52x52 backing plus a 16x16 element overlay from UV/source `(20,12)` (`GuiResearchRecipe.java:672-680`). `(20,7)` is arcane and must not be substituted. Exact delta: one missing 16x16 overlay on each of nine pages.
- Hazards/manual debt: preserve normal-vs-arcane UV distinction. No overlay/screenshot test. Manual smoke: inspect all nine pages against TC4.
- Outcome: R-PAGE-04.

### F-026-05 Research text color

- Type/disposition/severity/confidence: defect; `required`; low; high.
- Sources/reports: S-003/S-004/S-005; A-026-F05.
- Observed: `GuiResearchRecipe.java:568` passes `0x303030` for ordinary lines.
- Oracle/expected: TC4 `drawTextPage` passes color `0`; `TCFontRenderer.java:396-410` normalizes 0 to opaque black (`GuiResearchRecipe.java:1232-1237`). Exact delta: `0x303030` versus opaque black/TC4 color `0`.
- Hazards/manual debt: markup layout remains aligned; no pixel/color test. Manual smoke: compare representative Eldritch text pages at native GUI scale.
- Outcome: R-PAGE-05.

### F-026-06 Navigation and arcane output hitboxes

- Type/disposition/severity/confidence: defect; `required`; low; high.
- Sources/reports: S-003/S-004/S-005; A-026-F06.
- Observed: `GuiResearchRecipe.java:104-120` accepts 12x8 arrows, left `left-16..left-4`, right `right+262..right+274`, and `:150-157` renders fixed arrows without TC4 bobbing/page sound. Arcane output hover tests `y+22..y+38`.
- Oracle/expected: TC4 arrow hitboxes are 14x10 at TC4 positions with bobbing and page-change sound; output is drawn at `y+22` but hover is `y+27..y+43` (`GuiResearchRecipe.java:567,572-574`). Exact deltas: arrow width 12 versus 14, height 8 versus 10; output hover top is 5 px too high and bottom is 5 px too high; feedback lacks bobbing and sound.
- Affected: multi-spread `OCULUS`, `ADVALCHEMYFURNACE`, `PRIMALCRUSHER`, `VOIDMETAL`, `ESSENTIARESERVOIR`, `CAP_void`, `ARMORVOIDFORTRESS`; arcane outputs `ADVALCHEMYFURNACE` p2, `CAP_void` p2, `FOCUSPRIMAL` p2.
- Manual debt: boundary-click arrows and hover arcane outputs across 5-pixel boundary; no boundary/bobbing/sound test.
- Outcome: R-PAGE-06.

### F-026-07 Inline image geometry

- Type/disposition/severity/confidence: defect; `required`; low; high arithmetic, low user-visible severity.
- Sources/reports: S-003/S-004/S-005; A-026-F07.
- Observed: `ELDRITCHMAJOR` p1-p2 port rounds half-scale 255x255 to 128x128 with `Math.round`, centers via `x + (PAGE_WIDTH-drawWidth)/2`, and advances from rounded bottom.
- Oracle/expected: TC4 `TCFontRenderer` retains 127.5 logical pixels at 0.5 scale, uses `par2 - 3 + width/2 - sourceWidth/2*scale`, and advances `sourceHeight*scale - FONT_HEIGHT`. Exact delta: port omits `-3`, so image is about +2.5 px right; rounded 128 versus 127.5 causes about +1 px flow difference.
- Manual debt: compare both pages at supported GUI scales via screenshots/pixel capture; no geometry/flow test.
- Outcome: R-PAGE-07.

### F-026-08 Damageable wildcard cycle bound

- Type/disposition/severity/confidence: defect; `required`; low; high.
- Sources/reports: S-003/S-004/S-005; A-026-F08.
- Observed: `src/main/java/thaumcraft/common/lib/utils/InventoryUtils.java:472-475` uses `System.currentTimeMillis()/10 % (it.getMaxDamage()+1)`, producing `0..maxDamage`.
- Oracle/expected: TC4 `InventoryUtils.java:490-495` uses modulus `maxDamage`, producing `0..maxDamage-1`. Exact delta: inclusive upper bound in port versus exclusive upper bound in TC4; invalid metadata equal to max can display.
- Affected: `PRIMALCRUSHER` p2 wildcard pickaxe/shovel components, including void/elemental variants. Test debt: deterministic exact-max boundary test absent. Manual smoke: observe repeated cycle boundaries or add deterministic boundary proof.
- Outcome: R-PAGE-08.

## A-027 Crafting Visibility and Enforcement

### F-027-01 Dynamic JEI recipes are hidden-valid, not an enforcement defect

- Type/disposition/severity/confidence: `benign_delta`; `deferred`; moderate; high.
- Sources/reports: S-003/S-004/S-005; A-027-F01, `reports/A-027-crafting-visibility.md:69-81`.
- Observed: `JeiRecipeData.java:114-148` adapts shaped/shapeless arcane recipes but skips other `IArcaneRecipe` implementations. `ArcaneWandRecipe.java:88-119` and `ArcaneSceptreRecipe.java:99-139` have dynamic NBT/tagged outputs and empty `getRecipeOutput()`, so no custom JEI wrappers are registered.
- Expected/current accepted behavior: combinations remain craftable and research-gated; custom JEI has no entries. Void-cap wand and `ROD_primal_staff` combinations are absent from browsing. This is discoverability, not a server bypass.
- Exact controls to preserve if revisited: dynamic output NBT, cap/rod research checks, vis calculation, optional client boundary. Sceptres also require `SCEPTRE`.
- Reproduction/manual debt: complete relevant cap/rod research, craft in Arcane Workbench with vis, verify craft succeeds while custom JEI entry is absent. No test documents intentional omission or dynamic-wrapper contract.
- Disposition reason: deferred until an explicit JEI behavior decision; do not infer a product fix from the hidden-valid result.
- Outcome: R-CRAFT-01 if promoted later; no current implementation outcome.

### F-027-02 `doLimitedCrafting` Arcane Workbench platform edge

- Type/disposition/severity/confidence: `benign_delta` with constraint; `constraint`; low; high for Arcane Workbench path.
- Sources/reports: S-003/S-004/S-005; A-027-F02, `reports/A-027-crafting-visibility.md:83-94`.
- Observed: `ArcaneWorkbenchRecipeResolver.java:24-31,54-59` resolves ordinary recipes through the 1.12 recipe-book check when `doLimitedCrafting` is enabled. Nine Void armor/tool recipes in `ConfigRecipesSpecialSlice.java:536-636` are ordinary `ShapedOreRecipe`s with no research key.
- Oracle/expected: TC4 registered the same ordinary recipes directly in its global crafting manager and had no 1.12 recipe-book gate. Exact conditional delta: with `doLimitedCrafting=true` and no recipe-book unlock, port Arcane Workbench rejects a valid ordinary Void pattern; TC4 accepts it. No Thaumcraft research requirement is added.
- Constraint/hazard: decide separately whether compatibility policy permits bypassing 1.12 recipe-book gating. Preserve normal crafting-table behavior, which was not runtime-tested. No product fix is authorized by this evidence alone.
- Test debt/manual smoke: limited-crafting Arcane Workbench plus ordinary crafting-table control; fresh player lacking recipe unlock.
- Outcome: R-CRAFT-02 only after policy decision; otherwise constraint remains.

### F-027-03 Normal Void page handles versus craftability

- Type/disposition/severity/confidence: parity; `preserve`; informational; high.
- Sources/reports: S-003/S-004/S-005; A-027-F03, `:96-105`.
- Observed/expected: nine ordinary `ShapedOreRecipe`s under `VoidHelm` through `VoidSword` are displayed under `VOIDMETAL`, but carry no research key and are not checked by Thaumcraft matchers. Page handles may be hidden by `ThaumcraftApi.getCraftingRecipeKey` before `VOIDMETAL`, while a player with Void ingots can craft regardless of research.
- Exact parity delta: page visibility and ordinary craftability intentionally diverge; custom `ResearchVisibility` hides only custom arcane/crucible/infusion wrappers, not these normal recipes.
- Hazard: never convert page handles into research gates without an explicit behavior change.
- Control: PC-CRAFT-01.

### F-027-04 Thaumatorium persisted automation

- Type/disposition/severity/confidence: parity; `preserve`; informational; high.
- Sources/reports: S-003/S-004/S-005; A-027-F04, `:107-118`.
- Observed/expected: `ContainerThaumatorium.java:72-86,90-116` and `TileThaumatorium.java:95-159` require current-player research and catalyst during programming. After recipe hash, essentia requirements, and programmer name persist, execution does not recheck the current player's research, matching TC4 `ContainerThaumatorium.java:61-101` and `TileThaumatorium.java:183-242`.
- Exact lifecycle control: Player A can program; Player B without research can execute stored recipe. Preserve hash persistence, stored essentia/player arrays, catalyst/aspect validation, and programming gate.
- Test debt: no runtime cross-player test. Control: PC-CRAFT-02.

### F-027-05 Crafting matrix and enforcement controls

- Type/disposition/severity/confidence: parity; `preserve`; informational; high.
- Sources/reports: A-027 report `:47-65,120-141`; S-003/S-005.
- Exact recipe-key parity: `VOIDMETAL` (VoidSeed, VoidMetal), `ADVALCHEMYFURNACE`, `CAP_void` (inert/finished caps), `FOCUSPRIMAL`, `OCULUS`, `PRIMALCRUSHER`, `SANITYCHECK`, `ESSENTIARESERVOIR`, `ARMORVOIDFORTRESS`, `ROD_primal_staff`.
- Enforcement controls: arcane matchers gate player research; infusion matchers gate before central/component matching; crucible copies catalyst count one, checks thrower research, then aspect/catalyst and most-specific match; TileCrucible obtains entity thrower and falls back to thrower NBT; Thaumatorium gates programming only.
- Visibility controls: `ThaumcraftJeiPlugin` tracks nonempty custom keys; `ResearchVisibility` hides tracked wrappers and unhides exact client-capability keys; packet sync updates only client visibility; server crafting never trusts it. Wildcard/NBT display matching and dynamic JEI alternatives do not mutate source stacks.
- Control: PC-CRAFT-03. Material test debt: representative locked/unlocked matrix across arcane, crucible, infusion, normal, wildcard-output; Primal Crusher wildcard execution and JEI alternatives.

## A-028 Localization Loader and Packaging

### F-028-01 `%n` formatting defect

- Type/disposition/severity/confidence: defect; `required`; high; high.
- Sources/reports: S-003/S-004/S-005; A-028-F01, `reports/A-028-localization-loader.md:16-26`.
- Observed: `src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java:570-572` calls `net.minecraft.client.resources.I18n.format("tc.forbidden")`, then replaces `%n` with the translated warp level. `assets/thaumcraft/lang/en_us.lang:842-847` contains `Forbidden knowledge (%n)` with formatting codes.
- Exact runtime delta: Forge 1.12 `I18n.format` delegates to `Locale.formatMessage`, always Java `String.format`; `%n` becomes the platform line separator before `.replace("%n", ...)`. Output becomes `Forbidden knowledge (` + newline + `)` and omits `Mostly Harmless`-style level text.
- Oracle/expected: TC4 raw `StatCollector.translateToLocal("tc.forbidden")`, separately translates level, then replaces literal `%n`. Required local pattern is demonstrated by `ItemResearchNotes.java:100-105` and `ClientProxy.localizeOrFallback`.
- Manual debt: client with a warped Eldritch entry, hover browser entry, verify literal token replacement and localized level. Existing tests do not exercise browser warp path.
- Outcome: R-LOC-01.

### F-028-02 Uppercase locale alias can overwrite resource-pack override

- Type/disposition/severity/confidence: defect; `required`; low; high static evidence, no live repro.
- Sources/reports: S-003/S-005; A-028-F02, `:28-38`.
- Build/package observed: `build.gradle:106-122` copies normal resources, then copies `assets/thaumcraft/lang/en_us.lang` again as `en_US.lang`. Generated resources contain both exact files.
- Exact loader chain: Forge 1.12 `LanguageManager.onResourceManagerReload` constructs locale list `[en_us, currentLanguage]` when values differ. `Locale.loadLocaleDataFiles` requests exact `lang/%s.lang` paths. On case-sensitive ZIP/resource-pack lookup, with reachable `currentLanguage=en_US`, lowercase pass may load a pack override and uppercase pass then loads the mod's byte-identical `en_US.lang`, overwriting it. A pack with its own uppercase alias avoids this specific overwrite; standard `en_us` is unaffected.
- Expected/constraint: standard Forge convention is lowercase locale files; original TC4 used `en_US.lang`. Preserve intentional compatibility only if its precedence is accepted; otherwise remove alias or define an explicit policy. `GameSettings`/`LanguageManager` preserve raw `en_US`, so the path is reachable.
- Validation/package facts: `processResources` passed; lowercase and uppercase generated files each 187408 bytes, SHA-256 `e20d724086509c54a814d704c62eb683c712a79dfd6eefa1ae91c3a6778f585d`; `jar` passed and contains both locale entries and both lowercase Eldritch image entries. No resource-pack precedence test.
- Manual debt: set `options.txt` `lang:en_US`, enable pack overriding lowercase Eldritch key, reload, observe which value wins; test case-sensitive filesystem.
- Outcome: R-LOC-02.

### F-028-03 Loader, language, image, and build controls

- Type/disposition: parity/constraint; `preserve` for verified behavior and `constraint` for packaging policy.
- Sources/reports: A-028 positive controls `:40-46`; `build.gradle:31-40,106-122`; `Dockerfile:5-9,64-86`; S-005/S-007 where applicable.
- Exact controls: `LanguageManager` loads `en_us` first then selected locale and tolerates missing files; `.lang` parsing is UTF-8, splits only first `=`, preserves Unicode/formatting, returns key when missing; source is BOM-free UTF-8 with raw section-sign codes. Raw translation is used by `ResearchCategories.java:25-26`, `ResearchItem.java:189-195`, and `ResearchPage.java:106-111`. `<IMG>` seven-field syntax and `ResourceLocation` binding remain at `GuiResearchRecipe.java:561-679`; `textures/misc/eldritchajor1.png` and `eldritchajor2.png` are lowercase and exist in source/JAR, both source images 256x256 RGBA and byte-identical to source. Eldritch prose has no printf token and uses raw translation.
- Build/package contract: Java 8 (`sourceCompatibility = targetCompatibility = '1.8'`); Forge `1.12.2-14.23.5.2847`, mappings `stable_39`; `processResources` expands `mcmod.info` with advertised `6.1.BETA26`, copies `mcmod.info` and other resources, and explicitly aliases `en_us.lang` to `en_US.lang`; universal JAR is `jar` classifier; API JAR includes `thaumcraft/api/**`; dev JAR includes classes and sources; main JAR is reobfuscated while API/dev remain readable. Docker uses `eclipse-temurin:8-jdk`, ForgeGradle-compatible Gradle wrapper guard, CFR `/usr/local/bin/cfr`, and mounted `/workspace/thaumcraft` as non-root `thaum`.
- Required/forbidden constraint: do not silently alter locale names, mod/resource identifiers, Java/Forge/build versions, or API packaging while addressing loader behavior.
- Control: PC-LOC-01.

## A-029 Scan Engine

### F-029-01 Built-in phenomenon handler intercepts later handlers

- Type/disposition/severity/confidence: defect; `required`; high; high.
- Sources/reports: S-003/S-004/S-005; A-029-F01, `reports/A-029-scan-engine.md:16-27`.
- Exact TC4 scan-chain precedence: `Thaumcraft.java` registers `new ScanManager()`; TC4 `ScanManager.scanPhenomena(ItemStack, World, EntityPlayer)` returns `null` unconditionally (pass-through); `ItemThaumometer` continues to the next handler when result is null.
- Port delta: `src/main/java/thaumcraft/common/lib/research/ScanManager.java:47-60` returns null only for empty held stack; otherwise calls `scanItem`, creates type-1 result, and immediately `completeScan(player,result,"@")`. `src/main/java/thaumcraft/common/Thaumcraft.java:110-112` registration plus `ItemThaumometer.java:111-117,205-211` means `findRawScanTarget` stops at first non-null. `doActiveScan` rejects it because the held thaumometer has just been recorded as scanned.
- Exact effect: first fallback handler mutates client scan state while failing actual scan; later phenomenon handlers never receive target. Same result can enter server authority validation via server target lookup. For `BlockEldritchNothing.java:78-90`, empty pick/drop can fall through to phenomena; later Outer Lands/addon handler is starved. Held thaumometer is tagged at `ConfigAspects.java:424`; explicit End Portal bridge `ItemThaumometer.java:182-185` bypasses generic path.
- Expected: built-in phenomenon method remains side-effect-free and returns null unless a distinct phenomenon implementation is deliberately dispatched.
- Test/manual debt: assert null/no state mutation/later-handler reach; no manual in-game scan reproduction. Separate F-029-02 reproduction from handler list.
- Outcome: R-SCAN-01.

### F-029-02 Empty-aspect block feedback suppressed

- Type/disposition/severity/confidence: defect; `required`; medium; high.
- Sources/reports: S-003/S-004/S-005; A-029-F02, `:29-40`.
- Oracle: TC4 `doScan` creates type-1 block result from block pick/drop stack and metadata without requiring nonempty aspects; after timed scan `completeScan` resolves tags and `validScan` reaches invalid path, emitting unknown-object notification.
- Port: `ItemThaumometer.java:164,173,178,331-346` routes blocks through `toTaggedItemScan`, which rejects empty stack and any aspect list of size zero before returning timed target; later `validScan`/`notifyInvalidScan` is unreachable.
- Exact behavior delta: TC4 “timed scan then unknown-object feedback” versus port “no scan target, silent failure.” Eldritch `BlockEldritchNothing` returns `ItemStack.EMPTY` and air/zero drop, so it is directly affected; F-029-01 may mask first attempt.
- Expected: preserve block scan result through timed completion; aspect availability decides validity at completion, not target acquisition.
- Test/manual debt: add `BlockEldritchNothing` runtime case to `ScanProgressionRuntimeTest`; manual timed scan and unknown notification, isolated from handler ordering.
- Outcome: R-SCAN-02.

### F-029-03 First `#` scan base award suppressed

- Type/disposition/severity/confidence: defect; `deferred`; medium; high control-flow, low native reach.
- Sources/reports: S-003/S-004/S-005; A-029-F03, `:42-53`.
- Oracle: at entry to `completeScan`, TC4 evaluates `prefix.equals("#") && !isValidScanTarget(player,scan,"@")` before recording new key. First `#` receives normal base aspect amount plus `#` bonus; `#` after `@` suppresses repeated base award.
- Port delta: `ScanManager.java:189-220` records key first; `PlayerKnowledgeCapability.java:437-441` `addScanKey` records `#` and removes matching `@`; later `isValidScanTarget(...,"@")` fails for every `#`. Exact award delta: first `#` gets only `+1` bonus instead of base amount plus `+1` per eligible aspect.
- Reach/example: current `PacketScannedToServer.java:103-105` accepts only `@`, so native packet cannot reach it; public `completeScan` callers/addons/restored alternate route can. Eldritch examples: first direct/API `#` on Void Seed/Void Metal should award base/recipe-derived quantity plus one, but port awards one.
- Expected: evaluate prior `@` state before recording `#`, or retain pre-recording state.
- Test/manual debt: direct `completeScan(...,"#")` award test; no native manual reproduction unless alternate route exists.
- Disposition reason: confirmed semantic defect, but implementation is deferred because current native packet contract rejects `#`; promotion requires scope/route decision.
- Outcome: R-SCAN-03 only after reachability decision.

### F-029-04 Null generated aspect cached versus retried

- Type/disposition/severity/confidence: benign_delta/unknown; `deferred`; low, unknown native reach; medium.
- Sources/reports: S-003/S-004/S-005; A-029 bounded delta `:64-72`.
- Exact delta: TC4 registers null generated result as empty tag, permanently caching failed derivation; port `ThaumcraftCraftingManager.java:170-176` registers only non-null generated tags and retries misses. Native lifecycle initializes relevant recipes before aspects at `Thaumcraft.java:203-204`; only late-added addon recipes are implicated.
- Expected/constraint: no native Eldritch defect demonstrated; preserve native lifecycle ordering. Addon late-registration behavior is unknown.

### F-029-05 EE3 transmutation-stone ingredient exclusion absent

- Type/disposition/severity/confidence: benign_delta/unknown; `deferred`; low addon-dependent; medium.
- Sources/reports: S-003/S-004/S-005; A-029 bounded delta `:68-70`.
- Exact delta: TC4 excludes EE3 transmutation-stone ingredients through `Utils.isEETransmutionItem`; port arcane/vanilla derivation `ThaumcraftCraftingManager.java:346-403` has no equivalent. No effect on checked ordinary Void catalysts; addon-dependent.
- Expected/constraint: no Eldritch promotion absent EE3 scope/evidence.

### F-029-06 Arcane duplicate-output resolution differs

- Type/disposition/severity/confidence: benign_delta/unknown; `deferred`; low unknown reach; medium.
- Sources/reports: S-003/S-004/S-005; A-029 bounded delta `:70-71`.
- Exact delta: TC4 can let a later empty result overwrite earlier nonempty result; port retains nonempty result. No native Eldritch duplicate-output case found.

### F-029-07 Catalyst-list recursion history differs

- Type/disposition/severity/confidence: benign_delta/unknown; `deferred`; low unknown reach; medium.
- Sources/reports: S-003/S-004/S-005; A-029 bounded delta `:71-72`.
- Exact delta: TC4 chooses first catalyst-list entry and resets catalyst recursion history; port chooses first taggable alternative and shares recursion history. No native Eldritch catalyst-list case found.

### F-029-08 Entity hash state distinctions incomplete

- Type/disposition/severity/confidence: benign_delta/unknown; `deferred`; low unknown reach; medium.
- Sources/reports: S-003/S-004/S-005; A-029 bounded delta `:72-73`.
- Exact delta: TC4 entity hashes include creeper flashing, golem material, and legacy zombie-villager distinctions; port `ScanManager.java:328-343` preserves child and powered state but not those other distinctions. No directly registered Eldritch entity depends on omitted distinctions in this audit.

### F-029-09 Scan lookup and award controls

- Type/disposition/severity/confidence: parity; `preserve`; informational; high.
- Sources/reports: A-029 positive controls `:55-62`; S-003/S-005.
- Exact scan-chain/lookup precedence to preserve: exact metadata, grouped/range aliases, wildcard metadata, then derived recipe generation. Exact Eldritch block metas 3-6 override wildcard registration in `ConfigAspects.java:468-479`.
- Additional controls: damageable/non-subtyped item hashes collapse metadata as expected; entity lookup honors exact keys, matching NBT, and last-registration precedence; namespaced entities remain compatible with legacy dotted/plain trigger aliases via `ResearchManager.java:818-850`. Derived formulas remain ingredient scaling `0.75 / outputCount`, recipe essentia `floor(sqrt(amount) / outputCount)`, six-aspect culling, per-aspect cap 64, recursion protection, and recipe priority crucible then arcane then infusion then vanilla. Void Seed/Void Metal formulas (`ConfigRecipesCrucibleSlice.java:234-244`) and downstream Void equipment tags (`ConfigAspects.java:502-515`) match. Prerequisites require nonempty tags and direct compound parents; normal `@` awards/caps/clues/completion match. TC6 shims are not native TC4 scan requirements.
- Constraint: do not replace the exact precedence with TC6 scanning behavior or alter native packet prefix policy as an incidental fix.
- Control: PC-SCAN-01.

## Duplicate and Outcome Adjudication

### Duplicates

- No A-026..A-029 defect is a duplicate of another local defect. A-029-F02 is related to A-029-F01: F-029-01 can intercept the first attempt and mask F-029-02, but their causes and fixes differ, so both remain atomic.
- A-027-F01 is not a duplicate of normal recipe visibility in A-027-F03: dynamic `IArcaneRecipe` omission is a JEI wrapper omission; normal Void page/craft divergence is intentional TC4 semantics.
- A-028-F02 is not a duplicate of F-028-03: the former is the concrete alias-overwrite defect; the latter records the loader/package controls and policy constraint.
- A-029-F03 is not invalidated by packet rejection: public/API/addon reach remains, but its implementation outcome is deferred pending route/scope policy.

### Recommended implementation outcome groups

- R-PAGE-01..08: eight independent client rendering/navigation fixes; each needs focused static/deterministic evidence, with manual client checks for visual/interaction claims. No automated client smoke is required by repository policy for client-only changes.
- R-CRAFT-01: deferred JEI product-policy decision for dynamic wand/sceptre wrappers; if retained, document intentional omission and preserve dynamic NBT/gating. R-CRAFT-02: separate decision on bypassing recipe-book gating under `doLimitedCrafting`; do not silently change ordinary crafting semantics. PC-CRAFT-01..03 are mandatory preserve controls.
- R-LOC-01: raw translation/custom-token fix in browser. R-LOC-02: choose and test locale alias precedence before changing `processResources`; preserve exact identifiers and package contract. PC-LOC-01 covers loader, resources, images, Java/Forge/Gradle/Docker, and API/main/dev JAR boundaries.
- R-SCAN-01 and R-SCAN-02: native Eldritch-facing scan fixes with targeted runtime tests and `./scripts/dev.sh validate --smoke`. R-SCAN-03: deferred alternate-prefix API decision. R-SCAN-04: bounded addon/late-lifecycle deltas remain evidence, not promoted product requirements. PC-SCAN-01 is the exact precedence/formula control.

## Validation, Runtime Smoke, and Material Test Debt

- A-026 command: `./scripts/dev.sh gradle test --tests thaumcraft.common.config.ConfigResearchRecipeLookupTypeAuditTest --tests thaumcraft.common.config.ConfigResearchStrictRecipeLookupStaticGuardTest --tests thaumcraft.client.GuiResearchRecipeStaticGuardTest --tests thaumcraft.client.ResearchRecipeWildcardDisplayStaticGuardTest --tests thaumcraft.common.clientguard.ResearchRecipeItemTooltipStaticGuardTest`; result `BUILD SUCCESSFUL`. Read-only audit; no runtime smoke or client manual validation.
- A-027 evidence: CFR extraction of listed TC4 crafting/config/container/tile classes; existing focused tests inspected; no build or runtime smoke because report-only. Required material debt: dynamic JEI omission contract, limited-crafting Arcane Workbench plus ordinary-table control, cross-player Thaumatorium runtime, Eldritch locked/unlocked recipe matrix, and Primal Crusher wildcard execution/JEI alternatives.
- A-028 commands: `./scripts/dev.sh gradle processResources`; `cmp` source to generated `en_us.lang`; `cmp` source to generated `en_US.lang`; `./scripts/dev.sh gradle jar`; focused tests `EldritchLocalizationParityTest`, `ConfigResearchStaticGraphTest`, `GuiResearchRecipeStaticGuardTest`. All passed; both generated files 187408 bytes with SHA-256 `e20d724086509c54a814d704c62eb683c712a79dfd6eefa1ae91c3a6778f585d`; JAR contains both locale and both image entries. No live resource-pack or client smoke.
- A-029 evidence: `git status --short` and `/usr/local/bin/cfr ... --silent true`; no build/test/runtime smoke/manual scan. Required material debt: side-effect-free fallback/later-handler test, empty-aspect `BlockEldritchNothing` timed invalid notification, direct first `#` award test, and addon/late-lifecycle reproductions for F-029-04..08 if their scope is opened.
- Common/server implementation gate: `./scripts/dev.sh validate --smoke` is required for scan fixes or any common/server changes affecting scan lifecycle, knowledge mutation, packets, or registration. Final build gate: `./scripts/dev.sh build`. Documentation-only normalization itself requires neither runtime smoke nor build.
- Manual client matrix still outstanding for F-026-01..08 and F-028-01/F-028-02 as enumerated in their entries. Do not claim visual or live resource-pack parity from compile/JAR success.

## Completeness Reconciliation

- A-026 is terminal `complete`; every headed claim `A-026-F01` through `A-026-F08` is represented exactly once as F-026-01..08, with all 8 index rows, numeric deltas, paths/symbols, oracle locators, test gaps, and manual checks retained.
- A-027 is terminal `complete`; headed claims `A-027-F01`..`F04` are represented as F-027-01..04. The report's recipe matrix, ten execution-key classes, server enforcement paths, JEI visibility behavior, and five-item test-debt list are retained under F-027-01..05. F-027-05 is the report's unheaded positive matrix/control material, not an invented defect.
- A-028 report assignment header says `I09` while RECON/SOURCES map it to A-028; this normalization preserves the report path and normalizes its two headed findings `A-028-F01`/`F02` to F-028-01/02. Its unheaded positive controls and package facts are represented under F-028-03. Status `needs_fix` is retained as evidence; central assignment status is not edited.
- A-029 report assignment header says `I10` while RECON/SOURCES map it to A-029; this normalization preserves the report path and normalizes headed `A-029-F01`..`F03` to F-029-01..03. All five bounded non-native deltas are represented F-029-04..08, and all positive controls/precedence claims are F-029-09. The exact scan-chain precedence is stated verbatim in operational order: exact metadata -> grouped/range aliases -> wildcard metadata -> derived recipe generation; derived recipe priority is crucible -> arcane -> infusion -> vanilla.
- Every local headed claim in the four reports is accounted for: 8 + 4 + 2 + 3 = 17 headed findings; the remaining indexed entries are explicitly labeled positive/unheaded control material or bounded deltas, not silently dropped. No report claim was silently promoted to product scope. Unknowns, constraints, benign adaptations, test debt, manual smoke gaps, duplicates, deferred outcomes, and preserve controls are all explicitly indexed above.
- Loader/build packaging is reconciled to `build.gradle` and `Dockerfile`: Forge `1.12.2-14.23.5.2847`, MCP `stable_39`, Java 8, `en_us` then selected locale loader order, explicit `en_US` alias, exact generated-file hash/size, universal/API/dev JAR boundaries, reobfuscation boundary, and Java-8 Docker/CFR/wrapper details are preserved without editing those files.
- No commit was created and no product/report/central file was changed by this normalization request. The only intended new path is this evidence file.
