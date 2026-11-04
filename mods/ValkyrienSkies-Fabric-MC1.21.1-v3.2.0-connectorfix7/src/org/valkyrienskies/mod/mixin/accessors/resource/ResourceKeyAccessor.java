package org.valkyrienskies.mod.mixin.accessors.resource;

import net.minecraft.class_2960;
import net.minecraft.class_5321;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_5321.class)
public interface ResourceKeyAccessor {
   @Accessor
   class_2960 getRegistryName();

   @Invoker
   static <T> class_5321<T> callCreate(class_2960 parent, class_2960 location) {
      throw new AssertionError();
   }
}
