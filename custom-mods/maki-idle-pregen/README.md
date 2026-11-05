# maki-idle-pregen

Server-only idle pre-generation coordinator originally introduced for Issue #7.

## Issue #27 fix

Version 1.0.0 was compiled with an incompatible hand-written `@Mixin` annotation stub. The shipped class files stored class-level `@Mixin` in `RuntimeVisibleAnnotations`; Sponge Mixin 0.8.7 reads `@Mixin` from the class-retained/invisible annotation table during PREPARE, so the required mixin config failed with `missing an @Mixin annotation`.

Version 1.0.1 moves the maintained source into this Gradle/NeoForge project and compiles against the real NeoForge/Mixin dependency set rather than defining Mixin annotation ABI by hand.

The build also includes `verifyMixinAnnotationRetention`, which checks all three mixin classes with `javap -v` and fails `check` unless:

- class-level `@Mixin` is under `RuntimeInvisibleAnnotations`;
- injector `@Inject` annotations remain under `RuntimeVisibleAnnotations`.

## Runtime behavior

The functional behavior is unchanged from the final Issue #7 implementation:

- enter idle pregen only on a dedicated server with zero players and active Chunksmith/external idle work;
- freeze vanilla simulation without taking ownership of a pre-existing administrator freeze;
- leave immediately when a player appears or idle work stops;
- pause Millenaire village progression and spawn queue execution while preserving candidate admission/deferred recording;
- never start, stop, replace, or recenter Chunksmith tasks.

## Dependencies

- Minecraft 1.21.1
- NeoForge 21.1.262
- Chunksmith 4.6.0
- Millenaire 9.0.3
- Java 21

The local Chunksmith and Millenaire JARs in `../../mods` are compile-only dependencies. NeoForge ModDev supplies the real Minecraft, NeoForge, and Sponge Mixin APIs.

## Validation for 1.0.1

- old 1.0.0 SHA-256: `20e5ec54e5227f4d3f8e9725e133e83bb7423ee6950297b62894cfe459c4fbc3`;
- all three old mixin classes confirmed class-level `@Mixin` under `RuntimeVisibleAnnotations`;
- all three replacement mixin classes confirmed class-level `@Mixin` under `RuntimeInvisibleAnnotations`;
- `@Inject`/`@At` remain runtime-visible;
- replacement classes are Java 21 class major version 65;
- `javap -p -c -s` output for the three mixin classes is unchanged from 1.0.0, so injection methods/descriptors and executable bytecode are unchanged;
- JAR ZIP integrity check passes.

A full Minecraft/NeoForge dedicated-server end-to-end boot is still required in the deployment environment.
