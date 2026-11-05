package maki.millenairelod;

import com.kishku7.chunksmith.lod.HorizonGuard;
import com.kishku7.chunksmith.lod.LodHorizon;
import com.kishku7.chunksmith.lod.LodSupport;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;
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
   private static final Map<Village, Long> VILLAGE_VERSIONS = new WeakHashMap<>();
   private static final Object PROTECTION_LOCK = new Object();
   private static final int REPLAY_BUDGET = 64;
   private static volatile long protectionVersion;
   private static volatile long fullRangeVersion;
   private static boolean refreshFailureLogged;

   private Compat() {
   }

   public static void onChunk(VillageSpawnQueue var0, ChunkPos var1, boolean var2, ServerLevel var3) {
      var0.markResolved(var1);
      DeferredData var4 = DeferredData.get(var3);
      long var5 = var1.toLong();
      if ((var2 || var4.pending.contains(var5)) && !var4.queued.contains(var5)) {
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
      if (var0 != null && var1 != null) {
         Compat.Protection var2 = PROTECTION.computeIfAbsent(var0, var0x -> new Compat.Protection());
         if (var2.syncedVersion != protectionVersion) {
            syncProtection(var0, var2);
         }

         long var3 = var1.toLong();
         return var2.villages.contains(var3) || var2.reservations.contains(var3);
      } else {
         return false;
      }
   }

   private static void syncProtection(ServerLevel var0, Compat.Protection var1) {
      synchronized (PROTECTION_LOCK) {
         long var3 = protectionVersion;
         if (var1.syncedVersion != var3) {
            long var5 = fullRangeVersion;
            boolean var7 = var1.syncedFullRangeVersion != var5;
            Collection var8 = VillageSavedData.get(var0).getVillageManager().getAllVillages();
            IdentityHashMap var9 = new IdentityHashMap();

            for (Village var11 : var8) {
               var9.put(var11, Boolean.TRUE);
               long var12 = VILLAGE_VERSIONS.getOrDefault(var11, 0L);
               Compat.CachedVillage var14 = var1.byVillage.get(var11);
               if (var14 == null || var7 || var14.version != var12) {
                  replaceVillage(var1, var11, var12, var14);
               }
            }

            Iterator var17 = var1.byVillage.entrySet().iterator();

            while (var17.hasNext()) {
               Entry var18 = (Entry)var17.next();
               if (!var9.containsKey(var18.getKey())) {
                  removeChunks(var1, ((Compat.CachedVillage)var18.getValue()).chunks);
                  var17.remove();
               }
            }

            var1.syncedFullRangeVersion = var5;
            var1.syncedVersion = var3;
         }
      }
   }

   private static void replaceVillage(Compat.Protection var0, Village var1, long var2, Compat.CachedVillage var4) {
      if (var4 != null) {
         removeChunks(var0, var4.chunks);
      }

      Set var5 = var1.computeVillageChunks();
      long[] var6 = new long[var5.size()];
      int var7 = 0;

      for (ChunkPos var9 : var5) {
         long var10 = var9.toLong();
         var6[var7++] = var10;
         int var12 = var0.references.getOrDefault(var10, 0);
         var0.references.put(var10, var12 + 1);
         if (var12 == 0) {
            var0.villages.add(var10);
         }
      }

      var0.byVillage.put(var1, new Compat.CachedVillage(var2, var6));
   }

   private static void removeChunks(Compat.Protection var0, long[] var1) {
      for (long var5 : var1) {
         int var7 = var0.references.getOrDefault(var5, 0);
         if (var7 <= 1) {
            var0.references.remove(var5);
            var0.villages.remove(var5);
         } else {
            var0.references.put(var5, var7 - 1);
         }
      }
   }

   public static void invalidateVillage(Village var0) {
      if (var0 != null) {
         synchronized (PROTECTION_LOCK) {
            long var2 = protectionVersion + 1L;
            protectionVersion = var2;
            VILLAGE_VERSIONS.put(var0, var2);
         }
      }
   }

   public static void invalidateVillageMembership() {
      synchronized (PROTECTION_LOCK) {
         protectionVersion++;
      }
   }

   public static void invalidateAllVillageRanges() {
      synchronized (PROTECTION_LOCK) {
         long var1 = protectionVersion + 1L;
         protectionVersion = var1;
         fullRangeVersion = var1;
      }
   }

   public static void reserve(ServerLevel var0, BlockPos var1, int var2) {
      Compat.Protection var3 = PROTECTION.computeIfAbsent(var0, var0x -> new Compat.Protection());
      if (LodHorizon.isArmed(LodSupport.dimensionId(var0))) {
         ChunkPos var4 = new ChunkPos(var1);
         int var5 = Math.max(0, (var2 + 15) / 16) + 4;
         LodHorizon.keepAround(LodSupport.dimensionId(var0), var4.x, var4.z, var5);

         for (int var6 = var4.x - var5; var6 <= var4.x + var5; var6++) {
            for (int var7 = var4.z - var5; var7 <= var4.z + var5; var7++) {
               var3.reservations.add(ChunkPos.asLong(var6, var7));
            }
         }
      }
   }

   public static void refresh(ServerLevel var0, ChunkAccess var1) {
      if (var1 instanceof LevelChunk var2 && var2.isUnsaved() && protectedChunk(var0, var2.getPos()) && LodSupport.lodEnabled(var0.getServer())) {
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

   private static final class CachedVillage {
      final long version;
      final long[] chunks;

      CachedVillage(long var1, long[] var3) {
         this.version = var1;
         this.chunks = var3;
      }
   }

   private static final class Protection {
      long syncedVersion = Long.MIN_VALUE;
      long syncedFullRangeVersion = Long.MIN_VALUE;
      final IdentityHashMap<Village, Compat.CachedVillage> byVillage = new IdentityHashMap<>();
      final HashMap<Long, Integer> references = new HashMap<>();
      final LongOpenHashSet villages = new LongOpenHashSet();
      final LongOpenHashSet reservations = new LongOpenHashSet();
   }
}
