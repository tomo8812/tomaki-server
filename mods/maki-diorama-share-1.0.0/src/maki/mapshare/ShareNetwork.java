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

   public static JsonObject object(String var0) {
      JsonObject var1 = new JsonObject();
      var1.addProperty("op", var0);
      return var1;
   }

   public static ShareNetwork.Message message(JsonObject var0) {
      return new ShareNetwork.Message(GSON.toJson(var0));
   }

   public static void register(RegisterPayloadHandlersEvent var0) {
      var0.registrar("1").optional().executesOn(HandlerThread.MAIN).playBidirectional(ShareNetwork.Message.TYPE, ShareNetwork.Message.CODEC, (var0x, var1) -> {
         try {
            JsonObject var2 = JsonParser.parseString(var0x.json()).getAsJsonObject();
            if (var1.flow() == PacketFlow.SERVERBOUND && var1.player() instanceof ServerPlayer var3) {
               ServerSharing.receive(var3, var2);
            } else if (var1.flow() == PacketFlow.CLIENTBOUND) {
               ClientSharing.receive(var2);
            }
         } catch (RuntimeException var5) {
            LoggerFactory.getLogger("MakiMapShare").warn("Ignored invalid map-sharing payload: {}", var5.toString());
         }
      });
   }

   public record Message(String json) implements CustomPacketPayload {
      public static final Type<ShareNetwork.Message> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("maki_diorama_share", "sync"));
      public static final StreamCodec<RegistryFriendlyByteBuf, ShareNetwork.Message> CODEC = StreamCodec.of(
         (var0, var1) -> var0.writeUtf(var1.json, 16384), var0 -> new ShareNetwork.Message(var0.readUtf(16384))
      );

      public Type<ShareNetwork.Message> type() {
         return TYPE;
      }
   }
}
