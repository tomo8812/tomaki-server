# maki-ship-anchor-fix

Maintained source for the VS2 player/ship anchoring compatibility mod.

Issue #30 found that version 1.1.0 had all five registered Mixin classes compiled with class-level `@Mixin` in `RuntimeVisibleAnnotations`. Injector annotations were already runtime-visible.

Version 1.1.1 rebuilds the existing source against NeoForge ModDev 21.1.262 and the real Sponge Mixin API supplied by ModDev. No hand-written Mixin annotation stubs are used. `check` verifies retention for all five registered Mixin classes.

Runtime anchoring behavior and injection targets are intentionally unchanged.
