package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public abstract class bT implements cv {
   protected String a;
   protected JsonNode b;
   protected cp c;
   private boolean g;
   private dj i;
   private ce j;
   protected df d;
   protected final boolean e;
   protected final bS f;

   public bT(String var1, JsonNode var2, cp var3, dj var4, df var5) {
      this(var1, var2, var3, var4, false, var5.d() != null && var5.d().b(), var5.d() != null ? var5.d().c() : null);
   }

   @Deprecated
   public bT(String var1, JsonNode var2, cp var3, dj var4, boolean var5, boolean var6) {
      this(var1, var2, var3, var4, false, var6, null);
   }

   public bT(String var1, JsonNode var2, cp var3, dj var4, boolean var5, boolean var6, bS var7) {
      this.j = var4;
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.i = var4;
      this.g = var5;
      this.e = var6;
      this.f = var7 != null ? var7 : bS.a;
   }

   public String b() {
      return this.a;
   }

   public JsonNode c() {
      return this.b;
   }

   public cp d() {
      return this.c;
   }

   protected cp a(df var1) {
      return this.g ? null : a(this.b, var1);
   }

   private static cp a(JsonNode var0, df var1) {
      JsonNode var2;
      if ((var2 = var0.get("id")) == null) {
         return null;
      }

      if (var2.equals(var0.get("$schema"))) {
         return null;
      }

      if (var2.textValue() == null) {
         return null;
      }

      try {
         var4 = var1.a().a(var2.textValue());
      } catch (IllegalArgumentException var3) {
         return null;
      }

      return var1.c().a(var4, var1.d());
   }

   @Override
   public Set<dg> a(JsonNode var1) {
      return this.a(var1, var1, "$");
   }

   protected boolean a(double var1, double var3) {
      return Math.abs(var1 - var3) < 1.0E-12;
   }

   protected boolean b(double var1, double var3) {
      return var1 - var3 > 1.0E-12;
   }

   protected boolean c(double var1, double var3) {
      return var1 - var3 < -1.0E-12;
   }

   protected void a(String var1) {
      JsonNode var2;
      if ((var2 = this.d().c().get(var1)) != null && var2.isTextual() && StringUtils.isNotBlank(var1 = var2.asText())) {
         this.j = bY.a(var1);
      }
   }

   protected dg a(String var1, String... var2) {
      dg var3 = dg.a(this.e().a(), this.j, var1, this.a, var2);
      if (this.e && !this.g()) {
         throw new cq(var3);
      } else {
         return var3;
      }
   }

   protected void a(Logger var1, JsonNode var2, JsonNode var3, String var4) {
      if (var1.isDebugEnabled()) {
         var1.debug("validate( " + var2 + ", " + var3 + ", " + var4 + ")");
      }
   }

   protected dj e() {
      return this.i;
   }

   protected String f() {
      JsonNode var1;
      return (var1 = this.d().c().get("type")) != null ? var1.asText() : null;
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3, boolean var4) {
      Set var5 = new LinkedHashSet();
      if (var4) {
         var5 = this.a(var1, var2, var3);
      }

      return var5;
   }

   protected void a(Collection<cp> var1) {
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         ((cp)var3.next()).o();
      }
   }

   protected boolean g() {
      return this.c.a.equals(dj.ONE_OF.a());
   }

   protected static void a(df.a var0, ObjectNode var1, String var2, cp var3) {
      if (var2 == null) {
         var0.a();
      } else {
         JsonNode var4 = var1.get("mapping");
         if (var4 == null) {
            a(var0, var2, var3);
         } else {
            a(var0, var2, var4, var3);
            if (!var0.b() && a(var4, var3)) {
               a(var0, var2, var3);
            }
         }
      }
   }

   protected static void a(df.a var0, ObjectNode var1, cp var2, String var3) {
      JsonNode var4 = var2.b.get("discriminator");
      if (var4 != null && null != var0.a(var2.a)) {
         ObjectNode var5 = var4.get("propertyName");
         if (var5 != null) {
            throw new cq(var3 + " schema " + var2 + " attempts redefining the discriminator property");
         }

         var5 = (ObjectNode)var1.get("mapping");
         ObjectNode var6 = (ObjectNode)var4.get("mapping");
         if (var5 == null && var6 != null) {
            var1.set("mapping", var4);
         } else if (var5 != null && var6 != null) {
            Iterator var9 = var6.fields();

            while (var9.hasNext()) {
               Entry var11;
               String var7 = (String)(var11 = (Entry)var9.next()).getKey();
               JsonNode var12 = (JsonNode)var11.getValue();
               JsonNode var8 = var5.get(var7);
               if (var8 != null && var8 != var12) {
                  throw new cq(var3 + "discriminator mapping redefinition from " + var7 + "/" + var8 + " to " + var12);
               }

               if (var8 == null) {
                  var5.set(var7, var12);
               }
            }
         }
      }

      var0.a(var2.a, var1);
   }

   private static void a(df.a var0, String var1, cp var2) {
      if (var2.a.endsWith("/" + var1)) {
         var0.a();
      }
   }

   private static void a(df.a var0, String var1, JsonNode var2, cp var3) {
      Iterator var5 = var2.fields();

      while (var5.hasNext()) {
         Entry var4;
         if (((String)(var4 = (Entry)var5.next()).getKey()).equals(var1) && var3.a.equals(((JsonNode)var4.getValue()).asText())) {
            var0.a();
            return;
         }
      }
   }

   private static boolean a(JsonNode var0, cp var1) {
      Iterator var3 = var0.fields();

      while (var3.hasNext()) {
         if (((JsonNode)((Entry)var3.next()).getValue()).asText().equals(var1.a)) {
            return false;
         }
      }

      return true;
   }
}
