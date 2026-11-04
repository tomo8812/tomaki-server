package dev.terminalmc.clientsort.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record StackFillResultPayload(int result, String message) implements CustomPacketPayload {
   public static final StreamCodec<RegistryFriendlyByteBuf, StackFillResultPayload> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, StackFillResultPayload::result, ByteBufCodecs.STRING_UTF8, StackFillResultPayload::message, StackFillResultPayload::new
   );
   public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("clientsort", "stack_fill_result_s2c");
   public static final Type<StackFillResultPayload> TYPE = new Type(ID);

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
