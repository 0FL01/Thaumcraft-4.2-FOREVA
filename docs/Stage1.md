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

Открытое состояние:

- Есть high gaps в behavior/signature parity внутри Stage 1.
- Есть API-facing stubs в dependency implementation, которые делают часть публичного API практически неполной.
- Rendering/raytracing/font helpers не имеют ручной runtime verification; PRD прямо помечает их behavior-sensitive (`docs/PRD.md:218-220`).

Stage 1 сейчас нельзя считать complete, потому что остаются high gaps.

### 4.1 Утвержденная стратегия закрытия

Принят прагматичный baseline-вариант: сначала закрыть дешевые parity fixes и минимальный API helper backend, но не заявлять полное закрытие Stage 1 до реального закрытия `generateTags()` и client/runtime validation.

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

Эта стратегия закрывает практические вопросы и задает scoped implementation path, но full Stage 1 DoD остается заблокированным до `GAP-4`/`GAP-6` validation.

## 5. Gap list

### GAP-1: Potion API classes потеряли часть reference contract и behavior

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/api/potions/PotionVisExhaust.java:10-37`
- `src/main/java/thaumcraft/api/potions/PotionFluxTaint.java:13-48`
- `src/main/java/thaumcraft/common/config/Config.java:201-207`
- `src/main/java/thaumcraft/common/Thaumcraft.java:263-275`

**Референс:**
- `thaumcraft_src/thaumcraft/api/potions/PotionVisExhaust.class`
- `thaumcraft_src/thaumcraft/api/potions/PotionFluxTaint.class`

**Что не совпадает:**

Reference classes expose constructor `(int id, boolean isBadEffect, int color)`, static `init()`, `isInstant()` behavior returning true, icon setup in `init()` and effectiveness `0.25`. Current 1.12 classes expose constructor `(boolean, int)`, do not expose `init()`, do not override `isInstant()`, and set only `setIconIndex(0, 0)` plus potion name in constructor. The constructor signature change is partly required by Forge 1.12 registry mechanics, but the missing `isInstant()` and missing explicit init-equivalent behavior are behavior/API parity gaps. `PotionVisExhaust.isReady()` currently returns false at `PotionVisExhaust.java:33-35`, while the reference has instant-potion behavior.

Additional implementation gap: current resources do not include `src/main/resources/assets/thaumcraft/textures/misc/potions.png`; the source-of-truth asset exists at `thaumcraft_src/assets/thaumcraft/textures/misc/potions.png` and should be copied rather than recreated.

**Что нужно доделать:**

Restore the reference behavior on Forge 1.12.2 terms and decide whether addon-facing static `init()` compatibility can be preserved without reintroducing numeric potion IDs.

**Решение:**

Use a compatibility shim. Preserve the Forge 1.12 registry-owned constructor path, but add deprecated source/binary compatibility surface where it is safe: overload `(int ignoredId, boolean isBadEffect, int color)` delegates to the 1.12 constructor and does not control registry identity; `static init()` performs idempotent name/icon/effectiveness setup only. Assign `instance` from the active 1.12 instances so addons using `PotionFluxTaint.instance` / `PotionVisExhaust.instance` see the registered object.

**Как доделать:**
- In `PotionVisExhaust`, override `isInstant()` to match reference instant behavior.
- In `PotionFluxTaint`, override `isInstant()` if reference instant semantics remain applicable in 1.12.2.
- Set icon indexes equivalent to reference `init()` values: Flux Taint `(3, 1)`, Vis Exhaust `(5, 1)`, unless 1.12 texture atlas layout requires documented adaptation.
- Apply effectiveness `0.25D` via the 1.12 equivalent API if still relevant.
- Evaluate whether a no-op/adapted `static init()` should be restored for addon source/binary compatibility or explicitly documented as impossible due to 1.12 registry changes.
- Keep Forge registry creation in `Config.initPotions()` and registration in `Thaumcraft.registerPotions()` intact unless a minimal compatibility shim requires adjustment.
- Runtime scenario: applying Flux Taint damages/heals with reference cadence; Vis Exhaust behaves as original instant/non-ticking status intended by TC4; icons render from `textures/misc/potions.png`.

**Критерии приемки:**
- [ ] `javap -public` shows expected 1.12-compatible public contract, with any unavoidable constructor/init differences documented.
- [ ] `PotionVisExhaust` and `PotionFluxTaint` match reference `isInstant`, icon index and effectiveness semantics where Forge 1.12 allows.
- [ ] `compileJava apiJar` passes.
- [ ] Runtime/manual check confirms both potion icons and effect cadence in-game.

**Риски / зависимости:**

Forge 1.12 removed numeric potion IDs from the old constructor path, so exact constructor binary compatibility may be impossible. If old addons compile against `(int, boolean, int)`, preserving source compatibility may require an overloaded deprecated constructor that ignores or maps the numeric ID, but that must not silently break Forge registry identity.

### GAP-2: CCL RayTracer writes block coordinates into hitVec instead of preserving intercept

**Статус:** реализовано неправильно  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java:98-119`
- `src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java:130-164`

