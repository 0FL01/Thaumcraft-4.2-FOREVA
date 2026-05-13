# Agent Instructions — Thaumcraft 4.2.3.5 -> Forge 1.12.2 Port

This repository is a work-in-progress Java 8 Minecraft Forge 1.12.2 port of Thaumcraft 4.2.3.5 from Minecraft 1.7.10.

## Sources of truth

Read these files before changing code:

- `AGENTS.md`
- `docs/PRD.md`
- `docs/REPAIR.md`
- `docs/CODEX_GOAL.md`
- `build.gradle`
- `Dockerfile`

Use `thaumcraft_src/**` and `Thaumcraft-1.7.10-4.2.3.5.jar` as read-only original 1.7.10 reference material.

## Hard rules

- Do not edit `thaumcraft_src/**`.
- Do not edit `Thaumcraft-1.7.10-4.2.3.5.jar`.
- Do not create `GOAL.md`.
- Do not change public `thaumcraft.api.*` signatures unless there is no Forge 1.12.2-compatible alternative.
- Do not rename packages away from original Thaumcraft package boundaries.
- Do not change mod id, registry names, NBT keys, config keys, packet ids, GUI ids, or dimension ids silently.
- Do not upgrade Forge, Gradle, Java, Baubles, or bundled CodeChicken code unless the final report documents a hard blocker.
- Do not perform broad formatting-only cleanup.
- Do not make unrelated dependency changes.
- Do not claim parity based on compile success alone.
- Preserve existing behavior unless `docs/REPAIR.md` or `docs/CODEX_GOAL.md` explicitly authorizes a behavior change.

## Project stack

- Language: Java.
- Runtime target: Minecraft Forge 1.12.2.
- Java target: Java 8.
- Build system: Gradle wrapper with ForgeGradle 2.3.
- Forge version: `1.12.2-14.23.5.2847`.
- MCP mappings: `stable_39`.
- Hard dependency: Baubles via CurseMaven.
- Bundled library: `thaumcraft.codechicken.*`.
- Public addon API boundary: `thaumcraft.api.*`.

## Architecture boundaries

- `thaumcraft.api.*`: public addon API and API jar output. Keep stable.
- `thaumcraft.common.*`: server/common gameplay, registration, config, blocks, items, tiles, entities, worldgen, research, crafting, network.
- `thaumcraft.client.*`: client-only GUI, rendering, models, particles, shaders, keybinds, client event handlers.
- `thaumcraft.codechicken.*`: bundled CCL-style rendering/math helpers.
- `truetyper.*`: font rendering support.
- `src/main/resources/assets/thaumcraft/**`: assets, sounds, textures, models, recipes, lang, GUI resources.
- `thaumcraft_src/**`: read-only original reference.

## Required workflow

1. Start with `git status --short`.
2. Read the relevant docs and code before editing.
3. For every gameplay-critical class, inspect the original 1.7.10 behavior first:
   - read matching source if present under `thaumcraft_src/**`;
   - or decompile with CFR from the original class.
4. Make small reversible changes.
5. Run focused validation after each checkpoint.
6. If validation fails, fix the failure before expanding scope.
7. Keep the final diff scoped and reviewable.
8. End with a final report listing exact commands and results.

## Development practices

- Keep original package names.
- Prefer original field/method names when practical for traceability.
- Prefer porting original behavior over inventing new behavior.
- Prefer existing project conventions over new abstractions.
- Use small helpers only when they remove real duplication across multiple callers.
- Do not introduce speculative abstraction.
- Do not use ad-hoc regex fixes for broken Java signatures.
- If a generated or transformed file has corrupted method signatures, stop and regenerate from the original source or manually port the file.

## Validation commands

Use Docker unless the local environment is already known to be Java 8 Forge-compatible.

Build the Docker image:

    docker build -t thaumcraft-dev .

Discover Gradle tasks:

    docker run --rm \
      -v "$(pwd):/workspace/thaumcraft" \
      -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
      --user 1000:1000 \
      --entrypoint ./gradlew \
      thaumcraft-dev tasks

Compile Java:

    docker run --rm \
      -v "$(pwd):/workspace/thaumcraft" \
      -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
      --user 1000:1000 \
      --entrypoint ./gradlew \
      thaumcraft-dev compileJava

Build:

    docker run --rm \
      -v "$(pwd):/workspace/thaumcraft" \
      -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
      --user 1000:1000 \
      --entrypoint ./gradlew \
      thaumcraft-dev build

Build API/dev jars:

    docker run --rm \
      -v "$(pwd):/workspace/thaumcraft" \
      -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
      --user 1000:1000 \
      --entrypoint ./gradlew \
      thaumcraft-dev apiJar devJar

Run tests if the Gradle task exists:

    docker run --rm \
      -v "$(pwd):/workspace/thaumcraft" \
      -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
      --user 1000:1000 \
      --entrypoint ./gradlew \
      thaumcraft-dev test

Run client smoke test if display/X11 is available:

    docker run --rm -it \
      -v "$(pwd):/workspace/thaumcraft" \
      -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
      -e DISPLAY="$DISPLAY" \
      -v /tmp/.X11-unix:/tmp/.X11-unix \
      --user 1000:1000 \
      --entrypoint ./gradlew \
      thaumcraft-dev runClient

## Stop conditions

Stop successfully when:

- the current checkpoint is complete;
- validation passes, or pre-existing/environment failures are documented;
- final diff is scoped;
- final report is complete.

Stop as blocked when:

- original behavior cannot be determined safely;
- implementation requires forbidden public contract changes;
- validation cannot run due to missing environment;
- build/test failures are unclear;
- completing the task would require out-of-scope dependency or architecture changes.
