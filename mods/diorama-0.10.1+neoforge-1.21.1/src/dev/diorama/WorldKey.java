package dev.diorama;

import dev.diorama.mixin.BiomeManagerAccessor;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.level.storage.LevelResource;
import org.jspecify.annotations.Nullable;

public final class WorldKey {
   private static final long FIRST_WAIT_MS = 3000L;
   private static final long LATER_WAIT_MS = 1000L;
   public static volatile boolean waitForIdsForever;
   private static final String SAVE_ID = "diorama-world-id.txt";
   private static final String FOLDER_ID = "save-id.txt";
   private static final Map<ClientLevel, WorldKey.Naming> LEVELS = new WeakHashMap<>();
   private static @Nullable Integer lastId;
   private static @Nullable Boolean namedBefore;
   private static boolean silent;
   private static WorldKey.@Nullable Last last;
   private static final Map<ServerData, Long> REALMS = Collections.synchronizedMap(new WeakHashMap<>());

   private WorldKey() {
   }

   public static @Nullable String current() {
      return current(Minecraft.getInstance().level);
   }

   public static @Nullable String current(@Nullable ClientLevel level) {
      Minecraft mc = Minecraft.getInstance();
      IntegratedServer server = mc.getSingleplayerServer();
      if (server != null) {
         WorldKey.Last seen = last;
         if (seen != null && seen.matches(server, null, null, false, null)) {
            return seen.key;
         }

         String key = singleplayerKey(server.getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize());
         last = new WorldKey.Last(new WeakReference<>(server), null, null, false, null, key);
         return key;
      } else {
         ServerData data = mc.getCurrentServer();
         if (data != null && level != null) {
            WorldKey.Naming naming = LEVELS.computeIfAbsent(level, l -> new WorldKey.Naming());
            if (!naming.decided) {
               if (naming.id == null && expectingId(naming, data)) {
                  return null;
               }

               naming.decided = true;
               naming.named = naming.id != null ? naming.id : lastId;
            }

            Integer id = naming.named;
            boolean realm = isRealm(data);
            WorldKey.Last seen = last;
            if (seen != null && seen.matches(data, level, data.ip, realm, id)) {
               return seen.key;
            }

            String address = address(data);
            String key = id != null ? idKey(address, id) : seedKey(address, level);
            if (REALMS.containsKey(data)) {
               String old = key("realms", data.ip);
               String oldKey = id != null ? idKey(old, id) : seedKey(old, level);
               if (!adoptAddressFolder(key, oldKey)) {
                  REALMS.remove(data);
                  key = oldKey;
               }
            }

            last = new WorldKey.Last(new WeakReference<>(data), new WeakReference<>(level), data.ip, realm, id, key);
            return key;
         } else {
            return null;
         }
      }
   }

   private static String singleplayerKey(Path save) {
      String key = key("singleplayer", save.getFileName().toString());
      Path folder = folder(key);

      try {
         String id = saveId(save);
         String own = key + "-" + hash("save:" + id);
         if (Files.isDirectory(folder(own))) {
            return own;
         }

         Path claim = folder.resolve("save-id.txt");
         if (Files.isRegularFile(claim)) {
            String owner = Files.readString(claim, StandardCharsets.UTF_8).strip();
            if (owner.equals(id)) {
               return key;
            }

            Files.createDirectories(folder(own));
            return own;
         }

         Files.createDirectories(folder);
         Files.writeString(claim, id, StandardCharsets.UTF_8);
      } catch (IOException e) {
         Diorama.LOGGER.warn("Couldn't tell whether this save's map folder {} was made for it; using it", key, e);
      }

      return key;
   }

   private static String saveId(Path save) throws IOException {
      Path file = save.resolve("diorama-world-id.txt");
      if (Files.isRegularFile(file)) {
         String id = Files.readString(file, StandardCharsets.UTF_8).strip();
         if (!id.isEmpty()) {
            return id;
         }
      }

      String id = UUID.randomUUID().toString();
      Files.writeString(file, id, StandardCharsets.UTF_8);
      return id;
   }

   public static boolean waiting(@Nullable ClientLevel level) {
      WorldKey.Naming naming = level == null ? null : LEVELS.get(level);
      return naming != null && !naming.decided;
   }

