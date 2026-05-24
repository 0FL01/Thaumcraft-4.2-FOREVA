---
name: fixitemview
description: "Use when a TC4 port block renders correctly in the world but shows a wrong or full-size 3D model as an item in hand/inventory/drop. Covers item/generated routing, registerBuiltinItemModel, and per-meta item model JSON creation."
---

# Fix Block Item View: world model correct, item model wrong

## When to use

Use this skill when:
- A block renders correctly in the world (via blockstate → block model)
- The block's item in hand/inventory/drop shows:
  - a placeholder (magenta/black checkerboard)
  - the wrong texture (e.g., a candle model instead of a mushroom)
  - a full-size 3D block model instead of a miniature sprite
- You need to create a flat 2D sprite (`item/generated`) for the item

## Quick outline

```
Diagnosis → blockstate/block model ✅, item model ❌
  → create models/item/<block>_item_<meta>.json (parent: item/generated, layer0: blocks/<texture>)
  → replace registerBlockItemModel with registerBuiltinItemModel in ClientProxy.setupBlockRenderers()
  → fix fallback models/item/<block>.json
```

## Diagnosis

| Symptom | Cause | Fix |
|---------|-------|-----|
| Magenta/black checkerboard | Item model references nonexistent texture or parent | See step 1 |
| Wrong texture (candle etc.) | `models/item/<block>.json` has wrong parent | See step 1 |
| Full-size 3D model in hand | Uses `registerBlockItemModel`, which resolves the block model (e.g. `block/cross`) | See steps 2-4 |

## Step-by-step algorithm

### 1. Check current state

Identify which files are involved:

```
# Item model (may have wrong parent)
cat src/main/resources/assets/thaumcraft/models/item/<registry>.json

# Registration in ClientProxy
grep -n '<blockField>' src/main/java/thaumcraft/client/ClientProxy.java
```

Typical errors in `models/item/<block>.json`:

```json
// ❌ Wrong — parent points to an unrelated model
{ "parent": "thaumcraft:block/blockcandle" }

// ❌ Wrong — parent points to a block model, item looks like a block
{ "parent": "thaumcraft:block/blockcustomplant_5" }
```

### 2. Create item model JSON for each subtype

```
src/main/resources/assets/thaumcraft/models/item/<block>_item_<meta>.json
```

Format (single line, no extra whitespace):

```json
{"parent":"item/generated","textures":{"layer0":"thaumcraft:blocks/<texture_name>"}}
```

- `layer0` points to the block texture from `textures/blocks/`
- If the texture lives in `textures/items/`, use `thaumcraft:items/<name>`
- `item/generated` — vanilla parent for flat 2D sprites (flowers, saplings, plates)

For a block with N subtypes, N files are needed:

```
blockcustomplant_item_0.json → layer0: thaumcraft:blocks/greatwoodsapling
blockcustomplant_item_1.json → layer0: thaumcraft:blocks/silverwoodsapling
blockcustomplant_item_2.json → layer0: thaumcraft:blocks/shimmerleaf
...
```

### 3. Switch registration in ClientProxy

In `thaumcraft/client/ClientProxy.java`, method `setupBlockRenderers()`:

```java
// ❌ Before — uses blockstate variant, renders block model
registerBlockItemModel(plantItem, meta, "type=" + meta);

// ✅ After — uses a separate item model JSON
String[] itemModels = {
    "blockcustomplant_item_0", "blockcustomplant_item_1", ...,
    "blockcustomplant_item_N"
};
registerBuiltinItemModel(plantItem, meta, itemModels[meta]);
```

Method comparison:

| Method | Model | Item appearance |
|--------|-------|-----------------|
| `registerBlockItemModel(item, meta, variant)` | `blockstates/<block>.json#variant` → block model | 3D block |
| `registerBuiltinItemModel(item, meta, path)` | `models/item/<path>.json` | Flat 2D sprite |

