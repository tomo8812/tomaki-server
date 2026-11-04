package org.valkyrienskies.mod.mixin.mod_compat.echochest;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.Optional;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_3218;
import net.minecraft.class_5707;
import net.minecraft.class_5714;
import net.minecraft.class_5716;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Pseudo
@Mixin(targets = "fuzs.echochest.world.level.block.entity.EchoChestListener")
public abstract class MixinEchoChestListener implements class_5714 {
   @WrapOperation(
      method = {"handleGameEvent", "method_32947", "m_214068_"},
      require = 1,
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;distanceTo(Lnet/minecraft/world/phys/Vec3;)D")
   )
   private double replaceTravelTime(class_243 instance, class_243 dest, Operation<Double> original, @Local(argsOnly = true) class_3218 level) {
      return (Double)original.call(new Object[]{instance, VSGameUtilsKt.toWorldCoordinates(level, dest)});
   }

   @ModifyArg(
      method = {"handleGameEvent", "method_32947", "m_214068_"},
      require = 1,
      at = @At(
         value = "INVOKE",
         target = "net/minecraft/core/particles/VibrationParticleOption.<init>(Lnet/minecraft/world/level/gameevent/PositionSource;I)V"
      )
   )
   public class_5716 replacePositionSource(class_5716 source, @Local(argsOnly = true) class_3218 level, @Local(argsOnly = true) class_243 pos) {
      if (level != null) {
         Optional<class_243> optPos = source.method_32956(level);
         if (optPos.isPresent()) {
            class_243 worldPos = VSGameUtilsKt.toWorldCoordinates(level, optPos.get());
            return new class_5707(class_2338.method_49638(worldPos));
         }
      }

      return source;
   }
}
