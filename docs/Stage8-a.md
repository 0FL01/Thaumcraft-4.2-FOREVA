# Stage 8-a — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 8-a закрывает только клиентский bootstrap и client/server side separation для дальнейших GUI/render/FX работ Stage 8. Цель: клиентская прокси должна безопасно подключаться через Forge 1.12.2 lifecycle, регистрировать клиентские точки входа, key bindings, client event handlers, GUI routing boundary и клиентские packet/FX boundaries без dedicated-server крашей и без утверждения визуальной parity конкретных GUI/render/FX систем.

Эта фаза не закрывает визуальную parity GUI, TESR, entity renderers, particle engine, beam/bolt и shader effects. Эти элементы упоминаются ниже только как зависимости, когда их отсутствие блокирует bootstrap boundary.

## 2. Scope фазы

- `ClientProxy` как единственная client-side реализация `CommonProxy`: lifecycle hooks, registration entry points, GUI routing boundary, keybind bootstrap boundary.
- Client-side event handlers: наличие классов, регистрация на нужные Forge/FML event buses, отсутствие server-side загрузки client-only типов.
- Key bindings: регистрация клавиш, состояние нажатий, dispatch client-to-server packets.
- Client-only packet/FX registration boundaries: корректная регистрация сетевых сообщений и безопасное выполнение client-only handlers.
- Side annotations/boundaries: common/server код не должен напрямую тянуть client-only классы в server-only runtime paths.
- Client smoke readiness: мод должен доходить до client load/main menu без crash markers до проверки конкретной GUI/render/FX parity.

Вне scope Stage 8-a: реализация конкретных GUI экранов, TESR/entity renderer визуальная parity, particle/beam/bolt/shader fidelity, research/recipe/content completion. Если эти части нужны для компиляции или smoke, они отмечены как зависимости.

## 3. Источники сравнения

- PRD Phase 8 scope and risks: `docs/PRD.md:365-393`.
- PRD client smoke checklist: `docs/PRD.md:531-538`.
- Project client architecture boundary: `docs/PRD.md:117-138`.
- Required runtime smoke rule: `AGENTS.md:155-177`.
- Current mod lifecycle/proxy hookup: `src/main/java/thaumcraft/common/Thaumcraft.java:62-88`, `src/main/java/thaumcraft/common/Thaumcraft.java:164-172`.
- Current common proxy: `src/main/java/thaumcraft/common/CommonProxy.java:23-162`.
- Current client proxy: `src/main/java/thaumcraft/client/ClientProxy.java:22-106`.
- Current packet table: `src/main/java/thaumcraft/common/lib/network/PacketHandler.java:48-107`.
- Current client config GUI metadata: `src/main/java/thaumcraft/client/gui/GuiFactory.java:1-28`, `src/main/resources/mcmod.info:1-11`.
- Reference lifecycle/proxy: `thaumcraft_src/thaumcraft/common/Thaumcraft.class`, decompiled during analysis; key methods: `preInit`, `init`.
- Reference client proxy: `thaumcraft_src/thaumcraft/client/ClientProxy.class`, decompiled during analysis; key methods: `registerHandlers`, `registerKeyBindings`, `getClientGuiElement`, `registerDisplayInformation`, `setupItemRenderers`, `setupEntityRenderers`, `setupBlockRenderers`, `setupTileRenderers`.
- Reference common proxy: `thaumcraft_src/thaumcraft/common/CommonProxy.class`, decompiled during analysis; key methods: client-safe stubs and full GUI id routing.
- Reference key handler: `thaumcraft_src/thaumcraft/common/lib/events/KeyHandler.class`, decompiled during analysis.
- Reference client tick/render handlers: `thaumcraft_src/thaumcraft/client/lib/ClientTickEventsFML.class`, `thaumcraft_src/thaumcraft/client/lib/RenderEventHandler.class`, decompiled during analysis.
- Reference packet table: `thaumcraft_src/thaumcraft/common/lib/network/PacketHandler.class`, decompiled during analysis.

Lightweight validation/inspection commands run for this analysis:

