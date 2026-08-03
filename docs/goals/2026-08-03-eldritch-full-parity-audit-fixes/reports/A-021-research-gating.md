# Audit Packet: A-021 - Research Gating and Discovery

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-021
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare `ResearchManager` gating and discovery semantics used by Eldritch researches with Thaumcraft 4.2.3.5: prerequisites, visible/hidden parents, hidden/concealed/lost eligibility, item/block/entity/aspect triggers, clue keys including `@FOCUSPRIMAL`, completion and sibling handling, and item/meta/NBT matching.
- Anti-scope: Note generation details, persistence, networking, UI, product changes, central Goal Ledger changes, and broad re-audit.
- Oracle and comparison direction: `Thaumcraft-1.7.10-4.2.3.5.jar`, decompiled with CFR -> Forge 1.12.2 port.
- Read/write permissions: Only this report is writable; product and central-ledger paths are read-only.

## Coverage and Evidence

- TC4 oracle: decompiled `thaumcraft.common.lib.research.ResearchManager` methods `createClue` (CFR lines 84-117), `findHiddenResearch` (137-159), `findMatchingResearch` (162-185), `isResearchComplete` (389-398), and `doesPlayerHaveRequisites` (428-455).
- TC4 helper: decompiled `InventoryUtils.areItemStacksEqual`; exact null/empty, ore-dictionary, NBT, item identity, metadata, damageable-item and `Short.MAX_VALUE` wildcard behavior was inspected.
- Port: `src/main/java/thaumcraft/common/lib/research/ResearchManager.java:114-135,346-375,495-598,818-850`.
- Eldritch declarations: `src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java`; the original registrations were compared, including `FOCUSPRIMAL`, `ROD_primal_staff`, `OUTERREV`, `PRIMPEARL`, and `PRIMALCRUSHER`.
- Related consumers inspected: `GuiResearchBrowser.java:386-404,735-756`, `ItemResearchNotes.java:127-148`, `PacketPlayerCompleteToServer.java:63-105`, `PacketResearchComplete.java:38-51`, `ItemResource.java:123-163`, and `ScanManager.java:226-228,400-419`.

## Audit Result

One verified semantic defect was found. All other requested gating, discovery, trigger, completion, and stack-matching behavior inspected for Eldritch parity matches TC4, subject to the test gaps below.

### A-021-F01 - Entity trigger matching accepts unrelated namespaces

- Type: defect
- Severity: P2
- Confidence: high
- Source/oracle locator: TC4 CFR `ResearchManager.createClue`, lines 96-100; port `ResearchManager.java:818-850` and call path `createClue` at `:553-598`.
- TC4 behavior: Entity clue matching is an exact string comparison, `clue.equals(string)`. The Eldritch registration is `ROD_primal_staff` with entity trigger `Thaumcraft.PrimalOrb`.
- Port behavior: `entityTriggerMatches` lowercases and expands legacy dotted, namespaced, and path-only forms. This correctly adapts `Thaumcraft.PrimalOrb` to `thaumcraft:primalorb`, but also makes `Thaumcraft.PrimalOrb` match `othermod:primalorb` because the path-only form is accepted.
- Reproduction: With `ROD_primal_staff` registered and neither its full nor `@ROD_primal_staff` clue already known, call `ResearchManager.createClue(world, player, "othermod:primalorb", null)`. The port can grant `@ROD_primal_staff`; TC4's exact comparison cannot. Full completion remains correctly gated by `FOCUSPRIMAL` and all eight hidden rod parents, so the defect is premature discovery/clue eligibility rather than direct completion.
- Eldritch impact: A third-party entity sharing the `primalorb` path can reveal the primal staff clue without being Thaumcraft's Primal Orb. This can alter discovery ordering and expose a hidden Eldritch research through an unrelated entity scan.
- Hazard: Retain the legitimate legacy-to-Forge adaptation for `Thaumcraft.PrimalOrb` -> `thaumcraft:primalorb`, but do not use path-only equality across namespaces. Any fix must preserve case normalization only where it does not erase namespace identity.
- Test gap: `ResearchManagerEntityTriggerMatchTest` covers valid legacy conversion and mismatches, but lacks a wrong-namespace same-path assertion and lacks an end-to-end `createClue` regression using the Eldritch rod registration.

## Parity Matrix

