# Audit Packet: A-005 — Eldritch research art

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-005
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare the nine research-only PNG assets `gui_researchbackeldritch.png`, `r_eldritch.png`, `r_eldritchminor.png`, `r_eldritchmajor.png`, `r_outer.png`, `r_outerrev.png`, `r_nodes_2.png`, `eldritchajor1.png`, and `eldritchajor2.png` under the port and original TC4 `assets/thaumcraft` trees. Verify bytes/hashes, decoded dimensions, color mode, transparency, paths/case, and actual declaration/localization references.
- Anti-scope: Renderers and page-layout behavior; unrelated assets; translation expansion beyond shipped English; product fixes; manual client visual-parity claims.
- Oracle and comparison direction: S-003/S-004 Thaumcraft 4.2.3.5 assets, declarations, and localization -> S-005 Forge 1.12.2 port.
- Questions: Is any scoped port asset missing, byte-different, corrupt, wrongly sized, in the wrong color/transparency mode, misplaced, case-mismatched, or unreferenced? Do live port declarations and localization resolve the same exact paths as TC4?
- Expected evidence: `cmp` and SHA-256 results; PNG decoder metadata; case-sensitive and case-insensitive path inventory; port source/localization locators; original localization locators and CFR output from `ConfigResearch.class`; lifecycle reachability.
- Read/write permissions: Product files and central ledger files read-only; this report writable.
- Effort/tool budget: Targeted repository reads/searches and local binary/image inspection only; no build or runtime launch.
- Stop conditions: All nine assets have direct port/oracle comparisons and resolved reference evidence, or an asset/reference discrepancy remains unproven.
- Continuation predecessor: none.

## Coverage Performed

- Port asset root inspected: `src/main/resources/assets/thaumcraft/`.
- Oracle asset root inspected: `thaumcraft_src/assets/thaumcraft/` (S-004, extracted from S-003).
- Port declarations inspected: `src/main/java/thaumcraft/common/config/research/ConfigResearch.java`, `src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java`, and the `ConfigResearch.init()` caller in `src/main/java/thaumcraft/common/Thaumcraft.java`.
- Port localization inspected: `src/main/resources/assets/thaumcraft/lang/en_us.lang`; `build.gradle:106-121` packages that resource and also emits the 1.7-style `en_US.lang` filename.
- Oracle declarations inspected: `thaumcraft_src/thaumcraft/common/config/ConfigResearch.class`, decompiled with CFR 0.152.
- Oracle English localization inspected: `thaumcraft_src/assets/thaumcraft/lang/en_US.lang`.
- Additional oracle localization reference search covered `cs_CZ.lang`, `de_DE.lang`, `en_US.lang`, `es_AR.lang`, `es_ES.lang`, `es_MX.lang`, `es_UY.lang`, `es_VE.lang`, `fr_FR.lang`, `it_IT.lang`, `ko_KR.lang`, `pt_PT.lang`, `ru_RU.lang`, `zh_CN.lang`, and `zh_TW.lang`; all occurrences use the same lowercase `thaumcraft:textures/misc/eldritchajor1.png` / `eldritchajor2.png` resource paths.
- Commands/tools used: `git status --short`; `find`; targeted `glob`/`grep`/file reads; `cmp -s`; `sha256sum`; ImageMagick `identify`; `file`; CFR 0.152 decompilation of `ConfigResearch.class`.
- Uncovered scope: Renderer behavior and manual in-game appearance were excluded by assignment. No claim is made about visual composition, filtering, scaling, UV/layout behavior, or page rendering beyond exact asset/reference parity.

## Asset Evidence Matrix

Every port file below is byte-for-byte identical to the corresponding S-004 file. All decoded as non-interlaced, 8-bit PNGs in sRGB. The SHA-256 shown is shared by the port and oracle copies.

