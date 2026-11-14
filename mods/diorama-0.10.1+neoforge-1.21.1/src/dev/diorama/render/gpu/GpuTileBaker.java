package dev.diorama.render.gpu;

import dev.diorama.Diorama;
import dev.diorama.MapStyle;
import dev.diorama.bake.GpuMesher;
import dev.diorama.bake.TileBaker;
import dev.diorama.render.SavedChunkImport;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

public final class GpuTileBaker {
   private static final double SHARE = Math.max(0.01, Math.min(1.0, Double.parseDouble(System.getProperty("diorama.gpuBakeShare", "0.1"))));
   private static final double BACKGROUND_SHARE = Math.max(0.001, Math.min(1.0, Double.parseDouble(System.getProperty("diorama.gpuBackgroundShare", "0.03"))));
   private static final double TRACE_NANOS = 1.0E9;
   static final int SCENE = 1028;
   static final int SHADOW_SIZE = 2048;
   static final int SKY_WIDTH = 2048;
   static final int SKY_HEIGHT = 1024;
   static final int VOXEL_WIDTH = 4096;
   static final int VOXEL_HEIGHT = 512;
   static final int FACE_BYTES = 48;
   static final int CASTER_BYTES = 16;
   static final int UBO_SIZE = 528;
   private static final long STUCK_NANOS = 20000000000L;
   private static final int STUCK_FRAMES = 240;
   private static volatile boolean used;
   private static volatile boolean dead;
   private static volatile @Nullable String device;
   private static final ConcurrentLinkedQueue<GpuTileBaker.Job> QUEUE = new ConcurrentLinkedQueue<>();
   private static final ConcurrentLinkedQueue<GpuTileBaker.Job> BACKGROUND = new ConcurrentLinkedQueue<>();
   private static final Set<GpuTileBaker.Job> PENDING = ConcurrentHashMap.newKeySet();
   private static long lastFrame;
   private static double budget;
   private static double backgroundBudget;
   private static long frames;
   private static volatile double renderCost = 1000000.0;
   private static volatile double gpuCost = 1000000.0;
   private static final AtomicLong STAT_COUNT = new AtomicLong();
   private static final AtomicLong STAT_MESH = new AtomicLong();
   private static final AtomicLong STAT_PACK = new AtomicLong();
   private static final AtomicLong STAT_RECORD = new AtomicLong();
   private static final AtomicLong STAT_GPU = new AtomicLong();
   private static final AtomicLong STAT_LATENCY = new AtomicLong();

   private GpuTileBaker() {
   }

   public static void submit(GpuMesher.Mesh mesh, Consumer<GpuTileBaker.Result> done) {
      GpuTileBaker.Job job = new GpuTileBaker.Job(mesh, done);
      if (dead) {
         job.fail();
      } else {
         QUEUE.add(job);
         used = true;
         if (dead && QUEUE.remove(job)) {
            job.fail();
         }
      }
   }

   static byte[] channel(byte[] both, int first) {
      byte[] out = new byte[both.length / 2];

      for (int i = 0; i < out.length; i++) {
         out[i] = both[2 * i + first];
      }

      return out;
   }

   static void use() {
      used = true;
   }

   static void retry() {
      dead = false;
   }

   public static Runnable submitBackground(GpuMesher.Mesh mesh, Consumer<GpuTileBaker.Result> done) {
      GpuTileBaker.Job job = new GpuTileBaker.Job(mesh, done);
      if (dead) {
         job.fail();
         return () -> {};
      }

      BACKGROUND.add(job);
      used = true;
      if (dead && BACKGROUND.remove(job)) {
         job.fail();
      }

      return () -> {
         if (BACKGROUND.remove(job)) {
            job.cancel();
         }
      };
   }

   public static boolean keepsUpInBackground() {
      return cost() / BACKGROUND_SHARE < 1.0E9;
   }

   public static void cancelAll() {
      GpuTileBaker.Job job;
      while ((job = QUEUE.poll()) != null) {
         job.cancel();
      }

      while ((job = BACKGROUND.poll()) != null) {
         job.cancel();
      }

      for (GpuTileBaker.Job pending : PENDING) {
         pending.cancel();
      }
   }

   public static String device() {
      String name = device;
      return name == null ? "a GPU not set up yet" : name;
   }

