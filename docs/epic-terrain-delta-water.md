# Epic Terrain water delta placement

The shipped `mods/epicterrain-0.3.0-1.21.1.jar` contains the JSON definition tracked at `custom-resources/epicterrain/data/etn/worldgen/configured_feature/delta_water.json`. Replace that exact JAR entry when updating this definition; the loose source file is for maintenance and is not loaded automatically.

`size` is now fixed at 8 (previously 8–16), and `rim_size` is 0–7 (previously 0–8). Minecraft's DeltaFeature adds the rim offsets to the water coordinates. With an in-square origin of 0–15, the previous configuration could write at coordinate 39, beyond the FEATURES stage's permitted -16–31 range relative to the generating chunk's start. The new configuration writes within -8–30 on each horizontal axis, including the unshifted grass rim. Its adjacent-block checks stay within -9–31. Water deltas will be smaller.

Validation: parsed the JSON; exhaustively checked all 16×16 origins, all Manhattan-shape positions for size 8, and every rim offset 0–7 on each axis, including adjacent reads. The patched ZIP passes CRC validation and has the same entry list as the original; this JSON is the only entry with changed uncompressed content. No server/world-generation run has been performed.

Deploy the updated JAR and restart the server. The change applies to future generation; existing clipped terrain is not repaired. Existing generation caches may be invalidated by their normal compatibility fingerprint checks.
