package org.valkyrienskies.mod.mixin.server.world;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntFunction;
import net.minecraft.class_1923;
import net.minecraft.class_2791;
import net.minecraft.class_2806;
import net.minecraft.class_3193;
import net.minecraft.class_3898;
import net.minecraft.class_9259;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.VS2ChunkAllocator;

@Mixin(class_3898.class)
public class MixinChunkMapShipyard {
   @Inject(method = "getChunkRangeFuture", at = @At("HEAD"), cancellable = true)
   private void vs$skipNeighborsForShipyard(
      class_3193 centerHolder, int range, IntFunction<class_2806> statusByRange, CallbackInfoReturnable<CompletableFuture<class_9259<List<class_2791>>>> cir
   ) {
      if (range > 0) {
         class_1923 center = centerHolder.method_60473();
         if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(center.field_9181, center.field_9180)) {
            class_2806 requiredStatus = statusByRange.apply(0);
            CompletableFuture<class_9259<class_2791>> centerFuture = centerHolder.method_60458(requiredStatus, (class_3898)this);
            cir.setReturnValue(centerFuture.thenApply(result -> result.method_57127(List::of)));
         }
      }
   }
}