   public static boolean frame() {
      if (used && !dead) {
         try {
            if (!GpuBakeCheck.opened()) {
               device = GpuTileBaker.Backend.INSTANCE.device();
               GpuBakeCheck.open(device);
               if (GpuBakeCheck.failed()) {
                  cancelAll();
                  return false;
               }
            }

            GpuTileBaker.Backend.INSTANCE.poll();
            long now = System.nanoTime();
            frames++;

            for (GpuTileBaker.Job job : PENDING) {
               if (now - job.drawn > 20000000000L && frames - job.drawnFrame > 240L) {
                  throw new IllegalStateException("the pixels of " + job.mesh.key + " never came back");
               }
            }

            return drawQueued(now);
         } catch (Throwable t) {
            Diorama.LOGGER.error("GPU baking stopped", t);
            dead = true;
            GpuBakeCheck.broken(String.valueOf(t));
            failAll();
            return false;
         }
      } else {
         return false;
      }
   }

   private static void failAll() {
      GpuTileBaker.Job job;
      while ((job = QUEUE.poll()) != null) {
         job.fail();
      }

      while ((job = BACKGROUND.poll()) != null) {
         job.fail();
      }

      for (GpuTileBaker.Job pending : PENDING) {
         pending.fail();
      }
   }

   private static boolean drawQueued(long now) {
      if (lastFrame != 0L) {
         budget = Math.min(budget + SHARE * (now - lastFrame), 3.0 * cost());
         double backgroundShare = SavedChunkImport.playing() ? BACKGROUND_SHARE : SHARE;
         backgroundBudget = Math.min(backgroundBudget + backgroundShare * (now - lastFrame), cost());
      }

      lastFrame = now;
      boolean drew = false;

      while (budget >= cost()) {
         GpuTileBaker.Job job = QUEUE.poll();
         if (job == null) {
            break;
         }

         if (draw(job)) {
            budget = budget - cost();
            drew = true;
         }
      }

      if (!QUEUE.isEmpty()) {
         return drew;
      }

      while (backgroundBudget >= cost()) {
         GpuTileBaker.Job job = BACKGROUND.poll();
         if (job == null) {
            return drew;
         }

         if (draw(job)) {
            backgroundBudget = backgroundBudget - cost();
            drew = true;
         }
      }

      return drew;
   }

   private static boolean draw(GpuTileBaker.Job job) {
      try {
         if (job.mesh.empty) {
            job.done(new int[262144], job.mesh.overlays == null ? null : new byte[262144], job.mesh.lit ? null : new byte[262144], 0L, 0L);
            job.free();
         } else if (!GpuBakeCheck.failed()
            && !job.mesh.traceInstead
            && (job.voxels == null || job.voxelRows <= 512 && (job.opaqueCount + job.waterCount) * 4L <= 2097152L)) {
            if (GpuTileBaker.Backend.INSTANCE.draw(job)) {
               return true;
            }

            job.fail();
         } else {
            job.cancel();
         }
      } catch (Throwable t) {
         Diorama.LOGGER.error("GPU bake of {} failed", job.mesh.key, t);
         job.fail();
      }

      return false;
   }

   private static double cost() {
      return Math.max(renderCost, gpuCost);
   }

   static void recorded(long nanos) {
      renderCost = renderCost * 0.9 + nanos * 0.1;
   }

   private static void log(GpuTileBaker.Timing timing) {
      long n = STAT_COUNT.incrementAndGet();
      STAT_MESH.addAndGet(timing.meshNanos());
      STAT_PACK.addAndGet(timing.packNanos());
      STAT_RECORD.addAndGet(timing.recordNanos());
      STAT_GPU.addAndGet(Math.max(0L, timing.gpuNanos()));
      STAT_LATENCY.addAndGet(timing.latencyNanos());
      if (n % 25L == 0L) {
         Diorama.LOGGER
            .info(
               "GPU baked {} tiles; average ms: mesh {}, pack {}, render thread {}, GPU {}, submit to pixels {}",
               new Object[]{n, ms(STAT_MESH, n), ms(STAT_PACK, n), ms(STAT_RECORD, n), ms(STAT_GPU, n), ms(STAT_LATENCY, n)}
            );
      }
   }

