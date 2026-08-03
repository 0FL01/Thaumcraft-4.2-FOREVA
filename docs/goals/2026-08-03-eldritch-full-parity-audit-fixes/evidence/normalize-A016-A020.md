# Normalization Evidence: A-016..A-020

Goal: `goal-20260803-eldritch-full-parity-audit-fixes`  
Date: 2026-08-03  
Source authority: `SOURCES.md` (`S-002` confirmed-in-scope approval, `S-003`/`S-004` TC4 oracle, `S-005` port evidence, `S-006` chartered adaptations, `S-007` client-only donor guidance)  
Promotion: `confirmed_in_scope` for confirmed defects inside the Eldritch charter. Conditional persisted-world compatibility is not promoted without a support decision. Test debt is evidence for required findings, not an independent product requirement unless stated below.

This file is normalization evidence only. It does not amend `GOAL.md`, `RECON.md`, `SOURCES.md`, or product files.

## Disposition Rules

- `required`: confirmed TC4-to-port behavioral or data delta inside the charter; recommend one R-group below.
- `preserve`: correct TC4 parity or a charter-approved Forge/1.12 safety adaptation; regression control only.
- `blocking_question`: outcome depends on an unfrozen compatibility policy or explicit boundary. Do not implement by assumption.
- `deferred`: useful coverage/evidence debt, not a new behavior requirement.
- Literal TC4 bugs that the port corrected are not defects merely because they differ from TC4 when the charter explicitly protects the adaptation. Conversely, an original TC4 implementation bug remains a literal parity delta when the charter's oracle is TC4 behavior; it may be fixed only by an explicit adjudication, not silently relabeled as hardening.

## R-016: Outer World And Crystal Lifecycle

### Required findings

| Normalized ID | Report locator | Exact delta and affected path/symbol | Disposition |
|---|---|---|---|
| A016-F02 | `reports/A-016-outer-world-runtime.md:80-100` | Ordinary crystal metas `0..6` change from recorded-face support validation to any side-solid neighbor; meta `7` changes from exact oriented non-air support to any side-solid support. `BlockCrystal.onBlockAdded`, `neighborChanged`, `checkAndDropBlock`, `canBlockStay`; `GenCommon.processDecorations`; `TileCrystal.orientation`. Generation writes meta `7` beside non-full `BlockEldritch` meta `4` and writes orientation after placement, so the crystal is removed. | `required` |

### Explicit boundary, not promoted

| Normalized ID | Report locator | Exact delta and affected path/symbol | Disposition |
|---|---|---|---|
| A016-F01 | `reports/A-016-outer-world-runtime.md:60-78`; charter unknowns `:177-183` | Port `WorldProviderOuter.getSaveFolder()` always selects `DIM_OUTERLANDS`; TC4/default Forge behavior selects `DIM<dimensionId>`, default `DIM-42`. Root `labyrinth.dat` still loads, so maze state can survive while chunks/entities/tiles from `DIM-42` are ignored. Paths: `WorldProviderOuter.getSaveFolder`, `Config.dimensionOuterId`, `MazeHandler.loadMaze/saveMaze`, `EventHandlerWorld`. Existing port worlds may already use `DIM_OUTERLANDS`. | `blocking_question`; do not silently promote `.thaum` or `DIM-42` migration |

**Safe disposition:** retain as a compatibility boundary requiring a separate support decision. If direct TC4 migration is supported, define an explicit two-way migration/selection policy covering `DIM-42`, existing `DIM_OUTERLANDS`, nondefault IDs, region/entity/tile data, and root `labyrinth.dat`, then add a migrated-save fixture before implementation. If direct migration is unsupported, document the fixed folder as a new-save naming delta and separately define existing-port-world safety. No folder switch alone is safe.

### Preserve controls and benign adaptations

