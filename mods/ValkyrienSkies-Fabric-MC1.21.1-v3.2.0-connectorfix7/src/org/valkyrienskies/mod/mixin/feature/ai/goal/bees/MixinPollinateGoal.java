package org.valkyrienskies.mod.mixin.feature.ai.goal.bees;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_4466;
import net.minecraft.class_4466.class_4478;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_4478.class)
public abstract class MixinPollinateGoal {
   @Shadow
   @Final
   class_4466 field_20377;
   @Shadow
   @Final
   private Predicate<class_2680> field_20617;
   @Unique
   private class_2338 modifiedBeePosition;

   public MixinPollinateGoal() {
      this.modifiedBeePosition = this.field_20377.method_24515();
   }

   @Shadow
   protected abstract Optional<class_2338> method_22326(Predicate<class_2680> var1, double var2);

   @Inject(method = "findNearbyFlower", at = @At("RETURN"), cancellable = true)
   private void preFindNearbyFlower(CallbackInfoReturnable<Optional<class_2338>> cir) {
      if (((Optional)cir.getReturnValue()).isEmpty()) {
         Optional<class_2338> res = VSGameUtilsKt.transformToNearbyShipsAndWorld(
               this.field_20377.method_37908(),
               this.field_20377.method_24515().method_10263(),
               this.field_20377.method_24515().method_10264(),
               this.field_20377.method_24515().method_10260(),
               5.0
            )
            .stream()
            .flatMap(pos -> {
               this.modifiedBeePosition = class_2338.method_49637(pos.x, pos.y, pos.z);
               return this.method_22326(this.field_20617, 5.0).stream();
            })
            .min(
               Comparator.comparingDouble(
                  pos -> VSGameUtilsKt.squaredDistanceBetweenInclShips(
                     this.field_20377.method_37908(),
                     this.modifiedBeePosition.method_10263(),
                     this.modifiedBeePosition.method_10264(),
                     this.modifiedBeePosition.method_10260(),
                     pos.method_10263(),
                     pos.method_10264(),
                     pos.method_10260()
                  )
               )
            );
         this.modifiedBeePosition = this.field_20377.method_24515();
         cir.setReturnValue(res);
      }
   }

   @WrapOperation(
      method = "findNearestBlock",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Bee;blockPosition()Lnet/minecraft/core/BlockPos;")
   )
   private class_2338 onBlockPosition(class_4466 instance, Operation<class_2338> original) {
      return this.modifiedBeePosition;
   }
}
