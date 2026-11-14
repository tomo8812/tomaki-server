package dev.diorama.data;

import dev.diorama.Diorama;
import dev.diorama.bake.TileKey;
import dev.diorama.render.NightLook;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

public final class ImageExport {
   private static final int TILE = 512;
   public static final int COARSEST = 3;
   public static final int MAX_EDGE = 16384;
   public static final int DEFAULT_EDGE = 8192;
   public static final double WHOLE_MAP_MARGIN = 32.0;
   private static final double[] BYTES_PER_PIXEL = new double[]{1.1, 2.0, 2.3, 2.4};

   private ImageExport() {
   }

   public static List<Integer> levels(double u0, double v0, double u1, double v1) {
      List<Integer> levels = new ArrayList<>();

      for (int lod = 0; lod <= 3; lod++) {
         ImageExport.Area area = ImageExport.Area.ofUnits(lod, u0, v0, u1, v1);
         if (Math.max(area.width(), area.height()) <= 16384) {
            levels.add(lod);
         }
      }

      return levels;
   }

   public static int defaultLevel(double u0, double v0, double u1, double v1) {
      List<Integer> levels = levels(u0, v0, u1, v1);

      for (int lod : levels) {
         ImageExport.Area area = ImageExport.Area.ofUnits(lod, u0, v0, u1, v1);
         if (Math.max(area.width(), area.height()) <= 8192) {
            return lod;
         }
      }

      return levels.isEmpty() ? 3 : levels.get(levels.size() - 1);
   }

   public static long estimateBytes(ImageExport.Area area) {
      return Math.round(area.pixels() * BYTES_PER_PIXEL[Math.min(area.lod(), BYTES_PER_PIXEL.length - 1)]);
   }

   public static ImageExport.@Nullable Area explored(TileStore store, int lod) {
      int coarse = Math.min(11, lod + 3);
      Set<TileKey> keys = store.saved(coarse);
      long minX = Long.MAX_VALUE;
      long minY = Long.MAX_VALUE;
      long maxX = Long.MIN_VALUE;
      long maxY = Long.MIN_VALUE;

      for (TileKey key : keys) {
         int[] pixels = store.read(key);
         if (pixels != null) {
            for (int y = 0; y < 512; y++) {
               for (int x = 0; x < 512; x++) {
                  if (pixels[y * 512 + x] >>> 24 != 0) {
                     long gx = key.tileU() * 512L + x;
                     long gy = key.tileV() * 512L + y;
                     minX = Math.min(minX, gx);
                     minY = Math.min(minY, gy);
                     maxX = Math.max(maxX, gx);
                     maxY = Math.max(maxY, gy);
                  }
               }
            }
         }
      }

      if (minX > maxX) {
         return null;
      } else {
         long width = maxX - minX + 1L;
         long height = maxY - minY + 1L;
         if (width <= 2147483647L && height <= 2147483647L) {
            return new ImageExport.Area(coarse, minX, minY, (int)width, (int)height).at(lod);
         } else {
            throw new IllegalStateException("Mapped area too large");
         }
      }
   }

   public static void write(TileStore store, ImageExport.Area area, Path file, ImageExport.Finish finish, DoubleConsumer progress) throws IOException {
      write(store, area, file, finish, 0.0F, progress);
   }

   public static void write(TileStore store, ImageExport.Area area, Path file, ImageExport.Finish finish, float night, DoubleConsumer progress) throws IOException {
      write(ImageExport.Tiles.of(store), area, file, finish, night, progress);
   }

   public static void write(ImageExport.Tiles source, ImageExport.Area area, Path file, ImageExport.Finish finish, float night, DoubleConsumer progress) throws IOException {
      int lod = area.lod();
      long tileV0 = Math.floorDiv(area.y(), 512);
      long tileV1 = Math.floorDiv(area.y() + area.height() - 1L, 512);
      long tileU0 = Math.floorDiv(area.x(), 512);
      long tileU1 = Math.floorDiv(area.x() + area.width() - 1L, 512);
      int columns = (int)(tileU1 - tileU0 + 1L);
      int[][] tiles = new int[columns][];
      byte[][] lights = new byte[columns][];
      int[][] band = new int[512][];
      Path temp = file.resolveSibling(file.getFileName() + ".tmp");
      Files.createDirectories(file.toAbsolutePath().getParent());

      try (
         OutputStream stream = Files.newOutputStream(temp);
         PngWriter png = new PngWriter(stream, area.width(), area.height());
      ) {
         png.text("Software", "Diorama");

         for (long tv = tileV0; tv <= tileV1; tv++) {
            for (int c = 0; c < columns; c++) {
               TileStore.Saved saved = source.tile((int)(tileU0 + c), (int)tv, lod);
               tiles[c] = saved == null ? null : saved.pixels();
               lights[c] = saved == null ? null : saved.blockLight();
            }

            long rowStart = Math.max(area.y(), tv * 512L);
            int rows = (int)(Math.min(area.y() + area.height(), (tv + 1L) * 512L) - rowStart);
            IntStream.range(0, rows).parallel().forEach(rx -> {
               int[] row = band[rx] != null && band[rx].length == area.width() ? band[rx] : new int[area.width()];
               band[rx] = row;
               long gy = rowStart + rx;
               int ty = (int)(gy - Math.floorDiv(gy, 512) * 512L);

               for (int c = 0; c < columns; c++) {
                  long tileX0 = (tileU0 + c) * 512L;
                  int from = (int)Math.max(0L, area.x() - tileX0);
                  int to = (int)Math.min(512L, area.x() + area.width() - tileX0);
                  int at = (int)(tileX0 + from - area.x());
                  int[] tile = tiles[c];
                  if (tile == null) {
                     Arrays.fill(row, at, at + to - from, 0);
                  } else {
                     System.arraycopy(tile, ty * 512 + from, row, at, to - from);
                     if (night > 0.0F) {
                        byte[] light = lights[c];

                        for (int x = from; x < to; x++) {
                           row[at + x - from] = NightLook.light(row[at + x - from], light == null ? 0.0F : lightAt(light, x, ty), night);
                        }
                     }
                  }
               }

               finish.row(row, gy, area.x());
            });

            for (int r = 0; r < rows; r++) {
               png.row(band[r], 0);
            }

            progress.accept((double)(tv - tileV0 + 1L) / (tileV1 - tileV0 + 1L));
         }
      } catch (IOException | RuntimeException e) {
         Files.deleteIfExists(temp);
         throw e;
      }

      try {
         Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException | RuntimeException e) {
         Files.deleteIfExists(temp);
         throw e;
      }
   }

