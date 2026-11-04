package org.valkyrienskies.core.impl.shadow;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

public class dA implements dy {
   private final Map<String, dy> a;

   public dA(Map<String, dy> var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("URIFetcher map must not be null");
      }

      this.a = var1;
   }

   public Map<String, dy> a() {
      return this.a;
   }

   @Override
   public InputStream a(URI var1) {
      dy var2;
      if ((var2 = this.a.get(var1.getScheme())) == null) {
         throw new IllegalArgumentException(String.format("Unsupported URI scheme encountered: %s", var1.getScheme()));
      } else {
         return var2.a(var1);
      }
   }
}
