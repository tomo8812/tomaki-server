package net.satisfy.farm_and_charm.core.entity.ai;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.block.Block;
import net.satisfy.farm_and_charm.core.block.entity.ChickenCoopBlockEntity;
import net.satisfy.farm_and_charm.core.entity.ChickenCoopAccess;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

public class ChickenLocateCoopGoal extends Goal {
   private final Chicken chicken;
   private BlockPos foundCoop;
   private final List<BlockPos> cachedCoops = new ArrayList<>();
   private int searchCooldown = 0;

   public ChickenLocateCoopGoal(Chicken chicken) {
      this.chicken = chicken;
      this.setFlags(EnumSet.of(Flag.MOVE));
   }

   public boolean canUse() {
      if (this.chicken.isBaby()) {
         return false;
      }

      ChickenCoopAccess access = (ChickenCoopAccess)this.chicken;
      if (access.farmAndCharm$getCoopCooldown() > 0) {
         return false;
      }

      ServerLevel level = (ServerLevel)this.chicken.level();
      if (access.farmAndCharm$hasCoopTarget()) {
         if (this.isUsableCoop(level, access.farmAndCharm$getCoopTarget())) {
            return false;
         }

         access.farmAndCharm$clearCoopTarget();
      }

      if (this.searchCooldown > 0) {
         this.searchCooldown--;
         return false;
      }

      this.searchCooldown = 20 + this.chicken.getRandom().nextInt(20);
      Iterator<BlockPos> it = this.cachedCoops.iterator();

      while (it.hasNext()) {
         BlockPos cached = it.next();
         if (!this.isUsableCoop(level, cached)) {
            it.remove();
         } else if (this.chicken.getNavigation().createPath(cached, 0) != null) {
            this.foundCoop = cached;
            return true;
         }
      }

      BlockPos pos = this.chicken.blockPosition();
      this.foundCoop = BlockPos.findClosestMatch(pos, 16, 4, check -> {
         if (!this.isUsableCoop(level, check)) {
            return false;
         }

         if (this.chicken.getNavigation().createPath(check, 0) == null) {
            return false;
         }

         BlockPos immutable = check.immutable();
         if (!this.cachedCoops.contains(immutable)) {
            this.cachedCoops.add(immutable);
         }

         return true;
      }).<BlockPos>map(BlockPos::immutable).orElse(null);
      return this.foundCoop != null;
   }

   private boolean isUsableCoop(ServerLevel level, BlockPos pos) {
      if (pos == null) {
         return false;
      } else {
         return !level.getBlockState(pos).is((Block)ObjectRegistry.CHICKEN_COOP.get())
            ? false
            : level.getBlockEntity(pos) instanceof ChickenCoopBlockEntity coop && coop.hasSpaceForChicken();
      }
   }

   public void start() {
      ((ChickenCoopAccess)this.chicken).farmAndCharm$setCoopTarget(this.foundCoop);
      if (!this.chicken.getNavigation().isInProgress()) {
         this.chicken.getNavigation().moveTo(this.foundCoop.getX() + 0.5, this.foundCoop.getY() + 0.5, this.foundCoop.getZ() + 0.5, 1.0);
      }
   }

   public void stop() {
      this.foundCoop = null;
   }
}
