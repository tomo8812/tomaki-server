package maki.chunksmithcache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

final class BackfillScopes {
   private final Map<String, Map<Long, BackfillScopes.Session>> dimensions = new ConcurrentHashMap<>();

   void arm(String dimension, long target, Set<Long> positions) {
      Set<Long> owned = ConcurrentHashMap.newKeySet();
      owned.addAll(positions);
      BackfillScopes.Session previous = this.dimensions
         .computeIfAbsent(dimension, ignored -> new ConcurrentHashMap<>())
         .putIfAbsent(target, new BackfillScopes.Session(target, owned));
      if (previous != null) {
         throw new IllegalStateException("Backfill target already guarded");
      }
   }

   BackfillScopes.Session get(String dimension, long target) {
      Map<Long, BackfillScopes.Session> scopes = this.dimensions.get(dimension);
      return scopes == null ? null : scopes.get(target);
   }

   Collection<BackfillScopes.Session> sessions(String dimension) {
      Map<Long, BackfillScopes.Session> scopes = this.dimensions.get(dimension);
      return scopes == null ? List.of() : scopes.values();
   }

   void clear(String dimension, long target) {
      Map<Long, BackfillScopes.Session> scopes = this.dimensions.get(dimension);
      if (scopes != null) {
         scopes.remove(target);
      }
   }

   record Session(long target, Set<Long> suppressed) {
   }
}
