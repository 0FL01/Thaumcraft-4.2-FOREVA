---
description: General-purpose agent for researching complex questions and executing multi-step tasks. Use this agent to execute multiple units of work in parallel.
mode: subagent
model: openai/gpt-5.6-luna
variant: xhigh
permission:
  doom_loop: ask

  external_directory:
    "*": ask
    "~/.cargo/**": allow
    "~/.cargo*": allow
    "~/go/pkg/mod/**": allow
    "~/go/pkg/mod": allow
    "~/.local/lib/**": allow
    "~/miniconda3/lib/**": allow
    "~/.cache/uv/**": allow
    "~/.gradle/caches": allow
    "~/.gradle/*": allow
    "~/.ssh*": deny
    "~/.ssh/**": deny
    "$HOME/.ssh*": deny
    "$HOME/.ssh/**": deny
    "/tmp": deny
    "/tmp/": deny
    "/tmp/*": deny
    "/tmp/**": deny

  question: allow
  plan_enter: allow
  plan_exit: deny

  read:
    "~/.ssh*": deny
    "~/.ssh/**": deny
    "$HOME/.ssh*": deny
    "$HOME/.ssh/**": deny
    "*/.ssh*": deny
    "*/.ssh/**": deny
    "/tmp*": deny
    "/tmp/**": deny
    "*/tmp*": deny
    "*/tmp/**": deny
    "*.env": deny
    "*.env.example": allow

  edit:
    "~/.ssh*": deny
    "~/.ssh/**": deny
    "$HOME/.ssh*": deny
    "$HOME/.ssh/**": deny
    "*/.ssh*": deny
    "*/.ssh/**": deny
    "/tmp*": deny
    "/tmp/**": deny
    "*/tmp*": deny
    "*/tmp/**": deny

  bash:
    "*": allow
    "git*": allow
    "python*": allow
    "bash*": allow
    "uv*": allow
    "cargo*": allow
    "pnpm*": allow
    "npm*": allow
    "node*": allow
    "*~/.ssh*": deny
    "*$HOME/.ssh*": deny
    "*/.ssh/*": deny
    "/tmp*": deny
    "/tmp/**": deny
    "*/tmp/**": deny
---

