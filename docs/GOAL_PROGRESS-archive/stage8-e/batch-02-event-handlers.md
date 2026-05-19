# Stage 8-e Batch 02 — Event Handler Restoration (GAP-3 event handlers)

7 checkpoints covering server event handler paths for block-swap drops, TileSensor note-events, crafting returns, entity pickups, harvest mining, golem interactions, and champion modifier FX.

---

#### Checkpoint 2026-05-16 — Swapper drop and wand-sound paths restored

Статус: `ServerTickEventsFML` block-swap drop handling and wand sound cue now match reference behavior shape.

Что сделано:

- Restored silk-touch drop route in `tickBlockSwap(...)`:
  - uses `currentBlock.canSilkHarvest(world, pos, state, vs.player)` gate;
  - uses `BlockUtils.createStackedBlock(...)` for silk result.
- Restored non-silk drop route in `tickBlockSwap(...)`:
  - uses `currentBlock.getDrops(drops, world, pos, state, fortune)` with focus fortune.
- Restored queue-add wand sound cue in `addSwapper(...)`:
  - `world.playSound(player, x, y, z, TCSounds.WAND, SoundCategory.PLAYERS, 0.25f, 1.0f)`.
- Expanded static guard coverage in `ClientProxyFxStaticGuardTest`:
  - enforces silk-harvest/fortune-drop code-path presence;
  - enforces wand sound call-site presence.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This checkpoint restores block-swap drop/sound behavior only; deeper focus/block-chain parity remains outside this narrow scope.

---

#### Checkpoint 2026-05-16 — TileSensor note-event tracking hooks restored

Статус: server-side note-event buffering hooks for `TileSensor` are now active again.

Что сделано:

- Restored `TileSensor.noteBlockEvents` baseline buffer:
  - `WeakHashMap<WorldServer, ArrayList<Integer[]>>` field is now present in `TileSensor`.
- Restored event capture in `EventHandlerWorld.onNoteBlockPlay(...)`:
  - creates per-world list when missing;
  - stores reference-shaped payload tuple
    `(x, y, z, instrumentOrdinal, vanillaNoteId)`.
- Restored world cleanup in `EventHandlerWorld.onWorldUnload(...)`:
  - removes world entry from `TileSensor.noteBlockEvents` in a guarded `try/catch`.
- Restored per-tick list cleanup in `ServerTickEventsFML.serverWorldTick(...)`:
  - clears buffered note events for current `WorldServer`.
- Added static guard `TileSensorNoteEventStaticGuardTest` to lock this hook set.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This checkpoint restores note-event buffering hooks only; full `TileSensor` runtime parity (tone/note/redstone trigger logic) remains outside this narrow scope.

---

#### Checkpoint 2026-05-16 — EventHandlerWorld crafting-return hooks restored

Статус: special crafting return paths in `EventHandlerWorld.onItemCrafted(...)` are now active again.

Что сделано:

- Restored alumentum-style essence return hook:
  - `event.crafting == itemResource:13` with NBT gate;
  - iterates craft matrix and returns consumed `ItemEssence` containers by `grow(1)`.
- Restored arcane-bellows return hook:
  - `event.crafting == blockMetalDevice:3`;
  - returns center ingredient stack from craft slot `4` by `grow(1)`.
- Added static guard `EventHandlerWorldCraftingReturnStaticGuardTest` to lock both return paths.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This checkpoint restores crafting-return hooks only; harvest-drop special mining replacement path remains outside this narrow scope.

---

#### Checkpoint 2026-05-16 — EventHandlerEntity fake-pickup guard restored

Статус: `EventHandlerEntity.onItemPickup(...)` now restores the reference fake-player pickup cancel guard.

Что сделано:

- Restored server-side pickup cancellation for fake Thaumcraft actors:
  - `if (event.getEntityPlayer().getName().startsWith("FakeThaumcraft")) event.setCanceled(true);`
- Added static guard `EventHandlerEntityPickupStaticGuardTest` to lock this guard path.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This checkpoint restores only the fake-player pickup guard; broader research-discovery pickup parity remains out of scope.

---

#### Checkpoint 2026-05-16 — EventHandlerWorld harvest special-mining hook restored

Статус: `EventHandlerWorld.onHarvestDrops(...)` now restores the reference-shaped special-mining replacement path.

Что сделано:

- Restored tool/focus gate for special mining replacement:
  - `ItemElementalPickaxe`, `ItemPrimalCrusher`, or `ItemWandCasting` with `FocusExcavation.dowsing`.
- Restored replacement chance flow:
  - base fortune via `EnchantmentHelper` (or focus `treasure` level for wand focus);
  - `chance = 0.2 + fortune * 0.075`;
  - per-drop replacement via `Utils.findSpecialMiningResult(...)`.
- Restored utility baseline in `Utils` for this route:
  - `specialMiningResult` / `specialMiningChance` maps;
  - `addSpecialMiningResult(...)`;
  - `findSpecialMiningResult(...)`.
- Added static guard `EventHandlerWorldHarvestDropsStaticGuardTest`.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This checkpoint restores the replacement hook path only; full parity still depends on complete registration coverage of special-mining mappings in content init.

---

#### Checkpoint 2026-05-16 — EventHandlerEntity golem-owner interact guard restored

Статус: `EventHandlerEntity.onEntityInteract(...)` now restores the reference owner-check guard for golem interactions.

Что сделано:

- Restored interaction guard:
  - when target is `EntityGolemBase` with an owner;
  - and interacting player is not owner;
  - send player message `"You are not my Master!"` and cancel interaction event.
- Added static guard `EventHandlerEntityInteractStaticGuardTest`.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This checkpoint restores only the owner-interact guard path; Pech trade UX/flows remain outside this narrow scope.

---

#### Checkpoint 2026-05-16 — Champion modifier client FX hooks restored (fallback surface)

Статус: `ChampionMod*::showFX` placeholder cluster is now wired to client proxy fallback FX paths.

Что сделано:

- Restored `showFX(...)` implementations across champion modifier classes:
  - `ChampionModArmored`, `ChampionModBold`, `ChampionModFire`, `ChampionModGrim`,
    `ChampionModInfested`, `ChampionModMighty`, `ChampionModPoison`, `ChampionModSickly`,
    `ChampionModSpined`, `ChampionModUndying`, `ChampionModVampire`, `ChampionModWarded`, `ChampionModWarp`.
- Added/used proxy fallback surface for these hooks:
  - `CommonProxy`/`ClientProxy`: `drawGenericParticles(...)` and `slimeJumpFX(...)`;
  - champion hooks now route through `Thaumcraft.proxy.*` paths instead of TODO no-op bodies.
- Added static guard coverage:
  - `ChampionModFxStaticGuardTest` enforces showFX proxy routing and absence of placeholder TODO text;
  - `ClientProxyFxStaticGuardTest` now enforces `slimeJumpFX(...)` and `drawGenericParticles(...)` overrides.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This is fallback particle parity through proxy helpers; dedicated reference particle classes (`FXSpark`/`ParticleEngine`-backed champion visuals) remain outside this narrow checkpoint.
