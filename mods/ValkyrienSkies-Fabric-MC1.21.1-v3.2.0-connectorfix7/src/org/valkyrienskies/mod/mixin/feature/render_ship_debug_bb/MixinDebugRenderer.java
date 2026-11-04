package org.valkyrienskies.mod.mixin.feature.render_ship_debug_bb;

import net.minecraft.class_1921;
import net.minecraft.class_238;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_638;
import net.minecraft.class_761;
import net.minecraft.class_863;
import net.minecraft.class_4597.class_4598;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBdc;
import org.joml.primitives.AABBic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.internal.world.VsiClientShipWorld;
import org.valkyrienskies.mod.common.VSClientGameUtils;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(class_863.class)
public class MixinDebugRenderer {
   @Inject(method = "render", at = @At("HEAD"))
   private void postRender(class_4587 matrices, class_4598 bufferSource, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
      class_638 world = class_310.method_1551().field_1687;
      VsiClientShipWorld shipObjectClientWorld = VSGameUtilsKt.getShipObjectWorld(world);
      if (class_310.method_1551().method_1561().method_3958()) {
         for (ClientShip shipObjectClient : shipObjectClientWorld.getLoadedShips()) {
            ShipTransform shipRenderTransform = shipObjectClient.getRenderTransform();
            Vector3dc shipRenderPosition = shipRenderTransform.getShipPositionInWorldCoordinates();
            double renderRadius = 0.25;
            class_238 shipCenterOfMassBox = new class_238(
                  shipRenderPosition.x() - 0.25,
                  shipRenderPosition.y() - 0.25,
                  shipRenderPosition.z() - 0.25,
                  shipRenderPosition.x() + 0.25,
                  shipRenderPosition.y() + 0.25,
                  shipRenderPosition.z() + 0.25
               )
               .method_989(-cameraX, -cameraY, -cameraZ);
            class_761.method_22982(matrices, bufferSource.getBuffer(class_1921.method_23594()), shipCenterOfMassBox, 0.98039216F, 0.7607843F, 0.07450981F, 1.0F);
            AABBic shipVoxelAABBic = shipObjectClient.getShipAABB();
            if (shipVoxelAABBic != null) {
               matrices.method_22903();
               Vector3dc centerOfAABB = shipVoxelAABBic.center(new Vector3d());
               class_238 shipVoxelAABBAfterOffset = new class_238(
                  shipVoxelAABBic.minX() - centerOfAABB.x(),
                  shipVoxelAABBic.minY() - centerOfAABB.y(),
                  shipVoxelAABBic.minZ() - centerOfAABB.z(),
                  shipVoxelAABBic.maxX() - centerOfAABB.x(),
                  shipVoxelAABBic.maxY() - centerOfAABB.y(),
                  shipVoxelAABBic.maxZ() - centerOfAABB.z()
               );
               VSClientGameUtils.transformRenderWithShip(
                  shipRenderTransform, matrices, centerOfAABB.x(), centerOfAABB.y(), centerOfAABB.z(), cameraX, cameraY, cameraZ
               );
               class_761.method_22982(matrices, bufferSource.getBuffer(class_1921.method_23594()), shipVoxelAABBAfterOffset, 1.0F, 0.0F, 0.0F, 1.0F);
               matrices.method_22909();
            }

            AABBdc shipRenderAABBdc = shipObjectClient.getRenderAABB();
            class_238 shipRenderAABB = VectorConversionsMCKt.toMinecraft(shipRenderAABBdc);
            class_761.method_22982(
               matrices,
               bufferSource.getBuffer(class_1921.method_23594()),
               shipRenderAABB.method_989(-cameraX, -cameraY, -cameraZ),
               0.91764706F,
               0.0F,
               0.8509804F,
               1.0F
            );
         }
      }

      bufferSource.method_22993();
   }
}