- `git status --short` — clean before document creation.
- `find thaumcraft_src/thaumcraft -type f \( -name '*Proxy.class' -o -name '*Key*.class' -o -name '*Event*.class' -o -name '*Tick*.class' -o -name '*GuiFactory.class' -o -name '*Packet*.class' \) | sort`.
- `cfr --silent true thaumcraft_src/thaumcraft/client/ClientProxy.class`.
- `cfr --silent true thaumcraft_src/thaumcraft/common/CommonProxy.class`.
- `cfr --silent true thaumcraft_src/thaumcraft/common/lib/events/KeyHandler.class`.
- `cfr --silent true thaumcraft_src/thaumcraft/client/lib/ClientTickEventsFML.class`.
- `cfr --silent true thaumcraft_src/thaumcraft/client/lib/RenderEventHandler.class`.
- `cfr --silent true thaumcraft_src/thaumcraft/common/lib/network/PacketHandler.class`.
- `cfr --silent true thaumcraft_src/thaumcraft/common/Thaumcraft.class | sed -n '1,220p'`.
- `rg`/Glob-based searches via OpenCode tools for `ClientProxy`, `CommonProxy`, `KeyBinding`, event buses, side annotations, client imports, shader resources, and TODO/stub markers.

No heavy build or runtime smoke was run; this task is documentation-only gap analysis.

## 4. Текущее состояние Stage 8-a

Current bootstrap exists but is only a minimal skeleton:

- `@SidedProxy` points to `thaumcraft.client.ClientProxy` and `thaumcraft.common.CommonProxy`: `src/main/java/thaumcraft/common/Thaumcraft.java:84-88`.
- `@Mod(guiFactory = "thaumcraft.client.gui.GuiFactory")` points to a client GUI factory class: `src/main/java/thaumcraft/common/Thaumcraft.java:62-68`.
- In `init`, current lifecycle calls `proxy.registerDisplayInformation()`, registers `NetworkRegistry` GUI handler, then calls `proxy.registerKeyBindings()` and `proxy.registerHandlers()`: `src/main/java/thaumcraft/common/Thaumcraft.java:164-172`.
- `CommonProxy` implements `IGuiHandler`, owns GUI IDs, server GUI containers, client GUI null fallback, registration stubs and a small subset of FX stubs: `src/main/java/thaumcraft/common/CommonProxy.java:23-162`.
- `ClientProxy.registerDisplayInformation()` registers one generic inventory model location for every item and every meta `0..63`: `src/main/java/thaumcraft/client/ClientProxy.java:24-34`.
- `ClientProxy.registerKeyBindings()` now registers a client-only `KeyHandler` for the Hover Harness `H` key only; full reference `F`/`G` key dispatch and radial focus state remain open: `src/main/java/thaumcraft/client/ClientProxy.java:36-39`, `src/main/java/thaumcraft/client/lib/KeyHandler.java:1-54`.
- `ClientProxy.registerHandlers()` registers only an inner tooltip handler on `MinecraftForge.EVENT_BUS`: `src/main/java/thaumcraft/client/ClientProxy.java:41-59`.
- `ClientProxy.getClientGuiElement()` opens only focus pouch, hand mirror, and hover harness; all other declared GUI IDs return `null`: `src/main/java/thaumcraft/client/ClientProxy.java:61-88`.
- FX proxy overrides for sparkle/beam/bolt are explicit Phase 8 stubs: `src/main/java/thaumcraft/client/ClientProxy.java:90-105`.
- Packet discriminator table mostly mirrors the reference ordering and validates `REFERENCE_PACKET_COUNT = 39`: `src/main/java/thaumcraft/common/lib/network/PacketHandler.java:48-107`.
- Several current client-bound packet handlers live under `thaumcraft.common.*` and import `net.minecraft.client.Minecraft` directly, guarded only by `@SideOnly(Side.CLIENT)` on `onMessage`: for example `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncWarp.java:3-9`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncWarp.java:54-70`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketRunicCharge.java:3-8`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketRunicCharge.java:43-53`.
- No current source files exist for `src/main/java/thaumcraft/common/lib/events/KeyHandler.java`, `src/main/java/thaumcraft/client/lib/ClientTickEventsFML.java`, `src/main/java/thaumcraft/client/lib/RenderEventHandler.java`, or `src/main/java/thaumcraft/client/fx/ParticleEngine.java`; the current `thaumcraft.client.lib.KeyHandler` is a narrow Hover Harness H-key path, not the full reference key handler.
- No shader resources currently exist under `src/main/resources/**/shaders/**/*`; this is only a Stage 8-a dependency because the reference bootstrap registers client tick/render shader handlers.

Stage 8-a cannot be considered complete now: blocker/high gaps remain in full keybind dispatch, client event bootstrap, GUI routing boundary, proxy registration entry points, and client smoke verification.

## 5. Gap list

### GAP-1: `ClientProxy.registerDisplayInformation` не содержит renderer/model registration entry points

**Статус:** частично реализовано
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/client/ClientProxy.java:24-34`
- `src/main/java/thaumcraft/common/Thaumcraft.java:164-172`

