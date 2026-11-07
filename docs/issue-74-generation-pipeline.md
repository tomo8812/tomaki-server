# Issue 74: generation, persistence and promotion

This document describes cache 1.4.0, governor 1.4.1 and Millenaire compatibility 1.0.6 on `fix/issue-74`. Distributed MODs and the generated `decompiled` branch remain unchanged.

## Evidence and boundaries

Baseline main `4593d0c`, shipped cache 1.2.2 and its decompiled counterpart already contained `LodSupportMixin -> captureFromChunksmith -> capture`. The original issue's assertion that Backfill was the sole capture caller was incorrect. The generic hook was replaced with Chunksmith 4.6.0's actual FULL-request offer boundary, restricted to owned Follow tasks, zero players, server-thread execution and the original Horizon/compatibility vetoes.

The native code and actual compiled NeoForge 21.1.262 artifact were checked for:

- `NeoForgeWorld.getChunkAtAsync`: ticket safe point, FULL Future, offer, ticket cleanup.
- `CsLodStoreSink.offer/persist` and `CsLodRegionStore.write`: enqueue is not a write ACK; append payload precedes the slot update, and IOException is caught by the sink.
- `ChunkMap.save`: boolean true follows submission, not completion, of its write Future.
- `ChunkStorage.write -> IOWorker.store -> RegionFileStorage.write`: the Future is the native completed-write boundary, including failures. It does not imply power-loss fsync durability.
- `IOWorker.scanChunk`: pending normal writes take precedence over disk. A missing region header is only a discovery hint, never final ownership authority.
- `ChunkStatusTasks.full`: generated entities enter the persistent entity manager before FULL completion.
- `ChunkSerializer`: FULL writing omits entity storage; FULL post-load accepts legacy `entities`, and reading sections calls POI consistency reconstruction.
- `EntityStorage.storeEntities`: separate empty/nonempty native write call sites; its cached empty no-op is not an independent successful-write receipt.

## State and resource ownership

The cache MOD owns capture, compatibility, promotion and Backfill coordinate state. `maki-lod-cpu` owns Follow tasks and the shared generation lease. Millenaire compatibility owns its protected-chunk snapshots. Lightweight pressure observation crosses these boundaries; no second coordinate-owner map is introduced in the governor.

Backfill transitions are:

1. Bounded discovery and resumable dependency inventory; no Follow barrier yet.
2. Server-thread preflight; unavailable candidates become per-coordinate retries.
3. Reserve generation lease and drain owned Follow requests. External tasks are not adopted, stopped or capped.
4. Reclassify storage through the native scanner, including pending normal writes, in bounded batches. Recheck players, budget and all live holder maps on the server thread immediately before arming.
5. Acquire the owned ticket at the safe point and wait for FULL. In-flight generation is never force-cancelled by clearing its save guard.
6. Offer LOD and synchronously detach chunk/entity NBT while the FULL ticket still makes the chunk safe to access. No live LevelChunk is read on a worker.
7. Return the generation lease and ticket/idle-work claim. Detached validation, compression and publication proceed under separate memory/count/disk budgets. Follow can dispatch while these storage ACKs are pending.
8. Join the actual native LOD record ACK and cache publication ACK. Success retires the discovery/retry demand; failure schedules a retry and advances discovery. Both paths retain the save guard until natural holder drain or permanent player handoff. Unload during storage wait cannot prematurely clear active completion state.

Only one Backfill pipeline is active at a time. This deliberately bounds retained work while allowing Follow to overlap the storage stage; it is not unbounded concurrent worldgen.

## Capture and promotion protocol

Schema 2 explicitly requires an entity-complete FULL payload. Entity roots are selected from all existing persistent entity sections for the coordinate, including hidden generation sections, using vanilla `shouldBeSaved` / `Entity.save` root/passenger semantics. Detached entity NBT is included in the FULL tag's supported `entities` field. POI uses vanilla reconstruction; Millenaire deferred state remains owned by its existing SavedData/compatibility system.

Schema 1 cannot prove entity completeness and is rejected/quarantined when inspected rather than labelled complete. Old generations remain subject to the existing manifest/quarantine policy. This is an intentional serialization compatibility change; historical quarantine adoption does not turn a schema-1 payload into schema 2.

Promotion atomically renames an available entry to `.in-use` before returning it. An interrupted/in-use payload cannot be replayed or replaced by a late capture. A normal live handoff without a cache read also closes this publication/replay window before its normal write.

The state sequence is `available -> claimed/in-use -> loaded -> native chunk/entity write ACKs -> retired`. Save-enqueue is not an ACK. Nonempty entity payloads require an entity write started after entity restoration, avoiding correlation with an older outstanding write. Zero-entity payloads require the chunk ACK. Failed writes are sticky and retain the unavailable recovery payload; retaining it does not mean it may automatically roll back player changes.

