package net.satisfy.foundation.seat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class ChairEntity extends Entity {
    public ChairEntity(EntityType<?> type, Level level) { super(type, level); }
}
