package net.satisfy.brewery.core.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.satisfy.brewery.platform.PlatformHelper;

public class HaleyEffect extends MobEffect {
   public HaleyEffect(MobEffectCategory statusEffectCategory, int color) {
      super(statusEffectCategory, color);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (PlatformHelper.isHaleyEffectFlightEnabled() && entity instanceof Player player && !player.level().isClientSide) {
         player.getAbilities().mayfly = true;
         player.getAbilities().flying = true;
         player.onUpdateAbilities();
      }

      return true;
   }

   public void onMobRemoved(LivingEntity entity, int amplifier, RemovalReason reason) {
      if (entity instanceof Player player && !player.level().isClientSide) {
         boolean keep = player.isCreative() || player.isSpectator();
         player.getAbilities().mayfly = keep;
         player.getAbilities().flying = keep && player.getAbilities().flying;
         player.onUpdateAbilities();
      }
   }
}
