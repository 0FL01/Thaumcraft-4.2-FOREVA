# Stage 8-b — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 8-b закрывает только client GUI screens для портирования Thaumcraft 4.2.3.5 на Forge 1.12.2. Цель: все GUI из текущих GUI ID и из core scope открываются на клиенте, используют оригинальные контейнеры/тайлы/сущности, отрисовывают оригинальные ресурсы, тексты и интерактивные элементы, и проходят базовую ручную проверку без client crash.

Stage 8 overall в `docs/PRD.md` относит GUI screens к client layer responsibilities: `src/main/java/thaumcraft/client/**` отвечает за GUI screens, client event handlers, key bindings, renderers, particles and sounds (`docs/PRD.md:117-138`). Ресурсы GUI должны жить в `src/main/resources/**`, включая textures, GUI textures, sounds, language files and shader resources (`docs/PRD.md:140-158`). Runtime smoke checklist явно требует, чтобы core GUIs opened without client crash (`docs/PRD.md:531-533`).

## 2. Scope фазы

В scope Stage 8-b входят только клиентские GUI screens и напрямую необходимые ресурсы/маршрутизация:

- Core container GUIs: Arcane Workbench, Arcane Bore, Thaumatorium, Focal Manipulator, Hover Harness, Golem, Pech, Traveling Trunk.
- Current/common GUI IDs that reference client screens: Golem `0`, Pech `1`, Traveling Trunk `2`, Thaumatorium `3`, Focus Pouch `5`, Deconstruction Table `8`, Alchemy Furnace `9`, Research Table `10`, Thaumonomicon `12`, Arcane Workbench `13`, Arcane Bore `15`, Hand Mirror `16`, Hover Harness `17`, Magic Box `18`, Spa `19`, Focal Manipulator `20` in `src/main/java/thaumcraft/common/CommonProxy.java:25-40`.
- Item/entity interaction GUIs: Thaumonomicon research browser, Research Table / Research Notes GUI, entity GUIs for Golem, Pech and Traveling Trunk, and scan/Thaumometer overlays only to the extent they are implemented or routed as GUI screens.
- Client GUI resources: `textures/gui/**`, research GUI support textures under `textures/misc/**` and `textures/aspects/**` when directly used by these screens, lang keys used by these screens, and font helpers used by research GUIs.
- `getClientGuiElement` routing only insofar as it creates these screens.

Out of scope for this document: TESR/entity renderer parity, particles, beams, bolts, shaders, and Stage 9 content/recipe/research registration except where a missing data dependency directly blocks GUI behavior.

## 3. Источники сравнения

Required project docs and build context:

- `AGENTS.md:5-17` defines `thaumcraft_src/**` and `Thaumcraft-1.7.10-4.2.3.5.jar` as read-only original reference material, and says original assets may be copied from `thaumcraft_src/assets/`.
- `AGENTS.md:111-122` requires starting with `git status --short`, reading docs/code, inspecting original behavior, focused validation, and final scoped diff.
- `docs/PRD.md:117-138` defines client-layer responsibilities, including GUI screens.
- `docs/PRD.md:140-158` defines resource-layer responsibilities, including GUI textures and language files.
- `docs/PRD.md:503-511` lists project-specific scans; only lightweight inspection commands were used here, no build was run because this is documentation-only.
- `build.gradle:24-29` confirms Java 8, Forge `1.12.2-14.23.5.2847`, MCP `stable_39`.
- `Dockerfile:61-69` documents CFR availability in the dev image; for this analysis `javap` and `jar tf` were sufficient, avoiding repository writes outside `docs/Stage8-b.md`.

Current implementation sources:

- `src/main/java/thaumcraft/common/CommonProxy.java:25-40`, `src/main/java/thaumcraft/common/CommonProxy.java:61-119`, `src/main/java/thaumcraft/common/CommonProxy.java:122-126`.
- `src/main/java/thaumcraft/client/ClientProxy.java:61-88`.
- Existing client GUI classes: `src/main/java/thaumcraft/client/gui/GuiFocusPouch.java:1-36`, `src/main/java/thaumcraft/client/gui/GuiHandMirror.java:1-30`, `src/main/java/thaumcraft/client/gui/GuiHoverHarness.java:1-30`, `src/main/java/thaumcraft/client/gui/GuiFactory.java:1-27`.
- GUI open triggers: `src/main/java/thaumcraft/common/blocks/BlockTable.java:143-175`, `src/main/java/thaumcraft/common/blocks/BlockWoodenDevice.java:78-90`, `src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java:131-147`, `src/main/java/thaumcraft/common/blocks/BlockStoneDevice.java:124-149`, `src/main/java/thaumcraft/common/entities/golems/EntityGolemBase.java:346-353`, `src/main/java/thaumcraft/common/entities/monster/EntityPech.java:279-286`, `src/main/java/thaumcraft/common/entities/golems/EntityTravelingTrunk.java:84-90`, `src/main/java/thaumcraft/common/items/relics/ItemThaumonomicon.java:43-50`, `src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java:37-69`, `src/main/java/thaumcraft/common/items/armor/ItemHoverHarness.java:45-52`.
- Current lang/resource evidence: `src/main/resources/assets/thaumcraft/lang/en_us.lang:101-104`, absence of `src/main/resources/assets/thaumcraft/textures/gui/**`, and only `src/main/resources/assets/thaumcraft/textures/misc/potions.png` under `textures/misc/**`.

Reference implementation and resources:

- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/ClientProxy.class`: `getClientGuiElement` bytecode routes GUI IDs `0,1,2,3,5,8,9,10,12,13,15,16,17,18,19,20` to concrete client screens; offsets `108-461` instantiate `GuiGolem`, `GuiPech`, `GuiTravelingTrunk`, `GuiAlchemyFurnace`, `GuiThaumatorium`, `GuiResearchTable`, `GuiResearchBrowser`, `GuiArcaneWorkbench`, `GuiDeconstructionTable`, `GuiArcaneBore`, `GuiHandMirror`, `GuiHoverHarness`, `GuiFocusPouch`, `GuiMagicBox`, `GuiSpa`, and `GuiFocalManipulator`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiArcaneWorkbench.class`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiArcaneBore.class`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiThaumatorium.class`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiFocalManipulator.class` and `GuiFocalManipulator$Sparkle.class`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiGolem.class`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiPech.class`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiTravelingTrunk.class`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiResearchTable.class`, `GuiResearchTable$Coord2D.class`, `GuiResearchTable$Rune.class`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiResearchBrowser.class`, `GuiResearchRecipe.class`, `GuiResearchRecipe$Coord2D.class`, `GuiResearchPopup.class`.
- Additional current/common GUI ID references: `GuiDeconstructionTable.class`, `GuiAlchemyFurnace.class`, `GuiMagicBox.class`, `GuiSpa.class`, `GuiFocusPouch.class`, `GuiHandMirror.class`, `GuiHoverHarness.class`.
- Reference GUI textures exist under `thaumcraft_src/assets/thaumcraft/textures/gui/`: `gui_arcaneworkbench.png`, `gui_arcanebore.png`, `gui_thaumatorium.png`, `gui_wandtable.png`, `guihoverharness.png`, `guigolem.png`, `gui_pech.png`, `guitrunkbase.png`, `guiresearchtable2.png`, `gui_research.png`, `gui_researchbook.png`, `gui_researchbook_overlay.png`, `gui_researchback.png`, `gui_researchbackeldritch.png`, `gui_focuspouch.png`, `guihandmirror.png`, `gui_decontable.png`, `gui_alchemyfurnace.png`, `gui_spa.png`, `arcane.png`, `hex1.png`, `hex2.png`, `hud.png`.
- Reference support textures used by research GUIs exist under `thaumcraft_src/assets/thaumcraft/textures/misc/` and `thaumcraft_src/assets/thaumcraft/textures/aspects/`, including `parchment3.png`, `script.png`, `_back.png`, `_unknown.png`, and all aspect icons.

Lightweight commands run during analysis:

