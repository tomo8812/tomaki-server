package dev.diorama.share;

import dev.diorama.Diorama;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

public final class ShareProtocol {
   public static final ResourceLocation CHANNEL = Diorama.id("share");
   public static final int VERSION = 2;
   public static final int SETTINGS = 0;
   public static final int REGIONS = 1;
   public static final int TABLES = 2;
   public static final int CHUNKS = 3;
   public static final int TABLES_DONE = 4;
   public static final int LOCKS = 5;
   public static final int MAX_ASK = 3000;
   public static final int MAX_CHUNK_BYTES = 524288;
   private static volatile @Nullable Consumer<byte[]> client;
   private static volatile @Nullable BiConsumer<ServerPlayer, byte[]> sender;
   private static volatile @Nullable Consumer<byte[]> serverSender;
   public static final Type<ShareProtocol.Packet> TYPE = new Type(CHANNEL);
   public static final StreamCodec<FriendlyByteBuf, ShareProtocol.Packet> CODEC = StreamCodec.of(ShareProtocol::write, ShareProtocol::read);

   private ShareProtocol() {
   }

   public static void write(FriendlyByteBuf buf, ShareProtocol.Packet packet) {
      buf.writeBytes(packet.data());
   }

   public static ShareProtocol.Packet read(FriendlyByteBuf buf) {
      byte[] data = new byte[buf.readableBytes()];
      buf.readBytes(data);
      return new ShareProtocol.Packet(data);
   }

   public static void onClient(Consumer<byte[]> receiver) {
      client = receiver;
   }

   public static void toServer(Consumer<byte[]> send) {
      serverSender = send;
   }

   public static void sendToServer(byte[] data) {
      Consumer<byte[]> send = serverSender;
      if (send != null) {
         send.accept(data);
      }
   }

   public static long chunkKey(int chunkX, int chunkZ) {
      return (long)chunkX << 32 | chunkZ & 4294967295L;
   }

   public static void sender(BiConsumer<ServerPlayer, byte[]> send) {
      sender = send;
   }

   public static void toClient(byte[] data) {
      Consumer<byte[]> receiver = client;
      if (receiver != null) {
         receiver.accept(data);
      }
   }

   static void send(ServerPlayer player, byte[] data) {
      BiConsumer<ServerPlayer, byte[]> send = sender;
      if (send != null) {
         send.accept(player, data);
      }
   }

   public record Packet(byte[] data) implements CustomPacketPayload {
      public Type<ShareProtocol.Packet> type() {
         return ShareProtocol.TYPE;
      }
   }
}
