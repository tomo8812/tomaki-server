package net.satisfy.farm_and_charm.core.block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.block.entity.FeedingTroughBlockEntity;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import net.satisfy.foundation.block.LineConnectingBlock;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;

public class FeedingTroughBlock extends LineConnectingBlock implements EntityBlock {
   public static final IntegerProperty SIZE = IntegerProperty.create("size", 0, 4);
   private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      return Shapes.joinUnoptimized(shape, Shapes.box(0.0, 0.0, 0.125, 1.0, 0.625, 0.875), BooleanOp.OR);
   };
   public static final Map<Direction, VoxelShape> SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL.stream().toList()) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
      }
   });

   public FeedingTroughBlock(Properties settings) {
      super(settings);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(SIZE, 0));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{SIZE});
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      if (!stack.is(TagRegistry.FEEDING_TROUGH_FOOD)) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }

      if (level.getBlockEntity(pos) instanceof FeedingTroughBlockEntity trough) {
         if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
         }

         if (!trough.addOne(stack.getItem())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         }

         if (!player.getAbilities().instabuild) {
            stack.shrink(1);
         }

         return ItemInteractionResult.SUCCESS;
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new FeedingTroughBlockEntity(pos, state);
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE.get(state.getValue(FACING));
   }

   @NotNull
   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
      int earthy = 16767334;
      int gold = 16766720;
      if (Screen.hasShiftDown()) {
         tooltip.add(Component.translatable("tooltip.farm_and_charm.feeding_trough.info_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
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
}
