# A-028: Localization Loader and Resource Packaging Audit

Goal-ID: `goal-20260803-eldritch-full-parity-audit-fixes`
Assignment-ID: `I09`
Status: needs_fix
Report-Revision: 1
Last-Updated: 2026-08-03
Scope: Read-only audit of Eldritch localization lookup, formatting, fallback, research/category keys, inline images, and Forge 1.12 resource packaging against TC4 4.2.3.5.

## Result

One high-confidence runtime defect and one lower-severity compatibility defect were verified. The Eldritch localization values, research/category lookup paths, UTF-8 handling, and current `<IMG>` resources otherwise preserve parity.

## Findings

### A-028-F01 - Forbidden-knowledge `%n` is consumed by 1.12 formatting

- Type: product_bug
- Severity: high
- Confidence: high
- Locator: `src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java:570-572`; `src/main/resources/assets/thaumcraft/lang/en_us.lang:842-847`.
- Observed: The browser calls `net.minecraft.client.resources.I18n.format("tc.forbidden")` and then replaces `%n` with the translated warp level. The English value is `Forbidden knowledge (%n)` with formatting codes. Forge 1.12 `I18n.format` delegates to `Locale.formatMessage`, which always calls Java `String.format`; `%n` is therefore converted to the platform line separator before `.replace("%n", ...)` runs.
- Effect: Eldritch research hover tooltips with warp display `Forbidden knowledge (` followed by a newline and `)` and omit the level text, rather than showing values such as `Mostly Harmless`.
- Repro: In a client with an Eldritch research entry that has warp, hover the entry in the Thaumonomicon. The forbidden line contains a blank/newline level instead of the localized `tc.forbidden.level.*` value.
- Expected/parity control: TC4 uses raw `StatCollector.translateToLocal("tc.forbidden")`, translates the level separately, and replaces the literal `%n` token. The port's `ItemResearchNotes.java:100-105` and `ClientProxy.localizeOrFallback` demonstrate the required raw-translation pattern for this custom token.
- Test gap: No test exercises `GuiResearchBrowser`'s warp-line path. Existing static guards cover the item-note path, not this browser call site.

### A-028-F02 - Legacy `en_US` alias can overwrite resource-pack overrides

- Type: compatibility_bug
- Severity: low
- Confidence: high (static loader evidence; no live client repro)
- Locator: `build.gradle:106-122`; generated resources `assets/thaumcraft/lang/en_us.lang` and `assets/thaumcraft/lang/en_US.lang`.
- Observed: The normal resource task copies lowercase `en_us.lang`, then explicitly copies the same bytes as uppercase `en_US.lang`. Forge 1.12 `LanguageManager.onResourceManagerReload` constructs the locale list in exact order `[en_us, currentLanguage]` when the values differ. `Locale.loadLocaleDataFiles` requests exact `lang/%s.lang` paths, and ZIP/resource-pack lookup is case-sensitive.
- Effect: With reachable legacy `currentLanguage=en_US`, the lowercase pass can load a resource-pack override, then the uppercase pass loads the mod's byte-identical `en_US.lang` and overwrites that override. Standard `en_us` selection is unaffected.
- Repro: Set `options.txt` to `lang:en_US`, enable a resource pack overriding `assets/thaumcraft/lang/en_us.lang` for an Eldritch key, reload resources, and observe the mod's English value after the uppercase pass. A pack providing its own uppercase alias avoids this specific overwrite.
- Expected/parity control: Forge 1.12's standard convention is lowercase locale files (`en_us.lang`); TC4's original file is `en_US.lang`. Keeping the compatibility alias creates a case-variant load pass only for the legacy setting. `GameSettings` and `LanguageManager` preserve the raw option value, so `en_US` is reachable.
- Test gap: No test asserts generated locale entries, exact locale load order, or resource-pack precedence on a case-sensitive filesystem.

## Positive Parity Controls

- `LanguageManager` always loads `en_us` first, then the selected locale; missing locale files are tolerated. This gives the expected English fallback for normal non-English selections.
- `Locale.loadLocaleData` and the deprecated `LanguageMap` read `.lang` files as UTF-8, split only the first `=`, preserve raw Unicode and formatting codes, and return the key for missing translations. The port file is BOM-free UTF-8 and contains raw section-sign formatting codes.
- `ResearchCategories.java:25-26` uses raw translation for `tc.research_category.<key>`, matching TC4. `ResearchItem.java:189-195` and `ResearchPage.java:106-111` likewise use raw translation for research names, summaries, and pages. The `ELDRITCH` category registration and `tc.research_category.ELDRITCH` key are present.
- `GuiResearchRecipe.java:561-679` preserves TC4's seven-field `<IMG>` syntax and `ResourceLocation` binding. Eldritch tags use exact lowercase paths `textures/misc/eldritchajor1.png` and `eldritchajor2.png`; both exist in source and the built JAR. Lowercase namespace/path handling and exact Linux/JAR lookup therefore succeed for the current corpus.
- Eldritch research prose is fetched through raw translation rather than `String.format`; no relevant Eldritch page value contains a printf token. The dedicated research font enables Unicode rendering, matching the TC4 custom renderer intent.

## Validation

Commands run from the repository root:

```text
git status --short
./scripts/dev.sh gradle processResources
cmp src/main/resources/assets/thaumcraft/lang/en_us.lang build/resources/main/assets/thaumcraft/lang/en_us.lang
cmp src/main/resources/assets/thaumcraft/lang/en_us.lang build/resources/main/assets/thaumcraft/lang/en_US.lang
./scripts/dev.sh gradle jar
./scripts/dev.sh gradle test --tests thaumcraft.common.config.EldritchLocalizationParityTest --tests thaumcraft.common.config.ConfigResearchStaticGraphTest --tests thaumcraft.client.GuiResearchRecipeStaticGuardTest
git status --short
```

Results:

- `processResources`, JAR creation, and the focused tests passed.
- Both generated locale files are byte-identical to the lowercase source; each is 187408 bytes with SHA-256 `e20d724086509c54a814d704c62eb683c712a79dfd6eefa1ae91c3a6778f585d`.
- The JAR contains both exact locale entries and both lowercase Eldritch image entries.
- The focused tests do not catch either finding: they validate source values, references, and image existence, not `String.format` custom-token behavior or resource-pack precedence.
- Runtime smoke was not required or run: this was a read-only audit and no product code was changed. No manual client visual validation was run.

## Limitations

No live client/resource-pack reproduction was run for F02. The conclusion follows from exact Forge 1.12 loader bytecode behavior, generated JAR entries, and raw locale-option handling. Translation values were not re-audited beyond the Eldritch parity controls relevant to loader semantics.
