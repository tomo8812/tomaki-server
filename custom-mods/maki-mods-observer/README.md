# Maki Mods Observer

Server-only, opt-in diagnostics for Maki custom MODs. Normal operation and business-state visibility belong in standard Minecraft/NeoForge logging from each owning MOD; this module is only for bounded troubleshooting captures.

Both diagnostic paths are disabled by default. Enable them explicitly in `config/maki-mods-observer.properties` only while investigating a problem.

- General watchdog output: `logs/maki-mods-observer.jsonl`
- Slow NBT-copy output: `logs/maki-mods-observer-nbt.jsonl`

The watchdog retains the previous tick/server-thread CPU, stack, JVM, Linux PSI/cgroup and MOD-state sampling knowledge. Slow NBT tracing retains the bounded server-thread hook and 64-entry asynchronous writer queue introduced for Issue #36. These files are not normal status or authoritative state and may be absent when diagnostics are disabled.

No legacy `maki_lag_observer` MOD ID, old config filename, or old output filename is supported by this replacement.

Diagnostic output is rate-limited by the sampling thresholds/queue and capped per file by `diagnostic-max-file-mb` (default 16 MiB). Once the cap is reached, new samples are suppressed/dropped until an operator rotates or removes the file.
