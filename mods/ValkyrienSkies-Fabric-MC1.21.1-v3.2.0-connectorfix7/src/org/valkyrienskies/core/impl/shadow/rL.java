package org.valkyrienskies.core.impl.shadow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class rL<S extends qN, T extends qN> implements sb<S> {
   public final rV<S> a;
   public final rY<T> b;

   public rL(rV<S> var1, rY<T> var2) {
      this.a = var1;
      this.b = var2;
   }

   protected abstract rL<S, T> a(rV<S> var1, rY<T> var2);

   private rL<S, T> f() {
      return this.a(this.a.g(), this.b);
   }

   @Override
   public final rV<S> c() {
      return this.a;
   }

   public final rY<T> d() {
      return this.b;
   }

   @Override
   public double a() {
      return this.b.g();
   }

   private rL<S, T> b(sb<S> var1) {
      var1 = var1;
      return this.a(this.a, new rZ<T>().a(this.b, var1.b));
   }

   public final rL<S, T> a(sc<S, T> var1) {
      rV var2 = var1.a(this.a);
      HashMap var3 = new HashMap();
      rM var9 = this.a(this.b.a(false), var2, var1, var3);
      Iterator var4 = var3.entrySet().iterator();

      while (var4.hasNext()) {
         Entry var5;
         rO var6;
         if (((rM)(var5 = (Entry)var4.next()).getKey()).a != null && (var6 = (rO)((rM)var5.getKey()).e) != null) {
            rO var10 = (rO)((rM)var5.getValue()).e;

            for (rM var7 : var6.c) {
               var10.c.a((rM<S>)var3.get(var7));
            }
         }
      }

      return this.a(var2, this.b.b(var9));
   }

   private rM<T> a(rM<T> var1, rV<S> var2, sc<S, T> var3, Map<rM<T>, rM<T>> var4) {
      rM var8;
      if (var1.a == null) {
         var8 = new rM(var1.e);
      } else {
         rO var5;
         if ((var5 = (rO)var1.e) != null) {
            sb var6 = var5.a == null ? null : var3.a(var5.a, this.a, var2);
            sb var9 = var5.b == null ? null : var3.a(var5.b, this.a, var2);
            var5 = new rO(var6, var9, new rX<>());
         }

         var8 = new rM<>(var3.a(var1.a, this.a, var2), this.a(var1.b, var2, var3, var4), this.a(var1.c, var2, var3, var4), var5);
      }

      var4.put(var1, var8);
      return var8;
   }

   @Deprecated
   @Override
   public final int b(rV<S> var1) {
      return this.a(var1).c();
   }

   @Override
   public abstract sb.a<S> a(rV<S> var1);

   @Override
   public boolean b() {
      return this.b.d();
   }

   // $VF: synthetic method
   @Override
   public final rL a(sb var1) {
      rL var2 = var1;
      var1 = this;
      var2 = var2;
      return var1.a(var1.a, new rZ<T>().a(var1.b, var2.b));
   }

   // $VF: synthetic method
   @Override
   public final rL e() {
      rL var1 = this;
      return this.a(var1.a.g(), var1.b);
   }
}
