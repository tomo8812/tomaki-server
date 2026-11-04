package net.satisfy.candlelight.core.networking.handler;

import dev.architectury.networking.NetworkManager.NetworkReceiver;
import dev.architectury.networking.NetworkManager.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.candlelight.core.block.TypewriterBlock;
import net.satisfy.candlelight.core.block.entity.TypewriterEntity;
import net.satisfy.candlelight.core.networking.packet.SyncTypewriterDataC2SPacket;
import net.satisfy.candlelight.core.registry.ObjectRegistry;

public class TypewriterDataC2SPacketHandler implements NetworkReceiver<SyncTypewriterDataC2SPacket> {
   public void receive(SyncTypewriterDataC2SPacket packet, PacketContext context) {
      Player player = context.getPlayer();
      CompoundTag nbt = packet.nbt();
      BlockPos pos = packet.pos();
      boolean sign = packet.sign();
      ItemStack note = sign
         ? ((Item)ObjectRegistry.NOTE_PAPER_WRITTEN.get()).getDefaultInstance()
         : ((Item)ObjectRegistry.NOTE_PAPER_WRITEABLE.get()).getDefaultInstance();
      context.queue(() -> {
         if (player.level().getBlockEntity(pos) instanceof TypewriterEntity typeWriterEntity) {
            note.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
            typeWriterEntity.addPaper(note);
         }

         BlockState blockState = player.level().getBlockState(pos);
         if (sign) {
            player.level().setBlock(pos, (BlockState)blockState.setValue(TypewriterBlock.FULL, 2), 2);
            player.level().sendBlockUpdated(pos, blockState, (BlockState)blockState.setValue(TypewriterBlock.FULL, 2), 2);
         }
      });
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void receive(Object param1, PacketContext nullx) {
      this.receive((SyncTypewriterDataC2SPacket)var1, nullx);
   }
}