- `git status --short` showed unrelated untracked `docs/Stage8-a.md` before this document was created.
- `jar tf Thaumcraft-1.7.10-4.2.3.5.jar | rg 'thaumcraft/(client/gui|common/container|common/CommonProxy|client/ClientProxy|common/items/relics|common/items/wands|common/items/armor|common/entities/(golems|monster)|common/blocks)/.*(Gui|Container|Proxy|Thaumonomicon|Thaumometer|Research|Golem|Pech|Trunk|Hover|Bore|Focal|Workbench|Table|MetalDevice|StoneDevice|WoodenDevice).*\\.class|assets/thaumcraft/(textures/gui|lang)/'`.
- `javap -classpath Thaumcraft-1.7.10-4.2.3.5.jar -c -p thaumcraft.client.ClientProxy` focused on `getClientGuiElement`.
- `javap -classpath Thaumcraft-1.7.10-4.2.3.5.jar -p thaumcraft.client.gui.<GuiClass>` for reference class members.
- `javap -classpath Thaumcraft-1.7.10-4.2.3.5.jar -c -p thaumcraft.client.gui.<GuiClass> | rg 'textures|tc\\.|entity\\.|container\\.'` for reference resource/string constants.
- `unzip -p Thaumcraft-1.7.10-4.2.3.5.jar assets/thaumcraft/lang/en_US.lang | rg ...` for reference GUI language keys.

## 4. Текущее состояние Stage 8-b

Current state is not closeable. Server/common GUI IDs and containers exist for many screens, but client-side routing and client GUI classes are still incomplete. GUI IDs `3`, `5`, `8`, `9`, `13`, `15`, `16`, `17`, `18`, `19`, and `20` now have texture-backed Thaumatorium, Focus Pouch, Deconstruction Table, Alchemy Furnace, Arcane Workbench, Arcane Bore, Hand Mirror, Hover Harness, Magic Box, Spa, and Focal Manipulator baselines, but full visual parity and manual client validation remain open.

Concrete current findings:

- GUI ID constants exist for all active IDs in `src/main/java/thaumcraft/common/CommonProxy.java:25-40`.
- Server GUI routing exists for Golem, Pech, Traveling Trunk, Thaumatorium, Focus Pouch, Deconstruction Table, Alchemy Furnace, Research Table, Arcane Workbench, Arcane Bore, Hand Mirror, Hover Harness, Magic Box, Spa, and Focal Manipulator in `src/main/java/thaumcraft/common/CommonProxy.java:61-119`.
- Thaumonomicon is intentionally server-null in `src/main/java/thaumcraft/common/CommonProxy.java:95`, matching the reference pattern that its GUI is client-only.
- Base `CommonProxy.getClientGuiElement` returns null in `src/main/java/thaumcraft/common/CommonProxy.java:122-126`, as expected for common proxy.
- `ClientProxy.getClientGuiElement` constructs `GuiFocusPouch`, `GuiHandMirror`, `GuiHoverHarness`, `GuiMagicBox`, and `GuiSpa`, and now routes `GUI_THAUMATORIUM`, `GUI_DECONSTRUCTION_TABLE`, `GUI_ALCHEMY_FURNACE`, `GUI_ARCANE_WORKBENCH`, `GUI_ARCANE_BORE`, and `GUI_FOCAL_MANIPULATOR` to matching tile-backed screens; the remaining Stage 8-b/current GUI IDs still return null.
- `GuiFocusPouch`, `GuiHandMirror`, and `GuiHoverHarness` now bind original textures instead of grey placeholder rectangles.
- `src/main/resources/assets/thaumcraft/textures/gui/gui_thaumatorium.png`, `gui_arcaneworkbench.png`, `gui_arcanebore.png`, `gui_wandtable.png`, `gui_decontable.png`, `gui_alchemyfurnace.png`, `gui_spa.png`, `gui_focuspouch.png`, `guihandmirror.png`, and `guihoverharness.png` are copied byte-for-byte from `thaumcraft_src/assets/thaumcraft/textures/gui/`; the other reference GUI textures are still absent from current resources.
- `src/main/resources/assets/thaumcraft/textures/aspects/**` now contains the original aspect icon set needed by Thaumatorium and later research/aspect GUI rendering.
- Current English lang has only `container.focus_pouch`, `container.handmirror`, `container.hoverharness`, and `container.inventory` for implemented GUI labels in `src/main/resources/assets/thaumcraft/lang/en_us.lang:101-104`; research/golem/trunk/interaction GUI keys are absent.
- Thaumonomicon right-click opens GUI ID `12` server-side in `src/main/java/thaumcraft/common/items/relics/ItemThaumonomicon.java:43-50`, but client ID `12` returns null in `src/main/java/thaumcraft/client/ClientProxy.java:78-84`.
- Thaumometer currently performs server-side scanning in `src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java:37-69`; no `GuiScreen`/overlay implementation was found under `src/main/java/thaumcraft/client/**`.

## 5. Gap list

### GAP-1: Arcane Workbench GUI отсутствует на клиенте

**Статус:** частично реализовано
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/client/ClientProxy.java#getClientGuiElement` routes GUI ID `13` to `GuiArcaneWorkbench` only when the client tile at the provided coordinates is `TileArcaneWorkbench`.
- `src/main/java/thaumcraft/common/CommonProxy.java:96-99`
- `src/main/java/thaumcraft/common/blocks/BlockTable.java:153-155`
- `src/main/java/thaumcraft/client/gui/GuiArcaneWorkbench.java` exists as a minimal 1.12.2 `GuiContainer` baseline using `ContainerArcaneWorkbench`.
- `src/main/resources/assets/thaumcraft/textures/gui/gui_arcaneworkbench.png` exists and matches the original asset.

**Референс:**
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/ClientProxy.class`, `getClientGuiElement` offsets `255-279` instantiate `GuiArcaneWorkbench` for GUI ID `13`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiArcaneWorkbench.class`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_arcaneworkbench.png`.

**Что не совпадает:**
Current server routing opens `GUI_ARCANE_WORKBENCH`, and the client now returns a minimal texture-backed `GuiArcaneWorkbench` for the matching tile. Reference GUI also draws wand vis, recipe aspect costs, primal aspect positions and reference-specific state; those foreground/background parity pieces are still not ported.

**Что нужно доделать:**
Finish `GuiArcaneWorkbench` parity for 1.12.2: draw wand/vis and recipe aspect costs, align any slot/background overlays with the original, and manually validate the right-click open scenario.

**Как доделать:**
- Done: add `src/main/java/thaumcraft/client/gui/GuiArcaneWorkbench.java`.
- Done: update `src/main/java/thaumcraft/client/ClientProxy.java#getClientGuiElement` case `GUI_ARCANE_WORKBENCH` to fetch `TileArcaneWorkbench` and instantiate the GUI.
- Done: copy `thaumcraft_src/assets/thaumcraft/textures/gui/gui_arcaneworkbench.png` to `src/main/resources/assets/thaumcraft/textures/gui/gui_arcaneworkbench.png`.
- Verify all referenced aspect icon resources exist in `src/main/resources/assets/thaumcraft/textures/aspects/**`.
- Scenario: right-click Arcane Workbench table from `BlockTable.java:153-155` and craft/open with wand/vis display visible.

**Критерии приемки:**
- [ ] Right-clicking Arcane Workbench opens a non-null client GUI for GUI ID `13`.
- [ ] GUI uses `gui_arcaneworkbench.png` and does not render placeholder rectangles.
- [ ] Wand/crafting/aspect display matches reference layout closely enough for manual parity review.
- [ ] No missing texture errors for the workbench GUI path.