| Asset | Exact path below each `assets/thaumcraft/` root | Shared SHA-256 | Dimensions | Color mode / transparency | Port reference | TC4 reference |
|---|---|---|---|---|---|---|
| `gui_researchbackeldritch.png` | `textures/gui/gui_researchbackeldritch.png` | `6500110c9f988ddf33bd577e73dc2ec566f15c7057857e306b5c69488f394df4` | 512x512 | 8-bit RGB; no alpha channel; opaque | `ConfigResearch.java:113-115`, `ELDRITCH` category background | `ConfigResearch.class`, `initCategories()`, exact same `ResourceLocation` |
| `r_eldritch.png` | `textures/misc/r_eldritch.png` | `c4237a5dfd3843d224ea18f1c655e42064f881d346da248fd97a5315c37115f7` | 32x32 | 8-bit RGBA; alpha 0..255 | `ConfigResearch.java:113-115`, `ELDRITCH` category icon | `ConfigResearch.class`, `initCategories()`, exact same `ResourceLocation` |
| `r_eldritchminor.png` | `textures/misc/r_eldritchminor.png` | `d9e700223a4eb573df7242fd434a3b1be19faf75b6d4326a03c87e694160108b` | 32x32 | 8-bit RGBA; alpha 0..255 | `ConfigResearchEldritch.java:17-31`, `ELDRITCHMINOR` icon; exact path at line 24 | `ConfigResearch.class`, `initEldritchResearch()`, `ELDRITCHMINOR`, exact same `ResourceLocation` |
| `r_eldritchmajor.png` | `textures/misc/r_eldritchmajor.png` | `77b1ee3da7c8f1f529c2256022f5b3b7a179997e8d5b9af7305ecd616c8a6722` | 32x32 | 8-bit RGBA; alpha 0..255 | `ConfigResearchEldritch.java:350-366`, `ELDRITCHMAJOR` icon; exact path at line 358 | `ConfigResearch.class`, `initEldritchResearch()`, `ELDRITCHMAJOR`, exact same `ResourceLocation` |
| `r_outer.png` | `textures/misc/r_outer.png` | `293fc96e7645539f6606b687f06a33f53cbd3204025301ee68f1631dde4e5d02` | 32x32 | 8-bit RGBA; alpha 0..255 | `ConfigResearchEldritch.java:57-70`, `ENTEROUTER` icon; exact path at line 64 | `ConfigResearch.class`, `initEldritchResearch()`, `ENTEROUTER`, exact same `ResourceLocation` |
| `r_outerrev.png` | `textures/misc/r_outerrev.png` | `433d5ea35fc383fc694a18fdee802da6cc34364b11ae3c229b97810469b0ef8e` | 32x32 | 8-bit RGBA; alpha 0..255 | `ConfigResearchEldritch.java:72-90`, `OUTERREV` icon; exact path at line 81 | `ConfigResearch.class`, `initEldritchResearch()`, `OUTERREV`, exact same `ResourceLocation` |
| `r_nodes_2.png` | `textures/misc/r_nodes_2.png` | `8f1b937a19f9c2f8c573bb1bfc5cc2dfabb808d1b5a546a657975102c0517e2b` | 32x32 | 8-bit RGBA; alpha 0..255 | `ConfigResearchEldritch.java:116-132`, `PRIMNODE` icon; exact path at line 127 | `ConfigResearch.class`, `initEldritchResearch()`, `PRIMNODE`, exact same `ResourceLocation` |
| `eldritchajor1.png` | `textures/misc/eldritchajor1.png` | `67589c6cddf9f2528b6e918e5c92ef7efc884e5245f596dbfc1afdc24bde5f7d` | 256x256 | 8-bit RGBA; alpha 0..251 | `en_us.lang:1508`, `tc.research_page.ELDRITCHMAJOR.1`, exact `<IMG>thaumcraft:textures/misc/eldritchajor1.png:0:255:255:255:.5</IMG>` locator | `en_US.lang:1697`, same key and exact `<IMG>` locator |
| `eldritchajor2.png` | `textures/misc/eldritchajor2.png` | `8d8a689cfeca11659550e430632dbb75c99dbd0c65831b79ffbb2fc4136dfaa1` | 256x256 | 8-bit RGBA; alpha 0..243 | `en_us.lang:1509`, `tc.research_page.ELDRITCHMAJOR.2`, exact `<IMG>thaumcraft:textures/misc/eldritchajor2.png:0:255:255:255:.5</IMG>` locator | `en_US.lang:1698`, same key and exact `<IMG>` locator |

Case-insensitive inventories returned exactly the same nine correctly cased relative paths in both trees:

```text
textures/gui/gui_researchbackeldritch.png
textures/misc/eldritchajor1.png
textures/misc/eldritchajor2.png
textures/misc/r_eldritch.png
textures/misc/r_eldritchmajor.png
textures/misc/r_eldritchminor.png
textures/misc/r_nodes_2.png
textures/misc/r_outer.png
textures/misc/r_outerrev.png
```

The unusual `eldritchajor1.png` and `eldritchajor2.png` spellings are intentional TC4 filenames, not port misspellings: the filenames, port locators, and original locators all omit the `m` in `major` identically.

## Atomic Findings

### A-005-F01 — All nine PNG payloads and decoded properties match TC4

