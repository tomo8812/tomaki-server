package dev.terminalmc.clientsort.client.config;

import dev.terminalmc.clientsort.network.payload.SortPayload;
import dev.terminalmc.clientsort.network.payload.StackFillPayload;
import dev.terminalmc.clientsort.network.payload.TransferPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public enum Operation {
   SORT(SortPayload.TYPE, "sort"),
   STACK_FILL(StackFillPayload.TYPE, "stackFill"),
   MATCH_TRANSFER(TransferPayload.TYPE, "matchTransfer"),
   TRANSFER(TransferPayload.TYPE, "transfer");

   public final Type<?> type;
   public final ResourceLocation id;
   public final String translationKey;

   Operation(Type<?> type, String translationKey) {
      this.type = type;
      this.id = type.id();
      this.translationKey = translationKey;
   }

   public boolean isDirectional() {
      return !this.equals(SORT);
   }

   // $VF: synthetic method
   private static Operation[] $values() {
      return new Operation[]{SORT, STACK_FILL, MATCH_TRANSFER, TRANSFER};
   }
}
