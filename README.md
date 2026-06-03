# Thaumcraft 4.2.3.5 — Forge 1.12.2 Port

Port of Thaumcraft 4.2.3.5 from Minecraft 1.7.10 to 1.12.2.

Original mod by Azanor (2013-2015). This is an unofficial community port.

## Project stack

- **Language:** Java 8
- **Runtime:** Minecraft Forge 1.12.2 (14.23.5.2847)
- **Mappings:** stable_39
- **Build:** Gradle (ForgeGradle 2.3)
- **Dependency:** Baubles (CurseMaven)
- **Bundled:** CodeChicken Lib (thaumcraft.codechicken.*)

## What works

- Alchemy
- Golems
- Research system
- Thaumonomicon (text/images — semi-working)
- World generation / Aura (minor defects)
- Mob spawning, AI, textures
- Outer Lands dimension

## What does not work

- Certain item logic (Arcane Levitator, Traveller's Charm, Caging Stone, etc.)
- Infusion on the matrix (untested — may work)
- Anything not listed above is likely broken or incomplete

## Status

Early-to-mid stage port. Most core systems are wired but need polish.
Item logic and visual/rendering parity are the main gaps.

## Development

Solo project. Development assisted by LLM agents using various models (GPT 5.5, GLM 5.1, Deepseek V4, MiMo V2.5, etc.).

### Major known issue

Texture porting is the single largest resource drain. The original 1.7.10 rendering pipeline (ModelRenderer, AdvancedModelLoader OBJ, etc.) does not map cleanly to Forge 1.12.2 baked models. A significant amount of work goes into UV mapping, JSON model conversion, display transforms, and per-item model routing.

## Downloads

Pre-built binaries are available in [Releases](https://github.com/0FL01/Thaumcraft-4.2-FOREVA/releases). Download `Thaumcraft-1.0.0-universal.jar` from the latest release — builds are published automatically on every push.

## License

MIT License. See `LICENSE` for details.

Original Thaumcraft 4.2.3.5 (c) 2013-2015 Azanor.
