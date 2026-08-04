# Thaumcraft 4.2.3.5 — Forge 1.12.2 Port

An unofficial community port of Azanor's Thaumcraft 4.2.3.5 for Minecraft 1.12.2.

> ## Port is ~90% complete
> Most features are working; only hidden bugs remain to be found and fixed.

## Showcase

<p align="center">
  <img src="raw/showcase-thaumonomicon.jpg" width="48%" alt="Thaumonomicon research tree">
  <img src="raw/showcase-research.jpg" width="48%" alt="Research system">
  <br>
  <img src="raw/showcase-world.jpg" width="80%" alt="In-game showcase">
</p>

## Status

The port is approximately 90% complete and remains under active development. Most gameplay systems are fully functional, with the remaining work focused on finding and fixing hidden bugs.

- Research, scanning, and the Thaumonomicon
- Arcane crafting, crucible alchemy, and infusion
- Aura nodes, wands, foci, and warp
- Golems, essentia storage and transport, and magical devices
- World generation, mobs, taint ecology, and the Outer Lands
- TC4-style client rendering, particles, and native JEI recipe browsing

**Current limitations**
- Some item behavior and edge cases require parity validation
- Infusion gameplay requires broader runtime coverage
- Some models, UVs, display transforms, and renderer routes differ from TC4

## Requirements

Minecraft 1.12.2, Forge 14.23.5.2847, Java 8, and Baubles. JEI 4.16.1.1012 is optional.

## Installation

1. Install Forge and Baubles.
2. Download `Thaumcraft-1.0.0-universal.jar` from the [latest release](https://github.com/0FL01/Thaumcraft-4.2-FOREVA/releases/latest).
3. Place the jar in the Minecraft `mods/` directory.

## Development

```bash
./scripts/dev.sh image && ./scripts/dev.sh build
```

## License

MIT License. See [LICENSE](LICENSE). Thaumcraft 4.2.3.5 is by Azanor.
