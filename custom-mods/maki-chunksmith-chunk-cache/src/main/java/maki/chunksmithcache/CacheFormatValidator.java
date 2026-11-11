package maki.chunksmithcache;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

/**
 * The payload gate for adopting a FULL from a different compatibility
 * generation. Run on the cache I/O executor, never on the server tick.
 *
 * No JAR SHA, generation ID or calendar-date allowlist is involved. The
 * manifest comparison has already proved the worldgen/seed/config inputs
 * unchanged; this verifies the individual serialized artifact's format,
 * state, ownership and absence of Millenaire village state.
 */
final class CacheFormatValidator {
    private static final Pattern FILE = Pattern.compile("c\\.(-?\\d+)\\.(-?\\d+)\\.nbt\\.gz");
    private static final Pattern REGION = Pattern.compile("r\\.(-?\\d+)\\.(-?\\d+)");
    private static final long MAX_COMPRESSED_BYTES = 16L << 20;
    private static final long MAX_NBT_BYTES = 64L << 20;

    private CacheFormatValidator() {}

    /** Returns null on success, otherwise an audit reason to keep the old file. */
    static String rejectReason(Path file, FingerprintManifest.Manifest generation,
                               String dimension, int dataVersion) {
        try {
            if (!Files.isRegularFile(file) || Files.size(file) < 1
                || Files.size(file) > MAX_COMPRESSED_BYTES) return "compressed-file-size";

            Path parent = file.getParent();
            if (parent == null) return "missing-region";
            Matcher region = REGION.matcher(parent.getFileName().toString());
            Matcher position = FILE.matcher(file.getFileName().toString());
            if (!region.matches() || !position.matches()) return "bad-coordinate-path";
            int x = Integer.parseInt(position.group(1));
            int z = Integer.parseInt(position.group(2));
            if (Math.floorDiv(x, 32) != Integer.parseInt(region.group(1))
                || Math.floorDiv(z, 32) != Integer.parseInt(region.group(2))) {
                return "region-coordinate-mismatch";
            }

            CompoundTag envelope = NbtIo.readCompressed(file, NbtAccounter.create(MAX_NBT_BYTES));
            if (envelope == null || !envelope.contains("schema", Tag.TAG_INT)
                || envelope.getInt("schema") != CacheEnvelope.SCHEMA) return "cache-schema";
            if (!CacheEnvelope.completeEntities(envelope)) return "entities-or-lod-incomplete";
            if (!envelope.contains("fingerprint", Tag.TAG_STRING)
                || !generation.storedFingerprints().contains(envelope.getString("fingerprint"))) {
                return "unaccepted-stored-fingerprint";
            }
            if (!envelope.contains("dimension", Tag.TAG_STRING)
                || !dimension.equals(envelope.getString("dimension"))) return "dimension-mismatch";
            if (!envelope.contains("x", Tag.TAG_INT)
                || !envelope.contains("z", Tag.TAG_INT)
                || envelope.getInt("x") != x || envelope.getInt("z") != z) {
                return "coordinates-mismatch";
            }
            if (!envelope.contains("chunk", Tag.TAG_COMPOUND)) return "missing-chunk";
            CompoundTag chunk = envelope.getCompound("chunk");
            if (NormalChunkStorage.fromTag(chunk) != NormalChunkStorage.State.FULL) {
                return "chunk-not-full";
            }
            if (!chunk.contains("DataVersion", Tag.TAG_INT)
                || chunk.getInt("DataVersion") != dataVersion) return "data-version-mismatch";
            if (chunk.contains("xPos", Tag.TAG_INT) && chunk.getInt("xPos") != x) return "chunk-xPos";
            if (chunk.contains("zPos", Tag.TAG_INT) && chunk.getInt("zPos") != z) return "chunk-zPos";
            String terrain = terrainRejectReason(chunk);
            if (terrain != null) return terrain;

            // Pre-generated FULL caches must contain terrain only; Millenaire's
            // delayed village creation happens when a player later loads it.
            // Reject any actual namespace/serialized Millenaire markers rather
            // than assuming a cache from another time is village-free.
            if (containsMillenaireData(chunk)) return "contains-millenaire-content";
            return null;
        } catch (Throwable invalid) {
            return "invalid-or-unreadable-nbt:" + invalid.getClass().getSimpleName();
        }
    }

    private static String terrainRejectReason(CompoundTag chunk) {
        if (!chunk.contains("sections", Tag.TAG_LIST)) return "missing-terrain-sections";
        ListTag sections = chunk.getList("sections", Tag.TAG_COMPOUND);
        if (sections.isEmpty()) return "missing-terrain-sections";
        for (Tag entry : sections) {
            if (!(entry instanceof CompoundTag section)
                || !section.contains("Y", Tag.TAG_BYTE)
                || !section.contains("block_states", Tag.TAG_COMPOUND)
                || !section.contains("biomes", Tag.TAG_COMPOUND)) {
                return "incomplete-terrain-section";
            }
            CompoundTag blockStates = section.getCompound("block_states");
            ListTag blockPalette = blockStates.getList("palette", Tag.TAG_COMPOUND);
            if (blockPalette.isEmpty()
                || !(blockPalette.getFirst() instanceof CompoundTag firstState)
                || !firstState.contains("Name", Tag.TAG_STRING)
                || firstState.getString("Name").isBlank()) {
                return "incomplete-block-state-palette";
            }
            CompoundTag biomes = section.getCompound("biomes");
            ListTag biomePalette = biomes.getList("palette", Tag.TAG_STRING);
            if (biomePalette.isEmpty()
                || !(biomePalette.getFirst() instanceof StringTag firstBiome)
                || firstBiome.getAsString().isBlank()) {
                return "incomplete-biome-palette";
            }
        }
        return null;
    }

    private static boolean containsMillenaireData(Tag tag) {
        if (tag instanceof StringTag value) {
            String string = value.getAsString().toLowerCase(Locale.ROOT);
            return string.contains("millenaire:") || string.startsWith("org.millenaire.");
        }
        if (tag instanceof CompoundTag compound) {
            for (String key : compound.getAllKeys()) {
                String lowered = key.toLowerCase(Locale.ROOT);
                if (lowered.startsWith("millenaire:")) return true;
                Tag child = compound.get(key);
                if (child != null && containsMillenaireData(child)) return true;
            }
        } else if (tag instanceof ListTag list) {
            for (Tag child : list) if (containsMillenaireData(child)) return true;
        }
        return false;
    }
}
