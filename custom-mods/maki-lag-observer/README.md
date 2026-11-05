# Maki Lag Observer

Server-only stall diagnostics for Issue #36. This module intentionally does not change Chunksmith, Millénaire, Valkyrien Skies, tick rate, task ownership, or generation policy.

During normal ticks it only records a few timestamps. When a tick or between-tick stall exceeds the configured threshold it appends a JSON object to `logs/maki-lag-observer.jsonl` containing:

- wall time versus server-thread CPU time
- server-thread state, lock owner and sampled stack
- hottest JVM threads by CPU delta over a bounded baseline window
- process/system CPU, heap and GC totals
- Linux load average, CPU/IO/memory PSI, `/proc/self/io`, the server native task's context switches, and all-task context-switch totals
- the process cgroup's v2 `cpu.stat` / `cpu.max` plus visible ancestor limits (with v1 fallback)
- player count, vanilla average MSPT, freeze state, Chunksmith task count and idle-pregen state

The wall/CPU split is the primary discriminator: a long wall stall with similarly high server-thread CPU points at work on the server thread; a long wall stall with little server-thread CPU and RUNNABLE/BLOCKED/WAITING state points at scheduling, lock/I/O or another blocking path. The top-thread sample then identifies competing JVM workers such as worldgen or physics threads.

Configuration is read from `config/maki-lag-observer.properties` and can be changed between samples without restarting.

This observer is diagnostic rather than a speculative performance patch. It is intended to be deployed on the affected Oracle ARM64 server and correlated by timestamp with Minecraft's `Can't keep up` warnings, `maki-lod-cpu` status, LOD progress, and any follow-up JFR capture. A real-world run is still required to name the dominant production stall cause.
