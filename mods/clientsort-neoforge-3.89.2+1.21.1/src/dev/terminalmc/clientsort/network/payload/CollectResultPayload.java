package dev.terminalmc.clientsort.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record CollectResultPayload(int result, String message, String id) implements CustomPacketPayload {
   public static final StreamCodec<RegistryFriendlyByteBuf, CollectResultPayload> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT,
      CollectResultPayload::result,
      ByteBufCodecs.STRING_UTF8,
      CollectResultPayload::message,
      ByteBufCodecs.STRING_UTF8,
      CollectResultPayload::id,
      CollectResultPayload::new
   );
   public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("clientsort", "collect_result_s2c");
   public static final Type<CollectResultPayload> TYPE = new Type(ID);

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
