# Codex Goal Runbook — Thaumcraft 4.2.3.5 -> Forge 1.12.2 Parity

## 1. Purpose

This file exists to keep Codex `/goal` objectives short.

The `/goal` command should point to this file instead of embedding the full plan in the objective.

Do not create `GOAL.md`.

## 2. Primary objective

Complete the Thaumcraft 4.2.3.5 to Minecraft Forge 1.12.2 port to the closest safe parity possible, preserving original gameplay behavior, addon API compatibility, registry identity, save data, config keys, NBT keys, research/crafting progression, worldgen behavior, and client presentation.

Work checkpoint by checkpoint. Do not attempt the whole port as one unbounded cleanup.

## 3. Required reading order

Before changing code, read:

1. `AGENTS.md`
2. `docs/PRD.md`
3. `docs/REPAIR.md`
4. `docs/CODEX_GOAL.md`
5. `build.gradle`
6. `Dockerfile`
7. Relevant current source files under `src/main/java/**`
8. Matching original reference files under `thaumcraft_src/**` or CFR decompile output

## 4. Operating rules

- Preserve behavior unless explicitly allowed.
- Prefer small reversible changes.
- Keep the diff scoped.
- Do not perform unrelated cleanup.
- Do not add new dependencies unless necessary and justified.
- Do not silently change public contracts.
- Do not ignore failing validation.
- Do not continue through major ambiguity.
- Keep the repository coherent after each checkpoint.
- Prefer existing project conventions over new patterns.
- Compare against original 1.7.10 behavior before implementing gameplay-critical code.
- Separate server/common behavior from client-only rendering behavior.
- Update docs only after implementation and validation.

## 5. Forbidden changes

- Do not edit `thaumcraft_src/**`.
- Do not edit `Thaumcraft-1.7.10-4.2.3.5.jar`.
- Do not create `GOAL.md`.
- Do not change public `thaumcraft.api.*` signatures unless unavoidable.
- Do not change mod id.
- Do not change registry names silently.
- Do not change NBT keys silently.
- Do not change config keys silently.
- Do not change packet ids or GUI ids silently.
- Do not upgrade Forge, Gradle, Java, Baubles, or bundled CodeChicken code for convenience.
- Do not introduce broad formatting-only diffs.
- Do not touch unrelated modules.
- Do not commit generated build output.
- Do not mark docs as complete without validation evidence.

## 6. Checkpoint sequence

### Checkpoint A: Documentation and baseline audit

Goal:

- Verify that docs match the local repository.
- Do not change production code.

Allowed files:

- `AGENTS.md`
- `docs/PRD.md`
- `docs/REPAIR.md`
- `docs/CODEX_GOAL.md`

Validation:

- `git status --short`
- `find . -maxdepth 3 -type f | sort`
- `find . -maxdepth 3 -type d | sort`
- `./gradlew tasks` through Docker if practical
- `compileJava` through Docker if practical

Stop condition:

- Docs are accurate enough for implementation.
- Any local contradictions are documented.

### Checkpoint B: Pre-Phase8 P0 server blockers

Goal:

- Close or explicitly defer P0 server blockers from `docs/REPAIR.md`.

Targets:

- Remaining no-op focus server actions.
- Arcane Bore mining loop decision/implementation.

Validation:

- `compileJava`
- focused placeholder scan
- manual scenario notes

Stop condition:

- P0 is closed or deferred with evidence.

### Checkpoint C: Pre-Phase8 P1 server blockers

Goal:

- Close high-priority server/common parity gaps before client work.

Targets:

- Baubles/relic/wand integration.
- Research compatibility and Frugal enchant applicability.
- Boss/special mob behavior.
- Tool/armor repairability.

Validation:

- `compileJava`
- `build` if practical
- focused manual scenario notes

Stop condition:

- P1 is closed or deferred with evidence.

### Checkpoint D: Phase 8 client GUI/rendering/FX

Goal:

- Implement missing client-only GUI/rendering/FX/shader support in focused groups.

Targets:

- `thaumcraft.client.*`
- GUI screens.
- `ClientProxy`.
- key bindings.
- client event handlers.
- TESRs.
- entity renderers.
- models.
- particles/beams/bolts.
- resources required for client rendering.

Validation:

