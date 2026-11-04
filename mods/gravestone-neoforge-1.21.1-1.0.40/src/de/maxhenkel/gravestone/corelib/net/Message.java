package de.maxhenkel.gravestone.corelib.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface Message<T extends Message<T>> extends CustomPacketPayload {
   PacketFlow getExecutingSide();

   default void executeServerSide(IPayloadContext context) {
   }

   default void executeClientSide(IPayloadContext context) {
   }

   T fromBytes(RegistryFriendlyByteBuf var1);

   void toBytes(RegistryFriendlyByteBuf var1);

   Type<T> type();
}
