package maki.chunksmithcache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

public final class BackfillGuard {
   private static final Map<String, Long> ACTIVE = new ConcurrentHashMap<>();

   private BackfillGuard() {
   }

   public static void arm(ServerLevel var0, ChunkPos var1) {
      ACTIVE.put(key(var0), var1.toLong());
   }

   public static boolean active(ServerLevel var0, ChunkPos var1) {
      if (var0 != null && var1 != null) {
         Long var2 = ACTIVE.get(key(var0));
         return var2 != null && var2 == var1.toLong();
      } else {
         return false;
      }
   }

   public static boolean activePoi(Object var0, ChunkPos var1) {
      if (var0 != null && var1 != null) {
         for (ServerLevel var3 : BackfillController.levels()) {
            if (var3.getPoiManager() == var0 && active(var3, var1)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static void clear(ServerLevel var0, ChunkPos var1) {
      ACTIVE.remove(key(var0), var1.toLong());
   }

   private static String key(ServerLevel var0) {
      return var0.dimension().location().toString();
   }
}
