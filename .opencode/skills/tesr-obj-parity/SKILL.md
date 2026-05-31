---
name: tesr-obj-parity
description: "Use when TC4 port TESR/TEISR OBJ or hardcoded model blocks/items have missing faces, transparent holes, truncated tips, ghost planes, or wrong UVs in Forge 1.12.2. Covers 1.7.10 AdvancedModelLoader parity, OBJ group transcription, texture V-flip, and static guard tests."
---

# TESR OBJ Parity: missing faces, transparent holes, ghost planes

## When to use

Use this skill when a TC4 1.7.10 → Forge 1.12.2 ported block or item:

- is rendered by `TileEntitySpecialRenderer` or `TileEntityItemStackRenderer`;
- originally used `AdvancedModelLoader.loadModel(... .obj)` or `IModelCustom`;
- was ported to a hardcoded Java model using `BufferBuilder`;
- shows missing faces, transparent holes, truncated tips/apexes, black ghost planes, or wrong texture placement.

Typical examples:

- `Eldritch Obelisk #4220/1`
- `Eldritch Capstone #4220/3`
- any `ModelXxx` class transcribed from an original `.obj` asset.

Do **not** use this skill for ordinary baked JSON block/item model routing. Use `fixitemview` or `tex4port` for those.

## Quick diagnosis

```
symptom: missing/transparent TESR faces
  → confirm block meta uses TESR/TEISR, not baked JSON
  → decompile original 1.7.10 renderer
  → inspect original OBJ groups, vertices, UVs, normals, faces
  → compare every visually required OBJ group with hardcoded ModelXxx arrays
  → verify texture V origin: OBJ UVs usually need v = 1 - v in BufferBuilder
  → add guard tests for group presence and UV flip
```

## Step-by-step workflow

### 1. Confirm the block actually uses TESR

Check the reported block id/meta against `BlockEldritch` or the relevant block class:

- `hasTileEntity(...)`
- `createTileEntity(...)`
- `getRenderType(...)`
- `ClientRegistry.bindTileEntitySpecialRenderer(...)`
- item path: `registerBuiltinItemModel(...)` + `TileEntityItemStackRenderer`

If `getRenderType()` returns `INVISIBLE` for the meta, blockstate JSON is usually only a fallback. Do not spend the first pass editing JSON textures/models.

### 2. Decompile the original 1.7.10 renderer

Use the original source tree or CFR from `thaumcraft_src/**` / the TC4 jar.

Look for:

```java
AdvancedModelLoader.loadModel(new ResourceLocation("thaumcraft", "textures/models/name.obj"));
model.renderPart("PartName");
UtilsFX.bindTexture("textures/models/name.png");
GL11.glTranslated(...);
GL11.glRotated(...);
```

Record:

- OBJ path;
- texture path;
- renderPart names;
- translation/rotation order;
- OpenGL state: lighting, rescale normal, cull, blend, lightmap.

### 3. Inspect the OBJ asset, not only the Java port

Read the OBJ from the original assets and the port assets:

```
thaumcraft_src/assets/thaumcraft/textures/models/<name>.obj
src/main/resources/assets/thaumcraft/textures/models/<name>.obj
```

Inventory every group/object:

```
g Cap
g Tip
v ...
vt ...
vn ...
f v/vt/vn ...
```

For each visually required group, count:

| Item | Check |
|---|---|
| `v` | all vertices copied |
| `vt` | all UVs copied |
| `vn` | all normals copied |
| `f` | all triangles copied in the same index order |

Anti-pattern: assuming one hardcoded array named `TRIANGLES` means the whole OBJ was transcribed. The Eldritch cap bug was caused by copying only the `Cap` group and omitting the `Tip` group.

### 4. Check texture V origin and alpha coverage

OBJ UV coordinates use a bottom-left texture origin. Minecraft texture sampling via `BufferBuilder.tex(u, v)` is top-left oriented for these model textures. If a port copies `vt` values directly, triangles can land on transparent atlas quadrants.

For hardcoded OBJ models, prefer:

```java
buf.pos(x, y, z)
        .tex(uv[0], 1.0F - uv[1])
        .normal(nx, ny, nz)
        .endVertex();
```

Use a quick alpha-coverage sanity check before guessing about culling:

