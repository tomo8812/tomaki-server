package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class aG {
   private final aK a;
   private final aU b;
   private final bb c;
   private final List<ObjectNode> d;
   private final bo e;

   static ObjectNode a(aK var0, aU var1, Type var2, Type... var3) {
      aG var10000 = new aG(var0, var1);
      Type[] var11 = var3;
      Type var7 = var2;
      aG var6 = var10000;
      ResolvedType var8 = var10000.b.a(var7, var11);
      aX var12 = var6.c.c(var8);
      ObjectNode var13 = var6.a.o();
      if (var6.a.e()) {
         var13.put(var6.a.a(aP.TAG_SCHEMA), var6.a.a(aP.TAG_SCHEMA_VALUE));
      }

      boolean var4;
      if (var4 = var6.a.c()) {
         var6.c.a(var8, var13, null, false);
      }

      String var9 = var6.a.a(aP.TAG_DEFINITIONS);
      ObjectNode var5;
      if ((var5 = var6.a(var9, var12, var6.c)).size() > 0) {
         var13.set(var9, var5);
      }

      if (!var4) {
         ObjectNode var10 = var6.c.a(var12);
         var13.setAll(var10);
         var6.d.add(var13);
      }

      var6.a();
      return var13;
   }

   static aG a(aK var0, aU var1) {
      return new aG(var0, var1);
   }

   aG(aK var1, aU var2) {
      this.a = var1;
      this.b = var2;
      this.c = new bb(this.a, this.b);
      this.d = new ArrayList<>();
      bq var5;
      if ((var5 = var1.q()) == null) {
         var5 = new bp();
      }

      ba var3 = new ba(var1);
      Function var4 = var1.f() ? var3::b : var3::a;
      this.e = new bo(var5, var4);
   }

   private ObjectNode a(Type var1, Type... var2) {
      ResolvedType var6 = this.b.a(var1, var2);
      aX var9 = this.c.c(var6);
      ObjectNode var3 = this.a.o();
      if (this.a.e()) {
         var3.put(this.a.a(aP.TAG_SCHEMA), this.a.a(aP.TAG_SCHEMA_VALUE));
      }

      boolean var4;
      if (var4 = this.a.c()) {
         this.c.a(var6, var3, null, false);
      }

      String var7 = this.a.a(aP.TAG_DEFINITIONS);
      ObjectNode var5;
      if ((var5 = this.a(var7, var9, this.c)).size() > 0) {
         var3.set(var7, var5);
      }

      if (!var4) {
         ObjectNode var8 = this.c.a(var9);
         var3.setAll(var8);
         this.d.add(var3);
      }

      this.a();
      return var3;
   }

   private ObjectNode b(Type var1, Type... var2) {
      ResolvedType var3 = this.b.a(var1, var2);
      ResolvedType var6 = var3;
      bb var4 = this.c;
      ObjectNode var5 = this.c.a(var6, (bv)null);
      this.d.add(var5);
      return var5;
   }

   private ObjectNode a(String var1) {
      ObjectNode var2 = this.a(var1, null, this.c);
      this.a();
      return var2;
   }

   private void a() {
      ba var1 = new ba(this.a);
      if (this.a.h()) {
         var1.a(this.d);
      }

      var1.b(this.d);
   }

   private ObjectNode a(String var1, aX var2, bb var3) {
      ObjectNode var4 = this.a.o();
      boolean var5 = this.a.b();
      boolean var6 = this.a.d();
      AtomicBoolean var7 = new AtomicBoolean(false);
      Predicate var8 = var5x -> {
         if (var6) {
            return false;
         }

         if (var5x.equals(var2)) {
            return true;
         }

         List var6x = var3.b(var5x);
         if (var7.get() && var6x.isEmpty()) {
            return false;
         }

         List var7x = var3.c(var5x);
         return var5 || var6x.size() + var7x.size() > 1;
      };
      Map var9 = this.a(var2, var8, var3);
      var7.set(true);
      boolean var16 = this.a.c();
      Iterator var17 = var9.entrySet().iterator();

      while (var17.hasNext()) {
         Entry var10;
         String var11 = (String)(var10 = (Entry)var17.next()).getValue();
         aX var18 = (aX)var10.getKey();
         List var12 = var3.b(var18);
         List var13 = var3.c(var18);
         boolean var15;
         String var20;
         if (var15 = !var8.test(var18)) {
            ObjectNode var14 = var3.a(var18);
            var12.forEach(var1x -> aW.a(var1x, var14));
            var20 = null;
         } else {
            if (!var16 && var18.equals(var2)) {
               var20 = this.a.a(aP.TAG_REF_MAIN);
            } else {
               var4.set(var11, var3.a(var18));
               var20 = this.a.a(aP.TAG_REF_MAIN) + '/' + var1 + '/' + var11;
            }

            var12.forEach(var2x -> var2x.put(this.a.a(aP.TAG_REF), var20));
         }

         if (!var13.isEmpty()) {
            ObjectNode var21;
            if (var15) {
               var21 = var3.a(var18);
            } else {
               var21 = this.a.o().put(this.a.a(aP.TAG_REF), var20);
            }

            var3.a(var21);
            if (var6 || !var5 && var13.size() <= 1) {
               var13.forEach(var1x -> aW.a(var1x, var21));
            } else {
               String var19 = this.e.a(var18, var11, var3);
               var4.set(var19, var21);
               var13.forEach(var3x -> var3x.put(this.a.a(aP.TAG_REF), this.a.a(aP.TAG_REF_MAIN) + '/' + var1 + '/' + var19));
            }
         }
      }

      var4.forEach(var1x -> this.d.add(var1x));
      return var4;
   }

   private Map<aX, String> a(aX var1, Predicate<aX> var2, bb var3) {
      boolean var4 = this.a.c();
      Function var5 = var2x -> this.e.a(var2x, var3);
      Map var9 = var3.c().stream().collect(Collectors.groupingBy(var5, TreeMap::new, Collectors.toList()));
      LinkedHashMap var6 = new LinkedHashMap();
      Iterator var10 = var9.entrySet().iterator();

      while (var10.hasNext()) {
         Entry var7;
         ((List)(var7 = (Entry)var10.next()).getValue()).forEach(var1x -> var6.put(var1x, ""));
         List var8;
         if ((var8 = ((List)var7.getValue()).stream().filter(var2).collect(Collectors.toList())).size() != 1
            && (var8.size() != 2 || var4 || !var8.contains(var1))) {
            Map var12 = var8.stream().collect(Collectors.toMap(var0 -> (aX)var0, var1x -> (String)var7.getKey(), (var0, var1x) -> var0, LinkedHashMap::new));
            this.e.a(var12, var3);
            if (var8.size() != var12.size()) {
               throw new IllegalStateException(
                  bq.class.getSimpleName() + " of type " + this.e.getClass().getSimpleName() + " altered list of subschemas with duplicate names."
               );
            }

            var6.putAll(var12);
         } else {
            var8.forEach(var2x -> var6.put(var2x, (String)var7.getKey()));
         }
      }

      String var11;
      if (!(var11 = var6.values()
            .stream()
            .filter(var0 -> !var0.isEmpty())
            .collect(Collectors.groupingBy(var0 -> (String)var0, Collectors.counting()))
            .entrySet()
            .stream()
            .filter(var0 -> var0.getValue() > 1L)
            .map(Entry::getKey)
            .collect(Collectors.joining(", ")))
         .isEmpty()) {
         throw new IllegalStateException(bq.class.getSimpleName() + " of type " + this.e.getClass().getSimpleName() + " produced duplicate keys: " + var11);
      } else {
         return var6;
      }
   }
}
