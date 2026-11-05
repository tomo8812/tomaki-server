package maki.chunksmithcache.mixin;

import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import maki.chunksmithcache.BackfillGuard;
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
public abstract class SectionStorageMixin {
   @Shadow
   @Final
   private LongLinkedOpenHashSet dirty;
   @Shadow
   @Final
   protected LevelHeightAccessor levelHeightAccessor;

   @Inject(method = "writeColumn(Lnet/minecraft/world/level/ChunkPos;)V", at = @At("HEAD"), cancellable = true)
   private void maki$backfillPoiVeto(ChunkPos var1, CallbackInfo var2) {
      if (BackfillGuard.activePoi(this, var1)) {
         for (int var3 = this.levelHeightAccessor.getMinSection(); var3 < this.levelHeightAccessor.getMaxSection(); var3++) {
            this.dirty.remove(SectionPos.asLong(var1.x, var3, var1.z));
         }

         var2.cancel();
      }
   }
}
