package org.valkyrienskies.mod.mixin.feature.entity_movement_packets;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_1297;
import net.minecraft.class_2684;
import net.minecraft.class_2726;
import net.minecraft.class_2743;
import net.minecraft.class_2777;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3231;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.impl.networking.simple.SimplePacket;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.networking.PacketEntityShipMotion;
import org.valkyrienskies.mod.common.networking.PacketMobShipRotation;
import org.valkyrienskies.mod.common.util.EntityDragger;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.EntityLerper;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;
import org.valkyrienskies.mod.mixinducks.world.entity.PlayerDuck;

@Mixin(class_3231.class)
public class MixinServerEntity {
   @Shadow
   @Final
   private class_1297 field_14049;
   @Shadow
   @Final
   private class_3218 field_18258;

   @WrapOperation(method = "sendChanges", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"))
   private void wrapBroadcastAccept(Consumer instance, Object t, Operation<Void> original) {
      if ((t instanceof class_2743 || t instanceof class_2777 || t instanceof class_2684 || t instanceof class_2726)
         && EntityDragger.isDraggable(this.field_14049)) {
         IEntityDraggingInformationProvider draggedEntity = (IEntityDraggingInformationProvider)this.field_14049;
         EntityDraggingInformation dragInfo = draggedEntity.getDraggingInformation();
         if (dragInfo != null && dragInfo.isEntityBeingDraggedByAShip() && dragInfo.getLastShipStoodOn() != null) {
            ServerShip ship = VSGameUtilsKt.getShipObjectWorld(this.field_18258).getAllShips().getById(dragInfo.getLastShipStoodOn());
            if (ship != null) {
               Vector3d position = ship.getWorldToShip()
                  .transformPosition(new Vector3d(this.field_14049.method_23317(), this.field_14049.method_23318(), this.field_14049.method_23321()));
               if (dragInfo.getServerRelativePlayerPosition() != null) {
                  position = new Vector3d(dragInfo.getServerRelativePlayerPosition());
               }

               Vector3d motion = ship.getTransform()
                  .getWorldToShip()
                  .transformDirection(
                     new Vector3d(
                        this.field_14049.method_18798().method_10216(),
                        this.field_14049.method_18798().method_10214(),
                        this.field_14049.method_18798().method_10215()
                     ),
                     new Vector3d()
                  );
               double yaw;
               if (!(t instanceof class_2726)) {
                  yaw = EntityLerper.INSTANCE.yawToShip(ship, this.field_14049.method_36454());
               } else {
                  yaw = EntityLerper.INSTANCE.yawToShip(ship, this.field_14049.method_5791());
               }

               double pitch = this.field_14049.method_36455();
               SimplePacket vsPacket;
               if (!(t instanceof class_2726)) {
                  vsPacket = new PacketEntityShipMotion(
                     this.field_14049.method_5628(), ship.getId(), position.x, position.y, position.z, motion.x, motion.y, motion.z, yaw, pitch
                  );
               } else {
                  vsPacket = new PacketMobShipRotation(this.field_14049.method_5628(), ship.getId(), yaw, pitch);
               }

               List<class_3222> players = this.field_18258
                  .method_18766(player -> player.method_5727(this.field_14049.method_23317(), this.field_14049.method_23318(), this.field_14049.method_23321()));
               players.forEach(player -> ValkyrienSkiesMod.getVsCore().getSimplePacketNetworking().sendToClients(vsPacket, ((PlayerDuck)player).vs_getPlayer()));
               return;
            }
         }
      }

      original.call(new Object[]{instance, t});
   }
}
