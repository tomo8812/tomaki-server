package net.satisfy.farm_and_charm.core.effect;

import java.util.ArrayList;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FarmersBlessingEffect extends MobEffect {
   public FarmersBlessingEffect() {
      super(MobEffectCategory.BENEFICIAL, -14513374);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (!entity.level().isClientSide()) {
         ArrayList<Holder<MobEffect>> toRemove = new ArrayList<>();
         entity.getActiveEffectsMap().forEach((effectx, instance) -> {
            if (((MobEffect)effectx.value()).getCategory() == MobEffectCategory.HARMFUL) {
               toRemove.add(effectx);
            }
         });

         for (Holder<MobEffect> effect : toRemove) {
            entity.removeEffect(effect);
         }
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int i, int j) {
      return true;
   }

   public boolean isInstantenous() {
      return false;
   }
}
