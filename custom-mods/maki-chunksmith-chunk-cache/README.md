# maki-chunksmith-chunk-cache

Maintained source for the server-only Chunksmith cold-cache/backfill compatibility mod.

Issue #30 found that version 1.0.0 had all seven registered Mixin classes compiled with class-level `@Mixin` in `RuntimeVisibleAnnotations`. Injector annotations were already runtime-visible and the cache/backfill implementation itself was not implicated.

Version 1.0.1 rebuilds the decompiled 1.0.0 sources against NeoForge ModDev 21.1.262 and the real Sponge Mixin API supplied by ModDev. No hand-written Mixin annotation stubs are used. `check` runs `verifyMixinAnnotationRetention` for all seven registered Mixin classes.

The cache, promotion, fingerprint, Millenaire bridge and backfill logic are intentionally unchanged.


## Issue #35 observability and idle coordination

Version 1.0.2 keeps backfill ownership in this MOD and adds explicit runtime state instead of inferring progress from the shared Chunksmith LOD writer:

- writes `logs/maki-chunk-cache-backfill-status.json` with scan/busy phase, active chunk, counters, LOD submissions and durable LOD refresh completions;
- claims `maki-idle-pregen` external idle work only while a guarded backfill chunk is active, and releases that claim when ownership ends;
- does not start, stop, resume, replace, recenter or adopt any Chunksmith generation task;
- remains blocked while a Chunksmith generation task is active, preserving the existing separation between follow generation and cache backfill.
- releases the backfill guard and idle-work claim immediately when loading fails before a FULL chunk exists; loaded-chunk failures retain the guarded-unload lifecycle.

This makes non-zero global CSLOD writes explainable even when the follow-owned GenerationTask completion rate is zero.


## Issue #40 compatibility manifests and quarantine

Version 1.1.0 replaces the old "hash every file, then recursively delete every other generation" policy with an auditable compatibility boundary:

- unknown MODs, configs, serverconfigs, datapacks, level.dat worldgen state, seed, dimension, generator class and Minecraft DataVersion remain fail-closed compatibility inputs;
- maki_chunksmith_chunk_cache itself is keyed by the explicit compatibility schema instead of its JAR bytes, so maintenance-only releases do not invalidate chunk data accidentally;
- maki_lag_observer is classified as observation-only from its MOD id, and its hot-reload config is ignored by default because it only controls diagnostics;
- additional observation-only MOD ids or non-worldgen paths require an explicit administrator policy in config/maki-chunksmith-chunk-cache.properties;
- config/chunksmith/tasks/** remains excluded exactly as before; mutable Chunksmith task progress is not a compatibility input;
- every v2 generation gets manifest-v2.txt containing per-input digest, size, compatibility classification and reason;
- a previous v2 manifest is compared with the current one so logs identify old/new fingerprints, changed inputs, trigger (capture, load, or scan), entry count and bytes;
- incompatible generations are moved atomically to .quarantine rather than immediately deleted and are never used for promotion while incompatible;
- quarantines have retention/count/byte limits, but automatic capacity pruning protects freshly quarantined data for the configured minimum age;
- /makichunkcache quarantine list, discard <id>, and restore <id> run maintenance on the cache I/O executor. Restore is refused unless the quarantined manifest exactly matches the current compatibility fingerprint;
- a 1.0.x generation can be adopted without regeneration only when its exact legacy fingerprint still matches the current full input set. Otherwise it is quarantined because compatibility cannot be proven.

The compatibility classification is intentionally conservative. A diagnostic MOD should be added to the observation-only list only when its code is known not to alter generated/serialized chunk contents. If a future cache release changes serialization or promotion semantics, CompatibilityFingerprint.SCHEMA must be bumped.

check also runs FingerprintPolicySelfTest, covering observation-only JAR changes, explicitly ignored non-worldgen config, real worldgen MOD changes, datapack changes, seed changes, the existing Chunksmith task exclusion, and corrupt manifest fail-closed behavior.
## Issue #41 backfill ticket lifecycle

Version 1.0.3 removes the circular dependency between the backfill idle-work claim and vanilla's expiring `UNKNOWN` chunk ticket.

- the existing `BACKFILL_TICKET` is the sole ticket owned by backfill;
- FULL acquisition polls `getChunkFuture(..., FULL, false)`, so the lookup does not create an auxiliary vanilla `UNKNOWN` ticket;
- the custom ticket and idle-work claim are released as soon as durable LOD/cache work is finished, while `BackfillGuard` remains active until the chunk actually unloads or a player takes ownership;
- a successful completed guard is detached from the single active backfill pipeline, so a delayed unload does not block the next candidate; guards support multiple coordinates per dimension and are bounded to 8 detached entries;
- detached guards are released on the real unload callback or player handoff, warn after 30 seconds, and never force-unload or delete another owner's ticket;
- pre-FULL loading warns after 30 seconds; after five minutes an in-flight FULL future keeps its ticket and persistence guard until it settles while only the idle-work claim is released, and a timeout with no in-flight generation aborts safely;
- status schema 2 records phase/progress/ticket timestamps, active and detached guard state, server freeze state, unload wait time, and typed wait reasons.

This deliberately does not change Chunksmith 4.6.0, `maki-idle-pregen`, fingerprint rotation (#40), synchronous load scheduling (#44), status-file I/O (#45), or neighboring ProtoChunk persistence (#47).
## Issue #44 non-blocking backfill chunk acquisition

Version 1.0.3 removes the public `ServerChunkCache.getChunkFuture(...)` call from the server-thread backfill start path.

- Backfill requests are queued onto Chunksmith 4.6.0's existing ticket safe point.
- The request uses Chunksmith's `ServerChunkCacheMixin.invokeGetChunkFutureMainThread` path, matching Chunksmith's own `NeoForgeWorld.getChunkAtAsync` lifecycle instead of entering vanilla `managedBlock`.
- `BACKFILL_TICKET`, `BackfillGuard`, Millenaire spawn suppression, player handoff and cold-cache persistence remain owned by this MOD.
- Ticket installation is tracked explicitly so a player handoff that happens before the queued safe-point request runs cannot leave or later create an orphan backfill ticket.
- Backfill status records safe-point delay, distance-manager update wall time, private future-call wall time, setup CPU/wall time, future wait time, pending chunk tasks and pending chunk writes.
- The separate post-success unload lifecycle from Issue #41 and synchronous status-file I/O from Issue #45 are intentionally not changed here.
