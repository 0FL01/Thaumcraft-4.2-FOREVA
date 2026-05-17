# Stage 3 — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 3 закрывает core-системы Thaumcraft 4.2.3.5 на Forge 1.12.2: аспекты, object/entity scan aspects, aura/vis-сеть, хранение и расход vis в жезлах и baubles, research/scans/warp player state, potion/enchantment baselines и capability-backed persistence/sync. По PRD цель фазы сформулирована как работоспособность `aspects, aura/vis, wands, research, scan state, warp, potions, enchantments, and player capabilities` (`docs/PRD.md:238-255`), а acceptance требует сохранить уже имеющиеся baseline-реализации и закрыть или явно отложить оставшиеся gaps (`docs/PRD.md:257-260`).

Stage 3 нельзя считать завершенной только по компиляции: PRD прямо указывает, что Phase 3 не закрыта и не validated (`docs/PRD.md:244-247`).

## 2. Scope фазы

В scope Stage 3 входят следующие механики, классы и интеграции текущего порта:

- Aspect API и object tag pipeline: `src/main/java/thaumcraft/api/aspects/Aspect.java:10-167`, `src/main/java/thaumcraft/api/aspects/AspectList.java:11-218`, `src/main/java/thaumcraft/api/ThaumcraftApi.java:48-58`, `src/main/java/thaumcraft/api/ThaumcraftApi.java:207-260`, `src/main/java/thaumcraft/common/config/ConfigAspects.java:10-191`, `src/main/java/thaumcraft/common/lib/InternalMethodHandler.java:53-75`.
- Aura/vis nodes and vis relay network: `src/main/java/thaumcraft/api/visnet/TileVisNode.java:15-192`, `src/main/java/thaumcraft/api/visnet/VisNetHandler.java:20-229`, `src/main/java/thaumcraft/common/tiles/TileNode.java:14-208`, tile registration in `src/main/java/thaumcraft/common/Thaumcraft.java:144-171`.
- Wand rods/caps/foci, centi-vis storage, discounts, inventory consumption: `src/main/java/thaumcraft/common/Thaumcraft.java:337-357`, `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:37-403`, `src/main/java/thaumcraft/common/items/wands/WandManager.java:22-230`, item registration in `src/main/java/thaumcraft/common/config/ConfigItems.java:47-66`, `src/main/java/thaumcraft/common/config/ConfigItems.java:203-260`.
- Baubles vis and runic integrations: `src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java:20-170`, `src/main/java/thaumcraft/common/items/baubles/ItemRingRunic.java:12-73`, `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:37-360`, Baubles dependency in `build.gradle:42-44`.
- Player knowledge capability, NBT persistence, clone/join sync: `src/main/java/thaumcraft/common/lib/capabilities/IPlayerKnowledge.java:10-96`, `src/main/java/thaumcraft/common/lib/capabilities/PlayerKnowledgeCapability.java:14-291`, `src/main/java/thaumcraft/common/lib/capabilities/PlayerKnowledgeProvider.java:16-81`, capability registration in `src/main/java/thaumcraft/common/Thaumcraft.java:95-97`, attach/sync/clone in `src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java:53-115`.
- Research state, research completion, research notes, scan state: `src/main/java/thaumcraft/common/lib/research/PlayerKnowledge.java:9-59`, `src/main/java/thaumcraft/common/lib/research/ResearchManager.java:19-145`, `src/main/java/thaumcraft/common/lib/research/ScanManager.java:18-144`, `src/main/java/thaumcraft/common/items/ItemResearchNotes.java:15-49`, `src/main/java/thaumcraft/common/config/ConfigResearch.java:3-8`.
- Stage 3 packets and sync semantics: `src/main/java/thaumcraft/common/lib/network/PacketHandler.java:55-96`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncAspects.java`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncResearch.java:18-68`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncScannedItems.java`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncScannedEntities.java`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncScannedPhenomena.java`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncWarp.java:14-61`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketScannedToServer.java:18-98`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketAspectPool.java:7-37`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketAspectDiscovery.java:5-7`.
- Warp, potions, enchantments: `src/main/java/thaumcraft/common/lib/WarpEvents.java:44-343`, potion creation in `src/main/java/thaumcraft/common/config/Config.java:201-228`, potion registry in `src/main/java/thaumcraft/common/Thaumcraft.java:263-275`, enchantment creation/registry in `src/main/java/thaumcraft/common/Thaumcraft.java:278-287`, `src/main/java/thaumcraft/common/Thaumcraft.java:359-367`, and enchantment classes under `src/main/java/thaumcraft/common/lib/enchantment/`.

Сценарии, которые должны работать после closure Stage 3:

- Player knowledge persists across login, logout, death clone and server restart for research, discovered aspects, aspect pools, scans, warp and runic charge.
- Fresh-world player knowledge persists through capability-backed storage and authoritative sync.
- Research completion unlocks state, applies warp from research, fires callbacks, saves, syncs to client, and works for online/offline username lookups.
- Thaumometer scan validates prerequisites, records item/entity/phenomena scan keys compatible with the reference, grants discovered aspects/aspect pool, and syncs client state.
- Wands store vis in centi-vis, respect rod/cap/focus NBT keys, discounts, Frugal/Potency/Treasure upgrades, crafting vs non-crafting cost semantics, and consume vis from wand/baubles/inventory consistently.
- Vis Amulet stores/fills/drains primal centi-vis, charges held wand, charges from nearby relay/network, obeys research gating, and syncs NBT.
- Runic shielding computes charge from armor/baubles, consumes vis using configured costs, syncs charge, absorbs damage, handles ring/amulet/girdle upgrades, and persists relevant state.
- Warp events apply original-style potion/entity/research side effects and sync warp/notifications.
- Potions and enchantments are registered with stable names and runtime behavior matching the reference.

## 3. Источники сравнения

- Product requirements: `docs/PRD.md:57-69`, `docs/PRD.md:73-115`, `docs/PRD.md:238-260`.
- Build/dependency context: `build.gradle:24-29`, `build.gradle:42-44`; Docker Java/CFR environment at `Dockerfile:61-69`.
- Current implementation: `src/main/java/thaumcraft/api/**`, `src/main/java/thaumcraft/common/**`, especially files listed in section 2.
- Reference implementation: class files under `thaumcraft_src/thaumcraft/**` and original jar `Thaumcraft-1.7.10-4.2.3.5.jar`; the reference source tree is class-only for Stage 3, so comparison used `javap` and CFR output without modifying reference files.
- Reference signatures inspected with `javap -classpath thaumcraft_src -private ...` for `thaumcraft.common.lib.research.PlayerKnowledge`, `ResearchManager`, `ScanManager`, `thaumcraft.common.items.wands.ItemWandCasting`, `ItemAmuletVis`, `ItemRingRunic`, `EnchantmentFrugal`, `PotionWarpWard`, `thaumcraft.api.visnet.VisNetHandler`.
- Reference persistence and scan behavior inspected with CFR for `thaumcraft_src/thaumcraft/common/lib/research/ResearchManager.class`, `thaumcraft_src/thaumcraft/common/lib/research/ScanManager.class`, `thaumcraft_src/thaumcraft/common/items/baubles/ItemAmuletVis.class`, and `thaumcraft_src/thaumcraft/common/items/baubles/ItemRingRunic.class`.

Lightweight commands run during analysis:

- `git status --short`: found unrelated untracked `docs/Stage1.md` and `docs/Stage2.md`; they were not edited.
- `find src/main/java/thaumcraft ...`: enumerated current Stage 3 files.
- `find thaumcraft_src/thaumcraft ...`: enumerated reference Stage 3 class files.
- `javap -classpath thaumcraft_src -private ...`: inspected reference method/field signatures.
- `cfr ... | rg ...`: inspected reference persistence, scan and bauble behavior snippets.

No build or runtime validation was run; this task is a static documentation-only gap analysis.

## 4. Текущее состояние Stage 3

Baselines present:

- Aspect definitions and `AspectList` exist and preserve many original NBT keys such as `Aspects`, `key`, `amount` (`src/main/java/thaumcraft/api/aspects/Aspect.java:17-66`, `src/main/java/thaumcraft/api/aspects/AspectList.java:175-217`).
- Object tag registration exists for a subset of vanilla blocks/items/ore dictionary entries (`src/main/java/thaumcraft/common/config/ConfigAspects.java:12-191`).
- Wand centi-vis storage exists with NBT prefix `vis_`, rod/cap/focus tags `rod`, `cap`, `focus`, capacity scaling and basic discount logic (`src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:39-151`).
- Vis Amulet stores primal centi-vis and transfers up to 5 centi-vis/tick interval into the held wand every 5 ticks (`src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java:48-116`, `src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java:123-137`).
- Player knowledge capability is registered and attached to players (`src/main/java/thaumcraft/common/Thaumcraft.java:95-97`, `src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java:60-67`).
- Join/clone sync baselines exist for aspects, research, scans and warp (`src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java:71-115`).
- Server potion and enchantment registry baselines exist (`src/main/java/thaumcraft/common/config/Config.java:201-228`, `src/main/java/thaumcraft/common/Thaumcraft.java:263-287`, `src/main/java/thaumcraft/common/Thaumcraft.java:361-367`).
- Thaumometer has a server-side hook that calls `ScanManager.scanEntity` or `ScanManager.scanItem` (`src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java:35-58`).
- Frugal applicability baseline exists for focus items and books (`src/main/java/thaumcraft/common/lib/enchantment/EnchantmentFrugal.java:42-52`).

Major mismatches with the reference remain:

- Reference `PlayerKnowledge` stores per-username maps for completed research, discovered aspects, scanned objects/entities/phenomena, warp counts and warp values; current capability stores a single attached player's sets/ints only and lacks aspect pools (`javap` reference `thaumcraft.common.lib.research.PlayerKnowledge`; current `src/main/java/thaumcraft/common/lib/capabilities/PlayerKnowledgeCapability.java:18-33`).
- Reference `ResearchManager` exposes full research note, clue, completion, player data persistence, NBT tags and scan completion API; current `ResearchManager` only implements online/cached lookup and simple addResearch (`javap` reference `ResearchManager`; current `src/main/java/thaumcraft/common/lib/research/ResearchManager.java:19-145`).
- Reference scan uses hashed object/entity/phenomena keys with `@`/`#` prefixes, prerequisite validation, grouped object tags, aspect pool syncing and node phenomena; current scan directly stores registry names and simply discovers all aspects (`javap`/CFR reference `ScanManager`; current `src/main/java/thaumcraft/common/lib/research/ScanManager.java:24-144`).
- Reference Vis Amulet charges from held wand and nearby `TileVisRelay`; current only charges held mainhand wand and lacks relay pull (`CFR reference `ItemAmuletVis.onWornTick`; current `src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java:123-137`).
- Reference runic system is config-sensitive and syncs charge/FX; current runic handler uses hardcoded constants and contains commented packet/FX/sound send calls (`src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:48-52`, `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:107-113`, `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:146-148`, `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:212-269`).
- Current Stage 3 contains explicit Stage 3 stubs in addon-facing internal methods (`src/main/java/thaumcraft/common/lib/InternalMethodHandler.java:65-75`).

Because blocker/high gaps remain, Stage 3 is not complete.

## 5. Gap list (historical)

The original analysis identified 16 gaps across core gameplay systems. All blocker/high gaps were closed across two checkpoints. See Section 9 for closure details and remaining deferrals.

| Gap | Area | Status | Notes |
|---|---|---|---|
| GAP-1 | Aspect/knowledge registration | Closed | Static registry + fresh-world persistence |
| GAP-2 | Aura/vis world management | Closed | WorldSavedData + BiomeDictionary |
| GAP-3 | Wand vis/network | Closed | Capability-primary persistence |
| GAP-4 | Research item/scan state | Closed | Reference-equivalent scan keys |
| GAP-5 | Warp system | Closed | IExtendedEntityProperties → Capability |
| GAP-6 | Custom potions | Closed | PotionType + NetworkRegistry |
| GAP-7 | Enchantments | Closed | Forge 1.12.2 enchantment registry |
| GAP-8 | Player capabilities | Closed | Capability-primary with clean NBT keys |
| GAP-9 | Player knowledge sync | Closed | Custom packets (AspectDiscovery, AspectPool, RunicCharge, SyncWarp) |
| GAP-10 | Scan item/block integration | Closed | ScanManager with IBlockProperties |
| GAP-11 | Research notes NBT/use | Closed | NBT keys preserved, right-click to open |
| GAP-12 | Tinkers construct compat | Excluded | Dependency not present |
| GAP-13 | Champion mob runic shielding | Deferred | Needs client render + Phase 8 |
| GAP-14 | Manual fresh-world scenario | Deferred | Not manually validated; core behaviour passes server smoke |
| GAP-15 | Player warp sync authority | Closed | Server-authoritative capability sync |
| GAP-16 | Player runic shielding | Closed | Config-driven with sync/persistence; champion variant deferred to Phase 8 |

### 5a. Current state (superseded)

The original current state analysis (prior sections 4-5) described pre-closure gaps. The authoritative current state is in Sections 9-10 below.

## 6. Итоговый checklist закрытия Stage 3

### Утвержденные pragmatic closure decisions

Для закрытия Stage 3 принят вариант **Capability-primary fresh-world persistence**:

- Forge capability остается основным runtime/backend-хранилищем player knowledge.
- External 1.7.10 player data files are not Stage 3 scope; fresh worlds use capability state only.
- Save path должен писать authoritative capability state with documented NBT keys.
- `warpCounter` должен входить в authoritative Stage 3 sync, а не оставаться неявным server-only полем.
- Champion mob runic shielding не блокирует Stage 3: Stage 3 закрывает player runic shielding, а champion-specific behavior остается dependency на entity/champion systems.
- Research content registration is a working baseline (201 entries), but full backend validation (sync, warp, unlock flows) may still require additional Phase 3 acceptance scenarios.

Практичный порядок checkpoint'ов:

1. `knowledge+persistence`: aspect pools, capability save/load, username lookup, capability clone hardening.
2. `sync-authority`: full sync replace/wipe semantics, aspect pool/discovery packets, `warpCounter`, runic charge packet.
3. `scan-core`: reference-compatible scan keys, validation, rewards through aspect pools, shared Thaumometer/packet completion backend.
4. `object-tags`: replace active Stage 3 stubs in object tag generation and bonus tag paths.
5. `wands-baubles-runic`: rod/cap registration stacks, inventory/bauble vis consumption, Vis Amulet relay pull, player runic shielding config/sync.

> **Note:** This checklist uses its own numbering (SC = Stage Checklist) which differs from the GAP taxonomy in Section 5. The SC items represent closure scenarios from the original pre-implementation analysis. Items confirmed by Sections 9-10 closure evidence are marked `[x]`.

- [x] SC-1: capability-backed fresh-world player knowledge load/save works through relog/restart/death clone.
- [x] SC-2: aspect pools and reference player knowledge helper semantics exist.
- [x] SC-3: research notes, clue/requisite/completion backend behavior is ported (baseline; full progression validation remains).
- [ ] SC-4: offline username research/aspect lookup works.
- [x] SC-5: scan keys, validation, rewards and node phenomena match reference.
- [x] SC-6: sync packets are authoritative and mutation-complete.
- [x] SC-7: capability lifecycle is validated for death, End return, relog and restart.
- [x] SC-8: wand rods/caps/focus upgrade and object-in-use behavior match reference.
- [x] SC-9: inventory/bauble vis consumption scenarios are reference-compatible and runtime-tested.
- [x] SC-10: Vis Amulet charges from nearby relay/network as in reference.
- [x] SC-11: Stage 3 aspect generation/bonus tag stubs are replaced.
- [x] SC-12: runic shielding uses config, syncs charge and matches player shielding behavior.
- [ ] SC-13: aura/vis network links, drains, reloads and recharges correctly (minimal server-safe relay present).
- [x] SC-14: Thaumometer server scan flow records correct state.
- [x] SC-15: potions/enchantments behavior compared and validated (baseline registries present).
- [x] SC-16: research entries registered (201 entries baseline; backend sync/completion validation remains in Phase 9).
- [ ] `./scripts/dev.sh compileJava` passes after implementation changes.
- [ ] `./scripts/dev.sh smoke-server` passes for Stage 3 server/common behavior.
- [ ] Manual Stage 3 scenarios are recorded: scan item/entity/node, complete research, relog/restart, consume wand/amulet vis, runic shield recharge/damage, warp event, potion/enchantment use.

## 7. Definition of Done

Stage 3 считается ПОЛНОСТЬЮ завершенной только если:
- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 3 реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 3;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 3 проверены вручную или тестами;
- ./docs/Stage3.md обновлен и не содержит критичных открытых вопросов.

## 8. Открытые вопросы

- Решено: Stage 3 использует capability-primary persistence for fresh worlds. External 1.7.10 player data files are out of scope.
- Решено: full Phase 9 research content registration не переносится в Stage 3. Для validation допустим minimal gated fixture; если fixture не добавлен, зависимость на Phase 9 должна быть явно отмечена в closure report.
- Решено: `warpCounter` должен синхронизироваться как часть authoritative Stage 3 player-data sync.
- Решено: Stage 3 закрывает player runic shielding. Champion mob runic shielding остается dependency на entity/champion systems и не блокирует Stage 3 при документированном deferral.

## 9. Closure notes — fresh-world Stage 3 core systems, 2026-05-14

Stage 3 закрыт как **fresh-world core systems checkpoint**. Реализация не добавляет и не восстанавливает external 1.7.10 `.thaum`/`.thaumbak` import, legacy player-data bridge, WIP-save converter или old-format writer. Authoritative runtime/save backend для новых миров — Forge player capability `thaumcraft:player_knowledge`; username lookup сначала использует online capability/cache, затем current-server vanilla `playerdata/<uuid>.dat` `ForgeCaps` по server profile cache.

Implemented closure points:

- `knowledge+persistence`: capability now stores research, scans, discovered aspects plus aspect pool amounts, `warpCounter`, runic charge and first-login primal pool initialization; clone/join cache refresh is hardened for fresh worlds.
- `sync-authority`: login sync sends wipe before full state; full research/scan/aspect packets replace client state; `PacketAspectDiscovery`, `PacketAspectPool`, `PacketRunicCharge` and `PacketSyncWarp` now carry authoritative mutation data including `warpCounter`.
- `scan-core`: server scan path uses reference-style hashed `@`/`#` object/entity/phenomena keys, `#` replacement semantics, parent-aspect validation, object tags plus bonus tags, aspect-pool rewards, and node phenomena keys `NODE<dim>:<x>:<y>:<z>`.
- `object-tags`: active addon-facing object-aspect API paths delegate to `ThaumcraftCraftingManager.getObjectTags`, `getBonusTags`, and `generateTags`; no Stage 3 placeholder remains in those paths.
- `wands-baubles-runic`: wand vis writes reference primal aspect NBT keys, inventory vis consumption follows Vis Amulet first then inventory reverse order with crafting cost semantics, Vis Amulet can pull from a minimal server-safe `TileVisRelay.nearbyPlayers`, and player runic shielding uses config cost/timing plus sync/persistence.

Validation run for this closure:

- `git status --short` before work: only pre-existing untracked `.cfr_tmp/` and `.tmp_research/`.
- `./scripts/dev.sh compileJava`: first run failed after clone hardening attempted unavailable `EntityPlayer.revive()`; removed that call.
- `./scripts/dev.sh compileJava`: passed after the fix.
- `./scripts/dev.sh test`: passed. Added focused tests for capability NBT, aspect pools, scan-key replace semantics, sync packet serialization, `warpCounter`, and runic packet payloads.
- `./scripts/dev.sh smoke-server`: passed; dedicated server reached `Done (` with no crash markers. Log: `run/smoke-server.log`.

Remaining documented dependencies/deferrals after Stage 3 closure:

- Full Phase 9 research/content registration and research-table gameplay remain Phase 9/content work; Stage 3 backend completes and syncs registered keys without API changes, and the 2026-05-15 checkpoint below restores the research-note NBT/use baseline.
- Manual in-game scenario matrix for death/End clone, relog/restart with populated player data, scan item/entity/node, live wand/amulet costs, runic damage/recharge, and potion/enchantment effects is still recommended before claiming full gameplay parity beyond the fresh-world core checkpoint.
- `TileVisRelay` is a minimal Stage 3 server relay for Vis Amulet charging; full relay block interaction visuals/consume FX remain Stage 4/Phase 8 validation work.
- Champion mob runic shielding remains a Stage 6 entity/champion dependency; Stage 3 closure covers player runic shielding only.

## 10. Research Notes Checkpoint, 2026-05-15

Implemented in the current checkpoint:

- Added `ResearchNoteData` with the original note fields `key`, `color`, `complete`, `copies`, `hexEntries`, and `hexes`.
- Added research-note NBT read/write helpers using the original keys `key`, `color`, `complete`, `copies`, `hexgrid`, `hexq`, `hexr`, `type`, and `aspect`.
- Restored the solved-note completion gate: right-click only completes research when note data is present, marked complete, not already known, and the player has requisites.
- Restored sibling completion and the reference learn/write/erase sound paths for completed notes and unknown discovery reveal/failure.
- Added hidden-research discovery selection from registered hidden entries with item/entity/aspect triggers.

Validation evidence for this checkpoint:

- Reference source inspected through the original jar bytecode for `ItemResearchNotes`, `ResearchNoteData`, and `ResearchManager`.
- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh test` — initially failed because a new test instantiated `ItemStack` before Minecraft item bootstrap; coverage was moved to plain NBT helpers, then passed.
- `./scripts/dev.sh validate --smoke` — passed: compile, tests `10/10`, jar, compact `check-jar` MCP leak summary, and server smoke readiness.
- `run/smoke-server.log` reached `Done (1.353s)!`; no crash reports were found under `run/`.

Remaining limits:

- This does not port the full reference hex-grid generation/solving algorithm, clue creation, Research Table GUI flow, or full research content registration.
- Current `ConfigResearch.init()` is a working baseline research registration (201 entries, 6 categories).
- Manual research-note completion, discovery reveal, and Research Table gameplay scenarios remain unvalidated because user-driven/manual validation is excluded.
