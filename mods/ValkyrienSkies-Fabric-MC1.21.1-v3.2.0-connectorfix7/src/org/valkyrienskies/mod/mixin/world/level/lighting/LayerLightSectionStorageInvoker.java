package org.valkyrienskies.mod.mixin.world.level.lighting;

import net.minecraft.class_3560;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_3560.class)
public interface LayerLightSectionStorageInvoker {
   @Invoker("swapSectionMap")
   void vs$invokeSwapSectionMap();
}
