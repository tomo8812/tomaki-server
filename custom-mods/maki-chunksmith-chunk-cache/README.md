# maki-chunksmith-chunk-cache

Version 1.4.1 combines generation-focused compatibility auditing, per-entry serialization migration, ticket-safe asynchronous acquisition, bounded retired dependency scopes, and pressure-aware shared-resource scheduling. See [integration validation](../../docs/pr-integration-2026-11-06.md).


Maintained source for the server-only Chunksmith cold-cache/backfill compatibility mod.

Issue #30 found that version 1.0.0 had all seven registered Mixin classes compiled with class-level `@Mixin` in `RuntimeVisibleAnnotations`. Injector annotations were already runtime-visible and the cache/backfill implementation itself was not implicated.

Version 1.0.1 rebuilds the decompiled 1.0.0 sources against NeoForge ModDev 21.1.262 and the real Sponge Mixin API supplied by ModDev. No hand-written Mixin annotation stubs are used. `check` runs `verifyMixinAnnotationRetention` for all seven registered Mixin classes.

The cache, promotion, fingerprint, Millenaire bridge and backfill logic are intentionally unchanged.


## Issue #35 observability and idle coordination

Version 1.0.2 keeps backfill ownership in this MOD and adds explicit runtime state instead of inferring progress from the shared Chunksmith LOD writer:

- writes `logs/maki-chunk-cache-backfill-status.json` with scan/busy phase, active chunk, counters, LOD submissions and durable LOD refresh completions;
- claims `maki-idle-pregen` external idle work only while a guarded backfill chunk is active, and releases that claim when ownership ends;
- does not start, stop, resume, replace, recenter or adopt any Chunksmith generation task;
- remains blocked while a Chunksmith generation task is active, preserving the existing separation between follow generation and cache backfill.
- releases the backfill guard and idle-work claim immediately when loading fails before a FULL chunk exists; loaded-chunk failures retain the guarded-unload lifecycle.

This makes non-zero global CSLOD writes explainable even when the follow-owned GenerationTask completion rate is zero.


## Issue #40 compatibility manifests and quarantine

Version 1.1.0 replaces the old "hash every file, then recursively delete every other generation" policy with an auditable compatibility boundary:

