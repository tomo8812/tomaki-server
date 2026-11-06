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


## Issue #41 backfill ticket lifecycle

Version 1.0.3 removes the circular dependency between the backfill idle-work claim and vanilla's expiring `UNKNOWN` chunk ticket.

- the existing `BACKFILL_TICKET` is the sole ticket owned by backfill;
- FULL acquisition polls `getChunkFuture(..., FULL, false)`, so the lookup does not create an auxiliary vanilla `UNKNOWN` ticket;
- the custom ticket and idle-work claim are released as soon as durable LOD/cache work is finished, while `BackfillGuard` remains active until the chunk actually unloads or a player takes ownership;
- a successful completed guard is detached from the single active backfill pipeline, so a delayed unload does not block the next candidate; guards support multiple coordinates per dimension and are bounded to 8 detached entries;
- detached guards are released on the real unload callback or player handoff, warn after 30 seconds, and never force-unload or delete another owner's ticket;
- pre-FULL loading warns after 30 seconds and aborts safely after five minutes;
- status schema 2 records phase/progress/ticket timestamps, active and detached guard state, server freeze state, unload wait time, and typed wait reasons.

This deliberately does not change Chunksmith 4.6.0, `maki-idle-pregen`, fingerprint rotation (#40), synchronous load scheduling (#44), status-file I/O (#45), or neighboring ProtoChunk persistence (#47).
