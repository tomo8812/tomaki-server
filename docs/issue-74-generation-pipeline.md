# Issue 74: generation pipeline

## Evidence and ownership

Baseline: main `4593d0c`, Chunksmith 4.6.0, chunk-cache 1.2.2. Both main source and the shipped JAR (and its decompiled counterpart) already contain `LodSupportMixin -> CacheManager.captureFromChunksmith -> capture`. The issue's assertion that Backfill is the only capture caller is therefore incorrect. No production log/config/world was supplied to this workspace, so the cause of the reported 398 slow requests, or whether production actually ran that JAR, cannot be established retrospectively.

The confirmed admission defect is independent: `tryStartPending` reserved shared budget before checking current target/dependency holders. That stopped new Follow dispatch before knowing whether a candidate was currently eligible. A loaded scope is not proof of permanent ineligibility: Follow-owned transient holders may also disappear during a drain. The fix checks first on the server thread, then preserves the same final check after drain. The authoritative holder/scope owner remains BackfillGuard; the authoritative task owner remains maki-lod-cpu TaskOwnership. The existing cooldown and clean-sample handshake are unchanged.

The existing cache MOD is the natural owner of snapshots, pending writes, Backfill admission and cache metrics. Creating another MOD would duplicate that state. Chunksmith and other distributed JARs are unchanged. The decompiled branch is read-only evidence, never a source/base for this PR.

## Capture boundary

The generic `LodSupport.offer` tail could be invoked by explored saves/unloads, Millenaire refreshes, and external tasks, not just an owned FULL request. It is replaced by the exact `NeoForgeWorld.lambda$getChunkAtAsync$8` call site in the installed Chunksmith JAR. The original offer runs once; capture follows while the request still owns its ticket. A current owned task, zero players, server thread, no Backfill suppression and the existing HorizonGuard discard decision are all required. HorizonGuard excludes kept/player/previously generated positions and existing compatibility vetoes remain in force.

NBT serialization stays on the server thread via `ChunkSerializer.write`. Only detached NBT goes to the bounded writer. Before publication, normal storage is classified on the writer: existing FULL or unknown data rejects the capture independently of task-state/HorizonGuard hints. The server thread never joins that storage scan. Queue admission happens before serialization; a racing queue rejection still completes false. Direct and Backfill writes share pending futures keyed by the actual ServerLevel and coordinate; failures remove the pending marker and remain eligible for later scans. Completion means the existing compressed write/atomic publication finished, not merely enqueue. This does not introduce an fsync guarantee against power loss.

This reuses the existing cache envelope, fingerprints, quarantine, capacity maintenance, promotion and normal-save cleanup. It does not claim to have discovered why production did not benefit from the old hook.

## Interval diagnostics

Once per minute, `latest.log` reports effective Chunksmith configuration, event counts and bounded latency histograms. Percentiles are **bucket upper bounds** in milliseconds, with exact observed maximum and count above 1000 ms. No independent status file or per-chunk INFO is added.

- `native-dispatch`: the wall-clock elapsed value passed to `adjustFromChunkLatency`, including the preceding isChunkGenerated Future. It is sampled only when native I/O throttling invokes that decision; `native-slow-threshold` uses that task's actual `maxChunkMillis`. It counts threshold crossings, not successful width reductions (backoff is rate-limited). `native-width-last` is the last concurrent width observation after the decision, not a width-transition counter.
- `adapter-request`: getChunkAtAsync invocation through completion, including cleanup. It omits isChunkGenerated and does not have a one-to-one population match with native-dispatch (e.g. generated skips and throttling disabled). All GenerationTask adapters, including external tasks, are observed; they are not modified/capped.
- `ticket-safe-point-wait` and `ticket-safe-point-work`: queued safe-point callbacks; setup and completion callbacks are combined. These overlap other intervals and must not be summed.
- `full-future`: private FULL request invocation through Future completion. Disk reading vs worldgen vs scheduling remain **unknown**, not mislabeled as I/O.
- `lod-offer`: synchronous extraction/offer; it does not measure durable CSLOD persistence. Existing Chunksmith/governor written-count and queue reports remain the durable LOD evidence.
- `cache-serialize`, `cache-queue`, `cache-inspect`, `cache-write`: server-thread snapshot, writer queue wait, normal-storage classification (including scanner Future wait), and successful compressed publication. `cache-write` excludes classification. `capture-total` runs from pending registration through terminal success/failure, including rejected attempts.
- `cacheIo.pending`, `oldestPendingMs`, `writerQueue`, `writerActive`: live, weakly consistent gauges, including unresolved work. A stalled head inspection has not yet contributed a cache-inspect sample, and its successors have not contributed cache-queue samples until dequeued. The gauges expose this during the stall; completion histograms alone cannot. The single writer and bounded queue are unchanged.
- `backfill-drain` and `dispatch-barrier`: observed reservation-to-start/release durations; include productive draining work, not CPU idle time. Reservation cancellation is observed at the next controller request.
- `preflight-*` vs `postflight-*`, `reservations`, `direct-full-candidates`, `direct-ineligible`, source-specific `*-capture-started` / `*-saved` / `*-failed`, duplicate avoidance and queue rejection distinguish admission/capture outcomes.

