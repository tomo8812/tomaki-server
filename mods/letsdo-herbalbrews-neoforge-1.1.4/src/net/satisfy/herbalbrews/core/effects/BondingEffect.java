package net.satisfy.herbalbrews.core.effects;

import java.util.List;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BondingEffect extends MobEffect {
   public BondingEffect() {
      super(MobEffectCategory.BENEFICIAL, 65280);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (entity instanceof Player player && player.isAlive()) {
         Level world = player.level();
         double radius = 10.0;
         List<Player> nearbyPlayers = world.getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(radius), this::isAffectedEntity);
         int playerCount = nearbyPlayers.size() - 1;
         if (playerCount < 0) {
            playerCount = 0;
         }

         int finalAmplifier = this.calculateFinalAmplifier(playerCount);
         if (finalAmplifier > 0) {
            nearbyPlayers.stream().filter(p -> p.isAlive() && p != player && !p.isCreative()).forEach(p -> {
               p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 50, finalAmplifier, false, false, false));
               p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 50, finalAmplifier, false, false, false));
            });
         }
      }

      return super.applyEffectTick(entity, amplifier);
   }

   private int calculateFinalAmplifier(int playerCount) {
      float baseAmplifier = 1.0F;
      float reductionPerPlayer = 0.1F;
      float calculatedAmplifier = baseAmplifier - playerCount * reductionPerPlayer;
      if (calculatedAmplifier < 0.0F) {
         calculatedAmplifier = 0.0F;
      }

      int finalAmplifier = Math.round(calculatedAmplifier);
      return Math.max(0, Math.min(finalAmplifier, 3));
   }

   private boolean isAffectedEntity(Player player) {
      return player.isAlive() && !player.isCreative();
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int j) {
      return true;
   }
}
