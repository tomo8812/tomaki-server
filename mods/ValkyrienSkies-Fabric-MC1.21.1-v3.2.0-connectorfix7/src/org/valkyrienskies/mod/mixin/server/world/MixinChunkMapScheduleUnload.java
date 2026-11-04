package org.valkyrienskies.mod.mixin.server.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.function.BooleanSupplier;
import net.minecraft.class_1923;
import net.minecraft.class_3193;
import net.minecraft.class_3898;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.VS2ChunkAllocator;

@Mixin(class_3898.class)
public abstract class MixinChunkMapScheduleUnload {
   @Shadow
   @Final
   private Long2ObjectLinkedOpenHashMap<class_3193> field_17213;
   @Shadow
   @Final
   private Long2ObjectLinkedOpenHashMap<class_3193> field_18807;
   @Shadow
   @Final
   private LongSet field_17221;

   @Inject(method = "scheduleUnload", at = @At("HEAD"), cancellable = true)
   private void vs$fastScheduleUnloadForShipyard(long chunkPos, class_3193 holder, CallbackInfo ci) {
      int cx = class_1923.method_8325(chunkPos);
      int cz = class_1923.method_8332(chunkPos);
      if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(cx, cz)) {
         this.field_18807.remove(chunkPos);
         ci.cancel();
      }
   }

   @Inject(method = "processUnloads", at = @At("HEAD"))
   private void vs$bulkEvictShipyardAtHead(BooleanSupplier shouldContinue, CallbackInfo ci) {
      if (!this.field_17221.isEmpty()) {
         LongIterator it = this.field_17221.iterator();

         while (it.hasNext()) {
            long chunkPos = it.nextLong();
            int cx = class_1923.method_8325(chunkPos);
            int cz = class_1923.method_8332(chunkPos);
            if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(cx, cz)) {
               this.field_17213.remove(chunkPos);
               this.field_18807.remove(chunkPos);
               it.remove();
            }
         }
      }
   }
}
