package org.valkyrienskies.mod.mixin.feature.spawn_player_on_ship;

import net.minecraft.class_2828;
import net.minecraft.class_3222;
import net.minecraft.class_3244;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.util.EntityShipCollisionUtils;

@Mixin(class_3244.class)
public abstract class MixinServerGamePacketListenerImpl {
   @Shadow
   public class_3222 field_14140;
   @Shadow
   @Final
   static Logger field_14121;

   @Shadow
   public abstract void method_14363(double var1, double var3, double var5, float var7, float var8);

   @Inject(
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getBoundingBox()Lnet/minecraft/world/phys/AABB;"),
      method = "handleMovePlayer",
      cancellable = true
   )
   private void injectHandleMovePlayer(class_2828 packet, CallbackInfo ci) {
      if (EntityShipCollisionUtils.isCollidingWithUnloadedShips(this.field_14140)) {
         ci.cancel();
         field_14121.warn("{} moved while colliding with unloaded ships!", this.field_14140.method_5477().getString());
         this.method_14363(
            this.field_14140.method_23317(),
            this.field_14140.method_23318(),
            this.field_14140.method_23321(),
            this.field_14140.method_36454(),
            this.field_14140.method_36455()
         );
      }
   }
}
