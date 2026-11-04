package org.valkyrienskies.mod.mixin.accessors.client.render;

import net.minecraft.class_761;
import net.minecraft.class_769;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_761.class)
public interface LevelRendererAccessor {
   @Accessor
   class_769 getViewArea();
}
