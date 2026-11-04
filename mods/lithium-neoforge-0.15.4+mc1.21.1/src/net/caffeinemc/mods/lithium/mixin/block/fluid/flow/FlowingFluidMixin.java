package net.caffeinemc.mods.lithium.mixin.block.fluid.flow;

import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2ByteOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2ByteMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map;
import net.caffeinemc.mods.lithium.common.util.DirectionConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin {
   @Shadow
   public abstract Fluid getFlowing();

   @Shadow
   protected abstract FluidState getNewLiquid(Level var1, BlockPos var2, BlockState var3);

   @Shadow
   protected abstract boolean canHoldFluid(BlockGetter var1, BlockPos var2, BlockState var3, Fluid var4);

   @Shadow
   public abstract Fluid getSource();

   @Shadow
   protected abstract boolean isSourceBlockOfThisType(FluidState var1);

   @Shadow
   protected abstract boolean canPassThroughWall(Direction var1, BlockGetter var2, BlockPos var3, BlockState var4, BlockPos var5, BlockState var6);

   @Shadow
   protected abstract int getSlopeFindDistance(LevelReader var1);

   @Unique
   private static int getNumIndicesFromRadius(int radius) {
      return (radius + 1) * (2 * radius + 1);
   }

   @Unique
   private static byte indexFromDiamondXZOffset(BlockPos originPos, BlockPos offsetPos, int radius) {
      int xOffset = offsetPos.getX() - originPos.getX();
      int zOffset = offsetPos.getZ() - originPos.getZ();
      int row = (xOffset + zOffset + radius) / 2;
      int column = xOffset - zOffset + radius;
      int rowLength = 2 * radius + 1;
      return (byte)(row * rowLength + column);
   }

   @Inject(
      method = "getSpread(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Map;",
      at = @At("HEAD"),
      cancellable = true
   )
   public void getSpread(Level world, BlockPos pos, BlockState state, CallbackInfoReturnable<Map<Direction, FluidState>> cir) {
      Map<Direction, FluidState> flowResultByDirection = Maps.newEnumMap(Direction.class);
      int searchRadius = this.getSlopeFindDistance(world) + 1;
      int numIndicesFromRadius = getNumIndicesFromRadius(searchRadius);
      if (numIndicesFromRadius <= 256) {
         BlockState[] blockStateCache = new BlockState[numIndicesFromRadius];
         Direction onlyPossibleFlowDirection = null;
         BlockPos onlyBlockPos = null;
         BlockState onlyBlockState = null;

         for (Direction flowDirection : DirectionConstants.HORIZONTAL) {
            BlockPos flowTargetPos = pos.relative(flowDirection);
            byte blockIndex = indexFromDiamondXZOffset(pos, flowTargetPos, searchRadius);
            BlockState flowTargetBlock = world.getBlockState(flowTargetPos);
            blockStateCache[blockIndex] = flowTargetBlock;
            if (this.canMaybeFlowIntoBlock(world, flowTargetBlock, flowTargetPos)) {
               if (onlyPossibleFlowDirection != null) {
                  this.calculateComplexFluidFlowDirections(world, pos, state, blockStateCache, flowResultByDirection);
                  cir.setReturnValue(flowResultByDirection);
                  return;
               }

               onlyPossibleFlowDirection = flowDirection;
               onlyBlockPos = flowTargetPos;
               onlyBlockState = flowTargetBlock;
            }
         }

         if (onlyPossibleFlowDirection != null) {
            FluidState targetNewFluidState = this.getNewLiquid(world, onlyBlockPos, onlyBlockState);
            if (this.canPassThrough(
               world, targetNewFluidState.getType(), pos, state, onlyPossibleFlowDirection, onlyBlockPos, onlyBlockState, onlyBlockState.getFluidState()
            )) {
               FluidState fluidState = targetNewFluidState;
               flowResultByDirection.put(onlyPossibleFlowDirection, fluidState);
            }
         }

         cir.setReturnValue(flowResultByDirection);
      }
   }

   @Overwrite
   private boolean canPassThrough(
      BlockGetter world, Fluid fluid, BlockPos pos, BlockState state, Direction face, BlockPos fromPos, BlockState fromState, FluidState fluidState
   ) {
      return this.canHoldFluid(world, fromPos, fromState, fluid)
         && !this.isSourceBlockOfThisType(fluidState)
         && this.canPassThroughWall(face, world, pos, state, fromPos, fromState);
   }

   @Overwrite
   private boolean isWaterHole(BlockGetter world, Fluid fluid, BlockPos pos, BlockState state, BlockPos fromPos, BlockState fromState) {
      return (fromState.getFluidState().getType().isSame((FlowingFluid)this) || this.canHoldFluid(world, fromPos, fromState, fluid))
         && this.canPassThroughWall(Direction.DOWN, world, pos, state, fromPos, fromState);
   }

   @Unique
   private void calculateComplexFluidFlowDirections(
      Level world, BlockPos startPos, BlockState startState, BlockState[] blockStateCache, Map<Direction, FluidState> flowResultByDirection
   ) {
      Byte2ByteOpenHashMap prevPositions = new Byte2ByteOpenHashMap();
      Byte2ByteOpenHashMap currentPositions = new Byte2ByteOpenHashMap();
      Byte2BooleanOpenHashMap holeCache = new Byte2BooleanOpenHashMap();
      byte holeAccess = 0;
      int searchRadius = this.getSlopeFindDistance(world) + 1;

      for (int i = 0; i < DirectionConstants.HORIZONTAL.length; i++) {
         Direction flowDirection = DirectionConstants.HORIZONTAL[i];
         BlockPos flowTargetPos = startPos.relative(flowDirection);
         byte blockIndex = indexFromDiamondXZOffset(startPos, flowTargetPos, searchRadius);
         BlockState targetBlockState = this.getBlock(world, flowTargetPos, blockStateCache, blockIndex);
         FluidState targetNewFluidState = this.getNewLiquid(world, flowTargetPos, targetBlockState);
         flowResultByDirection.put(flowDirection, targetNewFluidState);
         if (this.canPassThrough(
            world, targetNewFluidState.getType(), startPos, startState, flowDirection, flowTargetPos, targetBlockState, targetBlockState.getFluidState()
         )) {
            prevPositions.put(blockIndex, (byte)(17 << i));
            if (this.isHoleBelow(world, holeCache, blockIndex, flowTargetPos, targetBlockState)) {
               holeAccess |= (byte)(1 << i);
            }
         }
      }

      for (int i = 0; i < this.getSlopeFindDistance(world) && holeAccess == 0; i++) {
         Fluid targetFluid = this.getFlowing();
         ObjectIterator<Entry> iterator = prevPositions.byte2ByteEntrySet().fastIterator();

         while (iterator.hasNext()) {
            Entry entry = (Entry)iterator.next();
            byte blockIndex = entry.getByteKey();
            byte currentInfo = entry.getByteValue();
            int rowLength = 2 * searchRadius + 1;
            int row = blockIndex / rowLength;
            int column = blockIndex % rowLength;
            int unevenColumn = column % 2;
            int xOffset = (row * 2 + column + unevenColumn - searchRadius * 2) / 2;
            int zOffset = xOffset - column + searchRadius;
            BlockPos currentPos = startPos.offset(xOffset, 0, zOffset);
            BlockState currentState = blockStateCache[blockIndex];

            for (int j = 0; j < DirectionConstants.HORIZONTAL.length; j++) {
               Direction flowDirection = DirectionConstants.HORIZONTAL[j];
               int oppositeDirection = DirectionConstants.HORIZONTAL_OPPOSITE_INDICES[j];
               if ((currentInfo >> 4 & 1 << oppositeDirection) == 0) {
                  BlockPos flowTargetPos = currentPos.relative(flowDirection);
                  byte targetPosBlockIndex = indexFromDiamondXZOffset(startPos, flowTargetPos, searchRadius);
                  if (!prevPositions.containsKey(targetPosBlockIndex)) {
                     byte oldInfo = currentPositions.getOrDefault(targetPosBlockIndex, (byte)0);
                     byte newInfo = oldInfo;
                     newInfo = (byte)(newInfo | (byte)(16 << j));
                     newInfo = (byte)(newInfo | (byte)(currentInfo & 15));
                     if ((newInfo & 15) == (oldInfo & 15)) {
                        currentPositions.put(targetPosBlockIndex, newInfo);
                     } else {
                        BlockState targetBlockState = this.getBlock(world, flowTargetPos, blockStateCache, targetPosBlockIndex);
                        if (this.canPassThrough(
                           world, targetFluid, currentPos, currentState, flowDirection, flowTargetPos, targetBlockState, targetBlockState.getFluidState()
                        )) {
                           currentPositions.put(targetPosBlockIndex, newInfo);
                           if (this.isHoleBelow(world, holeCache, targetPosBlockIndex, flowTargetPos, targetBlockState)) {
                              holeAccess |= (byte)(currentInfo & 15);
                           }
                        }
                     }
                  }
               }
            }
         }

         Byte2ByteOpenHashMap tmp = prevPositions;
         prevPositions = currentPositions;
         currentPositions = tmp;
         currentPositions.clear();
      }

      if (holeAccess != 0) {
         this.removeDirectionsWithoutHoleAccess(holeAccess, flowResultByDirection);
      }
   }

   @Unique
   private BlockState getBlock(Level world, BlockPos pos, BlockState[] blockStateCache, byte byteKey) {
      int key = Byte.toUnsignedInt(byteKey);
      BlockState blockState = blockStateCache[key];
      if (blockState == null) {
         blockState = world.getBlockState(pos);
         blockStateCache[key] = blockState;
      }

      return blockState;
   }

   @Unique
   private void removeDirectionsWithoutHoleAccess(byte holeAccess, Map<Direction, FluidState> flowResultByDirection) {
      for (int i = 0; i < DirectionConstants.HORIZONTAL.length; i++) {
         if ((holeAccess & 1 << i) == 0) {
            flowResultByDirection.remove(DirectionConstants.HORIZONTAL[i]);
         }
      }
   }

   @Unique
   private boolean canMaybeFlowIntoBlock(Level world, BlockState blockState, BlockPos flowTargetPos) {
      return this.canHoldFluid(world, flowTargetPos, blockState, this.getSource());
   }

   @Unique
   private boolean isHoleBelow(LevelReader world, Byte2BooleanOpenHashMap holeCache, byte key, BlockPos flowTargetPos, BlockState targetBlockState) {
      if (holeCache.containsKey(key)) {
         return holeCache.get(key);
      }

      BlockPos downPos = flowTargetPos.below();
      BlockState downBlock = world.getBlockState(downPos);
      boolean holeFound = this.isWaterHole(world, this.getFlowing(), flowTargetPos, targetBlockState, downPos, downBlock);
      holeCache.put(key, holeFound);
      return holeFound;
   }

   @Redirect(
      method = "canHoldFluid(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/Fluid;)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z")
   )
   private boolean isSign(BlockState blockState, TagKey<Block> tagKey, @Local Block block) {
      return tagKey == BlockTags.SIGNS ? block instanceof SignBlock : blockState.is(tagKey);
   }
}
