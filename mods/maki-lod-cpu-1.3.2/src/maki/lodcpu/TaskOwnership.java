package maki.lodcpu;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.GenerationTask;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TaskOwnership {
   private static final Logger LOG = LoggerFactory.getLogger("MakiLodFollow");
   private static final String OWNER_KEY = "maki-lod-cpu-owner";
   private static final String OWNER_PREFIX = "follow:";
   private static final String SESSION_OWNER = "follow:" + UUID.randomUUID();
   private static final Set<GenerationTask> OWNED = Collections.newSetFromMap(Collections.synchronizedMap(new WeakHashMap<>()));
   private static boolean loggedExternalActive;
   private static boolean loggedExternalPersisted;
   private static boolean loggedFreshRestart;

   private TaskOwnership() {
   }

   public static void claim(GenerationTask task, String source) {
      if (task != null && OWNED.add(task)) {
         LOG.debug("Claimed follow task ownership from {}", source);
      }
   }

   public static boolean isOwned(GenerationTask task) {
      return task != null && OWNED.contains(task);
   }

   public static TaskOwnership.PersistedOwner persistedOwner(Chunksmith chunky, String world) {
      Path path = taskFile(chunky, world);
      if (!Files.isRegularFile(path)) {
         return TaskOwnership.PersistedOwner.NONE;
      } else {
         String owner = readOwner(path);
         if (owner == null) {
            return TaskOwnership.PersistedOwner.EXTERNAL;
         } else {
            return SESSION_OWNER.equals(owner) ? TaskOwnership.PersistedOwner.CURRENT_SESSION : TaskOwnership.PersistedOwner.PREVIOUS_SESSION;
         }
      }
   }

   public static void markSaved(GenerationTask task) {
      if (isOwned(task)) {
         String world = task.getSelection().world().getName();
         Path path = taskFile(task.getChunky(), world);
         if (Files.isRegularFile(path)) {
            try {
               Files.writeString(path, "maki-lod-cpu-owner=" + SESSION_OWNER + "\n", StandardOpenOption.APPEND);
            } catch (IOException e) {
               LOG.error("Could not persist follow task ownership for {}; it will be treated as external after restart for safety", world, e);
            }
         }
      }
   }

   public static void noteExternalActive() {
      if (!loggedExternalActive) {
         loggedExternalActive = true;
         LOG.warn(
            "LOD follow is waiting: an active minecraft:overworld Chunksmith task is external/unowned and will not be stopped, recentered, or CPU-capped by maki-lod-cpu."
         );
      }

      loggedExternalPersisted = false;
   }

   public static void noteExternalPersisted() {
      if (!loggedExternalPersisted) {
         loggedExternalPersisted = true;
         LOG.warn(
            "LOD follow is waiting: persisted minecraft:overworld Chunksmith state has no maki-lod-cpu ownership marker and will not be resumed, replaced, or overwritten."
         );
      }

      loggedExternalActive = false;
   }

   public static void noteOwnedOrAbsent() {
      loggedExternalActive = false;
      loggedExternalPersisted = false;
   }

   public static void noteFreshRestart() {
      if (!loggedFreshRestart) {
         loggedFreshRestart = true;
         LOG.info("Previous-session follow task state found, but Chunksmith continueOnRestart=false; starting a fresh follow task instead of resuming it.");
      }
   }

   public static String status(TaskOwnership.PersistedOwner owner) {
      return switch (owner) {
         case NONE -> "none";
         case EXTERNAL -> "external";
         case CURRENT_SESSION, PREVIOUS_SESSION -> "follow";
      };
   }

   public static TaskOwnership.PersistedTaskSnapshot snapshot(Chunksmith chunky, String world) {
      Path path = taskFile(chunky, world);
      if (!Files.isRegularFile(path)) {
         return new TaskOwnership.PersistedTaskSnapshot(false, "none", -1L, null, null, null, null, null, null, null, null, null, null);
      }

      Properties props = new Properties();

      try (InputStream in = Files.newInputStream(path)) {
         props.load(in);
      } catch (IOException | RuntimeException e) {
         return new TaskOwnership.PersistedTaskSnapshot(true, "unreadable", modified(path), null, null, null, null, null, null, null, null, null, null);
      }

      String raw = props.getProperty("maki-lod-cpu-owner");
      String owner = raw != null && raw.startsWith("follow:") && raw.length() > "follow:".length()
         ? (SESSION_OWNER.equals(raw) ? "follow-current-session" : "follow-previous-session")
         : "external";
      return new TaskOwnership.PersistedTaskSnapshot(
         true,
         owner,
         modified(path),
         longValue(props, "chunks"),
         longValue(props, "time"),
         booleanValue(props, "cancelled"),
         doubleValue(props, "center-x"),
         doubleValue(props, "center-z"),
         doubleValue(props, "radius"),
         doubleValue(props, "radius-z"),
         doubleValue(props, "lod-horizon"),
         props.getProperty("shape"),
         props.getProperty("pattern")
      );
   }

   private static long modified(Path path) {
      try {
         return Files.getLastModifiedTime(path).toMillis();
      } catch (IOException e) {
         return -1L;
      }
   }

   private static Long longValue(Properties props, String key) {
      try {
         String value = props.getProperty(key);
         return value == null ? null : Long.valueOf(value);
      } catch (NumberFormatException e) {
         return null;
      }
   }

   private static Double doubleValue(Properties props, String key) {
      try {
         String value = props.getProperty(key);
         return value == null ? null : Double.valueOf(value);
      } catch (NumberFormatException e) {
         return null;
      }
   }

   private static Boolean booleanValue(Properties props, String key) {
      String value = props.getProperty(key);
      return value == null ? null : Boolean.valueOf(value);
   }

   private static Path taskFile(Chunksmith chunky, String world) {
      return chunky.getConfig().getDirectory().resolve("tasks").resolve(world.replace(':', '/') + ".properties");
   }

   private static String readOwner(Path path) {
      Properties props = new Properties();

      try (InputStream in = Files.newInputStream(path)) {
         props.load(in);
      } catch (IOException | RuntimeException e) {
         return null;
      }

      String value = props.getProperty("maki-lod-cpu-owner");
      return value != null && value.startsWith("follow:") && value.length() > "follow:".length() ? value : null;
   }

   public enum PersistedOwner {
      NONE,
      EXTERNAL,
      CURRENT_SESSION,
      PREVIOUS_SESSION;

      // $VF: synthetic method
      private static TaskOwnership.PersistedOwner[] $values() {
         return new TaskOwnership.PersistedOwner[]{NONE, EXTERNAL, CURRENT_SESSION, PREVIOUS_SESSION};
      }
   }

   public record PersistedTaskSnapshot(
      boolean present,
      String owner,
      long modifiedAt,
      Long chunks,
      Long time,
      Boolean cancelled,
      Double centerX,
      Double centerZ,
      Double radius,
      Double radiusZ,
      Double lodHorizon,
      String shape,
      String pattern
   ) {
   }
}
