package maki.airship;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public final class ShipSleepingRender {
   public static void apply(LivingEntity var0, float var1, PoseStack var2) {
      if (SleepCompat.anchored(var0) && !var0.isPassenger()) {
         if (!VSGameUtilsKt.isBlockInShipyard(var0.level(), var0.position())) {
            BlockPos var3 = (BlockPos)var0.getSleepingPos().orElseThrow();
            if (VSGameUtilsKt.getLoadedShipManagingPos(var0.level(), var3) instanceof ClientShip var5) {
               ShipTransform var6 = var5.getRenderTransform();
               Vec3 var7 = SleepCompat.bedPoint(var3);
               Vector3d var8 = var6.getShipToWorld().transformPosition(new Vector3d(var7.x, var7.y, var7.z));
               Vec3 var9 = var0.getPosition(var1);
               if (var8.isFinite()) {
                  var2.translate(var8.x - var9.x, var8.y - var9.y, var8.z - var9.z);
                  var2.mulPose(new Quaternionf(var6.getShipToWorldRotation()));
                  Vector3dc var10 = var6.getShipToWorldScaling();
                  var2.scale((float)var10.x(), (float)var10.y(), (float)var10.z());
               }
            }
         }
      }
   }
}
