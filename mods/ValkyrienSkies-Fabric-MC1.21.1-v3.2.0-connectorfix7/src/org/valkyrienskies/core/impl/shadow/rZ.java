package org.valkyrienskies.core.impl.shadow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public final class rZ<S extends qN> {
   final rZ<S>.c a = new rZ.c((byte)0);

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final rY<S> a(rV<S>... var1) {
      if (var1.length == 0) {
         return null;
      }

      rK var2;
      rM var3;
      rM var10000 = var3 = (var2 = var1[0].e()).a(false);
      Boolean var7 = Boolean.TRUE;
      var10000.e = var7;
      rV[] var4 = var1;
      int var5 = var1.length;

      for (int var6 = 0; var6 < var5; var6++) {
         rV var12 = var4[var6];
         if (var3.a(var12)) {
            var7 = null;
            var3.e = var7;
            var7 = Boolean.FALSE;
            rM var17 = var3.b;
            var3.b.e = var7;
            var10000 = var3 = var3.c;
            var7 = Boolean.TRUE;
            var10000.e = var7;
         } else {
            sb var8 = var12.f();

            for (rM var9 = var3; var9.d != null && var8 != null; var9 = var9.d) {
               rM var10 = var9.d;
               rV var16 = var9.d.a.c();
               sb.a var11 = var8.a(var16);
               switch (<unrepresentable>.a[var11.c() - 1]) {
                  case 1:
                     if (!var12.a(var16)) {
                        return this.a(var1[0].e());
                     }
                     break;
                  case 2:
                     throw new oV(px.NOT_CONVEX_HYPERPLANES);
                  default:
                     var8 = var11.b;
               }
            }
         }
      }

      return var2;
   }

   public final rY<S> a(rY<S> var1, rY<S> var2) {
      rM var3;
      (var3 = var1.a(false).a(var2.a(false), new rZ.d((byte)0))).a(this.a);
      return var1.b(var3);
   }

   public final rY<S> b(rY<S> var1, rY<S> var2) {
      rM var3;
      (var3 = var1.a(false).a(var2.a(false), new rZ.b((byte)0))).a(this.a);
      return var1.b(var3);
   }

   private rY<S> c(rY<S> var1, rY<S> var2) {
      rM var3;
      (var3 = var1.a(false).a(var2.a(false), new rZ.f((byte)0))).a(this.a);
      return var1.b(var3);
   }

   public final rY<S> a(rY<S> var1, rY<S> var2) {
      rM var3;
      (var3 = var1.a(false).a(var2.a(false), new rZ.a(var1, var2))).a(this.a);
      return var1.b(var3);
   }

   public final rY<S> a(rY<S> var1) {
      return var1.b(this.a(var1.a(false)));
   }

   rM<S> a(rM<S> var1) {
      HashMap var2 = new HashMap();
      var1 = this.a(var1, var2);
      Iterator var3 = var2.entrySet().iterator();

      while (var3.hasNext()) {
         Entry var4;
         rO var5;
         if (((rM)(var4 = (Entry)var3.next()).getKey()).a != null && (var5 = (rO)((rM)var4.getKey()).e) != null) {
            rO var9 = (rO)((rM)var4.getValue()).e;

            for (rM var6 : var5.c) {
               var9.c.a((rM<S>)var2.get(var6));
            }
         }
      }

      return var1;
   }

   private rM<S> a(rM<S> var1, Map<rM<S>, rM<S>> var2) {
      rM var3;
      if (var1.a == null) {
         var3 = new rM((Boolean)var1.e ? Boolean.FALSE : Boolean.TRUE);
      } else {
         rO var6;
         if ((var6 = (rO)var1.e) != null) {
            rL var4 = var6.b == null ? null : var6.b.e();
            rL var7 = var6.a == null ? null : var6.a.e();
            var6 = new rO<>(var4, var7, new rX<>());
         }

         var3 = new rM<>(var1.a.e(), this.a(var1.b, var2), this.a(var1.c, var2), var6);
      }

      var2.put(var1, var3);
      return var3;
   }

   // $VF: synthetic method
   static rM a(rZ var0, rM var1) {
      return var0.a(var1);
   }

   class a implements rM.a<S>, rM.b<S> {
      private final rY<S> a;
      private final rY<S> b;

      a(rY<S> var2, rY<S> var3) {
         this.a = var2.i();
         this.b = var3.i();
      }

      @Override
      public final rM<S> a(rM<S> var1, rM<S> var2, rM<S> var3, boolean var4, boolean var5) {
         if ((Boolean)var1.e) {
            var1 = var5 ? var2 : var1;
            rZ var6 = rZ.this;
            rM var9;
            (var9 = rZ.this.a(var1)).a(var3, var4, this);
            return var9;
         } else {
            rM var7;
            (var7 = var5 ? var1 : var2).a(var3, var4, this);
            return var7;
         }
      }

      @Override
      public final rM<S> a(rM<S> var1) {
         var1 = var1.a(Boolean.TRUE, Boolean.FALSE, null);
         qM var3 = this.a.b(var1).h();
         return new rM<>(this.a.b(var3) == rY.a.INSIDE$3ad793e5 && this.b.b(var3) == rY.a.OUTSIDE$3ad793e5);
      }
   }

   class b implements rM.a<S> {
      private b() {
      }

      @Override
      public final rM<S> a(rM<S> var1, rM<S> var2, rM<S> var3, boolean var4, boolean var5) {
         if ((Boolean)var1.e) {
            var2.a(var3, var4, rZ.this.new e(true));
            return var2;
         } else {
            var1.a(var3, var4, rZ.this.new e(false));
            return var1;
         }
      }

      // $VF: synthetic method
      b(byte var2) {
         this();
      }
   }

   class c implements rN<S> {
      private c() {
      }

      @Override
      public final int a(rM<S> var1) {
         return rN.a.PLUS_SUB_MINUS$672d2271;
      }

      @Override
      public final void b(rM<S> var1) {
         Object var2 = null;
         var1.e = var2;
      }

      @Override
      public final void c(rM<S> var1) {
      }

      // $VF: synthetic method
      c(byte var2) {
         this();
      }
   }

   class d implements rM.a<S> {
      private d() {
      }

      @Override
      public final rM<S> a(rM<S> var1, rM<S> var2, rM<S> var3, boolean var4, boolean var5) {
         if ((Boolean)var1.e) {
            var1.a(var3, var4, rZ.this.new e(true));
            return var1;
         } else {
            var2.a(var3, var4, rZ.this.new e(false));
            return var2;
         }
      }

      // $VF: synthetic method
      d(byte var2) {
         this();
      }
   }

   class e implements rM.b<S> {
      private final boolean a;

      e(boolean var2) {
         this.a = var2;
      }

      @Override
      public final rM<S> a(rM<S> var1) {
         rM var2 = var1.b;
         var2 = var1.c;
         if (var1.b.e.equals(var1.c.e)) {
            var2 = var1.b;
            return new rM<>(var1.b.e);
         } else {
            return new rM<>(this.a);
         }
      }
   }

   class f implements rM.a<S> {
      private f() {
      }

      @Override
      public final rM<S> a(rM<S> var1, rM<S> var2, rM<S> var3, boolean var4, boolean var5) {
         var2 = var2;
         if ((Boolean)var1.e) {
            var2 = var2;
            rZ var6 = rZ.this;
            var2 = rZ.this.a(var2);
         }

         var2.a(var3, var4, rZ.this.new e(true));
         return var2;
      }

      // $VF: synthetic method
      f(byte var2) {
         this();
      }
   }
}
