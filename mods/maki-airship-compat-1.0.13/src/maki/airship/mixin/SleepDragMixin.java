package maki.airship.mixin;

import maki.airship.SleepCompat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.util.EntityDragger;

@Mixin(value = EntityDragger.class, remap = false)
public abstract class SleepDragMixin {
   @Inject(method = "isDraggable(Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true, remap = false)
   private static void maki$avoidDoubleSleepMovement(Entity entity, CallbackInfoReturnable<Boolean> cir) {
      if (entity instanceof LivingEntity living && SleepCompat.anchored(living)) {
         cir.setReturnValue(false);
      }
   }
}
