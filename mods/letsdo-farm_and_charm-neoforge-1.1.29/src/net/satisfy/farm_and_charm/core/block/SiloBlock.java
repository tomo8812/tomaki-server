package net.satisfy.farm_and_charm.core.block;

import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.block.entity.SiloBlockEntity;
import net.satisfy.farm_and_charm.core.recipe.SiloRecipe;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.util.ConnectivityHandler;
import net.satisfy.foundation.block.FacingBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SiloBlock extends FacingBlock implements EntityBlock {
   public static final BooleanProperty TOP = BooleanProperty.create("top");
   public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
   public static final BooleanProperty OPEN = BooleanProperty.create("open");
   public static final EnumProperty<SiloBlock.Shape> SHAPE = EnumProperty.create("shape", SiloBlock.Shape.class);
   static final VoxelShape CAMPFIRE_SMOKE_CLIP = Block.box(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);

   public SiloBlock(Properties settings) {
      super(settings);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(TOP, true)).setValue(BOTTOM, true))
                  .setValue(OPEN, false))
               .setValue(SHAPE, SiloBlock.Shape.NONE))
            .setValue(FACING, Direction.NORTH)
      );
   }

   public static boolean isSilo(ItemStack itemStack) {
      return itemStack.getItem() instanceof BlockItem bi
         && (bi.getBlock() == ObjectRegistry.SILO_WOOD.get() || bi.getBlock() == ObjectRegistry.SILO_COPPER.get());
   }

   public static boolean isSilo(BlockState state) {
      return state.getBlock() instanceof SiloBlock;
   }

   public static boolean isDryItem(Level level, ItemStack itemStack) {
      return getDryItemRecipe(level, itemStack).isPresent();
   }

   public static Optional<RecipeHolder<SiloRecipe>> getDryItemRecipe(Level level, ItemStack itemStack) {
      return level.getRecipeManager()
         .getAllRecipesFor((RecipeType)RecipeTypeRegistry.SILO_RECIPE_TYPE.get())
         .stream()
         .filter(siloRecipe -> ((SiloRecipe)siloRecipe.value()).getInput().test(itemStack))
         .findFirst();
   }

   public Direction getFacing(BlockState state) {
      return (Direction)state.getValue(FACING);
   }

   public BlockState setFacing(BlockState state, Direction facing) {
      return (BlockState)state.setValue(FACING, facing);
   }

   protected ItemInteractionResult useItemOn(
      ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult
   ) {
      if (level.isClientSide) {
         return !itemStack.isEmpty() && !isDryItem(level, itemStack)
            ? (!isSilo(itemStack) && !player.isDiscrete() ? ItemInteractionResult.CONSUME : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION)
            : ItemInteractionResult.SUCCESS;
      }

      if (level.getBlockEntity(blockPos) instanceof SiloBlockEntity siloBE) {
         SiloBlockEntity siloController = siloBE.getControllerBE();
         if (siloController == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         }

         if (itemStack.isEmpty()) {
            if (player.isDiscrete()) {
               ItemStack returnStack = siloBE.tryRemoveItem();
               if (!returnStack.isEmpty()) {
                  player.addItem(itemStack);
                  level.playSound(null, blockPos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
               }
            } else {
               boolean isOpen = (Boolean)blockState.getValue(OPEN);
               siloController.open(!isOpen);
               level.playSound(null, blockPos, isOpen ? SoundEvents.IRON_TRAPDOOR_CLOSE : SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            return ItemInteractionResult.SUCCESS;
         }

         if (isDryItem(level, itemStack) && siloController.tryAddItem(itemStack)) {
            level.playSound(null, blockPos, SoundEvents.COMPOSTER_FILL_SUCCESS, SoundSource.BLOCKS, 1.0F, 1.0F);
            return ItemInteractionResult.SUCCESS;
         }
      }

      return !isSilo(itemStack) && !player.isDiscrete() ? ItemInteractionResult.CONSUME : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
      if (oldState.getBlock() != state.getBlock()) {
         if (!notify) {
            if (world.getBlockEntity(pos) instanceof SiloBlockEntity siloBlockEntity) {
               siloBlockEntity.requestConnectivityUpdate();
            }
         }
      }
   }

   public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean movedByPiston) {
      if (!level.isClientSide) {
         if (level.getBlockEntity(pos) instanceof SiloBlockEntity siloBlockEntity) {
            siloBlockEntity.requestConnectivityUpdate();
         }

         super.neighborChanged(state, level, pos, block, fromPos, movedByPiston);
      }
   }

   public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState newState, boolean moved) {
      if (blockState.hasBlockEntity() && (blockState.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
         if (!(level.getBlockEntity(blockPos) instanceof SiloBlockEntity siloBE)) {
            return;
         }

         Containers.dropContents(level, blockPos, siloBE);
         level.removeBlockEntity(blockPos);
         ConnectivityHandler.splitMulti(siloBE);
      }
   }

   public boolean skipRendering(BlockState blockState, BlockState blockState2, Direction direction) {
      return blockState2.is(this) || super.skipRendering(blockState, blockState2, direction);
   }

   public boolean hasAnalogOutputSignal(BlockState blockState) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
      return level.getBlockEntity(blockPos, (BlockEntityType)EntityTypeRegistry.SILO_BLOCK_ENTITY.get())
         .<Integer>map(AbstractContainerMenu::getRedstoneSignalFromContainer)
         .orElse(0);
   }

   @NotNull
   public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return context == CollisionContext.empty() ? CAMPFIRE_SMOKE_CLIP : state.getShape(world, pos);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{TOP, BOTTOM, OPEN, SHAPE, FACING});
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level tickerLevel, BlockState state, BlockEntityType<T> type) {
      return tickerLevel.isClientSide ? null : (level, pos, blockState, blockEntity) -> {
         if (blockEntity instanceof SiloBlockEntity silo) {
            silo.tick(level, pos, blockState, silo);
         }
      };
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new SiloBlockEntity(pos, state);
   }

   @NotNull
   public BlockState mirror(BlockState state, Mirror mirror) {
      if (mirror == Mirror.NONE) {
         return state;
      }

      return switch ((SiloBlock.Shape)state.getValue(SHAPE)) {
         case NORTH -> (BlockState)state.setValue(SHAPE, SiloBlock.Shape.SOUTH);
         case NORTH_EAST -> (BlockState)state.setValue(SHAPE, SiloBlock.Shape.SOUTH_WEST);
         case EAST -> (BlockState)state.setValue(SHAPE, SiloBlock.Shape.WEST);
         case SOUTH_EAST -> (BlockState)state.setValue(SHAPE, SiloBlock.Shape.NORTH_WEST);
         case SOUTH -> (BlockState)state.setValue(SHAPE, SiloBlock.Shape.NORTH);
         case SOUTH_WEST -> (BlockState)state.setValue(SHAPE, SiloBlock.Shape.NORTH_EAST);
         case WEST -> (BlockState)state.setValue(SHAPE, SiloBlock.Shape.EAST);
         case NORTH_WEST -> (BlockState)state.setValue(SHAPE, SiloBlock.Shape.SOUTH_EAST);
         default -> state;
      };
   }

   @NotNull
   public BlockState rotate(BlockState state, Rotation rotation) {
      for (int i = 0; i < rotation.ordinal(); i++) {
         state = this.rotateOnce(state);
      }

      return state;
   }

   private BlockState rotateOnce(BlockState state) {
      return switch ((SiloBlock.Shape)state.getValue(SHAPE)) {
         case NORTH -> (BlockState)state.setValue(SHAPE, SiloBlock.Shape.EAST);
         case NORTH_EAST -> (BlockState)state.setValue(SHAPE, SiloBlock.Shape.SOUTH_EAST);
         case EAST -> (BlockState)state.setValue(SHAPE, SiloBlock.Shape.SOUTH);
         case SOUTH_EAST -> (BlockState)state.setValue(SHAPE, SiloBlock.Shape.SOUTH_WEST);
         case SOUTH -> (BlockState)state.setValue(SHAPE, SiloBlock.Shape.WEST);
         case SOUTH_WEST -> (BlockState)state.setValue(SHAPE, SiloBlock.Shape.NORTH_WEST);
         case WEST -> (BlockState)state.setValue(SHAPE, SiloBlock.Shape.NORTH);
         case NORTH_WEST -> (BlockState)state.setValue(SHAPE, SiloBlock.Shape.NORTH_EAST);
         default -> state;
      };
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      list.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.GRAY));
   }

   public enum Shape implements StringRepresentable {
      NONE,
      NORTH,
      NORTH_EAST,
      EAST,
      SOUTH_EAST,
      SOUTH,
      SOUTH_WEST,
      WEST,
      NORTH_WEST;

      @NotNull
      public String getSerializedName() {
         return this.name().toLowerCase();
      }

      // $VF: synthetic method
      private static SiloBlock.Shape[] $values() {
         return new SiloBlock.Shape[]{NONE, NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST};
      }
   }
}
