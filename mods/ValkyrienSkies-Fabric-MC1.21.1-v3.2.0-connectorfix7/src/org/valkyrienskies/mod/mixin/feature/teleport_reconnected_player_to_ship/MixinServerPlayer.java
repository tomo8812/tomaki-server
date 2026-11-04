package org.valkyrienskies.mod.mixin.feature.teleport_reconnected_player_to_ship;

import com.mojang.authlib.GameProfile;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.config.VSGameConfig;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.EntityShipCollisionUtils;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

@Mixin(class_3222.class)
public abstract class MixinServerPlayer extends class_1657 {
   @Unique
   private Long vs$reconnectShipId = null;
   @Unique
   private Vector3d vs$reconnectPosInShip = null;
   @Unique
   private int vs$reconnectTicksWaited = 0;

   @Shadow
   public abstract class_3218 method_51469();

   public MixinServerPlayer(class_1937 level, class_2338 blockPos, float f, GameProfile gameProfile) {
      super(level, blockPos, f, gameProfile);
      throw new IllegalStateException("Unreachable");
   }

   @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
   void vs$queueReconnectToShip(class_2487 compoundTag, CallbackInfo ci) {
      if (VSGameConfig.SERVER.getTeleportReconnectedPlayers()) {
         if (compoundTag.method_10545("LastShipId")) {
            this.vs$reconnectShipId = compoundTag.method_10537("LastShipId");
            this.vs$reconnectPosInShip = new Vector3d(
               compoundTag.method_10574("RelativeShipX"), compoundTag.method_10574("RelativeShipY"), compoundTag.method_10574("RelativeShipZ")
            );
            this.vs$reconnectTicksWaited = 0;
         }
      }
   }

   @Inject(method = "tick", at = @At("TAIL"))
   void vs$reconnectSeatTick(CallbackInfo ci) {
      if (this.vs$reconnectShipId != null) {
         Ship ship = VSGameUtilsKt.getShipObjectWorld(this.method_51469()).getAllShips().getById(this.vs$reconnectShipId);
         if (ship != null) {
            this.vs$spawnAndMountSeat(ship);
            this.vs$reconnectShipId = null;
            this.vs$reconnectPosInShip = null;
         } else if (++this.vs$reconnectTicksWaited > 100) {
            this.vs$reconnectShipId = null;
            this.vs$reconnectPosInShip = null;
         }
      }
   }

   @Unique
   private void vs$spawnAndMountSeat(Ship ship) {
      class_3218 slevel = this.method_51469();
      Vector3d worldPos = ship.getShipToWorld().transformPosition(new Vector3d(this.vs$reconnectPosInShip));
      ShipMountingEntity seat = ShipMountingEntity.spawnPassengerSeat(
         slevel,
         worldPos.x,
         worldPos.y,
         worldPos.z,
         this.method_36454(),
         this.method_36455(),
         ship.getId(),
         this.vs$reconnectPosInShip.x,
         this.vs$reconnectPosInShip.y,
         this.vs$reconnectPosInShip.z
      );
      if (seat != null) {
         if (this.method_5873(seat, true)) {
            EntityShipCollisionUtils.markShipAsRecentlySpawned(ship.getId(), slevel.method_8510(), 200L);
         } else {
            seat.method_5768();
         }
      }
   }

   @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
   void vs$rememberLastShip(class_2487 compoundTag, CallbackInfo ci) {
      if (this.method_5854() instanceof ShipMountingEntity seat && seat.vs$isPassengerSeat() && seat.getDriveShipId() != null && seat.getDriveRelPos() != null) {
         compoundTag.method_10544("LastShipId", seat.getDriveShipId());
         compoundTag.method_10549("RelativeShipX", seat.getDriveRelPos().x);
         compoundTag.method_10549("RelativeShipY", seat.getDriveRelPos().y);
         compoundTag.method_10549("RelativeShipZ", seat.getDriveRelPos().z);
      } else {
         EntityDraggingInformation draggingInformation = ((IEntityDraggingInformationProvider)this).getDraggingInformation();
         if (draggingInformation.isEntityBeingDraggedByAShip()) {
            Long lastShipId = draggingInformation.getLastShipStoodOn();
            if (lastShipId != null) {
               Ship ship = VSGameUtilsKt.getShipObjectWorld(this.method_51469()).getAllShips().getById(lastShipId);
               if (ship != null) {
                  compoundTag.method_10544("LastShipId", lastShipId);
                  Vector3dc best = draggingInformation.bestRelativeEntityPosition();
                  Vector3d playerShipPosition = best != null
                     ? new Vector3d(best)
                     : ship.getWorldToShip().transformPosition(new Vector3d(this.method_23317(), this.method_23318(), this.method_23321()));
                  compoundTag.method_10549("RelativeShipX", playerShipPosition.x);
                  compoundTag.method_10549("RelativeShipY", playerShipPosition.y);
                  compoundTag.method_10549("RelativeShipZ", playerShipPosition.z);
               }
            }
         }
      }
   }
}
