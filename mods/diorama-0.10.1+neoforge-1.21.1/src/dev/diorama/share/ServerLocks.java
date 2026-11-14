package dev.diorama.share;

import dev.diorama.Diorama;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public record ServerLocks(boolean caveMapsAllowed, boolean caveFogOfWar, boolean radarAllowed, Set<String> mapOffDimensions) {
   public static final ServerLocks NONE = new ServerLocks(true, false, true, Set.of());
   private static final String CAVE_MAPS = "cave-maps-allowed";
   private static final String CAVE_FOG = "cave-fog-of-war";
   private static final String RADAR = "radar-allowed";
   private static final String MAP_OFF = "map-off-dimensions";
   private static final String[][] SETTINGS = new String[][]{
      {
            "cave-maps-allowed",
            "# Map settings this server decides for players with Diorama. Their own settings come back\n# when they leave. false: no cave maps underground, only the view from the sky; in the\n# Nether, only what each player has seen under the roof. The web map can't know what\n# each player has seen, so it leaves out the Nether while this is false.\n",
            "true"
      },
      {
            "cave-fog-of-war",
            "# true: cave maps show only the blocks each player has seen (fog of war), and players\n# can't turn it off. The web map leaves out the Nether while this is true.\n",
            "false"
      },
      {"radar-allowed", "# false: no players or mobs on the minimap or the map.\n", "true"},
      {
            "map-off-dimensions",
            "# Dimensions where Diorama is off: no minimap, no map, no waypoints in the world, and\n# nothing is mapped there; the web map leaves them out too. Dimension IDs separated by\n# commas, for example minecraft:the_nether,somemod:maze. Empty: the map works in every\n# dimension.\n",
            ""
      }
   };

   public boolean any() {
      return !this.equals(NONE);
   }

   public static ServerLocks load(Path configFolder, boolean addMissing) {
      Path file = configFolder.resolve("diorama-server.properties");
      if (!Files.exists(file) && !addMissing) {
         return NONE;
      }

      Properties properties = new Properties();
      if (Files.exists(file)) {
         try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            properties.load(reader);
         } catch (IOException | IllegalArgumentException e) {
            Diorama.LOGGER.warn("Couldn't read the map settings locks in {}; locking none", file, e);
            return NONE;
         }
      }

      if (addMissing) {
         StringBuilder missing = new StringBuilder();

         for (String[] setting : SETTINGS) {
            if (!properties.containsKey(setting[0])) {
               missing.append('\n').append(setting[1]).append(setting[0]).append('=').append(setting[2]).append('\n');
            }
         }

         if (!missing.isEmpty()) {
            try {
               Files.createDirectories(configFolder);
               Files.writeString(file, (Files.exists(file) ? Files.readString(file, StandardCharsets.UTF_8) : "") + missing, StandardCharsets.UTF_8);
            } catch (IOException e) {
               Diorama.LOGGER.warn("Couldn't add the map settings locks to {}", file, e);
            }
         }
      }

      return of(properties, file.toString());
   }

   private static ServerLocks of(Properties properties, String where) {
      return new ServerLocks(
         flag(properties, "cave-maps-allowed", NONE.caveMapsAllowed, where),
         flag(properties, "cave-fog-of-war", NONE.caveFogOfWar, where),
         flag(properties, "radar-allowed", NONE.radarAllowed, where),
         dimensions(properties.getProperty("map-off-dimensions", ""))
      );
   }

   public static Set<String> dimensions(String list) {
      Set<String> dimensions = new LinkedHashSet<>();

      for (String part : list.split(",")) {
         String id = part.trim().toLowerCase(Locale.ROOT);
         if (!id.isEmpty()) {
            dimensions.add(id.indexOf(58) < 0 ? "minecraft:" + id : id);
         }
      }

      return Collections.unmodifiableSet(dimensions);
   }

   private static boolean flag(Properties properties, String key, boolean otherwise, String where) {
      String text = properties.getProperty(key);
      if (text == null) {
         return otherwise;
      } else {
         text = text.trim();
         if (!text.equalsIgnoreCase("true") && !text.equalsIgnoreCase("false")) {
            Diorama.LOGGER.warn("{} in {} is {}, which is neither true nor false; using {}", new Object[]{key, where, text, otherwise});
            return otherwise;
         } else {
            return text.equalsIgnoreCase("true");
         }
      }
   }

   byte[] encode() {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();

      try (DataOutputStream out = new DataOutputStream(bytes)) {
         out.writeByte(5);
         out.writeUTF(
            "cave-maps-allowed="
               + this.caveMapsAllowed
               + "\ncave-fog-of-war="
               + this.caveFogOfWar
               + "\nradar-allowed="
               + this.radarAllowed
               + "\nmap-off-dimensions="
               + String.join(",", this.mapOffDimensions)
               + "\n"
         );
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }

      return bytes.toByteArray();
   }

   public static @Nullable ServerLocks decode(byte[] data) {
      try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(data))) {
         if (in.readUnsignedByte() != 5) {
            return null;
         }

         Properties properties = new Properties();
         properties.load(new StringReader(in.readUTF()));
         return of(properties, "the server's message");
      } catch (IOException | IllegalArgumentException e) {
         return null;
      }
   }
}
