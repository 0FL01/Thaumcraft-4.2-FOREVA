---
name: fixitemview
description: "Use when a TC4 block is correct in the world but its block-backed item resolves to the wrong baked model and the intended inventory/hand/drop appearance is a simple generated or layered sprite. Covers model-route diagnosis, dedicated item JSONs, metadata registration, and visual verification."
---

# Fix a block-backed item model route

## Use this skill when

- the world block already renders correctly;
- the affected stack is the `ItemBlock` form of that block;
- TC4/reference evidence says the item should be a flat or layered sprite; and
- inventory, hand, frame, or dropped-item rendering selects the wrong model.

Do not use it merely because an item is 3D. Use:

- `tex4port` for a cuboid/`ModelRenderer` item shell, UV conversion, or display transforms;
- `tesr-obj-parity` for selected OBJ groups, topology, or raw OBJ UVs;
- `tc4-client-vision-probe` for screenshot proof.

## Important routing semantics

The helper name `registerBuiltinItemModel` is historical and misleading. In
this repository it only selects a dedicated model location:

```java
ModelLoader.setCustomModelResourceLocation(item, meta,
        new ModelResourceLocation(
                new ResourceLocation("thaumcraft", modelPath), "inventory"));
```

The selected JSON determines the appearance:

| JSON route | Result |
|---|---|
| `"parent": "item/generated"` | flat sprite |
| `"parent": "block/block"` plus elements/inheritance | baked 3D item |
| `"parent": "builtin/entity"` plus an assigned TEISR | dynamic item renderer |

`registerBlockItemModel` selects the block registry name plus a blockstate
variant. That is correct when the block and item should share a baked model.
It is not inherently a full-size or incorrect route.

For the non-damageable subtype `ItemBlock`s covered here, Forge resolves exact
item/metadata registrations. An unregistered metadata does **not** fall back to
metadata zero or automatically use `models/item/<registry>.json`; it normally
resolves to the missing model unless a mesh definition or another explicit
route exists. Damageable items are a separate case: vanilla model lookup
normalizes their damage value to metadata zero.

## Workflow

### 1. Prove the expected item appearance

Inspect, in order:

1. original TC4 inventory renderer/item icon;
2. current `ClientProxy.setupBlockRenderers()` route for the exact metadata;
3. the selected `models/item/*.json`;
4. inherited parent and referenced textures;
5. whether a TEISR is assigned in `setupTileLinkedItemRenderers()`.

Do not convert a block item to `item/generated` solely from its block class or
render layer.

### 2. Choose the route

Keep the blockstate route when the item should share the world baked model:

```java
registerBlockItemModel(item, meta, "type=" + meta);
```

Choose a dedicated item model when the item has a distinct sprite:

```java
registerBuiltinItemModel(item, meta, "blockcustomplant_item_" + meta);
```

Current repository organization:

- `setupItemRenderers()` — ordinary `ConfigItems` model locations during the
  model-registry event;
- `setupBlockRenderers()` — block-backed item model locations during the
  model-registry event;
- `setupTileLinkedItemRenderers()` — block-backed TEISR assignment from client
  display initialization.

These lifecycle placements are current project conventions, not Forge API
requirements.

### 3. Create the dedicated sprite model

Example:

```json
{
  "parent": "item/generated",
  "textures": {
    "layer0": "thaumcraft:blocks/manashroom"
  }
}
```

Use `thaumcraft:items/...` for item textures. Preserve extra layers,
`tintindex`, animation metadata, and overrides when the reference requires
them. Multiple metadata values may share a model; one file per metadata is not
a universal requirement.

### 4. Keep world rendering independent

Changing an item model route should not silently alter blockstates or world
models. World rendering may be baked, TESR-only, or split baked/TESR.

Current Custom Plant example:

- item metas 0-5 route to dedicated generated models;
- world metas 0-3 and 5 use `block/cross` models;
- Ethereal Bloom meta 4 uses a TESR and an empty baked world model carrying a
  particle texture;
- `models/item/blockcustomplant.json` is a tracked registry-name model, not an
  invalid-metadata fallback.

## Verification

Add a focused guard that proves, for each affected metadata:

- the exact model registration path;
- the selected JSON parent and texture;
- referenced resources exist;
- unrelated metadata routes remain unchanged.

Then run:

```text
./scripts/dev.sh gradle test --tests <focused-test>
./scripts/dev.sh build
```

Compilation does not prove item appearance. Inspect inventory, first/third
person, dropped, fixed/frame, and GUI contexts. Use
`tc4-client-vision-probe` when a reproducible screenshot is needed. Server
smoke is required only if common/server registration or loading also changed.

## Anti-patterns

- Do not infer appearance from the registration helper name.
- Do not assume all plants or crossed blocks need flat item sprites.
- Do not describe a base item JSON as metadata fallback without proving the
  actual model lookup path.
- Do not edit world block models to repair an item-only route.
- Do not add or remove TEISR assignment without checking the exact `Item` and
  all metadata that share it.
- Do not claim success from compile/build without client visual evidence.
