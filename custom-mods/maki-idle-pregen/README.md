# maki-idle-pregen

Server-only idle pre-generation coordinator originally introduced for Issue #7.

## Issue #50 fix (1.0.2)

Vanilla tick freeze does not stop Valkyrien Skies' independent physics pipeline. A keep-active ship can therefore keep moving while the server is empty and idle pre-generation is active.

Version 1.0.2 extends the existing idle coordinator instead of adding ship-state ownership to another MOD:

- when idle pregen starts, inspect the server's current VS pipeline;
- if VS physics is running, set its existing `arePhysicsRunning` switch to false and record ownership;
- if physics was already paused, do not claim it and do not resume it later;
- if another owner resumes physics during the idle session, release ownership and do not immediately pause it again;
- on player return, idle-work completion, or server shutdown, resume only a pause owned by this MOD;
- while this MOD owns the pause, let VS `postTickGame()` perform its normal game-stage bookkeeping but redirect only the final `ConcurrentLinkedQueue.add(...)` physics-frame handoff so the paused physics queue cannot accumulate stale game frames.

The bundled VS implementation's physics loop resets its timing-debt accumulator and recent TPS queue before waiting while `arePhysicsRunning=false`. That means the supported pause path does not convert wall-clock idle duration into a large physics catch-up step on resume.

The `postTickGame` guard deliberately preserves VS game-stage bookkeeping and per-tick state reset; it suppresses only the final physics-frame enqueue. It also does not cancel VS chunk loading, ship activation management, terrain hooks, server saving, or Chunksmith generation. It exists only while the idle coordinator itself owns the physics pause.

The current VS bridge is version-sensitive because the distributed JAR exposes the pause switch through an internal pipeline API. The build therefore verifies the exact bundled
`ValkyrienSkies-Fabric-MC1.21.1-v3.2.0-connectorfix7.jar` for:

- `IShipObjectWorldServerProvider.getVsPipeline()`;
- `VsiPipeline.getArePhysicsRunning()/setArePhysicsRunning(boolean)`;
- the concrete pipeline `postTickGame()` method and its `ConcurrentLinkedQueue.add(Object)` physics-frame handoff used to prevent queued-frame backlog.

The distributed Valkyrien Skies JAR is not modified.

## Issue #27 fix (1.0.1)

Version 1.0.0 was compiled with an incompatible hand-written `@Mixin` annotation stub. The shipped class files stored class-level `@Mixin` in `RuntimeVisibleAnnotations`; Sponge Mixin 0.8.7 reads `@Mixin` from the class-retained/invisible annotation table during PREPARE, so the required mixin config failed with `missing an @Mixin annotation`.

Version 1.0.1 moved the maintained source into this Gradle/NeoForge project and compiles against the real NeoForge/Mixin dependency set rather than defining Mixin annotation ABI by hand.

The build includes `verifyMixinAnnotationRetention`, which checks every mixin class with `javap -v` and fails `check` unless:

- class-level `@Mixin` is under `RuntimeInvisibleAnnotations`;
- injector `@Inject` annotations remain under `RuntimeVisibleAnnotations`.

## Runtime behavior

The coordinator:

- enters idle pregen only on a dedicated server with zero players and active Chunksmith/external idle work;
- freezes vanilla simulation without taking ownership of a pre-existing administrator freeze;
- pauses VS physics with the same ownership rule;
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
- the current bundled VS pause ABI and concrete post-tick hook;
- a Java harness covering owned pause/resume, pre-existing external pause preservation, late pipeline availability, and external resume ownership release.

Minecraft/NeoForge end-to-end validation with a real keep-active ship remains an environment-level test and must verify ship transform/velocity stability, Chunksmith progress, and VS worker CPU during an actual idle generation interval.