- generation compatibility is based on evidence that can change generated results: Chunksmith/effective worldgen settings, datapacks, level.dat worldgen state, seed, dimension, generator class and Minecraft DataVersion remain relevant;
- ordinary QoL/runtime MODs and unrelated configs are generation-neutral by default instead of becoming relevant solely because they are unknown;
- known code-driven worldgen MODs remain protected, and JARs that contain worldgen/structure/dimension datapack resources are detected as generation-sensitive automatically;
- operators can add code-driven generation MOD IDs or config paths through fingerprint.generation-sensitive-mod-ids / fingerprint.generation-sensitive-paths; explicit ignore paths cannot neutralize built-in generation-sensitive paths;
- maki_chunksmith_chunk_cache itself is excluded from generated-result compatibility; cache envelope schema is validated per entry rather than mixed into the world-generation fingerprint;
- config/chunksmith/tasks/** remains excluded exactly as before; mutable Chunksmith task progress is not a compatibility input;
- every v2 generation gets manifest-v2.txt containing per-input digest, size, compatibility classification and reason;
- a previous v2 manifest is compared with the current one so logs identify old/new fingerprints, changed inputs, trigger (capture, load, or scan), entry count and bytes;
- incompatible generations are moved atomically to .quarantine rather than immediately deleted and are never used for promotion while incompatible;
- quarantines have retention/count/byte limits, but automatic capacity pruning protects freshly quarantined data for the configured minimum age;
- /makichunkcache quarantine list, discard <id>, and restore <id> run maintenance on the cache I/O executor. Restore is refused unless the quarantined manifest exactly matches the current compatibility fingerprint;
- a 1.0.x generation can be adopted without regeneration only when its exact legacy fingerprint still matches the current full input set. Otherwise it is quarantined because compatibility cannot be proven.

The current compatibility boundary is evidence-driven rather than an all-MOD allow/deny audit. Unknown QoL/runtime additions do not rotate the generation by themselves. Direct worldgen evidence, built-in code-driven generators and operator-declared generation inputs remain relevant; malformed/unreadable JAR inspection still fails closed.

check also runs FingerprintPolicySelfTest, covering default-neutral QoL/runtime updates, ordinary config changes, built-in and operator-declared worldgen inputs, JAR worldgen-resource detection, protected Chunksmith settings, datapack/seed changes, policy-transition re-adoption and corrupt manifest rejection.
## Issue #41 backfill ticket lifecycle

Version 1.0.3 removes the circular dependency between the backfill idle-work claim and vanilla's expiring `UNKNOWN` chunk ticket.

- the existing `BACKFILL_TICKET` is the sole ticket owned by backfill;
- FULL acquisition uses the Chunksmith ticket-safe-point private invoker with `createMissingChunk=false`, so the lookup does not create an auxiliary vanilla `UNKNOWN` ticket;
- the custom ticket and idle-work claim are released as soon as durable LOD/cache work is finished, while `BackfillGuard` remains active until the chunk actually unloads or a player takes ownership;
- a successful completed guard is detached from the single active backfill pipeline, so a delayed unload does not block the next candidate; guards support multiple coordinates per dimension and are bounded to 8 detached entries;
- detached guards are released on the real unload callback or player handoff, warn after 30 seconds, and never force-unload or delete another owner's ticket;
- pre-FULL loading warns after 30 seconds; after five minutes an in-flight FULL future keeps its ticket and persistence guard until it settles while only the idle-work claim is released, and a timeout with no in-flight generation aborts safely;
- status schema 2 records phase/progress/ticket timestamps, active and detached guard state, server freeze state, unload wait time, and typed wait reasons.

The integrated release also preserves #40, #44, #45 and #47; Chunksmith 4.6.0 itself is unchanged.
## Issue #44 non-blocking backfill chunk acquisition

Version 1.0.3 removes the public `ServerChunkCache.getChunkFuture(...)` call from the server-thread backfill start path.

- Backfill requests are queued onto Chunksmith 4.6.0's existing ticket safe point.
- The request uses Chunksmith's `ServerChunkCacheMixin.invokeGetChunkFutureMainThread` path, matching Chunksmith's own `NeoForgeWorld.getChunkAtAsync` lifecycle instead of entering vanilla `managedBlock`.
- `BACKFILL_TICKET`, `BackfillGuard`, Millenaire spawn suppression, player handoff and cold-cache persistence remain owned by this MOD.
- Ticket installation is tracked explicitly so a player handoff that happens before the queued safe-point request runs cannot leave or later create an orphan backfill ticket.
- Backfill status records safe-point delay, distance-manager update wall time, private future-call wall time, setup CPU/wall time, future wait time, pending chunk tasks and pending chunk writes.
- The integrated release combines this acquisition path with #41 lifecycle, #45 diagnostic I/O and #47 dependency scopes.
## Issue #47 dependency-safe legacy backfill

Version 1.0.4 makes legacy CSLOD backfill distinguish the state of normal chunk storage instead of treating every non-zero region header entry as explored/full.

- candidate scans classify normal storage as absent, incomplete ProtoChunk, FULL, or unknown by reading only the saved `Status` through the vanilla chunk scanner;
- incomplete ProtoChunks remain eligible for backfill, while existing FULL chunks remain authoritative and unknown/corrupt storage fails closed;
- a cache file counts as complete only when its envelope is valid for the current fingerprint and its serialized chunk is FULL;
- before FULL generation starts, the vanilla generation pyramid supplies the exact dependency radius; the guard snapshots only absent/incomplete positions and starts only when that owned scope has no existing updating/visible/pending-unload holders;
- chunk, entity, and POI persistence is vetoed for that owned dependency scope, while existing FULL positions are never owned by backfill;
- the own loading ticket and idle-pregen claim are released before waiting for dependency holders to drain, while the save guard remains until updating, visible, and pending-unload holders in the owned scope disappear; this avoids turning the broader #47 guard into the freeze/unload cycle handled by #41;
- when a real player acquires a dependency chunk, that coordinate is permanently removed from the backfill-owned save scope before player changes can later unload; for incomplete normal storage a valid FULL cold-cache entry is preferred, otherwise the saved ProtoChunk continues through vanilla generation;
- `waiting-no-candidate` is not reported when an LOD entry remains unresolved because normal storage could not be classified.

This change does not patch Chunksmith itself. It keeps ownership in the existing cache/backfill compatibility MOD and uses Vanilla/NeoForge chunk lifecycle boundaries plus the installed Chunksmith 4.6.0 API.

## Issue #66 shared follow/backfill resource scheduling

Version 1.2.0 no longer treats every active Chunksmith task as an unconditional backfill blocker.

- candidate discovery runs while follow continues, so an empty Backfill queue never disturbs LOD generation;
- once a safe real-chunk candidate exists, maki-lod-cpu 1.4.0 reserves a short shared-budget slice and holds only **new dispatch** from a follow-owned Chunksmith task; it never calls stop/resume/recenter or replaces the task;
- the already-dispatched follow requests drain to zero before Backfill starts, closing the race where follow and Backfill could generate the same target/dependency scope concurrently;
- external/unowned Chunksmith tasks are never stopped, adopted, recentered, capped, or dispatch-blocked by this path;
- CPU, actual MSPT, heap/GC, system-memory headroom, I/O PSI, LOD/write queues and native backpressure control admission. High CPU by itself is intentionally not a blocker;
- one guarded Backfill runs per slice. Its measured duration plus durable LOD/storage progress determines the next dynamic spacing, bounded so follow keeps priority while continuously safe Backfill cannot starve indefinitely;
- cache writes that are already in flight are tracked, and cache-backed FULL chunks are reported as generated to Chunksmith so follow does not immediately regenerate and rewrite the same cached chunk;
- the existing BACKFILL_TICKET, dependency-scope persistence guards, Millénaire suppression, player handoff, durable LOD refresh, cache capture, quarantine generations and unload handling remain authoritative.

This design deliberately uses a brief dispatch barrier instead of lifecycle pause/resume. Chunksmith 4.6.0 has no public per-coordinate reservation API, so fully concurrent follow/Backfill would leave dependency-overlap races unless the distributed MOD were patched. Keeping that exclusion in the custom compatibility MOD preserves upstream upgradeability.


## Issue #68 semantic compatibility inputs

Version 1.2.1 keeps the #40 manifest/quarantine mechanism but fingerprints effective semantics where equivalence can be proven without weakening the default fail-closed policy.

- `config/zfastnoise.mixin.properties` is parsed with the same `java.util.Properties.load(InputStream)` and `Boolean.parseBoolean` behavior used by zfastnoise 1.0.13. Only `mixin.perf.biome` and `mixin.perf.noise` affect compatibility; generated timestamp comments, key order, equivalent whitespace and unknown preserved keys stay audit-only raw differences. Missing keys use zfastnoise's real `true` defaults. Parse failures remain raw and compatibility-relevant.
- MOD JARs are **not** exempted by filename or MOD ID. `AuditedModCompatibility` contains exact artifact/content attestations for installed components whose decompiled/current code was audited as generation-neutral: `maki_lod_cpu`, `maki_lod_cpu_online_fix`, `maki_diorama_share`, `maki_mods_observer`, Jade, EMI, ClientSort and Sound Physics Remastered. The stable compatibility input is the audited behavior contract, while the raw JAR is retained as an ignored audit entry.
- Attestations accept either the exact audited raw SHA-256 or the exact canonical JAR-content SHA-256. The canonical digest sorts entry names and hashes entry payloads while excluding archive/signature packaging metadata, so a byte-for-byte rebuild/repack of the same audited code/resources does not invalidate cache compatibility. Any changed code/resource payload, unknown version/artifact, ambiguous multi-MOD JAR, malformed metadata, or declared indirect zfastnoise override remains fail-closed relevant.
- Generation-affecting MODs/configs/datapacks, world seed/dimension/generator state and unknown inputs remain compatibility-relevant. The change intentionally removes the previous MOD-ID-only observation exemption.
- Compatibility schema v3 can adopt a v2 generation only when every relevant input can be normalized to the same proven semantics. Historical zfastnoise recovery is limited to the two known Issue #68 raw signatures or an old raw entry that exactly matches the currently parsed source. Historical audited MOD entries normalize only from exact attested raw digests.
- Proven-equivalent quarantined generations are re-adopted automatically during reconcile. Existing active cache entries win path conflicts; only non-conflicting quarantined entries are recovered. `/makichunkcache quarantine restore <id>` uses the same proof and refuses unknown/unproven differences.
- Logs distinguish zfastnoise source-format/comment-only differences, effective setting changes, audited generation-neutral JAR changes and generic fail-closed changes. No additional unbounded diagnostic file is introduced.

The production quarantine manifest is not stored in this repository, so the specific 5,688-entry quarantine reported in Issue #68 cannot be declared reusable from repository evidence alone. It is preserved and is reused only if its stored manifest passes the proof above.

This implementation changes only the source-owned `maki-chunksmith-chunk-cache` MOD and its deployment JAR; it does not patch zfastnoise, Chunksmith, Jade, EMI, ClientSort, Sound Physics Remastered, or any other distributed MOD.

## Issue #74 pipeline admission and direct capture

Version 1.3.0 checks live candidate/dependency eligibility before reserving Follow's dispatch budget and repeats the check after drain. Direct capture moves from generic LOD refreshes to the owned FULL-request offer boundary, with shared pending-write futures, early queue admission and minute-level pipeline diagnostics. Distributed MODs and concurrency settings are unchanged. See [investigation, metrics and remaining runtime acceptance](../../docs/issue-74-generation-pipeline.md).

## Generation/storage stages (1.4.0)

Schema 2 includes generated entity roots/passengers and uses native chunk/entity write receipts for promotion retirement. Backfill releases generation exclusion after its safe snapshot, then awaits record-correlated LOD/cache publication separately. Snapshot memory, disk admission, resumable scans and per-coordinate retry debt are bounded. Interrupted `.in-use` entries are recovery artifacts, never automatic rollback candidates. See the [pipeline protocol and validation boundary](../../docs/issue-74-generation-pipeline.md).


## Issue #79 generation compatibility and retained cache migration

Version 1.4.1 separates generated-result compatibility from the cache envelope format. The generation fingerprint no longer changes merely because the cache envelope schema changes or because an unrelated QoL/runtime MOD/config changes. Chunksmith/effective worldgen inputs, known code-driven terrain/structure/village MODs, JAR worldgen resources and operator-declared generation inputs remain relevant. The Millénaire compatibility 1.0.5/1.0.6 releases are normalized only when their exact reviewed JAR hashes match the audited generated-result contract; a same-name rebuild fails closed. Policy transitions can re-adopt a previous generation when the only formerly-relevant inputs are now explicitly classified generation-neutral, while real generation-input differences still block adoption.

Old data is evaluated per entry instead of invalidating an entire generation. Schema 1 is retained but not promoted automatically because its historical writer did not persist a provable complete entity set. Schema 2 contains an entity-complete FULL payload and may therefore be promoted to normal storage, but it does not satisfy LOD/backfill completion because it predates the record-correlated LOD acknowledgement. Schema 3 is complete only when both entity completeness and the LOD receipt are present. Re-adoption copies only proven-safe entries into the active generation while preserving the original source and reporting source/adopted/recovered-existing/duplicate/promotion-only/unproven/invalid counts. If a server stops after a payload was copied but before the active manifest was committed, the next reconciliation only recognizes a byte-identical source/destination pair, reconstructs its LOD-complete sidecar, and restores the source fingerprint authorization.

Generation-wide automatic quarantine/deletion is disabled. Truly corrupt individual cache files can still be quarantined at the coordinate level. Historical and quarantined generations are retained unless an administrator explicitly uses the discard command. Warning thresholds count both `.quarantine` generations and inactive fingerprint directories and do not delete data; active cache writes remain bounded by the active-cache byte cap and normal-world free-space reserve, so storage pressure stops new cold-cache writes rather than consuming the space required for authoritative world saves.
