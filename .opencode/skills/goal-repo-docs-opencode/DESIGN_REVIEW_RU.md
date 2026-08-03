# Разбор текущего skill и архитектура замены

## Вердикт

Текущий `goal-repo-docs` хорошо ограничивает scope, требует measurable finish line и не даёт review/tests автоматически превращать гипотезы в требования. Его слабое место — модель хранения: один минимальный документ одновременно пытается быть контрактом, RECON, runtime state, evidence log и handoff.

Для короткой цели это работает. Для mass-RECON и длинной итеративной разработки это создаёт single point of lossy summarization.

## Что произошло в Eldritch-прогоне

Главный агент провёл девять аудитов и выдал достаточно насыщенный RECON: exact counts подтверждённого parity, конкретные numeric deltas, условия difficulty, ошибочные test guards, отсутствующие behavior tests и phased plan.

После вызова goal skill появился качественно выглядящий документ с `R1-R9`, constraints и change envelope. Но произошли три опасных преобразования.

### 1. Durable ссылка указывает на недолговечный объект

Goal пишет source как «completed Eldritch RECON», но сам RECON и девять audit packets не были сохранены рядом с goal. После Compact другой агент увидит ссылку, которую невозможно открыть или проверить.

### 2. Атомарные факты свернулись в тематические outcomes

Некоторые точные дельты стали общими формулировками:

- `underwater explosion 4 вместо 2`, `1/10% вместо 2/11%`, missing FX превратились в “normal and underwater impacts, special-effect probability…”;
- Void material `600/20` против `150/10` и Axe `9` против `6` превратились в “original durability/enchantability/attack intent”;
- exact Ender Pearl aspects превратились в “expose the TC4 Eldritch scan aspects”.

Такой outcome удобен для чтения, но после потери исходного чата невозможно доказать, что все поддельты действительно закрыты.

### 3. Появились provenance orphans

В goal есть детали, которых нет в видимом итоговом RECON summary: negative-coordinate placement, hand routing, Warden room ownership, creative/self-repair deltas, extraction/retries и несколько safe 1.12 adaptations.

Они могли быть корректно найдены subagents. Проблема не в самих деталях, а в отсутствии durable locator: новый агент не отличит подтверждённое subagent evidence от случайного расширения scope.

## Дополнительные риски текущего текста

- Обновление документа только после завершённого checkpoint оставляет незаписанным активный hypothesis/working set именно в момент, когда Compact наиболее опасен.
- “Keep a short checkpoint history” может удалить причинную цепочку failed attempts и scope decisions.
- “Use no subagents by default; one round” не соответствует bounded mass-RECON и мешает continuation waves.
- “Minimum sufficient evidence” верно для проверки результата, но не должно означать “minimum retained discovery detail”. Proof surface и discovery ledger — разные сущности.
- Positive parity фиксируется хуже, чем bugs, поэтому широкое исправление может сломать уже совпадающий corpus.

## Архитектурное решение

Ближайший практический silver bullet — не более длинный prompt, а сочетание:

1. **Write-ahead durable state** — активный checkpoint записывается до первого edit.
2. **Atomic traceability** — каждый confirmed delta имеет `F-*`, а outcomes покрывают IDs.
3. **Raw packet retention** — каждый subagent пишет независимый report.
4. **Positive parity controls** — корректные поверхности становятся regression controls.
5. **Frozen hashes + lint** — scope drift и ложное completion ловятся механически.
6. **Compaction rehydration hook** — Compact знает путь и active IDs, но не считается памятью.
7. **Recovery mode** — при конфликте summary/Git/files агент не продолжает по догадке.

## Почему не один огромный GOAL.md

Один гигантский документ будет постоянно загружаться и сам станет источником context pressure. Разделение даёт progressive disclosure:

- всегда читается маленький `STATE.md`;
- затем только активные `R-*` и `F-*`;
- raw reports и источники читаются по ссылке при споре;
- полный RECON нужен при freeze, scope amendment и closure audit.

## Почему Markdown, а не база данных

Markdown хорошо читается моделью и человеком, diff-friendly, работает без сервиса и коммитится вместе с кодом. Структура достаточно строгая для stdlib-линтера. Отдельная база или generated projection добавила бы новый sync failure mode.

## Scope promotion

Audit findings не должны автоматически становиться requirements во всех случаях. Поэтому policy выбирается до fan-out:

- `explicit_only` для обычного review;
- `confirmed_in_scope`, когда пользователь действительно делегировал аудит bounded surface и исправление всех подтверждённых дефектов;
- `triage`, когда нужен отдельный selection gate.

Это сохраняет сильную сторону старого skill и одновременно поддерживает ваш RECON → full implementation use case.