**Референс:**
- `thaumcraft_src/thaumcraft/client/ClientProxy.class`

**Что не совпадает:**

Reference `registerDisplayInformation()` is a bootstrap boundary that sets Optifine/NEI aspect shift state and delegates to four explicit registration entry points: item renderers, entity renderers, block renderers, and tile renderers. Current code only registers a generic `ModelResourceLocation` for all items/metas and has no equivalent `setupItemRenderers`, `setupEntityRenderers`, `setupBlockRenderers`, `setupTileRenderers`, `registerTileEntitySpecialRenderer`, or renderer registration helper boundaries.

Stage 8-a does not require visual parity of each renderer, but it does require the registration entry points and side-safe boundaries to exist. Without these boundaries, Stage 8-b/e work has no stable client bootstrap place to attach renderers.

**Что нужно доделать:**

Add Forge 1.12.2 client-only registration entry points in `ClientProxy` for model/item/entity/TESR registration, even if individual renderer parity is completed in later Stage 8 chunks.

**Как доделать:**
- Add private methods in `src/main/java/thaumcraft/client/ClientProxy.java`: `setupItemRenderers`, `setupEntityRenderers`, `setupTileRenderers`, and, if still useful in 1.12.2, `setupBlock/ModelRenderers`.
- Call those methods from `registerDisplayInformation()` after generic model registration.
- Use Forge 1.12.2 APIs: `ModelLoader` for item/block models, `RenderingRegistry.registerEntityRenderingHandler` for entity renderers, `ClientRegistry.bindTileEntitySpecialRenderer` for TESRs.
- Keep concrete renderer implementations as dependencies for later Stage 8 chunks if absent, but leave TODO-free method boundaries with documented no-op only when no current class exists yet.
- Verify no renderer classes are referenced from common/server code.

**Критерии приемки:**
- [ ] `ClientProxy.registerDisplayInformation()` delegates to explicit item/model, entity renderer, and TESR registration methods.
- [ ] Registration methods use client-only Forge 1.12.2 APIs and are only reachable through `ClientProxy`.
- [ ] Missing concrete renderer classes are documented as later Stage 8 dependencies, not silent null/no-op gaps.
- [ ] Dedicated server load does not attempt to load renderer/model/TESR classes.

**Риски / зависимости:**

Dependency: concrete renderer/model/TESR class parity belongs to later Stage 8 chunks. Risk: adding direct references to not-yet-ported renderer classes can break compile or dedicated server side separation.

### GAP-2: Отсутствует полноценный client event bootstrap

