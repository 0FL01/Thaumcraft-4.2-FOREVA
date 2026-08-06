---
name: tex4port
description: "Use when converting a TC4 ModelRenderer cuboid or inventory renderer into a Forge 1.12.2 baked item shell, or diagnosing its atlas UVs and display transforms. Covers TC4-first geometry evidence, non-square static textures, baked versus TEISR routing, and multi-context visual checks."
---

# TC4 cuboid model to a 1.12 baked item shell

## Scope

Use this skill when the desired item appearance comes from TC4
`ModelRenderer.addBox(...)`, `renderInventoryBlock`, or equivalent cuboid
geometry and needs a baked 1.12 item model.

Hand off instead when:

- the desired item is a simple flat sprite: `fixitemview`;
- the defect is selected OBJ groups/topology/raw OBJ UVs: `tesr-obj-parity`;
- the model is already correct and only needs screenshot proof:
  `tc4-client-vision-probe`.

World rendering does not always remain a full TESR. Current routes include:

- full TESR world rendering;
- baked world shell plus TESR animated parts;
- fully baked items while the world still has a TESR.

Treat world TESR, item TEISR, and model-location registration as separate
decisions.

## Evidence order

1. Inspect original TC4 model and the exact world/inventory renderer call.
2. Record cuboids, texture offsets/dimensions, rotation points, child models,
   mirror/inflation, and renderer transforms.
3. Inspect current block/item route and any TEISR assignment.
4. Use TC6 only as secondary evidence for a proven 1.12 API adaptation. Never
   substitute TC6 geometry or transforms merely because it is available.

## Geometry conversion

There is no universal one-line `ModelRenderer` to JSON formula. The mapping
depends on the renderer anchor and axis transforms.

For an audited unrotated part whose X/Z axes align with JSON and whose
ModelRenderer downward-positive Y must be converted to JSON upward-positive Y,
a limited mapping is:

```text
from.x = anchorX + rotationPointX + boxX
from.y = anchorY - (rotationPointY + boxY + boxHeight)
from.z = anchorZ + rotationPointZ + boxZ
to     = from + [boxWidth, boxHeight, boxDepth]
```

The anchor is renderer-specific; examples in this repository use different
centres such as `(8,16,8)` and `(8,8,8)`. Do not reuse the formula unchanged
for rotated parts, child models, mirrored boxes, inflation, or a renderer that
swaps/flips axes. Transform all eight corners or reproduce the original
transform chain instead.

## Texture and UV conversion

Start from original TC4 pixel coordinates:

```text
u = pixelX * 16 / sourceWidth
v = pixelY * 16 / sourceHeight
```

Forge's static atlas path rejects the non-square textures used by several TC4
models unless they have valid animation metadata. For an audited static
`W x H` source, one current adaptation is a nearest-neighbour square derivative.

If a `128x64` source is stretched to `128x128`, transform source Y coordinates
by the same factor before dividing by the new height:

```text
atlasY = sourceY * 2
v = atlasY * 16 / 128
```

Equivalently, keep source coordinates and divide by the original source
height. Do not stretch the PNG and then divide unchanged source Y values by
the square height; that halves the sampled region.

Use a reproducible repository path and document the derivative:

```python
from pathlib import Path
from PIL import Image

src = Path("src/main/resources/assets/thaumcraft/textures/models/name.png")
dst = src.with_name("name_inventory.png")
image = Image.open(src).convert("RGBA")
side = max(image.size)
image.resize((side, side), Image.Resampling.NEAREST).save(dst)
```

Pillow is a host-side aid, not guaranteed by the project image. Validate the
output dimensions and pixel relationship in a focused test.

## Choose baked versus TEISR item rendering

### Baked item shell

Use a dedicated item JSON with elements or a verified parent. Explicit
`display` is optional when inherited from a parent such as `block/block`.

```json
{
  "parent": "block/block",
  "textures": {
    "particle": "thaumcraft:blocks/arcane_stone",
    "surface": "thaumcraft:models/name_inventory"
  },
  "elements": []
}
```

The particle reference must resolve to a stitchable, valid sprite. Directory
name alone does not decide atlas eligibility; existence, dimensions/animation,
and the consuming model path do.

### TEISR item

Use `"parent": "builtin/entity"` and assign a TEISR to the `Item`. JSON camera
transforms and TEISR-local transforms compose; neither layer universally
replaces the other.

Reproduce the original model origin deliberately. Current item renderers such
as table, wooden-device, and crystal renderers contain legitimate local
translations/rotations/scales. A no-transform TEISR is one possible audited
case, not a rule.

Remember that TEISR assignment is per `Item`, while model routing is per
metadata. Audit every metadata sharing that item before changing assignment.

## Display transforms

Treat each context independently:

- GUI;
- fixed/item frame;
- ground/drop;
- first-person left/right;
- third-person left/right.

Axis labels are only a debugging aid. Do not assume a fixed Y-then-Z-then-X
workflow or derive the opposite hand by blindly adding 180 degrees. Parent
transforms, model basis, and TEISR-local transforms can change the result.

## Current route examples

| Case | Active route | Notes |
|---|---|---|
| Table meta 0 | `blocktable_0_inventory` | baked shell; square derivative |
| Deconstruction Table meta 14 | `blocktable_tesr` | active TEISR; old inventory JSON/PNG are inactive artifacts |
| Arcane Worktable meta 15 | `blocktable_tesr` | active TEISR |
| Focal Manipulator meta 13 | `blockstonedevice_13_inventory` | baked shell; square derivative |
| Runic Matrix meta 2 | `blockstonedevice_2_inventory` | baked model with documented donor-equivalent geometry |
| Hungry Chest meta 0 | `blockchesthungry` | complete baked item; no active chest TEISR |
| Centrifuge meta 2 | `blocktube_2_inventory` | baked item plus split baked/TESR world rendering |

Do not infer active use from a tracked historical model or renderer. Trace the
current `ClientProxy` route and TEISR assignment.

## Verification

Prefer parsed JSON/image assertions over string markers:

- selected model route for every affected metadata;
- parent, textures, elements, UV bounds, and referenced assets;
- square derivative dimensions/pixel mapping when used;
- TEISR assignment and exact local transform chain when used;
- unaffected metadata remain routed as before.

Then run focused tests and the final client build:

```text
./scripts/dev.sh gradle test --tests <focused-test>
./scripts/dev.sh build
```

Inspect all seven item contexts. Build and server smoke do not prove visual
parity; use `tc4-client-vision-probe` or an in-game TC4/port comparison.

## Anti-patterns

- Do not use TC6 as the first geometry source.
- Do not apply an anchor-free cuboid formula.
- Do not confuse world TESR removal with item TEISR removal.
- Do not prohibit or add TEISR transforms without comparing both transform
  chains.
- Do not treat every non-square texture identically; check animation metadata
  and the consuming atlas path.
- Do not call a tracked asset active without tracing its current route.
