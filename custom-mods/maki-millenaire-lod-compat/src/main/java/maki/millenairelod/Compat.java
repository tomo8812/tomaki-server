package maki.millenairelod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import org.millenaire.culture.ModCultures;
import org.millenaire.world.VillageSpawnQueue;
import org.millenaire.world.sitescore.SiteSurvey;

/** Retains village candidates during pregen and replays them on player loads. */
public final class Compat {
   private Compat() {}

   public static void onChunk(VillageSpawnQueue queue, ChunkPos pos, boolean natural, ServerLevel level) {
      // SiteSurvey.processQueue clears the native main queue and returns immediately.
      // Survey owns its own WAITING queue; do not claim an item that can never be polled.
      if (SiteSurvey.ENABLED) return;
      // Millenaire 9.1.0 resolves the chunk and records SiteSurvey/SiteHunter
      // before this hook. Keep the generation candidate until a real player
      // has a loaded FULL and its normal persistence is safe.
      DeferredData data = DeferredData.get(level);
      long key = pos.toLong();
      if ((natural || data.pending.contains(key)) && !data.queued.contains(key)) {
         // Typed lone retries are restored only by ReplayController into the
         // native lone queue; this load callback must never turn them into villages.
         if(data.lone.containsKey(key))return;
         BlockPos sample = pos.getMiddleBlockPosition(level.getMaxBuildHeight() - 1);
         Holder<Biome> biome = level.getBiome(sample);
         if (!VillageSpawnQueue.couldHostAnyVillage(ModCultures.getAllVillageTypes().values(), biome::is)) {
            data.consume(key);
         } else if (!VillagePlacementReadiness.candidateReady(level, pos)) {
            data.remember(key, false);
         } else {
            enqueue(level, queue, pos);
         }
      }
   }

   private static void enqueue(ServerLevel level, VillageSpawnQueue queue, ChunkPos pos) {
      DeferredData data = DeferredData.get(level);
      long key = pos.toLong();
      if (!data.queued.contains(key)) {
         var access=(maki.millenairelod.mixin.VillageQueueAccessor)queue;
         if(ReplayBudget.mainCandidates(access.maki$candidates().size(),
             ((maki.millenairelod.mixin.SiteHunterWorkAccessor)(Object)queue.hunter()).maki$work().size())==0){
            data.remember(key,false);return;
         }
         data.remember(key, true);
         queue.enqueue(pos.getMiddleBlockPosition(64));
      }
   }

   public static void replay(ServerLevel level, VillageSpawnQueue queue) {
      // The native SiteSurvey-only branch never processes replayed main candidates.
      if (!SiteSurvey.ENABLED) ReplayController.replay(level, queue);
   }

   public static boolean beginCandidate(ServerLevel level, BlockPos pos) {
      return ReplayController.beginCandidate(level, pos);
   }

   public static void enqueueLone(ServerLevel level,java.util.Deque<Object> queue,Object candidate){
      var lone=(maki.millenairelod.mixin.LoneCandidateAccessor)candidate;
      DeferredData data=DeferredData.get(level);
      data.rememberLone(lone.compat$pos(),lone.compat$distance(),lone.compat$enqueuedTick(),lone.compat$retryAfterTick());
      if(ReplayBudget.lone(queue.size())>0)queue.addLast(candidate);
      else data.release(new ChunkPos(lone.compat$pos()).toLong());
   }

   public static void expiredLone(ServerLevel level, BlockPos pos) {
      // Millenaire drops the LBCandidate when its native TTL has elapsed.
      // Drop our replay record too, or an expired lone building returns as a village.
      DeferredData.get(level).consume(new ChunkPos(pos).toLong());
      ReplayController.resolved(level,pos);
   }

}
