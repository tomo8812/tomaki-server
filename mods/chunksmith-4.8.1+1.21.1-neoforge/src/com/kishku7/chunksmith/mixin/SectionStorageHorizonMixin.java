package com.kishku7.chunksmith.mixin;

import com.kishku7.chunksmith.lod.HorizonGuard;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.storage.SectionStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SectionStorage.class)
public abstract class SectionStorageHorizonMixin {
   @Shadow
   @Final
   private LongLinkedOpenHashSet dirty;
   @Shadow
   @Final
   protected LevelHeightAccessor levelHeightAccessor;

   @Inject(method = "writeColumn(Lnet/minecraft/world/level/ChunkPos;)V", at = @At("HEAD"), cancellable = true)
   private void chunksmith$horizonVeto(ChunkPos pos, CallbackInfo ci) {
      if (HorizonGuard.armed() && HorizonGuard.dropPoi(this, pos)) {
         for (int y = this.levelHeightAccessor.getMinSection(); y < this.levelHeightAccessor.getMaxSection(); y++) {
            this.dirty.remove(SectionPos.asLong(pos.x, y, pos.z));
         }

         ci.cancel();
      }
   }
}
