package dev.terminalmc.clientsort.client.network;

import dev.terminalmc.clientsort.client.network.handler.CollectResultHandler;
import dev.terminalmc.clientsort.client.network.handler.SortResultHandler;
import dev.terminalmc.clientsort.client.network.handler.StackFillResultHandler;
import dev.terminalmc.clientsort.client.network.handler.TransferResultHandler;
import dev.terminalmc.clientsort.network.Registration;
import dev.terminalmc.clientsort.network.payload.CollectResultPayload;
import dev.terminalmc.clientsort.network.payload.SortResultPayload;
import dev.terminalmc.clientsort.network.payload.StackFillResultPayload;
import dev.terminalmc.clientsort.network.payload.TransferResultPayload;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public class ClientRegistration {
   public static List<ClientRegistration.RegisterablePayloadS2C<?>> PAYLOADS_S2C = List.of(
      new ClientRegistration.RegisterablePayloadS2C<>(CollectResultPayload.TYPE, CollectResultPayload.STREAM_CODEC, CollectResultHandler::handle),
      new ClientRegistration.RegisterablePayloadS2C<>(SortResultPayload.TYPE, SortResultPayload.STREAM_CODEC, SortResultHandler::handle),
      new ClientRegistration.RegisterablePayloadS2C<>(StackFillResultPayload.TYPE, StackFillResultPayload.STREAM_CODEC, StackFillResultHandler::handle),
      new ClientRegistration.RegisterablePayloadS2C<>(TransferResultPayload.TYPE, TransferResultPayload.STREAM_CODEC, TransferResultHandler::handle)
   );

   private ClientRegistration() {
   }

   public static class RegisterablePayloadS2C<T extends CustomPacketPayload> extends Registration.RegisterablePayload<T> {
      public final ClientRegistration.RegisterablePayloadS2C.PayloadHandlerS2C<T> handler;

      public RegisterablePayloadS2C(
         Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, ClientRegistration.RegisterablePayloadS2C.PayloadHandlerS2C<T> handler
      ) {
         super(type, streamCodec);
         this.handler = handler;
      }

      @FunctionalInterface
      public interface PayloadHandlerS2C<T extends CustomPacketPayload> {
         void accept(T var1, Minecraft var2, LocalPlayer var3);
      }
   }
}
