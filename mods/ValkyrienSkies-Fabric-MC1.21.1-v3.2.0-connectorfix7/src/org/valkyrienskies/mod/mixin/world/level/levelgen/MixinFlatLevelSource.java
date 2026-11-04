package org.valkyrienskies.mod.mixin.world.level.levelgen;

import java.util.concurrent.CompletableFuture;
import net.minecraft.class_1923;
import net.minecraft.class_2791;
import net.minecraft.class_2897;
import net.minecraft.class_5138;
import net.minecraft.class_6748;
import net.minecraft.class_7138;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.VS2ChunkAllocator;

@Mixin(class_2897.class)
public class MixinFlatLevelSource {
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
}