   private static boolean expectingId(WorldKey.Naming naming, ServerData data) {
      if (!silent && (lastId != null || XaeroChannels.serverMayNameWorlds() || namedBefore(data))) {
         if (!waitForIdsForever && System.currentTimeMillis() - naming.since >= (lastId == null ? 3000L : 1000L)) {
            if (lastId == null) {
               silent = true;
               Diorama.LOGGER.info("The server sent no level ID, so its world is named by its seed");
            }

            return false;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   private static boolean namedBefore(ServerData data) {
      if (namedBefore == null) {
         String named = idPrefix(data);

         try (Stream<Path> worlds = Files.list(Minecraft.getInstance().gameDirectory.toPath().resolve("diorama"))) {
            namedBefore = worlds.anyMatch(world -> world.getFileName().toString().startsWith(named));
         } catch (IOException e) {
            namedBefore = false;
         }
      }

      return namedBefore;
   }

   private static String address(ServerData data) {
      Long realm = REALMS.get(data);
      return realm != null ? key("realm", Long.toString(realm)) : key(isRealm(data) ? "realms" : "server", data.ip);
   }

   private static boolean adoptAddressFolder(String key, String old) {
      Path target = folder(key);
      Path source = folder(old);
      if (!Files.exists(target) && Files.isDirectory(source)) {
         try {
            Files.move(source, target);
            Diorama.LOGGER.info("This realm's map moved from {} to {}, named by the realm instead of its address", old, key);
            return true;
         } catch (IOException e) {
            Diorama.LOGGER.warn("Couldn't move this realm's map from {} to {}; using it where it is", new Object[]{old, key, e});
            return false;
         }
      } else {
         return true;
      }
   }

   private static String idPrefix(ServerData data) {
      return address(data) + "-id-";
   }

   private static String idKey(String address, int id) {
      return address + "-id-" + HexFormat.of().toHexDigits(id);
   }

   private static String seedKey(String address, ClientLevel level) {
      return address + "-world-" + HexFormat.of().toHexDigits(((BiomeManagerAccessor)level.getBiomeManager()).diorama$biomeZoomSeed());
   }

   public static void serverLevelId(int id) {
      Minecraft mc = Minecraft.getInstance();
      ClientLevel level = mc.level;
      ServerData data = mc.getCurrentServer();
      if (level != null && data != null && mc.getSingleplayerServer() == null) {
         try {
            Files.createDirectories(folder(idKey(address(data), id)));
         } catch (IOException e) {
            Diorama.LOGGER.warn("Couldn't make the folder for this server's world {}", HexFormat.of().toHexDigits(id), e);
         }

         WorldKey.Naming naming = LEVELS.computeIfAbsent(level, l -> new WorldKey.Naming());
         lastId = id;
         silent = false;
         if (!Integer.valueOf(id).equals(naming.id)) {
            naming.id = id;
            if (!naming.decided) {
               Diorama.LOGGER.info("The server names this world {}", HexFormat.of().toHexDigits(id));
            } else if (!Integer.valueOf(id).equals(naming.named)) {
               Diorama.LOGGER
                  .info("The server named this world {} after its map opened, so the name applies from the next world on", HexFormat.of().toHexDigits(id));
            }
         }
      }
   }

   public static void realmEntry(ServerData data, long id) {
      REALMS.put(data, id);
   }

   public static boolean isRealm(ServerData data) {
      return data.isRealm();
   }

   public static void onJoinOrLeave() {
      LEVELS.clear();
      lastId = null;
      namedBefore = null;
      silent = false;
   }

   public static Path folder(String key) {
      return Minecraft.getInstance().gameDirectory.toPath().resolve("diorama").resolve(key);
   }

   public static String key(String kind, String identity) {
      return kind + "-" + sanitize(identity) + "-" + hash(kind + ":" + identity);
   }

   private static String hash(String text) {
      try {
         byte[] digest = MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8));
         return HexFormat.of().formatHex(digest, 0, 4);
      } catch (NoSuchAlgorithmException e) {
         throw new IllegalStateException(e);
      }
   }

   private static String sanitize(String name) {
      String cleaned = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]+", "_");
      return cleaned.isEmpty() ? "unnamed" : cleaned;
   }

   private record Last(
      WeakReference<Object> source, @Nullable WeakReference<ClientLevel> level, @Nullable String ip, boolean realm, @Nullable Integer id, String key
   ) {
      boolean matches(Object source, @Nullable ClientLevel level, @Nullable String ip, boolean realm, @Nullable Integer id) {
         return this.source.get() == source
            && (this.level == null ? level == null : this.level.get() == level)
            && Objects.equals(this.ip, ip)
            && this.realm == realm
            && Objects.equals(this.id, id);
      }
   }

   private static final class Naming {
      @Nullable Integer id;
      final long since = System.currentTimeMillis();
      boolean decided;
      @Nullable Integer named;
   }
}
