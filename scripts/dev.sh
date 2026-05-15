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
  ./scripts/dev.sh check-jar [jar-path]
  ./scripts/dev.sh smoke-server
  ./scripts/dev.sh smoke-client

Examples:
  ./scripts/dev.sh image
  ./scripts/dev.sh tasks
  ./scripts/dev.sh compileJava
  ./scripts/dev.sh build
  ./scripts/dev.sh check-jar
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

write_smoke_log4j_config() {
  cat > "$1" <<'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
  <Appenders>
    <Console name="Console" target="SYSTEM_OUT">
      <PatternLayout pattern="[%d{HH:mm:ss}] [%t/%level]: %msg%n"/>
    </Console>
  </Appenders>
  <Loggers>
    <Root level="info">
      <AppenderRef ref="Console"/>
    </Root>
  </Loggers>
</Configuration>
EOF
}

duration_to_seconds() {
  local spec="$1"
  case "$spec" in
    *s) printf '%s\n' "${spec%s}" ;;
    *m) printf '%s\n' "$(( ${spec%m} * 60 ))" ;;
    *h) printf '%s\n' "$(( ${spec%h} * 3600 ))" ;;
    *) printf '%s\n' "$spec" ;;
  esac
}

file_is_fresh() {
  local file="$1"
  local since_file="$2"
  [[ -f "$file" && "$file" -nt "$since_file" ]]
}

server_ready_reached() {
  local since_file="$1"
  shift
  local file
  for file in "$@"; do
    if file_is_fresh "$file" "$since_file" && grep -Fq 'Done (' "$file"; then
      return 0
    fi
  done
  return 1
}

collect_log_markers() {
  local since_file="$1"
  shift
  local file
  local output=""
  local markers
  for file in "$@"; do
    if ! file_is_fresh "$file" "$since_file"; then
      continue
    fi
    markers="$(crash_markers "$file")"
    if [[ -n "$markers" ]]; then
      output+=$'\n'"$file"$'\n'"$markers"
    fi
  done
  printf '%s' "${output#$'\n'}"
}