- Type: parity
- Severity: preserve
- Confidence: high
- Source/oracle locator: S-004 `thaumcraft_src/assets/thaumcraft/textures/{gui,misc}/...`; S-005 `src/main/resources/assets/thaumcraft/textures/{gui,misc}/...`; exact paths and hashes are in the Asset Evidence Matrix.
- Observed: All nine scoped port files exist. Each compares equal with `cmp -s` and has the same SHA-256 as its S-004 counterpart. `file` and ImageMagick decode all nine successfully with the dimensions, 8-bit RGB/RGBA modes, and alpha ranges recorded above.
- Expected: The port should contain one correctly named copy of each TC4 asset with unchanged bytes and therefore unchanged decoded dimensions, channel mode, and transparency.
- Exact deltas: None. Nine of nine files are byte-identical. One 512x512 image is RGB/opaque; six 32x32 images are RGBA with alpha 0..255; two 256x256 images are RGBA with alpha ranges 0..251 and 0..243 respectively.
- Affected paths/symbols: The nine paths in the Asset Evidence Matrix.
- Evidence/reproduction: `cmp -s`, `sha256sum`, `file`, and `identify` results recorded in this packet.
- Regression hazards: Re-encoding, resizing, mode conversion, alpha normalization, or recreating these assets would destroy byte parity and may alter research-browser/page appearance.
- Candidate disposition: preserve.

### A-005-F02 — Category and research icon declarations resolve exact TC4 paths

- Type: parity
- Severity: preserve
- Confidence: high
- Source/oracle locator: S-004 `thaumcraft_src/thaumcraft/common/config/ConfigResearch.class`, methods `initCategories()` and `initEldritchResearch()`; S-005 `ConfigResearch.java:91-116` and `ConfigResearchEldritch.java:16-132,350-366`.
- Observed: The port declares the category background/icon and all five research icons with exact lowercase namespace/path pairs matching CFR output from TC4: `thaumcraft:textures/gui/gui_researchbackeldritch.png`, `thaumcraft:textures/misc/r_eldritch.png`, `r_eldritchminor.png`, `r_eldritchmajor.png`, `r_outer.png`, `r_outerrev.png`, and `r_nodes_2.png`.
- Expected: Each category/research declaration should identify the same asset path and owning `ELDRITCH`, `ELDRITCHMINOR`, `ELDRITCHMAJOR`, `ENTEROUTER`, `OUTERREV`, or `PRIMNODE` key as TC4.
- Exact deltas: None across seven declared assets and seven owning keys.
- Affected paths/symbols: `ConfigResearch.initCategories()`, `ConfigResearchEldritch.initEldritchResearchBaseline()`, and `ConfigResearchEldritch.initEldritchResearchTextOnlyBaseline()`.
- Evidence/reproduction: Targeted source reads and `cfr thaumcraft_src/thaumcraft/common/config/ConfigResearch.class --silent true | rg -n -C 5 '<asset/key pattern>'`.
- Regression hazards: Case/path changes or reassignment to a different key would produce missing textures or the wrong category/research icon despite intact PNG files.
- Candidate disposition: preserve.

### A-005-F03 — Both embedded research-page image locators match TC4 localization

- Type: parity
- Severity: preserve
- Confidence: high
- Source/oracle locator: S-004 `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1695-1698`; S-005 `src/main/resources/assets/thaumcraft/lang/en_us.lang:1506-1509`.
- Observed: The port's `ELDRITCHMAJOR.1` and `.2` page text contains the exact TC4 `<IMG>` namespace, lowercase path, image parameter sequence `:0:255:255:255:.5`, and closing tag for `eldritchajor1.png` and `eldritchajor2.png`. The associated English research name/text/page keys exist. The historical filename spelling is consistent with all searched TC4 locale locators.
- Expected: The two page keys should retain the exact original image locators so page parsing resolves the packaged PNGs.
- Exact deltas: None for either `<IMG>` locator. Text around the locators also matches the inspected TC4 English entries.
- Affected paths/symbols: `tc.research_page.ELDRITCHMAJOR.1`, `tc.research_page.ELDRITCHMAJOR.2`, and the two 256x256 PNG paths.
- Evidence/reproduction: Targeted `grep` and line reads in both localization trees.
- Regression hazards: Correcting the apparent `eldritchajor` typo in only the filename or only localization would break resource resolution; changing `<IMG>` parameters could alter page presentation.
- Candidate disposition: preserve.

### A-005-F04 — All scoped assets are uniquely placed and reachable from live registrations

- Type: parity
- Severity: preserve
- Confidence: high
- Source/oracle locator: S-005 `Thaumcraft.java:199-207`, `ConfigResearch.java:52-71,91-116`, `ConfigResearchEldritch.java:16-132,350-366`, and `en_us.lang:853,1503-1509,1559-1574`; S-004 corresponding `ConfigResearch.class` declarations and `en_US.lang:870,1691-1698,1764-1783` keys.
- Observed: Case-insensitive inventory checks find exactly one correctly cased scoped path in each tree. `Thaumcraft.postInit()` calls `ConfigResearch.init()` at line 206; that method registers the category and invokes both Eldritch registration methods at lines 67-68. All category/research/page localization keys associated with these references are present in port English localization and in original English localization.
- Expected: Each scoped asset should have one canonical path and a reachable category, research-item, or research-page reference with its owning localization key.
- Exact deltas: None. Nine of nine assets are referenced; no duplicate case variants or orphaned scoped assets were found.
- Affected paths/symbols: `Thaumcraft.postInit()`, `ConfigResearch.init()`, `ConfigResearch.initCategories()`, both `ConfigResearchEldritch` initialization methods, and the listed localization keys.
- Evidence/reproduction: `find` case-insensitive inventories, targeted `grep`, source reads, and CFR declaration comparison.
- Regression hazards: Removing lifecycle calls, registrations, localization keys, or canonical files would make otherwise valid art unreachable or unresolved.
- Candidate disposition: preserve.

