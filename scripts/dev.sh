#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
IMAGE="${THAUMCRAFT_DOCKER_IMAGE:-thaumcraft-dev}"
GRADLE_HOME_DIR="${THAUMCRAFT_GRADLE_HOME:-$ROOT/.gradle_home}"
SMOKE_TIMEOUT="${THAUMCRAFT_SMOKE_TIMEOUT:-180s}"
SMOKE_CACHE_DIR="${THAUMCRAFT_SMOKE_DIR:-$ROOT/.smoke}"
DAEMON_CONTAINER="thaumcraft-gradle-daemon"

usage() {
  cat <<'EOF'
Usage:
  ./scripts/dev.sh image
  ./scripts/dev.sh compileJava          # quiet compile-only check (logs to run/validate/)
  ./scripts/dev.sh validate [--smoke]   # multi-step pipeline
  ./scripts/dev.sh check-jar [jar-path]
  ./scripts/dev.sh smoke-server
  ./scripts/dev.sh smoke-modset <name>
  ./scripts/dev.sh smoke-client
  ./scripts/dev.sh gradle <task>        # advanced: passthrough Gradle task
  ./scripts/dev.sh daemon-start         # start persistent Gradle daemon container
  ./scripts/dev.sh daemon-stop          # stop persistent Gradle daemon container

Examples:
  ./scripts/dev.sh image
  ./scripts/dev.sh tasks
  ./scripts/dev.sh compileJava
  ./scripts/dev.sh build
  ./scripts/dev.sh check-jar
  ./scripts/dev.sh validate --smoke
  ./scripts/dev.sh apiJar devJar
  ./scripts/dev.sh smoke-modset fossils
  ./scripts/dev.sh smoke-client

Environment:
  THAUMCRAFT_DOCKER_IMAGE    Docker image name, default: thaumcraft-dev
  THAUMCRAFT_GRADLE_HOME     Mounted Gradle cache, default: .gradle_home
  THAUMCRAFT_SMOKE_DIR       Local third-party smoke jar cache, default: .smoke
  THAUMCRAFT_SMOKE_TIMEOUT   Smoke timeout, default: 180s
  THAUMCRAFT_NO_DAEMON       Set to 1 to disable persistent daemon container
EOF
}

docker_daemon_start() {
  if docker container inspect "$DAEMON_CONTAINER" >/dev/null 2>&1; then
    if docker container inspect -f '{{.State.Running}}' "$DAEMON_CONTAINER" 2>/dev/null | grep -q 'true'; then
      return 0
    fi
    docker rm -f "$DAEMON_CONTAINER" >/dev/null 2>&1 || true
  fi
  docker run -d --name "$DAEMON_CONTAINER" \
    -v "$ROOT:/workspace/thaumcraft" \
    -v "$GRADLE_HOME_DIR:/home/ubuntu/.gradle" \
    --user "$(id -u):$(id -g)" \
    -w /workspace/thaumcraft \
    --entrypoint tail \
    "$IMAGE" -f /dev/null >/dev/null
}

docker_daemon_stop() {
  docker stop -t 2 "$DAEMON_CONTAINER" >/dev/null 2>&1 || true
  docker rm -f "$DAEMON_CONTAINER" >/dev/null 2>&1 || true
}

docker_gradle() {
  if [[ "${THAUMCRAFT_NO_DAEMON:-}" == "1" ]]; then
    docker run --rm \
      -v "$ROOT:/workspace/thaumcraft" \
      -v "$GRADLE_HOME_DIR:/home/ubuntu/.gradle" \
      --user "$(id -u):$(id -g)" \
      --entrypoint ./gradlew \
      "$IMAGE" "$@"
    return
  fi

  docker_daemon_start
  docker exec -w /workspace/thaumcraft \
    "$DAEMON_CONTAINER" ./gradlew "$@"
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
  # Coremod scanners such as JEID/MixinBooter log WARN-level optional target misses as
  # "Error loading class ... ClassNotFoundException". Keep fatal CNFE markers, but do
  # not stop smoke runs for these optional mixin discovery warnings.
  grep -nE 'LoaderException|LoaderExceptionModCrash|Game crashed|Caught exception|NoClassDefFoundError|ClassNotFoundException|NoSuchMethodError|NoSuchFieldError|ExceptionInInitializerError|Repair material has already been set|Encountered an unexpected exception|crash-reports|FATAL|fatal' "$1" \
    | grep -vE 'WARN.*Error loading class: .*ClassNotFoundException: The specified class .* was not found' || true
}

