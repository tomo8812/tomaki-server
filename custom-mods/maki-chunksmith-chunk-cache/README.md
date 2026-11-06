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


## Issue #44 non-blocking backfill chunk acquisition

Version 1.0.3 removes the public `ServerChunkCache.getChunkFuture(...)` call from the server-thread backfill start path.

- Backfill requests are queued onto Chunksmith 4.6.0's existing ticket safe point.
- The request uses Chunksmith's `ServerChunkCacheMixin.invokeGetChunkFutureMainThread` path, matching Chunksmith's own `NeoForgeWorld.getChunkAtAsync` lifecycle instead of entering vanilla `managedBlock`.
- `BACKFILL_TICKET`, `BackfillGuard`, Millenaire spawn suppression, player handoff and cold-cache persistence remain owned by this MOD.
- Ticket installation is tracked explicitly so a player handoff that happens before the queued safe-point request runs cannot leave or later create an orphan backfill ticket.
- Backfill status records safe-point delay, distance-manager update wall time, private future-call wall time, setup CPU/wall time, future wait time, pending chunk tasks and pending chunk writes.
- The separate post-success unload lifecycle from Issue #41 and synchronous status-file I/O from Issue #45 are intentionally not changed here.
