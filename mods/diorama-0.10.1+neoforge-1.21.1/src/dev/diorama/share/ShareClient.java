package dev.diorama.share;

import dev.diorama.Diorama;
import dev.diorama.ServerRules;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import org.jspecify.annotations.Nullable;

public final class ShareClient {
   private static @Nullable ShareSettings settings;

   private ShareClient() {
   }

   public static void init() {
      ShareProtocol.onClient(ShareClient::receive);
   }

   private static void receive(byte[] data) {
      if (data.length != 0) {
         if (data[0] == 5) {
            ServerLocks locks = ServerLocks.decode(data);
            if (locks != null) {
               ServerRules.onLocks(locks);
            }
         } else if (data[0] != 0) {
            try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(data))) {
               SharedChunks.receive(in.readUnsignedByte(), in);
            } catch (IOException | RuntimeException e) {
               Diorama.LOGGER.warn("Couldn't read a message about the shared map", e);
            }
         } else {
            settings = ShareSettings.decode(data);
            if (settings != null) {
               Diorama.LOGGER
                  .info("This server has Diorama: {}", settings.share() ? "it shares its map (" + settings.land().key() + " land)" : "it doesn't share its map");
            }
         }
      }
   }

   public static @Nullable ShareSettings settings() {
      return settings;
   }

   public static boolean sharing() {
      ShareSettings known = settings;
      return known != null && known.share();
   }

   public static void onJoinOrLeave() {
      settings = null;
      SharedChunks.closeCurrent();
   }
}
