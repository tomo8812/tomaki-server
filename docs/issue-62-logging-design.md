# Custom MOD logging and diagnostics design

Normal Minecraft/NeoForge logging is the operational interface for every maintained custom MOD. `latest.log` must be sufficient to distinguish startup, normal operation, intentional waiting, degraded or retryable behavior, and hard failure. High-volume diagnostic capture is owned only by `maki-mods-observer`.

## Logging ownership

| Custom MOD | Normal INFO visibility | WARN / ERROR policy | Files |
| --- | --- | --- | --- |
| `maki-airship-compat` | startup; recovered delayed passenger state | recovery expiry is WARN; real failures include stack traces | none |
| `maki-airship-foundation-compat` | startup; successful ship furniture mount | rejected/cancelled seat cleanup is WARN | none |
| `maki-assembly-tracking` | startup; bounded repair schedule; config state | invalid config disables repair with WARN | config only |
| `maki-chunksmith-chunk-cache` | startup/shutdown; cache lifecycle; backfill state transitions and 30 s active summary | guarded wait/degradation is WARN with suppression; terminal failure is ERROR/WARN by recoverability | authoritative cache/cursor data remains; no status JSON |
| `maki-diorama-share` | startup and owning-service share lifecycle | protocol/persistence failures retain stack traces | Diorama persistence remains |
| `maki-idle-pregen` | startup; task/pause/resume lifecycle | guard/incompatible-state/recovery failures are WARN/ERROR | generation/task data remains |
| `maki-lod-cpu` | startup/shutdown; governor changes and low-frequency CPU/MSPT summary; follow state transitions and 60 s summary | migration/persistence failures are WARN/ERROR | Chunksmith task state and migration backup/audit remain; no status JSON |
| `maki-millenaire-lod-compat` | startup; queue/lifecycle summaries | unsafe transitions and worker failures are WARN/ERROR | authoritative Millenaire/Chunksmith state only |
| `maki-mods-observer` | startup and explicit diagnostic configuration | writer/config failures are WARN | optional `logs/maki-mods-observer.jsonl` and `logs/maki-mods-observer-nbt.jsonl`, disabled by default |
| `maki-ship-anchor-fix` | startup | anchor recovery/compatibility failure is WARN/ERROR | none |
| `maki-ship-edge` | startup | no periodic warning path expected | none |
| `maki-ship-footsteps` | startup | no periodic warning path expected | none |
| `maki-ship-save` | startup; scoped LOD discard decisions | shipyard lookup failure is ERROR with stack trace and fail-safe task stop | Chunksmith/VS persistence remains authoritative |

## Level rules

- **DEBUG**: packet/entity/chunk-level detail and troubleshooting-only traces.
- **INFO**: initialization, enable/disable, meaningful state transitions, start/complete results and low-frequency summaries.
- **WARN**: unexpected waiting, retryable degradation, timeout, fallback or skipped work. Repeated conditions must be state-change or interval limited.
- **ERROR**: real failures requiring intervention or cases where safe operation is stopped; include the throwable.

No custom MOD should emit INFO every tick, every chunk, every entity or every packet.

## Diagnostics owner

`maki-mods-observer` replaces `maki-lag-observer` without a compatibility alias. It provides a bounded server-stall/CPU/JVM/Linux sampler and slow NBT-copy caller/structure tracing. Detailed file output is opt-in in `config/maki-mods-observer.properties`; both `enabled` and `nbt-copy-observer-enabled` default to `false`. Both diagnostic streams use bounded asynchronous writers. The default unchanged-config session limit is 30 minutes and 5000 events per stream, with a 16 MiB cap per file; rewriting the config explicitly starts a new session. Stall diagnostics defer `/proc`/cgroup reads, JSON encoding and file I/O to the writer thread instead of the Minecraft server thread. The observer does not become the authoritative owner of LOD, cache, ship, village or player state.

## Removed status I/O

`maki-status-io` and the diagnostic files `logs/maki-lod-cpu-status.json`, `logs/maki-lod-follow-status.json`, and `logs/maki-chunk-cache-backfill-status.json` are retired. Their operational content is represented by state-change and low-frequency standard log summaries in the owning MODs. Persistent task progress, cache data, Diorama data, migration backups/audits and game/mod save data are not status files and are not removed.

## Deployment migration

There is intentionally no legacy alias or automatic migration. Before deployment: stop the server; remove old `maki-lag-observer-*.jar` and `maki-status-io-*.jar`; remove `config/maki-lag-observer.properties`; optionally remove old observer/status diagnostic outputs; deploy the complete rebuilt `maki-*.jar` set; and enable `config/maki-mods-observer.properties` only for bounded troubleshooting. Do not delete world data, Chunksmith state/cache, Diorama persistence, LOD state, migration backup/audit data or other authoritative saves.
