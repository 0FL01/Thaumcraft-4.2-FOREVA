# Разбор архитектуры Goal Ledger 2.1 для GPT-5.6 Sol

## Вердикт

У задачи две независимые угрозы:

1. **Silent loss:** RECON, точные дельты и текущая гипотеза исчезают после DCP/Compact.
2. **Recursive scope growth:** Sol считает каждую подтверждённую дельту, review finding, synthetic test и «возможный edge case» новым обязательным исправлением.

Версия 2.0 хорошо закрывала первую угрозу, но недостаточно закрывала вторую. Более того, policy `confirmed_in_scope` могла её усилить: большой swarm находит десятки реальных различий, после чего каждое различие автоматически получает implementation budget. Формула «все confirmed P0/P1» плюс «no fixed budget» является прямым приглашением к бесконечной воронке.

Версия 2.1 удаляет `confirmed_in_scope` и вводит механическую цепочку:

```text
confirmed delta
→ production reachability + concrete impact
→ allowed admission basis
→ finite reproduction/fix budget
→ required OR deferred
```

## Почему старый skill всё же содержал правильные идеи

Исходный `goal-repo-docs` уже запрещал evidence создавать requirements, не позволял discoveries расширять scope по умолчанию, требовал minimum sufficient evidence, останавливал два бесплодных checkpoint подряд и делал completion terminal. Это сильная anti-recursion база.

Его слабость для mass-RECON была в хранении: один минимальный документ одновременно пытался быть контрактом, RECON, runtime state, evidence log и handoff. Полные subagent packets и exact deltas оставались в чате.

Новая схема сохраняет anti-scope философию исходника, но делает её repo-local и lintable.

## Что произошло в Eldritch-прогоне

Главный агент провёл девять аудитов и выдал насыщенный RECON: exact parity counts, numeric deltas, difficulty conditions, wrong test guards и phased plan.

После вызова goal skill появился качественно выглядящий `R1-R9`, но одновременно возникли две группы проблем.

### Потеря provenance и точности

- Goal ссылался на «completed Eldritch RECON», которого не было в durable файле.
- `underwater 4 → 2`, probability boundary и FX свернулись в широкое “restore behavior”.
- В goal появились детали без открываемого subagent locator.

После Compact невозможно было доказать, какие поддельты действительно закрыты.

### Scope bomb

Goal обещал закрыть практически все confirmed P0/P1 и ряд low-cost corrections, а budget был фактически открытым. Для Sol это означает:

```text
fix
→ add tests
→ review
→ discover adjacent issue
→ promote it
→ harden neighboring path
→ review the review
→ repeat
```

Даже если каждый отдельный шаг выглядит разумно, совокупно 80% ресурсов уходят на сценарии без production trigger.

## Главная модель 2.1

Три вопроса разделены:

1. `Confidence`: действительно ли дельта существует?
2. `Production Gate`: достигается ли она в поддерживаемом production envelope и имеет ли concrete impact?
3. `Promotion + Resource Governor`: разрешено ли исправление и сколько на него можно потратить?

`Confidence: confirmed` совместим с `Disposition: deferred`.

## Production Admission Gate

Finding может стать `required` только по одному из basis:

- `explicit_requirement`;
- `production_incident`;
- `deterministic_supported_path`;
- `current_diff_regression`;
- `blocks_explicit_requirement`;
- `credible_critical_risk`;
- `user_override`.

Недостаточно:

- «теоретически возможно»;
- synthetic-only test;
- unsupported configuration;
- advisory review;
- generic robustness;
- parity delta без explicit exact-parity finish line;
- соседний баг, найденный во время фикса.

Для обычного кандидата разрешена одна bounded reproduction attempt. Если реалистичный supported trigger не воспроизводится и deterministic proof отсутствует, finding становится `deferred`. Нельзя строить всё более искусственные fixtures, пока bug наконец не появится.

Редкая проблема допускается без инцидента только через `credible_critical_risk`: concrete security/data-loss/safety/irreversible-corruption preconditions. Абстрактное «может быть плохо» не проходит.

## One-hop causality

Admitted finding разрешает:

- smallest direct fix;
- direct compatibility edits;
- regression текущего diff;
- minimum direct evidence.