**Статус:** частично реализовано
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/client/ClientProxy.java:41-59`
- Absent: `src/main/java/thaumcraft/client/lib/ClientTickEventsFML.java`
- Absent: `src/main/java/thaumcraft/client/lib/RenderEventHandler.java`
- Absent: `src/main/java/thaumcraft/client/fx/ParticleEngine.java`

**Референс:**
- `thaumcraft_src/thaumcraft/client/ClientProxy.class`
- `thaumcraft_src/thaumcraft/client/lib/ClientTickEventsFML.class`
- `thaumcraft_src/thaumcraft/client/lib/RenderEventHandler.class`

**Что не совпадает:**

Reference `ClientProxy.registerHandlers()` registers client tick events on the FML bus, render events on `MinecraftForge.EVENT_BUS`, `ConfigBlocks.blockTube` events, `ParticleEngine.instance` on both Forge and FML buses. Current `registerHandlers()` only registers an inner tooltip handler. There is no current equivalent for client tick, render overlay/world-last/highlight/fog handlers, shader frame update handler, particle engine event handler, or client HUD/bootstrap handlers.

The current tooltip handler is useful but is not a substitute for the reference event bootstrap. Stage 8-a requires event handler registration boundaries even before visual parity is complete.

**Что нужно доделать:**

Port or scaffold the reference client event handler boundaries in 1.12.2-compatible client packages and register them from `ClientProxy.registerHandlers()`.

**Как доделать:**
- Create `src/main/java/thaumcraft/client/lib/ClientTickEventsFML.java` with Forge 1.12.2 `TickEvent.ClientTickEvent`, `TickEvent.PlayerTickEvent`, and `TickEvent.RenderTickEvent` boundaries as needed.
- Create `src/main/java/thaumcraft/client/lib/RenderEventHandler.java` with Forge 1.12.2 render/highlight/overlay/world-last/fog event registration boundaries.
- Create or port `src/main/java/thaumcraft/client/fx/ParticleEngine.java` if event registration depends on it.
- Update `ClientProxy.registerHandlers()` to register the client tick handler, render event handler, particle engine, and existing tooltip handler on the correct Forge 1.12.2 buses.
- Keep implementation minimal where later Stage 8 visual parity is out of scope, but avoid TODO/stub comments inside Stage 8-a scope.

**Критерии приемки:**
- [ ] Client tick, render event, and particle engine event handler classes exist under client-only packages.
- [ ] `ClientProxy.registerHandlers()` registers all Stage 8-a client event boundaries on the correct Forge/FML buses.
- [ ] Client handlers are not loaded from `CommonProxy` or dedicated-server-only lifecycle paths.
- [ ] A client smoke reaches mod load/main menu without event registration crashes.

**Риски / зависимости:**

Dependency: detailed HUD, scan overlay, shader, and particle behavior belongs to later Stage 8 chunks. Risk: Forge 1.7.10 FML bus events must be carefully mapped to Forge 1.12.2 event classes; blind decompile copying will not compile.

### GAP-3: Key binding registration and dispatch отсутствуют

**Статус:** частично реализовано
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/client/ClientProxy.java:36-39`
- Absent: `src/main/java/thaumcraft/common/lib/events/KeyHandler.java`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/events/KeyHandler.class`
- `thaumcraft_src/thaumcraft/client/ClientProxy.class`

**Что не совпадает:**

Reference `registerKeyBindings()` registers a `KeyHandler` on the FML event bus. Reference `KeyHandler` creates three bindings: `Change Wand Focus` (`F`), `Activate Hover Harness` (`H`), and `Misc Wand Toggle` (`G`); tracks press state and timestamps; opens/locks wand focus radial behavior; sends `PacketFocusChangeToServer` for focus removal; sends `PacketItemKeyToServer` for golem bell reset and misc wand toggle; toggles hover harness on client key press.

Current `registerKeyBindings()` registers a client-only `thaumcraft.client.lib.KeyHandler` for the Hover Harness `H` binding, and `Hover.toggleHover` now sends `PacketFlyToServer` plus `hhon`/`hhoff` sounds for successful client toggles. The reference `F` and `G` bindings are still absent, as are the focus radial state transitions and misc wand/golem-bell packet dispatch paths. The server packet classes needed by those remaining key dispatches do exist: `src/main/java/thaumcraft/common/lib/network/misc/PacketItemKeyToServer.java:14-58` and `src/main/java/thaumcraft/common/lib/network/misc/PacketFocusChangeToServer.java:13-52`.

**Что нужно доделать:**

Port key binding registration and dispatch to Forge 1.12.2, preserving the original key meanings and packet dispatch semantics.

**Как доделать:**
- Add a client-only key handler class, preferably `src/main/java/thaumcraft/client/lib/KeyHandler.java` or another client package to avoid common package client dependencies.
- Register `KeyBinding` instances through `ClientRegistry.registerKeyBinding` from `ClientProxy.registerKeyBindings()`.
- Use Forge 1.12.2 input/tick events to detect key state and mirror reference one-shot/release behavior.
- Dispatch `PacketFocusChangeToServer` and `PacketItemKeyToServer` using `PacketHandler.INSTANCE.sendToServer`.
- Preserve public/static state needed by radial focus UI if later render handler depends on it, but keep it in a client-only package.

**Критерии приемки:**
- [ ] `F`, `G`, and `H` keybindings are visible in Minecraft controls with stable translation/category decisions.
- [ ] Pressing `F` with wand sends focus remove packet when sneaking and exposes focus radial state when not sneaking.
- [ ] Pressing `G` sends the misc item key packet once per press.
- [ ] Pressing `H` with Hover Harness toggles hover through the existing `Hover` boundary without repeated toggles while held.
- [ ] Dedicated server does not load `KeyBinding`, `ClientRegistry`, or LWJGL classes.

**Риски / зависимости:**

Dependency: radial focus UI rendering belongs to later Stage 8 chunks, but the key state boundary must exist now. Risk: current Hover Harness behavior is already listed as a pre-Phase8 deferral in `AGENTS.md:57`; key dispatch can be implemented before full hover parity.

### GAP-4: `getClientGuiElement` routes only 3 of 15 declared GUI IDs

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/CommonProxy.java:25-40`
- `src/main/java/thaumcraft/common/CommonProxy.java:61-126`
- `src/main/java/thaumcraft/client/ClientProxy.java:61-88`

