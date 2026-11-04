package org.valkyrienskies.mod.mixin.server.command.level;

import com.mojang.authlib.GameProfile;
import java.util.Set;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2709;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(class_3222.class)
public abstract class MixinServerPlayer extends class_1657 {
   public MixinServerPlayer(class_1937 level, class_2338 blockPos, float f, GameProfile gameProfile) {
      super(level, blockPos, f, gameProfile);
   }

   @Shadow
   public abstract void method_5859(double var1, double var3, double var5);

   @Shadow
   public abstract boolean method_48105(class_3218 var1, double var2, double var4, double var6, Set<class_2709> var8, float var9, float var10);

   @Inject(at = @At("HEAD"), method = "teleportTo(DDD)V", cancellable = true)
   private void beforeTeleportTo(double x, double y, double z, CallbackInfo ci) {
      class_3218 level = ((class_3222)this).method_51469();
      Ship ship = VSGameUtilsKt.getShipManagingPos(level, x, y, z);
      if (ship != null) {
         ci.cancel();
         Vector3d inWorld = VSGameUtilsKt.toWorldCoordinates(ship, x, y, z);
         this.method_5859(inWorld.x, inWorld.y, inWorld.z);
      }
   }

   @Inject(at = @At("HEAD"), method = "dismountTo", cancellable = true)
   private void beforeDismountTo(double x, double y, double z, CallbackInfo ci) {
      class_3218 level = ((class_3222)this).method_51469();
      Ship ship = VSGameUtilsKt.getShipManagingPos(level, x, y, z);
      if (ship != null) {
         ci.cancel();
         Vector3d lookVector = VectorConversionsMCKt.toJOML(this.method_5720());
         Vector3d transformedLook = ship.getTransform().getShipToWorld().transformDirection(lookVector);
         double yaw = Math.atan2(-transformedLook.x, transformedLook.z) * 180.0 / Math.PI;
         double pitch = Math.atan2(-transformedLook.y, Math.sqrt(transformedLook.x * transformedLook.x + transformedLook.z * transformedLook.z))
            * 180.0
            / Math.PI;
         this.method_36456((float)yaw);
         this.method_36457((float)pitch);
         Vector3d inWorld = ship.getTransform().getShipToWorld().transformPosition(x, y, z, new Vector3d());
         Vector3d inWorldPrev = ship.getPrevTickTransform().getShipToWorld().transformPosition(x, y, z, new Vector3d());
         Vector3d inWorldNext = inWorld.mul(3.0, new Vector3d()).sub(inWorldPrev.mul(2.0, new Vector3d()));
         this.method_48105(level, inWorldNext.x, inWorldNext.y, inWorldNext.z, Set.of(), this.method_36454(), this.method_36455());
         ((IEntityDraggingInformationProvider)this).vs$dragImmediately(ship);
      }
   }
}
