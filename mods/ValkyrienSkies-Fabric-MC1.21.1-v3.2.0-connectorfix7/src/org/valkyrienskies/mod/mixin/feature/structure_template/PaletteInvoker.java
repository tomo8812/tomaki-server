package org.valkyrienskies.mod.mixin.feature.structure_template;

import java.util.List;
import net.minecraft.class_3499.class_3501;
import net.minecraft.class_3499.class_5162;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_5162.class)
public interface PaletteInvoker {
   @Invoker("<init>")
   static class_5162 invokeInit(List<class_3501> blocks) {
      throw new AssertionError();
   }
}
