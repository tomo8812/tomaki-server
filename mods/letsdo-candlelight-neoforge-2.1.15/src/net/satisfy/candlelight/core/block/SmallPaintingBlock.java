package net.satisfy.candlelight.core.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.foundation.block.FacingBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmallPaintingBlock extends FacingBlock {
   public static final IntegerProperty PAINTING = IntegerProperty.create("painting", 0, 6);
   private static final Map<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap(
      ImmutableMap.of(
         Direction.NORTH,
         Block.box(1.0, 1.0, 15.0, 15.0, 15.0, 16.0),
         Direction.SOUTH,
         Block.box(1.0, 1.0, 0.0, 15.0, 15.0, 1.0),
         Direction.WEST,
         Block.box(15.0, 1.0, 1.0, 16.0, 15.0, 15.0),
         Direction.EAST,
         Block.box(0.0, 1.0, 1.0, 1.0, 15.0, 15.0)
      )
   );

   public SmallPaintingBlock(Properties settings) {
      super(settings);
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH)).setValue(PAINTING, 0));
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return BOUNDING_SHAPES.get(state.getValue(FACING));
   }

   public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
      Direction direction = (Direction)state.getValue(FACING);
      BlockPos blockPos = pos.relative(direction.getOpposite());
      BlockState blockState = world.getBlockState(blockPos);
      return blockState.isFaceSturdy(world, blockPos, direction);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      BlockState blockState = this.defaultBlockState();
      Level world = ctx.getLevel();
      BlockPos pos = ctx.getClickedPos();

      for (Direction direction : ctx.getNearestLookingDirections()) {
         if (direction.getAxis().isHorizontal() && (blockState = (BlockState)blockState.setValue(FACING, direction.getOpposite())).canSurvive(world, pos)) {
            return blockState;
         }
      }

      return null;
   }

   @NotNull
   public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
      return direction.getOpposite() == state.getValue(FACING) && !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : state;
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack itemStack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      if (player.isDiscrete()) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         return world.isClientSide && this.switchPaintings(world, pos, state, player).consumesAction()
            ? ItemInteractionResult.SUCCESS
            : this.switchPaintings(world, pos, state, player);
      }
   }

   private ItemInteractionResult switchPaintings(LevelAccessor world, BlockPos pos, BlockState state, Player player) {
      world.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 0.5F, world.getRandom().nextFloat() * 0.1F + 0.9F);
      int i = (Integer)state.getValue(PAINTING);
      world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
      int next = i + 1;
      if (next <= 6) {
         world.setBlock(pos, (BlockState)state.setValue(PAINTING, next), 3);
      } else {
         world.setBlock(pos, (BlockState)state.setValue(PAINTING, 0), 3);
      }

      return ItemInteractionResult.SUCCESS;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{PAINTING});
   }
}