| Surface | TC4 behavior | Port result | Verdict |
|---|---|---|---|
| Prerequisites | `doesPlayerHaveRequisites` requires every `parents` entry and every `parentsHidden` entry to be complete. | `ResearchManager.java:346-375` requires both arrays. | Match |
| Clue prerequisite gating | `createClue` considers tagged hidden/lost research and does not prerequisite-gate the clue itself. | Port follows the same clue path. | Match |
| Hidden discovery | `findHiddenResearch` considers hidden, tagged, incomplete research with satisfied prerequisites and a matching trigger; random selection is seeded from world time/50. | `:495-521` follows the same eligibility shape and selection basis. | Match |
| Lost discovery | Lost research is not selected by hidden discovery; it is eligible through matching clues. | `findMatchingResearch`/`createClue` preserve this distinction. | Match |
| Concealed discovery | Concealed is not excluded by matching-research eligibility; GUI visibility is controlled separately and prerequisites apply when unlocking/viewing. | Port GUI and manager behavior match the original rules. | Match |
| Matching exclusions | Matching research excludes hidden, lost, auto-unlock, virtual, and stub entries, plus secondary entries excluded by difficulty; concealed is not excluded. | `:523-548` has the same exclusions. | Match |
| Full vs `@KEY` visibility | Full `KEY` is visible normally; `@KEY` reveals hidden/lost research before prerequisites, while completion still requires prerequisites. | GUI `:386-404` and `:735-756` match. | Match |
| Item/block trigger | Uses stack equality with exact item identity, metadata/wildcard and NBT rules, with ore-dictionary behavior as implemented by `InventoryUtils`. | Port uses the equivalent stack comparison in clue matching. | Match |
| Entity trigger | TC4 compares the registered entity clue string exactly. | Port has the namespace-collision defect in A-021-F01. | Defect |
| Aspect trigger | Only a positive awarded aspect amount triggers a matching research. | Port requires a positive aspect amount. | Match |
| `@FOCUSPRIMAL` | The charm clue key is granted only for the original special clue condition (`r == 42`); it does not grant unrelated clues. | `ItemResource.java:123-163` preserves this behavior. | Match |
| Completion | `isResearchComplete` accepts the full key or its clue form according to the original knowledge representation. | `ResearchManager.java:114-135` matches. | Match |
| Siblings | Completion/note consumers handle sibling completion in the caller, not as a different manager trigger rule. | `ItemResearchNotes.java:127-148` and packet consumers match; no Eldritch declaration uses siblings. | Match |

## Eldritch Trigger and Parent Parity

- `FOCUSPRIMAL`: concealed, parent `ELDRITCHMINOR`; port and TC4 declarations match.
- `ROD_primal_staff`: hidden; entity trigger `Thaumcraft.PrimalOrb`; item trigger `focusPrimal`; parent `FOCUSPRIMAL`; eight hidden rod parents match. Its entity trigger is the sole verified gating/discovery delta because of the port matcher adaptation.
- `OUTERREV`: lost; `blockEldritch` metadata 5 and 10 triggers; parent `ENTEROUTER`; match.
- `PRIMPEARL`: lost; `itemEldritchObject` metadata 3 trigger; parent `ELDRITCHMINOR`; match.
- `PRIMALCRUSHER`: concealed; parent `PRIMPEARL`; hidden parents `VOIDMETAL`, `ELEMENTALPICK`, and `ELEMENTALSHOVEL`; match.
- Other inspected Eldritch parent, hidden-parent, flag, and trigger declarations match the TC4 corpus. No Eldritch declaration uses siblings, auto-unlock, virtual, or aspect triggers.

## Validation

Focused command run and passed:

```text
./scripts/dev.sh gradle test --tests thaumcraft.common.lib.research.ResearchClueAndNotesRuntimeTest --tests thaumcraft.common.lib.research.ScanProgressionRuntimeTest --tests thaumcraft.common.lib.research.ResearchManagerEntityTriggerMatchTest --tests thaumcraft.common.lib.research.ResearchManagerFindMatchingResearchStaticGuardTest --tests thaumcraft.common.lib.network.playerdata.PacketPlayerCompleteRuntimeTest
```

Runtime smoke: skipped. This was a read-only audit with no product changes; no runtime validation was required.

Known limitation: The focused suite passed but does not yet encode the wrong-namespace/same-path reproduction in A-021-F01. No claim of visual, persistence, networking, or note-generation parity is made.
