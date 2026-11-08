package maki.chunksmithcache;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

/** Serialization compatibility is evaluated per cache entry, not as a world-generation input. */
final class CacheEnvelope {
    static final int SCHEMA = 3;

    enum Reuse {
        GENERATED_COMPLETE,
        PROMOTION_ONLY,
        LEGACY_UNPROVEN,
        INVALID
    }

    private CacheEnvelope() {}

    static Reuse reuse(CompoundTag envelope) {
        if (envelope == null || !envelope.contains("chunk")) return Reuse.INVALID;
        int schema = envelope.getInt("schema");
        if (schema == 1) {
            // Schema 1 never defined an entity-completeness receipt. Even if a
            // similarly named field is present, it cannot upgrade the old contract.
            return Reuse.LEGACY_UNPROVEN;
        }
        // Schema-1 recovery has a deliberately narrower entity contract: inject no
        // cached historical entities, preserve native EntityStorage/POI authority,
        // and prove only the required generation-time entity set.
        if (schema == SCHEMA && LegacySchema1Recovery.isFunctionalEnvelope(envelope)) {
            return Reuse.PROMOTION_ONLY;
        }
        if (schema != 2 && schema != SCHEMA) return Reuse.INVALID;
        if (!envelope.getBoolean("entitiesComplete") || !validEntities(envelope)) return Reuse.INVALID;

        // Draft schema 2 proves the detached entity payload but predates the
        // record-correlated native LOD receipt. It is safe for a player promotion,
        // but cannot suppress Backfill/Follow retry debt.
        if (schema == 2) return Reuse.PROMOTION_ONLY;
        return envelope.getBoolean("lodCommitted") ? Reuse.GENERATED_COMPLETE : Reuse.PROMOTION_ONLY;
    }

    static boolean promotionSafe(CompoundTag envelope) {
        Reuse reuse = reuse(envelope);
        return reuse == Reuse.PROMOTION_ONLY || reuse == Reuse.GENERATED_COMPLETE;
    }

    static boolean generatedComplete(CompoundTag envelope) {
        return reuse(envelope) == Reuse.GENERATED_COMPLETE;
    }

    /** Kept as the entity/promotion safety predicate for existing claim classification call sites. */
    static boolean completeEntities(CompoundTag envelope) {
        return promotionSafe(envelope);
    }

    private static boolean validEntities(CompoundTag envelope) {
        Tag entities = envelope.getCompound("chunk").get("entities");
        return entities instanceof ListTag list && (list.isEmpty() || list.getElementType() == Tag.TAG_COMPOUND);
    }
}