   private static String ms(AtomicLong total, long n) {
      return String.format(Locale.ROOT, "%.2f", (double)total.get() / n / 1000000.0);
   }

   private static final class Backend {
      static final BakeBackend INSTANCE = new GlBakeBackend();
   }

   static final class Job {
      final GpuMesher.Mesh mesh;
      final Consumer<GpuTileBaker.Result> callback;
      MapStyle look;
      final long submitted = System.nanoTime();
      long drawn;
      long drawnFrame;
      final AtomicBoolean completed = new AtomicBoolean();
      final long packNanos;
      final int opaqueCount;
      final int tintedCount;
      final int waterCount;
      final int casterCount;
      final int voxelRows;
      @Nullable ByteBuffer faces;
      @Nullable ByteBuffer casters;
      @Nullable ByteBuffer uniforms;
      @Nullable ByteBuffer voxels;

      Job(GpuMesher.Mesh mesh, Consumer<GpuTileBaker.Result> callback) {
         this.mesh = mesh;
         this.callback = callback;
         long start = System.nanoTime();
         this.opaqueCount = mesh.opaque.count;
         this.tintedCount = mesh.tinted.count;
         this.waterCount = mesh.water.count;
         this.casterCount = mesh.casters.count;
         int originX = mesh.originX;
         int originZ = mesh.originZ;
         boolean sky = mesh.voxels != null;
         ByteBuffer faces = MemoryUtil.memAlloc(Math.max(1, this.faceCount() * 48)).order(ByteOrder.LITTLE_ENDIAN);
         putFaces(faces, mesh.opaque, originX, originZ, sky ? 0 : -1);
         putFaces(faces, mesh.tinted, originX, originZ, -1);
         putFaces(faces, mesh.water, originX, originZ, sky ? this.opaqueCount : -1);
         faces.flip();
         this.faces = faces;
         ByteBuffer casters = MemoryUtil.memAlloc(Math.max(1, this.casterCount * 16)).order(ByteOrder.LITTLE_ENDIAN);
         GpuMesher.Quads d = mesh.casters;

         for (int q = 0; q < this.casterCount; q++) {
            int i = q * 14;
            casters.putShort((short)(d.get(i) - originX)).putShort((short)d.get(i + 1)).putShort((short)(d.get(i + 2) - originZ)).putShort((short)d.get(i + 3));
            casters.putInt(d.get(i + 8)).putInt(d.get(i + 9));
         }

         casters.flip();
         this.casters = casters;
         this.look = MapStyle.get();
         this.uniforms = uniforms(mesh, originX, originZ, this.look);
         if (sky) {
            this.voxelRows = (mesh.voxelCount + 4096 - 1) / 4096;
            ByteBuffer voxels = MemoryUtil.memCalloc(Math.max(1, this.voxelRows) * 4096 * 4).order(ByteOrder.LITTLE_ENDIAN);
            voxels.asIntBuffer().put(mesh.voxels, 0, mesh.voxelCount);
            this.voxels = voxels;
         } else {
            this.voxelRows = 0;
         }

         mesh.freeFaces();
         this.packNanos = System.nanoTime() - start;
      }

      int faceCount() {
         return this.opaqueCount + this.tintedCount + this.waterCount;
      }

      private static void putFaces(ByteBuffer out, GpuMesher.Quads quads, int originX, int originZ, int firstSky) {
         GpuMesher.Quads d = quads;

         for (int q = 0; q < quads.count; q++) {
            int i = q * 14;
            out.putShort((short)(d.get(i) - originX))
               .putShort((short)d.get(i + 1))
               .putShort((short)(d.get(i + 2) - originZ))
               .putShort((short)(d.get(i + 3) | d.get(i + 7) << 4));
            int color = d.get(i + 4);
            out.put((byte)(color >> 16)).put((byte)(color >> 8)).put((byte)color).put((byte)(color >>> 24));
            int ao = d.get(i + 5);
            out.put((byte)(ao >>> 24)).put((byte)(ao >> 16)).put((byte)(ao >> 8)).put((byte)ao);
            int light = d.get(i + 6);
            out.put((byte)(light >>> 24)).put((byte)(light >> 16)).put((byte)(light >> 8)).put((byte)light);
            out.putInt(firstSky < 0 ? -1 : firstSky + q);
            out.putInt(d.get(i + 8)).putInt(d.get(i + 9));
            out.putInt(d.get(i + 10)).putInt(d.get(i + 11)).putInt(d.get(i + 12)).putInt(d.get(i + 13));
         }
      }

