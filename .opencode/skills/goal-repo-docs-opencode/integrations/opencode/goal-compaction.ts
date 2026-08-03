import type { Plugin } from "@opencode-ai/plugin"
import { readFile, realpath } from "node:fs/promises"
import { isAbsolute, relative, resolve, sep } from "node:path"

const MAX_STATE_CHARS = 18_000
const MAX_ACTIVE_GOAL_CHARS = 12_000
const MAX_ACTIVE_RECON_CHARS = 20_000
const MAX_SOURCE_CHARS = 8_000
const MAX_LOG_TAIL_CHARS = 8_000

async function safeResolve(root: string, relativePath: string): Promise<string> {
  const rootReal = await realpath(root)
  const candidate = resolve(rootReal, relativePath.trim())
  const candidateReal = await realpath(candidate)
  const back = relative(rootReal, candidateReal)
  if (back === ".." || back.startsWith(`..${sep}`) || isAbsolute(back)) {
    throw new Error(`active goal path escapes worktree: ${relativePath}`)
  }
  return candidateReal
}

function clip(value: string, limit: number): string {
  const text = value.trim()
  if (text.length <= limit) return text
  return `${text.slice(0, limit).trimEnd()}\n[clipped; authoritative content remains in the repository file]`
}

function bulletField(markdown: string, name: string): string {
  const escaped = name.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")
  return markdown.match(new RegExp(`^-\\s+${escaped}:\\s*(.+?)\\s*$`, "m"))?.[1]?.trim() ?? "none"
}

function ids(value: string, prefix: "R" | "F" | "S"): Set<string> {
  const found = new Set<string>()
  for (const match of value.matchAll(/\b([RFS]-\d{3,})\b/g)) {
    if (match[1].startsWith(`${prefix}-`)) found.add(match[1])
  }
  return found
}

function extractSection(markdown: string, heading: string): string {
  const lines = markdown.split(/\r?\n/)
  const marker = `## ${heading}`
  const start = lines.indexOf(marker)
  if (start < 0) return ""
  let end = start + 1
  while (end < lines.length && !lines[end].startsWith("## ")) end += 1
  return lines.slice(start, end).join("\n").trim()
}

function extractBlocks(markdown: string, wanted: Set<string>, prefix: "R" | "F" | "S"): string {
  if (wanted.size === 0) return "(none selected)"
  const lines = markdown.split(/\r?\n/)
  const blocks: string[] = []

  for (let index = 0; index < lines.length; index += 1) {
    const match = lines[index].match(new RegExp(`^##\\s+(${prefix}-\\d{3,})\\b`))
    if (!match || !wanted.has(match[1])) continue

    const start = index
    index += 1
    while (index < lines.length && !lines[index].startsWith("## ")) index += 1
    blocks.push(lines.slice(start, index).join("\n").trim())
    index -= 1
  }

  return blocks.length > 0 ? blocks.join("\n\n") : "(requested IDs not found; rehydrate from files)"
}

async function logWarning(client: any, message: string): Promise<void> {
  try {
    await client.app.log({
      body: {
        service: "goal-compaction",
        level: "warn",
        message,
      },
    })
  } catch {
    // Compaction must not fail because optional logging failed.
  }
}

export const GoalCompactionPlugin: Plugin = async ({ worktree, directory, client }) => {
  const root = resolve(worktree || directory)

  return {
    "experimental.session.compacting": async (_input, output) => {
      try {
        const pointerPath = resolve(root, ".opencode", "active-goal")
        const pointer = (await readFile(pointerPath, "utf8")).split(/\r?\n/)[0]?.trim()
        if (!pointer) return

        const goalDir = await safeResolve(root, pointer)
        const [state, goal, recon, sources, log] = await Promise.all([
          readFile(resolve(goalDir, "STATE.md"), "utf8"),
          readFile(resolve(goalDir, "GOAL.md"), "utf8"),
          readFile(resolve(goalDir, "RECON.md"), "utf8"),
          readFile(resolve(goalDir, "SOURCES.md"), "utf8"),
          readFile(resolve(goalDir, "LOG.md"), "utf8"),
        ])

        const activeOutcomes = ids(bulletField(state, "Active Outcomes"), "R")
        const activeFindings = ids(bulletField(state, "Active Findings"), "F")
        const activeGoal = extractBlocks(goal, activeOutcomes, "R")
        const scopePromotion = extractSection(goal, "Scope Promotion")
        const resourceGovernor = extractSection(goal, "Resource Governor")
        const productionEnvelope = extractSection(recon, "Production Relevance Envelope")
        const activeRecon = extractBlocks(recon, activeFindings, "F")
        const sourceIds = ids(activeRecon, "S")
        const activeSources = extractBlocks(sources, sourceIds, "S")

        output.context.push(`## Durable Goal Ledger context

Active goal path: ${pointer}

### Authoritative STATE.md
${clip(state, MAX_STATE_CHARS)}

### Frozen scope and resource firewall
${clip([scopePromotion, resourceGovernor, productionEnvelope].filter(Boolean).join("\n\n") || "(missing; run goal_lint.py)", MAX_ACTIVE_GOAL_CHARS)}

### Active outcome definitions
${clip(activeGoal, MAX_ACTIVE_GOAL_CHARS)}

### Active atomic findings
${clip(activeRecon, MAX_ACTIVE_RECON_CHARS)}

### Referenced source entries
${clip(activeSources, MAX_SOURCE_CHARS)}

### Material log tail
${clip(log.slice(-MAX_LOG_TAIL_CHARS), MAX_LOG_TAIL_CHARS)}

### Compaction continuation rules
- The repository Goal Ledger and Git evidence are authoritative; this generated summary is only a navigation aid.
- Preserve the exact active goal path, State-Revision, Contract/RECON versions and all frozen bundle hashes, promotion policy, Production Admission Gate, Resource Governor/counters, active R/F IDs, hypothesis, smallest next action, expected evidence, stop/replan condition, Git branch/HEAD, working set, and blockers.
- Confirmed findings are not automatic scope. Never promote adjacent discoveries, synthetic-only failures, cleanup, hardening, or review findings unless a versioned user/source-authorized amendment passes the gate.
- Do not infer that a finding or outcome is verified, waived, complete, or clean unless STATE.md says so with evidence. Stop at the frozen reproduction/replan/review budget; do not review the review.
- The first post-compaction action is to read the active pointer and STATE.md, verify hashes and resource counters, inspect live Git status/HEAD/diff, and reload active R/F/S entries before editing.
`)
      } catch (error) {
        const message = error instanceof Error ? error.message : String(error)
        await logWarning(client, `Could not inject Goal Ledger context: ${message}`)
      }
    },
  }
}
