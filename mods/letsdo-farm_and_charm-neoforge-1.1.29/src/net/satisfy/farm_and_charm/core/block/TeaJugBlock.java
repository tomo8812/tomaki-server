package net.satisfy.farm_and_charm.core.block;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.foundation.block.FacingBlock;
import org.jetbrains.annotations.NotNull;

public class TeaJugBlock extends FacingBlock {
   public static final IntegerProperty FILL = IntegerProperty.create("fill", 0, 2);

   public TeaJugBlock(Properties settings) {
      super(settings);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FILL, 2));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{FILL});
   }

   @NotNull
   public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
      InteractionHand hand = player.getUsedItemHand();
      ItemStack itemStack = player.getItemInHand(hand);
      if (itemStack.getItem() == Items.GLASS_BOTTLE && (Integer)state.getValue(FILL) > 0) {
         ItemStack teaItemStack = new ItemStack(this.getTeaItem(state));
         player.addItem(teaItemStack);
         int newFillLevel = (Integer)state.getValue(FILL) - 1;
         if (newFillLevel > 0) {
            world.setBlock(pos, (BlockState)state.setValue(FILL, newFillLevel), 3);
         } else {
            world.destroyBlock(pos, true);
         }

         if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
         }

         return InteractionResult.sidedSuccess(world.isClientSide);
      } else {
         return super.useWithoutItem(state, world, pos, player, hit);
      }
   }

   public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
      VoxelShape shape = world.getBlockState(pos.below()).getShape(world, pos.below());
      Direction direction = Direction.UP;
      return Block.isFaceFull(shape, direction);
   }

   public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
      if (!state.canSurvive(world, pos)) {
         world.destroyBlock(pos, true);
      }
   }

   @NotNull
   public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
      if (!state.canSurvive(world, pos)) {
         world.scheduleTick(pos, this, 1);
      }

      return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
   }

   private Item getTeaItem(BlockState state) {
      if (this.equals(ObjectRegistry.STRAWBERRY_TEA.get())) {
         return (Item)ObjectRegistry.STRAWBERRY_TEA_CUP.get();
      } else if (this.equals(ObjectRegistry.NETTLE_TEA.get())) {
         return (Item)ObjectRegistry.NETTLE_TEA_CUP.get();
      } else {
         return this.equals(ObjectRegistry.RIBWORT_TEA.get()) ? (Item)ObjectRegistry.RIBWORT_TEA_CUP.get() : Items.AIR;
      }
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      list.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.GRAY));
   }
}
