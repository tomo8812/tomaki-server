package fuzs.goldenagecombat.handler;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedDouble;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class ClassicCombatHandler {
   public static EventResult onProjectileImpact(Projectile projectile, HitResult hitResult) {
      if (!((ServerConfig)GoldenAgeCombat.CONFIG.get(ServerConfig.class)).weakAttacksKnockBackPlayers) {
         return EventResult.PASS;
      }

      if (hitResult.getType() == Type.ENTITY && projectile.getOwner() == null) {
         projectile.setOwner(projectile);
      }

      return EventResult.PASS;
   }

   public static void onUseItemFinish(LivingEntity entity, MutableValue<ItemStack> stack, int remainingUseDuration, ItemStack originalUseItem) {
      if (((ServerConfig)GoldenAgeCombat.CONFIG.get(ServerConfig.class)).goldenAppleEffects) {
         if (((ItemStack)stack.get()).getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
            entity.removeEffect(MobEffects.ABSORPTION);
            entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 4));
         }
      }
   }

   public static EventResult onLivingKnockBack(LivingEntity entity, DefaultedDouble strength, DefaultedDouble ratioX, DefaultedDouble ratioZ) {
      if (!((ServerConfig)GoldenAgeCombat.CONFIG.get(ServerConfig.class)).upwardsKnockback) {
         return EventResult.PASS;
      }

      if (!entity.onGround() && !entity.isInWater()) {
         strength.mapDouble(s -> s * (1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
         Vec3 deltaMovement = entity.getDeltaMovement();
         entity.setDeltaMovement(deltaMovement.x, Math.min(0.4, deltaMovement.y / 2.0 + strength.getAsDouble()), deltaMovement.x);
      }

      return EventResult.PASS;
   }

   public static EventResult onPlaySoundAtPosition(
      Level level, Vec3 position, MutableValue<Holder<SoundEvent>> sound, MutableValue<SoundSource> source, DefaultedFloat volume, DefaultedFloat pitch
   ) {
      return !((ServerConfig)GoldenAgeCombat.CONFIG.get(ServerConfig.class)).canceledAttackSounds.contains(((Holder)sound.get()).value())
         ? EventResult.PASS
         : EventResult.INTERRUPT;
   }
}
