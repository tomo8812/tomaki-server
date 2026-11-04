package org.valkyrienskies.mod.mixin.client.player;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_238;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;

@Mixin(class_1657.class)
public abstract class MixinPlayer extends class_1309 {
   @Shadow
   public abstract int method_7278();

   protected MixinPlayer(class_1299<? extends class_1309> entityType, class_1937 level) {
      super(entityType, level);
   }

   @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getBoundingBox()Lnet/minecraft/world/phys/AABB;"), method = "aiStep")
   private class_238 redirectEntityGetBoundingBox(class_1297 entity, Operation<class_238> getBoundingBox) {
      return VSGameUtilsKt.transformAabbToWorld(entity.method_37908(), (class_238)getBoundingBox.call(new Object[]{entity}));
   }

   @WrapOperation(
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getBoundingBox()Lnet/minecraft/world/phys/AABB;"),
      method = "aiStep"
   )
   private class_238 redirectPlayerGetBoundingBox(class_1657 entity, Operation<class_238> getBoundingBox) {
      return VSGameUtilsKt.transformAabbToWorld(entity.method_37908(), (class_238)getBoundingBox.call(new Object[]{entity}));
   }

   @WrapOperation(
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
      ),
      method = "aiStep"
   )
   private List<class_1297> redirectAiStep(class_1937 instance, class_1297 entity, class_238 aabb, Operation<List<class_1297>> getEntities) {
      return this.method_5854() instanceof ShipMountingEntity ? new ArrayList<>() : (List)getEntities.call(new Object[]{instance, entity, aabb});
   }
}
