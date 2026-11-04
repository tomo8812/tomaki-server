package maki.airship;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.satisfy.foundation.seat.ChairEntity;
import net.satisfy.foundation.seat.SeatUtil;
import org.joml.Vector3d;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public final class SeatCompat {
   private static final Map<Entity, SeatCompat.Anchor> ANCHORS = new WeakHashMap<>();

   public static void remember(Level var0, BlockPos var1, ChairEntity var2, BlockPos var3) {
      if (!var0.isClientSide()) {
         LoadedShip var4 = VSGameUtilsKt.getLoadedShipManagingPos(var0, var1);
         Vector3d var5 = var4 == null ? null : var4.getWorldToShip().transformPosition(new Vector3d(var3.getX() + 0.5, var3.getY(), var3.getZ() + 0.5));
         ANCHORS.put(var2, new SeatCompat.Anchor(var1.immutable(), var4 == null ? null : var4.getId(), var5));
      }
   }

   public static Vec3 dismount(Entity var0, LivingEntity var1) {
      SeatCompat.Anchor var2 = ANCHORS.get(var0);
      if (var2 != null && var2.shipId() != null && !var0.level().isClientSide()) {
         LoadedShip var3 = VSGameUtilsKt.getLoadedShipManagingPos(var0.level(), var0.blockPosition());
         if (var3 != null && var3.getId() == var2.shipId()) {
            Vector3d var4 = var3.getShipToWorld().transformPosition(new Vector3d(var2.relative()));
            if (!var4.isFinite()) {
               return null;
            }

            for (double var8 : new double[]{0.0, 0.5, 1.0, 1.5, 2.0}) {
               Vec3 var10 = new Vec3(var4.x, var4.y + var8, var4.z);
               AABB var11 = var1.getBoundingBox().move(var10.subtract(var1.position()));
               if (var0.level().noCollision(var1, var11)) {
                  return var10;
               }
            }

            Vec3 var12 = var1.position();
            return Double.isFinite(var12.x) && Double.isFinite(var12.y) && Double.isFinite(var12.z) && !VSGameUtilsKt.isBlockInShipyard(var1.level(), var12)
               ? var12
               : null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static boolean mount(Player var0, Entity var1) {
      LoadedShip var2 = VSGameUtilsKt.getLoadedShipManagingPos(var1.level(), var1.blockPosition());
      boolean var3 = var2 != null && var1 instanceof ChairEntity;
      if (!var3 || !var0.isShiftKeyDown() && var1.getPassengers().isEmpty()) {
         boolean var4 = var3 ? var0.startRiding(var1, true) : var0.startRiding(var1);
         if (!var4) {
            var1.discard();
            LoggerFactory.getLogger("MakiSeatCompat").warn("Chair mount rejected; cleaned pending seat: onShip={}", var3);
         }

         return var4;
      } else {
         return false;
      }
   }

   public static void remove(Entity var0) {
      SeatCompat.Anchor var1 = ANCHORS.remove(var0);
      if (var1 != null && !var0.level().isClientSide()) {
         SeatUtil.removeChairEntity(var0.level(), var1.key());
      }
   }

   private record Anchor(BlockPos key, Long shipId, Vector3d relative) {
   }
}
