package org.valkyrienskies.mod.mixin.accessors.world.level.block;

import net.minecraft.class_2510;
import net.minecraft.class_265;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_2510.class)
public interface StairBlockAccessor {
   @Accessor("TOP_SHAPES")
   static class_265[] getTopShapes() {
      throw new AssertionError();
   }

   @Accessor("BOTTOM_SHAPES")
   static class_265[] getBottomShapes() {
      throw new AssertionError();
   }
}
