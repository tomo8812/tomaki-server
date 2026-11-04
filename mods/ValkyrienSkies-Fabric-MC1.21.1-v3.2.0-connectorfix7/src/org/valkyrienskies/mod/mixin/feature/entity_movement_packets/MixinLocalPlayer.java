package org.valkyrienskies.mod.mixin.feature.entity_movement_packets;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2596;
import net.minecraft.class_2828;
import net.minecraft.class_2833;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_746;
import net.minecraft.class_2828.class_2829;
import net.minecraft.class_2828.class_2830;
import net.minecraft.class_2828.class_2831;
import net.minecraft.class_2828.class_5911;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.networking.PacketEntityShipMotion;
import org.valkyrienskies.mod.common.networking.PacketPlayerShipMotion;
import org.valkyrienskies.mod.common.util.EntityLerper;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(class_746.class)
public abstract class MixinLocalPlayer extends class_1297 implements IEntityDraggingInformationProvider {
   @Shadow
   public abstract float method_5705(float var1);

   public MixinLocalPlayer(class_1299<?> entityType, class_1937 level) {
      super(entityType, level);
   }

   @WrapOperation(
      method = "sendPosition",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V")
   )
   private void wrapSendPosition(class_634 instance, class_2596<?> arg, Operation<Void> original) {
      class_2596<?> realArg = arg;
      if (this.getDraggingInformation().isEntityBeingDraggedByAShip()) {
         if (this.getDraggingInformation().getLastShipStoodOn() != null) {
            ClientShip ship = VSGameUtilsKt.getShipObjectWorld(class_310.method_1551().field_1687)
               .getAllShips()
               .getById(this.getDraggingInformation().getLastShipStoodOn());
            if (ship != null) {
               Vector3dc relativePosition = ship.getWorldToShip().transformPosition(VectorConversionsMCKt.toJOML(this.method_30950(1.0F)), new Vector3d());
               double relativeYaw = EntityLerper.INSTANCE.yawToShip(ship, this.method_5705(1.0F));
               PacketPlayerShipMotion packet = new PacketPlayerShipMotion(
                  ship.getId(), relativePosition.x(), relativePosition.y(), relativePosition.z(), relativeYaw
               );
               ValkyrienSkiesMod.getVsCore().getSimplePacketNetworking().sendToServer(packet);
            }
         }

         if (realArg instanceof class_2828 movePacket) {
            boolean draggedAndGrounded = this.getDraggingInformation().isEntityBeingDraggedByAShip()
               && !this.vs$isGliding()
               && !((class_1657)this).method_31549().field_7479
               && this.method_18798().field_1351 <= 0.0;
            boolean isOnGround = movePacket.method_12273() || draggedAndGrounded;
            if (movePacket.method_36171() && movePacket.method_36172()) {
               realArg = new class_2830(
                  movePacket.method_12269(0.0),
                  movePacket.method_12268(0.0),
                  movePacket.method_12274(0.0),
                  movePacket.method_12271(0.0F),
                  movePacket.method_12270(0.0F),
                  isOnGround
               );
            } else if (movePacket.method_36171()) {
               realArg = new class_2829(movePacket.method_12269(0.0), movePacket.method_12268(0.0), movePacket.method_12274(0.0), isOnGround);
            } else if (movePacket.method_36172()) {
               realArg = new class_2831(movePacket.method_12271(0.0F), movePacket.method_12270(0.0F), isOnGround);
            } else {
               realArg = new class_5911(isOnGround);
            }
         }
      }

      original.call(new Object[]{instance, realArg});
   }

   @WrapOperation(
      method = "tick",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V")
   )
   private void wrapSendVehiclePosition(class_634 instance, class_2596<?> arg, Operation<Void> original) {
      if (arg instanceof class_2833 vehiclePacket
         && this.method_5668() instanceof IEntityDraggingInformationProvider dragProvider
         && dragProvider.getDraggingInformation().isEntityBeingDraggedByAShip()
         && dragProvider.getDraggingInformation().getLastShipStoodOn() != null) {
         if (!dragProvider.vs$shouldDrag()) {
            return;
         }

         ClientShip ship = VSGameUtilsKt.getShipObjectWorld(class_310.method_1551().field_1687)
            .getAllShips()
            .getById(dragProvider.getDraggingInformation().getLastShipStoodOn());
         if (ship != null) {
            Vector3dc relativePosition = ship.getWorldToShip()
               .transformPosition(VectorConversionsMCKt.toJOML(this.method_5668().method_30950(1.0F)), new Vector3d());
            double relativeYaw = EntityLerper.INSTANCE.yawToShip(ship, this.method_5668().method_36454());
            PacketEntityShipMotion packet = new PacketEntityShipMotion(
               this.method_5668().method_5628(),
               ship.getId(),
               relativePosition.x(),
               relativePosition.y(),
               relativePosition.z(),
               0.0,
               0.0,
               0.0,
               relativeYaw,
               0.0
            );
            ValkyrienSkiesMod.getVsCore().getSimplePacketNetworking().sendToServer(packet);
         }
      }

      original.call(new Object[]{instance, arg});
   }
}
