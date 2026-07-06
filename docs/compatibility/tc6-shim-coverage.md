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
| Astral Sorcery | No local evidence yet; keep as pending RECON before adding dependencies or shims | none | none |

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
