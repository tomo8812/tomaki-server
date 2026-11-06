# maki-idle-pregen

Server-only idle pre-generation coordinator originally introduced for Issue #7.

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

Version 1.0.0 was compiled with an incompatible hand-written `@Mixin` annotation stub. The shipped class files stored class-level `@Mixin` in `RuntimeVisibleAnnotations`; Sponge Mixin 0.8.7 reads `@Mixin` from the class-retained/invisible annotation table during PREPARE, so the required mixin config failed with `missing an @Mixin annotation`.

Version 1.0.1 moved the maintained source into this Gradle/NeoForge project and compiles against the real NeoForge/Mixin dependency set rather than defining Mixin annotation ABI by hand.

The build includes `verifyMixinAnnotationRetention`, which checks every mixin class with `javap -v` and fails `check` unless class-level `@Mixin` is runtime-invisible and injector annotations are runtime-visible.

## Runtime behavior

The coordinator:

- enters idle pregen only on a dedicated server with zero players and active Chunksmith/external idle work;
- freezes vanilla simulation without taking ownership of a pre-existing administrator freeze;
- freezes VS physics time while preserving VS state-frame processing;
- leaves immediately when a player appears or idle work stops;
- pauses Millenaire village progression and spawn queue execution while preserving candidate admission/deferred recording;
- never starts, stops, replaces, or recenters Chunksmith tasks.

## Dependencies

- Minecraft 1.21.1
- NeoForge 21.1.262
- Chunksmith 4.6.0
- Millenaire 9.0.3
- Java 21
- optional runtime integration: the currently bundled Valkyrien Skies build

The local Chunksmith and Millenaire JARs in `../../mods` are compile-only dependencies. Valkyrien Skies is accessed reflectively at runtime and is checked from the bundled JAR during `check`, avoiding a hard class-link dependency from the always-on idle coordinator.

## Validation

`gradle clean check jar` verifies:

- real NeoForge/Mixin compilation;
- Mixin annotation retention for all registered mixins;
- the current bundled VS pause/physics-step/game-frame-drain ABI;
- a Java harness covering async owned pause/pulse/re-pause/resume, pre-existing pause preservation, synchronized zero-delta behavior, late pipeline availability, external resume ownership release, and async/synchronized mode transitions.

Minecraft/NeoForge end-to-end validation with a real keep-active ship remains an environment-level test. It must compare actual ship transform/velocity, Chunksmith progress, and VS worker CPU across a real idle-generation interval. In synchronized mode, correctness is covered by the zero-delta boundary but a comparable CPU reduction is not expected because VS still executes synchronized passes.
