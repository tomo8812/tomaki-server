package net.satisfy.candlelight.core.networking.handler;

import dev.architectury.networking.NetworkManager.NetworkReceiver;
import dev.architectury.networking.NetworkManager.PacketContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import net.satisfy.candlelight.core.networking.packet.SignNoteC2SPacket;
import net.satisfy.candlelight.core.registry.ObjectRegistry;

public class SignNoteC2SPacketHandler implements NetworkReceiver<SignNoteC2SPacket> {
   public void receive(SignNoteC2SPacket packet, PacketContext context) {
      Player player = context.getPlayer();
      CompoundTag stack = packet.stack();
      int slot = packet.slot();
      boolean sign = packet.sign();
      ItemStack itemStack = new ItemStack((ItemLike)ObjectRegistry.NOTE_PAPER_WRITTEN.get());
      itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(stack));
      if (sign) {
         player.getInventory().setItem(slot, itemStack);
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void receive(Object param1, PacketContext nullx) {
      this.receive((SignNoteC2SPacket)var1, nullx);
   }
}
