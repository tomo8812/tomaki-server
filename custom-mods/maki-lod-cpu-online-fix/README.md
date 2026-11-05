# maki-lod-cpu-online-fix

Compatibility fix for Issue #11.

The existing `maki-lod-cpu` remains the owner of Chunksmith concurrency. This mod only adjusts the online path of `AdaptivePolicy.update(...)`:

- with players online and `generate-while-online=true`, adaptive sampling remains active;
- reads of the adaptive maximum inside `update(...)` are bounded by `min(max-concurrent, online-max-concurrent)`;
- reads of the adaptive minimum are also bounded by `online-max-concurrent`, so a larger global `min-concurrent` cannot force the online ceiling above the configured online limit;
- entering or leaving online adaptive mode resets the experiment once, rather than once per sample;
- with `generate-while-online=false`, the original `players-online-paused` path is unchanged.

It is intentionally separate from the `maki-lod-cpu` JAR because Issues #5 and #10 already have concurrent branches replacing that binary. The dependency range is constrained to `maki_lod_cpu [1.2.1,1.4.0)`; revalidate the Mixin target before widening it.

Validation performed for 1.0.1:

- Java 21 compilation against compile-only stubs matching the referenced public types/annotations.
- The compile-only `@ModifyVariable`, `@Redirect`, and nested `@At` stubs use `RetentionPolicy.RUNTIME`, matching Sponge Mixin, so injector annotations are emitted as runtime-visible.
- `javap -p -v` verification that `@ModifyVariable` and both `@Redirect` annotations are under `RuntimeVisibleAnnotations` in the shipped JAR.
- `javap -p -v` verification that the compiled Mixin targets `AdaptivePolicy.update(AdaptiveSample,int,Settings)` and redirects both `Settings.max` and `Settings.min`.
- Boundary checks include `min-concurrent=4`, `max-concurrent=8`, `online-max-concurrent=2`, which yields effective online min/max of `2/2`.
- Cross-check against `maki-lod-cpu 1.2.1` decompiled code and the current `fix/issue-5` 1.3.0 source, which retain the same target method and fields.

Issue #32 / version 1.0.2:

- Moved `AdaptivePolicyMixin` into `maki.lodcpu.onlinefix.mixin`. The ordinary
  `@Mod` entrypoint stays in `maki.lodcpu.onlinefix`, outside the reserved package.
- Added a Gradle build using NeoForge 21.1.262's real Sponge Mixin API instead of
  annotation stubs. This also corrects the two `@Unique` field annotations from
  class retention to runtime retention. The policy handlers are unchanged.
- Build: `gradle -p custom-mods/maki-lod-cpu-online-fix clean check jar` (Java 21,
  Gradle 8.10.2). Output: `build/libs/maki-lod-cpu-online-fix-1.0.2.jar`.
- Verified the shipped JAR's annotation descriptors, element encoding, retention,
  config registration and package boundary. Verified `AdaptivePolicy` transforms
  in the full dedicated server, and the server reaches `Done`.

See [the complete audit](../../docs/issue-32-mixin-audit.md) for all 13 mods,
runtime evidence, interactions and client-side validation limits.
