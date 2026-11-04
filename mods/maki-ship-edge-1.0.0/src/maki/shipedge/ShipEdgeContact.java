package maki.shipedge;

import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.internal.world.VsiShipWorld;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.EntityDragger;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

public final class ShipEdgeContact {
   private ShipEdgeContact() {
   }

   public static Vec3 query(Player var0, Vec3 var1, MoverType var2, boolean var3) {
      if (Double.isFinite(var1.x)
         && Double.isFinite(var1.y)
         && Double.isFinite(var1.z)
         && !(var1.y > 0.0)
         && (var2 == MoverType.SELF || var2 == MoverType.PLAYER)
         && var0.onGround()
         && !var0.isPassenger()
         && !var0.getAbilities().flying
         && var3
         && (var1.x != 0.0 || var1.z != 0.0)) {
         if (!(var0 instanceof IEntityDraggingInformationProvider var4)) {
            return null;
         } else {
            EntityDraggingInformation var9 = var4.getDraggingInformation();
            if (var9.isEntityBeingDraggedByAShip() && var9.getLastShipStoodOn() != null && var9.getTicksSinceStoodOnShip() == 1) {
               VsiShipWorld var6 = VSGameUtilsKt.getShipObjectWorld(var0.level());
               if (var6 == null) {
                  return null;
               }

               Ship var7 = var6.getLoadedShips().getById(var9.getLastShipStoodOn());
               if (var7 == null) {
                  return null;
               }

               Vec3 var8 = EntityDragger.backOff(var1, var7, var0, var0.level());
               return Double.isFinite(var8.x) && Double.isFinite(var8.y) && Double.isFinite(var8.z) ? var8 : null;
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }
}
