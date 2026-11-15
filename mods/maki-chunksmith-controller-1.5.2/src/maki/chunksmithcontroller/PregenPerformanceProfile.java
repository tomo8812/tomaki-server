package maki.chunksmithcontroller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.OptionalInt;
import java.util.Properties;

final class PregenPerformanceProfile {
   static final String CONTROLLER_VERSION = "native-full-v8";
   static final long MAX_AGE_MILLIS = 86400000L;

   private PregenPerformanceProfile() {
   }

   static String workClassBand(long newPairs, long repairPairs) {
      long total = Math.max(0L, newPairs) + Math.max(0L, repairPairs);
      if (total < 8L) {
         return "UNKNOWN";
      }

      double ratio = (double)Math.max(0L, newPairs) / total;
      return ratio >= 0.7 ? "NEW_HEAVY" : (ratio <= 0.3 ? "REPAIR_HEAVY" : "MIXED");
   }

   static OptionalInt load(Path file, PregenPerformanceProfile.Environment current, Settings settings, long nowMillis) {
      if (file != null && current != null && settings != null && Files.isRegularFile(file)) {
         Properties p = new Properties();

         try (InputStream in = Files.newInputStream(file)) {
            p.load(in);
         } catch (IOException | RuntimeException invalid) {
            return OptionalInt.empty();
         }

         try {
            if (!"native-full-v8".equals(p.getProperty("controllerVersion"))) {
               return OptionalInt.empty();
            }

            long savedAt = Long.parseLong(p.getProperty("savedAt", "-1"));
            if (savedAt <= 0L || nowMillis < savedAt || nowMillis - savedAt > 86400000L) {
               return OptionalInt.empty();
            }

            if (!same(p, current)) {
               return OptionalInt.empty();
            }

            int stored = Integer.parseInt(p.getProperty("cap", "2"));
            return OptionalInt.of(Math.max(settings.min, Math.min(settings.max, stored)));
         } catch (RuntimeException invalid) {
            return OptionalInt.empty();
         }
      } else {
         return OptionalInt.empty();
      }
   }

   static boolean save(Path file, PregenPerformanceProfile.Environment environment, int cap, long nowMillis) {
      if (file != null && environment != null && !"UNKNOWN".equals(environment.workClassBand())) {
         Properties p = new Properties();
         p.setProperty("controllerVersion", environment.controllerVersion());
         p.setProperty("savedAt", Long.toString(nowMillis));
         p.setProperty("cap", Integer.toString(cap));
         p.setProperty("cpuAffinity", environment.cpuAffinity());
         p.setProperty("cpuQuota", environment.cpuQuota());
         p.setProperty("cpuCapacity", Double.toString(environment.cpuCapacity()));
         p.setProperty("heapMaxBytes", Long.toString(environment.heapMaxBytes()));
         p.setProperty("chunksmithConfig", environment.chunksmithConfig());
         p.setProperty("pairFeedVersion", environment.pairFeedVersion());
         p.setProperty("fastSignature", Long.toString(environment.fastSignature()));
         p.setProperty("workClassBand", environment.workClassBand());
         Path tmp = file.resolveSibling(file.getFileName() + ".tmp");

         try {
            Files.createDirectories(file.toAbsolutePath().normalize().getParent());

            try (OutputStream out = Files.newOutputStream(tmp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
               p.store(out, "maki-chunksmith-controller pregen performance hint; native generation performance hint");
            }

            try {
               Files.move(tmp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException unsupported) {
               Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }

            return true;
         } catch (IOException | RuntimeException failure) {
            try {
               Files.deleteIfExists(tmp);
            } catch (IOException var10) {
            }

            return false;
         }
      } else {
         return false;
      }
   }

   private static boolean same(Properties p, PregenPerformanceProfile.Environment e) {
      if (!e.controllerVersion().equals(p.getProperty("controllerVersion", ""))) {
         return false;
      } else if (!e.cpuAffinity().equals(p.getProperty("cpuAffinity", ""))) {
         return false;
      } else if (!e.cpuQuota().equals(p.getProperty("cpuQuota", ""))) {
         return false;
      } else {
         double capacity = Double.parseDouble(p.getProperty("cpuCapacity", "NaN"));
         if (!Double.isFinite(capacity) || Math.abs(capacity - e.cpuCapacity()) > 0.05) {
            return false;
         } else if (e.heapMaxBytes() != Long.parseLong(p.getProperty("heapMaxBytes", "-1"))) {
            return false;
         } else if (!e.chunksmithConfig().equals(p.getProperty("chunksmithConfig", ""))) {
            return false;
         } else if (!e.pairFeedVersion().equals(p.getProperty("pairFeedVersion", ""))) {
            return false;
         } else {
            return e.fastSignature() != Long.parseLong(p.getProperty("fastSignature", "0"))
               ? false
               : e.workClassBand().equals(p.getProperty("workClassBand", ""));
         }
      }
   }

   record Environment(
      String controllerVersion,
      String cpuAffinity,
      String cpuQuota,
      double cpuCapacity,
      long heapMaxBytes,
      String chunksmithConfig,
      String pairFeedVersion,
      long fastSignature,
      String workClassBand
   ) {
   }
}
