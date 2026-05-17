# Stage 1 — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 1 должна закрыть совместимость и рабочую роль трех низкоуровневых областей порта:

- публичный addon API `thaumcraft.api.*`, включая API jar для сторонних модов;
- встроенные CCL-style helpers `thaumcraft.codechicken.*`, которые используются для математики, raytracing, lighting и рендера;
- TrueType font support `truetyper.*`, физически лежащий в `src/main/java/thaumcraft/truetyper/**`, но объявляющий package `truetyper`.

Источник требований: `docs/PRD.md:73-92` описывает роль API layer, `docs/PRD.md:206-220` задает Phase 1 scope, validation и риск, `build.gradle:62-68` задает `apiJar` и включение `thaumcraft/api/**`.

Stage 1 не считается закрытой только по факту компиляции: `docs/PRD.md:173-185` явно разделяет compile status и parity status.

## 2. Scope фазы

В scope Stage 1 входят:

- все Java-файлы под `src/main/java/thaumcraft/api/**`;
- все Java-файлы под `src/main/java/thaumcraft/codechicken/**`, с практическим фокусом на `thaumcraft.codechicken.lib/**` как на CCL helper layer;
- все Java-файлы под `src/main/java/thaumcraft/truetyper/**`, которые компилируются в package `truetyper`;
- Gradle task `apiJar`, который должен включать `thaumcraft/api/**` согласно `build.gradle:62-68`;
- публичные классы, методы, поля, nested types и поведенческие роли 1.7.10 API, адаптированные к Forge 1.12.2 там, где старые типы невозможны напрямую;
- сценарии addon/API use: регистрация scan handlers, object tags, warp metadata, smelting bonus, crucible/arcane/infusion recipes, wand/focus contracts, vis/aura helper calls, potion API classes, research objects;
- сценарии helper use: CCL vectors/matrices/transformations/UV/lighting/model rendering/raytracing, TrueType font loading/drawing.

Файловый scope текущей реализации:

- `src/main/java/thaumcraft/api/**`: 61 `.java` files;
- `src/main/java/thaumcraft/codechicken/**`: 40 `.java` files;
- `src/main/java/thaumcraft/truetyper/**`: 4 `.java` files.

Файловый scope референса:

- `thaumcraft_src/thaumcraft/api/**`: 61 public top-level `.class` files;
- `thaumcraft_src/thaumcraft/codechicken/lib/**`: 40 public top-level `.class` files;
- `thaumcraft_src/truetyper/**`: 4 public top-level `.class` files.

Прямые зависимости, которые могут блокировать Stage 1 behavior, но не должны расширять анализ на другие стадии:

- `src/main/java/thaumcraft/common/Thaumcraft.java:111` устанавливает `ThaumcraftApi.internalMethods = new InternalMethodHandler()`;
- `src/main/java/thaumcraft/common/lib/InternalMethodHandler.java:47-74` содержит API-facing реализации для части `ThaumcraftApiHelper`;
- `src/main/java/thaumcraft/common/config/Config.java:201-207` создает Stage 1 API potion classes;
- `src/main/java/thaumcraft/common/Thaumcraft.java:263-275` регистрирует эти potion instances в Forge registry.

## 3. Источники сравнения

Документы и build:

- `docs/PRD.md:31` указывает наличие `apiJar` для `thaumcraft/api/**`;
- `docs/PRD.md:40-44` перечисляет API, CodeChicken и TrueType source layout;
- `docs/PRD.md:73-92` задает API responsibilities и правило сохранения signatures;
- `docs/PRD.md:206-220` задает Stage 1 goal, validation и риск;
- `build.gradle:62-68` задает `apiJar` include `thaumcraft/api/**`.

Текущая реализация:

- `src/main/java/thaumcraft/api/**`;
- `src/main/java/thaumcraft/codechicken/**`;
- `src/main/java/thaumcraft/truetyper/**`.

Референс 1.7.10:

- `thaumcraft_src/thaumcraft/api/**`;
- `thaumcraft_src/thaumcraft/codechicken/lib/**`;
- `thaumcraft_src/thaumcraft/codechicken/core/launch/DepLoader.class`;
- `thaumcraft_src/truetyper/**`;
- `Thaumcraft-1.7.10-4.2.3.5.jar` как исходный jar, если нужен дополнительный decompile.

Проверки, выполненные для анализа:

- `git status --short`: clean output до изменений;
- `./scripts/dev.sh compileJava apiJar`: BUILD SUCCESSFUL;
- `jar tf build/libs/Thaumcraft-1.0.0-api.jar | grep '^thaumcraft/api/'`: API jar содержит `thaumcraft/api/**`, count 78 entries;
- `comm` inventory comparison текущих compiled classes vs `thaumcraft_src/**`: API, `codechicken/lib`, `truetyper` совпадают по top-level class count; отдельно обнаружен reference-only `thaumcraft/codechicken/core/launch/DepLoader.class`;
- `javap -public` и выборочный `javap -c` для reference classes: `thaumcraft.api.potions.*`, `thaumcraft.codechicken.lib.math.MathHelper`, `thaumcraft.codechicken.lib.raytracer.RayTracer`, ключевые API классы.

## 4. Текущее состояние Stage 1

Положительное состояние:

- `compileJava` и `apiJar` проходят.
- `apiJar` фактически содержит `thaumcraft/api/**`, что соответствует `build.gradle:62-68`.
- Top-level class inventory для `thaumcraft.api.*`, `thaumcraft.codechicken.lib.*` и `truetyper.*` в целом совпадает с reference inventory.
- Большинство public signatures совпадают с референсом после неизбежной адаптации 1.7.10 типов к 1.12.2 типам, например `MovingObjectPosition` -> `RayTraceResult`, `Vec3` -> `Vec3d`, `ForgeDirection` -> `EnumFacing`, `IIcon` -> `TextureAtlasSprite`.

Состояние после implementation checkpoint 2026-05-14:

- High implementation gaps Stage 1 закрыты в коде: potion compatibility shim, CCL raytrace `hitVec`/`BlockPos`, `MathHelper.compare`, API helper backend для object/bonus/generated tags и magic workbench lookup.
- `apiJar` продолжает включать `thaumcraft/api/**`.
- Legacy `DepLoader` остается явным исключением Stage 1, без no-op stub.
- Rendering/TrueType visual parity не закрыта runtime-визуально: server smoke проходит, client smoke в текущей среде падает до mod init на LWJGL display enumeration. Это accepted validation deferral, а не новый implementation blocker.

### 4.1 Утвержденная стратегия закрытия

Принят прагматичный baseline-вариант: закрыть cheap parity fixes и минимальный API helper backend, а визуальную проверку CCL render/TrueType держать отдельным accepted validation deferral до рабочей client scene/display среды.

Практический порядок работ:

1. **Checkpoint 1 — cheap parity fixes:**
   - восстановить 1.12-compatible compatibility shim для `PotionFluxTaint` и `PotionVisExhaust`: deprecated overload `(int id, boolean isBadEffect, int color)`, `static init()`, assignment в `instance`, reference icon/effectiveness setup и `isInstant()`;
   - сохранить Forge 1.12 registry path через `Config.initPotions()` и `Thaumcraft.registerPotions()`;
   - скопировать missing `textures/misc/potions.png` из `thaumcraft_src/assets/thaumcraft/textures/misc/potions.png` в `src/main/resources/assets/thaumcraft/textures/misc/potions.png`;
   - исправить `MathHelper.compare(double, double)` на reference return type `int`;
   - исправить CCL raytrace semantics: precise `hitVec` не перезаписывать block coordinates, block position хранить через 1.12 `BlockPos` / `RayTraceResult#getBlockPos()`;
   - после raytrace fix проверить и исправить port-side assumptions, включая `VisNetHandler.canNodeBeSeen()`, который должен сравнивать block coordinates через `getBlockPos()`, а не через `hitVec`.