**Checkpoint 2026-05-15 — Arcane Workbench GUI baseline:**
- Added `GuiArcaneWorkbench` with reference size `190x234`, `ContainerArcaneWorkbench`, and original `gui_arcaneworkbench.png`.
- Routed client GUI ID `13` to the screen with a `TileArcaneWorkbench` type check.
- Validation: `./scripts/dev.sh compileJava` passed; `./scripts/dev.sh validate --smoke` passed with tests `10/10`, jar/check-jar summary `5134` MCP leak lines / `1028` unique leaks, server ready at `Done (1.205s)!`, and no crash reports under `run/`.
- Client smoke/manual GUI open was skipped because `DISPLAY=` and user-driven GUI/graphics validation is excluded for this run.
- Remaining: full wand/cost/aspect display, manual right-click/open scenario, visual parity, and Stage 9 recipe output coverage.

**Риски / зависимости:**
Depends on current `ContainerArcaneWorkbench` and tile vis/wand data being behaviorally correct. Stage 9 recipe registration can affect whether recipe output appears, but it should not block GUI opening/layout.

### GAP-2: Arcane Bore GUI отсутствует на клиенте

**Статус:** частично реализовано
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/client/ClientProxy.java#getClientGuiElement` routes GUI ID `15` to `GuiArcaneBore` only when the client tile at the provided coordinates is `TileArcaneBore`.
- `src/main/java/thaumcraft/common/CommonProxy.java:100-103`
- `src/main/java/thaumcraft/common/blocks/BlockWoodenDevice.java:78-90`
- `src/main/java/thaumcraft/client/gui/GuiArcaneBore.java` exists as a 1.12.2 `GuiContainer` baseline using `ContainerArcaneBore`.
- `src/main/resources/assets/thaumcraft/textures/gui/gui_arcanebore.png` exists and matches the original asset.

**Референс:**
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/ClientProxy.class`, offsets `305-329` instantiate `GuiArcaneBore` for GUI ID `15`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiArcaneBore.class`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_arcanebore.png`.

**Что не совпадает:**
Current block activation reaches the server container, and the client now returns a texture-backed `GuiArcaneBore` for the matching tile. Reference layout state such as inventory slots, damaged-pick overlay, width/speed/property text is ported as a baseline, but manual opening and visual parity are still unverified.

**Что нужно доделать:**
Finish Arcane Bore GUI parity by manually validating the open scenario and comparing slot/state/progress layout in a client runtime.

**Как доделать:**
- Done: add `src/main/java/thaumcraft/client/gui/GuiArcaneBore.java`.
- Done: update `ClientProxy#getClientGuiElement` case `GUI_ARCANE_BORE` to fetch `TileArcaneBore`.
- Done: copy `thaumcraft_src/assets/thaumcraft/textures/gui/gui_arcanebore.png`.
- Scenario: activate Arcane Bore without wand through `BlockWoodenDevice.java:87-89` and verify GUI opens.

**Критерии приемки:**
- [ ] GUI ID `15` opens `GuiArcaneBore` on client.
- [x] Bore inventory slots and basic state/property display are implemented from the reference layout.
- [x] GUI uses the original bore texture.
- [ ] Manual client check confirms no crash when opening a placed bore.

**Checkpoint 2026-05-15 — Arcane Bore GUI baseline:**
- Added `GuiArcaneBore` with reference size `176x141`, `ContainerArcaneBore`, damaged-pick overlay, width/speed/property text, and original `gui_arcanebore.png`.
- Routed client GUI ID `15` to the screen with a `TileArcaneBore` type check.
- Validation: `./scripts/dev.sh compileJava` passed; `./scripts/dev.sh validate --smoke` passed with tests `10/10`, jar/check-jar summary `5149` MCP leak lines / `1030` unique leaks, server ready at `Done (1.266s)!`, and no crash reports under `run/`.
- Client smoke/manual GUI open was skipped because `DISPLAY=` and user-driven GUI/graphics validation is excluded for this run.
- Remaining: manual open/no-crash validation, visual comparison in client, and any renderer-side bore state parity outside the GUI screen.

**Риски / зависимости:**
Depends on `TileArcaneBore` state fields and `ContainerArcaneBore` slot layout matching the original enough for GUI display.

### GAP-3: Thaumatorium GUI отсутствует на клиенте

**Статус:** частично реализовано
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/client/ClientProxy.java#getClientGuiElement` routes GUI ID `3` to `GuiThaumatorium` only when the client tile at the provided coordinates is `TileThaumatorium`.
- `src/main/java/thaumcraft/common/CommonProxy.java:78-81`
- `src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java:131-147`
- `src/main/java/thaumcraft/client/gui/GuiThaumatorium.java` exists as a 1.12.2 `GuiContainer` baseline using `ContainerThaumatorium`.
- `src/main/resources/assets/thaumcraft/textures/gui/gui_thaumatorium.png` exists and matches the original asset.
- `src/main/resources/assets/thaumcraft/textures/aspects/**` contains the original aspect icons used by recipe display.

**Референс:**
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/ClientProxy.class`, offsets `200-224` instantiate `GuiThaumatorium` for GUI ID `3`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiThaumatorium.class`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_thaumatorium.png`.

**Что не совпадает:**
Reference GUI has recipe selection state (`index`, `lastSize`, `startAspect`), aspect output drawing, click handlers and button sounds. Current client now has those baseline state fields, output rendering, aspect icon/progress rendering, recipe navigation, and `sendEnchantPacket` selection, but visual/manual parity is not yet verified in a client runtime.

**Что нужно доделать:**
Finish Thaumatorium GUI parity by manually validating lower/top block opening, empty and populated recipe lists, aspect/progress visuals, and selected recipe behavior in a client runtime.

**Как доделать:**
- Done: add `src/main/java/thaumcraft/client/gui/GuiThaumatorium.java`.
- Done: route `GUI_THAUMATORIUM` in `ClientProxy#getClientGuiElement` to `TileThaumatorium`.
- Done: copy `gui_thaumatorium.png` and the original aspect texture set needed by the GUI.
- Scenario: open both lower and upper Thaumatorium block variants through `BlockMetalDevice.java:134-145`.

**Критерии приемки:**
- [ ] GUI ID `3` opens a Thaumatorium GUI for both base and top block activation paths.
- [x] Catalyst/recipe/aspect/progress areas are implemented with original texture resources and current tile/container data.
- [x] Selecting recipes sends/uses the same container interaction semantics as reference.
- [ ] Manual check covers empty recipe list and at least one valid catalyst.

**Checkpoint 2026-05-15 — Thaumatorium GUI baseline:**
- Added `GuiThaumatorium` with recipe index tracking, output rendering, aspect icon/progress rendering, recipe/aspect scroll controls, selection click handling, and reference button sounds.
- Routed client GUI ID `3` to the screen with a `TileThaumatorium` type check.
- Copied original `gui_thaumatorium.png` and the original `textures/aspects/**` set needed by aspect display.
- Validation: `./scripts/dev.sh compileJava` passed; `./scripts/dev.sh validate --smoke` passed with tests `10/10`, jar/check-jar summary `5173` MCP leak lines / `1039` unique leaks, server ready at `Done (1.236s)!`, and no crash reports under `run/`.
- Client smoke/manual GUI open was skipped because `DISPLAY=` and user-driven GUI/graphics validation is excluded for this run.
- Remaining: manual lower/top block open, empty and populated catalyst scenarios, visual comparison, and Stage 9 recipe availability coverage.

**Риски / зависимости:**
Dependency: recipe availability may depend on Stage 9 content/recipe registration. GUI opening and empty-state rendering are still Stage 8-b requirements.

### GAP-4: Focal Manipulator GUI отсутствует на клиенте

**Статус:** частично реализовано
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/client/ClientProxy.java#getClientGuiElement` routes GUI ID `20` to `GuiFocalManipulator` only when the client tile at the provided coordinates is `TileFocalManipulator`.
- `src/main/java/thaumcraft/common/CommonProxy.java:114-117`
- `src/main/java/thaumcraft/common/blocks/BlockStoneDevice.java:144-149`
- `src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java` exists as a 1.12.2 `GuiContainer` baseline using `ContainerFocalManipulator`.
- `src/main/java/thaumcraft/client/gui/GuiFocalManipulator$Sparkle.java` equivalent inner implementation remains absent/deferred.
- `src/main/resources/assets/thaumcraft/textures/gui/gui_wandtable.png` exists and matches the original asset.
- Focus upgrade localization keys and `wandtable.text1..3` exist in `src/main/resources/assets/thaumcraft/lang/en_us.lang`.

