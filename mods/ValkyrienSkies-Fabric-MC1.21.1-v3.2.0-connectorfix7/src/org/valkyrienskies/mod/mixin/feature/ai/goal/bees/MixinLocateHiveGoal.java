package org.valkyrienskies.mod.mixin.feature.ai.goal.bees;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.Comparator;
import java.util.stream.Stream;
import net.minecraft.class_2338;
import net.minecraft.class_4466;
import net.minecraft.class_4466.class_4476;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_4476.class)
public class MixinLocateHiveGoal {
   @Shadow
   @Final
   class_4466 field_20375;

   @WrapOperation(
      method = "findNearbyHivesWithSpace",
      at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;sorted(Ljava/util/Comparator;)Ljava/util/stream/Stream;")
   )
   private Stream<class_2338> onComparingDouble(
      Stream<class_2338> instance, Comparator<class_2338> comparator, Operation<Stream<class_2338>> original, @Local class_2338 blockPos
   ) {
      return (Stream<class_2338>)original.call(
         new Object[]{
            instance,
            Comparator.comparingDouble(
               pos -> class_2338.method_49638(VSGameUtilsKt.toWorldCoordinates(this.field_20375.method_37908(), (class_2338)pos)).method_10262(blockPos)
            )
         }
      );
   }
}