2. **Checkpoint 2 — API helper minimum:**
   - реализовать `InternalMethodHandler.getStackInRowAndColumn()` через текущий `TileMagicWorkbench`/inventory equivalent;
   - убрать recursive API-manager loop в `ThaumcraftCraftingManager.getObjectTags()`, `getBonusTags()` и `generateTags()`;
   - перенести минимум reference-equivalent object tag lookup: exact meta, grouped meta, wildcard meta and safe generated fallback;
   - перенести минимум reference-equivalent bonus tags для реализованных 1.12 систем: `IEssentiaContainerItem`, armor/tool/weapon/bow/harvest/enchantment-derived tags;
   - `generateTags()` либо портировать достаточно для representative vanilla/Thaumcraft stacks, либо оставить явно documented blocker — без этого Stage 1 не считается complete.
3. **Validation/deferral:**
   - `DepLoader` закрыть как explicit exclusion, без no-op stub;
   - CCL render/TrueType визуальную проверку не подменять compile success. Если Phase 8 client hooks еще отсутствуют, оставить явный accepted deferral; если требуется полное закрытие Stage 1, добавить/использовать минимальную client scene.

Эта стратегия закрыта для implementation scope 2026-05-14. Full visual validation Stage 1 остается зависимой от `GAP-6` client/manual scene.

## 5. Gap list

All gaps from the original analysis are closed. See Section 9 for closure evidence.

| Gap | Status | Notes |
|---|---|---|
| GAP-1 Potion API | Closed | Compat shim for `(int, boolean, int)` constructor + `static init()` |
| GAP-2 CCL ray tracer | Closed | `hitVec`/`BlockPos` fixed for 1.12.2 |
| GAP-3 MathHelper.compare | Closed | Return type changed to `int` |
| GAP-4 API helper stubs | Closed | Min backend in `CraftingManager` |
| GAP-5 DepLoader | Excluded | Legacy launch hook, not ported |
| GAP-6 Render/TrueType | Deferred | Needs Phase 8 client scene for visual validation |

## 6. Итоговый checklist закрытия Stage 1

- [x] Close GAP-1 potion API behavior/signature parity or document unavoidable constructor/init limitations.
- [x] Close GAP-2 by preserving CCL raytrace hit vectors and moving block coordinates to 1.12 block positions.
- [x] Close GAP-3 by restoring `MathHelper.compare(double, double)` return type to `int`.
- [x] Close GAP-4 by replacing active API-facing internal handler placeholders with reference-equivalent Stage 1 minimum behavior.
- [x] Resolve GAP-5 by explicitly excluding legacy `DepLoader`; no safe compatibility class is needed by current code paths.
- [ ] Complete GAP-6 runtime/manual validation for CCL render/raytrace and TrueType helpers; CCL render/TrueType visual checks are accepted deferrals.
- [x] Re-run `./scripts/dev.sh compileJava`.
- [x] Re-run `./scripts/dev.sh apiJar devJar`.
- [x] Re-check `jar tf build/libs/Thaumcraft-1.0.0-api.jar` contains `thaumcraft/api/**`.
- [x] Re-run targeted public signature/search checks for changed Stage 1 API/CCL/helper methods.
- [x] Confirm no TODO/stub/placeholder comments remain in active Stage 1 API-facing paths.
- [x] Update `docs/Stage1.md` with validation evidence and remaining non-critical risks.

## 7. Definition of Done

Stage 1 implementation checkpoint считается закрытым, но Stage 1 визуальная parity-validation считается полной только если:
- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 1 реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 1;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 1 проверены вручную или тестами, включая CCL render/TrueType client scene;
- ./docs/Stage1.md обновлен и не содержит критичных открытых вопросов.

## 8. Открытые вопросы