**Референс:**
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/ClientProxy.class`, offsets `437-461` instantiate `GuiFocalManipulator` for GUI ID `20`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiFocalManipulator.class`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiFocalManipulator$Sparkle.class`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_wandtable.png`.

**Что не совпадает:**
Reference GUI computes possible upgrades, selected upgrade, rank, aspect/XP costs, sparkles, fixed hover text and click handling. Current client now computes possible upgrades from the focus, shows selected cost/progress, renders upgrade/aspect icons, shows hover text, and sends the selected upgrade id through the existing container path. Sparkle animation and exact fixed tooltip positioning are not ported.

**Что нужно доделать:**
Finish Focal Manipulator parity by porting/refining sparkle animation and exact tooltip/layout behavior, then manually validate opening, focus insertion, upgrade selection, and start behavior in a client runtime.

**Как доделать:**
- Done: add `src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java` baseline.
- Remaining: add/refine inner `Sparkle` data if visual parity requires it.
- Done: update `ClientProxy#getClientGuiElement` case `GUI_FOCAL_MANIPULATOR`.
- Done: copy `gui_wandtable.png`; required aspect/focus textures exist in current resources.
- Scenario: open a placed Focal Manipulator through `BlockStoneDevice.java:145-147`, insert focus, inspect upgrade list and start button behavior.

**Критерии приемки:**
- [x] GUI ID `20` is routed to a tile-backed screen and displays focus slot, upgrade options, costs and progress from current tile/container data.
- [x] Upgrade selection and start interaction use the existing container `enchantItem` semantics.
- [ ] Sparkle/hover text rendering does not crash even if particle systems are otherwise deferred.
- [x] GUI uses original `gui_wandtable.png`.

**Checkpoint 2026-05-15 — Focal Manipulator GUI baseline:**
- Added `GuiFocalManipulator` with reference size `192x233`, `ContainerFocalManipulator`, possible-upgrade icon rendering, selected cost/progress display, hover text, and upgrade-id click routing through `sendEnchantPacket`.
- Routed client GUI ID `20` to the screen with a `TileFocalManipulator` type check.
- Copied original `gui_wandtable.png` and added focus upgrade/wandtable language keys needed by the screen.
- Validation: `./scripts/dev.sh compileJava` passed; `./scripts/dev.sh validate --smoke` passed with tests `10/10`, jar/check-jar summary `5187` MCP leak lines / `1040` unique leaks, server ready at `Done (1.177s)!`, and no crash reports under `run/`.
- Client smoke/manual GUI open was skipped because `DISPLAY=` and user-driven GUI/graphics validation is excluded for this run.
- Remaining: manual open/focus insertion/start scenarios, exact visual layout comparison, and sparkle animation parity.

**Риски / зависимости:**
Depends on focus upgrade data and relay/CV tile data being populated. Stage 9/content gaps can reduce available upgrades but must not prevent GUI opening.

### GAP-5: Golem GUI отсутствует на клиенте

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/client/ClientProxy.java:71-84`
- `src/main/java/thaumcraft/common/CommonProxy.java:66-69`
- `src/main/java/thaumcraft/common/entities/golems/EntityGolemBase.java:346-353`
- Отсутствует `src/main/java/thaumcraft/client/gui/GuiGolem.java`.
- Отсутствует `src/main/resources/assets/thaumcraft/textures/gui/guigolem.png`.

**Референс:**
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/ClientProxy.class`, offsets `108-128` instantiate `GuiGolem` for GUI ID `0` using entity ID `x`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiGolem.class`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/guigolem.png`.

**Что не совпадает:**
Current entity opens server GUI for golem cores with GUI, but client returns null. Reference GUI includes golem model preview, threat setting, scrollable inventory/filter controls, slot tooltips and button click packets. Current Stage 8-b has none of this screen.

**Что нужно доделать:**
Port Golem GUI enough to display core controls, inventory/filter slots and the original background, and route entity-based GUI ID `0`.

**Как доделать:**
- Add `src/main/java/thaumcraft/client/gui/GuiGolem.java`.
- Route `GUI_GOLEM` in `ClientProxy#getClientGuiElement` by fetching `world.getEntityByID(x)` and validating `EntityGolemBase`.
- Copy `guigolem.png`.
- Review current `ContainerGolem` for fields expected by reference (`currentScroll`, `maxScroll`, refresh behavior) before implementation.
- Scenario: interact with a golem core that `ItemGolemCore.hasGUI` accepts in `EntityGolemBase.java:347-350`.

**Критерии приемки:**
- [ ] GUI ID `0` opens for GUI-capable golems.
- [ ] Core-specific controls and filter/inventory scroll interactions are visible and usable.
- [ ] No client crash if entity ID is stale or invalid; invalid entity returns null safely.
- [ ] Texture and labels match reference layout.

**Риски / зависимости:**
Golem AI/task parity is outside Stage 8-b, but the GUI depends on current `ContainerGolem` fields matching reference interaction IDs.

### GAP-6: Pech trade GUI отсутствует на клиенте

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/client/ClientProxy.java:72-84`
- `src/main/java/thaumcraft/common/CommonProxy.java:70-73`
- `src/main/java/thaumcraft/common/entities/monster/EntityPech.java:279-286`
- Отсутствует `src/main/java/thaumcraft/client/gui/GuiPech.java`.
- Отсутствует `src/main/resources/assets/thaumcraft/textures/gui/gui_pech.png`.

**Референс:**
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/ClientProxy.class`, offsets `129-153` instantiate `GuiPech` for GUI ID `1`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiPech.class`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_pech.png`.

**Что не совпадает:**
Reference GUI draws `gui_pech.png`, checks trade offer slot/value state, and sends button interaction when trade conditions are satisfied. Current client returns null for the Pech GUI, so tamed Pech interaction cannot open the trade screen.

**Что нужно доделать:**
Port `GuiPech`, including trade button state, click handling and original texture.

**Как доделать:**
- Add `src/main/java/thaumcraft/client/gui/GuiPech.java`.
- Route `GUI_PECH` through `world.getEntityByID(x)` and `EntityPech` validation.
- Copy `gui_pech.png`.
- Scenario: interact with a tamed Pech as implemented in `EntityPech.java:279-283`.

**Критерии приемки:**
- [ ] GUI ID `1` opens for tamed Pech.
- [ ] Trade button only appears/enables under reference-equivalent valued-item conditions.
- [ ] Trade click sends the expected container/enchantment-button action.
- [ ] Original Pech GUI texture is used.

**Риски / зависимости:**
Pech taming/trade item valuation is common gameplay; if incomplete, GUI can still be implemented but final acceptance requires a valid in-game trade scenario.

### GAP-7: Traveling Trunk GUI отсутствует на клиенте

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/client/ClientProxy.java:73-84`
- `src/main/java/thaumcraft/common/CommonProxy.java:74-77`
- `src/main/java/thaumcraft/common/entities/golems/EntityTravelingTrunk.java:84-90`
- Отсутствует `src/main/java/thaumcraft/client/gui/GuiTravelingTrunk.java`.
- Отсутствует `src/main/resources/assets/thaumcraft/textures/gui/guitrunkbase.png`.

**Референс:**
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/ClientProxy.class`, offsets `154-174` instantiate `GuiTravelingTrunk` for GUI ID `2`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiTravelingTrunk.class`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/guitrunkbase.png`.

**Что не совпадает:**
Reference GUI sizes itself by trunk inventory rows, draws owner/name text via `entity.trunk.guiname`, has stay/move button text (`entity.trunk.move`, `entity.trunk.stay`), and sends a button action. Current client returns null and has no trunk texture or lang keys.

**Что нужно доделать:**
Port Traveling Trunk GUI, including dynamic rows, open/close behavior, stay/move toggle and lang keys.

