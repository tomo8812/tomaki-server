package org.valkyrienskies.mod.mixin.feature.transform_particles;

import net.minecraft.class_2394;
import net.minecraft.class_638;
import net.minecraft.class_703;
import net.minecraft.class_761;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_761.class)
public abstract class MixinLevelRenderer {
   @Shadow
   private class_638 field_4085;

   @Shadow
   @Nullable
   protected abstract class_703 method_3288(
      class_2394 var1, boolean var2, boolean var3, double var4, double var6, double var8, double var10, double var12, double var14
   );

   @Inject(
      method = "addParticleInternal(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;",
      at = @At("HEAD"),
      cancellable = true
   )
   private void spawnParticleInWorld(
      class_2394 parameters,
      boolean alwaysSpawn,
      boolean canSpawnOnMinimal,
      double x,
      double y,
      double z,
      double velocityX,
      double velocityY,
      double velocityZ,
      CallbackInfoReturnable<class_703> cir
   ) {
      ClientShip ship = VSGameUtilsKt.getLoadedShipManagingPos(this.field_4085, (int)x >> 4, (int)z >> 4);
      if (ship != null) {
         Matrix4dc transform = ship.getRenderTransform().getShipToWorldMatrix();
         Vector3d p = transform.transformPosition(new Vector3d(x, y, z));
         Vector3d v = transform.transformDirection(new Vector3d(velocityX, velocityY, velocityZ)).fma(0.05, ship.getVelocity());
         cir.setReturnValue(this.method_3288(parameters, alwaysSpawn, canSpawnOnMinimal, p.x, p.y, p.z, v.x, v.y, v.z));
      }
   }
}
