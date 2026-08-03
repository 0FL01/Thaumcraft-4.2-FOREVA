#!/usr/bin/env python3
"""Validate traceability, continuity, and closure invariants of a Goal Ledger."""

from __future__ import annotations

import argparse
import hashlib
import json
import re
import subprocess
import sys
from dataclasses import asdict, dataclass
from pathlib import Path
from typing import Iterable

REQUIRED_FILES = ("GOAL.md", "RECON.md", "SOURCES.md", "STATE.md", "LOG.md")
ID_PATTERNS = {
    "S": re.compile(r"^##\s+(S-\d{3,})\s+(?:—|-)\s*(.*?)\s*$", re.MULTILINE),
    "F": re.compile(r"^##\s+(F-\d{3,})\s+(?:—|-)\s*(.*?)\s*$", re.MULTILINE),
    "R": re.compile(r"^##\s+(R-\d{3,})\s+(?:—|-)\s*(.*?)\s*$", re.MULTILINE),
}
ANY_ID = re.compile(r"\b([SARF]-\d{3,})\b")
HEX64 = re.compile(r"^[0-9a-f]{64}$")

FINDING_TYPES = {"defect", "parity", "constraint", "unknown", "test_debt", "benign_delta"}
PROMOTION_POLICIES = {"explicit_only", "production_gate", "triage"}
PRODUCTION_GATES = {"pass", "fail", "not_applicable"}
PASS_ADMISSION_BASES = {
    "explicit_requirement",
    "production_incident",
    "deterministic_supported_path",
    "current_diff_regression",
    "blocks_explicit_requirement",
    "credible_critical_risk",
    "user_override",
}
FAIL_ADMISSION_BASES = {
    "not_reproduced_within_budget",
    "over_capacity",
    "unsupported_or_unreachable",
    "no_concrete_impact",
    "speculative_hardening",
    "over_budget",
    "outside_scope",
    "adjacent_non_requirement",
    "synthetic_only",
    "triage_not_selected",
    "insufficient_authority",
    "insufficient_evidence",
    "duplicate",
    "invalidated",
}
ADMISSION_BUDGET_RE = re.compile(
    r"^reproduce_attempts=(\d+);\s*fix_checkpoints=(\d+);\s*review_passes=(\d+)$"
)
BUDGET_GRANT_RE = re.compile(
    r"^audit_assignments=(\d+);\s*audit_waves=(\d+);\s*required_findings=(\d+);"
    r"\s*total_fix_checkpoints=(\d+);\s*scope_amendments=(\d+);"
    r"\s*implementation_subagent_waves=(\d+)$"
)
OPEN_ENDED_BUDGET_RE = re.compile(
    r"\b(?:unlimited|unbounded|no\s+fixed|as\s+needed|until\s+clean|until\s+no\s+issues|exhaustive|безлимит|без\s+лимита|по\s+необходимости|до\s+полной\s+чистоты)\b",
    re.IGNORECASE,
)
DISPOSITIONS = {
    "required",
    "preserve",
    "constraint",
    "deferred",
    "invalidated",
    "duplicate",
    "blocking_question",
}
GOAL_STATUSES = {"draft", "active", "blocked", "recovery", "complete", "unmet"}
CONTRACT_STATUSES = {"draft", "frozen", "superseded"}
RECON_STATUSES = {"collecting", "adjudicating", "frozen", "superseded"}
CHECKPOINT_STATUSES = {"planned", "in_progress", "verifying", "committing", "closed"}
REQUIRED_STATUSES = {
    "pending",
    "in_progress",
    "verified",
    "blocked",
    "waived_by_user",
    "not_applicable",
    "superseded",
}
PRESERVE_STATUSES = {"pending", "verified", "regressed", "not_applicable", "superseded"}
CONSTRAINT_STATUSES = {"pending", "satisfied", "violated", "not_applicable", "superseded"}
SUCCESS_REQUIRED = {"verified", "waived_by_user", "not_applicable", "superseded"}
SUCCESS_PRESERVE = {"verified", "not_applicable", "superseded"}
SUCCESS_CONSTRAINT = {"satisfied", "not_applicable", "superseded"}
TERMINAL_ASSIGNMENT = re.compile(
    r"^(?:complete|no_findings|blocked|superseded|continued_as\s+A-\d{3,})$"
)

SKILL_DEFAULT_LIMITS = {
    "Audit-Assignments-Cap": 12,
    "Audit-Waves-Cap": 1,
    "Candidate-Reproduction-Cap-Per-Finding": 1,
    "Required-Findings-Cap": 8,
    "Total-Fix-Checkpoints-Cap": 12,
    "Scope-Amendments-Cap": 0,
    "Max Material Replans Per Required Finding": 2,
    "Max Implementation Subagent Waves": 0,
    "Max Closure Review Passes": 1,
}


@dataclass(frozen=True)
class Issue:
    level: str
    code: str
    message: str


class Result:
    def __init__(self) -> None:
        self.issues: list[Issue] = []

    def error(self, code: str, message: str) -> None:
        self.issues.append(Issue("error", code, message))

    def warn(self, code: str, message: str) -> None:
        self.issues.append(Issue("warning", code, message))

    @property
    def errors(self) -> list[Issue]:
        return [item for item in self.issues if item.level == "error"]

    @property
    def warnings(self) -> list[Issue]:
        return [item for item in self.issues if item.level == "warning"]


@dataclass
class Block:
    ident: str
    title: str
    body: str
    fields: dict[str, str]


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def bundle_sha256(goal_dir: Path, relative_roots: Iterable[str]) -> str:
    """Hash paths and bytes deterministically; reject symlinks and missing roots."""
    files: list[Path] = []
    for relative_root in relative_roots:
        root = goal_dir / relative_root
        if not root.exists():
            raise ValueError(f"bundle root is missing: {relative_root}")
        if root.is_symlink():
            raise ValueError(f"bundle root must not be a symlink: {relative_root}")
        if root.is_file():
            files.append(root)
            continue
        for child in root.rglob("*"):
            if child.is_symlink():
                raise ValueError(f"bundle contains symlink: {child.relative_to(goal_dir)}")
            if child.is_file():
                files.append(child)

    digest = hashlib.sha256()
    for path in sorted(files, key=lambda item: item.relative_to(goal_dir).as_posix()):
        relative = path.relative_to(goal_dir).as_posix().encode("utf-8")
        digest.update(relative)
        digest.update(b"\0")
        with path.open("rb") as handle:
            for chunk in iter(lambda: handle.read(1024 * 1024), b""):
                digest.update(chunk)
        digest.update(b"\0")
    return digest.hexdigest()


def header(text: str, key: str) -> str | None:
    match = re.search(rf"(?m)^{re.escape(key)}:\s*(.*?)\s*$", text)
    return match.group(1).strip() if match else None


def bullet_field(text: str, key: str) -> str | None:
    match = re.search(rf"(?m)^-\s+{re.escape(key)}:\s*(.*?)\s*$", text)
    return match.group(1).strip() if match else None


