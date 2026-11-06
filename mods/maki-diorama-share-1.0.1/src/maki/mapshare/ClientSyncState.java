package maki.mapshare;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

final class ClientSyncState {
   private final Map<UUID, ShareState.Mark> sent = new LinkedHashMap<>();
   private final Map<UUID, ShareState.Mark> acknowledged = new LinkedHashMap<>();
   private long acknowledgedRevision;

   void clear() {
      this.sent.clear();
      this.acknowledged.clear();
      this.acknowledgedRevision = 0L;
   }

   Map<UUID, ShareState.Mark> sent() {
      return new LinkedHashMap<>(this.sent);
   }

   Map<UUID, ShareState.Mark> acknowledged() {
      return new LinkedHashMap<>(this.acknowledged);
   }

   long acknowledgedRevision() {
      return this.acknowledgedRevision;
   }

   void markSent(Map<UUID, ShareState.Mark> current) {
      this.sent.clear();
      this.sent.putAll(current);
   }

   void acceptSnapshot(Map<UUID, ShareState.Mark> serverOwn, long ackRevision) {
      if (ackRevision >= this.acknowledgedRevision) {
         this.acknowledged.clear();
         this.acknowledged.putAll(serverOwn);
         this.acknowledgedRevision = ackRevision;
      }
   }

   boolean isAcknowledgedThrough(long revision) {
      return this.acknowledgedRevision >= revision;
   }

   Set<UUID> unsentChanges(Map<UUID, ShareState.Mark> current) {
      Set<UUID> ids = new HashSet<>(this.sent.keySet());
      ids.addAll(current.keySet());
      Set<UUID> changed = new HashSet<>();

      for (UUID id : ids) {
         if (!Objects.equals(this.sent.get(id), current.get(id))) {
            changed.add(id);
         }
      }

      return changed;
   }

   void alignSent(Map<UUID, ShareState.Mark> reconciledLocal, Set<UUID> unsentLocalIds) {
      Set<UUID> ids = new HashSet<>(this.sent.keySet());
      ids.addAll(reconciledLocal.keySet());

      for (UUID id : ids) {
         if (!unsentLocalIds.contains(id)) {
            ShareState.Mark mark = reconciledLocal.get(id);
            if (mark == null) {
               this.sent.remove(id);
            } else {
               this.sent.put(id, mark);
            }
         }
      }
   }
}
