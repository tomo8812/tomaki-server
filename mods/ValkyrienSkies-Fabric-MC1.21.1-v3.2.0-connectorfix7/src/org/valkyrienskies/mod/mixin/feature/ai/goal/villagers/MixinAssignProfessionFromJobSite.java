package org.valkyrienskies.mod.mixin.feature.ai.goal.villagers;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.class_1646;
import net.minecraft.class_2338;
import net.minecraft.class_2374;
import net.minecraft.class_4114;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_4114.class)
public class MixinAssignProfessionFromJobSite {
   @WrapOperation(
      method = "method_46890(Lnet/minecraft/world/entity/ai/behavior/declarative/BehaviorBuilder$Instance;Lnet/minecraft/world/entity/ai/behavior/declarative/MemoryAccessor;Lnet/minecraft/world/entity/ai/behavior/declarative/MemoryAccessor;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;closerToCenterThan(Lnet/minecraft/core/Position;D)Z")
   )
   private static boolean onCloserToCenterThan(class_2338 instance, class_2374 position, double v, Operation<Boolean> original, @Local class_1646 villager) {
      return (Boolean)original.call(new Object[]{class_2338.method_49638(VSGameUtilsKt.toWorldCoordinates(villager.method_37908(), instance)), position, v});
   }
}
