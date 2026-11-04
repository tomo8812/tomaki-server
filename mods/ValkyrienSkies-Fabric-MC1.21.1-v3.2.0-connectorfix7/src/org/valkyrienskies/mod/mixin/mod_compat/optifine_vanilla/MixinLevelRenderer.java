package org.valkyrienskies.mod.mixin.mod_compat.optifine_vanilla;

import net.minecraft.class_1923;
import net.minecraft.class_638;
import net.minecraft.class_761;
import net.minecraft.class_769;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.mixinducks.client.world.ClientChunkCacheDuck;

@Mixin(class_761.class)
public abstract class MixinLevelRenderer {
   @Shadow
   private class_638 field_4085;
   @Shadow
   private class_769 field_4112;

   @Inject(method = "allChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ViewArea;repositionCamera(DD)V"))
   private void afterRefresh(CallbackInfo ci) {
      if (this.field_4085.method_2935() instanceof ClientChunkCacheDuck chunks) {
         chunks.vs$getShipChunks().forEach((pos, chunk) -> {
            for (int y = this.field_4085.method_32891(); y < this.field_4085.method_31597(); y++) {
               this.field_4112.method_16040(class_1923.method_8325(pos), y, class_1923.method_8332(pos), false);
            }
         });
      }
   }
}
