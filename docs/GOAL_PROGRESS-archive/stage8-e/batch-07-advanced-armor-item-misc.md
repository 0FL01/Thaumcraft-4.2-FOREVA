# Stage 8-e Batch 07 — GAP-11 Advanced Armor & Misc Item Contracts

11 checkpoints covering fortress-armor tooltip/lang, void-armor core, traveller-boots hover, hover-harness core, bucket-pure use-result, research-notes consumption, eldritch-object tooltip + use-first, loot-bag consumption, mana-bean use-result, and essence-phial client result contracts.

---

#### Checkpoint 2026-05-17 — GAP-11 fortress-armor tooltip/lang contracts baseline

Статус: частично продвинут.

Что сделано:

- `ItemFortressArmor` получил reference-shaped tooltip contracts для helm upgrades:
  - в `addInformation(...)` восстановлены `goggles`/`mask` NBT tooltip ветки;
  - goggles tooltip использует `item.ItemGoggles.name`;
  - mask tooltip использует `item.HelmetFortress.mask.<id>`.
- В `en_us.lang` добавлены отсутствующие tooltip keys из reference baseline:
  - `item.ItemGoggles.name`;
  - `item.HelmetFortress.mask.0..2`.
- `ItemFortressArmorCoreContractsStaticGuardTest` расширен проверками tooltip contracts и присутствия новых lang-ключей.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это non-GUI tooltip/lang baseline; визуальная model/icon-equipped parity fortress armor остаётся в Stage 8 client-render scope.

---

#### Checkpoint 2026-05-17 — GAP-11 void-armor core contracts baseline

Статус: частично продвинут.

Что сделано:

- Восстановлены reference-shaped core contracts для void armor family:
  - `ItemVoidArmor`: `UNCOMMON` rarity baseline;
  - `ItemVoidRobeArmor`: `EPIC` rarity, расширенная interface surface `IGoggles` + `IRevealer` + `ISpecialArmor`, vis-discount tooltip line, helmet-only revealer gates (`showNodes/showIngamePopups`), и special-armor mitigation hooks (`getProperties/getArmorDisplay/damageArmor`).
- Сохранены существующие void-ingot repair/self-repair и warp/vis contracts.
- Добавлен `ItemVoidArmorCoreContractsStaticGuardTest` для фиксации этих контрактов.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common/server+non-GUI tooltip baseline; полный client model/icon overlay parity для void robe остаётся в Stage 8 client-render scope.

---

#### Checkpoint 2026-05-17 — GAP-11 traveller-boots hover core contracts baseline

Статус: частично продвинут.

Что сделано:

- `Hover.doHover(...)` приведён к reference-shaped core movement contracts для `ItemBootsTraveller`:
  - creative+forward gate;
  - client-side step-height boost (`1.0F`) с восстановлением предыдущего значения;
  - on-ground bonus thrust (`moveRelative(..., bonus)` с water penalty);
  - airborne movement factor branch (`0.03F/0.05F` через hover state);
  - gradual fall-distance dampening (`-0.25F` с нижней границей `0`).
- `Hover.resetHover(...)` теперь использует сохранённый previous-step restore path вместо жёсткого reset.
- Расширен `ItemRobeTravellerArmorCoreContractsStaticGuardTest`:
  - фиксирует связь `ItemBootsTraveller.onArmorTick -> Hover.doHover(...)`;
  - фиксирует ключевые movement contracts в `Hover`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common/server+non-GUI contract baseline; client/manual movement-feel parity boots в реальной игре остаётся в manual runtime зоне Stage 5.

---

#### Checkpoint 2026-05-17 — GAP-11 hover-harness core contracts baseline

Статус: частично продвинут.

Что сделано:

- `ItemHoverHarness` доведён до reference-shaped server/common contract в right-click GUI path:
  - координаты открытия GUI теперь вычисляются через `MathHelper.floor(...)` вместо `(int)` cast, что сохраняет correct floor-behavior на отрицательных координатах.
- Добавлен `ItemHoverHarnessCoreContractsStaticGuardTest` для фиксации core contracts:
  - `EPIC` rarity + iron-ingot repair key;
  - vis-discount baseline (`Aer=5`, иначе `2`);
  - creative gate + `Hover.handleHoverArmor(...)` dispatch;
  - server-side `GUI_HOVER_HARNESS` open path с `MathHelper.floor(...)`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common/server+non-GUI contract baseline; полноценная client/manual hover toggle/sound/tooltip visual parity остаётся в Stage 5/8 manual зоне.

---

#### Checkpoint 2026-05-17 — GAP-11 bucket-pure use-result contracts baseline

Статус: частично продвинут.

Что сделано:

- `ItemBucketPure.onItemRightClick(...)` приведён к reference-shaped use-result semantics:
  - ветки без фактического действия (no-hit / no-edit / no-place) теперь возвращают `PASS` вместо `FAIL`;
  - успешный place сохраняет существующий split: creative — исходный stack, survival — `Items.BUCKET`.
- Добавлен `ItemBucketPureCoreContractsStaticGuardTest`, фиксирующий:
  - raytrace/no-op return contracts;
  - creative/survival result split;
  - pure-fluid source-state placement contract (`BlockFluidPure.SOURCE_LEVEL`).

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common/server+non-GUI baseline; runtime/manual сценарии с pickup/collision/WarpWard всё ещё остаются в Stage 5 manual зоне.

---

#### Checkpoint 2026-05-17 — GAP-11 research-notes core consumption contracts baseline

Статус: частично продвинут.

Что сделано:

