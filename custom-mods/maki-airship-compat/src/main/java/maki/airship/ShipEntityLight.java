package maki.airship;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.entity.ShipMountedToData;

public final class ShipEntityLight {
    private ShipEntityLight() {}

    public static int packed(Entity entity, float partialTicks, int packedLight) {
        ShipMountedToData mount = VSGameUtilsKt.getShipMountedToData(entity, partialTicks);
        if (mount == null || !(mount.getShipMountedTo() instanceof ClientShip ship)) return packedLight;
        Vec3 probe = entity.getLightProbePosition(partialTicks);
        Vector3d local = ship.getRenderTransform().getWorldToShip()
            .transformPosition(new Vector3d(probe.x, probe.y, probe.z));
        if (!local.isFinite()) return packedLight;
        int deck = entity.level().getBrightness(LightLayer.BLOCK, BlockPos.containing(local.x, local.y, local.z));
        int current = packedLight >>> 4 & 15;
        return deck > current ? packedLight & -241 | (deck & 15) << 4 : packedLight;
    }
}
