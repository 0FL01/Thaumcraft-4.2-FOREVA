import { readFile } from "node:fs/promises"
import { resolve } from "node:path"

const LIMIT = 16_000

export const GoalAnchorCompaction = async ({ worktree, directory }) => {
  const root = resolve(worktree || directory || process.cwd())
  const path = resolve(root, ".opencode", "goal.md")

  return {
    "experimental.session.compacting": async (_input, output) => {
      try {
        let anchor = (await readFile(path, "utf8")).trim()
        if (!anchor) return
        if (anchor.length > LIMIT) {
          anchor = `${anchor.slice(0, LIMIT).trimEnd()}\n[clipped; read .opencode/goal.md after compaction]`
        }

        output.context.push(`## Durable repo-local goal anchor

The following file is the current durable continuation anchor. Preserve its objective, done criteria, constraints, non-goals, decisions, exact technical details, Resume fields, verification state, and the distinction between current and deferred work. Do not infer completion or promote deferred items. After compaction, re-read the file and reconcile live Git before editing.

Path: .opencode/goal.md

${anchor}`)
      } catch {
        // Continuity aid only: never block OpenCode's native compaction.
        return
      }
    },
  }
}
