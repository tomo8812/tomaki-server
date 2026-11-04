package org.valkyrienskies.core.impl.shadow;

import java.net.URI;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class dz implements dx {
   private static final Pattern a = Pattern.compile("^([a-z][a-z0-9+\\.\\-\\\\]*):");
   private final Map<String, dx> b;

   public dz(Map<String, dx> var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("URIFactory map must not be null");
      }

      this.b = var1;
   }

   public Map<String, dx> a() {
      return this.b;
   }

   private String b(String var1) {
      Matcher var2;
      return (var2 = a.matcher(var1)).find() ? var2.group(1) : null;
   }

   private dx c(String var1) {
      dx var2;
      if ((var2 = this.b.get(var1)) == null) {
         throw new IllegalArgumentException(String.format("Unsupported URI scheme encountered: %s", var1));
      } else {
         return var2;
      }
   }

   @Override
   public URI a(String var1) {
      String var2;
      if ((var2 = this.b(var1)) == null) {
         throw new IllegalArgumentException(String.format("Couldn't find URI scheme: %s", var1));
      } else {
         return this.c(var2).a(var1);
      }
   }

   @Override
   public URI a(URI var1, String var2) {
      if (var1 == null) {
         return this.a(var2);
      } else {
         String var3;
         if ((var3 = this.b(var2)) == null) {
            var3 = var1.getScheme();
            return this.c(var3).a(var1, var2);
         } else {
            return this.c(var3).a(var2);
         }
      }
   }
}
