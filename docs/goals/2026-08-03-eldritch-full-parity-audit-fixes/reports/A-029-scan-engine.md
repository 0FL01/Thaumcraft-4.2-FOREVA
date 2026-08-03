# A-029: Scan Engine and Aspect-Resolution Audit

Goal-ID: `goal-20260803-eldritch-full-parity-audit-fixes`  
Assignment-ID: `I10`  
Status: `needs_fix`  
Report-Revision: 1  
Last-Updated: 2026-08-03  
Scope: Read-only audit of scan dispatch, invalid-object feedback, scan awards, aspect lookup, and derived-aspect resolution against Thaumcraft 4.2.3.5, with Eldritch-facing examples. Registration values themselves were bounded by the A18 exclusion.

## Result

Three semantic differences were verified. Two affect the native Eldritch-facing scan path; the third is a public/alternate-path defect with low reach through the current native packet flow. Lookup precedence, Eldritch registrations, normal `@` awards, and Void-derived aspect calculations otherwise preserve the checked TC4 behavior.

## Findings

### A-029-F01 - Built-in phenomenon handler intercepts later handlers

- Type: `product_bug`
- Severity: high
- Confidence: high
- Locator: `src/main/java/thaumcraft/common/lib/research/ScanManager.java:47-60`; `src/main/java/thaumcraft/common/Thaumcraft.java:110-112`; `src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java:111-117,205-211`
- TC4 control flow: TC4 registers `new ScanManager()` as a scan handler, but the original `ScanManager.scanPhenomena(ItemStack, World, EntityPlayer)` returns `null` unconditionally. It is a pass-through. `ItemThaumometer` continues to the next handler when the result is null.
- Port control flow: The port's `scanPhenomena` returns `null` only for an empty held stack; otherwise it calls `scanItem`. `scanItem` creates a type-1 scan result for the held stack and immediately calls `completeScan(player, result, "@")`. `ItemThaumometer.findRawScanTarget` stops at this first non-null result. `doActiveScan` then checks scan validity and rejects the result because the held thaumometer has just been recorded as scanned.
- Effect: The first fallback handler can mutate client scan state while failing the actual scan, and handlers registered after `ScanManager` do not receive the target. The same result can be reached during server authority validation through the server-side target lookup. This changes both fallback ordering and side-effect behavior.
- Eldritch example: `BlockEldritchNothing` has no useful pick stack or drop (`BlockEldritchNothing.java:78-90`), so its block path can fall through to phenomenon handlers. An Outer Lands/addon phenomenon handler registered after the built-in handler is therefore starved on the first attempt. The held thaumometer is itself tagged in `ConfigAspects.java:424`, so the self-scan is live once its prerequisites are known. The explicit End Portal bridge (`ItemThaumometer.java:182-185`) avoids this generic fallback path.
- Expected parity: The port's built-in phenomenon method must remain side-effect-free and return null unless a distinct phenomenon implementation is deliberately being dispatched.
- Test gap: No test asserts that the built-in handler returns null without changing scan state or that a later phenomenon handler is reached.

### A-029-F02 - Empty-aspect blocks lose TC4 unknown-object feedback

- Type: `product_bug`
- Severity: medium
- Confidence: high
- Locator: `src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java:164,173,178,331-346`; `src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java:94`
- TC4 control flow: `ItemThaumometer.doScan` creates the type-1 block scan result from the block pick/drop stack and metadata without first requiring a nonempty aspect list. After the timed scan, `completeScan` resolves the object tags and calls `validScan`; an empty or missing tag reaches TC4's invalid-scan path and emits the unknown-object notification.
- Port control flow: Block candidates are routed through `toTaggedItemScan`. That method rejects an empty stack and rejects any resolved aspect list with size zero before a timed target is returned. The port does contain the later `validScan`/`notifyInvalidScan` path, but no target exists to reach it.
- Effect: Empty-aspect blocks silently fail to produce the timed TC4 invalid-object feedback. This is a behavior change from “scan, then report unknown” to “no scan target.”
- Eldritch example: `BlockEldritchNothing` returns `ItemStack.EMPTY` from its pick path and drops air/zero quantity. It should exercise the timed unknown-object result under TC4. In the port it produces no target, with F01 potentially intercepting the first attempt as well.
- Expected parity: Preserve the block scan result long enough for the timed completion and invalid-object notification; aspect availability should determine validity at completion, not suppress target acquisition.
- Test gap: `ScanProgressionRuntimeTest` covers a nonempty block with a missing parent aspect and the End Portal bridge, but no empty-aspect block such as `BlockEldritchNothing`.

### A-029-F03 - `#` award ordering suppresses the first base award

