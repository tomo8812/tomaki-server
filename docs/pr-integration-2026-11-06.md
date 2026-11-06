# Open PR integration — 2026-11-06

Base: `c1de3e1840c93494b024de1ba86ab5cbb59dd1e8` (`main`).
All eleven PR heads are retained as merge ancestors, so their commits and rationale remain inspectable.

| PR | Preserved behavior | Deployment artifact |
| --- | --- | --- |
| #51 | Unsent Diorama add/edit/delete intent survives snapshots; cumulative ACK gating | maki-diorama-share 1.0.1 |
| #52 | Chunksmith ticket-safe-point/private-invoker FULL acquisition, timing telemetry, stale-request cancellation | chunk-cache 1.1.1 |
| #53 | Deep NBT-copy caller/structure attribution with bounded asynchronous diagnostics | maki-lag-observer 1.1.0 |
| #54 | One shared coalescing diagnostic I/O worker; authoritative progress remains durable/synchronous | maki-status-io 1.0.0; LOD CPU 1.3.4; chunk-cache 1.1.1 |
| #55 | Owned async VS pause and zero-delta state-frame synchronization; force/listener suppression; synchronized-mode handling | idle-pregen 1.1.1 |
| #56 | Loaded-only, budgeted Millenaire initialization, upstream throttle-state preservation and startup rehydration guard | idle-pregen 1.1.1 |
| #57 | Compatibility manifests, fail-closed generation quarantine, audit/restore and periodic retention maintenance | chunk-cache 1.1.1 |
| #58 | Status-aware FULL cache/normal-storage validation; dependency-footprint persistence protection and permanent player handoff | chunk-cache 1.1.1 |
| #59 | No auxiliary UNKNOWN ticket; guarded in-flight timeout; early own-ticket/idle-claim release; bounded retired guards | chunk-cache 1.1.1 |
| #60 | Explicit backup/resume/stash/switch/restore without adopting external task ownership; TaskLoader cache synchronization | LOD CPU 1.3.4 |
| #61 | Immutable protected explored-LOD snapshots, worker extraction/store submission, coalescing and shutdown drain | millenaire-lod-compat 1.0.5 |

## Overlap resolution

The independent pre-integration JARs are replaced with one rebuilt version per MOD ID. The LOD CPU build combines migration tests/accessors with the status-I/O dependency and publication paths. Idle-pregen registers both the VS and Millenaire Mixin sets, and retains the VS ownership harness and ABI checks.

Chunk-cache combines the complementary lifecycle changes at source level:

- Acquisition uses only `BACKFILL_TICKET` and `invokeGetChunkFutureMainThread(..., FULL, false)` at Chunksmith's ticket safe point. A queued/in-flight request is observed across every load phase, including timeout, rather than being lost when the phase name changes.
- In-flight timeout retains the ticket, token and complete save-protection scope; only the idle-work claim is released.
- Successful durable LOD/cache work releases its ticket/claim and detaches the entire dependency scope from the active pipeline. Up to eight scopes may drain independently.
- A target unload callback alone does not clear its dependencies. Updating, visible and pending-unload holder maps must all drain first. Failed pre/post-FULL scopes follow the same protection rule.
- Player-owned coordinates leave the suppression set permanently. Target handoff retains protection for other dependencies until those holders drain.
- New scopes cannot overlap outstanding scopes or take over pre-existing holders. Separate targets and dimensions cannot overwrite each other's ownership.
- FULL-envelope validation retains the generation store's legacy-fingerprint adoption rule and corrupt-file quarantine.
- Backfill diagnostics retain lifecycle, scope, acquisition-stage and ownership fields and publish through the shared status-I/O worker.

## Validation

Local Java 21 / Gradle 8.10.2 / NeoForge 21.1.262:

- `clean check jar` passed for all nine maintained source projects: idle-pregen, status-io, lod-cpu, chunk-cache, lod-cpu-online-fix, diorama-share, lag-observer, millenaire-lod-compat and ship-anchor-fix.
- Existing migration JUnit tests, Diorama state-machine tests, slow-I/O tests, VS pause ownership harness, fingerprint/quarantine tests, issue #41/#47 checks and Mixin/third-party ABI checks passed.
- Added `BackfillScopesSelfTest`: independent outstanding scopes, dimension isolation, permanent player-coordinate release, selective drain and duplicate-admission refusal.
- Shipped custom-MOD Mixin annotation audit passed; six audit regression tests passed.
- Deployment check passed: fifteen unique custom MOD IDs; 104 embedded maintained Java sources match the checkout. All custom JAR ZIP checks passed. The deployment-source check is included in CI.
- An isolated dedicated server with the combined deployment set started, loaded all dimensions, executed external-task status/backup/stash/restore, froze/unfroze, saved and shut down with exit code 0. After stash the persisted owner was `none`; after restore it was `external`. The restored task exactly matched the original bytes and SHA-256 `0482ea6ccd8056e8636d67496dbe474eb02a68770cc394206a34b4437cf95e0a`.

The runtime test used a disposable world and loopback-only port, not the production world. Startup included upstream optional-compat warnings and VS startup frame-queue warnings; no fatal Mixin/application error prevented startup or normal shutdown.

## Deployment and remaining validation

Preserve the production `config/chunksmith/tasks/` directory while applying the JAR update. It is now ignored by Git; do not use a mirroring deployment that deletes this runtime progress before migration backup. Use `/makilod task status` and `backup` before an explicit switch.

The isolated test is not a production-world performance A/B run. Production village initialization latency, two-client Diorama timing, keepActive-ship transforms/forces, continuous real backfill across overlapping dependency footprints, and power-loss durability still require workload-specific runtime validation.

PR #53 supplies attribution instrumentation. It does not identify or optimize the original production deep-copy caller; that issue's caller-specific performance work remains dependent on a fresh captured sample.
