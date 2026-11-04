package net.caffeinemc.mods.lithium.mixin.ai.useless_sensors.parent_animal_sensor;

import java.util.Optional;
import net.caffeinemc.mods.lithium.common.ai.brain.SensorHelper;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AgeableMob.class)
public abstract class AgeableMobMixin extends LivingEntity {
   @Shadow
   public abstract boolean isBaby();

   protected AgeableMobMixin(EntityType<? extends LivingEntity> entityType, Level world) {
      super(entityType, world);
   }

   @Inject(
      method = "onSyncedDataUpdated",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AgeableMob;refreshDimensions()V"),
      require = 1,
      allow = 1
   )
   private void handleParentSensor(EntityDataAccessor<?> data, CallbackInfo ci) {
      if (!this.level().isClientSide()) {
         if (this.isBaby()) {
            SensorHelper.enableSensor((AgeableMobMixin & AgeableMob)this, SensorType.NEAREST_ADULT, true);
         } else {
            SensorHelper.disableSensor((AgeableMob)this, SensorType.NEAREST_ADULT);
            if (this.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_ADULT)) {
               this.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT, Optional.empty());
            }
         }
      }
   }
}