new_crash_reports() {
  local since_file="$1"
  local crash_dir="$ROOT/run/crash-reports"
  if [[ -d "$crash_dir" ]]; then
    find "$crash_dir" -type f -newer "$since_file" -print
  fi
  return 0
}

trim_string() {
  local value="$1"
  value="${value#"${value%%[![:space:]]*}"}"
  value="${value%"${value##*[![:space:]]}"}"
  printf '%s' "$value"
}

smoke_modset() {
  if [[ "$#" -ne 1 || -z "${1:-}" ]]; then
    printf 'Usage: ./scripts/dev.sh smoke-modset <name>\n' >&2
    printf 'Available modsets:\n' >&2
    find "$ROOT/scripts/smoke-modsets" -maxdepth 1 -type f -name '*.txt' -printf '  %f\n' 2>/dev/null | sed 's/\.txt$//' >&2 || true
    return 2
  fi

  local modset="$1"
  if [[ ! "$modset" =~ ^[A-Za-z0-9._-]+$ ]]; then
    printf 'smoke-modset: invalid modset name: %s\n' "$modset" >&2
    return 2
  fi

  local manifest="$ROOT/scripts/smoke-modsets/$modset.txt"
  if [[ ! -f "$manifest" ]]; then
    printf 'smoke-modset: manifest not found: %s\n' "$manifest" >&2
    return 1
  fi

  local jars=()
  local raw line jar expected actual source_path line_no=0 missing=0
  while IFS= read -r raw || [[ -n "$raw" ]]; do
    line_no=$((line_no + 1))
    line="$(trim_string "$raw")"
    [[ -z "$line" || "${line:0:1}" == "#" ]] && continue

    IFS='|' read -r jar expected _ <<< "$line"
    jar="$(trim_string "${jar:-}")"
    expected="$(trim_string "${expected:-}")"
    if [[ -z "$jar" || "$jar" == */* ]]; then
      printf 'smoke-modset: invalid jar name at %s:%s\n' "$manifest" "$line_no" >&2
      return 1
    fi

    source_path="$SMOKE_CACHE_DIR/$jar"
    if [[ ! -f "$source_path" ]]; then
      printf 'smoke-modset: missing %s in %s\n' "$jar" "$SMOKE_CACHE_DIR" >&2
      missing=1
      continue
    fi

    if [[ -n "$expected" && "$expected" != "-" ]]; then
      actual="$(sha256sum "$source_path" | awk '{print $1}')"
      if [[ "$actual" != "$expected" ]]; then
        printf 'smoke-modset: sha256 mismatch for %s\n' "$jar" >&2
        printf '  expected: %s\n' "$expected" >&2
        printf '  actual:   %s\n' "$actual" >&2
        missing=1
        continue
      fi
    fi

    jars+=("$jar")
  done < "$manifest"

  if [[ "$missing" -ne 0 ]]; then
    printf 'smoke-modset: place required third-party jars in .smoke/; jars are not committed.\n' >&2
    return 1
  fi
  if [[ "${#jars[@]}" -eq 0 ]]; then
    printf 'smoke-modset: manifest contains no jars: %s\n' "$manifest" >&2
    return 1
  fi

  local mods_dir="$ROOT/run/mods"
  mkdir -p "$mods_dir"
  find "$mods_dir" -maxdepth 1 -type f -name '*.jar' -delete

  for jar in "${jars[@]}"; do
    cp "$SMOKE_CACHE_DIR/$jar" "$mods_dir/$jar"
  done

  printf "Smoke modset '%s': prepared %s jar(s) from %s.\n" "$modset" "${#jars[@]}" "$SMOKE_CACHE_DIR"
  # The smoke server runs Gradle with --no-daemon; stop the persistent daemon first to avoid cache lock false failures.
  docker_daemon_stop
  smoke_server
}

mcp_leak_summary() {
  local jar_path="${1:-$ROOT/build/libs/Thaumcraft-1.0.0-universal.jar}"
  local mappings="$ROOT/.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg"

  if [[ ! -f "$jar_path" ]]; then
    printf 'MCP leak summary FAILED: jar not found: %s\n' "$jar_path" >&2
    return 1
  fi
  if [[ ! -f "$mappings" ]]; then
    printf 'MCP leak summary FAILED: MCP mapping file not found: %s\n' "$mappings" >&2
    return 1
  fi

  python3 - "$jar_path" "$mappings" <<'PY'
import hashlib
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
            _, dst_name = dst.rsplit('/', 1)
            if src_owner.startswith('net/minecraft/') and src_name != dst_name:
                mapped_fields[(src_owner, src_name)] = dst_name
        elif parts[0] == 'MD:' and len(parts) >= 5:
            src, src_desc, dst = parts[1], parts[2], parts[3]
            src_owner, src_name = src.rsplit('/', 1)
            _, dst_name = dst.rsplit('/', 1)
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
            leaks.append(('F', owner, name, ''))
        elif tag in (10, 11) and (owner, name, desc) in mapped_methods:
            leaks.append(('M', owner, name, desc))
    return leaks

findings = set()
unique_leaks = set()
with zipfile.ZipFile(jar_path) as jar:
    for name in jar.namelist():
        if not name.endswith('.class') or name.startswith('net/minecraft/'):
            continue
        try:
            for leak in parse_class(jar.read(name)):
                findings.add((name,) + leak)
                unique_leaks.add(leak)
        except Exception as exc:
            print('MCP leak summary FAILED: could not inspect {}: {}'.format(name, exc), file=sys.stderr)
            sys.exit(1)

hash_data = sorted('{}:{}:{}:{}'.format(*leak) for leak in unique_leaks)
hash_value = hashlib.sha256('\n'.join(hash_data).encode('utf-8')).hexdigest()
print('Count: {} (unique leaks: {})'.format(len(findings), len(unique_leaks)))
print('Hash: {}'.format(hash_value))
PY
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

  local prod_jar="$ROOT/build/libs/Thaumcraft-1.0.0-universal.jar"
  local jar_backup=""
  local had_prod_jar=0
  if [[ -f "$prod_jar" ]]; then
    jar_backup="$(mktemp)"
    cp -p "$prod_jar" "$jar_backup"
    had_prod_jar=1
  fi

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

  if [[ "$had_prod_jar" -eq 1 ]]; then
    cp -p "$jar_backup" "$prod_jar"
    rm -f "$jar_backup"
  elif [[ -f "$prod_jar" ]]; then
    rm -f "$prod_jar"
  fi

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

VALIDATE_TOTAL=0
VALIDATE_PASSED=0
VALIDATE_SKIPPED=0
VALIDATE_LAST_LOG=""
VALIDATE_VERBOSE=0
VALIDATE_PASSED_STEPS=()
VALIDATE_SKIPPED_STEPS=()

relative_path() {
  local path="$1"
  printf '%s' "${path#$ROOT/}"
}

validate_gradle_log() {
  local stage="$1"
  shift
  local log_dir="$ROOT/run/validate"
  mkdir -p "$log_dir"
  local log="$log_dir/$stage.log"
  VALIDATE_LAST_LOG="$(relative_path "$log")"

  set +e
  docker_gradle --console=plain -q "$@" > "$log" 2>&1
  local status="$?"
  set -e
  return "$status"
}

validate_git_status() {
  if ! git -C "$ROOT" rev-parse --is-inside-work-tree >/dev/null 2>&1; then
    printf 'not a git repository'
    return 2
  fi

  local count
  count="$(git -C "$ROOT" status --short | wc -l | tr -d ' ')"
  if [[ "$count" -eq 0 ]]; then
    printf 'clean'
  else
    printf '%s dirty entries' "$count"
  fi
  return 0
}

validate_compile_java() {
  set +e
  validate_gradle_log compileJava compileJava
  local status="$?"
  set -e

  if [[ "$status" -eq 0 ]]; then
    printf 'ok; log: %s' "$VALIDATE_LAST_LOG"
    return 0
  fi

  printf 'exit %s; log: %s' "$status" "$VALIDATE_LAST_LOG"
  return "$status"
}

append_junit_failures() {
  local results_dir="$1"
  local log_file="$2"
  python3 - "$results_dir" "$log_file" <<'PY'
import glob
import os
import sys
import xml.etree.ElementTree as ET

results_dir, log_file = sys.argv[1:3]
failures = []
for path in sorted(glob.glob(os.path.join(results_dir, 'TEST-*.xml'))):
    try:
        suite = ET.parse(path).getroot()
    except ET.ParseError as exc:
        failures.append((os.path.basename(path), '<xml-parse>', 'could not parse XML: {}'.format(exc), ''))
        continue
    suite_name = suite.attrib.get('name', os.path.basename(path))
    for case in suite.findall('testcase'):
        case_name = case.attrib.get('name', '<unknown>')
        class_name = case.attrib.get('classname', suite_name)
        for node_name in ('failure', 'error'):
            node = case.find(node_name)
            if node is not None:
                message = node.attrib.get('message') or (node.text or '').split('\n', 1)[0]
                trace = node.text or ''
                failures.append((class_name, case_name, message, trace))

if not failures:
    sys.exit(0)

with open(log_file, 'a', encoding='utf-8') as fh:
    fh.write('\n--- JUnit failure summary ---\n')
    for index, (class_name, case_name, message, trace) in enumerate(failures, 1):
        fh.write('{}. {}#{}: {}\n'.format(index, class_name, case_name, message))
        first_project_frame = ''
        for line in trace.splitlines():
            stripped = line.strip()
            if stripped.startswith('at thaumcraft.'):
                first_project_frame = stripped
                break
        if first_project_frame:
            fh.write('   {}\n'.format(first_project_frame))
PY
}

validate_tests() {
  if [[ ! -d "$ROOT/src/test/java" ]] || ! find "$ROOT/src/test/java" -name '*.java' -print -quit | grep -q .; then
    printf 'no test sources'
    return 2
  fi

  set +e
  validate_gradle_log test test
  local status="$?"
  set -e
  if [[ "$status" -ne 0 ]]; then
    append_junit_failures "$ROOT/build/test-results/test" "$ROOT/$VALIDATE_LAST_LOG"
    printf 'exit %s; log: %s' "$status" "$VALIDATE_LAST_LOG"
    return "$status"
  fi

  local summary
  summary="$(python3 - "$ROOT/build/test-results/test" <<'PY'
import glob
import os
import sys
import xml.etree.ElementTree as ET

root = sys.argv[1]
tests = failures = errors = skipped = 0
for path in glob.glob(os.path.join(root, 'TEST-*.xml')):
    tree = ET.parse(path)
    suite = tree.getroot()
    tests += int(suite.attrib.get('tests', 0))
    failures += int(suite.attrib.get('failures', 0))
    errors += int(suite.attrib.get('errors', 0))
    skipped += int(suite.attrib.get('skipped', 0))

if tests == 0:
    print('ok; no JUnit XML summary')
else:
    passed = tests - failures - errors - skipped
    print('{}/{} passed, {} skipped'.format(passed, tests, skipped))
PY
)"
  printf '%s; log: %s' "$summary" "$VALIDATE_LAST_LOG"
  return 0
}

validate_jar_task() {
  set +e
  validate_gradle_log jar jar
  local status="$?"
  set -e
  if [[ "$status" -ne 0 ]]; then
    printf 'exit %s; log: %s' "$status" "$VALIDATE_LAST_LOG"
    return "$status"
  fi

  local jar_path="$ROOT/build/libs/Thaumcraft-1.0.0-universal.jar"
  if [[ ! -f "$jar_path" ]]; then
    printf 'jar missing: %s; log: %s' "$(relative_path "$jar_path")" "$VALIDATE_LAST_LOG"
    return 1
  fi

  local size
  size="$(du -h "$jar_path" | cut -f1)"
  printf '%s; log: %s' "$size" "$VALIDATE_LAST_LOG"
  return 0
}

validate_mcp_summary() {
  local check_file="$ROOT/run/validate/check-jar.log"
  mkdir -p "$(dirname "$check_file")"
  VALIDATE_LAST_LOG="$(relative_path "$check_file")"

  set +e
  check_jar > "$check_file" 2>&1
  local status="$?"
  set -e
  if [[ "$status" -ne 0 ]]; then
    printf 'MCP leaks found; log: %s' "$VALIDATE_LAST_LOG"
    return "$status"
  fi

  printf 'no MCP leaks; log: %s' "$VALIDATE_LAST_LOG"
  return 0
}

validate_smoke_server() {
  VALIDATE_LAST_LOG="run/smoke-server.log"
  local output status
  set +e
  output="$(smoke_server 2>&1)"
  status="$?"
  set -e

  if [[ "$status" -eq 0 ]]; then
    printf 'server ready; log: %s' "$VALIDATE_LAST_LOG"
    return 0
  fi

  local line
  line="$(printf '%s\n' "$output" | sed '/^[[:space:]]*$/d' | tail -1)"
  printf '%s; log: %s' "${line:-exit $status}" "$VALIDATE_LAST_LOG"
  return "$status"
}

validate_step() {
  local name="$1"
  shift
  local output status

  VALIDATE_LAST_LOG=""
  set +e
  output="$("$@" 2>&1)"
  status="$?"
  set -e

  case "$status" in
    0)
      VALIDATE_TOTAL=$((VALIDATE_TOTAL + 1))
      VALIDATE_PASSED=$((VALIDATE_PASSED + 1))
      VALIDATE_PASSED_STEPS+=("$name")
      ;;
    2)
      VALIDATE_SKIPPED=$((VALIDATE_SKIPPED + 1))
      VALIDATE_SKIPPED_STEPS+=("$name")
      ;;
    *)
      VALIDATE_TOTAL=$((VALIDATE_TOTAL + 1))
      ;;
  esac

  if [[ "$status" -ne 0 && "$status" -ne 2 ]]; then
    printf 'FAIL validate: %s\n' "$name"
    printf 'reason: %s\n' "${output:-exit $status}"
    if [[ -n "$VALIDATE_LAST_LOG" ]]; then
      printf 'log: %s\n' "$VALIDATE_LAST_LOG"
    fi
    if [[ "$VALIDATE_VERBOSE" -eq 1 && -n "$VALIDATE_LAST_LOG" && -f "$ROOT/$VALIDATE_LAST_LOG" ]]; then
      printf -- '--- %s tail ---\n' "$VALIDATE_LAST_LOG"
      tail -40 "$ROOT/$VALIDATE_LAST_LOG"
    fi
    return 1
  fi

  return 0
}

validate_step_batch_gradle() {
  # Run multiple Gradle tasks in a single invocation to keep the daemon warm
  local name="$1"
  shift
  local output status

  VALIDATE_LAST_LOG=""
  set +e
  output="$(validate_gradle_log "$name" "$@" 2>&1)"
  status="$?"
  set -e

  case "$status" in
    0)
      VALIDATE_TOTAL=$((VALIDATE_TOTAL + 1))
      VALIDATE_PASSED=$((VALIDATE_PASSED + 1))
      VALIDATE_PASSED_STEPS+=("$name")
      ;;
    2)
      VALIDATE_SKIPPED=$((VALIDATE_SKIPPED + 1))
      VALIDATE_SKIPPED_STEPS+=("$name")
      ;;
    *)
      VALIDATE_TOTAL=$((VALIDATE_TOTAL + 1))
      ;;
  esac

  if [[ "$status" -ne 0 && "$status" -ne 2 ]]; then
    printf 'FAIL validate: %s\n' "$name"
    printf 'reason: exit %s; log: %s\n' "$status" "$VALIDATE_LAST_LOG"
    if [[ "$VALIDATE_VERBOSE" -eq 1 && -n "$VALIDATE_LAST_LOG" && -f "$ROOT/$VALIDATE_LAST_LOG" ]]; then
      printf -- '--- %s tail ---\n' "$VALIDATE_LAST_LOG"
      tail -40 "$ROOT/$VALIDATE_LAST_LOG"
    fi
    return 1
  fi

  return 0
}

validate() {
  local run_smoke=0
  VALIDATE_TOTAL=0
  VALIDATE_PASSED=0
  VALIDATE_SKIPPED=0
  VALIDATE_VERBOSE=0
  VALIDATE_PASSED_STEPS=()
  VALIDATE_SKIPPED_STEPS=()

  while [[ "$#" -gt 0 ]]; do
    case "$1" in
      --smoke)
        run_smoke=1
        ;;
      --verbose)
        VALIDATE_VERBOSE=1
        ;;
      -h|--help)
        printf 'Usage: ./scripts/dev.sh validate [--smoke] [--verbose]\n'
        return 0
        ;;
      *)
        printf 'validate: unknown option: %s\n' "$1" >&2
        return 2
        ;;
    esac
    shift
  done

  validate_step git-status validate_git_status || return 1
  # Batch compileJava + test + production reobf jar in a single Gradle invocation to keep daemon warm.
  validate_step_batch_gradle 'compile+test+reobf' compileJava test jar reobfJar || return 1
  validate_step check-jar validate_mcp_summary || return 1
  if [[ "$run_smoke" -eq 1 ]]; then
    # Smoke server uses --no-daemon and its own docker run: stop daemon first to avoid lock conflicts
    docker_daemon_stop
    validate_step smoke-server validate_smoke_server || return 1
  fi
  printf 'PASS validate: %s\n' "${VALIDATE_PASSED_STEPS[*]}"
  if [[ "$VALIDATE_SKIPPED" -gt 0 ]]; then
    printf 'SKIP validate: %s\n' "${VALIDATE_SKIPPED_STEPS[*]}"
  fi
  if [[ "$run_smoke" -eq 1 ]]; then
    printf 'logs: run/validate/, run/smoke-server.log\n'
  else
    printf 'logs: run/validate/\n'
  fi
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
    if [[ "$1" == "compileJava" && $# -eq 1 ]]; then
      # redirect to quiet handler (same as `dev.sh compileJava`)
      if validate_gradle_log compileJava compileJava; then
        printf 'compileJava ok; log: %s\n' "$VALIDATE_LAST_LOG"
        printf 'EXIT_CODE=0\n'
        exit 0
      else
        status=$?
        printf 'compileJava FAILED (exit %s); log: %s\n' "$status" "$VALIDATE_LAST_LOG" >&2
        log_path="$ROOT/$VALIDATE_LAST_LOG"
        if [[ -f "$log_path" ]]; then
          printf -- '--- %s tail (last 30 lines) ---\n' "$VALIDATE_LAST_LOG" >&2
          tail -30 "$log_path" >&2
        fi
        printf 'EXIT_CODE=%s\n' "$status" >&2
        exit "$status"
      fi
    fi
    docker_gradle "$@"
    ;;
  check-jar)
    shift
    check_jar "$@"
    ;;
  validate)
    shift
    validate "$@"
    ;;
  smoke-server)
    smoke_server
    ;;
  smoke-modset)
    shift
    smoke_modset "$@"
    ;;
  smoke-client)
    smoke_client
    ;;
  daemon-start)
    docker_daemon_start
    printf 'Daemon container %s started.\n' "$DAEMON_CONTAINER"
    ;;
  daemon-stop)
    docker_daemon_stop
    printf 'Daemon container %s stopped.\n' "$DAEMON_CONTAINER"
    ;;
  *)
    docker_gradle "$@"
    ;;
esac