- `ItemResearchNotes` приведён к reference-shaped consumption/result contracts:
  - при отсутствии requisites completion path теперь возвращает нейтральный `PASS` (вместо `FAIL`);
  - расход note (`stack.shrink(1)`) восстановлен как unconditional в двух reference-flow ветках:
    - успешный completion note;
    - unknown discovery path с `findHiddenResearch == "FAIL"` и выдачей fragments.
- Добавлен `ItemResearchNotesCoreContractsStaticGuardTest`, фиксирующий:
  - meta/rarity split (`24/42/64`, `RARE/EPIC`);
  - requisites-miss `PASS` contract;
  - mandatory note-consumption и hidden-fragment fallback contract.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common/server+non-GUI baseline; полная research-note gameplay/GUI/manual parity (table interaction/hex puzzle flow) остаётся зависимостью Stage 3/8/9 runtime/manual сценариев.

---

#### Checkpoint 2026-05-17 — GAP-11 eldritch-object tooltip/lang contracts baseline

Статус: частично продвинут.

Что сделано:

- `ItemEldritchObject` получил reference-shaped metadata tooltip branches в `addInformation(...)`:
  - восстановлены текстовые ветки для `meta 0..3`;
  - для `meta 4` восстановлен tooltip `Creative Mode Only`.
- В `en_us.lang` добавлены отсутствующие reference keys:
  - `item.ItemEldritchObject.text.1..6`.
- Добавлен `ItemEldritchObjectCoreContractsStaticGuardTest`, фиксирующий:
  - subtype/rarity split (`UNCOMMON/RARE/EPIC`);
  - `CRIMSON` unlock right-click path;
  - tooltip branch presence + lang key presence.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это non-GUI tooltip/lang baseline; manual runtime parity для primordial pearl node mutation/flux scatter и creative obelisk placement остаётся отдельной Stage 5/6 runtime задачей.

---

#### Checkpoint 2026-05-17 — GAP-11 eldritch-object use-first side-result contracts baseline

Статус: частично продвинут.

Что сделано:

- `ItemEldritchObject.onItemUseFirst(...)` выровнен по reference side-result semantics:
  - для `META_ELDRITCH_OBJECT_3` (primordial pearl/node path): server branch возвращает `SUCCESS`, client branch — `PASS`;
  - для `META_OB_PLACER` (creative obelisk path): server branch возвращает `SUCCESS`, client branch — `PASS`.
- Логика mutate/place оставлена server-only как ранее, изменения касаются только result-contract согласования между сторонами.
- `ItemEldritchObjectCoreContractsStaticGuardTest` расширен проверками этих side-result контрактов.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это non-GUI side-result baseline; manual runtime parity для полного node-mutation/scatter и obelisk placement scenarios остаётся отдельной Stage 5/6 runtime задачей.

---

#### Checkpoint 2026-05-17 — GAP-11 loot-bag consumption contracts baseline

Статус: частично продвинут.

Что сделано:

- `ItemLootBag.onItemRightClick(...)` приведён к reference-shaped consumption timing:
  - расход stack (`stack.shrink(1)`) вынесен из server-only блока и теперь выполняется всегда после использования.
- Добавлен `ItemLootBagCoreContractsStaticGuardTest`, фиксирующий:
  - subtype/rarity split (`common/uncommon/rare`);
  - loot-roll baseline (`8 + rand.nextInt(5)`) и `Utils.generateLoot(...)` dispatch;
  - post-use consumption placement outside `if (!world.isRemote)` branch;
  - наличие loot-bag lang/tooltip keys.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common/server+non-GUI baseline; полный loot-table distribution parity и runtime/manual opening scenarios остаются зависимостью Stage 9 content + manual validation.

---

#### Checkpoint 2026-05-17 — GAP-11 mana-bean use-result contracts baseline

Статус: частично продвинут.

Что сделано:

- `ItemManaBean.onItemUse(...)` выровнен по reference-shaped interaction semantics:
  - invalid/no-op gates (`cannot edit`, `wrong side`, `non-magical biome`, invalid support) теперь возвращают `PASS` вместо `FAIL`;
  - при валидной точке роста и занятой нижней клетке результат теперь `SUCCESS` (action-consumed contract как в reference boolean-path).
- Существующая server-side логика установки `blockManaPod`, переноса аспекта в `TileManaPod` и расхода stack сохранена.
- Добавлен `ItemManaBeanCoreContractsStaticGuardTest`, фиксирующий:
  - `IEssentiaContainerItem`/aspect container baseline;
  - use-result semantics (PASS gates + SUCCESS occupied-place path);
  - magical-biome/support gating;
  - наличие tooltip/lang baseline (`tc.aspect.unknown`, `mana_bean`).

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common/server+non-GUI baseline; full mana-pod growth/harvest gameplay parity и client visual parity остаются в runtime/manual Stage 5/8 зоне.

---

#### Checkpoint 2026-05-17 — GAP-11 essence-phial client result contracts baseline

Статус: частично продвинут.

Что сделано:

- `ItemEssence` выровнен по reference-shaped client result semantics для fill/empty preview path:
  - в `fillPhialFromContainer(...)` client-side swing ветка теперь возвращает `PASS` (вместо `SUCCESS`);
  - в `emptyPhialIntoJar(...)` client-side swing ветка теперь возвращает `PASS` (вместо `SUCCESS`).
- Server-side логика transfer/shrink/drop/sound/container-sync не менялась.
- Добавлен `ItemEssenceCoreContractsStaticGuardTest`, фиксирующий:
  - phial amount/subtype baseline (`PHIAL_AMOUNT=8`, `meta 0/1`);
  - fill/empty transfer path contracts;
  - client preview `swing + PASS` contracts;
  - tooltip/lang baseline keys.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common/server+non-GUI baseline; полная in-world/manual parity по alembic/jar fill/empty сценариям остаётся отдельной runtime/manual задачей Stage 5.
