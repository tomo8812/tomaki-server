package maki.chunksmithcache.mixin;

import java.util.concurrent.CompletableFuture;
import maki.chunksmithcache.CacheManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets="com.kishku7.chunksmith.platform.NeoForgeWorld",remap=false)
public abstract class NeoForgeWorldMixin {
   @Shadow @Final private ServerLevel world;

   @Inject(
      method="isChunkGenerated(II)Ljava/util/concurrent/CompletableFuture;",
      at=@At("HEAD"),
      cancellable=true,
      require=1
   )
   private void maki$cachedFullCountsAsGenerated(
      int x,int z,CallbackInfoReturnable<CompletableFuture<Boolean>> cir
   ) {
      if(CacheManager.chunksmithGeneratedHint(this.world,new ChunkPos(x,z))) {
         cir.setReturnValue(CompletableFuture.completedFuture(true));
      }
   }
}
