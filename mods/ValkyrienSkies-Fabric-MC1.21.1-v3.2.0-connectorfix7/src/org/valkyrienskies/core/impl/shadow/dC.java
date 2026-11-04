package org.valkyrienskies.core.impl.shadow;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Set;

public final class dC implements dy {
   public static final Set<String> a = Collections.unmodifiableSet(dB.a);

   @Override
   public final InputStream a(URI var1) {
      URLConnection var2 = var1.toURL().openConnection();
      return this.a(var2);
   }

   private InputStream a(URLConnection var1) {
      int var3 = 0;

      boolean var2;
      InputStream var4;
      do {
         if (var1 instanceof HttpURLConnection) {
            ((HttpURLConnection)var1).setInstanceFollowRedirects(false);
         }

         var4 = var1.getInputStream();
         var2 = false;
         HttpURLConnection var5;
         int var6;
         if (var1 instanceof HttpURLConnection
            && (var6 = (var5 = (HttpURLConnection)var1).getResponseCode()) >= 300
            && var6 <= 307
            && var6 != 306
            && var6 != 304) {
            URL var7 = var5.getURL();
            String var8 = var5.getHeaderField("Location");
            URL var9 = null;
            if (var8 != null) {
               var9 = new URL(var7, var8);
            }

            var5.disconnect();
            if (var9 == null || !var9.getProtocol().equals("http") && !var9.getProtocol().equals("https") || var3 >= 5) {
               throw new SecurityException("illegal URL redirect");
            }

            var2 = true;
            var1 = var9.openConnection();
            var3++;
         }
      } while (var2);

      return var4;
   }
}
