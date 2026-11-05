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

Runtime Minecraft/NeoForge integration is not executed in this environment.
