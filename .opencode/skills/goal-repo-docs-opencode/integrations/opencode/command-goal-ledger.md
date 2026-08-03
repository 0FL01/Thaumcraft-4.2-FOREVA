---
description: Start or resume production-gated durable RECON and implementation
---

Load the `goal-repo-docs` skill before substantive work.

If `.opencode/active-goal` exists, rehydrate it, verify hashes and Resource Governor counters, and reconcile Git before editing. Otherwise initialize a Goal Ledger before RECON or subagent fan-out.

Use promotion policy `production_gate` unless the user/source explicitly requires `explicit_only` or post-RECON `triage`. `confirmed_in_scope` is forbidden.

Freeze the actual supported production envelope and finite budgets before fan-out. Without explicit authoritative limits, use `skill-default`: 12 audit assignments, one wave, 8 admitted findings, 12 total fix checkpoints, and zero scope amendments. Higher limits require one authoritative source with an exact finite `Budget Grant`; broad “fix everything” wording does not raise capacity. A confirmed delta is not a required fix. Promote only findings with an allowed admission basis, concrete supported trigger, concrete impact/contract, direct evidence, and finite fix budget. Allow at most one bounded reproduction attempt for a non-critical candidate; otherwise defer it.

During implementation, do not open a new audit map, review a review, auto-promote adjacent findings, add generic hardening, or raise budgets. Finish immediately when the frozen admitted findings and affected constraints pass.

User objective:

$ARGUMENTS
