package de.maxhenkel.gravestone.corelib.inventory;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TileEntityContainerProvider implements MenuProvider {
   private TileEntityContainerProvider.ContainerCreator container;
   private BlockEntity tileEntity;

   public TileEntityContainerProvider(TileEntityContainerProvider.ContainerCreator container, BlockEntity tileEntity) {
      this.container = container;
      this.tileEntity = tileEntity;
   }

   public Component getDisplayName() {
      return Component.translatable(this.tileEntity.getBlockState().getBlock().getDescriptionId());
   }

   public static void openGui(Player player, BlockEntity tileEntity, TileEntityContainerProvider.ContainerCreator containerCreator) {
      if (player instanceof ServerPlayer serverPlayer) {
         serverPlayer.openMenu(
            new TileEntityContainerProvider(containerCreator, tileEntity), packetBuffer -> packetBuffer.writeBlockPos(tileEntity.getBlockPos())
         );
      }
   }

   public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
      return this.container.create(i, playerInventory, playerEntity);
   }

   public interface ContainerCreator {
      AbstractContainerMenu create(int var1, Inventory var2, Player var3);
   }
}
