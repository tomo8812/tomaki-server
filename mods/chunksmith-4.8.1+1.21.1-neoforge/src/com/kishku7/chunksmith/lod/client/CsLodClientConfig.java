package com.kishku7.chunksmith.lod.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class CsLodClientConfig {
   public static final String FILE_NAME = "chunksmith-lod.properties";
   public static final String KEY_SYNC_SECONDS = "sync-interval-seconds";
   public static final String KEY_REINJECT = "reinject-on-join";
   public static final String KEY_MAX_DISK_MB = "max-disk-mb";
   public static final int DEFAULT_SYNC_SECONDS = 300;
   public static final int MIN_SYNC_SECONDS = 30;
   private static final String COMMENT = " Chunksmith LOD client.\n\n Both settings can also be changed in-game with /cslod set <name> <value>, which applies\n the value immediately and rewrites this file. Editing by hand still works; the file is\n read at startup.\n\n sync-interval-seconds: how often (in SECONDS) to ask the server whether its LOD store\n has changed. The check itself is two tiny messages; a full index is only pulled when the\n answer is 'yes'. This is what lets a player who is STANDING STILL pick up terrain from a\n pregen that is still running, with no relog and no need to go for a walk.\n\n Default 300. Values below 30 are clamped to 30.\n\n reinject-on-join: normally Chunksmith remembers which LOD regions it has already\n given to your renderer, so joining a world does not re-send terrain voxy or Distant\n Horizons already has. Set this to true for ONE join if you have deleted or reset your\n renderer's own data and want everything sent again. Default false.\n\n max-disk-mb: a ceiling, in MEGABYTES, on what Chunksmith will store per\n dimension per server. 0 (the default) means no ceiling, which is how every version\n before 4.0.0 behaved. How much arrives is otherwise entirely the server's choice.\n Regions nearest you are kept first, so a ceiling shortens your far horizon rather\n than leaving gaps nearby.";
   private static volatile int syncSeconds = 300;
   private static volatile boolean reinject;
   private static volatile long maxDiskMb;
   private static volatile boolean loaded;
   private static volatile Path file;

   private CsLodClientConfig() {
   }

   public static synchronized String load(Path configDir) {
      Path path = configDir.resolve("chunksmith-lod.properties");
      file = path;
      Properties properties = new Properties();
      boolean present = false;
      if (Files.isRegularFile(path)) {
         try (InputStream in = Files.newInputStream(path)) {
            properties.load(in);
            present = true;
         } catch (IOException e) {
            loaded = true;
            syncSeconds = 300;
            reinject = false;
            maxDiskMb = 0L;
            return "could not read chunksmith-lod.properties (" + e + "); using the defaults";
         }
      }

      reinject = Boolean.parseBoolean(properties.getProperty("reinject-on-join", "false").trim());
      long disk = 0L;
      String rawDisk = properties.getProperty("max-disk-mb");
      if (rawDisk != null && !rawDisk.isBlank()) {
         try {
            disk = Math.max(0L, Long.parseLong(rawDisk.trim()));
         } catch (NumberFormatException ignored) {
            disk = 0L;
         }
      }

      maxDiskMb = disk;
      String raw = properties.getProperty("sync-interval-seconds");
      int requested = 300;
      boolean unparseable = false;
      if (raw != null && !raw.isBlank()) {
         try {
            requested = Integer.parseInt(raw.trim());
         } catch (NumberFormatException e) {
            unparseable = true;
         }
      }

      int clamped = clamp(requested);
      syncSeconds = clamped;
      loaded = true;
      if (!present) {
         save();
         return "wrote chunksmith-lod.properties with the defaults (sync every " + clamped + "s)";
      } else if (unparseable) {
         return "chunksmith-lod.properties: '" + raw + "' is not a number; syncing every 300s";
      } else if (clamped != requested) {
         return "chunksmith-lod.properties: sync-interval-seconds=" + requested + " is below the 30s minimum; syncing every " + clamped + "s instead";
      } else {
         return reinject
            ? "syncing with the server every "
               + clamped
               + "s; reinject-on-join is ON, so every LOD region will be sent to your renderer again this session (set it back to false once your terrain is back)"
            : "syncing with the server every " + clamped + "s";
      }
   }

   public static long syncIntervalMillis() {
      return syncSeconds * 1000L;
   }

   public static int syncIntervalSeconds() {
      return syncSeconds;
   }

   public static boolean reinjectOnJoin() {
      return reinject;
   }

   public static long maxDiskMb() {
      return maxDiskMb;
   }

   public static long maxDiskBytes() {
      long megabytes = maxDiskMb;
      return megabytes <= 0L ? 0L : megabytes * 1024L * 1024L;
   }

   public static synchronized long setMaxDiskMb(long megabytes) {
      maxDiskMb = Math.max(0L, megabytes);
      save();
      return maxDiskMb;
   }

   public static boolean isLoaded() {
      return loaded;
   }

   public static synchronized int setSyncSeconds(int seconds) {
      syncSeconds = clamp(seconds);
      save();
      return syncSeconds;
   }

   public static synchronized void setReinjectOnJoin(boolean value) {
      reinject = value;
      save();
   }

   public static int clamp(int seconds) {
      return Math.max(30, seconds);
   }

   static void setForTesting(int seconds) {
      syncSeconds = clamp(seconds);
      loaded = true;
   }

   static void setReinjectForTesting(boolean value) {
      reinject = value;
   }

   private static void save() {
      Path path = file;
      if (path != null) {
         Properties out = new Properties();
         out.setProperty("sync-interval-seconds", Integer.toString(syncSeconds));
         out.setProperty("reinject-on-join", Boolean.toString(reinject));
         out.setProperty("max-disk-mb", Long.toString(maxDiskMb));

         try {
            Files.createDirectories(path.getParent());

            try (OutputStream stream = Files.newOutputStream(path)) {
               out.store(
                  stream,
                  " Chunksmith LOD client.\n\n Both settings can also be changed in-game with /cslod set <name> <value>, which applies\n the value immediately and rewrites this file. Editing by hand still works; the file is\n read at startup.\n\n sync-interval-seconds: how often (in SECONDS) to ask the server whether its LOD store\n has changed. The check itself is two tiny messages; a full index is only pulled when the\n answer is 'yes'. This is what lets a player who is STANDING STILL pick up terrain from a\n pregen that is still running, with no relog and no need to go for a walk.\n\n Default 300. Values below 30 are clamped to 30.\n\n reinject-on-join: normally Chunksmith remembers which LOD regions it has already\n given to your renderer, so joining a world does not re-send terrain voxy or Distant\n Horizons already has. Set this to true for ONE join if you have deleted or reset your\n renderer's own data and want everything sent again. Default false.\n\n max-disk-mb: a ceiling, in MEGABYTES, on what Chunksmith will store per\n dimension per server. 0 (the default) means no ceiling, which is how every version\n before 4.0.0 behaved. How much arrives is otherwise entirely the server's choice.\n Regions nearest you are kept first, so a ceiling shortens your far horizon rather\n than leaving gaps nearby."
               );
            }
         } catch (IOException var7) {
         }
      }
   }
}
