package maki.chunksmithcache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Outstanding save ownership, including completed targets whose holders still drain. */
final class BackfillScopes {
    private final Map<String, Map<Long, Session>> dimensions = new ConcurrentHashMap<>();

    void arm(String dimension, long target, Set<Long> positions) {
        Set<Long> owned = ConcurrentHashMap.newKeySet();
        owned.addAll(positions);
        Session previous = dimensions.computeIfAbsent(dimension, ignored -> new ConcurrentHashMap<>())
            .putIfAbsent(target, new Session(target, owned));
        if (previous != null) throw new IllegalStateException("Backfill target already guarded");
    }

    Session get(String dimension, long target) {
        Map<Long, Session> scopes = dimensions.get(dimension);
        return scopes == null ? null : scopes.get(target);
    }

    Collection<Session> sessions(String dimension) {
        Map<Long, Session> scopes = dimensions.get(dimension);
        return scopes == null ? java.util.List.of() : scopes.values();
    }

    void clear(String dimension, long target) {
        Map<Long, Session> scopes = dimensions.get(dimension);
        if (scopes != null) scopes.remove(target);
    }

    record Session(long target, Set<Long> suppressed) {}
}
