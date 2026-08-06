#!/usr/bin/env python3
"""Small helper for the single-file OpenCode Goal Anchor."""

from __future__ import annotations

import argparse
import datetime as dt
import re
import shutil
import subprocess
import sys
from pathlib import Path

ANCHOR = Path(".opencode/goal.md")
SOFT_LIMIT = 12_000
HARD_LIMIT = 16_000
REQUIRED_HEADINGS = (
    "## Objective",
    "## Done",
    "## Constraints",
    "## Non-goals",
    "## Resume",
    "## Decisions",
    "## Findings / Progress",
    "## Verification",
    "## Deferred",
    "## Sources",
)
VALID_STATUSES = {"active", "blocked", "complete"}


def run_git(root: Path, *args: str) -> str:
    try:
        result = subprocess.run(
            ["git", "-C", str(root), *args],
            check=False,
            capture_output=True,
            text=True,
        )
    except FileNotFoundError:
        return "git-unavailable"
    return (result.stdout or result.stderr).strip() or "(empty)"


def repo_root(start: Path) -> Path:
    value = run_git(start, "rev-parse", "--show-toplevel")
    if value not in {"git-unavailable", "(empty)"} and "fatal:" not in value.lower():
        return Path(value).resolve()
    return start.resolve()


def anchor_path(root: Path) -> Path:
    return root / ANCHOR


def git_ignored(root: Path, path: Path) -> bool:
    try:
        relative = path.resolve().relative_to(root.resolve())
        result = subprocess.run(
            ["git", "-C", str(root), "check-ignore", "-q", "--no-index", relative.as_posix()],
            check=False,
            capture_output=True,
            text=True,
        )
    except (FileNotFoundError, ValueError):
        return False
    return result.returncode == 0


def git_tracked(root: Path, path: Path) -> bool:
    try:
        relative = path.resolve().relative_to(root.resolve())
        result = subprocess.run(
            ["git", "-C", str(root), "ls-files", "--error-unmatch", "--", relative.as_posix()],
            check=False,
            capture_output=True,
            text=True,
        )
    except (FileNotFoundError, ValueError):
        return False
    return result.returncode == 0


def status_of(text: str) -> str | None:
    match = re.search(r"(?m)^Status:\s*([a-z_]+)\s*$", text)
    return match.group(1) if match else None


def slugify(value: str) -> str:
    slug = re.sub(r"[^a-z0-9]+", "-", value.lower()).strip("-")
    return slug or "goal"


def render_template(template: str, title: str, root: Path) -> str:
    updated = dt.datetime.now().astimezone().isoformat(timespec="seconds")
    branch = run_git(root, "branch", "--show-current")
    head = run_git(root, "rev-parse", "--short=12", "HEAD")
    if branch in {"(empty)", "git-unavailable"} or branch.startswith("fatal:"):
        branch = "unknown"
    if head in {"(empty)", "git-unavailable"} or head.startswith("fatal:"):
        head = "unknown"
    return (
        template.replace("{{TITLE}}", title.strip())
        .replace("{{UPDATED}}", updated)
        .replace("{{BRANCH}}", branch)
        .replace("{{HEAD}}", head)
    )


def cmd_init(args: argparse.Namespace, root: Path) -> int:
    path = anchor_path(root)
    if path.exists():
        current = path.read_text(encoding="utf-8")
        current_status = status_of(current) or "unknown"
        print(f"error: {ANCHOR} already exists with status={current_status}; archive or edit it first", file=sys.stderr)
        return 1

    template_path = Path(__file__).resolve().parents[1] / "templates" / "GOAL.md"
    template = template_path.read_text(encoding="utf-8")
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(render_template(template, args.title, root), encoding="utf-8")
    if git_tracked(root, path):
        print(f"warning: {ANCHOR} is tracked; ignore rules will not keep it out of product diffs", file=sys.stderr)
    elif not git_ignored(root, path):
        print(f"warning: {ANCHOR} is not ignored; commit it intentionally or add it to .git/info/exclude", file=sys.stderr)
    print(ANCHOR.as_posix())
    return 0


def field_value(text: str, name: str) -> str | None:
    match = re.search(rf"(?m)^- {re.escape(name)}:\s*(.*?)\s*$", text)
    return match.group(1).strip() if match else None


def is_placeholder(value: str | None) -> bool:
    if not value:
        return True
    lowered = value.lower()
    return value.startswith("<") or lowered in {"todo", "tbd", "unknown", "none"}