- A016-PC01, report `:104-109`: preserve one Eldritch biome, empty terrain, fog `0xA080A0` scaled by `0.15`, average ground `50`, no sky light/respawn/weather, and provider messages.
- A016-PC02, `:111-116`: preserve empty chunks, biome fill, root `labyrinth.dat`, `Data`/`cells`/`x,z,cell` format, primary/`_old` backups; scoped streams/direct-write fallback are benign persistence hardening.
- A016-PC03, `:118-124`: preserve seed retry/publication, direction bits `1,2,4,8,16,32`, feature high byte, center/boss/key/features `1..14`, topology and invariant coverage.
- A016-PC04, `:126-133`: preserve structure geometry, metadata, loot, probabilities, and room features; avoiding repeated web writes over the center spawner is benign normalization of final output.
- A016-PC05, `:135-141`: preserve Eldritch meta/tile routing, drops, interaction gates, portal/nothing collision and damage semantics, explicit 1.12 state/tile representation.
- A016-PC06, `:143-153`: preserve altar/portal/obelisk/cap/lock/trap/crab-spawner NBT and timing, portal cooldown/entry research, and crystal orientation NBT apart from F02. Missing lock `count` defaults to `-1` and reduced lock render distance are safe hardening/performance adaptations.
- A016-PC07, `:155-160`: preserve bounded portal search, destination preparation, target dimensions, rider restrictions, cooldown, safe floor/air checks, and no verified first-entry/return defect.
- A016 adaptations `:167-175`: preserve synchronous maze publication, OCULUS ownership boundary, loaded-area cascade guard, safe teleporter/spawn restrictions, falling-state cleanup, non-null 1.12 AABBs/state synchronization, creative key retention, successful activation return, lock NBT default, and render-distance reduction. These are charter-approved platform hardening, not literal parity defects.

## R-017: Outer Entities, Bosses, Projectiles, And Drops

All rows below are `required` literal TC4 behavioral deltas unless the report explicitly marks the delta as a benign adaptation. Preserve the neighboring mechanics listed in each report's regression-hazard text.

| Normalized ID | Report locator | Exact delta; affected path/symbol |
|---|---|---|
| A017-F01 | `reports/A-017-outer-entities-drops.md:74-86` | Orb applies 160-tick Wither instead of TC4 160-tick Weakness. `common/entities/projectile/EntityEldritchOrb.java:35-59`. |
| A017-F02 | `:88-100` | Guardian sonic 10% branch applies 400-tick Blindness instead of Wither. `common/entities/monster/EntityEldritchGuardian.java:279-315`. |
| A017-F03 | `:102-114` | `getMaxSpawnedInChunk() -> 4` replaces missing `getTotalArmorValue() -> 4`; armor `4 -> 0`. `EntityEldritchGuardian.java:87-90`. |
| A017-F04 | `:116-128` | XP `20 -> 500` and talk interval approximately `80 -> 500` is reversed: move `500` to talk interval and retain XP `20`. `EntityEldritchGuardian.java:44-50,165-168`. |
| A017-F05 | `:130-142` | Warden immunity changes from Drown+Wither to Out-of-World+generic Magic. `EntityEldritchWarden.java:294-304`. |
| A017-F06 | `:144-156` | Port suppresses movement outside home; TC4 permits movement and uses return-home AI. `EntityEldritchGuardian.java:255-258`. |
| A017-F07 | `:158-170` | Server sends status `18` at spawn timer `150`, but client lacks receive branch; synchronized timer `150 -> 0`. `EntityEldritchWarden.java:171-175,320-331`. |
| A017-F08 | `:172-184` | Port deterministically cleans aggro/selects highest target and scales all resolved players; TC4 order-dependent `>25`/`10%` loop can fail retargeting and commonly miss nominal two-player scaling. `EntityThaumcraftBoss.updateAggroAndPlayerScaling:192-245`. This is a literal oracle delta despite appearing corrective; no silent “improvement” exemption. |
| A017-F09 | `:186-198` | Bosses inherit web slowdown instead of TC4 empty `setInWeb`. `EntityThaumcraftBoss` missing override. |
| A017-F10 | `:200-212` | Hard/local-difficulty permanent random spider potion is absent because no `EntitySpider.GroupData`/Hard roll occurs. `EntityEldritchCrab.onInitialSpawn:98-106`. |
| A017-F11 | `:214-226` | Port helmeted Crab speed `0.275`; TC4 normal helmeted runtime remains `0.3`, setting `0.3` only on break. `EntityEldritchCrab.setHelm:58-64`. Treat as required strict parity, not dormant-intent cleanup. |
| A017-F12 | `:228-240` | Guardian lacks TC4 `isOnSameTeam(IEldritchMob)`: Eldritch ally -> vanilla team result. `EntityEldritchGuardian`. |
| A017-F13 | `:242-254` | Crab lacks TC4 same-species team relation. `EntityEldritchCrab`. |
| A017-F14 | `:256-268` | Boss calls 1.12 `super.onInitialSpawn` (random follow-range/handedness) and floors `BlockPos`; TC4 skips super and truncates coordinates. `EntityThaumcraftBoss.java:108-113`. |
| A017-F15 | `:270-282` | Warden follow range `48 -> inherited TC4 40` before any spawn modifier. `EntityEldritchWarden.java:71-78`. |
| A017-F16 | `:284-296` | Warden ward ceiling uses base health `200*0.66=132`, not effective maximum health after modifiers. `EntityEldritchWarden.java:81-85,119-128`. |
| A017-F17 | `:298-310` | Warden talk interval inherits approximately `80` instead of TC4 `500` ticks. `EntityEldritchWarden`. |
| A017-F18 | `:312-324` | Warden/Golem champion name generation is empty; TC4 emits localized title/modifier names. `EntityThaumcraftBoss.generateName:298`; Warden/Golem subclass generation. |
| A017-F19 | `:326-338` | Ranged AI rejects targets beyond firing range `24`; TC4 only rejects below minimum and pursues until firing range. `AILongRangeAttack.java:25-45`. |
| A017-F20 | `:340-352` | Warden teleport tries random offsets before exact home and emits no 128 portal particles; TC4 exact-home-first with particle trail. `EntityEldritchWarden.teleportHome:250-292`. |
| A017-F21 | `:354-366` | Port schedules normal airy meta-11 field cleanup at `250..399` ticks; TC4 schedules only frenzy-ring cleanup. `EntityEldritchWarden.fillEldritchField/performFieldFrenzy:204-248`. |
| A017-F22 | `:368-380` | Golem Orb impact omits `Thaumcraft.proxy.burst(...,1.0f)`. `EntityGolemOrb.java:53-66`. |
| A017-F23 | `:382-394` | Crab hurt sound is Guardian hurt instead of generic hostile hurt. `EntityEldritchCrab.java:133-135`. |
| A017-F24 | `:396-408` | Crab lacks TC4 spider step sound `mob.spider.step`, volume `0.15`, pitch `1.0`. `EntityEldritchCrab` missing `playStepSound`. |

