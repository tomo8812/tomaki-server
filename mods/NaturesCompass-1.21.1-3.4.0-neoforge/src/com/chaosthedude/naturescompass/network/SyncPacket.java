package com.chaosthedude.naturescompass.network;

import com.chaosthedude.naturescompass.NaturesCompass;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncPacket(
   boolean canTeleport,
   int maxNextSearches,
   boolean infiniteXp,
   List<ResourceLocation> allowedBiomes,
   Map<ResourceLocation, Integer> xpLevelsForAllowedBiomes,
   ListMultimap<ResourceLocation, ResourceLocation> dimensionKeysForAllowedBiomeKeys
) implements CustomPacketPayload {
   public static final Type<SyncPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("naturescompass", "sync"));
   public static final StreamCodec<FriendlyByteBuf, SyncPacket> CODEC = StreamCodec.ofMember(SyncPacket::write, SyncPacket::read);

   public static SyncPacket read(FriendlyByteBuf buf) {
      boolean canTeleport = buf.readBoolean();
      int maxNextSearches = buf.readInt();
      boolean infiniteXp = buf.readBoolean();
      List<ResourceLocation> allowedBiomes = new ArrayList<>();
      Map<ResourceLocation, Integer> xpLevelsForAllowedBiomes = new HashMap<>();
      ListMultimap<ResourceLocation, ResourceLocation> dimensionKeysForAllowedBiomeKeys = ArrayListMultimap.create();
      int listSize = buf.readInt();

      for (int i = 0; i < listSize; i++) {
         ResourceLocation biomeKey = buf.readResourceLocation();
         int numDimensions = buf.readInt();
         List<ResourceLocation> dimensionKeys = new ArrayList<>();

         for (int j = 0; j < numDimensions; j++) {
            dimensionKeys.add(buf.readResourceLocation());
         }

         int xpLevels = buf.readInt();
         if (biomeKey != null) {
            allowedBiomes.add(biomeKey);
            xpLevelsForAllowedBiomes.put(biomeKey, xpLevels);
            dimensionKeysForAllowedBiomeKeys.putAll(biomeKey, dimensionKeys);
         }
      }

      return new SyncPacket(canTeleport, maxNextSearches, infiniteXp, allowedBiomes, xpLevelsForAllowedBiomes, dimensionKeysForAllowedBiomeKeys);
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeBoolean(this.canTeleport);
      buf.writeInt(this.maxNextSearches);
      buf.writeBoolean(this.infiniteXp);
      buf.writeInt(this.allowedBiomes.size());

      for (ResourceLocation biomeKey : this.allowedBiomes) {
         buf.writeResourceLocation(biomeKey);
         List<ResourceLocation> dimensionKeys = this.dimensionKeysForAllowedBiomeKeys.get(biomeKey);
         buf.writeInt(dimensionKeys.size());

         for (ResourceLocation dimensionKey : dimensionKeys) {
            buf.writeResourceLocation(dimensionKey);
         }

         int xpLevels = this.xpLevelsForAllowedBiomes.get(biomeKey);
         buf.writeInt(xpLevels);
      }
   }

   public static void handle(SyncPacket packet, IPayloadContext context) {
      if (context.flow().isClientbound()) {
         context.enqueueWork(() -> {
            NaturesCompass.synced = true;
            canTeleport = packet.canTeleport;
            maxNextSearches = packet.maxNextSearches;
            infiniteXp = packet.infiniteXp;
            allowedBiomes = packet.allowedBiomes;
            xpLevelsForAllowedBiomes = packet.xpLevelsForAllowedBiomes;
            dimensionKeysForAllowedBiomeKeys = packet.dimensionKeysForAllowedBiomeKeys;
         });
      }
   }

   public Type<SyncPacket> type() {
      return TYPE;
   }
}
