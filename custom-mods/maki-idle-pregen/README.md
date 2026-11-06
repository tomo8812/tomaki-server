# maki-idle-pregen

Server-only idle pre-generation coordinator introduced for Issue #7 and maintained as the authoritative owner of the dedicated-server zero-player simulation phase.

## Issue #42: bounded Millenaire initialization

Millenaire 9.0.3 initializes derived village state from Village.tick. Two startup paths are especially expensive:

- BuildingInstance.backfillBedPositions scans every block in a building plan and can synchronously request chunks through ServerLevel.getBlockState.
- VillageWaypointGraph.rebuild validates many waypoint pairs with full path searches, so a single graph rebuild can perform many MillWalkNodeEvaluator block-state lookups.

Version 1.1.0 keeps those operations synchronous on the server thread, but changes admission:

- with zero players, waypoint graph rebuilds and bed-position backfills are deferred even before a Chunksmith task has started;
- with players online, at most one heavy Millenaire initialization operation is admitted per server tick;
- bed backfill runs only when every chunk intersecting the building volume is already loaded;
- waypoint graph rebuild runs only when chunks within the path-search radius around the village center and all building navigation anchors are already loaded;
- a missing-chunk preflight is retried at most once per second for each operation, avoiding a new polling hotspot;
- deferred operations retain Millenaire's existing dirty/uninitialized state, so they are retried by the normal owner instead of being marked complete or moved off-thread.

The graph preflight uses Millenaire's 64-block maximum waypoint edge/search range plus one chunk of safety margin. It does not call Village.computeVillageChunks, avoiding the protection-cache hot path fixed by Issue #3.

### Diagnostics

Each admitted operation reports its wall time and, while it runs:

- waypoint traversal search count;
- MillWalkNodeEvaluator.getPathType calls;
- PathNavigationRegion accesses that returned EmptyLevelChunk;
- actual EmptyLevelChunk constructor calls during the rebuild;
- ServerChunkCache.getChunk calls;
- calls that requested loading;
- load-request calls whose chunk was not already loaded at method entry.

The guard also periodically reports zero-player, per-tick-budget, and missing-chunk deferral counters. This instrumentation is scoped to the synchronous initialization call stack and does not add background world access.

## Existing idle-pregen behavior

When a dedicated server has zero players and Chunksmith or another registered external idle job is active:

- freeze vanilla simulation without taking ownership of a pre-existing administrator freeze;
- leave immediately when a player appears or idle work stops;
- pause Millenaire village progression and spawn queue execution while preserving deferred candidate recording;
- never start, stop, replace, or recenter Chunksmith tasks.

## Issue #27 build fix

Version 1.0.1 moved this MOD to NeoForge ModDev and the real Sponge Mixin API after the original hand-built JAR placed class-level @Mixin in the wrong annotation-retention table. verifyMixinAnnotationRetention remains part of check and now covers every registered mixin.

## Dependencies

- Minecraft 1.21.1
- NeoForge 21.1.262
- Chunksmith 4.6.0
- Millenaire 9.0.3
- Java 21

The local Chunksmith and Millenaire JARs in ../../mods are compile-only dependencies. NeoForge ModDev supplies the real Minecraft, NeoForge, and Sponge Mixin APIs.

A full Minecraft/NeoForge dedicated-server end-to-end test with the production world remains a deployment-environment validation step.
