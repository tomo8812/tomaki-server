package maki.shipedge;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.EntityDragger;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

public final class ShipEdgeContact {
   private ShipEdgeContact() {
   }

   public static Vec3 query(Player player, Vec3 movement, MoverType mover, boolean stayingOnSurface) {
      if (Double.isFinite(movement.x)
         && Double.isFinite(movement.y)
         && Double.isFinite(movement.z)
         && !(movement.y > 0.0)
         && (mover == MoverType.SELF || mover == MoverType.PLAYER)
         && player.onGround()
         && !player.isPassenger()
         && !player.getAbilities().flying
         && stayingOnSurface
         && (movement.x != 0.0 || movement.z != 0.0)) {
         if (!(player instanceof IEntityDraggingInformationProvider provider)) {
            return null;
         } else {
            EntityDraggingInformation dragging = provider.getDraggingInformation();
            if (dragging.isEntityBeingDraggedByAShip() && dragging.getLastShipStoodOn() != null && dragging.getTicksSinceStoodOnShip() == 1) {
               Vector3dc relative = dragging.getRelativePositionOnShip();
               if (relative == null) {
                  return null;
               } else {
                  Ship ship = VSGameUtilsKt.getLoadedShipManagingPos(player.level(), BlockPos.containing(relative.x(), relative.y(), relative.z()));
                  if (ship != null && ship.getId() == dragging.getLastShipStoodOn()) {
                     Vec3 adjusted = EntityDragger.backOff(movement, ship, player, player.level());
                     return Double.isFinite(adjusted.x) && Double.isFinite(adjusted.y) && Double.isFinite(adjusted.z) ? adjusted : null;
                  } else {
                     return null;
                  }
               }
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }
}
