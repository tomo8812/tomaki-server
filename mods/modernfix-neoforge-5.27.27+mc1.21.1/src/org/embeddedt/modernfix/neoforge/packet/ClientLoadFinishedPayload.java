package org.embeddedt.modernfix.neoforge.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import org.embeddedt.modernfix.duck.suspend_integrated_server_during_load.IDeferrableIntegratedServer;

public enum ClientLoadFinishedPayload implements CustomPacketPayload {
   INSTANCE;

   public static final Type<ClientLoadFinishedPayload> TYPE = new Type(IDeferrableIntegratedServer.CLIENT_LOAD_SENTINEL);
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientLoadFinishedPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   // $VF: synthetic method
   private static ClientLoadFinishedPayload[] $values() {
      return new ClientLoadFinishedPayload[]{INSTANCE};
   }
}
