package org.valkyrienskies.mod.mixin.world.level.levelgen;

import java.util.concurrent.CompletableFuture;
import net.minecraft.class_1923;
import net.minecraft.class_2680;
import net.minecraft.class_2791;
import net.minecraft.class_3233;
import net.minecraft.class_3754;
import net.minecraft.class_4543;
import net.minecraft.class_4966;
import net.minecraft.class_5138;
import net.minecraft.class_5284;
import net.minecraft.class_5309;
import net.minecraft.class_5539;
import net.minecraft.class_6748;
import net.minecraft.class_6880;
import net.minecraft.class_7138;
import net.minecraft.class_2893.class_2894;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.VS2ChunkAllocator;

@Mixin(class_3754.class)
public class MixinNoiseBasedChunkGenerator {
   @Shadow
   @Final
   protected class_6880<class_5284> field_24774;

   @Inject(method = "getBaseColumn", at = @At("HEAD"), cancellable = true)
   private void preGetBaseColumn(int i, int j, class_5539 levelHeightAccessor, class_7138 randomState, CallbackInfoReturnable<class_4966> cir) {
      if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(i, j)) {
         class_5309 noiseSettings = ((class_5284)this.field_24774.comp_349()).comp_474();
         int k = Math.max(noiseSettings.comp_173(), levelHeightAccessor.method_31607());
         cir.setReturnValue(new class_4966(k, new class_2680[0]));
      }
   }

   @Inject(
      method = "buildSurface(Lnet/minecraft/server/level/WorldGenRegion;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/chunk/ChunkAccess;)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void preBuildSurface(class_3233 worldGenRegion, class_5138 structureManager, class_7138 randomState, class_2791 chunkAccess, CallbackInfo ci) {
      class_1923 chunkPos = chunkAccess.method_12004();
      if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(chunkPos.field_9181, chunkPos.field_9180)) {
         ci.cancel();
      }
   }

   @Inject(method = "applyCarvers", at = @At("HEAD"), cancellable = true)
   private void preApplyCarvers(
      class_3233 worldGenRegion,
      long l,
      class_7138 randomState,
      class_4543 biomeManager,
      class_5138 structureManager,
      class_2791 chunkAccess,
      class_2894 carving,
      CallbackInfo ci
   ) {
      class_1923 chunkPos = chunkAccess.method_12004();
      if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(chunkPos.field_9181, chunkPos.field_9180)) {
         ci.cancel();
      }
   }

   @Inject(method = "fillFromNoise", at = @At("HEAD"), cancellable = true)
   private void preFillFromNoise(
      class_6748 blender,
      class_7138 randomState,
      class_5138 structureManager,
      class_2791 chunkAccess,
      CallbackInfoReturnable<CompletableFuture<class_2791>> cir
   ) {
      class_1923 chunkPos = chunkAccess.method_12004();
      if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(chunkPos.field_9181, chunkPos.field_9180)) {
         cir.setReturnValue(CompletableFuture.completedFuture(chunkAccess));
      }
   }

   @Inject(method = "spawnOriginalMobs", at = @At("HEAD"), cancellable = true)
   private void preSpawnOriginalMobs(class_3233 worldGenRegion, CallbackInfo ci) {
      class_1923 chunkPos = worldGenRegion.method_33561();
      if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(chunkPos.field_9181, chunkPos.field_9180)) {
         ci.cancel();
      }
   }
}
