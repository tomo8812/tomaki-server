package org.valkyrienskies.mod.mixin.accessors.client.render;

import net.minecraft.class_2338;
import net.minecraft.class_769;
import net.minecraft.class_846.class_851;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_769.class)
public interface ViewAreaAccessor {
   @Invoker("getRenderSectionAt")
   class_851 callGetRenderSectionAt(class_2338 var1);
}
