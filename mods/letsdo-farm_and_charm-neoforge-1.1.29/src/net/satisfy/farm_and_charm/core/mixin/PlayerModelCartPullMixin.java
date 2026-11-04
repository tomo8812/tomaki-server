package net.satisfy.farm_and_charm.core.mixin;

import java.util.List;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.satisfy.farm_and_charm.core.entity.AbstractCartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class PlayerModelCartPullMixin<T extends LivingEntity> {
   @Shadow
   public ModelPart leftArm;
   @Shadow
   public ModelPart rightArm;

   @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
   private void farm_and_charm_lockArmsWhilePulling(
      T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callbackInfo
   ) {
      if (entity.level() != null && entity.level().isClientSide()) {
         List<AbstractCartEntity> carts = entity.level().getEntitiesOfClass(AbstractCartEntity.class, entity.getBoundingBox().inflate(64.0));
         boolean isPulling = false;

         for (AbstractCartEntity cart : carts) {
            Entity puller = cart.getPulling();
            if (puller == entity) {
               isPulling = true;
               break;
            }
         }

         if (isPulling) {
            this.leftArm.xRot = 0.0F;
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.rightArm.xRot = 0.0F;
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = 0.0F;
         }
      }
   }
}
