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


## Issue #47 dependency-safe legacy backfill

Version 1.0.4 makes legacy CSLOD backfill distinguish the state of normal chunk storage instead of treating every non-zero region header entry as explored/full.

- candidate scans classify normal storage as absent, incomplete ProtoChunk, FULL, or unknown by reading only the saved `Status` through the vanilla chunk scanner;
- incomplete ProtoChunks remain eligible for backfill, while existing FULL chunks remain authoritative and unknown/corrupt storage fails closed;
- a cache file counts as complete only when its envelope is valid for the current fingerprint and its serialized chunk is FULL;
- before FULL generation starts, the vanilla generation pyramid supplies the exact dependency radius; the guard snapshots only absent/incomplete positions and starts only when that owned scope has no existing updating/visible/pending-unload holders;
- chunk, entity, and POI persistence is vetoed for that owned dependency scope, while existing FULL positions are never owned by backfill;
- the own loading ticket and idle-pregen claim are released before waiting for dependency holders to drain, while the save guard remains until updating, visible, and pending-unload holders in the owned scope disappear; this avoids turning the broader #47 guard into the freeze/unload cycle handled by #41;
- when a real player loads an incomplete normal chunk, a valid FULL cold-cache entry is preferred and can then promote normally; without a valid cache, the saved ProtoChunk continues through vanilla generation;
- `waiting-no-candidate` is not reported when an LOD entry remains unresolved because normal storage could not be classified.

This change does not patch Chunksmith itself. It keeps ownership in the existing cache/backfill compatibility MOD and uses Vanilla/NeoForge chunk lifecycle boundaries plus the installed Chunksmith 4.6.0 API.
