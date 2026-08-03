# A-004: Eldritch Localization Parity Audit

Status: PASS - no verified discrepancy
Confidence: High
Audit date: 2026-08-03
Scope: Read-only comparison of the English localization used by the 16 TC4 Eldritch researches

## Result

The port's complete English localization corpus used by the 16 Eldritch researches matches Thaumcraft 4.2.3.5 exactly after removing the original file's carriage-return line terminators. Keys, values, capitalization, punctuation, intentional trailing spaces, page numbering, markup, formatting codes, image payloads, and absence of printf-style format tokens are unchanged.

No product bug was verified.

## Corpus

The audited corpus contains 56 keys total:

- 1 category key: `tc.research_category.ELDRITCH`
- 16 generated research-name keys: `tc.research_name.<research key>`
- 16 generated research-summary keys: `tc.research_text.<research key>`
- 23 explicitly referenced text-page keys: `tc.research_page.<research key>.<page>`
- 55 research keys excluding the category key

The name and summary lookup behavior is generated from each research key by:

- `src/main/java/thaumcraft/api/research/ResearchItem.java:189-195`

The category lookup behavior is generated from the category key by:

- `src/main/java/thaumcraft/api/research/ResearchCategories.java:25-27`

The 16 port research declarations and their 23 text-page references are in:

- `src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java:17-366`

The port registers the Eldritch category at:

- `src/main/java/thaumcraft/common/config/research/ConfigResearch.java:112-115`

### Research Line Evidence

| Research key | Text pages | Port localization lines | Original localization lines |
| --- | ---: | --- | --- |
| `ROD_primal_staff` | 1 | `src/main/resources/assets/thaumcraft/lang/en_us.lang:1023-1025` | `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1082-1084` |
| `FOCUSPRIMAL` | 1 | `src/main/resources/assets/thaumcraft/lang/en_us.lang:1051-1053` | `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1119-1121` |
| `ELDRITCHMINOR` | 1 | `src/main/resources/assets/thaumcraft/lang/en_us.lang:1503-1505` | `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1691-1693` |
| `ELDRITCHMAJOR` | 2 | `src/main/resources/assets/thaumcraft/lang/en_us.lang:1506-1509` | `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1695-1698` |
| `VOIDMETAL` | 2 | `src/main/resources/assets/thaumcraft/lang/en_us.lang:1510-1513` | `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1700-1703` |
| `ARMORVOIDFORTRESS` | 1 | `src/main/resources/assets/thaumcraft/lang/en_us.lang:1514-1516` | `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1705-1707` |
| `CAP_void` | 1 | `src/main/resources/assets/thaumcraft/lang/en_us.lang:1517-1519` | `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1709-1711` |
| `SANITYCHECK` | 1 | `src/main/resources/assets/thaumcraft/lang/en_us.lang:1526-1528` | `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1721-1723` |
| `OCULUS` | 2 | `src/main/resources/assets/thaumcraft/lang/en_us.lang:1532-1535` | `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1729-1732` |
| `ESSENTIARESERVOIR` | 2 | `src/main/resources/assets/thaumcraft/lang/en_us.lang:1536-1539` | `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1734-1737` |
| `PRIMPEARL` | 2 | `src/main/resources/assets/thaumcraft/lang/en_us.lang:1555-1558` | `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1759-1762` |
| `PRIMNODE` | 1 | `src/main/resources/assets/thaumcraft/lang/en_us.lang:1559-1561` | `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1764-1766` |
| `ADVALCHEMYFURNACE` | 2 | `src/main/resources/assets/thaumcraft/lang/en_us.lang:1562-1565` | `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1768-1771` |
| `ENTEROUTER` | 1 | `src/main/resources/assets/thaumcraft/lang/en_us.lang:1569-1571` | `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1777-1779` |
| `OUTERREV` | 1 | `src/main/resources/assets/thaumcraft/lang/en_us.lang:1572-1574` | `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1781-1783` |
| `PRIMALCRUSHER` | 2 | `src/main/resources/assets/thaumcraft/lang/en_us.lang:1578-1581` | `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1789-1792` |

