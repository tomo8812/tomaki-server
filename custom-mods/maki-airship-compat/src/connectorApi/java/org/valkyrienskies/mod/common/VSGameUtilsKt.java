package org.valkyrienskies.mod.common;

import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.entity.ShipMountedToData;

public final class VSGameUtilsKt {
    private VSGameUtilsKt() {}
    public static ShipMountedToData getShipMountedToData(Entity entity, Float partialTicks) { throw new UnsupportedOperationException(); }
    public static LoadedShip getLoadedShipManagingPos(Level level, Vec3i pos) { throw new UnsupportedOperationException(); }
    public static boolean isBlockInShipyard(Level level, Vec3 pos) { throw new UnsupportedOperationException(); }
    public static Ship getShipManaging(Entity entity) { throw new UnsupportedOperationException(); }
}
