---
name: tex4port
description: "Use when porting TC4 (1.7.10) model-rendered block visuals to Forge 1.12.2 baked item/block models. Covers non-square TC4 textures, ModelRenderer-to-JSON translation, UV mapping, display transforms, and inventory routing."
---

# Texture Porting: TC4 ModelRenderer -> Forge 1.12.2 Baked Item Models

## When to use

Use this skill when porting a TC4 block/item that originally renders via TESR + `ModelRenderer` (Java) to a baked JSON model for inventory/hand in the 1.12.2 port. World rendering stays on TESR.

## Quick outline

```
ClientProxy → registerBuiltinItemModel(item, meta, "model_name")
  → JSON: "textures": { "surface": "thaumcraft:models/..._inventory" }
  → JSON: "elements": [ ... baked box per ModelRenderer part ]
  → JSON: "display": { ... TC6/Forge block display transforms }
  → texture: square PNG copy from TC4 model texture
```

## Step-by-step algorithm

### 1. Study the reference

- Find the class in `thaumcraft_src/**` or decompile from the corresponding jar.
- For a block, determine which `TileEntitySpecialRenderer` draws it.
- Record all `ModelRenderer` calls: `new ModelRenderer(this, offsetX, offsetY)`, `addBox`, `setRotationPoint`, `textureWidth`, `textureHeight`.
- Map Java classes:

  | TC4 Java class | Used by |
  |---|---|
  | `ModelTable` | Table |
  | `ModelArcaneWorkbench` | Arcane Workbench, Deconstruction Table, Focal Manipulator |
  | (others) | — |

### 2. Create square atlas copy of the texture

TC4 model textures are often non-square (`64x32`, `128x64`). Forge 1.12 block atlas rejects non-square sprites → magenta missing texture.

**Rule**: create a square copy with NEAREST scaling.

```python
from PIL import Image
img = Image.open('textures/models/name.png').convert('RGBA')
side = max(img.size)
img.resize((side, side), Image.Resampling.NEAREST).save('textures/models/name_inventory.png')
```

Place the file next to the original: `textures/models/name_inventory.png`.

### 3. Calculate UV for JSON faces

Formula for converting texture pixels to Minecraft UV coordinates (0–16):

```
u = pixelX * 16 / textureWidth
v = pixelY * 16 / textureHeight
```

For the square copy `textureWidth == textureHeight == side`, so
division simplifies, but v may be scaled by half if the original was half
as tall.

**ModelRenderer face → JSON face**:

| ModelRenderer offset box | JSON element `from`/`to` |
|--------------------------|--------------------------|
| `addBox(x, y, z, w, h, d)` at `setRotationPoint(px, py, pz)` | `from = [px+8, 16-py-d, pz+8]` (flip Y) |
| Dimensions: `w×h×d` | `to = [from[0]+w, from[1]+h, from[2]+d]` |

**180° X rotation** in TESR means up/down in JSON must be checked
visually. For `worktable`/`wandtable` the correct pair is:

```json
"up":   { "uv": [2, 0, 4, 4] },
"down": { "uv": [4, 0, 6, 4] }
```

### 4. Display transforms — axis guide and debug workflow

Format: `"rotation": [X, Y, Z]`

| Axis | Effect | Example change |
|------|--------|---------------|
| X | Tilt (pitch) — look down/up at item | `0 → 30` (tilt down for isometric) |
| Y | Facing (yaw) — which side faces viewer | `45 → 225` (180° flip, show front instead of back) |
| Z | Flip (roll) — upright vs upside-down | `0 → 180` (flip top-to-bottom) |

**Debug workflow:**

1. Start by fixing **Y** (facing). The item shows the wrong face (back instead of front). Add/remove 180°.
2. Fix **Z** (flip). The item faces correctly but is upside-down. Add 180° to Z.
3. Fix **X** (tilt) last if the viewing angle is off.

Always fix the **same context first** (e.g. `firstperson_righthand`), then mirror to the opposite hand by adding 180° to Y:
```
right hand Y = 45  → left hand Y = 225
right hand Y = 225 → left hand Y = 45
```

### 5. Item JSON structure

Required sections:

```json
{
  "ambientocclusion": false,
  "display": {
    "gui":   { "rotation": [30, 225, 0], "translation": [0, 0, 0], "scale": [0.625, 0.625, 0.625] },
    "fixed": { "rotation": [0, 0, 0],    "translation": [0, 0, 0], "scale": [0.5, 0.5, 0.5] },
    "ground":  { "rotation": [0, 0, 0], "translation": [0, 3, 0], "scale": [0.25, 0.25, 0.25] },
    "thirdperson_righthand":  { "rotation": [75, 45, 0], "translation": [0, 2.5, 0], "scale": [0.375, 0.375, 0.375] },
    "thirdperson_lefthand":   { "rotation": [75, 225, 0], "translation": [0, 2.5, 0], "scale": [0.375, 0.375, 0.375] },
    "firstperson_righthand":  { "rotation": [0, 45, 0], "translation": [0, 0, 0], "scale": [0.4, 0.4, 0.4] },
    "firstperson_lefthand":   { "rotation": [0, 225, 0], "translation": [0, 0, 0], "scale": [0.4, 0.4, 0.4] }
  },
  "textures": {
    "particle": "thaumcraft:blocks/name",
    "surface": "thaumcraft:models/name_inventory"
  },
  "elements": [
    { "from": [...], "to": [...], "faces": { ... } }
  ]
}
```

