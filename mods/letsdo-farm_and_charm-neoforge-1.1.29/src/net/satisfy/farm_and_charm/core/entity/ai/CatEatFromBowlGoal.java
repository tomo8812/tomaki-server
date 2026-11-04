package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.PetBowlBlock;
import net.satisfy.farm_and_charm.core.block.entity.PetBowlBlockEntity;
import net.satisfy.farm_and_charm.core.entity.BowlAccessor;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.joml.Vector3f;

public class CatEatFromBowlGoal extends Goal {
   private final Cat cat;
   private BlockPos targetBowl;
   private Vector3f targetVec;
   private int eatTicks;
   private ItemStack foodStack;

   public CatEatFromBowlGoal(Cat cat) {
      this.cat = cat;
   }

   public boolean canUse() {
      if (this.cat.isTame() && !this.cat.isOrderedToSit()) {
         Level level = this.cat.level();
         BlockPos catPos = this.cat.blockPosition();
         double closestDist = Double.MAX_VALUE;
         BlockPos closest = null;
         ItemStack candidateFood = ItemStack.EMPTY;

         for (BlockPos pos : BlockPos.betweenClosed(catPos.offset(-16, -4, -16), catPos.offset(16, 4, 16))) {
            BlockState state = level.getBlockState(pos);
            if (state.is((Block)ObjectRegistry.PET_BOWL.get())
               && state.hasProperty(PetBowlBlock.FOOD_TYPE)
               && state.getValue(PetBowlBlock.FOOD_TYPE) == GeneralUtil.FoodType.CAT
               && level.getBlockEntity(pos) instanceof PetBowlBlockEntity bowl
               && !bowl.isEmpty()
               && bowl.canBeUsedBy(this.cat)) {
               double dist = pos.distSqr(catPos);
               if (dist < closestDist) {
                  closestDist = dist;
                  closest = pos.immutable();
                  candidateFood = bowl.getItem(0).copy();
               }
            }
         }

         if (closest != null) {
            this.targetBowl = closest;
            this.targetVec = new Vector3f(closest.getX() + 0.5F, closest.getY(), closest.getZ() + 0.5F);
            this.foodStack = candidateFood;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void start() {
      this.cat.getNavigation().moveTo(this.targetVec.x(), this.targetVec.y(), this.targetVec.z(), 1.0);
      this.eatTicks = 0;
   }

   public boolean canContinueToUse() {
      if (this.targetBowl == null) {
         return false;
      }

      Level level = this.cat.level();
      BlockState state = level.getBlockState(this.targetBowl);
      if (state.getBlock() instanceof PetBowlBlock && state.hasProperty(PetBowlBlock.FOOD_TYPE)) {
         if (level.getBlockEntity(this.targetBowl) instanceof PetBowlBlockEntity bowl && !bowl.isEmpty() && bowl.canBeUsedBy(this.cat)) {
            float distSqr = this.targetVec.distanceSquared((float)this.cat.getX(), (float)this.cat.getY(), (float)this.cat.getZ());
            return distSqr > 4.0F || this.eatTicks < 60;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void tick() {
      if (this.targetBowl == null) {
         this.stop();
      } else {
         Level level = this.cat.level();
         if (level.getBlockEntity(this.targetBowl) instanceof PetBowlBlockEntity bowl && !bowl.isEmpty()) {
            float distSqr = this.targetVec.distanceSquared((float)this.cat.getX(), (float)this.cat.getY(), (float)this.cat.getZ());
            if (distSqr <= 4.0F) {
               this.eatTicks++;
               this.cat.setOrderedToSit(true);
               this.cat.getLookControl().setLookAt(this.targetVec.x(), this.targetVec.y(), this.targetVec.z());
               if (!level.isClientSide && this.eatTicks <= 40) {
                  ParticleOptions particle = this.getParticleFromFood();
                  if (particle != null) {
                     ((ServerLevel)level)
                        .sendParticles(particle, this.targetVec.x(), this.targetVec.y() + 0.09375F, this.targetVec.z(), 3, 0.2, 0.2, 0.2, 0.05);
                  }

                  if (this.eatTicks % 10 == 0) {
                     level.playSound(null, this.cat.blockPosition(), SoundEvents.CAT_EAT, SoundSource.NEUTRAL, 0.8F, 1.0F);
                  }
               }

               if (this.eatTicks == 40) {
                  bowl.decreaseFood();
                  ((BowlAccessor.FedTracker)this.cat).farmAndCharm$$markAsFed();
                  if (!level.isClientSide) {
                     ((ServerLevel)level).sendParticles(ParticleTypes.HEART, this.cat.getX(), this.cat.getY() + 0.5, this.cat.getZ(), 3, 0.3, 0.3, 0.3, 0.01);
                     level.playSound(null, this.cat.blockPosition(), SoundEvents.CAT_PURR, this.cat.getSoundSource(), 1.0F, 1.0F);
                     BlockState old = level.getBlockState(this.targetBowl);
                     if (old.getBlock() instanceof PetBowlBlock && old.hasProperty(PetBowlBlock.FOOD_TYPE)) {
                        level.setBlockAndUpdate(this.targetBowl, (BlockState)old.setValue(PetBowlBlock.FOOD_TYPE, GeneralUtil.FoodType.NONE));
                     }
                  }
               }
            }
         } else {
            this.stop();
         }
      }
   }

   public void stop() {
      this.targetBowl = null;
      this.targetVec = null;
      this.eatTicks = 0;
      this.foodStack = null;
      this.cat.setOrderedToSit(false);
   }

   private ParticleOptions getParticleFromFood() {
      return this.foodStack != null && !this.foodStack.isEmpty() ? new ItemParticleOption(ParticleTypes.ITEM, this.foodStack) : null;
   }
}
