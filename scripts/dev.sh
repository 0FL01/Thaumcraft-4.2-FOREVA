#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
IMAGE="${THAUMCRAFT_DOCKER_IMAGE:-thaumcraft-dev}"
GRADLE_HOME_DIR="${THAUMCRAFT_GRADLE_HOME:-$ROOT/.gradle_home}"
SMOKE_TIMEOUT="${THAUMCRAFT_SMOKE_TIMEOUT:-180s}"

usage() {
  cat <<'EOF'
Usage:
  ./scripts/dev.sh image
  ./scripts/dev.sh <gradle-task> [gradle-args...]
  ./scripts/dev.sh gradle <gradle-task> [gradle-args...]
  ./scripts/dev.sh smoke-server
  ./scripts/dev.sh smoke-client

Examples:
  ./scripts/dev.sh image
  ./scripts/dev.sh tasks
  ./scripts/dev.sh compileJava
  ./scripts/dev.sh build
  ./scripts/dev.sh apiJar devJar
  ./scripts/dev.sh smoke-server

Environment:
  THAUMCRAFT_DOCKER_IMAGE   Docker image name, default: thaumcraft-dev
  THAUMCRAFT_GRADLE_HOME    Mounted Gradle cache, default: .gradle_home
  THAUMCRAFT_SMOKE_TIMEOUT  Smoke timeout, default: 180s
EOF
}

docker_gradle() {
  docker run --rm \
    -v "$ROOT:/workspace/thaumcraft" \
    -v "$GRADLE_HOME_DIR:/home/ubuntu/.gradle" \
    --user "$(id -u):$(id -g)" \
    --entrypoint ./gradlew \
    "$IMAGE" "$@"
}

crash_markers() {
  grep -nE 'LoaderException|LoaderExceptionModCrash|Game crashed|Caught exception|NoClassDefFoundError|ClassNotFoundException|NoSuchMethodError|NoSuchFieldError|ExceptionInInitializerError|Repair material has already been set|Encountered an unexpected exception|crash-reports|FATAL|fatal' "$1" || true
}

new_crash_reports() {
  local since_file="$1"
  local crash_dir="$ROOT/run/crash-reports"
  if [[ -d "$crash_dir" ]]; then
    find "$crash_dir" -type f -newer "$since_file" -print
  fi
  return 0
}

smoke_server() {
  mkdir -p "$ROOT/run"
  printf 'eula=true\n' > "$ROOT/run/eula.txt"

  local log="$ROOT/run/smoke-server.log"
  rm -f "$log"

  set +e
  timeout "$SMOKE_TIMEOUT" docker run --rm \
    -v "$ROOT:/workspace/thaumcraft" \
    -v "$GRADLE_HOME_DIR:/home/ubuntu/.gradle" \
    --user "$(id -u):$(id -g)" \
    --entrypoint ./gradlew \
    "$IMAGE" runServer -x getAssets --no-daemon 2>&1 | tee "$log"
  local status="${PIPESTATUS[0]}"
  set -e

  local markers
  markers="$(crash_markers "$log")"
  local reports
  reports="$(new_crash_reports "$log")"
  if [[ -n "$markers" ]]; then
    printf '\nSmoke server FAILED: crash markers found in %s\n' "$log" >&2
    printf '%s\n' "$markers" >&2
    return 1
  fi

  if [[ -n "$reports" ]]; then
    printf '\nSmoke server FAILED: new crash reports found.\n' >&2
    printf '%s\n' "$reports" >&2
    return 1
  fi

  if grep -Fq 'Done (' "$log"; then
    printf '\nSmoke server PASSED: server reached ready state. Log: %s\n' "$log"
    return 0
  fi

  if [[ "$status" -eq 124 ]]; then
    printf '\nSmoke server FAILED: timed out before ready state. Log: %s\n' "$log" >&2
    return 1
  fi

  if [[ "$status" -ne 0 ]]; then
    printf '\nSmoke server FAILED: command exited with %s. Log: %s\n' "$status" "$log" >&2
    return "$status"
  fi

  printf '\nSmoke server FAILED: ready marker `Done (` was not found. Log: %s\n' "$log" >&2
  return 1
}

smoke_client() {
  if [[ -z "${DISPLAY:-}" ]]; then
    printf 'Smoke client skipped: DISPLAY is not set.\n' >&2
    return 2
  fi

  mkdir -p "$ROOT/run"
  local log="$ROOT/run/smoke-client.log"
  rm -f "$log"

  set +e
  timeout "$SMOKE_TIMEOUT" docker run --rm \
    -v "$ROOT:/workspace/thaumcraft" \
    -v "$GRADLE_HOME_DIR:/home/ubuntu/.gradle" \
    -e DISPLAY="$DISPLAY" \
    -v /tmp/.X11-unix:/tmp/.X11-unix \
    --user "$(id -u):$(id -g)" \
    --entrypoint ./gradlew \
    "$IMAGE" runClient -x getAssets 2>&1 | tee "$log"
  local status="${PIPESTATUS[0]}"
  set -e

  local markers
  markers="$(crash_markers "$log")"
  local reports
  reports="$(new_crash_reports "$log")"
  if [[ -n "$markers" ]]; then
    printf '\nSmoke client FAILED: crash markers found in %s\n' "$log" >&2
    printf '%s\n' "$markers" >&2
    return 1
  fi

  if [[ -n "$reports" ]]; then
    printf '\nSmoke client FAILED: new crash reports found.\n' >&2
    printf '%s\n' "$reports" >&2
    return 1
  fi

  if grep -Fq 'Forge Mod Loader has successfully loaded' "$log"; then
    printf '\nSmoke client PASSED: Forge reported successful mod loading. Log: %s\n' "$log"
    return 0
  fi

  if [[ "$status" -eq 124 ]]; then
    printf '\nSmoke client FAILED: timed out before successful mod loading. Log: %s\n' "$log" >&2
    return 1
  fi

  if [[ "$status" -ne 0 ]]; then
    printf '\nSmoke client FAILED: command exited with %s. Log: %s\n' "$status" "$log" >&2
    return "$status"
  fi

  printf '\nSmoke client FAILED: successful mod loading marker was not found. Log: %s\n' "$log" >&2
  return 1
}

cmd="${1:-help}"
case "$cmd" in
  help|-h|--help)
    usage
    ;;
  image|docker-build)
    docker build -t "$IMAGE" "$ROOT"
    ;;
  gradle)
    shift
    docker_gradle "$@"
    ;;
  smoke-server)
    smoke_server
    ;;
  smoke-client)
    smoke_client
    ;;
  *)
    docker_gradle "$@"
    ;;
esac
