# Maki Millenaire Chunksmith Compatibility

This project owns the maintained patch layer for the deployed `maki-millenaire-lod-compat` mod.

The 1.0.5 build overlays its changed classes/resources onto the deployed 1.0.4 JAR. This keeps the unrelated, already-tested reservation/replay/protection logic byte-for-byte identical while making the Issue #43 explored-LOD save/unload path maintainable in source.

Issue #43 routes both existing village LOD refreshes and the protected-chunk initial explored capture through the same safe snapshot pipeline. The server thread copies immutable section/biome/light data and serializes each distinct BlockState only once; a single low-priority worker builds/submits the `CsLodChunk`.

Chunksmith remains the owner of initial-capture admission, presence marking and explored-LOD counters. The compatibility Mixin only redirects the one `LodSupport.offer` invocation for Millénaire-protected chunks, so #9's one-enqueue rule is preserved. Non-protected explored chunks keep Chunksmith's native path.
