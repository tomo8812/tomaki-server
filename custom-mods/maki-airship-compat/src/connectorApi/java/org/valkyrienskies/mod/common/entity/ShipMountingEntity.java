package org.valkyrienskies.mod.common.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class ShipMountingEntity extends Entity {
    protected ShipMountingEntity(EntityType<?> type, Level level) { super(type, level); }
    public abstract ShipMountedToData provideShipMountedToData(Entity passenger, Float partialTicks);
}
