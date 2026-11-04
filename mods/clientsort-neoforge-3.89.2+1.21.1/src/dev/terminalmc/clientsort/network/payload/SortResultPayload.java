package dev.terminalmc.clientsort.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SortResultPayload(int result, String message) implements CustomPacketPayload {
   public static final StreamCodec<RegistryFriendlyByteBuf, SortResultPayload> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, SortResultPayload::result, ByteBufCodecs.STRING_UTF8, SortResultPayload::message, SortResultPayload::new
   );
   public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("clientsort", "sort_result_s2c");
   public static final Type<SortResultPayload> TYPE = new Type(ID);

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
