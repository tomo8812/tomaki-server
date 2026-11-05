package maki.shipanchor.mixin;

import maki.shipanchor.SleepAnchorAccess;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.util.EntityDragger;

@Mixin(value = EntityDragger.class, remap = false)
public abstract class SleepDragMixin {
   @Inject(method = "isDraggable(Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true, remap = false)
   private static void maki$suppressNativeDragForAuthoritativeSleepAnchor(Entity var0, CallbackInfoReturnable<Boolean> var1) {
      if (var0 instanceof SleepAnchorAccess var2 && var2.maki$isSleepAnchorActive()) {
         var1.setReturnValue(false);
      }
   }
}
