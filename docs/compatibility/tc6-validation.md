# TC6 compatibility validation

This port provides a hybrid compatibility layer for the pinned addon corpus. It
does not implement every Thaumcraft 6 gameplay subsystem.

## Evidence and support levels

- `EXACT`: the observed ABI and tested contract match the donor contract.
- `PROJECTED`: the TC6 operation is backed by canonical TC4 state with a
  documented lossy mapping.
- `LINK_ONLY`: the symbol resolves, but no gameplay-semantic guarantee is made.
- `UNSUPPORTED`: the ABI may exist, but the operation has no safe TC4 backend
  and must not silently maintain substitute state.

`docs/compatibility/abi/tc6-target.txt` classifies every distinct symbol in the
pinned addon demand snapshot. Rules are ordered in
`scripts/tc6-semantic-policy.txt`; a new unclassified symbol fails validation.
Addon names are evidence for a symbol, not production special cases.

Known unsupported observed operations are TC6 chunk aura/flux mutation and TC6
JSON research-location ingestion. Chunk aura access fails closed instead of
creating a second world-state store. Untyped `drainVis` is separately
`PROJECTED` onto the canonical TC4 primal vis network.

## Inputs

- Donor: `Thaumcraft-1.12.2-6.1.BETA26.jar`, SHA-256 recorded in
  `docs/compatibility/abi/tc6-6.1.BETA26-api.txt`.
- Corpus: original production jars and hashes in
  `scripts/tc6-addon-corpus.txt`; jars remain local under `.smoke/`.
- Demand: `docs/compatibility/abi/tc6-addon-demand.txt`.
- Accepted semantic target: `docs/compatibility/abi/tc6-target.txt`.
- Complete donor-to-target API gap set:
  `docs/compatibility/abi/tc6-current-gaps.txt`.

## Gates

`./scripts/dev.sh compat-validate` verifies donor provenance and ABI, corpus
hashes and demand, exact JVM linkage against the final universal jar, and full
semantic classification. It also verifies the complete donor-to-target gap
snapshot so new regressions or newly closed gaps require explicit review.
Corpus rows marked `supported` are the reviewed linkage floor and fail on any
unresolved class, field, or method; visible donor gaps outside that bounded floor
are not claimed as supported.

`./scripts/dev.sh compat-release` runs normal compile/test/reobfuscation and MCP
leak checks, `compat-validate`, the configured supported dedicated-server modsets,
then a final build and artifact/compatibility check.

ForgeGradle dev smoke uses owner-aware SRG-to-MCP copies only in ignored
`run/smoke-remapped/`. Source jars are hash-checked and never modified. This
server gate does not by itself prove client rendering or full TC6 gameplay
semantics; those remain bounded by the classifications above.

To refresh a reviewed snapshot intentionally:

```text
python3 scripts/tc6-compat.py abi ... --output docs/compatibility/abi/tc6-6.1.BETA26-api.txt
python3 scripts/tc6-compat.py abi-diff ... --output docs/compatibility/abi/tc6-current-gaps.txt
python3 scripts/tc6-compat.py demand ... --output docs/compatibility/abi/tc6-addon-demand.txt
python3 scripts/tc6-compat.py target --demand docs/compatibility/abi/tc6-addon-demand.txt --policy scripts/tc6-semantic-policy.txt --output docs/compatibility/abi/tc6-target.txt
```

Snapshot changes require review; routine validation always uses `--check`.
