# Thaumcraft 4.2.3.5 — Full Aspect Map

> Source of truth: decompiled `Aspect.java` + `ConfigAspects.java` from `Thaumcraft-1.7.10-4.2.3.5.jar`, cross-referenced with the 1.12.2 port.

---

## Table of Contents

1. [Aspect Definitions](#1-aspect-definitions)
2. [Forward Map: Blocks/Items -> Aspects](#2-forward-map-blocksitems---aspects)
3. [Entity Aspect Map](#3-entity-aspect-map)
4. [Reverse Index: Aspect -> Items](#4-reverse-index-aspect---items)
5. [Dynamic Aspect Generation Reference](#5-dynamic-aspect-generation-reference)

---

## 1. Aspect Definitions

48 total aspects: **6 Primal** + **42 Compound**.

### 1.1 Primal Aspects

| Tag | Name | Color | Chat |
|-----|------|-------|------|
| `aer` | Air | `#FFFF7E` | e |
| `terra` | Earth | `#56C000` | 2 |
| `ignis` | Fire | `#FF5A01` | c |
| `aqua` | Water | `#3CD4FC` | 3 |
| `ordo` | Order | `#D5D4EC` | 7 |
| `perditio` | Entropy | `#404040` | 8 |

### 1.2 Compound Aspects — Hierarchical Tree

```
aer (Air)
├── vacuos (Void) = aer + perditio
│   ├── tenebrae (Darkness) = vacuos + lux
│   │   └── alienis (Eldritch) = vacuos + tenebrae
│   ├── praecantatio (Magic) = vacuos + potentia
│   │   ├── auram (Aura) = praecantatio + aer
│   │   └── vitium (Taint) = praecantatio + perditio
│   └── fames (Hunger) = victus + vacuos
│       └── lucrum (Greed) = humanus + fames
├── lux (Light) = aer + ignis
├── tempestas (Weather) = aer + aqua
├── motus (Motion) = aer + ordo
│   ├── volatus (Flight) = aer + motus
│   ├── iter (Travel) = motus + terra
│   ├── bestia (Beast) = motus + victus
│   │   ├── corpus (Flesh) = mortuus + bestia
│   │   └── humanus (Man) = bestia + cognitio
│   │       ├── perfodio (Mine) = humanus + terra
│   │       ├── instrumentum (Tool) = humanus + ordo
│   │       │   ├── meto (Harvest) = messis + instrumentum
│   │       │   ├── telum (Weapon) = instrumentum + ignis
│   │       │   ├── tutamen (Armor) = instrumentum + terra
│   │       │   ├── pannus (Cloth) = instrumentum + bestia
│   │       │   ├── fabrico (Craft) = humanus + instrumentum
│   │       │   └── machina (Mechanism) = motus + instrumentum
│   │       ├── lucrum (Greed) = humanus + fames
│   │       └── messis (Crop) = herba + humanus
│   │           └── meto (Harvest) = messis + instrumentum
│   ├── exanimis (Undead) = motus + mortuus
│   ├── vinculum (Trap) = motus + perditio
│   └── machina (Mechanism) = motus + instrumentum
├── arbor (Tree) = aer + herba
├── sensus (Senses) = aer + spiritus
└── volatus (Flight) = aer + motus

terra (Earth)
├── vitreus (Crystal) = terra + ordo
│   └── metallum (Metal) = terra + vitreus
├── herba (Plant) = victus + terra
│   ├── arbor (Tree) = aer + herba
│   └── messis (Crop) = herba + humanus
├── iter (Travel) = motus + terra
├── tutamen (Armor) = instrumentum + terra
└── perfodio (Mine) = humanus + terra

ignis (Fire)
├── gelum (Cold) = ignis + perditio
├── potentia (Energy) = ordo + ignis
├── cognitio (Mind) = ignis + spiritus
├── lux (Light) = aer + ignis
└── telum (Weapon) = instrumentum + ignis

aqua (Water)
├── victus (Life) = aqua + terra
│   ├── mortuus (Death) = victus + perditio
│   │   ├── spiritus (Soul) = victus + mortuus
│   │   │   ├── cognitio (Mind) = ignis + spiritus
│   │   │   └── sensus (Senses) = aer + spiritus
│   │   ├── exanimis (Undead) = motus + mortuus
│   │   └── corpus (Flesh) = mortuus + bestia
│   ├── sano (Heal) = victus + ordo
│   ├── limus (Slime) = victus + aqua
│   ├── herba (Plant) = victus + terra
│   ├── fames (Hunger) = victus + vacuos
│   └── bestia (Beast) = motus + victus
├── venenum (Poison) = aqua + perditio
├── tempestas (Weather) = aer + aqua
└── limus (Slime) = victus + aqua

ordo (Order)
├── potentia (Energy) = ordo + ignis
├── permutatio (Exchange) = perditio + ordo
├── vitreus (Crystal) = terra + ordo
├── motus (Motion) = aer + ordo
├── sano (Heal) = victus + ordo
└── instrumentum (Tool) = humanus + ordo

perditio (Entropy)
├── vacuos (Void) = aer + perditio
├── gelum (Cold) = ignis + perditio
├── venenum (Poison) = aqua + perditio
├── permutatio (Exchange) = perditio + ordo
├── mortuus (Death) = victus + perditio
├── vitium (Taint) = praecantatio + perditio
├── vinculum (Trap) = motus + perditio
└── mortuus (Death) = victus + perditio
```

### 1.3 Compound Aspect Table

| # | Tag | Name | Color | Components |
|---|-----|------|-------|-----------|
| 1 | `vacuos` | Void | `#888888` | Aer + Perditio |
| 2 | `lux` | Light | `#FFF663` | Aer + Ignis |
| 3 | `tempestas` | Weather | `#FFFFFF` | Aer + Aqua |
| 4 | `motus` | Motion | `#CDCCF4` | Aer + Ordo |
| 5 | `gelum` | Cold | `#E1FFFF` | Ignis + Perditio |
| 6 | `vitreus` | Crystal | `#80FFFF` | Terra + Ordo |
| 7 | `victus` | Life | `#DE0005` | Aqua + Terra |
| 8 | `venenum` | Poison | `#89F000` | Aqua + Perditio |
| 9 | `potentia` | Energy | `#C0FFFF` | Ordo + Ignis |
| 10 | `permutatio` | Exchange | `#578357` | Perditio + Ordo |
| 11 | `metallum` | Metal | `#B5B5CD` | Terra + Vitreus |
| 12 | `mortuus` | Death | `#887788` | Victus + Perditio |
| 13 | `volatus` | Flight | `#E7E7D7` | Aer + Motus |
| 14 | `spiritus` | Soul | `#EBEBFB` | Victus + Mortuus |
| 15 | `sano` | Heal | `#FF2F34` | Victus + Ordo |
| 16 | `iter` | Travel | `#E0585B` | Motus + Terra |
| 17 | `tenebrae` | Darkness | `#222222` | Vacuos + Lux |
| 18 | `alienis` | Eldritch | `#805080` | Vacuos + Tenebrae |
| 19 | `praecantatio` | Magic | `#9700C0` | Vacuos + Potentia |
| 20 | `auram` | Aura | `#FFC0FF` | Praecantatio + Aer |
| 21 | `vitium` | Taint | `#800080` | Praecantatio + Perditio |
| 22 | `limus` | Slime | `#01F800` | Victus + Aqua |
| 23 | `herba` | Plant | `#01AC00` | Victus + Terra |
| 24 | `arbor` | Tree | `#876531` | Aer + Herba |
| 25 | `bestia` | Beast | `#9F6409` | Motus + Victus |
| 26 | `corpus` | Flesh | `#EE478D` | Mortuus + Bestia |
| 27 | `exanimis` | Undead | `#3A4000` | Motus + Mortuus |
| 28 | `cognitio` | Mind | `#FFC2B3` | Ignis + Spiritus |
| 29 | `sensus` | Senses | `#0FD9FF` | Aer + Spiritus |
| 30 | `humanus` | Man | `#FFD7C0` | Bestia + Cognitio |
| 31 | `messis` | Crop | `#E1B371` | Herba + Humanus |
| 32 | `perfodio` | Mine | `#DCD2D8` | Humanus + Terra |
| 33 | `instrumentum` | Tool | `#4040EE` | Humanus + Ordo |
| 34 | `meto` | Harvest | `#EEAD82` | Messis + Instrumentum |
| 35 | `telum` | Weapon | `#C05050` | Instrumentum + Ignis |
| 36 | `tutamen` | Armor | `#00C0C0` | Instrumentum + Terra |
| 37 | `fames` | Hunger | `#9A0305` | Victus + Vacuos |
| 38 | `lucrum` | Greed | `#E6BE44` | Humanus + Fames |
| 39 | `fabrico` | Craft | `#809D80` | Humanus + Instrumentum |
| 40 | `pannus` | Cloth | `#EAEAC2` | Instrumentum + Bestia |
| 41 | `machina` | Mechanism | `#8080A0` | Motus + Instrumentum |
| 42 | `vinculum` | Trap | `#9A8080` | Motus + Perditio |

---

## 2. Forward Map: Blocks/Items -> Aspects

### 2.1 Vanilla Blocks

| Block | Meta | Aspects |
|-------|------|---------|
| Stone | 0 | Terra(3) |
| Stone (Granite) | 1 | Terra(3), Vitreus(1) |
| Stone (Diorite) | 3 | Terra(3), Vitreus(1) |
| Stone (Andesite) | 5 | Terra(3) |
| Cobblestone | Any | Terra(2) |
| Grass Block | 0 | Terra(1), Herba(1) |
| Dirt | Any | Terra(2) |
| Sand | Any | Terra(1), Aer(1) |
| Gravel | Any | Terra(2) |
| Log | Any | Arbor(4) |
| Log2 | Any | Arbor(4) |
| Planks | Any | Arbor(2) |
| Leaves | Any | Herba(2), Aer(1) |
| Leaves2 | Any | Herba(2), Aer(1) |
| Sapling | Any | Herba(2), Arbor(1) |
| Iron Ore | 0 | Terra(2), Metallum(3) |
| Gold Ore | 0 | Terra(2), Metallum(5) |
| Diamond Ore | 0 | Terra(2), Vitreus(6) |
| Redstone Ore | 0 | Terra(2), Potentia(3) |
| Coal Ore | 0 | Terra(2), Potentia(2) |
| Lapis Ore | 0 | Terra(2), Vitreus(3) |
| Emerald Ore | 0 | Terra(2), Vitreus(5) |
| Nether Quartz Ore | 0 | Terra(2), Vitreus(3) |
| Obsidian | 0 | Terra(4), Ignis(2), Tenebrae(2) |
| Glowstone | 0 | Lux(4), Vitreus(2) |
| Ice | 0 | Aqua(2), Gelum(3) |
| Snow | 0 | Aqua(1), Gelum(2) |
| Clay | 0 | Terra(1), Aqua(1) |
| Water | 0 | Aqua(3) |
| Flowing Water | 0 | Aqua(2) |
| Lava | 0 | Ignis(3), Terra(1) |
| Flowing Lava | 0 | Ignis(2) |
| Wool | Any | Herba(1), Instrumentum(1) |
| Cactus | 0 | Herba(2), Aqua(1) |
| Pumpkin | 0 | Herba(2), Victus(1) |
| Melon Block | 0 | Herba(2), Victus(1) |
| Vine | 0 | Herba(2), Motus(1) |
| Waterlily | 0 | Herba(2), Aqua(1) |
| Netherrack | 0 | Terra(1), Ignis(2) |
| Soul Sand | 0 | Terra(1), Spiritus(2), Mortuus(1) |

### 2.2 Vanilla Items — Tools

| Item | Aspects |
|------|---------|
| Iron Pickaxe | Instrumentum(3), Metallum(3) |
| Iron Axe | Instrumentum(3), Metallum(3) |
| Iron Shovel | Instrumentum(2), Metallum(2) |
| Iron Sword | Instrumentum(3), Telum(3), Metallum(3) |
| Iron Hoe | Instrumentum(2), Metallum(2), Meto(1) |
| Diamond Pickaxe | Instrumentum(4), Vitreus(5) |
| Diamond Sword | Instrumentum(4), Telum(4), Vitreus(4) |
| Diamond Axe | Instrumentum(4), Vitreus(4) |
| Diamond Shovel | Instrumentum(3), Vitreus(3) |
| Diamond Hoe | Instrumentum(3), Vitreus(3), Meto(2) |
| Golden Pickaxe | Instrumentum(2), Metallum(4) |
| Golden Sword | Instrumentum(2), Telum(2), Metallum(4) |
| Golden Axe | Instrumentum(2), Metallum(4) |
| Golden Shovel | Instrumentum(1), Metallum(4) |
| Golden Hoe | Instrumentum(1), Metallum(4), Meto(1) |
| Stone Pickaxe | Instrumentum(2), Terra(3) |
| Stone Sword | Instrumentum(2), Telum(2), Terra(3) |
| Stone Axe | Instrumentum(2), Terra(3) |
| Stone Shovel | Instrumentum(1), Terra(2) |
| Stone Hoe | Instrumentum(1), Terra(2), Meto(1) |
| Wooden Pickaxe | Instrumentum(1), Arbor(3) |
| Wooden Sword | Instrumentum(1), Telum(1), Arbor(3) |
| Wooden Axe | Instrumentum(1), Arbor(3) |
| Wooden Shovel | Instrumentum(1), Arbor(2) |
| Wooden Hoe | Instrumentum(1), Arbor(2), Meto(1) |

### 2.3 Vanilla Items — Food

| Item | Aspects |
|------|---------|
| Apple | Herba(2), Victus(1) |
| Bread | Herba(2), Victus(2) |
| Cooked Beef | Bestia(2), Victus(3) |
| Cooked Porkchop | Bestia(2), Victus(3) |
| Cooked Chicken | Bestia(2), Victus(2), Volatus(1) |
| Cooked Fish | Bestia(2), Victus(2), Aqua(1) |

### 2.4 Vanilla Items — Materials & Mob Drops

| Item | Aspects |
|------|---------|
| Iron Ingot | Metallum(4) |
| Gold Ingot | Metallum(6) |
| Diamond | Vitreus(8) |
| Emerald | Vitreus(6), Permutatio(2) |
| Redstone | Potentia(3) |
| Coal | Potentia(2) |
| Stick | Arbor(1) |
| String | Bestia(1), Instrumentum(1) |
| Feather | Bestia(1), Volatus(2) |
| Leather | Bestia(2), Corpus(1) |
| Bone | Exanimis(2), Terra(1) |
| Gunpowder | Ignis(2), Potentia(2), Perditio(1) |
| Ender Pearl | Vacuos(4), Iter(2) |
| Rotten Flesh | Exanimis(2), Corpus(2), Mortuus(1) |
| Spider Eye | Bestia(2), Venenum(3), Sensus(1) |
| Blaze Rod | Ignis(4), Potentia(2), Praecantatio(1) |
| Ghast Tear | Spiritus(4), Ignis(2), Sensus(1) |
| Magma Cream | Ignis(3), Limus(2) |
| Slime Ball | Limus(3), Victus(1) |
| Glass Bottle | Vitreus(1), Vacuos(1) |
| Nether Wart | Herba(2), Praecantatio(2), Ignis(1) |
| Golden Carrot | Herba(2), Metallum(4), Sensus(2) |
| Speckled Melon | Herba(2), Metallum(4), Sano(2) |
| Book | Arbor(2), Cognitio(2) |
| Paper | Arbor(1), Cognitio(1) |
| Flint | Terra(1), Instrumentum(1) |
| Bucket | Metallum(3), Vacuos(1) |

### 2.5 Vanilla Utility & Mechanism

| Item/Block | Method | Aspects |
|-----------|--------|---------|
| Bow | COMPLEX | Telum(1) |
| Cake | COMPLEX | Fabrico(2), Aqua(2) |
| Minecart | COMPLEX | Machina(2), Iter(4) |
| Boat | COMPLEX | Aqua(4), Iter(4) |
| Repeater | COMPLEX | Machina(2) |
| Compass | COMPLEX | Sensus(2) |
| Clock | COMPLEX | Sensus(4), Aer(4) |
| Dispenser | COMPLEX | Machina(1) |
| Rail | COMPLEX | Metallum(1), Iter(1) |
| Daylight Detector | COMPLEX | Sensus(4), Machina(2), Aer(4) |
| Redstone Torch | COMPLEX | Machina(1), Potentia(1) |
| Redstone Lamp | COMPLEX | Sensus(2), Lux(3), Machina(3) |
| Torch | COMPLEX | Lux(1) |
| Fire | COMPLEX | Ignis(4) |
| Crafting Table | Object | Fabrico(4) |
| Enchanting Table | COMPLEX | Auram(2), Praecantatio(2), Permutatio(2) |
| Anvil | COMPLEX | Metallum(64), Fabrico(2), Instrumentum(2) |
| Piston | COMPLEX | Machina(2), Motus(4) |
| Sticky Piston | COMPLEX | Machina(2), Motus(4) |
| Ender Chest | COMPLEX | Permutatio(2), Iter(2), Vacuos(4) |
| Hopper | COMPLEX | Machina(1), Permutatio(1), Vacuos(1) |
| Dropper | COMPLEX | Machina(1), Permutatio(1), Vacuos(1) |
| Trapped Chest | COMPLEX | Machina(1), Permutatio(1), Vacuos(1) |
| Beacon | Object | Sensus(2), Lux(3), Machina(3) |

### 2.6 OreDictionary Registrations

| Key | Condition | Aspects |
|-----|-----------|---------|
| `ingotCopper` | foundCopper | Metallum(3) |
| `nuggetCopper` | foundCopper | Metallum(1) |
| `dustCopper` | foundCopper | Metallum(2), Perditio(1) |
| `ingotTin` | foundTin | Metallum(3) |
| `nuggetTin` | foundTin | Metallum(1) |
| `dustTin` | foundTin | Metallum(2), Perditio(1) |
| `ingotSilver` | foundSilver | Metallum(4) |
| `nuggetSilver` | foundSilver | Metallum(1) |
| `dustSilver` | foundSilver | Metallum(2), Perditio(1) |
| `ingotLead` | foundLead | Metallum(3) |
| `nuggetLead` | foundLead | Metallum(1) |
| `dustLead` | foundLead | Metallum(2), Perditio(1) |
| `oreCopper` | foundCopperOre | Terra(2), Metallum(3) |
| `oreTin` | foundTinOre | Terra(2), Metallum(3) |
| `oreSilver` | foundSilverOre | Terra(2), Metallum(4) |
| `oreLead` | foundLeadOre | Terra(2), Metallum(3) |
| `stone` | always | Terra(2) |
| `cobblestone` | always | Terra(1), Perditio(1) |
| `nuggetIron` | always | Metallum(1) |
| `oreIron` | always | Terra(1), Metallum(3) |
| `dustIron` | always | Metallum(3), Perditio(1) |
| `oreGold` | always | Terra(1), Metallum(2), Lucrum(1) |
| `dustGold` | always | Metallum(2), Perditio(1), Lucrum(1) |
| `oreLapis` | always | Terra(1), Sensus(3) |
| `oreDiamond` | always | Terra(1), Lucrum(3), Vitreus(3) |
| `gemDiamond` | always | Vitreus(4), Lucrum(4) |
| `oreRedstone` | always | Terra(1), Potentia(2), Machina(2) |
| `oreEmerald` | always | Terra(1), Lucrum(4), Vitreus(3) |
| `gemEmerald` | always | Vitreus(4), Lucrum(5) |
| `oreQuartz` | always | Terra(1), Vitreus(3) |
| `dustRedstone` | always | Potentia(2), Machina(1) |
| `dustGlowstone` | always | Sensus(1), Lux(2) |
| `treeSapling` | always | Herba(2), Arbor(1) |
| `treeLeaves` | always | Herba(2), Aer(1) |
| `logWood` | always | Arbor(4) |
| `plankWood` | always | Arbor(2) |
| `slabWood` | always | Arbor(1) |
| `stairWood` | always | Arbor(1) |
| `stickWood` | always | Arbor(1) |
| `blockGlass` | always | Vitreus(2) |
| `paneGlass` | always | Vitreus(1) |
| `blockWool` | always | Herba(2), Instrumentum(1) |
| `dyeBlack`..`dyeWhite` (16) | always | Sensus(1) |

### 2.7 Thaumcraft — Shards & Vis

| Item | Meta | Aspects |
|------|------|---------|
| Air Shard | 0 | Praecantatio(1), Aer(2), Vitreus(1) |
| Fire Shard | 1 | Praecantatio(1), Ignis(2), Vitreus(1) |
| Water Shard | 2 | Praecantatio(1), Aqua(2), Vitreus(1) |
| Earth Shard | 3 | Praecantatio(1), Terra(2), Vitreus(1) |
| Order Shard | 4 | Praecantatio(1), Ordo(2), Vitreus(1) |
| Entropy Shard | 5 | Praecantatio(1), Perditio(2), Vitreus(1) |
| Balanced Shard | 6 | Aer(2), Ignis(2), Aqua(2), Terra(2), Ordo(2), Perditio(2), Vitreus(1) |
| itemResource 14 | 14 | (Balanced Shard clone) + Praecantatio(2), -Vitreus |

### 2.8 Thaumcraft — Nuggets

| Item | Meta | Aspects |
|------|------|---------|
| Nugget | 5 | Metallum(1) |
| Nugget | 6 | Metallum(1) |
| Nugget (Thaumium) | 16 | Ordo(1), Metallum(6), Terra(1) |
| Nugget (Void) | 31 | Ordo(1), Metallum(4), Terra(1), Lucrum(2) |
| Nugget (Admixed) | 21 | Ordo(1), Metallum(4), Terra(1), Permutatio(4), Venenum(2) |
| NuggetEdible (all) | 0-3 | Fames(1) |
| Triple Meat Treat | WILDCARD | Sano(1), -Fames(1) |

### 2.9 Thaumcraft — Custom Ores (blockCustomOre)

| Meta | Aspects |
|------|---------|
| 0 (Cinnabar) | Terra(1), Metallum(2), Permutatio(2), Venenum(1) |
| 1 (Amber) | Terra(1), Aer(3), Vitreus(2) |
| 2 | Terra(1), Ignis(3), Vitreus(2) |
| 3 | Terra(1), Aqua(3), Vitreus(2) |
| 4 | Terra(1), Terra(3), Vitreus(2) |
| 5 | Terra(1), Ordo(3), Vitreus(2) |
| 6 | Terra(1), Perditio(3), Vitreus(2) |
| 7 | Terra(1), Vinculum(3), Vitreus(2) |

### 2.10 Thaumcraft — Taint

| Block | Meta | Aspects |
|-------|------|---------|
| Taint (log) | 0 | Arbor(1), Vitium(3) |
| Taint (soil) | 1 | Terra(1), Vitium(3) |
| Taint Fibres | 0 | Victus(1), Vitium(2) |
| Taint Fibres | 1 | Herba(1), Vitium(1) |
| Taint Fibres | 2 | Herba(1), Vitium(1) |
| Taint Fibres | 3 | Bestia(1), Herba(1), Vitium(2) |
| Taint Fibres | 4 | Bestia(1), Herba(1), Vitium(2) |

### 2.11 Thaumcraft — Magic Wood & Stone

| Block | Meta | Aspects |
|-------|------|---------|
| Greatwood Log | 0 | Arbor(3), Praecantatio(1) |
| Silverwood Log | 1 | Arbor(3), Praecantatio(1), Ordo(1) |
| Greatwood Leaves | 0 | Herba(1) |
| Silverwood Leaves | 1 | Herba(1) |
| Obsidian Tile | 0 | Terra(4), Tenebrae(2), Alienis(2) |
| Arcane Stone | 6 | Terra(1), Praecantatio(1) |
| Arcane Brick | 7 | Terra(1), Praecantatio(1) |
| Arcane Stone Slab | 4 | Metallum(8), Praecantatio(2) |
| Candle White | 5 | Corpus(4), Lux(1), Praecantatio(1) |
| Pedestal (Ancient) | 11 | Terra(1), Alienis(1) |
| Pedestal (Arcane) | 12 | Terra(1), Alienis(1) |

### 2.12 Thaumcraft — Devices & Machinery

| Block/Item | Meta | Aspects |
|-----------|------|---------|
| blockMetalDevice | 0 | Metallum(4), Fabrico(4), Praecantatio(4) |
| blockCandle | 0 | Lux(2), Corpus(1), Praecantatio(1) |
| blockAiry (Nitor) | 2 | Lux(1) |
| blockAiry (Nitor) | 3 | Lux(1) |
| Arcane Furnace | WILDCARD | Praecantatio(8), Aqua(8), Fabrico(8) |
| Wooden Device (Arcane Levitator) | 1 | COMPLEX — Sensus(4) |
| Wooden Device (Crimson Cloth) | 8 | Alienis(1), Arbor(2), Pannus(3) |
| blockTable | 0 | Arbor(4), Fabrico(2) |
| Goggles of Revealing | WILDCARD | COMPLEX — Sensus(4) |
| Thaumometer | 0 | Sensus(3), Metallum(2), Vitreus(1), Praecantatio(1) |
| Inkwell | 0 | Aqua(1), Tenebrae(1), Instrumentum(1) |
| Primal Arrow | 0 | COMPLEX — Telum(1) |
| Pech Focus | 0 | Praecantatio(5), Venenum(5), Perditio(5), Alienis(5), Telum(5) |

### 2.13 Thaumcraft — Plants

| Block | Meta | Aspects |
|-------|------|---------|
| Greatwood Sapling | 0 | Herba(2), Arbor(1), Praecantatio(1) |
| Silverwood Sapling | 1 | Herba(2), Arbor(1), Praecantatio(1) |
| Ethereal Bloom | 2 | Herba(2), Permutatio(2), Praecantatio(2) |
| Cinderpearl | 3 | Herba(2), Ignis(2), Praecantatio(2) |
| Shimmerleaf | 5 | Herba(2), Venenum(1), Praecantatio(2) |

### 2.14 Thaumcraft — Essences & Resources

| Item | Meta | Aspects |
|------|------|---------|
| Alchemical Salts | 0 | Vacuos(1) |
| Alumentum | 1 | (empty) |
| Wisp Essence | 0 | Auram(2) |
| Crystal Essence | 0 | (empty) |
| Thaumonomicon | WILDCARD | Arbor(2), Cognitio(4), Praecantatio(2) |
| Quicksilver | 3 | Metallum(3), Venenum(1), Permutatio(2) |
| Amber | 6 | Vinculum(2), Vitreus(2) |
| Zombie Brain (resource) | 9 | Cognitio(8) |
| Vitreous | 11 | Vitium(3), Limus(1) |
| Tallow | 12 | Vitium(2), Lucrum(1), Fames(1) |
| Golden Pearl | 18 | Lucrum(1) |
| Zombie Brain (item) | 0 | Corpus(2), Cognitio(4), Exanimis(2) |
| Loot Bag (Common) | 0 | Lucrum(8) |
| Loot Bag (Uncommon) | 1 | Lucrum(16) |
| Loot Bag (Rare) | 2 | Lucrum(32) |

### 2.15 Thaumcraft — Baubles

| Item | Meta | Aspects |
|------|------|---------|
| Blank Ring | 0 | Praecantatio(2), Metallum(1) |
| Blank Amulet | 1 | Praecantatio(2), Lucrum(1) |
| Blank Girdle | 2 | Praecantatio(2), Humanus(1) |
| Blank Tablet | 3 | Praecantatio(5) |

### 2.16 Thaumcraft — Eldritch Items

| Item | Meta | Aspects |
|------|------|---------|
| Primordial Pearl | 0 | Alienis(5), Auram(3), Praecantatio(3), Sensus(3), Spiritus(3) |
| Crimson Rites | 1 | Cognitio(5), Praecantatio(3), Alienis(3), Spiritus(3) |
| Crimson Portal | 2 | Vinculum(4), Cognitio(4), Machina(4) |
| Nether Brain | 3 | Aer(16), Terra(16), Ignis(16), Aqua(16), Ordo(16), Perditio(16) |

### 2.17 Thaumcraft — Eldritch Blocks

| Block | Meta | Aspects |
|-------|------|---------|
| blockEldritch (any) | WILDCARD | Vacuos(8), Alienis(8), Sensus(4) |
| Eldritch Portal | 0 | Vacuos(8), Alienis(8), Iter(8) |
| blockEldritch | 3 | Vacuos(4), Alienis(4) |
| blockEldritch | 4 | Lux(1), Terra(1), Alienis(1) |
| blockEldritch | 5 | Cognitio(2), Terra(1), Alienis(1) |
| blockEldritch | 6 | Metallum(2), Machina(2), Alienis(1) |

### 2.18 Thaumcraft — Cultist Gear

| Item | Meta | Aspects |
|------|------|---------|
| Cultist Plate | WILDCARD | Metallum(5), Alienis(1) |
| Cultist Robe | WILDCARD | Metallum(3), Pannus(2), Alienis(1) |
| Cultist Leader | WILDCARD | Metallum(5), Alienis(2) |
| Cultist Boots | WILDCARD | Metallum(4), Alienis(1) |

### 2.19 Thaumcraft — Thaumium Gear

| Item | Aspects |
|------|---------|
| Thaumium Helmet | Metallum(10), Tutamen(6), Praecantatio(2) |
| Thaumium Chestplate | Metallum(14), Tutamen(8), Praecantatio(2) |
| Thaumium Leggings | Metallum(12), Tutamen(7), Praecantatio(2) |
| Thaumium Boots | Metallum(8), Tutamen(5), Praecantatio(2) |
| Thaumium Sword | Metallum(8), Telum(5), Praecantatio(2) |
| Thaumium Pickaxe | Metallum(8), Instrumentum(5), Praecantatio(2) |
| Thaumium Axe | Metallum(8), Instrumentum(5), Praecantatio(2) |
| Thaumium Shovel | Metallum(6), Instrumentum(4), Praecantatio(2) |
| Thaumium Hoe | Metallum(6), Instrumentum(4), Praecantatio(2) |

### 2.20 Thaumcraft — Void Gear

| Item | Aspects |
|------|---------|
| Void Helmet | Metallum(10), Tutamen(6), Vacuos(3), Praecantatio(2) |
| Void Chestplate | Metallum(14), Tutamen(8), Vacuos(4), Praecantatio(2) |
| Void Leggings | Metallum(12), Tutamen(7), Vacuos(3), Praecantatio(2) |
| Void Boots | Metallum(8), Tutamen(5), Vacuos(2), Praecantatio(2) |
| Void Sword | Metallum(8), Telum(5), Vacuos(2), Praecantatio(2) |
| Void Pickaxe | Metallum(8), Instrumentum(5), Vacuos(2), Praecantatio(2) |
| Void Axe | Metallum(8), Instrumentum(5), Vacuos(2), Praecantatio(2) |
| Void Shovel | Metallum(6), Instrumentum(4), Vacuos(1), Praecantatio(2) |
| Void Hoe | Metallum(6), Instrumentum(4), Vacuos(1), Praecantatio(2) |

### 2.21 Thaumcraft — Special Nuggets (Mod Integration)

These are registered conditionally when other mods add copper/tin/silver/lead:

| Item | Condition | Aspects |
|------|-----------|---------|
| itemNugget meta 17 | foundCopperOre | Ordo(1), Metallum(5), Terra(1), Permutatio(2) |
| itemNugget meta 18 | foundTinOre | Ordo(1), Metallum(5), Terra(1), Vitreus(2) |
| itemNugget meta 19 | foundSilverOre | Ordo(1), Metallum(5), Terra(1), Lucrum(2) |
| itemNugget meta 20 | foundLeadOre | Ordo(1), Metallum(5), Terra(1), Ordo(2) |

---

## 3. Entity Aspect Map

### 3.1 Vanilla Entities

| Entity ID | Aspect Aspects |
|-----------|--------------|
| `minecraft:zombie` | Exanimis(2), Humanus(1), Terra(1) |
| `minecraft:giant` | Exanimis(4), Humanus(3), Terra(3) |
| `minecraft:skeleton` | Exanimis(3), Humanus(1), Terra(1) |
| `minecraft:wither_skeleton` | Exanimis(4), Humanus(1), Ignis(2) |
| `minecraft:creeper` | Herba(2), Ignis(2) |
| `minecraft:creeper` (charged) | Herba(3), Ignis(3), Potentia(3) |
| `minecraft:horse` | Bestia(4), Terra(1), Aer(1) |
| `minecraft:pig` | Bestia(2), Terra(2) |
| `minecraft:xp_orb` | Cognitio(5) |
| `minecraft:sheep` | Bestia(2), Terra(2) |
| `minecraft:cow` | Bestia(3), Terra(3) |
| `minecraft:mooshroom` | Bestia(3), Herba(1), Terra(2) |
| `minecraft:snowman` | Gelum(3), Aqua(1) |
| `minecraft:ocelot` | Bestia(3), Perditio(3) |
| `minecraft:chicken` | Bestia(2), Volatus(2), Aer(1) |
| `minecraft:squid` | Bestia(2), Aqua(2) |
| `minecraft:wolf` | Bestia(3), Terra(3) |
| `minecraft:bat` | Bestia(1), Volatus(1), Aer(1) |
| `minecraft:spider` | Bestia(3), Perditio(2) |
| `minecraft:slime` | Limus(2), Aqua(2) |
| `minecraft:ghast` | Exanimis(3), Ignis(2) |
| `minecraft:zombie_pigman` | Exanimis(4), Ignis(2) |
| `minecraft:enderman` | Alienis(4), Iter(2), Aer(2) |
| `minecraft:cave_spider` | Bestia(2), Venenum(2), Terra(1) |
| `minecraft:silverfish` | Bestia(1), Terra(1) |
| `minecraft:blaze` | Alienis(4), Ignis(1) |
| `minecraft:magma_cube` | Limus(3), Ignis(2) |
| `minecraft:ender_dragon` | Alienis(20), Bestia(20), Perditio(20) |
| `minecraft:wither` | Exanimis(20), Perditio(20), Ignis(15) |
| `minecraft:witch` | Humanus(3), Praecantatio(2), Ignis(1) |
| `minecraft:villager` | Humanus(3), Aer(2) |
| `minecraft:villager_golem` | Metallum(4), Terra(3) |
| `minecraft:minecart` | Machina(3), Aer(2) |
| `minecraft:chest_minecart` | Machina(3), Aer(1), Vacuos(1) |
| `minecraft:furnace_minecart` | Machina(3), Aer(1), Ignis(1) |
| `minecraft:tnt_minecart` | Machina(3), Aer(1), Ignis(1) |
| `minecraft:hopper_minecart` | Machina(3), Aer(1), Permutatio(1) |
| `minecraft:spawner_minecart` | Machina(3), Aer(1), Praecantatio(1) |
| `minecraft:ender_crystal` | Alienis(3), Praecantatio(3), Sano(3) |
| `minecraft:item_frame` | Sensus(3), Pannus(1) |
| `minecraft:painting` | Sensus(5), Pannus(3) |

### 3.2 Thaumcraft Entities

| Entity ID | NBT | Aspects |
|-----------|-----|---------|
| `thaumcraft:primal_orb` | — | Aer(5), Perditio(10), Praecantatio(10), Potentia(10) |
| `thaumcraft:firebat` | — | Bestia(2), Volatus(1), Ignis(2) |
| `thaumcraft:pech` | PechType=0 (Greed) | Humanus(2), Praecantatio(2), Permutatio(2), Lucrum(2) |
| `thaumcraft:pech` | PechType=1 (Weapons) | Humanus(2), Praecantatio(2), Permutatio(2), Telum(2) |
| `thaumcraft:pech` | PechType=2 (Magic) | Humanus(2), Praecantatio(4), Permutatio(2) |
| `thaumcraft:thaum_slime` | — | Limus(2), Praecantatio(1), Aqua(1) |
| `thaumcraft:brainy_zombie` | — | Exanimis(3), Humanus(1), Cognitio(1), Terra(1) |
| `thaumcraft:giant_brainy_zombie` | — | Exanimis(4), Humanus(2), Cognitio(1), Terra(2) |
| `thaumcraft:taintacle` | — | Vitium(3), Aqua(2) |
| `thaumcraft:taintacle_tiny` | — | Vitium(1), Aqua(1) |
| `thaumcraft:taint_spider` | — | Vitium(1), Terra(1) |
| `thaumcraft:taint_spore` | — | Vitium(2), Aer(2) |
| `thaumcraft:taint_swarmer` | — | Vitium(2), Aer(2) |
| `thaumcraft:taint_swarm` | — | Vitium(3), Aer(3) |
| `thaumcraft:tainted_pig` | — | Vitium(2), Terra(2) |
| `thaumcraft:tainted_sheep` | — | Vitium(2), Terra(2) |
| `thaumcraft:tainted_cow` | — | Vitium(3), Terra(3) |
| `thaumcraft:tainted_chicken` | — | Vitium(2), Volatus(2), Aer(1) |
| `thaumcraft:tainted_villager` | — | Vitium(3), Aer(2) |
| `thaumcraft:tainted_creeper` | — | Vitium(2), Ignis(2) |
| `thaumcraft:mind_spider` | — | Vitium(2), Ignis(2) |
| `thaumcraft:eldritch_guardian` | — | Alienis(4), Mortuus(2), Exanimis(4) |
| `thaumcraft:eldritch_orb` | — | Alienis(2), Mortuus(2) |
| `thaumcraft:cultist_knight` | — | Alienis(1), Humanus(2), Perditio(1) |
| `thaumcraft:cultist_cleric` | — | Alienis(1), Humanus(2), Perditio(1) |
| `thaumcraft:wisp` | Type=<aspect> | {aspect}(2), Praecantatio(1), Aer(1) |
| `thaumcraft:golem` | — | Aer(2), Terra(2), Praecantatio(2) |

---

## 4. Reverse Index: Aspect -> Items

### Aer (Air)

- Sand — Terra(1), Aer(1)
- Leaves — Herba(2), Aer(1)
- Leaves2 — Herba(2), Aer(1)
- Air Shard — Praecantatio(1), Aer(2), Vitreus(1)
- Balanced Shard — Aer(2), ... (6 primals)
- Amber blockCustomOre meta 1 — Terra(1), Aer(3), Vitreus(2)
- Clock — Sensus(4), Aer(4)
- Daylight Detector — Sensus(4), Machina(2), Aer(4)
- Nether Brain — Aer(16), Terra(16), Ignis(16), Aqua(16), Ordo(16), Perditio(16)
- **Entity: horse** — Bestia(4), Terra(1), Aer(1)
- **Entity: chicken** — Bestia(2), Volatus(2), Aer(1)
- **Entity: bat** — Bestia(1), Volatus(1), Aer(1)
- **Entity: enderman** — Alienis(4), Iter(2), Aer(2)
- **Entity: minecraft:minecart** — Machina(3), Aer(2)
- **Entity: chest_minecart** — Machina(3), Aer(1), Vacuos(1)
- **Entity: furnace_minecart** — Machina(3), Aer(1), Ignis(1)
- **Entity: tnt_minecart** — Machina(3), Aer(1), Ignis(1)
- **Entity: hopper_minecart** — Machina(3), Aer(1), Permutatio(1)
- **Entity: spawner_minecart** — Machina(3), Aer(1), Praecantatio(1)
- **Entity: villager** — Humanus(3), Aer(2)
- **Entity: primal_orb** — Aer(5), Perditio(10), Praecantatio(10), Potentia(10)
- **Entity: taint_spore** — Vitium(2), Aer(2)
- **Entity: taint_swarmer** — Vitium(2), Aer(2)
- **Entity: taint_swarm** — Vitium(3), Aer(3)
- **Entity: tainted_chicken** — Vitium(2), Volatus(2), Aer(1)
- **Entity: tainted_villager** — Vitium(3), Aer(2)
- **Entity: wisp** — any aspect(2), Praecantatio(1), Aer(1)
- **Entity: golem** — Aer(2), Terra(2), Praecantatio(2)

### Aqua (Water)

- Ice — Aqua(2), Gelum(3)
- Snow — Aqua(1), Gelum(2)
- Clay — Terra(1), Aqua(1)
- Water — Aqua(3)
- Flowing Water — Aqua(2)
- Cactus — Herba(2), Aqua(1)
- Waterlily — Herba(2), Aqua(1)
- Water Shard — Praecantatio(1), Aqua(2), Vitreus(1)
- Balanced Shard — Aer(2), ... Aqua(2), ... (6 primals)
- blockCustomOre meta 3 — Terra(1), Aqua(3), Vitreus(2)
- Cake — Fabrico(2), Aqua(2)
- Boat — Aqua(4), Iter(4)
- Arcane Furnace — Praecantatio(8), Aqua(8), Fabrico(8)
- Inkwell — Aqua(1), Tenebrae(1), Instrumentum(1)
- Cooked Fish — Bestia(2), Victus(2), Aqua(1)
- Nether Brain — Aer(16), ..., Aqua(16), ...
- **Entity: snowman** — Gelum(3), Aqua(1)
- **Entity: squid** — Bestia(2), Aqua(2)
- **Entity: slime** — Limus(2), Aqua(2)
- **Entity: thaum_slime** — Limus(2), Praecantatio(1), Aqua(1)
- **Entity: taintacle** — Vitium(3), Aqua(2)
- **Entity: taintacle_tiny** — Vitium(1), Aqua(1)

### Arbor (Tree)

- Log — Arbor(4)
- Log2 — Arbor(4)
- Planks — Arbor(2)
- Sapling — Herba(2), Arbor(1)
- Stick — Arbor(1)
- Wooden Pickaxe — Instrumentum(1), Arbor(3)
- Wooden Sword — Instrumentum(1), Telum(1), Arbor(3)
- Wooden Axe — Instrumentum(1), Arbor(3)
- Wooden Shovel — Instrumentum(1), Arbor(2)
- Wooden Hoe — Instrumentum(1), Arbor(2), Meto(1)
- Book — Arbor(2), Cognitio(2)
- Paper — Arbor(1), Cognitio(1)
- Thaumonomicon — Arbor(2), Cognitio(4), Praecantatio(2)
- Greatwood Log — Arbor(3), Praecantatio(1)
- Silverwood Log — Arbor(3), Praecantatio(1), Ordo(1)
- Greatwood Sapling — Herba(2), Arbor(1), Praecantatio(1)
- Silverwood Sapling — Herba(2), Arbor(1), Praecantatio(1)
- Table — Arbor(4), Fabrico(2)
- Crimson Cloth blockWoodenDevice meta 8 — Alienis(1), Arbor(2), Pannus(3)
- Taint (log) — Arbor(1), Vitium(3)
- **OreDict:** `logWood`, `plankWood`, `slabWood`, `stairWood`, `stickWood`, `treeSapling`

### Auram (Aura)

- Wisp Essence — Auram(2)
- Enchanting Table — Auram(2), Praecantatio(2), Permutatio(2)
- Primordial Pearl — Alienis(5), Auram(3), ...

### Alienis (Eldritch)

- Obsidian — Terra(4), Ignis(2), Tenebrae(2)
- Obsidian Tile blockCosmeticSolid meta 0 — Terra(4), Tenebrae(2), Alienis(2)
- Pech Focus — Praecantatio(5), Venenum(5), Perditio(5), Alienis(5), Telum(5)
- Primordial Pearl — Alienis(5), Auram(3), Praecantatio(3), Sensus(3), Spiritus(3)
- Crimson Rites — Cognitio(5), Praecantatio(3), Alienis(3), Spiritus(3)
- Cultist Plate — Metallum(5), Alienis(1)
- Cultist Robe — Metallum(3), Pannus(2), Alienis(1)
- Cultist Leader — Metallum(5), Alienis(2)
- Cultist Boots — Metallum(4), Alienis(1)
- Crimson Cloth blockWoodenDevice meta 8 — Alienis(1), Arbor(2), Pannus(3)
- blockEldritch (any) — Vacuos(8), Alienis(8), Sensus(4)
- Eldritch Portal — Vacuos(8), Alienis(8), Iter(8)
- blockEldritch meta 3 — Vacuos(4), Alienis(4)
- blockEldritch meta 4 — Lux(1), Terra(1), Alienis(1)
- blockEldritch meta 5 — Cognitio(2), Terra(1), Alienis(1)
- blockEldritch meta 6 — Metallum(2), Machina(2), Alienis(1)
- Pedestal blockCosmeticSolid meta 11/12 — Terra(1), Alienis(1)
- **Entity: enderman** — Alienis(4), Iter(2), Aer(2)
- **Entity: blaze** — Alienis(4), Ignis(1)
- **Entity: ender_dragon** — Alienis(20), Bestia(20), Perditio(20)
- **Entity: ender_crystal** — Alienis(3), Praecantatio(3), Sano(3)
- **Entity: eldritch_guardian** — Alienis(4), Mortuus(2), Exanimis(4)
- **Entity: eldritch_orb** — Alienis(2), Mortuus(2)
- **Entity: cultist_knight** — Alienis(1), Humanus(2), Perditio(1)
- **Entity: cultist_cleric** — Alienis(1), Humanus(2), Perditio(1)

### Bestia (Beast)

- String — Bestia(1), Instrumentum(1)
- Feather — Bestia(1), Volatus(2)
- Leather — Bestia(2), Corpus(1)
- Spider Eye — Bestia(2), Venenum(3), Sensus(1)
- Cooked Beef — Bestia(2), Victus(3)
- Cooked Porkchop — Bestia(2), Victus(3)
- Cooked Chicken — Bestia(2), Victus(2), Volatus(1)
- Cooked Fish — Bestia(2), Victus(2), Aqua(1)
- Taint Fibres meta 3/4 — Bestia(1), Herba(1), Vitium(2)
- **Entity:** horse, pig, sheep, cow, mooshroom, ocelot, chicken, squid, wolf, bat, spider, cave_spider, silverfish
- **Entity:** firebat, ender_dragon
- **OreDict:** `blockWool` — bestia is not directly, but tool+plant matching uses beast-related

### Cognitio (Mind)

- Book — Arbor(2), Cognitio(2)
- Paper — Arbor(1), Cognitio(1)
- Thaumonomicon — Arbor(2), Cognitio(4), Praecantatio(2)
- Zombie Brain (resource meta 9) — Cognitio(8)
- Zombie Brain (item) — Corpus(2), Cognitio(4), Exanimis(2)
- Crimson Rites — Cognitio(5), Praecantatio(3), Alienis(3), Spiritus(3)
- Crimson Portal — Vinculum(4), Cognitio(4), Machina(4)
- blockEldritch meta 5 — Cognitio(2), Terra(1), Alienis(1)
- **Entity: xp_orb** — Cognitio(5)
- **Entity: brainy_zombie** — Exanimis(3), Humanus(1), Cognitio(1), Terra(1)
- **Entity: giant_brainy_zombie** — Exanimis(4), Humanus(2), Cognitio(1), Terra(2)

### Corpus (Flesh)

- Leather — Bestia(2), Corpus(1)
- Rotten Flesh — Exanimis(2), Corpus(2), Mortuus(1)
- Zombie Brain (item) — Corpus(2), Cognitio(4), Exanimis(2)
- Candle (white) — Corpus(4), Lux(1), Praecantatio(1)
- Candle — Lux(2), Corpus(1), Praecantatio(1)

### Fabrico (Craft)

- Crafting Table — Fabrico(4)
- Cake — Fabrico(2), Aqua(2)
- Anvil — Metallum(64), Fabrico(2), Instrumentum(2)
- Arcane Furnace — Praecantatio(8), Aqua(8), Fabrico(8)
- blockMetalDevice — Metallum(4), Fabrico(4), Praecantatio(4)
- Table — Arbor(4), Fabrico(2)

### Fames (Hunger)

- NuggetEdible (all metas) — Fames(1)
- Triple Meat Treat — Sano(1), -Fames(1)
- Tallow — Vitium(2), Lucrum(1), Fames(1)

### Gelum (Cold)

- Ice — Aqua(2), Gelum(3)
- Snow — Aqua(1), Gelum(2)
- **Entity: snowman** — Gelum(3), Aqua(1)

### Herba (Plant)

- Grass Block — Terra(1), Herba(1)
- Leaves — Herba(2), Aer(1)
- Leaves2 — Herba(2), Aer(1)
- Sapling — Herba(2), Arbor(1)
- Wool — Herba(1), Instrumentum(1)
- Cactus — Herba(2), Aqua(1)
- Pumpkin — Herba(2), Victus(1)
- Melon Block — Herba(2), Victus(1)
- Vine — Herba(2), Motus(1)
- Waterlily — Herba(2), Aqua(1)
- Nether Wart — Herba(2), Praecantatio(2), Ignis(1)
- Apple — Herba(2), Victus(1)
- Bread — Herba(2), Victus(2)
- Golden Carrot — Herba(2), Metallum(4), Sensus(2)
- Speckled Melon — Herba(2), Metallum(4), Sano(2)
- Greatwood Leaves — Herba(1)
- Silverwood Leaves — Herba(1)
- Greatwood/Silverwood Sapling — Herba(2), Arbor(1), Praecantatio(1)
- Ethereal Bloom — Herba(2), Permutatio(2), Praecantatio(2)
- Cinderpearl — Herba(2), Ignis(2), Praecantatio(2)
- Shimmerleaf — Herba(2), Venenum(1), Praecantatio(2)
- Taint Fibres meta 1-4 — Herba(1), Vitium(1-2), sometimes Bestia(1)
- **Entity: creeper** — Herba(2), Ignis(2)
- **Entity: mooshroom** — Bestia(3), Herba(1), Terra(2)
- **OreDict:** `treeSapling`, `treeLeaves`, `blockWool`

### Humanus (Man)

- Blank Girdle (Bauble) — Praecantatio(2), Humanus(1)
- **Entity: zombie** — Exanimis(2), Humanus(1), Terra(1)
- **Entity: giant** — Exanimis(4), Humanus(3), Terra(3)
- **Entity: skeleton** — Exanimis(3), Humanus(1), Terra(1)
- **Entity: wither_skeleton** — Exanimis(4), Humanus(1), Ignis(2)
- **Entity: witch** — Humanus(3), Praecantatio(2), Ignis(1)
- **Entity: villager** — Humanus(3), Aer(2)
- **Entity: pech** — Humanus(2), Praecantatio(2), Permutatio(2), ...
- **Entity: brainy_zombie** — Exanimis(3), Humanus(1), Cognitio(1), Terra(1)
- **Entity: giant_brainy_zombie** — Exanimis(4), Humanus(2), Cognitio(1), Terra(2)
- **Entity: cultist_knight** — Alienis(1), Humanus(2), Perditio(1)
- **Entity: cultist_cleric** — Alienis(1), Humanus(2), Perditio(1)

### Ignis (Fire)

- Lava — Ignis(3), Terra(1)
- Flowing Lava — Ignis(2)
- Netherrack — Terra(1), Ignis(2)
- Obsidian — Terra(4), Ignis(2), Tenebrae(2)
- Fire — Ignis(4)
- Gunpowder — Ignis(2), Potentia(2), Perditio(1)
- Blaze Rod — Ignis(4), Potentia(2), Praecantatio(1)
- Ghast Tear — Spiritus(4), Ignis(2), Sensus(1)
- Magma Cream — Ignis(3), Limus(2)
- Nether Wart — Herba(2), Praecantatio(2), Ignis(1)
- Fire Shard — Praecantatio(1), Ignis(2), Vitreus(1)
- Balanced Shard — Aer(2), ..., Ignis(2), ...
- blockCustomOre meta 2 — Terra(1), Ignis(3), Vitreus(2)
- Cinderpearl — Herba(2), Ignis(2), Praecantatio(2)
- Nether Brain — ..., Ignis(16), ...
- **Entity: creeper** — Herba(2), Ignis(2)
- **Entity: ghast** — Exanimis(3), Ignis(2)
- **Entity: zombie_pigman** — Exanimis(4), Ignis(2)
- **Entity: blaze** — Alienis(4), Ignis(1)
- **Entity: magma_cube** — Limus(3), Ignis(2)
- **Entity: witch** — Humanus(3), Praecantatio(2), Ignis(1)
- **Entity: wither** — Exanimis(20), Perditio(20), Ignis(15)
- **Entity: firebat** — Bestia(2), Volatus(1), Ignis(2)
- **Entity: tainted_creeper** — Vitium(2), Ignis(2)
- **Entity: mind_spider** — Vitium(2), Ignis(2)

### Instrumentum (Tool)

- Wool — Herba(1), Instrumentum(1)
- String — Bestia(1), Instrumentum(1)
- Flint — Terra(1), Instrumentum(1)
- All tools (wooden, stone, iron, gold, diamond) — various Instrumentum + material
- Anvil — Metallum(64), Fabrico(2), Instrumentum(2)
- Inkwell — Aqua(1), Tenebrae(1), Instrumentum(1)
- Thaumium/Axe/Shovel/Hoe — Instrumentum(4-5), ...
- Void Shovel — Instrumentum(4), ...
- **OreDict:** `blockWool`

### Iter (Travel)

- Ender Pearl — Vacuos(4), Iter(2)
- Minecart — Machina(2), Iter(4)
- Boat — Aqua(4), Iter(4)
- Rail — Metallum(1), Iter(1)
- Ender Chest — Permutatio(2), Iter(2), Vacuos(4)
- Eldritch Portal — Vacuos(8), Alienis(8), Iter(8)
- **Entity: enderman** — Alienis(4), Iter(2), Aer(2)

### Limus (Slime)

- Magma Cream — Ignis(3), Limus(2)
- Slime Ball — Limus(3), Victus(1)
- Vitreous resource meta 11 — Vitium(3), Limus(1)
- **Entity: slime** — Limus(2), Aqua(2)
- **Entity: magma_cube** — Limus(3), Ignis(2)
- **Entity: thaum_slime** — Limus(2), Praecantatio(1), Aqua(1)

### Lucrum (Greed)

- Loot Bag (Common) — Lucrum(8)
- Loot Bag (Uncommon) — Lucrum(16)
- Loot Bag (Rare) — Lucrum(32)
- Golden Pearl — Lucrum(1)
- Tallow — Vitium(2), Lucrum(1), Fames(1)
- Blank Amulet — Praecantatio(2), Lucrum(1)
- Nugget (Void) meta 31 — Ordo(1), Metallum(4), Terra(1), Lucrum(2)
- **OreDict:** `oreGold` — Terra(1), Metallum(2), Lucrum(1)
- **OreDict:** `dustGold` — Metallum(2), Perditio(1), Lucrum(1)
- **OreDict:** `oreDiamond` — Terra(1), Lucrum(3), Vitreus(3)
- **OreDict:** `gemDiamond` — Vitreus(4), Lucrum(4)
- **OreDict:** `oreEmerald` — Terra(1), Lucrum(4), Vitreus(3)
- **OreDict:** `gemEmerald` — Vitreus(4), Lucrum(5)

### Lux (Light)

- Glowstone — Lux(4), Vitreus(2)
- Torch — Lux(1)
- Redstone Lamp — Sensus(2), Lux(3), Machina(3)
- Beacon — Sensus(2), Lux(3), Machina(3)
- Nitor blockAiry meta 2/3 — Lux(1)
- Candle — Lux(2), Corpus(1), Praecantatio(1)
- Candle (white) — Corpus(4), Lux(1), Praecantatio(1)
- blockEldritch meta 4 — Lux(1), Terra(1), Alienis(1)

### Machina (Mechanism)

- Minecart — Machina(2), Iter(4)
- Repeater — Machina(2)
- Dispenser — Machina(1)
- Daylight Detector — Sensus(4), Machina(2), Aer(4)
- Redstone Torch — Machina(1), Potentia(1)
- Redstone Lamp — Sensus(2), Lux(3), Machina(3)
- Piston — Machina(2), Motus(4)
- Sticky Piston — Machina(2), Motus(4)
- Hopper — Machina(1), Permutatio(1), Vacuos(1)
- Dropper — Machina(1), Permutatio(1), Vacuos(1)
- Trapped Chest — Machina(1), Permutatio(1), Vacuos(1)
- Beacon — Sensus(2), Lux(3), Machina(3)
- Crimson Portal — Vinculum(4), Cognitio(4), Machina(4)
- blockEldritch meta 6 — Metallum(2), Machina(2), Alienis(1)
- **Entity: boat** — Machina(2), Aqua(2)
- **Entity: minecart** — Machina(3), Aer(2)
- **Entity: chest_minecart** — Machina(3), Aer(1), Vacuos(1)
- **Entity: furnace_minecart** — Machina(3), Aer(1), Ignis(1)
- **Entity: tnt_minecart** — Machina(3), Aer(1), Ignis(1)
- **Entity: hopper_minecart** — Machina(3), Aer(1), Permutatio(1)
- **Entity: spawner_minecart** — Machina(3), Aer(1), Praecantatio(1)

### Metallum (Metal)

- Iron Ore — Terra(2), Metallum(3)
- Gold Ore — Terra(2), Metallum(5)
- Iron Ingot — Metallum(4)
- Gold Ingot — Metallum(6)
- Bucket — Metallum(3), Vacuos(1)
- All iron/gold tools — various Instrumentum + Metallum
- Anvil — Metallum(64), Fabrico(2), Instrumentum(2)
- Rail — Metallum(1), Iter(1)
- Golden Carrot — Herba(2), Metallum(4), Sensus(2)
- Speckled Melon — Herba(2), Metallum(4), Sano(2)
- Thaumometer — Sensus(3), Metallum(2), Vitreus(1), Praecantatio(1)
- blockMetalDevice — Metallum(4), Fabrico(4), Praecantatio(4)
- Thaumium/Void armor/tools — Metallum(6-14), ...
- Arcane Stone Slab — Metallum(8), Praecantatio(2)
- Quicksilver — Metallum(3), Venenum(1), Permutatio(2)
- Cinnabar blockCustomOre meta 0 — Terra(1), Metallum(2), ...
- All Nuggets (5,6,16,31,21) — Metallum(1-6)
- blockEldritch meta 6 — Metallum(2), Machina(2), Alienis(1)
- Cultist gear — Metallum(3-5), ...
- **Entity: villager_golem** — Metallum(4), Terra(3)
- **OreDict:** all ingot/nugget/dust/ore entries

### Meto (Harvest)

- Iron Hoe — Instrumentum(2), Metallum(2), Meto(1)
- Diamond Hoe — Instrumentum(3), Vitreus(3), Meto(2)
- Golden Hoe — Instrumentum(1), Metallum(4), Meto(1)
- Stone Hoe — Instrumentum(1), Terra(2), Meto(1)
- Wooden Hoe — Instrumentum(1), Arbor(2), Meto(1)

### Mortuus (Death)

- Soul Sand — Terra(1), Spiritus(2), Mortuus(1)
- Rotten Flesh — Exanimis(2), Corpus(2), Mortuus(1)
- **Entity: eldritch_guardian** — Alienis(4), Mortuus(2), Exanimis(4)
- **Entity: eldritch_orb** — Alienis(2), Mortuus(2)

### Motus (Motion)

- Vine — Herba(2), Motus(1)
- Piston — Machina(2), Motus(4)
- Sticky Piston — Machina(2), Motus(4)

### Ordo (Order)

- Order Shard — Praecantatio(1), Ordo(2), Vitreus(1)
- Balanced Shard — ..., Ordo(2), ...
- Silverwood Log — Arbor(3), Praecantatio(1), Ordo(1)
- blockCustomOre meta 5 — Terra(1), Ordo(3), Vitreus(2)
- Nugget meta 16/31/21 — Ordo(1), ...
- Nether Brain — ..., Ordo(16), ...

### Pannus (Cloth)

- Crimson Cloth blockWoodenDevice meta 8 — Alienis(1), Arbor(2), Pannus(3)
- Cultist Robe — Metallum(3), Pannus(2), Alienis(1)
- **Entity: item_frame** — Sensus(3), Pannus(1)
- **Entity: painting** — Sensus(5), Pannus(3)

### Perditio (Entropy)

- Gunpowder — Ignis(2), Potentia(2), Perditio(1)
- Entropy Shard — Praecantatio(1), Perditio(2), Vitreus(1)
- Balanced Shard — ..., Perditio(2), ...
- blockCustomOre meta 6 — Terra(1), Perditio(3), Vitreus(2)
- Pech Focus — ..., Perditio(5), ...
- Nether Brain — ..., Perditio(16), ...
- **Entity: ocelot** — Bestia(3), Perditio(3)
- **Entity: spider** — Bestia(3), Perditio(2)
- **Entity: ender_dragon** — Alienis(20), Bestia(20), Perditio(20)
- **Entity: wither** — Exanimis(20), Perditio(20), Ignis(15)
- **Entity: primal_orb** — Aer(5), Perditio(10), ...
- **Entity: cultist_knight** — Alienis(1), Humanus(2), Perditio(1)
- **Entity: cultist_cleric** — Alienis(1), Humanus(2), Perditio(1)
- **OreDict:** `cobblestone` — Terra(1), Perditio(1)
- **OreDict:** various `dustX` — Metallum, Perditio(1), ...

### Permutatio (Exchange)

- Emerald — Vitreus(6), Permutatio(2)
- Enchanting Table — Auram(2), Praecantatio(2), Permutatio(2)
- Ender Chest — Permutatio(2), Iter(2), Vacuos(4)
- Hopper — Machina(1), Permutatio(1), Vacuos(1)
- Dropper — Machina(1), Permutatio(1), Vacuos(1)
- Trapped Chest — Machina(1), Permutatio(1), Vacuos(1)
- Quicksilver — Metallum(3), Venenum(1), Permutatio(2)
- Cinnabar blockCustomOre meta 0 — Terra(1), Metallum(2), Permutatio(2), Venenum(1)
- Ethereal Bloom — Herba(2), Permutatio(2), Praecantatio(2)
- Nugget meta 21 — Ordo(1), Metallum(4), Terra(1), Permutatio(4), Venenum(2)
- **Entity: pech** (all types) — ..., Permutatio(2), ...

### Potentia (Energy)

- Redstone — Potentia(3)
- Coal — Potentia(2)
- Redstone Ore — Terra(2), Potentia(3)
- Coal Ore — Terra(2), Potentia(2)
- Gunpowder — Ignis(2), Potentia(2), Perditio(1)
- Blaze Rod — Ignis(4), Potentia(2), Praecantatio(1)
- Redstone Torch — Machina(1), Potentia(1)
- **Entity: creeper** (charged) — Herba(3), Ignis(3), Potentia(3)
- **Entity: primal_orb** — ..., Potentia(10)
- **OreDict:** `oreRedstone`, `dustRedstone`

### Praecantatio (Magic)

- All 6 shards — Praecantatio(1), element(2), Vitreus(1)
- Balanced Shard (via itemResource 14) — (+Praecantatio(2))
- Blaze Rod — Ignis(4), Potentia(2), Praecantatio(1)
- Nether Wart — Herba(2), Praecantatio(2), Ignis(1)
- Thaumometer — Sensus(3), Metallum(2), Vitreus(1), Praecantatio(1)
- Greatwood/Silverwood Log — Arbor(3), Praecantatio(1)
- All Thaumcraft saplings — Herba(2), Arbor(1), Praecantatio(1)
- Ethereal Bloom, Cinderpearl, Shimmerleaf — Herba(2), Praecantatio(2) + element
- Arcane Stone — Terra(1), Praecantatio(1)
- Arcane Stone Slab — Metallum(8), Praecantatio(2)
- blockMetalDevice — Metallum(4), Fabrico(4), Praecantatio(4)
- Arcane Furnace — Praecantatio(8), Aqua(8), Fabrico(8)
- Candle — Lux(2), Corpus(1), Praecantatio(1)
- Candle White — Corpus(4), Lux(1), Praecantatio(1)
- Thaumonomicon — Arbor(2), Cognitio(4), Praecantatio(2)
- Blank Baubles — Praecantatio(2-5)
- Pech Focus — Praecantatio(5), ...
- Thaumium gear — ..., Praecantatio(2)
- Void gear — ..., Praecantatio(2)
- Primordial Pearl, Crimson Rites — ..., Praecantatio(3)
- **Entity: witch** — Humanus(3), Praecantatio(2), Ignis(1)
- **Entity: spawner_minecart** — Machina(3), Aer(1), Praecantatio(1)
- **Entity: ender_crystal** — Alienis(3), Praecantatio(3), Sano(3)
- **Entity: all pech** — Humanus(2), Praecantatio(2-4), ...
- **Entity: thaum_slime** — Limus(2), Praecantatio(1), Aqua(1)
- **Entity: wisp** — {aspect}(2), Praecantatio(1), Aer(1)
- **Entity: golem** — Aer(2), Terra(2), Praecantatio(2)
- **Entity: primal_orb** — ..., Praecantatio(10)

### Sano (Heal)

- Speckled Melon — Herba(2), Metallum(4), Sano(2)
- Triple Meat Treat — Sano(1), -Fames(1)
- **Entity: ender_crystal** — Alienis(3), Praecantatio(3), Sano(3)

### Sensus (Senses)

- Spider Eye — Bestia(2), Venenum(3), Sensus(1)
- Ghast Tear — Spiritus(4), Ignis(2), Sensus(1)
- Golden Carrot — Herba(2), Metallum(4), Sensus(2)
- Compass — Sensus(2)
- Clock — Sensus(4), Aer(4)
- Daylight Detector — Sensus(4), Machina(2), Aer(4)
- Redstone Lamp — Sensus(2), Lux(3), Machina(3)
- Beacon — Sensus(2), Lux(3), Machina(3)
- Thaumometer — Sensus(3), Metallum(2), Vitreus(1), Praecantatio(1)
- Goggles of Revealing — COMPLEX — Sensus(4)
- Arcane Levitator blockWoodenDevice meta 1 — COMPLEX — Sensus(4)
- Primordial Pearl — Alienis(5), Auram(3), Praecantatio(3), Sensus(3), Spiritus(3)
- blockEldritch (any) — Vacuos(8), Alienis(8), Sensus(4)
- **Entity: item_frame** — Sensus(3), Pannus(1)
- **Entity: painting** — Sensus(5), Pannus(3)
- **All OreDict dyes** — Sensus(1)
- **OreDict:** `oreLapis`, `dustGlowstone`

### Spiritus (Soul)

- Soul Sand — Terra(1), Spiritus(2), Mortuus(1)
- Ghast Tear — Spiritus(4), Ignis(2), Sensus(1)
- Primordial Pearl — Alienis(5), Auram(3), Praecantatio(3), Sensus(3), Spiritus(3)
- Crimson Rites — Cognitio(5), Praecantatio(3), Alienis(3), Spiritus(3)

### Telum (Weapon)

- Iron Sword — Instrumentum(3), Telum(3), Metallum(3)
- Diamond Sword — Instrumentum(4), Telum(4), Vitreus(4)
- Golden Sword — Instrumentum(2), Telum(2), Metallum(4)
- Stone Sword — Instrumentum(2), Telum(2), Terra(3)
- Wooden Sword — Instrumentum(1), Telum(1), Arbor(3)
- Bow — COMPLEX — Telum(1)
- Primal Arrow — COMPLEX — Telum(1)
- Pech Focus — ..., Telum(5)
- Thaumium/Void Sword — ..., Telum(5)

### Tenebrae (Darkness)

- Obsidian — Terra(4), Ignis(2), Tenebrae(2)
- Obsidian Tile blockCosmeticSolid meta 0 — Terra(4), Tenebrae(2), Alienis(2)
- Inkwell — Aqua(1), Tenebrae(1), Instrumentum(1)

### Terra (Earth)

- All stone, dirt, sand, gravel variants
- All ores (some Terra)
- All stone-based blocks
- Most Thaumcraft blocks (cosmetic, taint soil, etc.)
- **Entity:** zombies, skeletons, giants, golems, etc.

### Tutamen (Armor)

- Thaumium armor — Metallum(6-14), Tutamen(5-8), Praecantatio(2)
- Void armor — Metallum(6-14), Tutamen(5-8), Vacuos(2-4), Praecantatio(2)

### Vacuos (Void)

- Ender Pearl — Vacuos(4), Iter(2)
- Glass Bottle — Vitreus(1), Vacuos(1)
- Bucket — Metallum(3), Vacuos(1)
- Alchemical Salts — Vacuos(1)
- Ender Chest — Permutatio(2), Iter(2), Vacuos(4)
- Hopper — Machina(1), Permutatio(1), Vacuos(1)
- Dropper — Machina(1), Permutatio(1), Vacuos(1)
- Trapped Chest — Machina(1), Permutatio(1), Vacuos(1)
- Void armor/tools — ..., Vacuos(1-4), ...
- blockEldritch (any) — Vacuos(8), Alienis(8), Sensus(4)
- Eldritch Portal — Vacuos(8), Alienis(8), Iter(8)
- blockEldritch meta 3 — Vacuos(4), Alienis(4)
- **Entity: chest_minecart** — Machina(3), Aer(1), Vacuos(1)

### Venenum (Poison)

- Spider Eye — Bestia(2), Venenum(3), Sensus(1)
- Quicksilver — Metallum(3), Venenum(1), Permutatio(2)
- Cinnabar blockCustomOre meta 0 — Terra(1), Metallum(2), Permutatio(2), Venenum(1)
- Shimmerleaf — Herba(2), Venenum(1), Praecantatio(2)
- Nugget meta 21 — Ordo(1), Metallum(4), Terra(1), Permutatio(4), Venenum(2)
- Pech Focus — Praecantatio(5), Venenum(5), ...
- **Entity: cave_spider** — Bestia(2), Venenum(2), Terra(1)

### Victus (Life)

- Pumpkin — Herba(2), Victus(1)
- Melon Block — Herba(2), Victus(1)
- Apple — Herba(2), Victus(1)
- Bread — Herba(2), Victus(2)
- Cooked Beef — Bestia(2), Victus(3)
- Cooked Porkchop — Bestia(2), Victus(3)
- Cooked Chicken — Bestia(2), Victus(2), Volatus(1)
- Cooked Fish — Bestia(2), Victus(2), Aqua(1)
- Slime Ball — Limus(3), Victus(1)
- Taint Fibres meta 0 — Victus(1), Vitium(2)

### Vinculum (Trap)

- Amber blockCustomOre meta 7 — Terra(1), Vinculum(3), Vitreus(2)
- Amber resource meta 6 — Vinculum(2), Vitreus(2)
- Crimson Portal — Vinculum(4), Cognitio(4), Machina(4)

### Vitium (Taint)

- All taint blocks — Arbor/Terra + Vitium(1-3)
- All taint fibre metas — various + Vitium(1-2)
- Vitreous resource meta 11 — Vitium(3), Limus(1)
- Tallow resource meta 12 — Vitium(2), Lucrum(1), Fames(1)
- **All tainted entities** — Vitium(1-3) + element

### Vitreus (Crystal)

- All stone with CRYSTAL (granite, diorite)
- All ore blocks (Diamond, Emerald, Lapis, Quartz — some Vitreus)
- Glowstone — Lux(4), Vitreus(2)
- Diamond — Vitreus(8)
- Emerald — Vitreus(6), Permutatio(2)
- Glass Bottle — Vitreus(1), Vacuos(1)
- Glass blocks/panes — Vitreus(1-2)
- All shards — ..., Vitreus(1)
- All custom ores — ..., Vitreus(2)
- Thaumometer — ..., Vitreus(1), ...
- Amber resource — Vinculum(2), Vitreus(2)
- **OreDict:** all `gemDiamond`, `gemEmerald`, `oreDiamond`, `oreEmerald`, `oreQuartz`, `blockGlass`, `paneGlass`

### Volatus (Flight)

- Feather — Bestia(1), Volatus(2)
- Cooked Chicken — Bestia(2), Victus(2), Volatus(1)
- **Entity: chicken** — Bestia(2), Volatus(2), Aer(1)
- **Entity: bat** — Bestia(1), Volatus(1), Aer(1)
- **Entity: firebat** — Bestia(2), Volatus(1), Ignis(2)
- **Entity: tainted_chicken** — Vitium(2), Volatus(2), Aer(1)

### Exanimis (Undead)

- Bone — Exanimis(2), Terra(1)
- Rotten Flesh — Exanimis(2), Corpus(2), Mortuus(1)
- Zombie Brain (item) — Corpus(2), Cognitio(4), Exanimis(2)
- **Entity: zombie** — Exanimis(2), Humanus(1), Terra(1)
- **Entity: giant** — Exanimis(4), Humanus(3), Terra(3)
- **Entity: skeleton** — Exanimis(3), Humanus(1), Terra(1)
- **Entity: wither_skeleton** — Exanimis(4), Humanus(1), Ignis(2)
- **Entity: ghast** — Exanimis(3), Ignis(2)
- **Entity: zombie_pigman** — Exanimis(4), Ignis(2)
- **Entity: wither** — Exanimis(20), Perditio(20), Ignis(15)
- **Entity: brainy_zombie** — Exanimis(3), Humanus(1), Cognitio(1), Terra(1)
- **Entity: giant_brainy_zombie** — Exanimis(4), Humanus(2), Cognitio(1), Terra(2)
- **Entity: eldritch_guardian** — Alienis(4), Mortuus(2), Exanimis(4)

---

## 5. Dynamic Aspect Generation Reference

Items NOT explicitly registered in `ConfigAspects` get aspects generated dynamically through several mechanisms.

### 5.1 Recipe-Based Generation

Order (first match wins): Crucible > Arcane > Infusion > Vanilla Crafting.

```
generateTags(item, meta):
  1. Check ThaumcraftApi.objectTags (explicit registrations)
  2. If not found, try recipe-based generation:
     a. Crucible recipe match → sum catalyst aspects + recipe aspect cost
     b. Arcane recipe match → sum ingredient aspects × 0.75 / outputCount + recipe cost
     c. Infusion recipe match → sum center + component aspects × 0.75 / outputCount
     d. Vanilla crafting recipe match → sum ingredient aspects × 0.75 / outputCount
        (picks recipe with lowest visSize to avoid inflation)
  3. Cache result back into ThaumcraftApi.objectTags
```

### 5.2 Potion Effect -> Aspect Map

| Potion Effect | Aspect(s) | Amplifier Mult |
|--------------|-----------|---------------|
| Blindness | Tenebrae ×3 | amp+1 |
| Nausea | Alienis ×3 | amp+1 |
| Strength | Telum ×3 | amp+1 |
| Mining Fatigue, Slowness | Vinculum ×3 | amp+1 |
| Haste | Instrumentum ×3 | amp+1 |
| Fire Resistance | Tutamen ×1, Ignis ×2 | amp+1 |
| Instant Damage, Hunger, Weakness | Mortuus ×3 | amp+1 |
| Instant Health, Regeneration | Sano ×3 | amp+1 |
| Invisibility, Night Vision | Sensus ×3 | amp+1 |
| Jump Boost | Volatus ×3 | amp+1 |
| Speed | Motus ×3 | amp+1 |
| Poison | Venenum ×3 | amp+1 |
| Resistance | Tutamen ×3 | amp+1 |
| Water Breathing | Aer ×3 | amp+1 |

All potion items also get: Aqua(1), Perditio(2) if splash/lingering, Praecantatio(2 × (amp+1)).

### 5.3 Enchantment -> Aspect Map

| Enchantment(s) | Aspect × Level |
|---------------|---------------|
| Aqua Affinity | Aqua |
| Bane of Arthropods, Lure | Bestia |
| Blast/Fire/Projectile/Protection | Tutamen |
| Efficiency, Repair | Instrumentum |
| Feather Falling | Volatus |
| Fire Aspect, Flame | Ignis |
| Fortune, Looting, Luck of the Sea | Lucrum |
| Infinity | Fabrico |
| Knockback, Punch, Respiration, Depth Strider, Frost Walker | Aer |
| Power, Sharpness, Smite | Telum |
| Silk Touch | Permutatio |
| Thorns | Perditio |
| Unbreaking | Terra |
| Haste (mod) | Motus |

Also: sum of all enchantment levels added as Praecantatio.

### 5.4 Tool/Weapon/Armor Bonus Aspects (on scan)

| Item Type | Aspects |
|-----------|---------|
| Armor | Tutamen = damageReduceAmount |
| Sword | Telum = attackDamage + 1 |
| Bow | Telum(3), Volatus(1) |
| Pickaxe | Perfodio = harvestLevel + 1 |
| Tool | Instrumentum = harvestLevel + 1 |
| Shears, Hoe | Meto = tier-based (maxDamage) |
| IEssentiaContainerItem | Adds contents as bonus aspects |

---

*Generated from Thaumcraft 4.2.3.5 (1.7.10) — port reference for 1.12.2.*