def check_anchor(path: Path) -> tuple[list[str], list[str]]:
    errors: list[str] = []
    warnings: list[str] = []
    if not path.is_file():
        return [f"missing {ANCHOR}"], warnings

    text = path.read_text(encoding="utf-8")
    size = len(text)
    if size > HARD_LIMIT:
        errors.append(f"anchor is {size} chars; hard limit is {HARD_LIMIT}")
    elif size > SOFT_LIMIT:
        warnings.append(f"anchor is {size} chars; compact it below {SOFT_LIMIT}")

    status = status_of(text)
    if status not in VALID_STATUSES:
        errors.append(f"Status must be one of {sorted(VALID_STATUSES)}, got {status!r}")

    for heading in REQUIRED_HEADINGS:
        if heading not in text:
            errors.append(f"missing heading: {heading}")

    if "<protect>" not in text or "</protect>" not in text:
        errors.append("Resume section must remain inside <protect>...</protect>")

    if "<One observable end state.>" in text:
        warnings.append("Objective is still a placeholder")
    if re.search(r"(?m)^- \[ \] <Observable acceptance criterion>$", text):
        warnings.append("Done is still a placeholder")

    if status in {"active", "blocked"}:
        for name in ("Now", "Why", "Next", "Expected proof", "Stop/replan if", "Working set"):
            if is_placeholder(field_value(text, name)):
                warnings.append(f"Resume field {name!r} is empty/placeholder")

    blocker = (field_value(text, "Blocker") or "").lower()
    if status == "blocked" and blocker in {"", "none", "unknown", "todo", "tbd"}:
        warnings.append("blocked anchor should name a concrete Blocker")

    if status == "complete":
        unchecked = re.findall(r"(?m)^- \[ \] ", text)
        if unchecked:
            errors.append("complete anchor still has unchecked Done criteria")
        for name in ("Now", "Next"):
            value = (field_value(text, name) or "").lower()
            if value != "none":
                warnings.append(f"complete anchor should set {name}: none")
        pending = (field_value(text, "Pending") or "").lower()
        if pending != "none":
            errors.append("complete anchor must set Verification / Pending: none")
        if blocker not in {"", "none"}:
            errors.append("complete anchor must set Blocker: none")
        if is_placeholder(field_value(text, "Passed")):
            warnings.append("complete anchor should record final verification under Passed")

    if re.search(r"(?i)(api[_-]?key|token|password|secret)\s*[:=]\s*\S+", text):
        warnings.append("possible secret-like value; inspect before committing or sharing")

    return errors, warnings


def cmd_check(_args: argparse.Namespace, root: Path) -> int:
    errors, warnings = check_anchor(anchor_path(root))
    for item in errors:
        print(f"ERROR: {item}")
    for item in warnings:
        print(f"WARN: {item}")
    print(f"goal-check: {len(errors)} error(s), {len(warnings)} warning(s)")
    return 1 if errors else 0


def cmd_show(_args: argparse.Namespace, root: Path) -> int:
    path = anchor_path(root)
    if not path.is_file():
        print(f"error: missing {ANCHOR}", file=sys.stderr)
        return 1
    print(path.read_text(encoding="utf-8").rstrip())
    print("\n--- LIVE GIT ---")
    print(f"branch: {run_git(root, 'branch', '--show-current')}")
    print(f"head: {run_git(root, 'rev-parse', '--short=12', 'HEAD')}")
    print("status:")
    print(run_git(root, "status", "--short"))
    print("diff-stat:")
    print(run_git(root, "diff", "--stat"))
    return 0


def cmd_archive(args: argparse.Namespace, root: Path) -> int:
    path = anchor_path(root)
    if not path.is_file():
        print(f"error: missing {ANCHOR}", file=sys.stderr)
        return 1
    text = path.read_text(encoding="utf-8")
    if status_of(text) != "complete" and not args.force:
        print("error: archive requires Status: complete (or --force)", file=sys.stderr)
        return 1

    title_match = re.search(r"(?m)^# Goal Anchor:\s*(.+?)\s*$", text)
    title = title_match.group(1) if title_match else "goal"
    stamp = dt.datetime.now().astimezone().strftime("%Y%m%d-%H%M%S")
    target_dir = root / ".opencode" / "goal-history"
    target_dir.mkdir(parents=True, exist_ok=True)
    target = target_dir / f"{stamp}-{slugify(title)}.md"
    if target.exists():
        print(f"error: archive target already exists: {target.relative_to(root)}", file=sys.stderr)
        return 1
    if not git_ignored(root, target):
        print(
            "warning: .opencode/goal-history/ is not ignored; the archived anchor will appear as untracked",
            file=sys.stderr,
        )
    shutil.move(str(path), target)
    print(target.relative_to(root).as_posix())
    return 0


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=Path.cwd(), help="repository start directory")
    sub = parser.add_subparsers(dest="command", required=True)

    init = sub.add_parser("init", help="create .opencode/goal.md")
    init.add_argument("--title", required=True)

    sub.add_parser("show", help="print the anchor and minimal live Git state")
    sub.add_parser("check", help="run lightweight structural checks")

    archive = sub.add_parser("archive", help="move a completed anchor out of the active path")
    archive.add_argument("--force", action="store_true", help="archive even when status is not complete")
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    root = repo_root(args.root)
    commands = {
        "init": cmd_init,
        "show": cmd_show,
        "check": cmd_check,
        "archive": cmd_archive,
    }
    return commands[args.command](args, root)


if __name__ == "__main__":
    raise SystemExit(main())
