package dev.diorama.share;

import dev.diorama.Diorama;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;
import org.jspecify.annotations.Nullable;

public record ShareSettings(boolean share, ShareSettings.Land land, int kilobytesPerSecond) {
   public static final String FILE = "diorama-server.properties";
   public static final ShareSettings DEFAULTS = new ShareSettings(true, ShareSettings.Land.VISITED, 500);

   public static ShareSettings load(Path configFolder) {
      Path file = configFolder.resolve("diorama-server.properties");
      if (!Files.exists(file)) {
         try {
            Files.createDirectories(configFolder);
            Files.writeString(file, DEFAULTS.text());
         } catch (IOException e) {
            Diorama.LOGGER.warn("Couldn't write the default map sharing settings to {}", file, e);
         }

         return DEFAULTS;
      } else {
         Properties properties = new Properties();

         try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            properties.load(reader);
         } catch (IOException | IllegalArgumentException e) {
            Diorama.LOGGER.warn("Couldn't read the map sharing settings in {}; using the defaults", file, e);
            return DEFAULTS;
         }

         String shareText = properties.getProperty("share-map", "true").trim();
         boolean share = !shareText.equalsIgnoreCase("false");
         if (share && !shareText.equalsIgnoreCase("true")) {
            Diorama.LOGGER.warn("share-map in {} is {}, which is neither true nor false; sharing", file, shareText);
         }

         String landText = properties.getProperty("share-land", ShareSettings.Land.VISITED.key());
         ShareSettings.Land land = ShareSettings.Land.of(landText);
         if (land == null) {
            Diorama.LOGGER.warn("share-land in {} is {}, which is neither visited nor generated; using visited", file, landText);
            land = ShareSettings.Land.VISITED;
         }

         int kilobytes = DEFAULTS.kilobytesPerSecond();
         String rate = properties.getProperty("share-kilobytes-per-second");
         if (rate != null) {
            try {
               kilobytes = Math.max(1, Integer.parseInt(rate.trim()));
            } catch (NumberFormatException e) {
               Diorama.LOGGER.warn("share-kilobytes-per-second in {} is {}, which isn't a number; using {}", new Object[]{file, rate, kilobytes});
            }
         }

         return new ShareSettings(share, land, kilobytes);
      }
   }

   String text() {
      return "# Diorama on this server: which part of the map players with Diorama share.\n# Players without Diorama, and servers without it, are unaffected.\n\n# true: players with Diorama see land other players explored on their map, drawn with\n# their own resource pack. Only the view from the sky; caves stay what each player found.\nshare-map=%s\n\n# Which land is shared:\n#   visited   - chunks players have spent time near (default)\n#   generated - every finished chunk in the world, for worlds pre-generated with Chunky and the like\nshare-land=%s\n\n# The most this server sends each player for the shared map, in kilobytes a second.\nshare-kilobytes-per-second=%d\n"
         .formatted(this.share, this.land.key(), this.kilobytesPerSecond);
   }

   byte[] encode() {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();

      try (DataOutputStream out = new DataOutputStream(bytes)) {
         out.writeByte(0);
         out.writeShort(2);
         out.writeBoolean(this.share);
         out.writeByte(this.land.ordinal());
         out.writeInt(this.kilobytesPerSecond);
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }

      return bytes.toByteArray();
   }

   public static @Nullable ShareSettings decode(byte[] data) {
      try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(data))) {
         if (in.readUnsignedByte() != 0) {
            return null;
         } else {
            int version = in.readUnsignedShort();
            if (version != 2) {
               Diorama.LOGGER.info("The server's Diorama shares its map in another way (version {}, this one {}); not sharing", version, 2);
               return null;
            } else {
               boolean share = in.readBoolean();
               int land = in.readUnsignedByte();
               int kilobytes = in.readInt();
               return new ShareSettings(
                  share, land < ShareSettings.Land.values().length ? ShareSettings.Land.values()[land] : ShareSettings.Land.VISITED, kilobytes
               );
            }
         }
      } catch (IOException e) {
         return null;
      }
   }

   public enum Land {
      VISITED,
      GENERATED;

      String key() {
         return this.name().toLowerCase(Locale.ROOT);
      }

      static ShareSettings.@Nullable Land of(String key) {
         for (ShareSettings.Land land : values()) {
            if (land.key().equals(key.trim().toLowerCase(Locale.ROOT))) {
               return land;
            }
         }

         return null;
      }

      // $VF: synthetic method
      private static ShareSettings.Land[] $values() {
         return new ShareSettings.Land[]{VISITED, GENERATED};
      }
   }
}
