package dev.diorama.web;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class WebTiles {
   static final float QUALITY = 0.85F;
   static final int BACKGROUND = 1382172;
   static final int SIZE = 512;

   private WebTiles() {
   }

   static Path file(Path dimension, int zoom, int u, int v) {
      return dimension.resolve(Integer.toString(zoom)).resolve(u + "_" + v + ".jpg");
   }

   static long write(Path file, int[] abgr) throws IOException {
      BufferedImage image = image(abgr);
      return image != null ? writeJpeg(file, image) : delete(file);
   }

   static byte @Nullable [] jpeg(int[] abgr) throws IOException {
      BufferedImage image = image(abgr);
      if (image == null) {
         return null;
      }

      ByteArrayOutputStream bytes = new ByteArrayOutputStream(65536);
      encode(bytes, image);
      return bytes.toByteArray();
   }

   private static @Nullable BufferedImage image(int[] abgr) {
      BufferedImage image = new BufferedImage(512, 512, 1);
      boolean any = false;
      int[] row = new int[512];

      for (int y = 0; y < 512; y++) {
         for (int x = 0; x < 512; x++) {
            int p = abgr[y * 512 + x];
            int a = p >>> 24;
            any |= a != 0;
            row[x] = over(p & 0xFF, p >> 8 & 0xFF, p >> 16 & 0xFF, a);
         }

         image.setRGB(0, y, 512, 1, row, 0, 512);
      }

      return any ? image : null;
   }

   private static int over(int r, int g, int b, int a) {
      int br = 21;
      int bg = 23;
      int bb = 28;
      return br + (r - br) * a / 255 << 16 | bg + (g - bg) * a / 255 << 8 | bb + (b - bb) * a / 255;
   }

   static long shrink(Path out, Path @Nullable [] children) throws IOException {
      BufferedImage image = null;
      int half = 256;
      int[] row = new int[1024];
      int[] shrunk = new int[half];

      for (int i = 0; i < 4; i++) {
         Path child = children[i];
         if (child != null && Files.exists(child)) {
            BufferedImage read = ImageIO.read(child.toFile());
            if (read != null) {
               if (image == null) {
                  image = new BufferedImage(512, 512, 1);
                  int[] fill = new int[512];
                  Arrays.fill(fill, 1382172);

                  for (int y = 0; y < 512; y++) {
                     image.setRGB(0, y, 512, 1, fill, 0, 512);
                  }
               }

               int ox = (i & 1) * half;
               int oy = (i >> 1) * half;

               for (int y = 0; y < half; y++) {
                  read.getRGB(0, 2 * y, 512, 2, row, 0, 512);

                  for (int x = 0; x < half; x++) {
                     int a = row[2 * x];
                     int b = row[2 * x + 1];
                     int c = row[512 + 2 * x];
                     int d = row[512 + 2 * x + 1];
                     int r = (a >> 16 & 0xFF) + (b >> 16 & 0xFF) + (c >> 16 & 0xFF) + (d >> 16 & 0xFF) + 2 >> 2;
                     int g = (a >> 8 & 0xFF) + (b >> 8 & 0xFF) + (c >> 8 & 0xFF) + (d >> 8 & 0xFF) + 2 >> 2;
                     int bl = (a & 0xFF) + (b & 0xFF) + (c & 0xFF) + (d & 0xFF) + 2 >> 2;
                     shrunk[x] = r << 16 | g << 8 | bl;
                  }

                  image.setRGB(ox, oy + y, half, 1, shrunk, 0, half);
               }
            }
         }
      }

      return image == null ? delete(out) : writeJpeg(out, image);
   }

   private static long delete(Path file) throws IOException {
      Files.deleteIfExists(file);
      return 0L;
   }

   private static long writeJpeg(Path file, BufferedImage image) throws IOException {
      Files.createDirectories(file.getParent());
      Path part = file.resolveSibling(file.getFileName() + ".part");

      try (OutputStream raw = Files.newOutputStream(part)) {
         encode(raw, image);
      }

      long size = Files.size(part);
      Files.move(part, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      return size;
   }

   private static void encode(OutputStream raw, BufferedImage image) throws IOException {
      Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
      if (!writers.hasNext()) {
         throw new IOException("this Java has no JPEG writer");
      }

      ImageWriter writer = writers.next();

      try (ImageOutputStream out = ImageIO.createImageOutputStream(raw)) {
         writer.setOutput(out);
         ImageWriteParam param = writer.getDefaultWriteParam();
         param.setCompressionMode(2);
         param.setCompressionQuality(0.85F);
         IIOMetadata metadata = writer.getDefaultImageMetadata(ImageTypeSpecifier.createFromRenderedImage(image), param);
         Node tree = metadata.getAsTree("javax_imageio_jpeg_image_1.0");
         NodeList components = ((Element)tree).getElementsByTagName("componentSpec");

         for (int i = 0; i < components.getLength(); i++) {
            Element component = (Element)components.item(i);
            component.setAttribute("HsamplingFactor", "1");
            component.setAttribute("VsamplingFactor", "1");
         }

         metadata.setFromTree("javax_imageio_jpeg_image_1.0", tree);
         writer.write(null, new IIOImage(image, null, metadata), param);
      } finally {
         writer.dispose();
      }
   }
}