   private static float lightAt(byte[] light, int x, int y) {
      int edge = 128;
      float fx = (x + 0.5F) / 4.0F - 0.5F;
      float fy = (y + 0.5F) / 4.0F - 0.5F;
      int x0 = (int)Math.floor(fx);
      int y0 = (int)Math.floor(fy);
      float tx = fx - x0;
      float ty = fy - y0;
      int xa = Math.max(0, x0);
      int xb = Math.min(edge - 1, x0 + 1);
      int ya = Math.max(0, y0);
      int yb = Math.min(edge - 1, y0 + 1);
      float top = (light[ya * edge + xa] & 0xFF) * (1.0F - tx) + (light[ya * edge + xb] & 0xFF) * tx;
      float bottom = (light[yb * edge + xa] & 0xFF) * (1.0F - tx) + (light[yb * edge + xb] & 0xFF) * tx;
      return (top * (1.0F - ty) + bottom * ty) / 255.0F;
   }

   public static void deleteUnfinished(Path folder, String prefix) {
      if (Files.isDirectory(folder)) {
         try (Stream<Path> files = Files.list(folder)) {
            for (Path file : files.toList()) {
               String name = file.getFileName().toString();
               if (name.startsWith(prefix) && name.endsWith(".png.tmp")) {
                  Files.deleteIfExists(file);
               }
            }
         } catch (IOException e) {
            Diorama.LOGGER.warn("Couldn't clear unfinished exports in {}", folder, e);
         }
      }
   }

   static int over(int abgr, int argb) {
      int a = abgr >>> 24;
      if (a == 255) {
         return abgr;
      }

      int br = argb >> 16 & 0xFF;
      int bg = argb >> 8 & 0xFF;
      int bb = argb & 0xFF;
      int r = ((abgr & 0xFF) * a + br * (255 - a)) / 255;
      int g = ((abgr >> 8 & 0xFF) * a + bg * (255 - a)) / 255;
      int b = ((abgr >> 16 & 0xFF) * a + bb * (255 - a)) / 255;
      return 0xFF000000 | b << 16 | g << 8 | r;
   }

   public record Area(int lod, long x, long y, int width, int height) {
      public ImageExport.Area at(int lod) {
         if (lod == this.lod) {
            return this;
         }

         double scale = Math.pow(2.0, this.lod - lod);
         long x0 = (long)Math.floor(this.x * scale);
         long y0 = (long)Math.floor(this.y * scale);
         long x1 = (long)Math.ceil((this.x + this.width) * scale);
         long y1 = (long)Math.ceil((this.y + this.height) * scale);
         return new ImageExport.Area(lod, x0, y0, (int)Math.max(1L, x1 - x0), (int)Math.max(1L, y1 - y0));
      }

      public static ImageExport.Area ofUnits(int lod, double u0, double v0, double u1, double v1) {
         double perUnit = 4.0 / (1 << lod);
         long x0 = (long)Math.floor(Math.min(u0, u1) * perUnit);
         long y0 = (long)Math.floor(Math.min(v0, v1) * perUnit);
         long x1 = (long)Math.ceil(Math.max(u0, u1) * perUnit);
         long y1 = (long)Math.ceil(Math.max(v0, v1) * perUnit);
         return new ImageExport.Area(lod, x0, y0, (int)Math.max(1L, x1 - x0), (int)Math.max(1L, y1 - y0));
      }

      public double unitsPerPixel() {
         return (1 << this.lod) / 4.0;
      }

      public double[] units() {
         double k = this.unitsPerPixel();
         return new double[]{this.x * k, this.y * k, (this.x + this.width) * k, (this.y + this.height) * k};
      }

      public long pixels() {
         return (long)this.width * this.height;
      }
   }

   public interface Finish {
      ImageExport.Finish NONE = (abgr, y, x0) -> {};

      void row(int[] var1, long var2, long var4);

      default ImageExport.Finish then(ImageExport.Finish next) {
         return (abgr, y, x0) -> {
            this.row(abgr, y, x0);
            next.row(abgr, y, x0);
         };
      }

      static ImageExport.Finish over(int argb) {
         return (abgr, y, x0) -> {
            for (int x = 0; x < abgr.length; x++) {
               abgr[x] = ImageExport.over(abgr[x], argb);
            }
         };
      }
   }

   public interface Tiles {
      TileStore.@Nullable Saved tile(int var1, int var2, int var3);

      default void prepare(ImageExport.Area area, DoubleConsumer progress) {
      }

      default double prepareShare() {
         return 0.0;
      }

      static ImageExport.Tiles of(TileStore store) {
         return (tileU, tileV, lod) -> store.readSaved(new TileKey(Integer.MIN_VALUE, tileU, tileV, lod));
      }
   }
}
