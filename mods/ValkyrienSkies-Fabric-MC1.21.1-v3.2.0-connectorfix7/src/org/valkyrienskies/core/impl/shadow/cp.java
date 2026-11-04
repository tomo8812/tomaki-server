package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class cp extends bT {
   private static final Pattern i = Pattern.compile("^[0-9]+$");
   private Map<String, cv> j;
   private final co k;
   private boolean l = false;
   private final URI m;
   private cv n = null;
   private cv o = null;
   dO g = null;
   private static Comparator<String> p = (var0, var1) -> {
      if (var0.equals(var1)) {
         return 0;
      } else if (var0.endsWith("/properties")) {
         return -1;
      } else {
         return var1.endsWith("/properties") ? 1 : var0.compareTo(var1);
      }
   };

   public cp(df var1, URI var2, JsonNode var3) {
      this(var1, "#", var2, var3, null);
   }

   public cp(df var1, String var2, URI var3, JsonNode var4, cp var5) {
      this(var1, var2, var3, var4, var5, false);
   }

   public cp(df var1, URI var2, JsonNode var3, boolean var4) {
      this(var1, "#", var2, var3, null, var4);
   }

   private cp(df var1, String var2, URI var3, JsonNode var4, cp var5, boolean var6) {
      super(var2, var4, var5, null, var6, var1.d() != null && var1.d().b(), var1.d() != null ? var1.d().c() : null);
      super.d = var1;
      this.k = var1.f();
      this.m = this.a(var3, var4);
      if (var1.d() != null) {
         this.g = new dI(super.d.d().i());
         if (var1.d().m()) {
            ObjectNode var7 = (ObjectNode)var4.get("discriminator");
            if (var7 != null && null != var1.e()) {
               var1.e().a(var2, var7);
            }
         }
      }
   }

   df h() {
      return super.d;
   }

   private URI a(URI var1, JsonNode var2) {
      String var4;
      if ((var4 = super.d.a(var2)) == null) {
         return var1;
      }

      if (this.a(var1, var4)) {
         return null;
      }

      try {
         return super.d.a().a(var1, var4);
      } catch (IllegalArgumentException var3) {
         throw new cq(dg.a(dj.ID.a(), dj.ID, var4, super.a, var1 == null ? "null" : var1.toString()));
      }
   }

   private boolean a(URI var1, String var2) {
      return var2.startsWith("#") && var1 == null;
   }

   public URI i() {
      return this.m;
   }

   public JsonNode b(String var1) {
      cp var2;
      JsonNode var3 = (var2 = this.j()).c();
      if (var1.startsWith("#/")) {
         String[] var4;
         int var5 = (var4 = var1.substring(2).split("/")).length;

         for (int var6 = 0; var6 < var5; var6++) {
            String var7 = var4[var6];

            try {
               var7 = URLDecoder.decode(var7, "utf-8");
            } catch (UnsupportedEncodingException var9) {
            }

            if (i.matcher(var7).matches()) {
               var3 = var3.get(Integer.parseInt(var7));
            } else {
               var3 = var3.get(var7);
            }

            if (var3 == null) {
               var3 = this.a(var1, var2);
            }

            if (var3 == null) {
               break;
            }
         }
      } else if (var1.startsWith("#") && var1.length() > 1 && (var3 = this.k.a(var1, var3)) == null) {
         var3 = this.a(var1, var2);
      }

      return var3;
   }

   public cp j() {
      cp var1 = this;
      if (this.d() != null) {
         var1 = this.d().j();
      }

      return var1;
   }

   private JsonNode a(String var1, cp var2) {
      cp var3;
      return (var3 = var2.a(super.d)) != null ? var3.b(var1) : null;
   }

   private Map<String, cv> c(JsonNode var1) {
      TreeMap var2 = new TreeMap<>(p);
      if (var1.isBoolean()) {
         if (var1.booleanValue()) {
            String var3 = this.a(var1, "true");
            cv var4 = super.d.a(this.b(), "true", var1, this, var3);
            var2.put(this.b() + "/true", var4);
         } else {
            String var7 = this.a(var1, "false");
            cv var9 = super.d.a(this.b(), "false", var1, this, var7);
            var2.put(this.b() + "/false", var9);
         }
      } else {
         Iterator var8 = var1.fieldNames();

         while (var8.hasNext()) {
            String var10;
            JsonNode var5 = (var10 = (String)var8.next()).equals("if") ? var1 : var1.get(var10);
            String var6 = this.a(var1, var10);
            cv var11 = super.d.a(this.b(), var10, var5, this, var6);
            if (dj.UNEVALUATED_PROPERTIES.a().equals(var10)) {
               this.o = var11;
            }

            if (var11 != null && !dj.UNEVALUATED_PROPERTIES.a().equals(var10)) {
               var2.put(this.b() + "/" + var10, var11);
               if (var10.equals("required")) {
                  this.n = var11;
               }
            }
         }
      }

      return var2;
   }

   private String a(JsonNode var1, String var2) {
      cp var3 = this.d();
      JsonNode var4;
      return (var4 = this.a(var1, var3)) != null && var4.get(var2) != null ? var4.get(var2).asText() : null;
   }

   private JsonNode a(JsonNode var1, cp var2) {
      if (var2 == null) {
         var1 = var1;
      } else {
         var1 = var2.b;
      }

      return var1.get("message");
   }

   @Override
   public Set<dg> a(JsonNode var1) {
      try {
         return this.a(var1, var1, "$");
      } finally {
         if (super.d.d().o()) {
            bV.a().c();
         }
      }
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      cT var4 = super.d.d();
      LinkedHashSet var5 = new LinkedHashSet();
      this.k();
      this.a(false, true);

      for (cv var7 : this.n().values()) {
         var5.addAll(var7.a(var1, var2, var3));
      }

      var5.addAll(this.a(var1, var2, var3, true, true));
      if (var4 != null && var4.m()) {
         ObjectNode var13 = (ObjectNode)super.b.get("discriminator");
         if (var13 != null) {
            df.a var14 = super.d.e();
            if (var14 != null) {
               var2 = var14.a(super.a);
               if (var2 == null) {
                  var14.a(super.a, var13);
                  var2 = var13;
               } else {
                  var2 = var2;
               }

               var3 = var2.get("propertyName").asText();
               JsonNode var8;
               String var9 = (var8 = var1.get(var3)) == null ? null : var8.asText();
               a(var14, var2, var9, this);
            }
         }
      }

      return var5;
   }

   public dh b(JsonNode var1) {
      return this.b(var1, var1, "$");
   }

   private dh b(JsonNode var1, JsonNode var2, String var3) {
      try {
         cT var4 = super.d.d();
         bV var5 = this.k();
         this.a(false, true);
         Set var8 = this.a(var1, var2, var3);
         if (var4.n()) {
            var5.d();
         }

         return new dh(var8, var5);
      } finally {
         if (super.d.d().o()) {
            bV.a().c();
         }
      }
   }

   public dh a(JsonNode var1, boolean var2) {
      try {
         cT var3 = super.d.d();
         bV var4 = this.k();
         this.a(true, var2);
         Set var5 = this.a(var1, var1, "$", var2);
         if (var3.n()) {
            var4.d();
         }

         var5.addAll(this.a(var1, var1, "$", var2, false));
         return new dh(var5, var4);
      } finally {
         if (super.d.d().o()) {
            bV.a().c();
         }
      }
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3, boolean var4) {
      LinkedHashSet var5 = new LinkedHashSet();
      Iterator var6 = this.n().entrySet().iterator();

      while (var6.hasNext()) {
         Entry var7;
         dL var8 = (dL)(var7 = (Entry)var6.next()).getValue();
         String var11 = (String)var7.getKey();

         try {
            if (this.g.b(var11, var1, var2, var3, super.a, super.b, super.c, super.d, super.d.c())) {
               var5.addAll(var8.a(var1, var2, var3, var4));
            }
         } finally {
            this.g.a(var11, var1, var2, var3, super.a, super.b, super.c, super.d, super.d.c(), var5);
         }
      }

      if (var4) {
         var5.addAll(this.a(var1, var2, var3, true, true));
      }

      return var5;
   }

   private void a(boolean var1, boolean var2) {
      if (bV.a().a("com.networknt.schema.ValidatorState") == null) {
         di var3;
         (var3 = new di()).c(var1);
         var3.d(var2);
         bV.a().a("com.networknt.schema.ValidatorState", var3);
      }
   }

   public bV k() {
      cT var1 = super.d.d();
      bV var2;
      if ((var2 = (bV)cW.a("com.networknt.schema.CollectorKey")) == null) {
         if (var1 != null && var1.k() != null) {
            var2 = var1.k();
         } else {
            var2 = new bV();
         }

         cW.a("com.networknt.schema.CollectorKey", var2);
      }

      return var2;
   }

   @Override
   public String toString() {
      return "\"" + this.b() + "\" : " + this.c().toString();
   }

   public boolean l() {
      return this.n != null;
   }

   public cv m() {
      return this.n;
   }

   public Map<String, cv> n() {
      if (this.j == null) {
         this.j = Collections.unmodifiableMap(this.c(this.c()));
      }

      return this.j;
   }

   public void o() {
      if (!this.l) {
         this.l = true;
         Iterator var1 = this.n().values().iterator();

         while (var1.hasNext()) {
            ((cv)var1.next()).a_();
         }
      }
   }

   private Set<dg> a(JsonNode var1, JsonNode var2, String var3, boolean var4, boolean var5) {
      if (this.o == null) {
         return Collections.emptySet();
      }

      if (!var5) {
         Set var8 = new HashSet();

         try {
            if (this.g.b(this.b() + "/" + dj.UNEVALUATED_PROPERTIES.a(), var1, var2, var3, super.a, super.b, super.c, super.d, super.d.c())) {
               var8 = this.o.a(var1, var2, var3, var4);
            }
         } finally {
            this.g.a(this.b() + "/" + dj.UNEVALUATED_PROPERTIES.a(), var1, var2, var3, super.a, super.b, super.c, super.d, super.d.c(), var8);
         }

         return var8;
      } else {
         return this.o.a(var1, var2, var3, var4);
      }
   }
}
