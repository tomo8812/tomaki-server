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
   private ShipSleepingRender() {
   }

   public static void apply(LivingEntity entity, float partialTicks, PoseStack pose) {
      if (SleepCompat.anchored(entity) && !entity.isPassenger() && !VSGameUtilsKt.isBlockInShipyard(entity.level(), entity.position())) {
         BlockPos bed = (BlockPos)entity.getSleepingPos().orElseThrow();
         if (VSGameUtilsKt.getLoadedShipManagingPos(entity.level(), bed) instanceof ClientShip ship) {
            ShipTransform var10 = ship.getRenderTransform();
            Vec3 localBed = SleepCompat.bedPoint(bed);
            Vector3d worldBed = var10.getShipToWorld().transformPosition(new Vector3d(localBed.x, localBed.y, localBed.z));
            Vec3 entityPosition = entity.getPosition(partialTicks);
            if (worldBed.isFinite()) {
               pose.translate(worldBed.x - entityPosition.x, worldBed.y - entityPosition.y, worldBed.z - entityPosition.z);
               pose.mulPose(new Quaternionf(var10.getShipToWorldRotation()));
               Vector3dc scale = var10.getShipToWorldScaling();
               pose.scale((float)scale.x(), (float)scale.y(), (float)scale.z());
            }
         }
      }
   }
}
