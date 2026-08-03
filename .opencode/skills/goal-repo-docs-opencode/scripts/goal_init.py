#!/usr/bin/env python3
"""Scaffold a durable Goal Ledger from the bundled templates."""

from __future__ import annotations

import argparse
import datetime as dt
import re
import subprocess
import sys
from pathlib import Path

TEMPLATE_FILES = ("GOAL.md", "RECON.md", "SOURCES.md", "STATE.md", "LOG.md")


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


def slugify(value: str) -> str:
    value = value.strip().lower()
    value = re.sub(r"[^a-z0-9]+", "-", value)
    value = re.sub(r"-{2,}", "-", value).strip("-")
    return value or "goal"


def render(template: str, *, title: str, goal_id: str, date: str) -> str:
    return (
        template.replace("{{TITLE}}", title)
        .replace("{{GOAL_ID}}", goal_id)
        .replace("{{DATE}}", date)
    )


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--title", required=True, help="Human-readable objective title")
    parser.add_argument("--slug", help="Directory slug; defaults to a normalized title")
    parser.add_argument("--goal-id", help="Stable goal ID; defaults to goal-YYYYMMDD-<slug>")
    parser.add_argument("--date", help="YYYY-MM-DD; defaults to the local calendar date")
    parser.add_argument("--root", type=Path, default=Path.cwd(), help="Repository start/root directory")
    parser.add_argument(
        "--goals-dir",
        default="docs/goals",
        help="Goal parent relative to repository root (default: docs/goals)",
    )
    parser.add_argument("--no-activate", action="store_true", help="Do not write .opencode/active-goal")
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    start = args.root.resolve()
    root = git_root(start) or start

    date = args.date or dt.date.today().isoformat()
    if not re.fullmatch(r"\d{4}-\d{2}-\d{2}", date):
        print(f"error: invalid date {date!r}; expected YYYY-MM-DD", file=sys.stderr)
        return 2

    slug = slugify(args.slug or args.title)
    goal_id = args.goal_id or f"goal-{date.replace('-', '')}-{slug}"
    goal_dir = (root / args.goals_dir / f"{date}-{slug}").resolve()

    try:
        goal_dir.relative_to(root)
    except ValueError:
        print("error: goal directory escapes repository root", file=sys.stderr)
        return 2

    if goal_dir.exists() and any(goal_dir.iterdir()):
        print(f"error: refusing to overwrite non-empty {goal_dir}", file=sys.stderr)
        return 1

    templates_dir = Path(__file__).resolve().parents[1] / "templates"
    missing = [name for name in TEMPLATE_FILES if not (templates_dir / name).is_file()]
    if missing:
        print(f"error: missing bundled templates: {', '.join(missing)}", file=sys.stderr)
        return 1

    goal_dir.mkdir(parents=True, exist_ok=True)
    (goal_dir / "reports").mkdir(exist_ok=True)
    (goal_dir / "sources").mkdir(exist_ok=True)
    (goal_dir / "evidence").mkdir(exist_ok=True)
    (goal_dir / "reports" / ".gitkeep").touch()
    (goal_dir / "sources" / ".gitkeep").touch()
    (goal_dir / "evidence" / ".gitkeep").touch()

    for name in TEMPLATE_FILES:
        source = (templates_dir / name).read_text(encoding="utf-8")
        (goal_dir / name).write_text(
            render(source, title=args.title, goal_id=goal_id, date=date),
            encoding="utf-8",
        )

    if not args.no_activate:
        pointer = root / ".opencode" / "active-goal"
        pointer.parent.mkdir(parents=True, exist_ok=True)
        relative_goal = goal_dir.relative_to(root).as_posix()
        pointer.write_text(relative_goal + "\n", encoding="utf-8")

    print(goal_dir.relative_to(root).as_posix())
    print("Next: register sources, freeze production envelope and finite budgets, define the audit map, apply the Production Admission Gate, then run goal_lint.py.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