**Как доделать:**
- Add `src/main/java/thaumcraft/client/gui/GuiTravelingTrunk.java`.
- Route `GUI_TRAVELING_TRUNK` through `world.getEntityByID(x)` and `EntityTravelingTrunk` validation.
- Copy `guitrunkbase.png`.
- Add lang keys equivalent to reference: `entity.trunk.guiname`, `entity.trunk.move`, `entity.trunk.stay`.
- Scenario: interact with Traveling Trunk and toggle stay/move.

**Критерии приемки:**
- [ ] GUI ID `2` opens for Traveling Trunk.
- [ ] Inventory row count matches trunk capacity.
- [ ] Stay/move toggle renders localized text and updates state through container button interaction.
- [ ] Closing GUI calls the appropriate open-state cleanup without client/server desync.

**Риски / зависимости:**
Depends on current `EntityTravelingTrunk` and `ContainerTravelingTrunk` preserving reference state fields and button IDs.

### GAP-8: Thaumonomicon / Research Browser GUI отсутствует

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/items/relics/ItemThaumonomicon.java:43-50`
- `src/main/java/thaumcraft/common/CommonProxy.java:95`
- `src/main/java/thaumcraft/client/ClientProxy.java:78-84`
- Отсутствует `src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java`.
- Отсутствует `src/main/java/thaumcraft/client/gui/GuiResearchRecipe.java`.
- Отсутствует `src/main/java/thaumcraft/client/gui/GuiResearchPopup.java`.
- Отсутствуют `src/main/resources/assets/thaumcraft/textures/gui/gui_research.png`, `gui_researchbook.png`, `gui_researchbook_overlay.png`, `gui_researchback.png`, `gui_researchbackeldritch.png`.

**Референс:**
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/ClientProxy.class`, offsets `247-254` instantiate `GuiResearchBrowser` for GUI ID `12`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiResearchBrowser.class`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiResearchRecipe.class`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiResearchPopup.class`.
- Reference string/resource constants include `textures/gui/gui_research.png`, `tc.research.shortprim`, `tc.research.short`, `tc.forbidden`, `tc.forbidden.level.`, `tc.research.hasnote`, `tc.research.getprim`, `tc.research.purchase`, `tc.researchmissing`, `tc.research.popup`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_research*.png` and research background textures.

**Что не совпадает:**
Thaumonomicon right-click can request GUI ID `12`, but the client returns null. The full research browser, category map, popups, recipe page display, unlock/purchase interactions, and supporting textures/lang are absent.

**Что нужно доделать:**
Port Thaumonomicon research browser and recipe/popup screens sufficiently to browse current research categories, open recipe pages, and handle locked/forbidden states without crashes.

**Как доделать:**
- Add `GuiResearchBrowser`, `GuiResearchRecipe`, `GuiResearchPopup` and needed inner helper structures under `src/main/java/thaumcraft/client/gui/`.
- Route `GUI_THAUMONOMICON` case `12` to `new GuiResearchBrowser()` in `ClientProxy#getClientGuiElement` while keeping server element null.
- Copy direct GUI textures from `thaumcraft_src/assets/thaumcraft/textures/gui/` and direct support textures from `textures/misc/**` / `textures/aspects/**`.
- Add required lang keys from reference `en_US.lang` into `src/main/resources/assets/thaumcraft/lang/en_us.lang` using 1.12 lowercase locale path.
- Scenario: right-click Thaumonomicon, switch categories, click known and locked research entries, open recipe page.

**Критерии приемки:**
- [ ] GUI ID `12` opens a research browser client screen.
- [ ] Browser displays categories and research nodes without missing texture crashes.
- [ ] Clicking research opens recipe/detail screen or locked-state feedback matching reference behavior.
- [ ] Required research lang keys and GUI textures are present.

**Риски / зависимости:**
Dependency: actual research category/item population is Stage 9/content-sensitive. Stage 8-b acceptance can verify empty/partial data states, but full browsing parity needs registered research data.

### GAP-9: Research Table / Research Notes GUI отсутствует

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/client/ClientProxy.java:77-84`
- `src/main/java/thaumcraft/common/CommonProxy.java:91-94`
- `src/main/java/thaumcraft/common/blocks/BlockTable.java:161-170`
- Отсутствует `src/main/java/thaumcraft/client/gui/GuiResearchTable.java`.
- Отсутствуют `src/main/resources/assets/thaumcraft/textures/gui/guiresearchtable2.png`, `hex1.png`, `hex2.png`.
- Current lang has no `tile.researchtable.noink.*` or `tc.research.copy` entries in `src/main/resources/assets/thaumcraft/lang/en_us.lang:1-118`.

**Референс:**
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/ClientProxy.class`, offsets `225-246` instantiate `GuiResearchTable` for GUI ID `10`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiResearchTable.class`, `GuiResearchTable$Rune.class`, `GuiResearchTable$Coord2D.class`.
- Reference resource constants include `textures/gui/guiresearchtable2.png`, `textures/gui/hex1.png`, `textures/gui/hex2.png`, `textures/aspects/_back.png`, `textures/aspects/_unknown.png`, `textures/misc/parchment3.png`, `textures/misc/script.png`.

**Что не совпадает:**
Server/common can open `GUI_RESEARCH_TABLE`, including adjacent table lookup, but client returns null. Reference GUI implements research-note puzzle pages, aspect selection/combine/write/erase/scroll clicks, rune drawing, lines/highlights and no-ink messages. Current has no screen, textures, lang or client interaction layer.

**Что нужно доделать:**
Port `GuiResearchTable` and direct support resources/lang.

**Как доделать:**
- Add `src/main/java/thaumcraft/client/gui/GuiResearchTable.java` with helper inner classes.
- Route `GUI_RESEARCH_TABLE` to `TileResearchTable`.
- Copy `guiresearchtable2.png`, `hex1.png`, `hex2.png`, aspect back/unknown textures, `parchment3.png`, `script.png` and any directly used research GUI misc textures.
- Add reference lang keys such as `tc.research.copy`, `tile.researchtable.noink.0`, `tile.researchtable.noink.1`.
- Scenario: open research table with no note, with a note, with ink missing, and attempt aspect/rune interaction.

**Критерии приемки:**
- [ ] GUI ID `10` opens for the research table and adjacent paired table path.
- [ ] Research note puzzle surface renders with hex/rune/aspect visuals.
- [ ] Aspect interactions send expected container/button actions.
- [ ] No missing lang key is visible for no-ink/copy states.

**Риски / зависимости:**
Depends on current `ResearchNoteData`, aspect lists and `ContainerResearchTable` behavior. Full note-solving parity may require Stage 3/9 research data completion.

### GAP-10: Additional current/common GUI IDs with reference screens return null

**Статус:** частично реализовано
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/client/ClientProxy.java#getClientGuiElement` routes GUI ID `8` to `GuiDeconstructionTable` only when the client tile is `TileDeconstructionTable`.
- `src/main/java/thaumcraft/client/ClientProxy.java#getClientGuiElement` routes GUI ID `9` to `GuiAlchemyFurnace` only when the client tile is `TileAlchemyFurnace`.
- `src/main/java/thaumcraft/client/ClientProxy.java#getClientGuiElement` routes GUI ID `18` to `GuiMagicBox` when the target tile implements `IInventory`.
- `src/main/java/thaumcraft/client/ClientProxy.java#getClientGuiElement` routes GUI ID `19` to `GuiSpa` only when the client tile is `TileSpa`.
- `src/main/java/thaumcraft/client/gui/GuiDeconstructionTable.java` exists as a 1.12.2 `GuiContainer` baseline using `ContainerDeconstructionTable`.
- `src/main/java/thaumcraft/client/gui/GuiAlchemyFurnace.java` exists as a 1.12.2 `GuiContainer` baseline using `ContainerAlchemyFurnace`.
- `src/main/java/thaumcraft/client/gui/GuiMagicBox.java` exists as a chest-style 1.12.2 `GuiContainer` baseline using `ContainerMagicBox`.
- `src/main/java/thaumcraft/client/gui/GuiSpa.java` exists as a texture-backed 1.12.2 `GuiContainer` baseline using `ContainerSpa`.
- `src/main/java/thaumcraft/common/container/ContainerDeconstructionTable.java` now has the reference input/player slot layout, `breaktime` property sync, shift-click routing, and aspect-claim button handling.
- `src/main/java/thaumcraft/common/container/SlotLimitedHasAspects.java` ports the original aspect-bearing item slot filter.
- `src/main/java/thaumcraft/common/container/ContainerMagicBox.java` now has chest/player slot layout and shift-click routing for inventory tiles.
- `src/main/java/thaumcraft/common/container/ContainerSpa.java` now has bath-salts slot, player inventory binding, mix toggle button handling, and shift-click routing.
- `src/main/java/thaumcraft/common/tiles/TileSpa.java` now has inventory/fluid tank state, NBT persistence, mix state, and fluid capability exposure required by the GUI/container baseline.
- `src/main/resources/assets/thaumcraft/textures/gui/gui_decontable.png` and `gui_alchemyfurnace.png` exist and match the original assets.
- `src/main/resources/assets/thaumcraft/textures/gui/gui_spa.png` exists and matches the original asset.
- `src/main/java/thaumcraft/common/CommonProxy.java:83-90`
- `src/main/java/thaumcraft/common/CommonProxy.java:106-113`
- `src/main/java/thaumcraft/common/blocks/BlockTable.java:157-159`
- `src/main/java/thaumcraft/common/blocks/BlockStoneDevice.java:127-130`