**Референс:**
- `thaumcraft_src/thaumcraft/client/ClientProxy.class`
- `thaumcraft_src/thaumcraft/common/CommonProxy.class`

**Что не совпадает:**

Reference `getClientGuiElement()` handles GUI IDs `0,1,2,3,5,8,9,10,12,13,15,16,17,18,19,20`, resolving client entities through `WorldClient` for entity GUIs and client tile entities for tile GUIs. Current `ClientProxy` returns screens only for `GUI_FOCUS_POUCH`, `GUI_HAND_MIRROR`, and `GUI_HOVER_HARNESS`; all other declared GUI IDs explicitly return `null`.

Stage 8-a does not require completing every screen implementation, but it does require a correct routing boundary and explicit dependency mapping for missing screens. Current silent `null` returns make client smoke/manual GUI checks fail indistinguishably from unimplemented screens.

**Что нужно доделать:**

Complete the client GUI routing boundary for every GUI ID declared in `CommonProxy`, using reference-compatible entity/tile lookup behavior and safe null handling.

**Как доделать:**
- Update `src/main/java/thaumcraft/client/ClientProxy.java#getClientGuiElement` to route every ID declared in `CommonProxy`.
- For entity GUI IDs, lookup via `world.getEntityByID(x)` and type-check before constructing the screen.
- For tile GUI IDs, lookup via `world.getTileEntity(new BlockPos(x, y, z))` and type-check before constructing the screen.
- Add or depend on later Stage 8 GUI screen classes for missing screens: golem, pech, traveling trunk, thaumatorium, deconstruction table, alchemy furnace, research table, thaumonomicon/research browser, arcane workbench, arcane bore, magic box, spa, focal manipulator.
- If a screen is intentionally deferred, document it in the Stage 8-b GUI plan and fail gracefully with a clear log rather than silent `null`.

**Критерии приемки:**
- [ ] Every GUI ID in `CommonProxy` has an explicit client routing branch with type-safe lookup.
- [ ] Implemented screens open without client crash.
- [ ] Deferred screens have explicit documented dependencies and do not silently masquerade as missing routing.
- [ ] Entity GUI routing uses entity IDs consistently with server container routing.

**Риски / зависимости:**

Dependency: concrete GUI screen parity is Stage 8 GUI work. Risk: routing to absent GUI classes will not compile; route completion should be coordinated with minimal screen scaffolds or explicit deferral handling.

### GAP-5: Client-only packet handlers are registered, but side boundary is risky and FX handlers are mostly placeholders

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/network/PacketHandler.java:48-107`
- `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncWarp.java:3-9`
- `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncWarp.java:54-70`
- `src/main/java/thaumcraft/common/lib/network/playerdata/PacketRunicCharge.java:3-8`
- `src/main/java/thaumcraft/common/lib/network/playerdata/PacketRunicCharge.java:43-53`
- `src/main/java/thaumcraft/common/lib/network/fx/PacketFXBlockSparkle.java:1-7`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/network/PacketHandler.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/*.class`
- `thaumcraft_src/thaumcraft/common/lib/network/playerdata/*.class`

**Что не совпадает:**

