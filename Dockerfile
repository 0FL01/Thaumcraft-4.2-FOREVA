# syntax=docker/dockerfile:1
# =============================================================================
# Thaumcraft 4.2.3.5 (1.7.10) -> 1.12.2 Port — Development Environment
# =============================================================================
# Base: eclipse-temurin:8-jdk  (JDK 8u492, Ubuntu 24.04 LTS)
# Forge 1.12.2 + ForgeGradle 2.3 requires Java 8 specifically.
# =============================================================================

FROM eclipse-temurin:8-jdk AS base

LABEL description="Thaumcraft 4→1.12.2 Port — Java 8 Dev Environment"
LABEL org.opencontainers.image.source="https://github.com/stfu/thaumcraft"
LABEL org.opencontainers.image.version="1.0"

# ── Prevent interactive prompts ──────────────────────────────────────────────
ENV DEBIAN_FRONTEND=noninteractive
ENV LC_ALL=C.UTF-8
ENV LANG=C.UTF-8

# ── System packages (single RUN layer with cleanup) ─────────────────────────
RUN set -eux; \
    apt-get update; \
    apt-get install -y --no-install-recommends \
        # Version control / download
        git \
        ca-certificates \
        curl \
        wget \
        # Compression / archives
        unzip \
        xz-utils \
        # Build toolchain
        make \
        gcc \
        g++ \
        python3 \
        # Code search / inspection
        ripgrep \
        jq \
        tree \
        # Forge client runtime libs (OpenGL, X11, ALSA)
        # Ubuntu 24.04 Noble package names:
        libgl1 \
        libgl1-mesa-dri \
        libglx-mesa0 \
        libglu1-mesa \
        libx11-6 \
        libxext6 \
        libxrandr2 \
        libxrender1 \
        libxxf86vm1 \
        libxinerama1 \
        libxcursor1 \
        libxi6 \
        libopenal1 \
        libasound2t64; \
    apt-get clean; \
    rm -rf /var/lib/apt/lists/*

# ── CFR Java Decompiler (v0.152) ─────────────────────────────────────────────
# https://github.com/leibnitz27/cfr — latest on Maven Central as of Dec 2021
RUN set -eux; \
    curl -fsSLo /opt/cfr.jar \
        https://github.com/leibnitz27/cfr/releases/download/0.152/cfr-0.152.jar; \
    echo '#!/bin/bash\njava -jar /opt/cfr.jar "$@"' > /usr/local/bin/cfr; \
    chmod +x /usr/local/bin/cfr; \
    # Verify extraction works
    java -jar /opt/cfr.jar --version 2>/dev/null | head -1 || true

# ── Gradle wrapper guard ─────────────────────────────────────────────────────
# The project uses gradlew (Gradle wrapper).  System Gradle not installed —
# the wrapper auto-downloads Gradle 4.x which is required by ForgeGradle 2.3.
RUN { \
    echo '#!/bin/bash'; \
    echo 'if [ ! -f ./gradlew ]; then'; \
    echo '  echo "ERROR: gradlew not found. Generate it via:"'; \
    echo '  echo "  gradle wrapper --gradle-version 4.9"'; \
    echo '  echo "(requires Gradle installed temporarily)"'; \
    echo '  exit 1'; \
    echo 'fi'; \
    echo 'exec ./gradlew "$@"'; \
} > /usr/local/bin/gradlew && chmod +x /usr/local/bin/gradlew

# ── Non-root user (for filesystem safety) ───────────────────────────────────
RUN set -eux; \
    groupadd --gid 1024 thaum; \
    useradd --uid 1024 --gid thaum --create-home --shell /bin/bash thaum

WORKDIR /workspace/thaumcraft

# By default, run as thaum user.  Mount the project at /workspace/thaumcraft.
USER thaum

ENTRYPOINT ["/bin/bash"]
