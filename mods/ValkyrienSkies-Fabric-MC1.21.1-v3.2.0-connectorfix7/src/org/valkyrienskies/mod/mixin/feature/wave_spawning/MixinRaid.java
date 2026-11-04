package org.valkyrienskies.mod.mixin.feature.wave_spawning;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_2338;
import net.minecraft.class_3218;
import net.minecraft.class_3765;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_3765.class)
public abstract class MixinRaid {
   @Shadow
   @Final
   private class_3218 field_16619;

   @Shadow
   public abstract class_2338 method_16495();

   @Shadow
   protected abstract void method_20509(class_2338 var1);

   @WrapOperation(
      method = "getValidSpawnPos",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;findRandomSpawnPos(II)Lnet/minecraft/core/BlockPos;")
   )
   private class_2338 onFindRandomSpawnPos(class_3765 instance, int i, int j, Operation<class_2338> original) {
      class_2338 originalCenter = this.method_16495();
      this.method_20509(class_2338.method_49638(VSGameUtilsKt.toWorldCoordinates(this.field_16619, originalCenter)));
      class_2338 result = (class_2338)original.call(new Object[]{instance, i, j});
      this.method_20509(originalCenter);
      return result != null ? result : (class_2338)original.call(new Object[]{instance, i, j});
   }

   @WrapOperation(
      method = "tick",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;findRandomSpawnPos(II)Lnet/minecraft/core/BlockPos;")
   )
   private class_2338 onFindRandomSpawnPos2(class_3765 instance, int i, int j, Operation<class_2338> original) {
      class_2338 originalCenter = this.method_16495();
      this.method_20509(class_2338.method_49638(VSGameUtilsKt.toWorldCoordinates(this.field_16619, originalCenter)));
      class_2338 result = (class_2338)original.call(new Object[]{instance, i, j});
      this.method_20509(originalCenter);
      return result != null ? result : (class_2338)original.call(new Object[]{instance, i, j});
   }
}
