## Durable Goal Ledger and Sol recursion firewall

For any RECON/audit followed by multi-stage implementation, load `goal-repo-docs` before spawning subagents or editing product files.

Default promotion policy is `production_gate`. Freeze `skill-default` capacity before fan-out: at most 12 audit assignments in one wave, 8 admitted findings, and 12 total fix checkpoints; higher limits require one authoritative source with an exact finite `Budget Grant`; broad “fix everything” wording is not authority to raise capacity. Never use `confirmed_in_scope`: a confirmed delta is not automatically production-relevant or actionable.

Before fan-out, freeze the supported production envelope and finite audit/resource budgets. Every audit subagent writes a progressive `reports/A-*.md`; product files stay read-only during RECON.

A finding may become required only through the Production Admission Gate in the skill. Synthetic-only failures, unsupported paths, generic hardening, cleanup, test debt, advisory review findings, and adjacent bugs default to deferred. For a non-critical candidate, allow at most one bounded reproduction attempt.

After GOAL freeze, no new audit map, no review-of-review, no adjacent auto-promotion, and no budget increase by the agent. Reviews may only prove a frozen finding unresolved or a regression caused by the current diff. Completion is terminal.

If `.opencode/active-goal` exists, rehydrate the ledger, verify hashes/resource counters, and reconcile Git before any product edit. Goal Ledger files and Git evidence are authoritative over chat, todo, DCP, and Compact summaries.