The packet discriminator table matches the reference structure closely and validates the count. However, several current client-bound handlers live in common packages and directly import `net.minecraft.client.Minecraft`. The method body is annotated `@SideOnly(Side.CLIENT)`, but the class itself is still registered from common lifecycle via `PacketHandler.init()` in `src/main/java/thaumcraft/common/Thaumcraft.java:125-127`. This may be safe in Forge dev by delayed resolution, but it is a dedicated-server smoke risk until verified.

FX packet classes are registered as client packets, but at least `PacketFXBlockSparkle` is an empty subclass with no decode or client action. Other FX packet files need the same focused audit before Stage 8-a can claim that packet/FX registration boundaries are ready.

**Что нужно доделать:**

Audit all `Side.CLIENT` packet registrations for dedicated-server safety and ensure client packet handler boundaries either execute safely on the client thread or explicitly defer visual payload behavior to later Stage 8 chunks without breaking decode/dispatch.

**Как доделать:**
- Inspect every class registered with `Side.CLIENT` in `PacketHandler.init()`.
- Move direct `Minecraft.getMinecraft()` work behind a client-only handler class, proxy method, or safe sided executor pattern if dedicated server loading fails.
- Ensure each FX packet has `fromBytes`, `toBytes`, and a client `onMessage` boundary, even if the actual particle visual is a later dependency.
- Run `./scripts/dev.sh smoke-server` after changes to catch `NoClassDefFoundError`/`ClassNotFoundException` from client-only imports.
- Run `./scripts/dev.sh smoke-client` when display/X11 is available.

**Критерии приемки:**
- [ ] All `Side.CLIENT` packet classes registered in `PacketHandler` are audited for server classloading safety.
- [ ] No client-only Minecraft classes are resolved during dedicated server mod load.
- [ ] FX packets decode and dispatch to a client-side boundary without crashing, even if visual implementation is deferred.
- [ ] Server and client smoke logs contain no side-related crash markers.

**Риски / зависимости:**

Dependency: actual FX visual parity belongs to later Stage 8 FX work. Risk: fixing side boundaries can require touching many packet classes; keep changes mechanical and scoped to classloading/dispatch safety.

### GAP-6: `CommonProxy` client boundary surface is much narrower than the reference

**Статус:** частично реализовано  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/common/CommonProxy.java:122-162`

**Референс:**
- `thaumcraft_src/thaumcraft/common/CommonProxy.class`
- `thaumcraft_src/thaumcraft/client/ClientProxy.class`

**Что не совпадает:**

Reference `CommonProxy` defines a broad server-safe no-op surface for client-only world, particle, beam, bolt, HUD helper, icon, shift-key, and renderer-adjacent operations. Current `CommonProxy` exposes only a small subset: display/key/handler hooks plus `blockSparkle`, one `beam` signature, `bolt`, `burst`, `wispFX3`, `sparkle`, and `particleCount`.

Stage 8-a only needs the bootstrap boundary, but future Stage 8 packet/event code will call proxy FX methods. If the boundary remains incomplete, later client handler ports will either fail to compile or introduce direct client calls into common code.

**Что нужно доделать:**

Define the minimal Forge 1.12.2 proxy boundary needed by client packet/event/key bootstrap, based on current call sites plus reference method names where practical.

**Как доделать:**
- Audit current and planned Stage 8-a packet/event classes for `Thaumcraft.proxy.*` calls.
- Add server-safe no-op methods to `CommonProxy` only where a current Stage 8-a class needs a boundary.
- Override those methods in `ClientProxy` or client helper classes only when needed for bootstrap readiness.
- Avoid adding the entire reference FX API blindly unless a current caller requires it.

**Критерии приемки:**
- [ ] Stage 8-a client event/key/packet code does not call client-only APIs from common classes directly.
- [ ] Required proxy methods exist in `CommonProxy` as server-safe no-ops.
- [ ] `ClientProxy` overrides only the client behavior needed for Stage 8-a bootstrap.
- [ ] No broad speculative FX API expansion is introduced without current call sites.

**Риски / зависимости:**

Dependency: full FX proxy parity belongs to later Stage 8 FX work. Risk: over-expanding `CommonProxy` creates dead API surface and hides actual missing visual work.

### GAP-7: Client bootstrap lifecycle timing differs from reference and is not smoke-validated

**Статус:** требует проверки  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/Thaumcraft.java:95-147`
- `src/main/java/thaumcraft/common/Thaumcraft.java:164-172`

