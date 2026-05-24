# Rendering note: baked inventory models for TESR-backed blocks

## Problem

Some TC4 blocks are rendered in-world by TESR/`ModelRenderer` and have empty or
placeholder block models in the 1.12.2 port. If their item/inventory path also
uses TEISR, GUI/hand coordinates can drift because the legacy renderer transform
stack does not match Forge 1.12 baked item display transforms.

Observed fixed cases:

- `blocktable` meta `0` — Table.
- `blocktable` meta `14` — Deconstruction Table.
- `blocktable` meta `15` — Arcane Worktable.
- `blockstonedevice` meta `2` — Runic Matrix.
- `blockstonedevice` meta `13` — Focal Manipulator.

## Working solution

Keep the world renderer unchanged, but route the item model to a dedicated baked
JSON model:

1. Leave the blockstate/world path on the existing empty/particle block model so
   the TESR still owns in-world rendering.
2. Register the problematic item meta to a dedicated inventory model in
   `ClientProxy.registerItemModels()`.
3. Put TC6/Forge 1.12 block-style `display` transforms in that item JSON.
4. Rebuild the visible item shell as baked JSON elements using TC4 model
   geometry, not TC6 geometry.
5. Keep any remaining TEISR route only for metas that still need live tile
   animation/state in item form.

This separates the two concerns:

- world parity stays with the original TESR;
- inventory/hand positioning uses the stable Forge baked item transform path.

## Texture rule

Do not point baked JSON directly at TC4 `textures/models/*.png` atlases when they
are non-square (`64x32`, `128x64`). Forge's block atlas can reject or miss them,
which appears as magenta missing textures.

For baked inventory shells, add a square inventory atlas copy derived from the
original TC4 model texture:

- `textures/models/table.png` -> `textures/models/table_inventory.png`
- `textures/models/decontable.png` -> `textures/models/decontable_inventory.png`
- `textures/models/worktable.png` -> `textures/models/worktable_inventory.png`
- `textures/models/wandtable.png` -> `textures/models/wandtable_inventory.png`

Use nearest-neighbor scaling to preserve pixel art, then target those square
copies from JSON, for example:

```json
"textures": {
  "particle": "thaumcraft:models/worktable_inventory",
  "surface": "thaumcraft:models/worktable_inventory"
}
```

## Geometry rule

Do not copy TC6 model geometry for TC4 visuals. It fixes coordinates but gives
the wrong silhouette and texture placement.

Instead, translate the TC4 `ModelRenderer` boxes into baked JSON elements:

- `ModelTable` is a top slab, two legs, and one crossbar.
- `ModelArcaneWorkbench` is a top block, base block, and four short legs.
- Preserve TC4 atlas UVs explicitly per face.
- If a TESR used `rotate(180, X)`, verify visible baked `up/down` faces against
  the in-game screenshot. For `worktable`/`wandtable`, the corrected visible top
  uses `up: [2, 0, 4, 4]` and `down: [4, 0, 6, 4]` on the square atlas.

## Checklist for the next problematic block

1. Inspect original TC4 class under `thaumcraft_src/**` or decompile the matching
   class with CFR.
2. Record `ModelRenderer(textureOffsetX, textureOffsetY)`, `addBox`, and
   `setRotationPoint` for each part.
3. Add a dedicated item JSON with donor/Forge block display transforms.
4. Convert only the item shell to baked JSON elements.
5. Use original TC4 texture art; create a square `_inventory.png` copy only if
   the source model texture is non-square.
6. Register only the affected item meta to the new inventory model.
7. Keep world block models empty/particle-only when TESR owns world visuals.
8. Add/update static guard tests for routing, texture path, geometry markers, and
   critical UVs.
9. Validate with `./scripts/dev.sh validate --smoke` for runtime-affecting model
   or registration changes.

## Known pitfall

Compile success is not enough. Item model JSON issues often only show up in the
client as shifted models, wrong TC6-like texture placement, or magenta missing
textures. Always require a client visual check before calling the checkpoint
complete.
