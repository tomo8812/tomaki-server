package org.millenaire.goal.impl;

import com.mojang.logging.LogUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.millenaire.config.MillenaireServerConfig;
import org.millenaire.entity.MillVillager;
import org.millenaire.entity.VillagerNavDriver;
import org.millenaire.goal.GoalContext;
import org.millenaire.goal.GoalUtils;
import org.millenaire.goal.NavigationHelperUtils;
import org.millenaire.goal.ProgressAwareTask;
import org.millenaire.goal.StopReason;
import org.millenaire.goal.TravelPhase;
import org.millenaire.goal.VillagerGoal;
import org.millenaire.goal.VillagerTask;
import org.millenaire.tag.ModTags;
import org.millenaire.tool.ToolCategory;
import org.millenaire.tool.ToolCategoryRegistry;
import org.millenaire.village.TerraformProject;
import org.millenaire.village.VillageGrowthManager;
import org.millenaire.world.TerraformJobCompiler;
import org.millenaire.world.TerrainPreparer;
import org.slf4j.Logger;

public class TerraformGoal implements VillagerGoal {
   public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("millenaire", "terraform");
   private static final Logger LOGGER = LogUtils.getLogger();
   static final int MAX_WORKERS = 2;
   static final int OPS_PER_TASK = 16;
   private static final int REACH_HORIZONTAL_SQ = 25;
   private static final int REMOTE_ACTION_HORIZONTAL_SQ = 900;
   private static final int NAVIGATE_STUCK_THRESHOLD = 100;
   private static final int STAND_REFRESH_TICKS = 40;
   private static final int WALKING_STUCK_TIMEOUT = 400;

   @Override
   public ResourceLocation id() {
      return ID;
   }

   @Override
   public int computePriority(GoalContext context) {
      return 1400;
   }

   @Override
   public boolean canStart(GoalContext context) {
      if (!(Boolean)MillenaireServerConfig.SERVER.villagerTerraforming.get()) {
         return false;
      }

      TerraformProject project = context.village().getTerraformProject();
      return project != null && project.hasUnclaimedWork() ? GoalUtils.countSimultaneous(context, ID) < 2 : false;
   }

   @Override
   public VillagerTask start(GoalContext context) {
      return new TerraformGoal.Task();
   }

   @Override
   public boolean showInTravelBook() {
      return false;
   }

   static boolean inReach(BlockPos feet, BlockPos target) {
      int dx = feet.getX() - target.getX();
      int dz = feet.getZ() - target.getZ();
      return dx * dx + dz * dz < 25;
   }

   static boolean mayActRemotely(BlockPos feet, BlockPos target) {
      int dx = feet.getX() - target.getX();
      int dz = feet.getZ() - target.getZ();
      return dx * dx + dz * dz < 900;
   }

   public static boolean applyOp(ServerLevel level, TerraformJobCompiler.Op op, @Nullable MillVillager worker, boolean effects) {
      BlockPos pos = op.pos();
      BlockState current = level.getBlockState(pos);
      if (isProtected(level, pos, current)) {
         LOGGER.debug("Terraform op at {} skipped: protected block {}", pos.toShortString(), current);
         return false;
      }

      switch (op.kind()) {
         case REMOVE:
            if (!current.isAir() && !isWaterToFill(current)) {
               if (effects) {
                  level.levelEvent(2001, pos, Block.getId(current));
               }

               level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
               stabilizeAround(level, pos);
               return true;
            }

            return false;
         case PLACE:
            if (op.state() != null && isFillable(current)) {
               if (worker != null) {
                  liftOutOf(worker, pos);
               }

               BlockState state = isFillable(level.getBlockState(pos.below())) ? TerraformJobCompiler.stable(op.state()) : op.state();
               level.setBlock(pos, state, 3);
               stabilizeAround(level, pos);
               if (effects) {
                  playPlaceSound(level, pos, state);
               }

               return true;
            }

            return false;
         case RESURFACE:
            if (op.state() != null && !current.isAir() && !current.is(op.state().getBlock())) {
               level.setBlock(pos, op.state(), 3);
               stabilizeAround(level, pos);
               if (effects) {
                  playPlaceSound(level, pos, op.state());
               }

               return true;
            }

            return false;
         default:
            return false;
      }
   }

   private static void stabilizeAround(ServerLevel level, BlockPos pos) {
      stabilize(level, pos.above());
      stabilize(level, pos.below());
      stabilize(level, pos.north());
      stabilize(level, pos.south());
      stabilize(level, pos.east());
      stabilize(level, pos.west());
   }

   private static void stabilize(ServerLevel level, BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      if (state.getBlock() instanceof Fallable && FallingBlock.isFree(level.getBlockState(pos.below()))) {
         level.setBlock(pos, TerraformJobCompiler.stable(state), 2);
      }
   }