**Референс:**
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/ClientProxy.class`, offsets `175-199` instantiate `GuiAlchemyFurnace`, `280-304` instantiate `GuiDeconstructionTable`, `387-411` instantiate `GuiMagicBox`, `412-436` instantiate `GuiSpa`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiDeconstructionTable.class`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiAlchemyFurnace.class`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiMagicBox.class`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiSpa.class`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_decontable.png`, `gui_alchemyfurnace.png`, `gui_spa.png`; Magic Box uses vanilla/container-style chest texture behavior in reference class.

**Что не совпадает:**
The user scope says to include any current/common GUI IDs that reference client screens. Deconstruction Table, Alchemy Furnace, Magic Box, and Spa now have non-null client screens and baseline routing/resources. Deconstruction item-breaking logic, Spa world-fluid behavior, and legacy Magic Box linked-storage behavior are still broader gameplay parity tasks outside this GUI checkpoint.

**Что нужно доделать:**
Run manual client scenarios and document any remaining behavior gaps, then decide whether gameplay-level differences (especially Spa/Deconstruction/Magic Box backend behavior) stay deferred to later non-GUI checkpoints.

**Как доделать:**
- Done: add `GuiDeconstructionTable` and `GuiAlchemyFurnace` under `src/main/java/thaumcraft/client/gui/`.
- Done: route `GUI_DECONSTRUCTION_TABLE` and `GUI_ALCHEMY_FURNACE` in `ClientProxy#getClientGuiElement`.
- Done: copy `gui_decontable.png` and `gui_alchemyfurnace.png`.
- Done: add `GuiMagicBox` and `GuiSpa` under `src/main/java/thaumcraft/client/gui/`.
- Done: route `GUI_MAGIC_BOX` and `GUI_SPA` in `ClientProxy#getClientGuiElement`.
- Done: copy `gui_spa.png` and add missing SPA lang keys used by the GUI.
- Scenarios: open Deconstruction Table, Alchemy Furnace, Magic Box tile, and Spa tile.

**Критерии приемки:**
- [x] Current/common GUI IDs `8`, `9`, `18`, and `19` now have non-null client screens and matching server container routing.
- [ ] Deconstruction Table and Alchemy Furnace open from their block activation paths.
- [ ] Magic Box and Spa screens open when corresponding tiles are present.
- [x] Deconstruction Table, Alchemy Furnace, and Spa textures/resources are present and used by code.

**Checkpoint 2026-05-15 — Deconstruction Table and Alchemy Furnace GUI baseline:**
- Added `GuiDeconstructionTable` with the original `gui_decontable.png`, breaktime bar rendering, aspect icon/tooltip rendering, and aspect claim click through `sendEnchantPacket(windowId, 1)`.
- Added `GuiAlchemyFurnace` with the original `gui_alchemyfurnace.png` and reference burn, cook-progress, vis-content, and overlay bars.
- Routed client GUI IDs `8` and `9` to the matching tile-backed screens.
- Restored the Deconstruction Table input/player slot layout, aspect-bearing slot filter, breaktime property sync, shift-click routing, and server aspect-claim handling in its container.
- Validation: `./scripts/dev.sh compileJava` passed; `./scripts/dev.sh validate --smoke` passed with tests `10/10`, jar/check-jar summary `5224` MCP leak lines / `1042` unique leaks, server ready at `Done (1.208s)!`, and no crash reports under `run/`.
- Client smoke/manual GUI open was skipped because `DISPLAY=` and user-driven GUI/graphics validation is excluded for this run.
- Remaining: manual Deconstruction/Alchemy open checks, visual comparison, and full Deconstruction item-breaking gameplay parity.

**Checkpoint 2026-05-15 — Magic Box and Spa GUI baseline:**
- Added `GuiMagicBox` using the reference chest-screen style (`generic_54` texture path) over the existing Magic Box container route.
- Added `GuiSpa` with original `gui_spa.png`, mix-toggle button icon/tooltip/click path, and fluid tooltip/bar rendering from tile tank state.
- Routed client GUI IDs `18` and `19` to `GuiMagicBox` and `GuiSpa`.
- Restored baseline server/container support needed by these screens: Magic Box chest/player slot binding and shift-click, Spa bath-salts slot, mix toggle button path, and a minimal persisted `TileSpa` inventory/tank/mix/capability surface.
- Added `BlockStoneDevice` openGui route for Spa (`GUI_SPA`) to match the active stone-device meta path.
- Validation: `./scripts/dev.sh compileJava` passed; `./scripts/dev.sh validate --smoke` passed with tests `10/10`, jar/check-jar summary `5292` MCP leak lines / `1047` unique leaks, server ready at `Done (1.175s)!`, and no crash reports under `run/`.
- Client smoke/manual GUI open was skipped because `DISPLAY=` and user-driven GUI/graphics validation is excluded for this run.
- Remaining: manual Magic Box/Spa open checks and broader gameplay parity for Magic Box linked storage and Spa fluid placement behavior.

**Риски / зависимости:**
Spa fluid rendering now depends on block-atlas fluid sprites and a valid tank state. Deconstruction item-breaking remains dependent on the tile update logic outside this GUI checkpoint, while Magic Box linked-storage and Spa world-fluid automation remain separate gameplay parity tasks.

### GAP-11: Existing Focus Pouch, Hand Mirror and Hover Harness GUIs are placeholder-like and not reference-parity

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/client/ClientProxy.java` routes GUI IDs `5`, `16`, and `17` to the three screens.
- `src/main/java/thaumcraft/client/gui/GuiFocusPouch.java` binds `gui_focuspouch.png`, uses reference size `175x232`, and disables hotbar key swaps while open.
- `src/main/java/thaumcraft/client/gui/GuiHandMirror.java` binds `guihandmirror.png` and disables hotbar key swaps while open.
- `src/main/java/thaumcraft/client/gui/GuiHoverHarness.java` binds `guihoverharness.png` and disables hotbar key swaps while open.
- `src/main/java/thaumcraft/common/container/ContainerFocusPouch.java` uses reference slot coordinates for the taller Focus Pouch texture.
- `src/main/resources/assets/thaumcraft/textures/gui/gui_focuspouch.png`, `guihandmirror.png`, and `guihoverharness.png` exist and match the original assets.

**Референс:**
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiFocusPouch.class` uses `textures/gui/gui_focuspouch.png`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiHandMirror.class` uses `textures/gui/guihandmirror.png`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiHoverHarness.class` uses `textures/gui/guihoverharness.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_focuspouch.png`, `guihandmirror.png`, `guihoverharness.png`.

