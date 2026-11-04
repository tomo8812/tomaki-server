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

   public static int count(int var0) {
      return Math.max(1, Math.min(1000, var0));
   }

   public static boolean valid(ShareState.Mark var0) {
      return var0 != null
         && var0.source() != null
         && var0.owner() != null
         && text(var0.ownerName(), 64)
         && text(var0.name(), 128)
         && text(var0.initials(), 8)
         && var0.dimension() != null
         && var0.dimension().length() <= 256
         && var0.dimension().matches("[a-z0-9_.-]+:[a-z0-9_./-]+")
         && Math.abs((long)var0.x()) <= 30000000L
         && Math.abs((long)var0.z()) <= 30000000L
         && Math.abs((long)var0.y()) <= 20000000L
         && var0.created() >= 0L;
   }

   private static boolean text(String var0, int var1) {
      return var0 != null && var0.length() <= var1 && var0.codePoints().noneMatch(var0x -> Character.isISOControl(var0x));
   }

   public boolean putManual(UUID var1, String var2, ShareState.Mark var3) {
      if (var3 == null) {
         return false;
      }

      ShareState.Mark var4 = new ShareState.Mark(
         var3.source(), var1, var2, false, var3.name(), var3.initials(), var3.x(), var3.y(), var3.z(), var3.dimension(), var3.color(), var3.created(), 0L
      );
      if (!valid(var4)) {
         return false;
      }

      if (!this.marks.containsKey(var4.key()) && this.marks.values().stream().filter(var1x -> !var1x.death() && var1x.owner().equals(var1)).count() >= 2048L) {
         return false;
      }

      ShareState.Mark var5 = this.marks.put(var4.key(), var4);
      return !var4.equals(var5);
   }

   public boolean deleteManual(UUID var1, UUID var2) {
      return this.marks.remove(var1 + "/manual/" + var2) != null;
   }

   public boolean addDeath(ShareState.Mark var1, boolean var2) {
      if (valid(var1) && var1.death() && (!var2 || !this.migrated.contains(var1.owner()))) {
         if (this.marks.containsKey(var1.key())) {
            return false;
         }

         ShareState.Mark var3 = new ShareState.Mark(
            var1.source(),
            var1.owner(),
            var1.ownerName(),
            true,
            "",
            "",
            var1.x(),
            var1.y(),
            var1.z(),
            var1.dimension(),
            var1.color(),
            var1.created(),
            ++this.serial
         );
         this.marks.put(var3.key(), var3);
         List var4 = this.deaths(var3.owner());

         for (int var5 = 1000; var5 < var4.size(); var5++) {
            this.marks.remove(((ShareState.Mark)var4.get(var5)).key());
         }

         return true;
      } else {
         return false;
      }
   }

   public List<ShareState.Mark> deaths(UUID var1) {
      return this.marks
         .values()
         .stream()
         .filter(var1x -> var1x.death() && var1x.owner().equals(var1))
         .sorted(
            Comparator.comparingLong(ShareState.Mark::created)
               .thenComparingLong(ShareState.Mark::order)
               .reversed()
               .thenComparing(var0 -> var0.source().toString())
         )
         .toList();
   }

   public List<ShareState.View> view(int var1) {
      int var2 = count(var1);
      ArrayList var3 = new ArrayList();

      for (ShareState.Mark var5 : this.marks.values()) {
         if (!var5.death()) {
            var3.add(new ShareState.View(var5, 0));
         }
      }

      LinkedHashSet var9 = new LinkedHashSet();

      for (ShareState.Mark var6 : this.marks.values()) {
         if (var6.death()) {
            var9.add(var6.owner());
         }
      }

      for (UUID var12 : var9) {
         List var7 = this.deaths(var12);

         for (int var8 = 0; var8 < Math.min(var2, var7.size()); var8++) {
            var3.add(new ShareState.View((ShareState.Mark)var7.get(var8), var8 + 1));
         }
      }

      return List.copyOf(var3);
   }

   public void validate() {
      if (this.worldId != null && this.marks != null && this.migrated != null) {
         for (Entry var2 : this.marks.entrySet()) {
            if (!valid((ShareState.Mark)var2.getValue()) || !((String)var2.getKey()).equals(((ShareState.Mark)var2.getValue()).key())) {
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
