package net.satisfy.herbalbrews.core.effects;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FeralEffect extends MobEffect {
   public FeralEffect() {
      super(MobEffectCategory.BENEFICIAL, 65280);
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.withDefaultNamespace("effect.strength"), 3.0, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.MAX_HEALTH, ResourceLocation.withDefaultNamespace("effect.health_boost"), 4.0, Operation.ADD_VALUE);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (entity instanceof Player player && player.isAlive()) {
         Level world = player.level();
         double radius = 10.0;
         List<Player> nearbyPlayers = world.getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(radius));
         int playerCount = nearbyPlayers.size() - 1;
         if (playerCount < 0) {
            playerCount = 0;
         }

         int finalAmplifier = this.calculateFinalAmplifier(playerCount);
         if (finalAmplifier > 0) {
            this.addAttributeModifiers(entity.getAttributes(), finalAmplifier);
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

   public boolean shouldApplyEffectTickThisTick(int duration, int j) {
      return true;
   }
}