### Preserve-worthy entity adaptations and benign deltas

- A017-PC01-PC04, report `:440-463`: preserve special-item size/motion/explosion immunity/drop helper, FollowingItem homing, Orb mechanics other than F01, and practical PermanentItem persistence.
- A017-PC05-PC12, `:465-515`: preserve Guardian/Crab/boss/Warden/Golem core mechanics, exact loot/NBT, BossInfoServer, melee AI, and listed unaffected projectile/phase behavior.
- A017-F25, `:410-422`: preserve complete coordinate-target FollowingItem payload as a benign runtime/network repair. TC4's coordinate-only payload is broken; production `BlockUtils.java:154-173` supplies an entity target. Do not regress without a concrete compatibility requirement.
- A017-F26, `:424-436`: preserve practical `Integer.MAX_VALUE` lifespan restoration after NBT load. The theoretical delta is impossible expiration -> expiry after about 2.147 billion ticks/about 3.4 years continuously loaded. This is benign unless literal infinite lifetime is separately required.
- Adaptation controls `:517-527`: preserve `EntityDataManager`, `BossInfoServer`, registered 1.12 sounds/status APIs, removed `yOffset`, inline rare-drop logic, `captureDrops`, and all IDs/registries/NBT/packet/dimension boundaries.

## R-018: Aspects, Scan Data, And Derived Tags

