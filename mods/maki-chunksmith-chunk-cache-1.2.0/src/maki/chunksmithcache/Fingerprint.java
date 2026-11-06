package maki.chunksmithcache;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;

final class Fingerprint {
   private static final String LEGACY_SCHEMA = "maki-chunk-cache-v1";
   private static final Map<MinecraftServer, CompletableFuture<Fingerprint.SharedState>> SHARED = new WeakHashMap<>();
   private static final Map<ServerLevel, Fingerprint.Snapshot> LEVEL_SNAPSHOTS = new WeakHashMap<>();

   private Fingerprint() {
   }

   static synchronized void kick(MinecraftServer server) {
      SHARED.computeIfAbsent(server, value -> CompletableFuture.supplyAsync(() -> sharedState(value)));
   }

   static String forLevel(ServerLevel level) {
      Fingerprint.Snapshot snapshot = snapshotForLevel(level);
      return snapshot == null ? null : snapshot.fingerprint();
   }

   static Fingerprint.Snapshot snapshotForLevel(ServerLevel level) {
      MinecraftServer server = level.getServer();
      kick(server);
      CompletableFuture<Fingerprint.SharedState> future;
      synchronized (Fingerprint.class) {
         Fingerprint.Snapshot cached = LEVEL_SNAPSHOTS.get(level);
         if (cached != null) {
            return cached;
         }

         future = SHARED.get(server);
      }

      if (future != null && future.isDone() && !future.isCompletedExceptionally()) {
         try {
            Fingerprint.SharedState shared = future.join();
            if (shared == null) {
               return null;
            }

            List<FingerprintManifest.Entry> entries = new ArrayList<>(shared.entries());
            addValue(entries, "level:seed", Long.toString(level.getSeed()), "world seed");
            addValue(entries, "level:dimension", level.dimension().location().toString(), "dimension identity");
            addValue(entries, "level:generator-class", level.getChunkSource().getGenerator().getClass().getName(), "chunk generator implementation");
            int dataVersion = WorldVersionBridge.getDataVersion(SharedConstants.getCurrentVersion()).getVersion();
            addValue(entries, "minecraft:data-version", Integer.toString(dataVersion), "serialized chunk data version");
            String fingerprint = CompatibilityFingerprint.compute(entries);
            MessageDigest legacy = CompatibilityFingerprint.digest();
            CompatibilityFingerprint.update(legacy, "maki-chunk-cache-v1");
            CompatibilityFingerprint.update(legacy, shared.legacySharedFingerprint());
            CompatibilityFingerprint.update(legacy, Long.toString(level.getSeed()));
            CompatibilityFingerprint.update(legacy, level.dimension().location().toString());
            CompatibilityFingerprint.update(legacy, level.getChunkSource().getGenerator().getClass().getName());
            CompatibilityFingerprint.update(legacy, Integer.toString(dataVersion));
            String legacyFingerprint = CompatibilityFingerprint.hex(legacy.digest()).substring(0, 24);
            Fingerprint.Snapshot computed = new Fingerprint.Snapshot(fingerprint, legacyFingerprint, shared.policy(), List.copyOf(entries));
            synchronized (Fingerprint.class) {
               Fingerprint.Snapshot cached = LEVEL_SNAPSHOTS.get(level);
               if (cached != null) {
                  return cached;
               }

               LEVEL_SNAPSHOTS.put(level, computed);
            }

            return computed;
         } catch (Throwable ignored) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static Fingerprint.SharedState sharedState(MinecraftServer server) {
      try {
         Path serverDirectory = server.getServerDirectory();
         FingerprintPolicy policy = FingerprintPolicy.load(serverDirectory);
         List<FingerprintManifest.Entry> entries = new ArrayList<>();
         addValue(entries, "policy:compatibility", policy.compatibilitySignature(), "compatibility classification policy");
         MessageDigest legacy = CompatibilityFingerprint.digest();
         CompatibilityFingerprint.update(legacy, "maki-chunk-cache-v1");
         collectTree(legacy, entries, serverDirectory.resolve("mods"), "mods", policy);
         collectTree(legacy, entries, serverDirectory.resolve("config"), "config", policy);
         collectTree(legacy, entries, serverDirectory.resolve("serverconfig"), "serverconfig", policy);
         Path worldRoot = server.getWorldPath(LevelResource.ROOT);
         collectTree(legacy, entries, worldRoot.resolve("serverconfig"), "world/serverconfig", policy);
         collectTree(legacy, entries, worldRoot.resolve("datapacks"), "world/datapacks", policy);
         collectLevelDat(legacy, entries, worldRoot.resolve("level.dat"));
         return new Fingerprint.SharedState(policy, List.copyOf(entries), CompatibilityFingerprint.hex(legacy.digest()));
      } catch (Throwable ignored) {
         return null;
      }
   }

   private static void collectTree(MessageDigest legacy, List<FingerprintManifest.Entry> entries, Path root, String logicalRoot, FingerprintPolicy policy) throws Exception {
      if (Files.isDirectory(root)) {
         List<Path> files = new ArrayList<>();

         try (Stream<Path> stream = FingerprintWalkBridge.walk(root)) {
            stream.filter(x$0 -> Files.isRegularFile(x$0)).forEach(files::add);
         }

         files.sort(Comparator.comparing(path -> root.relativize(path).toString()));
         byte[] buffer = new byte[65536];

         for (Path file : files) {
            String relative = root.relativize(file).toString().replace('\\', '/');
            if (!relative.equals("maki-chunksmith-chunk-cache.properties")) {
               long size = Files.size(file);
               CompatibilityFingerprint.update(legacy, relative);
               CompatibilityFingerprint.update(legacy, Long.toString(size));
               MessageDigest content = CompatibilityFingerprint.digest();

               int read;
               try (InputStream input = Files.newInputStream(file)) {
                  while ((read = input.read(buffer)) >= 0) {
                     if (read != 0) {
                        legacy.update(buffer, 0, read);
                        content.update(buffer, 0, read);
                     }
                  }
               }

               String key = logicalRoot + "/" + relative;
               FingerprintManifest.Impact impact = policy.classify(key, file);
               entries.add(new FingerprintManifest.Entry(key, size, CompatibilityFingerprint.hex(content.digest()), impact, policy.reason(key, file, impact)));
            }
         }
      }
   }

   private static void collectLevelDat(MessageDigest legacy, List<FingerprintManifest.Entry> entries, Path levelDat) {
      if (Files.isRegularFile(levelDat)) {
         try {
            CompoundTag root = NbtIo.readCompressed(levelDat, NbtAccounter.unlimitedHeap());
            CompoundTag data = root.getCompound("Data");
            String dataPacks = data.getCompound("DataPacks").toString();
            String worldGen = data.getCompound("WorldGenSettings").toString();
            CompatibilityFingerprint.update(legacy, dataPacks);
            CompatibilityFingerprint.update(legacy, worldGen);
            addValue(entries, "level.dat:DataPacks", dataPacks, "enabled datapack state");
            addValue(entries, "level.dat:WorldGenSettings", worldGen, "world generation settings");
         } catch (Throwable ignored) {
            CompatibilityFingerprint.update(legacy, "level-dat-settings-unreadable");
            addValue(entries, "level.dat:unreadable", "level-dat-settings-unreadable", "fail-closed level.dat read failure");
         }
      }
   }

   private static void addValue(List<FingerprintManifest.Entry> entries, String key, String value, String reason) {
      byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
      entries.add(new FingerprintManifest.Entry(key, bytes.length, CompatibilityFingerprint.sha256(value), FingerprintManifest.Impact.RELEVANT, reason));
   }

   private record SharedState(FingerprintPolicy policy, List<FingerprintManifest.Entry> entries, String legacySharedFingerprint) {
   }

   record Snapshot(String fingerprint, String legacyFingerprint, FingerprintPolicy policy, List<FingerprintManifest.Entry> entries) {
      Snapshot {
         entries = List.copyOf(entries);
      }

      FingerprintManifest.Manifest manifest(Set<String> storedFingerprints) {
         return new FingerprintManifest.Manifest(this.fingerprint, System.currentTimeMillis(), storedFingerprints, this.entries);
      }
   }
}
