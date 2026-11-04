package net.satisfy.candlelight.core.block;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.candlelight.core.block.entity.SideTableBlockEntity;
import net.satisfy.candlelight.core.registry.ObjectRegistry;
import net.satisfy.foundation.block.LineConnectingBlock;
import net.satisfy.foundation.block.LineConnectingType;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SideTableBlock extends LineConnectingBlock implements EntityBlock {
   public static final EnumProperty<SideTableBlock.Light> LIGHT = EnumProperty.create("light", SideTableBlock.Light.class);
   public static final BooleanProperty LIT = BlockStateProperties.LIT;
   private static final VoxelShape SINGLE_SHAPE = Shapes.or(
      Block.box(2.0, 8.0, 2.0, 14.0, 10.0, 14.0),
      new VoxelShape[]{
         Block.box(3.0, 0.0, 3.0, 5.0, 8.0, 5.0),
         Block.box(11.0, 0.0, 3.0, 13.0, 8.0, 5.0),
         Block.box(3.0, 0.0, 11.0, 5.0, 8.0, 13.0),
         Block.box(11.0, 0.0, 11.0, 13.0, 8.0, 13.0),
         Block.box(4.0, 2.0, 4.0, 12.0, 3.0, 12.0)
      }
   );
   private static final VoxelShape END_SHAPE = Shapes.or(
      Block.box(2.0, 8.0, 2.0, 16.0, 10.0, 14.0),
      new VoxelShape[]{Block.box(3.0, 0.0, 3.0, 5.0, 8.0, 5.0), Block.box(3.0, 0.0, 11.0, 5.0, 8.0, 13.0), Block.box(4.0, 2.0, 4.0, 16.0, 3.0, 12.0)}
   );
   private static final VoxelShape MIDDLE_SHAPE = Shapes.or(Block.box(0.0, 8.0, 2.0, 16.0, 10.0, 14.0), Block.box(0.0, 2.0, 4.0, 16.0, 3.0, 12.0));
   private static final Map<LineConnectingType, Map<Direction, VoxelShape>> SHAPES = (Map<LineConnectingType, Map<Direction, VoxelShape>>)Util.make(
      new EnumMap(LineConnectingType.class), map -> {
         for (LineConnectingType type : LineConnectingType.values()) {
            VoxelShape base = switch (type) {
               case NONE -> SINGLE_SHAPE;
               case MIDDLE -> MIDDLE_SHAPE;
               default -> END_SHAPE;
            };
            Map<Direction, VoxelShape> rotated = new EnumMap<>(Direction.class);

            for (Direction direction : Plane.HORIZONTAL) {
               Direction modelDirection = type == LineConnectingType.RIGHT ? direction.getOpposite() : direction;
               rotated.put(direction, ShapeUtil.rotateShape(Direction.NORTH, modelDirection, base));
            }

            map.put(type, rotated);
         }
      }
   );

   public SideTableBlock(Properties properties) {
      super(properties.lightLevel(state -> state.getValue(LIGHT) != SideTableBlock.Light.NONE && state.getValue(LIT) ? 15 : 0));
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIGHT, SideTableBlock.Light.NONE)).setValue(LIT, false)
      );
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPES.get(state.getValue(TYPE)).get(state.getValue(FACING));
   }

   @NotNull
   public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
      if (!state.canSurvive(world, pos)) {
         world.scheduleTick(pos, this, 1);
      }

      return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{LIGHT, LIT});
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new SideTableBlockEntity(pos, state);
   }

   private static SideTableBlock.Light lightFor(ItemStack stack) {
      if (stack.is(((Block)ObjectRegistry.LAMP.get()).asItem())) {
         return SideTableBlock.Light.LAMP;
      } else {
         return !stack.is(Items.LANTERN) && !stack.is(Items.SOUL_LANTERN) ? SideTableBlock.Light.NONE : SideTableBlock.Light.LANTERN;
      }
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      if (level.getBlockEntity(pos) instanceof SideTableBlockEntity table) {
         boolean hasLight = state.getValue(LIGHT) != SideTableBlock.Light.NONE;
         ItemStack items = table.getItems();
         if (stack.isEmpty()) {
            if (level.isClientSide()) {
               return ItemInteractionResult.SUCCESS;
            }

            if (player.isShiftKeyDown()) {
               if (hasLight) {
                  giveBack(player, table.getLight());
                  table.setLight(ItemStack.EMPTY);
                  level.setBlockAndUpdate(pos, (BlockState)((BlockState)state.setValue(LIGHT, SideTableBlock.Light.NONE)).setValue(LIT, false));
                  level.playSound(null, pos, SoundEvents.LANTERN_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                  return ItemInteractionResult.SUCCESS;
               } else if (!items.isEmpty()) {
                  giveBack(player, items);
                  table.setItems(ItemStack.EMPTY);
                  level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.6F, 1.0F);
                  return ItemInteractionResult.SUCCESS;
               } else {
                  return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
               }
            } else if (hasLight) {
               boolean lit = !(Boolean)state.getValue(LIT);
               level.setBlockAndUpdate(pos, (BlockState)state.setValue(LIT, lit));
               level.playSound(null, pos, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 1.0F, lit ? 1.1F : 0.9F);
               return ItemInteractionResult.SUCCESS;
            } else if (!items.isEmpty()) {
               giveBack(player, items.split(1));
               table.setItems(items);
               level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.6F, 1.0F);
               return ItemInteractionResult.SUCCESS;
            } else {
               return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
         } else {
            if (hasLight) {
               return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            SideTableBlock.Light light = lightFor(stack);
            if (light != SideTableBlock.Light.NONE && items.isEmpty()) {
               if (!level.isClientSide()) {
                  table.setLight(stack.copyWithCount(1));
                  level.setBlockAndUpdate(pos, (BlockState)((BlockState)state.setValue(LIGHT, light)).setValue(LIT, true));
                  level.playSound(null, pos, SoundEvents.LANTERN_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                  stack.consume(1, player);
               }

               return ItemInteractionResult.sidedSuccess(level.isClientSide());
            } else {
               boolean canStack = items.isEmpty() || ItemStack.isSameItemSameComponents(items, stack) && items.getCount() < SideTableBlockEntity.maxFor(items);
               if (!canStack) {
                  return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
               }

               if (!level.isClientSide()) {
                  ItemStack placed = items.isEmpty() ? stack.copyWithCount(1) : items.copyWithCount(items.getCount() + 1);
                  table.setItems(placed);
                  level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 0.7F, 1.2F);
                  stack.consume(1, player);
               }

               return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }
         }
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   private static void giveBack(Player player, ItemStack stack) {
      if (!stack.isEmpty() && !player.getInventory().add(stack)) {
         player.drop(stack, false);
      }
   }

   protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
      if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof SideTableBlockEntity table) {
         Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), table.getItems());
         Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), table.getLight());
      }

      super.onRemove(state, level, pos, newState, moved);
   }

   public enum Light implements StringRepresentable {
      NONE("none"),
      LAMP("lamp"),
      LANTERN("lantern");

      private final String name;

      Light(String name) {
         this.name = name;
      }

      @NotNull
      public String getSerializedName() {
         return this.name;
      }

      // $VF: synthetic method
      private static SideTableBlock.Light[] $values() {
         return new SideTableBlock.Light[]{NONE, LAMP, LANTERN};
      }
   }
}
