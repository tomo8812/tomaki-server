package maki.shipanchor.mixin;

import maki.shipanchor.SleepAnchorAccess;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "maki.airship.SleepCompat", remap = false)
public abstract class AirshipSleepCompatMixin {
   @Inject(method = "follow(Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
   private static void maki$disableLegacyPhysicalFollow(LivingEntity var0, CallbackInfo var1) {
      var1.cancel();
   }

   @Inject(method = "anchored(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
   private static void maki$useAuthoritativeSleepAnchorState(LivingEntity var0, CallbackInfoReturnable<Boolean> var1) {
      var1.setReturnValue(var0 instanceof SleepAnchorAccess var2 && var2.maki$isSleepAnchorActive());
   }
}
