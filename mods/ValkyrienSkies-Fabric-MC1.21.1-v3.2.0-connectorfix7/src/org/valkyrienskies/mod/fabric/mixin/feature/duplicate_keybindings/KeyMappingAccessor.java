package org.valkyrienskies.mod.fabric.mixin.feature.duplicate_keybindings;

import net.minecraft.class_304;
import net.minecraft.class_3675.class_306;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_304.class)
public interface KeyMappingAccessor {
   @Accessor
   class_306 getKey();

   @Accessor
   int getClickCount();

   @Accessor
   void setClickCount(int var1);
}
