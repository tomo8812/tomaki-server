package dev.diorama.render.gpu;

import dev.diorama.Diorama;
import dev.diorama.Settings;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import org.jspecify.annotations.Nullable;

public final class GpuBakeCheck {
   private static final String FORCED = System.getenv("DIORAMA_GPU_BAKE");
   private static final int TILES = 4;
   private static final int FAILURES = 3;
   private static final double MAX_MEAN = 6.0;
   private static final double MAX_BIAS = 2.2;
   private static final double MAX_CHANNEL_BIAS = 3.0;
   private static final double MAX_MISMATCH = 0.02;
   private static final double MAX_ALPHA = 0.08;
   private static final double MAX_OVERLAY = 0.02;
   private static final int PASS_PIXELS = 65536;
   private static final int JUDGE_PIXELS = 2048;
   private static final long MAX_COST = Long.getLong("diorama.gpuBakeMaxMs", 50L) * 1000000L;
   private static final int FAILURES_IN_A_ROW = 3;
   private static final Object LOCK = new Object();
   private static volatile GpuBakeCheck.@Nullable Off off;
   private static volatile boolean opened;
   private static boolean unproven;
   private static boolean retrying;
   private static @Nullable String openedDevice;
   private static boolean closingHooked;
   private static boolean closing;
   private static int failuresInARow;
   public static final int SKY = 0;
   public static final int CAVE = 1;
   public static final int UNDER_ROOF = 2;
   private static final GpuBakeCheck.Check[] CHECKS = new GpuBakeCheck.Check[]{
      new GpuBakeCheck.Check("sky view"),
      new GpuBakeCheck.Check("cave view"),
      new GpuBakeCheck.Check("view under a roof"),
      new GpuBakeCheck.Check("sky view with overlays"),
      new GpuBakeCheck.Check("cave view with overlays"),
      new GpuBakeCheck.Check("view under a roof with overlays")
   };

   private GpuBakeCheck() {
   }

   public static boolean wanted() {
      return "0".equals(FORCED) ? false : forcedOn() || Settings.get().gpuBaking;
   }

   public static boolean failed() {
      return off != null;
   }

   public static GpuBakeCheck.@Nullable Off off() {
      return off;
   }

   private static boolean forcedOn() {
      return "1".equals(FORCED);
   }

   static boolean opened() {
      return opened;
   }

   static void open(@Nullable String device) {
      synchronized (LOCK) {
         opened = true;
         openedDevice = device;
         if (device != null) {
            GpuBakeRecord record = GpuBakeRecord.load();
            String mod = modVersion();
            boolean elsewhere = readMark(record);
            if (retrying) {
               retrying = false;
               record.off.remove(device);
            }

            GpuBakeCheck.Off saved = forcedOn() ? null : record.result(device, mod);
            if (!forcedOn() && saved == null && record.off.containsKey(device) && mod.equals(record.off.get(device).mod)) {
               Diorama.LOGGER.info("GPU baking turned off once before on {} ({}); checking it again.", device, record.off.get(device).result);
            }

            if (saved != null) {
               off = saved;
               Diorama.LOGGER.info("GPU baking stays off on {}: {} here before. Tiles are traced on the CPU.", device, saved);
               record.save();
            } else if (!elsewhere) {
               mark(record, device, mod);
            }
         }
      }
   }

   private static boolean readMark(GpuBakeRecord record) {
      String crashed = record.starting;
      if (crashed == null || record.startingProcess == ProcessHandle.current().pid()) {
         return false;
      }

      if (ProcessHandle.of(record.startingProcess).filter(ProcessHandle::isAlive).isPresent()) {
         return true;
      }

      record.starting = null;
      if (!forcedOn()) {
         Diorama.LOGGER.warn("The game went down during the first GPU bakes on {}. Tiles are traced on the CPU on that card from now on.", crashed);
         record.turnedOff(crashed, GpuBakeCheck.Off.CRASHED, record.startingMod == null ? "" : record.startingMod);
      }

      return false;
   }

