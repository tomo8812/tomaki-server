package maki.mapshare;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import org.slf4j.LoggerFactory;

public final class ShareNetwork {
   public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

   private ShareNetwork() {
   }

   public static JsonObject object(String operation) {
      JsonObject payload = new JsonObject();
      payload.addProperty("op", operation);
      return payload;
   }

   public static ShareNetwork.Message message(JsonObject payload) {
      return new ShareNetwork.Message(GSON.toJson(payload));
   }

   public static void register(RegisterPayloadHandlersEvent event) {
      event.registrar("1")
         .optional()
         .executesOn(HandlerThread.MAIN)
         .playBidirectional(ShareNetwork.Message.TYPE, ShareNetwork.Message.CODEC, (message, context) -> {
            try {
               JsonObject payload = JsonParser.parseString(message.json()).getAsJsonObject();
               if (context.flow() == PacketFlow.SERVERBOUND && context.player() instanceof ServerPlayer player) {
                  ServerSharing.receive(player, payload);
               } else if (context.flow() == PacketFlow.CLIENTBOUND) {
                  ClientSharing.receive(payload);
               }
            } catch (RuntimeException exception) {
               LoggerFactory.getLogger("MakiMapShare").warn("Ignored invalid map-sharing payload: {}", exception.toString());
            }
         });
   }

   public record Message(String json) implements CustomPacketPayload {
      public static final Type<ShareNetwork.Message> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("maki_diorama_share", "sync"));
      public static final StreamCodec<RegistryFriendlyByteBuf, ShareNetwork.Message> CODEC = StreamCodec.of(
         (buffer, message) -> buffer.writeUtf(message.json, 16384), buffer -> new ShareNetwork.Message(buffer.readUtf(16384))
      );

      public Type<ShareNetwork.Message> type() {
         return TYPE;
      }
   }
}
