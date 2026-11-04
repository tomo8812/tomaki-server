package net.caffeinemc.mods.lithium.mixin.world.raycast;

import java.util.function.BiFunction;
import java.util.function.Function;
import net.caffeinemc.mods.lithium.common.util.Pos;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockGetter.class)
public interface BlockGetterMixin {
   @Shadow
   BlockState getBlockState(BlockPos var1);

   @Shadow
   @Nullable
   BlockHitResult clipWithInteractionOverride(Vec3 var1, Vec3 var2, BlockPos var3, VoxelShape var4, BlockState var5);

   @Shadow
   static <T, C> T traverseBlocks(Vec3 start, Vec3 end, C context, BiFunction<C, BlockPos, T> blockHitFactory, Function<C, T> missFactory) {
      throw new AssertionError();
   }

   @Shadow(aliases = "lambda$clip$2")
   BlockHitResult method_17743(ClipContext var1, BlockPos var2);

   @Shadow(aliases = "lambda$clip$3")
   static BlockHitResult method_17746(ClipContext par1) {
      throw new AssertionError();
   }

   @Overwrite
   default BlockHitResult clip(ClipContext context) {
      return traverseBlocks(
         context.getFrom(),
         context.getTo(),
         context,
         !(this instanceof ServerLevel) && !(this instanceof Level l && l.isClientSide()) ? this::method_17743 : this.lithium$blockHitFactory(context),
         BlockGetterMixin::method_17746
      );
   }

   @Unique
   private BiFunction<ClipContext, BlockPos, BlockHitResult> lithium$blockHitFactory(final ClipContext context) {
      return new BiFunction<ClipContext, BlockPos, BlockHitResult>() {
         int chunkX = Integer.MIN_VALUE;
         int chunkZ = Integer.MIN_VALUE;
         ChunkAccess chunk = null;
         final boolean handleFluids = ((ClipContextAccessor)context).getFluidHandling() != Fluid.NONE;

         public BlockHitResult apply(ClipContext innerContext, BlockPos pos) {
            BlockState blockState = this.getBlock((LevelReader)BlockGetterMixin.this, pos);
            Vec3 start = innerContext.getFrom();
            Vec3 end = innerContext.getTo();
            VoxelShape blockShape = innerContext.getBlockShape(blockState, (BlockGetter)BlockGetterMixin.this, pos);
            BlockHitResult blockHitResult = BlockGetterMixin.this.clipWithInteractionOverride(start, end, pos, blockShape, blockState);
            double d = blockHitResult == null ? Double.MAX_VALUE : innerContext.getFrom().distanceToSqr(blockHitResult.getLocation());
            double e = Double.MAX_VALUE;
            BlockHitResult fluidHitResult = null;
            if (this.handleFluids) {
               FluidState fluidState = blockState.getFluidState();
               VoxelShape fluidShape = innerContext.getFluidShape(fluidState, (BlockGetter)BlockGetterMixin.this, pos);
               fluidHitResult = fluidShape.clip(start, end, pos);
               e = fluidHitResult == null ? Double.MAX_VALUE : innerContext.getFrom().distanceToSqr(fluidHitResult.getLocation());
            }

            return d <= e ? blockHitResult : fluidHitResult;
         }

         private BlockState getBlock(LevelReader world, BlockPos blockPos) {
            if (world.isOutsideBuildHeight(blockPos.getY())) {
               return Blocks.VOID_AIR.defaultBlockState();
            }

            int chunkX = Pos.ChunkCoord.fromBlockCoord(blockPos.getX());
            int chunkZ = Pos.ChunkCoord.fromBlockCoord(blockPos.getZ());
            if (this.chunkX != chunkX || this.chunkZ != chunkZ) {
               this.chunk = world.getChunk(chunkX, chunkZ);
               this.chunkX = chunkX;
               this.chunkZ = chunkZ;
            }

            ChunkAccess chunk = this.chunk;
            if (chunk != null) {
               LevelChunkSection section = chunk.getSections()[Pos.SectionYIndex.fromBlockCoord(chunk, blockPos.getY())];
               if (section != null && !section.hasOnlyAir()) {
                  return section.getBlockState(blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15);
               }
            }

            return Blocks.AIR.defaultBlockState();
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public Object apply(Object param1, Object nullx) {
            return this.apply((ClipContext)var1, (BlockPos)nullx);
         }
      };
   }
}
