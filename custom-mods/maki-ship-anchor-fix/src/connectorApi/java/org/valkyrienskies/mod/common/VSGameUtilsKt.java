package org.valkyrienskies.mod.common;

import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.mod.common.entity.ShipMountedToData;

/**
 * Compile-only view of the VS2 methods used by maki-ship-anchor-fix after
 * Sinytra Connector remaps the raw Fabric JAR from intermediary names to Mojmap.
 *
 * <p>This source belongs to the connectorApi source set and is never packaged.
 * Runtime linkage still targets org.valkyrienskies.mod.common.VSGameUtilsKt
 * supplied by VS2.</p>
 */
public final class VSGameUtilsKt {
    private VSGameUtilsKt() {
    }

    public static ShipMountedToData getShipMountedToData(Entity passenger, Float partialTicks) {
        throw new UnsupportedOperationException("compile-only facade");
    }

    public static LoadedShip getLoadedShipManagingPos(Level level, Vec3i pos) {
        throw new UnsupportedOperationException("compile-only facade");
    }
}
