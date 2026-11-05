package maki.millenairelod;

import com.kishku7.chunksmith.lod.HorizonGuard;
import com.kishku7.chunksmith.lod.LodHorizon;
import com.kishku7.chunksmith.lod.LodSupport;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.millenaire.culture.ModCultures;
import org.millenaire.village.Village;
import org.millenaire.village.VillageSavedData;
import org.millenaire.world.VillageSpawnQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Compat {
   private static final Logger LOG = LoggerFactory.getLogger("MakiMillenaireLOD");
   private static final Map<ServerLevel, Compat.Protection> PROTECTION = new WeakHashMap<>();
   private static final int REPLAY_BUDGET = 64;
   private static boolean refreshFailureLogged;

   private Compat() {
   }

   public static void onChunk(VillageSpawnQueue var0, ChunkPos var1, boolean var2, ServerLevel var3) {
      var0.markResolved(var1);
      DeferredData var4 = DeferredData.get(var3);
      long var5 = var1.toLong();
      if (var2 || var4.pending.contains(var5)) {
         if (!var4.queued.contains(var5)) {
            BlockPos var7 = var1.getMiddleBlockPosition(var3.getMaxBuildHeight() - 1);
            Holder var8 = var3.getBiome(var7);
            if (!VillageSpawnQueue.couldHostAnyVillage(ModCultures.getAllVillageTypes().values(), var8::is)) {
               var4.consume(var5);
            } else if (HorizonGuard.drop(var3, var1)) {
               var4.remember(var5, false);
            } else {
               enqueue(var3, var0, var1);
            }
         }
      }
   }

   private static void enqueue(ServerLevel var0, VillageSpawnQueue var1, ChunkPos var2) {
      DeferredData var3 = DeferredData.get(var0);
      long var4 = var2.toLong();
      if (!var3.queued.contains(var4)) {
         var3.remember(var4, true);
         var1.enqueue(var2.getMiddleBlockPosition(64));
      }
   }

   public static void replay(ServerLevel var0, VillageSpawnQueue var1) {
      ReplayController.replay(var0, var1);
   }

   public static boolean beginCandidate(ServerLevel var0, BlockPos var1) {
      return ReplayController.beginCandidate(var0, var1);
   }

   public static void waiting(ServerLevel var0, BlockPos var1, boolean var2) {
      DeferredData.get(var0).remember(new ChunkPos(var1).toLong(), var2);
   }

   public static boolean protectedChunk(ServerLevel var0, ChunkPos var1) {
      ReservationLifecycle.expire(var0);
      if (var0 != null && var1 != null) {
         Compat.Protection var2 = PROTECTION.computeIfAbsent(var0, var0x -> new Compat.Protection());
         if (var2.tick != var0.getGameTime()) {
            var2.villages.clear();

            for (Village var4 : VillageSavedData.get(var0).getVillageManager().getAllVillages()) {
               for (ChunkPos var6 : var4.computeVillageChunks()) {
                  var2.villages.add(var6.toLong());
               }
            }

            var2.tick = var0.getGameTime();
         }

         return var2.villages.contains(var1.toLong()) || var2.reservations.contains(var1.toLong());
      } else {
         return false;
      }
   }

   public static void reserve(ServerLevel var0, BlockPos var1, int var2) {
      Compat.Protection var3 = PROTECTION.computeIfAbsent(var0, var0x -> new Compat.Protection());
      var3.tick = Long.MIN_VALUE;
      if (!LodHorizon.isArmed(LodSupport.dimensionId(var0))) {
         ReservationLifecycle.begin(var0, var1, var2);
      } else {
         ChunkPos var4 = new ChunkPos(var1);
         int var5 = Math.max(0, (var2 + 15) / 16) + 4;
         ReservationLifecycle.ignoreKeepAround(LodSupport.dimensionId(var0), var4.x, var4.z, var5);

         for (int var6 = var4.x - var5; var6 <= var4.x + var5; var6++) {
            for (int var7 = var4.z - var5; var7 <= var4.z + var5; var7++) {
               var3.reservations.add(ChunkPos.asLong(var6, var7));
            }
         }

         ReservationLifecycle.begin(var0, var1, var2);
      }
   }

   public static void refresh(ServerLevel var0, ChunkAccess var1) {
      if (var1 instanceof LevelChunk var2 && var2.isUnsaved()) {
         if (protectedChunk(var0, var2.getPos()) && LodSupport.lodEnabled(var0.getServer())) {
            try {
               LodSupport.offer(var0, var2);
            } catch (RuntimeException | LinkageError var4) {
               if (!refreshFailureLogged) {
                  refreshFailureLogged = true;
                  LOG.error("Could not refresh village LOD; real chunk saving will continue", var4);
               }
            }
         }
      }
   }

   // $VF: synthetic method
   public static Object maki$protection(Object var0) {
      return PROTECTION.get(var0);
   }

   // $VF: synthetic method
   public static Object maki$reservations(Object var0) {
      return ((Compat.Protection)var0).reservations;
   }

   // $VF: synthetic method
   public static boolean maki$containsReservation(Object var0, long var1) {
      return ((LongOpenHashSet)var0).contains(var1);
   }

   // $VF: synthetic method
   public static void maki$removeReservation(Object var0, long var1) {
      ((LongOpenHashSet)var0).remove(var1);
   }

   // $VF: synthetic method
   public static void maki$invalidateVillageCache(Object var0) {
      ((Compat.Protection)var0).tick = Long.MIN_VALUE;
   }

   // $VF: synthetic method
   public static long maki$gameTime(Object var0) {
      return ((ServerLevel)var0).getGameTime();
   }

   // $VF: synthetic method
   public static int maki$blockX(Object var0) {
      return ((BlockPos)var0).getX();
   }

   // $VF: synthetic method
   public static int maki$blockZ(Object var0) {
      return ((BlockPos)var0).getZ();
   }

   private static final class Protection {
      long tick = Long.MIN_VALUE;
      final LongOpenHashSet villages = new LongOpenHashSet();
      final LongOpenHashSet reservations = new LongOpenHashSet();
   }
}
