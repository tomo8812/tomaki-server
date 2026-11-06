# maki-idle-pregen

Server-only idle pre-generation coordinator introduced for Issue #7 and maintained as the authoritative owner of the dedicated-server zero-player simulation phase.

<<<<<<< HEAD
## Issue #50 fix (1.0.2)

Vanilla tick freeze does not stop Valkyrien Skies' independent physics pipeline. A keep-active ship can therefore keep moving while the server is empty and idle pre-generation is active.

Version 1.0.2 extends the existing idle coordinator instead of adding ship-state ownership to another MOD.

### Simulation boundary

During idle pre-generation the coordinator now treats the following as frozen simulation:

- vanilla game simulation through the existing server tick freeze;
- Millenaire village/queue progression through the existing guards;
- Valkyrien Skies **physics time** by forcing each VS physics-step delta to `0.0`.

VS game/physics state synchronization is deliberately not discarded. `postTickGame()` still constructs its normal `FG` frame, and the VS physics stage still drains and applies those frames. This preserves one-shot body lifecycle, terrain/voxel, dimension, joint, and other game-to-physics updates generated while Chunksmith is working.

### Async physics

For the repository's normal `synchronizePhysics=false` configuration:

- if the VS pipeline is running, idle pregen sets its existing `arePhysicsRunning` switch to false and records ownership;
- if it was already paused, the coordinator does not claim or later resume another owner's pause;
- after each VS `postTickGame()` frame is created, an owned pause wakes the worker for one state-synchronization pass;
- the mixin replaces that pass's physics delta with `0.0`, so the frame is drained on VS' own physics thread without advancing ship time;
- when the zero-delta pass returns, the coordinator pauses the worker again;
- player return, idle-work completion, or server shutdown resumes only a pause owned by this MOD;
- an external async resume releases ownership and is not immediately re-paused in the same idle session.

The async runner resets its timing-debt accumulator and TPS sample queue whenever it waits on `arePhysicsRunning=false`, so wall-clock idle duration is not converted into a later catch-up step.

With the repository's `physicsTicksPerGameTick=3`, normal async physics targets about three physics passes per game tick. Idle mode wakes it at most once per game frame, reducing solver wakeups while still preserving the state synchronization required by worldgen/shipyard data.

### Synchronized physics

VS' synchronized runner does not consult `arePhysicsRunning`. The coordinator therefore does **not** claim that flag as a successful pause when the concrete pipeline's synchronized-mode field is active.

The same `FQ.a(double)` delta guard still applies, so synchronized physics steps receive `0.0` seconds while idle. This preserves the synchronized pipeline's own handshakes and frame processing without advancing ship time. CPU suppression is weaker in this mode because VS still executes its configured synchronized passes; the current repository configuration is `synchronizePhysics=false`.

### Compatibility boundary

The distributed Valkyrien Skies JAR is not modified. The integration is version-sensitive because the current bundled implementation exposes the pause flag publicly but the concrete physics-step/synchronized-mode details are internal.

`gradle clean check jar` therefore verifies the exact bundled
`ValkyrienSkies-Fabric-MC1.21.1-v3.2.0-connectorfix7.jar` for:

- `IShipObjectWorldServerProvider.getVsPipeline()`;
- `VsiPipeline.getArePhysicsRunning()/setArePhysicsRunning(boolean)`;
- concrete `FQ` synchronized-mode field `c`;
- concrete `FQ.a(double)` physics-step entry point;
- `FQ.postTickGame()`;
- the `FN.a(double, boolean)` path still draining queued game frames before physics work.

A VS update that changes these internals must fail the build-time compatibility check and be re-reviewed rather than silently using an invalid hook.

## Issue #27 fix (1.0.1)
=======
## Issue #42: bounded Millenaire initialization
>>>>>>> origin/fix/issue-42

Millenaire 9.0.3 initializes derived village state from Village.tick. Two startup paths are especially expensive:

<<<<<<< HEAD
Version 1.0.1 moved the maintained source into this Gradle/NeoForge project and compiles against the real NeoForge/Mixin dependency set rather than defining Mixin annotation ABI by hand.

