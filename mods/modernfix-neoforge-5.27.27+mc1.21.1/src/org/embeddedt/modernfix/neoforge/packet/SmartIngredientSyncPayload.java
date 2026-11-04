package org.embeddedt.modernfix.neoforge.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public enum SmartIngredientSyncPayload implements CustomPacketPayload {
   INSTANCE;

   public static final ThreadLocal<Boolean> CLIENT_HAS_SMART_INGREDIENT_SYNC = ThreadLocal.withInitial(() -> false);
   public static final Type<SmartIngredientSyncPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("modernfix", "ingredient_sync"));
   public static final StreamCodec<RegistryFriendlyByteBuf, SmartIngredientSyncPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   // $VF: synthetic method
   private static SmartIngredientSyncPayload[] $values() {
      return new SmartIngredientSyncPayload[]{INSTANCE};
   }
}
