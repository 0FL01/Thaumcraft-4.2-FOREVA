# Stage 9-e — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 9-e закрывает research content и Thaumonomicon progression для порта Thaumcraft 4.2.3.5 на Forge 1.12.2: категории исследований, research entries, страницы Thaumonomicon, ссылки страниц на рецепты/аспекты/сущности/блоки/предметы, и контентные условия открытия исследований.

Фаза не включает глубокую реализацию рецептов и клиентского GUI, но должна проверить, что research keys, page references, category icons/backgrounds, lang keys и unlock-flow совместимы с оригинальным контентом. По PRD Stage 9 должен обеспечить progression parity, а риск связан с совпадением registry names, research keys, recipe ids и GUI references.

## 2. Scope фазы

- Research categories: `BASICS`, `THAUMATURGY`, `ALCHEMY`, `ARTIFICE`, `GOLEMANCY`, `ELDRITCH`.
- Research items/entries: ключи, категории, координаты, complexity, aspect tags, parents, hidden parents, siblings, flags, triggers.
- Research pages: text, concealed text, image/aspect, vanilla crafting, arcane crafting, crucible, infusion, infusion-enchantment, compound/list, smelting.
- Thaumonomicon content references: category icons/backgrounds, page text keys, image paths, recipe page objects, item/entity/aspect trigger IDs.
- Lang keys: category names, research names, research subtitles, research page text, research-note status text.
- Recipe/research unlock flow as references only: recipe keys and page references must align with arcane/infusion/crucible recipe registration.
- Research note/content flow required by original behavior: note creation, hex-grid note data, completion, discovery use, hidden research from knowledge fragments.
- Scanning/discovery prerequisites only as research-content gates: hidden/lost research triggers from item/entity/aspect scans.
- Thaumonomicon GUI references only as content data/IDs; client GUI rendering itself is a dependency outside Stage 9-e.

## 3. Источники сравнения