**Референс:**
- `thaumcraft_src/thaumcraft/common/Thaumcraft.class`

**Что не совпадает:**

Reference lifecycle calls `proxy.registerHandlers()` during `preInit` after constructing event handler instances and network setup, while `proxy.registerDisplayInformation()` and `proxy.registerKeyBindings()` are called during `init`. Current code calls both `proxy.registerKeyBindings()` and `proxy.registerHandlers()` during `init` after GUI handler registration.

Forge 1.12.2 lifecycle does not require identical timing for every event, but client tick/render/particle handlers and model registration can be timing-sensitive. Since current handler bootstrap is incomplete and no client smoke was run for this chunk, readiness cannot be confirmed statically.

**Что нужно доделать:**

Decide and validate the Forge 1.12.2 lifecycle placement for client handler, key binding, model/renderer, and GUI handler registration.

**Как доделать:**
- Keep model/renderer registration in `init` or registry/model events according to Forge 1.12.2 rules.
- Register pure event handlers early enough that first client world/tick/render events are observed, but not before dependent config/items/blocks are initialized.
- Document any intentional timing divergence from the 1.7.10 reference in code or Stage 8-a closure notes.
- Run `./scripts/dev.sh compileJava` and `./scripts/dev.sh smoke-client` when display/X11 is available.

**Критерии приемки:**
- [ ] Lifecycle placement for display/model, keybind, client event, packet, and GUI handler registration is explicitly chosen for Forge 1.12.2.
- [ ] Client smoke reaches main menu/mod load without missing model-registry timing crashes or event handler crashes.
- [ ] Server smoke remains clean after client-side bootstrap changes.
- [ ] Any reference timing divergence is documented with a Forge 1.12.2 reason.

**Риски / зависимости:**

Risk: moving registration earlier can expose uninitialized config/block/item classes; moving it later can miss model or event registration windows.

### GAP-8: Client smoke readiness has not been proven

**Статус:** требует проверки  
**Критичность:** blocker

**Текущая реализация:**
- `AGENTS.md:155-177`
- `docs/PRD.md:483-493`
- `docs/PRD.md:531-538`

**Референс:**
- Not applicable as code reference; this is a port validation requirement.

**Что не совпадает:**

Stage 8 client bootstrap affects lifecycle handlers, GUI handler registration, model/resource loading, packet side handlers, key bindings, and renderer registration. Project rules explicitly say compile success is not enough and require client smoke for client-only or mixed client/common changes. No client smoke result is currently documented for Stage 8-a.

**Что нужно доделать:**

After closing bootstrap gaps, run and document client smoke and at least one server smoke side-separation check.

**Как доделать:**
- Run `./scripts/dev.sh compileJava` after code changes.
- Run `./scripts/dev.sh smoke-server` to catch dedicated server classloading of client-only code.
- Run `./scripts/dev.sh smoke-client` if display/X11 is available.
- Inspect generated logs for crash markers listed in `AGENTS.md:169`.
- Document skipped client smoke only with a concrete environment reason, not as pass.

**Критерии приемки:**
- [ ] `compileJava` passes after Stage 8-a code changes.
- [ ] Dedicated server smoke reaches normal ready state or any failure is proven pre-existing/environmental.
- [ ] Client smoke reaches main menu/mod load phase with no new crash reports or crash markers.
- [ ] Stage 8-a closure notes include exact commands and results.

**Риски / зависимости:**

Risk: headless environment may prevent client smoke. If so, Stage 8-a should remain not fully validated until a display-capable run is performed.

### GAP-9: Client bootstrap metadata exists but is only minimally verified

**Статус:** требует проверки  
**Критичность:** low

**Текущая реализация:**
- `src/main/java/thaumcraft/common/Thaumcraft.java:62-68`
- `src/main/java/thaumcraft/client/gui/GuiFactory.java:1-28`
- `src/main/resources/mcmod.info:1-11`

**Референс:**
- `thaumcraft_src/thaumcraft/common/Thaumcraft.class`
- `thaumcraft_src/thaumcraft/client/ThaumcraftGuiFactory.class`

**Что не совпадает:**

