package net.satisfy.farm_and_charm.core.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.satisfy.farm_and_charm.platform.PlatformHelper;

public class SustenanceEffect extends MobEffect {
   public SustenanceEffect() {
      super(MobEffectCategory.BENEFICIAL, 0);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (!entity.level().isClientSide() && entity instanceof Player player) {
         int healAmount = PlatformHelper.getSustenanceEffectHealAmount();
         int foodIncrement = PlatformHelper.getSustenanceEffectFoodIncrement();
         FoodData foodData = player.getFoodData();
         if (foodData.getFoodLevel() >= 20) {
            player.heal(healAmount);
         } else {
            foodData.setFoodLevel(Math.min(foodData.getFoodLevel() + foodIncrement, 20));
         }
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      int interval = Math.max(1, PlatformHelper.getSustenanceEffectInterval());
      return duration % interval == 0;
   }
}
