package maki.shipedge;

import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.EntityDragger;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

/** Pure movement query: never applies movement, changes flags, or sends packets. */
public final class ShipEdgeContact {
    private ShipEdgeContact() {}
    public static Vec3 query(Player player, Vec3 movement, MoverType mover, boolean stayingOnSurface) {
        if (!Double.isFinite(movement.x) || !Double.isFinite(movement.y) || !Double.isFinite(movement.z)
                || movement.y>0 || (mover!=MoverType.SELF && mover!=MoverType.PLAYER)
                || !player.onGround() || player.isPassenger() || player.getAbilities().flying
                || !stayingOnSurface || (movement.x==0 && movement.z==0)) return null;
        if (!((Object)player instanceof IEntityDraggingInformationProvider provider)) return null;
        var dragging=provider.getDraggingInformation();
        // Age zero stays with VS; older contact is never extended after leaving a ship.
        if (!dragging.isEntityBeingDraggedByAShip() || dragging.getLastShipStoodOn()==null
                || dragging.getTicksSinceStoodOnShip()!=1) return null;
        var world=VSGameUtilsKt.getShipObjectWorld(player.level());
        if (world==null) return null;
        Ship ship=world.getLoadedShips().getById(dragging.getLastShipStoodOn());
        if (ship==null) return null;
        Vec3 adjusted=EntityDragger.backOff(movement, ship, player, player.level());
        return Double.isFinite(adjusted.x) && Double.isFinite(adjusted.y) && Double.isFinite(adjusted.z) ? adjusted : null;
    }
}
