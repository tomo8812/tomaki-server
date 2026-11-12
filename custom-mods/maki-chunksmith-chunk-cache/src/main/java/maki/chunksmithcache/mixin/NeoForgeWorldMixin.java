package maki.chunksmithcache.mixin;

import java.util.concurrent.CompletableFuture;
import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.mixin.ServerChunkCacheMixin;
import maki.chunksmithcache.PipelineMetrics;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.injection.Redirect;
import maki.chunksmithcache.CacheManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets="com.kishku7.chunksmith.platform.NeoForgeWorld",remap=false)
public abstract class NeoForgeWorldMixin {
   @Shadow @Final private ServerLevel world;

   @org.spongepowered.asm.mixin.injection.Inject(method="lambda$getChunkAtAsync$8", at=@At("RETURN"), require=1)
   private void maki$captureFull(ChunkAccess access, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
      if(access instanceof LevelChunk chunk)
         CacheManager.captureFromChunksmith(world,chunk,CacheManager.currentChunksmithRequestTask());
   }

   @Redirect(method="getChunkAtAsync(II)Ljava/util/concurrent/CompletableFuture;", at=@At(value="INVOKE",
      target="Lcom/kishku7/chunksmith/mixin/ServerChunkCacheMixin;invokeGetChunkFutureMainThread(IILnet/minecraft/world/level/chunk/status/ChunkStatus;Z)Ljava/util/concurrent/CompletableFuture;"), require=1)
   private CompletableFuture<ChunkResult<ChunkAccess>> maki$measureFull(
      ServerChunkCacheMixin source, int x, int z, ChunkStatus status, boolean create) {
      ChunkPos pos=new ChunkPos(x,z);
      GenerationTask sourceTask=CacheManager.currentChunksmithRequestTask();
      boolean unified=CacheManager.unifiedPregenRequestAdmitted(this.world,pos,sourceTask);
      long started = System.nanoTime();
      CompletableFuture<ChunkResult<ChunkAccess>> future = source.invokeGetChunkFutureMainThread(x,z,status,create);
      future.whenComplete((value,error) -> PipelineMetrics.time("full-future",System.nanoTime()-started));
      if(!unified)return future;
      return future.thenCompose(result -> {
         java.util.concurrent.atomic.AtomicBoolean full=new java.util.concurrent.atomic.AtomicBoolean();
         if(result!=null)result.ifSuccess(chunk -> full.set(chunk instanceof LevelChunk));
         // Mark the FULL before checking task liveness: failure completion may
         // otherwise cancel the normal-persistence guard of a loaded FULL.
         if(full.get())CacheManager.notePregenFullLoaded(this.world,pos,sourceTask);
         if(full.get())return CompletableFuture.completedFuture(result);
         PipelineMetrics.count("full-result-failed");
         return CompletableFuture.failedFuture(
            new IllegalStateException("Unified pregen FULL request returned no LevelChunk at "+pos));
      });
   }

   @Redirect(method={"lambda$getChunkAtAsync$10","lambda$getChunkAtAsync$9"},at=@At(value="INVOKE",
      target="Lnet/minecraft/server/level/ServerChunkCache;removeRegionTicket(Lnet/minecraft/server/level/TicketType;Lnet/minecraft/world/level/ChunkPos;ILjava/lang/Object;)V"),require=2)
   private static <T> void maki$holdFullTicket(net.minecraft.server.level.ServerChunkCache source,
      net.minecraft.server.level.TicketType<T> type,ChunkPos pos,int distance,T value){
      CacheManager.releaseTicketWhenSaved(source,pos,()->source.removeRegionTicket(type,pos,distance,value));
   }

}
