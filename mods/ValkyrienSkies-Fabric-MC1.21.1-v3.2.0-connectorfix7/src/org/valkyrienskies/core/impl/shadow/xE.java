package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Deprecated
public final class xE extends wH<wR> {
   final xF a;
   private final List<wR> b = new ArrayList<>();

   private xE(xF var1, int var2, zH var3) {
      super(var1, var2, var3);
      this.a = var1;
   }

   private wR[] m() {
      xE var1 = this;
      Collections.sort(this.b, new Comparator<wR>(var1) {
         private final sA a;
         private final tq b;
         // $VF: synthetic field
         private xE c;

         {
            this.c = var1;
            var1 = this.c;
            this.a = new sA(this.c.a.d(), false);
            var1 = this.c;
            this.b = this.c.a.b();
         }

         private int a(wR var1, wR var2x) {
            if (var1 == null) {
               return var2x == null ? 0 : 1;
            } else {
               return var2x == null ? -1 : Double.compare(this.a(var1), this.a(var2x));
            }
         }

         private double a(wR var1) {
            sA var2x = new sA(var1.b(), false);
            tu var3;
            return (var3 = this.a.k(var2x)).b(this.b.a(var3));
         }

         // $VF: synthetic method
         @Override
         public final int compare(Object var1, Object var2) {
            wR var10001 = (wR)var1;
            wR var3 = var2;
            var2 = var10001;
            var var4 = this;
            int var10000;
            if (var2 == null) {
               if (var3 != null) {
                  return 1;
               }

               var10000 = 0;
            } else {
               if (var3 == null) {
                  return -1;
               }

               var10000 = Double.compare(var4.a(var2), var4.a(var3));
            }

            return var10000;
         }
      });
      return this.b.toArray(new wR[0]);
   }

   private void a(wR var1) {
      this.b.add(var1);
   }

   @Override
   public final void d() {
      this.b.clear();
   }

   private Comparator<wR> n() {
      return new Comparator<wR>(this) {
         private final sA a;
         private final tq b;
         // $VF: synthetic field
         private xE c;

         {
            this.c = var1;
            var1 = this.c;
            this.a = new sA(this.c.a.d(), false);
            var1 = this.c;
            this.b = this.c.a.b();
         }

         private int a(wR var1, wR var2x) {
            if (var1 == null) {
               return var2x == null ? 0 : 1;
            } else {
               return var2x == null ? -1 : Double.compare(this.a(var1), this.a(var2x));
            }
         }

         private double a(wR var1) {
            sA var2x = new sA(var1.b(), false);
            tu var3;
            return (var3 = this.a.k(var2x)).b(this.b.a(var3));
         }

         // $VF: synthetic method
         @Override
         public final int compare(Object var1, Object var2) {
            wR var10001 = (wR)var1;
            wR var3 = var2;
            var2 = var10001;
            var var4 = this;
            int var10000;
            if (var2 == null) {
               if (var3 != null) {
                  return 1;
               }

               var10000 = 0;
            } else {
               if (var3 == null) {
                  return -1;
               }

               var10000 = Double.compare(var4.a(var2), var4.a(var3));
            }

            return var10000;
         }
      };
   }

   // $VF: synthetic method
   @Override
   public final void a(Object var1) {
      wR var2 = (wR)var1;
      this.b.add(var2);
   }

   // $VF: synthetic method
   @Override
   public final Co[] b() {
      xE var1 = this;
      xE var2 = var1;
      Collections.sort(this.b, new Comparator<wR>(var2) {
         private final sA a;
         private final tq b;
         // $VF: synthetic field
         private xE c;

         {
            this.c = var1;
            var1 = this.c;
            this.a = new sA(this.c.a.d(), false);
            var1 = this.c;
            this.b = this.c.a.b();
         }

         private int a(wR var1, wR var2x) {
            if (var1 == null) {
               return var2x == null ? 0 : 1;
            } else {
               return var2x == null ? -1 : Double.compare(this.a(var1), this.a(var2x));
            }
         }

         private double a(wR var1) {
            sA var2x = new sA(var1.b(), false);
            tu var3;
            return (var3 = this.a.k(var2x)).b(this.b.a(var3));
         }

         // $VF: synthetic method
         @Override
         public final int compare(Object var1, Object var2) {
            wR var10001 = (wR)var1;
            wR var3 = var2;
            var2 = var10001;
            var var4 = this;
            int var10000;
            if (var2 == null) {
               if (var3 != null) {
                  return 1;
               }

               var10000 = 0;
            } else {
               if (var3 == null) {
                  return -1;
               }

               var10000 = Double.compare(var4.a(var2), var4.a(var3));
            }

            return var10000;
         }
      });
      return (wR[])var1.b.toArray(new wR[0]);
   }

   // $VF: synthetic method
   static xF a(xE var0) {
      return var0.a;
   }
}