**Референс:**
- `thaumcraft_src/thaumcraft/codechicken/lib/raytracer/RayTracer.class`
- `thaumcraft_src/thaumcraft/codechicken/lib/raytracer/ExtendedMOP.class`
- `thaumcraft_src/thaumcraft/codechicken/lib/raytracer/IndexedCuboid6.class`

**Что не совпадает:**

Reference `MovingObjectPosition` stores the precise intercept in `hitVec` and stores block coordinates separately in `blockX/blockY/blockZ`. Current 1.12 port creates a `RayTraceResult` with the intercept at `RayTracer.java:107`, but then overwrites `mop.hitVec` with integer block coordinates at `RayTracer.java:116`, `RayTracer.java:148` and `RayTracer.java:162`. In 1.12, block position should be represented by `RayTraceResult#getBlockPos()` / constructor block position, while `hitVec` should remain the actual ray/cuboid intersection.

**Что нужно доделать:**

Port the `MovingObjectPosition` block coordinate fields to 1.12 `BlockPos` without destroying the hit vector.

**Решение:**

Fix `RayTracer` and `ExtendedMOP` to preserve precise intercepts and carry block coordinates through `BlockPos`. Also audit known 1.12 port-side assumptions after the fix: `VisNetHandler.canNodeBeSeen()` currently compares `mop.hitVec` to target block coordinates and should use `mop.getBlockPos()` for reference-equivalent semantics.

**Как доделать:**
- In `rayTraceCuboid(..., BlockCoord pos)`, return/create a `RayTraceResult` that preserves the existing `hitVec`, side and type, and carries `new BlockPos(pos.x, pos.y, pos.z)` as the block position.
- In `rayTraceCuboids(..., BlockCoord pos, Block block)`, preserve the `ExtendedMOP` distance/data and precise intercept while setting block position.
- In the `hitList` overload, add `ExtendedMOP` entries with precise hit vector and block position.
- Audit callers that read `hitVec` or `getBlockPos()` to ensure they now receive reference-equivalent semantics.
- Add focused test or debug scenario with a sub-block cuboid where hit point is not equal to block origin.

**Критерии приемки:**
- [ ] Raytrace result `hitVec` remains the precise intersection point for cuboids.
- [ ] Raytrace result block position equals the target `BlockCoord`.
- [ ] `ExtendedMOP` still preserves `data` and distance ordering.
- [ ] Runtime/manual check validates selection/collision behavior on a CCL cuboid model.

**Риски / зависимости:**

This is behavior-sensitive rendering/interaction infrastructure called out by `docs/PRD.md:218-220`. Fixing it may reveal caller assumptions introduced during the 1.12 port.

### GAP-3: `MathHelper.compare(double, double)` has wrong public return type

