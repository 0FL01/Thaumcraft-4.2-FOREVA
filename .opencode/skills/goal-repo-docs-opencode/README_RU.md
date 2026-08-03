# Goal Ledger для OpenCode

**TL;DR:** версия 2.1 одновременно закрывает две мины GPT-5.6 Sol: потерю деталей после DCP/Compact и рекурсивное исправление всех мыслимых багов. Чат считается кэшем, а repo-local ledger хранит не только факты, но и механический firewall: `confirmed ≠ required`, production admission, конечные бюджеты, ноль автопромоушенов соседних находок и один closure-review.

## Что входит

- `SKILL.md` — основной skill, заточенный под RECON → swarm audit → freeze → iterative implementation → commits/closure.
- `templates/` — строгие шаблоны frozen contract, RECON ledger, source registry, write-ahead state, append-only log и subagent packet.
- `scripts/goal_init.py` — создаёт goal workspace до RECON.
- `scripts/goal_lint.py` — механически проверяет покрытие `F-* → R-*`, production admission, конечные бюджеты, recursion firewall, источники, отчёты, статусы, hashes и условия завершения.
- `scripts/goal_context.py` — собирает компактный rehydration capsule после Compact/DCP или при новой сессии.
- `integrations/opencode/goal-compaction.ts` — через compaction hook добавляет активный durable state в prompt штатного Compact.
- `integrations/opencode/dcp.jsonc` — консервативная DCP-конфигурация с protect tags/files и поддержкой subagent sessions.
- `references/production-admission.md` — строгий gate между «дельта подтверждена» и «на неё разрешено тратить implementation-токены».
- `examples/eldritch-fragment.md` — тот же Eldritch RECON, где реальные gameplay-баги admitted, а probability/cosmetic/adjacent дельты durable, но deferred.


## Критическая настройка для GPT-5.6 Sol

Не используйте старую policy `confirmed_in_scope`. Она удалена и линтер считает её ошибкой. Подтверждённая дельта доказывает только факт различия, но не production reachability, вред и окупаемость исправления.

Для обычного сценария «RECON → исправить реальные баги» используйте:

```text
Audit-Promotion-Policy: production_gate
```

Finding становится `required` только при concrete supported trigger, concrete impact/contract, direct evidence и finite budget. Для обычного кандидата разрешена максимум одна bounded reproduction attempt. Если она не воспроизводит проблему и нет deterministic supported-path proof, finding получает `deferred` и больше не потребляет implementation-токены.

Hard defaults, замороженные до fan-out:

```text
Budget-Authority: skill-default
Audit-Assignments-Cap: 12
Audit-Waves-Cap: 1
Max Required Findings: 8
Max Total Fix Checkpoints: 12
Max Candidate Reproduction Attempts Per Finding: 1
Max Material Replans Per Required Finding: 2
Max Implementation Subagent Waves: 0
Max Closure Review Passes: 1
Max Scope Amendments: 0
Adjacent Finding Auto-Promotions: 0
Post-Closure Work Items: 0
```

Если semantic gate пропустил больше кандидатов, чем помещается в `8 findings / 12 checkpoints`, Sol обязан выбрать наиболее обязательные и production-impactful, а остальные пометить `over_capacity` и отложить. Эти значения попадают в Compact capsule и проверяются линтером. Для каждого admitted `F-*` STATE также хранит `checkpoints_started` и `material_replans`; линтер сравнивает их с frozen budget. Агент не может увеличить лимиты самостоятельно. Повышение требует одного authoritative `S-*` с точным `Budget Grant`; фраза «почини всё» без конечных чисел не является grant.

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


Установите готовую команду запуска:

```bash
mkdir -p .opencode/commands
cp .opencode/skills/goal-repo-docs/integrations/opencode/command-goal-ledger.md \
  .opencode/commands/goal-ledger.md
```

Содержимое `integrations/opencode/AGENTS-goal-ledger-snippet.md` добавьте в корневой `AGENTS.md`. Оно принудительно выбирает `production_gate` до fan-out и запрещает review-of-review/adjacent auto-promotion.

После этого запуск выглядит так:

```text
/goal-ledger Проведи RECON подсистемы, затем исправь только admitted production-relevant findings
```

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

После этого вызовите `/goal-ledger <задача>` или явно попросите верхнеуровневый `goal` skill сначала загрузить `goal-repo-docs`.

Критически важно: workspace создаётся **до** массовых subagents. Каждый subagent получает уникальный `A-*` и пишет собственный `reports/A-*.md` прогрессивно.

## Freeze после RECON

После нормализации всех отчётов и применения Production Admission Gate:

```bash
python3 .opencode/skills/goal-repo-docs/scripts/goal_lint.py \
  docs/goals/<date>-<slug> --stamp

python3 .opencode/skills/goal-repo-docs/scripts/goal_lint.py \
  docs/goals/<date>-<slug>
```

`--stamp` фиксирует SHA-256 `GOAL.md`, `RECON.md`, source bundle и полный набор audit reports в `STATE.md`. Линтер также запрещает `confirmed_in_scope`, open-ended budgets, превышение pre-RECON `8/12` capacity, required findings без production gate, deferred findings с fix-бюджетом, ненулевые scope amendments/adjacent auto-promotions и review/subagent counters выше governor.

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

После rehydration агент читает promotion policy, Production Relevance Envelope, Resource Governor/counters, активные `R-*`, `F-*`, связанные `S-*`, последний material log и live diff. Поэтому Compact не превращает deferred candidate обратно в работу.

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

## Как Sol перестаёт чинить всё подряд

Пример: RECON подтвердил четыре дельты Primal Orb.

- отсутствие launch velocity — deterministic supported path, каждый cast сломан: `required`;
- underwater strength `4 → 2` — realistic supported trigger и concrete gameplay impact: `required`;
- небольшой probability delta без установленного harmful impact: `deferred`;
- cosmetic FX, не входящий в production-critical finish line: `deferred`.

Все четыре факта сохраняются в `RECON.md`, но outcomes покрывают только admitted findings. Sol не может заявить, что «раз уж F-003 найден, надо ещё построить statistical harness»: у F-003 `fix_checkpoints=0`, а линтер не пропустит его в `R-*`.

Review после фикса тоже не открывает новую воронку. Он может вернуть агент к уже frozen F-* или доказать regression, вызванную текущим diff. Независимая находка получает `deferred`, а review-of-review запрещён.

## Почему `R-*` остаются достаточно крупными

Слишком мелкий GOAL быстро разрастается и сам съедает контекст. Поэтому outcomes могут быть кластерными, но их статус вычисляется из атомарных `F-*`.

Например, `R-002 Restore production-relevant Primal Orb behavior` покрывает только admitted `F-001` и `F-002`: launch velocity и underwater strength. Подтверждённые, но deferred `F-003`/`F-004` остаются в RECON и не блокируют completion. При этом `R-002` нельзя закрыть после исправления только velocity, пока admitted underwater finding не verified. Это проверяет линтер.

## Что эта схема не обещает

Абсолютной гарантии нет, если агенту запрещена запись в репозиторий, он сознательно обходит skill/линтер, пользователь сам требует exact exhaustive parity без бюджета или внешние инструменты меняют файлы вне Git. При нормальной OpenCode-среде схема убирает два главных класса ошибок: silent loss после Compact и recursive scope growth из reviews/tests/adjacent findings.