Normal FULL data remains authoritative. If normal storage is incomplete but an interrupted `.in-use` entry exists, the outer load Future fails explicitly with the retained payload location instead of silently re-promoting an old snapshot or generating replacement terrain. This is an exceptional recovery condition, not a wait with no terminal result. Do not delete such a marker merely to make a failed coordinate load: inspect/recover its state first. A restart after only one native store completed is not an atomic cross-store transaction; administrative recovery may still be required.

## Backpressure, retry and shutdown

- Cache snapshots: bounded simultaneous count and estimated NBT bytes, per-entry size limit, synchronous admission before snapshot allocation. Validation is separate from the publication writer and never joins the native scanner on that writer.
- Inspection timeout drops detached work; native inspection slots remain bounded until completion (or server shutdown). Expiry/stop is checked before publication; a valid atomic publication racing a timeout may still leave a reusable complete entry. The persistent in-use ownership barrier is checked under the publication lock and cannot be reopened by a late capture.
- Disk admission: active cache bytes and a normal-world free-space reserve, separately from quarantine retention. Defaults are 20 GiB active / 2 GiB free reserve. No automatic active-data eviction is added. Disk observations run off-thread and refresh every 10 seconds; generation reconciliation runs independently of capture/scan admission, invalidates inventory after moves, and retries failures rather than latching permanently.
- Governor admission observes cache and Millenaire pressure in addition to native queues/MSPT/memory/GC/I/O signals. CPU saturation alone remains insufficient to pause generation. Long generation leases repay their actual service cost rather than clamping all Follow time to 30 seconds; storage time is not charged as generation exclusion.
- Millenaire snapshots reserve bounded count/estimated memory before allocation; coalesced/discarded/drained snapshots return their leases. The executor is bounded. The existing synchronous caller fallback preserves a necessary initial/update LOD when asynchronous admission fails; this exceptional overload path may still cost server time and is not claimed to be free.
- Discovery is sliced by time and position count, resumes dependency progress across yields, caches directory inventories, batches region headers, and pauses admission for players/pressure. Final native authority probes are separate from those cheap hints. Filesystem calls themselves are not guaranteed interruptible.
- Retry debt records coordinate, attempt, next time and reason in a bounded persistent ledger. Failed targets do not pin the discovery cursor. Dimensions rotate and each dimension alternates due retries with new discovery. Cursor wrap remains the fallback when the bounded retry table is full; no strict per-coordinate deadline is claimed for an arbitrary backlog.
- Stopping closes admission, invalidates detached captures, releases the generation budget/owned tickets and retains save guards through world closure. Native in-flight generation is not unsafely cancelled. Interrupted promotion claims remain unavailable on disk across restart.

## Diagnostics

One-minute pipeline reports distinguish native-dispatch (the native slow-decision elapsed value, including generated checks), adapter-request, ticket safe-point wait/work, FULL Future, LOD offer, cache serialization, validation queue, inspection, publication queue/write, and capture total. Percentiles are bucket upper bounds, with maximum and counts above 1000 ms. Native threshold crossings are distinct from actual rate-limited width reductions.

Intervals overlap and must not be summed. Native and adapter populations differ; percentile subtraction is not a measure of generated-check cost. Worldgen internal versus disk I/O remains unknown. Live pending count/bytes/age and queue/disk gauges cover unresolved work that has not yet produced a histogram sample. LOD publication receipts mean native write completion, not fsync or atomic completion of all world stores.

## Validation and remaining runtime work

Automated checks cover admission races; resumable dependency traversal even at one position per slice; persistent retries and per-coordinate delays; exact record-identity LOD receipts and errors; count/byte admission and recovery; promotion ACK order, empty/nonempty/passenger NBT, failed/restarted claims and publication races; capacity recovery and disk reserve; clean Follow samples and long-slice service repayment; bounded Millenaire memory; installed Chunksmith/NeoForge method/call-site ABI; existing fingerprint/quarantine/scope/ticket regressions; and deployment-source/Mixin annotation consistency.

These checks do not replace a full installed-MOD server run. Still required on a disposable copy of the production world:

1. Match CPU quota, seed, MODs, configs and ungenerated workload in A/B/A windows. Compare LOD writes, direct/cache success, Backfill completions, actual generation-lease time, queues/bytes, CPU/MSPT/GC/memory/I/O and latency. No throughput improvement percentage is claimed here.
2. Exercise normal start, many cooperative yields, transient holder conflicts, persistent failed coordinates, multiple dimensions, pressure entry/recovery, old-generation quarantine, player join/leave, ticket/unload, uncompleted native Futures and server stop.
3. Promote chunks with structural entities/passengers, block entities, structures, light, scheduled ticks, POI and Millenaire deferred state; save and restart. Confirm no duplicate/lost entities or premature Diorama exploration.
4. Inject native chunk/entity/LOD write failure and interrupted promotion. Confirm recovery artifacts are retained, normal FULL data wins, and incomplete claims fail explicitly rather than replaying stale player state.
5. Confirm every Mixin applies with the full MOD set. Bytecode/annotation checks do not prove runtime multi-Mixin composition.
