package net.satisfy.farm_and_charm.core.effect;

import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.satisfy.farm_and_charm.platform.PlatformHelper;

public class FeastEffect extends MobEffect {
   public FeastEffect() {
      super(MobEffectCategory.BENEFICIAL, -7667712);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      int satiationInterval = Math.max(1, PlatformHelper.getFeastEffectSatiationInterval());
      int sustenanceInterval = Math.max(1, PlatformHelper.getFeastEffectSustenanceInterval());
      int healAmount = PlatformHelper.getFeastEffectHealAmount();
      if (!entity.level().isClientSide() && entity instanceof Player player) {
         int duration = this.getDuration(entity, this);
         if (duration % satiationInterval == 0
            && !player.getFoodData().needsFood()
            && !player.hasEffect(MobEffects.REGENERATION)
            && player.getFoodData().getSaturationLevel() > 0.0F) {
            player.heal(healAmount + amplifier);
         }

         if (duration % sustenanceInterval == 0) {
            FoodData foodData = player.getFoodData();
            if (foodData.getFoodLevel() >= 20) {
               player.heal(healAmount);
            } else {
               foodData.setFoodLevel(Math.min(foodData.getFoodLevel() + 1, 20));
            }
         }
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      int satiationInterval = Math.max(1, PlatformHelper.getFeastEffectSatiationInterval());
      int sustenanceInterval = Math.max(1, PlatformHelper.getFeastEffectSustenanceInterval());
      return duration % satiationInterval == 0 || duration % sustenanceInterval == 0;
   }

   private int getDuration(LivingEntity entity, MobEffect effect) {
      return entity.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect)) != null
         ? Objects.requireNonNull(entity.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect))).getDuration()
         : 0;
   }
}
