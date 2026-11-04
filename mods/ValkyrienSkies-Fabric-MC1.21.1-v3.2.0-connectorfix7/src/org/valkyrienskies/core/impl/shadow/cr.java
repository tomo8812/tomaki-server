package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cr {
   private static final Logger a = LoggerFactory.getLogger(cr.class);
   private final ObjectMapper b;
   private final String c;
   private final dz d;
   private final dA e;
   private final dD f;
   private final Map<String, co> g;
   private final Map<String, String> h;
   private final ConcurrentMap<URI, cp> i = new ConcurrentHashMap<>();
   private final boolean j;
   private final boolean k;

   private cr(ObjectMapper var1, String var2, dz var3, dA var4, dD var5, Map<String, co> var6, Map<String, String> var7, boolean var8, boolean var9) {
      if (var1 == null) {
         throw new IllegalArgumentException("ObjectMapper must not be null");
      }

      if (var2 == null || var2.trim().isEmpty()) {
         throw new IllegalArgumentException("defaultMetaSchemaURI must not be null or empty");
      }

      if (var3 == null) {
         throw new IllegalArgumentException("URIFactory must not be null");
      }

      if (var4 == null) {
         throw new IllegalArgumentException("URIFetcher must not be null");
      }

      if (var6 == null || var6.isEmpty()) {
         throw new IllegalArgumentException("Json Meta Schemas must not be null or empty");
      }

      if (var6.get(var2) == null) {
         throw new IllegalArgumentException("Meta Schema for default Meta Schema URI must be provided");
      }

      if (var7 == null) {
         throw new IllegalArgumentException("URL Mappings must not be null");
      }

      this.b = var1;
      this.c = var2;
      this.d = var3;
      this.e = var4;
      this.f = var5;
      this.g = var6;
      this.h = var7;
      this.j = var8;
      this.k = var9;
   }

   static cr.a a() {
      return new cr.a();
   }

   @Deprecated
   public static cr b() {
      return a(cU.a.V4);
   }

   public static cr a(cU.a var0) {
      co var1 = b(var0).a();
      return a().a(var1.e()).a(var1).a();
   }

   public static ct b(cU.a var0) {
      ct var1 = null;
      switch (var0) {
         case V202012:
            var1 = new dl();
            break;
         case V201909:
            var1 = new dk();
            break;
         case V7:
            var1 = new do();
            break;
         case V6:
            var1 = new dn();
            break;
         case V4:
            var1 = new dm();
      }

      return var1;
   }

   public static cr.a a(cr var0) {
      cr.a var1 = a().a(var0.g.values()).a(var0.c).a(var0.b).a(var0.h);

      for (Entry var3 : var0.d.a().entrySet()) {
         var1 = var1.a((dx)var3.getValue(), (String)var3.getKey());
      }

      for (Entry var5 : var0.e.a().entrySet()) {
         var1 = var1.a((dy)var5.getValue(), (String)var5.getKey());
      }

      return var1;
   }

   protected cp a(URI var1, JsonNode var2, cT var3) {
      df var4;
      (var4 = this.a(var2)).a(var3);
      return new cp(var4, var1, var2);
   }

   protected df a(JsonNode var1) {
      co var2 = this.c(var1);
      return new df(this.d, this.f, var2, this, null);
   }

   private co c(JsonNode var1) {
      if ((var1 = var1.get("$schema")) != null && !var1.isNull() && !var1.isTextual()) {
         throw new cq("Unknown MetaSchema: " + var1.toString());
      } else {
         String var4 = var1 != null && !var1.isNull() ? a(var1.textValue(), this.j, this.k) : this.c;
         co var2;
         if ((var2 = this.g.get(var4)) == null) {
            throw new cq("Unknown MetaSchema: " + var4);
         } else {
            return var2;
         }
      }
   }

   public dx c() {
      return this.d;
   }

   public cp a(String var1, cT var2) {
      try {
         JsonNode var4 = this.b.readTree(var1);
         return this.a(null, var4, var2);
      } catch (IOException var3) {
         a.error("Failed to load json schema!", var3);
         throw new cq(var3);
      }
   }

   public cp a(String var1) {
      return this.a(var1, null);
   }

   public cp a(InputStream var1, cT var2) {
      try {
         JsonNode var4 = this.b.readTree(var1);
         return this.a(null, var4, var2);
      } catch (IOException var3) {
         a.error("Failed to load json schema!", var3);
         throw new cq(var3);
      }
   }

   public cp a(InputStream var1) {
      return this.a(var1, null);
   }

   public cp a(URI var1, cT var2) {
      try {
         InputStream var3 = null;
         Map var4;
         (var4 = var2 != null ? var2.d() : new HashMap()).putAll(this.h);

         try {
            var15 = this.d.a(var4.get(var1.toString()) != null ? (String)var4.get(var1.toString()) : var1.toString());
         } catch (IllegalArgumentException var10) {
            a.error("Failed to create URI.", var10);
            throw new cq(var10);
         }

         if (this.i.containsKey(var15)) {
            cp var16;
            (var16 = this.i.get(var15)).h().a(var2);
            return var16;
         }

         try {
            var3 = this.e.a(var15);
            JsonNode var5 = this.b.readTree(var3);
            co var6 = this.c(var5);
            cp var13;
            if (this.a(var6, var5, var1)) {
               var13 = new cp(new df(this.d, this.f, var6, this, var2), var15, var5, true);
            } else {
               df var14;
               (var14 = this.a(var5)).a(var2);
               var13 = new cp(var14, var15, var5);
            }

            this.i.put(var15, var13);
            return var13;
         } finally {
            if (var3 != null) {
               var3.close();
            }
         }
      } catch (IOException var12) {
         a.error("Failed to load json schema!", var12);
         throw new cq(var12);
      }
   }

   public cp a(URI var1) {
      return this.a(var1, new cT());
   }

   public cp b(URI var1, JsonNode var2, cT var3) {
      return this.a(var1, var2, var3);
   }

   public cp a(JsonNode var1, cT var2) {
      return this.a(null, var1, var2);
   }

   public cp a(URI var1, JsonNode var2) {
      return this.a(var1, var2, null);
   }

   public cp b(JsonNode var1) {
      return this.a((URI)null, var1, (cT)null);
   }

   private boolean a(co var1, JsonNode var2, URI var3) {
      String var4;
      if ((var4 = var1.a(var2)) != null && !var4.isEmpty()) {
         boolean var5 = var4.equals(var3.toString());
         if (a.isDebugEnabled()) {
            a.debug("Matching " + var4 + " to " + var3.toString() + ": " + var5);
         }

         return var5;
      } else {
         return false;
      }
   }

   protected static String a(String var0, boolean var1, boolean var2) {
      try {
         URI var3 = new URI(var0);
         String var5 = var1 ? "https" : var3.getScheme();
         URI var6 = new URI(var5, var3.getUserInfo(), var3.getHost(), var3.getPort(), var3.getPath(), null, null);
         return !var2 && var0.endsWith("#") ? var6 + "#" : var6.toString();
      } catch (URISyntaxException var4) {
         throw new cq("Wrong MetaSchema URI: " + var0);
      }
   }

   public static class a {
      private ObjectMapper a = new ObjectMapper();
      private String b;
      private final Map<String, dx> c = new HashMap<>();
      private final Map<String, dy> d = new HashMap<>();
      private dD e;
      private final Map<String, co> f = new HashMap<>();
      private final Map<String, String> g = new HashMap<>();
      private boolean h = true;
      private boolean i = true;

      public a() {
         dB var1 = new dB();

         for (String var3 : dB.a) {
            this.c.put(var3, var1);
         }

         dC var7 = new dC();

         for (String var5 : dC.a) {
            this.d.put(var5, var7);
         }

         du var10 = new du();
         dv var6 = new dv();

         for (String var4 : du.b) {
            this.c.put(var4, var10);
            this.d.put(var4, var6);
         }
      }

      public cr.a a(ObjectMapper var1) {
         this.a = var1;
         return this;
      }

      public cr.a a(String var1) {
         this.b = var1;
         return this;
      }

      public cr.a a(dx var1, String... var2) {
         return this.a(var1, Arrays.asList(var2));
      }

      public cr.a a(dx var1, Iterable<String> var2) {
         for (String var3 : var2) {
            this.c.put(var3, var1);
         }

         return this;
      }

      public cr.a a(dy var1, String... var2) {
         return this.a(var1, Arrays.asList(var2));
      }

      public cr.a a(dy var1, Iterable<String> var2) {
         for (String var3 : var2) {
            this.d.put(var3, var1);
         }

         return this;
      }

      public cr.a a(co var1) {
         this.f.put(var1.e(), var1);
         return this;
      }

      public cr.a a(Collection<? extends co> var1) {
         for (co var2 : var1) {
            this.f.put(var2.e(), var2);
         }

         return this;
      }

      public cr.a a(Map<String, String> var1) {
         this.g.putAll(var1);
         return this;
      }

      public cr.a a(dD var1) {
         this.e = var1;
         return this;
      }

      public cr.a a(boolean var1) {
         this.h = var1;
         return this;
      }

      public cr.a b(boolean var1) {
         this.i = var1;
         return this;
      }

      public cr a() {
         return new cr(this.a == null ? new ObjectMapper() : this.a, this.b, new dz(this.c), new dA(this.d), this.e, this.f, this.g, this.h, this.i);
      }
   }
}
