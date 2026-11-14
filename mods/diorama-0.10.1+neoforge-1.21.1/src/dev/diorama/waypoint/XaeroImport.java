package dev.diorama.waypoint;

import dev.diorama.Diorama;
import dev.diorama.WorldKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.fml.loading.FMLPaths;
import org.jspecify.annotations.Nullable;

public final class XaeroImport {
   private static final DyeColor[] EXTRA_COLORS = new DyeColor[]{DyeColor.MAGENTA, DyeColor.LIGHT_BLUE, DyeColor.LIME, DyeColor.PINK, DyeColor.BROWN};
   private static final Map<String, String> XAERO_NAMES = Map.of("gui.xaero_deathpoint", "Latest Death", "gui.xaero_deathpoint_old", "Old Death");
   private static final int MAX_NAME = 32;
   private static final int MAX_HORIZONTAL = 30000000;
   private static final int MAX_VERTICAL = 4096;
   private static final long MAX_FILE = 4194304L;
   private static final String SHARED_FOLDER = "Multiplayer_Any Address";

   private XaeroImport() {
   }

   public static XaeroImport.@Nullable Found find() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.level == null) {
         return null;
      }

      Path base = mc.gameDirectory.toPath().resolve("xaero").resolve("minimap");
      if (!Files.isDirectory(base)) {
         base = mc.gameDirectory.toPath().resolve("XaeroWaypoints");
      }

      IntegratedServer server = mc.getSingleplayerServer();
      ServerData data = mc.getCurrentServer();

      try {
         List<String> roots;
         if (server != null) {
            roots = singleplayerFolders(server.getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize().getFileName().toString());
         } else {
            if (data == null || WorldKey.isRealm(data)) {
               return null;
            }

            Path config = FMLPaths.CONFIGDIR.get();
            roots = serverFolders(data.ip, byAddress(config.resolve("xaero").resolve("minimap").resolve("client.cfg")));
         }

         BlockPos spawn = mc.level.getSharedSpawnPos();
         String spawnId = "mw" + (spawn.getX() >> 6) + "," + (spawn.getY() >> 6) + "," + (spawn.getZ() >> 6);
         int height = mc.player != null ? mc.player.getBlockY() : 64;
         return find(base, roots, server != null, spawnId, height);
      } catch (IOException | RuntimeException e) {
         Diorama.LOGGER.warn("Couldn't read Xaero's Minimap waypoints from {}", base, e);
         return null;
      }
   }

   public static XaeroImport.@Nullable Found find(Path base, List<String> roots, boolean singleplayer, String spawnId, int height) throws IOException {
      Path root = existing(base, roots);
      if (root == null) {
         return null;
      }

      Map<String, List<XaeroImport.World>> worlds = new LinkedHashMap<>();

      try (Stream<Path> dimensions = Files.list(root)) {
         Iterator perDimension = dimensions.filter(x$0 -> Files.isDirectory(x$0)).sorted().toList().iterator();

         label144:
         while (true) {
            String dimension;
            List<Path> files;
            while (true) {
               if (!perDimension.hasNext()) {
                  break label144;
               }

               Path folder = (Path)perDimension.next();
               dimension = dimension(folder.getFileName().toString());
               if (dimension != null) {
                  try (Stream<Path> list = Files.list(folder)) {
                     files = list.filter(x$0 -> Files.isRegularFile(x$0)).sorted().toList();
                     break;
                  } catch (IOException | RuntimeException e) {
                     Diorama.LOGGER.warn("Skipping Xaero's Minimap folder {}: can't read it", folder, e);
                  }
               }
            }

            Iterator var32 = files.iterator();

            while (true) {
               Path file;
               String id;
               String name;
               while (true) {
                  if (!var32.hasNext()) {
                     continue label144;
                  }

                  file = (Path)var32.next();
                  String fileName = file.getFileName().toString();
                  if (fileName.endsWith(".txt")) {
                     String stem = fileName.substring(0, fileName.length() - ".txt".length());
                     id = stem;
                     name = null;
                     if (stem.equals("waypoints")) {
                        break;
                     }

                     String[] parts = stem.split("_");
                     if (parts.length >= 2) {
                        id = parts[0];
                        name = parts[1].replace("%us%", "_").replace("§§", ":");
                        break;
                     }
                  }
               }

               List<String> lines;
               try {
                  lines = read(file);
               } catch (IOException | RuntimeException e) {
                  Diorama.LOGGER.warn("Skipping Xaero's Minimap file {}: can't read it", file, e);
                  continue;
               }

               worlds.computeIfAbsent(id, key -> new ArrayList<>()).add(new XaeroImport.World(id, name, dimension, parse(lines, dimension, height), false));
            }
         }
      }

      if (singleplayer && worlds.containsKey("waypoints")) {
         XaeroImport.World world = merge("waypoints", worlds.get("waypoints"), true);
         return world.entries().isEmpty() ? null : new XaeroImport.Found(List.of(world), world);
      }

      boolean shared = root.getFileName().toString().equalsIgnoreCase("Multiplayer_Any Address");
      boolean perDimension = shared || worlds.keySet().stream().anyMatch(idx -> idx.startsWith("mw$") || idx.startsWith("cm$"));
      String likely = !singleplayer && !shared && !perDimension ? xaeroChoice(root.resolve("config.txt"), spawnId) : null;
      List<XaeroImport.World> candidates = new ArrayList<>();

      for (Map.Entry<String, List<XaeroImport.World>> world : worlds.entrySet()) {
         if (perDimension) {
            candidates.addAll(world.getValue());
         } else {
            candidates.add(merge(world.getKey(), world.getValue(), world.getKey().equals(likely)));
         }
      }

      candidates.removeIf(worldx -> worldx.entries().isEmpty());
      candidates.sort(Comparator.comparing(worldx -> !worldx.likely()));
      return candidates.isEmpty() ? null : new XaeroImport.Found(candidates, null);
   }

   private static XaeroImport.World merge(String id, List<XaeroImport.World> files, boolean likely) {
      List<XaeroImport.Entry> entries = files.stream().flatMap(world -> world.entries().stream()).toList();
      String name = files.stream().map(XaeroImport.World::name).filter(n -> n != null).findFirst().orElse(null);
      return new XaeroImport.World(id, name, null, entries, likely);
   }

   private static @Nullable String xaeroChoice(Path config, String spawnId) {
      if (!Files.isRegularFile(config)) {
         return null;
      }

      List<String> lines;
      try {
         lines = read(config);
      } catch (IOException | RuntimeException e) {
         Diorama.LOGGER.warn("Couldn't read Xaero's Minimap world config {}", config, e);
         return null;
      }

      boolean detection = false;
      String pinned = null;

      for (String line : lines) {
         String[] parts = line.split(":");
         if (parts.length >= 2 && parts[0].equals("usingMultiworldDetection")) {
            detection = parts[1].equals("true");
         } else if (parts.length >= 2 && parts[0].equals("defaultMultiworldId")) {
            pinned = parts[1];
         }
      }

      return detection ? spawnId : pinned;
   }

   static @Nullable Boolean byAddress(Path clientConfig) {
      if (!Files.isRegularFile(clientConfig)) {
         return null;
      }

      List<String> lines;
      try {
         lines = read(clientConfig);
      } catch (IOException | RuntimeException e) {
         Diorama.LOGGER.warn("Couldn't read Xaero's Minimap options {}", clientConfig, e);
         return null;
      }

      for (String line : lines) {
         String[] parts = line.split("=");
         if (parts.length == 2 && parts[0].trim().equals("differentiate_by_server_address")) {
            return parts[1].trim().equals("true");
         }
      }

      return null;
   }

   private static List<String> read(Path file) throws IOException {
      if (Files.size(file) > 4194304L) {
         Diorama.LOGGER.warn("Skipping {}: too big for a Xaero's Minimap file", file);
         return List.of();
      } else {
         return Files.readAllLines(file, StandardCharsets.UTF_8);
      }
   }

   private static @Nullable Path existing(Path base, List<String> names) throws IOException {
      if (!Files.isDirectory(base)) {
         return null;
      }

      List<Path> folders;
      try (Stream<Path> list = Files.list(base)) {
         folders = list.filter(x$0 -> Files.isDirectory(x$0)).toList();
      }

      for (String name : names) {
         for (Path folder : folders) {
            if (folder.getFileName().toString().equalsIgnoreCase(name)) {
               return folder;
            }
         }
      }

      return null;
   }

   public static List<String> singleplayerFolders(String saveFolder) {
      String older = saveFolder.replace("_", "%us%").replace("/", "%fs%").replace("\\", "%bs%");
      String newer = older.replace("[", "%lb%").replace("]", "%rb%");
      return newer.equals(older) ? List.of(newer) : List.of(newer, older);
   }

   public static List<String> serverFolders(String address, @Nullable Boolean byAddress) {
      if (Boolean.FALSE.equals(byAddress)) {
         return List.of("Multiplayer_Any Address");
      }

      List<String> names = new ArrayList<>();

      for (int version = 4; version >= 0; version--) {
         String name = serverFolder(address, version);
         if (!names.contains(name)) {
            names.add(name);
         }
      }

      if (byAddress == null) {
         names.add("Multiplayer_Any Address");
      }

      return names;
   }

   private static String serverFolder(String address, int version) {
      String ip = address;
      int port = version >= 1 && ip.indexOf(58) != ip.lastIndexOf(58) ? ip.lastIndexOf("]:") + 1 : ip.indexOf(58);
      if (port > 0) {
         ip = ip.substring(0, port);
      }

      while (ip.endsWith(".")) {
         ip = ip.substring(0, ip.length() - 1);
      }

      if (version >= 2) {
         ip = ip.replace("[", "").replace("]", "");
      }

      ip = ip.replace(":", version < 3 ? "§" : ".").replace("_", "%us%").replace("/", "%fs%").replace("\\", "%bs%");
      if (version >= 4) {
         ip = ip.trim();
         int end = ip.length();

         while (end > 0 && ip.charAt(end - 1) == '.') {
            end--;
         }

         ip = ip.substring(0, end) + ",".repeat(ip.length() - end);
      }

      return "Multiplayer_" + (ip.isEmpty() ? "Empty Address" : ip);
   }

   static @Nullable String dimension(String folder) {
      return switch (folder) {
         case "dim%0", "Overworld" -> "minecraft:overworld";
         case "dim%-1", "Nether" -> "minecraft:the_nether";
         case "dim%1", "The End" -> "minecraft:the_end";
         default -> folder.startsWith("dim%") && folder.contains("$") ? WaypointShare.ourDimension(folder.replace(',', '.')) : null;
      };
   }

   static List<XaeroImport.Entry> parse(List<String> lines, String dimension, int height) {
      List<XaeroImport.Entry> entries = new ArrayList<>();

      for (String line : lines) {
         String[] args = line.split(":");
         if (args.length >= 10 && args[0].equalsIgnoreCase("waypoint")) {
            try {
               int x = Integer.parseInt(args[3]);
               boolean heightKnown = !args[4].equals("~");
               int y = heightKnown ? Integer.parseInt(args[4]) : height;
               int z = Integer.parseInt(args[5]);
               int color = Integer.parseInt(args[6]);
               int type = Integer.parseInt(args[8]);
               if (type != 3
                  && (type != 0 || args.length <= 13 || !args[13].equals("true"))
                  && Math.abs(x) <= 30000000
                  && Math.abs(z) <= 30000000
                  && Math.abs(y) <= 4096) {
                  String name = WaypointShare.cut(WaypointShare.chatSafe(name(args[1].replace("§§", ":"))).trim(), 32).trim();
                  Waypoint waypoint = new Waypoint(name.isEmpty() ? "Waypoint" : name, x, y, z, dimension, WaypointShare.nearestPaletteColor(color(color)));
                  String initials = WaypointShare.cut(WaypointShare.chatSafe(args[2].replace("§§", ":")).trim(), 4);
                  if (!initials.isEmpty()) {
                     waypoint.initials = initials;
                  }

                  waypoint.visible = !args[7].equals("true");
                  if (type == 1 || type == 2) {
                     waypoint.kind = Waypoint.Kind.DEATH;
                  }

                  entries.add(new XaeroImport.Entry(waypoint, heightKnown));
               }
            } catch (NumberFormatException var16) {
            }
         }
      }

      return entries;
   }

   private static String name(String stored) {
      String fallback = XAERO_NAMES.get(stored);
      return fallback == null ? stored : Language.getInstance().getOrDefault(stored, fallback);
   }

   static int color(int index) {
      int clamped = Math.max(0, Math.min(15 + EXTRA_COLORS.length, index));
      return clamped < 16 ? WaypointShare.chatColor(clamped) : EXTRA_COLORS[clamped - 16].getTextColor();
   }

   public static List<Waypoint> missing(XaeroImport.World world, WaypointStore store) {
      Set<String> exact = new HashSet<>();
      Set<String> anyHeight = new HashSet<>();

      for (Waypoint w : store.all()) {
         exact.add(key(w, true));
         anyHeight.add(key(w, false));
      }

      List<Waypoint> out = new ArrayList<>();

      for (XaeroImport.Entry entry : world.entries()) {
         Waypoint in = entry.waypoint();
         boolean present = entry.heightKnown() ? exact.contains(key(in, true)) : anyHeight.contains(key(in, false));
         if (!present) {
            out.add(in);
            exact.add(key(in, true));
            anyHeight.add(key(in, false));
         }
      }

      return out;
   }

   private static String key(Waypoint w, boolean height) {
      String what = w.kind == Waypoint.Kind.DEATH ? "death" : "name|" + w.name;
      return w.dimension + "|" + w.x + "|" + (height ? w.y : "") + "|" + w.z + "|" + what;
   }

   public static List<Waypoint> importInto(XaeroImport.World world, WaypointStore store) {
      List<Waypoint> added = missing(world, store).stream().map(XaeroImport::copy).toList();
      store.addAll(added);
      Diorama.LOGGER.info("Imported {} waypoints from Xaero's Minimap world {}", added.size(), world.id());
      return added;
   }

   private static Waypoint copy(Waypoint from) {
      Waypoint to = new Waypoint(from.name, from.x, from.y, from.z, from.dimension, from.color);
      to.initials = from.initials;
      to.visible = from.visible;
      to.kind = from.kind;
      return to;
   }

   public record Entry(Waypoint waypoint, boolean heightKnown) {
   }

   public record Found(List<XaeroImport.World> worlds, XaeroImport.@Nullable World chosen) {
   }

   public record World(String id, @Nullable String name, @Nullable String dimension, List<XaeroImport.Entry> entries, boolean likely) {
      public String label() {
         return this.name != null && !this.name.isBlank() ? this.name : this.id;
      }
   }
}
