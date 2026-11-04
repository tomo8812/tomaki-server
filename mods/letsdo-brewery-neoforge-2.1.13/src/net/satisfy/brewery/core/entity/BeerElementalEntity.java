package net.satisfy.brewery.core.entity;

import java.util.EnumSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.satisfy.brewery.core.registry.SoundEventRegistry;
import org.jetbrains.annotations.NotNull;

public class BeerElementalEntity extends Monster {
   private float allowedHeightOffset = 0.5F;
   private int nextHeightOffsetChangeTick;

   public BeerElementalEntity(EntityType<? extends Monster> entityType, Level level) {
      super(entityType, level);
      this.xpReward = 8;
   }

   public static Builder createAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 80.0)
         .add(Attributes.MOVEMENT_SPEED, 0.4)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.ATTACK_DAMAGE, 8.0);
   }

   @NotNull
   public EntityDimensions getDimensions(Pose pose) {
      return EntityDimensions.scalable(1.0F, 2.0F);
   }

   protected void registerGoals() {
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.goalSelector.addGoal(3, new BeerElementalEntity.BeerElementalAttackGoal(this));
      this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 1.0));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0, 0.0F));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
   }

   public void aiStep() {
      if (!this.onGround() && this.getDeltaMovement().y < 0.0) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6, 1.0));
      }

      if (this.level().isClientSide) {
         if (this.random.nextInt(24) == 0 && !this.isSilent()) {
            this.level()
               .playLocalSound(
                  this.getX() + 0.5,
                  this.getY() + 0.5,
                  this.getZ() + 0.5,
                  SoundEvents.BLAZE_BURN,
                  this.getSoundSource(),
                  1.0F + this.random.nextFloat(),
                  this.random.nextFloat() * 0.7F + 0.3F,
                  false
               );
         }

         for (int i = 0; i < 2; i++) {
            this.level().addParticle(ParticleTypes.UNDERWATER, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0);
         }
      }

      super.aiStep();
   }

   protected void customServerAiStep() {
      this.nextHeightOffsetChangeTick--;
      if (this.nextHeightOffsetChangeTick <= 0) {
         this.nextHeightOffsetChangeTick = 100;
         this.allowedHeightOffset = (float)this.random.triangle(0.5, 6.891);
      }

      LivingEntity target = this.getTarget();
      if (target != null && target.getEyeY() > this.getEyeY() + this.allowedHeightOffset && this.canAttack(target)) {
         Vec3 velocity = this.getDeltaMovement();
         velocity = velocity.add(0.0, (0.3 - velocity.y) * 0.3, 0.0);
         this.setDeltaMovement(velocity);
         this.hasImpulse = true;
      }
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)SoundEventRegistry.BEER_ELEMENTAL_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return (SoundEvent)SoundEventRegistry.BEER_ELEMENTAL_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)SoundEventRegistry.BEER_ELEMENTAL_DEATH.get();
   }

   public boolean causeFallDamage(float f, float g, DamageSource damageSource) {
      return false;
   }

   private static class BeerElementalAttackGoal extends Goal {
      private final BeerElementalEntity elemental;
      private int attackStep;
      private int attackTime;
      private int lastSeen;

      public BeerElementalAttackGoal(BeerElementalEntity elemental) {
         this.elemental = elemental;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity target = this.elemental.getTarget();
         return target != null && target.isAlive() && this.elemental.canAttack(target);
      }

      public void start() {
         this.attackStep = 0;
      }

      public void stop() {
         this.lastSeen = 0;
      }

      public void tick() {
         this.attackTime--;
         LivingEntity target = this.elemental.getTarget();
         if (target != null) {
            boolean canSee = this.elemental.getSensing().hasLineOfSight(target);
            this.lastSeen = canSee ? 0 : this.lastSeen + 1;
            double dist = this.elemental.distanceToSqr(target);
            double range = this.getFollowDistance() * this.getFollowDistance();
            if (dist < 4.0) {
               if (!canSee) {
                  return;
               }

               if (this.attackTime <= 0.0) {
                  this.attackTime = 20;
                  this.elemental.doHurtTarget(target);
               }

               this.elemental.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0);
            } else if (dist < range && canSee) {
               if (this.attackTime <= 0) {
                  this.attackStep++;
                  if (this.attackStep == 1) {
                     this.attackTime = 60;
                  } else if (this.attackStep <= 4) {
                     this.attackTime = 6;
                  } else {
                     this.attackTime = 100;
                     this.attackStep = 0;
                  }

                  if (this.attackStep > 1) {
                     if (!this.elemental.isSilent()) {
                        this.elemental.playSound((SoundEvent)SoundEventRegistry.BEER_ELEMENTAL_ATTACK.get(), 1.0F, 1.0F);
                     }

                     double dX = target.getX() - this.elemental.getX();
                     double dY = target.getY(0.5) - this.elemental.getY(0.5);
                     double dZ = target.getZ() - this.elemental.getZ();
                     double f = Math.sqrt(Math.sqrt(dist)) * 0.5;
                     Vec3 velocity = new Vec3(this.elemental.getRandom().triangle(dX, 2.297 * f), dY, this.elemental.getRandom().triangle(dZ, 2.297 * f));
                     BeerElementalAttackEntity attack = new BeerElementalAttackEntity(
                        this.elemental.level(), this.elemental.getX(), this.elemental.getY(0.5) + 0.5, this.elemental.getZ(), velocity
                     );
                     this.elemental.level().addFreshEntity(attack);
                  }
               }

               this.elemental.getLookControl().setLookAt(target, 10.0F, 10.0F);
            } else if (this.lastSeen < 5) {
               this.elemental.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0);
            }

            super.tick();
         }
      }

      private double getFollowDistance() {
         return this.elemental.getAttributeValue(Attributes.FOLLOW_RANGE);
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }
   }
}
