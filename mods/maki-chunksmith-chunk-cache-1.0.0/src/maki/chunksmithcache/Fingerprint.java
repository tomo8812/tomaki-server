package maki.chunksmithcache;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
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
   private static final String SCHEMA = "maki-chunk-cache-v1";
   private static final Map<MinecraftServer, CompletableFuture<String>> SHARED = new WeakHashMap<>();

   private Fingerprint() {
   }

   static synchronized void kick(MinecraftServer var0) {
      SHARED.computeIfAbsent(var0, var0x -> CompletableFuture.supplyAsync(() -> sharedFingerprint(var0x)));
   }

   static String forLevel(ServerLevel var0) {
      MinecraftServer var1 = var0.getServer();
      kick(var1);
      CompletableFuture var2;
      synchronized (Fingerprint.class) {
         var2 = SHARED.get(var1);
      }

      if (var2 != null && var2.isDone() && !var2.isCompletedExceptionally()) {
         try {
            String var7 = (String)var2.join();
            if (var7 == null) {
               return null;
            }

            MessageDigest var4 = digest();
            update(var4, "maki-chunk-cache-v1");
            update(var4, var7);
            update(var4, Long.toString(var0.getSeed()));
            update(var4, var0.dimension().location().toString());
            update(var4, var0.getChunkSource().getGenerator().getClass().getName());
            update(var4, Integer.toString(WorldVersionBridge.getDataVersion(SharedConstants.getCurrentVersion()).getVersion()));
            return hex(var4.digest()).substring(0, 24);
         } catch (Throwable var5) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static String sharedFingerprint(MinecraftServer var0) {
      try {
         MessageDigest var1 = digest();
         update(var1, "maki-chunk-cache-v1");
         Path var2 = var0.getServerDirectory();
         hashTree(var1, var2.resolve("mods"), true);
         hashTree(var1, var2.resolve("config"), true);
         hashTree(var1, var2.resolve("serverconfig"), true);
         hashTree(var1, var0.getWorldPath(LevelResource.ROOT).resolve("serverconfig"), true);
         hashTree(var1, var0.getWorldPath(LevelResource.ROOT).resolve("datapacks"), true);
         hashLevelDatSettings(var1, var0.getWorldPath(LevelResource.ROOT).resolve("level.dat"));
         return hex(var1.digest());
      } catch (Throwable var3) {
         return null;
      }
   }

   private static void hashLevelDatSettings(MessageDigest var0, Path var1) {
      if (Files.isRegularFile(var1)) {
         try {
            CompoundTag var2 = NbtIo.readCompressed(var1, NbtAccounter.unlimitedHeap());
            CompoundTag var3 = var2.getCompound("Data");
            update(var0, var3.getCompound("DataPacks").toString());
            update(var0, var3.getCompound("WorldGenSettings").toString());
         } catch (Throwable var4) {
            update(var0, "level-dat-settings-unreadable");
         }
      }
   }

   private static void hashTree(MessageDigest var0, Path var1, boolean var2) throws IOException {
      if (Files.isDirectory(var1)) {
         ArrayList var3 = new ArrayList();

         try (Stream var4 = FingerprintWalkBridge.walk(var1)) {
            var4.filter(var0x -> Files.isRegularFile(var0x)).forEach(var3::add);
         }

         var3.sort(Comparator.comparing(var1x -> var1.relativize(var1x).toString()));
         byte[] var15 = new byte[65536];

         for (Path var6 : var3) {
            String var7 = var1.relativize(var6).toString().replace('\\', '/');
            if (!var7.equals("maki-chunksmith-chunk-cache.properties")) {
               update(var0, var7);
               update(var0, Long.toString(Files.size(var6)));
               if (var2) {
                  int var9;
                  try (InputStream var8 = Files.newInputStream(var6)) {
                     while ((var9 = var8.read(var15)) >= 0) {
                        var0.update(var15, 0, var9);
                     }
                  }
               }
            }
         }
      }
   }

   private static MessageDigest digest() {
      try {
         return MessageDigest.getInstance("SHA-256");
      } catch (NoSuchAlgorithmException var1) {
         throw new IllegalStateException(var1);
      }
   }

   private static void update(MessageDigest var0, String var1) {
      var0.update(var1.getBytes(StandardCharsets.UTF_8));
      var0.update((byte)0);
   }

   private static String hex(byte[] var0) {
      StringBuilder var1 = new StringBuilder(var0.length * 2);

      for (byte var5 : var0) {
         var1.append(String.format("%02x", var5));
      }

      return var1.toString();
   }
}
