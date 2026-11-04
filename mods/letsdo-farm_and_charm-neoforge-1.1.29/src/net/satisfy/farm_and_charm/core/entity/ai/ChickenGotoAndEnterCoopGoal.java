package net.satisfy.farm_and_charm.core.entity.ai;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.block.entity.ChickenCoopBlockEntity;
import net.satisfy.farm_and_charm.core.entity.ChickenCoopAccess;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

public class ChickenGotoAndEnterCoopGoal extends Goal {
   private static final int MAX_APPROACH_TICKS = 400;
   private static final int RETRY_COOLDOWN = 600;
   private final Chicken chicken;
   private int approachTicks;

   public ChickenGotoAndEnterCoopGoal(Chicken chicken) {
      this.chicken = chicken;
      this.setFlags(EnumSet.of(Flag.MOVE));
   }

   public boolean canUse() {
      if (((ChickenCoopAccess)this.chicken).farmAndCharm$getCoopCooldown() > 0) {
         return false;
      }

      if (!((ChickenCoopAccess)this.chicken).farmAndCharm$hasCoopTarget()) {
         return false;
      }

      BlockPos target = ((ChickenCoopAccess)this.chicken).farmAndCharm$getCoopTarget();
      return !this.chicken.level().getBlockState(target).is((Block)ObjectRegistry.CHICKEN_COOP.get())
         ? false
         : this.chicken.level().getBlockEntity(target) instanceof ChickenCoopBlockEntity coop
            && coop.hasSpaceForChicken()
            && !coop.containsChicken(this.chicken);
   }

   private void tryEnterCoop(BlockPos coopPos) {
      double distance = this.chicken.position().distanceTo(Vec3.atCenterOf(coopPos));
      if (distance <= 1.2) {
         if (this.chicken.level().getBlockEntity(coopPos) instanceof ChickenCoopBlockEntity coop && coop.hasSpaceForChicken()) {
            this.chicken.level().playSound(null, this.chicken.blockPosition(), SoundEvents.BEEHIVE_ENTER, this.chicken.getSoundSource(), 1.0F, 1.0F);
            coop.addChicken(this.chicken);
            ((ChickenCoopAccess)this.chicken).farmAndCharm$clearCoopTarget();
            ((ChickenCoopAccess)this.chicken).farmAndCharm$setCoopCooldown(1200 * (3 + this.chicken.getRandom().nextInt(10)));
            this.chicken.getNavigation().stop();
         }
      } else if (!this.chicken.getNavigation().isInProgress()) {
         this.chicken.getNavigation().moveTo(coopPos.getX() + 0.5, coopPos.getY() + 0.5, coopPos.getZ() + 0.5, 1.0);
      }
   }

   public void start() {
      this.approachTicks = 0;
      BlockPos coopPos = ((ChickenCoopAccess)this.chicken).farmAndCharm$getCoopTarget();
      if (coopPos != null) {
         this.tryEnterCoop(coopPos);
      }
   }

   public void tick() {
      BlockPos coopPos = ((ChickenCoopAccess)this.chicken).farmAndCharm$getCoopTarget();
      if (coopPos != null) {
         if (!this.chicken.level().getBlockState(coopPos).is((Block)ObjectRegistry.CHICKEN_COOP.get())) {
            ((ChickenCoopAccess)this.chicken).farmAndCharm$clearCoopTarget();
            this.chicken.getNavigation().stop();
         } else if (++this.approachTicks > 400) {
            ((ChickenCoopAccess)this.chicken).farmAndCharm$clearCoopTarget();
            ((ChickenCoopAccess)this.chicken).farmAndCharm$setCoopCooldown(600);
            this.chicken.getNavigation().stop();
         } else {
            this.tryEnterCoop(coopPos);
         }
      }
   }

   public void stop() {
      this.approachTicks = 0;
      this.chicken.getNavigation().stop();
   }
}
