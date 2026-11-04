package org.valkyrienskies.mod.mixin.world.level.chunk;

import com.mojang.datafixers.util.Pair;
import net.minecraft.class_2338;
import net.minecraft.class_2794;
import net.minecraft.class_3195;
import net.minecraft.class_3218;
import net.minecraft.class_6880;
import net.minecraft.class_6885;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.VS2ChunkAllocator;

@Mixin(class_2794.class)
public class MixinChunkGenerator {
   @Inject(method = "findNearestMapStructure", at = @At("HEAD"), cancellable = true)
   private void preFindNearestMapFeature(
      class_3218 serverLevel,
      class_6885<class_3195> holderSet,
      class_2338 blockPos,
      int i,
      boolean bl,
      CallbackInfoReturnable<Pair<class_2338, class_6880<class_3195>>> cir
   ) {
      if (VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(blockPos.method_10263() >> 4, blockPos.method_10260() >> 4)) {
         cir.setReturnValue(null);
      }
   }
}
