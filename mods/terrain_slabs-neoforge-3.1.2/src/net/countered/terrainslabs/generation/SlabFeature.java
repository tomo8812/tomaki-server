package net.countered.terrainslabs.generation;

import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.countered.terrainslabs.block.ModSlabsMap;
import net.countered.terrainslabs.block.customslabs.specialslabs.CustomSlab;
import net.countered.terrainslabs.platform.PlatformConfigHooks;
import net.countered.terrainslabs.registries.ModBlocksRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SlabFeature extends Feature<NoneFeatureConfiguration> {
   public SlabFeature(Codec<NoneFeatureConfiguration> codec) {
      super(codec);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
      if (PlatformConfigHooks.isSlabGenerationEnabled()) {
         WorldGenLevel level = context.level();
         BlockPos origin = context.origin();
         this.generateSlabs(level, origin);
         return true;
      } else {
         return false;
      }
   }

   private void generateSlabs(WorldGenLevel level, BlockPos origin) {
      Set<BlockPos> botSlabPositions = new HashSet<>();
      Set<BlockPos> topSlabPositions = new HashSet<>();
      Set<BlockPos> extendedPositions = new HashSet<>();
      ChunkPos chunkPos = new ChunkPos(origin);
      int minY = level.getMinBuildHeight();
      int offsetXZ = PlatformConfigHooks.isCornerSlabsEnabled() ? 1 : 0;

      for (int x = -offsetXZ; x < 16 + offsetXZ; x++) {
         for (int z = -offsetXZ; z < 16 + offsetXZ; z++) {
            int worldX = chunkPos.getMinBlockX() + x;
            int worldZ = chunkPos.getMinBlockZ() + z;
            int maxY = level.getHeight(Types.WORLD_SURFACE_WG, worldX, worldZ);

            for (int y = maxY; y >= minY; y--) {
               BlockPos currentPos = new BlockPos(worldX, y, worldZ);
               if (this.shouldPlaceBottomSlab(level, currentPos, y == maxY - 1, false)) {
                  botSlabPositions.add(currentPos);
               } else if (this.shouldPlaceTopSlab(level, currentPos)) {
                  topSlabPositions.add(currentPos);
               }
            }
         }
      }

      this.placeBottomSlabs(level, botSlabPositions);
      this.placeTopSlabs(level, topSlabPositions);
      if (PlatformConfigHooks.getSlabRunLength() > 1) {
         this.generateExtended(level, botSlabPositions, extendedPositions);
      } else if (PlatformConfigHooks.isCornerSlabsEnabled()) {
         this.calculateCornerPositions(level, botSlabPositions, extendedPositions);
         this.placeBottomSlabs(level, extendedPositions);
      }
   }

   private void placeBottomSlabs(WorldGenLevel level, Set<BlockPos> slabPositions) {
      for (BlockPos pos : slabPositions) {
         this.placeBottomSlab(level, pos);
      }
   }

   private void placeTopSlabs(WorldGenLevel level, Set<BlockPos> topSlabPositions) {
      for (BlockPos pos : topSlabPositions) {
         this.placeTopSlab(level, pos);
      }
   }

   private void generateExtended(WorldGenLevel level, Set<BlockPos> botSlabPositions, Set<BlockPos> extendedPositions) {
      for (int i = 1; i < PlatformConfigHooks.getSlabRunLength(); i++) {
         extendedPositions.clear();
         this.calculateExtendedPositions(level, botSlabPositions, extendedPositions);
         this.placeBottomSlabs(level, extendedPositions);
         botSlabPositions = Set.copyOf(extendedPositions);
      }
   }

   private void calculateExtendedPositions(WorldGenLevel level, Set<BlockPos> botSlabPositions, Set<BlockPos> extendedPositions) {
      for (BlockPos pos : botSlabPositions) {
         for (Direction direction : Direction.values()) {
            BlockPos extendedPos = pos.relative(direction);
            if (this.shouldPlaceBottomSlab(level, extendedPos, false, true)) {
               extendedPositions.add(extendedPos);
            }
         }
      }
   }

   private void calculateCornerPositions(WorldGenLevel level, Set<BlockPos> botSlabPositions, Set<BlockPos> cornerPositions) {
      int[][] diagonals = new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

      for (BlockPos currentPos : botSlabPositions) {
         for (int[] d : diagonals) {
            int nx = currentPos.getX() + d[0];
            int nz = currentPos.getZ() + d[1];
            int y = currentPos.getY();
            if (botSlabPositions.contains(new BlockPos(nx, y, nz))) {
               BlockPos corner1 = new BlockPos(currentPos.getX(), y, nz);
               BlockPos corner2 = new BlockPos(nx, y, currentPos.getZ());
               if (this.shouldPlaceBottomSlab(level, corner1, false, true)) {
                  cornerPositions.add(corner1);
               }

               if (this.shouldPlaceBottomSlab(level, corner2, false, true)) {
                  cornerPositions.add(corner2);
               }
            }
         }
      }
   }

   private boolean shouldPlaceBottomSlab(WorldGenLevel level, BlockPos currentPos, boolean isMaxY, boolean neighbourCanBeSlab) {
      if (!isPosUpDownValid(level, currentPos)) {
         return false;
      }

      Biome biome = (Biome)level.getBiome(currentPos).value();
      return isMaxY && biome.shouldFreeze(level, currentPos, false) ? false : this.validSurroundingBottom(level, currentPos, neighbourCanBeSlab);
   }

   private static boolean isPosUpDownValid(WorldGenLevel level, BlockPos currentPos) {
      BlockState currentBlockState = level.getBlockState(currentPos);
      if (!currentBlockState.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).isEmpty() && !(currentBlockState.getBlock() instanceof SlabBlock)) {
         return false;
      }

      BlockState blockAboveState = level.getBlockState(currentPos.above());
      if (!blockAboveState.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).isEmpty()) {
         return false;
      }

      BlockState blockBelowState = level.getBlockState(currentPos.below());
      return ModSlabsMap.getSlabForBlock(blockBelowState.getBlock()) != null;
   }

   private boolean validSurroundingBottom(WorldGenLevel level, BlockPos currentPos, boolean neighbourCanBeSlab) {
      boolean validNeighbors = false;

      for (Direction direction : Plane.HORIZONTAL) {
         BlockPos neighborPos = currentPos.relative(direction);
         BlockPos oppositePos = currentPos.relative(direction.getOpposite());
         BlockPos belowOppositePos = oppositePos.below();
         BlockState neighborState = level.getBlockState(neighborPos);
         BlockState oppositeState = level.getBlockState(belowOppositePos);
         BlockState belowOppositeState = level.getBlockState(belowOppositePos);
         if (neighborState.is(Blocks.LAVA)) {
            return false;
         }

         if ((
               !neighborState.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).isEmpty() && !(neighborState.getBlock() instanceof SlabBlock)
                  || neighborState.getBlock() instanceof SlabBlock && neighbourCanBeSlab
            )
            && !(oppositeState.getBlock() instanceof SlabBlock)
            && !belowOppositeState.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).isEmpty()
            && !(belowOppositeState.getBlock() instanceof SlabBlock)
            && level.getBlockState(neighborPos.above()).getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).isEmpty()) {
            validNeighbors = true;
         }
      }

      return validNeighbors;
   }

   private void placeBottomSlab(WorldGenLevel level, BlockPos pos) {
      BlockPos blockBelowPos = pos.below();
      BlockPos blockAbovePos = pos.above();
      BlockState blockAboveState = level.getBlockState(blockAbovePos);
      BlockState currentBlockState = level.getBlockState(pos);
      BlockState blockBelowState = level.getBlockState(blockBelowPos);
      if (!(currentBlockState.getBlock() instanceof SlabBlock)) {
         Block slab = ModSlabsMap.getSlabForBlock(blockBelowState.getBlock());
         if (slab != null) {
            BlockState slabState = slab.defaultBlockState();
            if (blockAboveState.getBlock() instanceof DoublePlantBlock) {
               this.setBlockState(level, blockAbovePos, Blocks.AIR.defaultBlockState());
               if (!blockAboveState.getFluidState().isEmpty()) {
                  this.setBlockState(level, blockAbovePos, Blocks.WATER.defaultBlockState());
               }
            }

            if (ModSlabsMap.SOIL_SLAB_BLOCKS.contains(slabState.getBlock())) {
               this.setBlockState(level, blockBelowPos, Blocks.DIRT.defaultBlockState());
            }

            if (slabState.is((Block)ModBlocksRegistry.WARPED_NYLIUM_SLAB.get()) || slabState.is((Block)ModBlocksRegistry.CRIMSON_NYLIUM_SLAB.get())) {
               this.setBlockState(level, blockBelowPos, Blocks.NETHERRACK.defaultBlockState());
            }

            slabState = this.updateBottomWaterloggedState(currentBlockState, blockAboveState, slabState);
            this.setBlockState(level, pos, (BlockState)slabState.setValue(CustomSlab.GENERATED, true));
         }
      }
   }

   private BlockState updateBottomWaterloggedState(BlockState currentBlockState, BlockState blockAboveState, BlockState slabState) {
      return !slabState.hasProperty(BlockStateProperties.WATERLOGGED)
            || !currentBlockState.is(Blocks.WATER) && !blockAboveState.is(Blocks.WATER) && !currentBlockState.getFluidState().is(FluidTags.WATER)
         ? slabState
         : (BlockState)slabState.setValue(BlockStateProperties.WATERLOGGED, true);
   }

   private boolean shouldPlaceTopSlab(WorldGenLevel level, BlockPos currentPos) {
      BlockPos blockAbovePos = currentPos.above();
      BlockPos blockBelowPos = currentPos.below();
      BlockState currentBlockState = level.getBlockState(currentPos);
      if (!currentBlockState.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) {
         return false;
      }

      BlockState blockBelowState = level.getBlockState(blockBelowPos);
      if (blockBelowState.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) {
         return false;
      }

      BlockState blockAboveState = level.getBlockState(blockAbovePos);
      return ModSlabsMap.getSlabForBlock(blockAboveState.getBlock()) == null ? false : this.validSurroundingTop(level, currentPos);
   }

   private boolean validSurroundingTop(WorldGenLevel level, BlockPos currentPos) {
      boolean validNeighbors = false;
      boolean hasWaterNeighbor = false;
      boolean hasAirNeighbor = false;

      for (Direction direction : Plane.HORIZONTAL) {
         BlockPos neighborPos = currentPos.relative(direction);
         BlockPos oppositePos = currentPos.relative(direction.getOpposite());
         BlockPos aboveOppositePos = oppositePos.above();
         BlockState neighborState = level.getBlockState(neighborPos);
         BlockState oppositeState = level.getBlockState(oppositePos);
         BlockState aboveOppositeState = level.getBlockState(aboveOppositePos);
         if (neighborState.is(Blocks.GLOW_LICHEN) || neighborState.is(Blocks.LAVA)) {
            return false;
         }

         if (neighborState.is(Blocks.WATER)) {
            hasWaterNeighbor = true;
         } else if (neighborState.is(Blocks.AIR)) {
            hasAirNeighbor = true;
         }

         if ((neighborState.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO) || neighborState.getBlock() instanceof SlabBlock)
            && aboveOppositeState.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)
            && !oppositeState.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)
            && !(oppositeState.getBlock() instanceof SlabBlock)
            && !level.getBlockState(neighborPos.below()).isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) {
            validNeighbors = true;
         }
      }

      return validNeighbors && (!hasWaterNeighbor || !hasAirNeighbor);
   }

   private void placeTopSlab(WorldGenLevel level, BlockPos pos) {
      Boolean waterlogged = this.isTopStateWaterlogged(level, pos);
      BlockState blockAboveState = level.getBlockState(pos.above());
      BlockState slabState = Objects.requireNonNull(ModSlabsMap.getSlabForBlock(blockAboveState.getBlock())).defaultBlockState();
      if (!slabState.getBlock().equals(Blocks.AIR)) {
         if (ModSlabsMap.SOIL_SLAB_BLOCKS.contains(slabState.getBlock())) {
            slabState = ((Block)ModBlocksRegistry.DIRT_SLAB.get()).defaultBlockState();
         }

         if (slabState.is((Block)ModBlocksRegistry.WARPED_NYLIUM_SLAB.get()) || slabState.is((Block)ModBlocksRegistry.CRIMSON_NYLIUM_SLAB.get())) {
            slabState = ((Block)ModBlocksRegistry.NETHERRACK_SLAB.get()).defaultBlockState();
         }

         this.setBlockState(
            level,
            pos,
            (BlockState)((BlockState)((BlockState)slabState.setValue(CustomSlab.GENERATED, true)).setValue(BlockStateProperties.SLAB_TYPE, SlabType.TOP))
               .setValue(BlockStateProperties.WATERLOGGED, waterlogged)
         );
      }
   }

   private boolean isTopStateWaterlogged(LevelAccessor levelAccessor, BlockPos currentPos) {
      if (!levelAccessor.getBlockState(currentPos.below()).is(Blocks.WATER)) {
         return false;
      }

      for (Direction direction : Plane.HORIZONTAL) {
         if (levelAccessor.getBlockState(currentPos.relative(direction)).is(Blocks.WATER)) {
            return true;
         }
      }

      return false;
   }

   private void setBlockState(LevelAccessor world, BlockPos pos, BlockState state) {
      world.setBlock(pos, state, 3);
   }
}
