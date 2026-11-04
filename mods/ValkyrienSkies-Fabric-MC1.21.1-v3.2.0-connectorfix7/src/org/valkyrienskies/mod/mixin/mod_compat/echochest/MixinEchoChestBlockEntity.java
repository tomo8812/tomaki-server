package org.valkyrienskies.mod.mixin.mod_compat.echochest;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "fuzs.echochest.world.level.block.entity.EchoChestBlockEntity", remap = false)
public class MixinEchoChestBlockEntity {
   @WrapMethod(method = "isOccluded")
   private static boolean adjustOcclusionForWorldPosition(class_1937 level, class_243 pos1, class_243 pos2, Operation<Boolean> original) {
      return MixinVibrationSystemAccessor.isOccluded(level, pos1, pos2);
   }
}
