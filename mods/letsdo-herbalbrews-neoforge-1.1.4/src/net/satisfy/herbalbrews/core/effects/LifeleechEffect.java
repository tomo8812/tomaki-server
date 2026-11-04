package net.satisfy.herbalbrews.core.effects;

import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class LifeleechEffect extends MobEffect {
   private static final int RADIUS = 10;
   private static final int TRANSFER_INTERVAL = 160;

   public LifeleechEffect() {
      super(MobEffectCategory.BENEFICIAL, 65280);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (entity instanceof Player player && player.isAlive()) {
         Level world = player.level();
         List<Player> nearbyPlayers = world.getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(10.0), this::isAffectedEntity);
         int playerCount = nearbyPlayers.size() - 1;
         playerCount = Math.max(playerCount, 0);
         int finalAmplifier = this.calculateFinalAmplifier(playerCount);
         if (finalAmplifier > 0) {
            nearbyPlayers.stream().filter(p -> p.isAlive() && p != player && !p.isCreative()).forEach(p -> this.applyEffect(p, finalAmplifier));
         }

         if (this.isTransferTime(entity)) {
            this.transferHealth(player);
         }
      }

      return super.applyEffectTick(entity, amplifier);
   }

   private int calculateFinalAmplifier(int playerCount) {
      float baseAmplifier = 1.0F;
      float reductionPerPlayer = 0.1F;
      float calculatedAmplifier = baseAmplifier - playerCount * reductionPerPlayer;
      calculatedAmplifier = Math.max(calculatedAmplifier, 0.0F);
      return Math.min(Math.round(calculatedAmplifier), 3);
   }

   private void applyEffect(Player player, int amplifier) {
      player.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(this), 200, amplifier, false, false, false));
   }

   private boolean isAffectedEntity(Player player) {
      return player.isAlive() && !player.isCreative();
   }

   private boolean isTransferTime(LivingEntity entity) {
      return entity.tickCount % 160 == 0;
   }

   private void transferHealth(Player player) {
      Level world = player.level();

      for (LivingEntity enemy : world.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(10.0), this::isEnemy)) {
         if (enemy.getHealth() > 0.0F) {
            enemy.setHealth(Math.max(enemy.getHealth() - 1.0F, 0.0F));
            player.setHealth(Math.min(player.getHealth() + 1.0F, player.getMaxHealth()));
         }
      }
   }

   private boolean isEnemy(LivingEntity entity) {
      return entity instanceof Monster;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int j) {
      return true;
   }
}
