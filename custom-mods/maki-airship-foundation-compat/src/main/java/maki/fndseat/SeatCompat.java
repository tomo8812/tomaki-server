package maki.fndseat;

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
    private static final Map<Entity, Anchor> ANCHORS = new WeakHashMap<>();
    private SeatCompat() {}

    public static void remember(Level level, BlockPos key, ChairEntity chair, BlockPos dismount) {
        if (level.isClientSide()) return;
        LoadedShip ship = VSGameUtilsKt.getLoadedShipManagingPos(level, key);
        Vector3d relative = ship == null ? null : ship.getWorldToShip()
            .transformPosition(new Vector3d(dismount.getX() + 0.5, dismount.getY(), dismount.getZ() + 0.5));
        ANCHORS.put(chair, new Anchor(key.immutable(), ship == null ? null : ship.getId(), relative));
    }

    public static Vec3 dismount(Entity chair, LivingEntity passenger) {
        Anchor anchor = ANCHORS.get(chair);
        if (anchor == null || anchor.shipId() == null || chair.level().isClientSide()) return null;
        LoadedShip ship = VSGameUtilsKt.getLoadedShipManagingPos(chair.level(), chair.blockPosition());
        if (ship == null || ship.getId() != anchor.shipId()) return null;
        Vector3d world = ship.getShipToWorld().transformPosition(new Vector3d(anchor.relative()));
        if (!world.isFinite()) return null;
        for (double offset : new double[]{0.0, 0.5, 1.0, 1.5, 2.0}) {
            Vec3 candidate = new Vec3(world.x, world.y + offset, world.z);
            AABB box = passenger.getBoundingBox().move(candidate.subtract(passenger.position()));
            if (chair.level().noCollision(passenger, box)) return candidate;
        }
        Vec3 current = passenger.position();
        return Double.isFinite(current.x) && Double.isFinite(current.y) && Double.isFinite(current.z)
                && !VSGameUtilsKt.isBlockInShipyard(passenger.level(), current) ? current : null;
    }

    public static boolean mount(Player player, Entity chair) {
        LoadedShip ship = VSGameUtilsKt.getLoadedShipManagingPos(chair.level(), chair.blockPosition());
        boolean shipChair = ship != null && chair instanceof ChairEntity;
        if (shipChair && (player.isShiftKeyDown() || !chair.getPassengers().isEmpty())) return false;
        boolean mounted = shipChair ? player.startRiding(chair, true) : player.startRiding(chair);
        if (!mounted) {
            chair.discard();
            LoggerFactory.getLogger("MakiSeatCompat").warn("Chair mount rejected; cleaned pending seat: onShip={}", shipChair);
        }
        return mounted;
    }

    public static void remove(Entity chair) {
        Anchor anchor = ANCHORS.remove(chair);
        if (anchor != null && !chair.level().isClientSide()) SeatUtil.removeChairEntity(chair.level(), anchor.key());
    }

    private record Anchor(BlockPos key, Long shipId, Vector3d relative) {}
}
