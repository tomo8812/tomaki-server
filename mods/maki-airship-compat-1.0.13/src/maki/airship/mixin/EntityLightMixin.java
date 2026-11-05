package maki.airship.mixin;

import maki.airship.ShipEntityLight;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityRenderer.class, remap = false)
public abstract class EntityLightMixin {
   @Inject(method = "getPackedLightCoords(Lnet/minecraft/world/entity/Entity;F)I", at = @At("RETURN"), cancellable = true, remap = false)
   private void maki$sampleShipEyeLight(Entity var1, float var2, CallbackInfoReturnable<Integer> var3) {
      var3.setReturnValue(ShipEntityLight.packed(var1, var2, (Integer)var3.getReturnValue()));
   }
}
