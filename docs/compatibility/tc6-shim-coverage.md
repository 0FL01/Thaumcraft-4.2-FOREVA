# Thaumcraft 6 shim coverage

This project keeps its TC4-era internals, but advertises enough TC6 binary API
surface for 1.12 addons that detect `thaumcraft` and link against TC6 classes.
Do not remove these classes as addon-specific hotfixes: they are compatibility
contract shims guarded by unit tests and smoke modsets.

## Current coverage

| Addon / scenario | Covered TC6 surface | Regression guard | Smoke modset |
| --- | --- | --- | --- |
| EnderIO | Forge-visible `6.1.BETA26` version, `thaumcraft.api.items` aliases, `IInfusionStabiliser` `BlockPos` overload | `EnderIoThaumcraftSixApiShimStaticGuardTest`, `ThaumcraftAdvertisedVersionStaticGuardTest` | `scripts/smoke-modsets/enderio.txt` |
| Fossils and Archeology | `AspectRegistryEvent`, `AspectEventProxy`, TC6 aspect constants as TC4 aliases with legacy tag lookup, `ThaumcraftApi.registerSeed(Block, ItemStack)`, crop block registration | `FossilsThaumcraftSixApiShimStaticGuardTest`, `AspectTc42CompatibilityTest` | `scripts/smoke-modsets/fossils.txt` |
| Magic Bees + Forestry | `BlocksTC`, `ItemsTC`, aura facades, `SoundsTC`, research-location registration, taint/cult/tainted package aliases | `MagicBeesThaumcraftSixApiShimStaticGuardTest` | `scripts/smoke-modsets/magicbees.txt` |
| JEID + MixinBooter | TC6 `Utils.setBiomeAt(World, BlockPos, Biome, boolean)` coremod mixin target, `setBiomeAt(World, BlockPos, Biome)`, `resetBiomeAt` overloads, and legacy TC4 x/z delegation through the mixin-owned path | `UtilsThaumcraftSixBiomeCompatStaticGuardTest` | `scripts/smoke-modsets/jeid.txt` |
| Witchery Resurrected + Companion | `CommonInternals.scanEntities` canonical identity alias and untyped vis drain projected onto TC4 primal visnet | `WitcheryThaumcraftSixApiShimTest`, corpus resolver | `scripts/smoke-modsets/witchery.txt` |
| Aqua Acrobatics | TC6-style `TileCrucibleRenderer.renderFluid` client mixin target with the redirectable water texture lookup | `CrucibleRendererFidelityStaticGuardTest` | `scripts/smoke-modsets/aquaacrobatics.txt` |
| Balkon's Expansion | TC6 item interfaces, canonical tool-material aliases, projected ingot and void-seed fields | `Issues15To19ThaumcraftSixCompatibilityStaticGuardTest`, corpus resolver | `scripts/smoke-modsets/balkonsexpansion.txt` |
| Solar Flux | TC6 category/entry/stage projection, scanning/theorycraft linkage, recipe overloads, and canonical aspect/research insertion | `Issues15To19ThaumcraftSixCompatibilityStaticGuardTest`, corpus resolver | `scripts/smoke-modsets/solarflux.txt` |
| Botania CEu | canonical `warpward` registry identity with legacy remap, TC6 item interfaces, and object-aspect lookup | `Issues15To19ThaumcraftSixCompatibilityStaticGuardTest`, corpus resolver | `scripts/smoke-modsets/botania.txt` |
| Just Enough Magiculture | projected loot/item fields and unregistered TC4-backed entity class-token aliases | `Issues15To19ThaumcraftSixCompatibilityStaticGuardTest`, corpus resolver | `scripts/smoke-modsets/justenoughmagiculture.txt` |
| JAOPCA + WrapUp | dual TC4/TC6 research lookup return descriptors and projected recipe registration | `Issues15To19ThaumcraftSixCompatibilityStaticGuardTest`, corpus resolver | `scripts/smoke-modsets/jaopca.txt` |
| Astral Sorcery | No local evidence yet; keep as pending RECON before adding dependencies or shims | none | none |

Exact per-symbol support levels and known unsupported operations are recorded in
`docs/compatibility/abi/tc6-target.txt`; see
`docs/compatibility/tc6-validation.md` for their meaning. In particular, the
Magic Bees aura classes link, but TC6 chunk aura/flux world state and JSON
research ingestion are explicitly unsupported rather than backed by fake state.
The complete donor API gap set remains visible in
`docs/compatibility/abi/tc6-current-gaps.txt`; supported corpus rows define the
reviewed linkage floor rather than implying donor-wide TC6 parity.

## Maintenance rules

1. Preserve exact package/class/field/method descriptors that addons link
   against, even when the implementation delegates to TC4 objects.
2. Add or update a static guard test for every new shim surface.
3. Add a smoke modset only after dependency RECON records exact jars and
   checksums.
4. Treat addon names in tests/smoke manifests as regression evidence; production
   shim comments should describe the generic TC6 contract.
5. Treat required third-party mixin targets as compatibility surface too: a
   missing target descriptor can crash before regular addon code runs.
6. Every pinned demand symbol must have an `EXACT`, `PROJECTED`, `LINK_ONLY`, or
   `UNSUPPORTED` entry in the accepted target snapshot.
