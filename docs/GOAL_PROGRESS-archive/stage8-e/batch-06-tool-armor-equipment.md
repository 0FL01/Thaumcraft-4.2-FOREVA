# Stage 8-e Batch 06 — GAP-11 Tool & Armor Equipment Contracts

8 checkpoints covering thaumium-tools, void/crimson equipment, primal-arrow subtype, bone-bow draw, thaumium-armor/goggles, cultist-armor, robe/traveller-armor, and fortress-armor core contracts.

---

#### Checkpoint 2026-05-17 — GAP-11 thaumium-tools rarity/repair contract baseline

Статус: частично продвинут.

Что сделано:

- Восстановлены reference-shaped базовые контракты для `ItemThaumium*` equipment:
  - `ItemThaumiumSword`: `UNCOMMON` rarity + thaumium repair (`itemResource:2`);
  - `ItemThaumiumAxe`: `axe` toolClass + `UNCOMMON` rarity + thaumium repair;
  - `ItemThaumiumPickaxe`: `pickaxe` toolClass + `UNCOMMON` rarity + thaumium repair;
  - `ItemThaumiumShovel`: `shovel` toolClass + `UNCOMMON` rarity + thaumium repair;
  - `ItemThaumiumHoe`: enchantability `5` + `UNCOMMON` rarity + thaumium repair.
- Добавлен `ItemThaumiumToolsStaticGuardTest` для фиксации этих family-contracts.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это контрактный baseline без ручной gameplay проверки tool-balance; combat/efficiency runtime parity остаётся в общей Stage 5/6 manual зоне.

---

#### Checkpoint 2026-05-17 — GAP-11 void/crimson equipment debuff+repair baseline

Статус: частично продвинут.

Что сделано:

- Восстановлены reference-shaped core contracts для void/crimson equipment family:
  - `ItemVoidSword`: `UNCOMMON` rarity + pvp-gated combat-debuff helper surface (`canApplyVoidCombatDebuff`, `tryApplyVoidWither`) + sword-hit wither baseline (`60` ticks);
  - `ItemVoidAxe`/`ItemVoidPickaxe`/`ItemVoidShovel`/`ItemVoidHoe`: `UNCOMMON` rarity, toolClass contracts (`axe`/`pickaxe`/`shovel`), `VoidHoe` enchantability `5`, pvp-gated wither-on-hit baseline (`80` ticks), shared self-repair baseline (`repairVoid`) и shared void-charm repair key;
  - `ItemCrimsonSword`: `IWarpingGear` surface, `RARE` rarity, void-charm repair key, pvp-gated dual-debuff hit baseline (`WITHER 60`, `WEAKNESS 120`), shared self-repair baseline, warp `2`.
- Добавлен `ItemVoidCrimsonToolsStaticGuardTest` для фиксации family contracts.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это gameplay-contract baseline; client tooltip/icon parity для void/crimson equipment остаётся отдельным Stage 8 renderer/UI polish.

---

#### Checkpoint 2026-05-17 — GAP-11 primal-arrow subtype registry baseline

Статус: частично продвинут.

Что сделано:

- `ItemPrimalArrow.getSubItems(...)` восстановлен до reference-shaped subtype coverage:
  - creative listing теперь добавляет все primal variants `meta 0..5` (вместо только `0`).
- Добавлен `ItemPrimalArrowStaticGuardTest`, фиксирующий:
  - subtype/max-stack init surface;
  - translation-key metadata suffix;
  - `meta 0..5` creative-variant contract.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это metadata-availability baseline; визуальная икон-парити primal arrow variants остаётся в client-render/manual зоне.

---

#### Checkpoint 2026-05-17 — GAP-11 bone-bow core draw contracts baseline

Статус: частично продвинут.

Что сделано:

- `ItemBowBone` приведён к reference-shaped core contracts:
  - durability baseline `setMaxDamage(512)` (вместо `500`);
  - enchantability baseline `3` (вместо `15`);
  - сохранён bone repair contract (`Items.BONE`);
  - добавлен early-release draw hook в `onUsingTick(...)` (`ticks > 18 -> stopActiveHand()`).
- Добавлен `ItemBowBoneStaticGuardTest` для фиксации этих контрактов.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это server/common contract baseline без полного портирования legacy custom bow charge/arrow event internals; детальная projectile-feel parity остаётся на отдельный gameplay polish.

---

#### Checkpoint 2026-05-17 — GAP-11 thaumium-armor/goggles repair+rarity baseline

Статус: частично продвинут.

Что сделано:

- Восстановлены базовые reference-shaped контракты для двух armor-классов:
  - `ItemThaumiumArmor`: `UNCOMMON` rarity + thaumium repair key (`itemResource:2`);
  - `ItemGoggles`: `RARE` rarity + leather repair key (`Items.LEATHER`).
- Добавлен `ItemArmorRepairRarityStaticGuardTest` для фиксации этих контрактов.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это только repair/rarity baseline; дополнительные armor tooltip/icon paths остаются в общем Stage 8 client polish.

---

#### Checkpoint 2026-05-17 — GAP-11 cultist-armor core contracts baseline

Статус: частично продвинут.

Что сделано:

- Восстановлены reference-shaped core contracts для cultist armor family:
  - `ItemCultistRobeArmor`: `UNCOMMON` rarity + leather repair + `IVisDiscountGear`/`IWarpingGear` surface (`1%`/`warp 1`);
  - `ItemCultistPlateArmor`: `UNCOMMON` rarity + leather repair;
  - `ItemCultistLeaderArmor`: `RARE` rarity + leather repair;
  - `ItemCultistBoots`: `UNCOMMON` rarity + leather repair + `IWarpingGear`/`IVisDiscountGear` surface (`warp 1`/`1%`).
- Добавлен `ItemCultistArmorCoreContractsStaticGuardTest` для фиксации этих family contracts.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это contract-level baseline; полный model/icon/tooltip parity для cultist armor остаётся на отдельный Stage 8 client-render polish.

---

#### Checkpoint 2026-05-17 — GAP-11 robe/traveller armor rarity-repair baseline

Статус: частично продвинут.

Что сделано:

- Восстановлены reference-shaped core contracts:
  - `ItemRobeArmor`: `UNCOMMON` rarity + thaumic-cloth repair key (`itemResource:7`);
  - `ItemBootsTraveller`: `RARE` rarity baseline.
- Добавлен `ItemRobeTravellerArmorCoreContractsStaticGuardTest` для фиксации этих контрактов.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это contract baseline; расширенный hover-motion parity для Boots of the Traveller остаётся отдельным gameplay polish.

---

#### Checkpoint 2026-05-17 — GAP-11 fortress-armor core contracts baseline

Статус: частично продвинут.

Что сделано:

- `ItemFortressArmor` доведён до reference-shaped core contracts (без клиентских model/icon tooltip веток):
  - сохранена расширенная interface surface: `ISpecialArmor` + `IGoggles` + `IRevealer`;
  - rarity/repair baseline: `RARE` + thaumium repair key (`itemResource:2`);
  - восстановлен set/mask armor-ratio bonus в `getProperties(...)` (`0.875` base + `0.125` за каждый fortress-piece слотов `1..3` + `0.05` за `mask` tag);
  - revealer hooks `showNodes(...)`/`showIngamePopups(...)` закреплены на `goggles` NBT-gate.
- Добавлен `ItemFortressArmorCoreContractsStaticGuardTest` для фиксации этих контрактов.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common/server contract baseline; полный client tooltip/model/icon parity для fortress armor остаётся отдельным Stage 8 client-render polish.
