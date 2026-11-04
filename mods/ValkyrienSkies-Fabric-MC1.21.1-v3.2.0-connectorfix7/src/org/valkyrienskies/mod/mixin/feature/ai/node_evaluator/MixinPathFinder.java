package org.valkyrienskies.mod.mixin.feature.ai.node_evaluator;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.class_13;
import net.minecraft.class_1308;
import net.minecraft.class_2338;
import net.minecraft.class_4459;
import net.minecraft.class_8;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_13.class)
public class MixinPathFinder {
   @Shadow
   @Final
   private class_8 field_61;

   @WrapOperation(
      method = "findPath(Lnet/minecraft/world/level/PathNavigationRegion;Lnet/minecraft/world/entity/Mob;Ljava/util/Set;FIF)Lnet/minecraft/world/level/pathfinder/Path;",
      at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;collect(Ljava/util/stream/Collector;)Ljava/lang/Object;")
   )
   private Object onCollectPath(
      Stream<class_2338> instance,
      Collector<class_2338, ?, Map<class_4459, class_2338>> arCollector,
      Operation<Map<class_4459, class_2338>> original,
      @Local class_1308 mob
   ) {
      return original.call(new Object[]{instance, Collectors.toMap(blockPos -> {
         class_2338 transformedPos = class_2338.method_49638(VSGameUtilsKt.toWorldCoordinates(mob.method_37908(), blockPos));
         return this.field_61.method_16(transformedPos.method_10263(), transformedPos.method_10264(), transformedPos.method_10260());
      }, Function.identity(), (last, second) -> second)});
   }
}
