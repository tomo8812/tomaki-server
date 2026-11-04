package net.satisfy.farm_and_charm.core.block;

import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.block.entity.CuttingBoardBlockEntity;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardAssemblyRecipe;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardRecipe;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.util.Strippables;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CuttingBoardBlock extends BaseEntityBlock {
   public static final MapCodec<CuttingBoardBlock> CODEC = simpleCodec(CuttingBoardBlock::new);
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   private static final int CRUMBS = 8;
   public static final TagKey<Item> CLEAVERS = TagKey.create(Registries.ITEM, FarmAndCharm.identifier("cleavers"));
   public static final TagKey<Item> KNIVES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/knife"));
   private static final VoxelShape SHAPE = Shapes.or(Block.box(1.0, 0.0, 3.0, 11.0, 1.0, 13.0), Block.box(11.0, 0.0, 6.0, 15.0, 1.0, 10.0));
   private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

   public CuttingBoardBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   @NotNull
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new CuttingBoardBlockEntity(pos, state);
   }

   @NotNull
   protected RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (!(!stack.isEmpty() && level.getBlockEntity(pos) instanceof CuttingBoardBlockEntity board)) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }

      if (board.hasKnife()) {
         return autoCut(stack, level, pos, player, board);
      }

      if (Strippables.isAxe(stack) && board.isEmpty()) {
         return placeKnife(stack, level, pos, player, board);
      }

      if (stack.is(KNIVES)) {
         return board.isEmpty() ? placeKnife(stack, level, pos, player, board) : chop(stack, level, pos, player, hand, board);
      }

      if (board.isFull() || !canPlace(level, board, stack)) {
         return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
      }

      if (level.isClientSide) {
         return ItemInteractionResult.SUCCESS;
      }

      board.addItem(stack.copyWithCount(1));
      stack.consume(1, player);
      level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 0.7F, 1.2F);
      return ItemInteractionResult.CONSUME;
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      if (level.getBlockEntity(pos) instanceof CuttingBoardBlockEntity board) {
         if (board.hasKnife()) {
            if (board.isCutting()) {
               if (!level.isClientSide) {
                  board.chopPending(level);
               }

               return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
               if (!level.isClientSide) {
                  giveToPlayer(player, board.takeKnife());
                  level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5F, 1.0F);
               }

               return InteractionResult.sidedSuccess(level.isClientSide);
            }
         } else if (board.isEmpty()) {
            return InteractionResult.PASS;
         } else if (level.isClientSide) {
            return InteractionResult.SUCCESS;
         } else {
            Optional<RecipeHolder<CuttingBoardAssemblyRecipe>> assembly = player.isShiftKeyDown() ? Optional.empty() : findAssembly(level, board.getItems());
            if (assembly.isPresent()) {
               ItemStack result = ((CuttingBoardAssemblyRecipe)assembly.get().value())
                  .assemble(new CuttingBoardAssemblyRecipe.Input(board.getItems()), level.registryAccess());
               board.setContents(ItemStack.EMPTY);
               CuttingBoardBlockEntity.output(level, pos, result);
               level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
               return InteractionResult.CONSUME;
            } else {
               giveToPlayer(player, board.removeLastItem());
               level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5F, 1.0F);
               return InteractionResult.CONSUME;
            }
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   private static void giveToPlayer(Player player, ItemStack stack) {
      if (!player.getInventory().add(stack)) {
         player.drop(stack, false);
      }
   }

   private static ItemInteractionResult placeKnife(ItemStack stack, Level level, BlockPos pos, Player player, CuttingBoardBlockEntity board) {
      if (level.isClientSide) {
         return ItemInteractionResult.SUCCESS;
      }

      board.setKnife(stack.copyWithCount(1));
      stack.consume(1, player);
      level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.8F, 1.2F);
      return ItemInteractionResult.CONSUME;
   }

   private static ItemInteractionResult autoCut(ItemStack stack, Level level, BlockPos pos, Player player, CuttingBoardBlockEntity board) {
      if (board.isCutting()) {
         if (!level.isClientSide) {
            board.chopPending(level);
         }

         return ItemInteractionResult.sidedSuccess(level.isClientSide);
      } else {
         boolean valid = Strippables.isAxe(board.getKnife())
            ? Strippables.strip(stack).isPresent()
            : level.getRecipeManager()
               .getRecipeFor((RecipeType)RecipeTypeRegistry.CUTTING_BOARD_RECIPE_TYPE.get(), new SingleRecipeInput(stack), level)
               .isPresent();
         if (!valid) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
         }

         if (!level.isClientSide) {
            board.startCut(stack.copyWithCount(1));
            stack.consume(1, player);
            board.chopPending(level);
         }

         return ItemInteractionResult.sidedSuccess(level.isClientSide);
      }
   }

   private static ItemInteractionResult chop(ItemStack knife, Level level, BlockPos pos, Player player, InteractionHand hand, CuttingBoardBlockEntity board) {
      if (board.getItems().size() != 1) {
         return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
      }

      SingleRecipeInput input = new SingleRecipeInput(board.getItems().getFirst());
      Optional<RecipeHolder<CuttingBoardRecipe>> recipe = level.getRecipeManager()
         .getRecipeFor((RecipeType)RecipeTypeRegistry.CUTTING_BOARD_RECIPE_TYPE.get(), input, level);
      if (recipe.isEmpty()) {
         return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
      }

      if (level.isClientSide) {
         return ItemInteractionResult.SUCCESS;
      }

      CuttingBoardBlockEntity.playChopSound(level, pos, board.getItems().getFirst());
      if (level instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(
            new ItemParticleOption(ParticleTypes.ITEM, board.getItems().getFirst().copyWithCount(1)),
            pos.getX() + 0.5,
            pos.getY() + 0.15,
            pos.getZ() + 0.5,
            8,
            0.15,
            0.05,
            0.15,
            0.08
         );
      }

      if (board.chop() >= CuttingBoardBlockEntity.requiredChops(knife, (CuttingBoardRecipe)recipe.get().value())) {
         ItemStack ingredient = board.getItems().getFirst();
         ItemStack result = ((CuttingBoardRecipe)recipe.get().value()).assemble(input, level.registryAccess());
         ingredient.shrink(1);
         board.setContents(ingredient.isEmpty() ? ItemStack.EMPTY : ingredient);
         CuttingBoardBlockEntity.output(level, pos, result);

         for (ItemStack byproduct : ((CuttingBoardRecipe)recipe.get().value()).getByproducts()) {
            CuttingBoardBlockEntity.output(level, pos, byproduct.copy());
         }

         knife.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
      }

      return ItemInteractionResult.CONSUME;
   }

   private static boolean canPlace(Level level, CuttingBoardBlockEntity board, ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      }

      if (board.isEmpty()
         && level.getRecipeManager()
            .getRecipeFor((RecipeType)RecipeTypeRegistry.CUTTING_BOARD_RECIPE_TYPE.get(), new SingleRecipeInput(stack), level)
            .isPresent()) {
         return true;
      }

      List<ItemStack> candidate = new ArrayList<>(board.getItems());
      candidate.add(stack);
      return level.getRecipeManager()
         .getAllRecipesFor((RecipeType)RecipeTypeRegistry.CUTTING_BOARD_ASSEMBLY_RECIPE_TYPE.get())
         .stream()
         .anyMatch(recipe -> ((CuttingBoardAssemblyRecipe)recipe.value()).accepts(candidate));
   }

   private static Optional<RecipeHolder<CuttingBoardAssemblyRecipe>> findAssembly(Level level, List<ItemStack> items) {
      return level.getRecipeManager()
         .getRecipeFor((RecipeType)RecipeTypeRegistry.CUTTING_BOARD_ASSEMBLY_RECIPE_TYPE.get(), new CuttingBoardAssemblyRecipe.Input(List.copyOf(items)), level);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
      if (!state.is(newState.getBlock())) {
         if (level.getBlockEntity(pos) instanceof CuttingBoardBlockEntity cuttingBoard) {
            for (ItemStack stack : cuttingBoard.removeAllItems()) {
               Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
         }

         super.onRemove(state, level, pos, newState, moved);
      }
   }

   @NotNull
   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPES.get(state.getValue(FACING));
   }

   @NotNull
   protected BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   @NotNull
   protected BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   static {
      for (Direction direction : Plane.HORIZONTAL) {
         SHAPES.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, SHAPE));
      }
   }
}
