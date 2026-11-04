package net.satisfy.candlelight.core.block;

import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.candlelight.Candlelight;
import net.satisfy.candlelight.core.registry.EntityTypeRegistry;
import net.satisfy.candlelight.core.registry.StorageTypeRegistry;
import net.satisfy.foundation.storage.StorageBlock;
import net.satisfy.foundation.storage.StorageBlockEntity;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JewelryBoxBlock extends StorageBlock {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   private static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 5.0, 11.0);
   public static final TagKey<Item> RINGS = TagKey.create(Registries.ITEM, Candlelight.identifier("rings"));

   public BlockEntityType<?> blockEntityType() {
      return (BlockEntityType<?>)EntityTypeRegistry.STORAGE_BLOCK_ENTITY.get();
   }

   public JewelryBoxBlock(Properties settings) {
      super(settings);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(OPEN, false));
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack itemStack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (world.isClientSide) {
         return ItemInteractionResult.SUCCESS;
      }

      if (player.isShiftKeyDown() && blockEntity instanceof StorageBlockEntity) {
         world.setBlock(pos, (BlockState)state.setValue(OPEN, !(Boolean)state.getValue(OPEN)), 3);
         return ItemInteractionResult.SUCCESS;
      }

      if (blockEntity instanceof StorageBlockEntity shelfBlockEntity) {
         Optional<Tuple<Float, Float>> optional = ShapeUtil.getRelativeHitCoordinatesForBlockFace(
            hit, (Direction)state.getValue(FACING), this.unAllowedDirections()
         );
         if (optional.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         } else {
            Tuple<Float, Float> ff = optional.get();
            int i = this.getSection((Float)ff.getA(), (Float)ff.getB());
            if (i == Integer.MIN_VALUE) {
               return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            } else {
               ItemStack stack = player.getItemInHand(hand);
               if (!((ItemStack)shelfBlockEntity.getInventory().get(i)).isEmpty()) {
                  this.remove(world, pos, player, shelfBlockEntity, i);
                  return ItemInteractionResult.sidedSuccess(false);
               } else if (!stack.isEmpty() && this.canInsertStack(stack)) {
                  this.add(world, pos, player, shelfBlockEntity, stack, i);
                  return ItemInteractionResult.sidedSuccess(false);
               } else {
                  return ItemInteractionResult.CONSUME;
               }
            }
         }
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public int size() {
      return 1;
   }

   public ResourceLocation type() {
      return StorageTypeRegistry.JEWELRY_BOX;
   }

   public Direction[] unAllowedDirections() {
      return new Direction[0];
   }

   public boolean canInsertStack(ItemStack stack) {
      return stack.is(RINGS);
   }

   public int getSection(Float f, Float y) {
      float oneS = 1.0F;
      int nSection = (int)(f / oneS);
      return -nSection;
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
      return true;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      return (BlockState)this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, OPEN});
   }

   @NotNull
   public BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   @NotNull
   public BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
      tooltip.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.GRAY));
   }
}
