package net.caffeinemc.mods.lithium.common.world.blockview;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public record SingleBlockBlockView(BlockState state, BlockPos blockPos) implements BlockGetter, CollisionGetter {
   public static SingleBlockBlockView of(BlockState blockState, BlockPos blockPos) {
      return new SingleBlockBlockView(blockState, blockPos.immutable());
   }

   public BlockState getBlockState(BlockPos pos) {
      if (pos.equals(this.blockPos())) {
         return this.state();
      } else {
         throw SingleBlockBlockView.SingleBlockViewException.INSTANCE;
      }
   }

   public FluidState getFluidState(BlockPos pos) {
      if (pos.equals(this.blockPos())) {
         return this.state().getFluidState();
      } else {
         throw SingleBlockBlockView.SingleBlockViewException.INSTANCE;
      }
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos pos) {
      throw SingleBlockBlockView.SingleBlockViewException.INSTANCE;
   }

   public int getHeight() {
      throw SingleBlockBlockView.SingleBlockViewException.INSTANCE;
   }

   public int getMinBuildHeight() {
      throw SingleBlockBlockView.SingleBlockViewException.INSTANCE;
   }

   public WorldBorder getWorldBorder() {
      throw SingleBlockBlockView.SingleBlockViewException.INSTANCE;
   }

   @Nullable
   public BlockGetter getChunkForCollisions(int chunkX, int chunkZ) {
      throw SingleBlockBlockView.SingleBlockViewException.INSTANCE;
   }

   public boolean isUnobstructed(@Nullable Entity except, VoxelShape shape) {
      throw SingleBlockBlockView.SingleBlockViewException.INSTANCE;
   }

   public boolean isUnobstructed(BlockState state, BlockPos pos, CollisionContext context) {
      throw SingleBlockBlockView.SingleBlockViewException.INSTANCE;
   }

   public boolean isUnobstructed(Entity entity) {
      throw SingleBlockBlockView.SingleBlockViewException.INSTANCE;
   }

   public boolean noCollision(@Nullable Entity entity, AABB box) {
      throw SingleBlockBlockView.SingleBlockViewException.INSTANCE;
   }

   public List<VoxelShape> getEntityCollisions(@Nullable Entity entity, AABB box) {
      throw SingleBlockBlockView.SingleBlockViewException.INSTANCE;
   }

   public Iterable<VoxelShape> getCollisions(@Nullable Entity entity, AABB box) {
      throw SingleBlockBlockView.SingleBlockViewException.INSTANCE;
   }

   public Iterable<VoxelShape> getBlockCollisions(@Nullable Entity entity, AABB box) {
      throw SingleBlockBlockView.SingleBlockViewException.INSTANCE;
   }

   public boolean collidesWithSuffocatingBlock(@Nullable Entity entity, AABB box) {
      throw SingleBlockBlockView.SingleBlockViewException.INSTANCE;
   }

   public Optional<Vec3> findFreePosition(@Nullable Entity entity, VoxelShape shape, Vec3 target, double x, double y, double z) {
      throw SingleBlockBlockView.SingleBlockViewException.INSTANCE;
   }

   public static class SingleBlockViewException extends RuntimeException {
      public static final SingleBlockBlockView.SingleBlockViewException INSTANCE = new SingleBlockBlockView.SingleBlockViewException();

      private SingleBlockViewException() {
         this.setStackTrace(new StackTraceElement[0]);
      }

      @Override
      public synchronized Throwable fillInStackTrace() {
         this.setStackTrace(new StackTraceElement[0]);
         return this;
      }
   }
}
