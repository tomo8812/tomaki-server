package org.valkyrienskies.mod.mixin.feature.ai.node_evaluator;

import net.minecraft.class_1937;
import net.minecraft.class_1950;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_1950.class)
public interface PathNavigationRegionAccessor {
   @Accessor("level")
   class_1937 getLevel();
}
