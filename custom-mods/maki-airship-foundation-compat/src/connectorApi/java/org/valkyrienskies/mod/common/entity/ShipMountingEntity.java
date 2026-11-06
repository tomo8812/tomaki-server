package org.valkyrienskies.mod.common.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class ShipMountingEntity extends Entity {
    protected ShipMountingEntity(EntityType<?> type, Level level) { super(type, level); }
    public static ShipMountingEntity spawnPassengerSeat(
            ServerLevel level, double worldX, double worldY, double worldZ,
            float yaw, float pitch, long shipId, double relX, double relY, double relZ, boolean fadePrompt) {
        throw new UnsupportedOperationException();
    }
    @Override public abstract Vec3 getDismountLocationForPassenger(LivingEntity passenger);
}