The category value is identical (`Eldritch`) at:

- Port: `src/main/resources/assets/thaumcraft/lang/en_us.lang:853`
- Original: `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:870`

The original TC4 `ConfigResearch.class` and the port declaration reference the same set of 23 numbered text-page keys. No missing, extra, duplicated, or misnumbered page key was found in either English corpus.

Recipe pages constructed from recipe handles are not localization page keys and are therefore not included in the 23 text-page count.

## Value And Markup Results

Sorting the selected key/value lines and removing only `\r` from the original produced an empty diff for both the 55 research-key corpus and the category key. This comparison retained all value whitespace, including intentional trailing spaces.

Markup/token inventory is identical on both sides:

| Token | Count |
| --- | ---: |
| `<BR>` | 48 |
| `<IMG>` | 2 |
| `</IMG>` | 2 |
| `<LINE>` | 0 |
| `§m` | 1 |
| `§o` | 2 |
| `§r` | 3 |
| printf-style `%...` format tokens | 0 |

The two image tags are exact, including path case, coordinates, dimensions, and scale:

- `<IMG>thaumcraft:textures/misc/eldritchajor1.png:0:255:255:255:.5</IMG>`
- `<IMG>thaumcraft:textures/misc/eldritchajor2.png:0:255:255:255:.5</IMG>`

Both referenced files exist in the port and original at the same lowercase resource paths:

- `src/main/resources/assets/thaumcraft/textures/misc/eldritchajor1.png`
- `src/main/resources/assets/thaumcraft/textures/misc/eldritchajor2.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/eldritchajor1.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/eldritchajor2.png`

## Encoding And Bytes

Both source files are valid, BOM-free UTF-8 and are reported as `text/plain; charset=utf-8`.

The complete files are not expected to be byte-identical:

- Port: 1,581 lines, LF line termination, SHA-256 `e20d724086509c54a814d704c62eb683c712a79dfd6eefa1ae91c3a6778f585d`
- Original: 1,794 lines, 1,793 CRLF-terminated records plus one non-CRLF final record, SHA-256 `0b5d92850a8d45a6be89201d300ef1ad3cb85a9a84da80be604040811b323951`

The line-count difference concerns localization outside this audit corpus. Within the selected 56-key corpus, the only byte-level difference is the original's `\r` line-ending byte; key/value bytes otherwise compare exactly.

The original unpacked jar resource is byte-identical to the checked reference file `thaumcraft_src/assets/thaumcraft/lang/en_US.lang`.

## Resource Case Semantics

The Forge 1.12 source resource uses the lowercase locale filename:

- `src/main/resources/assets/thaumcraft/lang/en_us.lang`

`build.gradle:106-121` first copies the lowercase source through the normal resource set, then copies it again as legacy-case `en_US.lang`. Running `processResources` produced both distinct files on the case-sensitive Linux filesystem:

- `build/resources/main/assets/thaumcraft/lang/en_us.lang`
- `build/resources/main/assets/thaumcraft/lang/en_US.lang`

Both generated files are byte-identical to the lowercase source and have SHA-256 `e20d724086509c54a814d704c62eb683c712a79dfd6eefa1ae91c3a6778f585d`. This preserves Forge 1.12 lowercase lookup semantics while retaining the original-style filename as a compatibility copy. No case collision or stale generated value was observed.

## Exclusions

- The audit covers only the category/name/text/page keys used by the 16 Eldritch researches plus their English resource packaging behavior.
- Localization unrelated to this exact corpus was not classified.
- The port ships only `en_us.lang` from `src/main/resources/assets/thaumcraft/lang/`.
- Adding the 21 original non-English translations is explicitly excluded by `docs/goals/2026-08-03-eldritch-thaumonomicon-parity.md:92-99`. Their absence is not a port regression under the declared project scope.
- No previously excluded translation expansion was reclassified as a bug.

## Test Gaps

