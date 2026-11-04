package org.valkyrienskies.mod.mixin.feature.ai.node_evaluator;

import net.minecraft.class_1308;
import net.minecraft.class_1408;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_1408.class)
public interface PathNavigationMobAccessor {
   @Accessor("mob")
   class_1308 getMob();
}
