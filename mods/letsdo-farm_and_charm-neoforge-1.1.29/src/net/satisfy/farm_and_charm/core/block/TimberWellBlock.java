package net.satisfy.farm_and_charm.core.block;

import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.block.entity.TimberWellBlockEntity;
import net.satisfy.farm_and_charm.core.registry.SoundEventRegistry;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import net.satisfy.foundation.block.FacingBlock;
import net.satisfy.foundation.overlay.BlockInfoOverlay;
import net.satisfy.foundation.registry.FoundationParticles;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TimberWellBlock extends FacingBlock implements EntityBlock {
   public static final MapCodec<TimberWellBlock> CODEC = simpleCodec(TimberWellBlock::new);
   public static final EnumProperty<TimberWellBlock.TimberWellPart> PART = EnumProperty.create("part", TimberWellBlock.TimberWellPart.class);
   public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 3);
   private static final Supplier<VoxelShape> FOOT_SHAPE_SUPPLIER = () -> {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.joinUnoptimized(shape, Block.box(1.0, 0.0, 0.0, 5.0, 4.0, 16.0), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Block.box(0.0, 4.0, 0.0, 16.0, 6.0, 16.0), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Block.box(0.0, 6.0, 0.0, 4.0, 12.0, 16.0), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Block.box(4.0, 6.0, 0.0, 16.0, 12.0, 2.0), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Block.box(4.0, 6.0, 14.0, 16.0, 12.0, 16.0), BooleanOp.OR);
      return ShapeUtil.rotateShape(Direction.NORTH, Direction.WEST, shape);
   };
   private static final Supplier<VoxelShape> HEAD_SHAPE_SUPPLIER = () -> {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.joinUnoptimized(shape, Block.box(11.0, 0.0, 0.0, 15.0, 4.0, 16.0), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Block.box(0.0, 4.0, 0.0, 16.0, 6.0, 16.0), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Block.box(12.0, 6.0, 0.0, 16.0, 12.0, 16.0), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Block.box(0.0, 6.0, 0.0, 12.0, 12.0, 2.0), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Block.box(0.0, 6.0, 14.0, 12.0, 12.0, 16.0), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Block.box(12.0, 12.0, 6.0, 16.0, 16.0, 10.0), BooleanOp.OR);
      return ShapeUtil.rotateShape(Direction.NORTH, Direction.WEST, shape);
   };
   private static final Supplier<VoxelShape> TOP_SHAPE_SUPPLIER = () -> {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.joinUnoptimized(shape, Block.box(12.0, 0.0, 6.0, 16.0, 8.0, 10.0), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Block.box(6.0, 3.0, 7.0, 12.0, 5.0, 9.0), BooleanOp.OR);
      return ShapeUtil.rotateShape(Direction.NORTH, Direction.WEST, shape);
   };
   private static final Map<Direction, VoxelShape> FOOT_SHAPES = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, FOOT_SHAPE_SUPPLIER.get()));
      }
   });
   private static final Map<Direction, VoxelShape> HEAD_SHAPES = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, HEAD_SHAPE_SUPPLIER.get()));
      }
   });
   private static final Map<Direction, VoxelShape> TOP_SHAPES = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, TOP_SHAPE_SUPPLIER.get()));
      }
   });
   private static final VoxelShape TUB_FILL = Block.box(0.0, 4.0, 0.0, 16.0, 12.0, 16.0);

   public TimberWellBlock(Properties settings) {
      super(settings);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
               .setValue(PART, TimberWellBlock.TimberWellPart.FOOT))
            .setValue(LEVEL, 0)
      );
   }

   @NotNull
   protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
      return CODEC;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      Level level = context.getLevel();
      BlockPos originPos = context.getClickedPos();
      Direction facing = context.getHorizontalDirection().getOpposite().getClockWise();
      BlockPos headPos = originPos.relative(facing);
      BlockPos topPos = headPos.above();
      if (!level.getWorldBorder().isWithinBounds(headPos) || !level.getWorldBorder().isWithinBounds(topPos)) {
         return null;
      } else if (topPos.getY() >= level.getMaxBuildHeight()) {
         return null;
      } else if (!level.getBlockState(headPos).canBeReplaced(context)) {
         return null;
      } else {
         return !level.getBlockState(topPos).canBeReplaced(context)
            ? null
            : (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, facing)).setValue(PART, TimberWellBlock.TimberWellPart.FOOT);
      }
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      if (!level.isClientSide) {
         Direction facing = (Direction)state.getValue(FACING);
         BlockPos headPos = pos.relative(facing);
         BlockPos topPos = headPos.above();
         level.setBlock(topPos, (BlockState)state.setValue(PART, TimberWellBlock.TimberWellPart.TOP), 3);
         level.setBlock(headPos, (BlockState)state.setValue(PART, TimberWellBlock.TimberWellPart.HEAD), 3);
      }
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (!stack.is(Items.BUCKET)) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }

      if ((Integer)state.getValue(LEVEL) <= 0) {
         if (level.isClientSide) {
            BlockInfoOverlay.showNotice(
               getFootPos(pos, state).relative((Direction)state.getValue(FACING)).above(), Component.translatable("message.farm_and_charm.timber_well.empty")
            );
         }

         return ItemInteractionResult.CONSUME;
      } else {
         if (!level.isClientSide) {
            level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            ItemStack filledStack = ItemUtils.createFilledResult(stack, player, new ItemStack(Items.WATER_BUCKET));
            player.setItemInHand(hand, filledStack);
            setWaterLevel(level, pos, state, (Integer)state.getValue(LEVEL) - 1);
         }

         return ItemInteractionResult.SUCCESS;
      }
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      int currentLevel = (Integer)state.getValue(LEVEL);
      BlockPos topPos = getFootPos(pos, state).relative((Direction)state.getValue(FACING)).above();
      if (currentLevel >= 3 || !(level.getBlockEntity(topPos) instanceof TimberWellBlockEntity well && !well.isPumping(level.getGameTime()))) {
         return InteractionResult.PASS;
      }

      if (!hasGroundwater(level, pos, state)) {
         if (level.isClientSide) {
            BlockInfoOverlay.showNotice(topPos, Component.translatable("message.farm_and_charm.timber_well.no_groundwater"));
         }

         return InteractionResult.CONSUME;
      } else {
         if (!level.isClientSide) {
            level.blockEvent(topPos, this, 1, 0);
            setWaterLevel(level, pos, state, currentLevel + 1);
            level.playSound(null, topPos, (SoundEvent)SoundEventRegistry.WELL_PUMP.get(), SoundSource.BLOCKS, 0.5F, 0.9F + level.random.nextFloat() * 0.2F);
            level.playSound(
               null,
               getFootPos(pos, state).relative((Direction)state.getValue(FACING)),
               SoundEvents.GENERIC_SPLASH,
               SoundSource.BLOCKS,
               0.5F,
               0.9F + level.random.nextFloat() * 0.2F
            );
            if (level instanceof ServerLevel serverLevel) {
               serverLevel.sendParticles(
                  (SimpleParticleType)FoundationParticles.WATER_SPLASH.get(),
                  topPos.getX() + 0.5,
                  topPos.getY(),
                  topPos.getZ() + 0.5,
                  10,
                  0.15,
                  0.05,
                  0.15,
                  0.02
               );
            }
         }

         return InteractionResult.sidedSuccess(level.isClientSide);
      }
   }

   public static boolean hasWater(BlockState state) {
      return (Integer)state.getValue(LEVEL) > 0;
   }

   public static boolean drink(Level level, BlockPos pos, BlockState state) {
      int currentLevel = (Integer)state.getValue(LEVEL);
      if (currentLevel <= 0) {
         return false;
      }

      setWaterLevel(level, pos, state, currentLevel - 1);
      return true;
   }

   private static BlockPos getFootPos(BlockPos pos, BlockState state) {
      Direction facing = (Direction)state.getValue(FACING);

      return switch ((TimberWellBlock.TimberWellPart)state.getValue(PART)) {
         case FOOT -> pos;
         case HEAD -> pos.relative(facing.getOpposite());
         case TOP -> pos.below().relative(facing.getOpposite());
      };
   }

   private static void setWaterLevel(Level level, BlockPos pos, BlockState state, int waterLevel) {
      BlockPos footPos = getFootPos(pos, state);
      BlockPos headPos = footPos.relative((Direction)state.getValue(FACING));

      for (BlockPos partPos : new BlockPos[]{footPos, headPos, headPos.above()}) {
         BlockState partState = level.getBlockState(partPos);
         if (partState.getBlock() instanceof TimberWellBlock && (Integer)partState.getValue(LEVEL) != waterLevel) {
            level.setBlock(partPos, (BlockState)partState.setValue(LEVEL, waterLevel), 3);
         }
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return state.getValue(PART) == TimberWellBlock.TimberWellPart.TOP ? new TimberWellBlockEntity(pos, state) : null;
   }

   protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      return blockEntity != null && blockEntity.triggerEvent(id, param);
   }

   protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (state.getValue(PART) == TimberWellBlock.TimberWellPart.FOOT) {
         int currentLevel = (Integer)state.getValue(LEVEL);
         BlockPos topPos = pos.relative((Direction)state.getValue(FACING)).above();
         if (currentLevel < 3 && level.isRainingAt(topPos.above()) && random.nextInt(100) < PlatformHelper.getWellRainFillChance()) {
            setWaterLevel(level, pos, state, currentLevel + 1);
         }
      }
   }

   public static boolean hasGroundwater(LevelReader level, BlockPos pos, BlockState state) {
      Direction facing = (Direction)state.getValue(FACING);
      BlockPos footPos = getFootPos(pos, state);
      BlockPos headPos = footPos.relative(facing);
      int minX = Math.min(footPos.getX(), headPos.getX()) - 1;
      int maxX = Math.max(footPos.getX(), headPos.getX()) + 1;
      int minZ = Math.min(footPos.getZ(), headPos.getZ()) - 1;
      int maxZ = Math.max(footPos.getZ(), headPos.getZ()) + 1;

      for (BlockPos checkPos : BlockPos.betweenClosed(minX, footPos.getY() - PlatformHelper.getWellGroundwaterDepth(), minZ, maxX, footPos.getY() - 1, maxZ)) {
         FluidState fluidState = level.getFluidState(checkPos);
         if (fluidState.is(FluidTags.WATER) && fluidState.isSource()) {
            return true;
         }
      }

      return false;
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
      if (state.getBlock() != newState.getBlock()) {
         this.removeOtherParts(level, pos, state);
         if (level instanceof ServerLevel serverLevel) {
            spawnBreakWater(serverLevel, pos);
         }
      }

      super.onRemove(state, level, pos, newState, movedByPiston);
   }

   @NotNull
   protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      return !this.canSurvive(state, level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
   }

   protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      Direction facing = (Direction)state.getValue(FACING);
      TimberWellBlock.TimberWellPart part = (TimberWellBlock.TimberWellPart)state.getValue(PART);
      if (part == TimberWellBlock.TimberWellPart.FOOT) {
         BlockPos headPos = pos.relative(facing);
         BlockPos topPos = headPos.above();
         return this.isPartOrAir(level, headPos, facing, TimberWellBlock.TimberWellPart.HEAD)
            && this.isPartOrAir(level, topPos, facing, TimberWellBlock.TimberWellPart.TOP);
      } else if (part != TimberWellBlock.TimberWellPart.HEAD) {
         BlockPos headPos = pos.below();
         return this.isPart(level, headPos, facing);
      } else {
         BlockPos footPos = pos.relative(facing.getOpposite());
         BlockPos topPos = pos.above();
         return this.isPartOrAir(level, footPos, facing, TimberWellBlock.TimberWellPart.FOOT)
            && this.isPartOrAir(level, topPos, facing, TimberWellBlock.TimberWellPart.TOP);
      }
   }

   private boolean isPartOrAir(BlockGetter level, BlockPos pos, Direction facing, TimberWellBlock.TimberWellPart expectedPart) {
      BlockState otherState = level.getBlockState(pos);
      return otherState.isAir() ? true : otherState.getBlock() == this && otherState.getValue(FACING) == facing && otherState.getValue(PART) == expectedPart;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{PART, LEVEL});
   }

   private void removeOtherParts(Level level, BlockPos pos, BlockState state) {
      if (!level.isClientSide) {
         Direction facing = (Direction)state.getValue(FACING);
         TimberWellBlock.TimberWellPart part = (TimberWellBlock.TimberWellPart)state.getValue(PART);
         if (part == TimberWellBlock.TimberWellPart.FOOT) {
            BlockPos headPos = pos.relative(facing);
            BlockPos topPos = headPos.above();
            this.clearIfMatches(level, headPos, facing, TimberWellBlock.TimberWellPart.HEAD);
            this.clearIfMatches(level, topPos, facing, TimberWellBlock.TimberWellPart.TOP);
         } else if (part == TimberWellBlock.TimberWellPart.HEAD) {
            BlockPos footPos = pos.relative(facing.getOpposite());
            BlockPos topPos = pos.above();
            this.clearIfMatches(level, footPos, facing, TimberWellBlock.TimberWellPart.FOOT);
            this.clearIfMatches(level, topPos, facing, TimberWellBlock.TimberWellPart.TOP);
         } else {
            BlockPos headPos = pos.below();
            BlockPos footPos = headPos.relative(facing.getOpposite());
            this.clearIfMatches(level, headPos, facing, TimberWellBlock.TimberWellPart.HEAD);
            this.clearIfMatches(level, footPos, facing, TimberWellBlock.TimberWellPart.FOOT);
         }
      }
   }

   private void clearIfMatches(Level level, BlockPos pos, Direction facing, TimberWellBlock.TimberWellPart expectedPart) {
      BlockState otherState = level.getBlockState(pos);
      if (otherState.getBlock() == this) {
         if (otherState.getValue(FACING) == facing) {
            if (otherState.getValue(PART) == expectedPart) {
               level.levelEvent(2001, pos, Block.getId(otherState));
               level.setBlock(pos, Blocks.AIR.defaultBlockState(), 35);
            }
         }
      }
   }

   private boolean isPart(BlockGetter level, BlockPos pos, Direction facing) {
      BlockState state = level.getBlockState(pos);
      return state.getBlock() == this && state.getValue(FACING) == facing && state.getValue(PART) == TimberWellBlock.TimberWellPart.HEAD;
   }

   private static void spawnBreakWater(ServerLevel level, BlockPos pos) {
      SimpleParticleType drip = (SimpleParticleType)FoundationParticles.WATER_DRIP.get();
      RandomSource random = level.random;

      for (int i = 0; i < 4; i++) {
         double x = pos.getX() + 0.2 + random.nextDouble() * 0.6;
         double y = pos.getY() + 0.3 + random.nextDouble() * 0.5;
         double z = pos.getZ() + 0.2 + random.nextDouble() * 0.6;
         level.sendParticles(drip, x, y, z, 0, (random.nextDouble() - 0.5) * 0.2, 0.1 + random.nextDouble() * 0.15, (random.nextDouble() - 0.5) * 0.2, 1.0);
      }

      level.sendParticles(
         (SimpleParticleType)FoundationParticles.WATER_SPLASH.get(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 6, 0.3, 0.2, 0.3, 0.05
      );
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      if (level.isClientSide) {
         if (state.getValue(PART) == TimberWellBlock.TimberWellPart.TOP && hasGroundwater(level, pos, state)) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.075;
            double z = pos.getZ() + 0.5;
            SimpleParticleType drip = (SimpleParticleType)FoundationParticles.WATER_DRIP.get();
            level.addParticle(drip, x, y, z, 0.0, 0.0, 0.0);
            if (random.nextInt(12) == 0) {
               level.addParticle(drip, x, y, z, 0.0, -0.01, 0.0);
            }

            if (random.nextInt(40) == 0) {
               level.playLocalSound(x, pos.getY() + 0.5, z, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 0.2F, 0.9F + random.nextFloat() * 0.2F, false);
            }
         }
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      int earthy = 16767334;
      int gold = 16766720;
      if (Screen.hasShiftDown()) {
         tooltip.add(
            Component.translatable("tooltip.farm_and_charm.timber_well.info_0", new Object[]{PlatformHelper.getWellGroundwaterDepth()})
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy)))
         );
      } else {
         tooltip.add(
            Component.translatable(
                  "tooltip.farm_and_charm.tooltip_information.hold",
                  new Object[]{Component.literal("[SHIFT]").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(gold)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy)))
         );
      }
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      Direction facing = (Direction)state.getValue(FACING);
      TimberWellBlock.TimberWellPart part = (TimberWellBlock.TimberWellPart)state.getValue(PART);

      return switch (part) {
         case FOOT -> (VoxelShape)FOOT_SHAPES.get(facing);
         case HEAD -> (VoxelShape)HEAD_SHAPES.get(facing);
         case TOP -> (VoxelShape)TOP_SHAPES.get(facing);
      };
   }

   @NotNull
   protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
      VoxelShape shape = this.getShape(state, level, pos, CollisionContext.empty());
      return state.getValue(PART) == TimberWellBlock.TimberWellPart.TOP ? shape : Shapes.or(shape, TUB_FILL);
   }

   public enum TimberWellPart implements StringRepresentable {
      FOOT("foot"),
      HEAD("head"),
      TOP("top");

      private final String name;

      TimberWellPart(String name) {
         this.name = name;
      }

      @NotNull
      public String getSerializedName() {
         return this.name;
      }

      // $VF: synthetic method
      private static TimberWellBlock.TimberWellPart[] $values() {
         return new TimberWellBlock.TimberWellPart[]{FOOT, HEAD, TOP};
      }
   }
}
