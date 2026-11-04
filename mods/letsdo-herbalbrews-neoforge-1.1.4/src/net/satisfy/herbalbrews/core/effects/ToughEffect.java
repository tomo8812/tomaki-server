package net.satisfy.herbalbrews.core.effects;

import java.util.List;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ToughEffect extends MobEffect {
   public ToughEffect() {
      super(MobEffectCategory.NEUTRAL, 65280);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (entity instanceof Player player && this.isAffectedEntity(player)) {
         Level world = player.level();
         double radius = 10.0;
         List<Player> nearbyPlayers = world.getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(radius));
         int playerCount = nearbyPlayers.size() - 1;
         if (playerCount < 0) {
            playerCount = 0;
         }

         int finalAmplifier = this.calculateFinalAmplifier(playerCount);
         if (player.getHealth() <= 8.0F && finalAmplifier > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, finalAmplifier, false, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, finalAmplifier, false, false, false));
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
      return duration % 100 == 0;
   }
}