stop_container() {
  local container_name="$1"
  docker stop -t 5 "$container_name" >/dev/null 2>&1 || \
    docker kill "$container_name" >/dev/null 2>&1 || true
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

check_jar() {
  local jar_path="${1:-$ROOT/build/libs/Thaumcraft-1.0.0-universal.jar}"
  local mappings="$ROOT/.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg"

  if [[ ! -f "$jar_path" ]]; then
    printf 'Jar check FAILED: jar not found: %s\n' "$jar_path" >&2
    return 1
  fi
  if [[ ! -f "$mappings" ]]; then
    printf 'Jar check FAILED: MCP mapping file not found: %s\n' "$mappings" >&2
    return 1
  fi

  python3 - "$jar_path" "$mappings" <<'PY'
import struct
import sys
import zipfile

jar_path, mappings_path = sys.argv[1:3]

mapped_fields = {}
mapped_methods = {}
with open(mappings_path, 'r', encoding='utf-8') as fh:
    for raw in fh:
        parts = raw.strip().split()
        if not parts:
            continue
        if parts[0] == 'FD:' and len(parts) >= 3:
            src, dst = parts[1], parts[2]
            src_owner, src_name = src.rsplit('/', 1)
            dst_owner, dst_name = dst.rsplit('/', 1)
            if src_owner.startswith('net/minecraft/') and src_name != dst_name:
                mapped_fields[(src_owner, src_name)] = dst_name
        elif parts[0] == 'MD:' and len(parts) >= 5:
            src, src_desc, dst = parts[1], parts[2], parts[3]
            src_owner, src_name = src.rsplit('/', 1)
            dst_owner, dst_name = dst.rsplit('/', 1)
            if src_owner.startswith('net/minecraft/') and src_name != dst_name:
                mapped_methods[(src_owner, src_name, src_desc)] = dst_name

def parse_class(data):
    if data[:4] != b'\xca\xfe\xba\xbe':
        return []
    pos = 8
    cp_count = struct.unpack_from('>H', data, pos)[0]
    pos += 2
    cp = [None] * cp_count
    i = 1
    while i < cp_count:
        tag = data[pos]
        pos += 1
        if tag == 1:
            length = struct.unpack_from('>H', data, pos)[0]
            pos += 2
            cp[i] = ('Utf8', data[pos:pos + length].decode('utf-8', 'replace'))
            pos += length
        elif tag in (3, 4):
            pos += 4
        elif tag in (5, 6):
            pos += 8
            i += 1
        elif tag in (7, 8, 16):
            cp[i] = ('Index', struct.unpack_from('>H', data, pos)[0])
            pos += 2
        elif tag in (9, 10, 11):
            cp[i] = ('Ref', tag, struct.unpack_from('>H', data, pos)[0], struct.unpack_from('>H', data, pos + 2)[0])
            pos += 4
        elif tag == 12:
            cp[i] = ('NameAndType', struct.unpack_from('>H', data, pos)[0], struct.unpack_from('>H', data, pos + 2)[0])
            pos += 4
        elif tag == 15:
            pos += 3
        elif tag == 18:
            pos += 4
        else:
            raise ValueError('Unsupported constant-pool tag {}'.format(tag))
        i += 1

    def utf(index):
        entry = cp[index]
        return entry[1] if entry and entry[0] == 'Utf8' else None

    def class_name(index):
        entry = cp[index]
        return utf(entry[1]) if entry and entry[0] == 'Index' else None

    def name_and_type(index):
        entry = cp[index]
        if entry and entry[0] == 'NameAndType':
            return utf(entry[1]), utf(entry[2])
        return None, None

    leaks = []
    for entry in cp:
        if not entry or entry[0] != 'Ref':
            continue
        _, tag, class_index, nat_index = entry
        owner = class_name(class_index)
        name, desc = name_and_type(nat_index)
        if not owner or not name or not owner.startswith('net/minecraft/'):
            continue
        if tag == 9 and (owner, name) in mapped_fields:
            leaks.append(('field', owner, name, desc, mapped_fields[(owner, name)]))
        elif tag in (10, 11) and (owner, name, desc) in mapped_methods:
            leaks.append(('method', owner, name, desc, mapped_methods[(owner, name, desc)]))
    return leaks

findings = []
with zipfile.ZipFile(jar_path) as jar:
    for name in jar.namelist():
        if not name.endswith('.class'):
            continue
        if name.startswith('net/minecraft/'):
            continue
        try:
            for leak in parse_class(jar.read(name)):
                findings.append((name,) + leak)
        except Exception as exc:
            print('Jar check FAILED: could not inspect {}: {}'.format(name, exc), file=sys.stderr)
            sys.exit(1)

unique = sorted(set(findings))
if unique:
    print('Jar check FAILED: built jar contains MCP-named Minecraft references.')
    print('This usually means the jar is not production-reobfuscated and can crash in Prism/normal Forge.')
    prioritized = sorted(unique, key=lambda row: (0 if row[2] == 'net/minecraft/block/material/MapColor' else 1, 0 if row[1] == 'field' else 1, row))
    for cls, kind, owner, name, desc, mapped in prioritized[:80]:
        if kind == 'method':
            print('{}: {} {}.{}{} should be {}'.format(cls, kind, owner.replace('/', '.'), name, desc, mapped))
        else:
            print('{}: {} {}.{} should be {}'.format(cls, kind, owner.replace('/', '.'), name, mapped))
    if len(unique) > 80:
        print('... and {} more'.format(len(unique) - 80))
    sys.exit(1)

print('Jar check PASSED: no MCP-named Minecraft field/method references found in {}'.format(jar_path))
PY
}

smoke_server() {
  mkdir -p "$ROOT/run"
  mkdir -p "$ROOT/run/logs"
  printf 'eula=true\n' > "$ROOT/run/eula.txt"

  local log="$ROOT/run/smoke-server.log"
  local latest_log="$ROOT/run/logs/latest.log"
  rm -f "$log"

  local since_file
  since_file="$(mktemp "$ROOT/run/smoke-server.start.XXXXXX")"
  local status_file
  status_file="$(mktemp "$ROOT/run/smoke-server.status.XXXXXX")"
  local log4j_config
  log4j_config="$(mktemp "$ROOT/run/smoke-log4j2.XXXXXX.xml")"
  local container_name="thaumcraft-smoke-server-$$-$RANDOM"
  local log4j_arg="-Dlog4j.configurationFile=file:/workspace/thaumcraft/run/$(basename "$log4j_config")"
  local timeout_seconds
  timeout_seconds="$(duration_to_seconds "$SMOKE_TIMEOUT")"
  local started_at
  started_at="$(date +%s)"
  write_smoke_log4j_config "$log4j_config"

  local prod_jar="$ROOT/build/libs/Thaumcraft-1.0.0-universal.jar"
  local jar_backup=""
  local had_prod_jar=0
  if [[ -f "$prod_jar" ]]; then
    jar_backup="$(mktemp)"
    cp -p "$prod_jar" "$jar_backup"
    had_prod_jar=1
  fi

  set +e
  (
    docker run --rm --name "$container_name" \
      -v "$ROOT:/workspace/thaumcraft" \
      -v "$GRADLE_HOME_DIR:/home/ubuntu/.gradle" \
      -e JAVA_TOOL_OPTIONS="$log4j_arg" \
      --user "$(id -u):$(id -g)" \
      --entrypoint ./gradlew \
      "$IMAGE" runServer -x getAssets --no-daemon --console=plain > "$log" 2>&1
    printf '%s' "$?" > "$status_file"
  ) &
  local runner_pid="$!"
  set -e

  local timed_out=0
  local markers=""
  local reports=""
  while kill -0 "$runner_pid" 2>/dev/null; do
    if server_ready_reached "$since_file" "$log" "$latest_log"; then
      stop_container "$container_name"
      break
    fi

    markers="$(collect_log_markers "$since_file" "$log" "$latest_log")"
    if [[ -n "$markers" ]]; then
      stop_container "$container_name"
      break
    fi

    reports="$(new_crash_reports "$since_file")"
    if [[ -n "$reports" ]]; then
      stop_container "$container_name"
      break
    fi

    if (( $(date +%s) - started_at >= timeout_seconds )); then
      timed_out=1
      stop_container "$container_name"
      break
    fi

    sleep 1
  done

  wait "$runner_pid" || true
  local status="$(cat "$status_file" 2>/dev/null || printf '1')"

  if [[ "$had_prod_jar" -eq 1 ]]; then
    cp -p "$jar_backup" "$prod_jar"
    rm -f "$jar_backup"
  elif [[ -f "$prod_jar" ]]; then
    rm -f "$prod_jar"
  fi

  markers="$(collect_log_markers "$since_file" "$log" "$latest_log")"
  reports="$(new_crash_reports "$since_file")"

  local ready=0
  if server_ready_reached "$since_file" "$log" "$latest_log"; then
    ready=1
  fi

  rm -f "$since_file" "$status_file" "$log4j_config"

  if [[ -n "$markers" ]]; then
    printf '\nSmoke server FAILED: crash markers found in %s or %s\n' "$log" "$latest_log" >&2
    printf '%s\n' "$markers" >&2
    return 1
  fi

  if [[ -n "$reports" ]]; then
    printf '\nSmoke server FAILED: new crash reports found.\n' >&2
    printf '%s\n' "$reports" >&2
    return 1
  fi

  if [[ "$ready" -eq 1 ]]; then
    printf '\nSmoke server PASSED: server reached ready state. Logs: %s ; %s\n' "$log" "$latest_log"
    return 0
  fi

  if [[ "$timed_out" -eq 1 ]]; then
    printf '\nSmoke server FAILED: timed out before ready state. Logs: %s ; %s\n' "$log" "$latest_log" >&2
    return 1
  fi

  if [[ "$status" -ne 0 ]]; then
    printf '\nSmoke server FAILED: command exited with %s. Logs: %s ; %s\n' "$status" "$log" "$latest_log" >&2
    return "$status"
  fi

  printf '\nSmoke server FAILED: ready marker `Done (` was not found. Logs: %s ; %s\n' "$log" "$latest_log" >&2
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

  local xauth="${XAUTHORITY:-$HOME/.Xauthority}"
  local docker_args=(
    --rm
    -v "$ROOT:/workspace/thaumcraft"
    -v "$GRADLE_HOME_DIR:/home/ubuntu/.gradle"
    -e DISPLAY="$DISPLAY"
    -v /tmp/.X11-unix:/tmp/.X11-unix
    --user "$(id -u):$(id -g)"
    --entrypoint ./gradlew
  )

  if [[ -f "$xauth" ]]; then
    docker_args+=(
      -e XAUTHORITY=/tmp/.thaumcraft.Xauthority
      -v "$xauth:/tmp/.thaumcraft.Xauthority:ro"
    )
  fi

  set +e
  timeout "$SMOKE_TIMEOUT" docker run "${docker_args[@]}" \
    "$IMAGE" runClient -x getAssets --console=plain > "$log" 2>&1
  local status="$?"
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
  check-jar)
    shift
    check_jar "$@"
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
