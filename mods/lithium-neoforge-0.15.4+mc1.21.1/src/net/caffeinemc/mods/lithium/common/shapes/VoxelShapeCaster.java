package net.caffeinemc.mods.lithium.common.shapes;

import net.minecraft.world.phys.AABB;

public interface VoxelShapeCaster {
   boolean intersects(AABB var1, double var2, double var4, double var6);
}
