package maki.airship;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public final class SleepCompat {
   private static boolean finite(Vec3 var0) {
      return Double.isFinite(var0.x) && Double.isFinite(var0.y) && Double.isFinite(var0.z);
   }

   private static boolean yard(LivingEntity var0, Vec3 var1) {
      return VSGameUtilsKt.isBlockInShipyard(var0.level(), var1);
   }

   public static Vec3 bedPoint(BlockPos var0) {
      return new Vec3(var0.getX() + 0.5, var0.getY() + 0.6875, var0.getZ() + 0.5);
   }

   public static boolean anchored(LivingEntity var0) {
      return var0.isSleeping() && var0.getSleepingPos().map(var1 -> yard(var0, bedPoint(var1))).orElse(false);
   }

   public static void follow(LivingEntity var0) {
      if (anchored(var0) && !var0.isPassenger()) {
         BlockPos var1 = (BlockPos)var0.getSleepingPos().orElseThrow();
         LoadedShip var2 = VSGameUtilsKt.getLoadedShipManagingPos(var0.level(), var1);
         if (var2 != null) {
            Vec3 var3 = bedPoint(var1);
            Vector3d var4 = var2.getShipToWorld().transformPosition(new Vector3d(var3.x, var3.y, var3.z));
            if (var4.isFinite()) {
               var0.setPos(var4.x, var4.y, var4.z);
               var0.setDeltaMovement(Vec3.ZERO);
               var0.fallDistance = 0.0F;
            }
         }
      }
   }

   public static SleepCompat.WakeAnchor beforeWake(LivingEntity var0) {
      BlockPos var1 = (BlockPos)var0.getSleepingPos().orElse(null);
      if (var1 != null && yard(var0, bedPoint(var1))) {
         LoadedShip var2 = VSGameUtilsKt.getLoadedShipManagingPos(var0.level(), var1);
         return new SleepCompat.WakeAnchor(var1, var2 == null ? null : var2.getId(), var0.position());
      } else {
         return null;
      }
   }

   public static void afterWake(LivingEntity var0, SleepCompat.WakeAnchor var1) {
      if (var1 != null && yard(var0, var0.position())) {
         Vec3 var2 = var1.previous();
         LoadedShip var3 = VSGameUtilsKt.getLoadedShipManagingPos(var0.level(), var1.bed());
         if (var3 != null && var1.shipId() != null && var3.getId() == var1.shipId()) {
            Vec3 var4 = var0.position();
            Vector3d var5 = var3.getShipToWorld().transformPosition(new Vector3d(var4.x, var4.y, var4.z));
            if (var5.isFinite()) {
               for (double var9 : new double[]{0.0, 0.5, 1.0, 1.5, 2.0}) {
                  Vec3 var11 = new Vec3(var5.x, var5.y + var9, var5.z);
                  if (var0.level().noCollision(var0, var0.getBoundingBox().move(var11.subtract(var0.position())))) {
                     double var12 = Math.toRadians(var0.getYRot());
                     Vector3d var14 = var3.getShipToWorld().transformDirection(new Vector3d(-Math.sin(var12), 0.0, Math.cos(var12)));
                     var0.setPos(var11.x, var11.y, var11.z);
                     if (var14.isFinite() && var14.x * var14.x + var14.z * var14.z > 1.0E-12) {
                        var0.setYRot((float)Math.toDegrees(Math.atan2(-var14.x, var14.z)));
                     }

                     var0.setDeltaMovement(Vec3.ZERO);
                     var0.fallDistance = 0.0F;
                     return;
                  }
               }
            }
         }

         if (finite(var2) && !yard(var0, var2)) {
            var0.setPos(var2.x, var2.y, var2.z);
            var0.setDeltaMovement(Vec3.ZERO);
            var0.fallDistance = 0.0F;
         }
      }
   }

   public record WakeAnchor(BlockPos bed, Long shipId, Vec3 previous) {
   }
}
