package net.satisfy.herbalbrews.core.blocks;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.herbalbrews.core.blocks.entity.JugBlockEntity;
import net.satisfy.herbalbrews.core.items.DrinkBlockItem;
import net.satisfy.herbalbrews.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;

public class JugBlock extends Block implements EntityBlock {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final IntegerProperty FILL_STAGE = IntegerProperty.create("fill_stage", 0, 3);
   private static final VoxelShape SHAPE = Shapes.box(0.1875, 0.0, 0.1875, 0.8125, 0.875, 0.8125);

   public JugBlock(Properties settings) {
      super(settings);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(FILL_STAGE, 0));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, FILL_STAGE});
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite())).setValue(FILL_STAGE, 0);
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new JugBlockEntity(pos, state);
   }

   protected ItemInteractionResult useItemOn(
      ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult
   ) {
      if (level.isClientSide) {
         return ItemInteractionResult.SUCCESS;
      }

      ItemStack heldItem = player.getItemInHand(interactionHand);
      if (level.getBlockEntity(blockPos) instanceof JugBlockEntity jug) {
         if (heldItem.getItem() instanceof DrinkBlockItem) {
            if (jug.getDrinks().size() < 3) {
               ItemStack drinkToAdd = heldItem.copy();
               drinkToAdd.setCount(1);
               jug.addDrink(drinkToAdd);
               heldItem.shrink(1);
               int fillStage = jug.getDrinks().size();
               level.setBlock(blockPos, (BlockState)blockState.setValue(FILL_STAGE, fillStage), 3);
               level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
               return ItemInteractionResult.SUCCESS;
            } else {
               return ItemInteractionResult.FAIL;
            }
         } else if (!jug.getDrinks().isEmpty()) {
            int duration = PlatformHelper.getJugEffectDuration();
            jug.applyEffects(player, duration);
            jug.clearDrinks();
            level.setBlock(blockPos, (BlockState)blockState.setValue(FILL_STAGE, 0), 3);
            level.playSound(null, blockPos, SoundEvents.HONEY_DRINK, SoundSource.BLOCKS, 1.0F, 1.0F);
            return ItemInteractionResult.SUCCESS;
         } else {
            return ItemInteractionResult.FAIL;
         }
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
      if (Screen.hasShiftDown()) {
         tooltip.add(
            Component.translatable("tooltip.herbalbrews.description.title").withStyle(style -> style.withColor(TextColor.fromRgb(5025616)).withBold(true))
         );
         tooltip.add(
            Component.translatable("tooltip.herbalbrews.description.jug_1").withStyle(style -> style.withColor(TextColor.fromRgb(5025616)).withItalic(false))
         );
         tooltip.add(Component.empty());
         tooltip.add(
            Component.translatable("tooltip.herbalbrews.description.jug_2").withStyle(style -> style.withColor(TextColor.fromRgb(5025616)).withItalic(false))
         );
      } else {
         tooltip.add(Component.translatable("tooltip.herbalbrews.canbeplaced").withStyle(ChatFormatting.GRAY));
      }
   }

   public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock())) {
         if (world.getBlockEntity(pos) instanceof JugBlockEntity jug) {
            for (ItemStack drink : jug.getDrinks()) {
               Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), drink);
            }
         }

         super.onRemove(state, world, pos, newState, isMoving);
      }
   }
}
