package com.kishku7.chunksmith.lod.net;

import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class CsLodChannel {
   private static volatile Consumer<byte[]> clientSink;

   private CsLodChannel() {
   }

   public static void setClientSink(Consumer<byte[]> sink) {
      clientSink = sink;
   }

   private static void dispatchClient(byte[] data) {
      Consumer<byte[]> sink = clientSink;
      if (sink != null) {
         sink.accept(data);
      }
   }

   public static void register() {
   }

   public static void registerPayloads(IEventBus modBus) {
      modBus.addListener(CsLodChannel::onRegisterPayloads);
   }

   private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("1").optional();
      registrar.playBidirectional(CsLodChannel.Payload.TYPE, CsLodChannel.Payload.CODEC, (payload, context) -> context.enqueueWork(() -> {
         if (context.player() instanceof ServerPlayer player) {
            CsLodServerNet.receive(player, payload.data());
         } else {
            dispatchClient(payload.data());
         }
      }));
   }

   public static void send(ServerPlayer player, byte[] data) {
      PacketDistributor.sendToPlayer(player, new CsLodChannel.Payload(data), new CustomPacketPayload[0]);
   }

   public record Payload(byte[] data) implements CustomPacketPayload {
      public static final Type<CsLodChannel.Payload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("chunksmith", "lod"));
      public static final StreamCodec<RegistryFriendlyByteBuf, CsLodChannel.Payload> CODEC = StreamCodec.of(
         CsLodChannel.Payload::write, CsLodChannel.Payload::read
      );

      private static void write(RegistryFriendlyByteBuf buf, CsLodChannel.Payload payload) {
         buf.writeByteArray(payload.data());
      }

      private static CsLodChannel.Payload read(RegistryFriendlyByteBuf buf) {
         return new CsLodChannel.Payload(buf.readByteArray());
      }

      public Type<? extends CustomPacketPayload> type() {
         return TYPE;
      }
   }
}
