# EDITORIAL_RULES_FOR_PROGRESS.md

## Purpose

These rules define how future progress entries should be written so documentation does not outrun backend reality.

## Required vocabulary

Use these exact labels where possible.

### `data corpus present`

Use when entries, keys, recipes, lang, assets, or page definitions exist.

Do not use this to imply behavior works.

Example:

```md
Research data corpus present: six categories and 201 research entries are registered. Runtime progression remains unvalidated.
```

### `registered`

Use when a Forge/API/lifecycle registration path exists and is wired.

Do not use this to imply the registered object is usable in gameplay.

Example:

```md
Special recipes are registered through `RegistryEvent.Register<IRecipe>`. Craftability validation remains open.
```

### `implemented`

Use only when production code exists and is connected to expected lifecycle/consumer paths.

Do not use `implemented` for:

- a class shell;
- a method with no meaningful body;
- a static guard test;
- a display-only fallback handle;
- client UI that does not reach server behavior.

Better wording:

```md
Tile runtime implementation is present, but representative scenarios are not runtime validated.
```

### `partially wired`

Use when some path exists but normal gameplay, all consumers, or all server checks are missing.

Example:

```md
Research note model is partially wired: note NBT and completion checks exist, but the normal Thaumonomicon -> table -> note completion route is not validated.
```

### `static guarded only`

Use when evidence is a source/corpus/shape test.

Examples:

```md
Recipe key corpus is static guarded only; craftability is not proven.
```

```md
Packet discriminator count is guarded; packet authorization semantics are not validated.
```

### `runtime smoke passed`

Use only for load/build/server-start smoke.

Do not use as gameplay proof.

Example:

```md
`validate --smoke` passed for server load stability. No crucible/thaumatorium gameplay scenarios were executed.
```

### `runtime validated`

Use only when the behavior itself was exercised by an automated integration test, controlled server scenario, or documented manual run.

The entry must say what was validated.

Example:

```md
Runtime validated: Arcane Workbench shaped recipe consumes inputs, applies wand vis cost, and handles container remainders in `ArcaneWorkbenchRuntimeIntegrationTest`.
```

### `parity candidate`

Use only when implementation and runtime validation match the reference for the scoped behavior, and remaining deviations are documented.

Example:

```md
Arcane crafting is a parity candidate for the tested shaped/shapeless/wand/sceptre paths; recipe collision and shift-click edge cases remain open.
```

### `complete`

Use only when:

- blocker/high gaps in that stage scope are closed;
- validation evidence is listed;
- docs are updated;
- remaining limitations are low/explicitly scoped out.

Do not use `complete` for a stage because compile/test/smoke passed.

## Claims that must be downgraded automatically

Downgrade these phrases unless runtime behavior evidence is listed in the same section.

| Phrase | Replace with |
|---|---|
| `substantially ported and guarded` | `substantial implementation/corpus present; static guarded; runtime validation open` |
| `ready` | `load-stable`, `registered`, `partially wired`, or `runtime validated`, whichever is true |
| `implemented` | `implementation present` plus validation status |
| `parity` | `parity target`, `parity candidate`, or `not parity-validated` |
| `smoke passed` | `server load smoke passed; gameplay not validated` |
| `guarded` | `static guarded only` unless behavior test exists |
| `research complete enough` | `research data/model present; progression route and authority blockers remain` |
| `Outer Lands baseline restored` | `Outer Lands scaffold/hook present; portal/maze runtime validation open` |
| `backend substantially complete` | `backend substantially populated; backend parity not substantially validated` |

## Rules for writing `GOAL_PROGRESS.md`

1. Start each checkpoint with the evidence type: `static`, `compile`, `smoke`, `runtime`, or `manual`.
2. Separate implementation from validation.
3. For every “passed” validation claim, say what it did not test.
4. Never group unrelated Stage 8 visual progress and Stage 9 backend readiness into one closure claim.
5. Do not write “guarded” without saying static guarded or runtime validated.
6. Do not write “complete” while a blocker/high gap remains in the same stage document.
7. If a section is historical, mark it historical or move it to archive.
8. If current code contradicts an old gap title, rewrite the title or add a current interpretation note.
9. If a fallback implementation exists, document whether it is behavior-equivalent, display-only, or temporary.
10. If a packet accepts client input for progression, docs must say “untrusted until server-authoritative validation exists.”

## Rules for Stage docs

### Keep

- Source-of-truth references.
- Original behavior notes.
- Acceptance criteria.
- Runtime validation matrices.
- Explicit blockers and dependencies.

### Delete or archive

- Claims that a currently implemented class/method is absent.
- Old “stub” labels after production implementation exists.
- Duplicated checkpoint logs that hide current blockers.
- Visual/client progress inside backend status sections unless it affects backend route.

### Rewrite

- Any “absent” gap that is now implemented should become “implementation present; validation open.”
- Any “guarded” claim should specify static vs runtime.
- Any “smoke passed” claim should state gameplay scope not tested.
- Any Stage 9 research claim should distinguish data corpus, GUI route, server packets, and runtime progression.

## Data corpus vs behavior examples

Bad:

```md
Stage 9-e research is substantially complete; 201 entries are registered and graph tests pass.
```

Good:

```md
Stage 9-e research corpus is present: six categories and 201 entries are registered and statically guarded. Backend progression is not complete: Thaumonomicon action routing, research table packet authority, scan authority, and end-to-end note solving remain open.
```

Bad:

```md
Crucible/thaumatorium are implemented.
```

Good:

```md
Crucible and thaumatorium production code is present. Runtime validation remains open for player attribution, research gates, exact/extra essentia, output blocking, top/bottom transport, and save/load.
```

Bad:

```md
Outer Lands baseline restored.
```

Good:

```md
Outer Lands provider, generation hook, maze handler, and ring/altar scaffold are present. Do not claim baseline restored until fresh-world ring generation, maze persistence, portal activation, safe dimension transfer, and return path are runtime validated.
```

## Minimum evidence required for closure phrases

### To write `server-safe`

Need:

- dedicated server smoke for the checkpoint;
- no client-only classloading failures;
- packet handlers validate player/context where gameplay state changes;
- no known common/API side-only import hazard left undocumented.

### To write `progression validated`

Need:

- server-authoritative scan or research action;
- prerequisite/cost checks;
- state mutation in capability/NBT;
- client sync where required;
- invalid action rejection test;
- save/load if persistent state changed.

### To write `recipe validated`

Need:

- recipe registered or intentionally display-only documented;
- valid craft succeeds;
- invalid craft fails;
- inputs/aspects/vis/essentia consumed correctly;
- research gate works;
- output/remainder behavior checked.

### To write `tile NBT validated`

Need:

- write/read round-trip test or in-world save/reload scenario;
- mid-operation state if the tile has a process loop;
- inventory/aspect/fluid/custom fields checked;
- sync/update packet behavior checked if GUI/client depends on it.

## Progress entry template

Use this template for future checkpoints.

```md
### Checkpoint YYYY-MM-DD — <subsystem>

Evidence type: static / runtime integration / server smoke / manual scenario.

Implemented:

- ...

Validated:

- command/test/scenario and exact behavior covered.

Not validated:

- ...

Docs status:

- Stage doc updated: yes/no.
- Claims allowed after this checkpoint: ...
- Claims still forbidden: ...

Remaining blockers:

- ...
```
