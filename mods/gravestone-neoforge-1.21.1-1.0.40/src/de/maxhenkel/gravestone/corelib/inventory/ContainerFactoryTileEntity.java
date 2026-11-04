package de.maxhenkel.gravestone.corelib.inventory;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.IContainerFactory;

public class ContainerFactoryTileEntity<T extends AbstractContainerMenu, U extends BlockEntity> implements IContainerFactory<T> {
   private final ContainerFactoryTileEntity.ContainerCreator<T, U> containerCreator;

   public ContainerFactoryTileEntity(ContainerFactoryTileEntity.ContainerCreator<T, U> containerCreator) {
      this.containerCreator = containerCreator;
   }

   public T create(int windowId, Inventory inv, RegistryFriendlyByteBuf data) {
      BlockEntity te = inv.player.level().getBlockEntity(data.readBlockPos());

      try {
         return this.containerCreator.create(windowId, inv, (U)te);
      } catch (ClassCastException e) {
         return null;
      }
   }

   public interface ContainerCreator<T extends AbstractContainerMenu, U extends BlockEntity> {
      T create(int var1, Inventory var2, U var3);
   }
}
