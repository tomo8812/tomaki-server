package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.satisfy.farm_and_charm.core.util.SaturationTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
   @Inject(method = "dropAllDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V", at = @At("TAIL"))
   private void farm_and_charm$injectExtraLoot(ServerLevel serverLevel, DamageSource damageSource, CallbackInfo ci) {
      if (this instanceof Animal animal) {
         EntityType<?> type = animal.getType();
         if (type == EntityType.COW || type == EntityType.PIG || type == EntityType.SHEEP || type == EntityType.CHICKEN) {
            if (animal instanceof SaturationTracker.SaturatedAnimal saturated) {
               saturated.farm_and_charm$getSaturationTracker().dropExtraLoot(animal, damageSource);
            }
         }
      }
   }
}
