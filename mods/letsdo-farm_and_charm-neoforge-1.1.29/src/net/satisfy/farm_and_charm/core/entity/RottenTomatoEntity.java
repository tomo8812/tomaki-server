package net.satisfy.farm_and_charm.core.entity;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class RottenTomatoEntity extends ThrowableItemProjectile {
   public RottenTomatoEntity(Level world, LivingEntity owner) {
      super((EntityType)EntityTypeRegistry.ROTTEN_TOMATO.get(), owner, world);
   }

   public RottenTomatoEntity(EntityType<? extends RottenTomatoEntity> entityType, Level world) {
      super(entityType, world);
   }

   @NotNull
   protected Item getDefaultItem() {
      return (Item)ObjectRegistry.ROTTEN_TOMATO.get();
   }

   private ParticleOptions getParticleParameters() {
      return new ItemParticleOption(ParticleTypes.ITEM, new ItemStack((ItemLike)ObjectRegistry.ROTTEN_TOMATO.get()));
   }

   public void handleEntityEvent(byte id) {
      if (id == 3) {
         ParticleOptions particleEffect = this.getParticleParameters();

         for (int i = 0; i < 30; i++) {
            double offsetX = this.random.nextGaussian() * 0.02;
            double offsetY = this.random.nextGaussian() * 0.02;
            double offsetZ = this.random.nextGaussian() * 0.02;
            this.level().addParticle(particleEffect, this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ, 0.0, 0.0, 0.0);
         }
      } else {
         super.handleEntityEvent(id);
      }
   }

   protected void onHitEntity(EntityHitResult result) {
      super.onHitEntity(result);
      Entity entity = result.getEntity();
      if (entity instanceof LivingEntity livingEntity) {
         livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 30, 0, false, false));
      }

      int damage = 1;
      entity.hurt(entity.damageSources().thrown(this, this.getOwner()), damage);
   }

   protected void onHit(HitResult hitResult) {
      super.onHit(hitResult);
      if (!this.level().isClientSide) {
         this.level().broadcastEntityEvent(this, (byte)3);
         this.playSound(SoundEvents.SLIME_BLOCK_BREAK, 1.0F, 1.0F);
         if (this.random.nextFloat() < 0.15F) {
            this.spawnAtLocation((ItemLike)ObjectRegistry.TOMATO_SEEDS.get());
         }

         this.discard();
      }
   }
}