**Статус:** реализовано неправильно  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/codechicken/lib/math/MathHelper.java:87-93`

**Референс:**
- `thaumcraft_src/thaumcraft/codechicken/lib/math/MathHelper.class`

**Что не совпадает:**

Reference signature is `public static int compare(double, double)`, returning `-1`, `0` or `1`. Current source declares `public static double compare(double a, double b)` at `MathHelper.java:91`, returning numeric values that are semantically integral but have a different public descriptor. This is a binary/source compatibility mismatch for helper users and addons compiling against the bundled CCL helper.

**Что нужно доделать:**

Change the return type to `int` and preserve the reference comparison behavior.

**Как доделать:**
- Edit `MathHelper.compare(double, double)` to return `int`.
- Search for current call sites to ensure no code depends on `double` return type.
- Re-run public signature comparison for `MathHelper`.

**Критерии приемки:**
- [ ] `javap -public thaumcraft.codechicken.lib.math.MathHelper` shows `public static int compare(double, double)`.
- [ ] All current call sites compile without casts or behavior changes.
- [ ] `compileJava apiJar` passes.

**Риски / зависимости:**

Low implementation risk, high compatibility value. This should be a small direct fix.

### GAP-4: API helper internal methods expose incomplete behavior through stubs

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/api/ThaumcraftApiHelper.java:73-99`
- `src/main/java/thaumcraft/api/ThaumcraftApiHelper.java:176-193`
- `src/main/java/thaumcraft/api/internal/IInternalMethodHandler.java:9-35`
- `src/main/java/thaumcraft/api/internal/DummyInternalMethodHandler.java:1-69`
- `src/main/java/thaumcraft/common/Thaumcraft.java:111`
- `src/main/java/thaumcraft/common/lib/InternalMethodHandler.java:47-74`

**Референс:**
- `thaumcraft_src/thaumcraft/api/ThaumcraftApiHelper.class`
- `thaumcraft_src/thaumcraft/api/internal/IInternalMethodHandler.class`
- `thaumcraft_src/thaumcraft/api/internal/DummyInternalMethodHandler.class`
- original common/internal implementation in `Thaumcraft-1.7.10-4.2.3.5.jar` if deeper behavior confirmation is needed.

**Что не совпадает:**

The API surface exists, but the active internal implementation contains explicit phase placeholders: `getStackInRowAndColumn()` returns `ItemStack.EMPTY` with `// Phase 4: tile entity integration` at `InternalMethodHandler.java:47-50`; `getBonusObjectTags()` returns an empty `AspectList` with `// Phase 3: bonus tags from components` at `InternalMethodHandler.java:65-69`; `generateTags()` returns an empty `AspectList` with `// Phase 3: auto-generate tags from materials` at `InternalMethodHandler.java:71-74`. These methods are directly exposed by `ThaumcraftApiHelper` and are part of the public addon-facing API role described in `docs/PRD.md:73-92`.

**Что нужно доделать:**

Restore the original behavior behind the internal method handler, or explicitly isolate the remaining common-system dependencies while keeping API helper calls correct for implemented content.

**Решение:**

Implement a minimum viable reference-equivalent backend instead of broad Phase 3/4 closure. `getStackInRowAndColumn()` should delegate to the current 1.12 magic workbench/inventory implementation. Object tag APIs should move real lookup and bonus/generation behavior into `ThaumcraftCraftingManager` to avoid helper-manager recursion. Full `generateTags()` recipe inference remains the critical blocker: it must be ported for representative vanilla/Thaumcraft stacks or explicitly documented as the remaining Stage 1 blocker.

**Как доделать:**
- Port `getStackInRowAndColumn()` against the 1.12 inventory/table/tile implementation that replaces the 1.7 research/arcane table source.
- Port `getBonusObjectTags()` behavior from the original aspect/object tag system.
- Port `generateTags(Item, int)` behavior from the original aspect generation logic.
- Keep `DummyInternalMethodHandler` as safe fallback only; ensure normal mod init always installs the real handler before addon API calls that need it.
- Add focused API-level checks for `ThaumcraftApiHelper.getObjectAspects`, `getBonusObjectTags`, `generateTags`, and `getStackInRowAndColumn` using known vanilla/Thaumcraft stacks.

**Критерии приемки:**
- [ ] No `Phase 3` / `Phase 4` placeholder comments or empty fallback behavior remain in active Stage 1 API-facing handler paths.
- [ ] Known item aspect generation matches the 1.7.10 reference for representative stacks.
- [ ] Known crafting/grid lookup scenario returns the same slot result as the reference-equivalent 1.12 tile/container.
- [ ] `compileJava apiJar` passes and API helper behavior is manually or test verified.

**Риски / зависимости:**

