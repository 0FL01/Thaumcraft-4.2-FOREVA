#!/usr/bin/env python3
"""Print a compact, durable rehydration capsule for the active Goal Ledger."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
from pathlib import Path

ID_RE = re.compile(r"\b([RSF]-\d{3,})\b")


def git_root(start: Path) -> Path | None:
    try:
        result = subprocess.run(
            ["git", "-C", str(start), "rev-parse", "--show-toplevel"],
            check=True,
            capture_output=True,
            text=True,
        )
    except (FileNotFoundError, subprocess.CalledProcessError):
        return None
    return Path(result.stdout.strip()).resolve()


def safe_resolve(root: Path, value: str) -> Path:
    candidate = (root / value.strip()).resolve()
    try:
        candidate.relative_to(root)
    except ValueError as exc:
        raise ValueError(f"path escapes repository root: {value!r}") from exc
    return candidate


def extract_field(text: str, name: str) -> str:
    match = re.search(rf"(?m)^- {re.escape(name)}:\s*(.+?)\s*$", text)
    return match.group(1).strip() if match else "none"


def extract_level2_blocks(text: str, wanted: set[str]) -> str:
    lines = text.splitlines()
    blocks: list[str] = []
    i = 0
    while i < len(lines):
        match = re.match(r"^##\s+([RF]-\d{3,})\b", lines[i])
        if not match or match.group(1) not in wanted:
            i += 1
            continue
        start = i
        i += 1
        while i < len(lines) and not lines[i].startswith("## "):
            i += 1
        blocks.append("\n".join(lines[start:i]).strip())
    return "\n\n".join(blocks)


def extract_source_ids(text: str) -> set[str]:
    return {match.group(1) for match in ID_RE.finditer(text) if match.group(1).startswith("S-")}


def extract_source_blocks(text: str, wanted: set[str]) -> str:
    lines = text.splitlines()
    blocks: list[str] = []
    i = 0
    while i < len(lines):
        match = re.match(r"^##\s+(S-\d{3,})\b", lines[i])
        if not match or match.group(1) not in wanted:
            i += 1
            continue
        start = i
        i += 1
        while i < len(lines) and not lines[i].startswith("## "):
            i += 1
        blocks.append("\n".join(lines[start:i]).strip())
    return "\n\n".join(blocks)


def clip(text: str, limit: int) -> str:
    text = text.strip()
    if len(text) <= limit:
        return text
    return text[:limit].rstrip() + "\n[clipped; read the authoritative file for the rest]"


def run_git(root: Path, args: list[str]) -> str:
    try:
        result = subprocess.run(
            ["git", "-C", str(root), *args],
            check=False,
            capture_output=True,
            text=True,
        )
    except FileNotFoundError:
        return "git unavailable"
    output = (result.stdout or result.stderr).strip()
    return output or "(empty)"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=Path.cwd(), help="Repository start/root directory")
    parser.add_argument("--goal-dir", help="Goal directory relative to root; defaults to .opencode/active-goal")
    parser.add_argument("--max-chars", type=int, default=48000, help="Approximate output cap")
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    start = args.root.resolve()
    root = git_root(start) or start

    if args.goal_dir:
        relative_goal = args.goal_dir.strip()
    else:
        pointer = root / ".opencode" / "active-goal"
        if not pointer.is_file():
            print("error: .opencode/active-goal is missing", file=sys.stderr)
            return 1
        relative_goal = pointer.read_text(encoding="utf-8").splitlines()[0].strip()

    try:
        goal_dir = safe_resolve(root, relative_goal)
    except ValueError as exc:
        print(f"error: {exc}", file=sys.stderr)
        return 1

    paths = {name: goal_dir / name for name in ("STATE.md", "GOAL.md", "RECON.md", "SOURCES.md", "LOG.md")}
    missing = [name for name, path in paths.items() if not path.is_file()]
    if missing:
        print(f"error: missing ledger files in {goal_dir}: {', '.join(missing)}", file=sys.stderr)
        return 1

    state = paths["STATE.md"].read_text(encoding="utf-8")
    goal = paths["GOAL.md"].read_text(encoding="utf-8")
    recon = paths["RECON.md"].read_text(encoding="utf-8")
    sources = paths["SOURCES.md"].read_text(encoding="utf-8")
    log = paths["LOG.md"].read_text(encoding="utf-8")

    active_r = {x for x in ID_RE.findall(extract_field(state, "Active Outcomes")) if x.startswith("R-")}
    active_f = {x for x in ID_RE.findall(extract_field(state, "Active Findings")) if x.startswith("F-")}
    goal_blocks = extract_level2_blocks(goal, active_r)
    finding_blocks = extract_level2_blocks(recon, active_f)
    source_ids = extract_source_ids(finding_blocks)
    source_blocks = extract_source_blocks(sources, source_ids)

    git_state = "\n".join(
        [
            f"Branch: {run_git(root, ['branch', '--show-current'])}",
            f"HEAD: {run_git(root, ['rev-parse', 'HEAD'])}",
            "Status:",
            run_git(root, ["status", "--short"]),
        ]
    )

    per_section = max(4000, args.max_chars // 5)
    output = f"""# Goal Ledger rehydration capsule

Active goal: {goal_dir.relative_to(root).as_posix()}

## Authoritative STATE.md
{clip(state, per_section)}

## Active GOAL outcomes
{clip(goal_blocks or '(none selected)', per_section)}

## Active RECON findings
{clip(finding_blocks or '(none selected)', per_section)}

## Referenced sources
{clip(source_blocks or '(none selected)', per_section // 2)}

## Material log tail
{clip(log[-per_section:], per_section)}

## Live Git anchor
{git_state}

Continuation rule: repository files and Git evidence are authoritative. Verify hashes and reconcile discrepancies before edits. Never infer verified or complete status from a compaction summary.
"""
    print(clip(output, args.max_chars))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
