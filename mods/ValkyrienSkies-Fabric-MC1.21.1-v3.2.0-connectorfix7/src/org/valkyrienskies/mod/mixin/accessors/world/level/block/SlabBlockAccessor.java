package org.valkyrienskies.mod.mixin.accessors.world.level.block;

import net.minecraft.class_2482;
import net.minecraft.class_265;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_2482.class)
public interface SlabBlockAccessor {
   @Accessor("BOTTOM_AABB")
   static class_265 getBottomAABB() {
      throw new AssertionError();
   }

   @Accessor("TOP_AABB")
   static class_265 getTopAABB() {
      throw new AssertionError();
   }
}
