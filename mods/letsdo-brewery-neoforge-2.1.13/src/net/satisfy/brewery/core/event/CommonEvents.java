package net.satisfy.brewery.core.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.LootEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.event.events.common.LootEvent.LootTableModificationContext;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.EntityHitResult;
import net.satisfy.brewery.core.registry.ArmorSetRegistry;
import net.satisfy.brewery.core.registry.MobEffectRegistry;
import org.jetbrains.annotations.Nullable;

public class CommonEvents {
   private static final String HALEY_ACTIVE = "brewery:haley_active";
   private static final String HALEY_SET_MAYFLY = "brewery:haley_set_mayfly";
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !CommonEvents.class.desiredAssertionStatus();

   public static void init() {
      LootEvent.MODIFY_LOOT_TABLE.register(CommonEvents::onModifyLootTable);
      PlayerEvent.ATTACK_ENTITY.register(CommonEvents::onPlayerAttack);
      TickEvent.PLAYER_PRE.register(CommonEvents::tickHaley);
      TickEvent.PLAYER_POST.register(CommonEvents::tickHarddrinking);
   }

   private static void tickHarddrinking(Player player) {
      if (!player.level().isClientSide() && ArmorSetRegistry.hasHarddrinking(player)) {
         Holder<MobEffect> drunk = MobEffectRegistry.holder(MobEffectRegistry.DRUNK);
         if (player.hasEffect(drunk)) {
            player.removeEffect(drunk);
         }
      }
   }

   public static void onModifyLootTable(ResourceKey<LootTable> key, LootTableModificationContext context, boolean builtin) {
      LoottableInjector.InjectLoot(key, context);
   }

   public static EventResult onPlayerAttack(Player player, Level level, Entity target, InteractionHand hand, @Nullable EntityHitResult result) {
      if (player.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder((MobEffect)MobEffectRegistry.PROTECTIVETOUCH.get()))) {
         handleProtectiveTouch(level, target);
         return EventResult.interruptFalse();
      } else if (player.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder((MobEffect)MobEffectRegistry.HEALINGTOUCH.get()))) {
         handleHealingTouch(level, target);
         return EventResult.interruptFalse();
      } else if (player.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder((MobEffect)MobEffectRegistry.RENEWINGTOUCH.get()))) {
         handleRenewingTouch(level, target);
         return EventResult.interruptFalse();
      } else if (player.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder((MobEffect)MobEffectRegistry.TOXICTOUCH.get()))) {
         handleToxicTouch(level, target);
         return EventResult.pass();
      } else if (player.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder((MobEffect)MobEffectRegistry.LIGHTNING_STRIKE.get()))) {
         handlelightningStrike(level, target);
         return EventResult.pass();
      } else if (player.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder((MobEffect)MobEffectRegistry.EXPLOSION.get()))) {
         handleExplosiveTouch(level, target, player);
         return EventResult.pass();
      } else {
         return EventResult.pass();
      }
   }

   private static void tickHaley(Player player) {
      if (!player.level().isClientSide) {
         if (!player.isCreative() && !player.isSpectator()) {
            boolean has = player.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder((MobEffect)MobEffectRegistry.HALEY.get()));
            if (has) {
               if (!player.getTags().contains("brewery:haley_active")) {
                  player.addTag("brewery:haley_active");
                  if (!player.getAbilities().mayfly) {
                     player.addTag("brewery:haley_set_mayfly");
                     player.getAbilities().mayfly = true;
                     player.onUpdateAbilities();
                  }
               }
            } else if (player.getTags().contains("brewery:haley_active")) {
               player.removeTag("brewery:haley_active");
               if (player.getTags().contains("brewery:haley_set_mayfly")) {
                  player.removeTag("brewery:haley_set_mayfly");
                  if (player.getAbilities().mayfly || player.getAbilities().flying) {
                     player.getAbilities().mayfly = false;
                     player.getAbilities().flying = false;
                     player.onUpdateAbilities();
                  }
               }
            }
         }
      }
   }

   private static void handleRenewingTouch(Level level, Entity target) {
      if (target instanceof LivingEntity entity) {
         entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1, false, false));
         spawnParticles(level, entity, ParticleTypes.COMPOSTER);
      }
   }

   private static void handleProtectiveTouch(Level level, Entity target) {
      if (target instanceof LivingEntity entity) {
         entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 1, false, false));
         spawnParticles(level, entity, ParticleTypes.GLOW_SQUID_INK);
      }
   }

   private static void handleToxicTouch(Level level, Entity target) {
      if (target instanceof LivingEntity entity) {
         entity.addEffect(new MobEffectInstance(MobEffects.POISON, 300, 2, false, false));
         spawnParticles(level, entity, ParticleTypes.SCRAPE);
      }
   }

   private static void handleHealingTouch(Level level, Entity target) {
      if (target instanceof LivingEntity entity) {
         ((LivingEntity)target).heal(6.0F);
         spawnParticles(level, entity, ParticleTypes.HEART);
      }
   }

   private static void handlelightningStrike(Level level, Entity target) {
      float chance = level.isThundering() ? 0.75F : 0.1F;
      if (level.random.nextFloat() < chance) {
         LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
         lightningBolt.moveTo(target.getX(), target.getY(), target.getZ());
         level.addFreshEntity(lightningBolt);
      }
   }

   private static void handleExplosiveTouch(Level level, Entity target, Player player) {
      if (level.random.nextFloat() < 0.1) {
         Fireball fireball = (Fireball)EntityType.FIREBALL.create(level);
         double dx = target.getX() - player.getX();
         double dy = target.getY() + target.getBbHeight() / 2.0F - (player.getY() + player.getBbHeight() / 2.0F);
         double dz = target.getZ() - player.getZ();
         assert fireball != null;
         fireball.setPos(player.getX(), player.getY() + player.getBbHeight() / 2.0F, player.getZ());
         fireball.shoot(dx, dy, dz, 1.5F, 0.0F);
         level.addFreshEntity(fireball);
      }
   }

   private static void spawnParticles(Level level, Entity target, ParticleOptions particleType) {
      double height = target.getBbHeight();
      double x = target.getX();
      double y = target.getY();
      double z = target.getZ();

      for (double i = 2.5; i >= 1.5; i -= 0.5) {
         level.addParticle(particleType, x, y + height / i, z, 0.0, 0.0, 0.0);
      }
   }
}
