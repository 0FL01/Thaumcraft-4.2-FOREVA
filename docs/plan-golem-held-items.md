# Plan: Fix Golem Held Item Rendering

## Root Cause

В порте отсутствует рендеринг предметов в руках големов. В оригинальном 1.7.10
`RenderGolemBase` был метод `renderCarriedItems()`, который вызывался через
`func_77029_c` (RenderLiving.renderEquippedItems). Этот метод рисовал:

1. **Удочку** в руке Fisher-голема (core 11) — как 2D иконку на кваде.
2. **Несемый предмет** (`getCarriedForDisplay()`) в правой руке голема.
3. **Ведро/жидкость** для Fluid-голема (core 5) — 3D obj-модель ведра + иконка
   жидкости внутри.

В порте 1.12.2 `func_77029_c` больше не используется (заменён на
`LayerRenderer`), и метод `renderCarriedItems` **не был портирован**. Единственный
рендеринг "в руке" — это леска с поплавком в `RenderGolemBobber` (работает
корректно). Удочка как предмет и все carried предметы не рендерятся.

## Что рендерить

| Core | Тип | Что рендерить | Источник |
|------|-----|---------------|----------|
| 0 (Gather) / 2 (Pickup) / etc. | Любой несущий предмет | Предмет из `getCarriedForDisplay()` | Datawatcher CARRIED |
| 5 (Fluid) | Ведро/жидкость | **Deferred** (Phase 8, отдельный план) | — |
| 11 (Fisher) | Удочка | `new ItemStack(Items.FISHING_ROD)` | Рендер-тайм константа |
| 6 (Essentia) | Банка с эссенцией | `getCarriedForDisplay()` (уже корректно) | Datawatcher CARRIED |

## Технический подход

Добавить `LayerRenderer<EntityGolemBase>` — `GolemHeldItemLayer` — в
`RenderGolemBase.java`, зарегистрировать в конструкторе.

### Масштабирование

`ModelGolem.render()` применяет `GlStateManager.scale(0.4F, 0.4F, 0.4F)` внутри
своего push/pop. LayerRenderer вызывается ПОСЛЕ того, как модель отрендерилась, и
масштаб 0.4 уже снят со стека. Поэтому layer должен сам войти в scaled-пространство:

```java
GlStateManager.pushMatrix();
GlStateManager.scale(0.4F, 0.4F, 0.4F);
// позиционирование и рендеринг в 0.4-масштабированном пространстве
GlStateManager.popMatrix();
```

### Позиционирование предметов

Используем `ModelGolem.golemRightArm.postRender(0.0625F)` для учёта анимаций руки
(ходьба, swing, carrying pose), затем смещение к "кисти" руки:

```
postRender(0.0625F) → Translate(-armBoxX, armBoxHeight, 0) → Rotate → renderItemSide
```

Параметры руки (`ModelGolem`):
- RotationPoint: `(0, 30, 0)`
- Box: `(-12, -2.5, -3)` to `(-8, 22.5, 6)` — 25 единиц высоты
- Низ руки (кисть): примерно `x=-10`, `y=+20` относительно rotationPoint

### Рендеринг предмета

Используем `Minecraft.getItemRenderer().renderItemSide(entity, stack,
ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false)` —
современный 1.12.2 способ, аналогичный `PechHeldItemLayer`.

### Удочка Fisher

Оригинальный код (байткод) не использует `postRender`, а вручную считает
поворот на основе `golemRightArm.rotateAngleX`:

```
rotateX = 5 + 90 * armAngle / PI
translate(-0.26875, 1.6, -0.53)
rotateY = 90 вокруг оси -Y
rotateZ = 30 вокруг оси -Z
scale(0.66, -0.66, 0.66)
→ рендер иконки
```

В порте используем `renderItemSide` с `THIRD_PERSON_RIGHT_HAND` вместо иконки.

### Несемые предметы (Gather, Pickup и др.)

Используем `postRender` для учёта анимации рук, затем:
- Для block-предметов (`ItemBlock`): центрируем на руке.
- Для обычных предметов: смещаем к низу руки.

### DeathTime