No CPU cap, native backoff threshold, parallelism, clean sample or safety guard is relaxed. The old data does not justify any particular concurrency optimum.

## Validation boundary

Deterministic admission tests cover preflight rejection without reservation, dependency conflict introduced during drain, conflicts with an already-held reservation, not-ready drain and successful admission. Histogram tests cover threshold precision and overflow. Installed-JAR bytecode checks pin the synthetic method/call sites so an upstream change fails the build. Existing fingerprint/quarantine/legacy adoption, scopes, ticket lifecycle, shared-budget and Mixin checks remain enabled.

These tests do not replace an actual server run or prove worldgen-equivalent cache promotion. In particular ChunkSerializer writes chunk data (blocks, biomes, block entities, structures, light and scheduled ticks); vanilla entities and POI have separate storage paths. The pre-existing cache protocol is not extended with an entity-store envelope by this change. POI reconstruction, entity lifecycle and Millenaire pending behavior require the runtime checks below; no all-data equivalence is claimed solely from a FULL tag.

## Deployment acceptance still required

Use a disposable copy of the same production world, seed, MODs and configs for each comparison. Do not compare a warmed/generated region with an ungenerated region. Record effective config from the new runtime log, artifact hashes, CPU quota and time window.

1. Run equal-load A/B/A windows, collecting native width/cap/in-flight, successful LOD writes, cache saves by source, MSPT/CPU/heap/GC/memory/I/O PSI and queues. Exclude shared-budget transition windows from Follow concurrency comparisons as the existing governor does. Compare completed caches and durable LOD separately; do not add overlapping timing intervals or equate future count to worldgen count.
2. Compare pre/post-drain refusal counts, reservation count, observed barrier/drain time and Backfill completions/hour. A lower postflight rejection count alone does not prove improvement: preflight may also reject transient Follow-held scopes that would have drained. Check completed Backfills and LOD writes together. Confirm preflight-rejected coordinates return after cursor wrap (10-minute exhausted rescan, persisted across restart), rather than being marked complete. Test concentrated scope conflicts and eventual unload for starvation.
3. At a newly generated disposable coordinate, confirm direct `saved`, a valid cache envelope, no normal FULL region/entity/POI exposure and no Diorama exploration. Revisit it to confirm Backfill does not reacquire it. Fail a write/full queue, then confirm later retry; interrupt publication and confirm corrupt/incomplete entries fall back.
4. Move a player to that coordinate; inspect blocks/block entities/structures/light/ticks, POI, entities and Millenaire deferred/pending state, save and restart, then compare. Repeat with normal FULL data already present, external task, player join/leave during requests, unresolved Future, holder conflict after preflight, village protection and seed/worldgen changes.
5. Confirm all registered Mixins apply at server startup with the complete installed MOD set. Bytecode site checks alone do not prove multi-Mixin runtime composition.

The production acceptance conditions (including the historical 398 slow requests, 72 transitions and actual throughput improvement) remain unverified until these measurements are collected.


## Review observations and decisions

The human review on PR #78 raised four advisory checks, rather than requiring all four mechanisms to change.

| Concern | Decision and runtime evidence needed |
| --- | --- |
| Preflight may reject Follow-held transient scopes | Valid tradeoff; no ownership inference or guard bypass added. Holder availability does not identify its ticket owner or guarantee a future drain. Inspect preflight/postflight counts alongside Backfill completions and LOD writes, using DEBUG coordinate/scope records when necessary. Deferred coordinates return after cursor exhaustion and rescan; this is not a guaranteed ten-minute per-coordinate deadline with an arbitrarily large backlog. Production opportunity loss remains unmeasured. |
| adapter-request excludes the initial generated check | Added native-dispatch at the actual slow-decision boundary; retain adapter-request as a separate interval. Different populations/clocks mean their percentiles must not be subtracted to estimate the generated check. |
| Single writer can block on normal-storage inspection | Added separate cache-inspect and capture-total intervals, plus live pending-age/queue gauges. This also corrects the old cache-write label, which included inspection time. The writer is not speculatively parallelized and no timeout clears ownership while an operation may still finish. |
| Synchronous snapshot can contribute to native backoff | Valid; snapshot stays on the safe server thread. Compare cache-serialize, adapter-request and native-dispatch in matching workload windows, while distinguishing capture outcomes and native threshold crossings from actual width reductions. Coincident histogram spikes are evidence to investigate, not proof of per-request causation. No claim of zero added latency or measured benefit is made. |

Both read-side and snapshot-side optimization decisions remain measurement-driven; these review changes improve attribution rather than change the safety/admission policy.
