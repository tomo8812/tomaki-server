package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cN extends bT implements cv {
   private static final Logger g = LoggerFactory.getLogger(cN.class);
   private static final String i = "items";
   private final cp j;
   private final List<cp> k;
   private boolean l = true;
   private final cp m;
   private dO n;

   public cN(String var1, JsonNode var2, cp var3, df var4) {
      super(var1, var2, var3, dj.ITEMS, var4);
      this.k = new ArrayList<>();
      cp var5 = null;
      cp var6 = null;
      if (!var2.isObject() && !var2.isBoolean()) {
         for (JsonNode var7 : var2) {
            this.k.add(new cp(var4, var1, var3.i(), var7, var3));
         }

         if ((var2 = this.d().c().get("items")) != null) {
            if (var2.isBoolean()) {
               this.l = var2.asBoolean();
            } else if (var2.isObject()) {
               var6 = new cp(var4, "#", var3.i(), var2, var3);
            }
         }
      } else {
         var5 = new cp(var4, var1, var3.i(), var2, var3);
      }

      this.n = new dH(var4.d().j());
      super.d = var4;
      this.a(this.e().d());
      this.j = var5;
      this.m = var6;
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3) {
      this.a(g, (JsonNode)var1, var2, var3);
      LinkedHashSet var4 = new LinkedHashSet();
      if (!var1.isArray() && !super.d.d().a()) {
         return var4;
      }

      if (var1.isArray()) {
         int var5 = 0;

         for (JsonNode var6 : var1) {
            this.a(var4, var5, var6, var2, var3);
            var5++;
         }
      } else {
         this.a(var4, 0, var1, var2, var3);
      }

      return Collections.unmodifiableSet(var4);
   }

   private void a(Set<dg> var1, int var2, JsonNode var3, JsonNode var4, String var5) {
      if (this.j != null) {
         var1.addAll(this.j.a(var3, var4, var5 + "[" + var2 + "]"));
      }

      if (this.k != null) {
         if (var2 < this.k.size()) {
            var1.addAll(this.k.get(var2).a(var3, var4, var5 + "[" + var2 + "]"));
            return;
         }

         if (this.m != null) {
            var1.addAll(this.m.a(var3, var4, var5 + "[" + var2 + "]"));
            return;
         }

         if (!this.l) {
            var1.add(this.a(var5, "" + var2));
         }
      }
   }

   @Override
   public Set<dg> a(JsonNode var1, JsonNode var2, String var3, boolean var4) {
      LinkedHashSet var5 = new LinkedHashSet();
      if (var1 instanceof ArrayNode) {
         var1 = var1;
         JsonNode var6 = null;
         if (super.f.c() && this.j != null) {
            var6 = this.j.c().get("default");
         }

         int var7 = 0;

         for (Iterator var8 = var1.iterator(); var8.hasNext(); var7++) {
            JsonNode var9;
            if ((var9 = (JsonNode)var8.next()).isNull() && var6 != null) {
               var1.set(var7, var6);
               var9 = var6;
            }

            this.a(var5, var7, var9, var2, var3, var4);
         }
      } else {
         this.a(var5, 0, var1, var2, var3, var4);
      }

      return var5;
   }

   private void a(HashSet<dg> var1, int var2, JsonNode var3, JsonNode var4, String var5, boolean var6) {
      if (this.j != null) {
         this.a(this.j, var3, var4, var5 + "[" + var2 + "]", var6, var1);
      }

      if (this.k != null) {
         if (var2 < this.k.size()) {
            this.a(this.k.get(var2), var3, var4, var5 + "[" + var2 + "]", var6, var1);
            return;
         }

         if (this.m != null) {
            this.a(this.m, var3, var4, var5 + "[" + var2 + "]", var6, var1);
         }
      }
   }

   private void a(cp var1, JsonNode var2, JsonNode var3, String var4, boolean var5, Set<dg> var6) {
      if (this.n.b(dj.ITEMS.a(), var2, var3, var4, var1.b(), var1.c(), var1.d(), super.d, super.d.c())) {
         var6.addAll(var1.a(var2, var3, var4, var5));
      }

      this.n.a(dj.ITEMS.a(), var2, var3, var4, var1.b(), var1.c(), var1.d(), super.d, super.d.c(), var6);
   }

   public List<cp> h() {
      return this.k;
   }

   public cp i() {
      return this.j;
   }

   @Override
   public void a_() {
      if (this.j != null) {
         this.j.o();
      }

      this.a(this.k);
      if (this.m != null) {
         this.m.o();
      }
   }
}
