package maki.mapshare;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

public final class ShareState {
   public static final int MAX_HISTORY = 1000;
   public static final int MAX_MANUAL = 2048;
   public UUID worldId = UUID.randomUUID();
   public Map<String, ShareState.Mark> marks = new LinkedHashMap<>();
   public Set<UUID> migrated = new HashSet<>();
   public long serial;

   public static int count(int requested) {
      return Math.max(1, Math.min(1000, requested));
   }

   public static boolean valid(ShareState.Mark mark) {
      return mark != null
         && mark.source() != null
         && mark.owner() != null
         && validText(mark.ownerName(), 64)
         && validText(mark.name(), 128)
         && validText(mark.initials(), 8)
         && mark.dimension() != null
         && mark.dimension().length() <= 256
         && mark.dimension().matches("[a-z0-9_.-]+:[a-z0-9_./-]+")
         && Math.abs((long)mark.x()) <= 30000000L
         && Math.abs((long)mark.z()) <= 30000000L
         && Math.abs((long)mark.y()) <= 20000000L
         && mark.created() >= 0L;
   }

   private static boolean validText(String value, int maxLength) {
      return value != null && value.length() <= maxLength && value.codePoints().noneMatch(Character::isISOControl);
   }

   public boolean putManual(UUID owner, String ownerName, ShareState.Mark requested) {
      if (requested == null) {
         return false;
      }

      ShareState.Mark mark = new ShareState.Mark(
         requested.source(),
         owner,
         ownerName,
         false,
         requested.name(),
         requested.initials(),
         requested.x(),
         requested.y(),
         requested.z(),
         requested.dimension(),
         requested.color(),
         requested.created(),
         0L
      );
      if (!valid(mark)) {
         return false;
      }

      long manualCount = this.marks.values().stream().filter(existing -> !existing.death() && existing.owner().equals(owner)).count();
      if (!this.marks.containsKey(mark.key()) && manualCount >= 2048L) {
         return false;
      }

      ShareState.Mark previous = this.marks.put(mark.key(), mark);
      return !mark.equals(previous);
   }

   public boolean deleteManual(UUID owner, UUID source) {
      return this.marks.remove(owner + "/manual/" + source) != null;
   }

   public boolean addDeath(ShareState.Mark requested, boolean migration) {
      if (valid(requested) && requested.death() && (!migration || !this.migrated.contains(requested.owner()))) {
         if (this.marks.containsKey(requested.key())) {
            return false;
         }

         ShareState.Mark stored = new ShareState.Mark(
            requested.source(),
            requested.owner(),
            requested.ownerName(),
            true,
            "",
            "",
            requested.x(),
            requested.y(),
            requested.z(),
            requested.dimension(),
            requested.color(),
            requested.created(),
            ++this.serial
         );
         this.marks.put(stored.key(), stored);
         List<ShareState.Mark> deaths = this.deaths(stored.owner());

         for (int index = 1000; index < deaths.size(); index++) {
            this.marks.remove(deaths.get(index).key());
         }

         return true;
      } else {
         return false;
      }
   }

   public List<ShareState.Mark> deaths(UUID owner) {
      return this.marks
         .values()
         .stream()
         .filter(mark -> mark.death() && mark.owner().equals(owner))
         .sorted(
            Comparator.comparingLong(ShareState.Mark::created)
               .thenComparingLong(ShareState.Mark::order)
               .reversed()
               .thenComparing(mark -> mark.source().toString())
         )
         .toList();
   }

   public List<ShareState.View> view(int requestedCount) {
      int count = count(requestedCount);
      List<ShareState.View> result = new ArrayList<>();

      for (ShareState.Mark mark : this.marks.values()) {
         if (!mark.death()) {
            result.add(new ShareState.View(mark, 0));
         }
      }

      Set<UUID> deathOwners = new LinkedHashSet<>();

      for (ShareState.Mark mark : this.marks.values()) {
         if (mark.death()) {
            deathOwners.add(mark.owner());
         }
      }

      for (UUID owner : deathOwners) {
         List<ShareState.Mark> deaths = this.deaths(owner);

         for (int index = 0; index < Math.min(count, deaths.size()); index++) {
            result.add(new ShareState.View(deaths.get(index), index + 1));
         }
      }

      return List.copyOf(result);
   }

   public void validate() {
      if (this.worldId != null && this.marks != null && this.migrated != null) {
         for (Entry<String, ShareState.Mark> entry : this.marks.entrySet()) {
            if (!valid(entry.getValue()) || !entry.getKey().equals(entry.getValue().key())) {
               throw new IllegalArgumentException("Invalid stored marker");
            }
         }
      } else {
         throw new IllegalArgumentException("Missing sharing state");
      }
   }

   public record Mark(
      UUID source,
      UUID owner,
      String ownerName,
      boolean death,
      String name,
      String initials,
      int x,
      int y,
      int z,
      String dimension,
      int color,
      long created,
      long order
   ) {
      public String key() {
         return this.owner + "/" + (this.death ? "death/" : "manual/") + this.source;
      }

      public UUID displayId() {
         return UUID.nameUUIDFromBytes(this.key().getBytes(StandardCharsets.UTF_8));
      }
   }

   public record View(ShareState.Mark mark, int age) {
      public String label() {
         return this.mark.death()
            ? this.mark.ownerName() + " の死亡地点（" + (this.age == 1 ? "最新" : this.age - 1 + "つ前") + "）"
            : this.mark.name() + " [" + this.mark.ownerName() + "]";
      }
   }
}
