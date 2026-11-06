package maki.airship;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public final class SleepCompat {
    private SleepCompat() {}

    private static boolean finite(Vec3 value) {
        return Double.isFinite(value.x) && Double.isFinite(value.y) && Double.isFinite(value.z);
    }

    private static boolean shipyard(LivingEntity entity, Vec3 position) {
        return VSGameUtilsKt.isBlockInShipyard(entity.level(), position);
    }

    public static Vec3 bedPoint(BlockPos pos) {
        return new Vec3(pos.getX() + 0.5, pos.getY() + 0.6875, pos.getZ() + 0.5);
    }

    public static boolean anchored(LivingEntity entity) {
        return entity.isSleeping()
            && entity.getSleepingPos().map(pos -> shipyard(entity, bedPoint(pos))).orElse(false);
    }

    public static void follow(LivingEntity entity) {
        if (!anchored(entity) || entity.isPassenger()) return;
        BlockPos bed = entity.getSleepingPos().orElseThrow();
        LoadedShip ship = VSGameUtilsKt.getLoadedShipManagingPos(entity.level(), bed);
        if (ship == null) return;
        Vec3 local = bedPoint(bed);
        Vector3d world = ship.getShipToWorld().transformPosition(new Vector3d(local.x, local.y, local.z));
        if (!world.isFinite()) return;
        entity.setPos(world.x, world.y, world.z);
        entity.setDeltaMovement(Vec3.ZERO);
        entity.fallDistance = 0.0F;
    }

    public static WakeAnchor beforeWake(LivingEntity entity) {
        BlockPos bed = entity.getSleepingPos().orElse(null);
        if (bed == null || !shipyard(entity, bedPoint(bed))) return null;
        LoadedShip ship = VSGameUtilsKt.getLoadedShipManagingPos(entity.level(), bed);
        return new WakeAnchor(bed, ship == null ? null : ship.getId(), entity.position());
    }

    public static void afterWake(LivingEntity entity, WakeAnchor anchor) {
        if (anchor == null || !shipyard(entity, entity.position())) return;
        LoadedShip ship = VSGameUtilsKt.getLoadedShipManagingPos(entity.level(), anchor.bed());
        if (ship != null && anchor.shipId() != null && ship.getId() == anchor.shipId()) {
            Vec3 current = entity.position();
            Vector3d world = ship.getShipToWorld().transformPosition(new Vector3d(current.x, current.y, current.z));
            if (world.isFinite()) {
                for (double offset : new double[]{0.0, 0.5, 1.0, 1.5, 2.0}) {
                    Vec3 candidate = new Vec3(world.x, world.y + offset, world.z);
                    if (entity.level().noCollision(entity, entity.getBoundingBox().move(candidate.subtract(entity.position())))) {
                        double yaw = Math.toRadians(entity.getYRot());
                        Vector3d facing = ship.getShipToWorld()
                            .transformDirection(new Vector3d(-Math.sin(yaw), 0.0, Math.cos(yaw)));
                        entity.setPos(candidate.x, candidate.y, candidate.z);
                        if (facing.isFinite() && facing.x * facing.x + facing.z * facing.z > 1.0E-12) {
                            entity.setYRot((float) Math.toDegrees(Math.atan2(-facing.x, facing.z)));
                        }
                        entity.setDeltaMovement(Vec3.ZERO);
                        entity.fallDistance = 0.0F;
                        return;
                    }
                }
            }
        }
        Vec3 previous = anchor.previous();
        if (finite(previous) && !shipyard(entity, previous)) {
            entity.setPos(previous.x, previous.y, previous.z);
            entity.setDeltaMovement(Vec3.ZERO);
            entity.fallDistance = 0.0F;
        }
    }

    public record WakeAnchor(BlockPos bed, Long shipId, Vec3 previous) {}
}
