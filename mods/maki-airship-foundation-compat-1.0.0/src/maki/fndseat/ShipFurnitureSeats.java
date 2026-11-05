package maki.fndseat;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.satisfy.foundation.registry.FoundationEntities;
import net.satisfy.foundation.seat.ChairEntity;
import org.joml.Vector3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;

public final class ShipFurnitureSeats {
   private static final Map<ShipMountingEntity, ShipFurnitureSeats.Anchor> ANCHORS = new WeakHashMap<>();
   private static final Map<Level, Map<BlockPos, WeakReference<ShipMountingEntity>>> OCCUPIED = new WeakHashMap<>();
   private static final Logger LOG = LoggerFactory.getLogger("MakiFurnitureSeat");

   public static ItemInteractionResult use(Level var0, Player var1, InteractionHand var2, BlockHitResult var3, double var4) {
      LoadedShip var6 = VSGameUtilsKt.getLoadedShipManagingPos(var0, var3.getBlockPos());
      if (var6 == null) {
         return null;
      }

      if (var1.isShiftKeyDown() || var3.getDirection() == Direction.DOWN || !var1.getItemInHand(var2).isEmpty()) {
         return null;
      }

      if (var0.isClientSide()) {
         return ItemInteractionResult.SUCCESS;
      }

      if (var0 instanceof ServerLevel var7) {
         BlockPos var8 = var3.getBlockPos().immutable();
         Map var9 = OCCUPIED.computeIfAbsent(var0, var0x -> new HashMap<>());
         var9.entrySet().removeIf(var0x -> {
            ShipMountingEntity var1x = (ShipMountingEntity)((WeakReference)var0x.getValue()).get();
            return var1x == null || var1x.isRemoved() || var1x.getPassengers().isEmpty();
         });
         WeakReference var10 = (WeakReference)var9.get(var8);
         if (var10 != null) {
            return ItemInteractionResult.FAIL;
         }

         Vec3 var11 = var1.position();
         Vector3d var12 = FurnitureCoordinates.localPrevious(var11.x, var11.y, var11.z, VSGameUtilsKt.isBlockInShipyard(var0, var11), var6.getWorldToShip());
         ChairEntity var13 = new ChairEntity((EntityType)FoundationEntities.CHAIR.get(), var0);
         var13.moveTo(var8.getX() + 0.5, var8.getY() + 0.25 + var4, var8.getZ() + 0.5, 0.0F, 0.0F);
         Vec3 var14 = var13.getPassengerRidingPosition(var1).subtract(var1.getVehicleAttachmentPoint(var13));
         Vector3d var15 = new Vector3d(var14.x, var14.y + 0.5, var14.z);
         Vector3d var16 = var6.getShipToWorld().transformPosition(new Vector3d(var15));
         if (var16.isFinite() && var12.isFinite()) {
            ShipMountingEntity var17 = ShipMountingEntity.spawnPassengerSeat(
               var7, var16.x, var16.y, var16.z, var1.getYRot(), var1.getXRot(), var6.getId(), var15.x, var15.y, var15.z, true
            );
            if (var17 == null) {
               return ItemInteractionResult.FAIL;
            } else {
               ANCHORS.put(var17, new ShipFurnitureSeats.Anchor(var8, var0.getBlockState(var8).getBlock(), var6.getId(), var12));
               var9.put(var8, new WeakReference<>(var17));
               if (!var1.startRiding(var17, true)) {
                  remove(var17);
                  var17.discard();
                  LOG.warn("Furniture mount cancelled; released seat at {}", var8);
                  return ItemInteractionResult.FAIL;
               } else {
                  LOG.info("Mounted ship furniture via X-seat vehicle: ship={} block={} seat={}", new Object[]{var6.getId(), var8, var17.getId()});
                  return ItemInteractionResult.SUCCESS;
               }
            }
         } else {
            return ItemInteractionResult.FAIL;
         }
      } else {
         return null;
      }
   }

   public static void tick(ShipMountingEntity var0) {
      ShipFurnitureSeats.Anchor var1 = ANCHORS.get(var0);
      if (var1 != null && !var0.level().isClientSide()) {
         LoadedShip var2 = VSGameUtilsKt.getLoadedShipManagingPos(var0.level(), var1.block());
         if (var2 == null || var2.getId() != var1.shipId() || var0.level().getBlockState(var1.block()).getBlock() != var1.type()) {
            var0.ejectPassengers();
            var0.discard();
            remove(var0);
         }
      }
   }

   public static Vec3 dismount(ShipMountingEntity var0, LivingEntity var1) {
      ShipFurnitureSeats.Anchor var2 = ANCHORS.get(var0);
      if (var2 != null && !var0.level().isClientSide()) {
         LoadedShip var3 = VSGameUtilsKt.getLoadedShipManagingPos(var0.level(), var2.block());
         if (var3 != null && var3.getId() == var2.shipId()) {
            Vector3d var4 = var3.getShipToWorld().transformPosition(new Vector3d(var2.previous()));
            if (!var4.isFinite()) {
               return null;
            }

            for (double var8 : new double[]{0.0, 0.5, 1.0, 1.5, 2.0}) {
               Vec3 var10 = new Vec3(var4.x, var4.y + var8, var4.z);
               if (var0.level().noCollision(var1, var1.getBoundingBox().move(var10.subtract(var1.position())))) {
                  return var10;
               }
            }

            return var1.position();
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static void remove(ShipMountingEntity var0) {
      ShipFurnitureSeats.Anchor var1 = ANCHORS.remove(var0);
      if (var1 != null) {
         Map var2 = OCCUPIED.get(var0.level());
         if (var2 != null) {
            WeakReference var3 = (WeakReference)var2.get(var1.block());
            if (var3 != null && var3.get() == var0) {
               var2.remove(var1.block());
            }
         }
      }
   }

   private record Anchor(BlockPos block, Block type, long shipId, Vector3d previous) {
   }
}