- `compileJava`
- `processResources`
- `runClient` if display is available
- manual GUI/render smoke checks

Stop condition:

- Critical GUI/render/FX flows work or blockers are documented.

### Checkpoint E: Phase 9 recipes/research/content

Goal:

- Complete recipe/research/content registration needed for progression parity.

Targets:

- `ConfigRecipes`
- `ConfigResearch`
- crafting classes
- research classes
- recipe resources
- research/Thaumonomicon references
- aspect tag registration

Validation:

- `compileJava`
- `processResources`
- `build`
- `runClient` if practical
- manual progression/content checks

Stop condition:

- Critical progression path is usable or blockers are documented.

### Checkpoint F: Final parity and polish

Goal:

- Verify runtime stability and close limited polish items.

Targets:

- optional JEI only if optional;
- config GUI only if required and scaffolded;
- localization/resource completeness;
- performance-sensitive completed systems;
- crash/runtimes.

Validation:

- `clean build`
- `apiJar devJar`
- `runClient`
- `runServer` if available
- final placeholder scan
- final diff review

Stop condition:

- Acceptance criteria are satisfied or remaining blockers are explicitly documented.

## 7. Required validation commands

Build Docker image:

    docker build -t thaumcraft-dev .

Discover tasks:

    docker run --rm \
      -v "$(pwd):/workspace/thaumcraft" \
      -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
      --user 1000:1000 \
      --entrypoint ./gradlew \
      thaumcraft-dev tasks

Fresh workspace setup if required:

    docker run --rm \
      -v "$(pwd):/workspace/thaumcraft" \
      -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
      --user 1000:1000 \
      --entrypoint ./gradlew \
      thaumcraft-dev setupDecompWorkspace

Compile:

    docker run --rm \
      -v "$(pwd):/workspace/thaumcraft" \
      -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
      --user 1000:1000 \
      --entrypoint ./gradlew \
      thaumcraft-dev compileJava

Process resources:

    docker run --rm \
      -v "$(pwd):/workspace/thaumcraft" \
      -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
      --user 1000:1000 \
      --entrypoint ./gradlew \
      thaumcraft-dev processResources

Test if task exists:

    docker run --rm \
      -v "$(pwd):/workspace/thaumcraft" \
      -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
      --user 1000:1000 \
      --entrypoint ./gradlew \
      thaumcraft-dev test

Build:

    docker run --rm \
      -v "$(pwd):/workspace/thaumcraft" \
      -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
      --user 1000:1000 \
      --entrypoint ./gradlew \
      thaumcraft-dev build

Build jars:

    docker run --rm \
      -v "$(pwd):/workspace/thaumcraft" \
      -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
      --user 1000:1000 \
      --entrypoint ./gradlew \
      thaumcraft-dev apiJar devJar

Clean build before final success:

    docker run --rm \
      -v "$(pwd):/workspace/thaumcraft" \
      -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
      --user 1000:1000 \
      --entrypoint ./gradlew \
      thaumcraft-dev clean build

Client smoke test if display is available:

    docker run --rm -it \
      -v "$(pwd):/workspace/thaumcraft" \
      -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
      -e DISPLAY="$DISPLAY" \
      -v /tmp/.X11-unix:/tmp/.X11-unix \
      --user 1000:1000 \
      --entrypoint ./gradlew \
      thaumcraft-dev runClient

Server smoke test if available:

    docker run --rm \
      -v "$(pwd):/workspace/thaumcraft" \
      -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
      --user 1000:1000 \
      --entrypoint ./gradlew \
      thaumcraft-dev runServer

Placeholder scan:

    rg -n "TODO|TBD|placeholder|Phase 8:|return false|return null|no-op" src/main/java/thaumcraft docs

Diff review:

    git status --short
    git diff --stat
    git diff --name-only

API jar inspection after `apiJar`:

    jar tf build/libs/Thaumcraft-*-api.jar | rg '^thaumcraft/api/'

## 8. Original reference workflow

For every touched gameplay class:

1. Find matching class under `thaumcraft_src/**`.
2. If only `.class` exists, decompile with CFR:

       docker run --rm \
         -v "$(pwd):/workspace/thaumcraft" \
         thaumcraft-dev \
         -c "cfr thaumcraft_src/path/to/Class.class"

