package maki.millenairelod.mixin;

import com.kishku7.chunksmith.lod.ExploredLodGuard;
import com.kishku7.chunksmith.lod.LodSupport;
import maki.millenairelod.Compat;
import maki.millenairelod.LodRefreshQueue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ExploredLodGuard.class, remap = false)
public abstract class ExploredLodMixin {
   @Inject(method = "onSave", at = @At("HEAD"))
   private static void compat$refreshSave(ServerLevel level, ChunkAccess chunk, CallbackInfo ci) {
      Compat.refresh(level, chunk);
   }

   @Inject(method = "onUnload", at = @At("HEAD"))
   private static void compat$refreshUnload(ServerLevel level, LevelChunk chunk, CallbackInfo ci) {
      Compat.refresh(level, chunk);
   }

   @Redirect(
      method = "take",
      at = @At(
         value = "INVOKE",
         target = "Lcom/kishku7/chunksmith/lod/LodSupport;offer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/LevelChunk;)V"
      )
   )
   private static void compat$deferProtectedInitialLod(ServerLevel level, LevelChunk chunk) {
      if (Compat.protectedChunk(level, chunk.getPos())) {
         if (!LodRefreshQueue.captureInitial(level, chunk)) {
            LodSupport.offer(level, chunk);
         }
      } else {
         LodSupport.offer(level, chunk);
      }
   }
}
