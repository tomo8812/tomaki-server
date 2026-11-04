package maki.airship;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.entity.ShipMountedToData;

public final class ShipEntityLight {
   public static int packed(Entity var0, float var1, int var2) {
      ShipMountedToData var3 = VSGameUtilsKt.getShipMountedToData(var0, var1);
      if (var3 != null && var3.getShipMountedTo() instanceof ClientShip var4) {
         Vec3 var9 = var0.getLightProbePosition(var1);
         Vector3d var6 = var4.getRenderTransform().getWorldToShip().transformPosition(new Vector3d(var9.x, var9.y, var9.z));
         if (!var6.isFinite()) {
            return var2;
         }

         int var7 = var0.level().getBrightness(LightLayer.BLOCK, BlockPos.containing(var6.x, var6.y, var6.z));
         int var8 = var2 >>> 4 & 15;
         return var7 > var8 ? var2 & -241 | (var7 & 15) << 4 : var2;
      } else {
         return var2;
      }
   }
}