3. Compare original behavior to current 1.12.2 behavior.
4. Port behavior manually.
5. Adapt only the APIs that changed from 1.7.10 to 1.12.2.
6. Keep original names where practical.
7. Document the inspected reference class in the final report.

## 9. Acceptance criteria

Functional acceptance:

- Existing working behavior remains preserved.
- Public API remains compatible.
- Registry names remain compatible.
- NBT/config/save data contracts remain compatible.
- Critical user flows work.
- Edge cases related to changed systems are covered by tests or manual scenarios.

Architecture acceptance:

- Server gameplay remains in `thaumcraft.common.*`.
- Client-only rendering remains in `thaumcraft.client.*`.
- Public addon API remains in `thaumcraft.api.*`.
- Shared helpers are introduced only for real repeated logic.
- Old placeholders are removed only after replacements are connected.
- No speculative abstractions are introduced.

Validation acceptance:

- Required commands are run where practical.
- Failing commands are investigated.
- Pre-existing failures are documented with evidence.
- Environment skips are documented with reason.
- Final report includes exact commands and results.

Diff acceptance:

- Diff stays inside checkpoint scope.
- No unrelated files are changed.
- No generated output is committed.
- No local paths, secrets, or machine-specific values are introduced.
- Diff is reviewable module by module.

## 10. Final report template

Every run must end with this structure:

# Final Report: Thaumcraft 4.2.3.5 -> Forge 1.12.2 Checkpoint

## 1. Summary of changes

- ...

## 2. Files modified

- `path` — reason

## 3. Original reference inspected

- `thaumcraft_src/path` — what was compared

## 4. Architecture before/after

Before:

- ...

After:

- ...

## 5. Public contract status

- API signatures:
- Registry names:
- NBT keys:
- Config keys:
- Packet/GUI ids:
- Resource paths:

## 6. Acceptance criteria checklist

Functional:

- [ ] ...

Architecture:

- [ ] ...

Validation:

- [ ] ...

Diff:

- [ ] ...

## 7. Validation commands and results

- Command: `...`
- Result:
- Evidence:

## 8. Manual/runtime checks

- Scenario:
- Result:
- Evidence or limitation:

## 9. Known limitations

- ...

## 10. Blockers

- ...

## 11. Suggested next checkpoint

- ...

## 11. Short `/goal` commands

### Audit-only goal

Use this before implementation if docs may be stale:

    /goal Read AGENTS.md, docs/CODEX_GOAL.md, docs/PRD.md, docs/REPAIR.md and audit the repository against them. Do not modify production code. Only update docs if the local tree contradicts them. Verify build/tooling commands are discoverable, report exact blockers, and stop with an audit report.

### P0/P1 server blocker goal

Use this for the next implementation slice:

    /goal Read AGENTS.md, docs/CODEX_GOAL.md, docs/PRD.md, docs/REPAIR.md first. Complete only the pre-Phase8 P0/P1 server parity blockers documented in docs/REPAIR.md. Preserve public API, registry names, NBT/config keys, and existing behavior. Use original 1.7.10 reference sources before each gameplay implementation. Run documented Docker/Gradle validation after each checkpoint. Stop only when complete or blocked with a final report.

### Phase 8 client goal

Use only after P0 is closed or deferred:

    /goal Read AGENTS.md, docs/CODEX_GOAL.md, docs/PRD.md, docs/REPAIR.md first. Complete the next Phase 8 client GUI/rendering/FX checkpoint only. Keep server gameplay unchanged, preserve public contracts, port from original client references, run compile/resource/client smoke validation where practical, and stop with a final report when complete or blocked.

### Phase 9 content goal

Use after client basics are ready:

    /goal Read AGENTS.md, docs/CODEX_GOAL.md, docs/PRD.md, docs/REPAIR.md first. Complete the next Phase 9 recipes/research/content checkpoint only. Preserve registry names, research keys, recipe ids, public API, and progression semantics. Validate build/resources and document manual progression checks. Stop with a final report when complete or blocked.

### Final parity goal

Use only after earlier checkpoints are closed:

    /goal Read AGENTS.md, docs/CODEX_GOAL.md, docs/PRD.md, docs/REPAIR.md first. Perform a final parity audit and limited polish pass for already implemented systems. Do not add broad cleanup or new dependencies. Run full validation where practical, document remaining limitations, and stop with a final report.
