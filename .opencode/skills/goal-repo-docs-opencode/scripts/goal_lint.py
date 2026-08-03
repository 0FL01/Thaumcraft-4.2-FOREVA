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

    if contract_status not in CONTRACT_STATUSES:
        result.error("contract-status", f"invalid Contract-Status: {contract_status!r}")
    if recon_status not in RECON_STATUSES:
        result.error("recon-status", f"invalid Recon-Status: {recon_status!r}")
    if goal_status not in GOAL_STATUSES:
        result.error("goal-status", f"invalid Goal-Status: {goal_status!r}")
    if checkpoint_status not in CHECKPOINT_STATUSES:
        result.error("checkpoint-status", f"invalid Checkpoint Status: {checkpoint_status!r}")

    frozen_execution = goal_status in {"active", "blocked", "recovery", "complete", "unmet"}
    if frozen_execution and contract_status != "frozen":
        result.error("not-frozen", f"Goal-Status {goal_status!r} requires Contract-Status: frozen")
    if frozen_execution and recon_status != "frozen":
        result.error("recon-not-frozen", f"Goal-Status {goal_status!r} requires Recon-Status: frozen")

    sources = parse_blocks(sources_text, "S", result, "SOURCES.md")
    findings = parse_blocks(recon, "F", result, "RECON.md")
    outcomes = parse_blocks(goal, "R", result, "GOAL.md")

    if not sources:
        result.error("sources-empty", "SOURCES.md has no S-* entries")
    if frozen_execution and not findings:
        result.error("findings-empty", "frozen execution has no F-* findings")
    if frozen_execution and not outcomes:
        result.error("outcomes-empty", "frozen execution has no R-* outcomes")

    for source in sources.values():
        require_fields(
            result,
            source,
            ("Kind", "Authority", "Locator", "Version/Date/Commit", "Fingerprint", "Relevant scope"),
            "source",
        )

    assignments = parse_assignments(recon)
    if frozen_execution and not assignments:
        result.error("audit-coverage", "frozen RECON has no parseable A-* Audit Coverage entries")
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

        if disposition == "required":
            required_findings.add(finding.ident)
        elif disposition == "preserve":
            preserve_findings.add(finding.ident)
        elif disposition == "constraint":
            constraint_findings.add(finding.ident)
        elif disposition == "blocking_question":
            blocking_findings.add(finding.ident)

    if contract_status == "frozen" and blocking_findings:
        result.error(
            "blocking-question",
            "frozen contract contains scope-changing blocking questions: " + ", ".join(sorted(blocking_findings)),
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
        elif finding_id in required_findings:
            owners = coverage.get(finding_id, [])
            if owners and owners[0] not in active_outcomes:
                result.error(
                    "active-mapping",
                    f"active required {finding_id} belongs to {owners[0]}, which is not an active outcome",
                )

    if checkpoint_status in {"in_progress", "verifying", "committing"} and not active_findings:
        result.error("active-checkpoint", f"Checkpoint Status {checkpoint_status} requires Active Findings")
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