`registerBuiltinItemModel` internally does:
```java
ModelLoader.setCustomModelResourceLocation(item, meta,
    new ModelResourceLocation(new ResourceLocation("thaumcraft", path), "inventory"));
```

### 4. Fix fallback item model

The file `models/item/<block>.json` is used for metas without explicit registration (e.g., meta >= 6 for a 6-subtype block). Ensure it doesn't reference a nonexistent texture:

```json
{
  "parent": "thaumcraft:block/blockcustomplant_5"
}
```

or also `item/generated`:

```json
{
  "parent": "item/generated",
  "textures": {
    "layer0": "thaumcraft:blocks/manashroom"
  }
}
```

### 5. Verify block models

Block models `models/block/<block>_<meta>.json` stay unchanged — they handle world rendering:

```json
{
  "parent": "block/cross",
  "textures": {
    "cross": "thaumcraft:blocks/manashroom"
  }
}
```

### 6. JSON file structure summary

```
models/block/
├── blockcustomplant_0.json   ← block/cross → greatwoodsapling (world)
├── blockcustomplant_5.json   ← block/cross → manashroom (world)

models/item/
├── blockcustomplant.json          ← fallback (e.g. blockcustomplant_5)
├── blockcustomplant_item_0.json   ← item/generated → greatwoodsapling (item)
├── blockcustomplant_item_5.json   ← item/generated → manashroom (item)

blockstates/
└── blockcustomplant.json    ← type=N → blockcustomplant_N (world only)

ClientProxy.java (setupBlockRenderers):
    registerBuiltinItemModel(customPlantItem, 0, "blockcustomplant_item_0");
    registerBuiltinItemModel(customPlantItem, 5, "blockcustomplant_item_5");
```

## Example: BlockCustomPlant (Vishroom fix)

Before:
- `models/item/blockcustomplant.json`: `"parent": "thaumcraft:block/blockcandle"` → magenta texture
- `ClientProxy`: `registerBlockItemModel(plantItem, meta, "type=" + meta)` → 3D cross in inventory

After:
- 6 new files `models/item/blockcustomplant_item_0..5.json`: `item/generated` with `layer0: blocks/<texture>`
- `ClientProxy`: `registerBuiltinItemModel(plantItem, meta, plantItemModels[meta])` → flat 2D sprite
- `models/item/blockcustomplant.json`: `"parent": "thaumcraft:block/blockcustomplant_5"` (fallback)

## Validation

- `./scripts/dev.sh compileJava` — compilation (new JSON files don't need compilation, but ClientProxy changes do)
- `./scripts/dev.sh validate --smoke` — runtime smoke
- **Client visual check**: verify that inventory/hand/drop show a flat sprite, not a 3D block

## Known cases

| Block | Meta | Block model | Texture | Item model |
|-------|------|-------------|---------|------------|
| BlockCustomPlant | 0-5 | `block/cross` | `blocks/*.png` | `blockcustomplant_item_N.json` → `item/generated` |

## Related files

- `src/main/java/thaumcraft/client/ClientProxy.java` — `setupBlockRenderers()` (registration), `setupItemRenderers()` (do NOT touch for blocks)
- `src/main/java/thaumcraft/client/ClientProxy.java` — `registerBlockItemModel()` and `registerBuiltinItemModel()` (around line 879-885)
- `src/main/resources/assets/thaumcraft/models/item/*.json` — item models
- `src/main/resources/assets/thaumcraft/models/block/*.json` — block models (do NOT touch)
- `src/main/resources/assets/thaumcraft/blockstates/*.json` — blockstate variants

## Anti-patterns

- **Do NOT use `registerBlockItemModel` for plant-type blocks** — it renders a 3D block, not a flat sprite
- **Do NOT edit `setupItemRenderers()` for blocks** — it handles items; blocks register in `setupBlockRenderers()`
- **Do NOT remove `registerBlockItemModel` for blocks whose item and block look identical** (full blocks like ore) — blockstate variants are correct for them
- **Do NOT copy textures** — `item/generated` references the existing block texture via `layer0`