| Normalized ID | Report locator | Exact delta and affected path/symbol | Disposition |
|---|---|---|---|
| A018-F01 | `reports/A-018-aspects-scans-data.md:38-74` | Balanced shard meta 6 is explicitly `[AIR 2,FIRE 2,WATER 2,EARTH 2,ORDER 2,ENTROPY 2,CRYSTAL 1]` instead of TC4 recipe-derived `[MAGIC 1,AIR 2,CRYSTAL 1,FIRE 1,WATER 1,EARTH 1,ORDER 1,ENTROPY 1]`; resulting Focus Primal final list is port `[CRYSTAL 27,FIRE 11,WATER 11,EARTH 11,ORDER 11,ENTROPY 11]` vs TC4 `[CRYSTAL 18,GREED 14,AIR 11,WATER 10,ORDER 10,ENTROPY 10]`, losing `MAN/HUNGER` prerequisite. Also causal Gold `[METAL 6]` vs `[METAL 3,GREED 2]`, Diamond `[CRYSTAL 8]` vs `[CRYSTAL 4,GREED 4]`, Quartz `[]` vs `[CRYSTAL 1,ENERGY 1]`. Paths `ConfigAspects.java:133-211,311-355`, recipe/tag generation. | `required` |
| A018-F02 | `:76-100` | Void cap final scan list port `[ENTROPY 9,ORDER 9,FIRE 9,AIR 9,EARTH 6,MAGIC 6]` vs TC4 `[ENTROPY 6,ORDER 6,FIRE 6,AIR 9,MAGIC 9,ELDRITCH 4]`; `ELDRITCH` and `VOID/DARKNESS` parent prerequisite are lost. Paths `ConfigAspects`, `ConfigRecipesArcaneSlice.java:305-312`, `ConfigRecipesInfusionSlice.java:45-56`. Correct causal tags with F01, not a culling exception. | `required` |
| A018-F03 | `:102-113` | `blockMetalDevice:0` is `[METAL 4,CRAFT 4,MAGIC 4]` vs TC4 cauldron-derived `[METAL 21,CRAFT 4,MAGIC 4]`; port hard-codes METAL 4 instead of seven iron *4* *0.75. `ConfigAspects.java:393`; `ThaumcraftCraftingManager.java:411-427`. | `required` |

Preserve A018-PC01-PC06, report `:117-218`: explicit object/block values and exact-over-wildcard precedence; Void Seed/ingot/equipment derivations; namespaced entity values and intentional absence of later-entity tags; player aliases and End Portal phenomenon route; complete Eldritch trigger declarations; recipe lookup/derivation/culling mechanics. Preserve adaptations at `:220-227`: namespaced keys, End Portal itemless-block route, wildcard `32767`, direct Void equipment tags, exact-over-wildcard precedence, capability scan persistence, and server-authoritative awards.

## R-019: Client Visuals

All A019 findings are `required` visual parity defects. Their exact manual evidence is mandatory even when static source evidence proves the delta.

| ID | Report locator | Exact delta and affected path/symbol |
|---|---|---|
| A019-F01 | `reports/A-019-client-visuals.md:47-58` | Shared field helper uses TESR-relative planes minus interpolated world camera instead of separate dispatcher-camera and projected-camera spaces. `LayeredFieldPlaneHelper.java:36-65,99-100,121-189` and Hole/Mirror/Nothing/Obelisk callers. |
| A019-F02 | `:60-71` | Uses `ActiveRenderInfo.getRotationX/YZ/XZ()` as parallax numerators instead of projected camera coordinates. `LayeredFieldPlaneHelper.java:191-232`. |
| A019-F03 | `:73-84` | Primal Arrow flat tint only; missing six-type `wisp.png` 16-frame fullbright aura, size `0.5`, alpha fade, depth-mask and additive/alpha blend split. `RenderPrimalArrow.java:13-38`. |
| A019-F04 | `:86-97` | Linked Mirror visibility is `<=64` squared/8 blocks instead of TC4 FOV/third-person/near-cap visibility (`<2` near, practical `<400`). `TileMirrorRenderer.java:38-46,98-119`. |
| A019-F05 | `:99-110` | Nothing layered threshold `512` squared/22.627 linear -> `144` squared/12 linear. `TileEldritchNothingRenderer.java:28-55`. |
| A019-F06 | `:112-123` | Nothing TESR dispatch `256` squared/16 -> inherited TC4 range; far fallback becomes unreachable beyond 16. `TileEldritchNothing.java:20-29`. |
| A019-F07 | `:125-136` | Nothing renderer suppresses adjacent Nothing faces in addition to opaque neighbors; TC4 suppresses only opaque adjacency. `TileEldritchNothingRenderer.java:45-76`. |
| A019-F08 | `:138-149` | Lock field LOD `1024` squared/32 -> TC4 `512`/22.627. `TileEldritchLockRenderer.java:39-60,118-181`. |
| A019-F09 | `:151-162` | Lock rings/key are gated by `<1024`; TC4 always renders them whenever TESR dispatches, independently of field LOD. `TileEldritchLockRenderer.java:42-116`. |
| A019-F10 | `:164-175` | Lock render range `2304` squared/48 -> `9216`/96. `TileEldritchLock.java:359-369`. |
| A019-F11 | `:177-188` | Existing `focus_primal_depth.png` is neither stitched nor returned; missing depth shell and uses alpha `0.95` instead of depth-present `0.6`. `FocusPrimal.java:19-92`, `ClientModelRegistry.java:44-45,73-80`, `ModelWand.java:155-201`. |
| A019-F12 | `:190-201` | Hole always passes near-detail `true`; TC4 switches at strict squared `512` to one shade-`0.5` `particlefield32.png` quad. `TileHoleRenderer.java:21-59`. |
| A019-F13 | `:203-214` | Primal Orb writes lightmap `(240,240)` without restoring prior `lastBrightnessX/Y`; TC4 makes no such mutation. `RenderPrimalOrb.java:87-118`. |
| A019-F14 | `:216-227` | Eldritch Orb has the same unbalanced fullbright write. `RenderEldritchOrb.java:87-117`. |
| A019-F15 | `:229-240` | Obelisk field LOD `9216` squared/96 -> TC4 `512`/22.627. `TileEldritchObeliskRenderer.java:38-54,101-127`. |
| A019-F16 | `:242-253` | Obelisk shell/caps are gated by field LOD; TC4 always renders shell/caps while TESR dispatches. `TileEldritchObeliskRenderer.java:54-85`. |
| A019-F17 | `:255-266` | Obelisk dispatch `20736` squared/144 -> TC4 `9216`/96. `TileEldritchObelisk.java:62-71`. |
| A019-F18 | `:268-279` | Port-only `thaumcraft:itembootsvoidrobe` adds a fourth robe piece; TC4 has three. Icon uses ordinary Void Boots, no boot color handler, and FEET armor model has no enabled geometry. `ConfigItems.java:123-126,662-684`; `itembootsvoidrobe.json`; `ClientProxy.java:760-775`; `ItemVoidRobeArmor.java:61-94,173-190`. | `blocking_question` pending persisted-data policy |

