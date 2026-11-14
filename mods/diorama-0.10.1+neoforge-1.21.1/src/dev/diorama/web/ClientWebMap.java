package dev.diorama.web;

import com.sun.net.httpserver.HttpExchange;
import dev.diorama.Diorama;
import dev.diorama.Facing;
import dev.diorama.MapSession;
import dev.diorama.Projection;
import dev.diorama.ServerRules;
import dev.diorama.Settings;
import dev.diorama.bake.TileKey;
import dev.diorama.data.TileStore;
import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public final class ClientWebMap {
   private static final int PORT_TRIES = 10;
   private static final long LIST_MS = 1000L;
   private static @Nullable WebServer http;
   private static Settings.WebMap mode = Settings.WebMap.OFF;
   private static int port;
   private static @Nullable Component failure;
   private static boolean announced;
   private static volatile ClientWebMap.@Nullable Live live;
   private static volatile boolean thisComputerOnly;
   private static byte @Nullable [] page;
   private static final Object LIST_LOCK = new Object();
   private static @Nullable TileStore listedStore;
   private static long listedAt;
   private static String listed = "";
   private static final int PAGE_LEVELS = 6;
   private static final int FEW_TILES = 4;

   private ClientWebMap() {
   }

   public static int servingPort() {
      WebServer server = http;
      return server == null ? 0 : server.port();
   }

   public static void tick(Minecraft mc) {
      Settings settings = Settings.get();
      if (settings.webMap != mode || settings.webMapPort != port) {
         stop();
         mode = settings.webMap;
         port = settings.webMapPort;
         announced = false;
         if (mode != Settings.WebMap.OFF) {
            start();
         }
      }

      live = http == null ? null : snapshot(mc);
      if (mc.level == null || mc.player == null) {
         announced = false;
      } else if (!announced && mode != Settings.WebMap.OFF) {
         announced = true;
         Component message = (Component)(http != null ? Component.translatable("diorama.web_map.ready", new Object[]{link(address())}) : failure);
         if (message != null) {
            mc.gui.getChat().addMessage(message);
         }
      }
   }

   public static void stop() {
      if (http != null) {
         http.stop();
         http = null;
      }

      live = null;
      failure = null;
      mode = Settings.WebMap.OFF;
   }

   private static void start() {
      String bind = mode == Settings.WebMap.THIS_COMPUTER ? "127.0.0.1" : "";
      thisComputerOnly = mode == Settings.WebMap.THIS_COMPUTER;

      try {
         for (int i = 0; i < 10 && http == null && port + i <= 65535; i++) {
            try {
               http = WebServer.start(bind, port + i, ClientWebMap::serve);
            } catch (BindException var3) {
            }
         }

         if (http == null) {
            Diorama.LOGGER.warn("Diorama's web map found no free port from {} to {}", port, port + 10 - 1);
            failure = Component.translatable("diorama.web_map.no_port", new Object[]{port, port + 10 - 1});
         } else {
            failure = null;
            Diorama.LOGGER.info("Diorama's web map is at {}", address());
         }
      } catch (IOException | RuntimeException | NoClassDefFoundError e) {
         Diorama.LOGGER.warn("Diorama's web map couldn't start", e);
         failure = Component.translatable("diorama.web_map.failed", new Object[]{String.valueOf(e.getMessage() == null ? e : e.getMessage())});
      }
   }

   public static String address() {
      int serving = servingPort();
      String host = mode == Settings.WebMap.NETWORK ? networkAddress() : null;
      return "http://" + (host == null ? "localhost" : host) + ":" + serving + "/";
   }

   private static @Nullable String networkAddress() {
      try (DatagramSocket socket = new DatagramSocket()) {
         socket.connect(new InetSocketAddress("192.0.2.1", 9));
         InetAddress local = socket.getLocalAddress();
         if (local instanceof Inet4Address && !local.isAnyLocalAddress() && !local.isLoopbackAddress()) {
            return local.getHostAddress();
         }
      } catch (IOException | RuntimeException var7) {
      }

      try {
         for (NetworkInterface network : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            if (network.isUp() && !network.isLoopback() && !network.isVirtual()) {
               for (InetAddress address : Collections.list(network.getInetAddresses())) {
                  if (address instanceof Inet4Address && address.isSiteLocalAddress()) {
                     return address.getHostAddress();
                  }
               }
            }
         }
      } catch (IOException | RuntimeException var5) {
      }

      return null;
   }

   private static Component link(String address) {
      ClickEvent open = new ClickEvent(Action.OPEN_URL, address);
      return Component.literal(address)
         .withStyle(
            style -> style.withColor(ChatFormatting.GREEN)
               .withUnderlined(true)
               .withClickEvent(open)
               .withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.translatable("diorama.web_map.open")))
         );
   }

   private static ClientWebMap.@Nullable Live snapshot(Minecraft mc) {
      MapSession session = MapSession.get();
      Entity viewer = MapSession.viewer();
      if (session != null && viewer != null && mc.level != null && ServerRules.minimapAllowed()) {
         TileStore store = session.map().savedMap();
         if (store == null) {
            return null;
         }

         String dimension = mc.level.dimension().location().toString();
         Facing facing = Projection.facing();
         double yaw = Math.toRadians(viewer.getYRot());
         double lookX = -Math.sin(yaw);
         double lookZ = Math.cos(yaw);
         double heading = Math.atan2(facing.z(lookX, lookZ) * Projection.sin(), facing.x(lookX, lookZ));
         String player = String.format(
            Locale.ROOT,
            "{\"dimension\":\"%s\",\"name\":\"%s\",\"x\":%.2f,\"y\":%.2f,\"z\":%.2f,\"u\":%.3f,\"v\":%.3f,\"heading\":%.4f,\"color\":\"#%06x\"}",
            escape(dimension),
            escape(viewer.getName().getString()),
            viewer.getX(),
            viewer.getY(),
            viewer.getZ(),
            Projection.u(viewer.getX(), viewer.getZ()),
            Projection.v(viewer.getX(), viewer.getY(), viewer.getZ()),
            heading,
            Settings.get().arrowRgb()
         );
         String folder = dimension.replace(':', '_').replace('/', '_')
            + "-"
            + Integer.toHexString(store.folder().toAbsolutePath().normalize().toString().hashCode());
         return new ClientWebMap.Live(store, dimension, folder, Projection.sin(), Projection.cos(), facing, player);
      } else {
         return null;
      }
   }

   private static String escape(String text) {
      StringBuilder out = new StringBuilder();

      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         if (c == '"' || c == '\\') {
            out.append('\\').append(c);
         } else if (c >= ' ' && c <= '~') {
            out.append(c);
         } else {
            out.append(String.format(Locale.ROOT, "\\u%04x", Integer.valueOf(c)));
         }
      }

      return out.toString();
   }

   private static void serve(HttpExchange exchange) throws IOException {
      try {
         String method = exchange.getRequestMethod();
         if (!method.equals("GET") && !method.equals("HEAD")) {
            WebServer.send(exchange, 405, "text/plain", null);
            return;
         }

         if (thisComputerOnly && !thisComputer(exchange.getRequestHeaders().getFirst("Host"))) {
            WebServer.send(exchange, 403, "text/plain", null);
            return;
         }

         boolean head = method.equals("HEAD");
         String path = exchange.getRequestURI().getPath();
         ClientWebMap.Live now = live;
         exchange.getResponseHeaders().set("Cache-Control", "no-cache");
         if (path.equals("/") || path.equals("/index.html")) {
            byte[] body = page();
            WebServer.send(exchange, body == null ? 404 : 200, "text/html; charset=utf-8", head ? null : body);
            return;
         }

         if (path.equals("/map.json")) {
            WebServer.send(exchange, 200, "application/json", head ? null : mapJson(now).getBytes(StandardCharsets.UTF_8));
            return;
         }

         if (path.equals("/player.json")) {
            WebServer.send(exchange, 200, "application/json", head ? null : (now == null ? "{}" : now.player()).getBytes(StandardCharsets.UTF_8));
            return;
         }

         byte[] tile = now == null ? null : tile(now, path);
         if (tile != null) {
            String query = exchange.getRequestURI().getRawQuery();
            exchange.getResponseHeaders().set("Cache-Control", query != null && query.startsWith("v=") ? "private, max-age=31536000, immutable" : "no-cache");
            WebServer.send(exchange, 200, "image/jpeg", head ? null : tile);
            return;
         }

         WebServer.send(exchange, 404, "text/plain", null);
      } catch (IOException | RuntimeException var10) {
         return;
      } finally {
         exchange.close();
      }
   }

   private static boolean thisComputer(@Nullable String host) {
      if (host == null) {
         return false;
      }

      String name = host.trim().toLowerCase(Locale.ROOT);
      if (name.startsWith("[")) {
         int end = name.indexOf(93);
         name = end < 0 ? name : name.substring(0, end + 1);
      } else if (name.lastIndexOf(58) >= 0) {
         name = name.substring(0, name.lastIndexOf(58));
      }

      return name.equals("localhost") || name.equals("127.0.0.1") || name.equals("[::1]");
   }

   private static byte @Nullable [] page() throws IOException {
      if (page == null) {
         try (InputStream in = ClientWebMap.class.getResourceAsStream("/diorama/web/index.html")) {
            page = in == null ? null : in.readAllBytes();
         }
      }

      return page;
   }

   private static byte @Nullable [] tile(ClientWebMap.Live now, String path) throws IOException {
      String[] parts = path.substring(1).split("/");
      if (parts.length == 3 && parts[0].equals(now.folder()) && parts[2].endsWith(".jpg")) {
         String name = parts[2].substring(0, parts[2].length() - 4);
         int split = name.indexOf(95);

         int[] pixels;
         try {
            int zoom = Integer.parseInt(parts[1]);
            if (split < 1 || zoom < 0 || zoom > 11) {
               return null;
            }

            pixels = now.store()
               .read(new TileKey(Integer.MIN_VALUE, Integer.parseInt(name.substring(0, split)), Integer.parseInt(name.substring(split + 1)), zoom));
         } catch (NumberFormatException e) {
            return null;
         }

         return pixels == null ? null : WebTiles.jpeg(pixels);
      } else {
         return null;
      }
   }

   private static String mapJson(ClientWebMap.@Nullable Live now) {
      StringBuilder json = new StringBuilder();
      json.append("{\"tileUnits\":")
         .append(128)
         .append(",\"tilePixels\":")
         .append(512)
         .append(",\"sin\":")
         .append(String.format(Locale.ROOT, "%.6f", now == null ? Projection.sin() : now.sin()))
         .append(",\"cos\":")
         .append(String.format(Locale.ROOT, "%.6f", now == null ? Projection.cos() : now.cos()))
         .append(",\"background\":\"#")
         .append(String.format(Locale.ROOT, "%06x", 1382172))
         .append('"')
         .append(",\"updated\":")
         .append(System.currentTimeMillis())
         .append(",\"live\":true,\"facing\":")
         .append(now == null ? 0 : now.facing().turns)
         .append(",\"dimensions\":[");
      if (now != null) {
         json.append("{\"id\":\"")
            .append(escape(now.dimension()))
            .append("\",\"folder\":\"")
            .append(escape(now.folder()))
            .append("\",")
            .append(tiles(now.store()));
      }

      return json.append("]}").toString();
   }

   private static String tiles(TileStore store) {
      synchronized (LIST_LOCK) {
         long time = System.currentTimeMillis();
         if (store == listedStore && time - listedAt < 1000L) {
            return listed;
         }

         StringBuilder[] levels = new StringBuilder[12];
         int zoomLevels = 1;
         int enough = 11;

         for (int lod = 0; lod <= 11; lod++) {
            StringBuilder level = levels[lod] = new StringBuilder();
            Map<TileKey, Long> saved = store.savedTimes(lod);
            if (lod >= 5 && lod < enough && saved.size() <= 4) {
               enough = lod;
            }

            for (Entry<TileKey, Long> tile : saved.entrySet()) {
               if (level.length() > 0) {
                  level.append(',');
               }

               level.append(tile.getKey().tileU()).append(',').append(tile.getKey().tileV()).append(',').append(tile.getValue());
            }

            if (level.length() > 0 && lod <= enough) {
               zoomLevels = lod + 1;
            }
         }

         StringBuilder json = new StringBuilder("\"zoomLevels\":").append(zoomLevels).append(",\"versions\":true,\"tiles\":[");

         for (int lod = 0; lod < zoomLevels; lod++) {
            json.append(lod > 0 ? "," : "").append('[').append(levels[lod]).append(']');
         }

         listed = json.append("]}").toString();
         listedStore = store;
         listedAt = time;
         return listed;
      }
   }

   private record Live(TileStore store, String dimension, String folder, double sin, double cos, Facing facing, String player) {
   }
}
