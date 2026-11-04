package dev.terminalmc.clientsort.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record TransferResultPayload(int result, String message) implements CustomPacketPayload {
   public static final StreamCodec<RegistryFriendlyByteBuf, TransferResultPayload> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, TransferResultPayload::result, ByteBufCodecs.STRING_UTF8, TransferResultPayload::message, TransferResultPayload::new
   );
   public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("clientsort", "transfer_result_s2c");
   public static final Type<TransferResultPayload> TYPE = new Type(ID);

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
