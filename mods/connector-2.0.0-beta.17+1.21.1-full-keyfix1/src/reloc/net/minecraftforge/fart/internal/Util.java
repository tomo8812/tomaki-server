package reloc.net.minecraftforge.fart.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class Util {
   public static void forZip(ZipFile zip, Util.IOConsumer<ZipEntry> consumer) throws IOException {
      Enumeration<? extends ZipEntry> entries = zip.entries();

      while (entries.hasMoreElements()) {
         consumer.accept(entries.nextElement());
      }
   }

   public static byte[] toByteArray(InputStream input) throws IOException {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      copy(input, output);
      return output.toByteArray();
   }

   public static void copy(InputStream input, OutputStream output) throws IOException {
      byte[] buf = new byte[256];
      int cnt = 0;

      while ((cnt = input.read(buf, 0, buf.length)) != -1) {
         output.write(buf, 0, cnt);
      }
   }

   public static String nameToBytecode(Class<?> cls) {
      return cls == null ? null : cls.getName().replace('.', '/');
   }

   public static String nameToBytecode(String cls) {
      return cls == null ? null : cls.replace('.', '/');
   }

   @FunctionalInterface
   public interface IOConsumer<T> {
      void accept(T var1) throws IOException;
   }
}
