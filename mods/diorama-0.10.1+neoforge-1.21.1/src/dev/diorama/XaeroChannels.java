package dev.diorama;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jspecify.annotations.Nullable;

public final class XaeroChannels {
   static final int LEVEL_ID = 0;
   static final int HANDSHAKE = 1;
   static final int TRACKED_PLAYER = 2;
   static final int TRACKER_RESET = 3;
   private static final int PROTOCOL = 3;
   public static final Type<XaeroChannels.Message> MINIMAP = new Type(ResourceLocation.fromNamespaceAndPath("xaerominimap", "main"));
   public static final Type<XaeroChannels.Message> WORLD_MAP = new Type(ResourceLocation.fromNamespaceAndPath("xaeroworldmap", "main"));
   private static final ResourceLocation WORLD_INFO = ResourceLocation.fromNamespaceAndPath("worldinfo", "world_id");
   private static final List<ResourceLocation> CLAIMED = new ArrayList<>(2);
   private static final List<ResourceLocation> TAKEN = new ArrayList<>(2);
   private static @Nullable Type<XaeroChannels.Message> trackerChannel;
   private static final Map<Object, Queue<XaeroChannels.Message>> PEEKED = new WeakHashMap<>();

   private XaeroChannels() {
   }

   public static void register(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("1.0").optional();

      for (Type<XaeroChannels.Message> type : unclaimed()) {
         try {
            registrar.playBidirectional(type, codec(type), (message, context) -> {
               if (context.flow() == PacketFlow.CLIENTBOUND) {
                  receive(message);
               }
            });
         } catch (UnsupportedOperationException e) {
            taken(type);
         }
      }
   }

   private static void taken(Type<XaeroChannels.Message> type) {
      Diorama.LOGGER.info("Another mod has {}, so Diorama reads it without being its handler", type.id());
      TAKEN.add(type.id());
   }

   private static List<Type<XaeroChannels.Message>> unclaimed() {
      List<Type<XaeroChannels.Message>> unclaimed = new ArrayList<>(2);
      CLAIMED.clear();
      if (unclaimed(MINIMAP, "xaerominimap", "xaerominimapfair", "xaerobetterpvp")) {
         unclaimed.add(MINIMAP);
      } else {
         CLAIMED.add(MINIMAP.id());
      }

      if (unclaimed(WORLD_MAP, "xaeroworldmap")) {
         unclaimed.add(WORLD_MAP);
      } else {
         CLAIMED.add(WORLD_MAP.id());
      }

      return unclaimed;
   }

   private static boolean unclaimed(Type<XaeroChannels.Message> type, String... xaeroMods) {
      for (String mod : xaeroMods) {
         if (modLoaded(mod)) {
            Diorama.LOGGER.info("{} is installed, so it keeps {}; Diorama only reads level IDs there", mod, type.id());
            return false;
         }
      }

      return true;
   }

   private static boolean modLoaded(String mod) {
      return ModList.get().isLoaded(mod);
   }

   public static StreamCodec<FriendlyByteBuf, XaeroChannels.Message> codec(Type<XaeroChannels.Message> type) {
      return StreamCodec.of(XaeroChannels::write, buf -> read(type, buf));
   }

   public static void write(FriendlyByteBuf buf, XaeroChannels.Message message) {
      buf.writeByte(message.kind());
      buf.writeBytes(message.body());
   }

   public static XaeroChannels.Message read(Type<XaeroChannels.Message> type, FriendlyByteBuf buf) {
      int kind = buf.isReadable() ? buf.readByte() : -1;
      byte[] body = new byte[buf.readableBytes()];
      buf.readBytes(body);
      return new XaeroChannels.Message(type, kind, body);
   }

   private static void receive(XaeroChannels.Message message) {
      switch (message.kind()) {
         case 0:
            Integer id = message.levelId();
            if (id != null) {
               WorldKey.serverLevelId(id);
            }
            break;
         case 1:
            answer(message.type());
            break;
         case 2:
            if (message.type() == trackerChannel) {
               CompoundTag tag = readNbt(message.body());
               if (tag != null) {
                  TrackedPlayers.update(tag);
               }
            }
            break;
         case 3:
            if (message.type() == trackerChannel) {
               TrackedPlayers.clear();
            }
      }
   }

   private static void answer(Type<XaeroChannels.Message> channel) {
      if (!modLoaded("xaeroworldmap")) {
         ClientPacketListener connection = Minecraft.getInstance().getConnection();
         if (connection != null && hasChannel(connection, channel.id())) {
            if (trackerChannel == null || channel == MINIMAP && trackerChannel != MINIMAP) {
               trackerChannel = channel;
               TrackedPlayers.clear();
            }

            XaeroChannels.Message answer = new XaeroChannels.Message(channel, 1, ByteBuffer.allocate(4).putInt(3).array());
            connection.send(answer);
         } else {
            Diorama.LOGGER.info("The server sent a handshake on {} but doesn't listen there, so it isn't answered", channel.id());
         }
      }
   }

   private static @Nullable CompoundTag readNbt(byte[] body) {
      try {
         return new FriendlyByteBuf(Unpooled.wrappedBuffer(body)).readNbt();
      } catch (RuntimeException e) {
         Diorama.LOGGER.warn("Couldn't read a tracked player from the server", e);
         return null;
      }
   }

   public static void onJoinOrLeave() {
      trackerChannel = null;
   }

   public static XaeroChannels.@Nullable Message peek(ResourceLocation channel, ByteBuf body) {
      boolean levelId = CLAIMED.contains(channel) && body.readableBytes() >= 5 && body.getByte(body.readerIndex()) == 0;
      return !levelId && !TAKEN.contains(channel) ? null : read(channel.equals(MINIMAP.id()) ? MINIMAP : WORLD_MAP, new FriendlyByteBuf(body.slice()));
   }

   public static void peeked(Object payload, XaeroChannels.Message message) {
      synchronized (PEEKED) {
         PEEKED.computeIfAbsent(payload, key -> new ArrayDeque<>()).add(message);
      }
   }

   public static void queuePeeked(CustomPacketPayload payload, ClientCommonPacketListener listener) {
      XaeroChannels.Message message;
      synchronized (PEEKED) {
         Queue<XaeroChannels.Message> messages = PEEKED.get(payload);
         if (messages == null) {
            return;
         }

         message = messages.remove();
         if (messages.isEmpty()) {
            PEEKED.remove(payload);
         }
      }

      queue(message, listener);
   }

   private static void queue(XaeroChannels.Message message, ClientCommonPacketListener listener) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.isSameThread()) {
         receivePeeked(message);
      } else {
         mc.execute(() -> receivePeeked(message));
      }
   }

   private static void receivePeeked(XaeroChannels.Message message) {
      if (Minecraft.getInstance().getConnection() != null) {
         receive(message);
      }
   }

   static boolean serverMayNameWorlds() {
      ClientPacketListener connection = Minecraft.getInstance().getConnection();
      return connection != null && (hasChannel(connection, MINIMAP.id()) || hasChannel(connection, WORLD_MAP.id()) || hasChannel(connection, WORLD_INFO));
   }

   private static boolean hasChannel(ClientPacketListener connection, ResourceLocation channel) {
      return connection.hasChannel(channel);
   }

   public record Message(Type<XaeroChannels.Message> type, int kind, byte[] body) implements CustomPacketPayload {
      public @Nullable Integer levelId() {
         return this.kind == 0 && this.body.length >= 4 ? ByteBuffer.wrap(this.body).getInt() : null;
      }
   }
}
