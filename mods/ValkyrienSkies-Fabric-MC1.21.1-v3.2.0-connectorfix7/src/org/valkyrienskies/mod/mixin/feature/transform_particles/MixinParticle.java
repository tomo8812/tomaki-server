package org.valkyrienskies.mod.mixin.feature.transform_particles;

import net.minecraft.class_638;
import net.minecraft.class_703;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_703.class)
public abstract class MixinParticle {
   @Shadow
   protected double field_3852;
   @Shadow
   protected double field_3869;
   @Shadow
   protected double field_3850;
   @Shadow
   protected double field_3858;
   @Shadow
   protected double field_3838;
   @Shadow
   protected double field_3856;

   @Shadow
   public abstract void method_3063(double var1, double var3, double var5);

   @Inject(method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDD)V", at = @At("TAIL"))
   public void checkShipCoords(class_638 world, double x, double y, double z, CallbackInfo ci) {
      ClientShip ship = VSGameUtilsKt.getLoadedShipManagingPos(world, (int)x >> 4, (int)z >> 4);
      if (ship != null) {
         Vector3d tempVel = ship.getVelocity().get(new Vector3d());
         tempVel.add(
            ship.getRenderTransform()
               .getShipToWorld()
               .transformDirection(new Vector3d(x, y, z).sub(ship.getTransform().getPositionInShip()))
               .cross(ship.getOmega())
         );
         tempVel.mul(0.05);
         Vector3d p = ship.getTransform().getShipToWorld().transformPosition(new Vector3d(x, y, z));
         ship.getPrevTickTransform().getWorldToShip().transformPosition(p);
         ship.getTransform().getShipToWorld().transformPosition(p);
         this.method_3063(p.x, p.y, p.z);
         this.field_3858 = p.x;
         this.field_3838 = p.y;
         this.field_3856 = p.z;
      }
   }

   @Inject(method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDD)V", at = @At("TAIL"))
   public void checkShipPosAndVelocity(class_638 world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfo ci) {
      ClientShip ship = VSGameUtilsKt.getLoadedShipManagingPos(world, (int)x >> 4, (int)z >> 4);
      if (ship != null) {
         Matrix4dc transform = ship.getTransform().getShipToWorld();
         Vector3d p = transform.transformPosition(new Vector3d(x, y, z));
         ship.getPrevTickTransform().getWorldToShip().transformPosition(p);
         transform.transformPosition(p);
         Vector3d v = transform.transformDirection(new Vector3d(this.field_3852, this.field_3869, this.field_3850)).fma(0.05, ship.getVelocity());
         this.method_3063(p.x, p.y, p.z);
         this.field_3852 = v.x;
         this.field_3869 = v.y;
         this.field_3850 = v.z;
      }
   }
}
