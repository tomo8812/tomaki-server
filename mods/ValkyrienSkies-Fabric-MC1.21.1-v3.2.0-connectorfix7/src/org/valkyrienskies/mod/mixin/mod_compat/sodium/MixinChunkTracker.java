package org.valkyrienskies.mod.mixin.mod_compat.sodium;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.caffeinemc.mods.sodium.client.render.chunk.map.ChunkTracker;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(ChunkTracker.class)
public class MixinChunkTracker {
   @Shadow
   @Final
   private Long2IntOpenHashMap chunkStatus;
   @Shadow
   @Final
   private LongOpenHashSet chunkReady;
   @Shadow
   @Final
   private LongSet unloadQueue;
   @Shadow
   @Final
   private LongSet loadQueue;

   @Inject(method = "updateMerged", at = @At("HEAD"), cancellable = true, remap = false)
   public void beforeUpdateMerged(int x, int z, CallbackInfo ci) {
      class_1937 level = class_310.method_1551().field_1687;
      if (level != null && VSGameUtilsKt.isChunkInShipyard(level, x, z)) {
         long key = class_1923.method_8331(x, z);
         int flags = this.chunkStatus.get(key);
         if (flags == 3) {
            if (this.chunkReady.add(key) && !this.unloadQueue.remove(key)) {
               this.loadQueue.add(key);
            }
         } else if (this.chunkReady.remove(key) && !this.loadQueue.remove(key)) {
            this.unloadQueue.add(key);
         }

         ci.cancel();
      }
   }
}