   private static void mark(GpuBakeRecord record, String device, String mod) {
      record.starting = device;
      record.startingProcess = ProcessHandle.current().pid();
      record.startingMod = mod;
      record.save();
      unproven = true;
      if (!closingHooked) {
         closingHooked = true;
         Runtime.getRuntime().addShutdownHook(new Thread(GpuBakeCheck::close, "diorama-gpu-closing"));
      }
   }

   public static void checking(int view, boolean overlays) {
      synchronized (LOCK) {
         GpuBakeCheck.Check check = check(view, overlays);
         if (!check.sent) {
            check.sent = true;
            String device = openedDevice;
            if (!unproven && off == null && device != null && !closing) {
               GpuBakeRecord record = GpuBakeRecord.load();
               if (!readMark(record)) {
                  String mod = modVersion();
                  GpuBakeCheck.Off saved = forcedOn() ? null : record.result(device, mod);
                  if (saved != null) {
                     off = saved;
                     Diorama.LOGGER.warn("GPU baking turned off on {}: {} in another game. Tiles are traced on the CPU instead.", device, saved);
                     record.save();
                  } else {
                     mark(record, device, mod);
                  }
               }
            }
         }
      }
   }

   public static void readBack(int view, boolean overlays) {
      synchronized (LOCK) {
         check(view, overlays).readBack = true;
         if (unproven && allReadBack()) {
            proven();
         }
      }
   }

   private static boolean allReadBack() {
      return Arrays.stream(CHECKS).allMatch(check -> !check.sent || check.readBack);
   }

   private static void close() {
      synchronized (LOCK) {
         closing = true;
         proven();
      }
   }

   private static void proven() {
      synchronized (LOCK) {
         if (unproven) {
            GpuBakeRecord record = GpuBakeRecord.load();
            clearMark(record);
            record.save();
         }
      }
   }

   private static void clearMark(GpuBakeRecord record) {
      unproven = false;
      if (record.startingProcess == ProcessHandle.current().pid()) {
         record.starting = null;
      }
   }

   public static void retry() {
      synchronized (LOCK) {
         retrying = true;
         if (off == null) {
            if (opened && openedDevice != null) {
               retrying = false;
               GpuBakeRecord record = GpuBakeRecord.load();
               if (record.off.remove(openedDevice) != null) {
                  record.save();
               }
            }
         } else {
            off = null;
            opened = false;
            failuresInARow = 0;

            for (GpuBakeCheck.Check check : CHECKS) {
               check.reset();
            }

            GpuTileBaker.retry();
         }
      }
   }

   private static String modVersion() {
      return ModList.get().getModContainerById("diorama").map(mod -> mod.getModInfo().getVersion().toString()).orElse("");
   }

   private static GpuBakeCheck.Check check(int view, boolean overlays) {
      return CHECKS[view + (overlays ? 3 : 0)];
   }

   public static boolean trusted(int view) {
      return wanted() && !failed() && (check(view, false).passed || check(view, true).passed);
   }

   public static GpuBakeCheck.Mode mode(int view, boolean fogOfWar, boolean overlays) {
      if (!wanted() || off != null) {
         return GpuBakeCheck.Mode.TRACE;
      }

      if (!opened) {
         GpuTileBaker.use();
         return GpuBakeCheck.Mode.TRACE;
      }

      GpuBakeCheck.Check check = check(view, overlays);
      if (check.passed) {
         return GpuBakeCheck.Mode.GPU;
      }

      if (fogOfWar) {
         return GpuBakeCheck.Mode.TRACE;
      }

      synchronized (LOCK) {
         if (off == null && !check.passed && check.compared + check.out < 4) {
            check.out++;
            return GpuBakeCheck.Mode.CHECK;
         }
      }

      return GpuBakeCheck.Mode.TRACE;
   }

