# Agent Instructions — Thaumcraft 4.2.3.5 -> Forge 1.12.2 Port

This repository is a work-in-progress Java 8 Minecraft Forge 1.12.2 port of Thaumcraft 4.2.3.5 from Minecraft 1.7.10.

## Sources of truth

Read these files before changing code:

- `AGENTS.md`
- `docs/PRD.md`
- `build.gradle`
- `Dockerfile`

Use `thaumcraft_src/**` and `Thaumcraft-1.7.10-4.2.3.5.jar` as read-only original 1.7.10 reference material.

Asset origin: assets (textures, sounds, models, lang, shaders, etc.) for the port can be copied from `thaumcraft_src/assets/` into `src/main/resources/assets/thaumcraft/`. This is the source of truth for all ported assets — do not recreate assets from scratch when a working original exists in `thaumcraft_src/assets/`.

## Hard rules

- Do not edit `thaumcraft_src/**`.
- Do not edit `Thaumcraft-1.7.10-4.2.3.5.jar`.
- Do not change public `thaumcraft.api.*` signatures unless there is no Forge 1.12.2-compatible alternative.
- Do not rename packages away from original Thaumcraft package boundaries.
- Do not change mod id, registry names, NBT keys, config keys, packet ids, GUI ids, or dimension ids silently.
- Do not upgrade Forge, Gradle, Java, Baubles, or bundled CodeChicken code unless the final report documents a hard blocker.
- Do not perform broad formatting-only cleanup.
- Do not make unrelated dependency changes.
- Do not claim parity based on compile success alone.
- Preserve existing behavior unless the current task or `docs/PRD.md` explicitly authorizes a behavior change.

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

## Current status guard

As of the 2026-05-13 documentation cleanup, Phases 3, 4, 5, 6, and 7 are not closed or parity-validated. They have important common/server baselines, but still need runtime/manual validation and documented deferrals before any phase can be claimed complete.

Use the phase guidance in `docs/PRD.md` and the explicit deferrals below as the active pre-Phase8 mine list. Do not convert prior `compileJava`/`build` success into parity closure. Current explicit deferrals include Portable Hole/Warding visual renderers, Phase 8 client GUI/render/FX/shader work, Phase 9 recipe/research/content registration, Hover Harness flight behavior, and Outer Lands runtime/portal parity validation. The active target is fresh worlds; old 1.7.10/WIP saves and external player-data imports are out of scope.

## Commit policy

Work on a dedicated branch. Do not work directly on `master` or `main`.

Use one commit per completed checkpoint.

A checkpoint commit is allowed only when:

- the checkpoint scope is complete;
- the diff is limited to the checkpoint;
- `git status --short` was reviewed;
- relevant validation commands were run;
- failures are either fixed or documented as pre-existing/environment failures;
- no forbidden files were modified;
- no generated build output is staged.

Do not commit broken work unless explicitly instructed by the user. If a blocker is reached, stop with a final report and leave the diff uncommitted.

Before every commit, run:

    git status --short
    git diff --stat
    git diff --name-only

Stage only files that belong to the checkpoint:

    git add <explicit paths>

Do not use broad staging commands like:

    git add .
    git add -A

Use commit messages in this format:

    docs: add Codex parity runbook
    port: close pre-Phase8 focus server blockers
    port: restore wand and bauble vis integration
    port: restore research compatibility and frugal applicability
    port: restore boss and special mob server behavior
    client: add Phase 8 GUI registrations
    client: port core Thaumcraft GUIs
    client: port TESR and entity renderer baseline
    client: port particles beams and shader baseline
    content: restore recipes and research registrations
    polish: finalize runtime parity checks

Each final report must include:

- commit hash if a commit was created;
- files included in the commit;
- validation commands run before the commit;
- whether runtime smoke validation was required, run, passed, failed, or skipped with a concrete reason;
- known limitations after the commit.

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

Use the project wrapper instead of repeating long Docker commands:

    ./scripts/dev.sh image
    ./scripts/dev.sh tasks
    ./scripts/dev.sh compileJava
    ./scripts/dev.sh build
    ./scripts/dev.sh check-jar
    ./scripts/dev.sh apiJar devJar
    ./scripts/dev.sh test

Run arbitrary Gradle tasks through Docker with:

    ./scripts/dev.sh gradle <task> [args...]

Run `./scripts/dev.sh check-jar` after building a jar meant for Prism/normal Forge. It scans the built jar for MCP-named Minecraft field/method references that dev `runServer` can miss but production Forge reports as `NoSuchFieldError` or `NoSuchMethodError`.

## Runtime smoke validation

Compile success is not enough for parity or checkpoint completion.

Run runtime smoke validation whenever a change can affect mod loading, registries, config, items, blocks, materials, recipes, entities, dimensions, networking, proxies, GUI registration, renderers, models, assets, or lifecycle handlers.

For common/server-side changes, run the dedicated server smoke test first:

    ./scripts/dev.sh smoke-server

The smoke wrapper creates `run/eula.txt`, runs `runServer -x getAssets --no-daemon`, writes `run/smoke-server.log`, and fails on crash markers or new crash reports. `-x getAssets` avoids old ForgeGradle Mojang asset URL failures. The `run/` directory is generated/ignored and must not be staged.

A server smoke test passes only when Forge reaches normal ready state, for example a log line containing `Done (`, and no crash markers are present.

A runtime smoke test fails if `crash-reports/` contains a new crash report, or logs contain crash markers such as `LoaderException`, `LoaderExceptionModCrash`, `Game crashed`, `Caught exception`, `NoClassDefFoundError`, `ClassNotFoundException`, `NoSuchMethodError`, `NoSuchFieldError`, `ExceptionInInitializerError`, `Repair material has already been set`, or any Forge/FML fatal loading error.

Run client smoke for client-only or mixed client/common changes. It may be terminated by timeout after the main menu/load phase; timeout alone is not failure if there are no new crash reports or crash markers and Forge/FML reports successful mod loading.

Do not mark runtime-affecting checkpoints complete based only on `compileJava`, `build`, `apiJar`, or `devJar`. Documentation-only diffs do not require runtime smoke.

Run client smoke test if display/X11 is available:

    ./scripts/dev.sh smoke-client

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
