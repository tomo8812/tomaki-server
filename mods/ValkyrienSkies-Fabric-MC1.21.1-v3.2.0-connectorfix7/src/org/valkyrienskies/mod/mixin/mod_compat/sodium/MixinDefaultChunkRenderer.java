package org.valkyrienskies.mod.mixin.mod_compat.sodium;

import net.caffeinemc.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import net.caffeinemc.mods.sodium.client.render.chunk.DefaultChunkRenderer;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(DefaultChunkRenderer.class)
public abstract class MixinDefaultChunkRenderer {
   @Inject(method = "getVisibleFaces", at = @At("HEAD"), cancellable = true, remap = false)
   private static void cancelBlockFaceCulling(int originX, int originY, int originZ, int chunkX, int chunkY, int chunkZ, CallbackInfoReturnable<Integer> cir) {
      if (VSGameUtilsKt.isChunkInShipyard(class_310.method_1551().field_1687, chunkX, chunkZ)) {
         cir.setReturnValue(ModelQuadFacing.ALL);
      }
   }
}
