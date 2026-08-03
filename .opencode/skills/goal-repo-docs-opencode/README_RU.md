# Goal Ledger для OpenCode

**TL;DR:** это drop-in замена `goal-repo-docs`, которая делает потерю контекста безопасной. Чат, DCP и Compact считаются кэшем; durable source of truth живёт в репозитории и проверяется линтером.

## Что входит

- `SKILL.md` — основной skill, заточенный под RECON → swarm audit → freeze → iterative implementation → commits/closure.
- `templates/` — строгие шаблоны frozen contract, RECON ledger, source registry, write-ahead state, append-only log и subagent packet.
- `scripts/goal_init.py` — создаёт goal workspace до RECON.
- `scripts/goal_lint.py` — механически проверяет покрытие `F-* → R-*`, источники, отчёты, статусы, hashes и условия завершения.
- `scripts/goal_context.py` — собирает компактный rehydration capsule после Compact/DCP или при новой сессии.
- `integrations/opencode/goal-compaction.ts` — через compaction hook добавляет активный durable state в prompt штатного Compact.
- `integrations/opencode/dcp.jsonc` — консервативная DCP-конфигурация с protect tags/files и поддержкой subagent sessions.
- `examples/eldritch-fragment.md` — фрагмент миграции вашего примера без потери числовых и условных дельт.

## Главный принцип

Не пытаться сделать summary lossless. Сделать так, чтобы потеря summary ничего не ломала.

Для этого используются пять независимых ролей:

- `SOURCES.md` отвечает на «откуда это взялось?»;
- `RECON.md` хранит атомарные факты и точные дельты;
- `GOAL.md` хранит frozen finish line и отображение `R-* → F-*`;
- `STATE.md` хранит маленький текущий execution edge до начала изменений;
- `LOG.md` хранит причинную историю решений и evidence.

Сырые результаты subagents остаются в `reports/`. Основной агент не имеет права ссылаться на абстрактный «completed RECON», если этого RECON нет в репозитории.

## Установка в проект

Скопируйте каталог skill:

```bash
mkdir -p .opencode/skills
cp -R goal-repo-docs-opencode .opencode/skills/goal-repo-docs
```

Установите compaction hook:

```bash
mkdir -p .opencode/plugins
cp .opencode/skills/goal-repo-docs/integrations/opencode/goal-compaction.ts \
  .opencode/plugins/goal-compaction.ts
```

DCP project config:

```bash
cp .opencode/skills/goal-repo-docs/integrations/opencode/dcp.jsonc \
  .opencode/dcp.jsonc
```

Если `.opencode/dcp.jsonc` уже есть, merge-перенесите поля, не перезаписывайте существующие настройки вслепую.

Для native Compact merge-перенесите подходящий фрагмент:

- OpenCode с `compaction.reserved`: `opencode-v1-compaction.jsonc`;
- OpenCode V2 с `compaction.keep.tokens` и `compaction.buffer`: `opencode-v2-compaction.jsonc`.

Перезапустите OpenCode после изменения DCP/plugin/config.

## Глобальная установка

Skill можно положить в:

```text
~/.config/opencode/skills/goal-repo-docs/
```

Plugin — в:

```text
~/.config/opencode/plugins/goal-compaction.ts
```

DCP config — в:

```text
~/.config/opencode/dcp.jsonc
```

Для конкретного репозитория project-level конфигурация предпочтительнее: пути, budgets и compaction-настройки часто различаются.

## Начало новой цели

```bash
python3 .opencode/skills/goal-repo-docs/scripts/goal_init.py \
  --title "Eldritch Thaumonomicon parity restoration"
```

Скрипт создаст:

```text
docs/goals/<date>-<slug>/
.opencode/active-goal
```

После этого вызовите `/goal-repo-docs` или дайте вашему верхнеуровневому `goal` skill загрузить `goal-repo-docs`.

Критически важно: workspace создаётся **до** массовых subagents. Каждый subagent получает уникальный `A-*` и пишет собственный `reports/A-*.md` прогрессивно.

## Freeze после RECON

После нормализации всех отчётов:

```bash
python3 .opencode/skills/goal-repo-docs/scripts/goal_lint.py \
  docs/goals/<date>-<slug> --stamp

python3 .opencode/skills/goal-repo-docs/scripts/goal_lint.py \
  docs/goals/<date>-<slug>
```

`--stamp` фиксирует SHA-256 `GOAL.md`, `RECON.md`, source bundle и полный набор audit reports в `STATE.md`. Скрытое изменение frozen contract, источника или принятого subagent packet после этого обнаруживается.

Не начинайте product edits при ошибках линтера.

## Работа после Compact, DCP или новой сессии

```bash
python3 .opencode/skills/goal-repo-docs/scripts/goal_context.py --root .
git status --short
git branch --show-current
git rev-parse HEAD
python3 .opencode/skills/goal-repo-docs/scripts/goal_lint.py \
  "$(cat .opencode/active-goal)"
```

После rehydration агент читает только активные `R-*`, `F-*`, связанные `S-*`, последний material log и live diff. Это не перегружает контекст всем RECON, но сохраняет точность.

## DCP

Предложенная конфигурация начинает подталкивать compression примерно с 45% контекста и усиливает её к 70%. Это стартовая настройка, а не универсальное число.

Правильный порядок:

1. Записать новый факт в report/RECON.
2. Обновить active checkpoint в `STATE.md`.
3. Добавить material event в `LOG.md`.
4. Только затем использовать DCP compression.

Для ручного focus можно использовать текст из `integrations/opencode/dcp-compress-focus.txt`.

`experimental.allowSubAgents` включён, потому что packet protocol делает subagent crash-safe. При проблемах конкретной версии DCP его можно выключить: корректность ledger от DCP не зависит.

## CRW

CRW полезен как collector, но не должен быть единственной памятью о внешнем источнике. Для каждого значимого web-факта заносите в `SOURCES.md`:

- query или URL;
- дату/время retrieval;
- relevant excerpt или snapshot path;
- hash, когда практически возможно;
- что именно этот источник доказывает.

Так повторный scrape не сможет незаметно изменить frozen requirement.

## Почему `R-*` остаются достаточно крупными

Слишком мелкий GOAL быстро разрастается и сам съедает контекст. Поэтому outcomes могут быть кластерными, но их статус вычисляется из атомарных `F-*`.

Например, `R-002 Restore Primal Orb` может покрывать четыре независимых finding:

- отсутствующая launch velocity;
- underwater explosion `4 → 2`;
- probability boundary `1/10% → 2/11%`;
- отсутствующие trail/impact FX.

`R-002` нельзя закрыть после исправления только velocity. Это проверяет линтер.

## Что эта схема не обещает

Абсолютной гарантии нет, если агенту запрещена запись в репозиторий, он сознательно игнорирует skill, пользователь удаляет ledger или внешние инструменты меняют файлы вне Git. Но при нормальной OpenCode-среде это убирает основной класс silent-loss ошибок: важная информация больше не зависит от того, насколько удачно DCP/Compact пересказал длинную сессию.