**Что не совпадает:**
Current classes now bind original textures and block hotbar key swaps like the reference. Focus Pouch slot coordinates were moved to the original taller layout. Manual item movement/duplication checks and visual comparison are still unverified.

**Что нужно доделать:**
Finish item GUI parity by manually validating item movement, blocked backing-item slots, hotbar-key behavior, and visual alignment in a client runtime.

**Как доделать:**
- Done: update `GuiFocusPouch`, `GuiHandMirror`, `GuiHoverHarness` to bind `ResourceLocation` textures and draw reference backgrounds.
- Done: copy `gui_focuspouch.png`, `guihandmirror.png`, `guihoverharness.png`.
- Done: port `func_146983_a` semantics to 1.12.2 `checkHotbarKeys`.
- Scenarios: open each item GUI, try hotbar key swaps, close/reopen while item is in different inventory slot.

**Критерии приемки:**
- [x] Existing three GUIs use original textures, not grey rectangles.
- [x] Focus Pouch slot grid aligns with container slots and original texture coordinates.
- [x] Hand Mirror and Hover Harness block hotbar-key swaps per reference behavior.
- [ ] Manual open/close and item movement scenarios do not duplicate/delete items.

**Checkpoint 2026-05-15 — Item GUI texture baseline:**
- Replaced Focus Pouch, Hand Mirror, and Hover Harness placeholder backgrounds with original texture-backed backgrounds.
- Moved Focus Pouch slot coordinates to the original tall layout and disabled hotbar key swaps in all three item GUIs.
- Copied original `gui_focuspouch.png`, `guihandmirror.png`, and `guihoverharness.png`.
- Validation: `./scripts/dev.sh compileJava` passed; `./scripts/dev.sh validate --smoke` passed with tests `10/10`, jar/check-jar summary `5190` MCP leak lines / `1040` unique leaks, server ready at `Done (1.326s)!`, and no crash reports under `run/`.
- Client smoke/manual GUI open and item movement scenarios were skipped because `DISPLAY=` and user-driven GUI/graphics validation is excluded for this run.
- Remaining: manual open/close, hotbar-key, and item movement/duplication checks.

**Риски / зависимости:**
Inventory-slot blocking is behavior-sensitive and can cause item loss/duplication if ported incorrectly.

### GAP-12: GUI texture resource tree is missing from current resources

**Статус:** частично реализовано
**Критичность:** blocker

**Текущая реализация:**
- `src/main/resources/assets/thaumcraft/textures/gui/` now contains `gui_thaumatorium.png`, `gui_arcaneworkbench.png`, `gui_arcanebore.png`, `gui_wandtable.png`, `gui_decontable.png`, `gui_alchemyfurnace.png`, `gui_spa.png`, `gui_focuspouch.png`, `guihandmirror.png`, and `guihoverharness.png`.
- `src/main/resources/assets/thaumcraft/textures/misc/potions.png` is the only current `textures/misc` file found.
- `src/main/resources/assets/thaumcraft/textures/aspects/**` now contains the original aspect icon set copied for Thaumatorium/aspect GUI rendering.

**Референс:**
- `thaumcraft_src/assets/thaumcraft/textures/gui/arcane.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_arcaneworkbench.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_arcanebore.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_thaumatorium.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_wandtable.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/guihoverharness.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/guigolem.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_pech.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/guitrunkbase.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/guiresearchtable2.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_research.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_researchbook.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_researchbook_overlay.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_researchback.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_researchbackeldritch.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_focuspouch.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/guihandmirror.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_decontable.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_alchemyfurnace.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_spa.png`.
- `thaumcraft_src/assets/thaumcraft/textures/gui/hex1.png`, `hex2.png`, `hud.png`.
- Research support textures under `thaumcraft_src/assets/thaumcraft/textures/misc/**` and aspect icons under `thaumcraft_src/assets/thaumcraft/textures/aspects/**`.

**Что не совпадает:**
The texture resource tree now covers the newly ported Thaumatorium, Arcane Workbench, Arcane Bore, Focal Manipulator, Deconstruction Table, Alchemy Furnace, Spa, Focus Pouch, Hand Mirror, Hover Harness, and aspect-icon paths, but most remaining Stage 8-b GUI textures and direct research misc support textures are still absent.

**Что нужно доделать:**
Copy original GUI and directly used support textures from `thaumcraft_src/assets/` to `src/main/resources/assets/thaumcraft/`.

**Как доделать:**
- Create `src/main/resources/assets/thaumcraft/textures/gui/` and copy all Stage 8-b GUI textures listed above.
- Create/copy required `textures/aspects/**` for aspect icons used by workbench, thaumatorium, research and focal manipulator screens.
- Copy direct research misc textures: at minimum `parchment3.png`, `script.png`, plus any texture discovered while porting `GuiResearchBrowser`/`GuiResearchRecipe`.
- Run a resource path scan after implementation to ensure every `new ResourceLocation` or texture bind in Stage 8-b has a file.

**Критерии приемки:**
- [ ] Every Stage 8-b GUI texture referenced by code exists under `src/main/resources/assets/thaumcraft/textures/gui/`.
- [ ] Aspect and research support textures referenced by Stage 8-b GUI code exist.
- [ ] Client log has no missing texture warnings for Stage 8-b GUI screens during manual smoke.
- [ ] Assets are copied from `thaumcraft_src/assets/`, not recreated.

**Риски / зависимости:**
Resource path case changed from 1.7 `en_US.lang` style to 1.12 lowercase language file convention, but texture paths under `assets/thaumcraft/textures/**` should remain original-compatible lowercase paths.

