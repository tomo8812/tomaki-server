package maki.chunksmithcache;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

public final class PromotionLedger {
   private static final ConcurrentHashMap<String, Set<Long>> ACTIVE = new ConcurrentHashMap<>();

   private PromotionLedger() {
   }

   public static void mark(ServerLevel var0, ChunkPos var1) {
      ACTIVE.computeIfAbsent(key(var0), var0x -> ConcurrentHashMap.newKeySet()).add(var1.toLong());
   }

   public static boolean consume(ServerLevel var0, ChunkPos var1) {
      Set var2 = ACTIVE.get(key(var0));
      return var2 != null && var2.remove(var1.toLong());
   }

   public static void discard(ServerLevel var0, ChunkPos var1) {
      consume(var0, var1);
   }

   private static String key(ServerLevel var0) {
      return var0.dimension().location().toString();
   }
}