- Type: `product_bug`
- Severity: medium
- Confidence: high
- Locator: `src/main/java/thaumcraft/common/lib/research/ScanManager.java:189-220`; `src/main/java/thaumcraft/common/lib/capabilities/PlayerKnowledgeCapability.java:437-441`
- TC4 control flow: At entry to `completeScan`, TC4 computes whether a `#` scan follows an existing `@` scan: `prefix.equals("#") && !isValidScanTarget(player, scan, "@")`. It performs this check before recording the new scan key. A first `#` scan therefore receives the normal base aspect amount plus the `#` bonus; a `#` scan after `@` suppresses the repeated base award.
- Port control flow: The port records the scan key first, then evaluates the same condition. `addScanKey` records `#` and removes the matching `@` key. The subsequent `isValidScanTarget(..., "@")` check consequently fails for every `#` scan, so the base amount is set to zero and only the `+1` `#` bonus remains.
- Effect: First-time alternate-prefix scans under-award every eligible aspect. This is not normally reachable through the current native packet, which accepts only `@` (`PacketScannedToServer.java:103-105`), but it affects public `completeScan` callers, addons, and any restored alternate scan route.
- Eldritch example: A first direct/API `#` scan of Void Seed, Void Metal, or another Eldritch-tagged item should award its base/recipe-derived quantities plus one. The port awards only one per eligible known aspect.
- Expected parity: Evaluate the prior `@` state before recording the `#` key, or retain the pre-recording state for the award calculation.
- Test gap: No direct `completeScan(..., "#")` award test exists. The packet test covers rejection of `#`, not the public award semantics.

## Positive Parity Controls

- Object lookup precedence matches the checked TC4 behavior: exact metadata, grouped/range aliases, wildcard metadata, then derived recipe generation. Exact Eldritch block metas 3-6 override the wildcard registration in `ConfigAspects.java:468-479`.
- Damageable and non-subtyped item hashes use the expected collapsed metadata. Entity lookup honors exact keys, matching NBT, and last-registration precedence; current namespaced entity registrations remain compatible with legacy dotted/plain research trigger aliases in `ResearchManager.java:818-850`.
- Derived aspect calculations match the checked TC4 controls: ingredient scaling by `0.75 / outputCount`, recipe essentia contribution by `floor(sqrt(amount) / outputCount)`, six-aspect culling, per-aspect cap 64, recursion protection, and recipe priority of crucible, arcane, infusion, then vanilla.
- Void Seed and Void Metal crucible derivation in `ConfigRecipesCrucibleSlice.java:234-244`, plus downstream Void equipment tags in `ConfigAspects.java:502-515`, match the checked TC4 formulas.
- Prerequisite checks require nonempty tags and direct parents for compound aspects. Normal `@` scan awards, caps, clues, and completion behavior match the checked original path.
- TC6 scanning shims are not used by the native TC4 scan path and were not treated as parity requirements.

## Bounded Non-Native Deltas

These differences were verified or bounded during the audit but were not demonstrated on a native Eldritch path:

- TC4 registers a null generated result as an empty tag, permanently caching a failed derivation. The port registers only non-null generated tags (`ThaumcraftCraftingManager.java:170-176`) and retries misses. This can differ for late-added addon recipes; native recipe lifecycle ordering initializes the relevant recipes before aspects (`Thaumcraft.java:203-204`).
- TC4 excludes EE3 transmutation-stone ingredients through `Utils.isEETransmutionItem`; the port's arcane/vanilla derivation path (`ThaumcraftCraftingManager.java:346-403`) has no equivalent exclusion. This is addon-dependent and does not affect the ordinary Void catalysts checked here.
- TC4's arcane duplicate-output handling can allow a later empty result to overwrite an earlier nonempty result. The port retains a nonempty result instead. No native Eldritch duplicate-output case was found.
- TC4 catalyst-list resolution chooses the first entry and resets catalyst recursion history; the port chooses the first taggable alternative and shares recursion history. No native Eldritch catalyst-list case was found.
- TC4 entity hashes include additional state distinctions, including creeper flashing and golem material (and the legacy zombie-villager distinction). The port preserves child and powered state but not those other distinctions (`ScanManager.java:328-343`). No directly registered Eldritch entity depends on the omitted distinctions in this audit.

## Hazards and Gaps

- F01 changes handler ordering and mutates knowledge during a failed scan, so addon phenomenon behavior cannot be inferred from compile success or registration presence alone.
- F02 is primarily a user-feedback regression, but F01 can mask it on the first attempt; reproduction should separate the handler list from empty-aspect block targeting.
- F03 has low native reach because the current packet rejects `#`; removing that restriction or adding an addon/API caller would make the ordering defect observable.
- Existing coverage does not exercise side-effect-free fallback dispatch, empty-aspect block notification, or first-time `#` awards.
- No manual in-game visual or runtime scan reproduction was performed; conclusions are from source/decompiled control-flow comparison and existing static/runtime test inspection.

## Validation

Commands and evidence used from the repository root:

```text
git status --short
/usr/local/bin/cfr ... --silent true
```

The original TC4 classes were decompiled/read as reference material and the corresponding port sources/tests were inspected. The working tree was clean before the report-only change. No product code, original reference, or central ledger file was changed.

Build, test, and runtime smoke validation were not run. Runtime smoke was not required because this checkpoint changes documentation only; no product behavior was changed. No manual in-game validation was run.

## Limitations

The audit did not claim visual or live-server parity. The bounded deltas require addon-specific or late-lifecycle reproductions to determine practical Eldritch impact. Native `#` reach remains limited by the current packet contract, so F03 is confirmed by public method control flow rather than a native packet reproduction.