      private static ByteBuffer uniforms(GpuMesher.Mesh mesh, int originX, int originZ, MapStyle style) {
         double sin = mesh.sin;
         double cos = mesh.cos;
         double minV = mesh.key.minV();
         double maxV = minV + 128.0;
         int minY = mesh.minY;
         double tNear = -(mesh.topY + 2 + maxV * cos) / sin;
         double tFar = -(minY - 2 + minV * cos) / sin;
         double elevation = Math.toRadians(style.sunElevation());
         double lx = Math.cos(elevation) * -0.878;
         double ly = Math.sin(elevation);
         double lz = Math.cos(elevation) * 0.479;
         double ll = Math.sqrt(lx * lx + ly * ly + lz * lz);
         lx /= ll;
         ly /= ll;
         lz /= ll;
         double ux = -lz;
         double uz = lx;
         double ul = Math.sqrt(ux * ux + uz * uz);
         ux /= ul;
         uz /= ul;
         double vx = -uz * ly;
         double vy = uz * lx - ux * lz;
         double vz = ux * ly;
         double[] uRange = new double[]{Double.MAX_VALUE, -Double.MAX_VALUE};
         double[] vRange = new double[]{Double.MAX_VALUE, -Double.MAX_VALUE};
         double[] dRange = new double[]{Double.MAX_VALUE, -Double.MAX_VALUE};
         int x0 = mesh.casterX0 - originX;
         int x1 = mesh.casterX1 - originX;
         int z0 = mesh.casterZ0 - originZ;
         int z1 = mesh.casterZ1 - originZ;
         int y0 = mesh.casterY0 - 1;
         int y1 = Math.max(mesh.casterY1, mesh.topY) + 1;

         for (int c = 0; c < 8; c++) {
            double px = (c & 1) == 0 ? x0 : x1;
            double py = (c & 2) == 0 ? y0 : y1;
            double pz = (c & 4) == 0 ? z0 : z1;
            extend(uRange, px * ux + pz * uz);
            extend(vRange, px * vx + py * vy + pz * vz);
            extend(dRange, px * lx + py * ly + pz * lz);
         }

         double uExtent = uRange[1] - uRange[0];
         double vExtent = vRange[1] - vRange[0];
         double texel = Math.max(uExtent, vExtent) / 2048.0;
         ByteBuffer out = MemoryUtil.memAlloc(528).order(ByteOrder.LITTLE_ENDIAN);
         vec4(out, sin, cos, originZ * sin - minV, -cos * originZ - tNear);
         vec4(out, 0.0, tFar - tNear, 0.0, 0.0);
         vec4(out, lx, ly, lz, 40.0);
         vec4(out, ux, 0.0, uz, -uRange[0]);
         vec4(out, vx, vy, vz, -vRange[0]);
         vec4(out, 2.0 / uExtent, 2.0 / vExtent, dRange[0] - 1.0, dRange[1] + 1.0);
         if (mesh.lifted) {
            vec4(out, 0.26F, 0.52F, style.skyOcclusion(), 0.0);
            vec4(out, TileBaker.LID_KEY_TINT[0], TileBaker.LID_KEY_TINT[1], TileBaker.LID_KEY_TINT[2], 0.0);
            vec4(out, TileBaker.LID_FILL_TINT[0], TileBaker.LID_FILL_TINT[1], TileBaker.LID_FILL_TINT[2], 0.0);
         } else {
            vec4(out, style.ambient(), style.sunStrength(), style.skyOcclusion(), style.heightShade());
            vec4(out, style.sunTint()[0], style.sunTint()[1], style.sunTint()[2], 0.0);
            vec4(out, style.shadowTint()[0], style.shadowTint()[1], style.shadowTint()[2], 0.0);
         }

         vec4(out, style.saturation(), style.exposure(), style.outline(), style.clearWater() ? 1.0 : 0.0);
         vec4(out, 1.5 * texel + 0.02, style.cornerOcclusion(), TileBaker.outlineDepth(mesh.depthLook), mesh.depthLook ? 1.0 : 0.0);

         for (double[] j : TileBaker.SUN_JITTER) {
            vec4(out, j[0], j[1], j[2], 0.0);
         }

         vec4(out, mesh.lit ? 1.0 : 0.0, mesh.cave ? 1.0 : 0.0, mesh.topY, mesh.lifted ? 1.0 : 0.0);
         out.putInt(mesh.voxelOrigin[0]).putInt(mesh.voxelOrigin[1]).putInt(mesh.voxelOrigin[2]).putInt(mesh.rayTop);
         out.putInt(mesh.voxelSize[0]).putInt(mesh.voxelSize[1]).putInt(mesh.voxelSize[2]).putInt(0);

         for (int r = 0; r < TileBaker.SKY_RAYS.length; r++) {
            double[] d = TileBaker.SKY_RAYS[r];
            vec4(out, d[0], d[1], d[2], TileBaker.SKY_WEIGHTS[r]);
         }

         vec4(out, mesh.roof - 30, 55.0, 0.72F, 0.7F);
         vec4(out, 1.6F, 0.025F, 0.0, 0.0);
         out.flip();
         return out;
      }

