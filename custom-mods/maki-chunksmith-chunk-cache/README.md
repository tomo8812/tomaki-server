# maki-chunksmith-chunk-cache

Current source version: 1.4.0 (NeoForge 1.21.1, Chunksmith 4.6.0).

This MOD stores FULL chunks created by the follow-owned Chunksmith generation task
as cold-cache snapshots and coordinates their persistent LOD records. A generated
coordinate is reusable only after the required FULL snapshot and LOD are saved
and confirmed as one completed pipeline result.

- Normal-world FULL chunks remain authoritative and may supply missing LOD records.
- Player handoff claims a complete cached FULL, preserves Entity, Block Entity and
  POI data, and retires the cache only after normal-world storage acknowledges it.
- Generated-only chunks and their dependent data stay out of normal-world storage
  until legitimately loaded by a player.
- Incomplete writes and failed or interrupted captures remain retryable.
- Compatibility checks distinguish audited generation-neutral changes from worldgen
  changes. Cache capacity and normal-world disk safety are enforced separately.
- Runtime observation uses standard logger levels and the
  `maki-mods-observer` diagnostic MOD when enabled.

Issue #83 unified generation and LOD completion into one flow. The former
independently scheduled Backfill controller, dependency ticket/suppression scope,
shared Follow/Backfill budget and legacy retry scanner have been removed.
Any regression fixtures for those retired mechanisms were removed as well.
Only follow-owned generation and read-only LOD repair remain.

See [Issue #83](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/issues/83)
for the governing flow and [PR #84](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/84)
for its implementation.
