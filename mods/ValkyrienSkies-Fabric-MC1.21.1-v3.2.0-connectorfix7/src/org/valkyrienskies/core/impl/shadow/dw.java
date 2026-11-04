package org.valkyrienskies.core.impl.shadow;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class dw extends URLStreamHandler {
   public static final Set<String> a = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("classpath", "resource")));

   @Override
   protected URLConnection openConnection(URL pURL) {
      return new dw.a(pURL);
   }

   class a extends URLConnection {
      private Class<?> b = null;

      protected a(URL var2) {
         super(var2);
      }

      @Override
      public final void connect() {
         String var1 = this.url.getHost();

         try {
            if (var1 != null && var1.length() > 0) {
               this.b = Class.forName(var1);
            }

            this.connected = true;
         } catch (ClassNotFoundException var2) {
            throw new IOException("Class not found: " + var2.toString());
         }
      }

      @Override
      public final InputStream getInputStream() {
         if (!this.connected) {
            this.connect();
         }

         return this.a(this.url);
      }

      private InputStream a(URL var1) {
         String var4;
         if ((var4 = var1.getPath()).startsWith("/")) {
            var4 = var4.substring(1);
         }

         InputStream var2 = null;
         if (this.b != null) {
            var2 = this.b.getClassLoader().getResourceAsStream(var4);
         } else {
            ClassLoader var3;
            if ((var3 = Thread.currentThread().getContextClassLoader()) != null) {
               var2 = var3.getResourceAsStream(var4);
            }

            if (var2 == null) {
               var2 = this.getClass().getClassLoader().getResourceAsStream(var4);
            }

            if (var2 == null) {
               var2 = ClassLoader.getSystemResourceAsStream(var4);
            }
         }

         if (var2 == null) {
            throw new IOException("Resource " + var4 + " not found in classpath.");
         } else {
            return var2;
         }
      }
   }
}