```python
from PIL import Image
img = Image.open('src/main/resources/assets/thaumcraft/textures/models/name.png').convert('RGBA')
# Sample triangle UV centroids once with v, once with 1-v.
# If direct v samples transparent pixels and flipped v samples opaque pixels,
# the defect is UV origin, not missing texture files.
```

The Eldritch cap fix was confirmed this way: direct UVs sampled transparent pixels for most side triangles; `1.0F - v` sampled opaque pixels.

### 5. Reproduce all required OBJ groups in Java

Hardcoded model structure should make missing groups hard to miss:

```java
// Wavefront "Cap" group triangles from name.obj.
private static final int[][] CAP_TRIANGLES = { ... };

// Wavefront "Tip" group triangles from name.obj.
private static final int[][] TIP_TRIANGLES = { ... };

public void renderAll() {
    buf.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
    render(CAP_TRIANGLES, buf);
    render(TIP_TRIANGLES, buf);
    tess.draw();
}
```

Keep OBJ 1-based indices in arrays if that matches existing port style, but subtract one at lookup time.

### 6. Only then inspect culling, winding, and transforms

After geometry and UVs are verified, inspect:

- triangle winding vs `GlStateManager.disableCull()` / `enableCull()`;
- `pushMatrix()` / `popMatrix()` balance;
- exact original translation/rotation order;
- `OpenGlHelper.setLightmapTextureCoords(...)` parity;
- `GlStateManager.enableRescaleNormal()` when normals are used;
- blend state around alpha textures.

Do not lead with broad `disableCull()` patches unless OBJ geometry/UV parity has been checked first.

## Known fixed case: Eldritch Obelisk/Capstone

Defective blocks:

```text
Eldritch Obelisk #4220/1
Eldritch Capstone #4220/3
```

Fix summary:

- `ModelEldritchCap` was missing OBJ `Tip` geometry from `obelisk_cap.obj`.
- The hardcoded model copied OBJ UVs directly; texture V had to be flipped with `1.0F - uv[1]`.
- `TileEldritchObeliskRenderer.renderSides(...)` was aligned to the original half-width `0.5F`.
- Static guard test was updated to assert Tip geometry and UV flip markers.

Relevant files:

- `src/main/java/thaumcraft/client/renderers/models/ModelEldritchCap.java`
- `src/main/java/thaumcraft/client/renderers/tile/TileEldritchObeliskRenderer.java`
- `src/test/java/thaumcraft/client/TableEldritchRendererFidelityStaticGuardTest.java`
- `src/main/resources/assets/thaumcraft/textures/models/obelisk_cap.obj`

## Guard tests

For every hardcoded OBJ fix, add/update a static guard test that checks:

- original asset path is named in the model comment;
- every required group has a named triangle array;
- `DefaultVertexFormats.POSITION_TEX_NORMAL` is used for OBJ triangles;
- `1.0F - uv[1]` is present when OBJ UVs are copied;
- renderer still binds the correct texture and uses original transforms.

Example assertions:

```java
assertTrue(model.contains("Wavefront \"Tip\" group triangles from obelisk_cap.obj"));
assertTrue(model.contains("private static final int[][] TIP_TRIANGLES"));
assertTrue(model.contains(".tex(uv[0], 1.0F - uv[1])"));
assertTrue(renderer.contains("MODEL.renderCap();"));
```

## Validation

Runtime-affecting renderer/model changes require:

```text
./scripts/dev.sh compileJava
./scripts/dev.sh validate --smoke
```

Compile/smoke passing is not visual parity. Require an in-game screenshot or direct visual confirmation for the affected block/item metas.

## Anti-patterns

- Do not claim missing textures until alpha coverage and UV orientation are checked.
- Do not edit baked JSON first when the block meta routes through TESR.
- Do not assume `renderPart("X")` means all visually required OBJ geometry was hardcoded.
- Do not copy only the largest OBJ group and ignore small groups named `Tip`, `Gem`, `Core`, `Ring`, etc.
- Do not use `POSITION_TEX_COLOR` for OBJ geometry that has normals unless the original intentionally did not use lighting.
- Do not disable culling globally as a substitute for correct group transcription or winding.
- Do not move/scale TEISR models ad hoc before comparing original transform order.