   public static void compare(int view, int[] gpu, int[] traced, byte @Nullable [] gpuInfo, byte @Nullable [] tracedInfo, long costNanos) {
      boolean overlays = tracedInfo != null;
      long both = 0L;
      long mismatched = 0L;
      long alphaDiffers = 0L;
      long overlayDiffers = 0L;
      double sum = 0.0;
      double bias = 0.0;
      double redBias = 0.0;
      double greenBias = 0.0;
      double blueBias = 0.0;

      for (int i = 0; i < traced.length; i++) {
         int c = traced[i];
         int g = gpu[i];
         boolean ck = c >>> 24 != 0;
         if (ck != (g >>> 24 != 0)) {
            mismatched++;
         } else if (ck) {
            both++;
            if (c >>> 24 != g >>> 24) {
               alphaDiffers++;
            }

            if (overlays && (gpuInfo == null || gpuInfo[i] != tracedInfo[i])) {
               overlayDiffers++;
            }

            int dr = (g & 0xFF) - (c & 0xFF);
            int dg = (g >> 8 & 0xFF) - (c >> 8 & 0xFF);
            int db = (g >> 16 & 0xFF) - (c >> 16 & 0xFF);
            sum += (Math.abs(dr) + Math.abs(dg) + Math.abs(db)) / 3.0;
            bias += (dr + dg + db) / 3.0;
            redBias += dr;
            greenBias += dg;
            blueBias += db;
         }
      }

      GpuBakeCheck.Check check = check(view, overlays);
      synchronized (LOCK) {
         check.out--;
         if (off == null && !check.passed) {
            double mismatch = (double)mismatched / traced.length;
            boolean judged = both >= 2048L;
            double mean = judged ? sum / both : 0.0;
            double brighter = judged ? bias / both : 0.0;
            double channel = judged ? Math.max(Math.abs(redBias), Math.max(Math.abs(greenBias), Math.abs(blueBias))) / both : 0.0;
            double alpha = judged ? (double)alphaDiffers / Math.max(both, 65536L) : 0.0;
            double overlay = judged && overlays ? (double)overlayDiffers / both : 0.0;
            if (mean > 6.0 || Math.abs(brighter) > 2.2 || channel > 3.0 || mismatch > 0.02 || alpha > 0.08 || overlay > 0.02) {
               fail(
                  GpuBakeCheck.Off.NO_MATCH,
                  String.format(
                     Locale.ROOT,
                     "a %s tile doesn't match the tracer's (mean difference %.2f/255, %+.2f brighter, one channel off by %.2f, %.2f%% known/unknown mismatch, %.2f%% different alpha, %.2f%% different overlay bits)",
                     check.name,
                     mean,
                     brighter,
                     channel,
                     mismatch * 100.0,
                     alpha * 100.0,
                     overlay * 100.0
                  )
               );
            } else if (both >= 65536L) {
               check.costs[check.compared++] = costNanos;
               check.worstMean = Math.max(check.worstMean, mean);
               check.worstBias = Math.abs(brighter) > Math.abs(check.worstBias) ? brighter : check.worstBias;
               check.worstMismatch = Math.max(check.worstMismatch, mismatch);
               check.worstAlpha = Math.max(check.worstAlpha, alpha);
               check.worstOverlay = Math.max(check.worstOverlay, overlay);
               check.worstChannel = Math.max(check.worstChannel, channel);
               if (check.compared >= 4) {
                  long[] costs = (long[])check.costs.clone();
                  Arrays.sort(costs);
                  long cost = (costs[1] + costs[2]) / 2L;
                  if (cost > MAX_COST && !forcedOn()) {
                     fail(
                        GpuBakeCheck.Off.TOO_SLOW,
                        String.format(
                           Locale.ROOT, "a %s tile takes %.1f ms, past the %.0f ms that stalls a frame", check.name, cost / 1000000.0, MAX_COST / 1000000.0
                        )
                     );
                     return;
                  }

                  check.passed = true;
                  Diorama.LOGGER
                     .info(
                        "GPU baking matches the tracer on {} ({}): on",
                        GpuTileBaker.device(),
                        String.format(
                           Locale.ROOT,
                           "%s, %d tiles, %.1f ms a tile, worst: mean difference %.2f/255, %+.2f brighter, one channel off by %.2f, %.2f%% known/unknown mismatch, %.2f%% different alpha, %.2f%% different overlay bits",
                           check.name,
                           check.compared,
                           cost / 1000000.0,
                           check.worstMean,
                           check.worstBias,
                           check.worstChannel,
                           check.worstMismatch * 100.0,
                           check.worstAlpha * 100.0,
                           check.worstOverlay * 100.0
                        )
                     );
               }
            }
         }
      }
   }