**A019-F18 safe boundary:** TC4 parity recommends removal, but registry/item removal can break persisted stacks, NBT, recipes, or existing worlds. Do not delete or silently retain it. First establish whether the port has a supported persisted-data contract for this invented registry ID. If no contract, choose a safe removal/migration procedure and fixture; if retention is required, explicitly accept a non-TC4 compatibility item and implement coherent icon, dye, equipped, dropped, and model behavior. Until then it is `blocking_question`, not a promoted R-group requirement.

Preserve A019 positive controls and negative controls at report `:298-321`: byte-identical `focus_primal_depth.png`/`wisp.png`, scoped original assets/paths, Primal Crusher, ordinary Void gear and three robe pieces/`ModelRobe`, unaffected Eldritch/device routes, wand cap/rod models, TC4 Crystalizer lightmap behavior, and Obelisk sampled-light capture/restore. These are not license to “fix” the Crystalizer's matching TC4 behavior.

## R-020: Research Schema Evidence

No product defect is promoted from A-020.

- A020-F01, report `reports/A-020-research-schema.md:58-69`, is `preserve`: source-order delta `ELDRITCHMAJOR` before `ELDRITCHMINOR` (`ConfigResearch.java:67-68` vs TC4 `initEldritchResearch`), with no established schema effect because keys/coordinates are distinct and storage is a `HashMap`. Preserve unless insertion order becomes an explicit contract.
- A020-F02, `:71-81`, is `deferred` test debt: existing guards omit the complete 16-entry metadata/page/flags/graph/warp/bounds matrix. It becomes minimum evidence if schema code is changed, but is not an independent behavior requirement under the current audit.
- Preserve A020-PC01-PC05, `:287-314`: TC4-visible `ResearchItem`, `ResearchPage`, category collision/duplicate/replacement/bounds semantics, complete 16-entry declaration matrix, and five research plus two item warp associations.
- Preserve adaptations `:316-326`: TC6 `ResearchEntry` projection and setter shim, category compatibility overloads, 1.12 localization/mappings, typed recipe handles, renamed fields, split initializer, and unused smelting sentinel difference outside Eldritch-used pages. These are platform/API adaptations, not parity defects.

## Manual Smoke Requirements

Manual client evidence is required for A019 implementation closure; no claim of visual parity may rely on compile success or static guards alone. Execute the report matrix exactly enough to cover:

