package org.valkyrienskies.core.impl.shadow;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class IQ {
   private static boolean d = System.getProperty("os.name").contains("Windows");
   private static boolean e = System.getProperty("os.name").contains("Linux");
   private static boolean f = System.getProperty("os.name").contains("Mac");
   public static boolean a = false;
   public static boolean b = false;
   private static boolean g = System.getProperty("os.arch").startsWith("arm") || System.getProperty("os.arch").equals("aarch64");
   public static boolean c = System.getProperty("os.arch").equals("amd64")
      || System.getProperty("os.arch").equals("x86_64")
      || System.getProperty("os.arch").equals("aarch64");
   private static final HashSet<String> h;
   private String i;

   public IQ() {
   }

   private IQ(String var1) {
      this.i = var1;
   }

   private static String a(InputStream var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("input cannot be null.");
      }

      CRC32 var1 = new CRC32();
      byte[] var2 = new byte[4096];

      try {
         int var3;
         try {
            while ((var3 = var0.read(var2)) != -1) {
               var1.update(var2, 0, var3);
            }
         } catch (Exception var6) {
         }
      } finally {
         a((Closeable)var0);
      }

      return Long.toString(var1.getValue(), 16);
   }

   public static String a(String var0) {
      if (d) {
         return var0 + (c ? "64.dll" : ".dll");
      } else if (e) {
         return "lib" + var0 + (g ? "arm" : "") + (c ? "64.so" : ".so");
      } else {
         return f ? "lib" + var0 + (g ? "arm" : "") + (c ? "64.dylib" : ".dylib") : var0;
      }
   }

   public final void b(String var1) {
      if (!a) {
         synchronized (IQ.class) {
            if (!e(var1)) {
               String var3 = a(var1);
               System.out.println("KrunchPhysicsAPISharedLibraryLoader loading library " + var1);

               try {
                  if (b) {
                     System.loadLibrary(var3);
                  } else {
                     this.c(var3);
                  }

                  d(var1);
               } catch (Throwable var4) {
                  throw new RuntimeException(
                     "Couldn't load shared library '" + var3 + "' for target: " + System.getProperty("os.name") + (c ? ", 64-bit" : ", 32-bit"), var4
                  );
               }
            }
         }
      }
   }

   private InputStream f(String var1) {
      if (this.i == null) {
         InputStream var5;
         if ((var5 = IQ.class.getResourceAsStream("/" + var1)) == null) {
            throw new RuntimeException("Unable to read file for extraction: " + var1);
         } else {
            return var5;
         }
      } else {
         try {
            ZipFile var2;
            ZipEntry var3;
            if ((var3 = (var2 = new ZipFile(this.i)).getEntry(var1)) == null) {
               throw new RuntimeException("Couldn't find '" + var1 + "' in JAR: " + this.i);
            } else {
               return var2.getInputStream(var3);
            }
         } catch (IOException var4) {
            throw new RuntimeException("Error reading '" + var1 + "' in JAR: " + this.i, var4);
         }
      }
   }

   private File a(String var1, String var2) {
      try {
         String var3 = a(this.f(var1));
         if (var2 == null) {
            var2 = var3;
         }

         File var6;
         if ((var6 = this.b(var2, new File(var1).getName())) == null && (var6 = this.b(UUID.randomUUID().toString(), new File(var1).getName())) == null) {
            throw new RuntimeException("Unable to find writable path to extract file. Is the user home directory writable?");
         } else {
            return this.a(var1, var3, var6);
         }
      } catch (RuntimeException var4) {
         File var5;
         if ((var5 = new File(System.getProperty("java.library.path"), var1)).exists()) {
            return var5;
         } else {
            throw var4;
         }
      }
   }

   private void a(String var1, File var2) {
      this.a(var1, a(this.f(var1)), new File(var2, new File(var1).getName()));
   }

   private File b(String var1, String var2) {
      File var3 = new File(System.getProperty("java.io.tmpdir") + "/libgdx" + System.getProperty("user.name") + "/" + var1, var2);
      if (this.a(var3)) {
         return var3;
      }

      try {
         File var4;
         if ((var4 = File.createTempFile(var1, null)).delete()) {
            var4 = new File(var4, var2);
            if (this.a(var4)) {
               return var4;
            }
         }
      } catch (IOException var5) {
      }

      File var7 = new File(System.getProperty("user.home") + "/.libgdx/" + var1, var2);
      if (this.a(var7)) {
         return var7;
      } else {
         var7 = new File(".temp/" + var1, var2);
         if (this.a(var7)) {
            return var7;
         } else {
            return System.getenv("APP_SANDBOX_CONTAINER_ID") != null ? var3 : null;
         }
      }
   }

   private boolean a(File var1) {
      File var2 = var1.getParentFile();
      if (var1.exists()) {
         if (!var1.canWrite() || !b(var1)) {
            return false;
         }

         var1 = new File(var2, UUID.randomUUID().toString());
      } else {
         var2.mkdirs();
         if (!var2.isDirectory()) {
            return false;
         }

         var1 = var1;
      }

      try {
         new FileOutputStream(var1).close();
         if (b(var1)) {
            return true;
         }
      } catch (Throwable var5) {
         return false;
      } finally {
         var1.delete();
      }

      return false;
   }

   private static boolean b(File var0) {
      try {
         Method var1;
         if ((Boolean)(var1 = File.class.getMethod("canExecute")).invoke(var0)) {
            return true;
         }

         File.class.getMethod("setExecutable", boolean.class, boolean.class).invoke(var0, Boolean.TRUE, Boolean.FALSE);
         return (Boolean)var1.invoke(var0);
      } catch (Exception var3) {
         return false;
      }
   }

   private File a(String var1, String var2, File var3) {
      String var4 = null;
      if (var3.exists()) {
         try {
            var4 = a(new FileInputStream(var3));
         } catch (FileNotFoundException var10) {
         }
      }

      if (var4 == null || !var4.equals(var2)) {
         InputStream var13 = null;
         FileOutputStream var14 = null;

         try {
            var13 = this.f(var1);
            var3.getParentFile().mkdirs();
            var14 = new FileOutputStream(var3);
            byte[] var5 = new byte[4096];

            int var6;
            while ((var6 = var13.read(var5)) != -1) {
               var14.write(var5, 0, var6);
            }
         } catch (IOException var11) {
            throw new RuntimeException("Error extracting file: " + var1 + "\nTo: " + var3.getAbsolutePath(), var11);
         } finally {
            a((Closeable)var13);
            a(var14);
         }
      }

      return var3;
   }

   public void c(String var1) {
      String var2 = a(this.f(var1));
      String var3 = new File(var1).getName();
      File var4 = new File(System.getProperty("java.io.tmpdir") + "/libgdx" + System.getProperty("user.name") + "/" + var2, var3);
      Throwable var5;
      if ((var5 = this.b(var1, var2, var4)) != null) {
         try {
            if ((var4 = File.createTempFile(var2, null)).delete() && this.b(var1, var2, var4) == null) {
               return;
            }
         } catch (Throwable var6) {
         }

         var4 = new File(System.getProperty("user.home") + "/.libgdx/" + var2, var3);
         if (this.b(var1, var2, var4) != null) {
            var4 = new File(".temp/" + var2, var3);
            if (this.b(var1, var2, var4) != null) {
               if ((var4 = new File(System.getProperty("java.library.path"), var1)).exists()) {
                  System.load(var4.getAbsolutePath());
               } else {
                  throw new RuntimeException(var5);
               }
            }
         }
      }
   }

   private Throwable b(String var1, String var2, File var3) {
      try {
         System.load(this.a(var1, var2, var3).getAbsolutePath());
         return null;
      } catch (Throwable var4) {
         return var4;
      }
   }

   public static synchronized void d(String var0) {
      h.add(var0);
   }

   public static synchronized boolean e(String var0) {
      return h.contains(var0);
   }

   private static void a(Closeable var0) {
      if (var0 != null) {
         try {
            var0.close();
            return;
         } catch (Throwable var1) {
         }
      }
   }

   static {
      boolean var0 = "iOS".equals(System.getProperty("moe.platform.name"));
      String var1;
      if ((var1 = System.getProperty("java.runtime.name")) != null && var1.contains("Android Runtime")) {
         b = true;
         d = false;
         e = false;
         f = false;
         c = false;
      }

      if (var0 || !b && !d && !e && !f) {
         a = true;
         b = false;
         d = false;
         e = false;
         f = false;
         c = false;
      }

      h = new HashSet<>();
   }
}