   public static void undrawn(int view, boolean overlays, boolean cannotDraw) {
      GpuBakeCheck.Check check = check(view, overlays);
      synchronized (LOCK) {
         check.out--;
         if (cannotDraw && off == null && !check.passed && ++check.failures >= 3) {
            fail(GpuBakeCheck.Off.NO_MATCH, "couldn't draw any of " + check.failures + " " + check.name + " tiles");
         }
      }
   }

   public static void drawn(boolean drew) {
      synchronized (LOCK) {
         failuresInARow = drew ? 0 : failuresInARow + 1;
         if (failuresInARow >= 3 && off == null) {
            fail(GpuBakeCheck.Off.NO_MATCH, "couldn't draw " + failuresInARow + " tiles in a row");
         }
      }
   }

   static void broken(String why) {
      synchronized (LOCK) {
         if (off == null) {
            fail(GpuBakeCheck.Off.NO_MATCH, why);
         }
      }
   }

   private static void fail(GpuBakeCheck.Off why, String details) {
      off = why;
      String device = GpuTileBaker.device();
      Diorama.LOGGER.warn("GPU baking turned off on {}: {}. Tiles are traced on the CPU instead.", device, details);
      GpuBakeRecord record = GpuBakeRecord.load();
      record.turnedOff(device, why, modVersion());
      if (unproven && allReadBack()) {
         clearMark(record);
      }

      record.save();
      if (why != GpuBakeCheck.Off.TOO_SLOW) {
         Minecraft mc = Minecraft.getInstance();
         mc.execute(() -> {
            Component message = Component.translatable("diorama.gpu_baking.failed");
            mc.gui.getChat().addMessage(message);
         });
      }
   }

   private static final class Check {
      final String name;
      volatile boolean passed;
      boolean sent;
      boolean readBack;
      int out;
      int compared;
      int failures;
      double worstMean;
      double worstBias;
      double worstMismatch;
      double worstAlpha;
      double worstOverlay;
      double worstChannel;
      final long[] costs = new long[4];

      Check(String name) {
         this.name = name;
      }

      void reset() {
         this.passed = false;
         this.compared = 0;
         this.failures = 0;
         this.worstMean = 0.0;
         this.worstBias = 0.0;
         this.worstMismatch = 0.0;
         this.worstAlpha = 0.0;
         this.worstOverlay = 0.0;
         this.worstChannel = 0.0;
      }
   }

   public enum Mode {
      TRACE,
      GPU,
      CHECK;

      // $VF: synthetic method
      private static GpuBakeCheck.Mode[] $values() {
         return new GpuBakeCheck.Mode[]{TRACE, GPU, CHECK};
      }
   }

   public enum Off {
      NO_MATCH,
      TOO_SLOW,
      CRASHED;

      // $VF: synthetic method
      private static GpuBakeCheck.Off[] $values() {
         return new GpuBakeCheck.Off[]{NO_MATCH, TOO_SLOW, CRASHED};
      }
   }
}
