package net.satisfy.bakery.core.block;

import java.util.LinkedList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.bakery.core.registry.EntityTypeRegistry;
import net.satisfy.bakery.core.registry.StorageTypeRegistry;
import net.satisfy.foundation.storage.StorageBlock;
import net.satisfy.foundation.storage.StorageBlockEntity;
import org.jetbrains.annotations.NotNull;

public class CakeStandBlock extends StorageBlock {
   public static VoxelShape SHAPE = makeShape();

   public BlockEntityType<?> blockEntityType() {
      return (BlockEntityType<?>)EntityTypeRegistry.STORAGE_ENTITY.get();
   }

   public static VoxelShape makeShape() {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.join(shape, Shapes.box(0.125, 0.5000625, 0.125, 0.125, 1.0000625, 0.875), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.125, 0.5000625, 0.125, 0.875, 1.0000625, 0.125), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.125, 0.5000625, 0.875, 0.875, 1.0000625, 0.875), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.125, 1.0000625, 0.125, 0.875, 1.0000625, 0.875), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.875, 0.5000625, 0.125, 0.875, 1.0000625, 0.875), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.4375, 1.0, 0.4375, 0.5625, 1.0625, 0.5625), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.4375, 0.125, 0.4375, 0.5625, 0.4375, 0.5625), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.25, 0.375, 0.25, 0.75, 0.4375, 0.75), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.125, 0.4375, 0.125, 0.875, 0.5, 0.875), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.375, 0.0625, 0.375, 0.625, 0.1875, 0.625), BooleanOp.OR);
      return Shapes.join(shape, Shapes.box(0.3125, 0.0, 0.3125, 0.6875, 0.0625, 0.6875), BooleanOp.OR);
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   public CakeStandBlock(Properties settings) {
      super(settings);
   }

   public int findFirstEmpty(NonNullList<ItemStack> inv) {
      for (int i = 0; i < this.size(); i++) {
         ItemStack stack = (ItemStack)inv.get(i);
         if (stack.isEmpty()) {
            return i;
         }
      }

      return Integer.MIN_VALUE;
   }

   public int maxItemStack() {
      return 64;
   }

   private int findStackTarget(NonNullList<ItemStack> inv, ItemStack stack) {
      for (int i = 0; i < this.size(); i++) {
         ItemStack current = (ItemStack)inv.get(i);
         if (!current.isEmpty() && ItemStack.isSameItemSameComponents(current, stack) && current.getCount() < this.maxItemStack()) {
            return i;
         }
      }

      return this.findFirstEmpty(inv);
   }

   private int findLastFull(NonNullList<ItemStack> inv) {
      for (int i = this.size() - 1; i >= 0; i--) {
         if (!((ItemStack)inv.get(i)).isEmpty()) {
            return i;
         }
      }

      return Integer.MIN_VALUE;
   }

   private void insert(Level level, BlockPos pos, Player player, StorageBlockEntity storage, ItemStack stack, int index) {
      if (!level.isClientSide) {
         ItemStack current = (ItemStack)storage.getInventory().get(index);
         int moved = Math.min(stack.getCount(), this.maxItemStack() - current.getCount());
         storage.setStack(index, current.isEmpty() ? stack.copyWithCount(moved) : current.copyWithCount(current.getCount() + moved));
         if (!player.getAbilities().instabuild) {
            stack.shrink(moved);
         }

         level.playSound(null, pos, this.getAddSound(level, pos, player, index), SoundSource.BLOCKS, 1.0F, 1.0F);
         level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
      }
   }

   private void takeOne(Level level, BlockPos pos, Player player, StorageBlockEntity storage, int index) {
      if (!level.isClientSide) {
         ItemStack current = (ItemStack)storage.getInventory().get(index);
         ItemStack taken = current.copyWithCount(1);
         storage.setStack(index, current.getCount() > 1 ? current.copyWithCount(current.getCount() - 1) : ItemStack.EMPTY);
         if (!player.getInventory().add(taken)) {
            player.drop(taken, false);
         }

         level.playSound(null, pos, this.getRemoveSound(level, pos, player, index), SoundSource.BLOCKS, 1.0F, 1.0F);
         level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
      }
   }

   public int findFirstFull(NonNullList<ItemStack> inv) {
      for (int i = 0; i < this.size(); i++) {
         ItemStack stack = (ItemStack)inv.get(i);
         if (!stack.isEmpty()) {
            return i;
         }
      }

      return Integer.MIN_VALUE;
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack itemStack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      if (world.getBlockEntity(pos) instanceof StorageBlockEntity shelfBlockEntity) {
         List<Item> items = new LinkedList<>();
         shelfBlockEntity.getInventory().forEach(stackx -> {
            if (!stackx.isEmpty()) {
               items.add(stackx.getItem());
            }
         });
         if (player.isShiftKeyDown()) {
            boolean cCake = false;

            for (Item item : items) {
               if (item instanceof BlockItem) {
                  cCake = true;
                  break;
               }
            }

            if (cCake) {
               this.remove(world, pos, player, shelfBlockEntity, 0);
               return ItemInteractionResult.sidedSuccess(world.isClientSide());
            }

            int i = this.findFirstFull(shelfBlockEntity.getInventory());
            if (i != Integer.MIN_VALUE) {
               this.remove(world, pos, player, shelfBlockEntity, i);
               return ItemInteractionResult.sidedSuccess(world.isClientSide());
            }
         } else {
            ItemStack stack = player.getItemInHand(hand);
            if (!stack.isEmpty() && this.canInsertStack(stack)) {
               if (stack.getItem() instanceof BlockItem) {
                  if (items.isEmpty()) {
                     this.add(world, pos, player, shelfBlockEntity, stack, 0);
                     return ItemInteractionResult.sidedSuccess(world.isClientSide());
                  }
               } else if (!(((ItemStack)shelfBlockEntity.getInventory().get(0)).getItem() instanceof BlockItem)) {
                  int i = this.findStackTarget(shelfBlockEntity.getInventory(), stack);
                  if (i != Integer.MIN_VALUE) {
                     this.insert(world, pos, player, shelfBlockEntity, stack, i);
                     return ItemInteractionResult.sidedSuccess(world.isClientSide());
                  }
               }
            } else if (stack.isEmpty() && this.maxItemStack() > 1) {
               int i = this.findLastFull(shelfBlockEntity.getInventory());
               if (i != Integer.MIN_VALUE) {
                  this.takeOne(world, pos, player, shelfBlockEntity, i);
                  return ItemInteractionResult.sidedSuccess(world.isClientSide());
               }
            }
         }
      }

      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   public int size() {
      return 3;
   }

   public ResourceLocation type() {
      return StorageTypeRegistry.CAKE_STAND;
   }

   public Direction[] unAllowedDirections() {
      return new Direction[0];
   }

   public boolean canInsertStack(ItemStack stack) {
      return true;
   }

   public int getSection(Float x, Float y) {
      return 0;
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      list.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.GRAY));
   }
}
