#!/usr/bin/env bash
# Replace only the two pinned upstream JARs. Do not touch world, LOD or cache data.
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p mods
tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT

chunksmith="chunksmith-4.8.1+1.21.1-neoforge.jar"
millenaire="millenaire-9.1.0.jar"
curl --fail --location --retry 3 --show-error --silent \
  'https://cdn.modrinth.com/data/4BeAEBIb/versions/4Bd6ozCL/chunksmith-4.8.1%2B1.21.1-neoforge.jar' \
  --output "$tmp/$chunksmith"
curl --fail --location --retry 3 --show-error --silent \
  'https://new.millenaire.org/api/downloads/9.1.0~millenaire-9.1.0.jar' \
  --output "$tmp/$millenaire"

printf '%s  %s\n' \
  'bf6371873cb78f2a7645001eb1ef0f7f6d7e5e80c51a52667b7d37ee898888ca' "$tmp/$chunksmith" \
  '79932591423c867bada662827a984c52bd355525358b215fe31a5413897708ce' "$tmp/$millenaire" \
  | sha256sum --check --status

# Existing JARs live in Git and can be restored from history if needed.
rm -f 'mods/chunksmith-4.6.0+1.21.1-neoforge.jar' 'mods/millenaire-9.0.3.jar'
install -m 0644 "$tmp/$chunksmith" "mods/$chunksmith"
install -m 0644 "$tmp/$millenaire" "mods/$millenaire"
echo "Verified and staged upstream JARs for Chunksmith 4.8.1 and Millenaire 9.1.0"