- **Решено:** overload constructor `(int, boolean, int)` и `static init()` у API potion classes нужно сохранить как compatibility shim. Numeric id не должен управлять Forge 1.12 registry identity.
- **Решено:** legacy `thaumcraft.codechicken.core.launch.DepLoader` явно исключается из Stage 1 как 1.7.10 launch-time dependency loader. No-op stub не добавлять без конкретной runtime/addon необходимости.
- **Решено частично:** для CCL rendering и TrueType достаточной проверки сейчас нет, потому Phase 8 client hooks еще не закрыты. До появления minimal client scene это accepted validation deferral, а не основание считать Stage 1 complete.

Новый открытый blocker-level вопрос для implementation checkpoint:

- **Решено 2026-05-14:** `ThaumcraftCraftingManager.generateTags()` закрыт на Stage 1 minimum backend: crucible, arcane, infusion и Forge/vanilla recipe derivation, recursion guard, tag cap, exact/grouped/wildcard registry lookup. Full gameplay/content parity для всех рецептов остается в Phase 9, но это больше не блокирует Stage 1 API helper implementation closure.

## 9. Implementation closure evidence — 2026-05-14

Измененные области:

- `src/main/java/thaumcraft/api/potions/PotionFluxTaint.java`
- `src/main/java/thaumcraft/api/potions/PotionVisExhaust.java`
- `src/main/resources/assets/thaumcraft/textures/misc/potions.png`
- `src/main/java/thaumcraft/codechicken/lib/math/MathHelper.java`
- `src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java`
- `src/main/java/thaumcraft/codechicken/lib/raytracer/ExtendedMOP.java`
- `src/main/java/thaumcraft/api/visnet/VisNetHandler.java`
- `src/main/java/thaumcraft/api/ThaumcraftApi.java`
- `src/main/java/thaumcraft/common/config/Config.java`
- `src/main/java/thaumcraft/common/lib/InternalMethodHandler.java`
- `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java`

Validation commands and results:

- `./scripts/dev.sh compileJava`: BUILD SUCCESSFUL.
- `./scripts/dev.sh apiJar devJar`: BUILD SUCCESSFUL.
- `jar tf build/libs/Thaumcraft-1.0.0-api.jar | rg '^thaumcraft/api/' | wc -l`: `78` API entries.
- `javap -classpath build/classes/java/main -public ...`: confirmed `PotionFluxTaint(int, boolean, int)`, `PotionVisExhaust(int, boolean, int)`, both `static init()`, both `isInstant()`, `MathHelper.compare(double, double): int`, raytrace overloads, `ExtendedMOP(RayTraceResult, Object, double)`, and API helper backend methods.
- Targeted `rg` checks: no source/resource references to `DepLoader` or `thaumcraft.codechicken.core.launch`; no remaining `hitVec` block-coordinate overwrite patterns; no `Phase 3`/`Phase 4` placeholder strings in active API-facing handler paths.
- `./scripts/dev.sh smoke-server`: PASSED; server reached ready state and did not attempt legacy dependency loading.
- `./scripts/dev.sh smoke-client`: FAILED before mod init with `java.lang.ExceptionInInitializerError` caused by `org.lwjgl.opengl.LinuxDisplay.getAvailableDisplayModes` `ArrayIndexOutOfBoundsException`; treated as environment/display failure, not a Stage 1 code failure.
- Manual client potion validation by user: `/effect @p thaumcraft:flux_taint 400 0 false` showed the effect in vanilla inventory, rendered a valid icon, and applied periodic taint damage; `/effect @p thaumcraft:vis_exhaust 400 0 false` showed the effect in vanilla inventory, rendered a valid icon, and caused no crash.

Remaining limitations after implementation closure:

- CCL render helpers and TrueType drawing still require a working client/manual scene for visual parity.
- Potion icon visual confirmation and in-game effect cadence are manually validated; automated client smoke remains blocked by the current LWJGL display environment.
- Full content/progression recipe parity remains Phase 9; Stage 1 only restores the addon-facing API helper backend minimum.
