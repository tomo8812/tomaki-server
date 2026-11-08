package maki.chunksmithcache;

import java.util.Locale;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

/**
 * Functional recovery policy for schema-1 FULL payloads.
 *
 * The historical writer did not persist detached persistent entities. Recovery
 * therefore does not claim historical entity completeness. It may reuse terrain
 * only for the audited overworld recovery domain, when the saved structure
 * metadata shows that no structure-generated entity set needs reconstruction and
 * every known code-driven generation input is within the audited server stack.
 * Other dimensions fail closed because vanilla Features may place required
 * persistent entities without structure starts/references. Natural-spawn history
 * is intentionally outside this contract.
 */
final class LegacySchema1Recovery {
    static final String POLICY = "schema1-functional-entities-v2";
    static final String POLICY_TAG = "legacyRecoveryPolicy";
    static final String SOURCE_SHA_TAG = "legacySourceSha256";
    static final String REQUIRED_ENTITIES_TAG = "requiredGeneratedEntitiesComplete";

    private static final String RECOVERABLE_DIMENSION = "minecraft:overworld";

    private static final Set<String> AUDITED_CODE_DRIVEN_MODS = Set.of(
        "chunksmith", "zfastnoise", "epicterrain", "millenaire",
        "worldgenfeaturefix", "maki_millenaire_lod", "terrain_slabs", "disabler"
    );
    private static final String MILLENAIRE_CONTRACT_KEY = "compat:mod:maki_millenaire_lod";
    private static final String MILLENAIRE_CONTRACT = "millenaire-disposable-generation-v1";

    enum Outcome { REUSE_FULL, FULL_REGENERATE, HOLD }

    record Decision(Outcome outcome, String reason) {}

    private LegacySchema1Recovery() {}

    static Decision decide(
        CompoundTag envelope,
        FingerprintManifest.Manifest sourceManifest,
        FingerprintManifest.Manifest currentManifest
    ) {
        if (envelope == null || envelope.getInt("schema") != 1) {
            return new Decision(Outcome.HOLD, "not-schema1");
        }
        if (sourceManifest == null || currentManifest == null) {
            return new Decision(Outcome.HOLD, "generation-manifest-unavailable");
        }
        String dimension = envelope.getString("dimension");
        if (!RECOVERABLE_DIMENSION.equals(dimension)) {
            return new Decision(
                Outcome.FULL_REGENERATE,
                "dimension-not-audited-for-schema1-functional-recovery:" + dimension
            );
        }

        String inputRisk = unauditedDirectEntityInput(currentManifest);
        if (inputRisk != null) {
            return new Decision(Outcome.FULL_REGENERATE, inputRisk);
        }
        if (hasMillenaireContract(currentManifest) && !hasMillenaireContract(sourceManifest)) {
            return new Decision(Outcome.FULL_REGENERATE, "millenaire-generation-contract-unproven");
        }

        CompoundTag chunk = envelope.getCompound("chunk");
        Tag structuresTag = chunk.get("structures");
        if (!(structuresTag instanceof CompoundTag structures)) {
            return new Decision(Outcome.FULL_REGENERATE, "structure-evidence-missing");
        }
        Tag startsTag = structures.get("starts");
        Tag referencesTag = structures.get("References");
        if (referencesTag == null) referencesTag = structures.get("references");
        if (!(startsTag instanceof CompoundTag starts) || !(referencesTag instanceof CompoundTag references)) {
            return new Decision(Outcome.FULL_REGENERATE, "structure-evidence-malformed");
        }
        if (hasActiveStarts(starts)) {
            return new Decision(Outcome.FULL_REGENERATE, "structure-start-present");
        }
        if (!references.getAllKeys().isEmpty()) {
            return new Decision(Outcome.FULL_REGENERATE, "structure-reference-present");
        }
        return new Decision(Outcome.REUSE_FULL, "structure-free-audited-generation-stack");
    }

    static CompoundTag upgradedEnvelope(CompoundTag source, String sourceSha256) {
        CompoundTag upgraded = source.copy();
        CompoundTag chunk = upgraded.getCompound("chunk");
        // Empty embedded legacy entities inject nothing. Native EntityStorage and
        // POI remain independent and authoritative when the FULL is promoted.
        chunk.put("entities", new ListTag());
        upgraded.putInt("schema", CacheEnvelope.SCHEMA);
        upgraded.remove("entitiesComplete");
        upgraded.putBoolean("lodCommitted", false);
        upgraded.putString(POLICY_TAG, POLICY);
        upgraded.putString(SOURCE_SHA_TAG, sourceSha256);
        upgraded.putBoolean(REQUIRED_ENTITIES_TAG, true);
        return upgraded;
    }

    static boolean isFunctionalEnvelope(CompoundTag envelope) {
        if (envelope == null
            || envelope.getInt("schema") != CacheEnvelope.SCHEMA
            || !POLICY.equals(envelope.getString(POLICY_TAG))
            || !envelope.getBoolean(REQUIRED_ENTITIES_TAG)
            || envelope.getBoolean("entitiesComplete")
            || envelope.getBoolean("lodCommitted")) {
            return false;
        }
        String sourceSha = envelope.getString(SOURCE_SHA_TAG);
        if (!sourceSha.matches("[0-9a-f]{64}")) return false;
        Tag entities = envelope.getCompound("chunk").get("entities");
        return entities instanceof ListTag list && list.isEmpty();
    }

    static boolean isUpgradeOf(CompoundTag upgraded, String sourceSha256) {
        return isFunctionalEnvelope(upgraded)
            && sourceSha256.equals(upgraded.getString(SOURCE_SHA_TAG));
    }

    private static boolean hasActiveStarts(CompoundTag starts) {
        for (String key : starts.getAllKeys()) {
            Tag value = starts.get(key);
            if (!(value instanceof CompoundTag start)) return true;
            if (!"INVALID".equals(start.getString("id"))) return true;
        }
        return false;
    }

    private static String unauditedDirectEntityInput(FingerprintManifest.Manifest manifest) {
        for (FingerprintManifest.Entry entry : manifest.entries()) {
            if (entry.impact() != FingerprintManifest.Impact.RELEVANT
                || !entry.key().startsWith("mods/")
                || !entry.key().endsWith(".jar")) {
                continue;
            }
            String reason = entry.reason();
            if (!reason.startsWith("generation-sensitive MOD id=")) {
                return "unaudited-generation-artifact:" + entry.key();
            }
            String ids = reason.substring("generation-sensitive MOD id=".length());
            for (String id : ids.split(",")) {
                String normalized = id.trim().toLowerCase(Locale.ROOT);
                if (!AUDITED_CODE_DRIVEN_MODS.contains(normalized)) {
                    return "unaudited-direct-entity-mod:" + normalized;
                }
            }
        }
        return null;
    }

    private static boolean hasMillenaireContract(FingerprintManifest.Manifest manifest) {
        String expected = CompatibilityFingerprint.sha256(MILLENAIRE_CONTRACT);
        for (FingerprintManifest.Entry raw : manifest.entries()) {
            FingerprintManifest.Entry entry = CompatibilityInput.normalizeHistoricalEntry(raw);
            if (entry != null
                && MILLENAIRE_CONTRACT_KEY.equals(entry.key())
                && expected.equals(entry.sha256())) {
                return true;
            }
        }
        return false;
    }
}