def parse_fields(body: str) -> dict[str, str]:
    fields: dict[str, str] = {}
    for match in re.finditer(r"(?m)^-\s+([^:\n]+):\s*(.*?)\s*$", body):
        fields[match.group(1).strip()] = match.group(2).strip()
    return fields


def parse_blocks(text: str, kind: str, result: Result, filename: str) -> dict[str, Block]:
    pattern = ID_PATTERNS[kind]
    matches = list(pattern.finditer(text))
    blocks: dict[str, Block] = {}
    for index, match in enumerate(matches):
        ident = match.group(1)
        title = match.group(2).strip()
        start = match.end()
        end = matches[index + 1].start() if index + 1 < len(matches) else len(text)
        # A new unrelated level-2 heading ends the block before the next ID block.
        unrelated = re.search(r"(?m)^##\s+(?![SFR]-\d{3,}\b).+$", text[start:end])
        if unrelated:
            end = start + unrelated.start()
        body = text[start:end].strip()
        if ident in blocks:
            result.error("duplicate-id", f"{filename}: duplicate {ident}")
            continue
        blocks[ident] = Block(ident=ident, title=title, body=body, fields=parse_fields(body))
    return blocks


def section(text: str, heading: str) -> str:
    match = re.search(rf"(?m)^##\s+{re.escape(heading)}\s*$", text)
    if not match:
        return ""
    start = match.end()
    next_heading = re.search(r"(?m)^##\s+", text[start:])
    end = start + next_heading.start() if next_heading else len(text)
    return text[start:end].strip()


def ids(value: str | None, prefix: str | None = None) -> set[str]:
    found = {match.group(1) for match in ANY_ID.finditer(value or "")}
    return {item for item in found if prefix is None or item.startswith(prefix + "-")}


def is_placeholder(value: str | None) -> bool:
    if value is None:
        return True
    normalized = value.strip().lower()
    if not normalized:
        return True
    if normalized in {"none until freeze", "tbd", "todo", "unknown", "<none>"}:
        return True
    if "{{" in normalized or "}}" in normalized:
        return True
    return normalized.startswith("<") and normalized.endswith(">")


def parse_nonnegative_int(
    result: Result,
    value: str | None,
    *,
    context: str,
    maximum: int | None = None,
) -> int | None:
    if is_placeholder(value) or not re.fullmatch(r"\d+", (value or "").strip()):
        result.error("finite-budget", f"{context} must be a finite non-negative integer; found {value!r}")
        return None
    parsed = int((value or "0").strip())
    if maximum is not None and parsed > maximum:
        result.error("budget-cap", f"{context}={parsed} exceeds hard maximum {maximum}")
    return parsed


def reject_open_ended_budget(result: Result, value: str | None, *, context: str) -> None:
    if is_placeholder(value):
        result.error("finite-budget", f"{context} is missing or a placeholder")
        return
    assert value is not None
    if OPEN_ENDED_BUDGET_RE.search(value):
        result.error("open-ended-budget", f"{context} contains an open-ended budget: {value!r}")


def require_fields(result: Result, block: Block, names: Iterable[str], context: str) -> None:
    for name in names:
        value = block.fields.get(name)
        if is_placeholder(value):
            result.error("missing-field", f"{context} {block.ident}: field {name!r} is missing or a placeholder")


def parse_status_map(text: str, heading: str) -> tuple[dict[str, tuple[str, str]], list[str]]:
    content = section(text, heading)
    values: dict[str, tuple[str, str]] = {}
    duplicates: list[str] = []
    pattern = re.compile(r"(?m)^-\s+([FR]-\d{3,})\s*\|\s*([a-z_]+)\s*\|\s*evidence:\s*(.*?)\s*$")
    for match in pattern.finditer(content):
        ident, status, evidence = match.groups()
        if ident in values:
            duplicates.append(ident)
        values[ident] = (status, evidence.strip())
    return values, duplicates


def parse_assignments(text: str) -> dict[str, tuple[str, str]]:
    content = section(text, "Audit Coverage")
    assignments: dict[str, tuple[str, str]] = {}
    pattern = re.compile(
        r"(?m)^-\s+(A-\d{3,})\s*\|\s*status:\s*([^|]+?)\s*\|\s*report:\s*([^|]+?)\s*\|"
    )
    for match in pattern.finditer(content):
        assignments[match.group(1)] = (match.group(2).strip(), match.group(3).strip())
    return assignments


def parse_finding_work_counters(text: str) -> tuple[dict[str, tuple[int, int]], list[str]]:
    content = section(text, "Finding Work Counters")
    values: dict[str, tuple[int, int]] = {}
    duplicates: list[str] = []
    pattern = re.compile(
        r"(?m)^-\s+(F-\d{3,})\s*\|\s*checkpoints_started=(\d+)"
        r"\s*\|\s*material_replans=(\d+)\s*$"
    )
    for match in pattern.finditer(content):
        ident = match.group(1)
        if ident in values:
            duplicates.append(ident)
        values[ident] = (int(match.group(2)), int(match.group(3)))
    return values, duplicates


def stamp_state(
    state_path: Path,
    contract_hash: str,
    recon_hash: str,
    sources_hash: str,
    reports_hash: str,
) -> None:
    text = state_path.read_text(encoding="utf-8")
    replacements = {
        "Contract-SHA256": contract_hash,
        "Recon-SHA256": recon_hash,
        "Sources-SHA256": sources_hash,
        "Reports-SHA256": reports_hash,
    }
    for key, value in replacements.items():
        pattern = re.compile(rf"(?m)^{re.escape(key)}:\s*.*$")
        if pattern.search(text):
            text = pattern.sub(f"{key}: {value}", text, count=1)
        else:
            raise ValueError(f"STATE.md is missing {key}")
    state_path.write_text(text, encoding="utf-8")


def safe_report_path(goal_dir: Path, value: str) -> Path | None:
    if is_placeholder(value):
        return None
    candidate = (goal_dir / value).resolve()
    try:
        candidate.relative_to(goal_dir.resolve())
    except ValueError:
        return None
    return candidate


def git_value(goal_dir: Path, args: list[str]) -> str | None:
    try:
        proc = subprocess.run(
            ["git", "-C", str(goal_dir), *args],
            check=False,
            capture_output=True,
            text=True,
        )
    except FileNotFoundError:
        return None
    if proc.returncode != 0:
        return None
    return proc.stdout.strip()