Reference uses `guiFactory="thaumcraft.client.ThaumcraftGuiFactory"`. Current code uses `guiFactory="thaumcraft.client.gui.GuiFactory"`, and that class implements the Forge 1.12.2 `IModGuiFactory` interface with no config GUI. This is probably acceptable because config GUI parity is not Stage 8-a scope, but it still needs client smoke verification because the `@Mod` annotation references a client-only class by string during mod metadata/client bootstrap.

`mcmod.info` has no `guiFactory` field, but Forge 1.12.2 commonly uses the `@Mod` annotation path; this is not a blocker by itself.

**Что нужно доделать:**

Verify the GUI factory path loads on the client and is not loaded on dedicated server in a way that crashes.

**Как доделать:**
- Keep `GuiFactory` client-only under `thaumcraft.client.gui`.
- Do not add config GUI behavior unless a later phase requires it.
- Include GUI factory classloading in client and server smoke checks.

**Критерии приемки:**
- [ ] Client mod list/config entry does not crash while resolving `thaumcraft.client.gui.GuiFactory`.
- [ ] Dedicated server smoke does not attempt to load `GuiFactory` as a hard class dependency.
- [ ] Any future config GUI work is deferred outside Stage 8-a.

**Риски / зависимости:**

Risk is low. This is mainly a smoke-validation item, not an implementation blocker.

## 6. Итоговый checklist закрытия Stage 8-a

- [ ] `ClientProxy.registerDisplayInformation()` has explicit Forge 1.12.2 registration entry points for item/model, entity renderer, and TESR/bootstrap boundaries.
- [ ] `ClientProxy.registerHandlers()` registers client tick/render/particle/tooltip boundaries on correct Forge/FML buses.
- [ ] Client event handler classes exist in client-only packages and do not load on dedicated server.
- [ ] Key bindings `F`, `G`, and `H` are registered and dispatch equivalent client-to-server actions.
- [ ] GUI client routing covers every GUI ID declared by `CommonProxy`, with safe entity/tile lookup and explicit deferrals for missing concrete screens.
- [ ] Packet side boundary audit is complete for every `Side.CLIENT` packet in `PacketHandler`.
- [ ] FX packet classes have decode/dispatch boundaries or documented later visual implementation dependencies.
- [ ] No TODO/Phase 8 stub comments remain inside Stage 8-a scope.
- [ ] `compileJava` passes after implementation changes.
- [ ] `smoke-server` passes or any failure is documented as pre-existing/environmental.
- [ ] `smoke-client` passes where display/X11 is available, or remains explicitly unvalidated with concrete reason.
- [ ] `docs/Stage8-a.md` is updated with final commands/results after code closure.

## 7. Definition of Done

Stage 8-a считается ПОЛНОСТЬЮ завершенной только если:
- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 8-a реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 8-a;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 8-a проверены вручную или тестами;
- ./docs/Stage8-a.md обновлен и не содержит критичных открытых вопросов.

Concrete Stage 8-a manual/smoke scenarios:

- Start dedicated server and verify no client-only classloading crash.
- Start client to main menu/mod load and verify no crash reports or Forge/FML fatal errors.
- Open Controls and verify Thaumcraft key bindings exist.
- In a dev world, press `F`, `G`, and `H` in valid item contexts and verify expected packets/actions occur without repeated held-key spam.
- Trigger at least one server-opened GUI for an implemented screen and verify client routing returns a screen without crash.
- Confirm missing/deferred GUI/render/FX parity is documented outside Stage 8-a and does not block bootstrap smoke.

## 8. Открытые вопросы

- Should the port place key handling in `thaumcraft.client.lib.KeyHandler` instead of preserving the reference package `thaumcraft.common.lib.events.KeyHandler` to strengthen dedicated-server side separation? Recommended answer: use a client package unless addon/public compatibility requires the old package, because this is not public API.
- Should Stage 8-a introduce no-op client event handler classes before full HUD/render implementation, or should it wait until the first concrete Stage 8-b/e implementation? Recommended answer: introduce minimal, TODO-free boundaries now, but keep visual behavior deferred and explicitly documented.
- Is a display-capable environment available for `./scripts/dev.sh smoke-client`? If not, Stage 8-a can be implementation-complete but must not be marked fully validated.
