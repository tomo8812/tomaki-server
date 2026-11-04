package org.valkyrienskies.mod.mixin.feature.ai.goal;

import net.minecraft.class_5819;
import net.minecraft.class_4096.class_5397;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_5397.class)
public interface JitteredLinearRetryAccessor {
   @Invoker("<init>")
   static class_5397 create(class_5819 randomSource, long l) {
      throw new AssertionError();
   }
}