### GAP-13: GUI language keys and research font support are incomplete

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/resources/assets/thaumcraft/lang/en_us.lang` now contains the existing container labels plus focus upgrade names/descriptions and `wandtable.text1..3` for `GuiFocalManipulator`.
- TrueType helper classes exist under `src/main/java/thaumcraft/truetyper/TrueTypeFont.java`, `Formatter.java`, `FontLoader.java`, `FontHelper.java`, but no Stage 8-b GUI currently uses them.
- `src/main/java/thaumcraft/client/gui/**` has no research browser/table classes that would initialize the reference `galFontRenderer` fields.

**Референс:**
- `Thaumcraft-1.7.10-4.2.3.5.jar!/assets/thaumcraft/lang/en_US.lang` contains GUI/research/trunk keys including `tc.research.copy`, `tile.researchtable.noink.0`, `tile.researchtable.noink.1`, `entity.trunk.guiname`, `entity.trunk.move`, `entity.trunk.stay`, `tc.research.shortprim`, `tc.research.short`, `tc.forbidden`, `tc.forbidden.level.*`, `tc.research.hasnote`, `tc.research.getprim`, `tc.research.purchase`, `tc.researchmissing`, `tc.research.popup`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/gui/GuiResearchTable.class` and `GuiResearchBrowser.class` have `galFontRenderer` fields.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/truetyper/**` provides the reference font helpers.

**Что не совпадает:**
Current localization is insufficient for research browser/table, trunk controls, no-ink messages, forbidden/locked research states, and other GUI labels. Font helper code exists, but the research GUIs that use special fonts are absent, so font parity is unverified.

**Что нужно доделать:**
Add all Stage 8-b lang keys used by ported GUI code and verify the research font path initializes/render text correctly in 1.12.2.

**Как доделать:**
- Extract only GUI/research keys required by Stage 8-b from reference `assets/thaumcraft/lang/en_US.lang` into `src/main/resources/assets/thaumcraft/lang/en_us.lang`.
- While porting research GUIs, wire `FontLoader`/`FontHelper` or Minecraft `FontRenderer` fallback deliberately; do not leave null font fields.
- Scenario: browse research pages with normal, forbidden and missing-requirement text; open trunk and research table no-ink states.

**Критерии приемки:**
- [ ] No visible `tc.*`, `entity.trunk.*`, or `tile.researchtable.*` raw keys in Stage 8-b GUIs.
- [ ] Research browser/table special text renders legibly.
- [ ] Localization additions are limited to keys actually used by Stage 8-b screens.
- [ ] Font initialization does not crash headless/server side and stays client-only.

**Риски / зависимости:**
Adding every reference lang key broadly would be noisy; keep this scoped to Stage 8-b GUI keys unless Stage 9 requests broader localization.

### GAP-14: Thaumometer scan feedback has no client GUI/overlay implementation in current client tree

**Статус:** отсутствует  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java:37-69`
- No `GuiThaumometer`, scan overlay, scan HUD, or client event handler for scanner feedback found under `src/main/java/thaumcraft/client/**`.
- `src/main/java/thaumcraft/client/ClientProxy.java:42-44` registers only a tooltip event handler; no scan overlay handler.

**Референс:**
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/common/items/relics/ItemThaumometer.class`.
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/client/ClientProxy.class`, setup item renderers includes an `ItemThaumometerRenderer` registration at offsets `42-52` in the disassembled `setupItemRenderers` section.
- `thaumcraft_src/assets/thaumcraft/textures/models/scanner.png`, `scanner.obj`, `scanscreen.png`, `scanscreen.png.mcmeta` are reference item/model scan screen assets, not `textures/gui/**` GUI screens.

**Что не совпадает:**
The current Thaumometer has server-side scanning but no client visual scan overlay/screen in Stage 8-b terms. The reference appears to implement scan visuals primarily through item rendering/model assets rather than a `GuiScreen`, so this is a Stage 8-b edge only if the port chooses to represent scan feedback as a GUI/overlay.

**Что нужно доделать:**
Decide and document whether Thaumometer scan feedback belongs to Stage 8-b GUI screens or Stage 8 client render/overlay chunk. If implemented as GUI/HUD overlay, add scoped client overlay code and resources; if kept as item renderer/model work, mark as dependency outside Stage 8-b and verify no GUI route is expected.

**Как доделать:**
- Inspect/decompile `ItemThaumometerRenderer` and current item renderer strategy before implementation.
- If GUI overlay is implemented: add client-only overlay handler under `src/main/java/thaumcraft/client/**`, required `scanscreen` resources, and manual scan scenario.
- If not GUI: add no `GUI_*` route; document in Stage 8 render/overlay chunk instead.
- Scenario: use Thaumometer on entity, node and block, verify player gets visible feedback or documented non-GUI renderer path.

**Критерии приемки:**
- [ ] Stage 8-b document states whether scan feedback is a GUI screen/overlay or deferred to render/overlay work.
- [ ] If implemented as GUI/overlay, scan feedback appears on client after a valid scan.
- [ ] If not implemented as GUI, no dead GUI ID or null GUI route is left for Thaumometer.
- [ ] Manual scan scenario is recorded.

**Риски / зависимости:**
This is a dependency boundary with client item rendering/overlay work, not a blocker for container GUI closure if explicitly assigned outside Stage 8-b.

### GAP-15: No Stage 8-b runtime/manual validation evidence exists

**Статус:** требует проверки  
**Критичность:** high

**Текущая реализация:**
- `docs/PRD.md:531-533` requires core GUIs to open without client crash for runtime smoke checklist.
- `AGENTS.md:155-173` says compile success is not enough and client smoke should run for client-only or mixed client/common changes when display is available.
- No existing `docs/Stage8-b.md` was present before this analysis; no Stage 8-b validation evidence was found in the requested artifact location.

**Референс:**
- Reference jar contains complete client GUI routing/screens/resources listed in sections above.

**Что не совпадает:**
Current state has not been manually validated for Stage 8-b scenarios, and most screens cannot be opened because client routing returns null. Even after implementation, Stage 8-b cannot be claimed complete without runtime/manual smoke evidence.

**Что нужно доделать:**
After implementation, run build/resource checks and manual/client smoke for every Stage 8-b GUI scenario.

**Как доделать:**
- Run `./scripts/dev.sh compileJava` after code/resources are added.
- Run `./scripts/dev.sh build` and `./scripts/dev.sh check-jar` before packaging claims.
- Run `./scripts/dev.sh smoke-client` if display/X11 is available.
- Manual scenarios: open Arcane Workbench, Arcane Bore, Thaumatorium, Focal Manipulator, Hover Harness, Golem, Pech, Traveling Trunk, Thaumonomicon, Research Table, Focus Pouch, Hand Mirror, Deconstruction Table, Alchemy Furnace, Magic Box, Spa; record pass/fail and missing textures/lang.

**Критерии приемки:**
- [ ] Compile/build/check-jar results are recorded after implementation.
- [ ] Client smoke or concrete reason for skip is recorded.
- [ ] Manual open scenario for each Stage 8-b GUI is recorded.
- [ ] No blocker/high Stage 8-b gaps remain open.

**Риски / зависимости:**
Client smoke may require display/X11. Some scenarios depend on content/research/recipe data, but GUI opening and empty-state checks remain required.

## 6. Итоговый checklist закрытия Stage 8-b

- [ ] `ClientProxy#getClientGuiElement` routes every Stage 8-b/current GUI ID to a concrete client screen or a documented non-GUI/deferred path.
- [ ] `GuiArcaneWorkbench` ported and texture-backed.
- [ ] `GuiArcaneBore` ported and texture-backed.
- [ ] `GuiThaumatorium` ported and texture-backed.
- [ ] `GuiFocalManipulator` ported and texture-backed.
- [ ] `GuiGolem` ported and texture-backed.
- [ ] `GuiPech` ported and texture-backed.
- [ ] `GuiTravelingTrunk` ported and texture-backed.
- [ ] `GuiResearchBrowser`, `GuiResearchRecipe`, and `GuiResearchPopup` ported enough for Thaumonomicon browsing.
- [ ] `GuiResearchTable` ported enough for research-note/table interaction.
- [ ] Deconstruction Table, Alchemy Furnace, Magic Box and Spa screens either ported or explicitly moved out of Stage 8-b with a documented reason.
- [ ] Existing Focus Pouch, Hand Mirror and Hover Harness GUI placeholders replaced with original texture-backed layouts and safe slot/key behavior.
- [ ] All Stage 8-b GUI textures copied from `thaumcraft_src/assets/thaumcraft/textures/gui/` into `src/main/resources/assets/thaumcraft/textures/gui/`.
- [ ] Direct research/aspect/misc support textures required by Stage 8-b are present.
- [ ] Stage 8-b lang keys are present in `src/main/resources/assets/thaumcraft/lang/en_us.lang`.
- [ ] Research font handling is implemented or intentionally replaced with a verified 1.12-compatible renderer.
- [ ] Thaumometer scan feedback boundary is decided: GUI/overlay in Stage 8-b or renderer/overlay dependency outside Stage 8-b.
- [ ] `./scripts/dev.sh compileJava` passes after implementation.
- [ ] `./scripts/dev.sh build` and `./scripts/dev.sh check-jar` pass before packaging/runtime claims.
- [ ] Client smoke/manual GUI-open scenarios are recorded.

## 7. Definition of Done

Stage 8-b считается ПОЛНОСТЬЮ завершенной только если:
- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 8-b реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 8-b;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 8-b проверены вручную или тестами;
- ./docs/Stage8-b.md обновлен и не содержит критичных открытых вопросов.

## 8. Открытые вопросы

- Thaumometer scan feedback: reference evidence points to item renderer/model assets rather than a normal `GuiScreen`; decide whether its visible feedback belongs in Stage 8-b GUI overlay work or a separate Stage 8 render/overlay chunk before implementation.
- Magic Box and Spa are active current/common GUI IDs and reference client screens, but not named in the core user list; this analysis includes them because the task explicitly includes any current/common GUI IDs that reference client screens. If planning wants a narrower execution checkpoint, explicitly defer them before claiming Stage 8-b complete.
