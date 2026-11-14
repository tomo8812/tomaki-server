package com.kishku7.chunksmith.mixin;

import com.kishku7.chunksmith.lod.HorizonGuard;
import java.util.concurrent.CompletableFuture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkStorage.class)
public abstract class StorageWriteHorizonMixin {
   @Inject(
      method = "write(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/nbt/CompoundTag;)Ljava/util/concurrent/CompletableFuture;",
      at = @At("HEAD"),
      cancellable = true
   )
   private void chunksmith$horizonVeto(ChunkPos pos, CompoundTag tag, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
      if (this.chunksmith$drop(pos)) {
         cir.setReturnValue(CompletableFuture.completedFuture(null));
      }
   }

   private boolean chunksmith$drop(ChunkPos pos) {
      return HorizonGuard.armed() && this instanceof ChunkMap ? HorizonGuard.drop(((ChunkMapLevelAccessor)this).chunksmith$horizonLevel(), pos) : false;
   }
}
