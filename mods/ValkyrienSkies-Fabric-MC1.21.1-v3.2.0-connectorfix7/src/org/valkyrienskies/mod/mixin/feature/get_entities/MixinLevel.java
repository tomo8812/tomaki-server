package org.valkyrienskies.mod.mixin.feature.get_entities;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_238;
import net.minecraft.class_5575;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.util.RateLimiter;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.util.BugFixUtil;

@Mixin(class_1937.class)
public class MixinLevel {
   @Unique
   private static final Logger LOGGER = LogManager.getLogger("VS2 Get Entities Mixin");
   @Unique
   private static final RateLimiter LIMITER = new RateLimiter(Duration.ofSeconds(5L));

   @ModifyVariable(
      method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
      at = @At("HEAD"),
      argsOnly = true
   )
   private class_238 moveAABB1(class_238 aabb) {
      return VSGameUtilsKt.transformAabbToWorld(class_1937.class.cast(this), aabb);
   }

   @ModifyVariable(
      method = "getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
      at = @At("HEAD"),
      argsOnly = true
   )
   private class_238 moveAABB2(class_238 aabb) {
      return VSGameUtilsKt.transformAabbToWorld(class_1937.class.cast(this), aabb);
   }

   @Inject(
      method = "getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
      at = @At("HEAD"),
      cancellable = true
   )
   private <T extends class_1297> void check1(
      class_5575<class_1297, T> entityTypeTest, class_238 area, Predicate<? super T> predicate, CallbackInfoReturnable<List<T>> cir
   ) {
      if (BugFixUtil.INSTANCE.isCollisionBoxTooBig(area)) {
         cir.setReturnValue(Collections.emptyList());
         cir.cancel();
      }
   }

   @Inject(
      method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
      at = @At("HEAD"),
      cancellable = true
   )
   private <T extends class_1297> void check2(
      @Nullable class_1297 entity, class_238 area, Predicate<? super class_1297> predicate, CallbackInfoReturnable<List<class_1297>> cir
   ) {
      if (BugFixUtil.INSTANCE.isCollisionBoxTooBig(area)) {
         cir.setReturnValue(Collections.emptyList());
         cir.cancel();
      }
   }
}
