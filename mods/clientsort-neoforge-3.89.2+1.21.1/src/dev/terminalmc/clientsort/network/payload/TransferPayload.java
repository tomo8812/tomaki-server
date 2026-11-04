package dev.terminalmc.clientsort.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record TransferPayload(int srcContainerId, int[] srcSlotIds, int[] dstSlotIds, boolean reversed) implements CustomPacketPayload {
   public static final StreamCodec<RegistryFriendlyByteBuf, int[]> VAR_INT_ARRAY = new StreamCodec<RegistryFriendlyByteBuf, int[]>() {
      public int @NotNull [] decode(@NotNull RegistryFriendlyByteBuf byteBuf) {
         return byteBuf.readVarIntArray();
      }

      public void encode(@NotNull RegistryFriendlyByteBuf byteBuf, int @NotNull [] array) {
         byteBuf.writeVarIntArray(array);
      }

      // $VF: synthetic method
      // $VF: bridge method
      public @NotNull Object decode(@NotNull Object param1) {
         return this.decode((RegistryFriendlyByteBuf)var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      public void encode(@NotNull Object param1, @NotNull Object nullx) {
         this.encode((RegistryFriendlyByteBuf)var1, (int[])nullx);
      }
   };
   public static final StreamCodec<RegistryFriendlyByteBuf, TransferPayload> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT,
      TransferPayload::srcContainerId,
      VAR_INT_ARRAY,
      TransferPayload::srcSlotIds,
      VAR_INT_ARRAY,
      TransferPayload::dstSlotIds,
      ByteBufCodecs.BOOL,
      TransferPayload::reversed,
      TransferPayload::new
   );
   public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("clientsort", "transfer_c2s");
   public static final Type<TransferPayload> TYPE = new Type(ID);

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