Dependency: this touches behavior owned by later systems, but it directly blocks Stage 1 public API correctness because addons call `ThaumcraftApiHelper`. Do not broaden Stage 1 into full Stage 3/4 closure; implement only the minimum required backend behavior for these API methods or document exact remaining blockers.

### GAP-5: Reference contains `thaumcraft.codechicken.core.launch.DepLoader`, current port omits it

**Статус:** отсутствует  
**Критичность:** low

**Текущая реализация:**
- no file under `src/main/java/thaumcraft/codechicken/core/launch/DepLoader.java`
- current CCL helper files are under `src/main/java/thaumcraft/codechicken/lib/**`

**Референс:**
- `thaumcraft_src/thaumcraft/codechicken/core/launch/DepLoader.class`
- nested classes under `thaumcraft_src/thaumcraft/codechicken/core/launch/DepLoader$*.class`

**Что не совпадает:**

Inventory comparison found one top-level reference class outside `codechicken/lib`: `thaumcraft.codechicken.core.launch.DepLoader`. The current port does not include it. This is likely intentional because legacy CCL dependency downloading/launch injection is not appropriate for a Forge 1.12.2 bundled-helper port, and Stage 1 wording focuses on bundled CCL-style helpers rather than old launch-time dependency loading. Still, the absence should be explicitly decided/documented so it is not mistaken for an unreviewed missing class.

**Что нужно доделать:**

Confirm and document whether `DepLoader` is out of scope for the 1.12.2 port.

**Решение:**

Explicitly exclude legacy `thaumcraft.codechicken.core.launch.DepLoader` from Stage 1 implementation. It is a 1.7.10 launch-time dependency/core-plugin hook, not part of the bundled `codechicken.lib` helper layer, and current source/build paths do not reference it. Do not add a no-op compatibility class unless a concrete addon/runtime dependency is found.

**Как доделать:**
- Search all current source and resources for `DepLoader` and `thaumcraft.codechicken.core.launch` references.
- If no runtime references exist, document exclusion as intentional in Stage 1 closure notes.
- If any addon/API compatibility requirement depends on it, add a safe non-downloading compatibility stub only after confirming it cannot affect Forge loading.

**Критерии приемки:**
- [ ] No current code path references `thaumcraft.codechicken.core.launch.DepLoader`.
- [ ] Stage 1 closure notes explicitly mark legacy `DepLoader` as excluded or provide a safe compatibility class.
- [ ] Runtime mod load does not attempt legacy dependency downloads or launch injection.

**Риски / зависимости:**

Adding the original behavior would be risky and likely wrong for 1.12.2. The preferred closure is explicit exclusion unless a concrete compatibility need appears.

### GAP-6: Rendering, raytracing and TrueType helpers lack behavior validation

