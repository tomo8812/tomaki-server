# Maki Lag Observer

Server-only stall diagnostics for Issue #36. This module intentionally does not change Chunksmith, Millénaire, Valkyrien Skies, tick rate, task ownership, generation policy, or save semantics.

During normal ticks it records only lightweight timestamps and counters. When a tick or between-tick stall exceeds the configured threshold it appends a JSON object to `logs/maki-lag-observer.jsonl` containing:

- wall time versus server-thread CPU time
- server-thread state, lock owner and sampled stack
- hottest JVM threads by CPU delta over a bounded baseline window
- process/system CPU, heap and GC totals
- Linux load average, CPU/IO/memory PSI, `/proc/self/io`, the server native task's context switches, and all-task context-switch totals
- the process cgroup's v2 `cpu.stat` / `cpu.max` plus visible ancestor limits (with v1 fallback)
- player count, vanilla average MSPT, freeze state, Chunksmith task count and idle-pregen state

## Slow NBT copy tracing

Version 1.1.0 adds targeted instrumentation for the unresolved production stall where the 24-frame watchdog stack ended inside recursive `ListTag.copy` / `CompoundTag.copy` calls.

The observer hooks Minecraft's `CompoundTag.copy()` and `ListTag.copy()` only on the server thread. Nested hooks count the structure already traversed by the copy; they do not perform a second NBT walk. When a **top-level** copy exceeds `nbt-copy-threshold-ms`, the module writes `logs/maki-nbt-copy-observer.jsonl` with:

- wall and server-thread CPU time for the copy
- tick sequence, freeze state and player count
- root type and identity hash
- root direct-entry count
- compound/list container counts
- total container entries, which also gives the copied tag-occurrence count (`containerEntries + 1`)
- maximum container depth
- repeated slow copies of the same root identity in the same tick
- the first caller outside `net.minecraft.nbt`
- a deep caller stack, independently capped by `nbt-copy-max-stack-frames`

Slow-copy events are handed to a bounded 64-entry daemon queue so the server thread never performs the diagnostic file write. If the queue is full, the event is dropped and `droppedEventsTotal` is reported by later samples rather than blocking gameplay.

The general watchdog stack depth is also raised from 24 to 128 frames so a future stall sample is less likely to terminate inside recursive NBT frames.

Configuration is read from `config/maki-lag-observer.properties`. The NBT tracing settings are:

- `nbt-copy-observer-enabled`
- `nbt-copy-threshold-ms`
- `nbt-copy-max-stack-frames`

This remains diagnostic instrumentation rather than a speculative patch to a distributed mod. The production sample does not expose the caller below the NBT recursion, so changing Millénaire, Chunksmith, Valkyrien Skies, or vanilla save behavior before identifying the caller would risk data loss or unrelated regressions. A production rerun is required before any caller-specific redundant-copy removal can be justified.
