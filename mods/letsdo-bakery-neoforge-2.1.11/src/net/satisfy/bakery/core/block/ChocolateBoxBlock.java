package net.satisfy.bakery.core.block;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.bakery.core.registry.SoundEventRegistry;
import net.satisfy.foundation.block.FacingBlock;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;

public class ChocolateBoxBlock extends FacingBlock {
   public static final BooleanProperty OPEN = BooleanProperty.create("open");
   public static final IntegerProperty PIECES = IntegerProperty.create("pieces", 0, 6);
   private static final Map<Direction, VoxelShape> CLOSED_SHAPES = createShapes(Block.box(3.0, 0.0, 4.0, 13.0, 4.0, 12.0));
   private static final Map<Direction, VoxelShape> OPEN_SHAPES = createShapes(
      Shapes.or(Block.box(3.0, 0.0, 4.0, 13.0, 3.0, 12.0), Block.box(4.0, 0.0, 12.0, 14.0, 8.0, 13.0))
   );

   public ChocolateBoxBlock(Properties settings) {
      super(settings);
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(OPEN, false)).setValue(PIECES, 6));
   }

   private static Map<Direction, VoxelShape> createShapes(VoxelShape north) {
      Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);

      for (Direction direction : Plane.HORIZONTAL) {
         shapes.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, north).optimize());
      }

      return shapes;
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (!(Boolean)state.getValue(OPEN)) {
         if (!level.isClientSide) {
            level.setBlock(pos, (BlockState)state.setValue(OPEN, true), 3);
            level.playSound(null, pos, (SoundEvent)SoundEventRegistry.UNWRAP_GIFT.get(), SoundSource.BLOCKS, 1.0F, 0.9F + level.getRandom().nextFloat() * 0.2F);
            level.gameEvent(player, GameEvent.BLOCK_OPEN, pos);
         }

         return ItemInteractionResult.sidedSuccess(level.isClientSide);
      } else {
         int pieces = (Integer)state.getValue(PIECES);
         if (pieces != 0 && player.canEat(false)) {
            if (!level.isClientSide) {
               level.playSound(null, pos, SoundEvents.FOX_EAT, SoundSource.PLAYERS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
               player.getFoodData().eat(1, 0.4F);
               level.gameEvent(player, GameEvent.EAT, pos);
               level.setBlock(pos, (BlockState)state.setValue(PIECES, pieces - 1), 3);
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide);
         } else {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         }
      }
   }

   @NotNull
   public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      return direction == Direction.DOWN && !state.canSurvive(level, pos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      return level.getBlockState(pos.below()).isSolid();
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{OPEN, PIECES});
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return (state.getValue(OPEN) ? OPEN_SHAPES : CLOSED_SHAPES).get(state.getValue(FACING));
   }
}