Он не разрешает:

- аудит всех callers/callees;
- generic retry/fallback/cache framework;
- cleanup «раз уж мы здесь»;
- review-of-review;
- перенос test debt/observability в product scope;
- исправление следующего независимого бага.

Adjacent discovery записывается durable, но с `Disposition: deferred` и `fix_checkpoints=0`.

## Hard Resource Governor

До fan-out RECON фиксирует capacity `8 admitted findings / 12 total fix checkpoints` под `skill-default`. Frozen GOAL затем содержит конечные числа:

```text
Max Required Findings: 8
Frozen Required Finding Count: N
Max Total Fix Checkpoints: 12
Frozen Total Fix Checkpoints: M
Max Candidate Reproduction Attempts Per Finding: 1
Max Material Replans Per Required Finding: 2
Max Implementation Subagent Waves: 0
Max Closure Review Passes: 1
Max Scope Amendments: 0
Adjacent Finding Auto-Promotions: 0
Post-Closure Work Items: 0
```

Агент может расходовать budget, но не увеличивать его. Повышение требует одного authoritative source с точным конечным `Budget Grant`; общая формулировка «исправить всё» не подходит. Если semantic gate пропустил больше находок, чем capacity, лишние становятся `deferred / over_capacity`, а не расширяют GOAL. `No fixed budget`, `as needed`, `until clean` и `exhaustive` запрещены линтером.

После достижения cap действие терминально:

- candidate → `deferred`;
- required finding → `blocked` или `unmet`;
- либо versioned scope amendment от пользователя/source.

«Есть ещё один безопасный эксперимент» не является основанием продолжать.

## Как это применяется к Eldritch

Один и тот же RECON может дать:

- launch velocity отсутствует, normal cast всегда сломан → `required`;
- underwater strength `4 → 2`, реальный supported branch и concrete impact → `required`;
- probability expression отличается, но harmful effect/contract не установлен → `deferred`;
- FX отсутствует, но production-critical objective его не требует → `deferred`;
- research graph `16/16` → `preserve`;
- wrong test guard блокирует admitted fix → `required` только как `blocks_explicit_requirement` и только минимальная правка assertion.

Sol сохраняет все детали, но не получает права чинить всё найденное.

## Почему raw packets всё ещё нужны

Production gate не заменяет durable RECON. Он решает другую проблему.

- `reports/A-*.md` сохраняют сырые доказательства и ограничения.
- `RECON.md` хранит atomic facts и admission decisions.
- `GOAL.md` содержит только admitted finish line.
- `STATE.md` хранит active edge и counters.
- `LOG.md` хранит scope/budget decisions.

После Compact агент видит не только active F/R, но и Scope Promotion, Production Relevance Envelope, Resource Governor и counters. Поэтому summary не может незаметно превратить deferred finding в next action.

## Механические проверки 2.1

`goal_lint.py` останавливает freeze/closure, если:

- используется `confirmed_in_scope`;
- required finding не имеет `Production Gate: pass`;
- admission basis не разрешён policy;
- deferred finding резервирует fix checkpoints/review;
- reproduction/review cap выше единицы;
- material replan cap выше двух;
- budget открыт или сформулирован как “as needed/until clean”;
- frozen required count не совпадает с реальными `required F-*` или превышает pre-RECON cap;
- суммарный fix-checkpoint budget не совпадает с findings или превышает pre-RECON cap;
- adjacent auto-promotions или post-closure work ненулевые;
- implementation/review counters превышают governor;
- outcome покрывает deferred finding;
- contract/hash/state invariants нарушены.

Это не делает модель безошибочной, но переводит главный failure mode из «решение модели» в нарушение проверяемого файла.

## Практический итог

Silver bullet состоит не в просьбе «не переусердствуй», а в четырёх стенках одновременно:

```text
Production envelope
+ Admission gate
+ Hard resource governor
+ Terminal closure
```

Durable memory не позволяет Sol забыть важное. Production gate не позволяет ему объявить всё важным. Resource Governor не позволяет бесконечно доказывать гипотезу. Terminal closure не позволяет потратить остаток контекста на очередной hardening pass.