      private static void vec4(ByteBuffer out, double x, double y, double z, double w) {
         out.putFloat((float)x).putFloat((float)y).putFloat((float)z).putFloat((float)w);
      }

      private static void extend(double[] range, double value) {
         range[0] = Math.min(range[0], value);
         range[1] = Math.max(range[1], value);
      }

      void pending() {
         this.drawn = System.nanoTime();
         this.drawnFrame = GpuTileBaker.frames;
         GpuTileBaker.PENDING.add(this);
      }

      void done(int[] pixels, byte @Nullable [] info, byte @Nullable [] blockLight, long recordNanos, long gpuNanos) {
         GpuTileBaker.PENDING.remove(this);
         if (this.completed.compareAndSet(false, true)) {
            if (gpuNanos > 0L) {
               GpuTileBaker.gpuCost = GpuTileBaker.gpuCost * 0.9 + gpuNanos * 0.1;
            }

            GpuTileBaker.Timing timing = new GpuTileBaker.Timing(
               this.mesh.buildNanos,
               this.packNanos,
               recordNanos,
               gpuNanos,
               System.nanoTime() - this.submitted,
               this.faceCount(),
               this.casterCount,
               this.faceCount() * 48 + this.casterCount * 16
            );
            GpuTileBaker.log(timing);
            this.callback.accept(new GpuTileBaker.Result(pixels, info, blockLight, timing, false, this.look));
         }
      }

      void fail() {
         this.giveBack(false);
      }

      void cancel() {
         this.giveBack(true);
      }

      private void giveBack(boolean declined) {
         GpuTileBaker.PENDING.remove(this);
         if (this.completed.compareAndSet(false, true)) {
            this.free();
            this.callback
               .accept(
                  new GpuTileBaker.Result(
                     null,
                     null,
                     null,
                     new GpuTileBaker.Timing(this.mesh.buildNanos, this.packNanos, -1L, -1L, -1L, this.faceCount(), this.casterCount, 0),
                     declined,
                     this.look
                  )
               );
         }
      }

      void free() {
         if (this.faces != null) {
            MemoryUtil.memFree(this.faces);
            this.faces = null;
         }

         if (this.casters != null) {
            MemoryUtil.memFree(this.casters);
            this.casters = null;
         }

         if (this.uniforms != null) {
            MemoryUtil.memFree(this.uniforms);
            this.uniforms = null;
         }

         if (this.voxels != null) {
            MemoryUtil.memFree(this.voxels);
            this.voxels = null;
         }
      }
   }

   public record Result(
      int @Nullable [] pixels, byte @Nullable [] info, byte @Nullable [] blockLight, GpuTileBaker.Timing timing, boolean declined, MapStyle look
   ) {
   }

   public record Timing(long meshNanos, long packNanos, long recordNanos, long gpuNanos, long latencyNanos, int faces, int casters, int bytes) {
      public long frameNanos() {
         return Math.max(this.recordNanos, this.gpuNanos);
      }
   }
}