   static boolean isAlreadyDone(ServerLevel level, TerraformJobCompiler.Op op) {
      BlockState current = level.getBlockState(op.pos());

      return switch (op.kind()) {
         case REMOVE -> current.isAir() || isWaterToFill(current);
         case PLACE -> !isFillable(current);
         case RESURFACE -> current.isAir() || op.state() != null && current.is(op.state().getBlock());
      };
   }

   static boolean isProtected(ServerLevel level, BlockPos pos, BlockState state) {
      if (state.isAir()) {
         return false;
      } else if (isWaterToFill(state)) {
         return false;
      } else if (!state.getFluidState().isEmpty()) {
         return true;
      } else {
         return level.getBlockEntity(pos) != null ? true : state.is(ModTags.Blocks.ARTIFICIAL_BLOCKS);
      }
   }

   public static boolean isFillable(BlockState state) {
      return state.isAir() || state.canBeReplaced() || state.is(BlockTags.LEAVES) || isWaterToFill(state);
   }

   static boolean isWaterToFill(BlockState state) {
      return state.is(Blocks.WATER)
         || state.is(Blocks.BUBBLE_COLUMN)
         || state.is(Blocks.SEAGRASS)
         || state.is(Blocks.TALL_SEAGRASS)
         || state.is(Blocks.KELP)
         || state.is(Blocks.KELP_PLANT);
   }

   private static void liftOutOf(MillVillager villager, BlockPos pos) {
      if (villager.getBoundingBox().intersects(new AABB(pos))) {
         villager.teleportTo(villager.getX(), pos.getY() + 1.0, villager.getZ());
      }
   }

   private static void playPlaceSound(ServerLevel level, BlockPos pos, BlockState state) {
      SoundType sound = state.getSoundType();
      level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
   }

   private static class Task extends ProgressAwareTask {
      private TerraformGoal.Task.State state = TerraformGoal.Task.State.WALKING;
      @Nullable
      private List<Integer> claimed;
      private int cursor;
      private int actionTicks;
      private int navigateStuckTicks;
      private int walkingStuckTicks;
      private double bestDistance = Double.MAX_VALUE;
      private float shovelEfficiency = 2.0F;
      private boolean toolsResolved;
      private List<ItemStack> heldShovel = List.of(new ItemStack(Items.WOODEN_SHOVEL));
      private final List<ItemStack> heldEarth = List.of(new ItemStack(Items.DIRT));
      @Nullable
      private TerraformJobCompiler.Kind currentKind;
      @Nullable
      private BlockPos stand;
      private int standCursor = -1;
      private int standAge;

      @Override
      public ResourceLocation goalId() {
         return TerraformGoal.ID;
      }

      @Override
      public Component getGoalLabel() {
         return this.state == TerraformGoal.Task.State.WALKING
            ? Component.translatable("goal.millenaire.terraform.travelling")
            : Component.translatable("goal.millenaire.terraform");
      }

      @Override
      public List<ItemStack> getHeldItems(TravelPhase phase) {
         return this.currentKind == TerraformJobCompiler.Kind.PLACE ? this.heldEarth : this.heldShovel;
      }

      @Override
      public TravelPhase getTravelPhase() {
         return this.state == TerraformGoal.Task.State.WALKING ? TravelPhase.TRAVELLING : TravelPhase.AT_DESTINATION;
      }

      @Override
      public void tick(GoalContext ctx) {
         TerraformProject project = ctx.village().getTerraformProject();
         if (project != null && project.status() == TerraformProject.Status.ACTIVE) {
            if (this.claimed == null) {
               this.resolveTools(ctx.villager());
               this.claimed = project.claim(ctx.villager().getUUID(), 16, ctx.level().getGameTime());
               this.cursor = 0;
               if (this.claimed.isEmpty()) {
                  this.state = TerraformGoal.Task.State.DONE;
                  return;
               }
            }

            switch (this.state) {
               case WALKING:
                  this.tickWalking(ctx, project);
                  break;
               case ACTING:
                  this.tickActing(ctx, project);
               case DONE:
            }
         } else {
            this.state = TerraformGoal.Task.State.DONE;
         }
      }

