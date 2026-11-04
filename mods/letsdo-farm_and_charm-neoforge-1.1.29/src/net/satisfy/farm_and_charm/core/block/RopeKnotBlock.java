package net.satisfy.farm_and_charm.core.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.foundation.block.mimic.MimicBlock;
import net.satisfy.foundation.block.mimic.MimicBlockEntity;
import org.jetbrains.annotations.NotNull;

public class RopeKnotBlock extends BaseEntityBlock {
   public static final MapCodec<RopeKnotBlock> CODEC = simpleCodec(RopeKnotBlock::new);
   private static final VoxelShape POST = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
   private static final VoxelShape KNOT = Block.box(5.0, 7.0, 5.0, 11.0, 15.0, 11.0);
   private static final VoxelShape SHAPE = Shapes.or(POST, KNOT);
   public static final BooleanProperty NORTH = BooleanProperty.create("north");
   public static final BooleanProperty SOUTH = BooleanProperty.create("south");
   public static final BooleanProperty EAST = BooleanProperty.create("east");
   public static final BooleanProperty WEST = BooleanProperty.create("west");

   public RopeKnotBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(NORTH, false)).setValue(SOUTH, false)).setValue(EAST, false))
            .setValue(WEST, false)
      );
   }

   @NotNull
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{NORTH, SOUTH, EAST, WEST});
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new MimicBlockEntity((BlockEntityType)EntityTypeRegistry.ROPE_KNOT_BLOCK_ENTITY.get(), pos, state);
   }

   @NotNull
   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   @NotNull
   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   @NotNull
   public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
      return MimicBlock.addDrops(super.getDrops(state, builder), builder);
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      if (!(stack.getItem() instanceof ShearsItem) && !player.isShiftKeyDown()) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }

      if (!level.isClientSide && level.getBlockEntity(pos) instanceof MimicBlockEntity be) {
         popResource(level, pos, new ItemStack((ItemLike)ObjectRegistry.ROPE.get()));
         BlockState held = be.getMimicState();
         if (held != null && !held.isAir()) {
            level.setBlock(pos, held, 11);
         } else {
            level.removeBlock(pos, false);
         }

         level.playSound(player, pos, SoundEvents.LEASH_KNOT_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
         if (stack.getItem() instanceof ShearsItem) {
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
         }

         this.updateNeighbors(level, pos);
      }

      return level.isClientSide ? ItemInteractionResult.CONSUME : ItemInteractionResult.SUCCESS;
   }

   @NotNull
   public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      if (dir.getAxis().isHorizontal()) {
         boolean connected = this.connectsTo(neighborState, dir);
         state = (BlockState)state.setValue(this.getPropertyForDirection(dir), connected);
      }

      if (level.getBlockEntity(pos) instanceof MimicBlockEntity be) {
         BlockState held = be.getMimicState();
         if (held != null) {
            BlockState updated = held.updateShape(dir, neighborState, level, pos, neighborPos);
            be.setMimicState(updated);
         }
      }

      return state;
   }

   private BooleanProperty getPropertyForDirection(Direction direction) {
      return switch (direction) {
         case NORTH -> NORTH;
         case SOUTH -> SOUTH;
         case EAST -> EAST;
         case WEST -> WEST;
         default -> throw new IllegalArgumentException();
      };
   }

   public boolean useShapeForLightOcclusion(BlockState state) {
      return true;
   }

   private boolean connectsTo(BlockState neighbor, Direction dirTowardNeighbor) {
      if (neighbor.getBlock() instanceof RopeBlock) {
         Direction needOnNeighbor = dirTowardNeighbor.getOpposite();
         return (Boolean)neighbor.getValue(ropeProp(needOnNeighbor));
      } else {
         return false;
      }
   }

   private static BooleanProperty ropeProp(Direction dir) {
      return switch (dir) {
         case NORTH -> RopeBlock.NORTH;
         case SOUTH -> RopeBlock.SOUTH;
         case EAST -> RopeBlock.EAST;
         case WEST -> RopeBlock.WEST;
         default -> throw new IllegalArgumentException();
      };
   }

   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      Level level = ctx.getLevel();
      BlockPos pos = ctx.getClickedPos();
      BlockState state = this.defaultBlockState();

      for (Direction dir : Plane.HORIZONTAL) {
         state = (BlockState)state.setValue(this.getPropertyForDirection(dir), this.connectsTo(level.getBlockState(pos.relative(dir)), dir));
      }

      return state;
   }

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
      if (!level.isClientSide && !state.is(oldState.getBlock())) {
         this.updateNeighbors(level, pos);
      }

      super.onPlace(state, level, pos, oldState, moved);
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
      if (!level.isClientSide && !state.is(newState.getBlock())) {
         this.updateNeighbors(level, pos);
      }

      super.onRemove(state, level, pos, newState, moved);
   }

   private void updateNeighbors(Level level, BlockPos pos) {
      level.updateNeighborsAt(pos, this);

      for (Direction d : Plane.HORIZONTAL) {
         BlockPos n = pos.relative(d);
         level.updateNeighborsAt(n, level.getBlockState(n).getBlock());
      }
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      VoxelShape shape = SHAPE;
      if ((Boolean)state.getValue(NORTH)) {
         shape = Shapes.or(shape, Block.box(7.0, 11.0, 0.0, 9.0, 13.0, 5.0));
      }

      if ((Boolean)state.getValue(SOUTH)) {
         shape = Shapes.or(shape, Block.box(7.0, 11.0, 11.0, 9.0, 13.0, 16.0));
      }

      if ((Boolean)state.getValue(WEST)) {
         shape = Shapes.or(shape, Block.box(0.0, 11.0, 7.0, 5.0, 13.0, 9.0));
      }

      if ((Boolean)state.getValue(EAST)) {
         shape = Shapes.or(shape, Block.box(11.0, 11.0, 7.0, 16.0, 13.0, 9.0));
      }

      return shape;
   }
}
