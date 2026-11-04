package dev.emi.emi.network;

import dev.emi.emi.runtime.EmiLog;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CreateItemC2SPacket implements EmiPacket {
   private final int mode;
   private final ItemStack stack;

   public CreateItemC2SPacket(int mode, ItemStack stack) {
      this.mode = mode;
      this.stack = stack;
   }

   public CreateItemC2SPacket(RegistryFriendlyByteBuf buf) {
      this(buf.readByte(), (ItemStack)ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
   }

   @Override
   public void write(RegistryFriendlyByteBuf buf) {
      buf.writeByte(this.mode);
      ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, this.stack);
   }

   @Override
   public void apply(Player player) {
      if ((player.hasPermissions(2) || player.isCreative()) && player.containerMenu != null) {
         if (this.stack.isEmpty()) {
            if (this.mode == 1 && !player.containerMenu.getCarried().isEmpty()) {
               EmiLog.info(player.getName() + " deleted " + player.containerMenu.getCarried());
               player.containerMenu.setCarried(this.stack);
            }
         } else {
            EmiLog.info(player.getName() + " cheated in " + this.stack);
            if (this.mode == 0) {
               player.getInventory().placeItemBackInInventory(this.stack);
            } else if (this.mode == 1) {
               player.containerMenu.setCarried(this.stack);
            }
         }
      }
   }

   public Type<CreateItemC2SPacket> type() {
      return EmiNetwork.CREATE_ITEM;
   }
}
