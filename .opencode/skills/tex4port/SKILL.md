---
name: tex4port
description: "Use when porting TC4 (1.7.10) model-rendered block visuals to Forge 1.12.2 baked item/block models. Covers non-square TC4 textures, ModelRenderer-to-JSON translation, UV mapping, display transforms, and inventory routing."
---

# Texture Porting: TC4 ModelRenderer -> Forge 1.12.2 Baked Item Models

## Когда использовать

Этот skill применяй, когда нужно перенести TC4 блок/предмет, который в оригинале рендерится через TESR + `ModelRenderer` (Java), на baked JSON модель для инвентаря/руки в 1.12.2 порте. Мирный рендер остаётся на TESR.

## Короткая схема

```
ClientProxy → registerBuiltinItemModel(item, meta, "model_name")
  → JSON: "textures": { "surface": "thaumcraft:models/..._inventory" }
  → JSON: "elements": [ ... baked box per ModelRenderer part ]
  → JSON: "display": { ... TC6/Forge block display transforms }
  → текстура: square PNG copy из TC4 model texture
```

## Пошаговый алгоритм

### 1. Изучить reference

- Найди класс в `thaumcraft_src/**` или декомпиль из соответствующего jar.
- Для блока — определи, какой `TileEntitySpecialRenderer` его рисует.
- Запиши все `ModelRenderer` вызовы: `new ModelRenderer(this, offsetX, offsetY)`, `addBox`, `setRotationPoint`, `textureWidth`, `textureHeight`.
- Совмести Java-классы:

  | TC4 Java class | Используют |
  |---|---|
  | `ModelTable` | Table |
  | `ModelArcaneWorkbench` | Arcane Workbench, Deconstruction Table, Focal Manipulator |
  | (другие) | — |

### 2. Создать square atlas copy текстуры

TC4 model textures часто не квадратные (`64x32`, `128x64`). Forge 1.12 block atlas rejects non-square sprites → magenta missing texture.

**Правило**: создай квадратную копию с NEAREST-масштабированием.

```python
from PIL import Image
img = Image.open('textures/models/name.png').convert('RGBA')
side = max(img.size)
img.resize((side, side), Image.Resampling.NEAREST).save('textures/models/name_inventory.png')
```

Файл клади рядом с оригиналом: `textures/models/name_inventory.png`.

### 3. Рассчитать UV для JSON faces

Формула пересчёта из пикселей текстуры в UV-координаты Minecraft (0–16):

```
u = pixelX * 16 / textureWidth
v = pixelY * 16 / textureHeight
```

Для квадратной копии `textureWidth == textureHeight == side`, поэтому
деление упрощается, но v может быть scaled вдвое, если оригинал был вдвое
короче.

**ModelRenderer face → JSON face**:

| ModelRenderer offset box | JSON элемент `from`/`to` |
|---|---|
| `addBox(x, y, z, w, h, d)` at `setRotationPoint(px, py, pz)` | `from = [px+8, 16-py-d, pz+8]` (flip Y) |
| Размеры: `w×h×d` | `to = [from[0]+w, from[1]+h, from[2]+d]` |

**Поворот 180° X** в TESR означает, что up/down в JSON надо проверять
визуально. Для `worktable`/`wandtable` корректная пара:

```json
"up":   { "uv": [2, 0, 4, 4] },
"down": { "uv": [4, 0, 6, 4] }
```

### 4. Структура item JSON

Обязательные секции:

```json
{
  "ambientocclusion": false,
  "display": {
    "gui":   { "rotation": [30, 225, 0], "translation": [0, 0, 0], "scale": [0.625, 0.625, 0.625] },
    "fixed": { "rotation": [0, 0, 0],    "translation": [0, 0, 0], "scale": [0.5, 0.5, 0.5] },
    "ground":  { "rotation": [0, 0, 0], "translation": [0, 3, 0], "scale": [0.25, 0.25, 0.25] },
    "thirdperson_righthand":  { "rotation": [75, 45, 0], "translation": [0, 2.5, 0], "scale": [0.375, 0.375, 0.375] },
    "thirdperson_lefthand":   { "rotation": [75, 225, 0], "translation": [0, 2.5, 0], "scale": [0.375, 0.375, 0.375] },
    "firstperson_righthand":  { "rotation": [0, 45, 0], "translation": [0, 0, 0], "scale": [0.4, 0.4, 0.4] },
    "firstperson_lefthand":   { "rotation": [0, 225, 0], "translation": [0, 0, 0], "scale": [0.4, 0.4, 0.4] }
  },
  "textures": {
    "particle": "thaumcraft:models/name_inventory",
    "surface": "thaumcraft:models/name_inventory"
  },
  "elements": [
    { "from": [...], "to": [...], "faces": { ... } }
  ]
}
```

Display transforms копируй из TC6 donor `models/item/*.json`, если нет
особых требований.

### 5. Routing в ClientProxy

```java
// Обычные blockstate варианты
for (int meta = 0; meta <= maxMeta; meta++) {
    registerBlockItemModel(item, meta, "type=" + meta);
}
// override конкретных мет
registerBuiltinItemModel(item, meta, "blockname_meta_inventory");
// остальные меты, которым нужен TEISR
registerBuiltinItemModel(item, otherMeta, "blockname_tesr");
// TEISR для оставшихся
item.setTileEntityItemStackRenderer(new ItemXxxRenderer());
```

### 6. Обновить guard тесты

Для каждого изменённого JSON добавь проверку в существующий или новый
**static guard test**:

```java
String model = read("path/to/model.json");
assertTrue("описание", model.contains("\"surface\": \"thaumcraft:models/..._inventory\"")
        && model.contains("\"from\": [0, 8, 0]")
        && model.contains("\"thirdperson_righthand\"")
        && model.contains("[75, 45, 0]"));
```

Проверяй:
- texture path (на `_inventory`)
- display transforms
- хотя бы один geometry marker
- up/down UV для top-visible блоков

### 7. Валидация

- `jq empty ...json` — синтаксис.
- `./scripts/dev.sh compileJava` — компиляция.
- `./scripts/dev.sh validate --smoke` — runtime smoke (меняются model/registration).
- **Client visual check**: фиолетовые текстуры, смещённые координаты и
  TC6-стиль текстур не видны в compile-time.

## Известные кейсы

| Блок | Meta | TC4 Model | Texture | Square atlas |
|---|---|---|---|---|
| Table | 0 | ModelTable | `table.png` 64×32 | `table_inventory.png` 64×64 |
| Deconstruction Table | 14 | ModelArcaneWorkbench | `decontable.png` 128×64 | `decontable_inventory.png` 128×128 |
| Arcane Worktable | 15 | ModelArcaneWorkbench | `worktable.png` 128×64 | `worktable_inventory.png` 128×128 |
| Focal Manipulator | 13 | ModelArcaneWorkbench | `wandtable.png` 128×64 | `wandtable_inventory.png` 128×128 |
| Runic Matrix | 2 | TileRunicMatrixRenderer (hardcoded cluster) | `arcane_stone` block texture | не нужна (block texture) |

## Анти-паттерны

- **Не копируй TC6 geometry** — оно даёт правильные координаты, но
  неверное расположение текстур и силуэт.
- **Не ссылайся напрямую на `thaumcraft:models/table`** (без `_inventory`)
  если текстура не квадратная — получишь magenta missing texture.
- **Не удаляй TEISR** полностью — мирный рендер всё ещё нуждается в
  TileEntitySpecialRenderer.
- **Не выдумывай UV** — используй exact pixel offsets из `ModelRenderer`
  atlas layout, пересчитанные в 0–16.
