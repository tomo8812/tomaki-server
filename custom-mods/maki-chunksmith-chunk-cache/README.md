# maki-chunksmith-chunk-cache

Server-side cold FULL chunk cache and protected Backfill integration for Minecraft 1.21.1 / NeoForge and Chunksmith.

## Current behavior (1.4.7)

- Save completed pregenerated FULL chunks in compressed NBT, including their entity snapshots.
- Restore saved FULL chunks for normal play and as source data for Chunksmith LOD repair.
- Keep chunk, entity and POI persistence protected while pregeneration owns temporary FULL chunks.
- Hand ownership to normal vanilla storage once a player needs the chunk.
- Verify saved FULL schema, completed entities, coordinates, dimension, stored identity and terrain sections before using a cache file.
- Keep incomplete or damaged cache data in quarantine without discarding other valid chunks.
- Migrate previously saved FULL files from historical generation-named directories into one stable schema-3 storage namespace.
- Copy and verify old files before committing saved ownership identities; retain unreadable files and preservation claims for recovery.
- Coordinate guarded Backfill and shared resource budgets without taking ownership of Chunksmith's native LOD reuse decisions.
- Apply disk-capacity protections to cache writes and quarantine retention.

### Ownership and reuse

**Chunksmith decides whether existing `.cslod` LOD data can be reused.**
This MOD makes that decision only for its own saved FULL chunk NBT.

FULL cache storage uses a stable 24-character directory identity tied to the cache envelope format. It does not read or hash Minecraft's running DataVersion, the world seed, `level.dat`, generation settings, MOD JARs, ordinary server configuration, or datapacks to decide whether a FULL cache file can be reused.

Saved chunk NBT must still contain a correctly typed `DataVersion` field so the stored chunk has valid metadata. That field is **not compared** with the running Minecraft version.

`lodCommitted` is the completion receipt for publishing a FULL cache entry together with its corresponding LOD write. It does **not** authorize or reject reuse of the separate Chunksmith LOD cache.

Changing the game's content or generator deliberately is handled alongside that particular server change; there is no automatic prediction, exclusion or version-compatibility policy for future changes.

### Existing-world migration

Earlier releases used 24-hex generation directories and `manifest-v2.txt` files containing hashes of settings, MODs and versions. They remain readable as **historical cache owner metadata only**. The saved configuration entries are ignored. The MOD checks the actual NBT of each candidate FULL file and moves validated copies to the stable storage namespace.

The old FULL files are retained until the destination manifest records their original envelope identities. Corrupt or conflicting copies stay isolated for inspection. The `.promotions` recovery ledger continues protecting ownership of normally saved chunk/entity/POI data.

### Configuration

`config/maki-chunksmith-chunk-cache.properties` contains cache capacity and quarantine-retention controls only. No world-generation compatibility settings or monitoring extensions are supported.

### Testing

Run `./gradlew check` from this custom MOD's project. Tests cover persisted FULL NBT validation, legacy manifest parsing, durable migration copies, interrupted persistence, quarantine retention, backfill ownership and cross-MOD Mixin hooks.

Source: `custom-mods/maki-chunksmith-chunk-cache`.
