package maki.chunksmithcache.mixin;

import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import maki.chunksmithcache.BackfillGuard;
import maki.chunksmithcache.CacheManager;
import maki.chunksmithcache.PregenPoiDirtyBridge;
import maki.chunksmithcache.PromotionLedger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.storage.SectionStorage;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SectionStorage.class)
public abstract class SectionStorageMixin implements PregenPoiDirtyBridge {
   @Shadow
   @Final
   private LongLinkedOpenHashSet dirty;
   @Shadow
   @Final
   protected LevelHeightAccessor levelHeightAccessor;

   @Override
   public long[] maki$clearPregenPoiDirty(ChunkPos pos) {
      long[] removed = new long[this.levelHeightAccessor.getSectionsCount()];
      int count = 0;
      for (int y = this.levelHeightAccessor.getMinSection(); y < this.levelHeightAccessor.getMaxSection(); y++) {
         long section = SectionPos.asLong(pos.x, y, pos.z);
         if (this.dirty.remove(section)) removed[count++] = section;
      }
      return count == removed.length ? removed : Arrays.copyOf(removed, count);
   }

   @Override
   public void maki$restorePregenPoiDirty(long[] sections) {
      for (long section : sections) this.dirty.add(section);
   }


   // Observe the native POI region-store Future after the pregen veto is
   // lifted, so promotion retirement waits for the actual disk write ACK.
   @Redirect(method = "writeColumn(Lnet/minecraft/world/level/ChunkPos;)V",
      at = @At(value = "INVOKE", target =
         "Lnet/minecraft/world/level/chunk/storage/SimpleRegionStorage;write(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/nbt/CompoundTag;)Ljava/util/concurrent/CompletableFuture;"),
      require = 1)
   private CompletableFuture<Void> maki$observePoiWrite(SimpleRegionStorage storage, ChunkPos pos, CompoundTag tag) {
      CompletableFuture<Void> written = storage.write(pos, tag);
      PromotionLedger.observePoiWrite(this, pos, written);
      return written;
   }

   @Inject(method = "writeColumn(Lnet/minecraft/world/level/ChunkPos;)V", at = @At("HEAD"), cancellable = true)
   private void maki$backfillPoiVeto(ChunkPos pos, CallbackInfo ci) {
      if (BackfillGuard.activePoi(this, pos)) {
         this.maki$clearPregenPoiDirty(pos);
         ci.cancel();
         return;
      }
      if (CacheManager.suppressPregenPoiPersistence(this, pos)) ci.cancel();
   }
}
