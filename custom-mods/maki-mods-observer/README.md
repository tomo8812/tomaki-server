# Maki Mods Observer

Server-only, opt-in diagnostics for Maki custom MODs. Normal operation and business-state visibility belong in standard Minecraft/NeoForge logging from each owning MOD; this module is only for bounded troubleshooting captures.

Both diagnostic paths are disabled by default. Enable them explicitly in `config/maki-mods-observer.properties` only while investigating a problem.

- General watchdog output: `logs/maki-mods-observer.jsonl`
- Slow NBT-copy output: `logs/maki-mods-observer-nbt.jsonl`

The watchdog retains the previous tick/server-thread CPU, stack, JVM, Linux PSI/cgroup and MOD-state sampling knowledge. Slow NBT tracing retains the bounded server-thread hook and 64-entry asynchronous writer queue introduced for Issue #36. These files are not normal status or authoritative state and may be absent when diagnostics are disabled.

No legacy `maki_lag_observer` MOD ID, old config filename, or old output filename is supported by this replacement.

Diagnostic output is opt-in and bounded per unchanged config session by `diagnostic-max-duration-minutes` (default 30), `diagnostic-max-events` (default 5000 per stream), the bounded writer queues/sampling thresholds, and `diagnostic-max-file-mb` (default 16 MiB per file). Rewriting the config explicitly starts a new session; reaching any limit suppresses further samples until then. Diagnostic disk writes run on daemon writer threads, not the Minecraft server thread.