Не рендерим предметы во время анимации смерти: проверяем `entity.deathTime == 0`
(в Forge 1.12.2 это `deathTime` или `isDead`).

## Файлы для изменения

### 1. `src/main/java/thaumcraft/client/renderers/entity/RenderGolemBase.java`

**Что сделать:**
- Добавить импорты: `net.minecraft.client.Minecraft`,
  `net.minecraft.client.renderer.block.model.ItemCameraTransforms`,
  `net.minecraft.init.Items`, `net.minecraft.item.ItemBlock`,
  `net.minecraft.item.ItemStack`.
- Добавить inner-класс `GolemHeldItemLayer implements LayerRenderer<EntityGolemBase>`.
- Зарегистрировать слой: `this.addLayer(new GolemHeldItemLayer(this));` в
  конструкторе (после существующих слоёв).

**GolemHeldItemLayer — логика `doRenderLayer()`:**

```
1. Вычислить core = entity.getCore()
2. Проверить entity.deathTime == 0 (если deathTime != 0 — skip)
3. Если core == 11:
   - PushMatrix → scale(0.4) → rotate(5 + 90*armAngle/PI, 1,0,0)
     → translate(-0.26875, 1.6, -0.53) → rotate(90, 0,-1,0)
     → rotate(30, 0,0,-1) → scale(0.66, -0.66, 0.66)
     → renderItemSide(entity, new ItemStack(Items.FISHING_ROD), THIRD_PERSON_RIGHT_HAND, false)
     → PopMatrix
4. Если core != 5 && core != 11:
   carried = entity.getCarriedForDisplay()
   Если carried пустой — return
   PushMatrix → scale(0.4)
   → Если carried.getItem() instanceof ItemBlock:
        GlStateManager.translate(0, 2.5, -1.25)
        GlStateManager.rotate(180, 1, 0, 0)
        renderItemSide(entity, carried, GROUND, false)  // или NONE
     Иначе:
        golemRightArm.postRender(0.0625F)
        GlStateManager.translate(-10*0.0625F, 20*0.0625F, 0)
        GlStateManager.rotate(-90, 1, 0, 0)
        GlStateManager.rotate(180, 0, 1, 0)
        renderItemSide(entity, carried, THIRD_PERSON_RIGHT_HAND, false)
   → PopMatrix
```

**Примечание по импорту Items:**
```java
import net.minecraft.init.Items;
```
(Уже доступен как `net.minecraft.init.Items` в Forge 1.12.2.)

### 2. (Возможно) `test/.../ClientProxyEntityRendererRegistrationStaticGuardTest.java`

Проверить, не тестирует ли этот тест количество LayerRenderer-ов или их состав.
Если тест явно проверяет количество layers — обновить число.

## Риски и подводные камни

1. **Позиционирование предметов**: Смещения подобраны аналитически по размерам
   ModelGolem. Может потребоваться визуальная калибровка в игре.

2. **`THIRD_PERSON_RIGHT_HAND` transform**: Некоторые предметы (блоки, особые
   модели) могут иметь кастомные transforms в JSON модели, которые накладываются
   поверх наших GL-трансформаций. Рекомендуется `NONE` или `GROUND` для блоков.

3. **Bucket/Fluid (core 5)**: Отложено. Bucket 3D model
   (`textures/models/bucket.obj`) не портирован. Потребуется либо портировать
   obj-модель, либо использовать vanilla bucket item model.

4. **Fishing rod `Items.FISHING_ROD`**: В 1.12.2 `Items.FISHING_ROD` существует
   и имеет корректную JSON модель с `THIRD_PERSON_RIGHT_HAND` transforms.

5. **Совместимость с isDead**: В Forge 1.12.2 `Entity.deathTime` — это поле
   `public int deathTime` (наследуется от `EntityLivingBase`). Можно
   использовать `entity.deathTime != 0` или `!entity.isEntityAlive()`.

## Валидация

1. `./scripts/dev.sh compileJava` — проверить компиляцию.
2. `./scripts/dev.sh validate --smoke` — проверить загрузку мода в dedicated
   server.
3. Визуальная проверка в клиенте: создать голема с Gather core (должен
   показывать предмет в руке при подборе), Fisher core (должен показывать
   удочку в руке).
