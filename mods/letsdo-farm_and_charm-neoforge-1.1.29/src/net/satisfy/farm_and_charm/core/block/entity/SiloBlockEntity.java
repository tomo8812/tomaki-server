package net.satisfy.farm_and_charm.core.block.entity;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.satisfy.farm_and_charm.core.block.SiloBlock;
import net.satisfy.farm_and_charm.core.recipe.SiloRecipe;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.util.ConnectivityHandler;
import net.satisfy.farm_and_charm.core.util.IMultiBlockEntityContainer;
import net.satisfy.foundation.util.ImplementedInventory;
import net.satisfy.foundation.util.LibUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SiloBlockEntity
   extends BlockEntity
   implements IMultiBlockEntityContainer.Inventory,
   ImplementedInventory,
   MenuProvider,
   BlockEntityTicker<SiloBlockEntity> {
   private static final int MAX_WIDTH = 3;
   private static final int MAX_HEIGHT = 9;
   public static final int MAX_CAPACITY = 81;
   private static final int DRY_TIME = 200;
   protected BlockPos controller;
   protected boolean updateConnectivity;
   protected int width;
   protected int height;
   private NonNullList<ItemStack> items = NonNullList.withSize(162, ItemStack.EMPTY);
   private int[] times = new int[81];
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !SiloBlockEntity.class.desiredAssertionStatus();

   public SiloBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.SILO_BLOCK_ENTITY.get(), pos, state);
      this.updateConnectivity = false;
      this.height = 1;
      this.width = 1;
   }

   public static void dropStack(Level level, double x, double y, double z, ItemStack itemStack, Direction direction) {
      double g = EntityType.ITEM.getWidth();
      double h = 1.0 - g;
      double i = g / 2.0;
      double j = Math.floor(x) + level.random.nextDouble() * h + i;
      double k = Math.floor(y) + level.random.nextDouble() * h;
      double l = Math.floor(z) + level.random.nextDouble() * h + i;

      while (!itemStack.isEmpty()) {
         ItemEntity itemEntity = new ItemEntity(level, j, k, l, itemStack.split(level.random.nextInt(21) + 10));
         itemEntity.setDeltaMovement(
            level.random.triangle(direction.getStepX() * 0.4, 0.11485000171139836),
            level.random.triangle(-0.2, 0.11485000171139836),
            level.random.triangle(direction.getStepZ() * 0.4, 0.11485000171139836)
         );
         level.addFreshEntity(itemEntity);
      }
   }

   public void requestConnectivityUpdate() {
      if (this.level != null && !this.level.isClientSide) {
         SiloBlockEntity siloController = this.getControllerBE();
         if (siloController != null) {
            siloController.updateConnectivity = true;
            siloController.setChanged();
         }
      }
   }

   @Override
   public void preventConnectivityUpdate() {
      this.updateConnectivity = false;
   }

   @Override
   public boolean isController() {
      return this.controller == null || this.worldPosition.equals(this.controller);
   }

   @Override
   public BlockPos getController() {
      return this.isController() ? this.worldPosition : this.controller;
   }

   @Override
   public void setController(BlockPos controller) {
      assert this.level != null;
      if (!this.level.isClientSide && !controller.equals(this.controller)) {
         this.controller = controller;
         Containers.dropContents(this.level, this.worldPosition, this);
      }
   }

   @Override
   public <T extends BlockEntity & IMultiBlockEntityContainer> T getControllerBE() {
      if (this.isController()) {
         return (T)this;
      }

      assert this.level != null;
      BlockEntity be = this.level.getBlockEntity(this.controller);
      return (T)(be instanceof IMultiBlockEntityContainer ? be : null);
   }

   @Override
   public void removeController(boolean keepContents) {
      if (this.level != null && !this.level.isClientSide) {
         this.updateConnectivity = true;
         this.controller = null;
         this.width = 1;
         this.height = 1;
         BlockState state = this.getBlockState();
         if (SiloBlock.isSilo(state)) {
            BlockState updatedState = (BlockState)((BlockState)((BlockState)state.setValue(SiloBlock.BOTTOM, true)).setValue(SiloBlock.TOP, true))
               .setValue(SiloBlock.SHAPE, SiloBlock.Shape.NONE);
            if (!state.equals(updatedState)) {
               this.level.setBlock(this.worldPosition, updatedState, 23);
            }
         }
      }
   }

   public void tick(Level level, BlockPos blockPos, BlockState blockState, SiloBlockEntity blockEntity) {
      if (!level.isClientSide) {
         if (this.level == null) {
            this.level = level;
         }

         if (this.updateConnectivity) {
            this.updateConnectivity();
         }

         if (this.isController()) {
            this.dry();
            this.tryDropFinish(blockState);
         }
      }
   }

   public void updateConnectivity() {
      this.updateConnectivity = false;
      if (this.level != null && !this.level.isClientSide) {
         if (this.isController()) {
            ConnectivityHandler.formMulti(this);
         }
      }
   }

   public boolean tryAddItem(ItemStack itemStack) {
      boolean added = false;

      for (int slot = 0; !itemStack.isEmpty() && slot < this.getCapacity(); slot++) {
         if (this.getItem(slot).isEmpty()) {
            this.setItem(slot, itemStack.split(1));
            added = true;
         }
      }

      return added;
   }

   public ItemStack tryRemoveItem() {
      for (int slot = 81 + this.getCapacity() - 1; slot >= 81; slot--) {
         ItemStack stack = this.getItem(slot);
         if (!stack.isEmpty()) {
            return this.removeItem(slot, stack.getCount());
         }
      }

      return ItemStack.EMPTY;
   }

   @Override
   public void notifyMultiUpdated() {
      if (this.level != null) {
         BlockState currentState = this.getBlockState();
         if (SiloBlock.isSilo(currentState)) {
            BlockState updatedState = (BlockState)((BlockState)currentState.setValue(SiloBlock.BOTTOM, this.getController().getY() == this.getBlockPos().getY()))
               .setValue(SiloBlock.TOP, this.getController().getY() + this.height - 1 == this.getBlockPos().getY());
            BlockState controllerState = this.level.getBlockState(this.getController());
            if (controllerState.hasProperty(BlockStateProperties.OPEN)) {
               updatedState = (BlockState)updatedState.setValue(SiloBlock.OPEN, (Boolean)controllerState.getValue(BlockStateProperties.OPEN));
            }

            if (controllerState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
               updatedState = (BlockState)updatedState.setValue(
                  BlockStateProperties.HORIZONTAL_FACING, (Direction)controllerState.getValue(BlockStateProperties.HORIZONTAL_FACING)
               );
            }

            if (!currentState.equals(updatedState)) {
               this.level.setBlock(this.worldPosition, updatedState, 2);
            }
         }

         if (this.isController()) {
            this.updateShape();
         }
      }
   }

   public void updateShape() {
      this.forEachSiloBlock((pos, blockState, xOffset, zOffset) -> {
         SiloBlock.Shape shape = this.computeShape(xOffset, zOffset);
         if (blockState.getValue(SiloBlock.SHAPE) != shape) {
            this.level.setBlock(pos, (BlockState)blockState.setValue(SiloBlock.SHAPE, shape), 2);
         }
      });
   }

   private SiloBlock.Shape computeShape(int xOffset, int zOffset) {
      if (this.width == 2) {
         return xOffset == 0
            ? (zOffset == 0 ? SiloBlock.Shape.NORTH_WEST : SiloBlock.Shape.SOUTH_WEST)
            : (zOffset == 0 ? SiloBlock.Shape.NORTH_EAST : SiloBlock.Shape.SOUTH_EAST);
      }

      if (this.width == 3) {
         return switch (xOffset) {
            case 0 -> zOffset == 0 ? SiloBlock.Shape.NORTH_WEST : (zOffset == 2 ? SiloBlock.Shape.SOUTH_WEST : SiloBlock.Shape.WEST);
            case 1 -> zOffset == 0 ? SiloBlock.Shape.NORTH : (zOffset == 2 ? SiloBlock.Shape.SOUTH : SiloBlock.Shape.NONE);
            case 2 -> zOffset == 0 ? SiloBlock.Shape.NORTH_EAST : (zOffset == 2 ? SiloBlock.Shape.SOUTH_EAST : SiloBlock.Shape.EAST);
            default -> SiloBlock.Shape.NONE;
         };
      } else {
         return SiloBlock.Shape.NONE;
      }
   }

   private void forEachSiloBlock(SiloBlockEntity.SiloBlockAction action) {
      if (this.level != null) {
         for (int yOffset = 0; yOffset < this.height; yOffset++) {
            for (int xOffset = 0; xOffset < this.width; xOffset++) {
               for (int zOffset = 0; zOffset < this.width; zOffset++) {
                  BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
                  BlockState blockState = this.level.getBlockState(pos);
                  if (SiloBlock.isSilo(blockState)) {
                     action.accept(pos, blockState, xOffset, zOffset);
                  }
               }
            }
         }
      }
   }

   private void dry() {
      for (int fresh = 0; fresh < this.getCapacity(); fresh++) {
         ItemStack freshStack = this.getItem(fresh);
         assert this.level != null;
         Optional<RecipeHolder<SiloRecipe>> recipe = SiloBlock.getDryItemRecipe(this.level, freshStack);
         if (recipe.isPresent() && !freshStack.isEmpty()) {
            int dryTime = this.times[fresh];
            if (++dryTime >= 200) {
               for (int finish = 81; finish < 81 + this.getCapacity(); finish++) {
                  if (this.getItem(finish).isEmpty()) {
                     ItemStack finishStack = this.removeItem(fresh, freshStack.getCount());
                     ItemStack outputStack = ((SiloRecipe)recipe.get().value())
                        .getResultItem(this.level.registryAccess())
                        .copyWithCount(finishStack.getCount());
                     this.setItem(finish, SiloBlock.isDryItem(this.level, finishStack) ? outputStack : finishStack);
                     dryTime = 0;
                     this.level.playSound(null, this.worldPosition, SoundEvents.COMPOSTER_FILL_SUCCESS, SoundSource.BLOCKS, 0.7F, 1.0F);
                     break;
                  }
               }
            }

            this.times[fresh] = dryTime;
         }
      }
   }

   private void tryDropFinish(BlockState blockState) {
      if (this.level != null && (Boolean)blockState.getValue(SiloBlock.OPEN)) {
         for (int finish = 81; finish < 81 + this.getCapacity(); finish++) {
            ItemStack finishStack = this.getItem(finish);
            if (!finishStack.isEmpty()) {
               Direction direction = (Direction)blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);

               double x = switch (direction) {
                  case EAST -> this.width;
                  case WEST -> 0.0;
                  default -> this.width / 2.0;
               };

               double z = switch (direction) {
                  case NORTH -> 0.0;
                  case SOUTH -> this.width;
                  default -> this.width / 2.0;
               };
               dropStack(this.level, this.worldPosition.getX() + x, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + z, finishStack, direction);
               return;
            }
         }
      }
   }

   public void open(boolean open) {
      if (this.isController() && this.level != null) {
         this.forEachSiloBlock((pos, blockState, xOffset, zOffset) -> {
            if ((Boolean)blockState.getValue(SiloBlock.OPEN) != open) {
               this.level.setBlock(pos, (BlockState)blockState.setValue(SiloBlock.OPEN, open), 2);
            }
         });
      }
   }

   @Override
   public Axis getMainConnectionAxis() {
      return Axis.Y;
   }

   @Override
   public int getMaxLength(Axis longAxis, int width) {
      return longAxis.isHorizontal() ? this.getMaxWidth() : 9;
   }

   @Override
   public int getMaxWidth() {
      return 3;
   }

   @Override
   public int getHeight() {
      return this.height;
   }

   @Override
   public void setHeight(int height) {
      this.height = height;
   }

   @Override
   public int getWidth() {
      return this.width;
   }

   @Override
   public void setWidth(int width) {
      this.width = width;
   }

   public int getCapacity() {
      return !this.isController() ? 0 : this.width * this.width * this.height;
   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   @Override
   public boolean hasInventory() {
      return this.isController();
   }

   protected void saveAdditional(CompoundTag compoundTag, Provider provider) {
      super.saveAdditional(compoundTag, provider);
      if (this.controller != null) {
         LibUtil.putBlockPos(compoundTag, this.controller);
      }

      compoundTag.putBoolean("Update", this.updateConnectivity);
      compoundTag.putInt("Width", this.width);
      compoundTag.putInt("Height", this.height);
      ContainerHelper.saveAllItems(compoundTag, this.items, provider);
      compoundTag.putIntArray("Times", this.times);
   }

   protected void loadAdditional(CompoundTag compoundTag, Provider provider) {
      super.loadAdditional(compoundTag, provider);
      this.controller = LibUtil.readBlockPos(compoundTag);
      this.updateConnectivity = !compoundTag.contains("Update") || compoundTag.getBoolean("Update");
      this.width = compoundTag.contains("Width") ? compoundTag.getInt("Width") : 1;
      this.height = compoundTag.contains("Height") ? compoundTag.getInt("Height") : 1;
      this.items = NonNullList.withSize(162, ItemStack.EMPTY);
      ContainerHelper.loadAllItems(compoundTag, this.items, provider);
      this.times = compoundTag.contains("Times") ? compoundTag.getIntArray("Times") : new int[81];
   }

   public void setChanged() {
      super.setChanged();
      if (this.level != null && !this.level.isClientSide) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
      }
   }

   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @NotNull
   public CompoundTag getUpdateTag(Provider provider) {
      return this.saveWithoutMetadata(provider);
   }

   @NotNull
   public Component getDisplayName() {
      return Component.empty();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int i, net.minecraft.world.entity.player.Inventory inventory, Player player) {
      return null;
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void tick(Level param1, BlockPos nullx, BlockState nullxx, BlockEntity nullxxx) {
      this.tick(var1, nullx, nullxx, (SiloBlockEntity)nullxxx);
   }

   @FunctionalInterface
   private interface SiloBlockAction {
      void accept(BlockPos var1, BlockState var2, int var3, int var4);
   }
}
