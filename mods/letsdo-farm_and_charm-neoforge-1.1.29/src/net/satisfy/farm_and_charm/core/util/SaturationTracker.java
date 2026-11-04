package net.satisfy.farm_and_charm.core.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class SaturationTracker {
   private static final int[] FEEDING_THRESHOLDS = new int[]{5, 10, 15, 20};
   private int level = 0;
   private int foodCounter = 0;
   private long lastFedTick = -1L;
   private int decayDelay = -1;
   private long nextDecayGameTime = -1L;

   private void scheduleNextDecay(Animal animal, long now) {
      this.decayDelay = 2400 + animal.getRandom().nextInt(2400);
      this.nextDecayGameTime = now + this.decayDelay;
      this.lastFedTick = now;
   }

   public void tryFeed(Animal animal, Player player, InteractionHand hand) {
      if (!animal.level().isClientSide) {
         if (this.level < 4) {
            ItemStack stack = player.getItemInHand(hand);
            if (animal.isFood(stack) && !animal.isBaby()) {
               if (!player.getAbilities().instabuild) {
                  stack.shrink(1);
               }

               this.foodCounter++;
               long now = animal.level().getGameTime();
               this.scheduleNextDecay(animal, now);
               if (this.level < 4 && this.foodCounter >= FEEDING_THRESHOLDS[this.level]) {
                  this.level++;
                  this.foodCounter = 0;
               }
            }
         }
      }
   }

   public void tick(Animal animal) {
      if (!animal.level().isClientSide) {
         long now = animal.level().getGameTime();
         if ((animal.getId() + now) % 20L == 0L) {
            if (this.nextDecayGameTime != -1L) {
               if (now >= this.nextDecayGameTime) {
                  if (this.foodCounter > 0) {
                     this.foodCounter--;
                  } else if (this.level > 0) {
                     this.level--;
                     this.foodCounter = FEEDING_THRESHOLDS[Math.max(0, this.level - 1)] - 1;
                  }

                  this.scheduleNextDecay(animal, now);
               }
            }
         }
      }
   }

   public void dropExtraLoot(Animal animal, DamageSource source) {
      if (source != null) {
         if (animal.level() instanceof ServerLevel serverLevel) {
            if (serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
               int bonusRolls = switch (this.level) {
                  case 1 -> 1;
                  case 2 -> 1 + animal.getRandom().nextInt(2);
                  case 3 -> 2 + animal.getRandom().nextInt(2);
                  case 4 -> 3;
                  default -> 0;
               };
               if (bonusRolls != 0) {
                  Entity attacker = source.getEntity();
                  LootParams params = new Builder(serverLevel)
                     .withParameter(LootContextParams.THIS_ENTITY, animal)
                     .withParameter(LootContextParams.ORIGIN, animal.position())
                     .withParameter(LootContextParams.DAMAGE_SOURCE, source)
                     .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, attacker)
                     .withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, attacker instanceof Player p ? p : null)
                     .create(LootContextParamSets.ENTITY);
                  LootTable table = serverLevel.getServer().reloadableRegistries().getLootTable(animal.getLootTable());

                  for (int i = 0; i < bonusRolls; i++) {
                     for (ItemStack stack : table.getRandomItems(params)) {
                        serverLevel.addFreshEntity(new ItemEntity(serverLevel, animal.getX(), animal.getY(), animal.getZ(), stack));
                     }
                  }
               }
            }
         }
      }
   }

   public int level() {
      return this.level;
   }

   public int foodCounter() {
      return this.foodCounter;
   }

   public void feedDirectly(Animal animal, int amount) {
      if (this.level < 4) {
         this.foodCounter += amount;
         long now = animal.level().getGameTime();
         this.scheduleNextDecay(animal, now);

         while (this.level < 4 && this.foodCounter >= FEEDING_THRESHOLDS[this.level]) {
            this.foodCounter = this.foodCounter - FEEDING_THRESHOLDS[this.level];
            this.level++;
         }
      }
   }

   public int getDecayDelay() {
      return this.decayDelay;
   }

   public void setDecayDelay(int delay) {
      this.decayDelay = delay;
      if (this.lastFedTick >= 0L && delay >= 0) {
         this.nextDecayGameTime = this.lastFedTick + delay;
      }
   }

   public long getLastFedTick() {
      return this.lastFedTick;
   }

   public void setLastFedTick(long tick) {
      this.lastFedTick = tick;
      if (this.decayDelay >= 0) {
         this.nextDecayGameTime = tick + this.decayDelay;
      }
   }

   public void setLevel(int level) {
      this.level = Math.max(0, Math.min(4, level));
      if (this.level == 4) {
         this.foodCounter = 0;
      }
   }

   public void setFoodCounter(int count) {
      this.foodCounter = Math.max(0, count);
      if (this.level >= 4) {
         this.foodCounter = 0;
      }
   }

   public void clientSync(int syncedLevel, int syncedFoodCounter) {
      this.level = Math.max(0, Math.min(4, syncedLevel));
      this.foodCounter = Math.max(0, syncedFoodCounter);
      if (this.level == 4) {
         this.foodCounter = 0;
      }
   }

   public interface SaturatedAnimal {
      SaturationTracker farm_and_charm$getSaturationTracker();

      void farm_and_charm$setSaturationTracker(SaturationTracker var1);
   }
}