- `src/test/java/thaumcraft/common/config/ConfigResearchStaticGraphTest.java:84-96` verifies required localization-key presence but not exact values, page-corpus equality, trailing whitespace, markup, or original parity.
- `src/test/java/thaumcraft/common/config/ConfigResearchStaticGraphTest.java:100-119` checks that localization image paths resolve, but not that image tags retain their original coordinates, dimensions, scale, ordering, or exact text placement.
- `src/test/java/thaumcraft/common/config/EldritchLocalizationParityTest.java:15-35` covers selected Eldritch-related aspect, block, and item display values, not the 55 research keys or category key audited here.
- No automated test guards UTF-8/BOM status, line-level exact values, markup/token counts, page-number equality against TC4, or the dual `en_us.lang`/`en_US.lang` generated-resource behavior.
- No manual in-game Thaumonomicon visual validation was run, so actual wrapping and image placement remain a visual test gap despite exact source parity.

## Commands

The following read-only audit and generated-resource validation commands were run from the repository root. `processResources` wrote only ignored build output; no product or ledger source was modified.

```bash
git status --short

diff -u <(LC_ALL=C grep -E '^tc\.research_(name|text|page)\.(ELDRITCHMINOR|OCULUS|ENTEROUTER|OUTERREV|PRIMPEARL|PRIMNODE|ADVALCHEMYFURNACE|PRIMALCRUSHER|VOIDMETAL|ESSENTIARESERVOIR|CAP_void|ARMORVOIDFORTRESS|FOCUSPRIMAL|SANITYCHECK|ROD_primal_staff|ELDRITCHMAJOR)(\.|=)' thaumcraft_src/assets/thaumcraft/lang/en_US.lang | tr -d '\r' | sort) <(LC_ALL=C grep -E '^tc\.research_(name|text|page)\.(ELDRITCHMINOR|OCULUS|ENTEROUTER|OUTERREV|PRIMPEARL|PRIMNODE|ADVALCHEMYFURNACE|PRIMALCRUSHER|VOIDMETAL|ESSENTIARESERVOIR|CAP_void|ARMORVOIDFORTRESS|FOCUSPRIMAL|SANITYCHECK|ROD_primal_staff|ELDRITCHMAJOR)(\.|=)' src/main/resources/assets/thaumcraft/lang/en_us.lang | sort)

diff -u <(LC_ALL=C grep -E '^tc\.research_category\.ELDRITCH=' thaumcraft_src/assets/thaumcraft/lang/en_US.lang | tr -d '\r') <(LC_ALL=C grep -E '^tc\.research_category\.ELDRITCH=' src/main/resources/assets/thaumcraft/lang/en_us.lang)

grep -Ec '^tc\.research_(name|text|page)\.(ELDRITCHMINOR|OCULUS|ENTEROUTER|OUTERREV|PRIMPEARL|PRIMNODE|ADVALCHEMYFURNACE|PRIMALCRUSHER|VOIDMETAL|ESSENTIARESERVOIR|CAP_void|ARMORVOIDFORTRESS|FOCUSPRIMAL|SANITYCHECK|ROD_primal_staff|ELDRITCHMAJOR)(\.|=)' src/main/resources/assets/thaumcraft/lang/en_us.lang
grep -Ec '^tc\.research_(name|text|page)\.(ELDRITCHMINOR|OCULUS|ENTEROUTER|OUTERREV|PRIMPEARL|PRIMNODE|ADVALCHEMYFURNACE|PRIMALCRUSHER|VOIDMETAL|ESSENTIARESERVOIR|CAP_void|ARMORVOIDFORTRESS|FOCUSPRIMAL|SANITYCHECK|ROD_primal_staff|ELDRITCHMAJOR)(\.|=)' thaumcraft_src/assets/thaumcraft/lang/en_US.lang
grep -Ec '^tc\.research_page\.(ELDRITCHMINOR|OCULUS|ENTEROUTER|OUTERREV|PRIMPEARL|PRIMNODE|ADVALCHEMYFURNACE|PRIMALCRUSHER|VOIDMETAL|ESSENTIARESERVOIR|CAP_void|ARMORVOIDFORTRESS|FOCUSPRIMAL|SANITYCHECK|ROD_primal_staff|ELDRITCHMAJOR)\.' src/main/resources/assets/thaumcraft/lang/en_us.lang
grep -Ec '^tc\.research_page\.(ELDRITCHMINOR|OCULUS|ENTEROUTER|OUTERREV|PRIMPEARL|PRIMNODE|ADVALCHEMYFURNACE|PRIMALCRUSHER|VOIDMETAL|ESSENTIARESERVOIR|CAP_void|ARMORVOIDFORTRESS|FOCUSPRIMAL|SANITYCHECK|ROD_primal_staff|ELDRITCHMAJOR)\.' thaumcraft_src/assets/thaumcraft/lang/en_US.lang
grep -Ec '^tc\.research_(name|text)\.(ELDRITCHMINOR|OCULUS|ENTEROUTER|OUTERREV|PRIMPEARL|PRIMNODE|ADVALCHEMYFURNACE|PRIMALCRUSHER|VOIDMETAL|ESSENTIARESERVOIR|CAP_void|ARMORVOIDFORTRESS|FOCUSPRIMAL|SANITYCHECK|ROD_primal_staff|ELDRITCHMAJOR)=' src/main/resources/assets/thaumcraft/lang/en_us.lang
grep -Ec '^tc\.research_(name|text)\.(ELDRITCHMINOR|OCULUS|ENTEROUTER|OUTERREV|PRIMPEARL|PRIMNODE|ADVALCHEMYFURNACE|PRIMALCRUSHER|VOIDMETAL|ESSENTIARESERVOIR|CAP_void|ARMORVOIDFORTRESS|FOCUSPRIMAL|SANITYCHECK|ROD_primal_staff|ELDRITCHMAJOR)=' thaumcraft_src/assets/thaumcraft/lang/en_US.lang

diff -u <(javap -classpath Thaumcraft-1.7.10-4.2.3.5.jar -verbose thaumcraft.common.config.ConfigResearch | grep -oE 'tc\.research_page\.(ELDRITCHMINOR|OCULUS|ENTEROUTER|OUTERREV|PRIMPEARL|PRIMNODE|ADVALCHEMYFURNACE|PRIMALCRUSHER|VOIDMETAL|ESSENTIARESERVOIR|CAP_void|ARMORVOIDFORTRESS|FOCUSPRIMAL|SANITYCHECK|ROD_primal_staff|ELDRITCHMAJOR)\.[0-9]+' | LC_ALL=C sort -u) <(grep -oE 'tc\.research_page\.(ELDRITCHMINOR|OCULUS|ENTEROUTER|OUTERREV|PRIMPEARL|PRIMNODE|ADVALCHEMYFURNACE|PRIMALCRUSHER|VOIDMETAL|ESSENTIARESERVOIR|CAP_void|ARMORVOIDFORTRESS|FOCUSPRIMAL|SANITYCHECK|ROD_primal_staff|ELDRITCHMAJOR)\.[0-9]+' src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java | LC_ALL=C sort -u)

grep -E '^tc\.research_(name|text|page)\.(ELDRITCHMINOR|OCULUS|ENTEROUTER|OUTERREV|PRIMPEARL|PRIMNODE|ADVALCHEMYFURNACE|PRIMALCRUSHER|VOIDMETAL|ESSENTIARESERVOIR|CAP_void|ARMORVOIDFORTRESS|FOCUSPRIMAL|SANITYCHECK|ROD_primal_staff|ELDRITCHMAJOR)(\.|=)' src/main/resources/assets/thaumcraft/lang/en_us.lang | grep -oE '<BR>|<LINE>|<IMG>|</IMG>|§.' | LC_ALL=C sort | uniq -c
grep -E '^tc\.research_(name|text|page)\.(ELDRITCHMINOR|OCULUS|ENTEROUTER|OUTERREV|PRIMPEARL|PRIMNODE|ADVALCHEMYFURNACE|PRIMALCRUSHER|VOIDMETAL|ESSENTIARESERVOIR|CAP_void|ARMORVOIDFORTRESS|FOCUSPRIMAL|SANITYCHECK|ROD_primal_staff|ELDRITCHMAJOR)(\.|=)' thaumcraft_src/assets/thaumcraft/lang/en_US.lang | grep -oE '<BR>|<LINE>|<IMG>|</IMG>|§.' | LC_ALL=C sort | uniq -c
grep -E '^tc\.research_(name|text|page)\.(ELDRITCHMINOR|OCULUS|ENTEROUTER|OUTERREV|PRIMPEARL|PRIMNODE|ADVALCHEMYFURNACE|PRIMALCRUSHER|VOIDMETAL|ESSENTIARESERVOIR|CAP_void|ARMORVOIDFORTRESS|FOCUSPRIMAL|SANITYCHECK|ROD_primal_staff|ELDRITCHMAJOR)(\.|=)' src/main/resources/assets/thaumcraft/lang/en_us.lang | grep -oE '%([0-9]+\$)?[-#+ 0,(]*[0-9]*(\.[0-9]+)?[a-zA-Z%]'
grep -E '^tc\.research_(name|text|page)\.(ELDRITCHMINOR|OCULUS|ENTEROUTER|OUTERREV|PRIMPEARL|PRIMNODE|ADVALCHEMYFURNACE|PRIMALCRUSHER|VOIDMETAL|ESSENTIARESERVOIR|CAP_void|ARMORVOIDFORTRESS|FOCUSPRIMAL|SANITYCHECK|ROD_primal_staff|ELDRITCHMAJOR)(\.|=)' thaumcraft_src/assets/thaumcraft/lang/en_US.lang | grep -oE '%([0-9]+\$)?[-#+ 0,(]*[0-9]*(\.[0-9]+)?[a-zA-Z%]'

file -bi src/main/resources/assets/thaumcraft/lang/en_us.lang thaumcraft_src/assets/thaumcraft/lang/en_US.lang
xxd -l 4 src/main/resources/assets/thaumcraft/lang/en_us.lang
xxd -l 4 thaumcraft_src/assets/thaumcraft/lang/en_US.lang
iconv -f UTF-8 -t UTF-8 src/main/resources/assets/thaumcraft/lang/en_us.lang -o /dev/null
iconv -f UTF-8 -t UTF-8 thaumcraft_src/assets/thaumcraft/lang/en_US.lang -o /dev/null
LC_ALL=C awk '{ if (sub(/\r$/, "")) crlf++; else lf++ } END { printf "port: crlf=%d lf=%d lines=%d\n", crlf, lf, NR }' src/main/resources/assets/thaumcraft/lang/en_us.lang
LC_ALL=C awk '{ if (sub(/\r$/, "")) crlf++; else lf++ } END { printf "original: crlf=%d lf=%d lines=%d\n", crlf, lf, NR }' thaumcraft_src/assets/thaumcraft/lang/en_US.lang
unzip -p Thaumcraft-1.7.10-4.2.3.5.jar assets/thaumcraft/lang/en_US.lang | diff -u thaumcraft_src/assets/thaumcraft/lang/en_US.lang -

./scripts/dev.sh gradle processResources --rerun-tasks
find build/resources/main/assets/thaumcraft/lang -maxdepth 1 -type f -printf '%f\n' | LC_ALL=C sort
sha256sum src/main/resources/assets/thaumcraft/lang/en_us.lang thaumcraft_src/assets/thaumcraft/lang/en_US.lang build/resources/main/assets/thaumcraft/lang/en_us.lang build/resources/main/assets/thaumcraft/lang/en_US.lang
cmp -s src/main/resources/assets/thaumcraft/lang/en_us.lang build/resources/main/assets/thaumcraft/lang/en_us.lang
cmp -s src/main/resources/assets/thaumcraft/lang/en_us.lang build/resources/main/assets/thaumcraft/lang/en_US.lang
git status --short
```

Observed command results:

- Both normalized localization diffs were empty.
- Both page-reference sets were identical.
- Research-key counts were 55/55; name/text counts were 32/32; numbered page counts were 23/23; category count was 1/1.
- Both UTF-8 conversion checks succeeded and neither file had a BOM.
- Both format-token searches returned no matches.
- `processResources` completed successfully.
- Both generated case variants matched the port source byte-for-byte.
- Runtime smoke was not required or run because this was a read-only localization audit followed only by creation of this report.
