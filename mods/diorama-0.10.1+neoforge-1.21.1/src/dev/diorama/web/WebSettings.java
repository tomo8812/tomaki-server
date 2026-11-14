package dev.diorama.web;

import dev.diorama.Diorama;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;
import org.jspecify.annotations.Nullable;

public record WebSettings(boolean enabled, int port, String address, @Nullable Path folder, boolean everyChunk) {
   public static final String FILE = "diorama-server.properties";
   public static final int DEFAULT_PORT = 8100;
   public static final WebSettings DEFAULTS = new WebSettings(false, 8100, "", null, false);
   private static final String[][] SETTINGS = new String[][]{
      {
            "web-map",
            "# The web map: this server draws its map, in vanilla colours, for anyone to see in a browser.\n# Off unless set to true. It covers the same land as the shared map (share-land).\n",
            "false"
      },
      {
            "web-map-port",
            "# The port of the web map's own web server, at http://<this server>:<port>/. Set it to 0\n# to only write the map's files to web-map-folder, for a web server of your own to serve.\n",
            Integer.toString(8100)
      },
      {
            "web-map-address",
            "# The address the web map's server listens on: empty for every address, or 127.0.0.1 to\n# reach it only through a web server of your own on this machine.\n",
            ""
      },
      {"web-map-folder", "# Where the web map's files go. Empty: diorama-web in the world folder.\n", ""}
   };

   public static WebSettings load(Path configFolder) {
      Path file = configFolder.resolve("diorama-server.properties");
      Properties properties = new Properties();
      if (Files.exists(file)) {
         try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            properties.load(reader);
         } catch (IOException | IllegalArgumentException e) {
            Diorama.LOGGER.warn("Couldn't read the web map settings in {}; the web map stays off", file, e);
            return DEFAULTS;
         }
      }

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
            Diorama.LOGGER.warn("Couldn't add the web map settings to {}", file, e);
         }
      }

      if (!properties.containsKey("web-map")) {
         return DEFAULTS;
      }

      boolean enabled = properties.getProperty("web-map", "false").trim().equalsIgnoreCase("true");
      int port = 8100;
      String portText = properties.getProperty("web-map-port", String.valueOf(8100)).trim();

      try {
         port = Integer.parseInt(portText);
         if (port < 0 || port > 65535) {
            throw new NumberFormatException();
         }
      } catch (NumberFormatException e) {
         Diorama.LOGGER.warn("web-map-port in {} is {}, which isn't a port; using {}", new Object[]{file, portText, 8100});
         port = 8100;
      }

      String address = properties.getProperty("web-map-address", "").trim();
      String folderText = properties.getProperty("web-map-folder", "").trim();
      Path folder = folderText.isEmpty() ? null : Path.of(folderText);
      boolean everyChunk = properties.getProperty("share-land", "visited").trim().toLowerCase(Locale.ROOT).equals("generated");
      return new WebSettings(enabled, port, address, folder, everyChunk);
   }
}
