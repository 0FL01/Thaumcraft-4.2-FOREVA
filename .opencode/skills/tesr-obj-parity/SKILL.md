---
name: tesr-obj-parity
description: "Use when a TC4 TESR/TEISR or hardcoded renderer has missing/extra OBJ surfaces, transparent faces, wrong UVs, winding, or group transforms. Covers exact original call-site groups, parser-specific UV conversion, face topology, and comparative visual proof."
---

# OBJ parity for TESR/TEISR and hardcoded models

## Use this skill when

- original TC4 loads a Wavefront OBJ through `AdvancedModelLoader`;
- the port uses raw `BufferBuilder` arrays, bundled CCL OBJ parsing, Forge baked
  OBJ, TESR, or TEISR; and
- the defect concerns missing/extra groups, faces, UVs, winding, normals, or
  per-group transforms.

Use `fixitemview` for a wrong flat-sprite item route and `tex4port` for
`ModelRenderer` cuboids converted to baked JSON.

## Core rule: reproduce the original call site, not the whole OBJ

An OBJ file may contain groups used by different renderers. Decompile each
original renderer and record:

- exact `renderPart("...")` calls;
- call order;
- transform and texture bound for each group;
- animation/bobbing applied between group calls;
- blend, cull, lighting, lightmap, and normal state.

Do **not** merge every group merely because it exists in the same file.

Example from `obelisk_cap.obj`:

| Original TC4 renderer | Groups |
|---|---|
| Eldritch Obelisk | `Cap` only |
| Eldritch Capstone | `Cap` only |
| Flux Scrubber | `Cap`, then separately transformed `Tip` |

The current port's `ModelEldritchCap.renderCap()` renders both groups and a
static guard currently accepts that behavior. Treat this as unresolved product
parity debt, not as a known fixed case. The confirmed historical improvements
were the Cap UV orientation and Obelisk side width, not adding Tip to the cap.

## Identify the active rendering pipeline

Before changing UVs or arrays, classify the path:

1. **Raw hardcoded OBJ data** copied into Java arrays and emitted through
   `BufferBuilder`.
2. **Bundled CCL parser** (`CCModel.parseObjModels` or equivalent).
3. **Forge baked OBJ** routed through the model loader.
4. A model that is not OBJ-derived at all.

Parser conversions differ. Advice for raw arrays must not be applied again to
already parsed models.

## Texture coordinates

Forge 1.7.10's Wavefront parser stores OBJ texture V as `1 - v`. The bundled
current CCL parser also flips V while parsing. Forge 1.12 baked OBJ handling is
separate: its bake path applies a V flip only when the loader/model state has
`flip-v` enabled. Therefore:

- raw `vt` values copied directly into a hardcoded renderer usually need one
  `1.0F - v` conversion to match original TC4;
- CCL-parsed UVs must not be flipped a second time;
- Forge baked OBJ models must be checked for their effective `flip-v` state
  instead of assuming either orientation;
- hand-authored/current arrays require evidence of whether conversion already
  happened.

For raw arrays, an audited emission may look like:

```java
buffer.pos(x, y, z)
        .tex(u, 1.0F - v)
        .normal(nx, ny, nz)
        .endVertex();
```

This is pipeline-specific, not a universal renderer rule.

Alpha sampling can diagnose an obviously transparent region, but a triangle
centroid is not visual proof. It can miss partial alpha, filtering, intended
transparency, and edge coverage.

## Faces, winding, and normals

Inventory the original groups and face records:

```text
g GroupName
v ...
vt ...
vn ...
f ...
```

Then preserve or deliberately convert:

- face arity: original loader accepts triangles and quads;
- vertex order/winding;
- per-group boundaries;
- material/texture changes;
- missing `vt`/`vn` cases.

Triangulation is valid only when documented and winding is retained.

Do not blindly copy OBJ `vn`. The original 1.7.10 face renderer computes a
face normal and applies a small UV inset toward the face centroid; a literal
`vn` transcription is not automatically parity. Decide whether the current
lighting path needs face normals, vertex normals, or baked item normals.

Vertex format is also pipeline-specific:

- raw hardcoded models may use `POSITION_TEX_NORMAL`;
- bundled CCL paths use their own old-model format;
- Forge baked OBJ uses item/baked formats.

`enableRescaleNormal()` is relevant when the active transform scales normals;
the mere presence of normals is not sufficient reason.

## Transcription workflow

1. Confirm the affected world/item metadata route and renderer.
2. Decompile the exact original renderer with CFR.
3. Record group calls, textures, transforms, and GL state.
4. Parse the original OBJ and compare only the groups invoked by that call
   site.
5. Compare face counts, arity, indices, winding, UV conversion, and normal
   semantics with the port.
6. Fix geometry/UV evidence before changing culling or broad GL state.
7. Check every world TESR and item TEISR caller sharing the model.

Hardcoded arrays should retain named group boundaries:

```java
private static final int[][] CAP_FACES = { ... };
private static final int[][] TIP_FACES = { ... };

void renderCap() { renderGroup(CAP_FACES); }
void renderTip() { renderGroup(TIP_FACES); }
```

Do not create a convenience `renderAll()` when original callers intentionally
select groups or transform them separately.

## Deterministic checks

Prefer a parser-backed guard that verifies:

- original group names and face counts;
- exact group-to-renderer call mapping;
- array indices/arity and documented triangulation;
- one and only one UV-origin conversion for the active pipeline;
- textures and transform order per call site;
- no unrelated group is silently included.

String-presence tests are useful as tripwires but cannot prove geometry.

## Visual verification

Run focused tests and build:

```text
./scripts/dev.sh gradle test --tests <focused-test>
./scripts/dev.sh build
```

Server smoke does not exercise client OBJ rendering. Compare original TC4 and
the port in every affected context:

- world TESR from relevant sides/distances;
- inventory/hand/drop/frame when a TEISR uses the model;
- animated states and separately transformed groups;
- textures with different alpha layouts.

Use `tc4-client-vision-probe` for reproducible port screenshots. A port-only
image that merely "looks okay" is weaker than a TC4/port comparison.

## Anti-patterns

- Do not render every OBJ group by default.
- Do not double-flip parsed UVs.
- Do not mandate copied `vn`, triangles, or one vertex format.
- Do not use global `disableCull()` as a substitute for topology/winding.
- Do not claim visual parity from compile, server smoke, or source markers.
- Do not use unstable legacy numeric IDs when registry name plus metadata is
  available.
