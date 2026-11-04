package org.valkyrienskies.mod.mixin.accessors.client.render;

import net.minecraft.class_4583;
import net.minecraft.class_4588;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_4583.class)
public interface OverlayVertexConsumerAccessor {
   @Accessor("delegate")
   class_4588 getDelegate();
}