Copy display transforms from TC6 donor `models/item/*.json` if there are
no special requirements.

**Important:** `"particle"` texture for block models must be in the block atlas domain (`textures/blocks/`). Referencing `textures/models/` via `#variable` may fail to stitch into the block atlas, causing purple break particles. Either copy the texture to `textures/blocks/` and reference directly, or use a block texture.

### 6. Routing in ClientProxy

```java
// Normal blockstate variants
for (int meta = 0; meta <= maxMeta; meta++) {
    registerBlockItemModel(item, meta, "type=" + meta);
}
// Override specific metas
registerBuiltinItemModel(item, meta, "blockname_meta_inventory");
// Other metas that need TEISR
registerBuiltinItemModel(item, otherMeta, "blockname_tesr");
// TEISR for remaining
item.setTileEntityItemStackRenderer(new ItemXxxRenderer());
```

### 7. TEISR for split-model blocks (body in JSON, animated parts in TESR)

For blocks where the static body is baked into a JSON block model and only the animated parts (lid, door, knob) remain in the TESR:

```java
// In ClientProxy.setupTileLinkedItemRenderers():
Item item = Item.getItemFromBlock(ConfigBlocks.blockXxx);
if (item != null) {
    item.setTileEntityItemStackRenderer(new ItemXxxRenderer());
}

// In ClientProxy.setupBlockRenderers():
registerBuiltinItemModel(item, 0, "blockxxx_tesr"); // → models/item/blockxxx_tesr.json
```

The TEISR pattern for such blocks:

```java
public class ItemXxxRenderer extends TileEntityItemStackRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/models/xxx.png");
    private final ModelXxx model = new ModelXxx();

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        // Bind texture, push matrix, render full model, pop matrix
        // NO scale(1, -1, -1) — the display transforms in the JSON handle orientation
        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
        GlStateManager.pushMatrix();
        model.renderAll();
        GlStateManager.popMatrix();
    }
}
```

Key rules:
- `models/item/blockxxx_tesr.json`: `"parent": "builtin/entity"` + **full set of display transforms** (copy from `blockstonedevice_tesr` template)
- TEISR renders `model.renderAll()` (body + lid + knob for the full item)
- World rendering: JSON block model handles the static body; TESR adds animated parts
- The display transforms in the JSON are responsible for positioning and orientation — do NOT add `translate`/`scale`/`rotate` in the TEISR unless absolutely necessary
- The `builtin/entity` parent provides identity transforms; the `display` section overrides per-context

### 8. Update guard tests

For every changed JSON, add a check in an existing or new **static guard test**:

```java
String model = read("path/to/model.json");
assertTrue("description", model.contains("\"surface\": \"thaumcraft:models/..._inventory\"")
        && model.contains("\"from\": [0, 8, 0]")
        && model.contains("\"thirdperson_righthand\"")
        && model.contains("[75, 45, 0]"));
```

Check:
- texture path (for `_inventory`)
- display transforms
- at least one geometry marker
- up/down UV for top-visible blocks

## Known cases

| Block | Meta | TC4 Model | Texture | Square atlas |
|-------|------|-----------|---------|--------------|
| Table | 0 | ModelTable | `table.png` 64×32 | `table_inventory.png` 64×64 |
| Deconstruction Table | 14 | ModelArcaneWorkbench | `decontable.png` 128×64 | `decontable_inventory.png` 128×128 |
| Arcane Worktable | 15 | ModelArcaneWorkbench | `worktable.png` 128×64 | `worktable_inventory.png` 128×128 |
| Focal Manipulator | 13 | ModelArcaneWorkbench | `wandtable.png` 128×64 | `wandtable_inventory.png` 128×128 |
| Runic Matrix | 2 | TileRunicMatrixRenderer (hardcoded cluster) | `arcane_stone` block texture | not needed (block texture) |
| Hungry Chest | 0 | ModelChest (vanilla) | `chesthungry.png` 64×64 | square (no copy needed) |
| Hungry Chest (item) | 0 | — | `chesthungry.png` 64×64 | TEISR + `builtin/entity` + display transforms |

## Anti-patterns

- **Do NOT copy TC6 geometry** — it gives correct coordinates but
  wrong texture placement and silhouette.
- **Do NOT reference `thaumcraft:models/table` directly** (without `_inventory`)
  if the texture is non-square — you'll get magenta missing texture.
- **Do NOT remove TEISR entirely** — world rendering still needs the
  TileEntitySpecialRenderer.
- **Do NOT invent UV** — use exact pixel offsets from `ModelRenderer`
  atlas layout, recalculated to 0–16.
- **Do NOT use `scale(1, -1, -1)` in TEISR when display transforms are
  present** — the scale flips Y in the already-rotated display-transform
  space, causing position drift. Use Z rotation (180°) in `display`
  transforms instead to fix upside-down orientation.
- **Do NOT apply `translate`/`scale` in TEISR unnecessarily** — the
  `display` transforms in the model JSON handle positioning. Only add
  TEISR transforms when the model needs special handling that display
  transforms cannot express.
- **Do NOT reference `textures/models/` for `"particle"`** — block atlas
  may not stitch non-block-domain textures. Copy to `textures/blocks/`
  and reference directly.
