package net.satisfy.brewery.core.block;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.brewery.core.block.property.BrewMaterial;
import net.satisfy.brewery.core.registry.ObjectRegistry;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;

public class BrewKettleTopBlock extends BrewingstationBlock {
   public static final Map<Direction, VoxelShape> SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL.stream().toList()) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, Shapes.box(0.0, 0.0, 0.0625, 0.0625, 0.875, 1.0)));
      }
   });

   public BrewKettleTopBlock(Properties properties) {
      super(properties);
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE.get(state.getValue(FACING));
   }

   protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
      return true;
   }

   protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
      return 1.0F;
   }

   @NotNull
   protected RenderShape getRenderShape(BlockState state) {
      return RenderShape.INVISIBLE;
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult
   ) {
      BlockPos kettlePos = blockPos.below();
      BlockState kettleState = level.getBlockState(kettlePos);
      return kettleState.getBlock() instanceof BrewKettleBlock kettle
         ? kettle.useItemOn(itemStack, kettleState, level, kettlePos, player, interactionHand, blockHitResult.withPosition(kettlePos))
         : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   @NotNull
   @Override
   public ItemStack getCloneItemStack(LevelReader getter, BlockPos pos, BlockState state) {
      return switch ((BrewMaterial)state.getValue(MATERIAL)) {
         case COPPER -> new ItemStack((ItemLike)ObjectRegistry.COPPER_BREWINGSTATION.get());
         case WOOD -> new ItemStack((ItemLike)ObjectRegistry.WOODEN_BREWINGSTATION.get());
         case NETHERITE -> new ItemStack((ItemLike)ObjectRegistry.NETHERITE_BREWINGSTATION.get());
      };
   }
}