The build includes `verifyMixinAnnotationRetention`, which checks every mixin class with `javap -v` and fails `check` unless class-level `@Mixin` is runtime-invisible and injector annotations are runtime-visible.
=======
- BuildingInstance.backfillBedPositions scans every block in a building plan and can synchronously request chunks through ServerLevel.getBlockState.
- VillageWaypointGraph.rebuild validates many waypoint pairs with full path searches, so a single graph rebuild can perform many MillWalkNodeEvaluator block-state lookups.

Version 1.1.0 keeps those operations synchronous on the server thread, but changes admission:

- with zero players, waypoint graph rebuilds and bed-position backfills are deferred even before a Chunksmith task has started;
- Millenaire's `ServerStartedEvent` rehydration rebuild is routed through the same guarded stale-rebuild path instead of calling the graph rebuild directly;
- with players online, at most one heavy Millenaire initialization operation is admitted per server tick;
- bed backfill runs only when every chunk intersecting the building volume is already loaded;
- waypoint graph rebuild runs only when chunks within the path-search radius around the village center and all building navigation anchors are already loaded;
- graph chunk-envelope scanning occurs only after Millenaire's own `WaypointRebuildThrottle.tryAcquire` grants a rebuild; clean/throttled attempts do not rescan the village;
- if that post-throttle preflight defers the rebuild, the throttle's request/backoff state is restored so the custom guard does not create artificial upstream backoff;
- a missing-chunk preflight is retried at most once per second for each operation, avoiding a new polling hotspot;
- deferred operations retain Millenaire's existing dirty/uninitialized state, so they are retried by the normal owner instead of being marked complete or moved off-thread.
>>>>>>> origin/fix/issue-42

The graph preflight uses Millenaire's 64-block maximum waypoint edge/search range plus one chunk of safety margin. It does not call Village.computeVillageChunks, avoiding the protection-cache hot path fixed by Issue #3.

<<<<<<< HEAD
The coordinator:

- enters idle pregen only on a dedicated server with zero players and active Chunksmith/external idle work;
- freezes vanilla simulation without taking ownership of a pre-existing administrator freeze;
- freezes VS physics time while preserving VS state-frame processing;
- leaves immediately when a player appears or idle work stops;
- pauses Millenaire village progression and spawn queue execution while preserving candidate admission/deferred recording;
- never starts, stops, replaces, or recenters Chunksmith tasks.
=======
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
>>>>>>> origin/fix/issue-42

## Issue #27 build fix

Version 1.0.1 moved this MOD to NeoForge ModDev and the real Sponge Mixin API after the original hand-built JAR placed class-level @Mixin in the wrong annotation-retention table. verifyMixinAnnotationRetention remains part of check and now covers every registered mixin.

## Dependencies

- Minecraft 1.21.1
- NeoForge 21.1.262
- Chunksmith 4.6.0
- Millenaire 9.0.3
- Java 21
- optional runtime integration: the currently bundled Valkyrien Skies build

<<<<<<< HEAD
The local Chunksmith and Millenaire JARs in `../../mods` are compile-only dependencies. Valkyrien Skies is accessed reflectively at runtime and is checked from the bundled JAR during `check`, avoiding a hard class-link dependency from the always-on idle coordinator.

## Validation

`gradle clean check jar` verifies:

- real NeoForge/Mixin compilation;
- Mixin annotation retention for all registered mixins;
- the current bundled VS pause/physics-step/game-frame-drain ABI;
- a Java harness covering async owned pause/pulse/re-pause/resume, pre-existing pause preservation, synchronized zero-delta behavior, late pipeline availability, external resume ownership release, and async/synchronized mode transitions.

Minecraft/NeoForge end-to-end validation with a real keep-active ship remains an environment-level test. It must compare actual ship transform/velocity, Chunksmith progress, and VS worker CPU across a real idle-generation interval. In synchronized mode, correctness is covered by the zero-delta boundary but a comparable CPU reduction is not expected because VS still executes synchronized passes.
=======
The local Chunksmith and Millenaire JARs in ../../mods are compile-only dependencies. NeoForge ModDev supplies the real Minecraft, NeoForge, and Sponge Mixin APIs.

A full Minecraft/NeoForge dedicated-server end-to-end test with the production world remains a deployment-environment validation step.
>>>>>>> origin/fix/issue-42