      private void tickWalking(GoalContext ctx, TerraformProject project) {
         TerraformJobCompiler.Op op = this.currentOpOrNull(project);
         if (op == null) {
            this.state = TerraformGoal.Task.State.DONE;
         } else {
            this.currentKind = op.kind();
            if (TerraformGoal.isAlreadyDone(ctx.level(), op)) {
               this.advance(ctx, project);
            } else {
               MillVillager villager = ctx.villager();
               VillagerNavDriver nav = villager.getNavManager();
               BlockPos target = op.pos();
               BlockPos feet = villager.blockPosition();
               if (TerraformGoal.inReach(feet, target)) {
                  nav.stop(villager);
                  this.state = TerraformGoal.Task.State.ACTING;
                  this.actionTicks = this.actionDuration(ctx.level().getBlockState(target), op);
               } else {
                  if (this.stand == null || this.standCursor != this.cursor || ++this.standAge >= 40) {
                     this.stand = new BlockPos(target.getX(), TerrainPreparer.getSurfaceOrWaterHeight(ctx.level(), target.getX(), target.getZ()), target.getZ());
                     this.standCursor = this.cursor;
                     this.standAge = 0;
                  }

                  if (nav.getDestination() == null || !nav.getDestination().equals(this.stand)) {
                     nav.navigateTo(villager, this.stand, 0.5);
                  }

                  int dx = feet.getX() - target.getX();
                  int dz = feet.getZ() - target.getZ();
                  double distance = Math.sqrt(dx * dx + dz * dz);
                  if (distance < this.bestDistance - 1.0) {
                     this.bestDistance = distance;
                     this.navigateStuckTicks = 0;
                     this.walkingStuckTicks = 0;
                  }

                  if (++this.walkingStuckTicks > 400) {
                     NavigationHelperUtils.teleportToSafeNearTarget(villager, this.stand);
                     if (!TerraformGoal.mayActRemotely(villager.blockPosition(), target)) {
                        this.perform(ctx, project, op);
                     } else {
                        this.resetStuck();
                     }
                  } else {
                     if (++this.navigateStuckTicks > 100 && TerraformGoal.mayActRemotely(feet, target)) {
                        this.perform(ctx, project, op);
                     }
                  }
               }
            }
         }
      }

      private void tickActing(GoalContext ctx, TerraformProject project) {
         if (--this.actionTicks > 0) {
            if (this.actionTicks % 5 == 0) {
               ctx.villager().swing(InteractionHand.MAIN_HAND);
            }
         } else {
            TerraformJobCompiler.Op op = this.currentOpOrNull(project);
            if (op == null) {
               this.state = TerraformGoal.Task.State.DONE;
            } else {
               this.perform(ctx, project, op);
            }
         }
      }

      private void perform(GoalContext ctx, TerraformProject project, TerraformJobCompiler.Op op) {
         if (TerraformGoal.applyOp(ctx.level(), op, ctx.villager(), true)) {
            ctx.village().markWaypointGraphDirty();
         }

         ctx.villager().swing(InteractionHand.MAIN_HAND);
         this.advance(ctx, project);
      }

      private void advance(GoalContext ctx, TerraformProject project) {
         project.complete(ctx.villager().getUUID(), this.claimed.get(this.cursor), ctx.level().getGameTime());
         if (project.status() == TerraformProject.Status.READY) {
            VillageGrowthManager.onBuildingCompleted(ctx.village());
         }

         ctx.village().markDirty();
         this.reportProgress();
         this.cursor++;
         this.resetStuck();
         this.state = this.cursor < this.claimed.size() && project.status() == TerraformProject.Status.ACTIVE
            ? TerraformGoal.Task.State.WALKING
            : TerraformGoal.Task.State.DONE;
      }

      private void resetStuck() {
         this.navigateStuckTicks = 0;
         this.walkingStuckTicks = 0;
         this.bestDistance = Double.MAX_VALUE;
      }

      @Nullable
      private TerraformJobCompiler.Op currentOpOrNull(TerraformProject project) {
         return this.claimed != null && this.cursor < this.claimed.size() ? project.op(this.claimed.get(this.cursor)) : null;
      }

      private int actionDuration(BlockState current, TerraformJobCompiler.Op op) {
         boolean soft = current.isAir() || current.canBeReplaced() || current.is(BlockTags.LEAVES) || op.kind() == TerraformJobCompiler.Kind.RESURFACE;
         return Math.max(2, BuildGoal.computeConstructionDuration(this.shovelEfficiency, soft));
      }

      private void resolveTools(MillVillager villager) {
         if (!this.toolsResolved) {
            this.toolsResolved = true;
            ToolCategory category = ToolCategoryRegistry.get("toolsshovel");
            if (category != null) {
               this.shovelEfficiency = category.getBestDestroySpeed(
                  item -> villager.getInventory().getCount(item) > 0, Blocks.DIRT.defaultBlockState(), Items.WOODEN_SHOVEL
               );
               ToolCategory.ToolEntry best = category.getBestOwned(item -> villager.getInventory().getCount(item) > 0);
               if (best != null && best.item() != null) {
                  this.heldShovel = List.of(new ItemStack(best.item()));
               }
            }
         }
      }

      @Override
      public boolean isFinished() {
         return this.state == TerraformGoal.Task.State.DONE;
      }

      @Override
      public void stop(GoalContext context, StopReason reason) {
         if (context != null) {
            TerraformProject project = context.village().getTerraformProject();
            if (project != null) {
               project.release(context.villager().getUUID());
            }

            context.villager().getNavManager().stop(context.villager());
         }
      }

      private enum State {
         WALKING,
         ACTING,
         DONE;

         // $VF: synthetic method
         private static TerraformGoal.Task.State[] $values() {
            return new TerraformGoal.Task.State[]{WALKING, ACTING, DONE};
         }
      }
   }
}