def validate(goal_dir: Path, result: Result) -> None:
    texts: dict[str, str] = {}
    for name in REQUIRED_FILES:
        path = goal_dir / name
        if not path.is_file():
            result.error("missing-file", f"missing required file: {path}")
            continue
        texts[name] = path.read_text(encoding="utf-8")
    if len(texts) != len(REQUIRED_FILES):
        return

    goal = texts["GOAL.md"]
    recon = texts["RECON.md"]
    sources_text = texts["SOURCES.md"]
    state = texts["STATE.md"]

    goal_ids = {header(text, "Goal-ID") for text in texts.values()}
    if None in goal_ids or len(goal_ids) != 1:
        result.error("goal-id", f"Goal-ID must exist and match in all files; found {sorted(str(x) for x in goal_ids)}")

    contract_status = header(goal, "Contract-Status")
    recon_status = header(recon, "Recon-Status")
    goal_status = header(state, "Goal-Status")
    checkpoint_status = bullet_field(state, "Checkpoint Status")
    recon_policy = header(recon, "Audit-Promotion-Policy")
    goal_policy = bullet_field(goal, "Policy")

    if recon_policy == "confirmed_in_scope" or goal_policy == "confirmed_in_scope":
        result.error(
            "forbidden-policy",
            "confirmed_in_scope is forbidden: confirmed evidence is not automatic implementation scope",
        )
    if recon_policy not in PROMOTION_POLICIES:
        result.error("promotion-policy", f"invalid Audit-Promotion-Policy: {recon_policy!r}")
    if goal_policy not in PROMOTION_POLICIES:
        result.error("promotion-policy", f"invalid GOAL Scope Promotion policy: {goal_policy!r}")
    if recon_policy in PROMOTION_POLICIES and goal_policy in PROMOTION_POLICIES and recon_policy != goal_policy:
        result.error("promotion-policy", f"RECON policy {recon_policy!r} differs from GOAL policy {goal_policy!r}")

    if contract_status not in CONTRACT_STATUSES:
        result.error("contract-status", f"invalid Contract-Status: {contract_status!r}")
    if recon_status not in RECON_STATUSES:
        result.error("recon-status", f"invalid Recon-Status: {recon_status!r}")
    if goal_status not in GOAL_STATUSES:
        result.error("goal-status", f"invalid Goal-Status: {goal_status!r}")
    if checkpoint_status not in CHECKPOINT_STATUSES:
        result.error("checkpoint-status", f"invalid Checkpoint Status: {checkpoint_status!r}")

    frozen_execution = goal_status in {"active", "blocked", "recovery", "complete", "unmet"}
    if frozen_execution:
        for field_name in ("Authority", "Production envelope", "Freeze decision"):
            if is_placeholder(bullet_field(goal, field_name)):
                result.error(
                    "scope-promotion",
                    f"GOAL Scope Promotion field {field_name!r} is missing or placeholder",
                )
        authority_value = bullet_field(goal, "Authority")
        if authority_value and not ids(authority_value, "S"):
            result.error("scope-promotion", "GOAL Scope Promotion Authority must cite at least one S-* source")
    if frozen_execution and contract_status != "frozen":
        result.error("not-frozen", f"Goal-Status {goal_status!r} requires Contract-Status: frozen")
    if frozen_execution and recon_status != "frozen":
        result.error("recon-not-frozen", f"Goal-Status {goal_status!r} requires Recon-Status: frozen")

    sources = parse_blocks(sources_text, "S", result, "SOURCES.md")
    findings = parse_blocks(recon, "F", result, "RECON.md")
    outcomes = parse_blocks(goal, "R", result, "GOAL.md")

    if not sources:
        result.error("sources-empty", "SOURCES.md has no S-* entries")

    for source in sources.values():
        require_fields(
            result,
            source,
            (
                "Kind",
                "Authority",
                "Locator",
                "Version/Date/Commit",
                "Fingerprint",
                "Relevant scope",
                "Budget Grant",
            ),
            "source",
        )

    production_envelope_fields = (
        "Supported versions/configurations",
        "Supported entry points/data/lifecycle",
        "Production invariants",
        "Explicitly excluded/unreachable paths",
        "Concrete impact threshold",
        "Critical-risk exception policy",
    )
    if frozen_execution:
        for field_name in production_envelope_fields:
            if is_placeholder(bullet_field(recon, field_name)):
                result.error(
                    "production-envelope",
                    f"RECON Production Relevance Envelope field {field_name!r} is missing or placeholder",
                )

    recon_budget_authority = header(recon, "Budget-Authority")
    budget_authority_is_default = recon_budget_authority == "skill-default"
    budget_grant_values: tuple[int, int, int, int, int, int] | None = None
    if frozen_execution:
        if is_placeholder(recon_budget_authority):
            result.error("budget-authority", "RECON Budget-Authority is missing or placeholder")
        elif not budget_authority_is_default:
            authority_sources = ids(recon_budget_authority, "S")
            if len(authority_sources) != 1:
                result.error(
                    "budget-authority",
                    "RECON Budget-Authority must be skill-default or exactly one authoritative S-* source",
                )
            else:
                source_id = next(iter(authority_sources))
                authority_source = sources.get(source_id)
                if authority_source is None or authority_source.fields.get("Authority") != "authoritative":
                    result.error(
                        "budget-authority",
                        f"RECON Budget-Authority {source_id} must be an authoritative source",
                    )
                else:
                    grant_value = authority_source.fields.get("Budget Grant", "")
                    grant_match = BUDGET_GRANT_RE.fullmatch(grant_value)
                    if not grant_match:
                        result.error(
                            "budget-grant",
                            f"{source_id} Budget Grant must exactly enumerate finite capacity; found {grant_value!r}",
                        )
                    else:
                        budget_grant_values = tuple(int(item) for item in grant_match.groups())

    audit_assignment_cap = parse_nonnegative_int(
        result,
        header(recon, "Audit-Assignments-Cap"),
        context="RECON Audit-Assignments-Cap",
    ) if frozen_execution else None
    audit_wave_cap = parse_nonnegative_int(
        result,
        header(recon, "Audit-Waves-Cap"),
        context="RECON Audit-Waves-Cap",
    ) if frozen_execution else None
    audit_waves_used = parse_nonnegative_int(
        result,
        header(recon, "Audit-Waves-Used"),
        context="RECON Audit-Waves-Used",
    ) if frozen_execution else None
    recon_candidate_cap = parse_nonnegative_int(
        result,
        header(recon, "Candidate-Reproduction-Cap-Per-Finding"),
        context="RECON Candidate-Reproduction-Cap-Per-Finding",
        maximum=1,
    ) if frozen_execution else None
    required_findings_cap = parse_nonnegative_int(
        result,
        header(recon, "Required-Findings-Cap"),
        context="RECON Required-Findings-Cap",
    ) if frozen_execution else None
    total_fix_checkpoints_cap = parse_nonnegative_int(
        result,
        header(recon, "Total-Fix-Checkpoints-Cap"),
        context="RECON Total-Fix-Checkpoints-Cap",
    ) if frozen_execution else None
    scope_amendments_cap = parse_nonnegative_int(
        result,
        header(recon, "Scope-Amendments-Cap"),
        context="RECON Scope-Amendments-Cap",
    ) if frozen_execution else None

    if frozen_execution and budget_authority_is_default:
        recon_defaults = {
            "Audit-Assignments-Cap": audit_assignment_cap,
            "Audit-Waves-Cap": audit_wave_cap,
            "Candidate-Reproduction-Cap-Per-Finding": recon_candidate_cap,
            "Required-Findings-Cap": required_findings_cap,
            "Total-Fix-Checkpoints-Cap": total_fix_checkpoints_cap,
            "Scope-Amendments-Cap": scope_amendments_cap,
        }
        for field_name, value in recon_defaults.items():
            default_limit = SKILL_DEFAULT_LIMITS[field_name]
            if value is not None and value > default_limit:
                result.error(
                    "skill-default-budget",
                    f"RECON {field_name}={value} exceeds skill-default limit {default_limit}; "
                    "cite explicit authoritative budget authority to raise it",
                )
    elif frozen_execution and budget_grant_values is not None:
        grant_recon = {
            "Audit-Assignments-Cap": budget_grant_values[0],
            "Audit-Waves-Cap": budget_grant_values[1],
            "Required-Findings-Cap": budget_grant_values[2],
            "Total-Fix-Checkpoints-Cap": budget_grant_values[3],
            "Scope-Amendments-Cap": budget_grant_values[4],
        }
        actual_recon = {
            "Audit-Assignments-Cap": audit_assignment_cap,
            "Audit-Waves-Cap": audit_wave_cap,
            "Required-Findings-Cap": required_findings_cap,
            "Total-Fix-Checkpoints-Cap": total_fix_checkpoints_cap,
            "Scope-Amendments-Cap": scope_amendments_cap,
        }
        for field_name, granted in grant_recon.items():
            actual = actual_recon[field_name]
            if actual is not None and actual != granted:
                result.error(
                    "budget-grant",
                    f"RECON {field_name}={actual} does not match authoritative Budget Grant={granted}",
                )

    assignments = parse_assignments(recon)
    if frozen_execution and not assignments:
        result.error("audit-coverage", "frozen RECON has no parseable A-* Audit Coverage entries")
    if frozen_execution and audit_assignment_cap is not None and len(assignments) > audit_assignment_cap:
        result.error(
            "audit-budget",
            f"RECON has {len(assignments)} assignments but cap is {audit_assignment_cap}",
        )
    if frozen_execution and audit_waves_used is not None and audit_wave_cap is not None:
        if assignments and audit_waves_used < 1:
            result.error("audit-budget", "frozen RECON with assignments requires at least one used audit wave")
        if audit_waves_used > audit_wave_cap:
            result.error(
                "audit-budget",
                f"Audit-Waves-Used={audit_waves_used} exceeds Audit-Waves-Cap={audit_wave_cap}",
            )
    for assignment_id, (status, report_value) in assignments.items():
        if frozen_execution and not TERMINAL_ASSIGNMENT.fullmatch(status):
            result.error("audit-not-terminal", f"{assignment_id} has non-terminal status {status!r}")
        report_path = safe_report_path(goal_dir, report_value)
        if report_path is None:
            result.error("report-path", f"{assignment_id} has invalid/placeholder report path {report_value!r}")
        elif not report_path.is_file():
            result.error("report-missing", f"{assignment_id} report does not exist: {report_path}")

    required_findings: set[str] = set()
    preserve_findings: set[str] = set()
    constraint_findings: set[str] = set()
    blocking_findings: set[str] = set()
    finding_budgets: dict[str, tuple[int, int, int]] = {}
    finding_attempts_used: dict[str, int] = {}

    finding_required_fields = (
        "Type",
        "Disposition",
        "Severity",
        "Confidence",
        "Source IDs",
        "Audit IDs/Reports",
        "Oracle",
        "Observed",
        "Expected",
        "Exact deltas",
        "Affected paths/symbols",
        "Primary evidence",
        "Production Gate",
        "Admission Basis",
        "Production Trigger/Reachability",
        "Concrete Impact/Contract",
        "Admission Evidence",
        "Admission Budget",
        "Admission Attempts Used",
        "Speculation Boundary",
        "Regression hazards",
        "Outcome",
        "Notes/Disposition reason",
    )

    for finding in findings.values():
        if frozen_execution:
            require_fields(result, finding, finding_required_fields, "finding")
        kind = finding.fields.get("Type", "")
        disposition = finding.fields.get("Disposition", "")
        if kind not in FINDING_TYPES:
            result.error("finding-type", f"{finding.ident}: invalid Type {kind!r}")
        if disposition not in DISPOSITIONS:
            result.error("finding-disposition", f"{finding.ident}: invalid Disposition {disposition!r}")

        source_ids = ids(finding.fields.get("Source IDs"), "S")
        if frozen_execution and not source_ids:
            result.error("finding-source", f"{finding.ident}: no S-* source IDs")
        for source_id in source_ids:
            if source_id not in sources:
                result.error("finding-source", f"{finding.ident}: unknown source {source_id}")

        audit_ids = ids(finding.fields.get("Audit IDs/Reports"), "A")
        if frozen_execution and not audit_ids:
            result.error("finding-audit", f"{finding.ident}: no A-* audit IDs")
        for audit_id in audit_ids:
            if audit_id not in assignments:
                result.error("finding-audit", f"{finding.ident}: unknown audit assignment {audit_id}")

        if kind in {"defect", "parity"} and frozen_execution:
            require_fields(result, finding, ("Observed", "Expected", "Exact deltas", "Primary evidence"), "finding")

        production_gate = finding.fields.get("Production Gate", "")
        admission_basis = finding.fields.get("Admission Basis", "")
        if production_gate not in PRODUCTION_GATES:
            result.error("production-gate", f"{finding.ident}: invalid Production Gate {production_gate!r}")

        admission_budget = finding.fields.get("Admission Budget", "")
        budget_match = ADMISSION_BUDGET_RE.fullmatch(admission_budget)
        budget_values: tuple[int, int, int] | None = None
        if frozen_execution and not budget_match:
            result.error(
                "admission-budget",
                f"{finding.ident}: Admission Budget must match "
                "reproduce_attempts=N; fix_checkpoints=N; review_passes=N",
            )
        elif budget_match:
            budget_values = tuple(int(item) for item in budget_match.groups())
            finding_budgets[finding.ident] = budget_values
            reproduce_attempts, fix_checkpoints, review_passes = budget_values
            if reproduce_attempts > 1:
                result.error("admission-budget", f"{finding.ident}: reproduction attempts exceed hard maximum 1")
            if review_passes > 1:
                result.error("admission-budget", f"{finding.ident}: review passes exceed hard maximum 1")
            if recon_candidate_cap is not None and reproduce_attempts > recon_candidate_cap:
                result.error(
                    "admission-budget",
                    f"{finding.ident}: reproduction attempts {reproduce_attempts} exceed RECON cap {recon_candidate_cap}",
                )

        attempts_used = parse_nonnegative_int(
            result,
            finding.fields.get("Admission Attempts Used"),
            context=f"{finding.ident} Admission Attempts Used",
            maximum=1,
        ) if frozen_execution else None
        if attempts_used is not None:
            finding_attempts_used[finding.ident] = attempts_used
            if budget_values is not None and attempts_used > budget_values[0]:
                result.error(
                    "admission-attempts",
                    f"{finding.ident}: Admission Attempts Used={attempts_used} exceeds "
                    f"reproduce_attempts={budget_values[0]}",
                )
        if admission_basis == "not_reproduced_within_budget" and frozen_execution:
            if budget_values is None or budget_values[0] < 1:
                result.error(
                    "admission-attempts",
                    f"{finding.ident}: not_reproduced_within_budget requires one frozen reproduction attempt",
                )
            elif attempts_used != budget_values[0]:
                result.error(
                    "admission-attempts",
                    f"{finding.ident}: not_reproduced_within_budget requires exhausting the frozen "
                    f"reproduction budget ({budget_values[0]}); used={attempts_used}",
                )

        if admission_basis in {"explicit_requirement", "user_override"}:
            source_ids_for_authority = ids(finding.fields.get("Source IDs"), "S")
            if source_ids_for_authority and not any(
                sources.get(source_id)
                and sources[source_id].fields.get("Authority") == "authoritative"
                for source_id in source_ids_for_authority
            ):
                result.error(
                    "admission-authority",
                    f"{finding.ident}: {admission_basis} requires an authoritative S-* source",
                )
        if admission_basis == "blocks_explicit_requirement":
            linked_findings = ids(finding.fields.get("Admission Evidence"), "F")
            if not linked_findings:
                result.error(
                    "admission-link",
                    f"{finding.ident}: blocks_explicit_requirement must cite the blocked F-* in Admission Evidence",
                )
        if admission_basis == "credible_critical_risk":
            critical_policy = (bullet_field(recon, "Critical-risk exception policy") or "").strip().lower()
            if critical_policy in {"", "none", "not applicable", "n/a"}:
                result.error(
                    "critical-risk",
                    f"{finding.ident}: credible_critical_risk requires an authoritative non-empty critical-risk policy",
                )
            if finding.fields.get("Severity") != "P0":
                result.error(
                    "critical-risk",
                    f"{finding.ident}: credible_critical_risk must be severity P0",
                )

        if disposition == "required":
            required_findings.add(finding.ident)
            if production_gate != "pass":
                result.error("production-gate", f"{finding.ident}: required finding must have Production Gate: pass")
            if admission_basis not in PASS_ADMISSION_BASES:
                result.error(
                    "admission-basis",
                    f"{finding.ident}: required finding has invalid admission basis {admission_basis!r}",
                )
            if recon_policy == "explicit_only" and admission_basis not in {
                "explicit_requirement",
                "current_diff_regression",
                "blocks_explicit_requirement",
                "user_override",
            }:
                result.error(
                    "promotion-policy",
                    f"{finding.ident}: basis {admission_basis!r} is not allowed under explicit_only",
                )
            if recon_policy == "triage" and admission_basis not in {
                "explicit_requirement",
                "current_diff_regression",
                "blocks_explicit_requirement",
                "user_override",
            }:
                result.error(
                    "promotion-policy",
                    f"{finding.ident}: triage requires explicit selection/authority; basis={admission_basis!r}",
                )
            if budget_values is not None and budget_values[1] < 1:
                result.error("admission-budget", f"{finding.ident}: required finding needs at least one fix checkpoint")
            if kind == "test_debt" and admission_basis not in {
                "explicit_requirement",
                "blocks_explicit_requirement",
                "user_override",
            }:
                result.error(
                    "test-debt-scope",
                    f"{finding.ident}: test_debt cannot be required without explicit test authority",
                )
        elif disposition == "preserve":
            preserve_findings.add(finding.ident)
            if production_gate != "not_applicable" or admission_basis != "preserve_control":
                result.error(
                    "production-gate",
                    f"{finding.ident}: preserve finding requires not_applicable / preserve_control",
                )
            if budget_values is not None and budget_values != (0, 0, 0):
                result.error("admission-budget", f"{finding.ident}: preserve finding budget must be 0/0/0")
            if attempts_used not in {None, 0}:
                result.error("admission-attempts", f"{finding.ident}: preserve finding attempts used must be zero")
        elif disposition == "constraint":
            constraint_findings.add(finding.ident)
            if production_gate != "not_applicable" or admission_basis != "constraint":
                result.error(
                    "production-gate",
                    f"{finding.ident}: constraint finding requires not_applicable / constraint",
                )
            if budget_values is not None and budget_values != (0, 0, 0):
                result.error("admission-budget", f"{finding.ident}: constraint finding budget must be 0/0/0")
            if attempts_used not in {None, 0}:
                result.error("admission-attempts", f"{finding.ident}: constraint finding attempts used must be zero")
        else:
            if disposition == "blocking_question":
                blocking_findings.add(finding.ident)
            if production_gate != "fail":
                result.error(
                    "production-gate",
                    f"{finding.ident}: non-admitted finding must have Production Gate: fail",
                )
            if admission_basis not in FAIL_ADMISSION_BASES:
                result.error(
                    "admission-basis",
                    f"{finding.ident}: non-admitted finding has invalid failed-gate basis {admission_basis!r}",
                )
            if budget_values is not None and (budget_values[1] != 0 or budget_values[2] != 0):
                result.error(
                    "admission-budget",
                    f"{finding.ident}: non-admitted finding cannot reserve fix checkpoints or review passes",
                )

    total_required_fix_checkpoints = sum(
        finding_budgets[finding_id][1]
        for finding_id in required_findings
        if finding_id in finding_budgets
    )
    if frozen_execution and required_findings_cap is not None and len(required_findings) > required_findings_cap:
        result.error(
            "admission-capacity",
            f"{len(required_findings)} required findings exceed pre-RECON cap {required_findings_cap}; "
            "defer lower-priority findings or obtain an authoritative amendment",
        )
    if (
        frozen_execution
        and total_fix_checkpoints_cap is not None
        and total_required_fix_checkpoints > total_fix_checkpoints_cap
    ):
        result.error(
            "admission-capacity",
            f"required findings reserve {total_required_fix_checkpoints} fix checkpoints, "
            f"exceeding pre-RECON cap {total_fix_checkpoints_cap}",
        )

    if contract_status == "frozen" and blocking_findings:
        result.error(
            "blocking-question",
            "frozen contract contains scope-changing blocking questions: " + ", ".join(sorted(blocking_findings)),
        )

    if frozen_execution and required_findings and not outcomes:
        result.error("outcomes-empty", "required findings exist but GOAL.md has no R-* outcomes")
    if frozen_execution and goal_status == "active" and not required_findings:
        result.error(
            "no-actionable-work",
            "active goal has zero admitted required findings; close it terminally instead of searching for work",
        )

    governor: dict[str, int] = {}
    if frozen_execution:
        governor_specs = (
            ("Max Required Findings", None),
            ("Frozen Required Finding Count", None),
            ("Max Total Fix Checkpoints", None),
            ("Frozen Total Fix Checkpoints", None),
            ("Max Candidate Reproduction Attempts Per Finding", 1),
            ("Max Material Replans Per Required Finding", 2),
            ("Max Implementation Subagent Waves", 1),
            ("Max Closure Review Passes", 1),
            ("Max Scope Amendments", None),
            ("Adjacent Finding Auto-Promotions", 0),
            ("Post-Closure Work Items", 0),
        )
        for field_name, maximum in governor_specs:
            parsed = parse_nonnegative_int(
                result,
                bullet_field(goal, field_name),
                context=f"GOAL Resource Governor {field_name}",
                maximum=maximum,
            )
            if parsed is not None:
                governor[field_name] = parsed

        goal_required_cap = governor.get("Max Required Findings")
        if (
            goal_required_cap is not None
            and required_findings_cap is not None
            and goal_required_cap != required_findings_cap
        ):
            result.error(
                "budget-mismatch",
                f"GOAL Max Required Findings={goal_required_cap} differs from "
                f"RECON Required-Findings-Cap={required_findings_cap}",
            )
        frozen_count = governor.get("Frozen Required Finding Count")
        if frozen_count is not None and frozen_count != len(required_findings):
            result.error(
                "required-count",
                f"Frozen Required Finding Count={frozen_count}, actual required findings={len(required_findings)}",
            )
        if goal_required_cap is not None and frozen_count is not None and frozen_count > goal_required_cap:
            result.error(
                "required-count",
                f"Frozen Required Finding Count={frozen_count} exceeds Max Required Findings={goal_required_cap}",
            )

        goal_total_fix_cap = governor.get("Max Total Fix Checkpoints")
        if (
            goal_total_fix_cap is not None
            and total_fix_checkpoints_cap is not None
            and goal_total_fix_cap != total_fix_checkpoints_cap
        ):
            result.error(
                "budget-mismatch",
                f"GOAL Max Total Fix Checkpoints={goal_total_fix_cap} differs from "
                f"RECON Total-Fix-Checkpoints-Cap={total_fix_checkpoints_cap}",
            )
        frozen_total_fix = governor.get("Frozen Total Fix Checkpoints")
        if frozen_total_fix is not None and frozen_total_fix != total_required_fix_checkpoints:
            result.error(
                "fix-budget",
                f"Frozen Total Fix Checkpoints={frozen_total_fix}, actual required budget={total_required_fix_checkpoints}",
            )
        if (
            goal_total_fix_cap is not None
            and frozen_total_fix is not None
            and frozen_total_fix > goal_total_fix_cap
        ):
            result.error(
                "fix-budget",
                f"Frozen Total Fix Checkpoints={frozen_total_fix} exceeds Max Total Fix Checkpoints={goal_total_fix_cap}",
            )

        goal_scope_amendment_cap = governor.get("Max Scope Amendments")
        if (
            goal_scope_amendment_cap is not None
            and scope_amendments_cap is not None
            and goal_scope_amendment_cap != scope_amendments_cap
        ):
            result.error(
                "budget-mismatch",
                f"GOAL Max Scope Amendments={goal_scope_amendment_cap} differs from "
                f"RECON Scope-Amendments-Cap={scope_amendments_cap}",
            )

        goal_candidate_cap = governor.get("Max Candidate Reproduction Attempts Per Finding")
        if (
            goal_candidate_cap is not None
            and recon_candidate_cap is not None
            and goal_candidate_cap != recon_candidate_cap
        ):
            result.error(
                "budget-mismatch",
                f"RECON candidate cap {recon_candidate_cap} differs from GOAL governor {goal_candidate_cap}",
            )
        if governor.get("Adjacent Finding Auto-Promotions") not in {None, 0}:
            result.error("recursion-firewall", "Adjacent Finding Auto-Promotions must be zero")
        if governor.get("Post-Closure Work Items") not in {None, 0}:
            result.error("recursion-firewall", "Post-Closure Work Items must be zero")
        if governor.get("Max Implementation Subagent Waves", 0) > 1:
            result.warn(
                "subagent-budget",
                "more than one implementation subagent wave materially increases recursive scope risk",
            )

        budget_authority = bullet_field(goal, "Budget Authority")
        if is_placeholder(budget_authority):
            result.error("budget-authority", "GOAL Resource Governor Budget Authority is missing or placeholder")
        elif budget_authority != recon_budget_authority:
            result.error(
                "budget-authority",
                f"GOAL Budget Authority={budget_authority!r} differs from RECON Budget-Authority={recon_budget_authority!r}",
            )

        if budget_grant_values is not None:
            granted_impl_waves = budget_grant_values[5]
            actual_impl_waves = governor.get("Max Implementation Subagent Waves")
            if actual_impl_waves is not None and actual_impl_waves != granted_impl_waves:
                result.error(
                    "budget-grant",
                    f"GOAL Max Implementation Subagent Waves={actual_impl_waves} does not match "
                    f"authoritative Budget Grant={granted_impl_waves}",
                )

        if budget_authority_is_default:
            goal_defaults = {
                "Max Required Findings": governor.get("Max Required Findings"),
                "Max Total Fix Checkpoints": governor.get("Max Total Fix Checkpoints"),
                "Max Candidate Reproduction Attempts Per Finding": governor.get(
                    "Max Candidate Reproduction Attempts Per Finding"
                ),
                "Max Material Replans Per Required Finding": governor.get(
                    "Max Material Replans Per Required Finding"
                ),
                "Max Implementation Subagent Waves": governor.get("Max Implementation Subagent Waves"),
                "Max Closure Review Passes": governor.get("Max Closure Review Passes"),
                "Max Scope Amendments": governor.get("Max Scope Amendments"),
            }
            default_key_map = {
                "Max Required Findings": "Required-Findings-Cap",
                "Max Total Fix Checkpoints": "Total-Fix-Checkpoints-Cap",
                "Max Candidate Reproduction Attempts Per Finding": "Candidate-Reproduction-Cap-Per-Finding",
                "Max Material Replans Per Required Finding": "Max Material Replans Per Required Finding",
                "Max Implementation Subagent Waves": "Max Implementation Subagent Waves",
                "Max Closure Review Passes": "Max Closure Review Passes",
                "Max Scope Amendments": "Scope-Amendments-Cap",
            }
            for field_name, value in goal_defaults.items():
                default_limit = SKILL_DEFAULT_LIMITS[default_key_map[field_name]]
                if value is not None and value > default_limit:
                    result.error(
                        "skill-default-budget",
                        f"GOAL {field_name}={value} exceeds skill-default limit {default_limit}; "
                        "cite explicit authoritative budget authority to raise it",
                    )

        change_envelope_fields = (
            "Target behavior/artifact",
            "Expected paths, symbols, and direct consumers",
            "Allowed artifacts",
            "Forbidden artifacts",
            "API/platform/dependency boundaries",
            "User or harness budget",
        )
        for field_name in change_envelope_fields:
            value = bullet_field(goal, field_name)
            if is_placeholder(value):
                result.error("change-envelope", f"GOAL Change Envelope field {field_name!r} is missing or placeholder")
        user_budget = bullet_field(goal, "User or harness budget")
        reject_open_ended_budget(
            result,
            user_budget,
            context="GOAL User or harness budget",
        )
        if user_budget and not re.search(r"\d", user_budget) and "resource governor" not in user_budget.lower():
            result.error(
                "finite-budget",
                "GOAL User or harness budget needs a numeric cap or Resource Governor reference",
            )

        work_counters, work_counter_dupes = parse_finding_work_counters(state)
        for finding_id in work_counter_dupes:
            result.error("duplicate-counter", f"STATE.md has duplicate Finding Work Counter for {finding_id}")
        for finding_id in sorted(required_findings):
            if finding_id not in work_counters:
                result.error("finding-counter", f"STATE.md missing Finding Work Counter for required {finding_id}")
                continue
            checkpoints_started, material_replans = work_counters[finding_id]
            budget = finding_budgets.get(finding_id)
            if budget is not None and checkpoints_started > budget[1]:
                result.error(
                    "finding-counter",
                    f"{finding_id}: checkpoints_started={checkpoints_started} exceeds "
                    f"Admission Budget fix_checkpoints={budget[1]}",
                )
            replan_cap = governor.get("Max Material Replans Per Required Finding")
            if replan_cap is not None and material_replans > replan_cap:
                result.error(
                    "finding-counter",
                    f"{finding_id}: material_replans={material_replans} exceeds "
                    f"Max Material Replans Per Required Finding={replan_cap}",
                )
        for finding_id in sorted(work_counters):
            if finding_id not in findings:
                result.error("finding-counter", f"STATE.md counter references unknown {finding_id}")
            elif finding_id not in required_findings:
                result.error(
                    "finding-counter",
                    f"STATE.md must not allocate implementation counters to non-required {finding_id}",
                )

    coverage: dict[str, list[str]] = {finding_id: [] for finding_id in required_findings}
    outcome_covers: dict[str, set[str]] = {}
    for outcome in outcomes.values():
        if frozen_execution:
            require_fields(
                result,
                outcome,
                (
                    "Covers",
                    "Acceptance",
                    "Primary evidence",
                    "Mandatory broader gates",
                    "Change envelope/budget",
                    "Stop/Replan if",
                ),
                "outcome",
            )
        outcome_budget = outcome.fields.get("Change envelope/budget")
        if frozen_execution:
            reject_open_ended_budget(
                result,
                outcome_budget,
                context=f"{outcome.ident} Change envelope/budget",
            )
            if outcome_budget and not re.search(r"\d", outcome_budget) and "resource governor" not in outcome_budget.lower():
                result.error(
                    "finite-budget",
                    f"{outcome.ident} Change envelope/budget needs a numeric cap or Resource Governor reference",
                )
        covered = ids(outcome.fields.get("Covers"), "F")
        outcome_covers[outcome.ident] = covered
        if frozen_execution and not covered:
            result.error("outcome-empty", f"{outcome.ident}: Covers has no F-* IDs")
        for finding_id in covered:
            if finding_id not in findings:
                result.error("outcome-finding", f"{outcome.ident}: unknown finding {finding_id}")
            elif findings[finding_id].fields.get("Disposition") != "required":
                result.error("outcome-disposition", f"{outcome.ident}: {finding_id} is not disposition required")
            else:
                coverage.setdefault(finding_id, []).append(outcome.ident)

    for finding_id in sorted(required_findings):
        owners = coverage.get(finding_id, [])
        if len(owners) != 1:
            result.error(
                "coverage",
                f"{finding_id}: required finding must be covered exactly once; owners={owners or 'none'}",
            )
        declared = ids(findings[finding_id].fields.get("Outcome"), "R")
        if len(declared) != 1:
            result.error("finding-outcome", f"{finding_id}: Outcome must name exactly one R-* ID")
        elif owners and next(iter(declared)) != owners[0]:
            result.error(
                "finding-outcome",
                f"{finding_id}: RECON maps to {sorted(declared)}, but GOAL coverage maps to {owners}",
            )

    preserve_controls = ids(section(goal, "Preserve Controls"), "F")
    constraints = ids(section(goal, "Constraints"), "F")
    for finding_id in sorted(preserve_findings):
        if finding_id not in preserve_controls:
            result.error("preserve-control", f"{finding_id}: preserve finding missing from Preserve Controls")
    for finding_id in preserve_controls:
        if finding_id not in preserve_findings:
            result.error("preserve-control", f"{finding_id}: listed as Preserve Control but disposition is not preserve")
    for finding_id in sorted(constraint_findings):
        if finding_id not in constraints:
            result.error("constraint-map", f"{finding_id}: constraint finding missing from Constraints")
    for finding_id in constraints:
        if finding_id not in constraint_findings:
            result.error("constraint-map", f"{finding_id}: listed under Constraints but disposition is not constraint")

    outcome_status, outcome_dupes = parse_status_map(state, "Outcome Status")
    finding_status, finding_dupes = parse_status_map(state, "Finding Status")
    for ident in outcome_dupes + finding_dupes:
        result.error("duplicate-state", f"STATE.md has duplicate status entry for {ident}")

    for outcome_id in outcomes:
        if frozen_execution and outcome_id not in outcome_status:
            result.error("outcome-state", f"STATE.md missing status for {outcome_id}")
        elif outcome_id in outcome_status and outcome_status[outcome_id][0] not in REQUIRED_STATUSES:
            result.error("outcome-state", f"{outcome_id}: invalid status {outcome_status[outcome_id][0]!r}")

    for finding_id in required_findings:
        entry = finding_status.get(finding_id)
        if frozen_execution and entry is None:
            result.error("finding-state", f"STATE.md missing status for required {finding_id}")
        elif entry and entry[0] not in REQUIRED_STATUSES:
            result.error("finding-state", f"{finding_id}: invalid required status {entry[0]!r}")
    for finding_id in preserve_findings:
        entry = finding_status.get(finding_id)
        if frozen_execution and entry is None:
            result.error("finding-state", f"STATE.md missing status for preserve {finding_id}")
        elif entry and entry[0] not in PRESERVE_STATUSES:
            result.error("finding-state", f"{finding_id}: invalid preserve status {entry[0]!r}")
    for finding_id in constraint_findings:
        entry = finding_status.get(finding_id)
        if frozen_execution and entry is None:
            result.error("finding-state", f"STATE.md missing status for constraint {finding_id}")
        elif entry and entry[0] not in CONSTRAINT_STATUSES:
            result.error("finding-state", f"{finding_id}: invalid constraint status {entry[0]!r}")

    active_outcomes = ids(bullet_field(state, "Active Outcomes"), "R")
    active_findings = ids(bullet_field(state, "Active Findings"), "F")
    for outcome_id in active_outcomes:
        if outcome_id not in outcomes:
            result.error("active-id", f"STATE.md active outcome does not exist: {outcome_id}")
    for finding_id in active_findings:
        if finding_id not in findings:
            result.error("active-id", f"STATE.md active finding does not exist: {finding_id}")
        elif finding_id not in required_findings:
            result.error(
                "active-scope",
                f"STATE.md active finding {finding_id} is not admitted required work",
            )
        else:
            owners = coverage.get(finding_id, [])
            if owners and owners[0] not in active_outcomes:
                result.error(
                    "active-mapping",
                    f"active required {finding_id} belongs to {owners[0]}, which is not an active outcome",
                )

    if checkpoint_status in {"in_progress", "verifying", "committing"} and not active_findings:
        result.error("active-checkpoint", f"Checkpoint Status {checkpoint_status} requires Active Findings")

    if frozen_execution:
        counter_to_limit = (
            ("Implementation Subagent Waves Used", "Max Implementation Subagent Waves"),
            ("Closure Review Passes Used", "Max Closure Review Passes"),
            ("Scope Amendments Used", "Max Scope Amendments"),
            ("Adjacent Finding Auto-Promotions Used", "Adjacent Finding Auto-Promotions"),
            ("Post-Closure Work Items Used", "Post-Closure Work Items"),
        )
        for counter_name, limit_name in counter_to_limit:
            used = parse_nonnegative_int(
                result,
                bullet_field(state, counter_name),
                context=f"STATE Resource Counter {counter_name}",
            )
            limit = governor.get(limit_name)
            if used is not None and limit is not None and used > limit:
                result.error(
                    "resource-counter",
                    f"{counter_name}={used} exceeds {limit_name}={limit}",
                )
            if counter_name in {
                "Adjacent Finding Auto-Promotions Used",
                "Post-Closure Work Items Used",
            } and used not in {None, 0}:
                result.error("recursion-firewall", f"{counter_name} must remain zero")

    if frozen_execution and goal_status == "active":
        for key in ("Hypothesis", "Smallest Next Action", "Expected Evidence", "Stop/Replan If", "Working Set"):
            if is_placeholder(bullet_field(state, key)):
                result.error("rehydration-core", f"STATE.md field {key!r} is missing or placeholder")

    contract_version = header(goal, "Contract-Version")
    state_contract_version = header(state, "Contract-Version")
    recon_version = header(recon, "Recon-Version")
    state_recon_version = header(state, "Recon-Version")
    if contract_version != state_contract_version:
        result.error("version", f"Contract-Version mismatch: GOAL={contract_version!r}, STATE={state_contract_version!r}")
    if recon_version != state_recon_version:
        result.error("version", f"Recon-Version mismatch: RECON={recon_version!r}, STATE={state_recon_version!r}")

    actual_contract_hash = sha256(goal_dir / "GOAL.md")
    actual_recon_hash = sha256(goal_dir / "RECON.md")
    try:
        actual_sources_hash = bundle_sha256(goal_dir, ("SOURCES.md", "sources"))
        actual_reports_hash = bundle_sha256(goal_dir, ("reports",))
    except ValueError as exc:
        result.error("hash-bundle", str(exc))
        actual_sources_hash = ""
        actual_reports_hash = ""

    hash_checks = (
        ("Contract-SHA256", actual_contract_hash, "GOAL.md"),
        ("Recon-SHA256", actual_recon_hash, "RECON.md"),
        ("Sources-SHA256", actual_sources_hash, "source bundle"),
        ("Reports-SHA256", actual_reports_hash, "report bundle"),
    )
    for field_name, actual_hash, label in hash_checks:
        recorded_hash = header(state, field_name) or ""
        if frozen_execution:
            if not HEX64.fullmatch(recorded_hash):
                result.error("hash", f"STATE.md {field_name} is missing or unstamped")
            elif actual_hash and recorded_hash != actual_hash:
                result.error("hash", f"{label} hash differs from frozen STATE.md hash")
        elif HEX64.fullmatch(recorded_hash) and actual_hash and recorded_hash != actual_hash:
            result.warn("hash", f"draft {label} changed after its last stamp")

    for outcome_id, covered in outcome_covers.items():
        state_entry = outcome_status.get(outcome_id)
        if not state_entry:
            continue
        status = state_entry[0]
        covered_statuses = [finding_status.get(finding_id, ("missing", ""))[0] for finding_id in covered]
        if status in SUCCESS_REQUIRED and any(item not in SUCCESS_REQUIRED for item in covered_statuses):
            result.error(
                "derived-status",
                f"{outcome_id} is {status} while covered findings are {covered_statuses}",
            )

    if goal_status == "complete":
        for finding_id in sorted(required_findings):
            status = finding_status.get(finding_id, ("missing", ""))[0]
            if status not in SUCCESS_REQUIRED:
                result.error("completion", f"complete goal has unresolved required {finding_id}: {status}")
        for finding_id in sorted(preserve_findings):
            status = finding_status.get(finding_id, ("missing", ""))[0]
            if status not in SUCCESS_PRESERVE:
                result.error("completion", f"complete goal has unresolved/regressed preserve {finding_id}: {status}")
        for finding_id in sorted(constraint_findings):
            status = finding_status.get(finding_id, ("missing", ""))[0]
            if status not in SUCCESS_CONSTRAINT:
                result.error("completion", f"complete goal has unsatisfied constraint {finding_id}: {status}")
        for outcome_id in sorted(outcomes):
            status = outcome_status.get(outcome_id, ("missing", ""))[0]
            if status not in SUCCESS_REQUIRED:
                result.error("completion", f"complete goal has unresolved outcome {outcome_id}: {status}")
        if active_outcomes or active_findings:
            result.error("completion", "complete goal must have no active outcomes/findings")
        if checkpoint_status != "closed":
            result.error("completion", "complete goal requires Checkpoint Status: closed")
        queued_ids = ids(section(state, "Queued Next Checkpoint"))
        if queued_ids:
            result.error(
                "completion",
                "complete goal must not queue another R/F item: " + ", ".join(sorted(queued_ids)),
            )

    state_branch = header(state, "Git-Branch")
    state_head = header(state, "Git-HEAD")
    live_branch = git_value(goal_dir, ["branch", "--show-current"])
    live_head = git_value(goal_dir, ["rev-parse", "HEAD"])
    if live_branch and state_branch and not is_placeholder(state_branch) and state_branch != live_branch:
        result.warn("git-anchor", f"STATE Git-Branch={state_branch!r}, live branch={live_branch!r}")
    if live_head and state_head and HEX64.fullmatch(state_head.lower()) and state_head.lower() != live_head.lower():
        result.warn("git-anchor", f"STATE Git-HEAD={state_head}, live HEAD={live_head}")


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("goal_dir", type=Path, help="Path to docs/goals/<date>-<slug>")
    parser.add_argument("--stamp", action="store_true", help="Write current GOAL/RECON SHA-256 values into STATE.md")
    parser.add_argument("--json", action="store_true", help="Emit machine-readable JSON")
    parser.add_argument("--strict-warnings", action="store_true", help="Return failure when warnings exist")
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    goal_dir = args.goal_dir.resolve()
    if not goal_dir.is_dir():
        print(f"error: not a directory: {goal_dir}", file=sys.stderr)
        return 2

    if args.stamp:
        try:
            stamp_state(
                goal_dir / "STATE.md",
                sha256(goal_dir / "GOAL.md"),
                sha256(goal_dir / "RECON.md"),
                bundle_sha256(goal_dir, ("SOURCES.md", "sources")),
                bundle_sha256(goal_dir, ("reports",)),
            )
        except (OSError, ValueError) as exc:
            print(f"error: cannot stamp hashes: {exc}", file=sys.stderr)
            return 2

    result = Result()
    validate(goal_dir, result)

    if args.json:
        payload = {
            "goal_dir": str(goal_dir),
            "ok": not result.errors and not (args.strict_warnings and result.warnings),
            "errors": [asdict(item) for item in result.errors],
            "warnings": [asdict(item) for item in result.warnings],
        }
        print(json.dumps(payload, ensure_ascii=False, indent=2))
    else:
        for issue in result.issues:
            print(f"{issue.level.upper()} [{issue.code}] {issue.message}")
        print(f"goal-lint: {len(result.errors)} error(s), {len(result.warnings)} warning(s)")

    if result.errors:
        return 1
    if args.strict_warnings and result.warnings:
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
