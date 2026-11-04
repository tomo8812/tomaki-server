package org.valkyrienskies.mod.mixin.world.level.levelgen;

import java.util.concurrent.CompletableFuture;
import net.minecraft.class_1923;
import net.minecraft.class_2791;
import net.minecraft.class_9310;
import net.minecraft.class_9312;
import net.minecraft.class_9761;
import net.minecraft.class_9762;
import net.minecraft.class_9770;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.VS2ChunkAllocator;

@Mixin(class_9310.class)
public class MixinChunkStatus {
   @Inject(method = {"generateBiomes", "generateNoise"}, at = @At("HEAD"), cancellable = true)
   private static void vs$skipBiomeAndNoiseGeneration(
      class_9312 worldGenContext,
      class_9770 chunkStep,
      class_9762<class_9761> staticCache2D,
      class_2791 chunkAccess,
      CallbackInfoReturnable<CompletableFuture<class_2791>> cir
   ) {
      class_1923 chunkPos = chunkAccess.method_12004();
      if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(chunkPos.field_9181, chunkPos.field_9180)) {
         cir.setReturnValue(CompletableFuture.completedFuture(chunkAccess));
      }
   }

   @Inject(method = "generateStructureStarts", at = @At("HEAD"), cancellable = true)
   private static void vs$skipStructureStarts(
      class_9312 worldGenContext,
      class_9770 chunkStep,
      class_9762<class_9761> staticCache2D,
      class_2791 chunkAccess,
      CallbackInfoReturnable<CompletableFuture<class_2791>> cir
   ) {
      class_1923 chunkPos = chunkAccess.method_12004();
      if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(chunkPos.field_9181, chunkPos.field_9180)) {
         worldGenContext.comp_2434().method_39778(chunkAccess);
         cir.setReturnValue(CompletableFuture.completedFuture(chunkAccess));
      }
   }

   @Inject(
      method = {"generateStructureReferences", "generateSurface", "generateCarvers", "generateFeatures", "generateSpawn"},
      at = @At("HEAD"),
      cancellable = true
   )
   private static void vs$skipShipyardWorldgenStages(
      class_9312 worldGenContext,
      class_9770 chunkStep,
      class_9762<class_9761> staticCache2D,
      class_2791 chunkAccess,
      CallbackInfoReturnable<CompletableFuture<class_2791>> cir
   ) {
      class_1923 chunkPos = chunkAccess.method_12004();
      if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(chunkPos.field_9181, chunkPos.field_9180)) {
         cir.setReturnValue(CompletableFuture.completedFuture(chunkAccess));
      }
   }
}
