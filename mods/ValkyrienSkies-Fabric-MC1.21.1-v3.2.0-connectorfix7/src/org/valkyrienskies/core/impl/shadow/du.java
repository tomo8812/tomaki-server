package org.valkyrienskies.core.impl.shadow;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.Collections;
import java.util.Set;

public final class du implements dx {
   static final URLStreamHandler a = new dw();
   public static final Set<String> b = Collections.unmodifiableSet(dw.a);

   public static URL a(URI var0) {
      return new URL(null, var0.toString(), a);
   }

   @Override
   public final URI a(String var1) {
      try {
         return new URL(null, var1, a).toURI();
      } catch (MalformedURLException var2) {
         throw new IllegalArgumentException("Unable to create URI.", var2);
      } catch (URISyntaxException var3) {
         throw new IllegalArgumentException("Unable to create URI.", var3);
      }
   }

   @Override
   public final URI a(URI var1, String var2) {
      try {
         return new URL(a(var1), var2, a).toURI();
      } catch (MalformedURLException var3) {
         throw new IllegalArgumentException("Unable to create URI.", var3);
      } catch (URISyntaxException var4) {
         throw new IllegalArgumentException("Unable to create URI.", var4);
      }
   }
}