**Статус:** требует проверки  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/codechicken/lib/render/CCRenderState.java:24-362`
- `src/main/java/thaumcraft/codechicken/lib/render/CCModel.java`
- `src/main/java/thaumcraft/codechicken/lib/lighting/LightMatrix.java`
- `src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java:24-225`
- `src/main/java/thaumcraft/truetyper/FontHelper.java:13-70`
- `src/main/java/thaumcraft/truetyper/TrueTypeFont.java`

**Референс:**
- `thaumcraft_src/thaumcraft/codechicken/lib/render/**`
- `thaumcraft_src/thaumcraft/codechicken/lib/lighting/**`
- `thaumcraft_src/thaumcraft/codechicken/lib/raytracer/**`
- `thaumcraft_src/truetyper/**`

**Что не совпадает:**

Static class inventory and most public signatures match, but behavior cannot be confirmed statically for OpenGL/BufferBuilder rendering, lighting, sub-cuboid raytracing, and TrueType drawing. This is not speculative: Stage 1 PRD explicitly warns that rendering helpers and raytracing/math helpers may compile but remain behavior-sensitive (`docs/PRD.md:218-220`). Current `CCRenderState` uses 1.12 `BufferBuilder` and `DefaultVertexFormats.BLOCK` at `CCRenderState.java:141-153` and `CCRenderState.java:193-204`; `FontHelper` manipulates GL matrices and scaled resolution at `FontHelper.java:16-51`. These must be validated visually/runtime, not just compiled.

**Что нужно доделать:**

Add focused manual or automated runtime validation for Stage 1 helper scenarios.

**Решение:**

Accepted pragmatic path: do not claim full Stage 1 validation from `compileJava`/`apiJar`. Raytrace/math validation can be closed with focused non-GL checks after `GAP-2`/`GAP-3`; CCL render and TrueType visual validation requires either Phase 8 client hooks or a temporary/minimal client validation scene. If Phase 8 remains absent, keep this as an explicit accepted deferral rather than marking Stage 1 complete.

**Как доделать:**
- Create or reuse a minimal in-game scenario that renders a CCL model using `CCRenderState` and `CCModel`.
- Validate lighting/brightness via `LightMatrix` and `PlanarLightModel` on at least one block/entity render path.
- Validate raytracing against multiple cuboids, including non-full-block and edge hit cases.
- Validate `FontHelper.drawString()` and `TrueTypeFont` with color formatting and GUI scale changes.
- Capture validation commands/results in this document when closure work is done.

**Критерии приемки:**
- [ ] Client smoke or manual runtime reaches a scene using CCL render helpers without crash.
- [ ] Visual output for a representative helper-rendered model matches reference intent closely enough for the 1.12 renderer.
- [ ] Raytracing helper returns correct hit vector, side, block position and ordered `ExtendedMOP` results.
- [ ] TrueType text renders at expected position/scale/color on at least two GUI scale settings.

**Риски / зависимости:**

Dependency: meaningful validation may require later client render registrations. Keep this as Stage 1 validation dependency only; do not claim Stage 1 complete until either the scenarios exist or the lack of client hooks is documented with an explicit deferral accepted by the project.

## 6. Итоговый checklist закрытия Stage 1

- [ ] Close GAP-1 potion API behavior/signature parity or document unavoidable constructor/init limitations.
- [ ] Close GAP-2 by preserving CCL raytrace hit vectors and moving block coordinates to 1.12 block positions.
- [ ] Close GAP-3 by restoring `MathHelper.compare(double, double)` return type to `int`.
- [ ] Close GAP-4 by replacing active API-facing internal handler placeholders with reference-equivalent behavior.
- [ ] Resolve GAP-5 by explicitly excluding legacy `DepLoader` or adding a safe compatibility class if needed.
- [ ] Complete GAP-6 runtime/manual validation for CCL render/raytrace and TrueType helpers.
- [ ] Re-run `./scripts/dev.sh compileJava apiJar`.
- [ ] Re-check `jar tf build/libs/Thaumcraft-1.0.0-api.jar` contains `thaumcraft/api/**`.
- [ ] Re-run public signature comparison for Stage 1 classes against `thaumcraft_src/**`, allowing only documented 1.12 type substitutions.
- [ ] Confirm no TODO/stub/placeholder comments remain in active Stage 1 API-facing paths.
- [ ] Update `docs/Stage1.md` with validation evidence and remaining non-critical risks.

## 7. Definition of Done

Stage 1 считается ПОЛНОСТЬЮ завершенной только если:
- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 1 реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 1;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 1 проверены вручную или тестами;
- ./docs/Stage1.md обновлен и не содержит критичных открытых вопросов.

## 8. Открытые вопросы

- **Решено:** overload constructor `(int, boolean, int)` и `static init()` у API potion classes нужно сохранить как compatibility shim. Numeric id не должен управлять Forge 1.12 registry identity.
- **Решено:** legacy `thaumcraft.codechicken.core.launch.DepLoader` явно исключается из Stage 1 как 1.7.10 launch-time dependency loader. No-op stub не добавлять без конкретной runtime/addon необходимости.
- **Решено частично:** для CCL rendering и TrueType достаточной проверки сейчас нет, потому Phase 8 client hooks еще не закрыты. До появления minimal client scene это accepted validation deferral, а не основание считать Stage 1 complete.

Новый открытый blocker-level вопрос для implementation checkpoint:

- Насколько полно портировать `ThaumcraftCraftingManager.generateTags()` в Stage 1: minimum representative vanilla/Thaumcraft stacks или full reference recipe inference? Без принятого объема и validation `GAP-4` остается blocker для полного Stage 1 DoD.