- M-019-01 (`A-019-F01/F02`): origin/sign translation invariance, all supported faces, camera movement and yaw/pitch.
- M-019-02 (`F03`): arrow types `0..5`, flight/impact/embedded/fade, aura frame/blend/depth behavior.
- M-019-03 (`F04`): linked mirrors at 4/8/16/32, first/third person and FOV.
- M-019-04 (`F05-F07`): Nothing at 8/12/16/22/about 22.627/24, connected volumes and distant signed coordinates.
- M-019-05 (`F08-F10`): Lock at 22/24/32/40/48/64/96, inactive/active/key states.
- M-019-06 (`F11`): Primal Focus mounted on wand/sceptre/staves and compared with Pech depth behavior.
- M-019-07 (`F12`): Hole at 16/22/about 22.627/24/32/64 with floor/ceiling/wall and merged openings.
- M-019-08 (`F13/F14`): Orb render ordering beside dim objects; no following fullbright leak.
- M-019-09 (`F15-F17`): Obelisk normal/Outer at 22/24/96/112/144; shell/caps/textures across boundaries.
- M-019-10 (`F18`): if retained during adjudication, creative/dyed/equipped/dropped/inventory/hand behavior; expected TC4 result remains no fourth robe item.

Server/common changes promoted under R-016/R-017/R-018 require `./scripts/dev.sh validate --smoke`; R-019 is client-only and requires focused tests plus `./scripts/dev.sh build`, not automated client smoke. The report-level audit commands and results remain authoritative at each report's `Commands and Results` section.

## Test Debt Index

- A016: migrated-save fixture for both folder names/nondefault IDs; deterministic crystal support/order tests; room snapshots/meta invariants; portal round trip; maze primary/backup persistence round trip. Report `A-016:185-192`.
- A017: potion/effect, armor/XP/talk, damage-source, home recovery, status-18 client, aggro/scaling, web immunity, spawn/attribute, team, champion naming, ranged range, teleport/field lifetime, burst/audio, spawn-data, and lifespan/NBT tests. Report `A-017:537-560`.
- A018: runtime resolved-list fixture for balanced shard, Salis Mundus, Primal Charm, Focus, Void cap, Metal Device; assert direct-parent sets and preserve positive controls. Report `A-018:229-244`.
- A019: guards currently encode wrong coordinate substitution and omit exact thresholds, state balancing, persistent geometry, depth stitch, and boots policy; focused guards plus manual matrix remain necessary. Report `A-019:323-332`.
- A020: semantic 16-entry schema test and isolated category duplicate/collision/virtual-bound tests. Report `A-020:328-337`.

## Completeness Reconciliation

- Assignments covered: A-016, A-017, A-018, A-019, A-020; all five report packets are terminal `complete` at their report handoffs.
- Defects inventoried: A-016 one required plus one conditional; A-017 24 required; A-018 3 required; A-019 17 required plus one conditional; total 45 required literal parity defects and 2 explicit blocking compatibility questions. The A-017 report's “24 defects” is reconciled with F01-F24; A-017-F25/F26 are benign deltas, not defects.
- Benign deltas inventoried: A-016 platform/persistence hardening and minor interaction deltas; A-017-F25/F26; A-020-F01; A-020's listed API/platform adaptations. All are explicitly preserve/deferred rather than promoted.
- Parity controls inventoried: A-016-PC01-PC08; A-017-PC01-PC12; A-018-PC01-PC06; A-020-PC01-PC05; A-019 asset, renderer, and negative controls. Each is linked to a report locator above and must remain a preserve control in central normalization.
- Manual requirements inventoried: M-019-01 through M-019-10, with the persisted-world/crystal/portal and entity validation gaps separately recorded in the test-debt index.
- Unknowns/constraints inventoried: direct TC4 `.thaum`/`DIM-42` migration policy, existing `DIM_OUTERLANDS` safety, invented Void Robe boots persisted-data policy, charter anti-scope/API/ID/NBT/packet/dimension boundaries, and no visual parity claims without manual evidence.
- R-group recommendation: R-016 covers A016-F02; R-017 covers A017-F01..F24; R-018 covers A018-F01..F03; R-019 covers A019-F01..F17. A016-F01 and A019-F18 remain blocking questions. A017-F25/F26 and A020-F01 are preserve controls; A020-F02 and listed test debt are deferred/minimum evidence as marked.
- No report claim is silently omitted: every report atomic finding, positive control family, adaptation family, manual smoke group, unknown, and test-debt family has a disposition or explicit boundary here. Central ledger promotion and R-* creation remain outside this file by instruction.