- `docs/PRD.md:395-416` — Stage 9 scope and risks.
- `src/main/java/thaumcraft/common/Thaumcraft.java:186-191` — current post-init call order.
- `src/main/java/thaumcraft/common/config/research/ConfigResearch.java` and `ConfigResearch*.java` — current research registration baseline.
- `thaumcraft_src/thaumcraft/common/config/ConfigResearch.java` — original category/entry/page flow.
- `thaumcraft_src/thaumcraft/common/lib/research/ResearchManager.java` — original clue, note creation, hidden research, requisites, note serialization.
- `thaumcraft_src/thaumcraft/common/lib/research/ScanManager.java` — original scan completion clue unlock hook.
- `thaumcraft_src/assets/thaumcraft/lang/en_US.lang` — original category and research localization corpus.
- `src/main/resources/assets/thaumcraft/lang/en_us.lang` — current imported research localization corpus.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_researchback.png`, `gui_researchbackeldritch.png`, `gui_researchbook.png`, `gui_researchbook_overlay.png` — original Thaumonomicon backgrounds.
- `thaumcraft_src/assets/thaumcraft/textures/misc/r_thaumaturgy.png`, `r_crucible.png`, `r_artifice.png`, `r_golemancy.png`, `r_eldritch.png` — original category icons.

## 4. Текущее состояние Stage 9-e

Stage 9-e is not runtime/progression complete.

Current implementation present:

- `ConfigResearch.init()` registers six research categories.
- The category slices under `thaumcraft.common.config.research` contain 201 `ResearchItem` registrations.
- `ConfigResearch.recipes` is populated by the recipe foundation and supports recipe-backed pages.
- `ResearchNoteData`, note NBT read/write, note generation, note updates, and completion checks exist.
- `ResearchManager.createClue(...)` and `ScanManager.completeScan(...)` forward scan clue data after successful scans.
- `PacketPlayerCompleteToServer` now checks research existence, requisites, duplicate completion, type, note creation, aspect costs, and sibling gating.
- Targeted non-GUI runtime tests now cover `PacketPlayerCompleteToServer` primary note creation, prerequisite rejection, secondary aspect-cost completion, sibling grant gating, and wrong-type/insufficient-cost rejection.

Current blockers:

- Normal Thaumonomicon/research-browser click flow is not proven to send validated progression packets.
- Research table packet hardening now exists, but live GUI-route and broader adversarial/e2e validation remain open.
- `PacketScannedToServer` hardening now exists, but live packet-route and broader scan/runtime validation remain open.
- Hidden/lost clue grants need runtime verification against reference behavior.
- End-to-end progression runtime validation is still missing.

## 5. Gap list

### GAP-1: Research corpus exists; graph/runtime validation remains open

**Статус:** implementation present; static graph guarded; runtime validation open
**Критичность:** high

The old “entries are not registered” wording is stale. Current code registers six categories and 201 research entries. Remaining work is graph correctness and runtime behavior:

- verify parent/hidden-parent/sibling links against reference;
- verify recipe page references resolve to intended recipe objects or documented display-only handles;
- verify hidden/lost triggers under runtime scan scenarios;
- verify no impossible cycles or unreachable progression paths;
- validate representative pages through the live Thaumonomicon route once GUI action routing is functional.

### GAP-2: Recipe-visible lookup contract still needs full alignment

**Статус:** partial implementation; validation open
**Критичность:** medium/high

`ConfigResearch.recipes` now exists and is populated, but many reference page keys still need full validation. Do not treat the lookup map as proof that every research page has the correct recipe object. Remaining work:

- confirm all recipe keys referenced by Stage 9-e pages resolve to the intended objects or intentional display-only handles;
- keep page order and page type identical to reference;
- make missing recipe references fail loudly in development.

### GAP-3: Research localization corpus is present; runtime page rendering still unvalidated

**Статус:** corpus present; runtime/client verification open
**Критичность:** medium

Do not claim localization is absent. Keep coverage tests for missing keys, but treat page readability/rendering as client/manual validation, not backend closure.

### GAP-4: Baseline research assets are present; visual/manual validation remains open

**Статус:** baseline present; full visual/manual verification open
**Критичность:** low for backend, medium for final parity

Do not let asset completeness block backend progression unless it prevents opening or navigating the research system. Record visual/manual checks separately from backend completion.

### GAP-5A: Research note data flow exists, but end-to-end note gameplay is not validated

**Статус:** partial implementation; targeted non-GUI runtime tests passed; end-to-end validation open
**Критичность:** high

Current code has `ResearchNoteData`, note creation/update/read/write, and completion checks. Targeted non-GUI runtime coverage now proves:

- hidden discovery notes reveal into a real research note when an eligible hidden research exists;
- hidden discovery notes fall back to 7-9 knowledge fragments and consume the note when no eligible hidden research exists;
- completed notes require prerequisites at completion time and then grant the research key plus eligible siblings;
- the live-player note/discovery path no longer depends on username/cache lookups when a real `EntityPlayer` is already available.

Remaining work is to execute the normal gameplay route:

- primary research click creates a note rather than directly completing research;
- note has reference-compatible NBT keys and hex grid;
- research table solves the note using original-compatible connectivity rules;
- completed note grants research only after prerequisite checks;
- discovery/fragment behavior matches hidden/lost rules.

### GAP-5B: Research table C2S hardening is implemented; wider progression validation remains open

**Статус:** implementation hardened; targeted non-GUI runtime tests passed; live route/e2e validation open
**Критичность:** high

`PacketAspectPlaceToServer` and `PacketAspectCombinationToServer` now enforce the core server-authoritative guards required by the backend priority list:

- player has `ContainerResearchTable` open;
- container tile identity equals packet coordinates;
- tile is usable by player/distance check passes;
- target q/r exists in the current note hex grid;
- placed aspect is discovered and available from aspect pool or actual table bonus;
- bonus aspect consumption verifies current `bonusAspects` amount;
- aspect pool / bonus / ink costs are consumed atomically after validation;
- invalid packets cannot mutate note NBT.
- `TileResearchTable` bonus aspects are now explicitly presence-only at runtime, matching the original one-bit save/load contract, and targeted runtime tests cover both repeated recalc clamping and save/load round-trip symmetry.
- packet field-routing is now runtime-covered as well: matching `playerid`/`dim`/`coords` routes through the actual packet dispatch surface, while mismatched player/dimension/table coordinates are rejected before table mutation.

What remains open for this gap is proof around the live client route and broader adversarial coverage:

- validate the real GUI/research-table click path rather than packet field-routing/runtime-harness calls only;
- extend malicious-payload coverage beyond current player/dimension/coords dispatch guards into broader real client packet contexts;
- confirm the full note-solve/table-completion route under normal gameplay, not only isolated table placement/combination/persistence harnesses.

### GAP-6: Hidden/lost scan clues are wired, but prerequisite/runtime behavior needs verification

**Статус:** implementation aligned with reference; targeted non-GUI runtime tests passed; broader trigger/e2e validation open
**Критичность:** high

`ResearchManager.createClue(...)` exists and `ScanManager.completeScan(...)` forwards clue data after successful scans. Current targeted runtime coverage now proves:

- scan/discovery clue helpers grant only `@KEY` clue state and do not directly complete the full research;
- hidden discovery note reveal respects prerequisite-gated `findHiddenResearch(...)` selection when a live player already has the required parent knowledge;
- `findMatchingResearch(...)` and hidden discovery selection now read the live player capability directly instead of stale username/cache state.
- representative item-scan aspect triggers and legacy-name entity triggers now unlock hidden clue state through the real `ScanManager.completeScan(...)` path;
- knowledge-fragment fallback on discovery notes is runtime-covered when no hidden research is eligible.

Remaining work:

- verify `@KEY` clue state does not accidentally complete full research;
- expand runtime scenarios beyond the current focused harness into broader trigger-bearing content and full scan-to-clue progression;
- validate more real-content trigger cases beyond the current representative item/entity/aspect coverage.

### GAP-7: Research completion packet hardening is implemented; live route/e2e validation remains open

**Статус:** implementation hardened; targeted non-GUI runtime tests passed; live route/e2e validation open
**Критичность:** high

Do not describe `PacketPlayerCompleteToServer` as a direct-complete bypass anymore. It now checks research existence, duplicate completion, dimension/user, requisites, primary vs secondary action type, note creation, aspect cost, and sibling gating.

Targeted runtime coverage now proves:

- primary requests create a research note and do not grant the research key directly;
- missing prerequisites reject the request without consuming paper/ink or mutating progression;
- valid secondary requests consume aspect costs and unlock eligible siblings only after completion;
- wrong-type and insufficient-cost requests are rejected without mutation.

Remaining work:

- verify normal Thaumonomicon client actions actually send this packet with correct type semantics;
- validate the live packet-dispatch path, not only direct `processRequest(...)` runtime calls;
- confirm representative real research registrations behave the same way under the actual GUI/browser route before calling progression complete.

### GAP-9: Scan packet hardening is implemented; broader scan/progression validation remains open

**Статус:** implementation hardened; targeted non-GUI runtime tests passed; live route/e2e validation open
**Критичность:** high

`PacketScannedToServer` now rejects raw client-declared scan targets unless they match a live server-observed thaumometer scan. The current hardening covers:

- held thaumometer required on the server;
- only `@` prefix is accepted through the current thaumometer route;
- the server reruns the thaumometer target lookup and only accepts packet payloads that match the current server-observed block/entity/node scan;
- arbitrary item id/meta, entity id, or node-phenomena strings are rejected unless they match the authoritative server target;
- targeted non-GUI runtime tests cover valid block/entity/node matches, forged payload rejection, representative `ScanManager.completeScan(...)` item/entity/node execution, packet field-routing `playerid`/`dim` gates, dropped `EntityItem` authoritative scan matching, and awarded-aspect/clue progression side effects.

The live thaumometer route itself is now closer to the 1.7.10 reference again:

- `ItemThaumometer` no longer relies on the simplified `canBeCollidedWith()` player raypick for all entity scans; it restores the older permissive pointed-entity path (`minrange=0.5`, `range=10`, `padding=0`, `nonCollide=true`) so dropped item entities can be scanned again.
- target selection is split into a raw route and a validated route, so client HUD/readout, client use-tick scanning, and server authoritative revalidation no longer drift apart.
- client-side `blockRunes(...)` feedback is restored for valid entity/block/node scan candidates during use-tick scanning.

What remains open for this gap:

- validate the real GUI/client packet origin path, not just packet field-routing and extracted authority helpers;
- validate broader runtime scans end-to-end with awarded aspects and hidden/lost clue behavior;
- verify additional edge cases such as rapid aim changes, multi-hand thaumometer states, and any future non-thaumometer scan routes before calling the system complete.

### GAP-8: Manual/runtime validation for research content is not yet complete

**Статус:** partial implementation; validation open
**Критичность:** high

Current static validation covers the research corpus and asset references, but PRD still requires runtime/manual progression checks. After implementation gaps close, run focused validation and document results in this file.

## 6. Итоговый checklist закрытия Stage 9-e

- [x] Register six research categories and the reference-sized research corpus in category slice files.
- [x] Add baseline research note data model and NBT read/write/update flow.
- [x] Add scan-triggered hidden/lost clue wiring through `ResearchManager.createClue(...)`.
- [x] Harden `PacketPlayerCompleteToServer` against direct arbitrary completion for primary/secondary research flows.
- [ ] Validate full research graph against reference keys, parents, hidden parents, siblings, triggers, aspects, coordinates, complexity, flags, icons, and page order.
- [x] Harden research table aspect placement/combination packets with active-container, tile-identity, usable-distance, valid-hex, discovered-aspect, and atomic source-consumption checks.
- [x] Harden scan completion packet with held-thaumometer checks, server-observed target matching, and forged payload rejection coverage.
- [ ] Wire and validate normal Thaumonomicon/research-browser action flow.
- [ ] Validate end-to-end progression: scan -> clue/aspect -> note creation -> research table solve -> note completion -> unlock.
- [x] Fix and runtime-validate `TileResearchTable.bonusAspects` persistence semantics.
- [ ] Run runtime/manual validation for representative Thaumonomicon content and progression scenarios.

## 7. Definition of Done

Stage 9-e считается ПОЛНОСТЬЮ завершенной только если:

- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 9-e реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 9-e;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 9-e проверены вручную или тестами;
- `docs/Stage9-e.md` обновлен и не содержит критичных открытых вопросов.

## 8. Открытые вопросы

Stage 9 recipe chunks должны предоставить валидные recipe objects для referenced research-page recipe keys, а Stage 8 GUI может блокировать visual Thaumonomicon smoke. If visual smoke is blocked, record it as a client-smoke dependency, not as a reason to skip research graph validation.
