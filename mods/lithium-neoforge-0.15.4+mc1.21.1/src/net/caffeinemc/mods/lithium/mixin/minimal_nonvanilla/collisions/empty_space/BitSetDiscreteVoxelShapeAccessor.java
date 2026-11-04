package net.caffeinemc.mods.lithium.mixin.minimal_nonvanilla.collisions.empty_space;

import java.util.BitSet;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BitSetDiscreteVoxelShape.class)
public interface BitSetDiscreteVoxelShapeAccessor {
   @Accessor
   BitSet getStorage();
}