## Positive Parity

- Presence: all nine requested port assets exist at their exact TC4-relative paths.
- Binary integrity: every port/oracle pair is byte-identical and shares the SHA-256 in the matrix.
- Decode integrity: all nine are valid non-interlaced 8-bit PNGs; dimensions, RGB/RGBA mode, and alpha behavior are preserved exactly by binary identity and independently decoded successfully.
- Case/path parity: both trees expose exactly one copy under the same lowercase relative path; no alternate-case duplicate was found.
- Declaration parity: category background/icon and five research icons use exact original `ResourceLocation` paths and owning keys.
- Localization parity: the two page images use exact original `<IMG>` paths and parameters under the same `ELDRITCHMAJOR` page keys.
- Reachability: registration executes from the normal post-initialization path, and owning English category/research/page keys exist.
- Intentional spelling: `eldritchajor1/2` is preserved because it is the exact original filename and reference spelling.

## Validation Results

- `git status --short` before audit: clean.
- Asset discovery with exact-name globs: all nine found in each tree.
- Pairwise `cmp -s`: nine `IDENTICAL` results.
- Pairwise `sha256sum`: matching hashes for every pair, recorded in the matrix.
- `file` on all port assets: all recognized as non-interlaced 8-bit PNG; dimensions and RGB/RGBA modes agree with the matrix.
- ImageMagick `identify`: all nine decoded; dimensions, sRGB colorspace, channels, opacity, and alpha extrema agree with the matrix.
- Exact reference searches: seven Java declaration matches and two port localization matches account for all nine assets.
- CFR 0.152 comparison: original `ConfigResearch.class` declares the same category paths, research icon paths, owning keys, and page keys.
- Case-insensitive `find` inventories: the same nine canonical paths and no alternate-case scoped duplicates in either tree.
- Localization-key searches: associated `ELDRITCH`, `ELDRITCHMINOR`, `ELDRITCHMAJOR`, `ENTEROUTER`, `OUTERREV`, and `PRIMNODE` English keys exist in both port and original trees.
- Final `git status --short` after the read-only audit: clean.
- Build/runtime smoke: not run and not required for the read-only asset/reference audit. No product code or asset changed.

## Unknowns and Conflicts

- None within the assignment. No source conflict, missing file, hash mismatch, decode error, dimension/mode/transparency delta, path/case mismatch, declaration mismatch, localization mismatch, or unreferenced scoped asset was found.

## Gaps and Limitations

- Manual in-game visual validation was not run. Exact files and references prove asset parity, not renderer/page-layout parity.
- Renderer implementations, texture filtering/scaling, UV/layout behavior, GUI composition, and research-page parsing behavior were explicitly outside A-005 and belong to other audit assignments.
- The port ships English localization for this surface; absence of the original non-English translation corpus was not treated as an A-005 asset defect because translation expansion is outside the audit charter. The original locale search was used only to confirm the unusual `eldritchajor*` path spelling.
- No build was run because no product file changed. Packaging behavior was inspected statically in `build.gradle`, but no jar extraction was required to decide the nine asset/reference parity questions.

## Test Debt

- No product test was added or requested in this read-only assignment. The repository has no identified focused guard that freezes these nine asset hashes and declarations; this is a possible future regression guard, not a confirmed product defect or automatically promoted requirement.

## Impact

- Current impact: none; the complete scoped research-art surface has exact TC4 parity and is live/referenced.
- Preserve impact: future deletion, rename/case change, re-encoding, resizing, alpha/mode conversion, declaration reassignment, localization path edit, or lifecycle removal can cause missing/wrong art or visual drift. The matrix supplies exact preserve controls.

## Handoff

- Terminal status: complete.
- Material finding index: `A-005-F01` nine PNG payload/metadata parity; `A-005-F02` category/research declaration parity; `A-005-F03` embedded page-image localization parity; `A-005-F04` canonical path uniqueness and live reachability.
- Defect index: none.
- Preserve index: all four findings.
- Exact continuation point: none; A-005 coverage is complete.
- Smallest next action if continued: orchestrator acceptance and normalization of the four parity findings as preserve controls; do not create an A-005 product fix.
