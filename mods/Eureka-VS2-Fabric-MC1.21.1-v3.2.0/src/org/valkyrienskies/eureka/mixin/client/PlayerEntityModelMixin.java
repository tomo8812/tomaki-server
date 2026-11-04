package org.valkyrienskies.eureka.mixin.client;

import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_572;
import net.minecraft.class_591;
import net.minecraft.class_630;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;

@Mixin(class_591.class)
public abstract class PlayerEntityModelMixin<T extends class_1309> extends class_572<T> {
   @Shadow
   public class_630 field_3484;
   @Shadow
   public class_630 field_3486;
   @Shadow
   public class_630 field_3482;
   @Shadow
   public class_630 field_3479;

   public PlayerEntityModelMixin(class_630 model) {
      super(model);
   }

   @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("HEAD"))
   public void vs$standAtHelmHead(T livingEntity, float swing, float g, float tick, float i, float j, CallbackInfo info) {
      if (this.vs$isStandingHelmRider(livingEntity)) {
         this.field_3449 = false;
      }
   }

   @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
   public void vs$standAtHelmTail(T livingEntity, float swing, float g, float tick, float i, float j, CallbackInfo info) {
      if (this.vs$isStandingHelmRider(livingEntity)) {
         this.field_3392.field_3665 = true;
         this.field_3397.field_3665 = true;
         this.field_3392.field_3654 = 0.0F;
         this.field_3392.field_3675 = 0.0F;
         this.field_3392.field_3674 = 0.0F;
         this.field_3397.field_3654 = 0.0F;
         this.field_3397.field_3675 = 0.0F;
         this.field_3397.field_3674 = 0.0F;
         this.field_3401.field_3654 = -1.4F;
         this.field_3401.field_3675 = 0.0F;
         this.field_3401.field_3674 = 0.0F;
         this.field_27433.field_3654 = -1.4F;
         this.field_27433.field_3675 = 0.0F;
         this.field_27433.field_3674 = 0.0F;
         this.field_3484.method_17138(this.field_27433);
         this.field_3486.method_17138(this.field_3401);
         this.field_3482.method_17138(this.field_3397);
         this.field_3479.method_17138(this.field_3392);
      }
   }

   @Unique
   private boolean vs$isStandingHelmRider(T livingEntity) {
      class_1297 vehicle = livingEntity.method_5854();
      return vehicle instanceof ShipMountingEntity && !((ShipMountingEntity)vehicle).vs$isPassengerSeat();
   }
}
