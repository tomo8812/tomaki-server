package dev.terminalmc.clientsort.network;

import dev.terminalmc.clientsort.network.handler.CollectHandler;
import dev.terminalmc.clientsort.network.handler.SortHandler;
import dev.terminalmc.clientsort.network.handler.StackFillHandler;
import dev.terminalmc.clientsort.network.handler.TransferHandler;
import dev.terminalmc.clientsort.network.payload.CollectPayload;
import dev.terminalmc.clientsort.network.payload.CollectResultPayload;
import dev.terminalmc.clientsort.network.payload.SortPayload;
import dev.terminalmc.clientsort.network.payload.SortResultPayload;
import dev.terminalmc.clientsort.network.payload.StackFillPayload;
import dev.terminalmc.clientsort.network.payload.StackFillResultPayload;
import dev.terminalmc.clientsort.network.payload.TransferPayload;
import dev.terminalmc.clientsort.network.payload.TransferResultPayload;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class Registration {
   public static final List<Registration.RegisterablePayloadC2S<?>> PAYLOADS_C2S = List.of(
      new Registration.RegisterablePayloadC2S<>(CollectPayload.TYPE, CollectPayload.STREAM_CODEC, CollectHandler::handle),
      new Registration.RegisterablePayloadC2S<>(SortPayload.TYPE, SortPayload.STREAM_CODEC, SortHandler::handle),
      new Registration.RegisterablePayloadC2S<>(StackFillPayload.TYPE, StackFillPayload.STREAM_CODEC, StackFillHandler::handle),
      new Registration.RegisterablePayloadC2S<>(TransferPayload.TYPE, TransferPayload.STREAM_CODEC, TransferHandler::handle)
   );
   public static List<Registration.RegisterablePayloadS2C<?>> PAYLOADS_S2C = List.of(
      new Registration.RegisterablePayloadS2C(CollectResultPayload.TYPE, CollectResultPayload.STREAM_CODEC),
      new Registration.RegisterablePayloadS2C(SortResultPayload.TYPE, SortResultPayload.STREAM_CODEC),
      new Registration.RegisterablePayloadS2C(StackFillResultPayload.TYPE, StackFillResultPayload.STREAM_CODEC),
      new Registration.RegisterablePayloadS2C(TransferResultPayload.TYPE, TransferResultPayload.STREAM_CODEC)
   );

   private Registration() {
   }

   public abstract static class RegisterablePayload<T extends CustomPacketPayload> {
      public final Type<T> type;
      public final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

      public RegisterablePayload(Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
         this.type = type;
         this.streamCodec = streamCodec;
      }
   }

   public static class RegisterablePayloadC2S<T extends CustomPacketPayload> extends Registration.RegisterablePayload<T> {
      public final Registration.RegisterablePayloadC2S.PayloadHandlerC2S<T> handler;

      public RegisterablePayloadC2S(
         Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, Registration.RegisterablePayloadC2S.PayloadHandlerC2S<T> handler
      ) {
         super(type, streamCodec);
         this.handler = handler;
      }

      @FunctionalInterface
      public interface PayloadHandlerC2S<T extends CustomPacketPayload> {
         void accept(T var1, MinecraftServer var2, ServerPlayer var3);
      }
   }

   public static class RegisterablePayloadS2C<T extends CustomPacketPayload> extends Registration.RegisterablePayload<T> {
      public RegisterablePayloadS2C(Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
         super(type, streamCodec);
      }
   }
}
