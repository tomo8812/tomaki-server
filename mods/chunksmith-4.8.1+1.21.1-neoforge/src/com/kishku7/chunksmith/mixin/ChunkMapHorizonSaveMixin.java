package com.kishku7.chunksmith.mixin;

import com.kishku7.chunksmith.lod.ExploredLodGuard;
import com.kishku7.chunksmith.lod.HorizonGuard;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkMap.class)
public abstract class ChunkMapHorizonSaveMixin {
   @Inject(method = "save(Lnet/minecraft/world/level/chunk/ChunkAccess;)Z", at = @At("HEAD"), cancellable = true)
   private void chunksmith$horizonVeto(ChunkAccess chunk, CallbackInfoReturnable<Boolean> cir) {
      ExploredLodGuard.onSave(((ChunkMapLevelAccessor)this).chunksmith$horizonLevel(), chunk);
      if (HorizonGuard.armed()) {
         if (HorizonGuard.discardChunk(((ChunkMapLevelAccessor)this).chunksmith$horizonLevel(), chunk.getPos())) {
            chunk.setUnsaved(false);
            cir.setReturnValue(false);
         }
      }
   }
}
